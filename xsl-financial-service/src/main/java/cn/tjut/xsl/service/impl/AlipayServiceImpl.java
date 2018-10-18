package cn.tjut.xsl.service.impl;

import cn.tjut.xsl.enums.PayReturnEnum;
import cn.tjut.xsl.service.AlipayService;
import cn.tjut.xsl.utils.AlipayAppPayUtils;
import cn.tjut.xsl.utils.AlipayTransferUtils;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pojo.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class AlipayServiceImpl implements AlipayService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlipayServiceImpl.class);

    @Resource
    private AlipayAppPayUtils alipayAppPayUtils;
    @Resource
    private AlipayTransferUtils alipayTransferUtils;
    @Resource
    private AlipayServiceUtils alipayServiceUtils;


    @Value("${APP_ALIPAY_PRIVATE_KEY}")
    private String APP_ALIPAY_PRIVATE_KEY;
    @Value("${APP_ALIPAY_PUBLIC_KEY}")
    private String APP_ALIPAY_PUBLIC_KEY;
    @Value("${APP_ID}")
    private String APP_ID;
    @Value("${GATEWAY}")
    private String GATEWAY;
    @Value("${SIGN_TYPE}")
    private String SIGN_TYPE;
    @Value("${CHARSET}")
    private String CHARSET;
    @Value("${ALIPAY_RETURN_URL}")
    private String ALIPAY_RETURN_URL;
    @Value("${ALIPAY_NOTIFY_URL}")
    private String ALIPAY_NOTIFY_URL;

    private static final String FORMAT = "JSON";
    /**订单状态,完成*/
    private String TRADE_FINISHED = "TRADE_FINISHED";
    /**订单状态，成功*/
    private String TRADE_SUCCESS = "TRADE_SUCCESS";
    /**订单状态*/
    private String TRADE_STATUS = "trade_status";
    /**订单号的key*/
    private String OUT_TRADE_NO = "out_trade_no";
    /**交易号的key*/
    private String TRADE_NO = "trade_no";

    /**
     *
     * 功能描述: 订单生成（网页版），测试用，无实际意义。
     *
     * @param: [payInfo]
     * @return: java.lang.String
     * @auther: 11432_000
     * @date: 2018/9/6 14:32
     */
   /* public String userPayByPage(Map payInfo) {

        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY, APP_ID, APP_ALIPAY_PRIVATE_KEY, FORMAT, CHARSET, APP_ALIPAY_PUBLIC_KEY, SIGN_TYPE);
        AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel payModel = new AlipayTradePagePayModel();
        payRequest.setBizContent("{\"out_trade_no\":\""+ payInfo.get("out_trade_no") +"\","
                + "\"total_amount\":\""+ payInfo.get("total_amount") +"\","
                + "\"subject\":\""+ payInfo.get("subject") +"\","
                + "\"timeout_express\":\"10m\","
                + "\"body\":\""+ payInfo.get("body") +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        AlipayTradePagePayResponse response = null;
        payRequest.setNotifyUrl(ALIPAY_NOTIFY_URL);
        payRequest.setReturnUrl(ALIPAY_RETURN_URL);
        try{
            response = alipayClient.pageExecute(payRequest);
        }catch (Exception e){
            LOGGER.error("支付提交错误");
        }
        return response.getBody();
    }*/

    @Override
    /**
     *
     * 功能描述: 向支付宝发送请求，并返回订单信息或错误信息
     *
     * @param: [taskId, payInfo, extendParams, externalUserInfo]
     * @return: pojo.AlipayReturn
     * @auther: 11432_000
     * @date: 2018/9/11 16:38
     */
    public AlipayPayRerurn userPayByApp(int taskId, AlipayPayInfo payInfo, AlipayExtendParams extendParams, AlipayExternalUserInfo externalUserInfo) {
        AlipayPayRerurn payRerurn = new AlipayPayRerurn();
        AlipayTradeAppPayResponse response = getAppPayResponse(taskId, payInfo, extendParams, externalUserInfo);
        if (response == null){
            payRerurn.setIsSuccess("F");
            payRerurn.setMsg("无响应，获得响应时出错");
            return payRerurn;
        }
        if (!PayReturnEnum.SUCCESS.getValue().equals(response.getCode())){
            payRerurn.setIsSuccess("F");
            payRerurn.setMsg(response.getMsg());
            payRerurn.setSubMsg(response.getSubMsg());
            return payRerurn;
        }
        payRerurn.setIsSuccess("T");
        payRerurn.setOrderInfo(response.getBody());
        return payRerurn;
    }
    /**
     *
     * 功能描述:向支付宝发送请求，并返回订单信息或错误信息
     *
     * @param: [taskId, payInfo]
     * @return: pojo.AlipayPayRerurn
     * @auther: 11432_000
     * @date: 2018/9/18 13:48
     */
    @Override
    public AlipayPayRerurn userPayByApp(int taskId, AlipayPayInfo payInfo) {
        AlipayPayRerurn payRerurn = new AlipayPayRerurn();
        AlipayTradeAppPayResponse response = getAppPayResponse(taskId, payInfo, null, null);
        if (response == null){
            payRerurn.setIsSuccess("F");
            payRerurn.setMsg("无响应，获得响应时出错");
            return payRerurn;
        }
        if (!PayReturnEnum.SUCCESS.getValue().equals(response.getCode())){
            payRerurn.setIsSuccess("F");
            payRerurn.setMsg(response.getMsg());
            payRerurn.setSubMsg(response.getSubMsg());
            return payRerurn;
        }
        payRerurn.setIsSuccess("T");
        payRerurn.setOrderInfo(response.getBody());
        return payRerurn;
    }

    /**
     *
     * 功能描述: 提交转账请求，返回转账信息或错误信息。
     *
     * @param: [transferInfo, taskId]
     * @return: pojo.AlipayTransferReturn
     * @auther: 11432_000
     * @date: 2018/9/13 14:19
     */
    @Override
    public AlipayTransferReturn commissionPayment(AlipayTransferInfo transferInfo, int taskId) {

        AlipayTransferReturn transferReturn = new AlipayTransferReturn();
        AlipayFundTransToaccountTransferResponse response = getTransferResponse(transferInfo,taskId);
        if (response == null){
            transferReturn.setIsSuccess("F");
            transferReturn.setMsg("无响应，获得响应时出错");
            return transferReturn;
        }
        if (!PayReturnEnum.SUCCESS.getValue().equals(response.getCode())){
            transferReturn.setIsSuccess("F");
            transferReturn.setMsg(response.getMsg());
            transferReturn.setSubMsg(response.getSubMsg());
            return transferReturn;
        }
        transferReturn.setIsSuccess("T");
        transferReturn.setOrderId(response.getOrderId());
        transferReturn.setPayDate(response.getPayDate());
        transferReturn.setOutBizNo(response.getOutBizNo());
       return transferReturn;
    }

    @Override
    /**
     *
     * 功能描述: 支付宝异步通知处理,异常返回null。
     *
     * @param: [requestParams]
     * @return: java.lang.String
     * @auther: 11432_000
     * @date: 2018/9/9 13:58
     */
    public AlipayNotifyInfo alipayNotify(Map requestParams) {
        if (requestParams == null){
            return null;
        }
        AlipayNotifyInfo notifyInfo = new AlipayNotifyInfo();
        Map<String,String> map = new HashMap<String, String>();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            //接收返回的参数，类似于biz_content（含有多个小参数）
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
//            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            map.put(name, valueStr);
        }
        try{
            //rsaCheckV1验签方法主要用于支付接口的返回参数的验签比如：当面付，APP支付，手机网站支付，电脑网站支付 这些接口都是使用rsaCheckV1方法验签的
            boolean flag = AlipaySignature.rsaCheckV1(map, APP_ALIPAY_PUBLIC_KEY, CHARSET, SIGN_TYPE);
            // 获取支付宝的通知返回参数
            if(flag){
                if (!TRADE_FINISHED.equals(map.get(TRADE_STATUS)) && !TRADE_SUCCESS.equals(map.get(TRADE_STATUS))) {
                    LOGGER.error( "支付宝返回的交易状态不正确（trade_status=" + map.get("trade_status") + "）");
                    notifyInfo.setIsSuccess("F");
                    notifyInfo.setMsg( "交易失败：（trade_status=" + map.get("trade_status") + "）");
                    return notifyInfo;
                }
                notifyInfo.setIsSuccess("T");
                notifyInfo.setOrderNo(map.get(OUT_TRADE_NO));
                notifyInfo.setTradeNo(map.get(TRADE_NO));
            }
        }catch (Exception e){
            LOGGER.error(e.getMessage());
            return null;
        }
        return notifyInfo;
    }

    /**
     *
     * 功能描述: 获取支付宝的支付响应，出错返回null
     *
     * @param: [payInfo, extendParams, externalUserInfo]
     * @return: com.alipay.api.response.AlipayTradeAppPayResponse
     * @auther: 11432_000
     * @date: 2018/9/11 12:57
     */
    private AlipayTradeAppPayResponse getAppPayResponse(int taskId, AlipayPayInfo payInfo , AlipayExtendParams extendParams , AlipayExternalUserInfo externalUserInfo) {
        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY, APP_ID, APP_ALIPAY_PRIVATE_KEY, FORMAT, CHARSET, APP_ALIPAY_PUBLIC_KEY, SIGN_TYPE);
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest appPayRequest = new AlipayTradeAppPayRequest();
        if (payInfo != null){
            alipayServiceUtils.getMoneyAndOrderIdAndBody(payInfo,taskId);
        }
        //设置请求参数Model,(或者自行拼接biz_content)
        AlipayTradeAppPayModel payModel = null;
        if (payInfo != null){
             payModel = alipayAppPayUtils.getAppPayModelByPayInfo(payInfo,extendParams,externalUserInfo);
        }
        if (payModel == null){
            return null;
        }
        appPayRequest.setBizModel(payModel);
        //设置异步通知页面，仅在交易状态发生改变时调用
        appPayRequest.setNotifyUrl(ALIPAY_NOTIFY_URL);
        //设置同步通知页面（用户通知页面），仅在支付成功后调用一次
