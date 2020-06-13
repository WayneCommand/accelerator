package ltd.inmind.accelerator.service;

import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.model.po.UserProfile;
import ltd.inmind.accelerator.model.vo.MyHomePage;
import ltd.inmind.accelerator.model.vo.MyInfo;
import ltd.inmind.accelerator.model.vo.MySafety;
import ltd.inmind.accelerator.model.vo.VerifyCode;

import java.util.List;

public interface IUserService {

    /**
     * 对外提供的注册接口
     * 同时注册account和profile
     */
    void signUp(String account, String password);

    UserProfile getProfileByAccount(String account);

    UserAccount getAccountByAccount(String account);

    MyInfo getMyInfo(String account);

    MySafety getMySafety(String account);

    MyHomePage getMyHomePage(String account);

    void updateUserProfile(UserProfile userProfile, String account);

    Boolean verifyPassword(String account, String password);

    void changePassword(String account, String password);

    /**
     * 生成验证码
     * 在缓存中的key为 businessLine_account
     *         value为 seq_code
     * @param account 请求账户
     * @param businessLine 业务线
     * @return 一般情况下 4个 6位数字验证码
     */
    List<VerifyCode> generateVerifyCode(String account, String businessLine);

    /**
     * 验证验证码
     * @param verifyCode 需要包含seq和code
     * @param account 请求账户
     * @param businessLine 业务线
     */
    Boolean testVerifyCode(VerifyCode verifyCode, String account, String businessLine);

    void updateUserAccount(UserAccount userAccount);

    //FIXME 不允许block
    String refreshToken(String username);
}
