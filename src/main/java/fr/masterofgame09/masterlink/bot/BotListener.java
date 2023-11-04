package fr.masterofgame09.masterlink.bot;

import fr.masterofgame09.masterlink.MasterLink;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class BotListener extends ListenerAdapter {
    private final String url = "jdbc:sqlite:plugins/MasterLink/data.db";
    private final MasterLink masterLink;

    public BotListener(MasterLink masterLink) {
        this.masterLink = masterLink;

    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> data = new ArrayList<>();


        OptionData optionPseudo = new OptionData(OptionType.STRING, "pseudo", "Your minecraft pseudo").setRequired(true);


        data.add(Commands.slash("link", "Link your discord account with your minecraft account").addOptions(optionPseudo));
        data.add(Commands.slash("retrieve", "Retrieve a user with his minecraft pseudo").addOptions(optionPseudo));
        data.add(Commands.slash("ping", "Get the bot latency"));
        //data.add(Commands.slash("help", "Get the bot help"));
        data.add(Commands.slash("server", "Get the server info"));

        event.getGuild().updateCommands().addCommands(data).queue();
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String name = event.getName();
        if (name.equals("ping")) {
            event.reply("The bot latency is `" + event.getJDA().getGatewayPing() + "ms`").queue();
            return;
        }if (name.equals("server")) {
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Server Information")
                    .setDescription("Information about the server\n\n`ONLINE PLAYER` : " + masterLink.getServer().getOnlinePlayers().size())
                    .setTimestamp(event.getTimeCreated());
            event.replyEmbeds(builder.build()).queue();
            return;
        }if(name.equals("link")){

            OptionMapping optionPseudo = event.getOption("pseudo");
            assert optionPseudo != null;

            String pseudo = optionPseudo.getAsString();
            long id = event.getUser().getIdLong();


            try(Connection connection = DriverManager.getConnection(url)){


                PreparedStatement psmt = connection.prepareStatement("SELECT * FROM link_id where id = ?");
                psmt.setString(1, String.valueOf(id));
                ResultSet rs = psmt.executeQuery();
                if(rs.next()){
                    event.reply(masterLink.getConfig().getString("prefix-discord") + " you already have a link").setEphemeral(true).queue();
                    return;
                }

                int CodeGenerate = (int) (Math.random() * (9999 - 1000)) + 1000;


                psmt = connection.prepareStatement("SELECT * FROM wait_link WHERE id = ?");
                psmt.setString(1, String.valueOf(id));
                rs = psmt.executeQuery();
                if(rs.next()){
                    event.reply(masterLink.getConfig().getString("prefix-discord") + " you already have a link request, please wait for the expiration").setEphemeral(true).queue();
                    return;
                }



                psmt = connection.prepareStatement("INSERT INTO wait_link(id, name_mc, name_ds, code) VALUES(?, ?, ?, ?)");
                psmt.setLong(1, id);
                psmt.setString(2, pseudo);
                psmt.setString(3, event.getUser().getName());
                psmt.setString(4, String.valueOf(CodeGenerate));
                psmt.executeUpdate();

                event.reply(masterLink.getConfig().getString("prefix-discord") + " for accept the link, please do `/link " + CodeGenerate + "` in the server").setEphemeral(true).queue();

                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

                Runnable task = () -> {

                    try(Connection connection1 = DriverManager.getConnection(url)){
                        PreparedStatement psmt1 = connection1.prepareStatement("SELECT * FROM wait_link WHERE id = ?");

                        psmt1.setString(1, String.valueOf(id));
                        ResultSet rs1 = psmt1.executeQuery();
                        if (rs1.next()) {
                            event.getChannel().sendMessage(masterLink.getConfig().getString("prefix-discord") + " The link has expired").queue();
                            psmt1.executeUpdate("DELETE FROM wait_link WHERE id = " + id);
                            psmt1.executeUpdate();

                        }
                    } catch (SQLException e) {
                        masterLink.getLogger().log(Level.SEVERE, e.getSQLState());
                    }
                };

                int delay = 60;
                scheduler.schedule(task, delay, TimeUnit.SECONDS);
                scheduler.shutdown();


                return;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }if (name.equals("retrieve")){

            OptionMapping optionPseudo = event.getOption("pseudo");
            assert optionPseudo != null;

            String pseudo = optionPseudo.getAsString();

            try(Connection connection = DriverManager.getConnection(url)) {
                PreparedStatement psmt = connection.prepareStatement("SELECT id, name_mc FROM link_id WHERE name_mc = ?");
                psmt.setString(1, pseudo);
                ResultSet rs = psmt.executeQuery();
                if(rs.next()){
                    event.reply(masterLink.getConfig().getString("prefix-discord") + " The discord account linked to " + pseudo + " is <@" + rs.getString("id") + ">").queue();
                }else{
                    event.reply(masterLink.getConfig().getString("prefix-discord") + " The minecraft account " + pseudo + " is not linked").queue();
                }
            } catch (SQLException e) {
                event.reply(masterLink.getConfig().getString("prefix-discord") + " An error has occurred").queue();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        try(Connection conn = DriverManager.getConnection(url) ) {
            PreparedStatement psmt = conn.prepareStatement("SELECT * FROM link_id WHERE id = ?");
            psmt.setString(1, String.valueOf(event.getUser().getIdLong()));
            ResultSet rs = psmt.executeQuery();
            if(rs.next()){
                psmt = conn.prepareStatement("DELETE FROM link_id WHERE id = ?");
                psmt.setString(1, String.valueOf(event.getUser().getIdLong()));
                psmt.executeUpdate();
                if(Objects.equals(masterLink.getConfig().getString("discord.LikedBan"), "false")){
                    return;
                }else{
                    String commandban = "ban " + rs.getString("name_mc");
                    masterLink.getServer().dispatchCommand(masterLink.getServer().getConsoleSender(),commandban);
                    EmbedBuilder builder = new EmbedBuilder()
                            .setTitle("BAN LOG")
                            .setDescription("The user : **" + event.getUser().getName() + "** has been banned" +
                                    "\n\n" +
                                    "The user is linked with the minecraft account : **" + rs.getString("name_mc") + "** and is also banned on the server")
                            .setFooter("For deasative the linked ban go to config");
                    Objects.requireNonNull(event.getGuild().getChannelById(TextChannel.class, Objects.requireNonNull(masterLink.getConfig().getString("discord.Log-channel")))).sendMessageEmbeds(builder.build()).queue();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
