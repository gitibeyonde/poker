package com.poker.common.message;

import com.golconda.message.Response;

import java.util.HashMap;


public class ResponseBanner
    extends Response {
  private String _location;
  private String _type;
  private String _imageUrl;
  private String _text;
  private String _textColor;
  private int _duration;

  public ResponseBanner(int result, String loc, String type, String url,
                        String text, String color, int duration) {
    super(result, R_CONFIG);
    _location = loc;
    _type = type;
    _imageUrl = url;
    _text = text;
    _textColor = color;
    _duration = duration;
  }

  public ResponseBanner(HashMap str) {
    super(str);
    _location = (String) _hash.get("LOC");
    _type = (String) _hash.get("TYPE");
    _imageUrl = (String) _hash.get("URL");
    _text = (String) _hash.get("TXT");
    _textColor = (String) _hash.get("COLOR");
    _duration = Integer.parseInt( (String) _hash.get("DURATION"));
  }

  public String toString() {
    StringBuilder str = new StringBuilder(super.toString());
    str.append("&LOC=").append(_location);
    str.append("&TYPE=").append(_type);
    str.append("&URL=").append(_imageUrl);
    str.append("&TXT=").append(_text);
    str.append("&COLOR=").append(_textColor);
    str.append("&DURATION=").append(_duration);
    return str.toString();
  }

  public boolean equal(ResponseBanner r) {
    if (r.toString().equals(toString())) {
      return true;
    }
    else {
      return false;
    }
  }

}
