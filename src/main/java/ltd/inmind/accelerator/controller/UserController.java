package ltd.inmind.accelerator.controller;

import ltd.inmind.accelerator.constants.BusinessLineConst;
import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.model.po.UserProfile;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.model.vo.MyHomePage;
import ltd.inmind.accelerator.model.vo.VerifyCode;
import ltd.inmind.accelerator.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("my_info")
    public DataResponse myInfo(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();


        return new DataResponse()
                .success()
                .data("info", userService.getMyInfo(principal.getUsername()));
    }

    @GetMapping("my_safety")
    public DataResponse mySafety(Authentication authentication) {

        return new DataResponse()
                .success()
                .data("safety", userService.getMySafety(authentication.getName()));
    }

    @GetMapping("my_home_page")
    public DataResponse myHomePage(Authentication authentication) {

        MyHomePage myHomePage = userService.getMyHomePage(authentication.getName());

        return new DataResponse()
                .success()
                .data("homePage", myHomePage);
    }

    @PostMapping("update_profile")
    public DataResponse updateProfile(UserProfile profile, Authentication authentication) {

        userService.updateUserProfile(profile, authentication.getName());

        return new DataResponse().success();
    }

    @PostMapping("verify_password")
    public DataResponse verifyPassword(UserAccount account, Authentication authentication) {

        if (StringUtils.isBlank(account.getPassword()))
            return new DataResponse().failed();

        if (userService.verifyPassword(authentication.getName(),account.getPassword()))
            return new DataResponse().success();

        return new DataResponse().failed();
    }

    @PostMapping("change_password")
    public DataResponse changePassword(UserAccount account, Authentication authentication) {

        if (StringUtils.isBlank(account.getPassword()))
            return new DataResponse().failed();

        userService.changePassword(authentication.getName(), account.getPassword());

        return new DataResponse().success();
    }

    @PostMapping("verify_recovery_phone")
    public DataResponse verifyRecoveryPhone(UserAccount account, Authentication authentication) {

        return new DataResponse().success();
    }

    @PostMapping("verify_recovery_email")
    public DataResponse verifyRecoveryEmail(UserAccount account, Authentication authentication) {

        List<VerifyCode> verifyCodes = userService.generateVerifyCode(authentication.getName(), BusinessLineConst.SAFETY_UPDATE);

        return new DataResponse().success()
                .data("verifyCodes", verifyCodes);
    }

    @PostMapping("update_recovery_email")
    public DataResponse updateRecoveryEmail(VerifyCode verifyCode, UserAccount userAccount, Authentication authentication) {

        if (!userService.testVerifyCode(verifyCode, authentication.getName(), BusinessLineConst.SAFETY_UPDATE))
            return new DataResponse().failed()
                    .msg("验证码错误");

        userAccount.setAccount(authentication.getName());

        userService.updateUserAccount(userAccount);

        return new DataResponse().success();
    }

    @PostMapping("update_recovery_phone")
    public DataResponse updateRecoveryPhone(UserAccount userAccount, Authentication authentication) {
        //这个地方暂时没办法验证

        userAccount.setAccount(authentication.getName());

        userService.updateUserAccount(userAccount);

        return new DataResponse().success();
    }
}
