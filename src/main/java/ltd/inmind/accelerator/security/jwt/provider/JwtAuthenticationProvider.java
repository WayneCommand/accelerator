package ltd.inmind.accelerator.security.jwt.provider;

import ltd.inmind.accelerator.security.jwt.token.JwtAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        //TODO 忽略校验

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {

        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
