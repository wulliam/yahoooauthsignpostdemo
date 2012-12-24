package org.wuliang.demo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 
 * @author wuliang
 */
public class YahooOauthSignPostDemo {

    private static String PROTECTED_RESOURCE_URL = "http://social.yahooapis.com/v1/user/%1s/profile?format=json";

    public static void main(String[] args) throws Exception {
        OAuthConsumer consumer = getConsumer();
        queryProfile(consumer);
    }

    public static OAuthConsumer getConsumer() throws Exception {

        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
                "dj0yJmk9U2FpR0U5UmNUcU05JmQ9WVdrOWVFUlpSSFpQTkdVbWNHbzlNVFkyTVRnNU5URTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD1lMw--",
                "baa7bdb6e4e1b609c94d7e48b27c30d65fccc6b3");

        OAuthProvider provider = new DefaultOAuthProvider(
                "https://api.login.yahoo.com/oauth/v2/get_request_token",
                "https://api.login.yahoo.com/oauth/v2/get_token",
                "https://api.login.yahoo.com/oauth/v2/request_auth");

        System.out.println("Fetching request token from Yahoo...");

        // we do not support callbacks, thus pass OOB
        String authUrl = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);

        System.out.println("Request token: " + consumer.getToken());
        System.out.println("Token secret: " + consumer.getTokenSecret());

        System.out.println("Now visit:\n" + authUrl + "\n... and grant this app authorization");
        System.out.println("Enter the verification code and hit ENTER when you're done");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String code = br.readLine();

        System.out.println("Fetching access token from Yahoo...");

        provider.retrieveAccessToken(consumer, code);

        System.out.println("Access token: " + consumer.getToken());
        System.out.println("Token secret: " + consumer.getTokenSecret());
        String yahoo_guid = provider.getResponseParameters().get(YAHOO_GUID).first();
        System.out.println("yahoo_guid: " + yahoo_guid);
        PROTECTED_RESOURCE_URL = String.format(PROTECTED_RESOURCE_URL, yahoo_guid);

        return consumer;
    }

    public static void queryProfile(OAuthConsumer consumer) throws Exception {

        // PROTECTED_RESOURCE_URL =
        // "http://social.yahooapis.com/v1/user/SI4NXTYBIRRWN5ISOSEH6TIA7Y/profile?format=json";
        HttpGet request = new HttpGet(PROTECTED_RESOURCE_URL);
        consumer.sign(request);

        System.out.println("Get user profile in from Yahoo...");

        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(request);

        System.out.println("Response: " + response.getStatusLine().getStatusCode() + " "
                + response.getStatusLine().getReasonPhrase());
        System.out.println("body:" + toStr(response.getEntity().getContent()));
    }

    private static final String YAHOO_GUID = "xoauth_yahoo_guid";

    public static String toStr(InputStream in) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        return br.readLine();
    }

}
