package nsfw.post.api.danbooru;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import nsfw.post.autocomplete.AutocompleteResult;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DanbooruAutocompleteResult implements AutocompleteResult {

    private String name;

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String getValue() {
        return getLabel();
    }

    @Override
    public boolean isJson() {
        return true;
    }
}
