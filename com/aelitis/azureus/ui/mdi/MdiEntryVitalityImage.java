package com.aelitis.azureus.ui.mdi;

public abstract interface MdiEntryVitalityImage
{
  public abstract String getImageID();
  
  public abstract void setImageID(String paramString);
  
  public abstract MdiEntry getMdiEntry();
  
  public abstract void addListener(MdiEntryVitalityImageListener paramMdiEntryVitalityImageListener);
  
  public abstract void setToolTip(String paramString);
  
  public abstract void setVisible(boolean paramBoolean);
  
  public abstract boolean isVisible();
  
  public abstract void triggerClickedListeners(int paramInt1, int paramInt2);
  
  public abstract int getAlignment();
  
  public abstract void setAlignment(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/mdi/MdiEntryVitalityImage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */