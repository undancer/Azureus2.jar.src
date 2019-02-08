package org.gudy.azureus2.plugins.peers;

public abstract interface PeerReadRequest
{
  public static final int NORMAL_REQUEST_SIZE = 16384;
  
  public abstract int getPieceNumber();
  
  public abstract int getOffset();
  
  public abstract int getLength();
  
  public abstract void resetTime(long paramLong);
  
  public abstract boolean isExpired();
  
  public abstract void cancel();
  
  public abstract boolean isCancelled();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/peers/PeerReadRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */