package com.aelitis.azureus.core.speedmanager;

public abstract interface SpeedManagerLimitEstimate
{
  public static final float TYPE_UNKNOWN = -0.1F;
  public static final float TYPE_ESTIMATED = 0.0F;
  public static final float TYPE_CHOKE_ESTIMATED = 0.5F;
  public static final float TYPE_MEASURED_MIN = 0.8F;
  public static final float TYPE_MEASURED = 0.9F;
  public static final float TYPE_MANUAL = 1.0F;
  
  public abstract int getBytesPerSec();
  
  public abstract float getEstimateType();
  
  public abstract float getMetricRating();
  
  public abstract int[][] getSegments();
  
  public abstract long getWhen();
  
  public abstract String getString();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/speedmanager/SpeedManagerLimitEstimate.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */