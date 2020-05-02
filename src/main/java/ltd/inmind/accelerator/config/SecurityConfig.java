package ltd.inmind.accelerator.config;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.security.handler.AuthenticationFailureHandler;
import ltd.inmind.accelerator.security.handler.AuthenticationSuccessHandler;
import ltd.inmind.accelerator.security.service.AcceleratorReactiveUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private Gson gson;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf().disable()
                .cors().and()

                .httpBasic()
                .securityContextRepository(new WebSessionServerSecurityContextRepository())

                .and()
                .formLogin()
                .authenticationSuccessHandler(new AuthenticationSuccessHandler(gson))
                .authenticationFailureHandler(new AuthenticationFailureHandler(gson))

                .and()
                .authorizeExchange()
                .pathMatchers("/login/lookup")
                .permitAll()

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
