package com.aelitis.azureus.core.tag;

public abstract interface TagFeature
{
  public static final int TF_NONE = 0;
  public static final int TF_RATE_LIMIT = 1;
  public static final int TF_RSS_FEED = 2;
  public static final int TF_RUN_STATE = 4;
  public static final int TF_XCODE = 8;
  public static final int TF_FILE_LOCATION = 16;
  public static final int TF_PROPERTIES = 32;
  public static final int TF_EXEC_ON_ASSIGN = 64;
  public static final int TF_LIMITS = 128;
  public static final int TF_NOTIFICATIONS = 256;
  
  public abstract Tag getTag();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagFeature.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */