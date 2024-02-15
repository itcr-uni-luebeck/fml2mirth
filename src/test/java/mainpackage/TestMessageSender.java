package mainpackage;

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


public class TestMessageSender {

    private CloseableHttpClient httpClient;
    private String baseUrl;


    public TestMessageSender(String baseUrl) {

        this.baseUrl = baseUrl;

        try {
            // ignore ssl cert!
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


    public String sendMessage (String message) {

        HttpPost httpPost = new HttpPost(this.baseUrl);
        addHeaders(httpPost);

        httpPost.setEntity( new StringEntity(message.trim()) );

        String responseBody = null;
        CloseableHttpResponse response = null;
        try {
            response = this.httpClient.execute(httpPost);
            System.out.println(response.getCode() + " " + response.getReasonPhrase());
            HttpEntity entity = response.getEntity();
            responseBody = EntityUtils.toString(entity);
            response.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        return responseBody;
    }

    private void addHeaders(HttpUriRequestBase request) {
        request.addHeader("X-Requested-With", "XMLHttpRequest");
        request.addHeader("Accept", "*/*");
        request.addHeader("Accept-Encoding", "gzip, deflate, br");
        request.addHeader("Content-Type", "application/xml");
    }


}
