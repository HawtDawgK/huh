package post.autocomplete;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AutocompleteResponse {

    private final List<AutocompleteResult> resultList = new ArrayList<>();
}
