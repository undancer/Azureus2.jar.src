package org.gudy.azureus2.ui.swt.views.table;

import com.aelitis.azureus.ui.common.table.TableCellCore;
import com.aelitis.azureus.ui.common.table.TableColumnCore;
import com.aelitis.azureus.ui.common.table.TableRowCore;
import com.aelitis.azureus.ui.common.table.TableView;
import com.aelitis.azureus.ui.common.table.TableViewFilterCheck;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.gudy.azureus2.plugins.ui.tables.TableRowMouseEvent;
import org.gudy.azureus2.plugins.ui.tables.TableRowMouseListener;
import org.gudy.azureus2.plugins.ui.tables.TableRowRefreshListener;
import org.gudy.azureus2.ui.swt.plugins.UISWTView;
import org.gudy.azureus2.ui.swt.views.table.impl.TableViewSWT_TabsCommon;

public abstract interface TableViewSWT<DATASOURCETYPE>
  extends TableView<DATASOURCETYPE>
{
  public abstract void addKeyListener(KeyListener paramKeyListener);
  
  public abstract void addMenuFillListener(TableViewSWTMenuFillListener paramTableViewSWTMenuFillListener);
  
  public abstract DragSource createDragSource(int paramInt);
  
  public abstract DropTarget createDropTarget(int paramInt);
  
  public abstract Composite getComposite();
  
  public abstract TableRowCore getRow(DropTargetEvent paramDropTargetEvent);
  
  public abstract TableRowSWT getRowSWT(DATASOURCETYPE paramDATASOURCETYPE);
  
  public abstract Composite getTableComposite();
  
  public abstract void initialize(Composite paramComposite);
  
  public abstract void initialize(UISWTView paramUISWTView, Composite paramComposite);
  
  public abstract Image obfusticatedImage(Image paramImage);
  
  public abstract void removeKeyListener(KeyListener paramKeyListener);
  
  public abstract void setMainPanelCreator(TableViewSWTPanelCreator paramTableViewSWTPanelCreator);
  
  public abstract TableCellCore getTableCell(int paramInt1, int paramInt2);
  
  public abstract Point getTableCellMouseOffset(TableCellSWT paramTableCellSWT);
  
  public abstract void removeRefreshListener(TableRowRefreshListener paramTableRowRefreshListener);
  
  public abstract void addRefreshListener(TableRowRefreshListener paramTableRowRefreshListener);
  
  public abstract String getFilterText();
  
  public abstract void enableFilterCheck(Text paramText, TableViewFilterCheck<DATASOURCETYPE> paramTableViewFilterCheck);
  
  public abstract Text getFilterControl();
  
  public abstract void disableFilterCheck();
  
  public abstract boolean isFiltered(DATASOURCETYPE paramDATASOURCETYPE);
  
  public abstract void setFilterText(String paramString);
  
  public abstract boolean enableSizeSlider(Composite paramComposite, int paramInt1, int paramInt2);
  
  public abstract void disableSizeSlider();
  
  public abstract void addRowPaintListener(TableRowSWTPaintListener paramTableRowSWTPaintListener);
  
  public abstract void removeRowPaintListener(TableRowSWTPaintListener paramTableRowSWTPaintListener);
  
  public abstract void removeRowMouseListener(TableRowMouseListener paramTableRowMouseListener);
  
  public abstract void addRowMouseListener(TableRowMouseListener paramTableRowMouseListener);
  
  public abstract void refilter();
  
  public abstract void setMenuEnabled(boolean paramBoolean);
  
  public abstract boolean isMenuEnabled();
  
  public abstract void packColumns();
  
  public abstract void visibleRowsChanged();
  
  public abstract void invokePaintListeners(GC paramGC, TableRowCore paramTableRowCore, TableColumnCore paramTableColumnCore, Rectangle paramRectangle);
  
  public abstract boolean isVisible();
  
  public abstract TableColumnCore getTableColumnByOffset(int paramInt);
  
  public abstract TableRowSWT getTableRow(int paramInt1, int paramInt2, boolean paramBoolean);
  
  public abstract void setRowSelected(TableRowCore paramTableRowCore, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract void editCell(TableColumnCore paramTableColumnCore, int paramInt);
  
  public abstract void invokeRowMouseListener(TableRowMouseEvent paramTableRowMouseEvent);
  
  public abstract boolean isDragging();
  
  public abstract KeyListener[] getKeyListeners();
  
  public abstract TableViewSWTFilter getSWTFilter();
  
  public abstract void triggerDefaultSelectedListeners(TableRowCore[] paramArrayOfTableRowCore, int paramInt);
  
  public abstract void sortColumn(boolean paramBoolean);
  
  public abstract void openFilterDialog();
  
  public abstract boolean isSingleSelection();
  
  public abstract void expandColumns();
  
  public abstract void showColumnEditor();
  
  public abstract boolean isTabViewsEnabled();
  
  public abstract boolean getTabViewsExpandedByDefault();
  
  public abstract String[] getTabViewsRestrictedTo();
  
  public abstract Composite createMainPanel(Composite paramComposite);
  
  public abstract void tableInvalidate();
  
  public abstract void showRow(TableRowCore paramTableRowCore);
  
  public abstract TableRowCore getRowQuick(int paramInt);
  
  public abstract void invokeRefreshListeners(TableRowCore paramTableRowCore);
  
  public abstract TableViewSWT_TabsCommon getTabsCommon();
  
  public abstract void invokeExpansionChangeListeners(TableRowCore paramTableRowCore, boolean paramBoolean);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/TableViewSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */