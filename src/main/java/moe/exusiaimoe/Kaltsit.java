package moe.exusiaimoe;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.utils.BotConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Kaltsit extends JavaPlugin {
    public static ConfigurationSection config;
    public static Bot bot;
    public static Group group;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        config = this.getConfig();
        class BotThread implements Runnable {
            public Server server;
            public Plugin plugin;

            public BotThread(Server server, Plugin plugin) {
                this.server = server;
                this.plugin = plugin;
            }

            public void run() {
                Kaltsit(server, plugin);
            }
        }
        new Thread(new BotThread(this.getServer(), this)).start();
    }

    public static void Kaltsit(Server server, Plugin plugin) {
        Long qq = config.getLong("qqnumber");
        String password = config.getString("password");

        bot = BotFactory.INSTANCE.newBot(qq, password, new BotConfiguration() {
            {
                fileBasedDeviceInfo();
                redirectBotLogToDirectory(plugin.getDataFolder());
                noNetworkLog();
                setProtocol(MiraiProtocol.ANDROID_PHONE);
                enableContactCache();
            }
        });

        bot.login();

        group = bot.getGroup(config.getLong("group"));

        Bukkit.getServer().getPluginManager().registerEvents(new ServerListener(server, group), plugin);
        bot.getEventChannel().registerListenerHost(new BotListener(server, group, plugin));

        bot.join();
    }
}
