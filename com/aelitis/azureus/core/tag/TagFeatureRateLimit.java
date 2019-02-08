package com.aelitis.azureus.core.tag;

public abstract interface TagFeatureRateLimit
  extends TagFeature
{
  public static final int SR_ACTION_QUEUE = 0;
  public static final int SR_ACTION_PAUSE = 1;
  public static final int SR_ACTION_STOP = 2;
  public static final int SR_INDIVIDUAL_ACTION_DEFAULT = 0;
  public static final int SR_AGGREGATE_ACTION_DEFAULT = 1;
  public static final boolean AT_RATELIMIT_MAX_AGGREGATE_SR_PRIORITY_DEFAULT = true;
  
  public abstract boolean supportsTagRates();
  
  public abstract boolean supportsTagUploadLimit();
  
  public abstract boolean supportsTagDownloadLimit();
  
  public abstract int getTagUploadLimit();
  
  public abstract void setTagUploadLimit(int paramInt);
  
  public abstract int getTagCurrentUploadRate();
  
  public abstract int getTagDownloadLimit();
  
  public abstract void setTagDownloadLimit(int paramInt);
  
  public abstract int getTagCurrentDownloadRate();
  
  public abstract long[] getTagSessionUploadTotal();
  
  public abstract void resetTagSessionUploadTotal();
  
  public abstract long[] getTagSessionDownloadTotal();
  
  public abstract void resetTagSessionDownloadTotal();
  
  public abstract long[] getTagUploadTotal();
  
  public abstract long[] getTagDownloadTotal();
  
  public abstract void setRecentHistoryRetention(boolean paramBoolean);
  
  public abstract int[][] getRecentHistory();
  
  public abstract int getTagUploadPriority();
  
  public abstract void setTagUploadPriority(int paramInt);
  
  public abstract int getTagMinShareRatio();
  
  public abstract void setTagMinShareRatio(int paramInt);
  
  public abstract int getTagMaxShareRatio();
  
  public abstract void setTagMaxShareRatio(int paramInt);
  
  public abstract int getTagMaxShareRatioAction();
  
  public abstract void setTagMaxShareRatioAction(int paramInt);
  
  public abstract int getTagAggregateShareRatio();
  
  public abstract int getTagMaxAggregateShareRatio();
  
  public abstract void setTagMaxAggregateShareRatio(int paramInt);
  
  public abstract int getTagMaxAggregateShareRatioAction();
  
  public abstract void setTagMaxAggregateShareRatioAction(int paramInt);
  
  public abstract boolean getTagMaxAggregateShareRatioHasPriority();
  
  public abstract void setTagMaxAggregateShareRatioHasPriority(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagFeatureRateLimit.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */