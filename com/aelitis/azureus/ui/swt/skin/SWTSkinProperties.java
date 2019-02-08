package com.aelitis.azureus.ui.swt.skin;

import com.aelitis.azureus.ui.skin.SkinProperties;
import org.eclipse.swt.graphics.Color;

public abstract interface SWTSkinProperties
  extends SkinProperties
{
  public abstract Color getColor(String paramString);
  
  public abstract Color getColor(String paramString, Color paramColor);
  
  public abstract SWTColorWithAlpha getColorWithAlpha(String paramString);
  
  public abstract int getPxValue(String paramString, int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinProperties.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */