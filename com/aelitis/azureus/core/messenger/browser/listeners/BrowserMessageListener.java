package com.aelitis.azureus.core.messenger.browser.listeners;

import com.aelitis.azureus.core.messenger.ClientMessageContext;
import com.aelitis.azureus.core.messenger.browser.BrowserMessage;

public abstract interface BrowserMessageListener
{
  public abstract ClientMessageContext getContext();
  
  public abstract String getId();
  
  public abstract void handleMessage(BrowserMessage paramBrowserMessage);
  
  public abstract void setContext(ClientMessageContext paramClientMessageContext);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/browser/listeners/BrowserMessageListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */