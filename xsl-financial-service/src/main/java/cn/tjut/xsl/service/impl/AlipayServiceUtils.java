package cn.tjut.xsl.service.impl;

import cn.xsl.mapper.XslOrderMapper;
import cn.xsl.mapper.XslTaskMapper;
import cn.xsl.pojo.XslOrder;
import cn.xsl.pojo.XslOrderExample;
import cn.xsl.pojo.XslTask;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pojo.AlipayPayInfo;
import pojo.AlipayTransferInfo;

import javax.annotation.Resource;
import java.util.List;

/**
 * 说明：
 *
 * @Auther: 11432_000
 * @Date: 2018/9/18 14:37
 * @Description:
 */
@Component
public class AlipayServiceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayServiceUtils.class);

    @Resource
    private XslOrderMapper xslOrderMapper;
    @Resource
    private XslTaskMapper xslTaskMapper;
    /**
     *
     * 功能描述: 根据任务id取支付金额，订单号和任务标题，查询出错返回null
     *
     * @param: [payInfo, taskId]
     * @return: pojo.AlipayPayInfo
     * @auther: 11432_000
     * @date: 2018/9/11 16:30
     */
    public AlipayPayInfo getMoneyAndOrderIdAndBody(AlipayPayInfo payInfo, int taskId){

        XslOrderExample orderExample = new XslOrderExample();
        XslOrderExample.Criteria orderExampleCriteria = orderExample.createCriteria();
        orderExampleCriteria.andTaskidEqualTo(taskId);
        List<XslOrder> xslOrders = xslOrderMapper.selectByExample(orderExample);
        XslTask xslTask = xslTaskMapper.selectByPrimaryKey(taskId);
        if (xslOrders != null && xslOrders.size() > 0){
            XslOrder xslOrder = xslOrders.get(0);
            if (StringUtils.isNotBlank(payInfo.getTotalAmount()) && !payInfo.getTotalAmount().equals(xslOrder.getMoney())){
                    payInfo.setTotalAmount(xslOrder.getMoney().toString());
                    LOGGER.error("传入金额与数据库不符合，以数据库金额为准");
            }
            if (StringUtils.isBlank(payInfo.getTotalAmount())){
                payInfo.setTotalAmount(xslOrder.getMoney().toString());
            }
            payInfo.setOutTradeNo(xslOrder.getOrderid());
            if (xslTask != null){
                if (StringUtils.isNotBlank(payInfo.getBody())){
                    payInfo.setSubject(xslTask.getDescr());
                }
                return payInfo;
            }
        }
        return null;
    }
    /**
     *
     * 功能描述: 从数据库获取订单号，金额，接受者支付宝账号，出错返回null
     *
     * @param: [taskId]
     * @return: pojo.AlipayTransferInfo
     * @auther: 11432_000
     * @date: 2018/9/12 16:48
     */
    public AlipayTransferInfo getTransferInfoByTaskId(int taskId, AlipayTransferInfo transferInfo){

        XslOrderExample example = new XslOrderExample();
        XslOrderExample.Criteria criteria = example.createCriteria();
        criteria.andTaskidEqualTo(taskId);
        List<XslOrder> xslOrders = xslOrderMapper.selectByExample(example);
        if (xslOrders != null && xslOrders.size() > 0){
            XslOrder xslOrder = xslOrders.get(0);
            if (StringUtils.isNotBlank(transferInfo.getAmount()) && !transferInfo.getAmount().equals(xslOrder.getMoney())){
                transferInfo.setAmount(xslOrder.getMoney().toString());
                LOGGER.error("转账金额与数据库不符");
            }
            if (StringUtils.isBlank(transferInfo.getAmount())){
                transferInfo.setAmount(xslOrder.getMoney().toString());
            }
            transferInfo.setOutBizNo(xslOrder.getOrderid());
            //差一个账号
            return transferInfo;
        }
        return null;
    }
}
