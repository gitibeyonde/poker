package com.poker.client;

import java.util.Enumeration;
import java.util.Hashtable;


public class ShillServerData {
	//configurable parameters of the shill server
	public String ip = "";

	public Hashtable playersData = null;

	public ShillServerData() {
		playersData = new Hashtable();
	}

	public ShillServerData(Hashtable data) {
		playersData = new Hashtable();
		//look for bot count
		String botc = (String) data.get("botc");
		if (botc == null) { //single player data
			String botStr = (String) data.get("bot");
			addNewPlayer(botStr);
		} else {
			int botCount = Integer.parseInt(botc);
			if (botCount == 0) {
				return;
			}
			String botStr = (String) data.get("bots");
			String[] botd = botStr.split(",");
			for (int i = 0; i < botd.length; i++) {
				addNewPlayer(botd[i]);
			}
		}
	}

	public String addNewPlayer(String data) {
		if (data == null) {
			return null;
		}
		PlayerData bot_data = new PlayerData(data);
		playersData.put(bot_data.uid, bot_data);
		return bot_data.uid;
	}

	public String modifyPlayer(String data) {
		if (data == null) {
			return null;
		}
		String nv[] = data.split("|");
		if (nv.length > 0) {
			PlayerData pd = (PlayerData) playersData.get(nv[0]);
			if (pd == null) {
				return null;
			}
			pd.refresh(data);
			return nv[0];
		} else {
			return null;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("ss=");
		int tmp_nosPlrs = playersData.size();
		sb.append(ip).append("&botc=").append(tmp_nosPlrs);
		if (tmp_nosPlrs > 0) {
			sb.append("&bots=");
		}
		Enumeration enumer = playersData.keys();
		int count = 0;
		for (; enumer.hasMoreElements();) {
			if ((count++) > 0) {
				sb.append(",");
			}
			PlayerData pd = (PlayerData) playersData.get((String) enumer
					.nextElement());
			sb.append(pd.toString());
		}

		return sb.toString();
	}

	public PlayerData getPlayer(String uid) {
		return (PlayerData) playersData.get(uid);
	}

	public String toStringPlayerdata(String uid) {
		if (uid == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder("ss=");
		sb.append(ip).append(",&bot=");
		PlayerData pd = (PlayerData) playersData.get(uid);
		sb.append(pd.toString());
		return sb.toString();
	}

	public String toDisplayString() {
		//System.out.println("STARTED srvData.toDisplayString()");
		StringBuilder sb = new StringBuilder("Shill Server - ");
		sb.append(ip);
		Enumeration enumer = playersData.keys();
		for (; enumer.hasMoreElements();) {
			PlayerData pd = (PlayerData) playersData.get((String) enumer
					.nextElement());
			sb.append(pd.toPlayerDisplayString());
		}
		return sb.toString();
	}

	public String toPlayerDisplayString(String bot_uid) {
		StringBuilder sb = new StringBuilder("Shill Server - ");
		sb.append(ip);
		PlayerData pd = (PlayerData) playersData.get(bot_uid);
		sb.append(pd.toPlayerDisplayString());
		return sb.toString();
	}

	public class PlayerData {
		public String uid = null;

		public int iq = ShillConstants.IQ_BEGINER_0;

		public int gameTypes = ShillConstants.ALL_GAMES;

		public int preferedGames = ShillConstants.TEXAS_HOLDEM;

		public int gameCount;

		public float loss_ceil, win_ceil;

		public int delay, idleCount, sitoutCount;

		public int gamesPlayed, currIdleCnt, currSitoutCnt;

		public int tid, pos;

		public int state;

		public String botState = "";

		public String preGameString = "";

		public String botLevelString = "";

		public PlayerData(String data) {
			refresh(data);
		}

		public void refresh(String data) {
			//System.out.println("data = " + data);
			String pd[] = data.split("\\|");
			uid = pd[0];
			iq = Integer.parseInt(pd[1]);
			gameTypes = Integer.parseInt(pd[2]);
			preferedGames = Integer.parseInt(pd[3]);
			gameCount = Integer.parseInt(pd[4]);
			loss_ceil = Float.parseFloat(pd[5]);
			win_ceil = Float.parseFloat(pd[6]);
			delay = Integer.parseInt(pd[7]);
			idleCount = Integer.parseInt(pd[8]);
			sitoutCount = Integer.parseInt(pd[9]);
			gamesPlayed = Integer.parseInt(pd[10]);
			//currIdleCnt = Integer.parseInt(pd[11]);
			//currSitoutCnt = Integer.parseInt(pd[12]);
			state = Integer.parseInt(pd[13]);
			/*
			 * public static final int PLR_DISCONNECTED = 1; public static final
			 * int PLR_CONNECTING = 2; public static final int PLR_CONNECTED =
			 * 3; public static final int PLR_LOGGING = 4; public static final
			 * int PLR_LOGGED = 5; //PLR_IDLE - earlier public static final int
			 * PLR_REQUESTING_TL = 6; public static final int
			 * PLR_NEEDS_REGISTRATION = 7; public static final int
			 * PLR_REGISTERING = 8; public static final int PLR_REQ_OBSERVE = 9;
			 * public static final int PLR_OBSERVING = 10; public static final
			 * int PLR_JOINING = 11; public static final int PLR_JOINED = 12;
			 * //public static final int PLR_ACTIVE = 11; public static final
			 * int PLR_SITOUT = 13;
			 */

			switch (state) {
			case 1:
				botState = "Disconnected";
				break;
			case 2:
				botState = "Connecting";
				break;
			case 3:
				botState = "Connected";
				break;
			case 4:
				botState = "Logging";
				break;
			case 5:
				botState = "Logged";
				break;
			case 6:
				botState = "Requesting_TL";
				break;
			case 7:
				botState = "Needs_Registration";
				break;
			case 8:
				botState = "Registering";
				break;
			case 9:
				botState = "Req_Observe";
				break;
			case 10:
				botState = "Observing";
				break;
			case 11:
				botState = "Joining";
				break;
			case 12:
				botState = "Joined";
				break;
			case 13:
				botState = "Sitout";
				break;
			default:
				botState = "Disconnected";

			}

			switch (preferedGames) {
			case 1:
				preGameString = "Texas_Holdem";
				break;
			case 2:
				preGameString = "Omaha_Hi";
				break;
			case 4:
				preGameString = "Omaha_Hilo";
				break;
			case 8:
				preGameString = "Sevenstud_Hi";
				break;
			case 16:
				preGameString = "Sevenstud_Hilo";
			}

			switch (iq) {
			case 1:
				botLevelString = "Fish Bot";
				break;
			case 2:
				botLevelString = "Calling Bot";
				break;
			case 3:
				botLevelString = "Loose Bot";
				break;
			case 4:
				botLevelString = "Rock Bot";
				break;
			case 5:
				botLevelString = "Winning Bot";
			}

			//dead = Boolean.getBoolean(pd[14]);
			tid = Integer.parseInt(pd[15]);
			pos = Integer.parseInt(pd[16]);

		}

		public String toPlayerDisplayString() {
			StringBuilder sb = new StringBuilder();
			sb
					.append("\n\rBot id                   = ")
					.append(uid)
					.append("\n\rBot IQ                     = ")
					.append(iq)
					.append("\n\rGames the Bot can Play     = ")
					.append(
							((gameTypes & ShillConstants.TEXAS_HOLDEM) > 0) ? "TEXAS HOLDEM "
									: "")
					.append(
							((gameTypes & ShillConstants.OMAHA_HI) > 0) ? "OMAHA HI "
									: "")
					.append(
							((gameTypes & ShillConstants.OMAHA_HILO) > 0) ? "OMAHA HI/LO "
									: "")
					.append(
							((gameTypes & ShillConstants.SEVENSTUD_HI) > 0) ? "SEVENSTUD HI "
									: "")
					.append(
							((gameTypes & ShillConstants.SEVENSTUD_HILO) > 0) ? "SEVENSTUD HI/LO "
									: "")
					.append("\n\rBots Prefered games   = ")
					.append(
							((preferedGames &ShillConstants.TEXAS_HOLDEM) > 0) ? "TEXAS HOLDEM "
									: "")
					.append(
							((preferedGames & ShillConstants.OMAHA_HI) > 0) ? "OMAHA HI "
									: "")
					.append(
							((preferedGames & ShillConstants.OMAHA_HILO) > 0) ? "OMAHA HI/LO "
									: "")
					.append(
							((preferedGames & ShillConstants.SEVENSTUD_HI) > 0) ? "SEVENSTUD HI "
									: "")
					.append(
							((preferedGames & ShillConstants.SEVENSTUD_HILO) > 0) ? "SEVENSTUD HI/LO "
									: "").append(
							"\n\rMax Games the Bot can play = ").append(
							gameCount).append(
							"\n\rBot's Loss ceiling         = ").append(
							loss_ceil).append(
							"\n\rBot's Win ceiling          = ").append(
							win_ceil).append(
							"\n\rBot's Descision delay      = ").append(delay)
					.append("\n\rBot's Idle Count           = ").append(
							idleCount).append(
							"\n\rBot's Sitout count         = ").append(
							sitoutCount).append(
							"\n\rBot's State                = ").append(
							botState).append(
							"\n\rBot's tableid              = ").append(tid)
					.append("\n\rBot's position on table    = ").append(pos);
			return sb.toString();
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(uid).append("|").append(iq).append("|").append(gameTypes)
					.append("|").append(preferedGames).append("|").append(
							gameCount).append("|").append(loss_ceil)
					.append("|").append(win_ceil).append("|").append(delay)
					.append("|").append(idleCount).append("|").append(
							sitoutCount);

			return sb.toString();
		}
	}
}
