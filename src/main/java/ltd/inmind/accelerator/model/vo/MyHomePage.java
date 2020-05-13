package ltd.inmind.accelerator.model.vo;

import lombok.Data;
import ltd.inmind.accelerator.model.po.UserProfile;

import java.util.List;

@Data
public class MyHomePage {

    private UserProfile userProfile;

    private List<SecurityIssue> securityIssues;
}
