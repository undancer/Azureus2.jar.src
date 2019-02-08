package org.gudy.azureus2.core3.disk;

import java.io.File;
import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceList;
import org.gudy.azureus2.core3.disk.impl.piecemapper.DMPieceMap;
import org.gudy.azureus2.core3.torrent.TOTorrent;
import org.gudy.azureus2.core3.util.DirectByteBuffer;
import org.gudy.azureus2.core3.util.IndentWriter;

public abstract interface DiskManager
{
  public static final int INITIALIZING = 1;
  public static final int ALLOCATING = 2;
  public static final int CHECKING = 3;
  public static final int READY = 4;
  public static final int FAULTY = 10;
  public static final int ET_NONE = 0;
  public static final int ET_OTHER = 1;
  public static final int ET_INSUFFICIENT_SPACE = 2;
  public static final int BLOCK_SIZE_KB = 16;
  public static final int BLOCK_SIZE = 16384;
  
  public abstract void start();
  
  public abstract boolean stop(boolean paramBoolean);
  
  public abstract boolean isStopped();
  
  public abstract boolean filesExist();
  
  public abstract DirectByteBuffer readBlock(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract DiskManagerWriteRequest createWriteRequest(int paramInt1, int paramInt2, DirectByteBuffer paramDirectByteBuffer, Object paramObject);
  
  public abstract void enqueueWriteRequest(DiskManagerWriteRequest paramDiskManagerWriteRequest, DiskManagerWriteRequestListener paramDiskManagerWriteRequestListener);
  
  public abstract boolean hasOutstandingWriteRequestForPiece(int paramInt);
  
  public abstract DiskManagerReadRequest createReadRequest(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void enqueueReadRequest(DiskManagerReadRequest paramDiskManagerReadRequest, DiskManagerReadRequestListener paramDiskManagerReadRequestListener);
  
  public abstract boolean hasOutstandingReadRequestForPiece(int paramInt);
  
  public abstract DiskManagerCheckRequest createCheckRequest(int paramInt, Object paramObject);
  
  public abstract void enqueueCheckRequest(DiskManagerCheckRequest paramDiskManagerCheckRequest, DiskManagerCheckRequestListener paramDiskManagerCheckRequestListener);
  
  public abstract boolean hasOutstandingCheckRequestForPiece(int paramInt);
  
  public abstract void enqueueCompleteRecheckRequest(DiskManagerCheckRequest paramDiskManagerCheckRequest, DiskManagerCheckRequestListener paramDiskManagerCheckRequestListener);
  
  public abstract void setPieceCheckingEnabled(boolean paramBoolean);
  
  public abstract void saveResumeData(boolean paramBoolean)
    throws Exception;
  
  public abstract DiskManagerPiece[] getPieces();
  
  public abstract int getNbPieces();
  
  public abstract DiskManagerFileInfo[] getFiles();
  
  public abstract DiskManagerFileInfoSet getFileSet();
  
  public abstract DiskManagerPiece getPiece(int paramInt);
  
  public abstract DMPieceMap getPieceMap();
  
  public abstract DMPieceList getPieceList(int paramInt);
  
  public abstract int getState();
  
  public abstract long getTotalLength();
  
  public abstract int getPieceLength();
  
  public abstract int getPieceLength(int paramInt);
  
  public abstract long getRemaining();
  
  public abstract long getRemainingExcludingDND();
  
  public abstract int getPercentDone();
  
  public abstract String getErrorMessage();
  
  public abstract int getErrorType();
  
  public abstract void downloadEnded(OperationStatus paramOperationStatus);
  
  public abstract void downloadRemoved();
  
  public abstract void moveDataFiles(File paramFile, String paramString, OperationStatus paramOperationStatus);
  
  public abstract int getCompleteRecheckStatus();
  
  public abstract int getMoveProgress();
  
  public abstract boolean checkBlockConsistencyForWrite(String paramString, int paramInt1, int paramInt2, DirectByteBuffer paramDirectByteBuffer);
  
  public abstract boolean checkBlockConsistencyForRead(String paramString, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract boolean checkBlockConsistencyForHint(String paramString, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract TOTorrent getTorrent();
  
  public abstract File getSaveLocation();
  
  public abstract void addListener(DiskManagerListener paramDiskManagerListener);
  
  public abstract void removeListener(DiskManagerListener paramDiskManagerListener);
  
  public abstract boolean hasListener(DiskManagerListener paramDiskManagerListener);
  
  public abstract void saveState();
  
  public abstract boolean isInteresting(int paramInt);
  
  public abstract boolean isDone(int paramInt);
  
  public abstract int getCacheMode();
  
  public abstract long[] getReadStats();
  
  public abstract void generateEvidence(IndentWriter paramIndentWriter);
  
  public abstract long getSizeExcludingDND();
  
  public abstract int getPercentDoneExcludingDND();
  
  public abstract long getPriorityChangeMarker();
  
  public static abstract interface GettingThere
  {
    public abstract boolean hasGotThere();
  }
  
  public static abstract interface OperationStatus
  {
    public abstract void gonnaTakeAWhile(DiskManager.GettingThere paramGettingThere);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/DiskManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */