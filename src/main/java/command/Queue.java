/*
 * Copyright (c) 2019 Donovan Nelson
 */

package command;

import com.jagrosh.jdautilities.menu.Paginator;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Queue extends Command {
    public Queue() {
        identifiers = new String[]{"queue", "q"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] input) {
        Object[] queue = FlexiUtils.getGuildAudioPlayer(event.getGuild()).scheduler.getQueue().toArray();
        if(queue.length == 0){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(new Color(0x7289da));
            eb.setTitle("No queue available");
            event.getChannel().sendMessage(eb.build()).queue();
            return true;
        }
        String[] items = new String[queue.length];
        for (int i = 0; i < items.length; i++) {
            AudioTrack track = (AudioTrack) queue[i];
            items[i] = track.getInfo().title + " (" + String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(track.getInfo().length),
                    TimeUnit.MILLISECONDS.toSeconds(track.getInfo().length) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(track.getInfo().length))
            ) + ")";
        }
        Paginator paginator = new Paginator.Builder().setText("Here's your queue:").addItems(items).setColor(new Color(0x7289da)).setItemsPerPage(5).setBulkSkipNumber(3).setEventWaiter(FlexiUtils.waiter).build();
        paginator.display(event.getTextChannel());
        return true;
    }
}