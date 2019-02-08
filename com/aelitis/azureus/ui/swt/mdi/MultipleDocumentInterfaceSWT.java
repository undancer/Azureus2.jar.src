package com.aelitis.azureus.ui.swt.mdi;

import com.aelitis.azureus.ui.mdi.MdiEntry;
import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
import org.gudy.azureus2.ui.swt.plugins.PluginUISWTSkinObject;
import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCore;

public abstract interface MultipleDocumentInterfaceSWT
  extends MultipleDocumentInterface
{
  public abstract MdiEntry getEntryBySkinView(Object paramObject);
  
  public abstract UISWTViewCore getCoreViewFromID(String paramString);
  
  public abstract MdiEntry createEntryFromEventListener(String paramString1, UISWTViewEventListener paramUISWTViewEventListener, String paramString2, boolean paramBoolean, Object paramObject, String paramString3);
  
  public abstract MdiEntry createEntryFromEventListener(String paramString1, String paramString2, UISWTViewEventListener paramUISWTViewEventListener, String paramString3, boolean paramBoolean, Object paramObject, String paramString4);
  
  public abstract MdiEntry createEntryFromEventListener(String paramString1, Class<? extends UISWTViewEventListener> paramClass, String paramString2, boolean paramBoolean, Object paramObject, String paramString3);
  
  public abstract MdiEntrySWT getEntrySWT(String paramString);
  
  public abstract MdiEntrySWT getCurrentEntrySWT();
  
  public abstract MdiEntrySWT getEntryFromSkinObject(PluginUISWTSkinObject paramPluginUISWTSkinObject);
  
  public abstract void setCloseableConfigFile(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/mdi/MultipleDocumentInterfaceSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */