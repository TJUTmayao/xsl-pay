package pojo;

import java.io.Serializable;

/**
 * @Auther: 11432_000
 * @Date: 2018/9/13 13:23
 * @Description:
 */
public class AlipayTransferReturn extends AlipayReturn implements Serializable {

    private String outBizNo;
    //支付宝转账单据号
    private String orderId ;
    //时间
    private String payDate;

    public String getOutBizNo() {
        return outBizNo;
    }

    public void setOutBizNo(String outBizNo) {
        this.outBizNo = outBizNo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }
}
