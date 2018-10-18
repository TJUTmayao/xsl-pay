package cn.tjut.xsl.utils;

import cn.tjut.xsl.enums.PayEnum;
import cn.tjut.xsl.enums.TimeUnit;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.ExtUserInfo;
import com.alipay.api.domain.ExtendParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pojo.AlipayExtendParams;
import pojo.AlipayExternalUserInfo;
import pojo.AlipayPayInfo;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.regex.Pattern;


@Component
public class AlipayAppPayUtils {
    /**
     * @Auther: 11432_000
     * @Date: 2018/9/13 13:32
     * @Description:
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayAppPayUtils.class);


    /*常量定义*/
    /**转码编码方式*/
    private static final String ENCODING_METHOD = "GBK";
    /**最大金额*/
    private static final double GOLD_MAX = 100000000;
    /**最小金额*/
    private static final double GOLD_MIN = 0.01;
    /**商品类别,0表示虚拟商品,1表示实物类商品*/
    private static final String VIRTUAL_COMMODITY = "0";
    private static final String PHYSICAL_COMMODITY = "1";
    /**product_code参数定义*/
    private static final String PRODUCT_CODE_VALUE = "QUICK_MSECURITY_PAY";
    /**支付有效期最大值timeout_express*/
    private static final long TIMEOUT_EXPRESS_MAX_MINUTE = 21600;
    private static final long TIMEOUT_EXPRESS_MAX_HOUR = 360;
    private static final long TIMEOUT_EXPRESS_MAX_DAY = 15;
    private static final long THIS_DAY = 1;
    /**hb_fq_seller_percent参数，100表示商家承担手续费，0表示用户承担手续费*/
    private static final String BUSINESS = "100";
    private static final String CUSTOMER = "0";
    /**分期数*/
    private static final String[] STAGING_NUMBER = {"3","6","12"};
    /**支付渠道*/
    private static final String[] CHANNEL_OF_PAYMENTS = {"balance","moneyFund","coupon","pcredit","pcreditpayInstallment",
            "creditCard","creditCardExpress","creditCardCartoon","credit_group","debitCardExpress",
            "mcard","pcard","promotion","voucher","point","mdiscount","bankPay"};
    /** 证件类型*/
    private static final String[] CARD_TYPE = {"IDENTITY_CARD","PASSPORT","OFFICER_CARD","SOLDIER_CARD","HOKOU"};
    /**是否强制校验付款人身份信息，T:强制校验，F：不强制*/
    private static final String FIX_BUYER_TRUE = "T";
    private static final String FIX_BUYER_FALSE = "F";
    /**是否强制校验身份信息,T:强制校验，F：不强制*/
    private static final String NEED_CHECK_INFO_TRUE = "T";
    private static final String NEED_CHECK_INFO_FALSE = "F";

    /**
     *
     * 功能描述: 将pojo的参数设置到AlipayTradeAppPayModel，若参数校验失败或出错则返回null
     *
     * @param: [payInfo, extendParams, promoParams, externalUserInfo]
     * @return: com.alipay.api.domain.AlipayTradeAppPayModel
     * @auther: 11432_000
     * @date: 2018/9/6 13:22
     */
    public AlipayTradeAppPayModel getAppPayModelByPayInfo (AlipayPayInfo payInfo , AlipayExtendParams extendParams , AlipayExternalUserInfo externalUserInfo){
        try {
            if (checkEssentialAlipayPayPojo(payInfo ,extendParams ,externalUserInfo) ){
                LOGGER.error("参数错误,请仔细检查参数是否正确");
                return null;
            }
            AlipayTradeAppPayModel payModel = new AlipayTradeAppPayModel();
            //用Field数组接收payInfo的所有属性和值
            Field[] fields = payInfo.getClass().getDeclaredFields();
            //遍历fields中的属性,i为循环变量
            for (Field field : fields){
                //设置可以访问私有属性（不包含父类的属性）
                field.setAccessible(true);
                //过滤需要处理的属性
                if("extUserInfo".equals(field.getName()) ||"extendParams".equals(field.getName())){
                    continue;
                }
                //将非空属性设置到model
                if(field.get(payInfo) != null && StringUtils.isNotBlank(String.valueOf(field.get(payInfo)))){
                    setAttributeByAttributeName(payModel,field.getName(),String.valueOf(field.get(payInfo)));
                }
            }
            //设置需要转换的属性
            setNotStringAttribute(payModel,extendParams ,externalUserInfo ,payInfo);
            return payModel;
        }catch (IllegalAccessException e){
            LOGGER.error("参数设置出错");
            return null;
        }
    }

