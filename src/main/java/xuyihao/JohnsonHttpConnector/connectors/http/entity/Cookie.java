package xuyihao.JohnsonHttpConnector.connectors.http.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Cookie实体类
 * 
 * @author Johnson
 *
 */
public class Cookie {
	public final static String COOKIE_ATTRIBUTE_NAME_EXPIRE = "expire";
	public final static String COOKIE_ATTRIBUTE_NAME_PATH = "path";
	public final static String COOKIE_ATTRIBUTE_NAME_DOMAIN = "domain";
	public final static String COOKIE_ATTRIBUTE_NAME_SECURE = "secure";
	public final static String COOKIE_ATTRIBUTE_NAME_HTTPONLY = "HttpOnly";
	public final static String COOKIE_ATTRIBUTE_NAME_HTTPONLY_LOWERCASE = "httponly";
	/**
	 * cookie的有效值
	 */
	private Map<String, String> cookieValue = new HashMap<String, String>();
	/**
	 * cookie的有效期
	 */
	private String expire = "";
	/**
	 * cookie生效的服务器路径
	 */
	private String path = "";
	/**
	 * cookie的域名
	 */
	private String domain = "";
	/**
	 * 是否通过安全的HTTPS连接来传输cookie
	 */
	private boolean secure = false;
	private boolean HttpOnly = false;

	/**
	 * 从服务器获得的Set-Cookie字串解析成Cookie对象
	 * 
	 * @param set_cookie
	 * @return Cookie对象
	 */
	public static Cookie newCookieInstance(String set_cookie) {
		Cookie cookie = new Cookie();
		String[] params = set_cookie.split("; ");
		for (String param : params) {
			if (param.toLowerCase().contains(COOKIE_ATTRIBUTE_NAME_PATH)) {
				cookie.setPath(param.substring(param.indexOf("=") + 1));
			} else if (param.toLowerCase().contains(COOKIE_ATTRIBUTE_NAME_DOMAIN)) {
				cookie.setDomain(param.substring(param.indexOf("=") + 1));
			} else if (param.toLowerCase().contains(COOKIE_ATTRIBUTE_NAME_EXPIRE)) {
				cookie.setExpire(param.substring(param.indexOf("=") + 1));
			} else if (param.toLowerCase().contains(COOKIE_ATTRIBUTE_NAME_SECURE)) {
				cookie.setSecure(true);
			} else if (param.toLowerCase().contains(COOKIE_ATTRIBUTE_NAME_HTTPONLY_LOWERCASE)) {
				cookie.setHttpOnly(true);
			} else {
				String[] nameAndValue = param.split("=");
				cookie.addCookieValue(nameAndValue[0], nameAndValue[1]);
			}
		}
		return cookie;
	}

	/**
	 * 获取服务器发送客户端的set-cookie字串
	 * 
	 * @return
	 */
	public String convertCookieToCookieSetString() {
		String Set_Cookie = "";
		String cookieValueString = "";
		for (String name : this.cookieValue.keySet()) {
			cookieValueString += ("; " + name + "=" + this.cookieValue.get(name));
		}
		cookieValueString = cookieValueString.substring(2);
		Set_Cookie += cookieValueString;
		if (this.path != null && !this.path.equals("")) {
			Set_Cookie += ("; " + COOKIE_ATTRIBUTE_NAME_PATH + "=" + this.path);
		}
		if (this.domain != null && !this.domain.equals("")) {
			Set_Cookie += ("; " + COOKIE_ATTRIBUTE_NAME_DOMAIN + "=" + this.domain);
		}
		if (this.expire != null && !this.expire.equals("")) {
			Set_Cookie += (";" + COOKIE_ATTRIBUTE_NAME_EXPIRE + "=" + this.expire);
		}
		if (this.secure) {
			Set_Cookie += ("; " + COOKIE_ATTRIBUTE_NAME_SECURE);
		}
		if (this.HttpOnly) {
			Set_Cookie += ("; " + COOKIE_ATTRIBUTE_NAME_HTTPONLY);
		}
		return Set_Cookie;
	}

	/**
	 * 添加cookie值
	 * 
	 * @param name
	 * @param value
	 */
	public void addCookieValue(String name, String value) {
		this.cookieValue.put(name, value);
	}

	/**
	 * 获取cookie值的字串
	 * 
	 * @return
	 */
	public String convertCookieToCookieValueString() {
		String cookieValueString = "";
		for (String name : this.cookieValue.keySet()) {
			cookieValueString += ("; " + name + "=" + this.cookieValue.get(name));
		}
		cookieValueString = cookieValueString.substring(2);
		return cookieValueString;
	}

	public Map<String, String> getCookieValue() {
		return cookieValue;
	}

	public void setCookieValue(Map<String, String> cookieValue) {
		this.cookieValue = cookieValue;
	}

	public String getExpire() {
		return expire;
	}

	public void setExpire(String expire) {
		this.expire = expire;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public boolean isHttpOnly() {
		return HttpOnly;
	}

	public void setHttpOnly(boolean httpOnly) {
		HttpOnly = httpOnly;
	}
}
