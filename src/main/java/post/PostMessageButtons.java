package post;

import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;

public class PostMessageButtons {

    public static ActionRow actionRow() {
        return ActionRow.of(Button.primary("next-page", "Next page"),
                        Button.primary("random-page", "Random page"),
                        Button.primary("previous-page", "Previous page"),
                        Button.success("add-favorite", "Add favorite"),
                        Button.danger("delete-message", "Delete message"));
    }
}
