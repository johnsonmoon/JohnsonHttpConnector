package xuyihao.JohnsonHttpConnector.connectors;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

/**
 * created by xuyihao on 2016/5/20
 * @author johnson
 * @description 网络资源(文件)多线程下载工具类
 * @attention 发送GET请求，接收网络文件
 * @attention 此工具类支持多线程下载
 * @attention 添加会话(session)支持,在一些需要保持会话状态下载文件的情况下,通过HttpUtil获取的sessionID进行sessionID的初始化
 * @attention 需要服务器端发送文件内容长度响应，即响应头部包含文件长度Content-length
 * @attention 如果获取不到文件长度，则download方法会结束并返回false
 * */
public class MultiThreadDownUtil {
	/**
	 * fields
	 * @author johnson
	 * */
	private String sessionID = "";
	private String trueRequestURL = "";
	private int threadNum = 0;
	private long fileSize = 0;
	private DownloadThread[] threads;
	
	/**
	 * constructor
	 * @author johnson
	 * @param actionURL 需要下载资源的URL地址,不跟参数,或者直接将参数写在URL上面
	 * @param threadNumber 需要启动的下载线程数量
	 * */
	public MultiThreadDownUtil(String actionURL, int threadNumber){
		this.trueRequestURL = actionURL;
		this.threadNum = threadNumber;
		this.threads = new DownloadThread[this.threadNum];
	}
	
	/**
	 * constructor
	 * @author johnson
	 * @param actionURL 需要下载资源的URL地址
	 * @param parameters URL后的具体参数，以key=value的形式传递
	 * @param threadNumber 需要启动的下载线程数量
	 * */
	public MultiThreadDownUtil(String actionURL, HashMap<String, String> parameters, int threadNumber){
		this.trueRequestURL = actionURL;
		trueRequestURL += "?";
		Set<String> keys = parameters.keySet();
		for(String key : keys){
			trueRequestURL = trueRequestURL + key + "=" + parameters.get(key) + "&";
		}
		trueRequestURL = trueRequestURL.substring(0, trueRequestURL.lastIndexOf("&"));
		this.threadNum = threadNumber;
		this.threads = new DownloadThread[this.threadNum];
	}
	
	/**
	 * constructor
	 * @author johnson
	 * @param actionURL 需要下载资源的URL地址
	 * @param parameters URL后的具体参数，以key=value的形式传递
	 * @param threadNumber 需要启动的下载线程数量
	 * @param httpUtil 已经获取sessionID的HttpUtil工具类,用来初始化本类的sessionID
	 * */
	public MultiThreadDownUtil(String actionURL, HashMap<String, String> parameters, int threadNumber, HttpUtil httpUtil){
		this.trueRequestURL = actionURL;
		trueRequestURL += "?";
		Set<String> keys = parameters.keySet();
		for(String key : keys){
			trueRequestURL = trueRequestURL + key + "=" + parameters.get(key) + "&";
		}
		trueRequestURL = trueRequestURL.substring(0, trueRequestURL.lastIndexOf("&"));
		this.threadNum = threadNumber;
		this.threads = new DownloadThread[this.threadNum];
		this.sessionID = httpUtil.getSessionID();
	}
	
	/**
	 * @author johnson
	 * @method setSessionID
	 * @param httpUtil 已经获取sessionID的HttpUtil工具类
	 * @description 通过本包的HttoUtil工具类已经获取的sessionID来对本工具sessionID进行初始化的方法
	 * @attention 只能通过传入HttpUtil工具类来进行初始化,避免直接对sessionID字串进行赋值
	 * @attention 适用于一些只能保持会话状态才能下载文件的情况
	 * */
	public void setSessionID(HttpUtil httpUtil){
		this.sessionID = httpUtil.getSessionID();
	}
	
	/**
	 * @author johnson
	 * @method invalidateSessionID
	 * @description 使工具类中的sessionID无效，即删除会话信息
	 * */
	public void invalidateSessionID(){
		this.sessionID = "";
	}

	/**
	 * @method download
	 * @author johnson
	 * @throws Exception
	 * @param targetFilePathName 文件的目标保存完整路径文件名称
	 * @description method which starts the downloading
	 * @description 开始多线程下载
	 * @attention 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * @return boolean true if successfully, false if failed 如果成功,返回true并开始下载,如果失败返回false
	 * */
	public boolean download(String targetFilePathName){
		boolean flag = false;
		try{
			URL url = new URL(trueRequestURL);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(5*1000);
			connection.setRequestMethod("GET");
			//如果存在会话，则写入会话sessionID到cookie里面
			if(!this.sessionID.equals("")){
				connection.setRequestProperty("cookie", this.sessionID);
			}
			connection.setRequestProperty("Accept",	"image/gif, image/jpeg, image/pjpeg, image/pjpeg, " + "application/x-shockwave-flash, application/xaml+xml, " + "application/vnd.ms-xpsdocument, application/x-ms-xbap, " + "application/x-ms-application, application/vnd.ms-excel, " + "application/vnd.ms-powerpoint, application/msword, */*");
			connection.setRequestProperty("Accept-Language", "zh-CN");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Connection", "Keep-Alive");
			//check whether wen can get the exact length by the server, if not, stop the program
			//检查能否获取到准确的文件长度，如果不能则结束程序并报错
			if(connection.getContentLength() == -1){
				flag = false;
			}else{
				this.fileSize = connection.getContentLength();
				connection.disconnect();
				long currentPartSize = this.fileSize / this.threadNum + 1;
				RandomAccessFile file = new RandomAccessFile(targetFilePathName, "rw");
				//set the file size of the local file which would be written
				file.setLength(this.fileSize);
				file.close();
				for(int i = 0; i < this.threadNum; i++){
					//calculate the start position for each thread
					long startPosition = i * currentPartSize;
					//each thread use one RandomAccessFile to download
					RandomAccessFile currentFilePart = new RandomAccessFile(targetFilePathName, "rw");
					//locate the download position for the thread
					currentFilePart.seek(startPosition);
					//create thread
					if(this.sessionID.equals("")){
						threads[i] = new DownloadThread(startPosition, currentPartSize, currentFilePart);
					}else{
						threads[i] = new DownloadThread(startPosition, currentPartSize, currentFilePart, this.sessionID);
					}
					threads[i].start();
				}
				flag = true;
			}
		}catch(IOException e){
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}

	/**
	 * @method downloadToPath
	 * @author johnson
	 * @throws Exception
	 * @param targetPath 文件存放路径,文件名将从服务器响应中获取
	 * @description method which starts the downloading
	 * @description 开始多线程下载
	 * @attention 如果存在会话，本方法可以保持会话，如果要消除会话，请使用invalidateSessionID方法
	 * @attention 如果没有获取服务器响应的文件名,则返回false,结束下载
	 * @return boolean true if successfully, false if failed 如果成功,返回true并开始下载,如果失败返回false
	 * */
	public boolean downloadToPath(String targetPath){
		boolean flag = false;
		try{
			URL url = new URL(trueRequestURL);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(5*1000);
			connection.setRequestMethod("GET");
			//如果存在会话，则写入会话sessionID到cookie里面
			if(!this.sessionID.equals("")){
				connection.setRequestProperty("cookie", this.sessionID);
			}
			connection.setRequestProperty("Accept",	"image/gif, image/jpeg, image/pjpeg, image/pjpeg, " + "application/x-shockwave-flash, application/xaml+xml, " + "application/vnd.ms-xpsdocument, application/x-ms-xbap, " + "application/x-ms-application, application/vnd.ms-excel, " + "application/vnd.ms-powerpoint, application/msword, */*");
			connection.setRequestProperty("Accept-Language", "zh-CN");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Connection", "Keep-Alive");
			//check whether wen can get the exact length by the server, if not, stop the program
			//检查能否获取到准确的文件长度，如果不能则结束程序并报错
			if(connection.getContentLength() == -1){
				flag = false;
			}else{
				//检查是否获取文件名,如果没有获取返回false
				String ContentDisposition = connection.getHeaderField("Content-Disposition");
				if(ContentDisposition == null){
					System.out.println("No file name get from the response header!");
					flag = false;
				}else{
					String fileName = ContentDisposition.substring(ContentDisposition.lastIndexOf("filename=\"") + 10);
					fileName = fileName.substring(0, fileName.indexOf("\""));
					this.fileSize = connection.getContentLength();
					connection.disconnect();
					long currentPartSize = this.fileSize / this.threadNum + 1;
					RandomAccessFile file = new RandomAccessFile(targetPath + fileName, "rw");
					//set the file size of the local file which would be written
					file.setLength(this.fileSize);
					file.close();
					for(int i = 0; i < this.threadNum; i++){
						//calculate the start position for each thread
						long startPosition = i * currentPartSize;
						//each thread use one RandomAccessFile to download
						RandomAccessFile currentFilePart = new RandomAccessFile(targetPath + fileName, "rw");
						//locate the download position for the thread
						currentFilePart.seek(startPosition);
						//create thread
						if(this.sessionID.equals("")){
							threads[i] = new DownloadThread(startPosition, currentPartSize, currentFilePart);
						}else{
							threads[i] = new DownloadThread(startPosition, currentPartSize, currentFilePart, this.sessionID);
						}
						threads[i].start();
					}
					flag = true;
				}
			}
		}catch(IOException e){
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
	
	/**
	 * @author johnson
	 * @method getCompleteRate
	 * @description get the complete percentage of downloading
	 * @description 获取整个下载完成度百分比(double 显示)
	 * @return double percentage
	 * */
	public double getCompleteRate(){
		int sumSize = 0;
		for(int i = 0; i < this.threadNum; i++){
			sumSize += threads[i].length;
		}
		return sumSize * 1.0 / this.fileSize;
	}
	
	/**
	 * @author johnson
	 * @method printCompleteRate
	 * @description print the Complete Rate of Downloading on the screen by console
	 * @attention 默认一秒显示一次
	 * */
	public void printCompleteRate(){
		new CheckThread(this).start();
	}
	
	/**
	 * @author johnson
	 * @method printCompleteRate
	 * @description print the Complete Rate of Downloading on the screen by console
	 * @param showTimeBySeconds time by seconds 每隔多少秒显示一次
	 * */
	public void printCompleteRate(int showTimeBySeconds){
		new CheckThread(this, showTimeBySeconds).start();
	}
	
	/**
	 * @author johnson
	 * @class private class DownloadThread
	 * @description a thread of downloading part of file
	 * */
	private class DownloadThread extends Thread{
		/**
		 * fields
		 * */
		private String sessionID = "";
		//start position for the current thread
		private long startPosition;
		//current file size for the current thread
		private long currentPartSize;
		private RandomAccessFile currentFilePart;
		//the length of byte which is already downloaded
		public int length = 0;
		
		/**
		 * constructor
		 * */
		public DownloadThread(long startPos, long currentPartSize, RandomAccessFile currentPart){
			this.startPosition = startPos;
			this.currentPartSize = currentPartSize;
			this.currentFilePart= currentPart;
		}
		
		/**
		 * constructor2
		 * */
		public DownloadThread(long startPos, long currentPartSize, RandomAccessFile currentPart, String sessionIDh){
			this.sessionID = sessionIDh;
			this.startPosition = startPos;
			this.currentPartSize = currentPartSize;
			this.currentFilePart= currentPart;
		}
		
		@Override
		public void run(){
			try{
				URL url = new URL(trueRequestURL);
				HttpURLConnection connection = (HttpURLConnection)url.openConnection();
				connection.setConnectTimeout(5*1000);
				connection.setRequestMethod("GET");
				//如果存在会话，则写入会话sessionID到cookie里面
				if(!this.sessionID.equals("")){
					connection.setRequestProperty("cookie", this.sessionID);
				}
				connection.setRequestProperty("Accept",	"image/gif, image/jpeg, image/pjpeg, image/pjpeg, " + "application/x-shockwave-flash, application/xaml+xml, " + "application/vnd.ms-xpsdocument, application/x-ms-xbap, " + "application/x-ms-application, application/vnd.ms-excel, " + "application/vnd.ms-powerpoint, application/msword, */*");
				connection.setRequestProperty("Accept-Language", "zh-CN");
				connection.setRequestProperty("Charset", "UTF-8");
				InputStream in = connection.getInputStream();
				in.skip(this.startPosition);
				byte[] b = new byte[1024];
				int hasRead = 0;
				while((length < currentPartSize) && ((hasRead = in.read(b)) != -1)){
					currentFilePart.write(b, 0, hasRead);
					length += hasRead;
				}
				currentFilePart.close();
				in.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @author johnson
	 * @class CheckThread
	 * @description a thread class which checks the complete rate of downloading
	 * */
	private class CheckThread extends Thread{
		/**
		 * fields
		 * */
		private MultiThreadDownUtil down;
		private int showTime = 1000;
		
		/**
		 * constructor
		 * */
		public CheckThread(MultiThreadDownUtil d){
			this.down = d;
		}
		
		/**
		 * constructor2
		 * */
		public CheckThread(MultiThreadDownUtil d, int showTime){
			this.down = d;
			this.showTime = showTime * 1000;
		}
		
		@Override
		public void run(){
			while(true){
				String rate = String.valueOf(down.getCompleteRate() * 100);
				rate = rate.substring(0, rate.indexOf(".") + 2) + "%";
				System.out.println("Downloading....." + rate);
				try{
					Thread.sleep(this.showTime);
				}catch(Exception e){
					e.printStackTrace();
				}
				if(down.getCompleteRate() >= 1.0){
					System.out.println("Downloading.....100.00%");
					System.out.println("Downloadind suceeded!");
					break;
				}
			}
		}
	}
}
