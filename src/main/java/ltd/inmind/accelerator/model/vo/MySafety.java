package ltd.inmind.accelerator.model.vo;

import lombok.Data;
import ltd.inmind.accelerator.model.po.DeviceToken;
import ltd.inmind.accelerator.model.po.UserAccount;

import java.util.List;

@Data
public class MySafety {

    private UserAccount userAccount;

    private List<DeviceToken> deviceTokenList;
}
