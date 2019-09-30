package ltd.inmind.accelerator.security.service;

import ltd.inmind.accelerator.model.User;
import ltd.inmind.accelerator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import java.util.Collections;

@EnableWebFluxSecurity
public class JwtMapReactiveUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {

        User user = userService.getUserByUsername(username);

        if (user == null)
            return Mono.empty();

        return Mono.just(new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList()));
    }
}
