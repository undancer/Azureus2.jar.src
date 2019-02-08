package org.gudy.azureus2.plugins.ddb;

public abstract interface DistributedDatabaseProgressListener
{
  public abstract void reportSize(long paramLong);
  
  public abstract void reportActivity(String paramString);
  
  public abstract void reportCompleteness(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ddb/DistributedDatabaseProgressListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */