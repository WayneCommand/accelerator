package ltd.inmind.accelerator.controller;

import ltd.inmind.accelerator.model.AccessTokenResult;
import ltd.inmind.accelerator.service.Oauth2ClientService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/oauth/2")
public class Oauth2Controller {

    @Autowired
    private Oauth2ClientService oauth2ClientService;

    /**
     * 授权
     *
     * @param client_id 客户端ID
     * @param redirect_uri 重定向路径(不是必须的)
     * @param scope 权限范围
     * @return
     */
    @GetMapping("/authorize")
    public String authorize(String client_id, String response_type, String redirect_uri, String scope, HttpServletResponse response) {

        //验证client
        if (!oauth2ClientService.verifyClientId(client_id))
            return "Incomplete request";

        //用户需要确认授权 (这里就直接同意了)
        if (true) {
            //如果用户确认了授权 将带着授权码(code) 重定向到redirect url
            Object principal = SecurityUtils.getSubject().getPrincipal();
            String code = oauth2ClientService.grantCode(client_id,(String) principal);
            try {
                response.sendRedirect(String.format("%s?code=%s", redirect_uri, code));
            } catch (IOException e) {
                //LOG
            }
            return "redirect error";
        } else {
            return "user refused";
        }


    }

    /**
     * 发放token
     * @param client_id 客户端ID
     * @param client_secret 颁发给客户端的密钥
     * @param code authorize产生的密钥
     * @return AccessToken
     */
    @PostMapping("/access_token")
    public AccessTokenResult accessToken(String client_id, String client_secret, String code) {
        try {
            //验证id 和 secret 和 code
            String accessToken = oauth2ClientService.accessToken(client_id, client_secret, code);

            AccessTokenResult accessTokenResult = new AccessTokenResult();
            accessTokenResult.setAccess_token(accessToken);
            accessTokenResult.setTokenType(AccessTokenResult.TOKEN_TYPE.BEARER);

            //成功之后返回token
            return accessTokenResult;
        } catch (Exception e) {
            AccessTokenResult result = new AccessTokenResult();
            result.setMessage(e.getMessage());
            return result;
        }
    }

}
