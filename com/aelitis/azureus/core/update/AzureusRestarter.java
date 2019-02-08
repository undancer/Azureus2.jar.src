package com.aelitis.azureus.core.update;

import com.aelitis.azureus.core.AzureusCoreException;

public abstract interface AzureusRestarter
{
  public abstract void restart(boolean paramBoolean);
  
  public abstract void updateNow()
    throws AzureusCoreException;
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/update/AzureusRestarter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */