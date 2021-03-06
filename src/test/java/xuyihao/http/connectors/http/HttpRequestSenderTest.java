package xuyihao.http.connectors.http;

import java.util.HashMap;

import junit.framework.TestCase;
import xuyihao.http.common.utils.CommonUtils;
import xuyihao.http.connectors.http.HttpRequestSender;
import xuyihao.http.enums.MIME_FileType;
import xuyihao.http.util.RandomUtils;

public class HttpRequestSenderTest extends TestCase {
	HttpRequestSender requestSender = HttpRequestSender.getInstance();
	String actionURL = "http://115.28.192.61:8088/rongyi/accounts";

	public void test() {
		HashMap<String, String> parameters = new HashMap<String, String>();
		parameters.put("action", "register");
		parameters.put("Acc_name", RandomUtils.getRandomString(12));
		parameters.put("Acc_pwd", "123456");
		parameters.put("Acc_sex", "男");
		parameters.put("Acc_loc", "浙江工业大学屏峰校区");
		CommonUtils.output(requestSender.executeGet(actionURL, parameters));

		CommonUtils.output(requestSender.executeGet(actionURL + "?action=logout"));

		CommonUtils.output(requestSender.getCookie().convertCookieToCookieValueString());
		requestSender.invalidateCookie();
		CommonUtils.output(requestSender.getCookie() == null ? "null" : "not null");

		HashMap<String, String> parameters2 = new HashMap<String, String>();
		parameters2.put("action", "login");
		parameters2.put("Acc_name", "Johnson");
		parameters2.put("Acc_pwd", "123456");
		CommonUtils.output(requestSender.executePostByUsual(actionURL, parameters2));

		CommonUtils.output(requestSender.getCookie().convertCookieToCookieValueString());

		CommonUtils.output("输入Acc_ID");
		String Acc_ID = CommonUtils.input();

		HashMap<String, String> parameters4 = new HashMap<String, String>();
		parameters4.put("action", "addHeadPhoto");
		parameters4.put("Acc_ID", Acc_ID);
		CommonUtils.output(requestSender.singleFileUploadWithParameters(actionURL,
				"C:\\Users\\Administrator\\Desktop\\minion.jpg", "file", MIME_FileType.Image_jpg, parameters4));

	}
}
