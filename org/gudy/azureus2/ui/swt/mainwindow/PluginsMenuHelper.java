/*     */ package org.gudy.azureus2.ui.swt.mainwindow;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.TreeMap;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.plugins.PluginConfig;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.FormattersImpl;
/*     */ import org.gudy.azureus2.ui.common.util.MenuItemManager;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.BasicPluginViewImpl;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewEventListenerHolder;
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
/*     */ public class PluginsMenuHelper
/*     */ {
/*  42 */   private static PluginsMenuHelper INSTANCE = null;
/*     */   
/*  44 */   private AEMonitor plugin_helper_mon = new AEMonitor("plugin_helper_mon");
/*     */   
/*  46 */   private Comparator<String> alpha_comparator = new FormattersImpl().getAlphanumericComparator(true);
/*     */   
/*  48 */   private Map<String, IViewInfo> plugin_view_info_map = new TreeMap(this.alpha_comparator);
/*     */   
/*  50 */   private Map<String, IViewInfo> plugin_logs_view_info_map = new TreeMap(this.alpha_comparator);
/*     */   
/*  52 */   private List<PluginAddedViewListener> pluginAddedViewListener = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PluginsMenuHelper getInstance()
/*     */   {
/*  59 */     if (null == INSTANCE) {
/*  60 */       INSTANCE = new PluginsMenuHelper();
/*     */     }
/*  62 */     return INSTANCE;
/*     */   }
/*     */   
/*     */   public void buildPluginLogsMenu(Menu parentMenu)
/*     */   {
/*     */     try {
/*  68 */       this.plugin_helper_mon.enter();
/*     */       
/*  70 */       createViewInfoMenuItems(parentMenu, this.plugin_logs_view_info_map);
/*     */     }
/*     */     finally {
/*  73 */       this.plugin_helper_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private void sort(org.gudy.azureus2.plugins.ui.menus.MenuItem[] plugin_items)
/*     */   {
/*  80 */     Arrays.sort(plugin_items, new Comparator()
/*     */     {
/*     */ 
/*     */       public int compare(org.gudy.azureus2.plugins.ui.menus.MenuItem o1, org.gudy.azureus2.plugins.ui.menus.MenuItem o2)
/*     */       {
/*  85 */         return PluginsMenuHelper.this.alpha_comparator.compare(o1.getText(), o2.getText());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public boolean buildViewMenu(Menu viewMenu, Shell parent)
/*     */   {
/*  92 */     int itemCount = viewMenu.getItemCount();
/*     */     
/*  94 */     org.gudy.azureus2.plugins.ui.menus.MenuItem[] plugin_items = MenuItemManager.getInstance().getAllAsArray("mainmenu");
/*  95 */     if (plugin_items.length > 0) {
/*  96 */       sort(plugin_items);
/*     */       
/*  98 */       MenuBuildUtils.addPluginMenuItems(plugin_items, viewMenu, true, true, MenuBuildUtils.BASIC_MENU_ITEM_CONTROLLER);
/*     */     }
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 104 */       this.plugin_helper_mon.enter();
/*     */       
/* 106 */       if ((plugin_items.length > 0) && (this.plugin_view_info_map.size() > 0)) {
/* 107 */         new org.eclipse.swt.widgets.MenuItem(viewMenu, 2);
/*     */       }
/* 109 */       createViewInfoMenuItems(viewMenu, this.plugin_view_info_map);
/*     */     }
/*     */     finally {
/* 112 */       this.plugin_helper_mon.exit();
/*     */     }
/*     */     
/*     */ 
/* 116 */     return viewMenu.getItemCount() > itemCount;
/*     */   }
/*     */   
/*     */ 
/*     */   public void buildPluginMenu(Menu pluginMenu, Shell parent, boolean includeGetPluginsMenu)
/*     */   {
/*     */     try
/*     */     {
/* 124 */       this.plugin_helper_mon.enter();
/* 125 */       createViewInfoMenuItems(pluginMenu, this.plugin_view_info_map);
/*     */       
/*     */ 
/* 128 */       org.eclipse.swt.widgets.MenuItem menu_plugin_logViews = MenuFactory.addLogsViewMenuItem(pluginMenu);
/* 129 */       createViewInfoMenuItems(menu_plugin_logViews.getMenu(), this.plugin_logs_view_info_map);
/*     */     }
/*     */     finally
/*     */     {
/* 133 */       this.plugin_helper_mon.exit();
/*     */     }
/*     */     
/* 136 */     MenuFactory.addSeparatorMenuItem(pluginMenu);
/*     */     
/*     */ 
/* 139 */     org.gudy.azureus2.plugins.ui.menus.MenuItem[] plugin_items = MenuItemManager.getInstance().getAllAsArray("mainmenu");
/* 140 */     if (plugin_items.length > 0) {
/* 141 */       sort(plugin_items);
/*     */       
/* 143 */       MenuBuildUtils.addPluginMenuItems(plugin_items, pluginMenu, true, true, MenuBuildUtils.BASIC_MENU_ITEM_CONTROLLER);
/*     */       
/* 145 */       MenuFactory.addSeparatorMenuItem(pluginMenu);
/*     */     }
/*     */     
/* 148 */     MenuFactory.addPluginInstallMenuItem(pluginMenu);
/* 149 */     MenuFactory.addPluginUnInstallMenuItem(pluginMenu);
/*     */     
/* 151 */     if (includeGetPluginsMenu) {
/* 152 */       MenuFactory.addGetPluginsMenuItem(pluginMenu);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addPluginView(String sViewID, UISWTViewEventListener l) {
/* 157 */     IViewInfo view_info = new IViewInfo();
/* 158 */     view_info.viewID = sViewID;
/* 159 */     view_info.event_listener = l;
/*     */     
/* 161 */     String name = null;
/*     */     
/* 163 */     String sResourceID = "Views.plugins." + sViewID + ".title";
/* 164 */     boolean bResourceExists = MessageText.keyExists(sResourceID);
/* 165 */     if ((!bResourceExists) && 
/* 166 */       ((l instanceof UISWTViewEventListenerHolder))) {
/* 167 */       name = ((UISWTViewEventListenerHolder)l).getPluginInterface().getPluginconfig().getPluginStringParameter(sResourceID, null);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 172 */     if (bResourceExists) {
/* 173 */       name = MessageText.getString(sResourceID);
/* 174 */     } else if (name == null)
/*     */     {
/* 176 */       sResourceID = sViewID;
/* 177 */       bResourceExists = MessageText.keyExists(sResourceID);
/*     */       
/* 179 */       if (bResourceExists) {
/* 180 */         name = MessageText.getString(sResourceID);
/*     */       } else {
/* 182 */         name = sViewID.replace('.', ' ');
/*     */       }
/*     */     }
/*     */     
/* 186 */     view_info.name = name;
/*     */     
/*     */     Map<String, IViewInfo> map_to_use;
/*     */     Map<String, IViewInfo> map_to_use;
/* 190 */     if (((l instanceof BasicPluginViewImpl)) || (((l instanceof UISWTViewEventListenerHolder)) && (((UISWTViewEventListenerHolder)l).isLogView())))
/*     */     {
/*     */ 
/* 193 */       map_to_use = this.plugin_logs_view_info_map;
/*     */     }
/*     */     else {
/* 196 */       map_to_use = this.plugin_view_info_map;
/*     */     }
/*     */     try
/*     */     {
/* 200 */       this.plugin_helper_mon.enter();
/* 201 */       map_to_use.put(name, view_info);
/*     */     } finally {
/* 203 */       this.plugin_helper_mon.exit();
/*     */     }
/* 205 */     triggerPluginAddedViewListeners(view_info);
/*     */   }
/*     */   
/*     */   private void removePluginViewsWithID(String sViewID, Map map) {
/* 209 */     if (sViewID == null) {
/* 210 */       return;
/*     */     }
/* 212 */     Iterator itr = map.values().iterator();
/* 213 */     IViewInfo view_info = null;
/* 214 */     while (itr.hasNext()) {
/* 215 */       view_info = (IViewInfo)itr.next();
/* 216 */       if (sViewID.equals(view_info.viewID)) {
/* 217 */         itr.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void removePluginViews(final String sViewID) {
/*     */     try {
/* 224 */       this.plugin_helper_mon.enter();
/* 225 */       removePluginViewsWithID(sViewID, this.plugin_view_info_map);
/* 226 */       removePluginViewsWithID(sViewID, this.plugin_logs_view_info_map);
/*     */     } finally {
/* 228 */       this.plugin_helper_mon.exit();
/*     */     }
/* 230 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 232 */         UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 233 */         if (uiFunctions != null) {
/* 234 */           uiFunctions.closePluginViews(sViewID);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void createViewInfoMenuItem(Menu parent, final IViewInfo info)
/*     */   {
/* 247 */     org.eclipse.swt.widgets.MenuItem item = new org.eclipse.swt.widgets.MenuItem(parent, 0);
/* 248 */     item.setText(info.name);
/* 249 */     if (info.viewID != null) {
/* 250 */       item.setData("ViewID", info.viewID);
/*     */     }
/* 252 */     item.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 254 */         UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 255 */         if (uiFunctions != null) {
/* 256 */           info.openView(uiFunctions);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void createViewInfoMenuItems(Menu parent, Map menu_data) {
/* 263 */     Iterator itr = menu_data.values().iterator();
/* 264 */     while (itr.hasNext()) {
/* 265 */       createViewInfoMenuItem(parent, (IViewInfo)itr.next());
/*     */     }
/*     */   }
/*     */   
/*     */   public IViewInfo[] getPluginViewsInfo() {
/* 270 */     return (IViewInfo[])this.plugin_view_info_map.values().toArray(new IViewInfo[0]);
/*     */   }
/*     */   
/*     */   public IViewInfo[] getPluginLogViewsInfo() {
/* 274 */     return (IViewInfo[])this.plugin_logs_view_info_map.values().toArray(new IViewInfo[0]);
/*     */   }
/*     */   
/*     */ 
/*     */   public static class IViewInfo
/*     */   {
/*     */     public String name;
/*     */     public String viewID;
/*     */     public UISWTViewEventListener event_listener;
/*     */     
/*     */     public void openView(UIFunctionsSWT uiFunctions)
/*     */     {
/* 286 */       if (this.event_listener != null) {
/* 287 */         uiFunctions.openPluginView("Main", this.viewID, this.event_listener, null, true);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void addPluginAddedViewListener(PluginAddedViewListener l)
/*     */   {
/* 295 */     this.pluginAddedViewListener.add(l);
/*     */     
/* 297 */     IViewInfo[] viewsInfo = getPluginViewsInfo();
/* 298 */     for (IViewInfo info : viewsInfo) {
/* 299 */       l.pluginViewAdded(info);
/*     */     }
/* 301 */     viewsInfo = getPluginLogViewsInfo();
/* 302 */     for (IViewInfo info : viewsInfo) {
/* 303 */       l.pluginViewAdded(info);
/*     */     }
/*     */   }
/*     */   
/*     */   public void triggerPluginAddedViewListeners(final IViewInfo viewInfo) {
/* 308 */     final Object[] listeners = this.pluginAddedViewListener.toArray();
/* 309 */     if (this.pluginAddedViewListener.size() > 0) {
/* 310 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 312 */           for (int i = 0; i < listeners.length; i++) {
/* 313 */             PluginsMenuHelper.PluginAddedViewListener l = (PluginsMenuHelper.PluginAddedViewListener)listeners[i];
/* 314 */             l.pluginViewAdded(viewInfo);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public IViewInfo findIViewInfo(UISWTViewEventListener l)
/*     */   {
/* 326 */     IViewInfo foundViewInfo = null;
/*     */     
/* 328 */     IViewInfo[] pluginViewsInfo = getPluginViewsInfo();
/* 329 */     for (int i = 0; i < pluginViewsInfo.length; i++) {
/* 330 */       IViewInfo viewInfo = pluginViewsInfo[i];
/* 331 */       if (viewInfo.event_listener == l) {
/* 332 */         foundViewInfo = viewInfo;
/* 333 */         break;
/*     */       }
/*     */     }
/* 336 */     if (foundViewInfo == null) {
/* 337 */       pluginViewsInfo = getPluginLogViewsInfo();
/* 338 */       for (int i = 0; i < pluginViewsInfo.length; i++) {
/* 339 */         IViewInfo viewInfo = pluginViewsInfo[i];
/* 340 */         if (viewInfo.event_listener == l) {
/* 341 */           foundViewInfo = viewInfo;
/* 342 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 346 */     return foundViewInfo;
/*     */   }
/*     */   
/*     */   public static abstract interface PluginAddedViewListener
/*     */   {
/*     */     public abstract void pluginViewAdded(PluginsMenuHelper.IViewInfo paramIViewInfo);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/PluginsMenuHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */