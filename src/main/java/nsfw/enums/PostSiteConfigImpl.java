package nsfw.enums;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import nsfw.post.api.PostFetchOptions;
import nsfw.post.api.generic.GenericPost;
import nsfw.post.api.generic.GenericPostQueryResult;
import nsfw.post.autocomplete.AutocompleteResultImpl;

import java.util.Optional;

public abstract class PostSiteConfigImpl implements PostSiteConfig {

    private final TypeFactory typeFactory = TypeFactory.defaultInstance();

    @Override
    public String getUrl(PostFetchOptions postFetchOptions) {
        return getBaseUrl() + "page=" + postFetchOptions.getPage();
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
    public JavaType getPostType() {
        return typeFactory.constructType(GenericPost.class);
    }

    @Override
    public JavaType getPostQueryResultType() {
        return typeFactory.constructType(GenericPostQueryResult.class);
    }

    @Override
    public JavaType getAutocompleteResultType() {
        return typeFactory.constructType(AutocompleteResultImpl.class);
    }

}
