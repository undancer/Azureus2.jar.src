package com.aelitis.azureus.core.peermanager.piecepicker;

public abstract interface PieceRTAProvider
{
  public abstract long[] updateRTAs(PiecePicker paramPiecePicker);
  
  public abstract long getStartTime();
  
  public abstract long getStartPosition();
  
  public abstract long getCurrentPosition();
  
  public abstract long getBlockingPosition();
  
  public abstract void setBufferMillis(long paramLong1, long paramLong2);
  
  public abstract String getUserAgent();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/piecepicker/PieceRTAProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */