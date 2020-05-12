package ltd.inmind.accelerator.model.vo;

import lombok.Data;
import ltd.inmind.accelerator.model.po.UserProfile;

import java.util.Date;
import java.util.List;

@Data
public class MyInfo {

    private UserProfile userProfile;
    private List<String> emails;
    private List<String> phones;
    private Date passwordModifyTime;

}
