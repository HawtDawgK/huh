package post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.interaction.SlashCommandOptionChoice;
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
@UtilityClass
public class PostApiUtil {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String encodeSpaces(String tags) {
        return tags.replace(' ', '+');
    }

    public static String getLastAutocompleteString(String autocompleteInput) {
        String[] parts = autocompleteInput.split(" ");

        if (parts.length == 0) {
            return "";
        }

        return parts[parts.length - 1];
    }

    public static List<SlashCommandOptionChoice> autocomplete(String urlString, String oldInput) throws AutocompleteException {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).GET().build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new AutocompleteException("Error fetching autocomplete");
            }

            AutocompleteResult[] autocompleteResults = OBJECT_MAPPER.readValue(response.body(), AutocompleteResult[].class);

            return Arrays.stream(autocompleteResults)
                    .map(res -> res.toApplicationCommandOptionChoiceData(oldInput))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new AutocompleteException(e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AutocompleteException(e.getMessage(), e);
        }
    }

}
