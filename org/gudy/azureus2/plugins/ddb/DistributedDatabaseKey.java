package org.gudy.azureus2.plugins.ddb;

public abstract interface DistributedDatabaseKey
{
  public static final int FL_ANON = 1;
  public static final int FL_BRIDGED = 2;
  
  public abstract Object getKey()
    throws DistributedDatabaseException;
  
  public abstract String getDescription();
  
  public abstract void setFlags(int paramInt);
  
  public abstract int getFlags();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ddb/DistributedDatabaseKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */