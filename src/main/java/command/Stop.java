/*
 * Copyright (c) 2019 Donovan Nelson
 */

package command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static command.FlexiUtils.getGuildAudioPlayer;

public class Stop extends Command {
    public Stop() {
        identifiers = new String[]{"stop"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] input) {
        getGuildAudioPlayer(event.getGuild()).scheduler.clear();
        getGuildAudioPlayer(event.getGuild()).scheduler.nextTrack();
        event.getTextChannel().sendMessage("\u23F9 Stopped!").queue();
        return true;
    }
}
