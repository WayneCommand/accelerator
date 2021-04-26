package ltd.inmind.accelerator.security.events;

import ltd.inmind.accelerator.model.po.DeviceToken;
import org.springframework.context.ApplicationEvent;

public class DeviceTokenEvent extends ApplicationEvent {

    public DeviceTokenEvent(DeviceToken source) {
        super(source);
    }

    @Override
    public DeviceToken getSource() {
        return (DeviceToken) super.getSource();
    }
}
