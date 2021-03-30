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

  private String account;

  private String token;

  private String deviceId;

  private String deviceType;

  private String deviceModel;

  private String deviceName;

  private String deviceVersion;

  private String deviceSystem;

  private String ip;

  private String location;

  private String locationCountry;

  private String locationRegion;

  private String locationCity;

  private Date firstTime;

  private Date lastTime;

  private Integer activeCount;

  @TableField(fill = FieldFill.INSERT)
  private Date createTime;

  private Date modifyTime;
}
