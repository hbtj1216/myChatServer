package test.com.tao.auth.core.dao; 

import com.tao.auth.core.dao.UserDao;
import com.tao.auth.core.domain.User;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.jws.soap.SOAPBinding;
import java.util.List;

/** 
* UserDao Tester. 
* 
* @author <Authors name> 
* @since <pre>七月 12, 2017</pre> 
* @version 1.0 
*/
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位置
@ContextConfiguration({"classpath:spring/auth-spring-config.xml"})
public class UserDaoTest {

    @Autowired
    private UserDao userDao;


/** 
* 
* Method: add(User user) 
* 
*/ 
@Test
public void testAdd() throws Exception {

    for(int i = 1; i <= 20; i++) {
        User user = new User();
        user.setUserId("545554463_" + i);
        user.setPassword("123456");
        user.setNickName("追风少年_" + i);
        System.out.println(userDao.add(user));
    }




} 

/** 
* 
* Method: delete(String userId) 
* 
*/ 
@Test
public void testDelete() throws Exception {

    System.out.println(userDao.delete("545554463"));
} 

/** 
* 
* Method: update(User user) 
* 
*/ 
@Test
public void testUpdate() throws Exception {

    User user = new User();
    user.setUserId("545554463_5");
    user.setNickName("新的昵称");
    System.out.println(userDao.update(user));
} 

/** 
* 
* Method: getUserByUserId(String userId) 
* 
*/ 
@Test
public void testGetUserByUserId() throws Exception { 

    User user = userDao.getUserByUserId("545554463_6");
    System.out.println(user);
} 

/** 
* 
* Method: getAllUsers() 
* 
*/ 
@Test
public void testGetAllUsers() throws Exception {

    List<User> userList = userDao.getAllUsers();
    System.out.println(userList);
} 


} 
