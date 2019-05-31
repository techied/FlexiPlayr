package command;

import audio.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.concurrent.TimeUnit;

public class Play extends Command {

    public Play() {
        identifiers = new String[]{"play"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] input) {
        if (input.length < 2){
            return false;
        }
        loadAndPlay(event.getTextChannel(), input[1], event.getMember());
        return true;
    }

    private void loadAndPlay(final TextChannel channel, final String trackUrl, Member member) {
        GuildMusicManager musicManager = FlexiUtils.getGuildAudioPlayer(channel.getGuild());

        FlexiUtils.playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Adding track to queue");
                eb.setAuthor("YouTube", null, "https://cdn.discordapp.com/emojis/535586488801558538.png");
                eb.setDescription(track.getInfo().title);
                eb.addField("Track length", String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(track.getInfo().length),
                        TimeUnit.MILLISECONDS.toSeconds(track.getInfo().length) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(track.getInfo().length))
                ), true);
                channel.sendMessage(eb.build()).queue();
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

                play(channel.getGuild(), musicManager, track, member);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

                if (playlist.getTracks().size() == 0) {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Something went wrong trying to play this playlist");
                    eb.setDescription("There might be some songs missing.\nAdd some and try again!");
                    channel.sendMessage(eb.build()).queue();
                    return;
                }

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Adding playlist to queue");
                eb.setAuthor("YouTube", null, "https://cdn.discordapp.com/emojis/535586488801558538.png");
                eb.setDescription(playlist.getName());
                eb.addField("Track count", playlist.getTracks().size() + "", true);
                channel.sendMessage(eb.build()).queue();
                for (AudioTrack track : playlist.getTracks()) {
                    play(channel.getGuild(), musicManager, track, member);
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track, Member member) {
        connectToVoiceChannel(guild.getAudioManager(), member);

        musicManager.scheduler.queue(track);
    }

    private static void connectToVoiceChannel(AudioManager audioManager, Member member) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                for (Member m : voiceChannel.getMembers()) {
                    if (m.equals(member)) {
                        audioManager.openAudioConnection(voiceChannel);
                        return;
                    }
                }
            }
        }
    }
}
