package com.golconda.game;


public abstract class GameType {

  public int intVal;

  public GameType(int iv){
      intVal = iv;
  }

  public abstract boolean isReal();
  public abstract boolean isPlay();
  public abstract boolean isRegularGame();
  public abstract boolean isTourny();
  public abstract boolean isTPoker();
  public abstract boolean isBotGame();
  public abstract boolean isRandomBotGame();
  public abstract boolean equals(GameType g);
  public abstract int intVal();

}
