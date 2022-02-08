package post.api.danbooru;

import com.fasterxml.jackson.databind.ObjectMapper;
import enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import post.Post;
import post.api.PostFetchException;
import post.api.generic.GenericApi;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class DanbooruApi extends GenericApi {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String BASE_URL = "https://danbooru.donmai.us/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public PostSite getSite() {
        return PostSite.DANBOORU;
    }

    @Override
    public boolean hasAutocomplete() {
        return true;
    }

    @Override
    public String getAutocompleteUrl(String tags) {
        return BASE_URL + "autocomplete.json?search%5Bquery%5D=" + tags + "&search%5Btype%5D=tag_query&limit=10";
    }

    @Override
    public Optional<Integer> getMaxCount() {
        return Optional.empty();
    }

    @Override
    public Optional<Post> fetchById(long id) throws PostFetchException {
        String urlString = BASE_URL + "posts/" + id + ".json";

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
            HttpResponse<String> response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new PostFetchException("Could not fetch post");
            }

            return Optional.of(objectMapper.readValue(response.body(), DanbooruPost.class));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new PostFetchException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
            throw new PostFetchException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Post> fetchByTagsAndPage(String tags, int page) throws PostFetchException {
        String urlString = BASE_URL + "posts.json/" + "?tags=" + tags + "&page=" + page + "&limit=1";

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
            HttpResponse<String> response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new PostFetchException("Could not fetch post");
            }

            DanbooruPost[] posts = objectMapper.readValue(response.body(), DanbooruPost[].class);

            return Arrays.stream(posts).findFirst().map(x -> x);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new PostFetchException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
            throw new PostFetchException(e.getMessage(), e);
        }
    }

    @Override
    public int fetchCount(String tags) throws PostFetchException {
        try {
            String urlString = BASE_URL + "counts/posts.json?tags=" + tags;

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
            HttpResponse<String> response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            DanbooruCountsResponse danbooruCountsResponse = objectMapper
                    .readValue(response.body(), DanbooruCountsResponse.class);

            return danbooruCountsResponse.getCounts().getPosts();
        } catch (IOException e) {
            throw new PostFetchException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PostFetchException(e.getMessage(), e);
        }
    }

}
