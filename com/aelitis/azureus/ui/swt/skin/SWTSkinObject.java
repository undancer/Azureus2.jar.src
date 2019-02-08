package com.aelitis.azureus.ui.swt.skin;

import com.aelitis.azureus.ui.swt.views.skin.SkinView;
import org.eclipse.swt.widgets.Control;
import org.gudy.azureus2.ui.swt.debug.ObfusticateImage;
import org.gudy.azureus2.ui.swt.plugins.PluginUISWTSkinObject;

public abstract interface SWTSkinObject
  extends PluginUISWTSkinObject
{
  public abstract Control getControl();
  
  public abstract String getType();
  
  public abstract String getSkinObjectID();
  
  public abstract String getConfigID();
  
  public abstract SWTSkinObject getParent();
  
  public abstract SWTSkin getSkin();
  
  public abstract void setVisible(boolean paramBoolean);
  
  public abstract boolean isVisible();
  
  public abstract void setDefaultVisibility();
  
  public abstract void setBackground(String paramString1, String paramString2);
  
  public abstract String switchSuffix(String paramString, int paramInt, boolean paramBoolean);
  
  public abstract String switchSuffix(String paramString, int paramInt, boolean paramBoolean1, boolean paramBoolean2);
  
  public abstract String switchSuffix(String paramString);
  
  public abstract String getSuffix();
  
  public abstract void setProperties(SWTSkinProperties paramSWTSkinProperties);
  
  public abstract SWTSkinProperties getProperties();
  
  public abstract void addListener(SWTSkinObjectListener paramSWTSkinObjectListener);
  
  public abstract void removeListener(SWTSkinObjectListener paramSWTSkinObjectListener);
  
  public abstract SWTSkinObjectListener[] getListeners();
  
  public abstract String getViewID();
  
  public abstract void triggerListeners(int paramInt);
  
  public abstract void triggerListeners(int paramInt, Object paramObject);
  
  public abstract void dispose();
  
  public abstract void setTooltipID(String paramString);
  
  public abstract boolean getDefaultVisibility();
  
  public abstract Object getData(String paramString);
  
  public abstract void setData(String paramString, Object paramObject);
  
  public abstract boolean isDisposed();
  
  public abstract boolean isDebug();
  
  public abstract String getTooltipID(boolean paramBoolean);
  
  public abstract void setDebug(boolean paramBoolean);
  
  public abstract void relayout();
  
  public abstract void layoutComplete();
  
  public abstract void setObfusticatedImageGenerator(ObfusticateImage paramObfusticateImage);
  
  public abstract SkinView getSkinView();
  
  public abstract void setSkinView(SkinView paramSkinView);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */