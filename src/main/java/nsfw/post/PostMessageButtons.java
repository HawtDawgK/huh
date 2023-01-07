package nsfw.post;

import lombok.experimental.UtilityClass;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;

@UtilityClass
public class PostMessageButtons {

    public static final ActionRow[] PAGE_ROWS;

    static {
        ActionRow row1 = ActionRow.of(Button.primary("next-page", "Next page"),
                Button.primary("random-page", "Random page"),
                Button.primary("previous-page", "Previous page"));
        ActionRow row2 = ActionRow.of(Button.success("add-favorite", "Add favorite"),
                Button.danger("delete-message", "Delete message"),
                Button.danger("delete-favorite", "Remove favorite"));

        PAGE_ROWS = new ActionRow[]{row1, row2};
    }

}
