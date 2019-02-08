/*     */ package com.aelitis.azureus.core.proxy.impl.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.proxy.impl.AEProxySelectorImpl;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater.UIUpdaterListener;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Proxy;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.peer.PEPeerManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
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
/*     */ public class AEProxySelectorSWTImpl
/*     */ {
/*     */   private final AzureusCore core;
/*     */   private final AEProxySelectorImpl proxy_selector;
/*     */   private UISWTStatusEntry status;
/*     */   private Image icon_grey;
/*     */   private Image icon_green;
/*     */   private Image icon_yellow;
/*     */   private Image icon_red;
/*     */   private Image last_icon;
/*     */   private boolean flag_incoming;
/*     */   private long last_bad_peer_update;
/*     */   private volatile boolean is_visible;
/*     */   
/*     */   public AEProxySelectorSWTImpl(AzureusCore _core, AEProxySelectorImpl _proxy_selector)
/*     */   {
/*  76 */     COConfigurationManager.addAndFireParameterListener("Proxy.SOCKS.ShowIcon.FlagIncoming", new ParameterListener()
/*     */     {
/*     */ 
/*     */       public void parameterChanged(String name)
/*     */       {
/*  81 */         AEProxySelectorSWTImpl.this.flag_incoming = COConfigurationManager.getBooleanParameter(name);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  93 */     });
/*  94 */     this.core = _core;
/*  95 */     this.proxy_selector = _proxy_selector;
/*     */     
/*  97 */     final PluginInterface default_pi = PluginInitializer.getDefaultInterface();
/*     */     
/*  99 */     default_pi.getUIManager().addUIListener(new UIManagerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void UIAttached(final UIInstance instance)
/*     */       {
/*     */ 
/* 106 */         if ((instance instanceof UISWTInstance))
/*     */         {
/* 108 */           UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */           
/* 110 */           if (uif != null)
/*     */           {
/* 112 */             uif.getUIUpdater().addListener(new UIUpdater.UIUpdaterListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void updateComplete(int count)
/*     */               {
/*     */ 
/* 119 */                 AEProxySelectorSWTImpl.this.updateStatus();
/*     */               }
/*     */             });
/*     */           }
/*     */           
/* 124 */           Utils.execSWTThread(new AERunnable()
/*     */           {
/*     */ 
/*     */             public void runSupport()
/*     */             {
/*     */ 
/* 130 */               ImageLoader imageLoader = ImageLoader.getInstance();
/*     */               
/* 132 */               AEProxySelectorSWTImpl.this.icon_grey = imageLoader.getImage("grayled");
/* 133 */               AEProxySelectorSWTImpl.this.icon_yellow = imageLoader.getImage("yellowled");
/* 134 */               AEProxySelectorSWTImpl.this.icon_green = imageLoader.getImage("greenled");
/* 135 */               AEProxySelectorSWTImpl.this.icon_red = imageLoader.getImage("redled");
/*     */               
/* 137 */               UISWTInstance ui_instance = (UISWTInstance)instance;
/*     */               
/* 139 */               AEProxySelectorSWTImpl.this.status = ui_instance.createStatusEntry();
/*     */               
/* 141 */               AEProxySelectorSWTImpl.this.status.setText("SOCKS");
/*     */               
/* 143 */               AEProxySelectorSWTImpl.this.status.setImageEnabled(true);
/*     */               
/* 145 */               AEProxySelectorSWTImpl.this.status.setImage(AEProxySelectorSWTImpl.this.icon_grey);
/*     */               
/* 147 */               String icon_param = "Proxy.SOCKS.ShowIcon";
/*     */               
/* 149 */               boolean enable_proxy = COConfigurationManager.getBooleanParameter("Enable.Proxy");
/* 150 */               boolean enable_socks = COConfigurationManager.getBooleanParameter("Enable.SOCKS");
/*     */               
/* 152 */               AEProxySelectorSWTImpl.this.is_visible = ((enable_proxy) && (enable_socks) && (COConfigurationManager.getBooleanParameter("Proxy.SOCKS.ShowIcon")));
/*     */               
/*     */ 
/*     */ 
/* 156 */               AEProxySelectorSWTImpl.this.status.setVisible(AEProxySelectorSWTImpl.this.is_visible);
/*     */               
/* 158 */               if (AEProxySelectorSWTImpl.this.is_visible)
/*     */               {
/* 160 */                 AEProxySelectorSWTImpl.this.updateStatus();
/*     */               }
/*     */               
/* 163 */               final MenuItem mi_show = AEProxySelectorSWTImpl.2.this.val$default_pi.getUIManager().getMenuManager().addMenuItem(AEProxySelectorSWTImpl.this.status.getMenuContext(), "pairing.ui.icon.show");
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 168 */               mi_show.setStyle(2);
/* 169 */               mi_show.setData(Boolean.valueOf(AEProxySelectorSWTImpl.this.is_visible));
/*     */               
/* 171 */               mi_show.addListener(new MenuItemListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void selected(MenuItem menu, Object target)
/*     */                 {
/*     */ 
/*     */ 
/* 179 */                   COConfigurationManager.setParameter("Proxy.SOCKS.ShowIcon", false);
/*     */                 }
/*     */                 
/* 182 */               });
/* 183 */               COConfigurationManager.addParameterListener(new String[] { "Enable.Proxy", "Enable.SOCKS", "Proxy.SOCKS.ShowIcon" }, new ParameterListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void parameterChanged(String parameterName)
/*     */                 {
/*     */ 
/*     */ 
/* 191 */                   boolean enable_proxy = COConfigurationManager.getBooleanParameter("Enable.Proxy");
/* 192 */                   boolean enable_socks = COConfigurationManager.getBooleanParameter("Enable.SOCKS");
/*     */                   
/* 194 */                   AEProxySelectorSWTImpl.this.is_visible = ((enable_proxy) && (enable_socks) && (COConfigurationManager.getBooleanParameter("Proxy.SOCKS.ShowIcon")));
/*     */                   
/*     */ 
/*     */ 
/* 198 */                   AEProxySelectorSWTImpl.this.status.setVisible(AEProxySelectorSWTImpl.this.is_visible);
/*     */                   
/* 200 */                   mi_show.setData(Boolean.valueOf(AEProxySelectorSWTImpl.this.is_visible));
/*     */                   
/* 202 */                   if (AEProxySelectorSWTImpl.this.is_visible)
/*     */                   {
/* 204 */                     AEProxySelectorSWTImpl.this.updateStatus();
/*     */                   }
/*     */                   
/*     */                 }
/*     */                 
/* 209 */               });
/* 210 */               MenuItem mi_sep = AEProxySelectorSWTImpl.2.this.val$default_pi.getUIManager().getMenuManager().addMenuItem(AEProxySelectorSWTImpl.this.status.getMenuContext(), "");
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 215 */               mi_sep.setStyle(4);
/*     */               
/* 217 */               MenuItem mi_options = AEProxySelectorSWTImpl.2.this.val$default_pi.getUIManager().getMenuManager().addMenuItem(AEProxySelectorSWTImpl.this.status.getMenuContext(), "MainWindow.menu.view.configuration");
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 222 */               mi_options.addListener(new MenuItemListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void selected(MenuItem menu, Object target)
/*     */                 {
/*     */ 
/*     */ 
/* 230 */                   UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */                   
/* 232 */                   if (uif != null) {
/* 233 */                     uif.getMDI().showEntryByID("ConfigView", "proxy");
/*     */ 
/*     */                   }
/*     */                   
/*     */                 }
/*     */                 
/*     */ 
/* 240 */               });
/* 241 */               UISWTStatusEntryListener click_listener = new UISWTStatusEntryListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void entryClicked(UISWTStatusEntry entry)
/*     */                 {
/*     */ 
/* 248 */                   UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */                   
/* 250 */                   if (uif != null)
/*     */                   {
/* 252 */                     uif.getMDI().loadEntryByID(StatsView.VIEW_ID, true, false, "TransferStatsView");
/*     */                   }
/*     */                   
/*     */                 }
/* 256 */               };
/* 257 */               AEProxySelectorSWTImpl.this.status.setListener(click_listener);
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
/* 276 */     if (!this.is_visible)
/*     */     {
/* 278 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 284 */     Proxy active_proxy = this.proxy_selector.getActiveProxy();
/*     */     
/* 286 */     long now = SystemTime.getMonotonousTime();
/*     */     String tip_key;
/* 288 */     Image icon; String tip_key; if (active_proxy == null)
/*     */     {
/* 290 */       Image icon = this.icon_grey;
/* 291 */       tip_key = "label.inactive";
/*     */     }
/*     */     else {
/* 294 */       long last_con = this.proxy_selector.getLastConnectionTime();
/* 295 */       long last_fail = this.proxy_selector.getLastFailTime();
/*     */       
/* 297 */       long con_ago = now - last_con;
/* 298 */       long fail_ago = now - last_fail;
/*     */       String tip_key;
/* 300 */       if (last_fail < 0L)
/*     */       {
/* 302 */         Image icon = this.icon_green;
/* 303 */         tip_key = "PeerManager.status.ok";
/*     */       } else {
/*     */         String tip_key;
/* 306 */         if (fail_ago > 60000L) {
/*     */           String tip_key;
/* 308 */           if (con_ago < fail_ago)
/*     */           {
/* 310 */             Image icon = this.icon_green;
/* 311 */             tip_key = "PeerManager.status.ok";
/*     */           }
/*     */           else {
/* 314 */             Image icon = this.icon_grey;
/* 315 */             tip_key = "PeersView.state.pending";
/*     */           }
/*     */         }
/*     */         else {
/* 319 */           icon = this.icon_yellow;
/* 320 */           tip_key = "label.con_prob";
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 325 */     if (this.flag_incoming)
/*     */     {
/* 327 */       boolean bad_incoming = false;
/*     */       
/* 329 */       if (now - this.last_bad_peer_update > 15000L)
/*     */       {
/* 331 */         this.last_bad_peer_update = now;
/*     */         
/* 333 */         List<DownloadManager> dms = this.core.getGlobalManager().getDownloadManagers();
/*     */         
/* 335 */         for (DownloadManager dm : dms)
/*     */         {
/* 337 */           PEPeerManager pm = dm.getPeerManager();
/*     */           
/* 339 */           if (pm != null)
/*     */           {
/* 341 */             if (pm.getNbRemoteTCPConnections() + pm.getNbRemoteUDPConnections() + pm.getNbRemoteUTPConnections() > 0)
/*     */             {
/* 343 */               List<PEPeer> peers = pm.getPeers();
/*     */               
/* 345 */               for (PEPeer peer : peers)
/*     */               {
/* 347 */                 if (peer.isIncoming())
/*     */                 {
/* 349 */                   if (!peer.isLANLocal())
/*     */                   {
/*     */                     try {
/* 352 */                       if (InetAddress.getByAddress(HostNameToIPResolver.hostAddressToBytes(peer.getIp())).isLoopbackAddress()) {
/*     */                         continue;
/*     */                       }
/*     */                     }
/*     */                     catch (Throwable e) {}
/*     */                     
/*     */ 
/* 359 */                     bad_incoming = true;
/*     */                     
/* 361 */                     break;
/*     */                   }
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 368 */           if (bad_incoming) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/* 373 */       else if (this.last_icon == this.icon_red)
/*     */       {
/* 375 */         bad_incoming = true;
/*     */       }
/*     */       
/* 378 */       if (bad_incoming)
/*     */       {
/* 380 */         icon = this.icon_red;
/* 381 */         tip_key = "proxy.socks.bad.incoming";
/*     */       }
/*     */     }
/*     */     
/* 385 */     if (this.last_icon != icon)
/*     */     {
/* 387 */       final Image f_icon = icon;
/* 388 */       final String f_key = tip_key;
/*     */       
/* 390 */       Utils.execSWTThread(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/* 396 */           AEProxySelectorSWTImpl.this.last_icon = f_icon;
/*     */           
/* 398 */           AEProxySelectorSWTImpl.this.status.setImage(f_icon);
/*     */           
/* 400 */           AEProxySelectorSWTImpl.this.status.setTooltipText(MessageText.getString("proxy.socks.ui.icon.tip", new String[] { MessageText.getString(f_key) }));
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/impl/swt/AEProxySelectorSWTImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */