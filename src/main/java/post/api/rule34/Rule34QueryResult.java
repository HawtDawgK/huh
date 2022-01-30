package post.api.rule34;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import post.PostQueryResult;

import java.util.List;

@Getter
@JacksonXmlRootElement(localName = "posts")
public class Rule34QueryResult extends PostQueryResult {

    private int count;

    private long offset;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "post", isAttribute = true)
    private List<Rule34Post> posts;

}
