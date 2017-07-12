package com.tao.auth.core.service.impl;

import com.tao.auth.core.dao.UserDao;
import com.tao.auth.core.domain.User;
import com.tao.auth.core.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by michael on 17-7-12.
 */

/**
 * 注册用户服务的实现类.
 */
@Service("registerService")
public class RegisterServiceImpl implements RegisterService {


    @Autowired
    private UserDao userDao;



    @Override
    public synchronized boolean registerUser(User user) {

        //首先判断用户是否存在
        if(exist(user)) {
            return false;
        }
        //注册用户
        int n = userDao.add(user);
        if(n != 0) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    public boolean exist(User user) {

        User dbUser = userDao.getUserByUserId(user.getUserId());
        if(dbUser == null) {
            return false;
        }
        return true;
    }
}
