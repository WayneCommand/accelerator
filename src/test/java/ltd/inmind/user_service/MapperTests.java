package ltd.inmind.user_service;

import ltd.inmind.user_service.dao.UserDao;
import ltd.inmind.user_service.model.User;
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
    private UserDao userDao;

    @Test
    public void selectAll(){
        List<User> users = userDao.selectAll();

        System.out.println(users);
    }


    @Test
    public void testInsert(){

        User user = new User();
        user.setUsername("test1");
        user.setPassword("111111");
        user.setSalt("sss");
        user.setCreateTime(new Date());

        int insert = userDao.insert(user);
        System.out.println(user);
        System.out.println(insert);

    }

    @Test
    public void testUpdate(){
        List<User> users = userDao.selectAll();

        User user = users.get(0);

        user.setPassword("zddasdas");
        user.setSalt("zzzzzzz");

        int update = userDao.update(user);
        System.out.println(update);


    }
}
