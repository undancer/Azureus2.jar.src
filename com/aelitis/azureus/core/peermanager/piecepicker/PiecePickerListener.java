package com.aelitis.azureus.core.peermanager.piecepicker;

public abstract interface PiecePickerListener
{
  public abstract void providerAdded(PieceRTAProvider paramPieceRTAProvider);
  
  public abstract void providerRemoved(PieceRTAProvider paramPieceRTAProvider);
  
  public abstract void providerAdded(PiecePriorityProvider paramPiecePriorityProvider);
  
  public abstract void providerRemoved(PiecePriorityProvider paramPiecePriorityProvider);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/piecepicker/PiecePickerListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */