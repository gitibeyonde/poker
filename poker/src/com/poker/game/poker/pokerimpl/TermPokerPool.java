package com.poker.game.poker.pokerimpl;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Vector;
import java.util.logging.Logger;

import com.agneya.util.Utils;
import com.golconda.game.Game;
import com.poker.game.PokerPresence;
import com.poker.game.poker.Poker;
import com.poker.game.poker.Poker.LastLeft;

public class TermPokerPool {
	  // set the category for logging
	  static Logger _cat = Logger.getLogger(TermPokerPool.class.getName());
	static ConcurrentHashMap<String, Pool> _pools;
	
	public TermPokerPool(){
		_pools = new ConcurrentHashMap<String, Pool>();
	}
	
	public void addTermPool(TermHoldem tp){
		String pool_name = tp._name.substring(4);
		Pool p = _pools.get(pool_name);
		if (p==null){
			//create a new poo;
			p = new Pool(pool_name);
			_pools.put(pool_name, p);
		}
		p.addPool(tp);
	}
	
	public String getPoolDetails(TermHoldem g){
		StringBuilder buf = new StringBuilder();
		buf.append("grid=").append(g.grid()).append(",");
		buf.append("affiliate=").append(g.affiliateString()).append(",");
		buf.append("partners=").append(g.partnerString()).append(",");
		buf.append("response-id=").append(-99).append(",");
		buf.append("msgGID=").append(-99).append(",");
		buf.append("name=").append(g._name.substring(4)).append(",");
		buf.append("type=").append(g.type().intVal()).append(",");
		buf.append("stack=").append(g.getStack()).append(",");
		// buf.append("rake=").append(Utils.getRoundedString(_g.rake())).append(",");
		buf.append("max-players=").append(g.maxPlayers()).append(",");
		buf.append("min-players=").append(g.minPlayers()).append(",");
		buf.append("average-pot=").append(
				Utils.getRoundedDollarCent(g.averagePot())).append(",");
		
		buf.append("flop=").append(g._tpp.flopPlayers(g)).append(",");
		//get hands per hour for pool
		buf.append("hands-hour=").append(g._tpp.numHandsPerHour(g)).append(",");
		
		buf.append("max-rounds=").append(g.maxRounds()).append(",");
		buf.append("running=").append(g.isRunning()).append(",");
		// buf.append("rank=").append(_g.rank()).append(",");
		buf.append("bench=").append(((TermHoldem) g)._isBench).append(",");
		buf.append("ab=").append(Utils.getRoundedDollarCent(g.abValue()))
				.append(",");
		buf.append("min-bet=").append(Utils.getRoundedDollarCent(g.minBet()))
				.append(",");
		buf.append("max-bet=").append(Utils.getRoundedDollarCent(g.maxBet()))
				.append(",");

		// append blind/ante
		buf.append(g.abString()).append(",");
	
		Pool p = _pools.get(g._name.substring(4));
		// append all player details
		buf.append(p.playerDetails());
		return buf.toString();
	}
	
	public synchronized TermHoldem getBestTableToJoin(TermHoldem curr_table, PokerPresence pp){
		String pool_name = curr_table._name.substring(4);
		Pool p = _pools.get(pool_name);
		return p.getBestTableToJoin(curr_table, pp);
	}
	
	public synchronized TermHoldem getBestTableToJoin(String pool_name){
		Pool p = _pools.get(pool_name);
		return p.getBestTableToJoin();
	}
	
	public boolean onPool(String pool_name, String player){
		Pool p = _pools.get(pool_name);
		return p.onPool(player);
	}
	
	
	public TermHoldem getBrokeWaitingTable(TermHoldem curr_table){
		String pool_name = curr_table._name.substring(4);
		Pool p = _pools.get(pool_name);
		return p.getBrokeWaitingTable();
	}
	
	 public int numHandsPerHour(TermHoldem tp){
		int nh=0;
		String pool_name = tp._name.substring(4);
		Pool p = _pools.get(pool_name);
		for (TermHoldem th: p._v){
			nh+=th.numHandsPerHour();
		}
		return nh;
	 }
	 

	 public int flopPlayers(TermHoldem tp){
		int fp=0, count=1;
		String pool_name = tp._name.substring(4);
		Pool p = _pools.get(pool_name);
		for (TermHoldem th: p._v){
			if (th.flopPlayers() > 0){
				fp+=th.flopPlayers();
				count++;
			}
		}
		return fp/count;
	 }
	
