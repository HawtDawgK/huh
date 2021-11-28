package embed;

public class PostNotFoundEmbed extends ErrorEmbed {

    public PostNotFoundEmbed(String tags) {
        super();
        setTitle("No posts found");
        setDescription("No posts found for" + tags);
    }
}
