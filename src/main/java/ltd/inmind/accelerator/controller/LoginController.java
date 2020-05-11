package ltd.inmind.accelerator.controller;

import ltd.inmind.accelerator.constants.ExceptionConst;
import ltd.inmind.accelerator.exception.AcceleratorException;
import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public DataResponse lookup(String account) {
        UserAccount userAccount = userService.getAccountByAccount(account);

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
}
