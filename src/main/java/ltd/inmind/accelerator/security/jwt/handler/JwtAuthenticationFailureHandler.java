package ltd.inmind.accelerator.security.jwt.handler;

import com.google.gson.Gson;
import ltd.inmind.accelerator.model.LoginResult;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private Gson gson = new Gson();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");
        //假装都是密码不正确
        response.getWriter().println(gson.toJson(LoginResult.getResult(LoginResult.LoginStatusEnum.PASSWORD_NOT_MATCH)));
        exception.printStackTrace();
        //TODO 处理异常
    }


}
