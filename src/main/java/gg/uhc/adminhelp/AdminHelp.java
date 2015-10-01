package gg.uhc.adminhelp;

import com.google.common.base.Joiner;
import com.google.common.collect.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminHelp implements CommandExecutor, Listener {

    // permissions
    public static final String ASK_PERMISSION = "uhc.adminhelp.use";
    public static final String REPLY_PERMISSION = "uhc.adminhelp.admin";

    // reply strings
    public static final String NO_PERMISSION = ChatColor.RED + "You do not have permission to use this command";
    public static final String ONLY_PLAYER = ChatColor.RED + "This command can only be ran by a player";
    public static final String ADDED_MESSAGE = ChatColor.AQUA + "Message sent to the admins";
    public static final String REPLY_NOTICE_FORMAT = ChatColor.translateAlternateColorCodes('&', "&8Question ID %d has been answered: %s");
    public static final String INVALID_ID = ChatColor.RED + "Invalid ID %s";
    public static final String REPLY_FORMAT = ChatColor.AQUA + "Admin replies: %s";
    public static final String WAITING_ON_LOGIN = ChatColor.AQUA + "Player not online, will send at next login";
    public static final String REPLY_USAGE = ChatColor.RED + "/reply <id> to clear or /reply <id> <message> to reply";
    public static final String PROVIDE_QUESTION = ChatColor.RED + "You must provide a question to be asked";

    // stores id->unanswered question
    protected final Map<Integer, Question> questions = Maps.newHashMap();

    // messages waiting for player login
    protected final Multimap<UUID, String> waitingReplies = HashMultimap.create();

    // unique ID tracker
    protected int currentIndex = 0;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ask")) {
            return onAsk(sender, args);
        } else  if (command.getName().equalsIgnoreCase("reply")) {
            return onReply(sender, args);
        } else if (command.getName().equalsIgnoreCase("ahlist")) {
            return onList(sender);
        }

        // invalid command
        return false;
    }

    protected boolean onList(CommandSender sender) {
        if (!sender.hasPermission(REPLY_PERMISSION)) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }

        if (questions.size() == 0) {
            sender.sendMessage("No questions are pending");
            return true;
        }

        List<CommandSender> toSend = ImmutableList.of(sender);
        for (Question question : questions.values()) {
            sendClickableQuestionNotice(question, toSend);
        }
        return true;
    }

    protected boolean onAsk(CommandSender sender, String[] args) {
        if (!sender.hasPermission(ASK_PERMISSION)) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ONLY_PLAYER);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(PROVIDE_QUESTION);
            return true;
        }

        Player player = (Player) sender;

        Question question = new Question(player.getDisplayName(), player.getUniqueId(), Joiner.on(" ").join(args), currentIndex++, System.currentTimeMillis());
        questions.put(question.getId(), question);

        sender.sendMessage(ADDED_MESSAGE);
        sendToAdmins(question);
        return true;
    }

    protected boolean onReply(CommandSender sender, String[] args) {
        if (!sender.hasPermission(REPLY_PERMISSION)) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(REPLY_USAGE);
            return true;
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            sender.sendMessage(String.format(INVALID_ID, args[0]));
            return true;
        }

        Question question = questions.get(id);

        if (question == null) {
            sender.sendMessage(String.format(INVALID_ID, id));
            return true;
        }

        questions.remove(question.getId());

        args[0] = "";
        String reply = Joiner.on(" ").join(args);

        sendToAdmins(String.format(REPLY_NOTICE_FORMAT, question.getId(), reply));

        if (reply.length() > 0) {
            Player player = Bukkit.getPlayer(question.getUUID());

            String formatted = String.format(REPLY_FORMAT, reply);

            if (player == null) {
                waitingReplies.put(question.getUUID(), formatted);
                sender.sendMessage(WAITING_ON_LOGIN);
            } else {
                player.sendMessage(formatted);
            }
        }

        return true;
    }

    protected void sendClickableQuestionNotice(Question question, List<CommandSender> senders) {
        for (CommandSender sender : senders) {
            if (sender instanceof Player) {
                ((Player) sender).spigot().sendMessage(question.getClickableMessage());
            } else {
                sender.sendMessage(question.getConsoleMessage());
            }
        }
    }

    protected void sendToAdmins(String message) {
        Bukkit.getConsoleSender().sendMessage(message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(REPLY_PERMISSION)) {
                player.sendMessage(message);
            }
        }
    }

    protected void sendToAdmins(Question question) {
        List<CommandSender> toSend = Lists.<CommandSender>newArrayList(Bukkit.getConsoleSender());

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(REPLY_PERMISSION)) {
                toSend.add(player);
            }
        }

        sendClickableQuestionNotice(question, toSend);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        if (!waitingReplies.containsKey(event.getPlayer().getUniqueId())) return;

        Player player = event.getPlayer();

        for (String message : waitingReplies.removeAll(player.getUniqueId())) {
            player.sendMessage(message);
        }
    }
}
