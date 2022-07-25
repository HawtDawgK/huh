package post.api.hypnohub;

import enums.PostSite;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.javacord.api.interaction.SlashCommandOptionChoice;
import post.Post;
import post.api.PostApiUtil;
import post.api.PostFetchException;
import post.api.generic.GenericApi;
import post.autocomplete.AutocompleteException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HypnohubApi extends GenericApi {

    @Override
    public String getBaseUrl() {
        return "https://hypnohub.net/";
    }

    @Override
    public boolean hasAutocomplete() {
        return true;
    }

    @Override
    public String getAutocompleteUrl(String tags) {
        return getBaseUrl() + "tag/index.xml?limit=10&order=count&name=" + tags;
    }

    @Override
    public PostSite getSite() {
        return PostSite.HYPNOHUB;
    }

    @Override
    public Optional<Integer> getMaxCount() {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getMaxTags() {
        return Optional.empty();
    }

    @Override
    public int fetchCount(String tags) throws PostFetchException {
        String encodedTags = PostApiUtil.encodeSpaces(tags);
        String url = getBaseUrl() + "post/index.xml?limit=0&tags=" + encodedTags;

        try {
            return getCount(url);
        } catch (IOException e) {
            throw new PostFetchException(e.getMessage(), e);
        } catch ( InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PostFetchException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Post> fetchById(long id) throws PostFetchException {
        String urlString = getBaseUrl() + "post/index.xml?limit=1&tags=id:" + id;

        try {
            return getPost(urlString).map(x -> x);
        } catch (IOException e) {
            throw new PostFetchException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PostFetchException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<Post> fetchByTagsAndPage(String tags, int page) throws PostFetchException {
        String urlString = getBaseUrl() + "post/index.xml?limit=1&tags=" + tags + "&page=" + page;

        try {
            return getPost(urlString).map(x -> x);
        } catch (IOException e) {
            throw new PostFetchException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PostFetchException(e.getMessage(), e);
        }
    }

    @Override
    public List<SlashCommandOptionChoice> autocomplete(String input) throws AutocompleteException {
        String urlString = getBaseUrl() + "tag/index.xml?order=count&limit=25&name=" + input;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .build();

            HttpResponse<String> response = getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            HypnohubAutocompleteResponse autocompleteResponse = getXmlMapper()
                    .readValue(response.body(), HypnohubAutocompleteResponse.class);

            return autocompleteResponse.getTags().stream()
                    .map(HypnohubTag::toApplicationCommandOptionChoiceData)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new AutocompleteException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AutocompleteException(e.getMessage(), e);
        }
    }

    private Optional<HypnohubPost> getPost(String urlString) throws IOException, InterruptedException, PostFetchException {
        HttpClient httpClient = getHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new PostFetchException("Error occurred fetching post");
        }

        HypnoHubQueryResult hypnoHubQueryResult = getXmlMapper().readValue(response.body(), HypnoHubQueryResult.class);

        List<HypnohubPost> posts = hypnoHubQueryResult.getPosts().stream()
                .map(x -> (HypnohubPost) x).collect(Collectors.toList());
        return posts.stream().findFirst();
    }

    private int getCount(String urlString) throws IOException, InterruptedException {
        HttpClient httpClient = getHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        HypnoHubQueryResult hypnoHubQueryResult = getXmlMapper().readValue(response.body(), HypnoHubQueryResult.class);

        return hypnoHubQueryResult.getCount();
    }
}
