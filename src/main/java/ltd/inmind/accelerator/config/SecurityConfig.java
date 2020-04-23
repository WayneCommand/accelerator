package ltd.inmind.accelerator.config;

import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.security.service.AcceleratorReactiveUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .cors().and()

                .httpBasic()
                .securityContextRepository(new WebSessionServerSecurityContextRepository())


                .and()
                .formLogin()
                .authenticationSuccessHandler((webFilterExchange, auth) -> {
                    ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                    response.setStatusCode(HttpStatus.OK);
                    response.getHeaders()
                            .add("Content-Type", "application/json");

                    String msg = "{\"state\":\"success\"}";

                    DataBuffer buffer = response.bufferFactory().wrap(msg.getBytes(StandardCharsets.UTF_8));

                    return response.writeWith(Mono.just(buffer));
                })
                .authenticationFailureHandler((webFilterExchange, exception) -> {

                    ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                    response.setStatusCode(HttpStatus.OK);
                    response.getHeaders()
                            .add("Content-Type", "application/json");

                    String msg = "{\"state\":\"failed\"}";

                    DataBuffer buffer = response.bufferFactory().wrap(msg.getBytes(StandardCharsets.UTF_8));

                    return response.writeWith(Mono.just(buffer));
                })

                .and()
                .authorizeExchange()
                .anyExchange()
                .authenticated();

        return http.build();
    }

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService(){
        return new AcceleratorReactiveUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


}
