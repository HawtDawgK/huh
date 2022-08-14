package nsfw.post;

import nsfw.db.PostEntity;
import org.javacord.api.entity.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring", imports = {Date.class})
public interface PostMapper {

    List<PostResolvableEntry> fromPostEntities(List<PostEntity> postEntities);

    @Mapping(source = "siteName", target = "postSite")
    @Mapping(source = "storedAt", target = "storedAt")
    @Mapping(source = "postId", target = "postId")
    PostResolvableEntry toPostResolvableEntry(PostEntity postEntity);

    @Mapping(source = "postResolvable.postId", target = "postId")
    @Mapping(source = "postResolvable.postSite", target = "siteName")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "storedAt", expression = "java(Date.from(Instant.now()))")
    PostEntity toPostEntity(PostResolvable postResolvable, User user);
}
