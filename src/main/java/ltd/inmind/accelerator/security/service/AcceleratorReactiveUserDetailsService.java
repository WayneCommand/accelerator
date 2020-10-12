package ltd.inmind.accelerator.security.service;

import lombok.RequiredArgsConstructor;
import ltd.inmind.accelerator.service.IUserAccountService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class AcceleratorReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final IUserAccountService userAccountService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {

        return Mono.justOrEmpty(userAccountService.getByAccount(username))
                .map(byAccount ->
                        new User(byAccount.getAccount(),
                                byAccount.getPassword(),
                                Collections.emptyList()));
    }
}
