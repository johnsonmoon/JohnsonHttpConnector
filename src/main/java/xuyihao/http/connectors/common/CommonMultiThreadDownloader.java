package xuyihao.http.connectors.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import xuyihao.http.entity.Cookie;

/**
 * 网络资源(文件)多线程下载工具类
 *
 * <pre>
 * 发送GET请求，接收网络文件
 * 此工具类支持多线程下载
 * 添加cookie支持
 * 需要服务器端发送文件内容长度响应，即响应头部包含文件长度Content-length
 * 如果获取不到文件长度，则download方法会结束并返回false
 * </pre>
 * 
 * Created by Xuyh on 2016/12/9.
 */
public abstract class CommonMultiThreadDownloader {
	private int connectionType;
	/**
	 * cookie的配置逻辑： 每次请求发送时候都会在请求头带上cookie信息(如果cookie为null则不带上),
	 * 然后从响应头中获取新的cookie值刷新当前值,可以起到保存同服务器的会话的作用
	 */
	private Cookie cookie = null;
	private String trueRequestURL = "";
	private int threadNum = 0;
	private long fileSize = 0;
	private CommonMultiThreadDownloader.DownloadThread[] threads;

	/**
	 * 需要重写的方法
	 * <pre>
	 *     用法：设置连接类型
	 *     调用setConnectionType()方法
	 *     参数值
	 *     CommonConnection.CONNECTION_TYPE_HTTP
	 *     或
	 *     CommonConnection.CONNECTION_TYPE_HTTPS
	 * </pre>
	 *
	 */
	protected abstract void bindConnectionType();

	protected void setConnectionType(int connectionType) {
		this.connectionType = connectionType;
	}

	/**
	 *
	 * @param actionURL 需要下载资源的URL地址,不跟参数,或者直接将参数写在URL上面
	 * @param threadNumber 需要启动的下载线程数量
	 */
	public CommonMultiThreadDownloader(String actionURL, int threadNumber) {
		bindConnectionType();
		this.trueRequestURL = actionURL;
		this.threadNum = threadNumber;
		this.threads = new CommonMultiThreadDownloader.DownloadThread[this.threadNum];
	}

	/**
	 *
	 * @param actionURL 需要下载资源的URL地址
	 * @param parameters URL后的具体参数，以key=value的形式传递
	 * @param threadNumber 需要启动的下载线程数量
	 */
	public CommonMultiThreadDownloader(String actionURL, HashMap<String, String> parameters, int threadNumber) {
		bindConnectionType();
		this.trueRequestURL = actionURL;
		trueRequestURL += "?";
		Set<String> keys = parameters.keySet();
		for (String key : keys) {
			trueRequestURL = trueRequestURL + key + "=" + parameters.get(key) + "&";
		}
		trueRequestURL = trueRequestURL.substring(0, trueRequestURL.lastIndexOf("&"));
		this.threadNum = threadNumber;
		this.threads = new CommonMultiThreadDownloader.DownloadThread[this.threadNum];
	}

	/**
	 *
	 * @param actionURL 需要下载资源的URL地址
	 * @param parameters URL后的具体参数，以key=value的形式传递
	 * @param threadNumber 需要启动的下载线程数量
	 * @param cookie 保持会话信息的cookie
	 */
	public CommonMultiThreadDownloader(String actionURL, HashMap<String, String> parameters, int threadNumber,
			Cookie cookie) {
		bindConnectionType();
		this.trueRequestURL = actionURL;
		trueRequestURL += "?";
		Set<String> keys = parameters.keySet();
		for (String key : keys) {
			trueRequestURL = trueRequestURL + key + "=" + parameters.get(key) + "&";
		}
		trueRequestURL = trueRequestURL.substring(0, trueRequestURL.lastIndexOf("&"));
		this.threadNum = threadNumber;
		this.threads = new CommonMultiThreadDownloader.DownloadThread[this.threadNum];
		this.cookie = cookie;
	}

	public Cookie getCookie() {
		return cookie;
	}

	public void setCookie(Cookie cookie) {
		this.cookie = cookie;
	}

