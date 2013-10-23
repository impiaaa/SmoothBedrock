package smoothbedrock;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import smoothbedrock.common.SBPacketCoordinates;
import smoothbedrock.configs.SBConfig;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class SBRetroCommand extends CommandBase
{

    private final List commands;

    public SBRetroCommand()
    {
        commands = new ArrayList<String>();
        commands.add("smooth");
    }

    @Override
    public int compareTo (Object arg0)
    {
        return 0;
    }

    @Override
    public String getCommandName ()
    {
        return "smooth";
    }

    @Override
    public String getCommandUsage (ICommandSender icommandsender)
    {
        return String.format("/%s help|<radiusChunks>", getCommandName());
    }

    @Override
    public List getCommandAliases ()
    {
        return commands;
    }

    @Override
    public void processCommand (ICommandSender icommandsender, String[] astring)
    {
        if (!(icommandsender instanceof EntityPlayer))
            return;

        int radius = 4;
        int count = 81;
        ChatMessageComponent cmc = new ChatMessageComponent();
        if (astring.length == 1)
        {
            if (astring[0].equalsIgnoreCase("help"))
            {
                cmc.addText(String.format("[SmoothBedrock] Use '/%s %d' to cleanup the bedrock - 81 chunks.", getCommandName(), radius));
                icommandsender.sendChatToPlayer(cmc);
                return;
            }

            radius = Integer.parseInt(astring[0]);

        }
        else
        {
            cmc.addText(getCommandUsage(icommandsender));
            icommandsender.sendChatToPlayer(cmc);
            return;
        }

        EntityPlayer player = (EntityPlayer) icommandsender;

        int minX = (player.chunkCoordX - radius);
        int maxX = player.chunkCoordX + radius;
        int minZ = player.chunkCoordZ - radius;
        int maxZ = player.chunkCoordZ + radius;
        count = (maxX - minX + 1) * (maxZ - minZ + 1);

        cmc.addText(String.format("[SmoothBedrock] Starting smoothing process - %d Chunks", count));
        icommandsender.sendChatToPlayer(cmc);

        for (int x = minX; x <= maxX; x++)
        {
            for (int z = minZ; z <= maxZ; z++)
            {
                generate(x, z, icommandsender.getEntityWorld().provider.dimensionId, (Player) icommandsender);
            }
        }
        cmc = new ChatMessageComponent();
        cmc.addText(String.format("[SmoothBedrock] Finished smoothing process - %d Chunks", count));
        icommandsender.sendChatToPlayer(cmc);
    }

    @Override
    public boolean canCommandSenderUseCommand (ICommandSender icommandsender)
    {
        return super.canCommandSenderUseCommand(icommandsender)
                && (!icommandsender.getCommandSenderName().equalsIgnoreCase("rcon") && !icommandsender.getCommandSenderName().equalsIgnoreCase("server"));
    }

    @Override
    public List addTabCompletionOptions (ICommandSender icommandsender, String[] astring)
    {
        return null;
    }

    @Override
    public boolean isUsernameIndex (String[] astring, int i)
    {
        return false;
    }

    private void generate (int chunkX, int chunkZ, int dimension, Player player)
    {
        World world = MinecraftServer.getServer().worldServerForDimension(dimension);

        if (world.isRemote || !SBConfig.isActive || world.provider.terrainType == WorldType.FLAT || !SBConfig.isValidDimension(world))
        {
            return;
        }

        boolean isNether = (world.provider.dimensionId == -1);
        int posX = 0;
        int posZ = 0;
        double rand;

        int defaultBlockId = SBConfig.getDefaultBlockID(world, chunkX, chunkZ);
        int defaultBlockMeta = SBConfig.getDefaultBlockMetadata(world, chunkX, chunkZ);

        if (!SBConfig.isValidDimension(world))
        {
            return;
        }

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
                            setBlock(world, posX, posY, posZ, defaultBlockId, defaultBlockMeta, player);
                        }
                    }
                }

                for (int posY = 5; posY > 0; posY--)
                {
                    if (isBedrock(world, posX, posY, posZ))
                    {
                        setBlock(world, posX, posY, posZ, defaultBlockId, defaultBlockMeta, player);
                    }
                }
            }
        }
    }

    private void setBlock (World world, int posX, int posY, int posZ, int blockId, int blockMeta, Player player)
    {
        world.setBlock(posX, posY, posZ, blockId, blockMeta, 3);
        PacketDispatcher.sendPacketToPlayer(new SBPacketCoordinates(blockId, blockMeta, posX, posY, posZ).getPacket(), player);
    }

    private boolean isBedrock (World world, int x, int y, int z)
    {
        return (world.getBlockId(x, y, z) == Block.bedrock.blockID);
    }

}