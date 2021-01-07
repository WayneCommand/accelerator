package ltd.inmind.accelerator;

import lombok.RequiredArgsConstructor;
import ltd.inmind.accelerator.mapper.Oauth2ClientMapper;
import ltd.inmind.accelerator.mapper.UserAccountMapper;
import ltd.inmind.accelerator.model.oauth2.Oauth2Client;
import ltd.inmind.accelerator.service.IJwtTokenSecurityContext;
import ltd.inmind.accelerator.service.IUserAccountService;
import ltd.inmind.accelerator.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@SpringBootTest
class UserTests {

    private UserAccountMapper userAccountMapper;

    private IUserAccountService userAccountService;

    private IUserService userService;

    private Oauth2ClientMapper oauth2ClientMapper;

    private IJwtTokenSecurityContext jwtTokenSecurityContext;

    private ReactiveUserDetailsService reactiveUserDetailsService;


    @Test
    void selectAll() {
        userAccountMapper.selectList(null)
                .forEach(System.out::println);

        List<Oauth2Client> oauth2Clients = oauth2ClientMapper.selectList(null);
    }


    @Test
    void testRegister() {
        userService.signUp("shenlan","123456");

    }

    @Test
    void testJwtToken() {

        jwtTokenSecurityContext.create(Mono.empty())
                .subscribe(System.out::println);

    }

}
