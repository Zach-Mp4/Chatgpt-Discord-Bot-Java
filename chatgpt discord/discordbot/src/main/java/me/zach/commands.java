package me.zach;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;



import org.jetbrains.annotations.NotNull;
public class commands extends ListenerAdapter {
    
   @Override
   public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

    if(event.getName().equals("chat")){
        OptionMapping option = event.getOption("prompt");
        if (option == null) {
            event.reply("It didnt work thats weird").queue();
            
            return;
        }

        event.deferReply().queue();
        String apikey = "replace with openai api key";
            String prompt = option.getAsString();

        OpenAiService openai = new OpenAiService(apikey);
        CompletionRequest completionRequest = CompletionRequest.builder()
        .prompt(prompt)
        .model("text-davinci-003")
        .maxTokens(2048)
        .build();
        
        // Create a new ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        // Redirect System.out to the PrintStream
        PrintStream oldOut = System.out;
        System.setOut(ps);

        // Call the code that prints to System.out
        openai.createCompletion(completionRequest).getChoices().forEach(System.out::println);

        // Restore the old System.out and get the output as a string
        System.out.flush();
        System.setOut(oldOut);
        String output = baos.toString();


        event.getHook().sendMessage(output.substring(22, output.length() - 47)).queue();
    }
    else if (event.getName().equals("image")) {
        OptionMapping img = event.getOption("prompt");
        if (img == null){
            event.reply("It didn't work, that's weird.").queue();
            return;
        }
    
        event.deferReply().queue();
        String apikey = "replace with openai api key";
        String prompt = img.getAsString();
    
        OpenAiService openai = new OpenAiService(apikey);
        CreateImageRequest request = CreateImageRequest.builder()
                .prompt(prompt)
                .n(1)
                .size("1024x1024")
                .build();
    
        ImageResult imageData = null;
        try {
            imageData = openai.createImage(request);
            
        } catch (Exception e) {
            event.getHook().sendMessage("An error occurred while generating the image.").queue();
            e.printStackTrace();
            return;
        }
    
        List<Image> images = imageData.getData();
        if (!images.isEmpty()) {
            Image image = images.get(0);
            String imageUrl = image.getUrl();
            URL url;
            try {
                url = new URL(imageUrl);
                BufferedImage image2 = ImageIO.read(url);
    
                java.io.File outputFile = new java.io.File("output.jpg"); 
                ImageOutputStream outputStream = null;
                try {
                    outputStream = ImageIO.createImageOutputStream(outputFile);
                    ImageIO.write((RenderedImage) image2 , "jpg", outputStream);
                    String channelId = "replace with your channels id";
                    GuildChannel guildChannel = event.getGuild().getGuildChannelById(channelId);
                    if (guildChannel instanceof TextChannel) {
                        

                        

                       
                        try {
                            

                            EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("Image")
                            .setImage(imageUrl);
                          
                            event.getHook().sendMessage("Here is your image").queue();
                            event.getChannel().sendMessageEmbeds(embed.build()).queue();
            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                    } else {
                        event.getHook().sendMessage("That shit aint work").queue();
                    }
                } catch (IOException ex) {
                    event.getHook().sendMessage("An error occurred while writing the image to file.").queue();
                    ex.printStackTrace();
                    return;
                } finally {
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                event.getHook().sendMessage("An error occurred while reading the image data.").queue();
                e.printStackTrace();
                return;
            }
        } else {
            event.getHook().sendMessage("No image data found.").queue();
        }
    }
}
}
