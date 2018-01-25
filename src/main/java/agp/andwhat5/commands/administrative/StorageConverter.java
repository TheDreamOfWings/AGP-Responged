package agp.andwhat5.commands.administrative;

import agp.andwhat5.AGP;
import agp.andwhat5.commands.Command;
import agp.andwhat5.config.AGPConfig;
import agp.andwhat5.Utils;
import agp.andwhat5.config.structs.DataStruc;
import agp.andwhat5.config.structs.GymStruc;
import agp.andwhat5.config.structs.PlayerStruc;
import agp.andwhat5.storage.FlatFileProvider;
import agp.andwhat5.storage.sql.H2Provider;
import agp.andwhat5.storage.sql.MySQLProvider;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class StorageConverter extends Command
{
	public StorageConverter()
	{
		super("stc", "/stc <flatfile|h2|mysql> <confirm>");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 2)
		{
			if (args[1].equalsIgnoreCase("confirm"))
			{
				List<GymStruc> gymData = DataStruc.gcon.GymData;
				HashMap<UUID, PlayerStruc> playerData = DataStruc.gcon.PlayerData;
				try
				{
					AGP.getInstance().getStorage().shutdown();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				if (!args[0].equalsIgnoreCase(AGPConfig.Storage.storageType))
				{
					if (args[0].equalsIgnoreCase("flatfile"))
					{
						AGP.getInstance().setStorage(new FlatFileProvider());
					} else if (args[0].equalsIgnoreCase("h2"))
					{
						AGP.getInstance().setStorage(new H2Provider(AGPConfig.Storage.GymsTableName, AGPConfig.Storage.BadgesTableName));
					} else if (args[0].equalsIgnoreCase("mysql"))
					{
						AGP.getInstance().setStorage(new MySQLProvider(AGPConfig.Storage.GymsTableName, AGPConfig.Storage.BadgesTableName));
					} else
					{
						throw new CommandException("Invalid storage type");
					}
					try
					{
						AGP.getInstance().getStorage().init();
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					AGP.getInstance().getStorage().updateAllGyms(gymData);
					AGP.getInstance().getStorage().updateAllPlayers(playerData);
					Utils.saveAGPData();
				} else
				{
					sender.sendMessage(Utils.toText("&7This storage type is already in use!", true));
				}
			}
		} else
		{
			super.sendUsage(sender);
		}
	}
}