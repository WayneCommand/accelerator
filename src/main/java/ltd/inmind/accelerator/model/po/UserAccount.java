package ltd.inmind.accelerator.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("u_user_account")
public class UserAccount {

  private Long uId;
  private String account;
  private String password;
  private String email;
  private String recoveryEmail;
  private String phone;
  private Long phoneToLogin;
  private Long twoStepVerify;
  private Date passwordModifyTime;
  private Date phoneModifyTime;
  private Date createTime;
  private Date modifyTime;

}
