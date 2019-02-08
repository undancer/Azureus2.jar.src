package com.aelitis.azureus.core.custom;

public abstract interface CustomizationManager
{
  public abstract boolean preInitialize();
  
  public abstract void initialize();
  
  public abstract Customization getActiveCustomization();
  
  public abstract Customization[] getCustomizations();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/custom/CustomizationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */