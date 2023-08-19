package fr.masterofgame09.masterlink.bot;

import fr.masterofgame09.masterlink.DisLink;
import fr.masterofgame09.masterlink.commands.LinkCommand;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

public class BotConnection {



    public BotConnection(DisLink disLink) throws LoginException {

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(disLink.getConfig().getString("discord.Token"));

        builder.setActivity(Activity.playing("By masterproject.tech"));

        builder.setAutoReconnect(true);
        builder.setEnabledIntents(GatewayIntent.getIntents(3243775));

        builder.setMemberCachePolicy(MemberCachePolicy.NONE);
        builder.setChunkingFilter(ChunkingFilter.ALL);

        ShardManager shardManager = builder.build();
        shardManager.addEventListener(new BotListener(disLink), new LinkCommand(disLink));


    }
}
