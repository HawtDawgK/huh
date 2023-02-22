package nsfw.post.api.yandere;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nsfw.post.autocomplete.AutocompleteResultImpl;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class YandereAutocompleteResult {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "tag", isAttribute = true)
    private List<YandereAutocompleteSuggestion> tags;
}