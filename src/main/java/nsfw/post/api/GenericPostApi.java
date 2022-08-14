package nsfw.post.api;

import nsfw.post.Post;
import nsfw.post.autocomplete.AutocompleteResult;

public interface GenericPostApi extends PostApi<nsfw.post.api.generic.PostQueryResult<Post>, Post, AutocompleteResult> {
}
