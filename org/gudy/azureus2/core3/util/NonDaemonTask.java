package org.gudy.azureus2.core3.util;

public abstract interface NonDaemonTask
{
  public abstract Object run()
    throws Throwable;
  
  public abstract String getName();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/NonDaemonTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */