package xuyihao.http.connectors.http;

import java.util.HashMap;

import junit.framework.TestCase;
import xuyihao.http.common.utils.CommonUtils;
import xuyihao.http.connectors.http.HttpDownloader;
import xuyihao.http.connectors.http.HttpRequestSender;
import xuyihao.http.enums.MIME_FileType;

public class HttpDownloaderTest extends TestCase {

	public static String actionURL1 = "http://115.28.192.61:8088/rongyi/accounts";
	private HttpRequestSender requestSender = HttpRequestSender.getInstance();
	private HttpDownloader downloader = HttpDownloader.getInstance();

	public void test() {
		HashMap<String, String> parameters2 = new HashMap<String, String>();
		parameters2.put("action", "login");
		parameters2.put("Acc_name", "Johnson");
		parameters2.put("Acc_pwd", "123456");
		CommonUtils.output(requestSender.executePostByUsual(actionURL1, parameters2));

		CommonUtils.output(requestSender.getCookie().convertCookieToCookieValueString());

		CommonUtils.output("输入Acc_ID");
		String Acc_ID = CommonUtils.input();

		HashMap<String, String> parameters4 = new HashMap<String, String>();
		parameters4.put("action", "changeHeadPhoto");
		parameters4.put("Acc_ID", Acc_ID);
		CommonUtils.output(requestSender.singleFileUploadWithParameters(actionURL1,
				"C:\\Users\\Administrator\\Desktop\\minion.jpg", "file", MIME_FileType.Image_jpg, parameters4));

		CommonUtils.output("输入Acc_ID: ");
		String Acc_ID1 = CommonUtils.input();
		CommonUtils.output(requestSender.executeGet(actionURL1 + "?action=getHeadPhotoId&Acc_ID=" + Acc_ID1));

		downloader.setCookie(requestSender.getCookie());

		CommonUtils.output("输入Photo_ID: ");
		String Photo_ID = CommonUtils.input();
		downloader.downloadByGet("C:\\Users\\Administrator\\Desktop\\download.jpg",
				actionURL1 + "?action=getPhotoById&Photo_ID=" + Photo_ID);
	}
}
