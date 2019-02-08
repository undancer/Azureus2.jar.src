package org.gudy.azureus2.pluginsimpl.remote;

public abstract interface RPRequestDispatcher
{
  public abstract RPPluginInterface getPlugin();
  
  public abstract RPReply dispatch(RPRequest paramRPRequest)
    throws RPException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/RPRequestDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */