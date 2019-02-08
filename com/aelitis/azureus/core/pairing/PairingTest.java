package com.aelitis.azureus.core.pairing;

public abstract interface PairingTest
{
  public static final int OT_PENDING = 0;
  public static final int OT_SUCCESS = 1;
  public static final int OT_FAILED = 2;
  public static final int OT_SERVER_UNAVAILABLE = 3;
  public static final int OT_SERVER_OVERLOADED = 4;
  public static final int OT_SERVER_FAILED = 5;
  public static final int OT_CANCELLED = 6;
  
  public abstract int getOutcome();
  
  public abstract String getErrorMessage();
  
  public abstract void cancel();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/pairing/PairingTest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */