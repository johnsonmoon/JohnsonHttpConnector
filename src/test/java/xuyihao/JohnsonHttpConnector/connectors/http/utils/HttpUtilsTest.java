package xuyihao.JohnsonHttpConnector.connectors.http.utils;

import junit.framework.Assert;
import junit.framework.TestCase;
import xuyihao.JohnsonHttpConnector.connectors.http.entity.Cookie;

/**
 * 
 * create by Xuyh at 2016年10月1日 下午11:15:39.
 *
 */
public class HttpUtilsTest extends TestCase {

	public void testGetCookie() {
		Cookie cookie = HttpUtils.getCookie("https://github.com/johnsonmoon");
		Assert.assertNotNull(cookie);
		if (cookie != null) {
			System.out.println(cookie.convertCookieToCookieValueString());
			System.out.println(cookie.convertCookieToCookieSetString());
		}
	}

}
