package nsfw.post.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import nsfw.enums.PostSite;
import nsfw.post.Post;
import nsfw.post.autocomplete.AutocompleteException;
import nsfw.post.autocomplete.AutocompleteResult;
import org.javacord.api.interaction.SlashCommandOptionChoice;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface PostApi<R extends nsfw.post.api.generic.PostQueryResult<P>, P extends Post, A extends AutocompleteResult> {

    ObjectMapper xmlMapper = new XmlMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    ObjectMapper jsonMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    /**
     * @return the base url with trailing /
     */
    String getBaseUrl();

    PostSite getSite();

    default boolean hasAutocomplete() {
        return true;
    }

    default String getAutocompleteUrl(String tags) {
        return getBaseUrl() + "autocomplete.php?q=" + tags;
    }

    default Optional<Integer> getMaxCount() {
        return Optional.empty();
    }

    default Optional<Integer> getMaxTags() {
        return Optional.empty();
    }

    default int fetchCount(String tags) throws PostFetchException {
        return getPosts(getFetchByTagsAndPageUrl(tags, 0)).getCount();
    }

    default String getFetchByIdUrl(long id) {
        return getFetchByTagsAndPageUrl("id:" + id, 0);
    }

    default P fetchById(long id) throws PostFetchException {
        R result = getPosts(getFetchByIdUrl(id));
        return result.getPosts().stream().findFirst()
                .orElseThrow(() -> new PostFetchException("No post found"));
    }

    default String getFetchByTagsAndPageUrl(String tags, int page) {
        return getBaseUrl() + "index.php?page=dapi&s=post&q=index&limit=1&tags="
            + PostApiUtil.encodeSpaces(tags) + "&pid=" + page;
    }

    default Optional<P> fetchByTagsAndPage(String tags, int page) throws PostFetchException {
        R result = getPosts(getFetchByTagsAndPageUrl(tags, page));
        return result.getPosts().stream().findFirst();
    }

    default List<SlashCommandOptionChoice> autocomplete(String input) throws AutocompleteException {
        return Arrays.stream(getAutocompleteResult(input))
                .map(x -> SlashCommandOptionChoice.create(x.getLabel(), x.getValue()))
                .collect(Collectors.toList());
    }

    default ObjectMapper getXmlMapper() {
        return xmlMapper;
    }

    default ObjectMapper getJsonMapper() {
        return jsonMapper;
    }

    default R getPosts(String urlString) throws PostFetchException {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println(response.body());
                throw new PostFetchException("Error occurred fetching post");
            }

            R result = getXmlMapper().readValue(response.body(), new TypeReference<>() {});
            result.getPosts().forEach(p -> p.setSite(getSite()));

            return result;
        } catch (IOException e) {
            throw new PostFetchException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PostFetchException(e.getMessage(), e);
        }
    }

    default A[] getAutocompleteResult(String urlString) throws AutocompleteException {
        try {
            String url = getAutocompleteUrl(urlString);

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new AutocompleteException("Error fetching autocomplete");
            }

            return getJsonMapper().readValue(response.body(), new TypeReference<>() { });
        } catch (IOException e) {
            throw new AutocompleteException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AutocompleteException(e.getMessage(), e);
        }
    }
}
