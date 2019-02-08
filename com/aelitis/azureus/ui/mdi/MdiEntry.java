package com.aelitis.azureus.ui.mdi;

import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
import java.util.Map;
import org.gudy.azureus2.plugins.ui.UIPluginView;
import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarEnablerBase;

public abstract interface MdiEntry
  extends UIPluginView
{
  public abstract String getParentID();
  
  public abstract Object getDatasource();
  
  public abstract String getExportableDatasource();
  
  public abstract boolean isCloseable();
  
  public abstract String getId();
  
  public abstract MdiEntryVitalityImage addVitalityImage(String paramString);
  
  public abstract void addListeners(Object paramObject);
  
  public abstract void addListener(MdiCloseListener paramMdiCloseListener);
  
  public abstract void addListener(MdiChildCloseListener paramMdiChildCloseListener);
  
  public abstract void removeListener(MdiCloseListener paramMdiCloseListener);
  
  public abstract void removeListener(MdiChildCloseListener paramMdiChildCloseListener);
  
  public abstract void addListener(MdiEntryOpenListener paramMdiEntryOpenListener);
  
  public abstract void removeListener(MdiEntryOpenListener paramMdiEntryOpenListener);
  
  public abstract void addListener(MdiEntryDatasourceListener paramMdiEntryDatasourceListener);
  
  public abstract void removeListener(MdiEntryDatasourceListener paramMdiEntryDatasourceListener);
  
  public abstract void setImageLeftID(String paramString);
  
  public abstract void setCollapseDisabled(boolean paramBoolean);
  
  public abstract void addListener(MdiEntryDropListener paramMdiEntryDropListener);
  
  public abstract void removeListener(MdiEntryDropListener paramMdiEntryDropListener);
  
  public abstract void setDatasource(Object paramObject);
  
  public abstract void setLogID(String paramString);
  
  public abstract boolean isAdded();
  
  public abstract boolean isDisposed();
  
  public abstract ViewTitleInfo getViewTitleInfo();
  
  public abstract void setViewTitleInfo(ViewTitleInfo paramViewTitleInfo);
  
  public abstract String getLogID();
  
  public abstract MultipleDocumentInterface getMDI();
  
  public abstract MdiEntryVitalityImage[] getVitalityImages();
  
  public abstract boolean close(boolean paramBoolean);
  
  public abstract void updateUI();
  
  public abstract void redraw();
  
  public abstract void addListener(MdiEntryLogIdListener paramMdiEntryLogIdListener);
  
  public abstract void removeListener(MdiEntryLogIdListener paramMdiEntryLogIdListener);
  
  public abstract void hide();
  
  public abstract void requestAttention();
  
  public abstract String getTitle();
  
  public abstract void setTitle(String paramString);
  
  public abstract void setTitleID(String paramString);
  
  public abstract String getImageLeftID();
  
  public abstract boolean isExpanded();
  
  public abstract void setExpanded(boolean paramBoolean);
  
  public abstract void setDefaultExpanded(boolean paramBoolean);
  
  public abstract void expandTo();
  
  public abstract void setParentID(String paramString);
  
  public abstract UIToolBarEnablerBase[] getToolbarEnablers();
  
  public abstract void addToolbarEnabler(UIToolBarEnablerBase paramUIToolBarEnablerBase);
  
  public abstract void removeToolbarEnabler(UIToolBarEnablerBase paramUIToolBarEnablerBase);
  
  public abstract boolean isSelectable();
  
  public abstract void setSelectable(boolean paramBoolean);
  
  public abstract void setPreferredAfterID(String paramString);
  
  public abstract String getPreferredAfterID();
  
  public abstract void setUserData(Object paramObject1, Object paramObject2);
  
  public abstract Object getUserData(Object paramObject);
  
  public abstract Map<String, Object> getAutoOpenInfo();
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/mdi/MdiEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */