package nsfw.post.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nsfw.post.Post;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "posts")
public class PostQueryResult<P extends Post> {

    private int count;

    private int offset;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "post", isAttribute = true)
    private final List<P> posts = new ArrayList<>();

    public List<P> getPosts() {
        return new ArrayList<>(posts);
    }
}
