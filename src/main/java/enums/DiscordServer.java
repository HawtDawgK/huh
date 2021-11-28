package enums;

public enum DiscordServer {

    BIRD_UP_EMPIRE(594542271437864988L),
    CODERS_CLUB(585850878532124672L);

    private final long id;

    DiscordServer(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
