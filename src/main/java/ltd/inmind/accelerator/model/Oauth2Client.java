package ltd.inmind.accelerator.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_oauth2_client")
public class Oauth2Client {

    @TableId(type = IdType.AUTO)
    private int id;

    private String clientId;

    private String clientName;

    private String clientSecret;

    @TableField(exist = false)
    private transient String logo; //暂未实现

    @TableField(exist = false)
    private transient String description; //暂未实现

    private String callbackUrl;
}
