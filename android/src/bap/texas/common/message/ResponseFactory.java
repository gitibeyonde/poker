package bap.texas.common.message;



public class ResponseFactory {
  public String _com;

  public ResponseFactory(String str) {
    _com = str;
  }

  public Response getResponse() {
    if (_com == null) {
      return null;
    }
    Response r = new Response(_com);
    if (r.getResult() != 1){
      return r;
    }
    switch (r._response_name) {
      case Response.R_TABLELIST:
        return new ResponseTableList(r.getNVHash());
      case Response.R_TABLEDETAIL:
        return new ResponseTableDetail(r.getNVHash());
      case Response.R_TOURNYLIST:
        return new ResponseTournyList(r.getNVHash());
      case Response.R_MOVE:
        return new ResponseGameEvent(r.getNVHash());
      case Response.R_MESSAGE:
        return new ResponseMessage(r.getNVHash());
      case Response.R_TOURNYDETAIL:
        return new ResponseTournyDetail(r.getNVHash());
      case Response.R_TOURNYMYTABLE:
        return new ResponseTournyMyTable(r.getNVHash());
      case Response.R_CONFIG:
        return new ResponseConfig(r.getNVHash());
      case Response.R_REGISTER:
      case Response.R_LOGIN:
        return new ResponseLogin(r.getNVHash());
      case Response.R_BUYCHIPS:
        return new ResponseBuyChips(r.getNVHash());
      case Response.R_GET_CHIPS_INTO_GAME:
        return new ResponseGetChipsIntoGame(r.getNVHash());
      case Response.R_WAITER:
      case Response.R_RESET_ALL_IN:
        return new ResponseInt(r.getNVHash());
      case Response.R_PING:
        return new ResponsePing(r.getNVHash());
      case Response.R_TABLE_OPEN:
        return new ResponseTableOpen(r.getNVHash());
      case Response.R_PLAYER_REMOVED:
        return new ResponseString(r.getNVHash());

      default:
        return r;
    }
  }

}
