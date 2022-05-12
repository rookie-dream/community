package com.rookie.community.service;

import com.rookie.community.pojo.LoginTicket;
import com.rookie.community.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

public interface UserService {

    User findUserById(int id);

    User findUserByName(String username);
    //修改密码
    int updatePassword(int id, String password);


//注册账号
    Map<String,Object> register(User user);
//激活账号
    int activation(int userId,String code);
//账号登录
    Map<String,Object> login(String username,String password ,long expiredSeconds);

//    注销登录
    void logout(String ticket);

//    查找登录ticket
    LoginTicket findLoginTicket(String ticket);

//    修改头像
    int updateHeader(int userId,String headerUrl);


    Collection<? extends GrantedAuthority> getAuthorities(int userId);




}
