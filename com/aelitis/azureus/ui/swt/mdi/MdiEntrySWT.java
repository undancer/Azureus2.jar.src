package com.aelitis.azureus.ui.swt.mdi;

import com.aelitis.azureus.ui.mdi.MdiEntry;
import org.eclipse.swt.graphics.Image;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCore;

public abstract interface MdiEntrySWT
  extends MdiEntry, UISWTViewCore
{
  public abstract UISWTViewEventListener getEventListener();
  
  public abstract void addListener(MdiSWTMenuHackListener paramMdiSWTMenuHackListener);
  
  public abstract void removeListener(MdiSWTMenuHackListener paramMdiSWTMenuHackListener);
  
  public abstract void setImageLeft(Image paramImage);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/mdi/MdiEntrySWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */