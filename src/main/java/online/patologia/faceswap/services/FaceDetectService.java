package online.patologia.faceswap.services;

import com.google.gson.Gson;
import online.patologia.faceswap.models.Face;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.net.URI;

@Service
public class FaceDetectService {
    private static final String subscriptionKey = "<key>";
    private static final String uriBase =
            "https://westcentralus.api.cognitive.microsoft.com/face/v1.0/detect";

    public Face[] getFace(String url) {
        HttpClient httpclient = HttpClientBuilder.create().build();
        String imageWithFaces =
                "{\"url\":\""+url+"\"}";
        try {
            URIBuilder builder = new URIBuilder(uriBase);

            builder.setParameter("returnFaceId", "false");
            builder.setParameter("returnFaceLandmarks", "false");

            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            StringEntity reqEntity = new StringEntity(imageWithFaces);
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            InputStreamReader reader = new InputStreamReader(entity.getContent());
            Face[] face = new Gson().fromJson(reader, Face[].class);
            return face;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
