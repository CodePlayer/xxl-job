package com.xxl.job.core.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.xxl.job.core.biz.model.LogResult;
import com.xxl.job.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author xuxueli 2018-11-25 00:55:31
 */
public class XxlJobRemotingUtil {

	private static final Logger logger = LoggerFactory.getLogger(XxlJobRemotingUtil.class);
	public static final String XXL_JOB_ACCESS_TOKEN = "XXL-JOB-ACCESS-TOKEN";
	private static SSLSocketFactory sslSocketFactory;

	public static final TypeReference<ReturnT<String>> stringTypeRef = new TypeReference<ReturnT<String>>() {
	};
	public static final TypeReference<ReturnT<LogResult>> logResultTypeRef = new TypeReference<ReturnT<LogResult>>() {
	};

	// trust-https start
	private static void trustAllHosts(HttpsURLConnection connection) {
		// 内网一般是 http，无需直接静态初始化，需要时才懒加载并缓存工厂类
		SSLSocketFactory socketFactory = sslSocketFactory;
		if (socketFactory == null) {
			try {
				final SSLContext sslContext = SSLContext.getInstance("TLS");
				sslContext.init(null, new TrustManager[] {
						new X509TrustManager() {
							@Override
							public java.security.cert.X509Certificate[] getAcceptedIssuers() {
								return new java.security.cert.X509Certificate[] {};
							}

							@Override
							public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
							}

							@Override
							public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
							}
						}
				}, new java.security.SecureRandom());
				sslSocketFactory = socketFactory = sslContext.getSocketFactory();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		if (socketFactory != null) {
			connection.setSSLSocketFactory(socketFactory);
		}
		connection.setHostnameVerifier((hostname, session) -> true);
	}
	// trust-https end

	/**
	 * post
	 */
	public static <T> ReturnT<T> postBody(String url, String accessToken, int timeout, Object requestObj, TypeReference<ReturnT<T>> typeRef) {
		HttpURLConnection connection = null;
		BufferedReader bufferedReader = null;
		try {
			// connection
			URL realUrl = new URL(url);
			connection = (HttpURLConnection) realUrl.openConnection();

			// trust-https
			boolean useHttps = url.startsWith("https");
			if (useHttps) {
				HttpsURLConnection https = (HttpsURLConnection) connection;
				trustAllHosts(https);
			}

			// connection setting
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setReadTimeout(timeout * 1000);
			connection.setConnectTimeout(3 * 1000);
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
			connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");

			if (StringUtils.hasText(accessToken)) {
				connection.setRequestProperty(XXL_JOB_ACCESS_TOKEN, accessToken);
			}

			// do connection
			connection.connect();

			// write requestBody
			if (requestObj != null) {
				String requestBody = JSON.toJSONString(requestObj);

				DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
				dataOutputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
				dataOutputStream.flush();
				dataOutputStream.close();
			}

            /*byte[] requestBodyBytes = requestBody.getBytes("UTF-8");
            connection.setRequestProperty("Content-Length", String.valueOf(requestBodyBytes.length));
            OutputStream outwritestream = connection.getOutputStream();
            outwritestream.write(requestBodyBytes);
            outwritestream.flush();
            outwritestream.close();*/

			// valid StatusCode
			int statusCode = connection.getResponseCode();
			if (statusCode != 200) {
				return new ReturnT<>(ReturnT.FAIL_CODE, "xxl-job remoting fail, StatusCode(" + statusCode + ") invalid. for url : " + url);
			}

			// result
			bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				result.append(line);
			}
			String resultJson = result.toString();

			// parse returnT
			try {
				return JSON.parseObject(resultJson, typeRef);
			} catch (Exception e) {
				logger.error("xxl-job remoting (url=" + url + ") response content invalid(" + resultJson + ").", e);
				return new ReturnT<>(ReturnT.FAIL_CODE, "xxl-job remoting (url=" + url + ") response content invalid(" + resultJson + ").");
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return new ReturnT<>(ReturnT.FAIL_CODE, "xxl-job remoting error(" + e.getMessage() + "), for url : " + url);
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (connection != null) {
					connection.disconnect();
				}
			} catch (Exception e2) {
				logger.error(e2.getMessage(), e2);
			}
		}
	}

}