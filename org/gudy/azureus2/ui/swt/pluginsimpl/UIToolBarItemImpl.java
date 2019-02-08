/*     */ package org.gudy.azureus2.ui.swt.pluginsimpl;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem.ToolBarItemListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarActivationListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UIToolBarItemImpl
/*     */   implements ToolBarItem
/*     */ {
/*     */   private String id;
/*  42 */   private String imageID = "image.toolbar.run";
/*     */   
/*     */   private String textID;
/*     */   
/*  46 */   private boolean alwaysAvailable = false;
/*     */   
/*     */   private long state;
/*     */   
/*     */   private UIToolBarActivationListener defaultActivation;
/*     */   
/*     */   private String tooltipID;
/*     */   
/*  54 */   private String groupID = "main";
/*     */   
/*  56 */   private List<ToolBarItem.ToolBarItemListener> toolBarItemListeners = new ArrayList();
/*     */   private String toolTip;
/*     */   
/*     */   public UIToolBarItemImpl(String id)
/*     */   {
/*  61 */     this.id = id;
/*     */   }
/*     */   
/*     */   public void addToolBarItemListener(ToolBarItem.ToolBarItemListener l)
/*     */   {
/*  66 */     if (!this.toolBarItemListeners.contains(l)) {
/*  67 */       this.toolBarItemListeners.add(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeToolBarItemListener(ToolBarItem.ToolBarItemListener l)
/*     */   {
/*  73 */     this.toolBarItemListeners.remove(l);
/*     */   }
/*     */   
/*     */   private void triggerFieldChange() {
/*  77 */     ToolBarItem.ToolBarItemListener[] array = (ToolBarItem.ToolBarItemListener[])this.toolBarItemListeners.toArray(new ToolBarItem.ToolBarItemListener[0]);
/*  78 */     for (ToolBarItem.ToolBarItemListener l : array) {
/*  79 */       l.uiFieldChanged(this);
/*     */     }
/*     */   }
/*     */   
/*     */   public String getID()
/*     */   {
/*  85 */     return this.id;
/*     */   }
/*     */   
/*     */   public String getTextID()
/*     */   {
/*  90 */     return this.textID;
/*     */   }
/*     */   
/*     */   public void setTextID(String id)
/*     */   {
/*  95 */     this.textID = id;
/*  96 */     triggerFieldChange();
/*     */   }
/*     */   
/*     */   public String getImageID()
/*     */   {
/* 101 */     return this.imageID;
/*     */   }
/*     */   
/*     */   public void setImageID(String id)
/*     */   {
/* 106 */     this.imageID = id;
/* 107 */     triggerFieldChange();
/*     */   }
/*     */   
/*     */   public boolean isAlwaysAvailable()
/*     */   {
/* 112 */     return this.alwaysAvailable;
/*     */   }
/*     */   
/*     */   public void setAlwaysAvailable(boolean alwaysAvailable) {
/* 116 */     this.alwaysAvailable = alwaysAvailable;
/* 117 */     triggerFieldChange();
/*     */   }
/*     */   
/*     */   public long getState()
/*     */   {
/* 122 */     return this.state;
/*     */   }
/*     */   
/*     */   public void setState(long state)
/*     */   {
/* 127 */     this.state = state;
/* 128 */     triggerFieldChange();
/*     */   }
/*     */   
/*     */   public boolean triggerToolBarItem(long activationType, Object datasource)
/*     */   {
/* 133 */     ToolBarItem.ToolBarItemListener[] array = (ToolBarItem.ToolBarItemListener[])this.toolBarItemListeners.toArray(new ToolBarItem.ToolBarItemListener[0]);
/* 134 */     for (ToolBarItem.ToolBarItemListener l : array) {
/* 135 */       if (l.triggerToolBarItem(this, activationType, datasource)) {
/* 136 */         return true;
/*     */       }
/*     */     }
/* 139 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void setDefaultActivationListener(UIToolBarActivationListener defaultActivation)
/*     */   {
/* 145 */     this.defaultActivation = defaultActivation;
/*     */   }
/*     */   
/*     */   public UIToolBarActivationListener getDefaultActivationListener()
/*     */   {
/* 150 */     return this.defaultActivation;
/*     */   }
/*     */   
/*     */   public String getTooltipID()
/*     */   {
/* 155 */     return this.tooltipID;
/*     */   }
/*     */   
/*     */   public void setTooltipID(String tooltipID) {
/* 159 */     this.tooltipID = tooltipID;
/*     */   }
/*     */   
/*     */   public String getGroupID()
/*     */   {
/* 164 */     return this.groupID;
/*     */   }
/*     */   
/*     */   public void setGroupID(String groupID)
/*     */   {
/* 169 */     this.groupID = groupID;
/*     */   }
/*     */   
/*     */   public void setToolTip(String text)
/*     */   {
/* 174 */     this.toolTip = text;
/*     */   }
/*     */   
/*     */   public String getToolTip()
/*     */   {
/* 179 */     return this.toolTip;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/UIToolBarItemImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */