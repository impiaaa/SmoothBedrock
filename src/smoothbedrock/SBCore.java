package smoothbedrock;

import net.minecraftforge.common.Configuration;
import smoothbedrock.common.SBPacketHandler;
import smoothbedrock.common.SBServerProxy;
import smoothbedrock.worldgeneration.SBWorldGeneration;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = SBCore.ModId, version = "@VERSION@", name = SBCore.ModName, dependencies = "required-after:Forge@[9.10.0.800,)")
@NetworkMod(serverSideRequired = true, clientSideRequired = true, packetHandler = SBPacketHandler.class, channels = { SBCore.ModId })
public class SBCore {

	public static final String ModId = "SmoothBedrock";
	public static final String ModName = "Smooth Bedrock";

	@Instance(ModId)
	private static SBCore _instance;

	public static SBCore getInstance() {
		return _instance;
	}

	@SidedProxy(modId = "SmoothBedrock", clientSide = "smoothbedrock.client.SBClientProxy", serverSide = "smoothbedrock.common.SBServerProxy")
	public static SBServerProxy proxy;

	@EventHandler
	public void onPreInitializationEvent(FMLPreInitializationEvent e) {
		_instance = this;
		SBConfig.Log.setParent(FMLLog.getLogger());

		Configuration configFile = new Configuration(e.getSuggestedConfigurationFile());
		SBConfig.readConfig(configFile);
		if (configFile.hasChanged()) {
			configFile.save();
		}
	}

	@EventHandler
	public void onInitializationEvent(FMLInitializationEvent e) {
		if (SBConfig.usedForProjectRed) {
			SBConfig.isProjectRedDetected = Loader.isModLoaded("ProjRed|Exploration")
					|| Loader.isModLoaded("ProjRed|Core");
		}

		if (SBConfig.isActive)
			GameRegistry.registerWorldGenerator(new SBWorldGeneration());
	}

	@EventHandler
	public void onServerStartingEvent(FMLServerStartingEvent event) {
		if (SBConfig.isActive)
			event.registerServerCommand(new SBRetroCommand());
	}
}
