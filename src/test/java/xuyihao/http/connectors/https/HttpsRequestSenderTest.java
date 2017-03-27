package xuyihao.http.connectors.https;

import junit.framework.TestCase;
import xuyihao.http.common.utils.CommonUtils;
import xuyihao.http.connectors.https.HttpsRequestSender;

/**
 * Created by Xuyh on 2016/12/8.
 */
public class HttpsRequestSenderTest extends TestCase {
	private HttpsRequestSender sender = new HttpsRequestSender();

    public void test() {
        String response = sender.executeGet("https://192.168.192.128:9044/ibm/console/logon.jsp");
        CommonUtils.output(response);
	}
}
