package ltd.inmind.accelerator;

import ltd.inmind.accelerator.mapper.Oauth2ClientMapper;
import ltd.inmind.accelerator.mapper.UserMapper;
import ltd.inmind.accelerator.model.Oauth2Client;
import ltd.inmind.accelerator.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MapperTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private Oauth2ClientMapper oauth2ClientMapper;

    @Test
    public void selectAll(){
        List<User> users = userMapper.selectList(null);

        List<Oauth2Client> oauth2Clients = oauth2ClientMapper.selectList(null);

        System.out.println(users);
    }


    @Test
    public void testInsert(){

        User user = new User();
        user.setUsername("test1");
        user.setPassword("111111");
        user.setSalt("sss");
        user.setCreateTime(new Date());

        int insert = userMapper.insert(user);
        System.out.println(user);
        System.out.println(insert);

    }

    @Test
    public void testUpdate(){
        List<User> users = userMapper.selectList(null);

        User user = users.get(0);

        user.setPassword("zddasdas");
        user.setSalt("zzzzzzz");

        int update = userMapper.updateById(user);
        System.out.println(update);


    }
}
