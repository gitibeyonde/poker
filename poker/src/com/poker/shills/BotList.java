package com.poker.shills;


import com.agneya.util.Utils;
import com.golconda.db.*;
import com.golconda.game.Game;
import com.poker.game.poker.Poker;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

public class BotList {
    // set the category for logging
    static Logger _cat = Logger.getLogger(BotList.class.getName());

    private static Queue<BotPlayer> _bpl;
    private static List<BotPlayer> _in_use;
    private static int _length;

    private BotList(){}

    private static synchronized  Queue<BotPlayer> getBotPlayerList(){
         if (_bpl == null){
             _bpl = new ConcurrentLinkedQueue<BotPlayer>();
             _in_use = new ArrayList<BotPlayer>();
             _bpl.addAll(initBotPlayerList());
         }
        _length = _bpl.size();
        assert _length + _in_use.size() ==  BotData.getBotNames().length : "Missing bots";
        assert _length > 10 : "Available bots are not below 10";
        return _bpl;
    }

    public synchronized static BotPlayer find(String name){
        for (BotPlayer bp: _in_use){
            if (bp.name().equals(name)){
                return bp;
            }
        }
        throw new IllegalStateException("Bot player not found " + name);
    }

    public static void putBack(BotPlayer bp){
        _bpl.add(bp);
        _in_use.remove(bp);
    }

    public synchronized static BotPlayer getNext(BotGame bg){
        BotPlayer bp = getNextBot(bg);
        while (bp == null){
            for (BotPlayer p: _bpl)
                p._life++;
            bp = getNextBot(bg);
        }
        return bp;
    }

    private static BotPlayer getNextBot(BotGame bg){
        double chips = bg._pg.minBet() * (BotData.CHIPS + BotData.randomInt(10));
        double mrw = bg._pg.type().isReal() ? chips : 0;
        double mpw = bg._pg.type().isPlay() ? chips : 0;
        chips = Math.round(chips);

        _bpl =  getBotPlayerList();

        BotPlayer bp = _bpl.poll();
        for (int i=0;i<_length;i++){
            //_cat.info("name-" + bp.name() + " life=" + bp._life + " busy =" + bp._busy + "  " + mpw + " " +bp.playWorth() + " " + mrw + " " +  bp.realWorth());
            if (bp._life <= 0)  {
                bp._life++;
            }
            else {
                if (mpw <= bp.playWorth() && mrw <=  bp.realWorth()){
                    bp._life =  (BotData.randomInt(BotData.LIFE) + 10);
                    bp.attach(bg);
                    bp._initialChips = chips;
                    _in_use.add(bp);
                    return bp;
                }
                else {
                    if (bp.playWorth() < mpw){
                        bp._dbPlayer.setRealChipsShill(1000);
                    }
                    else if (bp.realWorth() < mrw){
                        bp._dbPlayer.setPlayChipsShill(1000);
                    }
                }
            }
            _bpl.add(bp);
            bp = _bpl.poll();
        }
        _cat.severe("BPL size = " + _bpl.size());
        return null;
    }


    private static List<BotPlayer> initBotPlayerList(){
        Game gv[] = Game.listAll();
        int required_bot_count=0;
        for (Game g: gv){
            Poker gp = (Poker)g;
            if (gp.type().isBotGame() || gp.type().isRandomBotGame()) {
                required_bot_count += gp.maxPlayers();
            }
        }
        _cat.finest("Number of bots required = " + (required_bot_count *= 5));

        List<String[]> bot_name = Arrays.asList(BotData.getBotNames());
        if (bot_name.size() < required_bot_count){
            throw new RuntimeException("Not enough bot data available to fill tables");
        }

        // randomize bot name
        Collections.shuffle(bot_name);

        //create players
        List<BotPlayer> pbpl = new Vector<BotPlayer>();
        for (int k=0;k<bot_name.size();k++){
            //check if the bot exists in DB
            String name= bot_name.get(k)[0];
            String passwd= bot_name.get(k)[1];
            //_cat.finest("Setting up bot " + name + " passwd=" + passwd);
            BotPlayer nbp = new BotPlayer(name);
            // ////////// DEFAULT PLAYER AUTHENTICATION
            boolean player_exists = false;
            DBPlayer dbp = new DBPlayer();
            try {
                player_exists = dbp.get(name, passwd, "system");
                /// GET chips out of games
                DBTransactionScratchPad[] dbtv = DBTransactionScratchPad.getTransaction(name);
                for (DBTransactionScratchPad dbt: dbtv){
                    dbp.chipsHouseKeeping(dbt._game_name, dbt._game_type, dbt._module, dbt._play, dbt._real, dbt._session);
                }
                /// END GET CHIPS OUT OF GAMES
            } catch (DBException e) {
                _cat.warning(e.getMessage());
                continue;
            } catch (IllegalStateException e) {
                _cat.warning(e.getMessage());
                continue;
            }

            try {
                if (player_exists) {
                    nbp.attach(name, dbp);
                    nbp.gender(dbp.getGender());
                    nbp.avatar(dbp.getAvatar());
                    nbp.city(dbp.getCity());
                    nbp.rank(dbp.getRank());
                    // check shill
                    nbp.shill(true);
                }
                else {
                    //register the player
                    dbp.setDisplayName(name);
                    dbp.setEmailId(name + "@shill.sh");

                    dbp.setAvatar(""+(BotData.randomInt(47) + 1));
                    //dbp.setCity(city);
                    //dbp.setCountry(country);
                    //dbp.setZip(zip);
                    dbp.setGender(0);
                    dbp.setPassword(passwd);
                    //dbp.setAllInTs(System.currentTimeMillis());
                    dbp.setPreferences(PlayerPreferences.DEFAULT_MASK);
                    dbp.setBonusCode("0");
                    dbp.setAffiliate("system");
                    int res = dbp.register(10000, 10000, new ModuleType(ModuleType.POKER));
                    if (res == 1){
                        nbp.setDBPlayer(dbp);
                        nbp.attach(name, dbp);
                        nbp.gender(dbp.getGender());
                        nbp.avatar(dbp.getAvatar());
                        nbp.city(dbp.getCity());
                        nbp.rank(dbp.getRank());
                        // check shill
                        nbp.shill(true);
                    }
                }
            } catch (DBException e) {
                _cat.warning(e.getMessage());
                continue;
            } catch (IllegalStateException e) {
                _cat.warning(e.getMessage());
                continue;
            }
            pbpl.add(nbp);
        } // END Players created in DB
        return pbpl;
    }
}
