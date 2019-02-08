package com.aelitis.azureus.core.messenger.browser;

import com.aelitis.azureus.core.messenger.browser.listeners.BrowserMessageListener;

public abstract interface BrowserMessageDispatcher
{
  public abstract void addListener(BrowserMessageListener paramBrowserMessageListener);
  
  public abstract void dispatch(BrowserMessage paramBrowserMessage);
  
  public abstract BrowserMessageListener getListener(String paramString);
  
  public abstract void removeListener(BrowserMessageListener paramBrowserMessageListener);
  
  public abstract void removeListener(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/browser/BrowserMessageDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */