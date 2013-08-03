package com.poker.shills;

import com.golconda.game.Game;
import com.poker.game.poker.Poker;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class BotSchedule extends TimerTask {
    // set the category for logging
    static Logger _cat = Logger.getLogger(BotSchedule.class.getName());

    static BotSchedule _bs = null;
    static _Schedule[] _schv  = null;
    static Format _formatter = new SimpleDateFormat("HH:mm");
    static _Schedule _last_schedule = null;

    // 100% is  10 full tables   10 random
    //  10% is 1 full table and 1 random
    // 20% is 2 full and 2 random

    /**static String _schedule[][] = {   // time, play random, play full, real random, real full
            {"16:00", "2", "3", "0", "5" },
            {"16:30", "2", "1", "0", "3" },
            {"16:35", "0", "3", "0", "1" },
            {"16:40", "2", "3", "0", "4" },
            {"16:45", "0", "1", "0", "3" },
            {"16:50", "2", "3", "0", "5" },
            {"16:55", "0", "1", "0", "1" },
            {"17:00", "3", "3", "0", "5" },
            {"17:05", "2", "3", "0", "5" },
            {"17:10", "2", "1", "0", "3" },
            {"17:15", "0", "1", "0", "1" },
            {"17:20", "2", "3", "0", "4" },
            {"17:25", "0", "1", "0", "3" },
            {"17:30", "2", "1", "0", "3" },
            {"17:35", "0", "1", "0", "1" },
            {"17:40", "2", "3", "0", "4" },
            {"17:45", "0", "1", "0", "3" },
            {"17:50", "2", "3", "0", "5" },
            {"17:55", "0", "1", "0", "1" },
            {"18:00", "3", "3", "0", "5" },
            {"18:05", "2", "3", "0", "5" },
            {"18:10", "2", "1", "0", "3" },
            {"18:15", "0", "1", "0", "1" },
            {"18:20", "2", "3", "0", "4" },
            {"18:25", "0", "1", "0", "3" },
            {"18:30", "2", "1", "0", "3" },
            {"18:35", "0", "1", "0", "1" },
            {"18:40", "2", "3", "0", "4" },
            {"18:45", "0", "1", "0", "3" },
            {"18:50", "2", "3", "0", "5" },
            {"18:55", "0", "1", "0", "1" },
            {"19:00", "3", "3", "0", "5" },
            {"19:05", "2", "3", "0", "5" },
            {"19:10", "2", "1", "0", "3" },
            {"19:15", "0", "1", "0", "1" },
            {"19:20", "2", "3", "0", "4" },
            {"19:25", "0", "1", "0", "3" },
            {"19:30", "2", "1", "0", "3" },
            {"19:35", "0", "1", "0", "1" },
            {"19:40", "2", "3", "0", "4" },
            {"19:45", "0", "1", "0", "3" },
            {"19:50", "2", "3", "0", "5" },
            {"19:55", "0", "1", "0", "1" },
            {"20:00", "6", "2", "0", "6" },
            {"21:00", "6", "2", "0", "6" },
            {"22:00", "4", "1", "0", "4" },
            {"23:00", "3", "1", "0", "2" },
    };  **/

    static String _schedule[][] = {   // time, play random, play full, real random, real full
            {"00:00", "1", "0", "0", "1" },
            {"08:00", "2", "1", "0", "1" },
            {"11:00", "2", "1", "0", "1" },
            {"12:00", "2", "1", "0", "2" },
            {"14:00", "2", "2", "0", "3" },
            {"15:00", "2", "2", "0", "4" },
            {"16:00", "2", "3", "0", "5" },
            {"17:00", "3", "3", "0", "5" },
            {"18:00", "4", "3", "0", "6" },
            {"19:00", "6", "3", "0", "6" },
            {"20:00", "6", "2", "0", "6" },
            {"21:00", "6", "2", "0", "6" },
            {"22:00", "4", "1", "0", "4" },
            {"23:00", "3", "1", "0", "2" },
    };

    private BotSchedule(){}

    public static synchronized BotSchedule getInstance(){
        if (_bs == null){
            _bs = new BotSchedule();
        }
        return _bs;
    }

     _Schedule[] initSchedule(){
        _Schedule[] sv = new _Schedule[_schedule.length];
        int i=0;
        for (String[] entry: _schedule){
            try {
                Calendar d = Calendar.getInstance();
                d = Calendar.getInstance();
                d.setTime((Date)((DateFormat) _formatter).parse(entry[0]))  ;
                Calendar dn = Calendar.getInstance();
                dn = Calendar.getInstance();
                dn.setTime(new Date());
                dn.set(Calendar.HOUR_OF_DAY, d.get(Calendar.HOUR_OF_DAY));
                dn.set(Calendar.MINUTE, d.get(Calendar.MINUTE));
                dn.set(Calendar.SECOND, d.get(Calendar.SECOND));
                _Schedule sch = new _Schedule(
                        dn,
                        Integer.parseInt(entry[1]),
                        Integer.parseInt(entry[2]),
                        Integer.parseInt(entry[3]),
                        Integer.parseInt(entry[4])
                );
                sv[i++] = sch;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return sv;
    }

    public _Schedule getNow(){
        _Schedule[] scv = initSchedule();
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        for (int i = scv.length -1 ; i >= 0 ; i--){
            if ((int)(now.getTimeInMillis() - scv[i]._d.getTimeInMillis()) > 0){
                return scv[i];
            }
        }
        return null;
    }

    public synchronized void startScheduler(){
         if (_schv == null){
             _schv = initSchedule();
             _last_schedule = new _Schedule();
             Timer t = new Timer("BotSch");
             t.scheduleAtFixedRate(this, 1000, BotData.SCHEDULE_TIMER * 1000);
         }
        else {
             throw new IllegalStateException("Bots Already scheduled");
         }
    }


    public void stopGames(int prandom, int pfull, int rrandom, int rfull){
        Game gv[] = Game.listAll();
        // place bots on tables
        for (Game g: gv){
            Poker p = (Poker)g;
            BotGame bg = BotGame.getBotGame(p.name());
            if (bg==null) continue;
            if (p.type().isReal()){
                if (p.type().isRandomBotGame() && rrandom < 0) {
                    bg.stop();
                    rrandom++;
                }
                else if (rfull < 0) {
                    bg.stop();
                    rfull++;
                }
            }
            else {
                if (p.type().isRandomBotGame() && prandom < 0) {
                    bg.stop();
                    prandom++;
                }
                else if (pfull < 0) {
                    bg.stop();
                    pfull++;
                }
            }
        }
        _cat.warning("Stop Games=" + prandom + ", " + pfull + ", " + rrandom + ", " + rfull);
    }



    public void startGames(int prandom, int pfull, int rrandom, int rfull){
        Game gv[] = Game.listAll();
        // place bots on tables
        for (Game g: gv){
            Poker gp = (Poker)g;
            if (gp.type().isReal()){
                if (gp.type().isRandomBotGame() && rrandom > 0) {
                    BotGame bg = new BotGame(gp, BotGame.RANDOM);
                    if (!bg.start()) continue;
                    rrandom--;
                }
                else if (gp.type().isBotGame() && rfull > 0) {
                    BotGame bg = new BotGame(gp, BotGame.FULL);
                    if (!bg.start()) continue;
                    rfull--;
                }
            }
            else {
                if (gp.type().isRandomBotGame() && prandom > 0) {
                    BotGame bg = new BotGame(gp, BotGame.RANDOM);
                    if (!bg.start()) continue;
                    prandom--;
                }
                else if (gp.type().isBotGame() && pfull >0) {
                    BotGame bg = new BotGame(gp, BotGame.FULL);
                    if (!bg.start()) continue;
                    pfull--;
                }
            }
        }
        _cat.warning("Start Games=" + prandom + ", " + pfull + ", " + rrandom + ", " + rfull);
    }


    public void checkGames(int pv[], int rv[]){
        int prandom= pv[0];
        int pfull = pv[1];
        int rrandom = rv[0];
        int rfull = rv[1];

        Collection<BotGame> bgv = BotGame.getBotGames();
        // place bots on tables
        for (BotGame g: bgv){
            if (g.isReal()){
                if (g.isRandom()) {
                    rrandom--;
                }
                else if (g.isFull()) {
                    rfull--;
                }
            }
            else {
                if (g.isRandom()) {
                    prandom--;
                }
                else if (g.isFull()) {
                    pfull--;
                }
            }
        }
        _cat.warning("Check Games=" + prandom + ", " + pfull + ", " + rrandom + ", " + rfull);
        startGames(prandom, pfull, rrandom, rfull);
        stopGames(prandom, pfull, rrandom, rfull);
    }

    static int curr=0;

    @Override
    public void run() {
        _Schedule s = getNow();
        _cat.info("Prev Schedule" + _last_schedule + "Schedule=" + s);
        if (!s.equals(_last_schedule)) {
            checkGames(s.playGames(), s.realGames());
            _last_schedule = s;
            curr++;
        }
    }

   /** @Override
    public void run() {
        //_Schedule s = getNow();
        //_cat.info("Prev Schedule" + _last_schedule + "Schedule=" + s);
        //if (!s.equals(_last_schedule)){
        String entry[] = _schedule[curr];
        Calendar dn = Calendar.getInstance();
        dn = Calendar.getInstance();
        dn.setTime(new Date());
        _Schedule s = new _Schedule(
                dn,
                Integer.parseInt(entry[1]),
                Integer.parseInt(entry[2]),
                Integer.parseInt(entry[3]),
                Integer.parseInt(entry[4])
        );
        if (!s.equals(_last_schedule)) {
            checkGames(s.playGames(), s.realGames());
            _last_schedule = s;
            curr++;
        }
        //}
    }  **/

    public class _Schedule {
        public Calendar _d;
        public int _play_random;
        public int _play_full;
        public int _real_random;
        public int _real_full;

        public  _Schedule(){
            _d = Calendar.getInstance();
            _play_full = _play_random = _real_random = _real_full =0;
        }

        public _Schedule(Calendar d, int plr, int plf, int rlr, int rlf){
            _d = d;
            _play_random = plr;
            _play_full = plf;
            _real_random = rlr;
            _real_full =  rlf;
        }

        public int[] realGames(){
            int v[] = new int[2];
            v[0] = _real_random;
            v[1] = _real_full;
            return v;
        }

        public int[] playGames(){
            int v[] = new int[2];
            v[0] = _play_random ;
            v[1] = _play_full;
            return v;
        }

        @Override
        public int hashCode() {
            int result = _play_random;
            result = 31 * result + _play_full;
            result = 31 * result + _real_random;
            result = 31 * result + _real_full;
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof _Schedule)) return false;

            _Schedule schedule = (_Schedule) o;

            if (_play_full != schedule._play_full) return false;
            if (_play_random != schedule._play_random) return false;
            if (_real_full != schedule._real_full) return false;
            if (_real_random != schedule._real_random) return false;

            return true;
        }

        @Override
        public String toString() {
            return _d.getTime().toString() + " pr=" +  _play_random  + ", pf="  +  _play_full + ", rr="  + _real_random + ", rf=" +  _real_full;
        }

    }

    public static void main(String[] args){
        BotSchedule bs = new BotSchedule();
        _Schedule s = bs.getNow();
        System.out.println(s);
    }
}
