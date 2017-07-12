package com.tao.auth.core.service;

/**
 * Created by michael on 17-7-12.
 */

import com.tao.auth.core.domain.User;

/**
 * 登录服务类.
 */
public interface LoginService {


    /**
     * 通过userId获得User对象.
     * @param userId
     * @return
     */
    public User getUserByUserId(String userId);

}




