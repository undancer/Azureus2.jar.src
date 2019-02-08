package org.gudy.azureus2.plugins.ui.config;

public abstract interface ColorParameter
  extends Parameter
{
  public abstract int getRedValue();
  
  public abstract int getGreenValue();
  
  public abstract int getBlueValue();
  
  public abstract void setRGBValue(int paramInt1, int paramInt2, int paramInt3);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/config/ColorParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */