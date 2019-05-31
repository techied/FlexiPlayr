import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import command.*;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

public class Main extends ListenerAdapter {
    private static Logger logger;
    private static ArrayList<Command> commands = new ArrayList<>();

    private static final String PREFIX = ">";

    public static void main(String[] args) throws Exception {
        commands.add(new Play());
        commands.add(new Skip());
        commands.add(new Exec());
        commands.add(new Clear());
        commands.add(new Stop());
        commands.add(new Pause());
        commands.add(new Resume());
        if (System.getenv("flexi_token") == null) {
            System.err.println("No token found!");
            System.exit(-1);
        }
        JDA jda = new JDABuilder(AccountType.BOT)
                .setToken(System.getenv("flexi_token"))
                .addEventListener(new Main())
                .build().awaitReady();
        for (Guild g : jda.getGuilds()) {
            System.out.println(g.getName());
        }
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
            for (Command command : commands) {
                for (String identifier : command.getIdentifiers()) {
                    logger.info(msg[0] + " -> " + identifier);
                    if (identifier.equalsIgnoreCase(msg[0])) {
                        if (!command.execute(event, msg)) {
                            event.getChannel().sendMessage("An error occurred while performing this commnad").queue();
                        }
                    }
                }
            }
        }

        super.onMessageReceived(event);
    }
}
