package pojo;

import java.io.Serializable;

/**
 * @Auther: 11432_000
 * @Date: 2018/9/13 13:19
 * @Description:
 */
public class AlipayPayRerurn extends AlipayReturn implements Serializable {
    //支付宝返回的订单信息，可直接传给app
    private String orderInfo;

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }
}
