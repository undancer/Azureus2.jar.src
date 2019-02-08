package org.gudy.azureus2.ui.swt.views.table;

import com.aelitis.azureus.ui.common.table.TableColumnCore;
import com.aelitis.azureus.ui.common.table.TableRowCore;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public abstract interface TableRowSWTPaintListener
{
  public abstract void rowPaint(GC paramGC, TableRowCore paramTableRowCore, TableColumnCore paramTableColumnCore, Rectangle paramRectangle);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/TableRowSWTPaintListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */