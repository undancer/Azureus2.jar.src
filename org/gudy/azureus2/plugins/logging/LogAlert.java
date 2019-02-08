package org.gudy.azureus2.plugins.logging;

public abstract interface LogAlert
{
  public static final int LT_INFORMATION = 1;
  public static final int LT_WARNING = 2;
  public static final int LT_ERROR = 3;
  
  public abstract int getGivenTimeoutSecs();
  
  public abstract int getTimeoutSecs();
  
  public abstract String getText();
  
  public abstract String getPlainText();
  
  public abstract Throwable getError();
  
  public abstract int getType();
  
  public abstract Object[] getContext();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/logging/LogAlert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */