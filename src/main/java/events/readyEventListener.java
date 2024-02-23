package events;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class readyEventListener implements EventListener {
    @Override
    public void onEvent(GenericEvent genericEvent) {
        if(genericEvent instanceof ReadyEvent){
            System.out.println("The bot is ready and online!");


        }
    }
}
