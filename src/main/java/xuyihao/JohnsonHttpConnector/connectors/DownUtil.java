package xuyihao.JohnsonHttpConnector.connectors;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

/**
 * 网络资源(文件)下载工具类
 * 
 * <pre>
 *  (1)发送GET POST请求，接收网络文件
 *  (2)此工具类不支持多线程下载，IO阻塞线程
 *  (3)由于下载时候的IO阻碍主线程,所以需要使用 
 *  	getCompleteRate printCompleteRate 
 *  	等方法时候需要先调用这几个方法,再开启下载方法
 *  	并且需要注意的是, 调用getCompleteRate printCompleteRate
 *  	等方法之前需要先调用initializeStates方法初始化状态变量
 *  (4)如果需要新建线程并使用 getCompleteRate 
 *  	方法查看进度, 也需要在调用下载的方法之前创建并start()
 *  (5)添加会话(session)支持,在一些需要保持会话状态下载文件的情况下,
 *  	通过HttpUtil获取的sessionID进行sessionID的初始化
 *  (6)添加查看进度支持,需要调用 getCompleteRate 方法
 * </pre>
 * 
 * @Author Xuyh created at 2016年9月30日 下午4:34:39
 */
public class DownUtil {
	private String sessionID = "";
	private long fileTotalLength = 0;
	private long fileReceiveLength = 0;
	/**
	 * 判断是否能够获取服务器响应的文件长度,初始化为不能即false
	 */
	private boolean ableToCaculate = false;
	/**
	 * 判断下载是否已经成功完成,初始化为没有完成即false
	 */
	private boolean downloadComplete = false;
	/**
	 * 判断下载是否失败, 初始化为不失败即false
	 */
	private boolean ifDownloadFailed = false;

	public DownUtil() {
	}

	/**
	 * 
	 * @param httpUtil 已经获取sessionID的HttpUtil工具类,用来初始化本类的sessionID
	 */
	public DownUtil(HttpUtil httpUtil) {
		this.sessionID = httpUtil.getSessionID();
	}

	/**
	 * <pre>
	 * 通过本包的HttoUtil工具类已经获取的sessionID来对本工具sessionID进行初始化的方法
	 * 只能通过传入HttpUtil工具类来进行初始化,避免直接对sessionID字串进行赋值
	 * 适用于一些只能保持会话状态才能下载文件的情况
	 * </pre>
	 * 
	 * @param httpUtil httpUtil 已经获取sessionID的HttpUtil工具类
	 */
	public void setSessionID(HttpUtil httpUtil) {
		this.sessionID = httpUtil.getSessionID();
	}

	/**
	 * 使工具类中的sessionID无效，即删除会话信息
	 */
	public void invalidateSessionID() {
		this.sessionID = "";
	}

