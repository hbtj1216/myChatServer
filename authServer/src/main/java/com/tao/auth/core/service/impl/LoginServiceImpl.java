package com.tao.auth.core.service.impl;

import com.tao.auth.core.dao.UserDao;
import com.tao.auth.core.domain.User;
import com.tao.auth.core.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by michael on 17-7-12.
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserDao userDao;


    @Override
    public User getUserByUserId(String userId) {

        User dbUser = userDao.getUserByUserId(userId);
        if(dbUser != null) {
            return dbUser;
        } else {
            return null;
        }
    }
}
