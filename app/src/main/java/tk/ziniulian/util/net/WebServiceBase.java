package tk.ziniulian.util.net;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * 基础的 WebService 访问
 * Created by 李泽荣 on 2018/7/23.
 */

public class WebServiceBase {
	private String url;		// 服务地址
	private String npc;		// 命名空间
	private String midp = "";	// 中间路径

	public WebServiceBase (String u, String n, String m) {
		this.url = u;
		this.npc = n;
		this.midp = m;
	}

	public WebServiceBase setUrl(String url) {
		this.url = url;
		return this;
	}

	public String qry (String mnam) {
		return qry(mnam, null, null);
	}

	public String qry (String mnam, String[] ks, Object[] vs) {
		String r = null;

		SoapObject req = new SoapObject(npc, mnam);
		if (vs != null) {
			for (int i = 0; i < ks.length; i ++) {
				req.addProperty(ks[i], vs[i]);
			}
		}

		SoapSerializationEnvelope msg = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		msg.bodyOut = req;
		msg.dotNet = true;
		HttpTransportSE ht = new HttpTransportSE(url);
		String soapAction = npc + midp + mnam;
		try {
			ht.call(soapAction, msg);
			SoapPrimitive res = (SoapPrimitive) msg.getResponse();
			// Object res = msg.getResponse();
			if (res != null) {
				r = res.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return r;
	}
}
