package pojo;

/**
 * 说明：
 *
 * @Auther: 11432_000
 * @Date: 2018/9/18 14:24
 * @Description:
 */
public class AlipayNotifyInfo {

    //支付宝交易号
    private String tradeNo ;
    //订单号
    private String OrderNo ;
    //标志支付请求成功与否
    private String isSuccess;
    //错误描述
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }


}
