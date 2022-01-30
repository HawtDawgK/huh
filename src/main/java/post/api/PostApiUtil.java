package post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import lombok.extern.slf4j.Slf4j;
import post.autocomplete.AutocompleteResult;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PostApiUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static List<ApplicationCommandOptionChoiceData> autocomplete(String urlString) {
        try {
            URL url = new URL(urlString);
            AutocompleteResult[] autocompleteResults = OBJECT_MAPPER.readValue(url, AutocompleteResult[].class);

            return Arrays.stream(autocompleteResults)
                    .map(AutocompleteResult::toApplicationCommandOptionChoiceData)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }



}
