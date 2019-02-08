package com.aelitis.azureus.core.metasearch;

public abstract interface ResultListener
{
  public abstract void contentReceived(Engine paramEngine, String paramString);
  
  public abstract void matchFound(Engine paramEngine, String[] paramArrayOfString);
  
  public abstract void resultsReceived(Engine paramEngine, Result[] paramArrayOfResult);
  
  public abstract void resultsComplete(Engine paramEngine);
  
  public abstract void engineFailed(Engine paramEngine, Throwable paramThrowable);
  
  public abstract void engineRequiresLogin(Engine paramEngine, Throwable paramThrowable);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/ResultListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */