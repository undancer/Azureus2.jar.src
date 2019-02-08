package com.aelitis.azureus.plugins.net.buddy;

import java.util.Map;
import org.gudy.azureus2.core3.download.DownloadManager;

public abstract interface BuddyPluginViewInterface
{
  public static final String VP_SWT_COMPOSITE = "swt_comp";
  public static final String VP_DOWNLOAD = "download";
  public static final String VP_CHAT = "chat";
  
  public abstract void openChat(BuddyPluginBeta.ChatInstance paramChatInstance);
  
  public abstract View buildView(Map<String, Object> paramMap, ViewListener paramViewListener);
  
  public abstract String renderMessage(BuddyPluginBeta.ChatInstance paramChatInstance, BuddyPluginBeta.ChatMessage paramChatMessage);
  
  public abstract void selectClassicTab();
  
  public static abstract interface DownloadAdapter
  {
    public abstract DownloadManager getCoreDownload();
    
    public abstract String[] getNetworks();
    
    public abstract String getChatKey();
  }
  
  public static abstract interface View
  {
    public abstract void activate();
    
    public abstract void handleDrop(String paramString);
    
    public abstract void destroy();
  }
  
  public static abstract interface ViewListener
  {
    public abstract void chatActivated(BuddyPluginBeta.ChatInstance paramChatInstance);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/BuddyPluginViewInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */