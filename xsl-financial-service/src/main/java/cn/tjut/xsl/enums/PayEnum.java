package cn.tjut.xsl.enums;

/**
 * 说明：长度限制
 * @Auther: 11432_000
 * @Date: 2018/9/13 14:49
 * @Description:
 */
public enum PayEnum {
    SIX(6),NINE(9),TWO_FIVE_SIX(256),SIX_FOUR(64),ONE_TWO_EIGHT(128),FIVE_ONE_TWO(512),
    THREE_TWO(32),ONE_SIX(16),TWO_ZERO(20),ONE_FIVE_ZERO(150),ONE_ZERO_ZERO(100),FIVE_ZERO_ZERO_ZERO_ZERO(50000);

    private final int value;

    PayEnum(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
