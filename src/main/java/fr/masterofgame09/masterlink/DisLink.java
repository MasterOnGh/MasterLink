package fr.masterofgame09.masterlink;

import fr.masterofgame09.masterlink.bot.BotConnection;
import fr.masterofgame09.masterlink.commands.LinkCommand;
import fr.masterofgame09.masterlink.commands.UnlinkCommand;
import fr.masterofgame09.masterlink.db.CreateNewTable;
import fr.masterofgame09.masterlink.db.getConnection;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public final class DisLink extends JavaPlugin {



    @Override
    public void saveDefaultConfig() {
        // Plugin startup logic
        saveResource("config.yml", false);
        super.saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        //verification de la configuration
        File file = new File("plugins/MasterLink/", "data.db");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // sql moments
        getConnection.connect();
        CreateNewTable.createNewTable();

        if(getConfig().getString("discord.Token").equals("") || getConfig().getString("discord.Guild").equals("") || getConfig().getString("discord.Log-channel").equals("")){
          getLogger().severe("Your config.yml is not configured, please configure it before starting the server");
            throw new RuntimeException();
        }

        try {
            BotConnection botConnection = new BotConnection(this);

        } catch (LoginException e) {
            getLogger().severe("Error while connecting to Discord, please check your token in config.yml");
            throw new RuntimeException(e);
        }


        Objects.requireNonNull(getCommand("link")).setExecutor(new LinkCommand(this));
        Objects.requireNonNull(getCommand("unlink")).setExecutor(new UnlinkCommand(this));


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
