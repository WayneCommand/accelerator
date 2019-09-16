package ltd.inmind.accelerator.security.jwt.token;

import lombok.Getter;
import ltd.inmind.accelerator.utils.JwtUtil;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String token;

    public JwtAuthenticationToken(String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return JwtUtil.getClaim(token, JwtUtil.JWT_USERNAME);
    }
}
