package com.aelitis.azureus.core.metasearch;

public abstract interface MetaSearchListener
{
  public abstract void engineAdded(Engine paramEngine);
  
  public abstract void engineUpdated(Engine paramEngine);
  
  public abstract void engineRemoved(Engine paramEngine);
  
  public abstract void engineStateChanged(Engine paramEngine);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/MetaSearchListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */