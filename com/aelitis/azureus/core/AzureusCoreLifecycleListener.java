package com.aelitis.azureus.core;

public abstract interface AzureusCoreLifecycleListener
{
  public abstract void componentCreated(AzureusCore paramAzureusCore, AzureusCoreComponent paramAzureusCoreComponent);
  
  public abstract void started(AzureusCore paramAzureusCore);
  
  public abstract void stopping(AzureusCore paramAzureusCore);
  
  public abstract void stopped(AzureusCore paramAzureusCore);
  
  public abstract boolean stopRequested(AzureusCore paramAzureusCore)
    throws AzureusCoreException;
  
  public abstract boolean restartRequested(AzureusCore paramAzureusCore)
    throws AzureusCoreException;
  
  public abstract boolean syncInvokeRequired();
  
  public abstract boolean requiresPluginInitCompleteBeforeStartedEvent();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/AzureusCoreLifecycleListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */