package ltd.inmind.accelerator.security.jwt.repository;

import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.constants.LoginConst;
import ltd.inmind.accelerator.security.jwt.token.JwtAuthenticationToken;
import ltd.inmind.accelerator.utils.JwtUtil;
import ltd.inmind.accelerator.utils.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtSecurityContextRepository implements SecurityContextRepository {

    private static final Map<String, String> TOKEN_SESSION = new HashMap<>();

    private static JwtSecurityContextRepository securityContextRepository = new JwtSecurityContextRepository();

    private JwtSecurityContextRepository() {

    }

    public static JwtSecurityContextRepository getInstance() {
        return securityContextRepository;
    }

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        //TODO 重新组装逻辑
        String authorization = requestResponseHolder.getRequest().getHeader(LoginConst.AUTHORIZATION_HEADER_NAME);
        if (StringUtils.isNotBlank(authorization) && TOKEN_SESSION.containsKey(authorization)) {
            SecurityContext securityContext = new SecurityContextImpl();
            securityContext.setAuthentication(new JwtAuthenticationToken(authorization, Collections.emptyList()));

            return securityContext;
        }

        return generateNewContext();
    }

    protected SecurityContext generateNewContext() {
        return SecurityContextHolder.createEmptyContext();
    }

    @Override
    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        final Authentication authentication = context.getAuthentication();


        if (authentication == null) {
            //trustResolver.isAnonymous(authentication)
            log.debug("SecurityContext is empty or contents are anonymous - context will not be stored in HttpSession.");
            return;
        }

        //如果是jwt的token则不需要再次保存(签发)
        if (!(authentication instanceof UsernamePasswordAuthenticationToken))
            return;

        User principal = (User)authentication.getPrincipal();

        String code = UUIDUtil.generateShortUuid();

        String token = JwtUtil.sign(principal.getUsername(), code);

        TOKEN_SESSION.put(token, code);

        response.setHeader(LoginConst.AUTHORIZATION_HEADER_NAME, token);
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {

        String authorization = request.getHeader(LoginConst.AUTHORIZATION_HEADER_NAME);

        return StringUtils.isNotBlank(authorization) && TOKEN_SESSION.containsKey(authorization);
    }
}
