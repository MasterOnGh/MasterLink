package fr.masterofgame09.masterlink.commands;

import fr.masterofgame09.masterlink.DisLink;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class UnlinkCommand implements CommandExecutor {

    private String url = "jdbc:sqlite:plugins/MasterLink/data.db";

    private DisLink disLink;
    public UnlinkCommand(DisLink disLink) {
        this.disLink = disLink;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage(disLink.getConfig().getString("prefix").replace("&", "§") + "You must be a player !");
            return false;
        }

        try(Connection connection = DriverManager.getConnection(url)){
            PreparedStatement psmt = connection.prepareStatement("SELECT * FROM link_id WHERE name_mc = ?");
            psmt.setString(1, sender.getName());
            ResultSet rs = psmt.executeQuery();
            if(rs.next()){
                PreparedStatement psmt2 = connection.prepareStatement("DELETE FROM link_id WHERE name_mc = ?");
                psmt2.setString(1, sender.getName());
                psmt2.executeUpdate();
                sender.sendMessage(disLink.getConfig().getString("prefix").replace("&", "§") + "You are successfully unlinked !");
            }else{

                sender.sendMessage(disLink.getConfig().getString("prefix").replace("&", "§") + "You haven't link !");
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}
