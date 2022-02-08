package post;

import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.component.LayoutComponent;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class PostMessageButtons {

    private static final ActionRow PAGE_ROW = ActionRow.of(Button.primary("next-page", "Next page"),
            Button.primary("random-page", "Random page"),
            Button.primary("previous-page", "Previous page"));

    public static List<LayoutComponent> actionRow() {
        ActionRow row2 = ActionRow.of(Button.success("add-favorite", "Add favorite"),
                        Button.danger("delete-message", "Delete message"));

        return List.of(PAGE_ROW, row2);
    }

    public static List<LayoutComponent> actionRowFavorites() {
        ActionRow row2 = ActionRow.of(Button.success("add-favorite", "Favorite"),
                Button.danger("delete-message", "Delete message"),
                Button.danger("delete-favorite", "Remove favorite"));

        return List.of(PAGE_ROW, row2);
    }

}
