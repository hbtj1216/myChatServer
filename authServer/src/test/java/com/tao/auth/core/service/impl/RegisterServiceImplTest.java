package test.com.tao.auth.core.service.impl; 

import com.tao.auth.core.domain.User;
import com.tao.auth.core.service.RegisterService;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/** 
* RegisterServiceImpl Tester. 
* 
* @author <Authors name> 
* @since <pre>七月 12, 2017</pre> 
* @version 1.0 
*/
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件的位置
@ContextConfiguration({"classpath:spring/auth-spring-config.xml"})
public class RegisterServiceImplTest { 

    @Autowired
    private RegisterService registerService;

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: registerUser(User user) 
* 
*/ 
@Test
public void testRegisterUser() throws Exception { 

    User user = new User("hbtj1216", "TJ8313250", "陶杰");
    //注册
    System.out.println(registerService.registerUser(user));
} 

/** 
* 
* Method: exist(User user) 
* 
*/ 
@Test
public void testExist() throws Exception {

    User user = new User();
    user.setUserId("545554463_1");
    System.out.println(registerService.exist(user));
} 


} 
