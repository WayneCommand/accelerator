package ltd.inmind.accelerator.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    //关掉csrf方便httpclient调试和以后的jwt token
                    .csrf().disable()

                    .formLogin()

                    //认证失败后的行为
                    .failureHandler((request, response, e) -> {
                        response.setStatus(HttpStatus.OK.value());
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().println("{\"code\":\"2\",\"msg\":\"登陆失败\"}");
                        e.printStackTrace();
                        //TODO 处理异常
                    })

                    //认证成功后的行为
                    .successHandler((request, response, e) -> {
                        response.setStatus(HttpStatus.OK.value());
                        response.setContentType("application/json;charset=UTF-8");
                        response.getWriter().println("{\"code\":\"1\",\"msg\":\"登陆成功\"}");
                    })
                    .and()

                    //所有的请求都需要认证
                    .authorizeRequests()
                    .anyRequest()
                    .authenticated();


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
