package ltd.inmind.accelerator.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("u_device_token")
public class DeviceToken {

  private Long uId;
  private String token;
  private String device;
  private String deviceName;
  private String ip;
  private String location;
  private Date firstTime;
  private Date lastTime;
  private Integer activeCount;
  private Date createTime;
  private Date modifyTime;


}
