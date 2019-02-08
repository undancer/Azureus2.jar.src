/*     */ package com.aelitis.azureus.core.pairing.impl.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminNetworkInterface;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminNetworkInterfaceAddress;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdminPropertyChangeListener;
/*     */ import com.aelitis.azureus.core.pairing.impl.PairingManagerImpl.UIAdapter;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerPasswordHandler.passwordDetails;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater.UIUpdaterListener;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.net.InetAddress;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimeFormatter;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*     */ import org.gudy.azureus2.plugins.ui.config.ParameterListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.auth.CryptoWindow;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTStatusEntry;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTStatusEntryListener;
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
/*     */ public class PMSWTImpl
/*     */   implements PairingManagerImpl.UIAdapter
/*     */ {
/*     */   private UISWTStatusEntry status;
/*     */   private volatile Set<String> local_addresses;
/*     */   private Image icon_idle;
/*     */   private Image icon_green;
/*     */   private Image icon_red;
/*     */   private int last_update_count;
/*     */   private Image last_image;
/*     */   private String last_tooltip_text;
/*     */   private long last_image_expiry_mono;
/*     */   private long last_image_expiry_uc_min;
/*     */   private final Map<String, RemoteHistory> history_map;
/*     */   
/*     */   public void initialise(final PluginInterface pi, final BooleanParameter icon_enable)
/*     */   {
/*  83 */     final NetworkAdmin na = NetworkAdmin.getSingleton();
/*     */     
/*  85 */     na.addPropertyChangeListener(new NetworkAdminPropertyChangeListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void propertyChanged(String property)
/*     */       {
/*     */ 
/*  92 */         if (property == "Network Interfaces")
/*     */         {
/*  94 */           PMSWTImpl.this.updateLocalAddresses(na);
/*     */         }
/*     */         
/*     */       }
/*  98 */     });
/*  99 */     updateLocalAddresses(na);
/*     */     
/* 101 */     pi.getUIManager().addUIListener(new UIManagerListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void UIAttached(final UIInstance instance)
/*     */       {
/*     */ 
/* 108 */         if ((instance instanceof UISWTInstance))
/*     */         {
/* 110 */           UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */           
/* 112 */           if (uif != null)
/*     */           {
/* 114 */             uif.getUIUpdater().addListener(new UIUpdater.UIUpdaterListener()
/*     */             {
/*     */ 
/*     */ 
/*     */               public void updateComplete(int count)
/*     */               {
/*     */ 
/* 121 */                 PMSWTImpl.this.last_update_count = count;
/*     */                 
/* 123 */                 PMSWTImpl.this.updateStatus(true);
/*     */               }
/*     */             });
/*     */           }
/*     */           
/* 128 */           Utils.execSWTThread(new AERunnable()
/*     */           {
/*     */ 
/*     */             public void runSupport()
/*     */             {
/*     */ 
/* 134 */               ImageLoader imageLoader = ImageLoader.getInstance();
/*     */               
/* 136 */               PMSWTImpl.this.icon_idle = imageLoader.getImage("pair_sb_idle");
/* 137 */               PMSWTImpl.this.icon_green = imageLoader.getImage("pair_sb_green");
/* 138 */               PMSWTImpl.this.icon_red = imageLoader.getImage("pair_sb_red");
/*     */               
/* 140 */               UISWTInstance ui_instance = (UISWTInstance)instance;
/*     */               
/* 142 */               PMSWTImpl.this.status = ui_instance.createStatusEntry();
/*     */               
/* 144 */               PMSWTImpl.this.last_tooltip_text = MessageText.getString("pairing.ui.icon.tip");
/*     */               
/* 146 */               PMSWTImpl.this.status.setTooltipText(PMSWTImpl.this.last_tooltip_text);
/*     */               
/* 148 */               PMSWTImpl.this.status.setImageEnabled(true);
/*     */               
/* 150 */               PMSWTImpl.this.status.setImage(PMSWTImpl.this.icon_idle);
/*     */               
/* 152 */               PMSWTImpl.this.last_image = PMSWTImpl.this.icon_idle;
/*     */               
/* 154 */               boolean is_visible = PMSWTImpl.2.this.val$icon_enable.getValue();
/*     */               
/* 156 */               PMSWTImpl.this.status.setVisible(is_visible);
/*     */               
/* 158 */               if (is_visible)
/*     */               {
/* 160 */                 PMSWTImpl.this.updateStatus(false);
/*     */               }
/*     */               
/* 163 */               final MenuItem mi_show = PMSWTImpl.2.this.val$pi.getUIManager().getMenuManager().addMenuItem(PMSWTImpl.this.status.getMenuContext(), "pairing.ui.icon.show");
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 168 */               mi_show.setStyle(2);
/* 169 */               mi_show.setData(Boolean.valueOf(is_visible));
/*     */               
/* 171 */               mi_show.addListener(new MenuItemListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void selected(MenuItem menu, Object target)
/*     */                 {
/*     */ 
/*     */ 
/* 179 */                   PMSWTImpl.2.this.val$icon_enable.setValue(false);
/*     */                 }
/*     */                 
/* 182 */               });
/* 183 */               PMSWTImpl.2.this.val$icon_enable.addListener(new ParameterListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void parameterChanged(Parameter param)
/*     */                 {
/*     */ 
/* 190 */                   boolean is_visible = PMSWTImpl.2.this.val$icon_enable.getValue();
/*     */                   
/* 192 */                   PMSWTImpl.this.status.setVisible(is_visible);
/*     */                   
/* 194 */                   mi_show.setData(Boolean.valueOf(is_visible));
/*     */                   
/* 196 */                   if (is_visible)
/*     */                   {
/* 198 */                     PMSWTImpl.this.updateStatus(false);
/*     */                   }
/*     */                   
/*     */                 }
/*     */                 
/* 203 */               });
/* 204 */               final AZ3Functions.provider az3 = AZ3Functions.getProvider();
/*     */               
/* 206 */               if (az3 != null)
/*     */               {
/* 208 */                 MenuItem mi_pairing = PMSWTImpl.2.this.val$pi.getUIManager().getMenuManager().addMenuItem(PMSWTImpl.this.status.getMenuContext(), "MainWindow.menu.pairing");
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 213 */                 mi_pairing.addListener(new MenuItemListener()
/*     */                 {
/*     */ 
/*     */ 
/*     */                   public void selected(MenuItem menu, Object target)
/*     */                   {
/*     */ 
/*     */ 
/* 221 */                     az3.openRemotePairingWindow();
/*     */                   }
/*     */                 });
/*     */               }
/*     */               
/* 226 */               MenuItem mi_sep = PMSWTImpl.2.this.val$pi.getUIManager().getMenuManager().addMenuItem(PMSWTImpl.this.status.getMenuContext(), "");
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 231 */               mi_sep.setStyle(4);
/*     */               
/* 233 */               MenuItem mi_options = PMSWTImpl.2.this.val$pi.getUIManager().getMenuManager().addMenuItem(PMSWTImpl.this.status.getMenuContext(), "MainWindow.menu.view.configuration");
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 238 */               mi_options.addListener(new MenuItemListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void selected(MenuItem menu, Object target)
/*     */                 {
/*     */ 
/*     */ 
/* 246 */                   UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */                   
/* 248 */                   if (uif != null)
/*     */                   {
/* 250 */                     uif.getMDI().showEntryByID("ConfigView", "Pairing");
/*     */ 
/*     */                   }
/*     */                   
/*     */                 }
/*     */                 
/*     */ 
/* 257 */               });
/* 258 */               UISWTStatusEntryListener click_listener = new UISWTStatusEntryListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public void entryClicked(UISWTStatusEntry entry)
/*     */                 {
/*     */ 
/* 265 */                   UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*     */                   
/* 267 */                   if (uif != null)
/*     */                   {
/* 269 */                     uif.getMDI().showEntryByID("ConfigView", "Pairing");
/*     */                   }
/*     */                   
/*     */                 }
/*     */                 
/*     */ 
/* 275 */               };
/* 276 */               PMSWTImpl.this.status.setListener(click_listener);
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
/*     */ 
/*     */   private void updateLocalAddresses(NetworkAdmin network_admin)
/*     */   {
/* 296 */     NetworkAdminNetworkInterface[] interfaces = network_admin.getInterfaces();
/*     */     
/* 298 */     Set<String> ias = new HashSet();
/*     */     
/* 300 */     for (NetworkAdminNetworkInterface intf : interfaces)
/*     */     {
/* 302 */       NetworkAdminNetworkInterfaceAddress[] addresses = intf.getAddresses();
/*     */       
/* 304 */       for (NetworkAdminNetworkInterfaceAddress address : addresses)
/*     */       {
/* 306 */         InetAddress ia = address.getAddress();
/*     */         
/* 308 */         ias.add(ia.getHostAddress());
/*     */       }
/*     */     }
/*     */     
/* 312 */     this.local_addresses = ias;
/*     */   }
/*     */   
/*     */   public PMSWTImpl()
/*     */   {
/*  65 */     this.local_addresses = new HashSet();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  73 */     this.last_tooltip_text = "";
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
/*     */ 
/* 315 */     this.history_map = new HashMap();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void recordRequest(final String name, final String ip, final boolean good)
/*     */   {
/* 323 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/* 329 */         PMSWTImpl.RemoteHistory entry = (PMSWTImpl.RemoteHistory)PMSWTImpl.this.history_map.get(name);
/*     */         
/* 331 */         if (entry == null)
/*     */         {
/* 333 */           entry = new PMSWTImpl.RemoteHistory(null);
/*     */           
/* 335 */           PMSWTImpl.this.history_map.put(name, entry);
/*     */         }
/*     */         
/* 338 */         PMSWTImpl.RemoteHistory.access$1100(entry, ip, good);
/*     */         
/* 340 */         PMSWTImpl.this.updateStatus(false);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void updateStatus(boolean update_completed)
/*     */   {
/* 349 */     int RECORD_EXPIRY = 3600000;
/* 350 */     int GOOD_EXPIRY = 1000;
/* 351 */     int BAD_EXPIRY = 300000;
/* 352 */     int MAX_IPS_PER_TYPE = 10;
/* 353 */     int MAX_TYPES = 10;
/*     */     
/* 355 */     if (this.status == null)
/*     */     {
/* 357 */       return;
/*     */     }
/*     */     
/* 360 */     long now_mono = SystemTime.getMonotonousTime();
/*     */     
/* 362 */     if (update_completed)
/*     */     {
/* 364 */       if ((this.last_image != this.icon_idle) && (this.last_update_count >= this.last_image_expiry_uc_min))
/*     */       {
/* 366 */         if (now_mono >= this.last_image_expiry_mono)
/*     */         {
/* 368 */           this.last_image = this.icon_idle;
/*     */           
/* 370 */           this.status.setImage(this.icon_idle);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 375 */     StringBuilder tooltip_text = new StringBuilder(256);
/*     */     
/* 377 */     tooltip_text.append(MessageText.getString("pairing.ui.icon.tip"));
/*     */     
/* 379 */     long newest_bad_mono = -1L;
/* 380 */     long newest_good_mono = -1L;
/*     */     
/* 382 */     Iterator<Map.Entry<String, RemoteHistory>> it = this.history_map.entrySet().iterator();
/*     */     
/* 384 */     String oldest_type = null;
/* 385 */     long oldest_type_mono = Long.MAX_VALUE;
/*     */     
/* 387 */     int records_added = 0;
/*     */     
/* 389 */     while (it.hasNext())
/*     */     {
/* 391 */       Map.Entry<String, RemoteHistory> entry = (Map.Entry)it.next();
/*     */       
/* 393 */       String name = (String)entry.getKey();
/* 394 */       RemoteHistory history = (RemoteHistory)entry.getValue();
/*     */       
/* 396 */       String oldest_ip = null;
/* 397 */       long oldest_ip_mono = Long.MAX_VALUE;
/*     */       
/* 399 */       Map<String, RemoteHistoryEntry> records = history.getEntries();
/*     */       
/* 401 */       Iterator<Map.Entry<String, RemoteHistoryEntry>> record_it = records.entrySet().iterator();
/*     */       
/* 403 */       StringBuilder tt_ip_details = new StringBuilder(256);
/*     */       
/* 405 */       while (record_it.hasNext())
/*     */       {
/* 407 */         Map.Entry<String, RemoteHistoryEntry> record = (Map.Entry)record_it.next();
/*     */         
/* 409 */         String ip = (String)record.getKey();
/* 410 */         RemoteHistoryEntry e = (RemoteHistoryEntry)record.getValue();
/*     */         
/* 412 */         long e_mono = e.getLastReceivedMono();
/*     */         
/* 414 */         if (e_mono < oldest_ip_mono)
/*     */         {
/* 416 */           oldest_ip_mono = e_mono;
/* 417 */           oldest_ip = ip;
/*     */         }
/*     */         
/* 420 */         long age = now_mono - e_mono;
/*     */         
/* 422 */         if (age > 3600000L)
/*     */         {
/* 424 */           record_it.remove();
/*     */         }
/*     */         else
/*     */         {
/* 428 */           String age_str = TimeFormatter.format(age / 1000L);
/*     */           
/* 430 */           tt_ip_details.append("\n        ");
/*     */           
/* 432 */           if (this.local_addresses.contains(ip))
/*     */           {
/* 434 */             tt_ip_details.append(MessageText.getString("DHTView.db.local")).append(" (").append(ip).append(")");
/*     */           }
/*     */           else
/*     */           {
/* 438 */             tt_ip_details.append(ip);
/*     */           }
/*     */           
/* 441 */           if (e.wasLastGood())
/*     */           {
/* 443 */             tt_ip_details.append(" OK");
/*     */             
/* 445 */             newest_good_mono = Math.max(newest_good_mono, e_mono);
/*     */           }
/*     */           else
/*     */           {
/* 449 */             tt_ip_details.append(" ").append(MessageText.getString("label.access.denied"));
/*     */             
/* 451 */             newest_bad_mono = Math.max(newest_bad_mono, e_mono);
/*     */           }
/*     */           
/* 454 */           tt_ip_details.append(" - ").append(age_str).append(" ago");
/*     */         }
/*     */       }
/*     */       
/* 458 */       if (records.size() == 0)
/*     */       {
/* 460 */         it.remove();
/*     */       }
/*     */       else
/*     */       {
/* 464 */         if (oldest_ip_mono < oldest_type_mono)
/*     */         {
/* 466 */           oldest_type_mono = oldest_ip_mono;
/* 467 */           oldest_type = name;
/*     */         }
/*     */         
/* 470 */         if (records.size() >= 10)
/*     */         {
/* 472 */           records.remove(oldest_ip);
/*     */         }
/*     */         else
/*     */         {
/* 476 */           tooltip_text.append("\n    ").append(name);
/* 477 */           tooltip_text.append(tt_ip_details);
/*     */           
/* 479 */           records_added++;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 484 */     if (this.history_map.size() > 10)
/*     */     {
/* 486 */       this.history_map.remove(oldest_type);
/*     */     }
/*     */     
/* 489 */     if (records_added == 0)
/*     */     {
/* 491 */       tooltip_text.append("\n    ").append(MessageText.getString("pairing.ui.icon.tip.no.recent"));
/*     */     }
/*     */     
/* 494 */     String tooltip_text_str = tooltip_text.toString();
/*     */     
/* 496 */     if (!tooltip_text_str.equals(this.last_tooltip_text))
/*     */     {
/* 498 */       this.last_tooltip_text = tooltip_text_str;
/*     */       
/* 500 */       this.status.setTooltipText(this.last_tooltip_text);
/*     */     }
/*     */     
/* 503 */     Image target_image = null;
/*     */     
/* 505 */     long age_newest_bad = now_mono - newest_bad_mono;
/*     */     
/* 507 */     if ((newest_bad_mono >= 0L) && (age_newest_bad <= 300000L))
/*     */     {
/* 509 */       target_image = this.icon_red;
/*     */       
/* 511 */       this.last_image_expiry_mono = (newest_bad_mono + 300000L);
/*     */     }
/*     */     else {
/* 514 */       long age_newest_good = now_mono - newest_good_mono;
/*     */       
/* 516 */       if ((newest_good_mono >= 0L) && (age_newest_good <= 1000L))
/*     */       {
/* 518 */         target_image = this.icon_green;
/*     */         
/* 520 */         this.last_image_expiry_mono = (age_newest_good + 1000L);
/*     */       }
/*     */     }
/*     */     
/* 524 */     if ((target_image != null) && (target_image != this.last_image))
/*     */     {
/* 526 */       this.last_image = target_image;
/*     */       
/* 528 */       this.last_image_expiry_uc_min = (this.last_update_count + 2);
/*     */       
/* 530 */       this.status.setImage(target_image);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public char[] getSRPPassword()
/*     */   {
/* 537 */     CryptoWindow pw_win = new CryptoWindow(true);
/*     */     
/* 539 */     CryptoManagerPasswordHandler.passwordDetails result = pw_win.getPassword(-1, 3, true, "Change SRP Password");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 545 */     if (result != null)
/*     */     {
/* 547 */       return result.getPassword();
/*     */     }
/*     */     
/* 550 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   private static class RemoteHistory
/*     */   {
/* 556 */     private final Map<String, PMSWTImpl.RemoteHistoryEntry> map = new HashMap();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     private void addRequest(String ip, boolean good)
/*     */     {
/* 563 */       PMSWTImpl.RemoteHistoryEntry entry = (PMSWTImpl.RemoteHistoryEntry)this.map.get(ip);
/*     */       
/* 565 */       if (entry == null)
/*     */       {
/* 567 */         entry = new PMSWTImpl.RemoteHistoryEntry(null);
/*     */         
/* 569 */         this.map.put(ip, entry);
/*     */       }
/*     */       
/* 572 */       PMSWTImpl.RemoteHistoryEntry.access$1600(entry, good);
/*     */     }
/*     */     
/*     */ 
/*     */     private Map<String, PMSWTImpl.RemoteHistoryEntry> getEntries()
/*     */     {
/* 578 */       return this.map;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class RemoteHistoryEntry
/*     */   {
/*     */     private long last_received_mono;
/*     */     
/*     */     private long last_received_rtc;
/*     */     
/*     */     private int request_count;
/*     */     private boolean last_was_good;
/*     */     
/*     */     private long getLastReceivedMono()
/*     */     {
/* 594 */       return this.last_received_mono;
/*     */     }
/*     */     
/*     */ 
/*     */     private long getLastReceivedRTC()
/*     */     {
/* 600 */       return this.last_received_rtc;
/*     */     }
/*     */     
/*     */ 
/*     */     private int getRequestCount()
/*     */     {
/* 606 */       return this.request_count;
/*     */     }
/*     */     
/*     */ 
/*     */     private boolean wasLastGood()
/*     */     {
/* 612 */       return this.last_was_good;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     private void update(boolean good)
/*     */     {
/* 619 */       this.last_received_mono = SystemTime.getMonotonousTime();
/* 620 */       this.last_received_rtc = SystemTime.getCurrentTime();
/*     */       
/* 622 */       this.request_count += 1;
/*     */       
/* 624 */       this.last_was_good = good;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/pairing/impl/swt/PMSWTImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */