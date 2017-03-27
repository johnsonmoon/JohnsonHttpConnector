package xuyihao.http;

import org.junit.Test;

import junit.framework.TestCase;
import xuyihao.http.common.utils.CommonUtils;
import xuyihao.http.connectors.ConnectorFactory;
import xuyihao.http.connectors.http.HttpRequestSender;

public class GeneralTest extends TestCase {
	@Test
	public void test() {
		HttpRequestSender sender = ConnectorFactory.getHttpRequestSender();
		String body = "{\"name\":\"xuyh\", \"password\":\"123456\", \"phoneNum\":\"15700083767\", \"email\":\"841846248@qq.com\"}";
		String result = sender.executePostByJSON("http://115.28.192.61:9680/monitor/rest/v1/client/user", body);
		CommonUtils.output(result);
	}
}
