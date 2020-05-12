package ltd.inmind.accelerator.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ltd.inmind.accelerator.exception.AcceleratorException;
import ltd.inmind.accelerator.mapper.UserAccountMapper;
import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.service.IUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static ltd.inmind.accelerator.constants.ExceptionConst.USER_ALREADY_EXIST;

@Service
public class UserAccountServiceImpl implements IUserAccountService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserAccountMapper userAccountMapper;


    @Override
    public UserAccount getByAccount(String account) {

        return userAccountMapper.selectOne(Wrappers.<UserAccount>lambdaQuery()
                .eq(UserAccount::getAccount, account));
    }

    @Override
    public void register(UserAccount userAccount) {

        UserAccount _userAccount = getByAccount(userAccount.getAccount());
        if (_userAccount != null)
            throw new AcceleratorException(USER_ALREADY_EXIST);

        String password = passwordEncoder.encode(userAccount.getPassword());
        userAccount.setPassword(password);

        userAccount.setPhoneToLogin(0L);
        userAccount.setTwoStepVerify(0L);

        userAccountMapper.insert(userAccount);
    }
}
