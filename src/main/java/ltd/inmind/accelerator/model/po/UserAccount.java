package ltd.inmind.accelerator.model.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("u_user_account")
public class UserAccount {

  @TableId(type = IdType.AUTO)
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

  @TableField(fill = FieldFill.INSERT)
  private Date createTime;

  private Date modifyTime;

}
