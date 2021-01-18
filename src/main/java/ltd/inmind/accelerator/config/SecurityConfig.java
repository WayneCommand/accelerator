package ltd.inmind.accelerator.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.security.filter.JwtAuthWebFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

@EnableWebFluxSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig {

    private final ServerAuthenticationSuccessHandler authenticationSuccessHandler;

    private final ServerAuthenticationFailureHandler authenticationFailureHandler;

    private final ReactiveUserDetailsService reactiveUserDetailsService;

    private final ServerSecurityContextRepository serverSecurityContextRepository;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .cors()

                .and()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .securityContextRepository(serverSecurityContextRepository)
                .addFilterAt(jwtAuthWebFilter(), SecurityWebFiltersOrder.FORM_LOGIN)
                .authorizeExchange(a -> a
                        .pathMatchers("/login/lookup", "/login/signUp", "/oauth/2/*")
                        .permitAll()
                )
                .authorizeExchange(a -> a
                        .anyExchange()
                        .authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private JwtAuthWebFilter jwtAuthWebFilter() {

        return new JwtAuthWebFilter(reactiveAuthenticationManager(), serverSecurityContextRepository,
                authenticationSuccessHandler, authenticationFailureHandler);
    }

    private ReactiveAuthenticationManager reactiveAuthenticationManager(){

        return new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
    }

}
