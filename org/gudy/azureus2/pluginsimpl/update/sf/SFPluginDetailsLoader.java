package org.gudy.azureus2.pluginsimpl.update.sf;

public abstract interface SFPluginDetailsLoader
{
  public abstract String[] getPluginIDs()
    throws SFPluginDetailsException;
  
  public abstract SFPluginDetails getPluginDetails(String paramString)
    throws SFPluginDetailsException;
  
  public abstract SFPluginDetails[] getPluginDetails()
    throws SFPluginDetailsException;
  
  public abstract void reset();
  
  public abstract void addListener(SFPluginDetailsLoaderListener paramSFPluginDetailsLoaderListener);
  
  public abstract void removeListener(SFPluginDetailsLoaderListener paramSFPluginDetailsLoaderListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/update/sf/SFPluginDetailsLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */