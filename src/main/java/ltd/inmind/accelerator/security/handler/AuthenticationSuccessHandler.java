package ltd.inmind.accelerator.security.handler;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import ltd.inmind.accelerator.model.po.DeviceToken;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.service.IDeviceTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static ltd.inmind.accelerator.constants.SecurityConst.AUTHENTICATION_HEADER;
import static ltd.inmind.accelerator.constants.SecurityConst.TOKEN_ATTR_NAME;


@AllArgsConstructor
@Component
public class AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Autowired
    private Gson gson;

    @Autowired
    private IDeviceTokenService deviceTokenService;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add("Content-Type", "application/json");
        response.getHeaders().add(AUTHENTICATION_HEADER, webFilterExchange.getExchange().getAttribute(TOKEN_ATTR_NAME));

        saveDeviceToken(webFilterExchange.getExchange().getAttribute(TOKEN_ATTR_NAME),
                webFilterExchange.getExchange().getRequest()
                        .getRemoteAddress().getAddress().getHostAddress(),
                1L);

        DataResponse dataResponse = new DataResponse()
                .success();

        String resp = gson.toJson(dataResponse);

        DataBuffer buffer = response.bufferFactory().wrap(resp.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    private void saveDeviceToken(String token, String ip, Long uId) {
        DeviceToken deviceToken = new DeviceToken();

        deviceToken.setToken(token);
        deviceToken.setIp(ip);
        deviceToken.setUId(uId);
        deviceToken.setActiveCount(0);

        deviceTokenService.saveDeviceToken(deviceToken);
    }
}
