package com.xxxx.seckill.rabbitmq;

import com.xxxx.seckill.pojo.SecKillMessage;
import com.xxxx.seckill.pojo.SeckillOrder;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IGoodsService;
import com.xxxx.seckill.service.IOrderService;
import com.xxxx.seckill.utils.JsonUtil;
import com.xxxx.seckill.vo.GoodsVo;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息消费者
 */
@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

//    @RabbitListener(queues = "queue")
//    public void receive(Object msg){
//        log.info("接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg){
//        log.info("QUEUE01接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg){
//        log.info("QUEUE02接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receive03(Object msg){
//        log.info("QUEUE01接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive04(Object msg){
//        log.info("QUEUE02接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic01")
//    public void receive05(Object msg){
//        log.info("QUEUE01接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic02")
//    public void receive06(Object msg){
//        log.info("QUEUE02接收消息:" + msg);
//    }
//
//    @RabbitListener(queues = "queue_headers01")
//    public void receive07(Message msg){
//        log.info("QUEUE01接收Message对象：" + msg);
//        log.info("QUEUE01接收消息:" + new String(msg.getBody()));
//    }
//
//    @RabbitListener(queues = "queue_headers02")
//    public void receive08(Message msg){
//        log.info("QUEUE02接收Message对象：" + msg);
//        log.info("QUEUE02接收消息:" + new String(msg.getBody()));
//    }

    /**
     * 下单操作
     */
    @RabbitListener(queues = "secKillQueue")
    public void receive(String message) {
        log.info("接收到消息：" + message);
        SecKillMessage secKillMessage = JsonUtil.jsonStr2Object(message, SecKillMessage.class);
        Long goodsId = secKillMessage.getGoodsId();
        User user = secKillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() < 1){
            return;
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
            return;
        }
        //下单操作
        orderService.seckill(user, goodsVo);
    }
}
