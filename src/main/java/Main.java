import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import command.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.discordbots.api.client.DiscordBotListAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends ListenerAdapter {

    private static Logger logger;
    private static final String PREFIX = ">";

    public static void main(String[] args) throws Exception {
        if (System.getenv("flexi_token") == null) {
            System.err.println("No token found!");
            System.exit(-1);
        }
        FlexiUtils.commands.put(new Play(), ConnectedState.CONNECTED);
        FlexiUtils.commands.put(new Skip(), ConnectedState.CONNECTED_WITH_BOT);
        FlexiUtils.commands.put(new Exec(), ConnectedState.NOT_CONNECTED);
        FlexiUtils.commands.put(new Clear(), ConnectedState.CONNECTED_WITH_BOT);
        FlexiUtils.commands.put(new Stop(), ConnectedState.CONNECTED_WITH_BOT);
        FlexiUtils.commands.put(new Pause(), ConnectedState.CONNECTED_WITH_BOT);
        FlexiUtils.commands.put(new Resume(), ConnectedState.CONNECTED_WITH_BOT);
        FlexiUtils.commands.put(new Queue(), ConnectedState.NOT_CONNECTED);
        FlexiUtils.commands.put(new Volume(), ConnectedState.CONNECTED_WITH_BOT);
        FlexiUtils.commands.put(new Shuffle(), ConnectedState.CONNECTED_WITH_BOT);
        FlexiUtils.commands.put(new Help(), ConnectedState.NOT_CONNECTED);
        FlexiUtils.waiter = new EventWaiter();
        JDA jda = new JDABuilder(AccountType.BOT)
                .setToken(System.getenv("flexi_token"))
                .addEventListener(new Main())
                .addEventListener(FlexiUtils.waiter)
                .setGame(Game.playing("music for some people"))
                .build().awaitReady();
        jda.getPresence().setGame(Game.playing("music for " + jda.getGuilds().size() + " servers"));
        for (Guild g : jda.getGuilds()) {
            System.out.println(g.getName());
        }
        FlexiUtils.api = new DiscordBotListAPI.Builder()
                .token(System.getenv("dbl_key"))
                .botId("339215794418352129")
                .build();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                FlexiUtils.api.setStats(jda.getGuilds().size());
            }
        }, 10, 300000);
    }

    private Main() {
        FlexiUtils.musicManagers = new HashMap<>();

        FlexiUtils.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(FlexiUtils.playerManager);
        AudioSourceManagers.registerLocalSource(FlexiUtils.playerManager);
        logger = LoggerFactory.getLogger(Main.class);
        logger.warn("JDA initialised");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            logger.info("[PM] " + event.getAuthor().getName() + ": " + event.getMessage().getContentDisplay());
        } else {
            logger.info("[" + event.getGuild().getName() + "] [" + event.getTextChannel().getName() + "] " + event.getMember().getEffectiveName() + ": " + event.getMessage().getContentDisplay());
        }
        String[] msg = event.getMessage().getContentRaw().split("\\s+", 2);
        Guild guild = event.getGuild();

        if (guild != null && !event.getAuthor().isBot() && msg[0].startsWith(PREFIX)) {
            msg[0] = msg[0].substring(PREFIX.length());
            for (Map.Entry<Command, Integer> command : FlexiUtils.commands.entrySet()) {
                for (String identifier : command.getKey().getIdentifiers()) {
                    logger.info(msg[0] + " -> " + identifier);
                    if (identifier.equalsIgnoreCase(msg[0])) {
                        if (command.getValue() == ConnectedState.CONNECTED_WITH_BOT && !event.getMember().getVoiceState().getChannel().equals(event.getGuild().getSelfMember().getVoiceState().getChannel())) {
                            event.getChannel().sendMessage(new EmbedBuilder().setTitle("\u274C You need to be in the voice channel with the bot to perform this command.").build()).queue();
                            return;
                        } else if (command.getValue() == ConnectedState.CONNECTED && !event.getMember().getVoiceState().inVoiceChannel()) {
                            event.getChannel().sendMessage(new EmbedBuilder().setTitle("\u274C You need to be in a voice channel to perform this command.").build()).queue();
                            return;
                        }
                        if (!command.getKey().execute(event, msg)) {
                            event.getChannel().sendMessage("An error occurred while performing this commnad").queue();
                        }
                        return;
                    }
                }
            }
        }

        super.onMessageReceived(event);
    }
}

