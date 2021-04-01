package ltd.inmind.accelerator.security.listener;

import lombok.RequiredArgsConstructor;
import ltd.inmind.accelerator.model.po.DeviceToken;
import ltd.inmind.accelerator.security.events.DeviceTokenEvent;
import ltd.inmind.accelerator.service.IDeviceTokenService;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeviceTokenSaveListener implements ApplicationListener<DeviceTokenEvent> {

    private final IDeviceTokenService deviceTokenService;

    @Override
    public void onApplicationEvent(DeviceTokenEvent deviceTokenEvent) {
        DeviceToken source = (DeviceToken) deviceTokenEvent.getSource();
        deviceTokenService.saveDeviceToken(source);
    }
}
