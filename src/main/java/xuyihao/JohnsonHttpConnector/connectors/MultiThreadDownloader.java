package xuyihao.JohnsonHttpConnector.connectors;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

/**
 * 网络资源(文件)多线程下载工具类
 * 
 * <pre>
 * 发送GET请求，接收网络文件
 * 此工具类支持多线程下载
 * 添加会话(session)支持,在一些需要保持会话状态下载文件的情况下,通过HttpUtil获取的sessionID进行sessionID的初始化
 * 需要服务器端发送文件内容长度响应，即响应头部包含文件长度Content-length
 * 如果获取不到文件长度，则download方法会结束并返回false
 * </pre>
 * 
 * @author Xuyh at 2016年9月30日 下午7:10:27.
 *
 */
public class MultiThreadDownloader {
	private String sessionID = "";
	private String trueRequestURL = "";
	private int threadNum = 0;
	private long fileSize = 0;
	private DownloadThread[] threads;

	/**
	 * 
	 * @param actionURL 需要下载资源的URL地址,不跟参数,或者直接将参数写在URL上面
	 * @param threadNumber 需要启动的下载线程数量
	 */
	public MultiThreadDownloader(String actionURL, int threadNumber) {
		this.trueRequestURL = actionURL;
		this.threadNum = threadNumber;
		this.threads = new DownloadThread[this.threadNum];
	}

	/**
	 * 
	 * @param actionURL 需要下载资源的URL地址
	 * @param parameters URL后的具体参数，以key=value的形式传递
	 * @param threadNumber 需要启动的下载线程数量
	 */
	public MultiThreadDownloader(String actionURL, HashMap<String, String> parameters, int threadNumber) {
		this.trueRequestURL = actionURL;
		trueRequestURL += "?";
		Set<String> keys = parameters.keySet();
		for (String key : keys) {
			trueRequestURL = trueRequestURL + key + "=" + parameters.get(key) + "&";
		}
		trueRequestURL = trueRequestURL.substring(0, trueRequestURL.lastIndexOf("&"));
		this.threadNum = threadNumber;
		this.threads = new DownloadThread[this.threadNum];
	}

	/**
	 * 
	 * @param actionURL 需要下载资源的URL地址
	 * @param parameters URL后的具体参数，以key=value的形式传递
	 * @param threadNumber 需要启动的下载线程数量
	 * @param requestSender 已经获取sessionID的HttpUtil工具类,用来初始化本类的sessionID
	 */
	public MultiThreadDownloader(String actionURL, HashMap<String, String> parameters, int threadNumber,
			RequestSender requestSender) {
		this.trueRequestURL = actionURL;
		trueRequestURL += "?";
		Set<String> keys = parameters.keySet();
		for (String key : keys) {
			trueRequestURL = trueRequestURL + key + "=" + parameters.get(key) + "&";
		}
		trueRequestURL = trueRequestURL.substring(0, trueRequestURL.lastIndexOf("&"));
		this.threadNum = threadNumber;
		this.threads = new DownloadThread[this.threadNum];
		this.sessionID = requestSender.getSessionID();
	}

	/**
	 * 通过本包的HttoUtil工具类已经获取的sessionID来对本工具sessionID进行初始化的方法
	 * 
	 * <pre>
	 * 只能通过传入HttpUtil工具类来进行初始化,避免直接对sessionID字串进行赋值
	 * 适用于一些只能保持会话状态才能下载文件的情况
	 * </pre>
	 * 
	 * @param requestSender 已经获取sessionID的HttpUtil工具类
	 */
	public void setSessionID(RequestSender requestSender) {
		this.sessionID = requestSender.getSessionID();
	}

	/**
	 * 使工具类中的sessionID无效，即删除会话信息
	 */
	public void invalidateSessionID() {
		this.sessionID = "";
	}

