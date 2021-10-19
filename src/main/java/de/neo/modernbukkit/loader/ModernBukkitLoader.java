package de.neo.modernbukkit.loader;

import de.neo.modernbukkit.ModernJavaPlugin;

public class ModernBukkitLoader extends ModernJavaPlugin {

    @Override
    public void onStart() {
        getLogger().info("ModernBukkit loaded.");
    }

}
