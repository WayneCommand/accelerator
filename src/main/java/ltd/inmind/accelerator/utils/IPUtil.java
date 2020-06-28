package ltd.inmind.accelerator.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;

public class IPUtil {

    private static final String UNKNOWN = "unknown";

    protected IPUtil(){

    }

    public static String getIp(ServerHttpRequest request) {

        return request
                .getRemoteAddress()
                .getAddress()
                .getHostAddress();

    }

}
