package xuyihao.http.connectors.https;

import java.util.Map;

import xuyihao.http.connectors.common.Connection;
import xuyihao.http.connectors.common.MultiThreadDownloader;
import xuyihao.http.entity.Cookie;

/**
 * 网络资源(文件)多线程下载工具类(https)
 *
 * <pre>
 * 发送GET请求，接收网络文件
 * 此工具类支持多线程下载
 * 添加cookie支持
 * 需要服务器端发送文件内容长度响应，即响应头部包含文件长度Content-length
 * 如果获取不到文件长度，则download方法会结束并返回false
 * </pre>
 * 
 * Created by Xuyh on 2016/12/9.
 */
public class HttpsMultiThreadDownloader extends MultiThreadDownloader {
	/**
	 * 获取HttpsMultiThreadDownloader实例对象
	 * 
	 * @param actionURL 需要下载资源的URL地址,不跟参数,或者直接将参数写在URL上面
	 * @param threadNumber 需要启动的下载线程数量
	 * @return
	 */
	public static HttpsMultiThreadDownloader getInstance(String actionURL, int threadNumber) {
		return new HttpsMultiThreadDownloader(actionURL, threadNumber);
	}

	/**
	 * 获取HttpsMultiThreadDownloader实例对象
	 * 
	 * @param actionURL 需要下载资源的URL地址
	 * @param parameters URL后的具体参数，以key=value的形式传递
	 * @param threadNumber 需要启动的下载线程数量
	 * @return
	 */
	public static HttpsMultiThreadDownloader getInstance(String actionURL, Map<String, String> parameters,
			int threadNumber) {
		return new HttpsMultiThreadDownloader(actionURL, parameters, threadNumber);
	}

	/**
	 * 获取HttpsMultiThreadDownloader实例对象
	 * 
	 * @param actionURL 需要下载资源的URL地址
	 * @param parameters URL后的具体参数，以key=value的形式传递
	 * @param threadNumber 需要启动的下载线程数量
	 * @param cookie 保持会话信息的cookie
	 * @return
	 */
	public static HttpsMultiThreadDownloader getInstance(String actionURL, Map<String, String> parameters,
			int threadNumber, Cookie cookie) {
		return new HttpsMultiThreadDownloader(actionURL, parameters, threadNumber, cookie);
	}

	private HttpsMultiThreadDownloader(String actionURL, int threadNumber) {
		super(actionURL, threadNumber);
	}

	private HttpsMultiThreadDownloader(String actionURL, Map<String, String> parameters, int threadNumber) {
		super(actionURL, parameters, threadNumber);
	}

	private HttpsMultiThreadDownloader(String actionURL, Map<String, String> parameters, int threadNumber,
			Cookie cookie) {
		super(actionURL, parameters, threadNumber, cookie);
	}

	protected void bindConnectionType() {
		setConnectionType(Connection.CONNECTION_TYPE_HTTPS);
	}
}
