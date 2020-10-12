package ltd.inmind.accelerator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.model.po.DeviceToken;
import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.model.po.UserProfile;
import ltd.inmind.accelerator.model.vo.MyHomePage;
import ltd.inmind.accelerator.model.vo.MyInfo;
import ltd.inmind.accelerator.model.vo.MySafety;
import ltd.inmind.accelerator.model.vo.VerifyCode;
import ltd.inmind.accelerator.service.*;
import ltd.inmind.accelerator.utils.KVPlusMap;
import ltd.inmind.accelerator.utils.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    private final IUserAccountService userAccountService;

    private final IUserProfileService userProfileService;

    private final IDeviceTokenService deviceTokenService;

    private final IJwtTokenSecurityContext jwtTokenSecurityContext;

    private final ReactiveUserDetailsService reactiveUserDetailsService;

    private final KVPlusMap<String, String> verifyCache = new KVPlusMap<>();

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

        List<String> emails = Stream.of(userProfile.getEmail(),
                userAccount.getEmail(),
                userAccount.getRecoveryEmail())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        myInfo.setEmails(emails);

        List<String> phones = Stream.of(userProfile.getPhone(),
                userAccount.getRecoveryPhone())
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

        List<DeviceToken> deviceTokens = deviceTokenService.getDeviceTokensByUId(userAccount.getUId());
        mySafety.setUserAccount(userAccount);
        mySafety.setDeviceTokenList(deviceTokens);

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

    @Override
    public List<VerifyCode> generateVerifyCode(String account, String businessLine) {

        List<VerifyCode> codeList = Stream.iterate(1, seq -> seq + 1)
                .limit(4)
                .map(seq -> getVerifyCode(seq, 6))
                .collect(Collectors.toList());

        //FIXME 总是取出第一个作为有效的验证码
        VerifyCode verifyCode = codeList.get(0);
        verifyCache.put(businessLine + "_" + account,
                verifyCode.getSeq() + "_" + verifyCode.getCode().replace("\\s*", ""),
                5 * 60 * 1000);

        return codeList;
    }

    private VerifyCode getVerifyCode(long seq,long length){
        String number = RandomUtil.number(length);

        String code = StringUtils.join(number.split(""), " ");

        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setSeq(seq);
        verifyCode.setCode(code);

        return verifyCode;
    }

    @Override
    public Boolean testVerifyCode(VerifyCode verifyCode, String account, String businessLine) {

        String key = businessLine + "_" + account;
        String val = verifyCode.getSeq() + "_" + verifyCode.getCode().replace("\\s*", "");
        String code = verifyCache.get(key);

        if (code == null)
            return false;

        return code.equals(val);

    }

    @Override
    public void updateUserAccount(UserAccount userAccount) {

        userAccountService.updateUserAccount(userAccount);
    }

    @Override
    public Mono<String> refreshToken(String username) {
        return jwtTokenSecurityContext.create(reactiveUserDetailsService.findByUsername(username));
    }

}
