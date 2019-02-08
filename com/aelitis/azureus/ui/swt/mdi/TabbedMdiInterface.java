package com.aelitis.azureus.ui.swt.mdi;

import org.eclipse.swt.custom.CTabFolder;

public abstract interface TabbedMdiInterface
  extends MultipleDocumentInterfaceSWT
{
  public abstract CTabFolder getTabFolder();
  
  public abstract void setMaximizeVisible(boolean paramBoolean);
  
  public abstract void setMinimizeVisible(boolean paramBoolean);
  
  public abstract boolean getMinimized();
  
  public abstract void setMinimized(boolean paramBoolean);
  
  public abstract int getFolderHeight();
  
  public abstract void addListener(MdiSWTMenuHackListener paramMdiSWTMenuHackListener);
  
  public abstract void setTabbedMdiMaximizeListener(TabbedMdiMaximizeListener paramTabbedMdiMaximizeListener);
  
  public abstract void updateUI();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/mdi/TabbedMdiInterface.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */