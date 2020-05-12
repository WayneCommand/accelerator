package ltd.inmind.accelerator.model.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("u_device_token")
public class DeviceToken {

  @TableId
  private Long uId;

  private String token;

  private String device;

  private String deviceName;

  private String ip;

  private String location;

  private Date firstTime;

  private Date lastTime;

  private Integer activeCount;

  @TableField(fill = FieldFill.INSERT)
  private Date createTime;

  @TableField(fill = FieldFill.UPDATE)
  private Date modifyTime;

}
