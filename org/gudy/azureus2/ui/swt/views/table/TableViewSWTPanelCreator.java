package org.gudy.azureus2.ui.swt.views.table;

import com.aelitis.azureus.ui.common.table.TableViewCreator;
import org.eclipse.swt.widgets.Composite;

public abstract interface TableViewSWTPanelCreator
  extends TableViewCreator
{
  public abstract Composite createTableViewPanel(Composite paramComposite);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/TableViewSWTPanelCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */