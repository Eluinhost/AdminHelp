package gg.uhc.adminhelp;

import java.util.UUID;

public class Question {

    protected final String question;
    protected final String askedName;
    protected final UUID asker;
    protected final long asked;
    protected final int id;

    public Question(String askedName, UUID asker, String question, int id, long asked) {
        this.askedName = askedName;
        this.asker = asker;
        this.question = question;
        this.asked = asked;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public UUID getUUID() {
        return asker;
    }

    public long getWhenAsked() {
        return asked;
    }

    public String getAskedName() {
        return askedName;
    }
}
