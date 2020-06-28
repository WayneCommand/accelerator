package ltd.inmind.accelerator.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ltd.inmind.accelerator.mapper.DeviceTokenMapper;
import ltd.inmind.accelerator.model.po.DeviceToken;
import ltd.inmind.accelerator.service.IDeviceTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DeviceTokenServiceImpl implements IDeviceTokenService {

    @Autowired
    private DeviceTokenMapper deviceTokenMapper;

    @Override
    public void saveDeviceToken(DeviceToken device) {
        Date now = new Date();

        device.setFirstTime(now);
        device.setLastTime(now);

        deviceTokenMapper.insert(device);
    }

    @Override
    public void updateToken(String origin, String token) {
        DeviceToken deviceToken = new DeviceToken();
        deviceToken.setToken(token);

        deviceToken.setModifyTime(new Date());

        deviceTokenMapper.update(deviceToken, Wrappers.<DeviceToken>lambdaQuery()
                .eq(DeviceToken::getToken, origin));
    }

    @Override
    public DeviceToken loadDeviceToken(String token) {
        return deviceTokenMapper.selectOne(Wrappers.<DeviceToken>lambdaQuery()
                .eq(DeviceToken::getToken, token));
    }

    @Override
    public void updateActive(String token, String ip) {
        DeviceToken _deviceToken = loadDeviceToken(token);

        DeviceToken deviceToken = new DeviceToken();
        deviceToken.setLastTime(new Date());

        if (!_deviceToken.getIp().equals(ip)) {
            deviceToken.setIp(ip);
        }

        deviceToken.setActiveCount(_deviceToken.getActiveCount() + 1);

        deviceTokenMapper.update(deviceToken, Wrappers.<DeviceToken>lambdaQuery()
                .eq(DeviceToken::getToken, token));
    }
}