	/**
	 * 使cookie无效，即删除会话信息
	 */
	public void invalidateCookie() {
		this.cookie = null;
	}

	/**
	 * <pre>
	 * 开始多线程下载
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateCookie方法
	 * </pre>
	 *
	 * @param targetFilePathName 文件的目标保存完整路径文件名称
	 * @return 如果获取不到文件大小，返回false
	 */
	public boolean download(String targetFilePathName) {
		boolean flag = false;
		try {
			URL url = new URL(trueRequestURL);
			CommonConnection connection = CommonConnection.getInstance(url, connectionType);
			connection.setConnectTimeout(5 * 1000);
			connection.setRequestMethod("GET");
			if (this.cookie != null) {
				connection.setRequestProperty("cookie", this.cookie.convertCookieToCookieValueString());
			}
			connection.setRequestProperty("Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg, " + "application/x-shockwave-flash, application/xaml+xml, "
							+ "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
							+ "application/x-ms-application, application/vnd.ms-excel, "
							+ "application/vnd.ms-powerpoint, application/msword, */*");
			connection.setRequestProperty("Accept-Language", "zh-CN");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Connection", "Keep-Alive");
			String set_cookie = connection.getHeaderField("Set-Cookie");
			if (set_cookie != null && !set_cookie.equals("")) {
				this.cookie = Cookie.newCookieInstance(set_cookie);
			}
			// 检查能否获取到准确的文件长度，如果不能则结束程序并报错
			if (connection.getContentLength() == -1) {
				flag = false;
			} else {
				this.fileSize = connection.getContentLength();
				connection.disconnect();
				long currentPartSize = this.fileSize / this.threadNum + 1;
				RandomAccessFile file = new RandomAccessFile(targetFilePathName, "rw");
				// set the file size of the local file which would be written
				file.setLength(this.fileSize);
				file.close();
				for (int i = 0; i < this.threadNum; i++) {
					// calculate the start position for each thread
					long startPosition = i * currentPartSize;
					// each thread use one RandomAccessFile to download
					RandomAccessFile currentFilePart = new RandomAccessFile(targetFilePathName, "rw");
					// locate the download position for the thread
					currentFilePart.seek(startPosition);
					// create thread
					if (this.cookie == null) {
						threads[i] = new CommonMultiThreadDownloader.DownloadThread(startPosition, currentPartSize,
								currentFilePart);
					} else {
						threads[i] = new CommonMultiThreadDownloader.DownloadThread(startPosition, currentPartSize, currentFilePart,
								this.cookie);
					}
					threads[i].start();
				}
				flag = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 *
	 * <pre>
	 * 开始多线程下载
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateCookie方法
	 * 如果没有获取服务器响应的文件名,则返回false,结束下载
	 * </pre>
	 *
	 * @param targetPath 文件存放路径,文件名将从服务器响应中获取
	 * @return boolean true if successfully, false if failed
	 *         如果成功,返回true并开始下载,如果失败返回false
	 *         如果获取不到文件大小，返回false
	 */
	public boolean downloadToPath(String targetPath) {
		boolean flag = false;
		try {
			URL url = new URL(trueRequestURL);
			CommonConnection connection = CommonConnection.getInstance(url, connectionType);
			connection.setConnectTimeout(5 * 1000);
			connection.setRequestMethod("GET");
			if (this.cookie != null) {
				connection.setRequestProperty("cookie", this.cookie.convertCookieToCookieValueString());
			}
			connection.setRequestProperty("Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg, " + "application/x-shockwave-flash, application/xaml+xml, "
							+ "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
							+ "application/x-ms-application, application/vnd.ms-excel, "
							+ "application/vnd.ms-powerpoint, application/msword, */*");
			connection.setRequestProperty("Accept-Language", "zh-CN");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Connection", "Keep-Alive");
			String set_cookie = connection.getHeaderField("Set-Cookie");
			if (set_cookie != null && !set_cookie.equals("")) {
				this.cookie = Cookie.newCookieInstance(set_cookie);
			}
			// 检查能否获取到准确的文件长度，如果不能则结束程序并报错
			if (connection.getContentLength() == -1) {
				flag = false;
			} else {
				// 检查是否获取文件名,如果没有获取返回false
				String ContentDisposition = connection.getHeaderField("Content-Disposition");
				if (ContentDisposition == null) {
					System.out.println("No file name get from the response header!");
					flag = false;
				} else {
					String fileName = ContentDisposition.substring(ContentDisposition.lastIndexOf("filename=\"") + 10);
					fileName = fileName.substring(0, fileName.indexOf("\""));
					this.fileSize = connection.getContentLength();
					connection.disconnect();
					long currentPartSize = this.fileSize / this.threadNum + 1;
					RandomAccessFile file = new RandomAccessFile(targetPath + File.separator + fileName, "rw");
					// set the file size of the local file which would be written
					file.setLength(this.fileSize);
					file.close();
					for (int i = 0; i < this.threadNum; i++) {
						// calculate the start position for each thread
						long startPosition = i * currentPartSize;
						// each thread use one RandomAccessFile to download
						RandomAccessFile currentFilePart = new RandomAccessFile(targetPath + fileName, "rw");
						// locate the download position for the thread
						currentFilePart.seek(startPosition);
						// create thread
						if (this.cookie == null) {
							threads[i] = new CommonMultiThreadDownloader.DownloadThread(startPosition, currentPartSize,
									currentFilePart);
						} else {
							threads[i] = new CommonMultiThreadDownloader.DownloadThread(startPosition, currentPartSize,
									currentFilePart, this.cookie);
						}
						threads[i].start();
					}
					flag = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 *
	 * <pre>
	 * 		get the complete percentage of downloading
	 * 		获取整个下载完成度百分比(double 显示)
	 * 		如果下载线程为空，则返回-1.0	
	 * </pre>
	 *
	 * @return
	 */
	public double getCompleteRate() {
		for (CommonMultiThreadDownloader.DownloadThread thread : threads) {
			if (thread == null) {
				return -1.0;
			}
		}
		int sumSize = 0;
		for (int i = 0; i < this.threadNum; i++) {
			sumSize += threads[i].length;
		}
		return sumSize * 1.0 / this.fileSize;
	}

	private class DownloadThread extends Thread {
		private Cookie cookie = null;
		/**
		 * start position for the current thread
		 *
		 */
		private long startPosition;
		/**
		 * current file size for the current thread
		 *
		 */
		private long currentPartSize;
		private RandomAccessFile currentFilePart;
		/**
		 * the length of byte which is already downloaded
		 *
		 */
		public int length = 0;

		public DownloadThread(long startPos, long currentPartSize, RandomAccessFile currentPart) {
			this.startPosition = startPos;
			this.currentPartSize = currentPartSize;
			this.currentFilePart = currentPart;
		}

		public DownloadThread(long startPos, long currentPartSize, RandomAccessFile currentPart, Cookie cookie) {
			this.cookie = cookie;
			this.startPosition = startPos;
			this.currentPartSize = currentPartSize;
			this.currentFilePart = currentPart;
		}

		@Override
		public void run() {
			try {
				URL url = new URL(trueRequestURL);
				CommonConnection connection = CommonConnection.getInstance(url, connectionType);
				connection.setConnectTimeout(5 * 1000);
				connection.setRequestMethod("GET");
				if (this.cookie != null) {
					connection.setRequestProperty("cookie", this.cookie.convertCookieToCookieValueString());
				}
				connection.setRequestProperty("Accept",
						"image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
								+ "application/x-shockwave-flash, application/xaml+xml, "
								+ "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
								+ "application/x-ms-application, application/vnd.ms-excel, "
								+ "application/vnd.ms-powerpoint, application/msword, */*");
				connection.setRequestProperty("Accept-Language", "zh-CN");
				connection.setRequestProperty("Charset", "UTF-8");
				InputStream in = connection.getInputStream();
				in.skip(this.startPosition);
				byte[] b = new byte[1024];
				int hasRead = 0;
				while ((length < currentPartSize) && ((hasRead = in.read(b)) != -1)) {
					currentFilePart.write(b, 0, hasRead);
					length += hasRead;
				}
				currentFilePart.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
