package nsfw.post.api.danbooru;

import com.fasterxml.jackson.databind.ObjectMapper;
import nsfw.enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import nsfw.post.api.GenericPostApi;
import nsfw.post.api.PostApiUtil;
import nsfw.post.api.PostFetchException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Slf4j
public class DanbooruApi implements GenericPostApi {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String BASE_URL = "https://danbooru.donmai.us/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ObjectMapper getXmlMapper() {
        return objectMapper;
    }

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
        return BASE_URL + "tags.json?search[name_matches]=*" + tags + "*&limit=10&search[order]=count";
    }

    @Override
    public Optional<Integer> getMaxCount() {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getMaxTags() {
        return Optional.of(2);
    }

    @Override
    public String getFetchByTagsAndPageUrl(String tags, int page) {
        return getBaseUrl() + "posts.json?tags=" + PostApiUtil.encodeSpaces(tags) + "&page=" + page + "&limit=1";
    }

//    @Override
//    public Optional<Post> fetchByTagsAndPage(String tags, int page) throws PostFetchException {
//        String encodedTags = PostApiUtil.encodeSpaces(tags);
//        String urlString =
//
//        try {
//            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
//            HttpResponse<String> response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//
//            if (response.statusCode() != 200) {
//                throw new PostFetchException("Could not fetch post");
//            }
//
//            DanbooruPost[] posts = objectMapper.readValue(response.body(), DanbooruPost[].class);
//
//            return Arrays.stream(posts).findFirst().map(x -> x);
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//            throw new PostFetchException(e.getMessage(), e);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            log.error(e.getMessage(), e);
//            throw new PostFetchException(e.getMessage(), e);
//        }
//    }

    @Override
    public int fetchCount(String tags) throws PostFetchException {
        try {
            String encodedTags = PostApiUtil.encodeSpaces(tags);
            String urlString = BASE_URL + "counts/posts.json?tags=" + encodedTags;

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
