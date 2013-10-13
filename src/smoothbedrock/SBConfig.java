package smoothbedrock;

import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;

public class SBConfig {

	private static boolean _isActive;

	public static boolean isActive() {
		return _isActive;
	}

	private static boolean _spawnDiamonds;

	public static boolean spawnDiamonds() {
		return _spawnDiamonds;
	}

	private static boolean _spawnLava;

	public static boolean spawnLava() {
		return _spawnLava;
	}

	private static boolean _spawnRedstone;

	public static boolean spawnRedstone() {
		return _spawnRedstone;
	}

	private static boolean _spawnLapis;

	public static boolean spawnLapis() {
		return _spawnLapis;
	}

	private static boolean _usedForNether;

	public static boolean usedForNether() {
		return _usedForNether;
	}

	private static boolean _usedForTwilighForest;

	public static boolean usedForTwilighForest() {
		return _usedForTwilighForest;
	}

	private static boolean _usedForGalacticraft;

	public static boolean usedForGalacticraft() {
		return _usedForGalacticraft;
	}

	private static boolean _usedForMineDonalds;

	public static boolean usedForMineDonalds() {
		return _usedForMineDonalds;
	}

	public static boolean Debug() {
		// TODO Auto-generated method stub
		return true;
	}

	public static void readConfig(Configuration config) {
		// loading / creating config

		_isActive = config
				.get("WorldGeneration", "isActive", true, "true, smooths out those annoying bumps of bedrock")
				.getBoolean(true);

		_usedForNether = config.get("WorldGeneration", "usedForNether", true,
				"true, applies in the generation of the nether").getBoolean(true);

		_usedForTwilighForest = config.get("WorldGeneration", "usedForTwilighForest", true,
				"true, applies in the generation of twilight forest").getBoolean(true);

		_usedForGalacticraft = config.get("WorldGeneration", "usedForGalacticraft", true,
				"true, applies in the generation of Galacticcraft Dimensions").getBoolean(true);

		_usedForMineDonalds = config.get("WorldGeneration", "usedForMineDonalds", true,
				"true, applies in the generation of MineDonalds Dimension").getBoolean(true);

		_spawnDiamonds = config.get("WorldGeneration", "spawnDiamonds", true,
				"true, if diamonds (very low chance) should be generated as bedrock alternative.").getBoolean(true);

		_spawnLava = config.get("WorldGeneration", "spawnLava", true,
				"true, if lava should be generated as bedrock alternative").getBoolean(true);

		_spawnRedstone = config.get("WorldGeneration", "spawnRedstone", true,
				"true, if redstone (low chance) should be generated as bedrock alternative.").getBoolean(true);

		_spawnLapis = config.get("WorldGeneration", "spawnLapis", true,
				"true, if lapis (low chance) should be generated as bedrock alternative.").getBoolean(true);

	}

	private static Hashtable<Integer, Boolean> _dimensions = new Hashtable<Integer, Boolean>();
	private static Hashtable<Integer, Integer> _dimensionDefaultBlock = new Hashtable<Integer, Integer>();
	private static Hashtable<Integer, Integer> _dimensionDefaultBlockMeta = new Hashtable<Integer, Integer>();

	public final static Logger Log = Logger.getLogger("SmoothBedrock");

	public static boolean isValidDimension(World world) {
		if (!_dimensions.containsKey(world.provider.dimensionId)) {
			SBConfig.Log.info(String.format("Unknown Dimension detected '%s (%d)', starting analysis",
					world.provider.getDimensionName(), world.provider.dimensionId));

			if (world.provider.dimensionId == -1) {
				_dimensions.put(-1, usedForNether());
				_dimensionDefaultBlock.put(-1, Block.netherrack.blockID);
				_dimensionDefaultBlockMeta.put(-1, 0);
			} else if (world.provider.dimensionId == 0) {
				_dimensions.put(0, true);
				_dimensionDefaultBlock.put(0, Block.stone.blockID);
				_dimensionDefaultBlockMeta.put(0, 0);
			} else if (world.provider.dimensionId == 1) {
				_dimensions.put(1, false);
				_dimensionDefaultBlock.put(1, 0);
				_dimensionDefaultBlockMeta.put(1, 0);
			} else if (world.provider.getDimensionName().equalsIgnoreCase("Twilight Forest")) {

				// Support for Twilight Forest
				_dimensions.put(world.provider.dimensionId, usedForTwilighForest());
			} else if (world.provider.getDimensionName().equalsIgnoreCase("Moon")
					|| world.provider.getDimensionName().equalsIgnoreCase("Mars")) {

				// Support for Galacticcraft
				_dimensions.put(world.provider.dimensionId, usedForGalacticraft());
			} else if (world.provider.getDimensionName().toLowerCase().contains("MineDonalds".toLowerCase())) {

				// Support for MineDonalds
				_dimensions.put(world.provider.dimensionId, usedForMineDonalds());
			} else {

				_dimensions.put(world.provider.dimensionId, false);
			}

		}
		return _dimensions.get(world.provider.dimensionId);
	}

	public static int getDefaultBlockID(World world, int chunkX, int chunkZ) {
		if (!_dimensionDefaultBlock.containsKey(world.provider.dimensionId)) {
			int posX = 0;
			int posZ = 0;
			ItemStack is;
			List<String> namelist = null;

			for (int x = 0; x < 16; x++) {
				for (int z = 0; z < 16; z++) {

					posX = chunkX * 16 + x;
					posZ = chunkZ * 16 + z;

					for (int posY = 5; posY > 0; posY--) {
						try {
							is = new ItemStack(Block.blocksList[world.getBlockId(posX, posY, posZ)], 1,
									world.getBlockMetadata(posX, posY, posZ));

							if (isSearchedBlock(is.getDisplayName())) {
								return setDimensionValues(world, posX, posZ, posY);
							}
						} catch (Exception e) {
						} catch (Throwable t) {
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

	private static boolean isSearchedBlock(String name) {
		return name.contains("Stone");
	}

	private static int setDimensionValues(World world, int posX, int posZ, int posY) {
		_dimensionDefaultBlock.put(world.provider.dimensionId, world.getBlockId(posX, posY, posZ));

		_dimensionDefaultBlockMeta.put(world.provider.dimensionId, world.getBlockMetadata(posX, posY, posZ));

		return _dimensionDefaultBlock.get(world.provider.dimensionId);
	}

	public static int getDefaultBlockMetadata(World world, int chunkX, int chunkZ) {
		if (!_dimensionDefaultBlockMeta.containsKey(world.provider.dimensionId)) {

			getDefaultBlockID(world, chunkX, chunkZ);
		}

		return _dimensionDefaultBlockMeta.get(world.provider.dimensionId);
	}
}