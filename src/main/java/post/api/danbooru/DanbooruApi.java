package post.api.danbooru;

import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import lombok.extern.slf4j.Slf4j;
import post.Post;
import post.api.PostApi;
import post.api.PostApiUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DanbooruApi implements PostApi {

    private static final String baseUrl = "https://danbooru.donmai.us/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Optional<Post> fetchById(long id) {
        String urlString = baseUrl + "posts/" + id + ".json";

        try {
            return Optional.of(objectMapper.readValue(new URL(urlString), Post.class));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Post> fetchByTagsAndPage(String tags, int page) {
        String urlString = baseUrl + "posts.json/" + "?tags=" + tags + "&page=" + page + "&limit=1";

        try {
            DanbooruPost[] posts = objectMapper.readValue(new URL(urlString), DanbooruPost[].class);

            if (posts.length == 0) {
                return Optional.empty();
            }

            return Optional.of(posts[0]);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public int fetchCount(String tags) {
        try {
            URL url = new URL(baseUrl + "counts/posts.json?tags=" + tags);

            DanbooruCountsResponse danbooruCountsResponse = objectMapper.readValue(url, DanbooruCountsResponse.class);

            return danbooruCountsResponse.getCounts().getPosts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasAutocomplete() {
        return true;
    }

    @Override
    public List<ApplicationCommandOptionChoiceData> autocomplete(String input) {
        String urlString =  baseUrl + "autocomplete.json?search%5Bquery%5D="
                + input + "&search%5Btype%5D=tag_query&limit=10";

        return PostApiUtil.autocomplete(urlString);
    }

    @Override
    public int getMaxCount() {
        return Integer.MAX_VALUE;
    }
}
