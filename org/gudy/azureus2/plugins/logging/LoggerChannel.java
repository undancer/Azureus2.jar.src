package org.gudy.azureus2.plugins.logging;

public abstract interface LoggerChannel
{
  public static final int LT_INFORMATION = 1;
  public static final int LT_WARNING = 2;
  public static final int LT_ERROR = 3;
  
  public abstract String getName();
  
  public abstract boolean isEnabled();
  
  public abstract void setDiagnostic();
  
  public abstract void setDiagnostic(long paramLong, boolean paramBoolean);
  
  public abstract void setForce(boolean paramBoolean);
  
  public abstract boolean getForce();
  
  public abstract void log(int paramInt, String paramString);
  
  public abstract void log(String paramString);
  
  public abstract void log(Throwable paramThrowable);
  
  public abstract void log(String paramString, Throwable paramThrowable);
  
  public abstract void log(Object[] paramArrayOfObject, int paramInt, String paramString);
  
  public abstract void log(Object paramObject, int paramInt, String paramString);
  
  public abstract void log(Object paramObject, String paramString, Throwable paramThrowable);
  
  public abstract void log(Object[] paramArrayOfObject, String paramString, Throwable paramThrowable);
  
  public abstract void log(Object[] paramArrayOfObject, String paramString);
  
  public abstract void log(Object paramObject, String paramString);
  
  public abstract void logAlert(int paramInt, String paramString);
  
  public abstract void logAlert(String paramString, Throwable paramThrowable);
  
  public abstract void logAlertRepeatable(int paramInt, String paramString);
  
  public abstract void logAlertRepeatable(String paramString, Throwable paramThrowable);
  
  public abstract void addListener(LoggerChannelListener paramLoggerChannelListener);
  
  public abstract void removeListener(LoggerChannelListener paramLoggerChannelListener);
  
  public abstract Logger getLogger();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/logging/LoggerChannel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */