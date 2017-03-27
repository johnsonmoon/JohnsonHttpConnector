package xuyihao.http.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 
 * create by Xuyh at 2016年10月2日 下午1:03:24.
 *
 */
public class CommonUtils {
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

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
