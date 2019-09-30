package ltd.inmind.accelerator.config;

import ltd.inmind.accelerator.security.service.JwtMapReactiveUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
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
                    return Mono.empty();
                })

                .and()
                .authorizeExchange()
                .anyExchange().authenticated();

        return http.build();
    }

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService(){
        return new JwtMapReactiveUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
