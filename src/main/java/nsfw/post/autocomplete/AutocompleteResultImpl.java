package nsfw.post.autocomplete;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutocompleteResultImpl implements AutocompleteResult {

    private String label;

    private String value;

    @Override
    public boolean isJson() {
        return true;
    }
}
