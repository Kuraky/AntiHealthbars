package me.kuraky.antihealthbars;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiHealthbars extends JavaPlugin {

    private float customHealth;
    private static AntiHealthbars instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        setCustomHealth(getConfig().getDouble("custom-health"));
        ProtocolLibrary.getProtocolManager().addPacketListener(new HealthbarAdapter());

        PluginCommand reloadCommand = getCommand("ahreload");
        if(reloadCommand != null) reloadCommand.setExecutor(new ReloadCommand());
    }

    public static AntiHealthbars getInstance() {
        return instance;
    }

    public float getCustomHealth() {
        return customHealth;
    }

    public void setCustomHealth(double customHealth) {
        if(customHealth < 0.00001) customHealth = 0.00001;
        this.customHealth = (float) customHealth;
    }
}
