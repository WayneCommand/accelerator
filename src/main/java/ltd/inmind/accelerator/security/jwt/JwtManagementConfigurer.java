package ltd.inmind.accelerator.security.jwt;

import ltd.inmind.accelerator.security.jwt.filter.JwtManagementFilter;
import ltd.inmind.accelerator.security.jwt.repository.JwtSecurityContextRepository;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.session.SessionManagementFilter;

public class JwtManagementConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<JwtManagementConfigurer<H>, H> {

    @Override
    public void configure(H builder) throws Exception {

        JwtManagementFilter jwtManagementFilter = new JwtManagementFilter();

        jwtManagementFilter.setSecurityContextRepository(JwtSecurityContextRepository.getInstance());

        JwtManagementFilter filter = postProcess(jwtManagementFilter);

        builder.addFilterBefore(filter, SessionManagementFilter.class);
    }
}
