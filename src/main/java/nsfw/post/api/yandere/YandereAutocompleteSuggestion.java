package nsfw.post.api.yandere;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import nsfw.post.autocomplete.AutocompleteResult;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class YandereAutocompleteSuggestion implements AutocompleteResult {

    private String name;

    private int count;

    @Override
    public String getLabel() {
        return name + " (" + count + ")";
    }

    @Override
    public String getValue() {
        return name;
    }
}
