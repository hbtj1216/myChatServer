package com.tao.auth.core.dao;

/**
 * Created by michael on 17-7-12.
 */

import com.tao.auth.core.domain.User;

import java.util.List;

/**
 * t_user用户表的DAO.
 */
public interface UserDao {

    /**
     * 添加新用户
     * @param user
     * @return
     */
    public Integer add(User user);


    /**
     * 通过userId删除user.
     * @param userId
     * @return
     */
    public Integer delete(String userId);

    /**
     * 更新user信息.
     * @param user
     * @return
     */
    public Integer update(User user);


    /**
     * 通过userId获得user.
     * @param userId
     * @return
     */
    public User getUserByUserId(String userId);

    /**
     * 获取所有的user.
     * @return
     */
    public List<User> getAllUsers();


}






