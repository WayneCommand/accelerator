package ltd.inmind.user_service.service.impl;

import ltd.inmind.user_service.constant.UserConst;
import ltd.inmind.user_service.dao.UserDao;
import ltd.inmind.user_service.model.User;
import ltd.inmind.user_service.service.UserService;
import ltd.inmind.user_service.utils.UUIDUtil;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;


    @Override
    public String signUp(User user) {

        try {
            String password = user.getPassword();

            String salt = UUIDUtil.generateShortUuid();

            SimpleHash simpleHash = new SimpleHash(UserConst.USER_PASSWORD_ALGORITHM, password, salt, UserConst.USER_PASSWORD_HASH_ITERATIONS);

            user.setCreateTime(new Date());

            user.setPassword(simpleHash.toHex());

            user.setSalt(salt);

            userDao.insert(user);
        } catch (Exception e) {
            return "error";
        }

        return "success";
    }

}
