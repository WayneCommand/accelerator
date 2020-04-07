package ltd.inmind.accelerator.constants;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface LoginConst {

    String AUTHORIZATION_HEADER_NAME = "Authorization";
    String AUTHORIZATION_PREFIX = "Bearer ";

    Map<String, String> TOKEN_SESSION = new ConcurrentHashMap<>();

}
