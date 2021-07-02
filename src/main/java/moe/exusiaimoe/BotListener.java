package moe.exusiaimoe;

import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberCardChangeEvent;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class BotListener extends SimpleListenerHost {
    private Server server;
    private Group group;
    private Plugin plugin;

    private HashMap<Long, String> id = new HashMap<>();

    public BotListener(Server server, Group group, Plugin plugin) {
        this.server = server;
        this.group = group;
        this.plugin = plugin;
    }

    @EventHandler
    public void onGroupMessageEvent(GroupMessageEvent event) {
        Member sender = event.getSender();
        String message = event.getMessage().contentToString();
        if (group.getId() == event.getGroup().getId()) {
            if (message.startsWith("#")) {
                String name = group.get(sender.getId()).getNameCard();
                server.getOnlinePlayers().forEach(player -> player.sendMessage("<" + name + "> " + message.substring(1)));
                return;
            } else if (message.startsWith("/")) {
                MemberPermission memberPermission = event.getGroup().get(sender.getId()).getPermission();
                if (memberPermission.equals(MemberPermission.ADMINISTRATOR) || memberPermission.equals(MemberPermission.OWNER)) {
                    String command = message.substring(1);
                    if (command.equals("connect")) {
                        TempData.botConsole = true;
                        MessageChain messageChain = new MessageChainBuilder()
                                .append(new PlainText("连接完成"))
                                .build();
                        group.sendMessage(messageChain);
                    } else if (command.equals("disconnect")) {
                        TempData.botConsole = false;
                        MessageChain messageChain = new MessageChainBuilder()
                                .append(new PlainText("断连完成"))
                                .build();
                        group.sendMessage(messageChain);
                    } else {
                        server.getScheduler().runTask(plugin, () -> {
                            server.dispatchCommand(server.getConsoleSender(), command);
                        });

                    }
                    return;
                }
            }
            return;
        } else {
            return;
        }
    }

    @EventHandler
    public void onMemberJoinRequestEvent(MemberJoinRequestEvent event) {
        String answer = event.getMessage().split("：")[2];
        id.put(event.getFromId(), answer);
        event.accept();
    }

    @EventHandler
    public void onMemberJoinEvent(MemberJoinEvent event) {
        NormalMember member = event.getGroup().get(event.getMember().getId());
        member.setNameCard(id.get(event.getMember().getId()));
        return;
    }

    @EventHandler
    public void onMemberCardChangeEvent(MemberCardChangeEvent event) {
        event.getMember().setNameCard(event.getOrigin());
        return;
    }
}
