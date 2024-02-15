package channelgenerator;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.utils.Base64;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/***
 * Class that handles the connection to the mirth server.
 */
public class MirthConnector {

    private String baseUrl;
    private String userName;
    private String password;

    private CloseableHttpClient httpClient;

    public MirthConnector(String baseUrl, String userName, String password) {
        this.baseUrl = baseUrl;
        this.userName = userName;
        this.password = password;

        try {
            // just to ignore ssl cert!
            TrustStrategy acceptingTrusStradegy = (cert, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrusStradegy).build();
            SSLConnectionSocketFactory sslSf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslSf)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();

            HttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSf)
                    .build();

             this.httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void postChannel (String channelXML) {

        HttpPost httpPost = new HttpPost(this.baseUrl + "/channels");
        addHeaders(httpPost);

        httpPost.setEntity( new StringEntity(channelXML) );

        CloseableHttpResponse response = null;
        try {
            response = this.httpClient.execute(httpPost);
            System.out.println(response.getCode() + " " + response.getReasonPhrase());
            HttpEntity entity = response.getEntity();
            if(response.getCode() != 200) {
                System.out.println(EntityUtils.toString(entity));
            }
            response.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void deployChannel (String channelID) {
        HttpPost httpPost = new HttpPost(this.baseUrl + "/channels/" + channelID + "/_deploy");
        addHeaders(httpPost);

        CloseableHttpResponse response = null;
        try {
            response = this.httpClient.execute(httpPost);
            System.out.println(response.getCode() + " " + response.getReasonPhrase());
            HttpEntity entity = response.getEntity();
            response.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String getChannel (String channelID) {

        HttpGet httpGet = new HttpGet(this.baseUrl + "/channels/" + channelID);
        addHeaders(httpGet);

        CloseableHttpResponse response = null;
        String channelTemplate = null;
        try {
            response = this.httpClient.execute(httpGet);
            System.out.println(response.getCode() + " " + response.getReasonPhrase());
            HttpEntity entity = response.getEntity();
            if(response.getCode() == 204) {
              System.out.println("No channel found with ID: " + channelID);
            }
            else if(response.getCode() != 200) {
                System.out.println(EntityUtils.toString(entity));
            } else {
                channelTemplate = EntityUtils.toString(entity);
            }
            response.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        return channelTemplate;
    }


    private void addHeaders(HttpUriRequestBase request) {
        request.addHeader("X-Requested-With", "XMLHttpRequest");
        request.addHeader("Accept", "*/*");
        request.addHeader("Accept-Encoding", "gzip, deflate, br");
        request.addHeader("Content-Type", "application/xml");

        String credentials = this.userName + ":" + this.password;
        byte[] encodeCreds = Base64.encodeBase64(credentials.getBytes(StandardCharsets.UTF_8));
        request.addHeader("Authorization","Basic " + new String(encodeCreds) );
    }

}
