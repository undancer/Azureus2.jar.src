package org.gudy.azureus2.platform.win32.access.impl;

public abstract interface AEWin32AccessCallback
{
  public abstract long windowsMessage(int paramInt1, int paramInt2, long paramLong);
  
  public abstract long generalMessage(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/win32/access/impl/AEWin32AccessCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */