package com.aelitis.azureus.ui.swt.browser.listener;

public abstract interface ExternalLoginListener
{
  public abstract void cookiesFound(ExternalLoginWindow paramExternalLoginWindow, String paramString);
  
  public abstract void canceled(ExternalLoginWindow paramExternalLoginWindow);
  
  public abstract void done(ExternalLoginWindow paramExternalLoginWindow, String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/browser/listener/ExternalLoginListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */