package ltd.inmind.accelerator.config;

import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.security.service.AcceleratorReactiveUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()

                .httpBasic()

                .and()
                .formLogin()
                .authenticationFailureHandler((webFilterExchange,exception)->{
                    //TODO handler
                    log.error("login failed.", exception);
                    return Mono.empty();
                })

                .and()
                .authorizeExchange()
                .anyExchange().authenticated();

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
