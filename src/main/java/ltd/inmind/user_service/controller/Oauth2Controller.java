package ltd.inmind.user_service.controller;

import ltd.inmind.user_service.service.Oauth2ClientService;
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
     * @param client_id
     * @param redirect_uri
     * @param scope
     * @return
     */
    @GetMapping("/authorize")
    public String authorize(String client_id, String redirect_uri, String scope, HttpServletResponse response) {
        //验证client
        if (!oauth2ClientService.verifyClientId(client_id))
            return "Incomplete request";

        //用户需要确认授权
        if (true) {
            //如果用户确认了授权 将带着授权码(code) 重定向到redirect url
            String code = oauth2ClientService.grantCode();
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
     * @param client_id
     * @param client_secret
     * @param code
     * @return 标准返回access_token=e72e16c7e42f292c6912e7710c838347ae178b4a&token_type=bearer
     */
    @PostMapping("/access_token")
    public String accessToken(String client_id, String client_secret, String code) {
        try {
            //验证id 和 secret 和 code
            String accessToken = oauth2ClientService.accessToken(client_id, client_secret, code);
            String tokenType = "bearer";

            //成功之后返回token
            return String.format("access_token=%s&token_type=%s", accessToken, tokenType);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
