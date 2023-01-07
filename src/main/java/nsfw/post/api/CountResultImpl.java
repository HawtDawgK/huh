package nsfw.post.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountResultImpl implements CountResult {

    private int count;

    @Override
    public int getCount() {
        return count;
    }
}
