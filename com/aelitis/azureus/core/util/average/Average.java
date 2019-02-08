package com.aelitis.azureus.core.util.average;

public abstract interface Average
{
  public abstract double update(double paramDouble);
  
  public abstract double getAverage();
  
  public abstract void reset();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/average/Average.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */