package com.aelitis.azureus.ui.swt;

import org.eclipse.swt.widgets.Composite;

public abstract interface UISkinnableSWTListener
{
  public abstract void skinBeforeComponents(Composite paramComposite, Object paramObject, Object[] paramArrayOfObject);
  
  public abstract void skinAfterComponents(Composite paramComposite, Object paramObject, Object[] paramArrayOfObject);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/UISkinnableSWTListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */