package xuyihao.JohnsonHttpConnector.connectors.http.entity;

import junit.framework.TestCase;

public class CookieTest extends TestCase {

	public void testNewCookieInstance() {
		Cookie cookie = Cookie.newCookieInstance("ASP.NET_SessionId_NS_Sig=oenCV6mdmn1_6VC_; path=/; HttpOnly");
		cookie.addCookieValue("JSession", "kdaoifhjoaihv");
		System.out.println(cookie.getPath());
		System.out.println(cookie.getDomain());
		System.out.println(cookie.getExpire());
		System.out.println(cookie.isSecure());
		System.out.println(cookie.isHttpOnly());
		System.out.println(cookie.convertCookieToCookieSetString());
		System.out.println(cookie.convertCookieToCookieValueString());
	}
}
