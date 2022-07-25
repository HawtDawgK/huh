package post;

import lombok.experimental.UtilityClass;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.entity.message.component.SelectMenuOption;

import java.util.List;

@UtilityClass
public class PostMessageButtons {

    private static final ActionRow PAGE_ROW = ActionRow.of(Button.primary("next-page", "Next page"),
            Button.primary("random-page", "Random page"),
            Button.primary("previous-page", "Previous page"));

    public static List<HighLevelComponent> actionRow() {
        ActionRow row2 = ActionRow.of(Button.success("add-favorite", "Add favorite"),
                        Button.danger("delete-message", "Delete message"));

        return List.of(PAGE_ROW, row2);
    }

    public static List<HighLevelComponent> actionRowFavorites() {
        ActionRow row2 = ActionRow.of(Button.success("add-favorite", "Favorite"),
                Button.danger("delete-message", "Delete message"),
                Button.danger("delete-favorite", "Remove favorite"));

        return List.of(PAGE_ROW, row2);
    }

}
