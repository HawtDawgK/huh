package nsfw.post;

import lombok.experimental.UtilityClass;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;

@UtilityClass
public class PostMessageButtons {

    public static ActionRow[] actionRows(boolean isError) {
        ActionRow row1 = ActionRow.of(Button.primary("nextPageId", "Next page"),
                Button.primary("randomPageId", "Random page"),
                Button.primary("previousPageId", "Previous page"));
        ActionRow row2 = ActionRow.of(Button.success("addFavoriteId", "Add favorite", isError),
                Button.danger("deleteMessageId", "Delete message"),
                Button.danger("deleteFavoriteId", "Remove favorite", isError));

        return new ActionRow[]{row1, row2};
    }
}
