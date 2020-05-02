package ltd.inmind.accelerator.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import ltd.inmind.accelerator.model.vo.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


@AllArgsConstructor
public class AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private Gson gson;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {

        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders()
                .add("Content-Type", "application/json");

        DataResponse dataResponse = new DataResponse()
                .success();

        String resp = gson.toJson(dataResponse);

        DataBuffer buffer = response.bufferFactory().wrap(resp.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }
}