//        appPayRequest.setReturnUrl(ALIPAY_RETURN_URL);
        //发送请求
        AlipayTradeAppPayResponse response = null;
        try {
            //app支付请求
            response = alipayClient.sdkExecute(appPayRequest);
        } catch (AlipayApiException e) {
            LOGGER.error("支付提交失败");
            return null;
        }
        return response;
    }
    /**
     *
     * 功能描述:获取支付宝转账响应，出错返回null
     *
     * @param: [transferInfo]
     * @return: com.alipay.api.response.AlipayFundTransToaccountTransferResponse
     * @auther: 11432_000
     * @date: 2018/9/12 16:21
     */
    private AlipayFundTransToaccountTransferResponse getTransferResponse(AlipayTransferInfo info , int taskId){
        AlipayTransferInfo transferInfo = alipayServiceUtils.getTransferInfoByTaskId(taskId, info);
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY, APP_ID, APP_ALIPAY_PRIVATE_KEY, FORMAT, CHARSET, APP_ALIPAY_PUBLIC_KEY, SIGN_TYPE);
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        String bizContent = alipayTransferUtils.getBizContent(transferInfo);
        if (bizContent == null){
            return null;
        }
        request.setBizContent(bizContent);
        AlipayFundTransToaccountTransferResponse response = null;
        try {
            response = alipayClient.execute(request);
        }catch (Exception e){
            LOGGER.error("转账提交出错"+e.getMessage());
            return null;
        }
        return response;
    }

}
