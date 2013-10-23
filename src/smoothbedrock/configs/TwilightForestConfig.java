package smoothbedrock.configs;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class TwilightForestConfig
{

    public static int dimensionId = -255;

    public static void read (File directory)
    {
        File confiFile = new File(directory, "TwilightForest.cfg");
        if (confiFile.exists())
        {
            Configuration config = new Configuration(confiFile);
            config.load();

            dimensionId = config.get("dimension", "dimensionID", -255).getInt();
        }
    }
}