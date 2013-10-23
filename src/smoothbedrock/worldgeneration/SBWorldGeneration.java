package smoothbedrock.worldgeneration;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import smoothbedrock.configs.SBConfig;
import cpw.mods.fml.common.IWorldGenerator;

public class SBWorldGeneration implements IWorldGenerator
{

    @Override
    public void generate (Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
    {

        if (!SBConfig.isActive || world.provider.terrainType == WorldType.FLAT || !SBConfig.isValidDimension(world))
            return;

        boolean isNether = (world.provider.dimensionId == -1);
        int defaultBlockId = SBConfig.getDefaultBlockID(world, chunkX, chunkZ);
        int defaultBlockMeta = SBConfig.getDefaultBlockMetadata(world, chunkX, chunkZ);

        if (!SBConfig.isValidDimension(world))
            return;

        boolean isDefaultStone = (defaultBlockId == Block.stone.blockID);
        boolean useCustomSpawn = (isDefaultStone && SBConfig.spawnDiamonds && SBConfig.spawnRedstone && SBConfig.spawnLapis && SBConfig.spawnLava && !SBConfig.isProjectRedDetected);
        int blockId = defaultBlockId;
        int blockMeta = defaultBlockMeta;
        int posX = 0;
        int posZ = 0;
        double rand;

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {

                posX = chunkX * 16 + x;
                posZ = chunkZ * 16 + z;

                if (isNether)
                {
                    for (int posY = 128; posY > 121; posY--)
                    {
                        if (isBedrock(world, posX, posY, posZ))
                        {
                            world.setBlock(posX, posY, posZ, defaultBlockId, defaultBlockMeta, 2);
                        }
                    }
                }

                for (int posY = 5; posY > 0; posY--)
                {
                    if (isBedrock(world, posX, posY, posZ))
                    {
                        if (useCustomSpawn)
                        {
                            rand = random.nextDouble();

                            if (isDefaultStone && SBConfig.spawnDiamonds && rand < 0.00001)
                            {
                                blockId = Block.oreDiamond.blockID;
                                blockMeta = 0;
                            }
                            else if (isDefaultStone && SBConfig.spawnRedstone && rand < 0.0002)
                            {
                                blockId = Block.oreRedstone.blockID;
                                blockMeta = 0;
                            }
                            else if (isDefaultStone && SBConfig.spawnLapis && rand < 0.0005)
                            {
                                blockId = Block.oreLapis.blockID;
                                blockMeta = 0;
                            }
                            else if (SBConfig.spawnLava && !SBConfig.isProjectRedDetected && rand < 0.05)
                            {
                                blockId = Block.lavaStill.blockID;
                                blockMeta = 0;
                            }
                            else
                            {
                                blockId = defaultBlockId;
                                blockMeta = defaultBlockMeta;
                            }
                        }

                        try
                        {
                            world.setBlock(posX, posY, posZ, blockId, blockMeta, 2);
                        }
                        catch (Exception ex)
                        {
                            world.setBlock(posX, posY, posZ, defaultBlockId, defaultBlockMeta, 2);
                        }
                    }
                }

            }
        }
    }

    private boolean isBedrock (World world, int x, int y, int z)
    {
        return (world.getBlockId(x, y, z) == Block.bedrock.blockID);
    }
}
