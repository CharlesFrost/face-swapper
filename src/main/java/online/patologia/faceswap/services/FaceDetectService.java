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
    private static final String subscriptionKey = "71b92edebf19453ab3cbf9a1ed2f04a1";
    private static final String uriBase =
            "https://westcentralus.api.cognitive.microsoft.com/face/v1.0/detect";

    private static final String faceAttributes =
            "age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise";
    public Face[] getFace(String url) {
        HttpClient httpclient = HttpClientBuilder.create().build();
        String imageWithFaces =
                "{\"url\":\""+url+"\"}";
        try {
            URIBuilder builder = new URIBuilder(uriBase);

            // Request parameters. All of them are optional.
            builder.setParameter("returnFaceId", "false");
            builder.setParameter("returnFaceLandmarks", "false");

            // Prepare the URI for the REST API call.
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            // Request body.
            StringEntity reqEntity = new StringEntity(imageWithFaces);
            request.setEntity(reqEntity);

            // Execute the REST API call and get the response entity.
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            InputStreamReader reader = new InputStreamReader(entity.getContent());
            Face[] face = new Gson().fromJson(reader, Face[].class);

//            if (entity != null)
//            {
//                // Format and display the JSON response.
//                System.out.println("REST Response:\n");
//
//                String jsonString = EntityUtils.toString(entity).trim();
//                if (jsonString.charAt(0) == '[') {
//                    JSONArray jsonArray = new JSONArray(jsonString);
//                    System.out.println(jsonArray.toString(2));
//                }
//                else if (jsonString.charAt(0) == '{') {
//                    JSONObject jsonObject = new JSONObject(jsonString);
//                    System.out.println(jsonObject.toString(2));
//                } else {
//                    System.out.println(jsonString);
//                }
//            }
            return face;
        }
        catch (Exception e)
        {
            // Display error message.
            System.out.println(e.getMessage());
            return null;
        }
    }
}
