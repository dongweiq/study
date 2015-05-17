package com.foxit.general.pdf_drm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.util.Log;

import com.foxit.general.BufferFileRead;
import com.foxit.general.DrmNative;
import com.foxit.general.ObjectRef;
import com.foxit.general.PdfBaseDef;
import com.foxit.general.PdfDocNative;
import com.foxit.general.PdfDrmNative;
import com.foxit.general.PdfSecurityNative;
import com.foxit.general.RsaKey;
import com.foxit.general.RtBaseDef;
import com.foxit.general.RtNative;

public class PDF_Foac {

	protected ObjectRef	m_encryptParams = null;
	private CookieStore m_cookieStore = null;
	private byte[] m_decoderPubKey = null;
	
	//µ•“≥ FoxitDRM.pdf
//	private final String m_userName = "yali_he@foxitsoftware.com";
//	private final String m_password = "heyali";
//	private final String m_devSN = "heyali";
	
	//∂‡“≥ Foxitdrm_online.pdf
//	private final String m_userName = "lipan_fang@foxitsoftware.com";
//	private final String m_password = "123123";
//	private final String m_devSN = "123123";
	
	//9yue
//	private final String m_userName = "tangbenrong@winshare.com.cn";
//	private final String m_password = "123456";
//	private final String m_devSN = "123456";
	
	//9yue
	private final String m_userName = "enno@vip.qq.com";
	private final String m_password = "222222";
	private final String m_devSN = "222222";
	
	private String m_host = null;
	private String m_port = null;
	private String m_object = null;
	private ObjectRef security = null;
	private RsaKey m_rsaKey = null;
	private String drmfile = null;
	private DefaultHttpClient httpClient = null;//new DefaultHttpClient();
	private String m_sessionID = null;
	
	public void setDRMFileName(String filename)
	{
		drmfile = filename;
	}
	protected boolean isFoxitDRM(ObjectRef document)
	{
		if (!PdfDrmNative.isDocWrapper(document)) return false;
		
		m_encryptParams = PdfDrmNative.getEncryptParams(document);

		if (m_encryptParams == null) return false;
		
		return true;
	}
	
	protected String getServiceURL()
	{
		return PdfDrmNative.getEncryptParamsItemString(m_encryptParams, PdfBaseDef.ENCRYPTPARAMS_SERVICEURL);
	}
	
	protected void parseURL(String serviceUrl)
	{
		if(serviceUrl == null) return;
		int hostIndex = serviceUrl.indexOf("://");
		String tmp = serviceUrl.substring(hostIndex + 3);
		int objIndex = tmp.indexOf("/");
		int portIndex = tmp.indexOf(":");
		if (portIndex < 0)
		{
			m_port = "80";
			m_host = tmp.substring(0, objIndex);
		}
		else
		{
			m_port = tmp.substring(portIndex + 1, 4);
			m_host = tmp.substring(0, portIndex);
		}
		m_object = tmp.substring(objIndex + 1);
	}
	
	
	protected String getSessionBeginRequest(String sessionID)
	{
		ObjectRef foac = DrmNative.createFoac(true);
		if (foac == null) return null;
		
		DrmNative.setFoacSessionID(foac, sessionID);
		
		ObjectRef category = DrmNative.getFoacDataCategory(foac);
		if (category == null)
		{
			DrmNative.deleteFoac(foac);
			return null;
		}
		
		DrmNative.setFoacRequestID(foac, "SessionBegin");
		
		ObjectRef subCategory = DrmNative.addSubCategory(category, "FlowCode", true);
		String flowCode = PdfDrmNative.getEncryptParamsItemString(m_encryptParams, PdfBaseDef.ENCRYPTPARAMS_FLOWCODE);
		DrmNative.setCategoryAttribute(subCategory, "Value", flowCode);
		
		String request = DrmNative.saveFoac(foac);
		DrmNative.deleteFoac(foac);
		return "XmlContent=" + request;
	}
	
	protected String sendAndReceive(String bsSend)
	{
		String result = null;
		try
		{
			httpClient = new DefaultHttpClient();
			String temp = bsSend.replace("&", "%26");
			temp = temp.replace("+", "%2B");
			InetAddress addr = InetAddress.getByName(m_host);
			
			String url;
			if (addr.toString().substring(1).indexOf("/") != -1)
				url = "http://" + m_host + ":" + m_port + "/" + m_object;
			else
				url = "http://" + m_host + ":" + m_port + "/" + m_object;
			HttpPost httpPost = new HttpPost(url);
			HttpEntity entity = new StringEntity(temp);
			httpPost.setEntity(entity);
			
			httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

		//	if (m_cookieStore != null)
				httpClient.setCookieStore(m_cookieStore);
			
			httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 300000);
			httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 300000);
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			int status = httpResponse.getStatusLine().getStatusCode();
			long len = httpResponse.getEntity().getContentLength();
			
			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			for (String s = reader.readLine(); s != null; s = reader.readLine())
			{
				builder.append(s);
			}
			result = builder.toString();
			m_cookieStore = ((AbstractHttpClient)httpClient).getCookieStore();

