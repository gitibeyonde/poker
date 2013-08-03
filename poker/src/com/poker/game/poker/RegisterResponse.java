package com.poker.game.poker;

import com.poker.game.poker.pokerimpl.HoldemTourny;


public class RegisterResponse
    extends PokerResponse {

  public RegisterResponse(HoldemTourny g) {
    super(g);
    buf = new StringBuilder().append(miniHeader()).append(registeredPlayerDetail());
    setCommand( g.inquirer(), buf.toString() ) ;
  }

  StringBuilder buf;
}
