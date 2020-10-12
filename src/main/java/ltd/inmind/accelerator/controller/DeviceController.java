package ltd.inmind.accelerator.controller;

import lombok.RequiredArgsConstructor;
import ltd.inmind.accelerator.model.po.DeviceToken;
import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.service.IDeviceTokenService;
import ltd.inmind.accelerator.service.IUserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/device")
public class DeviceController {

    private final IDeviceTokenService deviceTokenService;

    private final IUserService userService;

    @GetMapping("devices")
    public DataResponse devices(Authentication authentication) {

        String account = authentication.getName();

        UserAccount userAccount = userService.getAccountByAccount(account);

        return new DataResponse()
                .success()
                .data("devices", deviceTokenService.getDeviceTokensByUId(userAccount.getUId()));
    }

    @PostMapping("/delete/{deviceId}")
    public DataResponse delete(@PathVariable("deviceId") String deviceId){

        deviceTokenService.deleteDevice(deviceId);

        return new DataResponse()
                .success();
    }

    @PostMapping("/update/name")
    public DataResponse updateName(DeviceToken deviceToken, Authentication authentication) {

        String account = authentication.getName();
        UserAccount userAccount = userService.getAccountByAccount(account);

        if (userAccount == null)
            return new DataResponse()
                    .failed()
                    .msg("this account not found");

        deviceToken.setUId(userAccount.getUId());

        deviceTokenService.updateDeviceName(deviceToken);

        return new DataResponse()
                .success();
    }

}
