package com.poker.nio;

public interface Client {

  public boolean isDead();

  public void kill();

  public void handler(Handler h);

}
