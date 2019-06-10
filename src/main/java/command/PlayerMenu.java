package command;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;

public class PlayerMenu extends Command {
    public PlayerMenu() {
        identifiers = new String[]{"menu"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] input) {
        EmbedBuilder embed = new EmbedBuilder().setTitle("Player Menu").setColor(new Color(0x7289da));
        String builder = "\u23EF" +
                " Play/Pause\n" +
                "\u23E9" +
                " Skip\n" +
                "\u23F9" +
                " Stop";
        embed.setDescription(builder);
        Message message = event.getTextChannel().sendMessage(embed.build()).complete();
        message.addReaction("\u23EF").complete();
        message.addReaction("\u23E9").complete();
        message.addReaction("\u23F9").complete();
        return true;
    }
}
