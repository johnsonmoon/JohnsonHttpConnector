package xuyihao.JohnsonHttpConnector.connectors.https.ssl;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * <pre>
 *     重写HostNameVerifier的verify方法
 *     返回true代表任何host都返回true
 * </pre>
 *
 * Created by Xuyh on 2016/12/8.
 */
public class HttpsHostNameVerifier implements HostnameVerifier {
	public boolean verify(String s, SSLSession sslSession) {
		return true;
	}
}
