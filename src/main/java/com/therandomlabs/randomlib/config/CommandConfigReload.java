package com.therandomlabs.randomlib.config;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

public final class CommandConfigReload extends CommandBase {
	public enum ReloadPhase {
		PRE,
		POST
	}

	@FunctionalInterface
	public interface ConfigReloader {
		void reload(ReloadPhase phase, ICommand command, ICommandSender sender);
	}

	private final String name;
	private final String clientName;
	private final Class<?> configClass;
	private final ConfigReloader reloader;
	private final boolean isClient;
	private final String successMessage;

	private CommandConfigReload(
			String name, String clientName, Class<?> configClass, ConfigReloader reloader,
			Side side, String successMessage
	) {
		this.name = name;
		this.clientName = clientName;
		this.configClass = configClass;
		this.reloader = reloader;
		isClient = side.isClient();
		this.successMessage = successMessage;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		//If successMessage is null it is assumed that the mod is supposed to be installed
		//on both the client and server, implying that a translation key can be used
		return successMessage == null || isClient ? "commands." + name + ".usage" : "/" + name;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
			throws CommandException {
		if(reloader != null) {
			reloader.reload(ReloadPhase.PRE, this, sender);
		}

		ConfigManager.reloadFromDisk(configClass);

		if(reloader != null) {
			reloader.reload(ReloadPhase.POST, this, sender);
		}

		final boolean serverSided = server != null && server.isDedicatedServer();

		if(successMessage != null && serverSided) {
			notifyCommandListener(sender, this, successMessage);
		} else {
			final String actualName = serverSided ? name : clientName;
			sender.sendMessage(new TextComponentTranslation("commands." + actualName + ".success"));
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return isClient ? 0 : 4;
	}

	public static CommandConfigReload client(String name, Class<?> configClass) {
		return client(name, configClass, null);
	}

	public static CommandConfigReload client(
			String name, Class<?> configClass, ConfigReloader reloader
	) {
		return new CommandConfigReload(name, name, configClass, reloader, Side.CLIENT, null);
	}

	public static CommandConfigReload server(String name, String clientName, Class<?> configClass) {
		return server(name, clientName, configClass, null, null);
	}

	public static CommandConfigReload server(
			String name, String clientName, Class<?> configClass, String successMessage
	) {
		return server(name, clientName, configClass, successMessage, null);
	}

	public static CommandConfigReload server(
			String name, String clientName, Class<?> configClass, String successMessage,
			ConfigReloader reloader
	) {
		return new CommandConfigReload(
				name, clientName, configClass, reloader, Side.SERVER, successMessage
		);
	}
}
