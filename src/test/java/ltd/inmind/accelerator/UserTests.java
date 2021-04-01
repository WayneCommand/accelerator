package ltd.inmind.accelerator;

import ltd.inmind.accelerator.mapper.Oauth2ClientMapper;
import ltd.inmind.accelerator.mapper.UserAccountMapper;
import ltd.inmind.accelerator.security.context.IJwtTokenSecurityContext;
import ltd.inmind.accelerator.service.IUserAccountService;
import ltd.inmind.accelerator.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

//@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserTests {

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IUserService userService;

    @Autowired
    private Oauth2ClientMapper oauth2ClientMapper;

    @Autowired
    private IJwtTokenSecurityContext jwtTokenSecurityContext;

    @Autowired
    private ReactiveUserDetailsService reactiveUserDetailsService;

    @Test
    void selectAll() {
        userAccountMapper.selectList(null)
                .forEach(System.out::println);

        //List<Oauth2Client> oauth2Clients = oauth2ClientMapper.selectList(null);
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
