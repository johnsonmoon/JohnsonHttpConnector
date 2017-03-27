package xuyihao.http.connectors;

import java.util.HashMap;

import xuyihao.http.connectors.http.HttpDownloader;
import xuyihao.http.connectors.http.HttpMultiThreadDownloader;
import xuyihao.http.connectors.http.HttpRequestSender;
import xuyihao.http.connectors.https.HttpsDownloader;
import xuyihao.http.connectors.https.HttpsMultiThreadDownloader;
import xuyihao.http.connectors.https.HttpsRequestSender;
import xuyihao.http.entity.Cookie;

/**
 * 网络工具工厂类
 * 
 * <pre>
 * 	单例工具类:
 * 	HttpRequestSender,HttpDownloader,
 * 	HttpsRequestSender,HttpsDownloader
 * 	
 * 	非单例工具类:
 * 	HttpMultiThreadDownloader,HttpsMultiThreadDownloader
 * </pre>
 * 
 * Created by Xuyh at 2017年3月27日 下午4:39:10.
 */
public class ConnectorFactory {
	/**
	 * HTTP connectors 单例
	 */
	private static HttpRequestSender httpRequestSender = null;
	private static HttpDownloader httpDownloader = null;
	/**
	 * HTTPS connectors 单例
	 */
	private static HttpsRequestSender httpsRequestSender = null;
	private static HttpsDownloader httpsDownloader = null;

	/**
	 * 获取HttpRequestSender单例对象
	 * 
	 * @return
	 */
	public static HttpRequestSender getHttpRequestSender() {
		if (httpRequestSender == null)
			httpRequestSender = HttpRequestSender.getInstance();
		return httpRequestSender;
	}

	/**
	 * 获取HttpRequestSender单例对象
	 * 
	 * @param cookie
	 * @return
	 */
	public static HttpRequestSender getHttpRequestSender(Cookie cookie) {
		if (httpRequestSender == null)
			httpRequestSender = HttpRequestSender.getInstance(cookie);
		return httpRequestSender;
	}

	/**
	 * 获取HttpDownloader单例对象
	 * 
	 * @return
	 */
	public static HttpDownloader getHttpDownloader() {
		if (httpDownloader == null)
			httpDownloader = HttpDownloader.getInstance();
		return httpDownloader;
	}

	/**
	 * 获取HttpDownloader单例对象
	 * 
	 * @param cookie
	 * @return
	 */
	public static HttpDownloader getHttpDownloader(Cookie cookie) {
		if (httpDownloader == null)
			httpDownloader = HttpDownloader.getInstance(cookie);
		return httpDownloader;
	}

	/**
	 * 获取HttpsRequestSender单例对象
	 * 
	 * @return
	 */
	public static HttpsRequestSender getHttpsRequestSender() {
		if (httpsRequestSender == null)
			httpsRequestSender = HttpsRequestSender.getInstance();
		return httpsRequestSender;
	}

	/**
	 * 获取HttpsRequestSender单例对象
	 * 
	 * @param cookie
	 * @return
	 */
	public static HttpsRequestSender getHttpsRequestSender(Cookie cookie) {
		if (httpsRequestSender == null)
			httpsRequestSender = HttpsRequestSender.getInstance(cookie);
		return httpsRequestSender;
	}

	/**
	 * 获取HttpsDownloader单例对象
	 * 
	 * @return
	 */
	public static HttpsDownloader getHttpsDownloader() {
		if (httpsDownloader == null)
			httpsDownloader = HttpsDownloader.getInstance();
		return httpsDownloader;
	}

	/**
	 * 获取HttpsDownloader单例对象
	 * 
	 * @param cookie
	 * @return
	 */
	public static HttpsDownloader getHttpsDownloader(Cookie cookie) {
		if (httpsDownloader == null)
			httpsDownloader = HttpsDownloader.getInstance(cookie);
		return httpsDownloader;
	}

	/**
	 * 获取HttpMultiThreadDownloader对象实例(非单例)
	 * 
	 * @param actionURL 需要下载资源的URL地址,不跟参数,或者直接将参数写在URL上面
	 * @param threadNumber 需要启动的下载线程数量
	 * @return
	 */
	public static HttpMultiThreadDownloader getHttpMultiThreadDownloader(String actionURL, int threadNumber) {
		return HttpMultiThreadDownloader.getInstance(actionURL, threadNumber);
	}

	/**
	 * 获取HttpMultiThreadDownloader对象实例(非单例)
	 * 
	 * @param actionURL 需要下载资源的URL地址
	 * @param parameters URL后的具体参数，以key=value的形式传递
	 * @param threadNumber 需要启动的下载线程数量
	 * @return
	 */
	public static HttpMultiThreadDownloader getHttpMultiThreadDownloader(String actionURL,
			HashMap<String, String> parameters, int threadNumber) {
		return HttpMultiThreadDownloader.getInstance(actionURL, parameters, threadNumber);
	}

	/**
	 * 获取HttpMultiThreadDownloader对象实例(非单例)
	 * 
	 * @param actionURL 需要下载资源的URL地址
	 * @param parameters URL后的具体参数，以key=value的形式传递
	 * @param threadNumber 需要启动的下载线程数量
	 * @param cookie 保持会话信息的cookie
	 * @return
	 */
	public static HttpMultiThreadDownloader getHttpMultiThreadDownloader(String actionURL,
			HashMap<String, String> parameters, int threadNumber, Cookie cookie) {
		return HttpMultiThreadDownloader.getInstance(actionURL, parameters, threadNumber, cookie);
	}

	/**
	 * 获取HttpsMultiThreadDownloader对象实例(非单例)
	 * 
	 * @param actionURL 需要下载资源的URL地址,不跟参数,或者直接将参数写在URL上面
	 * @param threadNumber 需要启动的下载线程数量
	 * @return
	 */
	public static HttpsMultiThreadDownloader getHttpsMultiThreadDownloader(String actionURL, int threadNumber) {
		return HttpsMultiThreadDownloader.getInstance(actionURL, threadNumber);
	}

	/**
	 * 获取HttpsMultiThreadDownloader对象实例(非单例)
	 * 
	 * @param actionURL 需要下载资源的URL地址
	 * @param parameters URL后的具体参数，以key=value的形式传递
	 * @param threadNumber 需要启动的下载线程数量
	 * @return
	 */
	public static HttpsMultiThreadDownloader getHttpsMultiThreadDownloader(String actionURL,
			HashMap<String, String> parameters, int threadNumber) {
		return HttpsMultiThreadDownloader.getInstance(actionURL, parameters, threadNumber);
	}

	/**
	 * 获取HttpsMultiThreadDownloader对象实例(非单例)
	 * 
	 * @param actionURL 需要下载资源的URL地址
	 * @param parameters URL后的具体参数，以key=value的形式传递
	 * @param threadNumber 需要启动的下载线程数量
	 * @param cookie 保持会话信息的cookie
	 * @return
	 */
	public static HttpsMultiThreadDownloader getHttpsMultiThreadDownloader(String actionURL,
			HashMap<String, String> parameters, int threadNumber, Cookie cookie) {
		return HttpsMultiThreadDownloader.getInstance(actionURL, parameters, threadNumber, cookie);
	}
}
