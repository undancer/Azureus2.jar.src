package com.aelitis.azureus.core.peermanager.piecepicker;

import java.util.List;
import org.gudy.azureus2.core3.peer.PEPeer;
import org.gudy.azureus2.core3.peer.PEPiece;
import org.gudy.azureus2.core3.util.IndentWriter;

public abstract interface PiecePicker
{
  public static final int REQUEST_HINT_MAX_LIFE = 120000;
  
  public abstract boolean hasDownloadablePiece();
  
  public abstract long getNeededUndonePieceChange();
  
  public abstract void addHavePiece(PEPeer paramPEPeer, int paramInt);
  
  public abstract void updateAvailability();
  
  public abstract int[] getAvailability();
  
  public abstract int getAvailability(int paramInt);
  
  public abstract float getMinAvailability();
  
  public abstract int getMaxAvailability();
  
  public abstract float getAvgAvail();
  
  public abstract long getAvailWentBadTime();
  
  public abstract float getMinAvailability(int paramInt);
  
  public abstract long getBytesUnavailable();
  
  public abstract void allocateRequests();
  
  public abstract boolean isInEndGameMode();
  
  public abstract boolean hasEndGameModeBeenAbandoned();
  
  public abstract void clearEndGameChunks();
  
  public abstract void addEndGameChunks(PEPiece paramPEPiece);
  
  public abstract void removeFromEndGameModeChunks(int paramInt1, int paramInt2);
  
  public abstract int getNumberOfPieces();
  
  public abstract int getNbPiecesDone();
  
  public abstract void setForcePiece(int paramInt, boolean paramBoolean);
  
  public abstract boolean isForcePiece(int paramInt);
  
  public abstract void setGlobalRequestHint(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract int[] getGlobalRequestHint();
  
  public abstract void setReverseBlockOrder(boolean paramBoolean);
  
  public abstract boolean getReverseBlockOrder();
  
  public abstract void addRTAProvider(PieceRTAProvider paramPieceRTAProvider);
  
  public abstract void removeRTAProvider(PieceRTAProvider paramPieceRTAProvider);
  
  public abstract List getRTAProviders();
  
  public abstract void addPriorityProvider(PiecePriorityProvider paramPiecePriorityProvider);
  
  public abstract void removePriorityProvider(PiecePriorityProvider paramPiecePriorityProvider);
  
  public abstract List getPriorityProviders();
  
  public abstract void addListener(PiecePickerListener paramPiecePickerListener);
  
  public abstract void removeListener(PiecePickerListener paramPiecePickerListener);
  
  public abstract void destroy();
  
  public abstract void generateEvidence(IndentWriter paramIndentWriter);
  
  public abstract String getPieceString(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/peermanager/piecepicker/PiecePicker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */