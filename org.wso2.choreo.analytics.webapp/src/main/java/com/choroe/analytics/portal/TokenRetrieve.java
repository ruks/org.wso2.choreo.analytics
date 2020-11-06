package com.choroe.analytics.portal;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.servlet.http.Cookie;

public class TokenRetrieve {

	public TokenInfo getToken(String code) {
        Configuration conf = Configuration.getInstance();
		try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(conf.getTokenEndpoint());
            String auth = (conf.getConsumerKey()+":"+conf.getConsumerSecret());
            request.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString(auth.getBytes()));
            request.addHeader("Content-Type", "application/x-www-form-urlencoded");

            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("grant_type", "authorization_code"));
            list.add(new BasicNameValuePair("code", code));
            list.add(new BasicNameValuePair("redirect_uri", conf.getLoginCallback()));
			UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(list, Charset.defaultCharset());
            request.setEntity(reqEntity);
            try(CloseableHttpResponse response = httpClient.execute(request)) {
                System.out.println(response.getStatusLine().toString());
                HttpEntity entity = response.getEntity();
                if (entity != null && response.getStatusLine().getStatusCode() == 200) {
                	JSONObject reqObj = new JSONObject(EntityUtils.toString(entity));
                    System.out.println(reqObj);
                	String token = reqObj.getString("access_token");
                	String idToken = reqObj.getString("id_token");
                	if(token.split("\\.").length != 3 ) {
                        return null;
                    }
                	String body = token.split("\\.")[1];

                	String decodedBody = new String(Base64.getDecoder().decode(body.getBytes()));
                	JSONObject bodyObj = new JSONObject(decodedBody);
                	System.out.println(bodyObj);
                	TokenInfo info = new TokenInfo();
                	info.setUser(bodyObj.getString("sub"));
                    System.out.println(info.getUser());

                    List<Cookie> cookies = Utils.getLoginCookies(token, idToken);
                    info.setCookies(cookies.toArray(new Cookie[0]));
                    return info;
                }
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


}
