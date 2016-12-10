package xuyihao.JohnsonHttpConnector.connectors.http;

import java.util.HashMap;

import junit.framework.TestCase;
import sun.awt.windows.ThemeReader;
import xuyihao.JohnsonHttpConnector.common.utils.CommonUtils;
import xuyihao.JohnsonHttpConnector.connectors.https.HttpsMultiThreadDownloader;

public class HttpMultiThreadDownloaderTest extends TestCase {
	public static String actionURL1 = "http://115.28.192.61:8088/rongyi/accounts";
	private HttpRequestSender requestSender = new HttpRequestSender();

	public void testDownloadToPath() {
		/*
		HashMap<String, String> parameters2 = new HashMap<String, String>();
		parameters2.put("action", "login");
		parameters2.put("Acc_name", "Johnson");
		parameters2.put("Acc_pwd", "123456");
		CommonUtils.output(requestSender.executePostByUsual(actionURL1, parameters2));
		
		CommonUtils.output("输入Acc_ID: ");
		String Acc_ID = CommonUtils.input();
		CommonUtils.output(requestSender.executeGet(actionURL1 + "?action=getHeadPhotoId&Acc_ID=" + Acc_ID));
		
		CommonUtils.output("输入Photo_ID: ");
		String Photo_ID = CommonUtils.input();
		
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("action", "getPhotoById");
		parameters.put("Photo_ID", Photo_ID);
		HttpMultiThreadDownloader downloader = new HttpMultiThreadDownloader(actionURL1, parameters, 2, requestSender.getCookie());
		downloader.download("C:\\Users\\Administrator\\Desktop\\download333.jpg");
		*/
		String actionUrl = "http://115.28.192.61:8088/rongyi/courses?action=getVedioById&Vedio_ID=ufjnk11s5q9t7ojVedio";
		HttpMultiThreadDownloader downloader = new HttpMultiThreadDownloader(actionUrl, 3);
		downloader.downloadToPath("C:\\Users\\Administrator\\Desktop");
		double rate = 0.0;
		while (rate >= 0.0 && rate <= 1.0) {
			try {
				rate = downloader.getCompleteRate();
				String output = String.valueOf(rate  * 100);
				output = output.substring(0, output.indexOf(".") + 2) + "%";
				CommonUtils.output(output);
				Thread.sleep(5000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
