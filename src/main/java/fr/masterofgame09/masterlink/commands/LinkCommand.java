package fr.masterofgame09.masterlink.commands;

import fr.masterofgame09.masterlink.DisLink;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

public class LinkCommand extends ListenerAdapter implements CommandExecutor {

    private String url = "jdbc:sqlite:plugins/MasterLink/data.db";

    private DisLink disLink;
    public LinkCommand(DisLink disLink) {
        this.disLink = disLink;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length != 1){
            sender.sendMessage(disLink.getConfig().getString("prefix").replace("&", "§") + "You must enter the verification code !");
            return false;
        }
        try(Connection connection = DriverManager.getConnection(url)){
            PreparedStatement psmt = connection.prepareStatement("select * from wait_link where code = ? AND name_mc = ?");
            psmt.setString(1, args[0]);
            psmt.setString(2, sender.getName());
            ResultSet rs = psmt.executeQuery();
            if(rs.next()){
                
                long id = rs.getLong("id");
                String name_ds = rs.getString("name_ds");

                sender.sendMessage(disLink.getConfig().getString("prefix").replace("&", "§") + "You are successfully linked !");

                PreparedStatement psmt2 = connection.prepareStatement("INSERT INTO link_id(id, name_mc, name_ds, date) values (?, ?, ?, ?)");

                psmt2.setString(1, String.valueOf(id));
                psmt2.setString(2, sender.getName());
                psmt2.setString(3, name_ds);
                psmt2.setString(4, String.valueOf(System.currentTimeMillis()));
                psmt2.executeUpdate();

                PreparedStatement psmt3 = connection.prepareStatement("DELETE FROM wait_link WHERE name_mc = ? AND code = ?");

                psmt3.setString(1, sender.getName());
                psmt3.setString(2, args[0]);
                psmt3.executeUpdate();

            }else{
                sender.sendMessage(disLink.getConfig().getString("prefix").replace("&", "§") + "You haven't wait a link or the verification code is wrong !");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }


}
