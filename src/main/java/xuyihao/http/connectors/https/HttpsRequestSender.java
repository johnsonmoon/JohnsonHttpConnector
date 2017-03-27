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
	public HttpsRequestSender() {
	}

	public HttpsRequestSender(Cookie cookie) {
		super(cookie);
	}

	protected void bindConnectionType() {
		setConnectionType(CommonConnection.CONNECTION_TYPE_HTTPS);
	}
}
