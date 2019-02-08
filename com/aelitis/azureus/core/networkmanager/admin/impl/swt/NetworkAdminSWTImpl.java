/*     */ package com.aelitis.azureus.core.networkmanager.admin.impl.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.impl.NetworkAdminImpl;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater.UIUpdaterListener;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTStatusEntry;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTStatusEntryListener;
/*     */ import org.gudy.azureus2.ui.swt.views.stats.StatsView;
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
/*     */ public class NetworkAdminSWTImpl
/*     */ {
/*     */   private final NetworkAdminImpl network_admin;
/*     */   private UISWTStatusEntry status;
/*     */   private Image icon_grey;
/*     */   private Image icon_green;
/*     */   private Image icon_yellow;
/*     */   private Image icon_red;
/*     */   private Image last_icon;
/*     */   private String last_tip;
/*     */   private volatile boolean is_visible;
/*     */   
/*     */   public NetworkAdminSWTImpl(AzureusCore _core, NetworkAdminImpl _network_admin)
/*     */   {
/*  72 */     this.network_admin = _network_admin;
/*     */     
/*  74 */     final PluginInterface default_pi = PluginInitializer.getDefaultInterface();
/*     */     
/*  76 */     default_pi.getUIManager().addUIListener(new UIManagerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void UIAttached(final UIInstance instance)
/*     */       {
/*     */ 
/*  83 */         if ((instance instanceof UISWTInstance))
/*     */         {
/*  85 */           UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */           
/*  87 */           if (uif != null)
/*     */           {
/*  89 */             uif.getUIUpdater().addListener(new UIUpdater.UIUpdaterListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void updateComplete(int count)
/*     */               {
/*     */ 
/*  96 */                 NetworkAdminSWTImpl.this.updateStatus();
/*     */               }
/*     */             });
/*     */           }
/*     */           
/* 101 */           Utils.execSWTThread(new AERunnable()
/*     */           {
/*     */ 
/*     */             public void runSupport()
/*     */             {
/*     */ 
/* 107 */               ImageLoader imageLoader = ImageLoader.getInstance();
/*     */               
/* 109 */               NetworkAdminSWTImpl.this.icon_grey = imageLoader.getImage("st_net_grey");
/* 110 */               NetworkAdminSWTImpl.this.icon_yellow = imageLoader.getImage("st_net_yellow");
/* 111 */               NetworkAdminSWTImpl.this.icon_green = imageLoader.getImage("st_net_green");
/* 112 */               NetworkAdminSWTImpl.this.icon_red = imageLoader.getImage("st_net_red");
/*     */               
/* 114 */               UISWTInstance ui_instance = (UISWTInstance)instance;
/*     */               
/* 116 */               NetworkAdminSWTImpl.this.status = ui_instance.createStatusEntry();
/*     */               
/* 118 */               NetworkAdminSWTImpl.this.status.setText(MessageText.getString("label.routing"));
/*     */               
/* 120 */               NetworkAdminSWTImpl.this.status.setImageEnabled(true);
/*     */               
/* 122 */               NetworkAdminSWTImpl.this.status.setImage(NetworkAdminSWTImpl.this.icon_grey);
/*     */               
/* 124 */               String icon_param = "Show IP Bindings Icon";
/*     */               
/* 126 */               final MenuItem mi_show = NetworkAdminSWTImpl.1.this.val$default_pi.getUIManager().getMenuManager().addMenuItem(NetworkAdminSWTImpl.this.status.getMenuContext(), "pairing.ui.icon.show");
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 131 */               mi_show.setStyle(2);
/* 132 */               mi_show.setData(Boolean.valueOf(false));
/*     */               
/* 134 */               mi_show.addListener(new MenuItemListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void selected(MenuItem menu, Object target)
/*     */                 {
/*     */ 
/*     */ 
/* 142 */                   COConfigurationManager.setParameter("Show IP Bindings Icon", false);
/*     */                 }
/*     */                 
/* 145 */               });
/* 146 */               COConfigurationManager.addAndFireParameterListeners(new String[] { "Bind IP", "Show IP Bindings Icon" }, new ParameterListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void parameterChanged(String parameterName)
/*     */                 {
/*     */ 
/*     */ 
/* 154 */                   String bind_ip = COConfigurationManager.getStringParameter("Bind IP", "").trim();
/*     */                   
/* 156 */                   NetworkAdminSWTImpl.this.is_visible = ((bind_ip.trim().length() > 0) && (COConfigurationManager.getBooleanParameter("Show IP Bindings Icon")));
/*     */                   
/*     */ 
/*     */ 
/* 160 */                   NetworkAdminSWTImpl.this.status.setVisible(NetworkAdminSWTImpl.this.is_visible);
/*     */                   
/* 162 */                   mi_show.setData(Boolean.valueOf(NetworkAdminSWTImpl.this.is_visible));
/*     */                   
/* 164 */                   if (NetworkAdminSWTImpl.this.is_visible)
/*     */                   {
/* 166 */                     NetworkAdminSWTImpl.this.updateStatus();
/*     */                   }
/*     */                   
/*     */                 }
/* 170 */               });
/* 171 */               MenuItem mi_sep1 = NetworkAdminSWTImpl.1.this.val$default_pi.getUIManager().getMenuManager().addMenuItem(NetworkAdminSWTImpl.this.status.getMenuContext(), "sep1");
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 176 */               mi_sep1.setStyle(4);
/*     */               
/* 178 */               MenuItem mi_reset = NetworkAdminSWTImpl.1.this.val$default_pi.getUIManager().getMenuManager().addMenuItem(NetworkAdminSWTImpl.this.status.getMenuContext(), "menu.remove.net.binding");
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 183 */               mi_reset.addFillListener(new MenuItemFillListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void menuWillBeShown(MenuItem mi, Object data)
/*     */                 {
/*     */ 
/*     */ 
/* 191 */                   mi.setText(MessageText.getString("menu.remove.net.binding", new String[] { COConfigurationManager.getStringParameter("Bind IP", "") }));
/*     */ 
/*     */ 
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 199 */               });
/* 200 */               mi_reset.addListener(new MenuItemListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void selected(MenuItem menu, Object target)
/*     */                 {
/*     */ 
/*     */ 
/* 208 */                   COConfigurationManager.setParameter("Enforce Bind IP", false);
/* 209 */                   COConfigurationManager.setParameter("Bind IP", "");
/*     */                 }
/*     */                 
/* 212 */               });
/* 213 */               MenuItem mi_sep2 = NetworkAdminSWTImpl.1.this.val$default_pi.getUIManager().getMenuManager().addMenuItem(NetworkAdminSWTImpl.this.status.getMenuContext(), "sep2");
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 218 */               mi_sep2.setStyle(4);
/*     */               
/* 220 */               MenuItem mi_options = NetworkAdminSWTImpl.1.this.val$default_pi.getUIManager().getMenuManager().addMenuItem(NetworkAdminSWTImpl.this.status.getMenuContext(), "MainWindow.menu.view.configuration");
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 225 */               mi_options.addListener(new MenuItemListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void selected(MenuItem menu, Object target)
/*     */                 {
/*     */ 
/*     */ 
/* 233 */                   UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */                   
/* 235 */                   if (uif != null)
/*     */                   {
/* 237 */                     uif.getMDI().showEntryByID("ConfigView", "connection.advanced");
/*     */ 
/*     */                   }
/*     */                   
/*     */                 }
/*     */                 
/*     */ 
/* 244 */               });
/* 245 */               UISWTStatusEntryListener click_listener = new UISWTStatusEntryListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void entryClicked(UISWTStatusEntry entry)
/*     */                 {
/*     */ 
/* 252 */                   UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */                   
/* 254 */                   if (uif != null)
/*     */                   {
/* 256 */                     uif.getMDI().loadEntryByID(StatsView.VIEW_ID, true, false, "TransferStatsView");
/*     */                   }
/*     */                   
/*     */                 }
/* 260 */               };
/* 261 */               NetworkAdminSWTImpl.this.status.setListener(click_listener);
/*     */             }
/*     */           });
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void UIDetached(UIInstance instance) {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void updateStatus()
/*     */   {
/* 280 */     if (!this.is_visible)
/*     */     {
/* 282 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 288 */     Object[] bs_status = this.network_admin.getBindingStatus();
/*     */     
/* 290 */     int bs_state = ((Integer)bs_status[0]).intValue();
/* 291 */     String tip = (String)bs_status[1];
/*     */     Image icon;
/* 293 */     Image icon; if (bs_state == 0)
/*     */     {
/* 295 */       icon = this.icon_grey;
/*     */     } else { Image icon;
/* 297 */       if (bs_state == 1)
/*     */       {
/* 299 */         icon = this.icon_green;
/*     */       } else { Image icon;
/* 301 */         if (bs_state == 2)
/*     */         {
/* 303 */           icon = this.icon_yellow;
/*     */         }
/*     */         else
/*     */         {
/* 307 */           icon = this.icon_red; }
/*     */       }
/*     */     }
/* 310 */     if ((this.last_icon != icon) || (!tip.equals(this.last_tip)))
/*     */     {
/* 312 */       final Image f_icon = icon;
/* 313 */       final String f_tip = tip;
/*     */       
/* 315 */       Utils.execSWTThread(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/* 321 */           NetworkAdminSWTImpl.this.last_icon = f_icon;
/* 322 */           NetworkAdminSWTImpl.this.last_tip = f_tip;
/*     */           
/* 324 */           NetworkAdminSWTImpl.this.status.setImage(f_icon);
/*     */           
/* 326 */           NetworkAdminSWTImpl.this.status.setTooltipText(f_tip);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/networkmanager/admin/impl/swt/NetworkAdminSWTImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */