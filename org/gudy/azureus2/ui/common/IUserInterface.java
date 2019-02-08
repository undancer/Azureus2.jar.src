package org.gudy.azureus2.ui.common;

public abstract interface IUserInterface
{
  public abstract void init(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract String[] processArgs(String[] paramArrayOfString);
  
  public abstract void startUI();
  
  public abstract boolean isStarted();
  
  public abstract void openTorrent(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/IUserInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */