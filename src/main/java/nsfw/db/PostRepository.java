package nsfw.db;

import nsfw.enums.PostSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    boolean existsByUserIdAndPostIdAndSite(long userId, long postId, PostSite site);

    List<PostEntity> findByUserId(long userId);

}
