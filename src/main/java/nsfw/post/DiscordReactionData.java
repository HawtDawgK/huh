package nsfw.post;

import org.javacord.api.entity.message.component.ActionRow;

import java.util.Arrays;
import java.util.Objects;

public record DiscordReactionData(PostMessage postMessage, DiscordReactionType actionType, ActionRow[] actionRows) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiscordReactionData discordReactionData = (DiscordReactionData) o;

        if (!Objects.equals(postMessage, discordReactionData.postMessage))
            return false;
        if (actionType != discordReactionData.actionType) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(actionRows, discordReactionData.actionRows);
    }

    @Override
    public int hashCode() {
        int result = postMessage != null ? postMessage.hashCode() : 0;
        result = 31 * result + (actionType != null ? actionType.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(actionRows);
        return result;
    }

    @Override
    public String toString() {
        return "Action{" +
                "postMessage=" + postMessage +
                ", actionType=" + actionType +
                ", actionRows=" + Arrays.toString(actionRows) +
                '}';
    }
}
