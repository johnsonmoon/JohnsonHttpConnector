package xuyihao.JohnsonHttpConnector.connectors.http;

import xuyihao.JohnsonHttpConnector.connectors.Common.CommonConnection;
import xuyihao.JohnsonHttpConnector.connectors.Common.CommonRequestSender;
import xuyihao.JohnsonHttpConnector.entity.Cookie;

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
	public HttpRequestSender() {
	}

	public HttpRequestSender(Cookie cookie) {
		super(cookie);
	}

	protected void bindConnectionType() {
		setConnectionType(CommonConnection.CONNECTION_TYPE_HTTP);
	}
}
