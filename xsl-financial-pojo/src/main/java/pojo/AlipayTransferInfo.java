package pojo;

import java.io.Serializable;

/**
 * @Auther: 11432_000
 * @Date: 2018/9/11 17:00
 * @Description:
 */
public class AlipayTransferInfo implements Serializable {
    //商户转账唯一订单号。
    private String outBizNo;
    //收款方账户类型。默认为登录账号（手机或邮箱）
    private String payeeType = "ALIPAY_LOGONID";
    //收款方账户。
    private String payeeAccount;
    //转账金额，单位：元。
    private String amount;
    //付款方姓名,可选
    private String payerShowName;
    //收款方真实姓名,可选
    private String payeeRealName;
    //转账备注（支持200个英文/100个汉字）。当付款方为企业账户，且转账金额达到（大于等于）50000元
    private String remark;

    public String getOutBizNo() {
        return outBizNo;
    }

    public void setOutBizNo(String outBizNo) {
        this.outBizNo = outBizNo;
    }

    public String getPayeeType() {
        return payeeType;
    }

    public void setPayeeType(String payeeType) {
        this.payeeType = payeeType;
    }

    public String getPayeeAccount() {
        return payeeAccount;
    }

    public void setPayeeAccount(String payeeAccount) {
        this.payeeAccount = payeeAccount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayerShowName() {
        return payerShowName;
    }

    public void setPayerShowName(String payerShowName) {
        this.payerShowName = payerShowName;
    }

    public String getPayeeRealName() {
        return payeeRealName;
    }

    public void setPayeeRealName(String payeeRealName) {
        this.payeeRealName = payeeRealName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
