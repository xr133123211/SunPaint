package cn.nju.zouxuan.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Client {

	static HttpClient httpClient;
	public static String name;

	public static HttpResponse http_get(String url) {
		HttpGet request = new HttpGet(url);
		try {
			HttpResponse response = getHttpClient().execute(request);
			return response;
		} catch (Exception e) {
			return null;
		}
	}

	public static JSONObject jsonDecode(HttpResponse response) throws Exception {
		try {
			String responseString = EntityUtils.toString(response.getEntity());
			JSONTokener tokener = new JSONTokener(responseString);
			JSONObject jsonObject = (JSONObject) tokener.nextValue();
			return jsonObject;
		} catch (Exception e) {
			throw e;
		}
	}

	public  static HttpResponse http_post(String url, NameValuePair... pairs)
			throws Exception {

		HttpPost request = new HttpPost(url);
		List<NameValuePair> argList = new ArrayList<NameValuePair>();
		for (NameValuePair pair : pairs) {
			argList.add(pair);
		}
		try {
			request.setEntity(new UrlEncodedFormEntity(argList, "UTF-8"));
			HttpResponse response = getHttpClient().execute(request);
			return response;
		} catch (Exception e) {
			throw e;
		}
	}

	public static HttpClient getHttpClient() {
		if (httpClient == null) {
			httpClient = new DefaultHttpClient();
		}
		return httpClient;
	}

}
