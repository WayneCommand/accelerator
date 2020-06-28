package ltd.inmind.accelerator.service;

import ltd.inmind.accelerator.model.po.DeviceToken;

public interface IDeviceTokenService {

    void saveDeviceToken(DeviceToken device);

    void updateToken(String origin,String token);

    DeviceToken loadDeviceToken(String token);

    void updateActive(String token, String ip);

}
