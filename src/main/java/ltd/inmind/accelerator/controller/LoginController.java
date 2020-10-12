package ltd.inmind.accelerator.controller;

import lombok.RequiredArgsConstructor;
import ltd.inmind.accelerator.constants.ExceptionConst;
import ltd.inmind.accelerator.constants.SecurityConst;
import ltd.inmind.accelerator.exception.AcceleratorException;
import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.service.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@RestController
@RequestMapping("/login")
public class LoginController {

    private final IUserService userService;

    /**
     * 注册接口
     *
     * @param account account password
     * @return
     */
    @PostMapping("/signUp")
    public ResponseEntity<DataResponse> signUp(UserAccount account) {

        try {
            userService.signUp(account.getAccount(), account.getPassword());

            return ResponseEntity.ok(new DataResponse()
                    .success());
        } catch (AcceleratorException e) {

            String msg = ExceptionConst.CODE_MSG.get(e.getCode());

            return ResponseEntity.badRequest()
                    .body(new DataResponse()
                    .failed()
                    .msg(msg));
        }
    }

    @GetMapping("/lookup")
    public ResponseEntity<DataResponse> lookup(String username) {
        UserAccount userAccount = userService.getAccountByAccount(username);

        if (userAccount == null) {
            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.ok(new DataResponse().success()
                .data("isExist", "true"));
    }

    @PostMapping("refreshToken")
    public Mono<ResponseEntity<DataResponse>> refreshToken(UserAccount user, Authentication authentication) {
        //比对传过来的username和当前已授权的username
        if ("accept".equals(user.getAccount())){

            //生成 token
            return userService.refreshToken(authentication.getName())
                    .map(refreshToken -> ResponseEntity.ok()
                            .header(SecurityConst.AUTHENTICATION_HEADER, refreshToken)
                            .body(new DataResponse().success()));
        }

        return Mono.just(ResponseEntity.ok()
                .body(new DataResponse().failed()));
    }

}
