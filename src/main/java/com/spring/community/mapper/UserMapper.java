package com.spring.community.mapper;

import com.spring.community.dao.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@Mapper
public interface UserMapper {
    // #{name}自动获取user中的name进行赋值
    @Insert("INSERT INTO user (name, account_id, token, gmt_create, gmt_modified) VALUES (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified})")
    public void insert(User user);

    // token 不是个自定义类，所以加上param注解
    @Select("SELECT * FROM user WHERE token = #{token}")
    User findByToken(@Param("token") String token);
}
