package com.localgamedata;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class LocalGameDataPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(LocalGameDataPlugin.class);
		RuneLite.main(args);
	}
}