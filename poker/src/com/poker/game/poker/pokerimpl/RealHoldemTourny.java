package com.poker.game.poker.pokerimpl;

import com.agneya.util.Utils;

import com.golconda.db.ModuleType;
import com.golconda.game.GameStateEvent;
import com.golconda.game.Presence;
import com.golconda.game.resp.Response;
import com.golconda.game.util.Cards;
import com.golconda.game.util.MyDeck;

import com.poker.common.interfaces.MTTInterface;
import com.poker.common.message.ResponseGameEvent;
import com.poker.common.message.ResponseInt;
import com.poker.common.message.ResponseString;
import com.poker.game.PokerGameType;
import com.poker.game.PokerMoves;
import com.poker.game.PokerPresence;
import com.poker.game.poker.CollectABResponse;
import com.poker.game.poker.GameDetailsResponse;
import com.poker.game.poker.GameOverResponse;
import com.poker.game.poker.LeaveResponse;
import com.poker.game.poker.MoveResponse;
import com.poker.game.poker.Pot;
import com.poker.game.poker.SitInResponse;
import com.poker.game.poker.Tourny;
import com.poker.game.poker.TournyHandOverResponse;
import com.poker.game.util.Hand;
import com.poker.game.util.HandComparator;
import com.poker.server.GamePlayer;
import com.poker.server.GameProcessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Logger;


public class RealHoldemTourny
    extends HoldemTourny {

  public RealHoldemTourny( String name, int limit, int minPlayers, int maxPlayers,
                      String[] affiliate,
                      Observer stateObserver, Tourny tourny) {
    super(name, limit, minPlayers, maxPlayers, affiliate, stateObserver, tourny);
   _type = new PokerGameType(PokerGameType.Real_HoldemTourny);
  }


}
