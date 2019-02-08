package org.gudy.azureus2.ui.swt.components.graphics;

import org.eclipse.swt.graphics.Color;

public abstract interface ValueSource
{
  public static final int STYLE_NONE = 0;
  public static final int STYLE_UP = 1;
  public static final int STYLE_DOWN = 2;
  public static final int STYLE_NAMED = 4;
  public static final int STYLE_BOLD = 8;
  public static final int STYLE_INVISIBLE = 16;
  public static final int STYLE_DOTTED = 32;
  public static final int STYLE_HIDE_LABEL = 64;
  
  public abstract String getName();
  
  public abstract Color getLineColor();
  
  public abstract boolean isTrimmable();
  
  public abstract int getValue();
  
  public abstract int getStyle();
  
  public abstract int getAlpha();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/graphics/ValueSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */