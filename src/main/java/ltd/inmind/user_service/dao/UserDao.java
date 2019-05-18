package ltd.inmind.user_service.dao;

import ltd.inmind.user_service.model.User;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface UserDao {


    @Select("select * from t_user")
    @Results({
            @Result(id = true,column = "u_id",property = "uId"),
            @Result(column = "last_login",property = "lastLogin",javaType = Date.class),
            @Result(column = "create_time",property = "createTime",javaType = Date.class),
            @Result(column = "update_time",property = "updateTime",javaType = Date.class)
    })
    List<User> selectAll();

    @Select("select * from t_user where username = #{username}")
    @Results({
            @Result(id = true, column = "u_id", property = "uId"),
            @Result(column = "last_login", property = "lastLogin", javaType = Date.class),
            @Result(column = "create_time", property = "createTime", javaType = Date.class),
            @Result(column = "update_time", property = "updateTime", javaType = Date.class)
    })
    User selectUserByUsername(String username);

    @Insert("insert into t_user(username,password,salt,email,create_time)" +
            " values(#{user.username},#{user.password},#{user.salt},#{user.email},#{user.createTime})")
    @Options(keyColumn = "u_id", keyProperty = "uId",useGeneratedKeys = true)
    int insert(@Param("user") User user);


    @Update("update t_user set password = #{user.password},salt = #{user.salt} where u_id = #{user.uId}")
    int update(@Param("user") User user);
}
