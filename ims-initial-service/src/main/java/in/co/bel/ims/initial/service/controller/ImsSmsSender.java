package in.co.bel.ims.initial.service.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import in.co.bel.ims.initial.service.dto.ImsSMS;

@Component
public class ImsSmsSender {
	
	@Value("${BASE_URL_DOMAIN}")
	private String smsURL;
	@Value("${USERNAME}")
	private String username;
	@Value("${PIN}")
	private String pin;
	@Value("${SENDER_ID}")
	private String signature;
	@Value("${DLT_ENTITY_ID}")
	private String dltEntityId;

	public void send(ImsSMS smsData) {
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			TrustManager[] tempTrustManager = getTempTrustManager();
			sc.init(null, tempTrustManager, new java.security.SecureRandom());
			HostnameVerifier validHosts = getTempValidHosts();

			StringBuffer messageBody = new StringBuffer();
			messageBody.append("username="+username);
			messageBody.append("&pin="+pin);
			messageBody.append("&message=" + URLEncoder.encode(smsData.getMessage(), "UTF-8"));
			messageBody.append("&mnumber=91" + smsData.getMobileNo());
			messageBody.append("&signature="+signature);
			messageBody.append("&dlt_entity_id="+dltEntityId);
			messageBody.append("&dlt_template_id=" + smsData.getTemplateId());

			HttpsURLConnection conn = (HttpsURLConnection) new URL(smsURL).openConnection();
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(validHosts);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Length", Integer.toString(messageBody.toString().length()));
			conn.getOutputStream().write(messageBody.toString().getBytes());
			final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			final StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				stringBuffer.append(line);
			}

			System.out.println("SMS Data: " + messageBody.toString());
			System.out.println("SMS Response: " + stringBuffer.toString());

			rd.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TrustManager[] getTempTrustManager() {
		TrustManager[] tempTrustManager = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
					throws CertificateException {
				// TODO Auto-generated method stub

			}
		} };

		return tempTrustManager;
	}

	private HostnameVerifier getTempValidHosts() {
		HostnameVerifier validHosts = new HostnameVerifier() {

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		};

		return validHosts;
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(Exception.class)
	public String handleCustomExceptions(Exception ex) {
		ex.printStackTrace();
		return ex.getMessage();
	}
	
}