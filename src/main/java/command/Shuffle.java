package command;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Shuffle extends Command {
    public Shuffle() {
        identifiers = new String[]{"shuffle"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] input) {
        List<AudioTrack> queue = new ArrayList<>(FlexiUtils.getGuildAudioPlayer(event.getGuild()).scheduler.getQueue());
        Collections.shuffle(queue);
        FlexiUtils.getGuildAudioPlayer(event.getGuild()).scheduler.setQueue(new LinkedBlockingQueue<>(queue));
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("\uD83D\uDDC3 Queue shuffled!");
        eb.setColor(0x7289da);
        event.getChannel().sendMessage(eb.build()).queue();
        return true;
    }
}
