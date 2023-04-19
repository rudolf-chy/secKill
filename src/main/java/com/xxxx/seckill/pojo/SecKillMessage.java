package com.xxxx.seckill.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecKillMessage {

    private User user;

    private Long goodsId;
}
