package cn.tjut.xsl.service;

import pojo.*;

import java.util.Map;

/**
 * 说明：
 *
 * @Auther: 11432_000
 * @Date: 2018/10/8 13:02
 * @Description:
 */
public interface AlipayService {
    /**网页支付*/
    /* String userPayByPage(Map payInfo);*/
    /**app支付*/
    AlipayPayRerurn userPayByApp(int taskId, AlipayPayInfo payInfo, AlipayExtendParams extendParams, AlipayExternalUserInfo externalUserInfo);
    AlipayPayRerurn userPayByApp(int taskId, AlipayPayInfo payInfo);
    /**佣金支付（转账）*/
    AlipayTransferReturn commissionPayment(AlipayTransferInfo transferInfo, int taskId);
    /**异步通知处理*/
    AlipayNotifyInfo  alipayNotify(Map requestParams);
}
