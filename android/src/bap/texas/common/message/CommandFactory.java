package bap.texas.common.message;



public class CommandFactory {

  public static Command getCommand(Command c) {
    try {
      Command tc;
      switch (c.getCommandName()) {
        case Command.C_REGISTER:
          tc = new CommandRegister(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_LOGIN:
          tc = new CommandLogin(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_TABLELIST:
          tc = new CommandTableList(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_TABLEDETAIL:
          tc = new CommandTableDetail(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_TOURNYREGISTER:
          tc = new CommandTournyRegister(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_TOURNYUNREGISTER:
          tc = new CommandTournyUnRegister(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_TOURNYDETAIL:
          tc = new CommandTournyDetail(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_TOURNYMYTABLE:
          tc = new CommandTournyMyTable(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_MOVE:
          tc = new CommandMove(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_MESSAGE:
          tc = new CommandMessage(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_BUYCHIPS:
          tc = new CommandBuyChips(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_GET_CHIPS_INTO_GAME:
          tc = new CommandGetChipsIntoGame(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_PREFERENCES:
        case Command.C_WAIT_FOR_BLINDS:
        case Command.C_SIT_IN:
        case Command.C_WAITER:
        case Command.C_SIT_OUT:
        case Command.C_TURN_DEAF:
          tc = new CommandString(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_VOTE:
          tc = new CommandVote(c.getNVHash());
          tc.handler(c.handler());
          return tc;
        case Command.C_PLAYER_SEARCH:
          tc = new CommandString(c.getNVHash());
          tc.handler(c.handler());
          return tc;

        default:
          return c;
      }
    }
    catch (Throwable e) {
      System.out.println("BAD COMMAND " + c._cstr);
      return new Command(c.session(), Command.A_ILLEGAL);
    }
  }

}
