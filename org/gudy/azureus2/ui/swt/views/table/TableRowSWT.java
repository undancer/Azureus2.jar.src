package org.gudy.azureus2.ui.swt.views.table;

import com.aelitis.azureus.ui.common.table.TableRowCore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public abstract interface TableRowSWT
  extends TableRowCore
{
  public abstract boolean setIconSize(Point paramPoint);
  
  public abstract Color getForeground();
  
  public abstract boolean setForeground(Color paramColor);
  
  public abstract Color getBackground();
  
  public abstract TableCellSWT getTableCellSWT(String paramString);
  
  public abstract Rectangle getBounds();
  
  public abstract void setBackgroundImage(Image paramImage);
  
  public abstract int getFontStyle();
  
  public abstract boolean setFontStyle(int paramInt);
  
  public abstract int getAlpha();
  
  public abstract boolean setAlpha(int paramInt);
  
  public abstract void setWidgetSelected(boolean paramBoolean);
  
  public abstract boolean setShown(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract int getFullHeight();
  
  public abstract boolean isShown();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/TableRowSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */