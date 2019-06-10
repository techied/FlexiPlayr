/*
 * Copyright (c) 2019 Donovan Nelson
 */

package command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static command.FlexiUtils.getGuildAudioPlayer;

public class Pause extends Command {
    public Pause(){
        identifiers = new String[]{"pause"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] input) {
        getGuildAudioPlayer(event.getGuild()).scheduler.pause();
        event.getTextChannel().sendMessage("\u23F8 Paused the player!").queue();
        return true;
    }
}
