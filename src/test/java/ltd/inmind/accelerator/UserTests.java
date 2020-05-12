package ltd.inmind.accelerator;

import ltd.inmind.accelerator.mapper.Oauth2ClientMapper;
import ltd.inmind.accelerator.mapper.UserAccountMapper;
import ltd.inmind.accelerator.model.Oauth2Client;
import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.service.IUserAccountService;
import ltd.inmind.accelerator.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTests {

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IUserService userService;

    @Autowired
    private Oauth2ClientMapper oauth2ClientMapper;

    @Test
    public void selectAll() {
        userAccountMapper.selectList(null)
                .forEach(System.out::println);

        List<Oauth2Client> oauth2Clients = oauth2ClientMapper.selectList(null);
    }


    @Test
    public void testRegister() {
        userService.signUp("shenlan","123456");

    }

}
