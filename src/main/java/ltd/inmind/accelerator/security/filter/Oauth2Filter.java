package ltd.inmind.accelerator.security.filter;

import ltd.inmind.accelerator.exception.AcceleratorException;
import ltd.inmind.accelerator.service.Oauth2ClientService;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 接口参考：https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps
 */
public class Oauth2Filter implements WebFilter {

    private final Oauth2ClientService oauth2ClientService;

    public Oauth2Filter(Oauth2ClientService clientService) {
        this.oauth2ClientService = clientService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {


        return null;
    }

    // 1. 子系统发起认证请求 (/login/oauth/authorize)
    void authorize(String clientId, String redirectUri, String scope) {
        if (!oauth2ClientService.verifyClientId(clientId))
            throw new AcceleratorException();

        // 把这些信息带给用户，让用户去确认

    }

    // 2. 用户确认认证请求
    void doAuthorize(String clientId, String redirectUri, String scope) {
        // 如果用户确认了授权 将带着授权码(code) 重定向到redirect url

        // TODO 怎么取出已经登陆的用户
        String code = oauth2ClientService.grantCode(clientId, null);
        // response.sendRedirect(String.format("%s?code=%s", redirect_uri, code));
    }

    // 3. 请求 token (/login/oauth/access_token)
    void accessToken(String clientId, String clientSecret, String code) {

        //验证id 和 secret 和 code
        try {
            String accessToken = oauth2ClientService.accessToken(clientId, clientSecret, code);
        } catch (AcceleratorException e) {

            System.err.println(e.getMsg());
        }

        // {"access_token":"gho_16C7e42F292c6912E7710c838347Ae178B4a", "scope":"repo,gist", "token_type":"bearer"}
    }


}
