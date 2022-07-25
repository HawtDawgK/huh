package api;

import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ClientWrapper {

    private static final DiscordApi api = new DiscordApiBuilder()
            .setToken(getToken())
            .login().join();

    public static DiscordApi getApi() {
        return api;
    }

    private static String getToken() {
        Properties properties = new Properties();
        InputStream inputResource = ClientWrapper.class.getClassLoader()
                .getResourceAsStream("config.properties");

        try {
            properties.load(inputResource);

            if (!properties.containsKey("token")) {
                log.error("Token not present in config.properties");
                throw new RuntimeException("Token not present in config.properties");
            }

            return properties.getProperty("token");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
