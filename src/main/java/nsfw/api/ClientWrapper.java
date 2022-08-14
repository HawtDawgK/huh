package nsfw.api;

import lombok.RequiredArgsConstructor;
import nsfw.config.BotConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientWrapper {

    private final BotConfiguration botConfiguration;

    private DiscordApi api;

    @PostConstruct
    public void init() {
        api = new DiscordApiBuilder()
                .setToken(botConfiguration.getToken())
                .setWaitForServersOnStartup(true)
                .login().join();
        log.warn("STARTUP");
    }

    public DiscordApi getApi() {
        return api;
    }

}
