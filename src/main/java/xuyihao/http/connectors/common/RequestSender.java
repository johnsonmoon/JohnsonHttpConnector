package xuyihao.http.connectors.common;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import xuyihao.http.connectors.util.DataUtils;
import xuyihao.http.entity.Cookie;
import xuyihao.http.enums.MIME_FileType;

/**
 * 网络请求类
 *
 * <pre>
 * 发送GET POST请求, 接收字符串返回值
 * 添加会话(session)支持[cookie实现]
 * </pre>
 *
 * Created by Xuyh on 2016/12/9.
 */
public abstract class RequestSender {
	private int connectionType;
	/**
	 * cookie的配置逻辑： 每次请求发送时候都会在请求头带上cookie信息(如果cookie为null则不带上),
	 * 然后从响应头中获取新的cookie值刷新当前值,可以起到保存同服务器的会话的作用
	 */
	private Cookie cookie = null;

	/**
	 * 需要重写的方法
	 * 
	 * <pre>
	 *     用法：设置连接类型
	 *     调用setConnectionType()方法
	 *     参数值
	 *     Connection.CONNECTION_TYPE_HTTP
	 *     或
	 *     Connection.CONNECTION_TYPE_HTTPS
	 * </pre>
	 *
	 */
	protected abstract void bindConnectionType();

	protected void setConnectionType(int connectionType) {
		this.connectionType = connectionType;
	}

	public RequestSender() {
		bindConnectionType();
	}

	public RequestSender(Cookie cookie) {
		bindConnectionType();
		this.cookie = cookie;
	}

	public Cookie getCookie() {
		return cookie;
	}

	public void setCookie(Cookie cookie) {
		this.cookie = cookie;
	}

	/**
	 * 删除cookie信息，使cookie无效
	 */
	public void invalidateCookie() {
		this.cookie = null;
	}

	/**
	 * 执行post发送josn格式body的请求
	 * 
	 * <pre>
	 * 发送请求的请求body由json字串组成
	 * </pre>
	 * 
	 * @param actionURL 发送post请求的URL地址
	 * @param requestBody 请求body
	 * @return
	 */
	public String executePostByJSON(String actionURL, String requestBody) {
		String response = "";
		try {
			URL url = new URL(actionURL);
			Connection connection = Connection.getInstance(url, connectionType);
			DataUtils.setPostConnectionPropertiesByJSON(connection);
			// 如果cookie不为空
			if (this.cookie != null) {
				connection.setRequestProperty("cookie", this.cookie.convertCookieToCookieValueString());
			}
			// 设置请求数据内容
			DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
			// 使用write(requestContent.getBytes())是为了防止中文出现乱码
			ds.write(requestBody.getBytes());
			ds.flush();
			// 获取服务器响应头的cookie信息
			String set_cookie = connection.getHeaderField("Set-Cookie");
			if (set_cookie != null && !set_cookie.equals("")) {
				this.cookie = Cookie.newCookieInstance(set_cookie);
			}
			response = DataUtils.resolveResponse(connection.getInputStream());
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 执行发送post请求的方法
	 *
	 * <pre>
	 * 发送请求使用enctype="application/x-www-form-urlencoded"编码方式
	 * 参数形式形如key1=value1&key2=value2
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateCookie方法
	 * </pre>
	 *
	 * @param actionURL 发送post请求的URL地址
	 * @param parameters 发送post请求数据段中的参数,以Map<String, String>形式传入key=value值
	 * @return "" if no response get
	 */
	public String executePostByUsual(String actionURL, Map<String, String> parameters) {
		String response = "";
		try {
			URL url = new URL(actionURL);
			Connection connection = Connection.getInstance(url, connectionType);
			DataUtils.setPostConnectionPropertiesByURLEncoded(connection);
			// 如果cookie不为空
			if (this.cookie != null) {
				connection.setRequestProperty("cookie", this.cookie.convertCookieToCookieValueString());
			}
			// 设置请求数据内容
			String requestContent = "";
			Set<String> keys = parameters.keySet();
			for (String key : keys) {
				requestContent = requestContent + key + "=" + parameters.get(key) + "&";
			}
			requestContent = requestContent.substring(0, requestContent.lastIndexOf("&"));
			DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
			// 使用write(requestContent.getBytes())是为了防止中文出现乱码
			ds.write(requestContent.getBytes());
			ds.flush();
			// 获取服务器响应头的cookie信息
			String set_cookie = connection.getHeaderField("Set-Cookie");
			if (set_cookie != null && !set_cookie.equals("")) {
				this.cookie = Cookie.newCookieInstance(set_cookie);
			}
			response = DataUtils.resolveResponse(connection.getInputStream());
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 执行发送post请求的方法
	 * <p>
	 *
	 * <pre>
	 * 发送请求使用enctype="multipart/form-data"编码方式
	 * 请求内容格式参考表单提交方式
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateCookie方法
	 * </pre>
	 *
	 * @param actionURL
	 *          发送post请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @param parameters 发送post请求数据段中的参数,以Map<String, String>形式传入key=value值
	 * @return "" if no response get
	 */
	public String executePostByMultipart(String actionURL, Map<String, String> parameters) {
		String response = "";
		try {
			URL url = new URL(actionURL);
			Connection connection = Connection.getInstance(url, connectionType);
			DataUtils.setPostConnectionPropertiesByMultiPart(connection);
			// 如果存在会话，则写入会话sessionID到cookie里面
			// 如果cookie不为空
			if (this.cookie != null) {
				connection.setRequestProperty("cookie", this.cookie.convertCookieToCookieValueString());
			}
			DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
			DataUtils.addPostCommonData(parameters, ds);
			DataUtils.addPostEndData(ds);
			ds.flush();
			// 获取服务器响应头的cookie信息
			String set_cookie = connection.getHeaderField("Set-Cookie");
			if (set_cookie != null && !set_cookie.equals("")) {
				this.cookie = Cookie.newCookieInstance(set_cookie);
			}
			response = DataUtils.resolveResponse(connection.getInputStream());
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 执行发送get请求的方法
	 * <p>
	 *
	 * <pre>
	 * 直接通过actionURL发送请求,用户也可以自己设置actionURL后面的参数
	 * 这个方法比Map传递参数的方法性能要高
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateCookie方法
	 * </pre>
	 *
	 * @param actionURL 发送get请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @return "" if no response get
	 */
	public String executeGet(String actionURL) {
		String response = "";
		try {
			String trueRequestURL = actionURL;
			URL url = new URL(trueRequestURL);
			Connection connection = Connection.getInstance(url, connectionType);
			connection.setRequestMethod("GET");
			// 如果cookie不为空
			if (this.cookie != null) {
				connection.setRequestProperty("cookie", this.cookie.convertCookieToCookieValueString());
			}
			// 获取服务器响应头的cookie信息
			String set_cookie = connection.getHeaderField("Set-Cookie");
			if (set_cookie != null && !set_cookie.equals("")) {
				this.cookie = Cookie.newCookieInstance(set_cookie);
			}
			response = DataUtils.resolveResponse(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 执行发送get请求的方法
	 * <p>
	 *
	 * <pre>
	 * 最后发送的URL格式为(例如:http://www.johnson.cc:8080/Test/download?file=file1&name=xxx&pwd=aaa)
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateCookie方法
	 * </pre>
	 *
	 * @param actionURL 发送get请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @param parameters 发送get请求URL后跟着的具体参数,以Map<String, String>形式传入key=value值
	 * @return "" if no response get
	 */
	public String executeGet(String actionURL, Map<String, String> parameters) {
		String response = "";
		try {
			String trueRequestURL = actionURL;
			trueRequestURL += "?";
			Set<String> keys = parameters.keySet();
			for (String key : keys) {
				trueRequestURL = trueRequestURL + key + "=" + parameters.get(key) + "&";
			}
			trueRequestURL = trueRequestURL.substring(0, trueRequestURL.lastIndexOf("&"));
			URL url = new URL(trueRequestURL);
			Connection connection = Connection.getInstance(url, connectionType);
			connection.setRequestMethod("GET");
			// 如果cookie不为空
			if (this.cookie != null) {
				connection.setRequestProperty("cookie", this.cookie.convertCookieToCookieValueString());
			}
			// 获取服务器响应头的cookie信息
			String set_cookie = connection.getHeaderField("Set-Cookie");
			if (set_cookie != null && !set_cookie.equals("")) {
				this.cookie = Cookie.newCookieInstance(set_cookie);
			}
			response = DataUtils.resolveResponse(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 模拟提交表单数据上传单个文件
	 * <p>
	 *
	 * <pre>
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateCookie方法
	 * </pre>
	 *
	 * @param actionURL 上传文件的URL
	 * @param uploadFile 上传文件的路径字符串
	 * @param formFileName 表单中文件的名称
	 * @param fileType 文件类型(枚举类型)
	 * @return "" if no response get
	 */
	public String singleFileUpload(String actionURL, String uploadFile, String formFileName, MIME_FileType fileType) {
		String response = "";
		try {
			URL url = new URL(actionURL);
			Connection connection = Connection.getInstance(url, connectionType);
			DataUtils.setPostConnectionPropertiesByMultiPart(connection);
			// 如果cookie不为空
			if (this.cookie != null) {
				connection.setRequestProperty("cookie", this.cookie.convertCookieToCookieValueString());
			}
			DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
			String fileName = uploadFile.substring(uploadFile.lastIndexOf(File.separator) + 1);
			DataUtils.addPostSingleFileData(uploadFile, fileName, formFileName, fileType, ds);
			DataUtils.addPostEndData(ds);
			ds.flush();
			// 获取服务器响应头的cookie信息
			String set_cookie = connection.getHeaderField("Set-Cookie");
			if (set_cookie != null && !set_cookie.equals("")) {
				this.cookie = Cookie.newCookieInstance(set_cookie);
			}
			response = DataUtils.resolveResponse(connection.getInputStream());
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 模拟提交表单数据上传多个文件的方法
	 * <p>
	 *
	 * <pre></pre>
	 *
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateCookie方法
	 * </pre>
	 *
	 * @param actionURL 上传文件的URL地址包括URL
	 * @param uploadFiles 上传文件的路径字符串数组,表示多个文件
	 * @param formFileNames 表单中的文件名称数组
	 * @param fileType 文件类型(枚举类型)
	 * @return "" if no response get
	 */
	public String multipleFileUpload(String actionURL, String[] uploadFiles, String[] formFileNames,
			MIME_FileType fileType) {
		String response = "";
		try {
			URL url = new URL(actionURL);
			Connection connection = Connection.getInstance(url, connectionType);
			DataUtils.setPostConnectionPropertiesByMultiPart(connection);
			// 如果cookie不为空
			if (this.cookie != null) {
				connection.setRequestProperty("cookie", this.cookie.convertCookieToCookieValueString());
			}
			DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
			DataUtils.addPostMultiFileData(uploadFiles, formFileNames, ds, fileType);
			DataUtils.addPostEndData(ds);
			ds.flush();
			// 获取服务器响应头的cookie信息
			String set_cookie = connection.getHeaderField("Set-Cookie");
			if (set_cookie != null && !set_cookie.equals("")) {
				this.cookie = Cookie.newCookieInstance(set_cookie);
			}
			response = DataUtils.resolveResponse(connection.getInputStream());
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 集上传单个文件与传递参数于一体的方法
	 * <p>
	 *
	 * <pre>
	 * 上传文件name为file(服务器解析)
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateCookie方法
	 * </pre>
	 *
	 * @param actionURL 上传文件的URL地址包括URL
	 * @param uploadFile 上传文件的路径字符串
	 * @param formFileName 表单文件的名称
	 * @param fileType 文件类型(枚举类型)
	 * @param parameters 跟文件一起传输的参数(Map)
	 * @return "" if no response get
	 */
	public String singleFileUploadWithParameters(String actionURL, String uploadFile, String formFileName,
			MIME_FileType fileType, Map<String, String> parameters) {
		String response = "";
		try {
			URL url = new URL(actionURL);
			Connection connection = Connection.getInstance(url, connectionType);
			DataUtils.setPostConnectionPropertiesByMultiPart(connection);
			// 如果cookie不为空
			if (this.cookie != null) {
				connection.setRequestProperty("cookie", this.cookie.convertCookieToCookieValueString());
			}
			DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
			String fileName = uploadFile.substring(uploadFile.lastIndexOf(File.separator) + 1);
			DataUtils.addPostCommonData(parameters, ds);
			DataUtils.addPostSingleFileData(uploadFile, fileName, formFileName, fileType, ds);
			DataUtils.addPostEndData(ds);
			ds.flush();
			// 获取服务器响应头的cookie信息
			String set_cookie = connection.getHeaderField("Set-Cookie");
			if (set_cookie != null && !set_cookie.equals("")) {
				this.cookie = Cookie.newCookieInstance(set_cookie);
			}
			response = DataUtils.resolveResponse(connection.getInputStream());
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 集上传多个文件与传递参数于一体的方法
	 * <p>
	 *
	 * <pre>
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateCookie方法
	 * 上传文件name为file0,file1,file2,以此类推(服务器解析)
	 * </pre>
	 *
	 * @param actionURL 上传文件的URL地址包括URL
	 * @param uploadFiles 上传文件的路径字符串数组,表示多个文件
	 * @param formFileNames 表单文件名称数组
	 * @param fileType 文件类型(枚举类型)
	 * @param parameters 跟文件一起传输的参数(Map)
	 * @return "" if no response get
	 */
	public String multipleFileUploadWithParameters(String actionURL, String[] uploadFiles, String[] formFileNames,
			MIME_FileType fileType, Map<String, String> parameters) {
		String response = "";
		try {
			URL url = new URL(actionURL);
			Connection connection = Connection.getInstance(url, connectionType);
			DataUtils.setPostConnectionPropertiesByMultiPart(connection);
			if (this.cookie != null) {
				connection.setRequestProperty("cookie", this.cookie.convertCookieToCookieValueString());
			}
			DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
			DataUtils.addPostCommonData(parameters, ds);
			DataUtils.addPostMultiFileData(uploadFiles, formFileNames, ds, fileType);
			DataUtils.addPostEndData(ds);
			ds.flush();
			// 获取服务器响应头的cookie信息
			String set_cookie = connection.getHeaderField("Set-Cookie");
			if (set_cookie != null && !set_cookie.equals("")) {
				this.cookie = Cookie.newCookieInstance(set_cookie);
			}
			response = DataUtils.resolveResponse(connection.getInputStream());
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}
}
