package nsfw.post;

import org.javacord.api.entity.message.component.ActionRow;

import java.util.List;

public record DiscordReactionData(PostMessage postMessage, DiscordReactionType actionType, List<ActionRow> actionRows) {

    public ActionRow[] actionRowArray() {
        return actionRows.toArray(new ActionRow[6]);
    }
}
