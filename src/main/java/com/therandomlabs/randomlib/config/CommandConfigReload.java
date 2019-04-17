package com.therandomlabs.randomlib.config;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

public class CommandConfigReload extends CommandBase {
	public enum ReloadPhase {
		PRE,
		POST
	}

	@FunctionalInterface
	public interface ConfigReloader {
		void reload(ReloadPhase phase, ICommand command, ICommandSender sender);
	}

	private final String name;
	private final Class<?> configClass;
	private final ConfigReloader reloader;
	private final boolean isClient;
	private final String successMessage;

	public CommandConfigReload(String name, Class<?> configClass, Side side) {
		this(name, configClass, side, null);
	}

	public CommandConfigReload(String name, Class<?> configClass, Side side,
			String successMessage) {
		this(name, configClass, null, side, successMessage);
	}

	public CommandConfigReload(String name, Class<?> configClass,
			ConfigReloader reloader, Side side) {
		this(name, configClass, reloader, side, null);
	}

	public CommandConfigReload(String name, Class<?> configClass, ConfigReloader reloader,
			Side side, String successMessage) {
		this.name = name;
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

		if(successMessage != null && server != null && server.isDedicatedServer()) {
			notifyCommandListener(sender, this, successMessage);
		} else {
			sender.sendMessage(new TextComponentTranslation("commands." + name + ".success"));
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return isClient ? 0 : 4;
	}
}
