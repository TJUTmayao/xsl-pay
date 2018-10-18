package cn.tjut.xsl.utils;

import cn.tjut.xsl.enums.PayEnum;
import cn.tjut.xsl.enums.TransferParameter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pojo.AlipayTransferInfo;

import java.text.DecimalFormat;

/**
 * @Auther: 11432_000
 * @Date: 2018/9/11 17:17
 * @Description:
 */
@Component
public class AlipayTransferUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayTransferInfo.class);

    /**out_biz_no允许的范围*/
    private static final String OUT_BIZ_NO_RULE = "[0-9a-zA-Z-_]";

    /**
     *
     * 功能描述: 将AlipayTransferInfo变成字符串，传参为null或参数有误时返回null
     *
     * @param: [transferInfo]
     * @return: java.lang.String
     * @auther: 11432_000
     * @date: 2018/9/12 13:56
     */
    public  String getBizContent(AlipayTransferInfo transferInfo){

        if (transferInfo == null && checkTransferInfo(transferInfo)){
            LOGGER.error("转账参数为空或错误");
            return null;
        }
        String bizContont = "{"+
                "\"out_biz_no\":\""+ transferInfo.getOutBizNo() + "\"," +
                "\"payee_type\":\"" + transferInfo.getPayeeType() + "\"," +
                "\"payee_account\":\""+ transferInfo.getPayeeAccount() + "\"," +
                "\"amount\":\""+ transferInfo.getAmount() + "\"";
        if (StringUtils.isNotBlank(transferInfo.getPayerShowName())){
            bizContont = bizContont + ",\"payer_show_name\":\""+ transferInfo.getPayerShowName()+ "\"";
        }
        if (StringUtils.isNotBlank(transferInfo.getPayeeRealName())){
            bizContont = bizContont + ",\"payee_real_name\":\""+ transferInfo.getPayeeRealName()+ "\"";
        }
        if (StringUtils.isNotBlank(transferInfo.getRemark())){
            bizContont = bizContont + ",\"remark\":\""+ transferInfo.getRemark()+ "\"";
        }
        bizContont = bizContont + "}";
        return bizContont;
    }

    /**
     *
     * 功能描述: 转账参数校验，返回Boolean值
     *
     * @param: [transferInfo]
     * @return: boolean
     * @auther: 11432_000
     * @date: 2018/9/12 15:46
     */
    private boolean checkTransferInfo(AlipayTransferInfo transferInfo){

        boolean falg = true;
        if (!(StringUtils.isNotBlank(transferInfo.getOutBizNo()) && StringUtils.isNotBlank(transferInfo.getPayeeAccount()) && StringUtils.isNotBlank(transferInfo.getAmount()))){
            falg = false;
        }
        String outBizNo = transferInfo.getOutBizNo();
        String isNull = outBizNo.replaceAll(OUT_BIZ_NO_RULE,"");
        if (falg && !(StringUtils.isBlank(isNull) && outBizNo.length() < PayEnum.SIX_FOUR.getValue())){
            falg = false;
        }
        if (falg && !TransferParameter.ALIPAY_LOGONID.equals(transferInfo.getPayeeType()) && !TransferParameter.ALIPAY_USERID.equals(transferInfo.getPayeeType())){
            falg = false;
        }
        if (falg && transferInfo.getPayeeAccount().length() > PayEnum.ONE_ZERO_ZERO.getValue()){
            falg = false;
        }
        transferInfo.setAmount(amountTransformation(transferInfo.getAmount()));
        if (falg && transferInfo.getAmount().length() > PayEnum.ONE_SIX.getValue()){
            falg = false;
        }
        if (falg && transferInfo.getPayeeRealName().length() > PayEnum.ONE_ZERO_ZERO.getValue()){
            falg =false;
        }
        if (falg && transferInfo.getPayerShowName().length() > PayEnum.ONE_ZERO_ZERO.getValue()){
            falg =false;
        }
        if (falg && Double.parseDouble(transferInfo.getAmount()) > PayEnum.FIVE_ZERO_ZERO_ZERO_ZERO.getValue()){
            if (StringUtils.isBlank(transferInfo.getRemark())){
                falg = false;
            }
        }
        return falg;
    }

    /**
     *
     * 功能描述: 将String小数取小数点后两位
     *
     * @param: [amount]
     * @return: java.lang.String
     * @auther: 11432_000
     * @date: 2018/9/12 15:48
     */
    private  String amountTransformation(String amount){
        double dou = Double.parseDouble(amount);
        DecimalFormat format = new DecimalFormat("0.00");
        String string = format.format(dou);
        return string;
    }
}
