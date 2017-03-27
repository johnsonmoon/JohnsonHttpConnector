package xuyihao.http.connectors.https;

import xuyihao.http.connectors.common.CommonConnection;
import xuyihao.http.connectors.common.CommonRequestSender;
import xuyihao.http.entity.Cookie;

/**
 * 网络请求类(Https)
 *
 * <pre>
 * 发送GET POST请求, 接收字符串返回值
 * 添加会话(session)支持[cookie实现]
 * </pre>
 *
 * Created by Xuyh on 2016/12/8.
 */
public class HttpsRequestSender extends CommonRequestSender {
	/**
	 * 获取HttpsRequestSender实例对象
	 * 
	 * @return
	 */
	public static HttpsRequestSender getInstance() {
		return new HttpsRequestSender();
	}

	/**
	 * 获取HttpsRequestSender实例对象
	 * 
	 * @param cookie
	 * @return
	 */
	public static HttpsRequestSender getInstance(Cookie cookie) {
		return new HttpsRequestSender(cookie);
	}

	private HttpsRequestSender() {
	}

	private HttpsRequestSender(Cookie cookie) {
		super(cookie);
	}

	protected void bindConnectionType() {
		setConnectionType(CommonConnection.CONNECTION_TYPE_HTTPS);
	}
}
