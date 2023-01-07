package nsfw.db;

import lombok.Data;
import lombok.NoArgsConstructor;
import nsfw.enums.PostSite;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "post")
@IdClass(PostEntityKey.class)
public class PostEntity {

    @Id
    private long userId;

    @Id
    private long postId;

    @Id
    @Enumerated(EnumType.STRING)
    private @NonNull PostSite siteName;

    private @NonNull Date storedAt;

}
