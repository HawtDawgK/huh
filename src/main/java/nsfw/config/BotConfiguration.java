package nsfw.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.Getter;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Configuration
@PropertySource({"classpath:config.properties"})
public class BotConfiguration {

    @Value("${token}")
    private String token;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public XmlMapper xmlMapper() {
        return new XmlMapper();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

    @Bean
    public DiscordApi discordApi() {
        return new DiscordApiBuilder()
                .setToken(token)
                .setWaitForServersOnStartup(true)
                .login().join();
    }

}
