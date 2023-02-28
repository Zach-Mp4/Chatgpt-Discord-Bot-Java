package me.zach;
import net.dv8tion.jda.api.JDABuilder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType; 



public class App 
{
    public static void main(String[] args) throws Exception
    {






        JDA bot = JDABuilder.createDefault("replace with discord api token")
        .setActivity(Activity.playing("With the idea of taking over the world"))
        .addEventListeners(new commands())
        .build().awaitReady();
        
        Guild guild = bot.getGuildById("replace with guild id");

        if(guild != null) {
            guild.upsertCommand("chat", "Ask chat gpt a question or say hello!")
            .addOption(OptionType.STRING, "prompt", "prompt for gpt", true)
            .queue();
            guild.upsertCommand("image", "Ask chat gpt to generate an image!")
            .addOption(OptionType.STRING, "prompt", "prompt for image", true)
            .queue();
        }
        else if (guild == null){
            System.out.println("WHAT HAPPENED");
        }


        

    }
}
