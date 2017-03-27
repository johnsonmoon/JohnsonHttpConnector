package xuyihao.http.connectors.util;

import java.io.*;
import java.net.ProtocolException;
import java.util.Map;
import java.util.Set;

import xuyihao.http.connectors.common.Connection;
import xuyihao.http.enums.MIME_FileType;
import xuyihao.http.util.RandomUtils;

/**
 * 请求数据（POST）公用工具类
 *
 * Created by Xuyh on 2016/12/8.
 */
public class DataUtils {
	/**
	 * 回车符
	 */
	public static final String end = "\r\n";
	/**
	 * 两道杠符
	 */
	public static final String twoHyphens = "--";
	/**
	 * 随机生成的POST表单数据分隔符
	 */
	public static final String boundary = "----------------------" + RandomUtils.getRandomString(18);

	/**
	 * 设置连接基本参数(application/json)
	 * 
	 * @param connection
	 */
	public static void setPostConnectionPropertiesByJSON(Connection connection) {
		try {
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "application/json");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置连接基本参数(x-www-form-urlencoded)
	 * 
	 * @param connection
	 */
	public static void setPostConnectionPropertiesByURLEncoded(Connection connection) {
		try {
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置连接基本参数(multipart/form-data)
	 *
	 * @param connection
	 */
	public static void setPostConnectionPropertiesByMultiPart(Connection connection) {
		try {
			// 发送post请求需要下面两行
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// 设置请求参数
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + DataUtils.boundary);
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析响应流数据成字串
	 * 
	 * @param inputStream
	 * @return
	 */
	public static String resolveResponse(InputStream inputStream) {
		String response;
		try {
			// 获取URL的响应
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
			String s = "";
			String temp = "";
			while ((temp = reader.readLine()) != null) {
				s += temp;
			}
			response = s;
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			response = "";
		}
		return response;
	}

	/**
	 * 流写入一般POST表单数据
	 * 
	 * @param parameters
	 * @param ds
	 */
	public static void addPostCommonData(Map<String, String> parameters, DataOutputStream ds) {
		try {
			Set<String> keys = parameters.keySet();
			for (String key : keys) {
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; name=\"");
				ds.write(key.getBytes());
				ds.writeBytes("\"" + end);
				ds.writeBytes(end);
				ds.write(parameters.get(key).getBytes());
				ds.writeBytes(end);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 流写入POST表单文件
	 * 
	 * @param uploadFile
	 * @param fileName
	 * @param formFileName
	 * @param fileType
	 * @param ds
	 */
	public static void addPostSingleFileData(String uploadFile, String fileName, String formFileName,
			MIME_FileType fileType, DataOutputStream ds) {
		try {
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + formFileName + "\"; " + "filename=\"");
			// 防止中文乱码
			ds.write(fileName.getBytes());
			ds.writeBytes("\"" + end);
			ds.writeBytes("Content-Type: " + fileType.getValue() + end);
			ds.writeBytes(end);
			// 根据路径读取文件
			FileInputStream fis = new FileInputStream(uploadFile);
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = fis.read(buffer)) != -1) {
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 流写入POST表单多个文件
	 * 
	 * @param uploadFiles
	 * @param formFileNames
	 * @param ds
	 * @param fileType
	 */
	public static void addPostMultiFileData(String[] uploadFiles, String[] formFileNames, DataOutputStream ds,
			MIME_FileType fileType) {
		try {
			// 添加post数据
			for (int i = 0; i < uploadFiles.length; i++) {
				String uploadFile = uploadFiles[i];
				String fileName = uploadFile.substring(uploadFile.lastIndexOf(File.separator) + 1);
				ds.writeBytes(twoHyphens + boundary + end);
				ds.writeBytes("Content-Disposition: form-data; " + "name=\"" + formFileNames[i] + "\"; " + "filename=\"");
				// 防止中文乱码
				ds.write(fileName.getBytes());
				ds.writeBytes("\"" + end);
				ds.writeBytes("Content-Type: " + fileType.getValue() + end);
				ds.writeBytes(end);
				FileInputStream fis = new FileInputStream(uploadFile);
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = fis.read(buffer)) != -1) {
					ds.write(buffer, 0, length);
				}
				ds.writeBytes(end);
				fis.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 流写入POST表单数据末尾（必需）
	 * 
	 * @param ds
	 */
	public static void addPostEndData(DataOutputStream ds) {
		try {
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
			ds.writeBytes(end);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
