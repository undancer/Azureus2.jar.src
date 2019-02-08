/*     */ package org.gudy.azureus2.ui.common.util;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
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
/*     */ public class MenuItemManager
/*     */ {
/*     */   private static MenuItemManager instance;
/*  39 */   private static AEMonitor class_mon = new AEMonitor("MenuManager");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private Map<String, Map<String, List<MenuItem>>> items_map;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  57 */   private AEMonitor items_mon = new AEMonitor("MenuManager:items");
/*     */   
/*  59 */   private ArrayList<MenuItemManagerListener> listeners = new ArrayList(0);
/*     */   
/*     */   private MenuItemManager() {
/*  62 */     this.items_map = new HashMap();
/*     */   }
/*     */   
/*     */ 
/*     */   public static MenuItemManager getInstance()
/*     */   {
/*     */     try
/*     */     {
/*  70 */       class_mon.enter();
/*  71 */       if (instance == null)
/*  72 */         instance = new MenuItemManager();
/*  73 */       return instance;
/*     */     } finally {
/*  75 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addMenuItem(MenuItem item) {
/*     */     try {
/*  81 */       String name = item.getResourceKey();
/*  82 */       String sMenuID = item.getMenuID();
/*     */       try
/*     */       {
/*  85 */         this.items_mon.enter();
/*  86 */         Map<String, List<MenuItem>> mTypes = (Map)this.items_map.get(sMenuID);
/*     */         
/*  88 */         if (mTypes == null)
/*     */         {
/*  90 */           mTypes = new LinkedHashMap();
/*     */           
/*  92 */           this.items_map.put(sMenuID, mTypes);
/*     */         }
/*     */         
/*  95 */         List<MenuItem> mis = (List)mTypes.get(name);
/*     */         
/*  97 */         if (mis == null)
/*     */         {
/*  99 */           mis = new ArrayList(1);
/*     */           
/* 101 */           mTypes.put(name, mis);
/*     */         }
/*     */         
/* 104 */         mis.add(item);
/*     */       }
/*     */       finally
/*     */       {
/* 108 */         this.items_mon.exit();
/*     */       }
/*     */     } catch (Exception e) {
/* 111 */       System.out.println("Error while adding Menu Item");
/* 112 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeAllMenuItems(String sMenuID)
/*     */   {
/*     */     try {
/* 119 */       this.items_mon.enter();
/*     */       
/* 121 */       Map<String, List<MenuItem>> mTypes = (Map)this.items_map.get(sMenuID);
/*     */       
/* 123 */       if (mTypes == null) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 131 */       Iterator<Map.Entry<String, List<MenuItem>>> it = mTypes.entrySet().iterator();
/*     */       
/* 133 */       while (it.hasNext())
/*     */       {
/* 135 */         List<MenuItem> mis = (List)((Map.Entry)it.next()).getValue();
/*     */         
/* 137 */         if (mis.size() > 0) {
/* 138 */           mis.remove(0);
/*     */         }
/*     */         
/* 141 */         if (mis.size() == 0)
/*     */         {
/* 143 */           it.remove();
/*     */         }
/*     */       }
/*     */       
/* 147 */       if (mTypes.isEmpty())
/*     */       {
/* 149 */         this.items_map.remove(sMenuID);
/*     */       }
/*     */     }
/*     */     finally {
/* 153 */       this.items_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeMenuItem(MenuItem item)
/*     */   {
/*     */     try {
/* 160 */       this.items_mon.enter();
/*     */       
/* 162 */       Map<String, List<MenuItem>> menu_item_map = (Map)this.items_map.get(item.getMenuID());
/*     */       
/* 164 */       if (menu_item_map != null)
/*     */       {
/* 166 */         List<MenuItem> mis = (List)menu_item_map.get(item.getResourceKey());
/*     */         
/* 168 */         if (mis != null)
/*     */         {
/* 170 */           if (!mis.remove(item))
/*     */           {
/* 172 */             if (mis.size() > 0)
/*     */             {
/* 174 */               mis.remove(0);
/*     */             }
/*     */           }
/*     */           
/* 178 */           if (mis.size() == 0)
/*     */           {
/* 180 */             menu_item_map.remove(item.getResourceKey());
/*     */           }
/*     */         }
/*     */         
/* 184 */         if (menu_item_map.isEmpty())
/*     */         {
/* 186 */           this.items_map.remove(item.getMenuID());
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 191 */       this.items_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public MenuItem[] getAllAsArray(String sMenuID)
/*     */   {
/* 200 */     if (sMenuID != null) {
/* 201 */       triggerMenuItemQuery(sMenuID);
/*     */     }
/*     */     try
/*     */     {
/* 205 */       this.items_mon.enter();
/*     */       
/* 207 */       Map<String, List<MenuItem>> local_menu_item_map = (Map)this.items_map.get(sMenuID);
/* 208 */       Map<String, List<MenuItem>> global_menu_item_map = (Map)this.items_map.get(null);
/*     */       
/*     */ 
/* 211 */       if ((local_menu_item_map == null) && (global_menu_item_map == null)) {
/* 212 */         return new MenuItem[0];
/*     */       }
/*     */       
/* 215 */       if (sMenuID == null) { local_menu_item_map = null;
/*     */       }
/* 217 */       Object l = new ArrayList();
/*     */       
/* 219 */       if (local_menu_item_map != null)
/*     */       {
/* 221 */         for (List<MenuItem> mis : local_menu_item_map.values())
/*     */         {
/* 223 */           if (mis.size() > 0)
/*     */           {
/* 225 */             ((ArrayList)l).add(mis.get(0));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 230 */       if (global_menu_item_map != null)
/*     */       {
/* 232 */         for (List<MenuItem> mis : global_menu_item_map.values())
/*     */         {
/* 234 */           if (mis.size() > 0)
/*     */           {
/* 236 */             ((ArrayList)l).add(mis.get(0));
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 241 */       return (MenuItem[])((ArrayList)l).toArray(new MenuItem[((ArrayList)l).size()]);
/*     */     }
/*     */     finally
/*     */     {
/* 245 */       this.items_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public MenuItem[] getAllAsArray(String[] menu_ids)
/*     */   {
/* 254 */     ArrayList<MenuItem> l = new ArrayList();
/* 255 */     for (int i = 0; i < menu_ids.length; i++) {
/* 256 */       if (menu_ids[i] != null) {
/* 257 */         triggerMenuItemQuery(menu_ids[i]);
/*     */       }
/* 259 */       extractMenuItems(menu_ids[i], l);
/*     */     }
/* 261 */     extractMenuItems(null, l);
/* 262 */     return (MenuItem[])l.toArray(new MenuItem[l.size()]);
/*     */   }
/*     */   
/*     */   private void extractMenuItems(String menu_id, ArrayList<MenuItem> l) {
/*     */     try {
/* 267 */       this.items_mon.enter();
/*     */       
/* 269 */       Map<String, List<MenuItem>> menu_map = (Map)this.items_map.get(menu_id);
/*     */       
/* 271 */       if (menu_map != null)
/*     */       {
/* 273 */         for (List<MenuItem> mis : menu_map.values())
/*     */         {
/* 275 */           if (mis.size() > 0)
/*     */           {
/* 277 */             l.add(mis.get(0));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 283 */       this.items_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addListener(MenuItemManagerListener l) {
/* 288 */     synchronized (this.listeners) {
/* 289 */       if (!this.listeners.contains(l)) {
/* 290 */         this.listeners.add(l);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeListener(MenuItemManagerListener l) {
/* 296 */     synchronized (this.listeners) {
/* 297 */       this.listeners.remove(l);
/*     */     }
/*     */   }
/*     */   
/*     */   private void triggerMenuItemQuery(String id) {
/*     */     MenuItemManagerListener[] listenersArray;
/* 303 */     synchronized (this.listeners) {
/* 304 */       listenersArray = (MenuItemManagerListener[])this.listeners.toArray(new MenuItemManagerListener[0]);
/*     */     }
/* 306 */     for (MenuItemManagerListener l : listenersArray) {
/*     */       try {
/* 308 */         l.queryForMenuItem(id);
/*     */       } catch (Exception e) {
/* 310 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void triggerMenuItemCleanup(String id) {
/*     */     MenuItemManagerListener[] listenersArray;
/* 317 */     synchronized (this.listeners) {
/* 318 */       listenersArray = (MenuItemManagerListener[])this.listeners.toArray(new MenuItemManagerListener[0]);
/*     */     }
/* 320 */     for (MenuItemManagerListener l : listenersArray) {
/*     */       try {
/* 322 */         l.cleanupMenuItem(id);
/*     */       } catch (Exception e) {
/* 324 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/common/util/MenuItemManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */