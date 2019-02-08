/*      */ package com.aelitis.azureus.plugins.net.buddy.swt;
/*      */ 
/*      */ import com.aelitis.azureus.core.AzureusCore;
/*      */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*      */ import com.aelitis.azureus.core.security.CryptoHandler;
/*      */ import com.aelitis.azureus.core.security.CryptoManager;
/*      */ import com.aelitis.azureus.core.security.CryptoManagerFactory;
/*      */ import com.aelitis.azureus.core.security.CryptoManagerKeyListener;
/*      */ import com.aelitis.azureus.core.tag.Tag;
/*      */ import com.aelitis.azureus.core.tag.TagManager;
/*      */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*      */ import com.aelitis.azureus.core.tag.TagType;
/*      */ import com.aelitis.azureus.core.tag.Taggable;
/*      */ import com.aelitis.azureus.core.tag.TaggableLifecycleAdapter;
/*      */ import com.aelitis.azureus.core.util.DNSUtils;
/*      */ import com.aelitis.azureus.plugins.I2PHelpers;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPlugin;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginAZ2;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginAZ2.chatInstance;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginAZ2Listener;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginAdapter;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatInstance;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBeta.ChatMessage;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginBuddy;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginUtils;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginViewInterface;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginViewInterface.DownloadAdapter;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginViewInterface.View;
/*      */ import com.aelitis.azureus.plugins.net.buddy.BuddyPluginViewInterface.ViewListener;
/*      */ import com.aelitis.azureus.plugins.net.buddy.tracker.BuddyPluginTracker;
/*      */ import com.aelitis.azureus.plugins.net.buddy.tracker.BuddyPluginTrackerListener;
/*      */ import com.aelitis.azureus.ui.UIFunctions;
/*      */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*      */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import java.applet.Applet;
/*      */ import java.applet.AudioClip;
/*      */ import java.io.File;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import org.eclipse.swt.custom.CTabFolder;
/*      */ import org.eclipse.swt.custom.CTabItem;
/*      */ import org.eclipse.swt.custom.ScrolledComposite;
/*      */ import org.eclipse.swt.custom.StyleRange;
/*      */ import org.eclipse.swt.events.ControlAdapter;
/*      */ import org.eclipse.swt.events.ControlEvent;
/*      */ import org.eclipse.swt.events.SelectionAdapter;
/*      */ import org.eclipse.swt.events.SelectionEvent;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Group;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.ScrollBar;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.AEThread2;
/*      */ import org.gudy.azureus2.core3.util.AsyncDispatcher;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemTime;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*      */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*      */ import org.gudy.azureus2.plugins.PluginInterface;
/*      */ import org.gudy.azureus2.plugins.disk.DiskManagerFileInfo;
/*      */ import org.gudy.azureus2.plugins.download.Download;
/*      */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*      */ import org.gudy.azureus2.plugins.ui.UIManager;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuContext;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*      */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*      */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
/*      */ import org.gudy.azureus2.pluginsimpl.local.utils.FormattersImpl;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*      */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.ChatKeyResolver;
/*      */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.minibar.AllTransfersBar;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTStatusEntry;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTStatusEntryListener;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*      */ import org.gudy.azureus2.ui.swt.views.utils.TagUIUtils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class BuddyPluginView
/*      */   implements UISWTViewEventListener, BuddyPluginViewInterface
/*      */ {
/*      */   private BuddyPlugin plugin;
/*      */   private UISWTInstance ui_instance;
/*      */   private String VIEW_ID;
/*      */   private BuddyPluginViewInstance current_instance;
/*      */   private Image iconNLI;
/*      */   private Image iconIDLE;
/*      */   private Image iconIN;
/*      */   private Image iconOUT;
/*      */   private Image iconINOUT;
/*  130 */   private final String default_sound = "org/gudy/azureus2/ui/icons/downloadFinished.wav";
/*      */   
/*      */ 
/*      */   private boolean select_classic_tab_oustanding;
/*      */   
/*      */   private boolean beta_init_done;
/*      */   
/*      */ 
/*      */   public BuddyPluginView(BuddyPlugin _plugin, UIInstance _ui_instance, String _VIEW_ID)
/*      */   {
/*  140 */     this.plugin = _plugin;
/*  141 */     this.ui_instance = ((UISWTInstance)_ui_instance);
/*  142 */     this.VIEW_ID = _VIEW_ID;
/*      */     
/*  144 */     this.plugin.getAZ2Handler().addListener(new BuddyPluginAZ2Listener()
/*      */     {
/*      */ 
/*      */ 
/*      */       public void chatCreated(final BuddyPluginAZ2.chatInstance chat)
/*      */       {
/*      */ 
/*  151 */         final Display display = BuddyPluginView.this.ui_instance.getDisplay();
/*      */         
/*  153 */         if (!display.isDisposed())
/*      */         {
/*  155 */           display.asyncExec(new Runnable()
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/*  161 */               if (!display.isDisposed())
/*      */               {
/*  163 */                 new BuddyPluginViewChat(BuddyPluginView.this.plugin, display, chat);
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void chatDestroyed(BuddyPluginAZ2.chatInstance chat) {}
/*  177 */     });
/*  178 */     SimpleTimer.addEvent("BuddyStatusInit", SystemTime.getOffsetTime(1000L), new TimerEventPerformer()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void perform(TimerEvent event)
/*      */       {
/*      */ 
/*      */ 
/*  187 */         new BuddyPluginView.statusUpdater(BuddyPluginView.this, BuddyPluginView.this.ui_instance);
/*      */       }
/*      */       
/*  190 */     });
/*  191 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/*  193 */         ImageLoader imageLoader = ImageLoader.getInstance();
/*      */         
/*  195 */         BuddyPluginView.this.iconNLI = imageLoader.getImage("bbb_nli");
/*  196 */         BuddyPluginView.this.iconIDLE = imageLoader.getImage("bbb_idle");
/*  197 */         BuddyPluginView.this.iconIN = imageLoader.getImage("bbb_in");
/*  198 */         BuddyPluginView.this.iconOUT = imageLoader.getImage("bbb_out");
/*  199 */         BuddyPluginView.this.iconINOUT = imageLoader.getImage("bbb_inout");
/*      */       }
/*      */       
/*  202 */     });
/*  203 */     this.ui_instance.addView("Main", this.VIEW_ID, this);
/*      */     
/*  205 */     checkBetaInit();
/*      */   }
/*      */   
/*      */ 
/*      */   protected UISWTInstance getUISWTInstance()
/*      */   {
/*  211 */     return this.ui_instance;
/*      */   }
/*      */   
/*      */ 
/*      */   public void selectClassicTab()
/*      */   {
/*  217 */     this.select_classic_tab_oustanding = true;
/*      */     
/*  219 */     this.ui_instance.openView("Main", this.VIEW_ID, null);
/*      */     
/*  221 */     if (this.current_instance != null)
/*      */     {
/*  223 */       this.current_instance.selectClassicTab();
/*      */       
/*  225 */       this.select_classic_tab_oustanding = false;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean eventOccurred(UISWTViewEvent event)
/*      */   {
/*  232 */     switch (event.getType())
/*      */     {
/*      */ 
/*      */     case 0: 
/*  236 */       if (this.current_instance != null)
/*      */       {
/*  238 */         return false;
/*      */       }
/*      */       
/*  241 */       event.getView().setDestroyOnDeactivate(false);
/*      */       
/*  243 */       break;
/*      */     
/*      */ 
/*      */     case 2: 
/*  247 */       this.current_instance = new BuddyPluginViewInstance(this, this.plugin, this.ui_instance, (Composite)event.getData());
/*      */       
/*  249 */       if (this.select_classic_tab_oustanding)
/*      */       {
/*  251 */         this.select_classic_tab_oustanding = false;
/*      */         
/*  253 */         this.current_instance.selectClassicTab();
/*      */       }
/*      */       
/*      */ 
/*      */       break;
/*      */     case 7: 
/*      */     case 8: 
/*      */       try
/*      */       {
/*  262 */         if (this.current_instance != null)
/*      */         {
/*  264 */           this.current_instance.destroy();
/*      */         }
/*      */       }
/*      */       finally {
/*  268 */         this.current_instance = null;
/*      */       }
/*      */     }
/*      */     
/*      */     
/*      */ 
/*      */ 
/*  275 */     return true;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void openChat(final BuddyPluginBeta.ChatInstance chat)
/*      */   {
/*  282 */     final Display display = Display.getDefault();
/*      */     
/*  284 */     if (display.isDisposed())
/*      */     {
/*  286 */       return;
/*      */     }
/*      */     
/*  289 */     display.asyncExec(new Runnable()
/*      */     {
/*      */ 
/*      */       public void run()
/*      */       {
/*      */ 
/*  295 */         if (display.isDisposed())
/*      */         {
/*  297 */           return;
/*      */         }
/*      */         
/*  300 */         BuddyPluginViewBetaChat.createChatWindow(BuddyPluginView.this, BuddyPluginView.this.plugin, chat);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */   protected class statusUpdater
/*      */     implements BuddyPluginTrackerListener
/*      */   {
/*      */     private UISWTStatusEntry label;
/*      */     
/*      */     private UISWTStatusEntry status;
/*      */     
/*      */     private BuddyPluginTracker tracker;
/*      */     
/*      */     private TimerEventPeriodic update_event;
/*      */     
/*      */     private CryptoManager crypto;
/*      */     private boolean crypto_ok;
/*      */     private boolean has_buddies;
/*      */     
/*      */     protected statusUpdater(UISWTInstance instance)
/*      */     {
/*  323 */       this.status = BuddyPluginView.this.ui_instance.createStatusEntry();
/*  324 */       this.label = BuddyPluginView.this.ui_instance.createStatusEntry();
/*      */       
/*  326 */       this.label.setText(MessageText.getString("azbuddy.tracker.bbb.status.title"));
/*  327 */       this.label.setTooltipText(MessageText.getString("azbuddy.tracker.bbb.status.title.tooltip"));
/*      */       
/*  329 */       this.tracker = BuddyPluginView.this.plugin.getTracker();
/*      */       
/*  331 */       this.status.setText("");
/*      */       
/*  333 */       this.status.setImageEnabled(true);
/*      */       
/*  335 */       this.tracker.addListener(this);
/*      */       
/*  337 */       this.has_buddies = (BuddyPluginView.this.plugin.getBuddies().size() > 0);
/*      */       
/*  339 */       this.status.setVisible((this.tracker.isEnabled()) && (this.has_buddies));
/*  340 */       this.label.setVisible((this.tracker.isEnabled()) && (this.has_buddies));
/*      */       
/*  342 */       for (UISWTStatusEntry entry : new UISWTStatusEntry[] { this.label, this.status })
/*      */       {
/*  344 */         MenuItem mi = BuddyPluginView.this.plugin.getPluginInterface().getUIManager().getMenuManager().addMenuItem(entry.getMenuContext(), "azbuddy.view.friends");
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*  349 */         mi.addListener(new MenuItemListener()
/*      */         {
/*      */ 
/*      */ 
/*      */           public void selected(MenuItem menu, Object target)
/*      */           {
/*      */ 
/*      */ 
/*  357 */             BuddyPluginView.this.selectClassicTab();
/*      */           }
/*      */         });
/*      */       }
/*      */       
/*      */ 
/*  363 */       UISWTStatusEntryListener click_listener = new UISWTStatusEntryListener()
/*      */       {
/*      */ 
/*      */         public void entryClicked(UISWTStatusEntry entry)
/*      */         {
/*      */ 
/*      */           try
/*      */           {
/*  371 */             BuddyPluginView.this.plugin.getPluginInterface().getUIManager().openURL(new URL(MessageText.getString("azbuddy.classic.link.url")));
/*      */ 
/*      */           }
/*      */           catch (Throwable e)
/*      */           {
/*  376 */             Debug.printStackTrace(e);
/*      */           }
/*      */           
/*      */         }
/*  380 */       };
/*  381 */       this.status.setListener(click_listener);
/*  382 */       this.label.setListener(click_listener);
/*      */       
/*      */ 
/*  385 */       BuddyPluginView.this.plugin.addListener(new BuddyPluginAdapter()
/*      */       {
/*      */         public void initialised(boolean available) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void buddyAdded(BuddyPluginBuddy buddy)
/*      */         {
/*  398 */           if (!BuddyPluginView.statusUpdater.this.has_buddies)
/*      */           {
/*  400 */             BuddyPluginView.statusUpdater.this.has_buddies = true;
/*      */             
/*  402 */             BuddyPluginView.statusUpdater.this.updateStatus();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void buddyRemoved(BuddyPluginBuddy buddy)
/*      */         {
/*  410 */           BuddyPluginView.statusUpdater.this.has_buddies = (BuddyPluginView.this.plugin.getBuddies().size() > 0);
/*      */           
/*  412 */           if (!BuddyPluginView.statusUpdater.this.has_buddies)
/*      */           {
/*  414 */             BuddyPluginView.statusUpdater.this.updateStatus();
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void buddyChanged(BuddyPluginBuddy buddy) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void messageLogged(String str, boolean error) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void enabledStateChanged(boolean enabled) {}
/*  437 */       });
/*  438 */       this.crypto = CryptoManagerFactory.getSingleton();
/*      */       
/*  440 */       this.crypto.addKeyListener(new CryptoManagerKeyListener()
/*      */       {
/*      */         public void keyChanged(CryptoHandler handler) {}
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public void keyLockStatusChanged(CryptoHandler handler)
/*      */         {
/*  453 */           boolean ok = BuddyPluginView.statusUpdater.this.crypto.getECCHandler().isUnlocked();
/*      */           
/*  455 */           if (ok != BuddyPluginView.statusUpdater.this.crypto_ok)
/*      */           {
/*  457 */             BuddyPluginView.statusUpdater.this.crypto_ok = ok;
/*      */             
/*  459 */             BuddyPluginView.statusUpdater.this.updateStatus();
/*      */           }
/*      */           
/*      */         }
/*  463 */       });
/*  464 */       this.crypto_ok = this.crypto.getECCHandler().isUnlocked();
/*      */       
/*  466 */       updateStatus();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void networkStatusChanged(BuddyPluginTracker tracker, int new_status)
/*      */     {
/*  474 */       updateStatus();
/*      */     }
/*      */     
/*      */ 
/*      */     protected synchronized void updateStatus()
/*      */     {
/*  480 */       if ((this.tracker.isEnabled()) && (this.has_buddies))
/*      */       {
/*  482 */         this.status.setVisible(true);
/*  483 */         this.label.setVisible(true);
/*      */         
/*  485 */         if ((this.has_buddies) && (!this.crypto_ok))
/*      */         {
/*  487 */           this.status.setImage(BuddyPluginView.this.iconNLI);
/*      */           
/*  489 */           this.status.setTooltipText(MessageText.getString("azbuddy.tracker.bbb.status.nli"));
/*      */           
/*  491 */           disableUpdates();
/*      */         }
/*      */         else
/*      */         {
/*  495 */           int network_status = this.tracker.getNetworkStatus();
/*      */           
/*  497 */           if (network_status != 1)
/*      */           {
/*  499 */             long rates = this.tracker.getNetworkReceiveBytesPerSecond() + this.tracker.getNetworkSendBytesPerSecond();
/*      */             
/*  501 */             if (rates <= 0L)
/*      */             {
/*      */ 
/*      */ 
/*  505 */               SimpleTimer.addEvent("BP:backoff", SystemTime.getOffsetTime(1000L), new TimerEventPerformer()
/*      */               {
/*      */ 
/*      */ 
/*      */                 public void perform(TimerEvent event)
/*      */                 {
/*      */ 
/*  512 */                   BuddyPluginView.statusUpdater.this.updateStatus();
/*      */                 }
/*      */                 
/*  515 */               });
/*  516 */               return;
/*      */             }
/*      */           }
/*      */           
/*  520 */           if (network_status == 1)
/*      */           {
/*  522 */             this.status.setImage(BuddyPluginView.this.iconIDLE);
/*      */             
/*  524 */             this.status.setTooltipText(MessageText.getString("azbuddy.tracker.bbb.status.idle"));
/*      */             
/*  526 */             disableUpdates();
/*      */           }
/*  528 */           else if (network_status == 3)
/*      */           {
/*  530 */             this.status.setImage(BuddyPluginView.this.iconIN);
/*      */             
/*  532 */             enableUpdates();
/*      */           }
/*  534 */           else if (network_status == 2)
/*      */           {
/*  536 */             this.status.setImage(BuddyPluginView.this.iconOUT);
/*      */             
/*  538 */             enableUpdates();
/*      */           }
/*      */           else
/*      */           {
/*  542 */             this.status.setImage(BuddyPluginView.this.iconINOUT);
/*      */             
/*  544 */             enableUpdates();
/*      */           }
/*      */         }
/*      */       }
/*      */       else {
/*  549 */         disableUpdates();
/*      */         
/*  551 */         this.status.setVisible(false);
/*  552 */         this.label.setVisible(false);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected void enableUpdates()
/*      */     {
/*  559 */       if (this.update_event == null)
/*      */       {
/*  561 */         this.update_event = SimpleTimer.addPeriodicEvent("Buddy:GuiUpdater", 2500L, new TimerEventPerformer()
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */           public void perform(TimerEvent event)
/*      */           {
/*      */ 
/*      */ 
/*  570 */             synchronized (BuddyPluginView.statusUpdater.this)
/*      */             {
/*  572 */               if ((BuddyPluginView.statusUpdater.this.tracker.isEnabled()) && ((BuddyPluginView.statusUpdater.this.crypto_ok) || (!BuddyPluginView.statusUpdater.this.has_buddies)))
/*      */               {
/*      */ 
/*      */ 
/*  576 */                 int ns = BuddyPluginView.statusUpdater.this.tracker.getNetworkStatus();
/*      */                 String tt;
/*  578 */                 String tt; if (ns == 1)
/*      */                 {
/*  580 */                   tt = MessageText.getString("azbuddy.tracker.bbb.status.idle");
/*      */                 } else { String tt;
/*  582 */                   if (ns == 3)
/*      */                   {
/*  584 */                     tt = MessageText.getString("azbuddy.tracker.bbb.status.in") + ": " + DisplayFormatters.formatByteCountToKiBEtcPerSec(BuddyPluginView.statusUpdater.this.tracker.getNetworkReceiveBytesPerSecond());
/*      */                   } else { String tt;
/*  586 */                     if (ns == 2)
/*      */                     {
/*  588 */                       tt = MessageText.getString("azbuddy.tracker.bbb.status.out") + ": " + DisplayFormatters.formatByteCountToKiBEtcPerSec(BuddyPluginView.statusUpdater.this.tracker.getNetworkSendBytesPerSecond());
/*      */                     }
/*      */                     else
/*      */                     {
/*  592 */                       tt = MessageText.getString("azbuddy.tracker.bbb.status.inout") + ": " + DisplayFormatters.formatByteCountToKiBEtcPerSec(BuddyPluginView.statusUpdater.this.tracker.getNetworkReceiveBytesPerSecond()) + "/" + DisplayFormatters.formatByteCountToKiBEtcPerSec(BuddyPluginView.statusUpdater.this.tracker.getNetworkSendBytesPerSecond()); }
/*      */                   }
/*      */                 }
/*  595 */                 BuddyPluginView.statusUpdater.this.status.setTooltipText(tt);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     protected void disableUpdates()
/*      */     {
/*  606 */       if (this.update_event != null)
/*      */       {
/*  608 */         this.update_event.cancel();
/*      */         
/*  610 */         this.update_event = null;
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public void enabledStateChanged(BuddyPluginTracker tracker, boolean enabled)
/*      */     {
/*  619 */       updateStatus();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*  625 */   private static Object CHAT_LM_KEY = new Object();
/*      */   
/*  627 */   private HashMap<UISWTView, BetaSubViewHolder> beta_subviews = new HashMap();
/*      */   
/*  629 */   private Map<BuddyPluginBeta.ChatInstance, Integer> chat_uis = new HashMap();
/*      */   
/*      */   private UISWTStatusEntry beta_status;
/*      */   
/*      */   private Image bs_chat_gray;
/*      */   private Image bs_chat_gray_text;
/*      */   private Image bs_chat_green;
/*      */   
/*      */   private void checkBetaInit()
/*      */   {
/*  639 */     if ((this.plugin.isBetaEnabled()) && (this.plugin.getBeta().isAvailable()))
/*      */     {
/*  641 */       synchronized (this)
/*      */       {
/*  643 */         if (this.beta_init_done)
/*      */         {
/*  645 */           return;
/*      */         }
/*      */         
/*  648 */         this.beta_init_done = true;
/*      */       }
/*      */       
/*  651 */       MenuManager menu_manager = this.plugin.getPluginInterface().getUIManager().getMenuManager();
/*      */       
/*  653 */       MenuItem chat_item = menu_manager.addMenuItem("download_context", "label.chat");
/*      */       
/*  655 */       final MenuItem mi_chat = MenuBuildUtils.addChatMenu(menu_manager, chat_item, new MenuBuildUtils.ChatKeyResolver()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public String getChatKey(Object object)
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*  665 */           return BuddyPluginUtils.getChatKey((Download)object);
/*      */         }
/*      */         
/*  668 */       });
/*  669 */       addBetaSubviews(true);
/*      */       
/*  671 */       this.beta_status = this.ui_instance.createStatusEntry();
/*      */       
/*  673 */       this.beta_status.setImageEnabled(true);
/*      */       
/*  675 */       this.beta_status.setVisible(true);
/*      */       
/*  677 */       updateIdleTT(false);
/*      */       
/*  679 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*  681 */           ImageLoader imageLoader = ImageLoader.getInstance();
/*      */           
/*  683 */           BuddyPluginView.this.bs_chat_gray = imageLoader.getImage("dchat_gray");
/*  684 */           BuddyPluginView.this.bs_chat_gray_text = imageLoader.getImage("dchat_gray_text");
/*  685 */           BuddyPluginView.this.bs_chat_green = imageLoader.getImage("dchat_green");
/*      */           
/*  687 */           BuddyPluginView.this.setBetaStatus(BuddyPluginView.this.bs_chat_gray);
/*      */           
/*  689 */           mi_chat.setGraphic(BuddyPluginView.this.ui_instance.createGraphic(BuddyPluginView.this.bs_chat_gray));
/*      */         }
/*      */         
/*  692 */       });
/*  693 */       this.beta_status.setListener(new UISWTStatusEntryListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void entryClicked(UISWTStatusEntry entry)
/*      */         {
/*      */ 
/*  700 */           Set<BuddyPluginBeta.ChatInstance> current_instances = BuddyPluginView.this.menu_latest_instances;
/*      */           
/*  702 */           for (BuddyPluginBeta.ChatInstance chat : current_instances)
/*      */           {
/*  704 */             if (chat.getMessageOutstanding()) {
/*      */               try
/*      */               {
/*  707 */                 BuddyPluginView.this.openChat(chat.getClone());
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/*  711 */                 Debug.out(e);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/*      */         }
/*  717 */       });
/*  718 */       SimpleTimer.addPeriodicEvent("msgcheck", 30000L, new TimerEventPerformer()
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */         public void perform(TimerEvent event)
/*      */         {
/*      */ 
/*      */ 
/*  727 */           List<BuddyPluginBeta.ChatInstance> chats = BuddyPluginView.this.plugin.getBeta().getChats();
/*      */           
/*  729 */           synchronized (BuddyPluginView.pending_msg_map)
/*      */           {
/*  731 */             for (BuddyPluginBeta.ChatInstance chat : chats)
/*      */             {
/*  733 */               if (!chat.isInvisible())
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/*  738 */                 if (!BuddyPluginView.this.chat_uis.containsKey(chat))
/*      */                 {
/*  740 */                   if ((chat.isFavourite()) || (chat.isAutoNotify()) || (chat.isInteresting()))
/*      */                   {
/*      */ 
/*      */ 
/*  744 */                     if (!chat.isStatistics())
/*      */                     {
/*  746 */                       BuddyPluginBeta.ChatMessage last_msg = chat.getLastMessageRequiringAttention();
/*      */                       
/*  748 */                       if (last_msg != null)
/*      */                       {
/*  750 */                         BuddyPluginBeta.ChatMessage last_handled = (BuddyPluginBeta.ChatMessage)chat.getUserData(BuddyPluginView.CHAT_LM_KEY);
/*      */                         
/*  752 */                         long last_msg_time = last_msg.getTimeStamp();
/*      */                         
/*  754 */                         if ((last_handled == null) || (last_msg_time > last_handled.getTimeStamp()))
/*      */                         {
/*      */ 
/*  757 */                           chat.setUserData(BuddyPluginView.CHAT_LM_KEY, last_msg);
/*      */                           
/*  759 */                           BuddyPluginView.this.betaMessagePending(chat, null, last_msg);
/*      */                         }
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               }
/*      */             }
/*  767 */             BuddyPluginView.this.updateIdleTT(false);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void addBetaSubviews(boolean enable)
/*      */   {
/*  778 */     String[] views = { "MyLibrary.big", "MyTorrents", "MyTorrents.big", "MySeeders", "TagsView" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  786 */     if (enable)
/*      */     {
/*  788 */       TagManagerFactory.getTagManager().addTaggableLifecycleListener(2L, new TaggableLifecycleAdapter()
/*      */       {
/*      */         public void taggableTagged(TagType tag_type, Tag tag, Taggable taggable)
/*      */         {
/*      */           DownloadManager dm;
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  798 */           if (tag_type.getTagType() == 3)
/*      */           {
/*  800 */             dm = (DownloadManager)taggable;
/*      */             
/*  802 */             for (BuddyPluginView.BetaSubViewHolder h : BuddyPluginView.this.beta_subviews.values())
/*      */             {
/*  804 */               BuddyPluginView.BetaSubViewHolder.access$2200(h, dm);
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void taggableUntagged(TagType tag_type, Tag tag, Taggable taggable)
/*      */         {
/*      */           DownloadManager dm;
/*      */           
/*  815 */           if (tag_type.getTagType() == 3)
/*      */           {
/*  817 */             dm = (DownloadManager)taggable;
/*      */             
/*  819 */             for (BuddyPluginView.BetaSubViewHolder h : BuddyPluginView.this.beta_subviews.values())
/*      */             {
/*  821 */               BuddyPluginView.BetaSubViewHolder.access$2200(h, dm);
/*      */             }
/*      */             
/*      */           }
/*      */         }
/*  826 */       });
/*  827 */       UISWTViewEventListener listener = new UISWTViewEventListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public boolean eventOccurred(UISWTViewEvent event)
/*      */         {
/*      */ 
/*  834 */           UISWTView currentView = event.getView();
/*      */           
/*  836 */           switch (event.getType())
/*      */           {
/*      */           case 0: 
/*  839 */             BuddyPluginView.this.beta_subviews.put(currentView, new BuddyPluginView.BetaSubViewHolder(BuddyPluginView.this, null));
/*  840 */             currentView.setDestroyOnDeactivate(false);
/*      */             
/*  842 */             break;
/*      */           
/*      */ 
/*      */           case 2: 
/*  846 */             BuddyPluginView.BetaSubViewHolder subview = (BuddyPluginView.BetaSubViewHolder)BuddyPluginView.this.beta_subviews.get(currentView);
/*      */             
/*  848 */             if (subview != null)
/*      */             {
/*  850 */               BuddyPluginView.BetaSubViewHolder.access$2400(subview, event.getView(), (Composite)event.getData());
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             break;
/*      */           case 1: 
/*  857 */             BuddyPluginView.BetaSubViewHolder subview = (BuddyPluginView.BetaSubViewHolder)BuddyPluginView.this.beta_subviews.get(currentView);
/*      */             
/*  859 */             if (subview != null)
/*      */             {
/*  861 */               BuddyPluginView.BetaSubViewHolder.access$2500(subview, event.getData());
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             break;
/*      */           case 3: 
/*  868 */             BuddyPluginView.BetaSubViewHolder subview = (BuddyPluginView.BetaSubViewHolder)BuddyPluginView.this.beta_subviews.get(currentView);
/*      */             
/*  870 */             if (subview != null)
/*      */             {
/*  872 */               BuddyPluginView.BetaSubViewHolder.access$2600(subview);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             break;
/*      */           case 4: 
/*  879 */             BuddyPluginView.BetaSubViewHolder subview = (BuddyPluginView.BetaSubViewHolder)BuddyPluginView.this.beta_subviews.get(currentView);
/*      */             
/*  881 */             if (subview != null)
/*      */             {
/*  883 */               BuddyPluginView.BetaSubViewHolder.access$2700(subview);
/*      */             }
/*      */             
/*      */ 
/*      */ 
/*      */             break;
/*      */           case 7: 
/*  890 */             BuddyPluginView.BetaSubViewHolder subview = (BuddyPluginView.BetaSubViewHolder)BuddyPluginView.this.beta_subviews.remove(currentView);
/*      */             
/*  892 */             if (subview != null)
/*      */             {
/*  894 */               subview.destroy();
/*      */             }
/*      */             
/*      */             break;
/*      */           }
/*      */           
/*  900 */           return true;
/*      */         }
/*      */       };
/*      */       
/*  904 */       for (String table_id : views)
/*      */       {
/*  906 */         this.ui_instance.addView(table_id, "azbuddy.ui.menu.chat", listener);
/*      */       }
/*      */     }
/*      */     else {
/*  910 */       for (String table_id : views)
/*      */       {
/*  912 */         this.ui_instance.removeViews(table_id, "azbuddy.ui.menu.chat");
/*      */       }
/*      */       
/*  915 */       for (UISWTView entry : new ArrayList(this.beta_subviews.keySet()))
/*      */       {
/*  917 */         entry.closeView();
/*      */       }
/*      */       
/*  920 */       this.beta_subviews.clear();
/*      */     }
/*      */   }
/*      */   
/*  924 */   private static Map<String, Object[]> pending_msg_map = new HashMap();
/*      */   
/*      */   private static TimerEventPeriodic pending_msg_event;
/*      */   
/*      */ 
/*      */   protected void registerUI(BuddyPluginBeta.ChatInstance chat)
/*      */   {
/*  931 */     synchronized (pending_msg_map)
/*      */     {
/*  933 */       Integer num = (Integer)this.chat_uis.get(chat);
/*      */       Integer localInteger1;
/*  935 */       if (num == null)
/*      */       {
/*  937 */         num = Integer.valueOf(1);
/*      */       }
/*      */       else
/*      */       {
/*  941 */         localInteger1 = num;Integer localInteger2 = num = Integer.valueOf(num.intValue() + 1);
/*      */       }
/*      */       
/*  944 */       this.chat_uis.put(chat, num);
/*      */       
/*  946 */       if (num.intValue() == 1)
/*      */       {
/*  948 */         updateIdleTT(false);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void unregisterUI(BuddyPluginBeta.ChatInstance chat)
/*      */   {
/*  957 */     synchronized (pending_msg_map)
/*      */     {
/*  959 */       Integer num = (Integer)this.chat_uis.get(chat);
/*      */       Integer localInteger1;
/*  961 */       if (num != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  967 */         localInteger1 = num;Integer localInteger2 = num = Integer.valueOf(num.intValue() - 1);
/*      */       }
/*      */       
/*  970 */       if (num.intValue() == 0)
/*      */       {
/*  972 */         this.chat_uis.remove(chat);
/*      */         
/*  974 */         updateIdleTT(false);
/*      */       }
/*      */       else
/*      */       {
/*  978 */         this.chat_uis.put(chat, num);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private List<BuddyPluginBeta.ChatInstance> sortChats(Collection<BuddyPluginBeta.ChatInstance> chats)
/*      */   {
/*  987 */     List<BuddyPluginBeta.ChatInstance> result = new ArrayList(chats);
/*      */     
/*  989 */     Collections.sort(result, new Comparator()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public int compare(BuddyPluginBeta.ChatInstance o1, BuddyPluginBeta.ChatInstance o2)
/*      */       {
/*      */ 
/*      */ 
/*  998 */         int res = o1.getNetAndKey().compareTo(o2.getNetAndKey());
/*      */         
/* 1000 */         return res;
/*      */       }
/*      */       
/* 1003 */     });
/* 1004 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void updateIdleTT(boolean known_to_be_idle)
/*      */   {
/* 1011 */     Iterator<Map.Entry<String, Object[]>> it = pending_msg_map.entrySet().iterator();
/*      */     
/* 1013 */     boolean has_pending = false;
/*      */     
/* 1015 */     if (!known_to_be_idle)
/*      */     {
/* 1017 */       while (it.hasNext())
/*      */       {
/* 1019 */         Map.Entry<String, Object[]> map_entry = (Map.Entry)it.next();
/*      */         
/* 1021 */         Object[] entry = (Object[])map_entry.getValue();
/*      */         
/* 1023 */         BuddyPluginBeta.ChatInstance chat = (BuddyPluginBeta.ChatInstance)entry[2];
/*      */         
/* 1025 */         if (!chat.getDisableNewMsgIndications())
/*      */         {
/* 1027 */           has_pending = true;
/*      */           
/* 1029 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */     
/* 1034 */     if (!has_pending)
/*      */     {
/* 1036 */       Set<BuddyPluginBeta.ChatInstance> instances = new HashSet();
/*      */       
/* 1038 */       if (this.chat_uis.size() > 0)
/*      */       {
/* 1040 */         for (BuddyPluginBeta.ChatInstance chat : this.chat_uis.keySet())
/*      */         {
/* 1042 */           instances.add(chat);
/*      */         }
/*      */       }
/*      */       
/* 1046 */       List<BuddyPluginBeta.ChatInstance> chats = this.plugin.getBeta().getChats();
/*      */       
/* 1048 */       for (BuddyPluginBeta.ChatInstance chat : chats)
/*      */       {
/* 1050 */         if (!this.chat_uis.containsKey(chat))
/*      */         {
/* 1052 */           if ((chat.isFavourite()) || (chat.isPrivateChat()))
/*      */           {
/* 1054 */             instances.add(chat);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/* 1059 */       String text = MessageText.getString("label.no.messages");
/*      */       
/* 1061 */       for (BuddyPluginBeta.ChatInstance chat : sortChats(instances))
/*      */       {
/* 1063 */         text = text + "\n  " + chat.getShortName();
/*      */       }
/*      */       
/* 1066 */       if (this.beta_status != null)
/*      */       {
/* 1068 */         this.beta_status.setTooltipText(text);
/*      */       }
/*      */       
/* 1071 */       buildMenu(instances);
/*      */       
/* 1073 */       setBetaStatus(this.bs_chat_gray);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected void playSound()
/*      */   {
/* 1080 */     if (this.plugin.getBeta().getSoundEnabled())
/*      */     {
/* 1082 */       final String sound_file = this.plugin.getBeta().getSoundFile();
/*      */       
/* 1084 */       new AEThread2("BuddyPluginSound") {
/*      */         public void run() {
/*      */           try {
/* 1087 */             AudioClip audio_clip = null;
/*      */             
/* 1089 */             if (sound_file.length() == 0)
/*      */             {
/* 1091 */               audio_clip = Applet.newAudioClip(BuddyPluginView.class.getClassLoader().getResource("org/gudy/azureus2/ui/icons/downloadFinished.wav"));
/*      */             }
/*      */             else
/*      */             {
/* 1095 */               URL file_url = new File(sound_file).toURI().toURL();
/*      */               
/* 1097 */               audio_clip = Applet.newAudioClip(file_url);
/*      */             }
/*      */             
/* 1100 */             audio_clip.play();
/*      */             
/* 1102 */             Thread.sleep(2500L);
/*      */           }
/*      */           catch (Throwable e) {}
/*      */         }
/*      */       }.start();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void betaMessagePending(BuddyPluginBeta.ChatInstance chat, Control comp_maybe_null, BuddyPluginBeta.ChatMessage pending_message)
/*      */   {
/* 1118 */     synchronized (pending_msg_map)
/*      */     {
/* 1120 */       String key = chat.getNetAndKey();
/*      */       
/* 1122 */       Object[] entry = (Object[])pending_msg_map.get(key);
/*      */       
/* 1124 */       if (pending_message != null)
/*      */       {
/* 1126 */         if (chat.isOldOutstandingMessage(pending_message))
/*      */         {
/* 1128 */           return;
/*      */         }
/*      */         
/* 1131 */         chat.setMessageOutstanding(pending_message);
/*      */         
/* 1133 */         if (entry == null)
/*      */         {
/* 1135 */           entry = new Object[] { Integer.valueOf(1), new HashSet(), chat };
/*      */           
/* 1137 */           pending_msg_map.put(key, entry);
/*      */         }
/*      */         else
/*      */         {
/* 1141 */           entry[0] = Integer.valueOf(((Integer)entry[0]).intValue() + 1);
/*      */         }
/*      */         
/* 1144 */         HashSet<Control> controls = (HashSet)entry[1];
/*      */         
/* 1146 */         if (controls.contains(comp_maybe_null))
/*      */         {
/* 1148 */           return;
/*      */         }
/*      */         
/* 1151 */         controls.add(comp_maybe_null);
/*      */         
/* 1153 */         if (pending_msg_event == null)
/*      */         {
/* 1155 */           pending_msg_event = SimpleTimer.addPeriodicEvent("BPPM", 2500L, new TimerEventPerformer()
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1161 */             private int tick_count = 0;
/*      */             
/* 1163 */             private Set<BuddyPluginBeta.ChatInstance> prev_instances = new HashSet();
/*      */             
/*      */ 
/*      */ 
/*      */             public void perform(TimerEvent event)
/*      */             {
/* 1169 */               this.tick_count += 1;
/*      */               
/* 1171 */               synchronized (BuddyPluginView.pending_msg_map)
/*      */               {
/* 1173 */                 Set<BuddyPluginBeta.ChatInstance> current_instances = new HashSet();
/* 1174 */                 Map<BuddyPluginBeta.ChatInstance, Object> instance_map = new HashMap();
/*      */                 
/* 1176 */                 Iterator<Map.Entry<String, Object[]>> it = BuddyPluginView.pending_msg_map.entrySet().iterator();
/*      */                 
/* 1178 */                 boolean has_new = false;
/*      */                 
/* 1180 */                 while (it.hasNext())
/*      */                 {
/* 1182 */                   Map.Entry<String, Object[]> map_entry = (Map.Entry)it.next();
/*      */                   
/* 1184 */                   Object[] entry = (Object[])map_entry.getValue();
/*      */                   
/* 1186 */                   BuddyPluginBeta.ChatInstance chat = (BuddyPluginBeta.ChatInstance)entry[2];
/*      */                   
/* 1188 */                   if (chat.isDestroyed())
/*      */                   {
/* 1190 */                     it.remove();
/*      */                   }
/*      */                   else
/*      */                   {
/* 1194 */                     HashSet<Control> comps = (HashSet)entry[1];
/*      */                     
/* 1196 */                     Iterator<Control> control_it = comps.iterator();
/*      */                     
/* 1198 */                     while (control_it.hasNext())
/*      */                     {
/* 1200 */                       Control c = (Control)control_it.next();
/*      */                       
/* 1202 */                       if ((c != null) && (c.isDisposed()))
/*      */                       {
/* 1204 */                         it.remove();
/*      */                       }
/*      */                     }
/*      */                     
/* 1208 */                     if (comps.size() == 0)
/*      */                     {
/* 1210 */                       it.remove();
/*      */ 
/*      */ 
/*      */                     }
/* 1214 */                     else if (!chat.getDisableNewMsgIndications())
/*      */                     {
/* 1216 */                       current_instances.add(chat);
/*      */                       
/* 1218 */                       if (!this.prev_instances.contains(chat))
/*      */                       {
/* 1220 */                         has_new = true;
/*      */                       }
/*      */                       
/* 1223 */                       instance_map.put(chat, entry[0]);
/*      */                     }
/*      */                   }
/*      */                 }
/*      */                 
/*      */ 
/*      */ 
/* 1230 */                 if (BuddyPluginView.pending_msg_map.size() == 0)
/*      */                 {
/* 1232 */                   BuddyPluginView.pending_msg_event.cancel();
/*      */                   
/* 1234 */                   BuddyPluginView.access$2802(null);
/*      */                 }
/*      */                 
/* 1237 */                 if (current_instances.size() == 0)
/*      */                 {
/* 1239 */                   BuddyPluginView.this.updateIdleTT(true);
/*      */                 }
/*      */                 else
/*      */                 {
/* 1243 */                   String tt_text = "";
/*      */                   
/* 1245 */                   for (BuddyPluginBeta.ChatInstance chat : BuddyPluginView.this.sortChats(current_instances))
/*      */                   {
/* 1247 */                     String short_name = chat.getShortName();
/*      */                     
/* 1249 */                     tt_text = tt_text + (tt_text.length() == 0 ? "" : "\n") + instance_map.get(chat) + " - " + short_name;
/*      */                   }
/*      */                   
/* 1252 */                   BuddyPluginView.this.buildMenu(current_instances);
/*      */                   
/* 1254 */                   if (has_new)
/*      */                   {
/* 1256 */                     BuddyPluginView.this.playSound();
/*      */                   }
/*      */                   
/* 1259 */                   BuddyPluginView.this.beta_status.setTooltipText(tt_text);
/*      */                   
/* 1261 */                   BuddyPluginView.this.setBetaStatus(this.tick_count % 2 == 0 ? BuddyPluginView.this.bs_chat_gray_text : BuddyPluginView.this.bs_chat_green);
/*      */                 }
/*      */                 
/* 1264 */                 this.prev_instances = current_instances;
/*      */               }
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */       else {
/* 1271 */         chat.setUserData(CHAT_LM_KEY, chat.getLastMessageRequiringAttention());
/*      */         
/* 1273 */         chat.setMessageOutstanding(null);
/*      */         
/* 1275 */         if (entry != null)
/*      */         {
/* 1277 */           pending_msg_map.remove(key);
/*      */           
/* 1279 */           if (pending_msg_event == null)
/*      */           {
/* 1281 */             Debug.out("eh?");
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void setBetaStatus(final Image image)
/*      */   {
/* 1292 */     this.beta_status.setImage(image);
/*      */     
/* 1294 */     final AllTransfersBar bar = AllTransfersBar.getBarIfOpen(AzureusCoreFactory.getSingleton().getGlobalManager());
/*      */     
/* 1296 */     if (bar != null)
/*      */     {
/* 1298 */       Utils.execSWTThread(new Runnable()
/*      */       {
/*      */ 
/*      */         public void run()
/*      */         {
/*      */ 
/* 1304 */           bar.setIconImage(image == BuddyPluginView.this.bs_chat_gray ? null : image);
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public String renderMessage(BuddyPluginBeta.ChatInstance chat, BuddyPluginBeta.ChatMessage message)
/*      */   {
/* 1315 */     List<StyleRange> ranges = new ArrayList();
/*      */     
/* 1317 */     String msg = BuddyPluginViewBetaChat.renderMessage(null, chat, message, message.getMessage(), message.getMessageType(), 0, ranges, null, null, null);
/*      */     
/* 1319 */     StringBuilder new_msg = new StringBuilder();
/*      */     
/* 1321 */     int pos = 0;
/*      */     
/* 1323 */     for (StyleRange range : ranges)
/*      */     {
/* 1325 */       Object data = range.data;
/*      */       
/* 1327 */       if ((range.underline) && ((data instanceof String)))
/*      */       {
/* 1329 */         int start = range.start;
/* 1330 */         int length = range.length;
/*      */         
/* 1332 */         String link_text = msg.substring(start, start + length);
/* 1333 */         String link_url = (String)data;
/*      */         
/* 1335 */         if (start > pos)
/*      */         {
/* 1337 */           new_msg.append(msg, pos, start);
/*      */         }
/*      */         
/* 1340 */         new_msg.append("<A HREF=\"" + link_url + "\">" + link_text + "</A>");
/*      */         
/* 1342 */         pos = start + length;
/*      */       }
/*      */     }
/*      */     
/* 1346 */     if (pos == 0)
/*      */     {
/* 1348 */       return msg;
/*      */     }
/*      */     
/*      */ 
/* 1352 */     if (pos < msg.length())
/*      */     {
/* 1354 */       new_msg.append(msg.substring(pos));
/*      */     }
/*      */     
/* 1357 */     return new_msg.toString();
/*      */   }
/*      */   
/*      */ 
/* 1361 */   private List<MenuItem> menu_items = new ArrayList();
/* 1362 */   private Set<BuddyPluginBeta.ChatInstance> menu_latest_instances = new HashSet();
/*      */   
/*      */ 
/*      */ 
/*      */   private void buildMenu(final Set<BuddyPluginBeta.ChatInstance> current_instances)
/*      */   {
/* 1368 */     if ((this.menu_items.size() == 0) || (!this.menu_latest_instances.equals(current_instances)))
/*      */     {
/* 1370 */       for (MenuItem mi : this.menu_items)
/*      */       {
/* 1372 */         mi.remove();
/*      */       }
/*      */       
/* 1375 */       this.menu_items.clear();
/*      */       
/* 1377 */       final MenuManager menu_manager = this.plugin.getPluginInterface().getUIManager().getMenuManager();
/*      */       
/* 1379 */       MenuContext mc = this.beta_status.getMenuContext();
/*      */       
/* 1381 */       for (final BuddyPluginBeta.ChatInstance chat : sortChats(current_instances))
/*      */       {
/* 1383 */         String short_name = chat.getShortName();
/*      */         
/* 1385 */         MenuItem mi = menu_manager.addMenuItem(mc, "!" + short_name + "!");
/*      */         
/* 1387 */         mi.addListener(new MenuItemListener()
/*      */         {
/*      */           public void selected(MenuItem menu, Object target)
/*      */           {
/*      */             try
/*      */             {
/* 1393 */               BuddyPluginView.this.openChat(chat.getClone());
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 1397 */               Debug.out(e);
/*      */             }
/*      */             
/*      */           }
/* 1401 */         });
/* 1402 */         this.menu_items.add(mi);
/*      */       }
/*      */       
/* 1405 */       boolean need_sep = true;
/*      */       
/*      */ 
/*      */ 
/* 1409 */       if (current_instances.size() > 1)
/*      */       {
/* 1411 */         MenuItem mi = menu_manager.addMenuItem(mc, "sep1");
/*      */         
/* 1413 */         need_sep = false;
/*      */         
/* 1415 */         mi.setStyle(4);
/*      */         
/* 1417 */         this.menu_items.add(mi);
/*      */         
/* 1419 */         mi = menu_manager.addMenuItem(mc, "label.open.all");
/*      */         
/* 1421 */         mi.addListener(new MenuItemListener()
/*      */         {
/*      */ 
/*      */           public void selected(MenuItem menu, Object target)
/*      */           {
/* 1426 */             for (BuddyPluginBeta.ChatInstance chat : current_instances) {
/*      */               try
/*      */               {
/* 1429 */                 BuddyPluginView.this.openChat(chat.getClone());
/*      */               }
/*      */               catch (Throwable e)
/*      */               {
/* 1433 */                 Debug.out(e);
/*      */               }
/*      */               
/*      */             }
/*      */           }
/* 1438 */         });
/* 1439 */         this.menu_items.add(mi);
/*      */       }
/*      */       
/* 1442 */       if (need_sep)
/*      */       {
/* 1444 */         MenuItem mi = menu_manager.addMenuItem(mc, "sep2");
/*      */         
/* 1446 */         mi.setStyle(4);
/*      */         
/* 1448 */         this.menu_items.add(mi);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1453 */       MenuItem mi = menu_manager.addMenuItem(mc, "chat.view.create.chat");
/*      */       
/* 1455 */       mi.setStyle(5);
/*      */       
/* 1457 */       this.menu_items.add(mi);
/*      */       
/* 1459 */       mi.addFillListener(new MenuItemFillListener()
/*      */       {
/*      */         public void menuWillBeShown(MenuItem menu, Object data) {
/* 1462 */           menu.removeAllChildItems();
/*      */           
/* 1464 */           MenuItem mi = menu_manager.addMenuItem(menu, "!" + MessageText.getString("label.public") + "...!");
/*      */           
/* 1466 */           mi.addListener(new MenuItemListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void selected(MenuItem menu, Object target)
/*      */             {
/*      */ 
/*      */ 
/* 1474 */               SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("chat.view.enter.key.title", "chat.view.enter.key.msg");
/*      */               
/*      */ 
/* 1477 */               entryWindow.prompt();
/*      */               
/* 1479 */               if (entryWindow.hasSubmittedInput())
/*      */               {
/* 1481 */                 String key = entryWindow.getSubmittedInput().trim();
/*      */                 
/* 1483 */                 BuddyPluginUtils.createBetaChat("Public", key, null);
/*      */               }
/*      */               
/*      */             }
/* 1487 */           });
/* 1488 */           mi = menu_manager.addMenuItem(menu, "!" + MessageText.getString("label.anon") + "...!");
/*      */           
/* 1490 */           mi.addListener(new MenuItemListener()
/*      */           {
/*      */ 
/*      */ 
/*      */             public void selected(MenuItem menu, Object target)
/*      */             {
/*      */ 
/*      */ 
/* 1498 */               if (BuddyPluginView.this.plugin.getBeta().isI2PAvailable())
/*      */               {
/* 1500 */                 SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("chat.view.enter.key.title", "chat.view.enter.key.msg");
/*      */                 
/*      */ 
/* 1503 */                 entryWindow.prompt();
/*      */                 
/* 1505 */                 if (entryWindow.hasSubmittedInput())
/*      */                 {
/* 1507 */                   String key = entryWindow.getSubmittedInput().trim();
/*      */                   
/* 1509 */                   BuddyPluginUtils.createBetaChat("I2P", key, null);
/*      */                 }
/*      */               }
/*      */               else {
/* 1513 */                 I2PHelpers.installI2PHelper(null, null, null);
/*      */               }
/*      */             }
/*      */           });
/*      */           
/*      */ 
/* 1519 */           if (I2PHelpers.isInstallingI2PHelper())
/*      */           {
/* 1521 */             mi.setEnabled(false);
/* 1522 */             mi.setText(mi.getText() + " (" + MessageText.getString("PeersView.state.pending") + ")");
/*      */           }
/*      */           
/*      */         }
/*      */         
/*      */ 
/* 1528 */       });
/* 1529 */       mi = menu_manager.addMenuItem(mc, "!" + MessageText.getString("chats.view.heading") + "...!");
/*      */       
/* 1531 */       mi.addListener(new MenuItemListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void selected(MenuItem menu, Object target)
/*      */         {
/*      */ 
/*      */ 
/* 1539 */           UIFunctions uif = UIFunctionsManager.getUIFunctions();
/*      */           
/* 1541 */           if (uif != null)
/*      */           {
/* 1543 */             uif.getMDI().showEntryByID("ChatOverview");
/*      */           }
/*      */           
/*      */         }
/* 1547 */       });
/* 1548 */       this.menu_items.add(mi);
/*      */       
/* 1550 */       mi = menu_manager.addMenuItem(mc, "sep3");
/*      */       
/* 1552 */       mi.setStyle(4);
/*      */       
/* 1554 */       this.menu_items.add(mi);
/*      */       
/*      */ 
/*      */ 
/* 1558 */       mi = menu_manager.addMenuItem(mc, "MainWindow.menu.view.configuration");
/*      */       
/* 1560 */       mi.addListener(new MenuItemListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void selected(MenuItem menu, Object target)
/*      */         {
/*      */ 
/*      */ 
/* 1568 */           BuddyPluginView.this.ui_instance.openView("Main", BuddyPluginView.this.VIEW_ID, null);
/*      */         }
/*      */         
/* 1571 */       });
/* 1572 */       this.menu_items.add(mi);
/*      */       
/*      */ 
/* 1575 */       this.menu_latest_instances = current_instances;
/*      */     }
/*      */   }
/*      */   
/* 1579 */   private static AsyncDispatcher public_dispatcher = new AsyncDispatcher();
/* 1580 */   private static AsyncDispatcher anon_dispatcher = new AsyncDispatcher();
/*      */   
/* 1582 */   private static AtomicInteger public_done = new AtomicInteger();
/* 1583 */   private static AtomicInteger anon_done = new AtomicInteger();
/*      */   
/*      */ 
/* 1586 */   private static final Object adapter_key = new Object();
/*      */   
/*      */ 
/*      */ 
/*      */   public BuddyPluginViewInterface.DownloadAdapter getDownloadAdapter(final Download download)
/*      */   {
/* 1592 */     synchronized (adapter_key)
/*      */     {
/* 1594 */       BuddyPluginViewInterface.DownloadAdapter adapter = (BuddyPluginViewInterface.DownloadAdapter)download.getUserData(adapter_key);
/*      */       
/* 1596 */       if (adapter == null)
/*      */       {
/* 1598 */         adapter = new BuddyPluginViewInterface.DownloadAdapter()
/*      */         {
/*      */ 
/*      */           public DownloadManager getCoreDownload()
/*      */           {
/*      */ 
/* 1604 */             return PluginCoreUtils.unwrap(download);
/*      */           }
/*      */           
/*      */ 
/*      */           public String[] getNetworks()
/*      */           {
/* 1610 */             DownloadManager dm = getCoreDownload();
/*      */             
/* 1612 */             if (dm == null)
/*      */             {
/* 1614 */               return new String[0];
/*      */             }
/*      */             
/*      */ 
/* 1618 */             return dm.getDownloadState().getNetworks();
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */           public String getChatKey()
/*      */           {
/* 1625 */             return BuddyPluginUtils.getChatKey(download);
/*      */           }
/*      */           
/* 1628 */         };
/* 1629 */         download.setUserData(adapter_key, adapter);
/*      */       }
/*      */       
/* 1632 */       return adapter;
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public BuddyPluginViewInterface.View buildView(Map<String, Object> properties, BuddyPluginViewInterface.ViewListener listener)
/*      */   {
/* 1641 */     Composite swt_composite = (Composite)properties.get("swt_comp");
/*      */     
/* 1643 */     BuddyPluginBeta.ChatInstance chat = (BuddyPluginBeta.ChatInstance)properties.get("chat");
/*      */     
/* 1645 */     if (chat != null)
/*      */     {
/* 1647 */       final BuddyPluginViewBetaChat view = new BuddyPluginViewBetaChat(this, this.plugin, chat, swt_composite);
/*      */       
/* 1649 */       new BuddyPluginViewInterface.View()
/*      */       {
/*      */ 
/*      */         public void activate()
/*      */         {
/*      */ 
/* 1655 */           view.activate();
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void handleDrop(String drop)
/*      */         {
/* 1662 */           view.handleExternalDrop(drop);
/*      */         }
/*      */         
/*      */ 
/*      */         public void destroy()
/*      */         {
/* 1668 */           view.close();
/*      */         }
/*      */       };
/*      */     }
/* 1672 */     BetaSubViewHolder view = new BetaSubViewHolder(null);
/*      */     
/*      */ 
/* 1675 */     BuddyPluginViewInterface.DownloadAdapter download = (BuddyPluginViewInterface.DownloadAdapter)properties.get("download");
/*      */     
/* 1677 */     view.initialise(swt_composite, download, listener);
/*      */     
/* 1679 */     return view;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private class BetaSubViewHolder
/*      */     implements BuddyPluginViewInterface.View
/*      */   {
/* 1687 */     private int CHAT_DOWNLOAD = 0;
/* 1688 */     private int CHAT_TRACKERS = 1;
/* 1689 */     private int CHAT_TAG = 2;
/* 1690 */     private int CHAT_GENERAL = 3;
/* 1691 */     private int CHAT_FAVOURITES = 4;
/*      */     
/*      */     private boolean download_only_mode;
/*      */     
/*      */     private BuddyPluginViewInterface.ViewListener view_listener;
/*      */     
/*      */     private Composite[] chat_composites;
/*      */     
/* 1699 */     private List<Button> mode_buttons = new ArrayList();
/*      */     
/*      */     private Group middle;
/*      */     
/*      */     private CTabFolder tab_folder;
/*      */     
/*      */     private CTabItem public_item;
/*      */     private CTabItem anon_item;
/*      */     private CTabItem neither_item;
/* 1708 */     private int last_build_chat_mode = -1;
/* 1709 */     private int chat_mode = this.CHAT_DOWNLOAD;
/*      */     
/*      */     private String last_selected_network;
/*      */     
/*      */     private BuddyPluginViewInterface.DownloadAdapter current_download;
/*      */     
/*      */     private String current_tracker;
/*      */     
/*      */     private Tag current_tag;
/*      */     
/*      */     private String current_general;
/*      */     private String current_favourite_net;
/*      */     private String current_favourite_key;
/*      */     private Tag current_ds_tag;
/*      */     private boolean have_focus;
/* 1724 */     private boolean rebuild_outstanding = true;
/*      */     
/*      */     private Group lhs;
/*      */     
/*      */     private BetaSubViewHolder()
/*      */     {
/* 1730 */       BuddyPluginView.this.checkBetaInit();
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void initialise(Composite parent, BuddyPluginViewInterface.DownloadAdapter download, BuddyPluginViewInterface.ViewListener listener)
/*      */     {
/* 1739 */       this.view_listener = listener;
/* 1740 */       this.current_download = download;
/* 1741 */       this.download_only_mode = true;
/*      */       
/* 1743 */       initialiseSupport(parent);
/*      */       
/* 1745 */       String[] nets = this.current_download.getNetworks();
/*      */       
/* 1747 */       if (nets.length > 0)
/*      */       {
/* 1749 */         String net_to_activate = nets[0];
/*      */         
/* 1751 */         for (String net : nets)
/*      */         {
/* 1753 */           if (net == "Public")
/*      */           {
/* 1755 */             net_to_activate = net;
/*      */             
/* 1757 */             break;
/*      */           }
/*      */         }
/*      */         
/* 1761 */         activateNetwork(net_to_activate, true);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void initialise(UISWTView view, Composite parent)
/*      */     {
/* 1770 */       UISWTView parent_view = view.getParentView();
/*      */       
/* 1772 */       if (parent_view != null)
/*      */       {
/* 1774 */         Object initial_ds = parent_view.getInitialDataSource();
/*      */         
/* 1776 */         if ((initial_ds instanceof Tag))
/*      */         {
/* 1778 */           this.current_ds_tag = ((Tag)initial_ds);
/*      */         }
/*      */       }
/*      */       
/* 1782 */       initialiseSupport(parent);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void initialiseSupport(Composite parent)
/*      */     {
/* 1789 */       Composite composite = parent;
/*      */       
/* 1791 */       GridLayout layout = new GridLayout();
/* 1792 */       layout.numColumns = (this.download_only_mode ? 1 : 3);
/* 1793 */       layout.marginHeight = 0;
/* 1794 */       layout.marginWidth = 0;
/* 1795 */       layout.marginTop = 4;
/* 1796 */       layout.marginRight = 4;
/* 1797 */       composite.setLayout(layout);
/*      */       
/* 1799 */       GridData grid_data = new GridData(1808);
/* 1800 */       Utils.setLayoutData(composite, grid_data);
/*      */       
/* 1802 */       if (!this.download_only_mode)
/*      */       {
/*      */ 
/*      */ 
/* 1806 */         this.lhs = new Group(composite, 0);
/* 1807 */         this.lhs.setText(MessageText.getString("label.chat.type"));
/* 1808 */         layout = new GridLayout();
/* 1809 */         layout.numColumns = 1;
/* 1810 */         layout.horizontalSpacing = 1;
/* 1811 */         layout.verticalSpacing = 1;
/* 1812 */         this.lhs.setLayout(layout);
/* 1813 */         grid_data = new GridData(1040);
/*      */         
/* 1815 */         Utils.setLayoutData(this.lhs, grid_data);
/*      */         
/* 1817 */         Button downloads = new Button(this.lhs, 2);
/* 1818 */         downloads.setText(MessageText.getString("v3.MainWindow.button.download"));
/* 1819 */         downloads.setData(Integer.valueOf(this.CHAT_DOWNLOAD));
/*      */         
/* 1821 */         Button trackers = new Button(this.lhs, 2);
/* 1822 */         trackers.setText(MessageText.getString("label.trackers"));
/* 1823 */         trackers.setData(Integer.valueOf(this.CHAT_TRACKERS));
/*      */         
/* 1825 */         Button tags = new Button(this.lhs, 2);
/* 1826 */         tags.setText(MessageText.getString("label.tags"));
/* 1827 */         tags.setData(Integer.valueOf(this.CHAT_TAG));
/*      */         
/* 1829 */         Button general = new Button(this.lhs, 2);
/* 1830 */         general.setText(MessageText.getString("ConfigView.section.global"));
/* 1831 */         general.setData(Integer.valueOf(this.CHAT_GENERAL));
/*      */         
/* 1833 */         Button favourites = new Button(this.lhs, 2);
/* 1834 */         favourites.setText(MessageText.getString("label.favorites"));
/* 1835 */         favourites.setData(Integer.valueOf(this.CHAT_FAVOURITES));
/*      */         
/* 1837 */         if (this.download_only_mode)
/*      */         {
/* 1839 */           this.lhs.setVisible(false);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/* 1844 */         this.middle = new Group(composite, 0);
/* 1845 */         layout = new GridLayout();
/* 1846 */         layout.numColumns = 1;
/* 1847 */         this.middle.setLayout(layout);
/* 1848 */         grid_data = new GridData(1040);
/* 1849 */         grid_data.widthHint = 0;
/* 1850 */         Utils.setLayoutData(this.middle, grid_data);
/*      */         
/* 1852 */         this.middle.setText("");
/*      */         
/* 1854 */         this.middle.setVisible(false);
/*      */         
/* 1856 */         downloads.addSelectionListener(new SelectionAdapter() {
/*      */           public void widgetSelected(SelectionEvent e) {
/* 1858 */             BuddyPluginView.BetaSubViewHolder.this.buildChatMode(BuddyPluginView.BetaSubViewHolder.this.CHAT_DOWNLOAD, true);
/*      */           }
/* 1860 */         });
/* 1861 */         trackers.addSelectionListener(new SelectionAdapter() {
/*      */           public void widgetSelected(SelectionEvent e) {
/* 1863 */             BuddyPluginView.BetaSubViewHolder.this.buildChatMode(BuddyPluginView.BetaSubViewHolder.this.CHAT_TRACKERS, true);
/*      */           }
/* 1865 */         });
/* 1866 */         tags.addSelectionListener(new SelectionAdapter() {
/*      */           public void widgetSelected(SelectionEvent e) {
/* 1868 */             BuddyPluginView.BetaSubViewHolder.this.buildChatMode(BuddyPluginView.BetaSubViewHolder.this.CHAT_TAG, true);
/*      */           }
/* 1870 */         });
/* 1871 */         general.addSelectionListener(new SelectionAdapter() {
/*      */           public void widgetSelected(SelectionEvent e) {
/* 1873 */             BuddyPluginView.BetaSubViewHolder.this.buildChatMode(BuddyPluginView.BetaSubViewHolder.this.CHAT_GENERAL, true);
/*      */           }
/* 1875 */         });
/* 1876 */         favourites.addSelectionListener(new SelectionAdapter() {
/*      */           public void widgetSelected(SelectionEvent e) {
/* 1878 */             BuddyPluginView.BetaSubViewHolder.this.buildChatMode(BuddyPluginView.BetaSubViewHolder.this.CHAT_FAVOURITES, true);
/*      */           }
/* 1880 */         });
/* 1881 */         downloads.setSelection(true);
/*      */         
/* 1883 */         this.mode_buttons.add(downloads);
/* 1884 */         this.mode_buttons.add(trackers);
/* 1885 */         this.mode_buttons.add(tags);
/* 1886 */         this.mode_buttons.add(general);
/* 1887 */         this.mode_buttons.add(favourites);
/*      */         
/* 1889 */         setupButtonGroup(this.mode_buttons);
/*      */       }
/*      */       
/*      */ 
/*      */ 
/* 1894 */       this.tab_folder = new CTabFolder(composite, 16384);
/*      */       
/* 1896 */       this.tab_folder.setTabHeight(20);
/* 1897 */       grid_data = new GridData(1808);
/* 1898 */       Utils.setLayoutData(this.tab_folder, grid_data);
/*      */       
/*      */ 
/*      */ 
/* 1902 */       this.public_item = new CTabItem(this.tab_folder, 0);
/*      */       
/* 1904 */       this.public_item.setText(MessageText.getString("label.public.chat"));
/* 1905 */       this.public_item.setData("Public");
/*      */       
/* 1907 */       Composite public_composite = new Composite(this.tab_folder, 0);
/*      */       
/* 1909 */       this.public_item.setControl(public_composite);
/*      */       
/* 1911 */       grid_data = new GridData(1808);
/* 1912 */       Utils.setLayoutData(public_composite, grid_data);
/* 1913 */       public_composite.setData("tabitem", this.public_item);
/*      */       
/*      */ 
/*      */ 
/* 1917 */       Composite anon_composite = null;
/*      */       
/*      */ 
/* 1920 */       this.anon_item = new CTabItem(this.tab_folder, 0);
/*      */       
/* 1922 */       this.anon_item.setText(MessageText.getString("label.anon.chat"));
/* 1923 */       this.anon_item.setData("I2P");
/*      */       
/* 1925 */       anon_composite = new Composite(this.tab_folder, 0);
/*      */       
/* 1927 */       this.anon_item.setControl(anon_composite);
/*      */       
/* 1929 */       grid_data = new GridData(1808);
/* 1930 */       Utils.setLayoutData(anon_composite, grid_data);
/* 1931 */       anon_composite.setData("tabitem", this.anon_item);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/* 1936 */       Composite neither_composite = null;
/*      */       
/*      */ 
/* 1939 */       this.neither_item = new CTabItem(this.tab_folder, 0);
/*      */       
/* 1941 */       neither_composite = new Composite(this.tab_folder, 0);
/*      */       
/* 1943 */       this.neither_item.setControl(neither_composite);
/*      */       
/* 1945 */       grid_data = new GridData(1808);
/* 1946 */       Utils.setLayoutData(neither_composite, grid_data);
/* 1947 */       neither_composite.setData("tabitem", this.neither_item);
/*      */       
/* 1949 */       layout = new GridLayout();
/* 1950 */       layout.numColumns = 1;
/* 1951 */       neither_composite.setLayout(layout);
/*      */       
/* 1953 */       Label info = new Label(neither_composite, 0);
/* 1954 */       info.setText(MessageText.getString("dchat.select.network"));
/*      */       
/*      */ 
/* 1957 */       this.chat_composites = new Composite[] { public_composite, anon_composite, neither_composite };
/*      */       
/* 1959 */       this.tab_folder.addSelectionListener(new SelectionAdapter() {
/*      */         public void widgetSelected(SelectionEvent e) {
/* 1961 */           CTabItem item = (CTabItem)e.item;
/*      */           
/* 1963 */           String network = (String)item.getData();
/*      */           
/* 1965 */           BuddyPluginView.BetaSubViewHolder.this.activateNetwork(network);
/*      */         }
/*      */       });
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void tagsUpdated(DownloadManager dm)
/*      */     {
/* 1974 */       BuddyPluginViewInterface.DownloadAdapter download = this.current_download;
/*      */       
/* 1976 */       if (download == null)
/*      */       {
/* 1978 */         return;
/*      */       }
/*      */       
/* 1981 */       if (this.chat_mode == this.CHAT_TAG)
/*      */       {
/* 1983 */         if (dm == download.getCoreDownload())
/*      */         {
/* 1985 */           Utils.execSWTThread(new Runnable()
/*      */           {
/*      */ 
/*      */             public void run()
/*      */             {
/*      */ 
/* 1991 */               BuddyPluginView.BetaSubViewHolder.this.rebuild_outstanding = true;
/*      */               
/* 1993 */               BuddyPluginView.BetaSubViewHolder.this.activate();
/*      */             }
/*      */           });
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setupButtonGroup(final List<Button> buttons)
/*      */     {
/* 2004 */       for (final Button b : buttons)
/*      */       {
/* 2006 */         b.addSelectionListener(new SelectionAdapter() {
/*      */           public void widgetSelected(SelectionEvent e) {
/* 2008 */             if (!b.getSelection())
/*      */             {
/* 2010 */               b.setSelection(true);
/*      */             }
/* 2012 */             for (Button b2 : buttons)
/*      */             {
/* 2014 */               if (b2 != b) {
/* 2015 */                 b2.setSelection(false);
/*      */               }
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/* 2021 */       Utils.makeButtonsEqualWidth(buttons);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void selectButtonGroup(List<Button> buttons, int data)
/*      */     {
/* 2029 */       for (Button b : buttons)
/*      */       {
/* 2031 */         b.setSelection(((Integer)b.getData()).intValue() == data);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void selectButtonGroup(List<Button> buttons, String data)
/*      */     {
/* 2040 */       for (Button b : buttons)
/*      */       {
/* 2042 */         String str = (String)b.getData();
/*      */         
/* 2044 */         b.setSelection((str != null) && (str.endsWith(data)));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setChatMode(int mode)
/*      */     {
/* 2052 */       if (this.chat_mode == mode)
/*      */       {
/* 2054 */         return;
/*      */       }
/*      */       
/* 2057 */       this.chat_mode = mode;
/*      */       
/* 2059 */       selectButtonGroup(this.mode_buttons, mode);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     private void buildChatMode(int mode, boolean activate)
/*      */     {
/* 2067 */       BuddyPluginViewInterface.DownloadAdapter download = this.current_download;
/*      */       
/* 2069 */       this.chat_mode = mode;
/*      */       
/* 2071 */       if ((mode != this.CHAT_GENERAL) || (this.last_build_chat_mode != mode))
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2076 */         if (!this.download_only_mode)
/*      */         {
/* 2078 */           for (Control c : this.middle.getChildren())
/*      */           {
/* 2080 */             c.dispose();
/*      */           }
/*      */           
/* 2083 */           if ((mode == this.CHAT_DOWNLOAD) || (((mode == this.CHAT_TRACKERS) || (mode == this.CHAT_TAG)) && (download == null) && (this.current_ds_tag == null)))
/*      */           {
/* 2085 */             this.middle.setVisible(false);
/* 2086 */             this.middle.setText("");
/*      */             
/* 2088 */             GridData grid_data = new GridData(1040);
/* 2089 */             grid_data.widthHint = 0;
/* 2090 */             Utils.setLayoutData(this.middle, grid_data);
/*      */           }
/* 2092 */           else if (mode == this.CHAT_TRACKERS)
/*      */           {
/* 2094 */             this.middle.setVisible(true);
/* 2095 */             this.middle.setText(MessageText.getString("label.tracker.selection"));
/*      */             
/* 2097 */             Set<String> trackers = new HashSet();
/*      */             
/* 2099 */             if (this.current_download != null)
/*      */             {
/* 2101 */               DownloadManager core_dm = this.current_download.getCoreDownload();
/*      */               
/* 2103 */               if (core_dm != null)
/*      */               {
/* 2105 */                 trackers = TorrentUtils.getUniqueTrackerHosts(core_dm.getTorrent());
/*      */               }
/*      */             }
/*      */             
/* 2109 */             GridLayout layout = new GridLayout();
/* 2110 */             layout.horizontalSpacing = 1;
/* 2111 */             layout.verticalSpacing = 1;
/*      */             
/* 2113 */             layout.numColumns = 1;
/* 2114 */             this.middle.setLayout(layout);
/* 2115 */             GridData grid_data = new GridData(1040);
/* 2116 */             Utils.setLayoutData(this.middle, grid_data);
/*      */             
/* 2118 */             Set<String> reduced_trackers = new HashSet();
/*      */             
/* 2120 */             for (String tracker : trackers)
/*      */             {
/* 2122 */               tracker = DNSUtils.getInterestingHostSuffix(tracker);
/*      */               
/* 2124 */               if (tracker != null)
/*      */               {
/* 2126 */                 reduced_trackers.add(tracker);
/*      */               }
/*      */             }
/*      */             
/* 2130 */             int num_trackers = reduced_trackers.size();
/*      */             
/* 2132 */             if (num_trackers == 0)
/*      */             {
/* 2134 */               this.current_tracker = null;
/* 2135 */               Label label = new Label(this.middle, 0);
/* 2136 */               label.setText(MessageText.getString("label.none.assigned"));
/* 2137 */               label.setEnabled(false);
/*      */             }
/*      */             else
/*      */             {
/*      */               Composite tracker_area;
/*      */               Composite tracker_area;
/* 2143 */               if (num_trackers > 4)
/*      */               {
/* 2145 */                 tracker_area = createScrolledComposite(this.middle);
/*      */               }
/*      */               else
/*      */               {
/* 2149 */                 tracker_area = this.middle;
/*      */               }
/*      */               
/* 2152 */               List<String> sorted_trackers = new ArrayList(reduced_trackers);
/*      */               
/* 2154 */               Collections.sort(sorted_trackers);
/*      */               
/* 2156 */               if (!sorted_trackers.contains(this.current_tracker))
/*      */               {
/* 2158 */                 this.current_tracker = ((String)sorted_trackers.get(0));
/*      */               }
/*      */               
/* 2161 */               List<Button> buttons = new ArrayList();
/*      */               
/* 2163 */               for (final String tracker : sorted_trackers)
/*      */               {
/* 2165 */                 Button button = new Button(tracker_area, 2);
/*      */                 
/* 2167 */                 button.setText(tracker);
/* 2168 */                 button.setData(tracker);
/*      */                 
/* 2170 */                 button.addSelectionListener(new SelectionAdapter() {
/*      */                   public void widgetSelected(SelectionEvent e) {
/* 2172 */                     BuddyPluginView.BetaSubViewHolder.this.current_tracker = tracker;
/* 2173 */                     BuddyPluginView.BetaSubViewHolder.this.activate();
/* 2174 */                   } });
/* 2175 */                 buttons.add(button);
/*      */               }
/*      */               
/* 2178 */               setupButtonGroup(buttons);
/*      */               
/* 2180 */               selectButtonGroup(buttons, this.current_tracker);
/*      */             }
/* 2182 */           } else if (mode == this.CHAT_TAG)
/*      */           {
/* 2184 */             this.lhs.setVisible(download != null);
/* 2185 */             GridData grid_data = new GridData(1040);
/* 2186 */             if (download == null) {
/* 2187 */               grid_data.exclude = true;
/*      */             }
/* 2189 */             Utils.setLayoutData(this.lhs, grid_data);
/*      */             
/*      */ 
/* 2192 */             this.middle.setVisible(true);
/* 2193 */             this.middle.setText(MessageText.getString("label.tag.selection"));
/*      */             
/*      */             List<Tag> tags;
/*      */             List<Tag> tags;
/* 2197 */             if (this.current_download == null)
/*      */             {
/* 2199 */               tags = new ArrayList();
/*      */             }
/*      */             else
/*      */             {
/* 2203 */               DownloadManager core_dm = this.current_download.getCoreDownload();
/*      */               List<Tag> tags;
/* 2205 */               if (core_dm == null)
/*      */               {
/* 2207 */                 tags = new ArrayList();
/*      */               }
/*      */               else
/*      */               {
/* 2211 */                 tags = TagManagerFactory.getTagManager().getTagsForTaggable(3, core_dm);
/*      */               }
/*      */             }
/*      */             
/* 2215 */             if ((this.current_ds_tag != null) && (!tags.contains(this.current_ds_tag)))
/*      */             {
/* 2217 */               tags.add(this.current_ds_tag);
/*      */             }
/*      */             
/* 2220 */             GridLayout layout = new GridLayout();
/* 2221 */             layout.horizontalSpacing = 1;
/* 2222 */             layout.verticalSpacing = 1;
/*      */             
/* 2224 */             layout.numColumns = 1;
/* 2225 */             this.middle.setLayout(layout);
/* 2226 */             grid_data = new GridData(1040);
/* 2227 */             Utils.setLayoutData(this.middle, grid_data);
/*      */             
/* 2229 */             int num_tags = tags.size();
/*      */             
/* 2231 */             if (num_tags == 0)
/*      */             {
/* 2233 */               this.current_tag = null;
/* 2234 */               Label label = new Label(this.middle, 0);
/* 2235 */               label.setText(MessageText.getString("label.none.assigned"));
/* 2236 */               label.setEnabled(false);
/*      */             }
/*      */             else
/*      */             {
/*      */               Composite tag_area;
/*      */               Composite tag_area;
/* 2242 */               if (num_tags > 4)
/*      */               {
/* 2244 */                 tag_area = createScrolledComposite(this.middle);
/*      */               }
/*      */               else
/*      */               {
/* 2248 */                 tag_area = this.middle;
/*      */               }
/*      */               
/* 2251 */               tags = TagUIUtils.sortTags(tags);
/*      */               
/* 2253 */               if (!tags.contains(this.current_tag))
/*      */               {
/* 2255 */                 this.current_tag = ((Tag)tags.get(0));
/*      */               }
/*      */               
/* 2258 */               List<Button> buttons = new ArrayList();
/*      */               
/* 2260 */               for (final Tag tag : tags)
/*      */               {
/* 2262 */                 Button button = new Button(tag_area, 2);
/*      */                 
/* 2264 */                 String tag_name = tag.getTagName(true);
/*      */                 
/* 2266 */                 button.setText(tag_name);
/* 2267 */                 button.setData(tag_name);
/*      */                 
/* 2269 */                 button.addSelectionListener(new SelectionAdapter() {
/*      */                   public void widgetSelected(SelectionEvent e) {
/* 2271 */                     BuddyPluginView.BetaSubViewHolder.this.current_tag = tag;
/* 2272 */                     BuddyPluginView.BetaSubViewHolder.this.activate();
/* 2273 */                   } });
/* 2274 */                 buttons.add(button);
/*      */               }
/*      */               
/* 2277 */               setupButtonGroup(buttons);
/*      */               
/* 2279 */               selectButtonGroup(buttons, this.current_tag.getTagName(true));
/*      */             }
/* 2281 */           } else if (mode == this.CHAT_GENERAL)
/*      */           {
/* 2283 */             this.middle.setVisible(true);
/* 2284 */             this.middle.setText(MessageText.getString("azbuddy.dchat.general.chats"));
/*      */             
/* 2286 */             GridLayout layout = new GridLayout();
/* 2287 */             layout.horizontalSpacing = 1;
/* 2288 */             layout.verticalSpacing = 1;
/*      */             
/* 2290 */             layout.numColumns = 1;
/* 2291 */             this.middle.setLayout(layout);
/* 2292 */             GridData grid_data = new GridData(1040);
/* 2293 */             Utils.setLayoutData(this.middle, grid_data);
/*      */             
/* 2295 */             List<Button> buttons = new ArrayList();
/*      */             
/* 2297 */             String[][] general_data = { { "label.help", "General: Help" }, { "label.announce", "General: Announce" }, { "label.beta", "test:beta:chat" } };
/*      */             
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 2303 */             for (String[] entry : general_data)
/*      */             {
/* 2305 */               final String key = entry[1];
/*      */               
/* 2307 */               if ((key != "test:beta:chat") || (Constants.isCVSVersion()))
/*      */               {
/*      */ 
/*      */ 
/*      */ 
/* 2312 */                 Button button = new Button(this.middle, 2);
/*      */                 
/* 2314 */                 button.setText(MessageText.getString(entry[0]));
/* 2315 */                 button.setData(key);
/*      */                 
/* 2317 */                 button.addSelectionListener(new SelectionAdapter() {
/*      */                   public void widgetSelected(SelectionEvent e) {
/* 2319 */                     BuddyPluginView.BetaSubViewHolder.this.current_general = key;
/* 2320 */                     BuddyPluginView.BetaSubViewHolder.this.activate();
/*      */                   }
/* 2322 */                 });
/* 2323 */                 buttons.add(button);
/*      */               }
/*      */             }
/* 2326 */             setupButtonGroup(buttons);
/*      */             
/* 2328 */             if (this.current_general != null)
/*      */             {
/* 2330 */               selectButtonGroup(buttons, this.current_general);
/*      */             }
/*      */           }
/*      */           else {
/* 2334 */             this.middle.setVisible(true);
/* 2335 */             this.middle.setText(MessageText.getString("azbuddy.dchat.fave.chats"));
/*      */             
/* 2337 */             GridLayout layout = new GridLayout();
/* 2338 */             layout.horizontalSpacing = 1;
/* 2339 */             layout.verticalSpacing = 1;
/*      */             
/* 2341 */             layout.numColumns = 1;
/* 2342 */             this.middle.setLayout(layout);
/* 2343 */             GridData grid_data = new GridData(1040);
/* 2344 */             Utils.setLayoutData(this.middle, grid_data);
/*      */             
/* 2346 */             List<String[]> list = BuddyPluginView.this.plugin.getBeta().getFavourites();
/*      */             
/* 2348 */             int num_faves = list.size();
/*      */             
/* 2350 */             if (num_faves == 0)
/*      */             {
/* 2352 */               Label label = new Label(this.middle, 0);
/* 2353 */               label.setText(MessageText.getString("label.none.assigned"));
/* 2354 */               label.setEnabled(false);
/*      */             }
/*      */             else
/*      */             {
/*      */               Composite fave_area;
/*      */               Composite fave_area;
/* 2360 */               if (num_faves > 4)
/*      */               {
/* 2362 */                 fave_area = createScrolledComposite(this.middle);
/*      */               }
/*      */               else
/*      */               {
/* 2366 */                 fave_area = this.middle;
/*      */               }
/*      */               
/* 2369 */               List<Button> buttons = new ArrayList();
/*      */               
/* 2371 */               Collections.sort(list, new Comparator()
/*      */               {
/*      */ 
/*      */ 
/* 2375 */                 Comparator<String> c = new FormattersImpl().getAlphanumericComparator(true);
/*      */                 
/*      */                 public int compare(String[] o1, String[] o2)
/*      */                 {
/* 2379 */                   int result = o1[0].compareTo(o2[0]);
/*      */                   
/* 2381 */                   if (result == 0)
/*      */                   {
/* 2383 */                     result = this.c.compare(o1[1], o2[1]);
/*      */                   }
/*      */                   
/* 2386 */                   return result;
/*      */                 }
/*      */               });
/*      */               
/* 2390 */               for (String[] entry : list)
/*      */               {
/* 2392 */                 final String net = entry[0];
/* 2393 */                 final String key = entry[1];
/*      */                 
/* 2395 */                 Button button = new Button(fave_area, 2);
/*      */                 
/* 2397 */                 String short_name = "(" + MessageText.getString(net == "Public" ? "label.public.short" : "label.anon.short") + ")";
/*      */                 
/* 2399 */                 short_name = short_name + " " + key;
/*      */                 
/* 2401 */                 if (short_name.length() > 30)
/*      */                 {
/* 2403 */                   short_name = short_name.substring(0, 30) + "...";
/*      */                 }
/*      */                 
/* 2406 */                 String long_name = "(" + MessageText.getString(net == "Public" ? "label.public" : "label.anon") + ")";
/*      */                 
/* 2408 */                 long_name = long_name + " " + key;
/*      */                 
/* 2410 */                 button.setText(short_name);
/* 2411 */                 button.setAlignment(16384);
/* 2412 */                 button.setToolTipText(long_name);
/*      */                 
/* 2414 */                 button.setData(net + ":" + key);
/*      */                 
/* 2416 */                 button.addSelectionListener(new SelectionAdapter() {
/*      */                   public void widgetSelected(SelectionEvent e) {
/* 2418 */                     BuddyPluginView.BetaSubViewHolder.this.current_favourite_net = net;
/* 2419 */                     BuddyPluginView.BetaSubViewHolder.this.current_favourite_key = key;
/* 2420 */                     BuddyPluginView.BetaSubViewHolder.this.activate();
/*      */                   }
/* 2422 */                 });
/* 2423 */                 buttons.add(button);
/*      */               }
/*      */               
/* 2426 */               setupButtonGroup(buttons);
/*      */               
/* 2428 */               if (this.current_favourite_key != null)
/*      */               {
/* 2430 */                 selectButtonGroup(buttons, this.current_favourite_net + ":" + this.current_favourite_key);
/*      */               }
/*      */             }
/*      */           }
/*      */           
/* 2435 */           this.middle.getParent().layout(true, true);
/*      */         }
/*      */         
/* 2438 */         this.last_build_chat_mode = mode;
/*      */       }
/*      */       
/* 2441 */       if (activate)
/*      */       {
/* 2443 */         activate();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private Composite createScrolledComposite(final Composite parent)
/*      */     {
/* 2451 */       final ScrolledComposite scrollable = new ScrolledComposite(parent, 512)
/*      */       {
/*      */         private final Point bar_size;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         private int x_adjust;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         private boolean first_time;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         private boolean hacking;
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         public Point computeSize(int wHint, int hHint, boolean changed)
/*      */         {
/* 2477 */           Point point = super.computeSize(wHint, hHint, changed);
/*      */           
/* 2479 */           if (!this.hacking)
/*      */           {
/* 2481 */             final boolean was_visible = getVerticalBar().isVisible();
/*      */             
/* 2483 */             Utils.execSWTThreadLater(0, new Runnable()
/*      */             {
/*      */ 
/*      */ 
/*      */               public void run()
/*      */               {
/*      */ 
/* 2490 */                 if (BuddyPluginView.BetaSubViewHolder.14.this.isDisposed())
/*      */                 {
/* 2492 */                   return;
/*      */                 }
/*      */                 
/* 2495 */                 boolean is_visible = BuddyPluginView.BetaSubViewHolder.14.this.getVerticalBar().isVisible();
/*      */                 
/* 2497 */                 if ((BuddyPluginView.BetaSubViewHolder.14.this.first_time) || (was_visible != is_visible))
/*      */                 {
/* 2499 */                   BuddyPluginView.BetaSubViewHolder.14.this.x_adjust = (is_visible ? 0 : -BuddyPluginView.BetaSubViewHolder.14.this.bar_size.x);
/*      */                   try
/*      */                   {
/* 2502 */                     BuddyPluginView.BetaSubViewHolder.14.this.hacking = true;
/*      */                     
/* 2504 */                     BuddyPluginView.BetaSubViewHolder.14.this.val$parent.getParent().layout(true, true);
/*      */                   }
/*      */                   finally
/*      */                   {
/* 2508 */                     BuddyPluginView.BetaSubViewHolder.14.this.hacking = false;
/*      */                   }
/*      */                 }
/*      */               }
/*      */             });
/*      */           }
/*      */           
/* 2515 */           point.x += this.x_adjust;
/*      */           
/* 2517 */           return point;
/*      */         }
/*      */         
/* 2520 */       };
/* 2521 */       Utils.setLayoutData(scrollable, new GridData(1040));
/*      */       
/* 2523 */       final Composite scrollChild = new Composite(scrollable, 0);
/*      */       
/* 2525 */       GridLayout gLayoutChild = new GridLayout();
/* 2526 */       gLayoutChild.numColumns = 1;
/*      */       
/* 2528 */       gLayoutChild.horizontalSpacing = 1;
/* 2529 */       gLayoutChild.verticalSpacing = 1;
/* 2530 */       gLayoutChild.marginWidth = 0;
/* 2531 */       gLayoutChild.marginHeight = 0;
/* 2532 */       scrollChild.setLayout(gLayoutChild);
/* 2533 */       Utils.setLayoutData(scrollChild, new GridData(1040));
/*      */       
/* 2535 */       scrollable.setContent(scrollChild);
/* 2536 */       scrollable.setExpandVertical(true);
/* 2537 */       scrollable.setExpandHorizontal(true);
/* 2538 */       scrollable.setAlwaysShowScrollBars(false);
/*      */       
/* 2540 */       scrollable.setMinSize(scrollChild.computeSize(-1, -1));
/*      */       
/* 2542 */       scrollable.addControlListener(new ControlAdapter() {
/*      */         public void controlResized(ControlEvent e) {
/* 2544 */           Rectangle r = scrollable.getClientArea();
/* 2545 */           scrollable.setMinSize(scrollChild.computeSize(-1, -1));
/*      */         }
/*      */         
/* 2548 */       });
/* 2549 */       return scrollChild;
/*      */     }
/*      */     
/*      */ 
/*      */     public void activate()
/*      */     {
/* 2555 */       if (this.rebuild_outstanding)
/*      */       {
/* 2557 */         this.rebuild_outstanding = false;
/*      */         
/* 2559 */         if (this.current_download == null)
/*      */         {
/* 2561 */           setChatMode(this.current_ds_tag == null ? this.CHAT_GENERAL : this.CHAT_TAG);
/*      */         }
/*      */         
/* 2564 */         buildChatMode(this.chat_mode, false);
/*      */       }
/*      */       
/* 2567 */       activateNetwork(null);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     public void handleDrop(String drop)
/*      */     {
/* 2574 */       Debug.out("not supported");
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void activateNetwork(String network)
/*      */     {
/* 2581 */       if (network != null)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/* 2586 */         this.last_selected_network = network;
/*      */         
/* 2588 */         activateNetwork(network, false);
/*      */ 
/*      */ 
/*      */ 
/*      */       }
/* 2593 */       else if ((this.chat_mode == this.CHAT_FAVOURITES) && (this.current_favourite_net != null))
/*      */       {
/* 2595 */         activateNetwork(this.current_favourite_net, true);
/*      */       }
/*      */       else
/*      */       {
/* 2599 */         BuddyPluginViewInterface.DownloadAdapter download = this.current_download;
/*      */         
/* 2601 */         if (download == null)
/*      */         {
/*      */ 
/*      */ 
/* 2605 */           activateNetwork(null, true);
/*      */         }
/*      */         else
/*      */         {
/* 2609 */           String[] nets = download.getNetworks();
/*      */           
/* 2611 */           boolean pub = false;
/* 2612 */           boolean anon = false;
/*      */           
/* 2614 */           for (String net : nets)
/*      */           {
/* 2616 */             if (net == "Public")
/*      */             {
/* 2618 */               pub = true;
/*      */             }
/* 2620 */             else if (net == "I2P")
/*      */             {
/* 2622 */               anon = true;
/*      */             }
/*      */           }
/*      */           
/* 2626 */           if ((pub) && (anon)) {
/* 2627 */             activateNetwork("Public", true);
/* 2628 */             activateNetwork("I2P", false);
/* 2629 */           } else if (pub) {
/* 2630 */             activateNetwork("Public", true);
/* 2631 */           } else if (anon) {
/* 2632 */             activateNetwork("I2P", true);
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void activateNetwork(String network, boolean select_tab)
/*      */     {
/*      */       String key;
/*      */       
/*      */       String key;
/*      */       
/* 2646 */       if (this.chat_mode == this.CHAT_DOWNLOAD)
/*      */       {
/* 2648 */         BuddyPluginViewInterface.DownloadAdapter download = this.current_download;
/*      */         String key;
/* 2650 */         if (download == null)
/*      */         {
/* 2652 */           key = null;
/*      */         }
/*      */         else
/*      */         {
/* 2656 */           key = download.getChatKey(); }
/*      */       } else { String key;
/* 2658 */         if (this.chat_mode == this.CHAT_TRACKERS)
/*      */         {
/* 2660 */           String tracker = this.current_tracker;
/*      */           String key;
/* 2662 */           if (tracker == null)
/*      */           {
/* 2664 */             key = null;
/*      */           }
/*      */           else
/*      */           {
/* 2668 */             key = "Tracker: " + tracker; }
/*      */         } else { String key;
/* 2670 */           if (this.chat_mode == this.CHAT_TAG)
/*      */           {
/* 2672 */             Tag tag = this.current_tag;
/*      */             String key;
/* 2674 */             if (tag == null)
/*      */             {
/* 2676 */               key = null;
/*      */             }
/*      */             else
/*      */             {
/* 2680 */               key = TagUIUtils.getChatKey(tag); }
/*      */           } else { String key;
/* 2682 */             if (this.chat_mode == this.CHAT_GENERAL)
/*      */             {
/* 2684 */               key = this.current_general;
/*      */             }
/*      */             else
/*      */             {
/* 2688 */               key = this.current_favourite_key; }
/*      */           }
/*      */         } }
/* 2691 */       activateChat(network, key, select_tab);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     private void activateChat(String _network, final String key, boolean select_tab)
/*      */     {
/* 2700 */       if (_network == null)
/*      */       {
/* 2702 */         if (this.last_selected_network != null)
/*      */         {
/* 2704 */           _network = this.last_selected_network;
/*      */         }
/*      */         else {
/* 2707 */           if (select_tab)
/*      */           {
/* 2709 */             this.tab_folder.setSelection(this.neither_item);
/*      */             
/* 2711 */             this.neither_item.setText(MessageText.getString("GeneralView.section.info"));
/*      */           }
/*      */           
/* 2714 */           return;
/*      */         }
/*      */       }
/*      */       
/* 2718 */       final String network = _network;
/*      */       
/* 2720 */       this.neither_item.setText("");
/*      */       
/* 2722 */       final Composite chat_composite = this.chat_composites[1];
/*      */       
/* 2724 */       if (chat_composite == null)
/*      */       {
/* 2726 */         return;
/*      */       }
/*      */       
/* 2729 */       final String comp_key = network + ":" + key;
/*      */       
/* 2731 */       String existing_comp_key = (String)chat_composite.getData();
/*      */       
/* 2733 */       if ((existing_comp_key == null) || (!existing_comp_key.equals(comp_key)))
/*      */       {
/* 2735 */         for (Control c : chat_composite.getChildren())
/*      */         {
/* 2737 */           c.dispose();
/*      */         }
/*      */         
/* 2740 */         if (key == null)
/*      */         {
/* 2742 */           chat_composite.setData(comp_key);
/*      */           
/* 2744 */           return;
/*      */         }
/*      */         
/* 2747 */         AsyncDispatcher disp = network == "Public" ? BuddyPluginView.public_dispatcher : BuddyPluginView.anon_dispatcher;
/*      */         
/* 2749 */         final AtomicInteger counter = network == "Public" ? BuddyPluginView.public_done : BuddyPluginView.anon_done;
/*      */         
/* 2751 */         disp.dispatch(new AERunnable()
/*      */         {
/*      */ 
/*      */           public void runSupport()
/*      */           {
/*      */ 
/* 2757 */             if (chat_composite.isDisposed())
/*      */             {
/* 2759 */               return;
/*      */             }
/*      */             try
/*      */             {
/* 2763 */               final BuddyPluginBeta.ChatInstance chat = (network == "I2P") && (!BuddyPluginView.this.plugin.getBeta().isI2PAvailable()) ? null : BuddyPluginView.this.plugin.getBeta().getChat(network, key);
/*      */               
/* 2765 */               counter.incrementAndGet();
/*      */               
/* 2767 */               Utils.execSWTThread(new Runnable()
/*      */               {
/*      */ 
/*      */                 public void run()
/*      */                 {
/*      */ 
/* 2773 */                   if (BuddyPluginView.BetaSubViewHolder.16.this.val$chat_composite.isDisposed())
/*      */                   {
/* 2775 */                     return;
/*      */                   }
/*      */                   
/* 2778 */                   for (Control c : BuddyPluginView.BetaSubViewHolder.16.this.val$chat_composite.getChildren())
/*      */                   {
/* 2780 */                     c.dispose();
/*      */                   }
/*      */                   
/* 2783 */                   if (chat == null)
/*      */                   {
/* 2785 */                     GridLayout layout = new GridLayout();
/*      */                     
/*      */ 
/* 2788 */                     layout.numColumns = 3;
/*      */                     
/* 2790 */                     BuddyPluginView.BetaSubViewHolder.16.this.val$chat_composite.setLayout(layout);
/*      */                     
/* 2792 */                     Label label = new Label(BuddyPluginView.BetaSubViewHolder.16.this.val$chat_composite, 0);
/*      */                     
/* 2794 */                     label.setText(MessageText.getString("azbuddy.dchat.not.installed"));
/*      */                     
/* 2796 */                     final Button install_button = new Button(BuddyPluginView.BetaSubViewHolder.16.this.val$chat_composite, 0);
/*      */                     
/* 2798 */                     install_button.setText(MessageText.getString("UpdateWindow.columns.install"));
/*      */                     
/* 2800 */                     install_button.addSelectionListener(new SelectionAdapter()
/*      */                     {
/*      */ 
/*      */                       public void widgetSelected(SelectionEvent e)
/*      */                       {
/* 2805 */                         final boolean[] result = { false };
/*      */                         
/* 2807 */                         I2PHelpers.installI2PHelper(null, result, new Runnable()
/*      */                         {
/*      */ 
/*      */ 
/* 2811 */                           private long start = SystemTime.getMonotonousTime();
/*      */                           
/*      */                           private TimerEventPeriodic timer;
/*      */                           
/*      */                           public void run()
/*      */                           {
/* 2817 */                             if (result[0] != 0)
/*      */                             {
/* 2819 */                               Utils.execSWTThread(new Runnable()
/*      */                               {
/*      */ 
/*      */                                 public void run()
/*      */                                 {
/*      */ 
/* 2825 */                                   BuddyPluginView.BetaSubViewHolder.16.1.1.this.val$install_button.setEnabled(false);
/*      */                                 }
/*      */                                 
/* 2828 */                               });
/* 2829 */                               this.timer = SimpleTimer.addPeriodicEvent("install-waiter", 1000L, new TimerEventPerformer()
/*      */                               {
/*      */ 
/*      */ 
/*      */ 
/*      */                                 public void perform(TimerEvent event)
/*      */                                 {
/*      */ 
/*      */ 
/*      */ 
/* 2839 */                                   if (BuddyPluginView.this.plugin.getBeta().isI2PAvailable())
/*      */                                   {
/* 2841 */                                     BuddyPluginView.BetaSubViewHolder.16.1.1.1.this.timer.cancel();
/*      */                                     
/* 2843 */                                     Utils.execSWTThread(new Runnable()
/*      */                                     {
/*      */ 
/*      */                                       public void run()
/*      */                                       {
/*      */ 
/* 2849 */                                         String existing_comp_key = (String)BuddyPluginView.BetaSubViewHolder.16.this.val$chat_composite.getData();
/*      */                                         
/* 2851 */                                         if ((existing_comp_key == null) || (existing_comp_key.equals(BuddyPluginView.BetaSubViewHolder.16.this.val$comp_key)))
/*      */                                         {
/* 2853 */                                           BuddyPluginView.BetaSubViewHolder.16.this.val$counter.set(0);
/*      */                                           
/* 2855 */                                           BuddyPluginView.BetaSubViewHolder.16.this.val$chat_composite.setData(null);
/*      */                                           
/* 2857 */                                           BuddyPluginView.BetaSubViewHolder.this.activateChat(BuddyPluginView.BetaSubViewHolder.16.this.val$network, BuddyPluginView.BetaSubViewHolder.16.this.val$key, true);
/*      */                                         }
/*      */                                         
/*      */                                       }
/*      */                                     });
/*      */                                   }
/* 2863 */                                   else if (SystemTime.getMonotonousTime() - BuddyPluginView.BetaSubViewHolder.16.1.1.1.this.start > 300000L)
/*      */                                   {
/* 2865 */                                     BuddyPluginView.BetaSubViewHolder.16.1.1.1.this.timer.cancel();
/*      */                                   }
/*      */                                   
/*      */                                 }
/*      */                                 
/*      */ 
/*      */                               });
/*      */                             }
/*      */                             
/*      */                           }
/*      */                         });
/*      */                       }
/* 2877 */                     });
/* 2878 */                     List<Button> buttons = new ArrayList();
/*      */                     
/* 2880 */                     buttons.add(install_button);
/*      */                     
/* 2882 */                     Utils.makeButtonsEqualWidth(buttons);
/*      */                     
/* 2884 */                     BuddyPluginView.BetaSubViewHolder.16.this.val$chat_composite.layout(true, true);
/*      */                   }
/*      */                   else
/*      */                   {
/* 2888 */                     BuddyPluginViewBetaChat view = new BuddyPluginViewBetaChat(BuddyPluginView.this, BuddyPluginView.this.plugin, chat, BuddyPluginView.BetaSubViewHolder.16.this.val$chat_composite);
/*      */                     
/* 2890 */                     ((CTabItem)BuddyPluginView.BetaSubViewHolder.16.this.val$chat_composite.getData("tabitem")).setToolTipText(BuddyPluginView.BetaSubViewHolder.16.this.val$key);
/*      */                     
/* 2892 */                     BuddyPluginView.BetaSubViewHolder.16.this.val$chat_composite.layout(true, true);
/*      */                     
/* 2894 */                     BuddyPluginView.BetaSubViewHolder.16.this.val$chat_composite.setData(BuddyPluginView.BetaSubViewHolder.16.this.val$comp_key);
/*      */                     
/* 2896 */                     BuddyPluginView.BetaSubViewHolder.16.this.val$chat_composite.setData("viewitem", view);
/*      */                     
/* 2898 */                     if (BuddyPluginView.BetaSubViewHolder.this.view_listener != null) {
/*      */                       try
/*      */                       {
/* 2901 */                         BuddyPluginView.BetaSubViewHolder.this.view_listener.chatActivated(chat);
/*      */                       }
/*      */                       catch (Throwable e)
/*      */                       {
/* 2905 */                         Debug.out(e);
/*      */                       }
/*      */                     }
/*      */                   }
/*      */                 }
/*      */               });
/*      */             }
/*      */             catch (Throwable e)
/*      */             {
/* 2914 */               e.printStackTrace();
/*      */             }
/*      */           }
/*      */         });
/*      */         
/*      */ 
/* 2920 */         if (counter.get() == 0)
/*      */         {
/* 2922 */           GridLayout layout = new GridLayout();
/* 2923 */           layout.numColumns = 1;
/* 2924 */           chat_composite.setLayout(layout);
/*      */           
/* 2926 */           Label label = new Label(chat_composite, 0);
/*      */           
/* 2928 */           label.setText(MessageText.getString("v3.MainWindow.view.wait"));
/* 2929 */           GridData grid_data = new GridData(1808);
/* 2930 */           Utils.setLayoutData(label, grid_data);
/*      */         }
/*      */         
/*      */ 
/* 2934 */         chat_composite.layout(true, true);
/*      */       }
/*      */       else
/*      */       {
/* 2938 */         BuddyPluginViewBetaChat existing_chat = (BuddyPluginViewBetaChat)chat_composite.getData("viewitem");
/*      */         
/* 2940 */         if (existing_chat != null)
/*      */         {
/* 2942 */           existing_chat.activate();
/*      */         }
/*      */       }
/*      */       
/* 2946 */       if (select_tab)
/*      */       {
/* 2948 */         this.tab_folder.setSelection(network == "Public" ? this.public_item : this.anon_item);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */     private void setDataSource(Object obj)
/*      */     {
/* 2956 */       Download dl = null;
/* 2957 */       DiskManagerFileInfo dl_file = null;
/* 2958 */       Tag tag = null;
/*      */       
/* 2960 */       if ((obj instanceof Object[]))
/*      */       {
/* 2962 */         Object[] ds = (Object[])obj;
/*      */         
/* 2964 */         if (ds.length > 0)
/*      */         {
/* 2966 */           if ((ds[0] instanceof Download))
/*      */           {
/* 2968 */             dl = (Download)ds[0];
/*      */           }
/* 2970 */           else if ((ds[0] instanceof DiskManagerFileInfo))
/*      */           {
/* 2972 */             dl_file = (DiskManagerFileInfo)ds[0];
/*      */           }
/* 2974 */           else if ((ds[0] instanceof Tag))
/*      */           {
/* 2976 */             tag = (Tag)ds[0];
/*      */           }
/*      */           
/*      */         }
/*      */       }
/* 2981 */       else if ((obj instanceof Download))
/*      */       {
/* 2983 */         dl = (Download)obj;
/*      */       }
/* 2985 */       else if ((obj instanceof DiskManagerFileInfo))
/*      */       {
/* 2987 */         dl_file = (DiskManagerFileInfo)obj;
/*      */       }
/* 2989 */       else if ((obj instanceof Tag))
/*      */       {
/* 2991 */         tag = (Tag)obj;
/*      */       }
/*      */       
/*      */ 
/* 2995 */       if (dl_file != null) {
/*      */         try
/*      */         {
/* 2998 */           dl = dl_file.getDownload();
/*      */         }
/*      */         catch (Throwable e) {}
/*      */       }
/*      */       
/*      */ 
/* 3004 */       synchronized (this)
/*      */       {
/* 3006 */         if ((dl == this.current_download) && (tag == this.current_ds_tag))
/*      */         {
/* 3008 */           return;
/*      */         }
/*      */         
/* 3011 */         this.last_selected_network = null;
/*      */         
/* 3013 */         this.current_download = (dl == null ? null : BuddyPluginView.this.getDownloadAdapter(dl));
/* 3014 */         this.current_ds_tag = tag;
/*      */         
/* 3016 */         if (this.current_download != null) {
/* 3017 */           setChatMode(this.CHAT_DOWNLOAD);
/*      */         }
/*      */         
/* 3020 */         this.rebuild_outstanding = true;
/*      */         
/* 3022 */         if (this.have_focus)
/*      */         {
/* 3024 */           activate();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private void gotFocus()
/*      */     {
/* 3032 */       synchronized (this)
/*      */       {
/* 3034 */         this.have_focus = true;
/*      */         
/* 3036 */         activate();
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */     private void lostFocus()
/*      */     {
/* 3043 */       synchronized (this)
/*      */       {
/* 3045 */         this.have_focus = false;
/*      */       }
/*      */     }
/*      */     
/*      */     public void destroy() {}
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/plugins/net/buddy/swt/BuddyPluginView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */