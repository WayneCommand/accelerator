package ltd.inmind.user_service.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ltd.inmind.user_service.constants.LoginConst.SignUpStatusEnum;
import ltd.inmind.user_service.constants.UserConst;
import ltd.inmind.user_service.mapper.UserMapper;
import ltd.inmind.user_service.model.User;
import ltd.inmind.user_service.service.UserService;
import ltd.inmind.user_service.utils.UUIDUtil;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;


    @Override
    public SignUpStatusEnum signUp(User user) {

        try {

            return doSignUp(user);
        } catch (Exception e) {

            LOG.error("user service sign up error", e);
            return SignUpStatusEnum.FAILED;
        }
    }

    @Override
    public User getUserByUsername(String username) {

        return userMapper.selectOne(Wrappers
                .<User>lambdaQuery()
                .eq(User::getUsername, username));
    }

    private SignUpStatusEnum doSignUp(User user) {

        User byUsername = getUserByUsername(user.getUsername());
        if (byUsername != null)
            return SignUpStatusEnum.USER_NAME_ALREADY_EXIST;

        String password = user.getPassword();

        String salt = UUIDUtil.generateShortUuid();

        SimpleHash simpleHash = new SimpleHash(UserConst.USER_PASSWORD_ALGORITHM, password, salt, UserConst.USER_PASSWORD_HASH_ITERATIONS);

        user.setCreateTime(new Date());

        user.setPassword(simpleHash.toHex());

        user.setSalt(salt);

        userMapper.insert(user);

        return SignUpStatusEnum.SUCCESS;
    }



}
