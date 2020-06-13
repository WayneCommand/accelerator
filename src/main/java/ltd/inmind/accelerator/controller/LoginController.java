package ltd.inmind.accelerator.controller;

import ltd.inmind.accelerator.constants.ExceptionConst;
import ltd.inmind.accelerator.constants.SecurityConst;
import ltd.inmind.accelerator.exception.AcceleratorException;
import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.service.IJwtTokenSecurityContext;
import ltd.inmind.accelerator.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private IUserService userService;

    /**
     * 注册接口
     *
     * @param account 用户名
     * @param password 密码
     * @return
     */
    @PostMapping("/signUp")
    public DataResponse signUp(String account, String password) {

        try {

            userService.signUp(account, password);

            return new DataResponse()
                    .success();

        } catch (AcceleratorException e) {

            String msg = ExceptionConst.CODE_MSG.get(e.getCode());

            return new DataResponse()
                    .failed()
                    .msg(msg);
        }
    }

    @GetMapping("/lookup")
    public DataResponse lookup(String username) {
        UserAccount userAccount = userService.getAccountByAccount(username);

        if (userAccount == null) {

            return new DataResponse()
                    .success()
                    .data("isExist", "false")
                    .msg("我们未找到您的账户, 请检查您的账户名。");
        }

        return new DataResponse()
                .success()
                .data("isExist", "true");
    }

    @PostMapping("refreshToken")
    public ResponseEntity<DataResponse> refreshToken(UserAccount user, Authentication authentication) {
        //比对传过来的username和当前已授权的username
        if (authentication.getName().equals(user.getAccount())){

            //生成refresh token
            String refreshToken = userService.refreshToken(user.getAccount());

            //不出问题的情况下 用header返回token
            if (StringUtils.isNotBlank(refreshToken)) {
                return ResponseEntity.ok()
                        .header(SecurityConst.AUTHENTICATION_HEADER, refreshToken)
                        .body(new DataResponse().success());
            }

        }

        return ResponseEntity.ok()
                .body(new DataResponse().failed());

    }
}
