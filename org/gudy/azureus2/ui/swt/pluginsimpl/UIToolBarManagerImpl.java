/*     */ package org.gudy.azureus2.ui.swt.pluginsimpl;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.ToolBarItem;
/*     */ import com.aelitis.azureus.ui.selectedcontent.ISelectedContent;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentListener;
/*     */ import com.aelitis.azureus.ui.selectedcontent.SelectedContentManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarItem;
/*     */ import org.gudy.azureus2.plugins.ui.toolbar.UIToolBarManager;
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
/*     */ public class UIToolBarManagerImpl
/*     */   implements UIToolBarManagerCore
/*     */ {
/*     */   private static UIToolBarManagerImpl instance;
/*  41 */   private Map<String, UIToolBarItem> items = new LinkedHashMap();
/*     */   
/*  43 */   private Map<String, List<String>> mapGroupToItemIDs = new HashMap();
/*     */   
/*  45 */   public List<ToolBarManagerListener> listListeners = new ArrayList();
/*     */   
/*     */   public static UIToolBarManager getInstance() {
/*  48 */     if (instance == null) {
/*  49 */       instance = new UIToolBarManagerImpl();
/*     */     }
/*  51 */     return instance;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public UIToolBarManagerImpl()
/*     */   {
/*  63 */     SelectedContentManager.addCurrentlySelectedContentListener(new SelectedContentListener()
/*     */     {
/*     */       public void currentlySelectedContentChanged(ISelectedContent[] currentContent, String viewID) {
/*  66 */         if (viewID == null) {
/*  67 */           ToolBarItem[] allSWTToolBarItems = UIToolBarManagerImpl.this.getAllSWTToolBarItems();
/*  68 */           for (ToolBarItem item : allSWTToolBarItems) {
/*  69 */             item.setState(0L);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void addListener(ToolBarManagerListener l) {
/*  77 */     synchronized (this.listListeners) {
/*  78 */       this.listListeners.add(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeListener(ToolBarManagerListener l) {
/*  83 */     synchronized (this.listListeners) {
/*  84 */       this.listListeners.remove(l);
/*     */     }
/*     */   }
/*     */   
/*     */   public UIToolBarItem getToolBarItem(String itemID) {
/*  89 */     return (UIToolBarItem)this.items.get(itemID);
/*     */   }
/*     */   
/*     */   public UIToolBarItem[] getAllToolBarItems() {
/*  93 */     return (UIToolBarItem[])this.items.values().toArray(new UIToolBarItem[0]);
/*     */   }
/*     */   
/*     */   public ToolBarItem[] getAllSWTToolBarItems() {
/*  97 */     return (ToolBarItem[])this.items.values().toArray(new ToolBarItem[0]);
/*     */   }
/*     */   
/*     */   public UIToolBarItem createToolBarItem(String id)
/*     */   {
/* 102 */     UIToolBarItemImpl base = new UIToolBarItemImpl(id);
/* 103 */     return base;
/*     */   }
/*     */   
/*     */   public void addToolBarItem(UIToolBarItem item)
/*     */   {
/* 108 */     addToolBarItem(item, true);
/*     */   }
/*     */   
/*     */   public void addToolBarItem(UIToolBarItem item, boolean trigger)
/*     */   {
/* 113 */     if (item == null) {
/* 114 */       return;
/*     */     }
/* 116 */     if (this.items.containsKey(item.getID())) {
/* 117 */       return;
/*     */     }
/*     */     
/* 120 */     this.items.put(item.getID(), item);
/*     */     
/* 122 */     String groupID = item.getGroupID();
/* 123 */     synchronized (this.mapGroupToItemIDs) {
/* 124 */       List<String> list = (List)this.mapGroupToItemIDs.get(groupID);
/* 125 */       if (list == null) {
/* 126 */         list = new ArrayList();
/* 127 */         this.mapGroupToItemIDs.put(groupID, list);
/*     */       }
/* 129 */       list.add(item.getID());
/*     */     }
/*     */     
/* 132 */     if (trigger) {
/* 133 */       ToolBarManagerListener[] listeners = (ToolBarManagerListener[])this.listListeners.toArray(new ToolBarManagerListener[0]);
/* 134 */       for (ToolBarManagerListener l : listeners) {
/* 135 */         l.toolbarItemAdded(item);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public String[] getToolBarIDsByGroup(String groupID) {
/* 141 */     synchronized (this.mapGroupToItemIDs) {
/* 142 */       List<String> list = (List)this.mapGroupToItemIDs.get(groupID);
/* 143 */       if (list == null) {
/* 144 */         return new String[0];
/*     */       }
/* 146 */       return (String[])list.toArray(new String[0]);
/*     */     }
/*     */   }
/*     */   
/*     */   public UIToolBarItem[] getToolBarItemsByGroup(String groupID) {
/* 151 */     synchronized (this.mapGroupToItemIDs) {
/* 152 */       List<String> list = (List)this.mapGroupToItemIDs.get(groupID);
/* 153 */       if (list == null) {
/* 154 */         return new UIToolBarItem[0];
/*     */       }
/* 156 */       UIToolBarItem[] items = new UIToolBarItem[list.size()];
/* 157 */       int i = 0;
/* 158 */       for (String id : list) {
/* 159 */         items[i] = getToolBarItem(id);
/* 160 */         i++;
/*     */       }
/* 162 */       return items;
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeToolBarItem(String id)
/*     */   {
/* 168 */     UIToolBarItem toolBarItem = (UIToolBarItem)this.items.remove(id);
/* 169 */     if (toolBarItem != null)
/*     */     {
/* 171 */       synchronized (this.mapGroupToItemIDs) {
/* 172 */         List<String> list = (List)this.mapGroupToItemIDs.get(toolBarItem.getGroupID());
/* 173 */         if (list != null) {
/* 174 */           list.remove(toolBarItem.getID());
/*     */         }
/*     */       }
/*     */       
/* 178 */       ToolBarManagerListener[] listeners = (ToolBarManagerListener[])this.listListeners.toArray(new ToolBarManagerListener[0]);
/* 179 */       for (ToolBarManagerListener l : listeners) {
/* 180 */         l.toolbarItemRemoved(toolBarItem);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public String[] getGroupIDs()
/*     */   {
/* 187 */     return (String[])this.mapGroupToItemIDs.keySet().toArray(new String[0]);
/*     */   }
/*     */   
/*     */   public static abstract interface ToolBarManagerListener
/*     */   {
/*     */     public abstract void toolbarItemRemoved(UIToolBarItem paramUIToolBarItem);
/*     */     
/*     */     public abstract void toolbarItemAdded(UIToolBarItem paramUIToolBarItem);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/UIToolBarManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */