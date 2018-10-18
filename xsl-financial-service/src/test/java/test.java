import cn.tjut.xsl.enums.PayEnum;
import cn.xsl.mapper.XslOrderMapper;
import cn.xsl.pojo.XslOrder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pojo.AlipayTransferInfo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class test {
    private static final Logger LOGGER = LoggerFactory.getLogger(test.class);

    @Test
    public void teatBody(){
        //初始化容器
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-dao.xml");
        //从容器中取bean
        XslOrderMapper xslOrderMapper = applicationContext.getBean(XslOrderMapper.class);
        if (xslOrderMapper == null){
            System.out.println("null");
        }
        XslOrder xslOrder = xslOrderMapper.selectByPrimaryKey(11);

    }

    @Test
    public void test4(){
        AlipayTransferInfo alipayTransferInfo = new AlipayTransferInfo();
        alipayTransferInfo.setOutBizNo("sdsfafadsfdsfdfasd");
        alipayTransferInfo.setPayeeAccount("123456789");
        alipayTransferInfo.setAmount("1000.00");
        alipayTransferInfo.setRemark("备注");
        System.out.println("{" +
                "\"out_biz_no\":\"3142321423432\"," +
                "\"payee_type\":\"ALIPAY_LOGONID\"," +
                "\"payee_account\":\"abc@sina.com\"," +
                "\"amount\":\"12.23\"," +
                "\"payer_show_name\":\"上海交通卡退款\"," +
                "\"payee_real_name\":\"张三\"," +
                "\"remark\":\"转账备注\"" +
                "}");
    }



    @Test
    public void test() {

        LOGGER.debug("debug");
        LOGGER.error("error");
    }
    private static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 10);
        return key;
    }

    @Test
    public void test5(){
        String rull = "[0-9a-zA-Z-_]";
        String s = "fadgfadgdfsdgkbfdks-_--_bvubf,dsu";
        String m = s.replaceAll(rull,"");
        System.out.println(m);
        //枚举类遍历方法：values()
        for (PayEnum anEnum : PayEnum.values()){
            System.out.println(anEnum.getValue());
        }

    }


//    public void test2(){
//        Connection conn = null;
//        String sql;
//        // MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
//        // 避免中文乱码要指定useUnicode和characterEncoding
//        // 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
//        // 下面语句之前就要先创建javademo数据库
//        String url = "jdbc:mysql://localhost:3306/xsl?" + "user=root&password=123456&useUnicode=true&characterEncoding=UTF8";
//        try {
//            // 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
//            // 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以
//            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
//            // or:
//            // com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
//            // or：
//            // new com.mysql.jdbc.Driver();
//
//            System.out.println("成功加载MySQL驱动程序");
//            // 一个Connection代表一个数据库连接
//            conn = DriverManager.getConnection(url);
//            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
//            Statement stmt = conn.createStatement();
//            sql = "select * from xsl_task";
//            ResultSet resultSet = stmt.executeQuery(sql);// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
//            System.out.println("查完了");
//            if (resultSet.next()){
//                System.out.println(resultSet.getString("money"));
//            }
//
//    }catch (Exception e){
//
//            System.out.println("出错了");
//        }
//    }

    @Test
    public void test6(){

        System.out.println(TestEnum.ONE.name());
        System.out.println(TestEnum.ONE.ordinal());
        System.out.println(TestEnum.ONE.toString());
        System.out.println(TestEnum.ONE.getValue());
        System.out.println(TestEnum.valueOf("ONE"));
        TestEnum.ONE.show();
        try{
            InetAddress localHost = Inet4Address.getLocalHost();
            System.out.println();
            System.out.println(localHost);
        }catch (Exception e){

        }

    }
}
