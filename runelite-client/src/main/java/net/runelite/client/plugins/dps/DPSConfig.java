package net.runelite.client.plugins.dps;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("dps")
public interface DPSConfig extends Config {
    @ConfigItem(
            position = 0,
            keyName = "separateAttackStyle",
            name = "Should different Attack Styles be logged separately",
            description = "Longer version of Should different Attack Styles be logged separately"
    )
    default boolean separateAttackStyle()
    {
        return false;
    }

}
