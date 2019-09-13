package ltd.inmind.accelerator.security.jwt.provider;

import ltd.inmind.accelerator.security.jwt.token.JwtAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static ltd.inmind.accelerator.constants.LoginConst.TOKEN_SESSION;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!supports(authentication.getClass())) {
            return null;
        }

        JwtAuthenticationToken jwtAuthentication = (JwtAuthenticationToken) authentication;

        String token = jwtAuthentication.getToken();

        if (TOKEN_SESSION.containsKey(token))
            return authentication;

        throw new BadCredentialsException("token 已失效");
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
