package smoothbedrock.configs;

import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;

public class SBConfig
{
    public static boolean isActive;
    public static boolean spawnDiamonds;
    public static boolean spawnRedstone;
    public static boolean spawnLapis;
    public static boolean usedForNether;
    public static boolean usedForTwilighForest;
    public static boolean usedForGalacticraft;
    public static boolean usedForMineDonalds;

    public static void readConfig (Configuration config)
    {
        isActive = config.get("WorldGeneration", "isActive", true, "true, smooths out those annoying bumps of bedrock").getBoolean(true);

        usedForNether = config.get("WorldGeneration", "usedForNether", true, "true, applies in the generation of the nether").getBoolean(true);

        usedForTwilighForest = config.get("WorldGeneration", "usedForTwilighForest", true, "true, applies in the generation of twilight forest").getBoolean(true);

        usedForGalacticraft = config.get("WorldGeneration", "usedForGalacticraft", true, "true, applies in the generation of Galacticcraft Dimensions").getBoolean(true);

        usedForMineDonalds = config.get("WorldGeneration", "usedForMineDonalds", true, "true, applies in the generation of MineDonalds Dimension").getBoolean(true);

        spawnDiamonds = config.get("WorldGeneration", "spawnDiamonds", true, "true, if diamonds (very low chance) should be generated as bedrock alternative.").getBoolean(true);

        spawnRedstone = config.get("WorldGeneration", "spawnRedstone", true, "true, if redstone (low chance) should be generated as bedrock alternative.").getBoolean(true);

        spawnLapis = config.get("WorldGeneration", "spawnLapis", true, "true, if lapis (low chance) should be generated as bedrock alternative.").getBoolean(true);

        registerDimensions();

    }

    private static Hashtable<Integer, Boolean> _dimensions = new Hashtable<Integer, Boolean>();
    private static Hashtable<Integer, Integer> _dimensionDefaultBlock = new Hashtable<Integer, Integer>();
    private static Hashtable<Integer, Integer> _dimensionDefaultBlockMeta = new Hashtable<Integer, Integer>();

    public final static Logger Log = Logger.getLogger("SmoothBedrock");

    private static void registerDimensions ()
    {
        _dimensions.put(-1, usedForNether);
        _dimensionDefaultBlock.put(-1, Block.netherrack.blockID);
        _dimensionDefaultBlockMeta.put(-1, 0);

        _dimensions.put(0, true);
        _dimensionDefaultBlock.put(0, Block.stone.blockID);
        _dimensionDefaultBlockMeta.put(0, 0);

        _dimensions.put(1, false);
        _dimensionDefaultBlock.put(1, 0);
        _dimensionDefaultBlockMeta.put(1, 0);

        // Twilight Forest support
        if (TwilightForestConfig.dimensionId != -255)
            _dimensions.put(TwilightForestConfig.dimensionId, usedForTwilighForest);

        // GalactiCraft support
        if (GalactiCraftConfig.marsDimensionId != -255)
            _dimensions.put(GalactiCraftConfig.marsDimensionId, usedForGalacticraft);
        if (GalactiCraftConfig.moonDimensionId != -255)
            _dimensions.put(GalactiCraftConfig.moonDimensionId, usedForGalacticraft);

        // MineDonalds support
        if (MineDonaldsConfig.dimensionId != -255)
            _dimensions.put(MineDonaldsConfig.dimensionId, usedForMineDonalds);
    }

    public static boolean isValidDimension (World world)
    {
        if (!_dimensions.containsKey(world.provider.dimensionId))
        {
            _dimensions.put(world.provider.dimensionId, false);
        }
        return _dimensions.get(world.provider.dimensionId);
    }

    public static int getDefaultBlockID (World world, int chunkX, int chunkZ)
    {
        if (!_dimensionDefaultBlock.containsKey(world.provider.dimensionId))
        {
            int posX = 0;
            int posZ = 0;
            ItemStack is;
            List<String> namelist = null;

            for (int x = 0; x < 16; x++)
            {
                for (int z = 0; z < 16; z++)
                {

                    posX = chunkX * 16 + x;
                    posZ = chunkZ * 16 + z;

                    for (int posY = 5; posY > 0; posY--)
                    {
                        try
                        {
                            is = new ItemStack(Block.blocksList[world.getBlockId(posX, posY, posZ)], 1, world.getBlockMetadata(posX, posY, posZ));

                            if (isSearchedBlock(is.getDisplayName()))
                            {
                                return setDimensionValues(world, posX, posZ, posY);
                            }
                        }
                        catch (Exception e)
                        {
                        }
                        catch (Throwable t)
                        {
                        }
                    }
                }
            }
            _dimensions.put(world.provider.dimensionId, false);
            _dimensionDefaultBlock.put(world.provider.dimensionId, 0);
            _dimensionDefaultBlockMeta.put(world.provider.dimensionId, 0);
        }

        return _dimensionDefaultBlock.get(world.provider.dimensionId);
    }

    private static boolean isSearchedBlock (String name)
    {
        return name.contains("Stone");
    }

    private static int setDimensionValues (World world, int posX, int posZ, int posY)
    {
        _dimensionDefaultBlock.put(world.provider.dimensionId, world.getBlockId(posX, posY, posZ));
        _dimensionDefaultBlockMeta.put(world.provider.dimensionId, world.getBlockMetadata(posX, posY, posZ));

        return _dimensionDefaultBlock.get(world.provider.dimensionId);
    }

    public static int getDefaultBlockMetadata (World world, int chunkX, int chunkZ)
    {
        if (!_dimensionDefaultBlockMeta.containsKey(world.provider.dimensionId))
        {
            getDefaultBlockID(world, chunkX, chunkZ);
        }
        return _dimensionDefaultBlockMeta.get(world.provider.dimensionId);
    }
}