package post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.discordjson.json.ApplicationCommandOptionChoiceData;
import lombok.extern.slf4j.Slf4j;
import post.autocomplete.AutocompleteException;
import post.autocomplete.AutocompleteResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PostApiUtil {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static List<ApplicationCommandOptionChoiceData> autocomplete(String urlString) throws AutocompleteException {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new AutocompleteException("Error fetching autocomplete");
            }

            AutocompleteResult[] autocompleteResults = OBJECT_MAPPER.readValue(response.body(), AutocompleteResult[].class);

            return Arrays.stream(autocompleteResults)
                    .map(AutocompleteResult::toApplicationCommandOptionChoiceData)
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
