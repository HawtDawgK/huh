package nsfw.post.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class PostMessageCache {

    private static final Map<Long, PostMessage> postMessageMap = new ConcurrentHashMap<>();

    public Map<Long, PostMessage> getPostMessageMap() {
        return postMessageMap;
    }

    public void addPost(long id, PostMessage postMessage) {
        postMessageMap.put(id, postMessage);
    }

    public Optional<Long> findByPostMessageUuid(PostMessage postMessage) {
        return postMessageMap.entrySet().stream()
                .filter(entry -> entry.getValue().getUuid().equals(postMessage.getUuid()))
                .map(Map.Entry::getKey)
                .findFirst();
    }
}
