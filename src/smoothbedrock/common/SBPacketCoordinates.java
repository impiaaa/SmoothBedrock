package smoothbedrock.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class SBPacketCoordinates extends SBPacket {

	private int id;

	public int posX;
	public int posY;
	public int posZ;
	public int blockId;
	public int blockMeta;

	public SBPacketCoordinates() {
	}

	public SBPacketCoordinates(int blockId, int blockMeta, int x, int y, int z) {
		this.id = SBPacketIds.BLOCK_UPDATE;

		this.blockId = blockId;
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.blockMeta = blockMeta;
	}

	@Override
	public void writeData(DataOutputStream data) throws IOException {
		data.writeInt(posX);
		data.writeInt(posY);
		data.writeInt(posZ);
		data.writeInt(blockId);
		data.writeInt(blockMeta);
	}

	@Override
	public void readData(DataInputStream data) throws IOException {
		posX = data.readInt();
		posY = data.readInt();
		posZ = data.readInt();
		blockId = data.readInt();
		blockMeta = data.readInt();
	}

	@Override
	public int getID() {
		return id;
	}

	public boolean targetExists(World world) {
		return world.blockExists(posX, posY, posZ);
	}

	public Block getBlock(World world) {
		return Block.blocksList[world.getBlockId(posX, posY, posZ)];
	}
}