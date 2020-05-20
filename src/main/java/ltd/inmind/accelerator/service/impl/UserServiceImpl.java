package ltd.inmind.accelerator.service.impl;

import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.model.po.UserProfile;
import ltd.inmind.accelerator.model.vo.MyHomePage;
import ltd.inmind.accelerator.model.vo.MyInfo;
import ltd.inmind.accelerator.model.vo.MySafety;
import ltd.inmind.accelerator.service.IUserAccountService;
import ltd.inmind.accelerator.service.IUserProfileService;
import ltd.inmind.accelerator.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserAccountService userAccountService;

    @Autowired
    private IUserProfileService userProfileService;

    @Override
    @Transactional
    public void signUp(String account, String password) {
        UserAccount userAccount = new UserAccount();
        userAccount.setAccount(account);
        userAccount.setPassword(password);

        userAccountService.register(userAccount);

        UserProfile userProfile = new UserProfile();
        userProfile.setUId(userAccount.getUId());

        userProfileService.register(userProfile);
    }

    @Override
    public UserProfile getProfileByAccount(String account) {
        UserAccount userAccount = userAccountService.getByAccount(account);
        if (userAccount == null)
            return null;

        return userProfileService.getByUId(userAccount.getUId());
    }

    @Override
    public UserAccount getAccountByAccount(String account) {
        return userAccountService.getByAccount(account);
    }

    @Override
    public MyInfo getMyInfo(String account) {
        MyInfo myInfo = new MyInfo();

        UserAccount userAccount = userAccountService.getByAccount(account);

        UserProfile userProfile = userProfileService.getByUId(userAccount.getUId());

        myInfo.setUserProfile(userProfile);

        List<String> emails = Stream.of(userAccount.getEmail(),
                userAccount.getRecoveryEmail())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        myInfo.setEmails(emails);

        List<String> phones = Stream.of(userAccount.getPhone())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        myInfo.setPhones(phones);

        myInfo.setPasswordModifyTime(userAccount.getPasswordModifyTime());

        return myInfo;
    }

    @Override
    public MySafety getMySafety(String account) {
        MySafety mySafety = new MySafety();

        UserAccount userAccount = userAccountService.getByAccount(account);
        userAccount.setPassword(null);
        userAccount.setCreateTime(null);
        userAccount.setModifyTime(null);

        mySafety.setUserAccount(userAccount);
        mySafety.setDeviceTokenList(Collections.emptyList());

        return mySafety;
    }

    @Override
    public MyHomePage getMyHomePage(String account) {
        UserAccount userAccount = userAccountService.getByAccount(account);

        UserProfile userProfile = userProfileService.getByUId(userAccount.getUId());

        if (StringUtils.isBlank(userProfile.getNickname())){
            userProfile.setNickname(userAccount.getAccount());
        }

        userProfile.setCreateTime(null);
        userProfile.setModifyTime(null);

        MyHomePage homePage = new MyHomePage();
        homePage.setUserProfile(userProfile);
        homePage.setSecurityIssues(Collections.emptyList());

        return homePage;
    }

    @Override
    public void updateUserProfile(UserProfile userProfile, String account) {
        UserAccount userAccount = userAccountService.getByAccount(account);

        userProfile.setUId(userAccount.getUId());

        userProfileService.updateProfile(userProfile);
    }

    @Override
    public Boolean verifyPassword(String account, String password) {

        return userAccountService.verifyPassword(account, password);
    }

    @Override
    public void changePassword(String account, String password) {

        userAccountService.changePassword(account, password);
    }
}
