package ltd.inmind.accelerator.controller;

import ltd.inmind.accelerator.model.po.UserAccount;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.service.IDeviceTokenService;
import ltd.inmind.accelerator.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private IDeviceTokenService deviceTokenService;

    @Autowired
    private IUserService userService;

    @GetMapping("devices")
    public DataResponse devices(Authentication authentication) {

        String account = authentication.getName();

        UserAccount userAccount = userService.getAccountByAccount(account);

        return new DataResponse()
                .success()
                .data("devices", deviceTokenService.getDeviceTokensByUId(userAccount.getUId()));
    }

}
