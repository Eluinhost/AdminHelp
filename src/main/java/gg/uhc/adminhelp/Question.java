package gg.uhc.adminhelp;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.UUID;

public class Question {

    protected static final String CONSOLE_FORMAT = ChatColor.GRAY + "AdminHelp - [ID %d] %s asks: %s";
    protected static final String REPLY_COMMAND = "/adminhelp:reply %d ";

    protected final String question;
    protected final String askedName;
    protected final UUID asker;
    protected final long asked;
    protected final int id;

    protected final String consoleVersion;
    protected final BaseComponent clickableVersion;

    public Question(String askedName, UUID asker, String question, int id, long asked) {
        this.askedName = askedName;
        this.asker = asker;
        this.question = question;
        this.asked = asked;
        this.id = id;

        this.consoleVersion = String.format(CONSOLE_FORMAT, id, askedName, question);

        clickableVersion = new TextComponent("");
        clickableVersion.setColor(ChatColor.GRAY);

        TextComponent idSection = new TextComponent("[ID " + id + "] ");
        idSection.setBold(true);
        idSection.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, String.format(REPLY_COMMAND, id)));

        TextComponent asksSection = new TextComponent(askedName + " asks: ");
        asksSection.setItalic(true);

        TextComponent delete = new TextComponent(" DEL");
        delete.setBold(true);
        delete.setColor(ChatColor.DARK_RED);
        delete.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format(REPLY_COMMAND, id)));

        clickableVersion.addExtra(idSection);
        clickableVersion.addExtra(asksSection);
        clickableVersion.addExtra(question);
        clickableVersion.addExtra(delete);
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

    public String getConsoleMessage() {
        return consoleVersion;
    }

    public BaseComponent getClickableMessage() {
        return clickableVersion;
    }
}
