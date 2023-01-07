package nsfw.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nsfw.post.api.PostQueryResult;
import nsfw.post.api.generic.GenericPost;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostQueryResultImpl implements PostQueryResult {

    private int count;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "post", isAttribute = true)
    private List<GenericPost> posts;

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public List<Post> getPosts() {
        return new ArrayList<>(posts);
    }
}
