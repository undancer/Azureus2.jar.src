/*     */ package org.gudy.azureus2.ui.swt.views.table.utils;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
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
/*     */ public class TableContextMenuManager
/*     */ {
/*     */   private static TableContextMenuManager instance;
/*  41 */   private static AEMonitor class_mon = new AEMonitor("TableContextMenuManager");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private Map items;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  51 */   private AEMonitor items_mon = new AEMonitor("TableContextMenuManager:items");
/*     */   
/*     */   private TableContextMenuManager()
/*     */   {
/*  55 */     this.items = new HashMap();
/*     */   }
/*     */   
/*     */ 
/*     */   public static TableContextMenuManager getInstance()
/*     */   {
/*     */     try
/*     */     {
/*  63 */       class_mon.enter();
/*     */       
/*  65 */       if (instance == null)
/*  66 */         instance = new TableContextMenuManager();
/*  67 */       return instance;
/*     */     }
/*     */     finally {
/*  70 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addContextMenuItem(TableContextMenuItem item) {
/*     */     try {
/*  76 */       String name = item.getResourceKey();
/*  77 */       String sTableID = item.getTableID();
/*     */       try {
/*  79 */         this.items_mon.enter();
/*     */         
/*  81 */         Map mTypes = (Map)this.items.get(sTableID);
/*  82 */         if (mTypes == null)
/*     */         {
/*  84 */           mTypes = new LinkedHashMap();
/*  85 */           this.items.put(sTableID, mTypes);
/*     */         }
/*  87 */         mTypes.put(name, item);
/*     */       }
/*     */       finally
/*     */       {
/*  91 */         this.items_mon.exit();
/*     */       }
/*     */     } catch (Exception e) {
/*  94 */       System.out.println("Error while adding Context Table Menu Item");
/*  95 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeContextMenuItem(TableContextMenuItem item) {
/* 100 */     Map menu_item_map = (Map)this.items.get(item.getTableID());
/* 101 */     if (menu_item_map != null) menu_item_map.remove(item.getResourceKey());
/*     */   }
/*     */   
/*     */   public TableContextMenuItem[] getAllAsArray(String sMenuID) {
/* 105 */     Map local_menu_item_map = (Map)this.items.get(sMenuID);
/* 106 */     Map global_menu_item_map = (Map)this.items.get(null);
/* 107 */     if ((local_menu_item_map == null) && (global_menu_item_map == null)) {
/* 108 */       return new TableContextMenuItem[0];
/*     */     }
/*     */     
/* 111 */     ArrayList l = new ArrayList();
/* 112 */     if (local_menu_item_map != null) l.addAll(local_menu_item_map.values());
/* 113 */     if (global_menu_item_map != null) l.addAll(global_menu_item_map.values());
/* 114 */     return (TableContextMenuItem[])l.toArray(new TableContextMenuItem[l.size()]);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/utils/TableContextMenuManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */