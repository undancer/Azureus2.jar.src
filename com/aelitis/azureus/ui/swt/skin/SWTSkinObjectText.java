package com.aelitis.azureus.ui.swt.skin;

import org.eclipse.swt.graphics.Color;

public abstract interface SWTSkinObjectText
  extends SWTSkinObject
{
  public abstract void setText(String paramString);
  
  public abstract void setTextID(String paramString);
  
  public abstract void setTextID(String paramString, String[] paramArrayOfString);
  
  public abstract int getStyle();
  
  public abstract void setStyle(int paramInt);
  
  public abstract String getText();
  
  public abstract void addUrlClickedListener(SWTSkinObjectText_UrlClickedListener paramSWTSkinObjectText_UrlClickedListener);
  
  public abstract void removeUrlClickedListener(SWTSkinObjectText_UrlClickedListener paramSWTSkinObjectText_UrlClickedListener);
  
  public abstract void setTextColor(Color paramColor);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectText.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */