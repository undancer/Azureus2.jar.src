package org.gudy.azureus2.platform.win32.access;

public abstract interface AEWin32AccessListener
{
  public static final int ET_SHUTDOWN = 1;
  public static final int ET_SUSPEND = 2;
  public static final int ET_RESUME = 3;
  public static final int RT_SUSPEND_DENY = 1;
  
  public abstract int eventOccurred(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/win32/access/AEWin32AccessListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */