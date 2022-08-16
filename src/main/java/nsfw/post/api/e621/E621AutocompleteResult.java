package nsfw.post.api.e621;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import nsfw.post.autocomplete.AutocompleteResult;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class E621AutocompleteResult implements AutocompleteResult {

    private String name;

    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public String getValue() {
        return name;
    }
}
