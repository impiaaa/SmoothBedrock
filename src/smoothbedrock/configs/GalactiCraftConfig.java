package smoothbedrock.configs;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class GalactiCraftConfig
{

    public static int marsDimensionId = -255;
    public static int moonDimensionId = -255;

    public static void read (File directory)
    {
        File configFile = new File(directory, "Galacticraft/moon.conf");
        if (configFile.exists())
        {
            Configuration config = new Configuration(configFile);
            config.load();
            moonDimensionId = config.get("dimensions", "Moon Dimension ID", -255).getInt();
        }

        configFile = new File(directory, "Galacticraft/mars.conf");
        if (configFile.exists())
        {
            Configuration config = new Configuration(configFile);
            config.load();
            marsDimensionId = config.get("dimensions", "Mars Dimension ID", -255).getInt();
        }
    }
}