package moe.exusiaimoe;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ServerListener implements Listener {
    public Server server;
    public Group group;
    public ServerListener(Server server, Group group) {
        this.server = server;
        this.group = group;

        Filter filter = new ConsoleAppender(group);
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(filter);
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        String name = event.getPlayer().getName();
        MessageChain messageChain = new MessageChainBuilder()
                .append(new PlainText("<" + name + "> "+message))
                .build();
        group.sendMessage(messageChain);
        return;
    }


}
