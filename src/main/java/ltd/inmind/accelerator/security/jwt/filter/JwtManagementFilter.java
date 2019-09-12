package ltd.inmind.accelerator.security.jwt.filter;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.constants.LoginConst;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.session.InvalidSessionStrategy;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Setter
public class JwtManagementFilter extends GenericFilter {

    static final String FILTER_APPLIED = "__spring_security_session_jwt_filter_applied";

    private SecurityContextRepository securityContextRepository;
    private AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
    private InvalidSessionStrategy invalidSessionStrategy = null;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (request.getAttribute(FILTER_APPLIED) != null) {
            filterChain.doFilter(request, response);
            return;
        }

        request.setAttribute(FILTER_APPLIED, Boolean.TRUE);


        //校验 jwt
        if (!securityContextRepository.containsContext(request)) {
            Authentication authentication = SecurityContextHolder.getContext()
                    .getAuthentication();

            if (authentication != null && !trustResolver.isAnonymous(authentication)) {
                // The user has been authenticated during the current request, so call the
                // session strategy
                securityContextRepository.saveContext(SecurityContextHolder.getContext(),
                        request, response);
            }
            else {
                // No security context or authentication present. Check for a session
                // timeout
                if (isAuthorizationValid(request)) {

                    if (invalidSessionStrategy != null) {
                        invalidSessionStrategy
                                .onInvalidSessionDetected(request, response);
                        return;
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isAuthorizationValid(HttpServletRequest request) {

        String authorization = request.getHeader(LoginConst.AUTHORIZATION_HEADER_NAME);

        return StringUtils.isNotBlank(authorization);
    }
}
