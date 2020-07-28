package ltd.inmind.accelerator.config;

import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.security.filter.JwtAuthWebFilter;
import ltd.inmind.accelerator.security.repository.JwtTokenServerSecurityContextRepository;
import ltd.inmind.accelerator.security.service.AcceleratorReactiveUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    @Autowired
    private ServerAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private ServerAuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .cors()

                .and()
                .csrf().disable()
                .httpBasic().disable()
                .formLogin().disable()
                .securityContextRepository(securityContextRepository())
                .addFilterAt(jwtAuthWebFilter(), SecurityWebFiltersOrder.FORM_LOGIN)

                .authorizeExchange()
                .pathMatchers("/login/lookup","/login/signUp")
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

    @Bean
    public ServerSecurityContextRepository securityContextRepository() {
        return new JwtTokenServerSecurityContextRepository();
    }

    private JwtAuthWebFilter jwtAuthWebFilter() {

        return new JwtAuthWebFilter(reactiveAuthenticationManager(), securityContextRepository(),
                authenticationSuccessHandler, authenticationFailureHandler);
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(){

        return new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService());
    }

}
