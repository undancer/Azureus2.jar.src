package com.aelitis.azureus.core.neuronal;

public abstract interface ActivationFunction
{
  public abstract double getValueFor(double paramDouble);
  
  public abstract double getDerivedFunctionValueFor(double paramDouble);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/neuronal/ActivationFunction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */