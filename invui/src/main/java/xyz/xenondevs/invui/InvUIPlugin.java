package xyz.xenondevs.invui;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.invui.InvUI;

public class InvUIPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        InvUI.getInstance().setPlugin(this);
    }

}
