package com.aelitis.azureus.ui.common.table;

import org.gudy.azureus2.plugins.ui.tables.TableCell;
import org.gudy.azureus2.plugins.ui.tables.TableCellMouseEvent;

public abstract interface TableCellCore
  extends TableCell, Comparable
{
  public static final int TOOLTIPLISTENER_HOVER = 0;
  public static final int TOOLTIPLISTENER_HOVERCOMPLETE = 1;
  
  public abstract void invalidate(boolean paramBoolean);
  
  public abstract boolean refresh(boolean paramBoolean);
  
  public abstract boolean refresh();
  
  public abstract boolean refresh(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3);
  
  public abstract boolean refresh(boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void dispose();
  
  public abstract boolean needsPainting();
  
  public abstract void locationChanged();
  
  public abstract TableRowCore getTableRowCore();
  
  public abstract TableColumnCore getTableColumnCore();
  
  public abstract void invokeToolTipListeners(int paramInt);
  
  public abstract void invokeMouseListeners(TableCellMouseEvent paramTableCellMouseEvent);
  
  public abstract void invokeVisibilityListeners(int paramInt, boolean paramBoolean);
  
  public abstract void setUpToDate(boolean paramBoolean);
  
  public abstract boolean isUpToDate();
  
  public abstract String getObfusticatedText();
  
  public abstract int getCursorID();
  
  public abstract boolean setCursorID(int paramInt);
  
  public abstract boolean isMouseOver();
  
  public abstract boolean getVisuallyChangedSinceRefresh();
  
  public abstract void refreshAsync();
  
  public abstract void redraw();
  
  public abstract void setDefaultToolTip(Object paramObject);
  
  public abstract Object getDefaultToolTip();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/TableCellCore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */