package xuyihao.http.connectors.http;

import xuyihao.http.connectors.common.CommonConnection;
import xuyihao.http.connectors.common.CommonRequestSender;
import xuyihao.http.entity.Cookie;

/**
 * 网络请求类(Http)
 * 
 * <pre>
 * 发送GET POST请求, 接收字符串返回值
 * 添加会话(session)支持[cookie实现]
 * </pre>
 *
 * @Author Xuyh created at 2016年9月30日 下午5:15:45
 */
public class HttpRequestSender extends CommonRequestSender {
	/**
	 * 获取HttpRequestSender对象实例
	 * 
	 * @return
	 */
	public static HttpRequestSender getInstance() {
		return new HttpRequestSender();
	}

	/**
	 * 获取HttpRequestSender对象实例
	 * 
	 * @param cookie
	 * @return
	 */
	public static HttpRequestSender getInstance(Cookie cookie) {
		return new HttpRequestSender(cookie);
	}

	private HttpRequestSender() {
	}

	private HttpRequestSender(Cookie cookie) {
		super(cookie);
	}

	protected void bindConnectionType() {
		setConnectionType(CommonConnection.CONNECTION_TYPE_HTTP);
	}
}
