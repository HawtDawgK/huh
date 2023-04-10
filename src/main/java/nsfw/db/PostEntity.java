package nsfw.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import nsfw.enums.PostSite;


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
