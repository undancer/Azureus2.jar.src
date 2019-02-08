package com.aelitis.azureus.core.tag;

public abstract interface TagFeatureLimits
{
  public static final int RS_NONE = 0;
  public static final int RS_ARCHIVE = 1;
  public static final int RS_REMOVE_FROM_LIBRARY = 2;
  public static final int RS_DELETE_FROM_COMPUTER = 3;
  public static final int RS_MOVE_TO_OLD_TAG = 4;
  public static final int RS_DEFAULT = 0;
  public static final int OP_ADDED_TO_VUZE = 0;
  public static final int OP_ADED_TO_TAG = 1;
  public static final int OP_DEFAULT = 0;
  
  public abstract int getMaximumTaggables();
  
  public abstract void setMaximumTaggables(int paramInt);
  
  public abstract int getRemovalStrategy();
  
  public abstract void setRemovalStrategy(int paramInt);
  
  public abstract int getOrdering();
  
  public abstract void setOrdering(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagFeatureLimits.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */