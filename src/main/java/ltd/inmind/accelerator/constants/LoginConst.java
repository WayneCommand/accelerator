package ltd.inmind.accelerator.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginConst {

    public static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    public static final String AUTHORIZATION_PREFIX = "Bearer ";

    public static final Map<String, String> TOKEN_SESSION = new ConcurrentHashMap<>();


    public enum SignUpStatusEnum{
        SUCCESS("S"),
        FAILED("F"),
        USER_NAME_ALREADY_EXIST("UNAE");
        private String value;

        SignUpStatusEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
