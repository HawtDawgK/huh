package api;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Api {

    private static final DiscordApi API = new DiscordApiBuilder()
            .setToken(getToken())
            .login()
            .join();

    private static String getToken() {
        Properties properties = new Properties();
        InputStream inputResource = Api.class.getClassLoader().getResourceAsStream("config.properties");

        try {
            properties.load(inputResource);

            if (!properties.containsKey("token")) {
                throw new RuntimeException("Token property not present");
            }

            return properties.getProperty("token");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DiscordApi getAPI() {
        return API;
    }
}
