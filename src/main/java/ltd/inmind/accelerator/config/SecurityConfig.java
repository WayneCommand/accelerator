package ltd.inmind.accelerator.config;

import ltd.inmind.accelerator.security.jwt.*;
import ltd.inmind.accelerator.security.jwt.handler.JwtAuthenticationFailureHandler;
import ltd.inmind.accelerator.security.jwt.handler.JwtAuthenticationSuccessHandler;
import ltd.inmind.accelerator.security.jwt.provider.JwtAuthenticationProvider;
import ltd.inmind.accelerator.security.jwt.repository.JwtSecurityContextRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;

@EnableWebSecurity
public class SecurityConfig {

    @Configuration
    @Order(1)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Autowired
        @Qualifier("userDetailsServiceImpl")
        private UserDetailsService userDetailsService;


        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userDetailsService);
            auth.setSharedObject(SecurityContextRepository.class, JwtSecurityContextRepository.getInstance());
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    //关掉csrf方便httpclient调试和以后的jwt token
                    .csrf().disable()

                    .formLogin()

                    //认证失败后的行为
                    .failureHandler(new JwtAuthenticationFailureHandler())

                    //认证成功后的行为
                    .successHandler(new JwtAuthenticationSuccessHandler())

                    .and()
                    //所有的请求都需要认证
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated()

                    //无状态session
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                    //JWT相关配置
                    .and()
                    .apply(new JwtManagementConfigurer<>())

                    //JWT认证Provider
                    .and()
                    .authenticationProvider(new JwtAuthenticationProvider());

        }
    }

    @Configuration
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/**")
                    .authorizeRequests()
                    .anyRequest().hasRole("ADMIN")
                    .and()
                    .httpBasic();
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


}
