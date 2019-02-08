package org.gudy.azureus2.pluginsimpl.update.sf;

public abstract interface SFPluginDetails
{
  public abstract String getId();
  
  public abstract String getName();
  
  public abstract String getCategory();
  
  public abstract String getVersion();
  
  public abstract String getDownloadURL()
    throws SFPluginDetailsException;
  
  public abstract String getAuthor()
    throws SFPluginDetailsException;
  
  public abstract String getCVSVersion()
    throws SFPluginDetailsException;
  
  public abstract String getCVSDownloadURL()
    throws SFPluginDetailsException;
  
  public abstract String getDescription()
    throws SFPluginDetailsException;
  
  public abstract String getComment()
    throws SFPluginDetailsException;
  
  public abstract String getRelativeURLBase();
  
  public abstract String getInfoURL();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/update/sf/SFPluginDetails.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */