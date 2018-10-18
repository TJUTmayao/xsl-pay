/**
 * 说明：
 *
 * @Auther: 11432_000
 * @Date: 2018/10/8 13:16
 * @Description:
 */
public enum TestEnum implements EnumInterface{
    ONE("我是一号"){
        @Override
        public void show(){
            System.out.println("我是一");
        }
    },TOW("我是二号"){
        @Override
        public void show(){
            System.out.println("我是2");
        }
    };

    public static void shoe(){

    }

    private String value;

    TestEnum(String value){this.value = value;}

    public String getValue(){return this.value;}
}
