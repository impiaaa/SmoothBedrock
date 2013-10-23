package smoothbedrock.configs;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class MineDonaldsConfig
{

    public static int dimensionId = -255;

    public static void read (File directory)
    {
        File configFile = new File(directory, "MineDonalds.cfg");
        if (configFile.exists())
        {
            Configuration config = new Configuration(configFile);
            config.load();
            dimensionId = config.get("DimensionID", "Minedonalds Dimension", -255).getInt();
        }
    }
}