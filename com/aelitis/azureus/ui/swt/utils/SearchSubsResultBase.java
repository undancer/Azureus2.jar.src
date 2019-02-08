package com.aelitis.azureus.ui.swt.utils;

public abstract interface SearchSubsResultBase
{
  public abstract String getName();
  
  public abstract byte[] getHash();
  
  public abstract int getContentType();
  
  public abstract long getSize();
  
  public abstract String getSeedsPeers();
  
  public abstract long getSeedsPeersSortValue();
  
  public abstract String getVotesComments();
  
  public abstract long getVotesCommentsSortValue();
  
  public abstract int getRank();
  
  public abstract String getTorrentLink();
  
  public abstract String getDetailsLink();
  
  public abstract String getCategory();
  
  public abstract long getTime();
  
  public abstract boolean getRead();
  
  public abstract void setRead(boolean paramBoolean);
  
  public abstract void setUserData(Object paramObject1, Object paramObject2);
  
  public abstract Object getUserData(Object paramObject);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/utils/SearchSubsResultBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */