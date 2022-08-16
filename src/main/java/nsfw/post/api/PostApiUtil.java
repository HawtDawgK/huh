package nsfw.post.api;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@UtilityClass
public class PostApiUtil {

    public static String encodeSpaces(String tags) {
        return tags.replace(' ', '+');
    }

    public <T> T getResponseAsClass(JavaType javaType, ObjectMapper objectMapper, String url) throws PostApiException {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.error("Received response with code {}, body:{}", response.statusCode(), response.body());
                throw new PostApiException("Error fetching autocomplete");
            }

            return objectMapper.readValue(response.body(), javaType);
        } catch (IOException e) {
            throw new PostApiException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PostApiException(e.getMessage(), e);
        }
    }
}