    /**
     *
     * 功能描述: 设置需要转换的属性
     *
     * @param: [payModel, extendParams, promoParams, externalUserInfo, payInfo]
     * @return: void
     * @auther: 11432_000
     * @date: 2018/9/6 13:23
     */
    private  void setNotStringAttribute (AlipayTradeAppPayModel payModel, AlipayExtendParams extendParams , AlipayExternalUserInfo externalUserInfo , AlipayPayInfo payInfo){
        if (payInfo.isExtendParams() && extendParams != null){
            ExtendParams extendParamsResult = getExtendParams(extendParams);
            if (extendParamsResult != null){
                payModel.setExtendParams(extendParamsResult);
            }
        }
        if (payInfo.isExtUserInfo() && externalUserInfo != null){
            ExtUserInfo extUserInfoResult = getExtUserInfo(externalUserInfo);
            if (extUserInfoResult != null){
                payModel.setExtUserInfo(extUserInfoResult);
            }
        }
    }

    /**
     *
     * 功能描述: 将AlipayExternalUserInfo转为ExtUserInfo，出错返回null
     *
     * @param: [userInfo]
     * @return: com.alipay.api.domain.ExtUserInfo
     * @auther: 11432_000
     * @date: 2018/9/6 13:25
     */
    private  ExtUserInfo getExtUserInfo(AlipayExternalUserInfo userInfo){

        try {
            ExtUserInfo extUserInfo = new ExtUserInfo();
            Field[] fields = userInfo.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(userInfo) != null && StringUtils.isNotBlank(String.valueOf(field.get(userInfo)))){
                    setAttributeByAttributeName(extUserInfo,field.getName(),String.valueOf(field.get(userInfo)));
                }
            }
            return extUserInfo;
        }catch (Exception e){
            LOGGER.error("设置ExtUserInfo时出错");
            return null;
        }
    }

    /**
     *
     * 功能描述: 将AlipayExtendParams转换为ExtendParams,出错返回null
     *
     * @param: [alipayExtendParams]
     * @return: com.alipay.api.domain.ExtendParams
     * @auther: 11432_000
     * @date: 2018/9/6 13:26
     */
    private  ExtendParams getExtendParams(AlipayExtendParams alipayExtendParams){

        try {
            ExtendParams extendParams = new ExtendParams();
            Field[] fields = alipayExtendParams.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                //将非空属性设置到ExtendParams
                if (field.get(alipayExtendParams) != null && StringUtils.isNotBlank(String.valueOf(field.get(alipayExtendParams)))){
                    setAttributeByAttributeName(extendParams,field.getName(),String.valueOf(field.get(alipayExtendParams)));
                }
            }
            return extendParams;
        }catch (Exception e){
            LOGGER.error("设置ExtendParams时出错");
            return null;
        }
    }

    /**
     *
     * 功能描述: Field 根据属性名调用set方法
     *
     * @param: [x, attributeName, attributeValue]
     * @return: void
     * @auther: 11432_000
     * @date: 2018/9/6 13:26
     */
    public  void setAttributeByAttributeName(Object x ,String attributeName ,Object attributeValue){
        try {
            //获取属性名对应的反射
            Field field = x.getClass().getDeclaredField(attributeName);
            //设置访问权限，访问私有属性
            field.setAccessible(true);
            //设置属性的值
            field.set(x,attributeValue);
        }catch (Exception e){
            //写入日志 日志输出的三个级别debug，info，error。
            LOGGER.debug(e.getMessage());
        }
    }

    /**
     *
     * 功能描述: Field 根据属性名调用get方法
     *
     * @param: [x, attributeName]
     * @return: java.lang.Object
     * @auther: 11432_000
     * @date: 2018/9/6 13:27
     */
    public  Object getAttributeByAttributeName(Object x ,String attributeName){
        String string = null;
        try {
            //获取属性名对应的反射
            Field field = x.getClass().getDeclaredField(attributeName);
            //设置访问权限，访问私有属性
            field.setAccessible(true);
            //设置属性的值
            string = String.valueOf(field.get(x));
        }catch (Exception e){
            //写入日志 日志输出的三个级别debug，info，error。
            LOGGER.debug(e.getMessage());
        }
        return string;
    }

    /**
     *
     * 功能描述: 必需参数校验，调用扩展参数校验
     *
     * @param: [payPojo, extendParams, promoParams, externalUserInfo]
     * @return: boolean
     * @auther: 11432_000
     * @date: 2018/9/6 13:28
     */
    private  boolean checkEssentialAlipayPayPojo(AlipayPayInfo payPojo , AlipayExtendParams extendParams , AlipayExternalUserInfo externalUserInfo){
        boolean flag = true;
        //判断必需参数是否为空,subject,out_trade_no,total_amount,product_code
        if (StringUtils.isNotBlank(payPojo.getSubject()) && StringUtils.isNotBlank(payPojo.getOutTradeNo()) && StringUtils.isNotBlank(payPojo.getTotalAmount())){
            flag = false;
        }else {
            if (!PRODUCT_CODE_VALUE.equals(payPojo.getProductCode())){
                flag = false;
            }
            double gold = Double.valueOf(payPojo.getTotalAmount());
            if (!(gold > GOLD_MIN && gold < GOLD_MAX && payPojo.getTotalAmount().length() < PayEnum.NINE.getValue())){
                flag = false;
            }
            if (payPojo.getSubject().length() > PayEnum.TWO_FIVE_SIX.getValue()){
                flag = false;
            }
            if (payPojo.getOutTradeNo().length() > PayEnum.SIX_FOUR.getValue()){
                flag = false;
            }
        }
        if (!checkNotEssentialAlipayPayPojo(payPojo ,extendParams ,externalUserInfo)){
            flag = false;
        }
        return flag;
    }

    /**
     *
     * 功能描述: 扩展参数校验
     *
     * @param: [payPojo, extendParams, promoParams, externalUserInfo]
     * @return: boolean
     * @auther: 11432_000
     * @date: 2018/9/6 13:28
     */
    private  boolean checkNotEssentialAlipayPayPojo(AlipayPayInfo payPojo , AlipayExtendParams extendParams , AlipayExternalUserInfo externalUserInfo){
        boolean flag = true;
        if (StringUtils.isNotBlank(payPojo.getBody()) && payPojo.getBody().length() > PayEnum.ONE_TWO_EIGHT.getValue()){
            flag = false;
        }
        if(StringUtils.isNotBlank(payPojo.getTimeoutExpress()) && payPojo.getTimeoutExpress().length() > PayEnum.SIX.getValue()){
            flag = false;
        }
        if (StringUtils.isNotBlank(payPojo.getTimeoutExpress()) && !checkTimeoutExpress(payPojo.getTimeoutExpress())){
            flag = false;
        }
        if (StringUtils.isNotBlank(payPojo.getTimeoutExpress()) && !VIRTUAL_COMMODITY.equals(payPojo.getGoodsType()) && !PHYSICAL_COMMODITY.equals(payPojo.getGoodsType())){
            flag = false;
        }
        if (StringUtils.isNotBlank(payPojo.getPassbackParams())){
            //将url转码
            try {
                payPojo.setPassbackParams(URLEncoder.encode(payPojo.getPassbackParams(),ENCODING_METHOD));
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("转码错误"+ e.getMessage());
            }
            if (payPojo.getPassbackParams().length() > PayEnum.FIVE_ONE_TWO.getValue()){
                flag = false;
            }
        }
        if (StringUtils.isNotBlank(payPojo.getPromoParams()) && payPojo.getPromoParams().length() > PayEnum.FIVE_ONE_TWO.getValue()){
            flag = false;
        }
        if (payPojo.isExtendParams() && !checkExtendParams(extendParams)){
            flag = false;
        }
        if (StringUtils.isNotBlank(payPojo.getPassbackParams()) && !checkPayChannels(payPojo.getEnablePayChannels())){
            flag = false;
        }
        if (StringUtils.isNotBlank(payPojo.getDisablePayChannels()) && !checkPayChannels(payPojo.getDisablePayChannels())){
            flag = false;
        }
        if (payPojo.getEnablePayChannels() != null && payPojo.getDisablePayChannels() != null && !checkPayChannelsMutex(payPojo.getEnablePayChannels(),payPojo.getDisablePayChannels())){
            flag = false;
        }
        if (StringUtils.isNotBlank(payPojo.getStoreId()) && payPojo.getStoreId().length() > PayEnum.THREE_TWO.getValue()){
            flag = false;
        }
        if (payPojo.isExtUserInfo() && !checkExtUserInfo(externalUserInfo)){
            flag = false;
        }
        return flag;
    }

    /**
     *
     * 功能描述: timeout_express参数校验
     *
     * @param: [time]
     * @return: boolean
     * @auther: 11432_000
     * @date: 2018/9/6 13:29
     */
    private  boolean checkTimeoutExpress(String time){

        String rule = "[1-9][0-9]+[mhcd]";
        boolean flag = Pattern.matches(rule, time);
        if (flag){
            String unit  = time.replaceAll("[^a-z]", "");
            int value = Integer.valueOf(time.replaceAll("[^0-9]",""));
            if (TimeUnit.MINUTE.getUnit().equals(unit) && value > TIMEOUT_EXPRESS_MAX_MINUTE){
                flag = false;
            }if (TimeUnit.HOUR.getUnit().equals(unit) && value > TIMEOUT_EXPRESS_MAX_HOUR){
                flag = false;
            }if (TimeUnit.DAY.getUnit().equals(unit) && value > TIMEOUT_EXPRESS_MAX_DAY){
                flag = false;
            }if (TimeUnit.SAME_DAY.getUnit().equals(unit) && value != THIS_DAY){
                flag = false;
            }
        }
        return  flag;
    }

    /**
     *
     * 功能描述: extend_params参数检验
     *
     * @param: [extendParams]
     * @return: boolean
     * @auther: 11432_000
     * @date: 2018/9/6 13:29
     */
    private  boolean checkExtendParams(AlipayExtendParams extendParams){
        boolean flag = true;
        if (StringUtils.isNotBlank(extendParams.getSysServiceProviderId()) && extendParams.getSysServiceProviderId().length() > PayEnum.SIX_FOUR.getValue()){
            flag = false;
        }
        if (StringUtils.isNotBlank(extendParams.getHbFqNum()) && !STAGING_NUMBER[0].equals(extendParams.getHbFqNum()) && !STAGING_NUMBER[1].equals(extendParams.getHbFqNum()) && !STAGING_NUMBER[2].equals(extendParams.getHbFqNum())){
            flag = false;
        }
        if (StringUtils.isNotBlank(extendParams.getHbFqSellerPercent()) && !BUSINESS.equals(extendParams.getHbFqSellerPercent()) && !CUSTOMER.equals(extendParams.getHbFqSellerPercent())){
            flag = false;
        }
        return flag;
    }

    /**
     *
     * 功能描述: 渠道参数检验
     *
     * @param: [channel]
     * @return: boolean
     * @auther: 11432_000
     * @date: 2018/9/6 13:30
     */
    private  boolean checkPayChannels(String channel){

        boolean flag = false;
        String[] channels = channel.split(",");
        //校验渠道是否合法
        for (String chan : channels) {
            for (String channelOfPayments : CHANNEL_OF_PAYMENTS) {
                if (chan.equals(channelOfPayments)){
                    flag = true;
                    break;
                }
            }
            if (flag){
                break;
            }
        }
        return flag;
    }

    /**
     *
     * 功能描述: 可用渠道，禁用渠道互斥检验
     *
     * @param: [channel_1, channel_2]
     * @return: boolean
     * @auther: 11432_000
     * @date: 2018/9/6 13:31
     */
    public  boolean checkPayChannelsMutex(String channel1, String channel2){

        boolean flag = true;
        String[] channels1 = channel1.split(",");
        String[] channels2 = channel2.split(",");
        //互斥检验
        for (String channelOne : channels1) {
            for (String channelTwo : channels2) {
                if (channelOne.equals(channelTwo)){
                    flag = false;
                    break;
                }
            }
            if (!flag){
                break;
            }
        }
        return flag;
    }

    /**
     *
     * 功能描述: ext_user_info参数校验
     *
     * @param: [userInfo]
     * @return: boolean
     * @auther: 11432_000
     * @date: 2018/9/6 13:31
     */
    private  boolean checkExtUserInfo(AlipayExternalUserInfo userInfo){

        boolean flag = true;
        //证件种类数组
        if (StringUtils.isNotBlank(userInfo.getName()) && userInfo.getName().length() > PayEnum.ONE_SIX.getValue()){
            flag = false;
        }
        if (StringUtils.isNotBlank(userInfo.getMobile()) && userInfo.getMobile().length() > PayEnum.TWO_ZERO.getValue()){
            flag = false;
        }
        if (StringUtils.isNotBlank(userInfo.getCertType())){
            //证件类型检验
            for (String s : CARD_TYPE){
                if (s.equals(userInfo.getCertType())){
                    flag = false;
                    break;
                }
            }
        }
        if (StringUtils.isNotBlank(userInfo.getCertNo()) && userInfo.getCertNo().length() > PayEnum.SIX_FOUR.getValue()){
            flag = false;
        }
        if (StringUtils.isNotBlank(userInfo.getMinAge())){
            Integer age = Integer.valueOf(userInfo.getMinAge());
            if (age > 0 && age < PayEnum.ONE_FIVE_ZERO.getValue()){
                flag = false;
            }
        }
        if (StringUtils.isNotBlank(userInfo.getFixBuyer()) && !FIX_BUYER_TRUE.equals(userInfo.getFixBuyer()) && !FIX_BUYER_FALSE.equals(userInfo.getFixBuyer())){
            flag = false;
        }
        if (StringUtils.isNotBlank(userInfo.getNeedCheckInfo()) && !NEED_CHECK_INFO_TRUE.equals(userInfo.getNeedCheckInfo()) && !NEED_CHECK_INFO_FALSE.equals(userInfo.getNeedCheckInfo())){
            flag = false;
        }
        if (StringUtils.isNotBlank(userInfo.getNeedCheckInfo()) && !NEED_CHECK_INFO_TRUE.equals(userInfo.getNeedCheckInfo())){
            LOGGER.info("警告：一些参数可能无效！");
        }
        return flag;
    }

    /**
     *
     * 功能描述: 与支付宝关键key的重名检查（String类型）
     *
     * @param: [payPojo]
     * @return: boolean
     * @auther: 11432_000
     * @date: 2018/9/6 13:32
     */
    public  boolean checkAlipayKeyAboutValue(AlipayPayInfo payPojo){

        boolean flag = true;
        String[] keys = {"body","subject","out_trade_no","timeout_express","total_amount","product_code","goods_type",
        "passback_params","promo_params","extend_params","enable_pay_channels","disable_pay_channels","store_id",
        "ext_user_info"};
        Field[] fields = payPojo.getClass().getDeclaredFields();
        try{
            for (Field field : fields){
                field.setAccessible(true);
                if ("extUserInfo".equals(field.getName()) || "extendParams".equals(field.getName())){continue;}
                if (field.get(payPojo) != null && StringUtils.isNotBlank(String.valueOf(field.get(payPojo)))){
                    for (int i = 0; i < keys.length; i++) {
                        if (String.valueOf(field.get(payPojo)).indexOf(keys[i]) != -1){
                            flag = false;
                        }
                    }
                }
            }
        }catch (Exception e){
            LOGGER.error("查重错误");
            return false;
        }
        return flag;
    }

}
