package com.dunnkers.dunkbot.load.worlds;

import java.net.URL;

import com.dunnkers.dunkbot.load.Configuration;
import com.dunnkers.net.Net;

/**
 * Stores information of a RuneScape world.
 * @author Dunnkers
 */
public class World {

	private final int worldIndex;
	private final boolean member;
	private final int statusCode;
	private final String server;
	private int players;
	private final String country;
	private final String countryCode;
	private final String name;
	private final String activity;

	private final Status status;

	private enum Status {
		ONLINE(0), OFFLINE(1), FULL(2);

		private final int statusCode;

		Status(final int statusCode) {
			this.statusCode = statusCode;
		}

		public int getStatusCode() {
			return statusCode;
		}
	}

	public World(final int worldIndex, final boolean member,
			final int statusCode, final String server, final int players,
			final String country, final String countryCode, final String name,
			final String activity) {
		this.worldIndex = worldIndex;
		this.member = member;
		this.statusCode = statusCode;
		this.server = server;
		this.players = players;
		this.country = country;
		this.countryCode = countryCode;
		this.name = name;
		this.activity = (activity.isEmpty() || activity.equals("-")) ? "" : activity;
		
		Status s = Status.OFFLINE;
		for (Status status : Status.values()) {
			if (status.getStatusCode() == this.statusCode) {
				s = status;
				break;
			}
		}
		this.status = s;
	}

	public int getPlayers() {
		return players;
	}

	public void setPlayers(final int players) {
		this.players = players;
	}

	public int getIndex() {
		return worldIndex;
	}

	public boolean isMemberOnly() {
		return member;
	}

	public String getStatus() {
		return status.name();
	}

	public String getServer() {
		return server;
	}

	public String getCountry() {
		return country;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getName() {
		return name;
	}

	public String getActivity() {
		return activity;
	}

	@Override
	public String toString() {
		return String.format("%s (%s), %d players, %s, %s%s", this.getName(),
				this.getStatus(), this.getPlayers(), this.getCountry(), (this
						.isMemberOnly() ? "Members" : "Free"), (!getActivity()
						.isEmpty() ? ", " + getActivity() : ""));
	}

	public boolean isValid() {
		return status.equals(Status.ONLINE);
	}

	public URL getBaseURL() {
		return Net.URL(this.getBaseURLSpec());
	}

	public String getBaseURLSpec() {
		return Configuration.WORLD_URL_PREFIX + this.getServer()
				+ Configuration.WORLD_URL;
	}

	public String getURLSpec() {
		return getBaseURLSpec() + Configuration.WORLD_U;
	}
}
