package com.aelitis.azureus.core.pairing;

public abstract interface PairedService
{
  public abstract String getSID();
  
  public abstract PairingConnectionData getConnectionData();
  
  public abstract void remove();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/pairing/PairedService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */