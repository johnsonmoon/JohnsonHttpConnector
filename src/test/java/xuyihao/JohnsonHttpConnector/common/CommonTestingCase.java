package xuyihao.JohnsonHttpConnector.common;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xuyihao.JohnsonHttpConnector.common.utils.CommonUtils;
import xuyihao.JohnsonHttpConnector.connectors.http.HttpDownloader;
import xuyihao.JohnsonHttpConnector.connectors.http.HttpMultiThreadDownloader;
import xuyihao.JohnsonHttpConnector.connectors.http.HttpRequestSender;
import xuyihao.JohnsonHttpConnector.enums.MIME_FileType;
import xuyihao.JohnsonHttpConnector.util.RandomUtils;

/**
 * 
 * 
 * create by Xuyh at 2016年10月1日 下午11:16:08.
 *
 */
public class CommonTestingCase {
	public static void main(String[] args) {
		String url = CommonUtils.input();

		Map<String, List<String>> fields = getConnectionHeaderFields(url);
		for (String key : fields.keySet()) {
			String out = key + " : ";
			for (String value : fields.get(key)) {
				out += (value + " || ");
			}
			out = out.substring(0, out.length() - 5);
			CommonUtils.output(out);
		}

		String cookie = getCookie(url);
		if (cookie == null || cookie.equals("")) {
			CommonUtils.output("yes");
		}
		CommonUtils.output(cookie);

		String test = "test=kkl";
		test = test.substring(test.indexOf("=") + 1);
		CommonUtils.output(test);
		testDownload();
		testDownloadByMultiThread();
		testUploadFile();
		testMkDir();
		testSendingRequestToRongYi();
	}

	public static void testSendingRequestToRongYi() {
		HttpRequestSender sender = new HttpRequestSender();
		String actionURL = "http://127.0.0.1:8095/rongyi/accounts";
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("action", "register");
		parameters.put("Acc_name", RandomUtils.getRandomString(12));
		parameters.put("Acc_pwd", "123456");
		parameters.put("Acc_sex", "男");
		parameters.put("Acc_loc", "浙江工业大学");
		CommonUtils.output(sender.executePostByUsual(actionURL, parameters));
		String Acc_ID = CommonUtils.input();
		HashMap<String, String> parameters2 = new HashMap<String, String>();
		parameters2.put("action", "addHeadPhoto");
		parameters2.put("Acc_ID", Acc_ID);
		CommonUtils.output(sender.singleFileUploadWithParameters(actionURL, "C:\\Users\\Johnson\\Pictures\\发送\\3.jpg",
				"file", MIME_FileType.Image_jpg, parameters2));

		String actionURL2 = "http://127.0.0.1:8095/rongyi/courses";
		HashMap<String, String> parameters3 = new HashMap<String, String>();
		parameters3.put("action", "addCrs");
		parameters3.put("Crs_name", RandomUtils.getRandomString(14));
		sender.singleFileUploadWithParameters(actionURL2, "E:\\1.mp4", "file", MIME_FileType.Video_mp4, parameters3);
	}

	public static void testMkDir() {
		String path = "C:\\Users\\Johnson\\Desktop";
		String absolutePath = path + File.separator + "LMMNHDMKK" + File.separator + "photo" + File.separator + "vedioPhoto"
				+ File.separator + "vmn";
		File file = new File(absolutePath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static void testUploadFile() {
		String actionURL = "http://115.28.192.61:8088/rongyi/accounts";
		HttpRequestSender sender = new HttpRequestSender();
		CommonUtils.output(sender.executeGet(actionURL + "?action=register&Acc_name=" + RandomUtils.getRandomString(5)
				+ "Acc_pwd=jjnma&Acc_sex=M&Acc_loc=kmn"));
		CommonUtils.output("Input Acc_ID: ");
		String Acc_ID = CommonUtils.input();
		CommonUtils.output(sender.singleFileUpload(actionURL + "?action=addHeadPhoto&Acc_ID=" + Acc_ID,
				"C:\\Users\\Johnson\\Desktop\\minion.jpeg", "file", MIME_FileType.Image_jpeg));
	}

	public static void testDownload() {
		String actionURL = "http://115.28.192.61:8088/rongyi/courses";
		HttpRequestSender sender = new HttpRequestSender();
		CommonUtils.output(sender.executeGet(actionURL + "?action=getCachedCoursesList"));
		HttpDownloader downloader = new HttpDownloader(sender.getCookie());
		CommonUtils.output("Input Vedio_ID: ");
		String Vedio_ID = CommonUtils.input();
		downloader.downloadByGet("C:\\Users\\Johnson\\Desktop\\kkmlml.mp4",
				actionURL + "?action=getVedioById&Vedio_ID=" + Vedio_ID);
	}

	public static void testDownloadByMultiThread() {
		String actionURL = "http://115.28.192.61:8088/rongyi/courses";
		HttpRequestSender sender = new HttpRequestSender();
		CommonUtils.output(sender.executeGet(actionURL + "?action=getCachedCoursesList"));
		CommonUtils.output("Input Vedio_ID: ");
		String Vedio_ID = CommonUtils.input();
		HttpMultiThreadDownloader downloader = new HttpMultiThreadDownloader(
				actionURL + "?action=getVedioById&Vedio_ID=" + Vedio_ID, 5);
		downloader.setCookie(sender.getCookie());
		downloader.download("C:\\Users\\Johnson\\Desktop\\kkmadjioajfoial.mp4");
	}

	/**
	 * 测试获取所有响应头部内容
	 * 
	 * @param actionURL
	 * @return
	 */
	public static Map<String, List<String>> getConnectionHeaderFields(String actionURL) {
		Map<String, List<String>> headerFields;
		try {
			URL url = new URL(actionURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			headerFields = connection.getHeaderFields();
		} catch (IOException e) {
			e.printStackTrace();
			headerFields = null;
		}
		return headerFields;
	}

	/**
	 * 获取cookie信息
	 * 
	 * @param actionURL
	 * @return
	 */
	public static String getCookie(String actionURL) {
		String cookieValue = "";
		try {
			URL url = new URL(actionURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			cookieValue = connection.getHeaderField("Set-Cookie");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cookieValue;
	}
}
