package com.aelitis.azureus.core.tag;

import java.io.File;

public abstract interface TagFeatureFileLocation
{
  public static final long FL_NONE = 0L;
  public static final long FL_DATA = 1L;
  public static final long FL_TORRENT = 2L;
  public static final long FL_BOTH = 3L;
  public static final long FL_DEFAULT = 1L;
  
  public abstract boolean supportsTagInitialSaveFolder();
  
  public abstract File getTagInitialSaveFolder();
  
  public abstract void setTagInitialSaveFolder(File paramFile);
  
  public abstract long getTagInitialSaveOptions();
  
  public abstract void setTagInitialSaveOptions(long paramLong);
  
  public abstract boolean supportsTagMoveOnComplete();
  
  public abstract File getTagMoveOnCompleteFolder();
  
  public abstract void setTagMoveOnCompleteFolder(File paramFile);
  
  public abstract long getTagMoveOnCompleteOptions();
  
  public abstract void setTagMoveOnCompleteOptions(long paramLong);
  
  public abstract boolean supportsTagCopyOnComplete();
  
  public abstract File getTagCopyOnCompleteFolder();
  
  public abstract void setTagCopyOnCompleteFolder(File paramFile);
  
  public abstract long getTagCopyOnCompleteOptions();
  
  public abstract void setTagCopyOnCompleteOptions(long paramLong);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagFeatureFileLocation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */