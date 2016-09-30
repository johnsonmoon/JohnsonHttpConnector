package xuyihao.JohnsonHttpConnector.connectors;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import xuyihao.JohnsonHttpConnector.enums.MIME_FileType;

/**
 * 网络请求类
 * 
 * <pre>
 * 发送GET POST请求, 接收字符串返回值
 * 添加会话(session)支持
 * </pre>
 * 
 * @Author Xuyh created at 2016年9月30日 下午5:15:45
 */
public class HttpUtil {
	private final String end = "\r\n";
	private final String twoHyphens = "--";
	private final String boundary = "---------------------------7e0dd540448";
	private String sessionID = "";

	public HttpUtil() {
	}

	/**
	 * <pre>
	 * 通过此构造器可以区分使用此工具的平台，并且获取相应URL服务器的会话sessionID
	 * 
	 * 不一定会获取到URL的session
	 * </pre>
	 * 
	 * @param actionURL 需要获取会话sessionID的URL
	 */
	public HttpUtil(String actionURL) {
		this.getSessionIDFromCookie(actionURL);
	}

	/**
	 * 执行从cookie获取会话sessionID的方法，用于保持与服务器的会话
	 * 
	 * <pre>
	 * 执行次方法后执行其他请求方法将会提交cookie，保持会话
	 * </pre>
	 * 
	 * @param actionURL
	 * @return
	 */
	public boolean getSessionIDFromCookie(String actionURL) {
		boolean flag = false;
		try {
			URL url = new URL(actionURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			String cookieValue = connection.getHeaderField("set-cookie");
			if (cookieValue != null) {
				this.sessionID = cookieValue.substring(0, cookieValue.indexOf(";"));
				flag = true;
			} else {
				flag = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * 获取连接的sessionID
	 * 
	 * @return
	 */
	public String getSessionID() {
		return this.sessionID;
	}

	/**
	 * 使连接的sessionID无效，即删除会话信息
	 */
	public void invalidateSessionID() {
		this.sessionID = "";
	}

	/**
	 * 执行发送post请求的方法
	 * 
	 * <pre>
	 * 发送请求使用enctype="application/x-www-form-urlencoded"编码方式
	 * 参数形式形如key1=value1&key2=value2
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param actionURL
	 *          发送post请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @param parameters 发送post请求数据段中的参数,以HashMap<String, String>形式传入key=value值
	 * @return "" if no response get
	 */
	public String executePostByUsual(String actionURL, HashMap<String, String> parameters) {
		String response = "";
		try {
			URL url = new URL(actionURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			;
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			// 如果存在会话，则写入会话sessionID到cookie里面
			if (!this.sessionID.equals("")) {
				connection.setRequestProperty("cookie", this.sessionID);
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
			try {
				// 获取URL的响应
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String s = "";
				String temp = "";
				while ((temp = reader.readLine()) != null) {
					s += temp;
				}
				response = s;
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("No response get!!!");
			}
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
	 * 发送请求使用enctype="multipart/form-data"编码方式
	 * 请求内容格式参考表单提交方式
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param actionURL
	 *          发送post请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @param parameters 发送post请求数据段中的参数,以HashMap<String, String>形式传入key=value值
	 * @return "" if no response get
	 */
	public String executePostByMultipart(String actionURL, HashMap<String, String> parameters) {
		String response = "";
		try {
			URL url = new URL(actionURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// 发送post请求需要下面两行
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// 设置请求参数
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			// 如果存在会话，则写入会话sessionID到cookie里面
			if (!this.sessionID.equals("")) {
				connection.setRequestProperty("cookie", this.sessionID);
			}
			// 获取请求内容输出流
			DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
			// 开始写表单格式内容
			// 写参数
			Set<String> keys = parameters.keySet();
			for (String key : keys) {
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; name=\"");
				// 使用write(key.getBytes())是为了防止key是中文之后出现乱码
				ds.write(key.getBytes());
				ds.writeBytes("\"" + end);
				ds.writeBytes(end);
				ds.write(parameters.get(key).getBytes());
				ds.writeBytes(end);
			}
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			ds.writeBytes(end);
			ds.flush();
			try {
				// 获取URL的响应
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String s = "";
				String temp = "";
				while ((temp = reader.readLine()) != null) {
					s += temp;
				}
				response = s;
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("No response get!!!");
			}
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 执行发送get请求的方法
	 * 
	 * <pre>
	 * 直接通过actionURL发送请求,用户也可以自己设置actionURL后面的参数
	 * 这个方法比HashMap传递参数的方法性能要高
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
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
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// 如果存在会话，则写入会话sessionID到cookie里面
			if (!this.sessionID.equals("")) {
				connection.setRequestProperty("cookie", this.sessionID);
			}
			try {
				// 获取URL的响应
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String s = "";
				String temp = "";
				while ((temp = reader.readLine()) != null) {
					s += temp;
				}
				response = s;
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("No response get!!!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 执行发送get请求的方法
	 * 
	 * <pre>
	 * 最后发送的URL格式为(例如:http://www.johnson.cc:8080/Test/download?file=file1&name=xxx&pwd=aaa)
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param actionURL 发送get请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @param parameters 发送get请求URL后跟着的具体参数,以HashMap<String, String>形式传入key=value值
	 * @return "" if no response get
	 */
	public String executeGet(String actionURL, HashMap<String, String> parameters) {
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
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// 如果存在会话，则写入会话sessionID到cookie里面
			if (!this.sessionID.equals("")) {
				connection.setRequestProperty("cookie", this.sessionID);
			}
			try {
				// 获取URL的响应
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String s = "";
				String temp = "";
				while ((temp = reader.readLine()) != null) {
					s += temp;
				}
				response = s;
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("No response get!!!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 模拟提交表单数据上传单个文件
	 * 
	 * <pre>
	 * 上传文件name为file(服务器解析)
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param actionURL 上传文件的URL
	 * @param uploadFile 上传文件的路径字符串
	 * @param fileType 文件类型(枚举类型)
	 * @return "" if no response get
	 */
	public String singleFileUpload(String actionURL, String uploadFile, MIME_FileType fileType) {
		String response = "";
		try {
			URL url = new URL(actionURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// 发送post请求需要下面两行
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// 设置请求参数
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			// 如果存在会话，则写入会话sessionID到cookie里面
			if (!this.sessionID.equals("")) {
				connection.setRequestProperty("cookie", this.sessionID);
			}
			// 获取请求内容输出流
			DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
			String fileName = uploadFile.substring(uploadFile.lastIndexOf(File.separator) + 1);
			// 开始写表单格式内容
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"file\"; " + "filename=\"");
			// 防止中文乱码
			ds.write(fileName.getBytes());
			ds.writeBytes("\"" + end);
			ds.writeBytes("Content-Type: " + fileType.getValue() + end);
			ds.writeBytes(end);
			// 根据路径读取文件
			FileInputStream fis = new FileInputStream(uploadFile);
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = fis.read(buffer)) != -1) {
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			fis.close();
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			ds.writeBytes(end);
			ds.flush();
			try {
				// 获取URL的响应
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String s = "";
				String temp = "";
				while ((temp = reader.readLine()) != null) {
					s += temp;
				}
				response = s;
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("No response get!!!");
			}
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 模拟提交表单数据上传多个文件的方法
	 * 
	 * <pre>
	 * 上传文件name为file0,file1,file2,以此类推(服务器解析)
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param actionURL 上传文件的URL地址包括URL
	 * @param uploadFiles 上传文件的路径字符串数组,表示多个文件
	 * @param fileType 文件类型(枚举类型)
	 * @return "" if no response get
	 */
	public String multipleFileUpload(String actionURL, String[] uploadFiles, MIME_FileType fileType) {
		String response = "";
		try {
			URL url = new URL(actionURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// 发送post请求需要下面两行
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// 设置请求参数
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			// 如果存在会话，则写入会话sessionID到cookie里面
			if (!this.sessionID.equals("")) {
				connection.setRequestProperty("cookie", this.sessionID);
			}
			// 获取请求内容输出流
			DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
			// 添加post数据
			for (int i = 0; i < uploadFiles.length; i++) {
				String uploadFile = uploadFiles[i];
				String fileName = uploadFile.substring(uploadFile.lastIndexOf(File.separator) + 1);
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; " + "name=\"file" + i + "\"; " + "filename=\"");
				// 防止中文乱码
				ds.write(fileName.getBytes());
				ds.writeBytes("\"" + end);
				ds.writeBytes("Content-Type: " + fileType.getValue() + end);
				ds.writeBytes(end);
				FileInputStream fis = new FileInputStream(uploadFile);
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = fis.read(buffer)) != -1) {
					ds.write(buffer, 0, length);
				}
				ds.writeBytes(end);
				fis.close();
			}
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			ds.writeBytes(end);
			ds.flush();
			try {
				// 获取URL的响应
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String s = "";
				String temp = "";
				while ((temp = reader.readLine()) != null) {
					s += temp;
				}
				response = s;
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("No response get!!!");
			}
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 集上传单个文件与传递参数于一体的方法
	 * 
	 * <pre>
	 * 上传文件name为file(服务器解析)
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param actionURL 上传文件的URL地址包括URL
	 * @param uploadFile 上传文件的路径字符串
	 * @param fileType 文件类型(枚举类型)
	 * @param parameters 跟文件一起传输的参数(HashMap)
	 * @return "" if no response get
	 */
	public String singleFileUploadWithParameters(String actionURL, String uploadFile, MIME_FileType fileType,
			HashMap<String, String> parameters) {
		String response = "";
		try {
			URL url = new URL(actionURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// 发送post请求需要下面两行
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// 设置请求参数
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			// 如果存在会话，则写入会话sessionID到cookie里面
			if (!this.sessionID.equals("")) {
				connection.setRequestProperty("cookie", this.sessionID);
			}
			// 获取请求内容输出流
			DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
			String fileName = uploadFile.substring(uploadFile.lastIndexOf(File.separator) + 1);
			// 开始写表单格式内容
			// 写参数
			Set<String> keys = parameters.keySet();
			for (String key : keys) {
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; name=\"");
				ds.write(key.getBytes());
				ds.writeBytes("\"" + end);
				ds.writeBytes(end);
				ds.write(parameters.get(key).getBytes());
				ds.writeBytes(end);
			}
			// 写文件
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"file\"; " + "filename=\"");
			// 防止中文乱码
			ds.write(fileName.getBytes());
			ds.writeBytes("\"" + end);
			ds.writeBytes("Content-Type: " + fileType.getValue() + end);
			ds.writeBytes(end);
			// 根据路径读取文件
			FileInputStream fis = new FileInputStream(uploadFile);
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = fis.read(buffer)) != -1) {
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			fis.close();
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			ds.writeBytes(end);
			ds.flush();
			try {
				// 获取URL的响应
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String s = "";
				String temp = "";
				while ((temp = reader.readLine()) != null) {
					s += temp;
				}
				response = s;
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("No response get!!!");
			}
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}

	/**
	 * 集上传多个文件与传递参数于一体的方法
	 * 
	 * <pre>
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * 上传文件name为file0,file1,file2,以此类推(服务器解析)
	 * </pre>
	 * 
	 * @param actionURL 上传文件的URL地址包括URL
	 * @param uploadFiles 上传文件的路径字符串数组,表示多个文件
	 * @param fileType 文件类型(枚举类型)
	 * @param parameters 跟文件一起传输的参数(HashMap)
	 * @return "" if no response get
	 */
	public String multipleFileUploadWithParameters(String actionURL, String[] uploadFiles, MIME_FileType fileType,
			HashMap<String, String> parameters) {
		String response = "";
		try {
			URL url = new URL(actionURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			// 发送post请求需要下面两行
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// 设置请求参数
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			// 如果存在会话，则写入会话sessionID到cookie里面
			if (!this.sessionID.equals("")) {
				connection.setRequestProperty("cookie", this.sessionID);
			}
			// 获取请求内容输出流
			DataOutputStream ds = new DataOutputStream(connection.getOutputStream());
			// 开始写表单格式内容
			// 写参数
			Set<String> keys = parameters.keySet();
			for (String key : keys) {
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; name=\"");
				ds.write(key.getBytes());
				ds.writeBytes("\"" + end);
				ds.writeBytes(end);
				ds.write(parameters.get(key).getBytes());
				ds.writeBytes(end);
			}
			// 写文件
			for (int i = 0; i < uploadFiles.length; i++) {
				String uploadFile = uploadFiles[i];
				String fileName = uploadFile.substring(uploadFile.lastIndexOf(File.separator) + 1);
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; " + "name=\"file" + i + "\"; " + "filename=\"");
				// 防止中文乱码
				ds.write(fileName.getBytes());
				ds.writeBytes("\"" + end);
				ds.writeBytes("Content-Type: " + fileType.getValue() + end);
				ds.writeBytes(end);
				// 读取磁盘文件
				FileInputStream fis = new FileInputStream(uploadFile);
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = fis.read(buffer)) != -1) {
					ds.write(buffer, 0, length);
				}
				ds.writeBytes(end);
				fis.close();
			}
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			ds.writeBytes(end);
			ds.flush();
			try {
				// 获取URL的响应
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String s = "";
				String temp = "";
				while ((temp = reader.readLine()) != null) {
					s += temp;
				}
				response = s;
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("No response get!!!");
			}
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Request failed!");
		}
		return response;
	}
}
