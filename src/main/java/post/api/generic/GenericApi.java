package post.api.generic;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.interaction.SlashCommandOptionChoice;
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
public abstract class GenericApi implements PostApi {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        xmlMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
    }

    @Override
    public boolean hasAutocomplete() {
        return true;
    }

    @Override
    public String getAutocompleteUrl(String tags) {
        return getBaseUrl() + "autocomplete.php?q=" + tags;
    }

    @Override
    public Optional<Integer> getMaxCount() {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getMaxTags() {
        return Optional.empty();
    }

    public static HttpClient getHttpClient() {
        return HTTP_CLIENT;
    }

    public static XmlMapper getXmlMapper() {
        return xmlMapper;
    }

    @Override
    public int fetchCount(String tags) throws PostFetchException {
        String encodedTags = PostApiUtil.encodeSpaces(tags);
        String urlString = getBaseUrl() + "index.php?page=dapi&s=post&q=index&limit=0&tags=" + encodedTags;
        GenericPostQueryResult postQueryResult = getPosts(urlString);

        return postQueryResult.getCount();
    }

    @Override
    public Optional<Post> fetchById(long id) throws PostFetchException {
        String urlString = getBaseUrl() + "index.php?page=dapi&s=post&q=index&limit=1&id=" + id;
        GenericPostQueryResult queryResult = getPosts(urlString);
        return getFirstPost(queryResult);
    }

    @Override
    public Optional<Post> fetchByTagsAndPage(String tags, int page) throws PostFetchException {
        String encodedTags = PostApiUtil.encodeSpaces(tags);

        String urlString = getBaseUrl() + "index.php?page=dapi&s=post&q=index&limit=1&tags="
                + encodedTags + "&pid=" + page;
        GenericPostQueryResult queryResult = getPosts(urlString);

        return getFirstPost(queryResult);
    }

    @Override
    public List<SlashCommandOptionChoice> autocomplete(String input) throws AutocompleteException {
        String lastInput = PostApiUtil.getLastAutocompleteString(input);
        String urlString = getAutocompleteUrl(lastInput);
        return PostApiUtil.autocomplete(urlString, input);
    }

    private Optional<Post> getFirstPost(GenericPostQueryResult queryResult) {
        Optional<Post> optionalPost = queryResult.getPosts().stream().findFirst();

        return optionalPost.map(x -> {
            x.setSite(getSite());
            return x;
        });
    }

    private GenericPostQueryResult getPosts(String urlString) throws PostFetchException {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new PostFetchException("Error occurred fetching post");
            }

            return xmlMapper.readValue(response.body(), GenericPostQueryResult.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new PostFetchException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error(e.getMessage(), e);
            throw new PostFetchException(e.getMessage(), e);
        }
    }
}
