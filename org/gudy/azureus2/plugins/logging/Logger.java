package org.gudy.azureus2.plugins.logging;

import org.gudy.azureus2.plugins.PluginInterface;

public abstract interface Logger
{
  public abstract LoggerChannel getChannel(String paramString);
  
  public abstract LoggerChannel getTimeStampedChannel(String paramString);
  
  public abstract LoggerChannel getNullChannel(String paramString);
  
  public abstract LoggerChannel[] getChannels();
  
  public abstract PluginInterface getPluginInterface();
  
  public abstract void addAlertListener(LoggerAlertListener paramLoggerAlertListener);
  
  public abstract void removeAlertListener(LoggerAlertListener paramLoggerAlertListener);
  
  public abstract void addAlertListener(LogAlertListener paramLogAlertListener);
  
  public abstract void removeAlertListener(LogAlertListener paramLogAlertListener);
  
  public abstract void addFileLoggingListener(FileLoggerAdapter paramFileLoggerAdapter);
  
  public abstract void removeFileLoggingListener(FileLoggerAdapter paramFileLoggerAdapter);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/logging/Logger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */