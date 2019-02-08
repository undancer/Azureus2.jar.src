package com.aelitis.azureus.core.messenger;

import com.aelitis.azureus.core.messenger.browser.BrowserMessageDispatcher;
import com.aelitis.azureus.core.messenger.browser.listeners.BrowserMessageListener;
import java.util.Collection;
import java.util.Map;

public abstract interface ClientMessageContext
{
  public abstract void addMessageListener(BrowserMessageListener paramBrowserMessageListener);
  
  public abstract void removeMessageListener(String paramString);
  
  public abstract void removeMessageListener(BrowserMessageListener paramBrowserMessageListener);
  
  public abstract Object getBrowserData(String paramString);
  
  public abstract void setBrowserData(String paramString, Object paramObject);
  
  public abstract boolean sendBrowserMessage(String paramString1, String paramString2);
  
  public abstract boolean sendBrowserMessage(String paramString1, String paramString2, Map paramMap);
  
  public abstract boolean executeInBrowser(String paramString);
  
  public abstract void debug(String paramString);
  
  public abstract void debug(String paramString, Throwable paramThrowable);
  
  public abstract BrowserMessageDispatcher getDispatcher();
  
  public abstract boolean sendBrowserMessage(String paramString1, String paramString2, Collection paramCollection);
  
  public abstract void setMessageDispatcher(BrowserMessageDispatcher paramBrowserMessageDispatcher);
  
  public abstract void setTorrentURLHandler(torrentURLHandler paramtorrentURLHandler);
  
  public abstract void setContentNetworkID(long paramLong);
  
  public abstract long getContentNetworkID();
  
  public static abstract interface torrentURLHandler
  {
    public abstract void handleTorrentURL(String paramString);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/ClientMessageContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */