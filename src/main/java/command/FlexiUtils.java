/*
 * Copyright (c) 2019 Donovan Nelson
 */

package command;

import audio.GuildMusicManager;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import org.discordbots.api.client.DiscordBotListAPI;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class FlexiUtils {

    public static EventWaiter waiter;
    public static DiscordBotListAPI api;
    public static final String PREFIX = ">";
    public static Logger logger;
    public static HashMap<Command, Integer> commands = new HashMap<>();

    public static AudioPlayerManager playerManager;
    public static Map<Long, GuildMusicManager> musicManagers;

    public static synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }
}
