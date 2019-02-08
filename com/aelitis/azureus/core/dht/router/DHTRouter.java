package com.aelitis.azureus.core.dht.router;

import java.util.List;

public abstract interface DHTRouter
{
  public abstract int getK();
  
  public abstract byte[] getID();
  
  public abstract boolean isID(byte[] paramArrayOfByte);
  
  public abstract DHTRouterContact getLocalContact();
  
  public abstract void setAdapter(DHTRouterAdapter paramDHTRouterAdapter);
  
  public abstract void seed();
  
  public abstract void contactKnown(byte[] paramArrayOfByte, DHTRouterContactAttachment paramDHTRouterContactAttachment, boolean paramBoolean);
  
  public abstract void contactAlive(byte[] paramArrayOfByte, DHTRouterContactAttachment paramDHTRouterContactAttachment);
  
  public abstract DHTRouterContact contactDead(byte[] paramArrayOfByte, boolean paramBoolean);
  
  public abstract DHTRouterContact findContact(byte[] paramArrayOfByte);
  
  public abstract List<DHTRouterContact> findClosestContacts(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean);
  
  public abstract void recordLookup(byte[] paramArrayOfByte);
  
  public abstract boolean requestPing(byte[] paramArrayOfByte);
  
  public abstract void refreshIdleLeaves(long paramLong);
  
  public abstract byte[] refreshRandom();
  
  public abstract List<DHTRouterContact> findBestContacts(int paramInt);
  
  public abstract List<DHTRouterContact> getAllContacts();
  
  public abstract DHTRouterStats getStats();
  
  public abstract void setSleeping(boolean paramBoolean);
  
  public abstract void setSuspended(boolean paramBoolean);
  
  public abstract void destroy();
  
  public abstract void print();
  
  public abstract boolean addObserver(DHTRouterObserver paramDHTRouterObserver);
  
  public abstract boolean containsObserver(DHTRouterObserver paramDHTRouterObserver);
  
  public abstract boolean removeObserver(DHTRouterObserver paramDHTRouterObserver);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/router/DHTRouter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */