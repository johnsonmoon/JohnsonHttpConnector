package xuyihao.JohnsonHttpConnector.connectors.https;

import xuyihao.JohnsonHttpConnector.connectors.Common.CommonConnection;
import xuyihao.JohnsonHttpConnector.connectors.Common.CommonRequestSender;
import xuyihao.JohnsonHttpConnector.entity.Cookie;

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
