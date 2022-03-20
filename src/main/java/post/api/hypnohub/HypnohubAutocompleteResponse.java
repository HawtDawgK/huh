package post.api.hypnohub;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "tags")
public class HypnohubAutocompleteResponse {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "tag", isAttribute = true)
    private List<HypnohubTag> tags;
}
