package smoothbedrock.common;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class SBPacketHandler implements IPacketHandler
{

    private void onBlockUpdate (EntityPlayer player, SBPacketCoordinates packet) throws IOException
    {
        World world = player.worldObj;
        if (packet.targetExists(world))
            return;

        world.markBlockForUpdate(packet.posX, packet.posY, packet.posZ);
    }

    @Override
    public void onPacketData (INetworkManager manager, Packet250CustomPayload packet, Player player)
    {
        DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
        try
        {
            int packetID = data.read();

            switch (packetID)
            {

            case SBPacketIds.BLOCK_UPDATE:
            {
                SBPacketCoordinates bu = new SBPacketCoordinates();
                bu.readData(data);
                onBlockUpdate((EntityPlayer) player, bu);
            }

            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
}