	////////////////POOL/////////////////////////////
	public class Pool {
		String _name;
		Vector<TermHoldem> _v;
		
		public Pool(String name){
			_name = name;
			_v = new Vector<TermHoldem>();
		}
		
		public void addPool(TermHoldem tp){
			_v.add(tp);
		}
		
		public TermHoldem[] getSortedPool(){
			TermHoldem []tv = _v.toArray(new TermHoldem[_v.size()]);
			java.util.Arrays.sort(tv, new Comparator<Game>(){
				public int compare(Game arg0, Game arg1) {
					int pl1 = -1, pl2 =-1;
					try {
						pl1 = ((Poker)arg1).allPlayers(-1).length;
					}
					catch (Throwable t){
						t.printStackTrace();
					}
					try {
						pl2 = ((Poker)arg0).allPlayers(-1).length;
					}
					catch (Throwable t){
						t.printStackTrace();
					}
					return pl1 - pl2;
				}
		    });
			return tv;
		}
		
		public TermHoldem getBestTableToJoin(TermHoldem curr_table, PokerPresence pp){
		  TermHoldem []gv = getSortedPool();
		  for (TermHoldem th: gv){
	    		if (!th._isBench && !th.isRunning() && !th._name.equals(curr_table._name)  && ! th._name.equals(pp.getGameName()) &&
	    				(th.getAllPlayerCount() < th.maxPlayers()) && th._isAvailable && ! th.onTable(pp.name())){
	    			// check if presence is already sitting on this table
	    			_cat.finest("Most eligible game found " + th.details().getBroadcast());
	    			th._isAvailable = false;
    				return th;
		    	}
		   }
		   _cat.warning("Unable to find a table ");
		   // create a table
		  	return null;
		}
		

		public TermHoldem getBestTableToJoin(){
		  TermHoldem []gv = getSortedPool();
		  for (TermHoldem th: gv){
    			if (!th._isBench && !th.isRunning() && (th.getAllPlayerCount() < th.maxPlayers()) && th._isAvailable){
	    			_cat.info("Valid game found "  + th.details().getBroadcast());
	    			th._isAvailable = false;
    				return th;
    			}
		    }
		    _cat.warning("Unable to find a table ");
		    // create a table
		  	return null;
		}
		

		public boolean onPool(String player){
			TermHoldem []gv = _v.toArray(new TermHoldem[_v.size()]);
			for (TermHoldem th: gv){
				PokerPresence[] pdl = th.allPlayers(0);
				if (pdl == null) continue;
				for (PokerPresence pp: pdl){
					if (pp.name().equals(player))return true;
				}
			}
			return false;
		}
		
		
		public synchronized TermHoldem getBrokeWaitingTable(){
			TermHoldem []gv = getSortedPool();
			for (int i=gv.length-1;i>=0;i--){
				if (gv[i]._isBench && gv[i].getPlayerCount() < gv[i].maxPlayers()){
					return gv[i];
				}
			}
			// no empty bench found convert one
			for (int i=gv.length-1;i>=0;i--){
				if (gv[i].getPlayerCount() == 0 ){
					gv[i]._isBench = true;
					return gv[i];
				}
			}
			return null;
		}
		
		public String playerDetails(){
			StringBuilder buf = new StringBuilder("player-details=");
			TermHoldem []gv = _v.toArray(new TermHoldem[_v.size()]);
			for (TermHoldem th: gv){
				PokerPresence[] pdl = th.allPlayers(0);
				if (pdl == null) continue;
				for (PokerPresence pp: pdl){
					if (pp.isQickFold())continue;
					buf.append(pp.pos()).append("|");
			        buf.append(pp.netWorthString()).append("|");
			        buf.append(pp.currentRoundBetRoundedString()).append("|");
			        buf.append(pp.name()).append("|");
			        buf.append(pp.status()).append("|");
			        buf.append(pp.gender()).append("|");
			        buf.append(pp.rank()).append("|");
			        buf.append(pp.avatar()).append("|");
			        buf.append(pp.city()).append("|");
			        buf.append(pp.getHand().getCardsString());
			        buf.append("`");
				}
			}
			return buf.deleteCharAt(buf.length() - 1).append(",").toString();
		}
		
		
	}
	

	///////////////////////END POOL/////////////////////////////
	
}
