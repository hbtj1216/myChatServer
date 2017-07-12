package com.tao.auth.core.service;

/**
 * Created by michael on 17-7-12.
 */

import com.tao.auth.core.domain.User;

/**
 * 注册服务借口.
 */
public interface RegisterService {

    /**
     * 注册新用户.
     * @param user
     */
    public boolean registerUser(User user);

    /**
     * 判断用户是否存在.
     * @param user
     * @return
     */
    public boolean exist(User user);
}



