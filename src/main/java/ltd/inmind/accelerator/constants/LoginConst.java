package ltd.inmind.accelerator.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginConst {

    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String AUTHORIZATION_PREFIX = "Bearer ";

    public static final Map<String, String> TOKEN_SESSION = new ConcurrentHashMap<>();

}
