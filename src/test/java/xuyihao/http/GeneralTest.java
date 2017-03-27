package xuyihao.http;

import org.junit.Test;

import junit.framework.TestCase;
import xuyihao.http.common.utils.CommonUtils;
import xuyihao.http.connectors.ConnectorFactory;
import xuyihao.http.connectors.http.HttpRequestSender;
import xuyihao.http.entity.Cookie;

public class GeneralTest extends TestCase {
	@Test
	public void test() {
		HttpRequestSender sender = ConnectorFactory.getHttpRequestSender();
		String body = "{\"name\":\"xuyh\", \"password\":\"123456\"}";
		String result = sender.executePostByJSON("http://115.28.192.61:9680/monitor/rest/v1/client/user/bind", body);
		CommonUtils.output(result);
		String tocken = CommonUtils.input();
		Cookie cookie = new Cookie();
		cookie.addCookieValue("tocken", tocken);
		sender.setCookie(cookie);
		result = sender.executeGet("http://115.28.192.61:9680/monitor/rest/v1/client/user/info");
		CommonUtils.output(result);
	}
}
