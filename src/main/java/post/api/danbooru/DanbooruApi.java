package post.api.danbooru;

import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import enums.PostSite;
import lombok.extern.slf4j.Slf4j;
import post.Post;
import post.api.PostApiUtil;
import post.api.PostFetchException;
import post.api.generic.GenericApi;
import post.autocomplete.AutocompleteException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<ApplicationCommandOptionChoiceData> autocomplete(String input) throws AutocompleteException {
        String lastInput = PostApiUtil.getLastAutocompleteString(input);
        String urlString = getAutocompleteUrl(lastInput);

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .GET()
                    .build(), HttpResponse.BodyHandlers.ofString());

            DanbooruAutocompleteResponse[] responses = objectMapper.readValue(response.body(),
                    DanbooruAutocompleteResponse[].class);

            return Arrays.stream(responses).map(resp -> ApplicationCommandOptionChoiceData.builder()
                    .name(resp.getName() + " (" + resp.getPostCount() + ")")
                    .value(resp.getName())
                    .build()).collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new AutocompleteException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
            throw new AutocompleteException(e.getMessage(), e);
        }
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
        String encodedTags = PostApiUtil.encodeSpaces(tags);
        String urlString = BASE_URL + "posts.json?tags=" + encodedTags + "&page=" + page + "&limit=1";

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
