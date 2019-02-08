package org.gudy.azureus2.plugins.dht.mainline;

public abstract interface MainlineDHTProvider
{
  public abstract void notifyOfIncomingPort(String paramString, int paramInt);
  
  public abstract int getDHTPort();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/dht/mainline/MainlineDHTProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */