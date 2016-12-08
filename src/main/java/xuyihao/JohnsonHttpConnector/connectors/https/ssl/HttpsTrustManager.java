package xuyihao.JohnsonHttpConnector.connectors.https.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 自定义实现信任管理器
 * <pre>
 *     管理信任的数字证书
 *     如果实现方法不修改，则默认信任所有HTTPS站点的数字证书
 * </pre>
 *
 * Created by Xuyh on 2016/12/8.
 */
public class HttpsTrustManager implements X509TrustManager {
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

	}

	public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

	}

	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

	public static SSLSocketFactory getSSLSocketFactory() {
		SSLSocketFactory sslSocketFactory = null;
		try {
			TrustManager[] trustManagers = { new HttpsTrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, trustManagers, new SecureRandom());
			sslSocketFactory = sslContext.getSocketFactory();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e2) {
			e2.printStackTrace();
		}
		return sslSocketFactory;
	}
}
