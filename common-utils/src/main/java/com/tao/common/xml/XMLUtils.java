package com.tao.common.xml;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * XML文件解析工具
 * @author Tao
 *
 */
public final class XMLUtils {
	
	private XMLUtils() {

	}
	
	
	/**
	 * 解析xml文件，返回根节点。
	 * @param path
	 * @return
	 */
	public static Element parseXmlFile(String path) {
		
		try {
			//获取配置文件的位置
			String configFilePath = XMLUtils.class.getClassLoader().getResource(path).getPath();
			System.out.println("配置文件路径：" + configFilePath);
			//创建一个SAXReader对象
			SAXReader saxReader = new SAXReader();
			//获取document对象,如果文档无节点，则会抛出Exception提前结束  
			Document document = saxReader.read(new File(configFilePath));
			//获取根节点
			Element root = document.getRootElement();
			return root;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}














