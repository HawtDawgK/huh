package post.api.rule34;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import io.netty.handler.codec.http.HttpStatusClass;
import lombok.extern.slf4j.Slf4j;
import post.api.PostFetchException;
import post.autocomplete.AutocompleteException;
import post.autocomplete.AutocompleteResult;
import post.Post;
import post.PostMetadata;
import post.api.PostApi;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class Rule34Api implements PostApi {

    private static final String BASE_URL = "https://api.rule34.xxx/index.php?page=dapi&s=post&q=index";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private static final XmlMapper xmlMapper = new XmlMapper();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Optional<Post> fetchById(long id) throws PostFetchException {
        String urlString = BASE_URL + "&limit=1&tags=id:" + id;
        Rule34QueryResult rule34QueryResult = getResult(urlString);
        return getFirstPost(rule34QueryResult);
    }

    @Override
    public Optional<Post> fetchByTagsAndPage(String tags, int page) throws PostFetchException {
        String urlString = BASE_URL + "&limit=1&tags=" + tags + "&pid=" + page;
        Rule34QueryResult rule34QueryResult = getResult(urlString);

        Optional<Post> post = getFirstPost(rule34QueryResult);
        post.ifPresent(p -> p.setPostMetadata(new PostMetadata(rule34QueryResult.getCount(), page)));

        return post;
    }

    @Override
    public int fetchCount(String tags) throws PostFetchException {
        String urlString = BASE_URL + "&limit=0&tags=" + tags;
        Rule34QueryResult rule34QueryResult = getResult(urlString);

        return rule34QueryResult.getCount();
    }

    @Override
    public List<ApplicationCommandOptionChoiceData> autocomplete(String input) throws AutocompleteException {
        String urlString =  "https://rule34.xxx/autocomplete.php?q=" + input;

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new AutocompleteException("Error fetching autocomplete");
            }

            AutocompleteResult[] autocompleteResults = objectMapper.readValue(response.body(), AutocompleteResult[].class);

            return Arrays.stream(autocompleteResults)
                    .map(AutocompleteResult::toApplicationCommandOptionChoiceData)
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasAutocomplete() {
        return true;
    }

    @Override
    public int getMaxCount() {
        return 200000;
    }

    private Optional<Post> getFirstPost(Rule34QueryResult rule34QueryResult) {
        return rule34QueryResult.getPosts().stream().findFirst().map(x -> x);
    }

    private Rule34QueryResult getResult(String urlString) throws PostFetchException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new PostFetchException("Error occurred fetching post");
            }

            return xmlMapper.readValue(response.body(), Rule34QueryResult.class);
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new PostFetchException(e.getMessage(), e);
        }
    }
}
