package org.gudy.azureus2.core3.stats;

public abstract interface StatsWriterPeriodic
{
  public static final int DEFAULT_SLEEP_PERIOD = 30000;
  public static final String DEFAULT_STATS_FILE_NAME = "Azureus_Stats.xml";
  
  public abstract void start();
  
  public abstract void stop();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/stats/StatsWriterPeriodic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */