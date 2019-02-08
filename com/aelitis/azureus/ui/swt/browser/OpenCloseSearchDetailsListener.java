package com.aelitis.azureus.ui.swt.browser;

import java.util.Map;

public abstract interface OpenCloseSearchDetailsListener
{
  public abstract void openSearchResults(Map paramMap);
  
  public abstract void closeSearchResults(Map paramMap);
  
  public abstract void resizeMainBrowser();
  
  public abstract void resizeSecondaryBrowser();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/browser/OpenCloseSearchDetailsListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */