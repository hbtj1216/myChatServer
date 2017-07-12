package com.tao.protobuf.analysis;

import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Qzy on 2016/1/29.
 */


public class ParseMap {
    private static final Logger logger = LoggerFactory.getLogger(ParseMap.class);
    
    
    /**
     * 定义一个解析函数接口
     * @author Tao
     *
     */
    @FunctionalInterface
    public interface Parsing{
        Message  process(byte[] bytes) throws IOException;
    }

    /**
     * 存储解析函数的map.
     * key = ptoNum, value = 解析函数
     */
    public static HashMap<Integer, Parsing> parseMap = new HashMap<>();
    
    //存储msg和对应的ptoNum的map。
    //注意:这里使用Class对象作为键,这样同一个类的所有实例的ptoNum都是一样的。
    public static HashMap<Class<?>, Integer> msg2ptoNum = new HashMap<>();
    
    
    
    /**
     * 注册解析函数。
     * @param ptoNum	ptoNum值。
     * @param parse		解析函数
     * @param cla		被解析类的Class对象
     */
    public static void register(int ptoNum, Parsing parse, Class<?> cla) {
        
    	if (parseMap.get(ptoNum) == null) {
    		parseMap.put(ptoNum, parse);
    	}   
        else {
            logger.error("pto has been registered in parseMap, ptoNum: {}", ptoNum);
            return;
        }

        if(msg2ptoNum.get(cla) == null) {
        	msg2ptoNum.put(cla, ptoNum);
        }       
        else {
            logger.error("pto has been registered in msg2ptoNum, ptoNum: {}", ptoNum);
            return;
        }
    }
    
    
    /**
     * 根据ptoNum和消息内容的字节数组, 获取对应种类的Message对象。
     * @param ptoNum
     * @param bytes
     * @return
     * @throws IOException
     */
    public static Message getMessage(int ptoNum, byte[] bytes) throws IOException {
        //获得ptoNum对应的parser对象
    	Parsing parser = parseMap.get(ptoNum);
        if(parser == null) {
            logger.error("UnKnown Protocol Num: {}", ptoNum);
        }
        Message msg = parser.process(bytes);

        return msg;
    }
    
    
    /**
     * 通过Message对象, 获取ptoNum的值。
     * @param msg
     * @return
     */
    public static Integer getPtoNum(Message msg) {
        return getPtoNum(msg.getClass());
    }
    
    
    /**
     * 通过Class对象, 获取ptoNum的值。
     * @param clz
     * @return
     */
    public static Integer getPtoNum(Class<?> clz) {
        return msg2ptoNum.get(clz);
    }

}
