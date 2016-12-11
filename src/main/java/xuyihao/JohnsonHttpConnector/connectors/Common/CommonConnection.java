package xuyihao.JohnsonHttpConnector.connectors.common;

import xuyihao.JohnsonHttpConnector.connectors.common.ssl.HttpsHostNameVerifier;
import xuyihao.JohnsonHttpConnector.connectors.common.ssl.HttpsTrustManager;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

/**
 * <pre>
 * 对
 * java.net.HttpURLConnection
 * 以及javax.net.ssl.HttpsURLConnection
 * 进行简单的封装
 * </pre>
 * 
 * Created by Xuyh on 2016/12/9.
 */
public class CommonConnection {
	/**
	 * 连接类型：HTTP
	 */
	public static final int CONNECTION_TYPE_HTTP = 1;
	/**
	 * 连接类型：HTTPS
	 */
	public static final int CONNECTION_TYPE_HTTPS = 2;

	private int connectionType;

	private HttpURLConnection httpURLConnection;
	private HttpsURLConnection httpsURLConnection;

	/**
	 * 获取连接实例
	 *
	 * @param url
	 * @param connectionType 1:http--2:https
	 */
	public static CommonConnection getInstance(URL url, int connectionType) {
		return new CommonConnection(url, connectionType);
	}

	/**
	 * 创建连接实例
	 * 
	 * @param url
	 * @param connectionType 1:http--2:https
	 */
	public CommonConnection(URL url, int connectionType) {
		try {
			if (connectionType == 1) {
				this.connectionType = 1;
				httpURLConnection = (HttpURLConnection) url.openConnection();
			} else if (connectionType == 2) {
				this.connectionType = 2;
				httpsURLConnection = (HttpsURLConnection) url.openConnection();
				httpsURLConnection.setSSLSocketFactory(HttpsTrustManager.getSSLSocketFactory());
				httpsURLConnection.setHostnameVerifier(new HttpsHostNameVerifier());
			} else {
				this.connectionType = 1;
				httpURLConnection = (HttpURLConnection) url.openConnection();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setRequestMethod(String method) throws ProtocolException {
		if (connectionType == 1) {
			httpURLConnection.setRequestMethod(method);
		} else if (connectionType == 2) {
			httpsURLConnection.setRequestMethod(method);
		} else {
			httpURLConnection.setRequestMethod(method);
		}
	}

	public void setRequestProperty(String key, String value) {
		if (connectionType == 1) {
			httpURLConnection.setRequestProperty(key, value);
		} else if (connectionType == 2) {
			httpsURLConnection.setRequestProperty(key, value);
		} else {
			httpURLConnection.setRequestProperty(key, value);
		}
	}

	public int getContentLength() {
		if (connectionType == 1) {
			return httpURLConnection.getContentLength();
		} else if (connectionType == 2) {
			return httpsURLConnection.getContentLength();
		} else {
			return httpURLConnection.getContentLength();
		}
	}

	public String getHeaderField(String name) {
		if (connectionType == 1) {
			return httpURLConnection.getHeaderField(name);
		} else if (connectionType == 2) {
			return httpsURLConnection.getHeaderField(name);
		} else {
			return httpURLConnection.getHeaderField(name);
		}
	}

	public InputStream getInputStream() throws IOException {
		if (connectionType == 1) {
			return httpURLConnection.getInputStream();
		} else if (connectionType == 2) {
			return httpsURLConnection.getInputStream();
		} else {
			return httpURLConnection.getInputStream();
		}
	}

	public OutputStream getOutputStream() throws IOException {
		if (connectionType == 1) {
			return httpURLConnection.getOutputStream();
		} else if (connectionType == 2) {
			return httpsURLConnection.getOutputStream();
		} else {
			return httpURLConnection.getOutputStream();
		}
	}

	public void setUseCaches(boolean usecaches) {
		if (connectionType == 1) {
			httpURLConnection.setUseCaches(usecaches);
		} else if (connectionType == 2) {
			httpsURLConnection.setUseCaches(usecaches);
		} else {
			httpURLConnection.setUseCaches(usecaches);
		}
	}

	public void setDoOutput(boolean dooutput) {
		if (connectionType == 1) {
			httpURLConnection.setDoOutput(dooutput);
		} else if (connectionType == 2) {
			httpsURLConnection.setDoOutput(dooutput);
		} else {
			httpURLConnection.setDoOutput(dooutput);
		}
	}

	public void setDoInput(boolean doinput) {
		if (connectionType == 1) {
			httpURLConnection.setDoInput(doinput);
		} else if (connectionType == 2) {
			httpsURLConnection.setDoInput(doinput);
		} else {
			httpURLConnection.setDoInput(doinput);
		}
	}

	public void setConnectTimeout(int timeout) {
		if (connectionType == 1) {
			httpURLConnection.setConnectTimeout(timeout);
		} else if (connectionType == 2) {
			httpsURLConnection.setConnectTimeout(timeout);
		} else {
			httpURLConnection.setConnectTimeout(timeout);
		}
	}

	public void disconnect() {
		if (connectionType == 1) {
			httpURLConnection.disconnect();
		} else if (connectionType == 2) {
			httpsURLConnection.disconnect();
		} else {
			httpURLConnection.disconnect();
		}
	}
}
