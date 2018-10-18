package cn.tjut.xsl.enums;

/**
 * 说明：时间单位
 * @Auther: 11432_000
 * @Date: 2018/9/13 15:05
 * @Description:
 */
public enum TimeUnit {
    MINUTE("m"),HOUR("h"),DAY("d"),SAME_DAY("c");
    private final String unit;
    TimeUnit (String unit){
        this.unit = unit;
    }

    public String getUnit(){
        return unit;
    }
}
