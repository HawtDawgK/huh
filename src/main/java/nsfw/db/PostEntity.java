package nsfw.db;

import lombok.Data;
import lombok.NoArgsConstructor;
import nsfw.enums.PostSite;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@IdClass(PostEntity.class)
@Table(name = "post")
public class PostEntity implements Serializable {

    @Id
    private long userId;

    @Id
    private long postId;

    @Id
    private PostSite site;

}
