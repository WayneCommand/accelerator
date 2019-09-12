package ltd.inmind.accelerator.security.jwt.handler;

import com.google.gson.Gson;
import lombok.Setter;
import ltd.inmind.accelerator.model.LoginResult;
import ltd.inmind.accelerator.security.jwt.repository.JwtSecurityContextRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Setter
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private SecurityContextRepository securityContextRepository = JwtSecurityContextRepository.getInstance();

    private Gson gson = new Gson();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(gson.toJson(LoginResult.getResult(LoginResult.LoginStatusEnum.SUCCESS)));

    }

}
