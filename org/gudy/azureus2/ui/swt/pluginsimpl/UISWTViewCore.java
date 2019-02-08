package org.gudy.azureus2.ui.swt.pluginsimpl;

import org.eclipse.swt.widgets.Composite;
import org.gudy.azureus2.ui.swt.plugins.PluginUISWTSkinObject;
import org.gudy.azureus2.ui.swt.plugins.UISWTView;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;

public abstract interface UISWTViewCore
  extends UISWTView
{
  public static final int CONTROLTYPE_SKINOBJECT = 257;
  
  public abstract void initialize(Composite paramComposite);
  
  public abstract Composite getComposite();
  
  public abstract String getTitleID();
  
  public abstract String getFullTitle();
  
  public abstract void setPluginSkinObject(PluginUISWTSkinObject paramPluginUISWTSkinObject);
  
  public abstract PluginUISWTSkinObject getPluginSkinObject();
  
  public abstract void setUseCoreDataSource(boolean paramBoolean);
  
  public abstract boolean useCoreDataSource();
  
  public abstract UISWTViewEventListener getEventListener();
  
  public abstract void setUserData(Object paramObject1, Object paramObject2);
  
  public abstract Object getUserData(Object paramObject);
  
  public abstract void setParentView(UISWTView paramUISWTView);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/UISWTViewCore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */