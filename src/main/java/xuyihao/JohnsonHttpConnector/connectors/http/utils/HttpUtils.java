package xuyihao.JohnsonHttpConnector.connectors.http.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import xuyihao.JohnsonHttpConnector.connectors.http.entity.Cookie;

/**
 * HTTP工具类
 * 
 * create by Xuyh at 2016年10月1日 下午11:15:51.
 *
 */
public class HttpUtils {
	/**
	 * 从URL获取cookie信息
	 * 
	 * @param actionURL
	 * @return
	 */
	public static Cookie getCookie(String actionURL) {
		Cookie cookie = null;
		try {
			URL url = new URL(actionURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			String cookieValue = connection.getHeaderField("Set-Cookie");
			cookie = Cookie.newCookieInstance(cookieValue);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cookie;
	}
}
