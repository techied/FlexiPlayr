/*
 * Copyright (c) 2019 Donovan Nelson
 */

package command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static command.FlexiUtils.getGuildAudioPlayer;

public class Skip extends Command {
    public Skip() {
        identifiers = new String[]{"skip"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] input) {
        getGuildAudioPlayer(event.getGuild()).scheduler.nextTrack();
        event.getChannel().sendMessage("Skipped track!").queue();
        return true;
    }
}
