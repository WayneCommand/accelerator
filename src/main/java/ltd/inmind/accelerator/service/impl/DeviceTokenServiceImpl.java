package ltd.inmind.accelerator.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import ltd.inmind.accelerator.mapper.DeviceTokenMapper;
import ltd.inmind.accelerator.model.po.DeviceToken;
import ltd.inmind.accelerator.service.IDeviceTokenService;
import ltd.inmind.accelerator.service.IJwtTokenSecurityContext;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DeviceTokenServiceImpl implements IDeviceTokenService {

    private final DeviceTokenMapper deviceTokenMapper;

    private final IJwtTokenSecurityContext jwtTokenSecurityContext;

    @Override
    public void saveDeviceToken(DeviceToken device) {
        DeviceToken _deviceToken = loadDeviceTokenByDeviceId(device.getDeviceId());
        if (_deviceToken != null){
            //如果相同的deviceId 直接更新token即可
            updateTokenByDeviceId(device.getDeviceId(), device.getToken());
            return;
        }

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

    private void updateTokenByDeviceId(String deviceId, String token) {
        DeviceToken deviceToken = new DeviceToken();
        deviceToken.setToken(token);

        deviceToken.setModifyTime(new Date());

        deviceTokenMapper.update(deviceToken, Wrappers.<DeviceToken>lambdaQuery()
                .eq(DeviceToken::getDeviceId, deviceId));
    }

    @Override
    public DeviceToken loadDeviceToken(String token) {
        return deviceTokenMapper.selectOne(Wrappers.<DeviceToken>lambdaQuery()
                .eq(DeviceToken::getToken, token));
    }

    private DeviceToken loadDeviceTokenByDeviceId(String deviceId) {
        return deviceTokenMapper.selectOne(Wrappers.<DeviceToken>lambdaQuery()
                .eq(DeviceToken::getDeviceId, deviceId));
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

    @Override
    public List<DeviceToken> getDeviceTokensByUId(Long uId) {
        return deviceTokenMapper.selectList(Wrappers.<DeviceToken>lambdaQuery()
                .eq(DeviceToken::getUId, uId));
    }

    @Override
    public void deleteDevice(String deviceId) {
        DeviceToken _deviceToken = deviceTokenMapper.selectOne(Wrappers.<DeviceToken>lambdaQuery()
                .eq(DeviceToken::getDeviceId, deviceId));

        if (_deviceToken == null)
            return;

        //这里会真的删除记录 (隐私政策)
        deviceTokenMapper.delete(Wrappers.<DeviceToken>lambdaQuery()
                .eq(DeviceToken::getDeviceId, deviceId));

        //一并把token也处理掉
        String token = _deviceToken.getToken();
        jwtTokenSecurityContext.remove(token);
    }

    @Override
    public void updateDeviceName(DeviceToken deviceToken) {
        DeviceToken _dt = new DeviceToken();
        _dt.setDeviceName(deviceToken.getDeviceName());

        deviceTokenMapper.update(_dt, Wrappers.<DeviceToken>lambdaQuery()
                .eq(DeviceToken::getDeviceId, deviceToken.getDeviceId())
                .eq(DeviceToken::getUId, deviceToken.getUId()));
    }

}
