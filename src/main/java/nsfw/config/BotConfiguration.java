package nsfw.config;

import lombok.Getter;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Configuration
@PropertySource({"classpath:config.properties"})
public class BotConfiguration {

    @Value("${token}")
    private String token;

    @Bean
    public DiscordApi getDiscordApi() {
        return new DiscordApiBuilder()
                .setToken(token)
                .setWaitForServersOnStartup(true)
                .login().join();
    }
}
