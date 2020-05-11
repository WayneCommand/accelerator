package ltd.inmind.accelerator.security.service;

import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.service.IUserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class AcceleratorReactiveUserDetailsService implements ReactiveUserDetailsService {

    @Autowired
    private IUserAccountService userAccountService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {

        UserAccount byAccount = userAccountService.getByAccount(username);

        if (byAccount == null)
            return Mono.empty();

        return Mono.just(new org.springframework.security.core.userdetails.User(byAccount.getAccount(), byAccount.getPassword(), Collections.emptyList()));
    }
}