	/**
	 * 执行Get请求下载文件的方法
	 * 
	 * <pre>
	 * 直接通过参数actionURL发送请求,用户也可以通过自己设置actionURL后的参数发送请求
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param actionURL 发送get请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @return byte[] 返回一个储存文件内容的字节数组
	 */
	public byte[] downloadByGet(String actionURL) {
		this.fileTotalLength = 0L;
		this.fileReceiveLength = 0L;
		this.ableToCaculate = false;
		this.downloadComplete = false;
		this.ifDownloadFailed = false;
		byte[] data = new byte[0];
		try {
			String trueRequestURL = actionURL;
			URL url = new URL(trueRequestURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// 如果存在会话，则写入会话sessionID到cookie里面
			if (!this.sessionID.equals("")) {
				connection.setRequestProperty("cookie", this.sessionID);
			}
			// get the length of the file, if get, set ableToCaculate true
			long getLength = connection.getContentLength();
			if (getLength == -1) {
				this.ableToCaculate = false;
				this.fileTotalLength = 0L;
			} else {
				this.ableToCaculate = true;
				this.fileTotalLength = getLength;
			}
			try {
				// 获取URL的响应
				InputStream in = connection.getInputStream();
				byte[] b = new byte[1];
				while (in.read(b) != -1) {
					data = this.connectTwoByteArrays(data, b);
					// receive 1 byte content
					this.fileReceiveLength = this.fileReceiveLength + 1;
				}
				in.close();
				this.downloadComplete = true;
			} catch (IOException e) {
				e.printStackTrace();
				this.ifDownloadFailed = true;
				System.out.println("No response get!!!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			this.ifDownloadFailed = true;
			System.out.println("Request failed!");
		}
		return data;
	}

	/**
	 * 执行Get请求下载文件的方法
	 * 
	 * <pre>
	 * 最后发送的URL格式为(例如:http://www.johnson.cc:8080/Test/download?file=file1&name=XXX&pwd=aaa)
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param actionURL 发送get请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @param parameters 发送get请求URL后跟着的具体参数,以HashMap<String, String>形式传入key=value值
	 * @return byte[] 返回一个储存文件内容的字节数组
	 */
	public byte[] downloadByGet(String actionURL, HashMap<String, String> parameters) {
		this.fileTotalLength = 0L;
		this.fileReceiveLength = 0L;
		this.ableToCaculate = false;
		this.downloadComplete = false;
		this.ifDownloadFailed = false;
		byte[] data = new byte[0];
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
			// get the length of the file, if get, set ableToCaculate true
			long getLength = connection.getContentLength();
			if (getLength == -1) {
				this.ableToCaculate = false;
				this.fileTotalLength = 0L;
			} else {
				this.ableToCaculate = true;
				this.fileTotalLength = getLength;
			}
			try {
				// 获取URL的响应
				InputStream in = connection.getInputStream();
				byte[] b = new byte[1];
				while (in.read(b) != -1) {
					data = this.connectTwoByteArrays(data, b);
					// receive 1 byte content
					this.fileReceiveLength = this.fileReceiveLength + 1;
				}
				in.close();
				this.downloadComplete = true;
			} catch (IOException e) {
				e.printStackTrace();
				this.ifDownloadFailed = true;
				System.out.println("No response get!!!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			this.ifDownloadFailed = true;
			System.out.println("Request failed!");
		}
		return data;
	}

	/**
	 * 执行Get请求下载文件的方法
	 * 
	 * <pre>
	 * 直接通过参数actionURL发送请求,用户也可以通过自己设置actionURL后的参数发送请求
	 * 最后文件会以savePathName的路径名形式存储到磁盘中
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param savePathName 文件在磁盘中的储存路径&文件名,文件路径+名称需要自己定义
	 * @param actionURL 发送get请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @return 返回true如果接收文件成功
	 */
	public boolean downloadByGet(String savePathName, String actionURL) {
		this.fileTotalLength = 0L;
		this.fileReceiveLength = 0L;
		this.ableToCaculate = false;
		this.downloadComplete = false;
		this.ifDownloadFailed = false;
		boolean flag = false;
		try {
			String trueRequestURL = actionURL;
			URL url = new URL(trueRequestURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// 如果存在会话，则写入会话sessionID到cookie里面
			if (!this.sessionID.equals("")) {
				connection.setRequestProperty("cookie", this.sessionID);
			}
			// get the length of the file, if get, set ableToCaculate true
			long getLength = connection.getContentLength();
			if (getLength == -1) {
				this.ableToCaculate = false;
				this.fileTotalLength = 0L;
			} else {
				this.ableToCaculate = true;
				this.fileTotalLength = getLength;
			}
			try {
				// 获取URL的响应
				InputStream in = connection.getInputStream();
				File file = new File(savePathName);
				FileOutputStream out = new FileOutputStream(file);
				byte[] b = new byte[1024];
				int length = 0;
				while ((length = in.read(b)) != -1) {
					out.write(b, 0, length);
					// received length bytes
					this.fileReceiveLength = this.fileReceiveLength + length;
				}
				in.close();
				out.close();
				flag = true;
				this.downloadComplete = true;
			} catch (IOException e) {
				e.printStackTrace();
				flag = false;
				this.ifDownloadFailed = true;
				System.out.println("No response get!!!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
			this.ifDownloadFailed = true;
			System.out.println("Request failed!");
		}
		return flag;
	}

	/**
	 * 执行Get请求下载文件的方法
	 * 
	 * <pre>
	 * 最后发送的URL格式为(例如:http://www.johnson.cc:8080/Test/download?file=file1&name=XXX&pwd=aaa)
	 * 最后文件会以savePathName的路径名形式存储到磁盘中
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param savePathName 文件在磁盘中的储存路径&文件名,文件路径+名称需要自己定义
	 * @param actionURL actionURL
	 *          发送get请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @param parameters 发送get请求URL后跟着的具体参数,以HashMap<String, String>形式传入key=value值
	 * @return 返回true如果接收文件成功
	 */
	public boolean downloadByGet(String savePathName, String actionURL, HashMap<String, String> parameters) {
		this.fileTotalLength = 0L;
		this.fileReceiveLength = 0L;
		this.ableToCaculate = false;
		this.downloadComplete = false;
		this.ifDownloadFailed = false;
		boolean flag = false;
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
			// get the length of the file, if get, set ableToCaculate true
			long getLength = connection.getContentLength();
			if (getLength == -1) {
				this.ableToCaculate = false;
				this.fileTotalLength = 0L;
			} else {
				this.ableToCaculate = true;
				this.fileTotalLength = getLength;
			}
			try {
				// 获取URL的响应
				InputStream in = connection.getInputStream();
				File file = new File(savePathName);
				FileOutputStream out = new FileOutputStream(file);
				byte[] b = new byte[1024];
				int length = 0;
				while ((length = in.read(b)) != -1) {
					out.write(b, 0, length);
					// received length bytes
					this.fileReceiveLength = this.fileReceiveLength + length;
				}
				in.close();
				out.close();
				flag = true;
				this.downloadComplete = true;
			} catch (IOException e) {
				e.printStackTrace();
				flag = false;
				this.ifDownloadFailed = true;
				System.out.println("No response get!!!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
			this.ifDownloadFailed = true;
			System.out.println("Request failed!");
		}
		return flag;
	}

	/**
	 * 执行Get请求下载文件的方法
	 * 
	 * <pre>
	 * 直接通过参数actionURL发送请求,用户也可以通过自己设置actionURL后的参数发送请求
	 * 最后文件会存储到savePath路径中,路径需要以参数方式传入,文件名通过服务器获得
	 * 如果没有获取服务器响应传回的文件名,则返回false
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param savePath 文件在磁盘中的储存路径,文件名会从服务器获得
	 * @param actionURL 发送get请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @return 返回true如果接收文件成功
	 */
	public boolean downloadByGetSaveToPath(String savePath, String actionURL) {
		this.fileTotalLength = 0L;
		this.fileReceiveLength = 0L;
		this.ableToCaculate = false;
		this.downloadComplete = false;
		this.ifDownloadFailed = false;
		boolean flag = false;
		try {
			String trueRequestURL = actionURL;
			URL url = new URL(trueRequestURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// 如果存在会话，则写入会话sessionID到cookie里面
			if (!this.sessionID.equals("")) {
				connection.setRequestProperty("cookie", this.sessionID);
			}
			// get the length of the file, if get, set ableToCaculate true
			long getLength = connection.getContentLength();
			if (getLength == -1) {
				this.ableToCaculate = false;
				this.fileTotalLength = 0L;
			} else {
				this.ableToCaculate = true;
				this.fileTotalLength = getLength;
			}
			String ContentDisposition = connection.getHeaderField("Content-Disposition");
			if (ContentDisposition == null) {
				System.out.println("No file name get from the response header!");
				flag = false;
			} else {
				String fileName = ContentDisposition.substring(ContentDisposition.lastIndexOf("filename=\"") + 10);
				fileName = fileName.substring(0, fileName.indexOf("\""));
				try {
					// 获取URL的响应
					InputStream in = connection.getInputStream();
					File file = new File(savePath + fileName);
					FileOutputStream out = new FileOutputStream(file);
					byte[] b = new byte[1024];
					int length = 0;
					while ((length = in.read(b)) != -1) {
						out.write(b, 0, length);
						// received length bytes
						this.fileReceiveLength = this.fileReceiveLength + length;
					}
					in.close();
					out.close();
					flag = true;
					this.downloadComplete = true;
				} catch (IOException e) {
					e.printStackTrace();
					flag = false;
					this.ifDownloadFailed = true;
					System.out.println("No response get!!!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
			this.ifDownloadFailed = true;
			System.out.println("Request failed!");
		}
		return flag;
	}

	/**
	 * 执行Get请求下载文件的方法
	 * 
	 * <pre>
	 * 最后发送的URL格式为(例如:http://www.johnson.cc:8080/Test/download?file=file1&name=XXX&pwd=aaa)
	 * 最后文件会存储到savePath路径中,路径需要以参数方式传入,文件名通过服务器获得
	 * 如果没有获取服务器响应传回的文件名,则返回false
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param savePath 文件在磁盘中的储存路径,文件名会从服务器获得
	 * @param actionURL 发送get请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @param parameters 发送get请求URL后跟着的具体参数,以HashMap<String, String>形式传入key=value值
	 * @return 返回true如果接收文件成功
	 */
	public boolean downloadByGetSaveToPath(String savePath, String actionURL, HashMap<String, String> parameters) {
		this.fileTotalLength = 0L;
		this.fileReceiveLength = 0L;
		this.ableToCaculate = false;
		this.downloadComplete = false;
		this.ifDownloadFailed = false;
		boolean flag = false;
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
			// get the length of the file, if get, set ableToCaculate true
			long getLength = connection.getContentLength();
			if (getLength == -1) {
				this.ableToCaculate = false;
				this.fileTotalLength = 0L;
			} else {
				this.ableToCaculate = true;
				this.fileTotalLength = getLength;
			}
			String ContentDisposition = connection.getHeaderField("Content-Disposition");
			if (ContentDisposition == null) {
				System.out.println("No file name get from the response header!");
				flag = false;
			} else {
				String fileName = ContentDisposition.substring(ContentDisposition.lastIndexOf("filename=\"") + 10);
				fileName = fileName.substring(0, fileName.indexOf("\""));
				try {
					// 获取URL的响应
					InputStream in = connection.getInputStream();
					File file = new File(savePath + fileName);
					FileOutputStream out = new FileOutputStream(file);
					byte[] b = new byte[1024];
					int length = 0;
					while ((length = in.read(b)) != -1) {
						out.write(b, 0, length);
						// received length bytes
						this.fileReceiveLength = this.fileReceiveLength + length;
					}
					in.close();
					out.close();
					flag = true;
					this.downloadComplete = true;
				} catch (IOException e) {
					e.printStackTrace();
					flag = false;
					this.ifDownloadFailed = true;
					System.out.println("No response get!!!");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
			this.ifDownloadFailed = true;
			System.out.println("Request failed!");
		}
		return flag;
	}

	/**
	 * 执行发送post请求的方法
	 * 
	 * <pre>
	 * 请求内容在HTTP报文内容中
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param actionURL 发送get请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @param parameters 发送get请求数据段中的参数,以HashMap<String, String>形式传入key=value值
	 * @return byte[] 返回一个储存文件内容的字节数组
	 */
	public byte[] downloadByPost(String actionURL, HashMap<String, String> parameters) {
		this.fileTotalLength = 0L;
		this.fileReceiveLength = 0L;
		this.ableToCaculate = false;
		this.downloadComplete = false;
		this.ifDownloadFailed = false;
		byte[] data = new byte[0];
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
			// 防止中文乱码,使用String.getBytes()来获取字节数组
			ds.write(requestContent.getBytes());
			ds.flush();
			// get the length of the file, if get, set ableToCaculate true
			long getLength = connection.getContentLength();
			if (getLength == -1) {
				this.ableToCaculate = false;
				this.fileTotalLength = 0L;
			} else {
				this.ableToCaculate = true;
				this.fileTotalLength = getLength;
			}
			try {
				// 获取URL的响应
				// 获取URL的响应
				InputStream in = connection.getInputStream();
				byte[] b = new byte[1];
				while (in.read(b) != -1) {
					data = this.connectTwoByteArrays(data, b);
					// receive 1 byte content
					this.fileReceiveLength = this.fileReceiveLength + 1;
				}
				in.close();
				this.downloadComplete = true;
			} catch (IOException e) {
				e.printStackTrace();
				this.ifDownloadFailed = true;
				System.out.println("No response get!!!");
			}
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			this.ifDownloadFailed = true;
			System.out.println("Request failed!");
		}
		return data;
	}

	/**
	 * 执行发送post请求的方法
	 * 
	 * <pre>
	 * 最后发送的URL格式为(例如:http://www.johnson.cc:8080/Test/download?file=file1&name=XXX&pwd=aaa)
	 * 最后文件会以savePathName的路径名形式存储到磁盘中
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param savePathName 文件在磁盘中的储存路径&文件名,文件路径+名称需要自己定义
	 * @param actionURL 发送get请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @param parameters 发送get请求数据段中的参数,以HashMap<String, String>形式传入key=value值
	 * @return 返回true如果接收文件成功
	 */
	public boolean downloadByPost(String savePathName, String actionURL, HashMap<String, String> parameters) {
		this.fileTotalLength = 0L;
		this.fileReceiveLength = 0L;
		this.ableToCaculate = false;
		this.downloadComplete = false;
		this.ifDownloadFailed = false;
		boolean flag = false;
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
			// 防止中文乱码,使用String.getBytes()来获取字节数组
			ds.write(requestContent.getBytes());
			ds.flush();
			// get the length of the file, if get, set ableToCaculate true
			long getLength = connection.getContentLength();
			if (getLength == -1) {
				this.ableToCaculate = false;
				this.fileTotalLength = 0L;
			} else {
				this.ableToCaculate = true;
				this.fileTotalLength = getLength;
			}
			try {
				// 获取URL的响应
				InputStream in = connection.getInputStream();
				File file = new File(savePathName);
				FileOutputStream out = new FileOutputStream(file);
				byte[] b = new byte[1024];
				int length = 0;
				while ((length = in.read(b)) != -1) {
					out.write(b, 0, length);
					// received length bytes
					this.fileReceiveLength = this.fileReceiveLength + length;
				}
				in.close();
				out.close();
				flag = true;
				this.downloadComplete = true;
			} catch (IOException e) {
				e.printStackTrace();
				flag = false;
				this.ifDownloadFailed = true;
				System.out.println("No response get!!!");
			}
			ds.close();
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
			this.ifDownloadFailed = true;
			System.out.println("Request failed!");
		}
		return flag;
	}

	/**
	 * 执行发送post请求的方法
	 * 
	 * <pre>
	 * 最后发送的URL格式为(例如:http://www.johnson.cc:8080/Test/download?file=file1&name=XXX&pwd=aaa)
	 * 最后文件会存储到savePath路径中,路径需要以参数方式传入,文件名通过服务器获得
	 * 如果没有获取服务器响应传回的文件名,则返回false
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param savePath 文件在磁盘中的储存路径,文件名会从服务器获得
	 * @param actionURL 发送get请求的URL地址(例如：http://www.johnson.cc:8080/Test/download)
	 * @param parameters 发送get请求数据段中的参数,以HashMap<String, String>形式传入key=value值
	 * @return 返回true如果接收文件成功
	 */
	public boolean downloadByPostSaveToPath(String savePath, String actionURL, HashMap<String, String> parameters) {
		this.fileTotalLength = 0L;
		this.fileReceiveLength = 0L;
		this.ableToCaculate = false;
		this.downloadComplete = false;
		this.ifDownloadFailed = false;
		boolean flag = false;
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
			// 防止中文乱码,使用String.getBytes()来获取字节数组
			ds.write(requestContent.getBytes());
			ds.flush();
			// get the length of the file, if get, set ableToCaculate true
			long getLength = connection.getContentLength();
			if (getLength == -1) {
				this.ableToCaculate = false;
				this.fileTotalLength = 0L;
			} else {
				this.ableToCaculate = true;
				this.fileTotalLength = getLength;
			}
			String ContentDisposition = connection.getHeaderField("Content-Disposition");
			if (ContentDisposition == null) {
				System.out.println("No file name get from the response header!");
				flag = false;
			} else {
				String fileName = ContentDisposition.substring(ContentDisposition.lastIndexOf("filename=\"") + 10);
				fileName = fileName.substring(0, fileName.indexOf("\""));
				try {
					// 获取URL的响应
					InputStream in = connection.getInputStream();
					File file = new File(savePath + fileName);
					FileOutputStream out = new FileOutputStream(file);
					byte[] b = new byte[1024];
					int length = 0;
					while ((length = in.read(b)) != -1) {
						out.write(b, 0, length);
						// received length bytes
						this.fileReceiveLength = this.fileReceiveLength + length;
					}
					in.close();
					out.close();
					flag = true;
					this.downloadComplete = true;
				} catch (IOException e) {
					e.printStackTrace();
					flag = false;
					this.ifDownloadFailed = true;
					System.out.println("No response get!!!");
				}
				ds.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
			this.ifDownloadFailed = true;
			System.out.println("Request failed!");
		}
		return flag;
	}

	/**
	 * 连接两个byte数组的方法
	 * 
	 * @param front 前面的数组
	 * @param behind 后面的数组
	 * @return byte[] 返回一个新数组
	 */
	public byte[] connectTwoByteArrays(byte[] front, byte[] behind) {
		byte[] total = new byte[front.length + behind.length];
		System.arraycopy(front, 0, total, 0, front.length);
		System.arraycopy(behind, 0, total, front.length, behind.length);
		return total;
	}

	/**
	 * 用来初始化一些控制判断变量的方法,比如 ableToCaculate， downloadComplete等变量
	 * 
	 * <pre>
	 * 该方法在 getCompleteRate 方法之前调用，用来初始化getCompleteRate需要用到的变量值
	 * 使getCompleteRate方法能够正常的发挥作用, 特别是开启一个线程调用getCompleteRate方法时,
	 * 需要在线程体之外调用本方法
	 * </pre>
	 */
	public void initializeStates() {
		this.ableToCaculate = false;
		this.downloadComplete = false;
		this.ifDownloadFailed = false;
	}

	/**
	 * 下载完成度百分比(double 显示)
	 * 
	 * <pre>
	 * 如果需要开启线程并在该线程内调用此方法,则应该先调用initializeStates方法将变量初始化再新建线程
	 * 开启新线程调用此方法需要在下载方法之前,因为下载方法线程IO阻塞
	 * 如果获取不到服务器响应的文件大小,则返回0.01并且下载完成之后返回1.01
	 * 如果下载失败则返回-1.0表示下载失败了
	 * </pre>
	 * 
	 * @return double percentage if returns -1.0 means download failed
	 */
	public double getCompleteRate() {
		double flag = 0.0;
		if (this.ifDownloadFailed) {// 如果下载失败
			flag = -1.0D;
		} else {
			if (!this.ableToCaculate) {// 如果获取不到文件长度
				if (this.downloadComplete) {// 获取不到文件长度并且完成下载
					flag = 1.01;
				} else {
					flag = 0.01;
				}
			} else {
				flag = this.fileReceiveLength * 1.0 / this.fileTotalLength;
			}
		}
		return flag;
	}

	/**
	 * print the Complete Rate of Downloading on the screen by console
	 * 
	 * <pre>
	 * 默认一秒显示一次
	 * </pre>
	 */
	public void printCompleteRate() {
		new CheckThread(this).start();
	}

	/**
	 * print the Complete Rate of Downloading on the screen by console
	 * 
	 * @param showTimeBySeconds 每隔多少秒显示一次
	 */
	public void printCompleteRate(int showTimeBySeconds) {
		new CheckThread(this, showTimeBySeconds).start();
	}

	/**
	 * a thread class which checks the complete rate of downloading
	 * 
	 * @Author Xuyh created at 2016年9月30日 下午5:01:33
	 */
	private class CheckThread extends Thread {
		private DownUtil down;
		private int showTime = 1000;

		public CheckThread(DownUtil d) {
			this.down = d;
		}

		public CheckThread(DownUtil d, int showTime) {
			this.down = d;
			this.showTime = showTime * 1000;
		}

		@Override
		public void run() {
			while (true) {
				String rate = String.valueOf(down.getCompleteRate() * 100);
				rate = rate.substring(0, rate.indexOf(".") + 2) + "%";
				System.out.println("Downloading....." + rate);
				try {
					Thread.sleep(this.showTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (down.getCompleteRate() == -1.0) {
					System.out.println("Downloading.....00.00%");
					System.out.println("Downloadind failed!");
					break;
				}
				if (down.getCompleteRate() >= 1.0) {
					System.out.println("Downloading.....100.00%");
					System.out.println("Downloadind suceeded!");
					break;
				}
			}
		}
	}
}
