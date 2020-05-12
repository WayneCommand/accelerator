package ltd.inmind.accelerator.constants;

import java.util.HashMap;
import java.util.Map;

public interface ExceptionConst {

    String USER_ALREADY_EXIST = "LOGIN.USER_ALREADY_EXIST"; //用户已存在
    String SYSTEM_BUG = "SYS.ERROR";


    //这个地方以后会做成国际化的用法
    Map<String, String> CODE_MSG = new HashMap<String, String>() {{
        put("LOGIN.USER_ALREADY_EXIST", "用户已存在。");
        put("SYS.ERROR", "系统错误。");
    }};



}
