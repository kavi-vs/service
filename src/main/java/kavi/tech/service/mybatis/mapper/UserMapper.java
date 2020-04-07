package kavi.tech.service.mybatis.mapper;

import kavi.tech.service.mysql.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user")
    public List<User> list();

    @Insert("INSERT INTO user (username) VALUES (#{username})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public Integer insert(User user);
}
