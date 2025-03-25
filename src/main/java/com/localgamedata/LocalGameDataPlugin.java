package com.localgamedata;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

@Slf4j
@PluginDescriptor(
		name = "Local Game Data",
		description = "Sends local player position data via HTTP locally",
		tags = {"data", "http", "position"}
)
public class LocalGameDataPlugin extends Plugin {
	@Inject
	private Client client;

	private volatile int x = -1;
	private volatile int y = -1;
	private volatile int z = -1;
	private HttpServer server;

	@Override
	protected void startUp() throws Exception {
		// Start the HTTP server on port 8080
		server = HttpServer.create(new InetSocketAddress(8080), 0);
		server.createContext("/position", this::handlePositionRequest);
		server.setExecutor(null); // Use default executor
		server.start();
		log.info("LocalGameData HTTP server started on port 8080");
	}

	@Override
	protected void shutDown() throws Exception {
		// Stop the HTTP server
		if (server != null) {
			server.stop(0);
			log.info("LocalGameData HTTP server stopped");
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		// Update player position on each game tick
		if (client.getLocalPlayer() != null) {
			WorldPoint position = client.getLocalPlayer().getWorldLocation();
			x = position.getX();
			y = position.getY();
			z = position.getPlane();
		}
	}

	private void handlePositionRequest(HttpExchange exchange) throws IOException {
		// Handle HTTP requests to /position
		String response = String.format("{\"x\": %d, \"y\": %d, \"z\": %d}", x, y, z);
		exchange.getResponseHeaders().set("Content-Type", "application/json");
		exchange.sendResponseHeaders(200, response.length());
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(response.getBytes());
		}
	}
}