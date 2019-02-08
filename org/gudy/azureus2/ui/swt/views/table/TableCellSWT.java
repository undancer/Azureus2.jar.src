package org.gudy.azureus2.ui.swt.views.table;

import com.aelitis.azureus.ui.common.table.TableCellCore;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public abstract interface TableCellSWT
  extends TableCellCore
{
  public abstract boolean setForeground(Color paramColor);
  
  public abstract Image getIcon();
  
  public abstract boolean setIcon(Image paramImage);
  
  public abstract void doPaint(GC paramGC);
  
  public abstract Point getSize();
  
  public abstract Rectangle getBounds();
  
  public abstract Rectangle getBoundsOnDisplay();
  
  public abstract boolean setGraphic(Image paramImage);
  
  public abstract Image getGraphicSWT();
  
  public abstract Image getBackgroundImage();
  
  public abstract Color getForegroundSWT();
  
  public abstract TableRowSWT getTableRowSWT();
  
  public abstract Color getBackgroundSWT();
  
  public abstract int getTextAlpha();
  
  public abstract void setTextAlpha(int paramInt);
  
  public abstract void setMouseOver(boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/TableCellSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */