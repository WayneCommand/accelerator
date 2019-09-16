package ltd.inmind.accelerator.security.jwt.handler;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.model.LoginResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private Gson gson = new Gson();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("authentication failure", exception);

        LoginResult result;

        if (exception instanceof UsernameNotFoundException){

            result = LoginResult.getResult(LoginResult.LoginStatusEnum.USER_NOT_FOUND);
        } else if (exception instanceof AccountExpiredException) {

            result = LoginResult.getResult(LoginResult.LoginStatusEnum.TOKEN_EXPIRED);
        } else if (exception instanceof BadCredentialsException) {

            result = LoginResult.getResult(LoginResult.LoginStatusEnum.PASSWORD_NOT_MATCH);
        } else {
            result = new LoginResult(false, "系统异常");
        }

        sendResponse(response, result);
    }

    private void sendResponse(HttpServletResponse response, LoginResult result) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().println(gson.toJson(result));
    }


}
