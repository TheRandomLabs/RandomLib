package com.therandomlabs.randomlib.config;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;

public class CommandConfigReload extends CommandBase {
	private final String name;
	private final Class<?> configClass;
	private final Runnable runnable;
	private final boolean isClient;
	private final String successMessage;

	public CommandConfigReload(String name, Class<?> configClass, Side side,
			String successMessage) {
		this(name, configClass, null, side, successMessage);
	}

	public CommandConfigReload(String name, Class<?> configClass, Runnable runnable, Side side,
			String successMessage) {
		this.name = name;
		this.configClass = configClass;
		this.runnable = runnable;
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
		ConfigManager.reloadFromDisk(configClass);

		if(runnable != null) {
			runnable.run();
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
