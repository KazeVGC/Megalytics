import events.interactionEventListener;
import events.readyEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class discordBot {
    public static void main(String[] args) {
        //This is the LIVE Token. Use this when going live with the changes!
        final String TOKEN = ""; // INSERT TOKEN
    
        //This is the Testbot Token. Only use it for Testing and comment out the first one!
        //final String TOKEN = "MTExMDA5MTQwMTgwMDIwODQzNA.GqZ6cE.NVWlJwDQkJPSl6wQ6cFW1gRAUa6AsmeB2SOzTk";
        JDABuilder jdaBuilder = JDABuilder.createDefault(TOKEN);


        jdaBuilder.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES);

        //JDA jda = jdaBuilder.addEventListeners(new readyEventListener(), new messageEventListener(), new interactionEventListener()).build();
        JDA jda = jdaBuilder.addEventListeners(new readyEventListener(), new interactionEventListener()).build();


        //setGuild kann in production false gesetzt werden
        jda.upsertCommand("gregar-by-coverage", "Top commonly used chips in Gregar folders.").setGuildOnly(true).queue();
        jda.upsertCommand("gregar-by-count", "Top most frequently used chips by Gregar players in total.").setGuildOnly(true).queue();
        jda.upsertCommand("falzar-by-coverage", "Top most commonly used chips Falzar in folders.").setGuildOnly(true).queue();
        jda.upsertCommand("falzar-by-count", "Top most frequently used chips by Falzar players in total.").setGuildOnly(true).queue();
        jda.upsertCommand("top-coverage", "Top most commonly used chips in folders.").setGuildOnly(true).queue();
        jda.upsertCommand("top-count", "Top most frequently used chips in total.").setGuildOnly(true).queue();



    }
}





