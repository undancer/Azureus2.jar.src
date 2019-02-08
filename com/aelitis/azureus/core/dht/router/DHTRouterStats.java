package com.aelitis.azureus.core.dht.router;

public abstract interface DHTRouterStats
{
  public static final int ST_NODES = 0;
  public static final int ST_LEAVES = 1;
  public static final int ST_CONTACTS = 2;
  public static final int ST_REPLACEMENTS = 3;
  public static final int ST_CONTACTS_LIVE = 4;
  public static final int ST_CONTACTS_UNKNOWN = 5;
  public static final int ST_CONTACTS_DEAD = 6;
  
  public abstract long[] getStats();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/router/DHTRouterStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */