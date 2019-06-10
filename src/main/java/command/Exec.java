/*
 * Copyright (c) 2019 Donovan Nelson
 */

package command;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class Exec extends Command {
    public Exec(){
        identifiers = new String[]{"exec"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] input) {
        if (event.getAuthor().getIdLong() == 270737906644156416L) {
            String testString = "wow it works";
            Binding binding = new Binding();
            binding.setVariable("testString", testString);
            binding.setVariable("jda", event.getJDA());
            binding.setVariable("event", event);
            binding.setVariable("channel", event.getTextChannel());
            binding.setVariable("success", false);
            GroovyShell shell = new GroovyShell(binding);
            shell.evaluate("try{" + input[1] + "success = true} catch (Exception e) {e.printStackTrace()}");

            if((boolean) shell.getVariable("success")){
                event.getMessage().addReaction("\uD83D\uDC4C").queue();
            }
            return (boolean) shell.getVariable("success");
        }
        return false;
    }
}