			if (result.indexOf("xml") == -1)
				return null;
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}
	
	protected void parseSessionBeginRecieve(String receive)
	{
		ObjectRef foac = DrmNative.loadFoac(receive.getBytes());
		if (foac == null) return;
		
		String state = DrmNative.getFoacAnswerState(foac);
		if (state.indexOf("1") > -1)
		{
			ObjectRef category = DrmNative.getFoacDataCategory(foac);
						
			//Pubkey
			int iCount = DrmNative.countSubCategories(category, "ServerPubKey");
			if (iCount == 0)
			{
				DrmNative.deleteFoac(foac);
				return;
			}
			ObjectRef subCategory = DrmNative.getSubCategory(category, "ServerPubKey", 0);
			String bsPubKey = DrmNative.getCategoryAttributeValue(subCategory, "Value");
			
			ObjectRef sessionID =DrmNative.getSubCategory(category, "SessionID", 0);
			m_sessionID = DrmNative.getCategoryAttributeValue(sessionID, "Value");
			
		//	m_decoderPubKey = Base64.decode(bsPubKey, Base64.DEFAULT);
			m_decoderPubKey = RtNative.base64Decode(bsPubKey.getBytes(), 0, bsPubKey.length());		
		}
		DrmNative.deleteFoac(foac);
	}
	
	protected String EncryptString(String str)
	{
		byte[] data = DrmNative.pkiRsaEncrypt(str, m_decoderPubKey);
		//return Base64.encodeToString(data, Base64.DEFAULT);
		return RtNative.base64EncodeToString(data, 0, data.length);
	}
	
	protected String GetCheckAccountRequest(String sessionID)
	{
		String fileID = PdfDrmNative.getEncryptParamsItemString(m_encryptParams, PdfBaseDef.ENCRYPTPARAMS_FILEID);
		String strFileID = EncryptString(fileID);
		
		String order = PdfDrmNative.getEncryptParamsItemString(m_encryptParams, PdfBaseDef.ENCRYPTPARAMS_ORDER);
		String strOrder = EncryptString(order);
		
		String account = EncryptString(m_userName);
		String password = EncryptString(m_password);
		
		ObjectRef foac = DrmNative.createFoac(true);
		DrmNative.setFoacSessionID(foac, sessionID);
		
		DrmNative.setFoacRequestID(foac, "AuthAccount");
		
		ObjectRef category = DrmNative.getFoacDataCategory(foac);
		if (category == null)
		{
			DrmNative.deleteFoac(foac);
			return null;
		}
		
	
		ObjectRef subCategory = DrmNative.addSubCategory(category, "EncryptAccount", true);
		DrmNative.setCategoryAttribute(subCategory, "Value", account);
		
		subCategory = DrmNative.addSubCategory(category, "EncryptPassword", true);
		DrmNative.setCategoryAttribute(subCategory, "Value", password);
		
		subCategory =DrmNative.addSubCategory(category, "EncryptOrderID", true);
		DrmNative.setCategoryAttribute(subCategory, "Value", strOrder);
		
		subCategory =DrmNative.addSubCategory(category, "EncryptFileID", true);
		DrmNative.setCategoryAttribute(subCategory, "Value", strFileID);
		
		String request = DrmNative.saveFoac(foac);
		
		DrmNative.deleteFoac(foac);
		
		return "XmlContent=" + request;
	}
	
	protected boolean ParseCheckAccountRequest(String receive)
	{
		ObjectRef foac = DrmNative.loadFoac(receive.getBytes());
		if (foac == null) return false;
		
		boolean bRet = false;
		
		///foac verify
		String bsState = DrmNative.getFoacAnswerState(foac);
		if (bsState.equals("1"))
		{
			ObjectRef category = DrmNative.getFoacDataCategory(foac);
			
			int iCount = DrmNative.countSubCategories(category, "Result");
			if (iCount == 0)
			{
				DrmNative.deleteFoac(foac);
				return false;
			}
			ObjectRef subCategory = DrmNative.getSubCategory(category, "Result", 0);
			String result = DrmNative.getCategoryAttributeValue(subCategory, "Value");
			bRet = result.equals("1");
		}
		DrmNative.deleteFoac(foac);
		return bRet;
	}
	
	public String GetEnvelopRequest(String sessionID)
	{
		String fileID = PdfDrmNative.getEncryptParamsItemString(m_encryptParams, PdfBaseDef.ENCRYPTPARAMS_FILEID);
		String strFileID = EncryptString(fileID);
		
		String order = PdfDrmNative.getEncryptParamsItemString(m_encryptParams, PdfBaseDef.ENCRYPTPARAMS_ORDER);
		String strOrder = order;//EncryptString(order);
		
		String userName = m_userName;//EncryptString(m_userName);
		String password = EncryptString(m_password);
		String devSn = EncryptString(m_devSN);
		
		byte[] seed = {'F', 'o', 'x', 'i', 't', 'A', 'n', 'd', 'r', 'o', 'i', 'd'};
		
		m_rsaKey = DrmNative.createRsaKey(1024, seed, null);
		
		if(m_rsaKey == null) 
				return null;
		
		String strClientPubKey = RtNative.base64EncodeToString(m_rsaKey.publicKey, 0, m_rsaKey.publicKey.length);
	//	String strClientPubKey = Base64.encodeToString(clientPubKey, Base64.DEFAULT);
		
		ObjectRef foac = DrmNative.createFoac(true);
		DrmNative.setFoacSessionID(foac, sessionID);
		
		DrmNative.setFoacRequestID(foac, "GetEnvelop");
		
		ObjectRef category = DrmNative.getFoacDataCategory(foac);
		if (category == null)
		{
			DrmNative.deleteFoac(foac);
			return null;
		}
		
		ObjectRef subCategory = DrmNative.addSubCategory(category, "OrderID", true);
		DrmNative.setCategoryAttribute(subCategory, "Value", strOrder);
		
		subCategory = DrmNative.addSubCategory(category, "EncryptFileID", true);
		DrmNative.setCategoryAttribute(subCategory, "Value", strFileID);
		
		subCategory = DrmNative.addSubCategory(category, "ClientPubKey", true);
		DrmNative.setCategoryAttribute(subCategory, "Value", strClientPubKey);
		
		subCategory = DrmNative.addSubCategory(category, "Usermail", true);
		DrmNative.setCategoryAttribute(subCategory, "Value", userName);
		
		subCategory = DrmNative.addSubCategory(category, "EncryptPassword", true);
		DrmNative.setCategoryAttribute(subCategory, "Value", password);
		
		subCategory = DrmNative.addSubCategory(category, "EncryptDeviceSN", true);
		DrmNative.setCategoryAttribute(subCategory, "Value", devSn);
		
		String request = DrmNative.saveFoac(foac);
		
		DrmNative.deleteFoac(foac);
		
		return "XmlContent=" + request;
	}
	
	public boolean parseEnvelopRequest(ObjectRef document, String receive)
	{
	
		BufferFileRead bufReader = new BufferFileRead(receive.getBytes(), 0);
		ObjectRef envelope = DrmNative.loadEnvelope(bufReader);
		byte[] key = DrmNative.getEnvelopeKey(envelope);
		String newstring = new String(key);
		Log.i("demo", newstring);
			
		String algorithm = DrmNative.getEnvelopeAlgorithm(envelope);
		byte[] deKey = DrmNative.pkiRsaDecrypt(key, m_rsaKey.privateKey);
		String newstring1 = new String(key);
		Log.i("after", newstring1);
		
		security = new ObjectRef();
    	String filter = "FoxitSTD";
		int ret = PdfSecurityNative.createFoxitDRMSecurity(filter, algorithm.equals("FOXIT_ENCRYPT2") ? RtBaseDef.CIPHER_AES : RtBaseDef.CIPHER_RC4, deKey, security);   
  		if(ret != RtBaseDef.ERR_SUCCESS) return false;
	
  		int offset = PdfDrmNative.getDocWrapperOffset(document);
		boolean flag = PdfSecurityNative.verifyFoxitDRMSecurity(security);
		
		ret = PdfDocNative.closeDoc(document);
		if(ret != RtBaseDef.ERR_SUCCESS) return false;
		
		ret = PdfDocNative.loadDoc(drmfile, 0, offset, document);
	    if(ret != RtBaseDef.ERR_SUCCESS) return false;
	    
		return true;
	}
	
	public boolean decrypt(ObjectRef document)
	{
		if (document == null) return false;

		String serviceUrl = getServiceURL();
		if(serviceUrl == null) return false;
		parseURL(serviceUrl);
		System.out.println("URL =="+serviceUrl);
		
		String sessionID = "6F9629FF-8A86-D011-B42D-00C04FC964FF";
		String send = getSessionBeginRequest(sessionID);
		if(send == null)return false;
		String receive = sendAndReceive(send);
		if (receive == null) return false;
		parseSessionBeginRecieve(receive);
		
		String checkAccount = GetCheckAccountRequest(sessionID);
		if (checkAccount == null) return false;
		receive = sendAndReceive(checkAccount);
		if (receive == null) return false;
		if (!ParseCheckAccountRequest(receive)) return false;
		
		String envelope = GetEnvelopRequest(sessionID);
		if (envelope == null) return false;
		receive = sendAndReceive(envelope);
		if (receive == null) return false;
		
		return parseEnvelopRequest(document, receive);
	}
	
	public void destroy()
	{
		int ret = RtBaseDef.ERR_ERROR;
		if (m_encryptParams != null)
			ret = PdfDrmNative.releaseEncryptParams(m_encryptParams);
		if(security != null)
			ret = PdfSecurityNative.destroySecurity(security);

	}
}
