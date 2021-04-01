package ltd.inmind.accelerator.security.listener;

import ltd.inmind.accelerator.security.events.TokenRemoveEvent;
import org.springframework.context.ApplicationListener;

public class TokenRemoveListener implements ApplicationListener<TokenRemoveEvent> {


    @Override
    public void onApplicationEvent(TokenRemoveEvent tokenRemoveEvent) {
        // FIXME
    }
}
