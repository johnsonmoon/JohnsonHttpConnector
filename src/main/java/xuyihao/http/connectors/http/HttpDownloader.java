package xuyihao.http.connectors.http;

import xuyihao.http.connectors.common.Connection;
import xuyihao.http.connectors.common.Downloader;
import xuyihao.http.entity.Cookie;

/**
 * 网络资源(文件)下载工具类
 * 
 * <pre>
 *  (1)发送GET POST请求，接收网络文件
 *  (2)此工具类不支持多线程下载，IO阻塞线程
 *  (3)由于下载时候的IO阻碍主线程,所以需要使用 
 *  	getCompleteRate printCompleteRate 
 *  	等方法时候需要先调用这几个方法,再开启下载方法
 *  	并且需要注意的是, 调用getCompleteRate printCompleteRate
 *  	等方法之前需要先调用initializeStates方法初始化状态变量
 *  (4)如果需要新建线程并使用 getCompleteRate 
 *  	方法查看进度, 也需要在调用下载的方法之前创建并start()
 *  (5)添加cookie支持,在一些需要保持会话状态下载文件的情况下
 *  (6)添加查看进度支持,需要调用 getCompleteRate 方法
 * </pre>
 * 
 * @Author Xuyh created at 2016年9月30日 下午4:34:39
 */
public class HttpDownloader extends Downloader {
	/**
	 * 获取HttpDownloader实例对象
	 * 
	 * @return
	 */
	public static HttpDownloader getInstance() {
		return new HttpDownloader();
	}

	/**
	 * 获取HttpDownloader实例对象
	 * 
	 * @param cookie
	 * @return
	 */
	public static HttpDownloader getInstance(Cookie cookie) {
		return new HttpDownloader(cookie);
	}

	private HttpDownloader() {
	}

	private HttpDownloader(Cookie cookie) {
		super(cookie);
	}

	protected void bindConnectionType() {
		setConnectionType(Connection.CONNECTION_TYPE_HTTP);
	}
}
