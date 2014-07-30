package httpClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class First {
	HttpClient httpClient;
	
	protected HttpResponse http_get(String url){
		HttpGet request = new HttpGet(url);
		try {
			HttpResponse response = getHttpClient().execute(request);
			return response;
		} catch (Exception e) {
			return null;
		}
	}
	
	protected JSONObject jsonDecode(HttpResponse response) throws Exception {
		try {
			String responseString = EntityUtils.toString(response.getEntity());
			JSONTokener tokener = new JSONTokener(responseString);
			JSONObject jsonObject = (JSONObject) tokener.nextValue();
			return jsonObject;
		} catch (Exception e) {
			throw e;
		}
	}


	protected HttpResponse http_post(String url, NameValuePair... pairs)
			throws Exception {

		HttpPost request = new HttpPost(url);
		request.getParams().setParameter("http.useragent", "ThinkSNSClient");
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
	
		
	protected HttpClient getHttpClient() {
		if (httpClient == null) {
			synchronized (this) {
				// double check
				if (httpClient == null) {
					httpClient = new DefaultHttpClient();
				}
			}
		}
		return httpClient;
	}
	
	public static void main(String[] args) {
		First first=new First();
		JSONObject result = null;
		JSONObject result2 = null;
		
		try {
			HttpResponse response=first.http_post("http://1.zouxuan2.sinaapp.com/login.php", 
					new BasicNameValuePair("username","zouxuan"),new BasicNameValuePair("password", "12345"));
			result = first.jsonDecode(response);
			HttpResponse response2=first.http_post("http://1.zouxuan2.sinaapp.com/register.php", 
					new BasicNameValuePair("username","zou"),new BasicNameValuePair("password", "123"));
			result2 = first.jsonDecode(response2);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 		
		System.out.println("-------------");
		System.out.println(result.getString("result"));
		System.out.println(result2.getString("result"));
		
	}
	

}
