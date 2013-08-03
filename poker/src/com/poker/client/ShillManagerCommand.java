package com.poker.client;

public class ShillManagerCommand {
  public int cmd = 0;
  public boolean all_flag = true;
  public String ip = null;
  public int cmd_delay = 0;
  public String gid = null;
  public int gameTypes = 0;
  public int pref_gameTypes = 0;
  public int iq = 0;
  public String uid = null;
  public int sitOutVal = 0;
  public int idleVal = 0;

  //int maxWin = 0;
  //int maxLoss = 0;
  public float maxWin = 0;
  public float maxLoss = 0;
  public int gamesPerSession = 0;

  public ShillManagerCommand() {
    cmd = 0;
    all_flag = true;
    ip = null;
    cmd_delay = 0;
    gid = null;
    gamesPerSession = 0;
    gameTypes = 0;
    pref_gameTypes = 0;
    iq = 0;
    uid = null;
    sitOutVal = 0;
    idleVal = 0;
    maxWin = 0;
    maxLoss = 0;
  }

  public ShillManagerCommand(String cmdString) {
    parseData(cmdString);
  }

  public String toString() {
    StringBuilder command = new StringBuilder();
    String ip = null;
    if (cmd == ShillConstants.STOP) {
      command.append("cmd=").append("stop");
      if (cmd_delay > 0) {
        command.append("&delay=").append(cmd_delay);
      }
    }
    else if (cmd == ShillConstants.STATUS) {
      command.append("cmd=").append("status");
      if (uid != null && uid.length() > 0) {
        command.append("&uid=").append(uid);
      }
    }
    else if (cmd == ShillConstants.ADD_BOT) {
      command.append("cmd=").append("addbot");
      if (gid != null && gid.length() > 0) {
        command.append("&gid=").append(gid);
      }
      if (gameTypes > 0) {
        command.append("&gtypes=").append(gameTypes);
      }
      if (pref_gameTypes > 0) {
        command.append("&pgtypes=").append(pref_gameTypes);
      }
      if (gamesPerSession > 0) {
        command.append("&gpsession=").append(gamesPerSession);
      }
      if (iq > 0) {
        command.append("&iq=").append(iq);
      }
      if (uid != null && uid.length() > 0) {
        command.append("&uid=").append(uid);
      }
      if (idleVal > 0) {
        command.append("&iv=").append(idleVal);
      }
      if (sitOutVal > 0) {
        command.append("&sov=").append(sitOutVal);
      }
      if (maxLoss > 0) {
        command.append("&maxloss=").append(maxLoss);
      }
      if (maxWin > 0) {
        command.append("&maxwin=").append(maxWin);
      }

    }
    else if (cmd == ShillConstants.MODIFY_BOT) {
      command.append("cmd=").append("modbot");
      if (gid != null && gid.length() > 0) {
        command.append("&gid=").append(gid);
      }
      if (gameTypes > 0) {
        command.append("&gtypes=").append(gameTypes);
      }
      if (pref_gameTypes > 0) {
        command.append("&pgtypes=").append(pref_gameTypes);
      }
      if (iq > 0) {
        command.append("&iq=").append(iq);
      }
      if (gamesPerSession > 0) {
        command.append("&gpsession=").append(gamesPerSession);
      }
      if (uid != null && uid.length() > 0) {
        command.append("&uid=").append(uid);
      }
    }
    else if (cmd == ShillConstants.REMOVE_BOT) {
      command.append("cmd=").append("rmbot");
      if (gid != null && gid.length() > 0) {
        command.append("&gid=").append(gid);
      }
      if (gameTypes > 0) {
        command.append("&gtypes=").append(gameTypes);
      }
      if (uid != null && uid.length() > 0) {
        command.append("&uid=").append(uid);
      }
      if (iq > 0) {
        command.append("&iq=").append(iq);
      }
      if (idleVal > 0) {
        command.append("&iv=").append(idleVal);
      }
      if (sitOutVal > 0) {
        command.append("&sov=").append(sitOutVal);
      }
      if (maxLoss > 0) {
        command.append("&maxloss=").append(maxLoss);
      }
      if (maxWin > 0) {
        command.append("&maxwin=").append(maxWin);
      }
    }
    else if (cmd == ShillConstants.STOP_BOT) {
      command.append("cmd=").append("stopbot");
      if (uid != null && uid.length() > 0) {
        command.append("&uid=").append(uid);
      }
    }

    return command.toString();
  }

  public void parseData(String cmdString) {
    String nvp[] = cmdString.split("\\&");
    for (int i = 0; i < nvp.length; i++) {
      String nv[] = nvp[i].split("=");
      if (nv[0].equals("cmd")) {
        cmd = nv[1].equals("stop") ? ShillConstants.STOP :
            nv[1].equals("status") ?ShillConstants.STATUS :
            nv[1].equals("addbot") ?ShillConstants.ADD_BOT :
            nv[1].equals("rmbot") ?ShillConstants.REMOVE_BOT :
            nv[1].equals("modbot") ?ShillConstants.MODIFY_BOT :
            nv[1].equals("stopbot") ?ShillConstants.STOP_BOT : 0;
      }
      else if (nv[0].equals("type")) {
        all_flag = nv[1].equals("all") ? true : false;
      }
      else if (nv[0].equals("ip")) {
        ip = nv[1];
      }
      else if (nv[0].equals("delay")) {
        cmd_delay = Integer.parseInt(nv[1]);
      }
      else if (nv[0].equals("gid")) {
        gid = nv[1];
      }
      else if (nv[0].equals("gtypes")) {
        gameTypes = Integer.parseInt(nv[1]);
      }
      else if (nv[0].equals("pgtypes")) {
        pref_gameTypes = Integer.parseInt(nv[1]);
      }
      else if (nv[0].equals("iq")) {
        iq = Integer.parseInt(nv[1]);
      }
      else if (nv[0].equals("uid")) {
        uid = nv[1];
      }
      else if (nv[0].equals("iv")) {
        idleVal = Integer.parseInt(nv[1]);
      }
      else if (nv[0].equals("sov")) {
        sitOutVal = Integer.parseInt(nv[1]);
      }
      else if (nv[0].equals("maxloss")) {
        //maxLoss = Integer.parseInt(nv[1]);
        maxLoss = Float.parseFloat(nv[1]);
      }
      else if (nv[0].equals("maxwin")) {
        //maxWin = Integer.parseInt(nv[1]);
        maxWin = Float.parseFloat(nv[1]);
      }
      else if (nv[0].equals("gpsession")) {
        gamesPerSession = Integer.parseInt(nv[1]);
      }
    }
  }
}
