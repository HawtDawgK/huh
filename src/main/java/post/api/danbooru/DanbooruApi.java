package post.api.danbooru;

import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import lombok.extern.slf4j.Slf4j;
import post.Post;
import post.api.PostApi;
import post.api.PostApiUtil;
import post.api.PostFetchException;
import post.autocomplete.AutocompleteException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DanbooruApi implements PostApi {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final String BASE_URL = "https://danbooru.donmai.us/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Optional<Post> fetchById(long id) throws PostFetchException {
        String urlString = BASE_URL + "posts/" + id + ".json";

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
            HttpResponse<String> response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new PostFetchException("Could not fetch post");
            }

            return Optional.of(objectMapper.readValue(response.body(), Post.class));
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
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

            if (posts.length == 0) {
                return Optional.empty();
            }

            return Optional.of(posts[0]);
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public int fetchCount(String tags) {
        try {
            String urlString = BASE_URL + "counts/posts.json?tags=" + tags;

            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
            HttpResponse<String> response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            DanbooruCountsResponse danbooruCountsResponse = objectMapper
                    .readValue(response.body(), DanbooruCountsResponse.class);

            return danbooruCountsResponse.getCounts().getPosts();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasAutocomplete() {
        return true;
    }

    @Override
    public List<ApplicationCommandOptionChoiceData> autocomplete(String input) throws AutocompleteException {
        String urlString =  BASE_URL + "autocomplete.json?search%5Bquery%5D="
                + input + "&search%5Btype%5D=tag_query&limit=10";

        return PostApiUtil.autocomplete(urlString);
    }

    @Override
    public int getMaxCount() {
        return Integer.MAX_VALUE;
    }
}
