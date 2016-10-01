package xuyihao.JohnsonHttpConnector.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * create by Xuyh at 2016年10月1日 下午11:16:08.
 *
 */
public class CommonTestingCase {
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) {
		String url = input();
		/*
				Map<String, List<String>> fields = getConnectionHeaderFields(url);
				for (String key : fields.keySet()) {
					String out = key + " : ";
					for (String value : fields.get(key)) {
						out += (value + " || ");
					}
					out = out.substring(0, out.length() - 5);
					output(out);
				}
		*/
		String cookie = getCookie(url);
		if (cookie == null || cookie.equals("")) {
			output("yes");
		}
		output(cookie);

		/*
		String test = "test=kkl";
		test = test.substring(test.indexOf("=") + 1);
		output(test);
		*/
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

	/**
	 * 控制台输入字串
	 * 
	 * @return
	 */
	public static String input() {
		String result = "";
		try {
			result = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 控制台输出
	 * 
	 * @param output
	 */
	public static void output(String output) {
		System.out.println(output);
	}
}
