package gg.uhc.adminhelp;

import org.bukkit.plugin.java.JavaPlugin;

public class Entry extends JavaPlugin {

    @Override
    public void onEnable() {
        AdminHelp help = new AdminHelp();

        getServer().getPluginManager().registerEvents(help, this);
        getCommand("ask").setExecutor(help);
        getCommand("reply").setExecutor(help);
        getCommand("ahlist").setExecutor(help);
    }
}
