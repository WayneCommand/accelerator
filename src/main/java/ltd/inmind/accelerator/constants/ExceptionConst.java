package ltd.inmind.accelerator.constants;

import java.util.HashMap;
import java.util.Map;

public interface ExceptionConst {

    String USER_ALREADY_EXIST = "LOGIN.USER_ALREADY_EXIST"; //用户已存在
    String USER_NOT_EXIST = "USER.USER_NOT_EXIST";
    String SYSTEM_BUG = "SYS.ERROR";

    String OAUTH2_CLIENT_ID_INVALID = "OAUTH2.CLIENT_ID_INVALID";
    String OAUTH2_SECRET_INVALID = "OAUTH2.SECRET_INVALID";
    String OAUTH2_CODE_EXPIRED = "OAUTH2.CODE_EXPIRED";

    //这个地方以后会做成国际化的用法
    Map<String, String> CODE_MSG = new HashMap<>() {{
        put("LOGIN.USER_ALREADY_EXIST", "用户已存在。");
        put("SYS.ERROR", "系统错误。");
        put("USER.USER_NOT_EXIST", "用户不存在");
    }};



}
