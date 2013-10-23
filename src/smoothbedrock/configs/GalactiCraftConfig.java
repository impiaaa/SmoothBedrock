package smoothbedrock.configs;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class GalactiCraftConfig
{

    public static int marsDimensionId = -255;
    public static int moonDimensionId = -255;

    public static void read (File directory)
    {
        File configFile = new File(directory, "");
        if (configFile.exists())
        {
            Configuration config = new Configuration(configFile);
            config.load();
            marsDimensionId = config.get("", "", -255).getInt();
            moonDimensionId = config.get("", "", -255).getInt();
        }
    }
}