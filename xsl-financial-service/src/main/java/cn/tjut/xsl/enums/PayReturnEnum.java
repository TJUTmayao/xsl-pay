package cn.tjut.xsl.enums;

/**
 * 说明：返回码
 * @Auther: 11432_000
 * @Date: 2018/9/13 13:32
 * @Description:
 */
public enum  PayReturnEnum {
    SUCCESS("10000"),SERVICE_NOT_AVAILABLE("20000"),INSUFFICIIENT_AUTHORIZED_AUTHORITY("20001"),
    MISSING_REQUIRED_ARGUMENTS("40001"),FAILURE_OF_BUSINESS_PROCESS("40004"),LACK_OF_AUTHORITY("40006");

    private final String value;

    PayReturnEnum (String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
