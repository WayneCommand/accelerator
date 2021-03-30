package ltd.inmind.accelerator.security.events;

import org.springframework.context.ApplicationEvent;

public class DeviceTokenEvent extends ApplicationEvent {

    public DeviceTokenEvent(DeviceTokenEvent source) {
        super(source);
    }
}
