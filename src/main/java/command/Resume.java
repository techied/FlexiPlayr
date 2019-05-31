package command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import static command.FlexiUtils.getGuildAudioPlayer;

public class Resume extends Command {
    public Resume() {
        identifiers = new String[]{"resume"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] input) {
        getGuildAudioPlayer(event.getGuild()).scheduler.resume();
        return true;
    }
}