	/**
	 * <pre>
	 * 开始多线程下载
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * </pre>
	 * 
	 * @param targetFilePathName 文件的目标保存完整路径文件名称
	 * @return 
	 */
	public boolean download(String targetFilePathName) {
		boolean flag = false;
		try {
			URL url = new URL(trueRequestURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5 * 1000);
			connection.setRequestMethod("GET");
			//如果存在会话，则写入会话sessionID到cookie里面
			if (!this.sessionID.equals("")) {
				connection.setRequestProperty("cookie", this.sessionID);
			}
			connection.setRequestProperty("Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
							+ "application/x-shockwave-flash, application/xaml+xml, "
							+ "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
							+ "application/x-ms-application, application/vnd.ms-excel, "
							+ "application/vnd.ms-powerpoint, application/msword, */*");
			connection.setRequestProperty("Accept-Language", "zh-CN");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Connection", "Keep-Alive");
			//check whether wen can get the exact length by the server, if not, stop the program
			//检查能否获取到准确的文件长度，如果不能则结束程序并报错
			if (connection.getContentLength() == -1) {
				flag = false;
			} else {
				this.fileSize = connection.getContentLength();
				connection.disconnect();
				long currentPartSize = this.fileSize / this.threadNum + 1;
				RandomAccessFile file = new RandomAccessFile(targetFilePathName, "rw");
				//set the file size of the local file which would be written
				file.setLength(this.fileSize);
				file.close();
				for (int i = 0; i < this.threadNum; i++) {
					//calculate the start position for each thread
					long startPosition = i * currentPartSize;
					//each thread use one RandomAccessFile to download
					RandomAccessFile currentFilePart = new RandomAccessFile(targetFilePathName, "rw");
					//locate the download position for the thread
					currentFilePart.seek(startPosition);
					//create thread
					if (this.sessionID.equals("")) {
						threads[i] = new DownloadThread(startPosition, currentPartSize, currentFilePart);
					} else {
						threads[i] = new DownloadThread(startPosition, currentPartSize, currentFilePart, this.sessionID);
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
	 * 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * 如果没有获取服务器响应的文件名,则返回false,结束下载
	 * </pre>
	 * 
	 * @param targetPath 文件存放路径,文件名将从服务器响应中获取
	 * @return boolean true if successfully, false if failed 如果成功,返回true并开始下载,如果失败返回false
	 */
	public boolean downloadToPath(String targetPath) {
		boolean flag = false;
		try {
			URL url = new URL(trueRequestURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5 * 1000);
			connection.setRequestMethod("GET");
			//如果存在会话，则写入会话sessionID到cookie里面
			if (!this.sessionID.equals("")) {
				connection.setRequestProperty("cookie", this.sessionID);
			}
			connection.setRequestProperty("Accept",
					"image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
							+ "application/x-shockwave-flash, application/xaml+xml, "
							+ "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
							+ "application/x-ms-application, application/vnd.ms-excel, "
							+ "application/vnd.ms-powerpoint, application/msword, */*");
			connection.setRequestProperty("Accept-Language", "zh-CN");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Connection", "Keep-Alive");
			//check whether wen can get the exact length by the server, if not, stop the program
			//检查能否获取到准确的文件长度，如果不能则结束程序并报错
			if (connection.getContentLength() == -1) {
				flag = false;
			} else {
				//检查是否获取文件名,如果没有获取返回false
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
					RandomAccessFile file = new RandomAccessFile(targetPath + fileName, "rw");
					//set the file size of the local file which would be written
					file.setLength(this.fileSize);
					file.close();
					for (int i = 0; i < this.threadNum; i++) {
						//calculate the start position for each thread
						long startPosition = i * currentPartSize;
						//each thread use one RandomAccessFile to download
						RandomAccessFile currentFilePart = new RandomAccessFile(targetPath + fileName, "rw");
						//locate the download position for the thread
						currentFilePart.seek(startPosition);
						//create thread
						if (this.sessionID.equals("")) {
							threads[i] = new DownloadThread(startPosition, currentPartSize, currentFilePart);
						} else {
							threads[i] = new DownloadThread(startPosition, currentPartSize, currentFilePart, this.sessionID);
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
	 * get the complete percentage of downloading
	 * 获取整个下载完成度百分比(double 显示)
	 * </pre>
	 * 
	 * @return
	 */
	public double getCompleteRate() {
		int sumSize = 0;
		for (int i = 0; i < this.threadNum; i++) {
			sumSize += threads[i].length;
		}
		return sumSize * 1.0 / this.fileSize;
	}

	/**
	 * 
	 * <pre>
	 * print the Complete Rate of Downloading on the screen by console
	 * 默认一秒显示一次
	 * </pre>
	 */
	public void printCompleteRate() {
		new CheckThread(this).start();
	}

	/**
	 * 
	 * <pre>
	 * print the Complete Rate of Downloading on the screen by console
	 * showTimeBySeconds time by seconds 每隔多少秒显示一次
	 * </pre>
	 * 
	 * @param showTimeBySeconds
	 */
	public void printCompleteRate(int showTimeBySeconds) {
		new CheckThread(this, showTimeBySeconds).start();
	}

	/**
	 * 
	 * @author Johnson
	 *
	 */
	private class DownloadThread extends Thread {
		private String sessionID = "";
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

		public DownloadThread(long startPos, long currentPartSize, RandomAccessFile currentPart, String sessionIDh) {
			this.sessionID = sessionIDh;
			this.startPosition = startPos;
			this.currentPartSize = currentPartSize;
			this.currentFilePart = currentPart;
		}

		@Override
		public void run() {
			try {
				URL url = new URL(trueRequestURL);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(5 * 1000);
				connection.setRequestMethod("GET");
				//如果存在会话，则写入会话sessionID到cookie里面
				if (!this.sessionID.equals("")) {
					connection.setRequestProperty("cookie", this.sessionID);
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

	/**
	 * a thread class which checks the complete rate of downloading
	 * 
	 * @author Johnson
	 *
	 */
	private class CheckThread extends Thread {
		private MultiThreadDownloader down;
		private int showTime = 1000;

		public CheckThread(MultiThreadDownloader d) {
			this.down = d;
		}

		public CheckThread(MultiThreadDownloader d, int showTime) {
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
				if (down.getCompleteRate() >= 1.0) {
					System.out.println("Downloading.....100.00%");
					System.out.println("Downloadind suceeded!");
					break;
				}
			}
		}
	}
}
