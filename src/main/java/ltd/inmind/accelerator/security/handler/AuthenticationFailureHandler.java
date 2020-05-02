package ltd.inmind.accelerator.security.handler;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import ltd.inmind.accelerator.model.vo.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@AllArgsConstructor
public class AuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    private Gson gson;

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException e) {

        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders()
                .add("Content-Type", "application/json");

        DataResponse dataResponse;
        if (e instanceof BadCredentialsException){
            dataResponse = new DataResponse()
                    .failed()
                    .msg("用户名或密码错误.");
        }else {
            dataResponse = new DataResponse()
                    .failed()
                    .msg("登陆失败 请稍后再试.");
        }


        String resp = gson.toJson(dataResponse);

        DataBuffer buffer = response.bufferFactory().wrap(resp.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));

    }


}
