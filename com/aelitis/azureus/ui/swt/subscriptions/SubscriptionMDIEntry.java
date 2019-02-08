/*     */ package com.aelitis.azureus.ui.swt.subscriptions;
/*     */ 
/*     */ import com.aelitis.azureus.core.subs.Subscription;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionHistory;
/*     */ import com.aelitis.azureus.core.subs.SubscriptionListener;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfoManager;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*     */ import com.aelitis.azureus.ui.swt.mdi.BaseMdiEntry;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.ByteFormatter;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEventListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SubscriptionMDIEntry
/*     */   implements SubscriptionListener, ViewTitleInfo
/*     */ {
/*     */   private static final String ALERT_IMAGE_ID = "image.sidebar.vitality.alert";
/*     */   private static final String AUTH_IMAGE_ID = "image.sidebar.vitality.auth";
/*     */   private final MdiEntry mdiEntry;
/*     */   MdiEntryVitalityImage spinnerImage;
/*     */   private MdiEntryVitalityImage warningImage;
/*     */   private final Subscription subs;
/*     */   private String current_parent;
/*     */   
/*     */   public SubscriptionMDIEntry(Subscription subs, MdiEntry entry)
/*     */   {
/*  53 */     this.subs = subs;
/*  54 */     this.mdiEntry = entry;
/*  55 */     this.current_parent = subs.getParent();
/*  56 */     if ((this.current_parent != null) && (this.current_parent.length() == 0)) {
/*  57 */       this.current_parent = null;
/*     */     }
/*  59 */     setupMdiEntry();
/*     */   }
/*     */   
/*     */   private void setupMdiEntry() {
/*  63 */     if (this.mdiEntry == null) {
/*  64 */       return;
/*     */     }
/*     */     
/*  67 */     this.mdiEntry.setViewTitleInfo(this);
/*     */     
/*  69 */     this.mdiEntry.setImageLeftID("image.sidebar.subscriptions");
/*     */     
/*  71 */     this.warningImage = this.mdiEntry.addVitalityImage("image.sidebar.vitality.alert");
/*     */     
/*  73 */     this.spinnerImage = this.mdiEntry.addVitalityImage("image.sidebar.vitality.dots");
/*     */     
/*  75 */     if (this.spinnerImage != null) {
/*  76 */       this.spinnerImage.setVisible(false);
/*     */     }
/*     */     
/*  79 */     setWarning();
/*     */     
/*  81 */     setupMenus(this.subs, new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*  86 */         SubscriptionMDIEntry.this.refreshView();
/*     */       }
/*     */       
/*  89 */     });
/*  90 */     this.subs.addListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static String setupMenus(Subscription subs, final Runnable refresher)
/*     */   {
/*  98 */     PluginInterface pi = PluginInitializer.getDefaultInterface();
/*  99 */     UIManager uim = pi.getUIManager();
/*     */     
/* 101 */     MenuManager menu_manager = uim.getMenuManager();
/*     */     
/* 103 */     final String key = "sidebar.Subscription_" + ByteFormatter.encodeString(subs.getPublicKey());
/*     */     
/* 105 */     SubscriptionManagerUI.MenuCreator menu_creator = new SubscriptionManagerUI.MenuCreator()
/*     */     {
/*     */ 
/*     */ 
/*     */       public MenuItem createMenu(String resource_id)
/*     */       {
/*     */ 
/* 112 */         return this.val$menu_manager.addMenuItem(key, resource_id);
/*     */       }
/*     */       
/*     */       public void refreshView() {
/* 116 */         if (refresher != null) {
/* 117 */           refresher.run();
/*     */         }
/*     */         
/*     */       }
/* 121 */     };
/* 122 */     SubscriptionManagerUI.createMenus(menu_manager, menu_creator, new Subscription[] { subs });
/*     */     
/* 124 */     return key;
/*     */   }
/*     */   
/*     */   protected String getCurrentParent()
/*     */   {
/* 129 */     return this.current_parent;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean isDisposed()
/*     */   {
/* 135 */     return this.mdiEntry.isDisposed();
/*     */   }
/*     */   
/*     */   public void subscriptionDownloaded(Subscription subs, boolean auto) {}
/*     */   
/*     */   public void subscriptionChanged(Subscription subs, int reason)
/*     */   {
/* 142 */     this.mdiEntry.redraw();
/* 143 */     ViewTitleInfoManager.refreshTitleInfo(this.mdiEntry.getViewTitleInfo());
/*     */   }
/*     */   
/*     */   protected void refreshView() {
/* 147 */     if (!(this.mdiEntry instanceof BaseMdiEntry)) {
/* 148 */       return;
/*     */     }
/* 150 */     UISWTViewEventListener eventListener = ((BaseMdiEntry)this.mdiEntry).getEventListener();
/* 151 */     if ((eventListener instanceof SubscriptionView)) {
/* 152 */       SubscriptionView subsView = (SubscriptionView)eventListener;
/* 153 */       subsView.refreshView();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setWarning()
/*     */   {
/* 162 */     if (this.warningImage == null)
/*     */     {
/* 164 */       return;
/*     */     }
/*     */     
/* 167 */     SubscriptionHistory history = this.subs.getHistory();
/*     */     
/* 169 */     String last_error = history.getLastError();
/*     */     
/* 171 */     boolean auth_fail = history.isAuthFail();
/*     */     
/*     */ 
/*     */ 
/* 175 */     if ((history.getConsecFails() < 3) && (!auth_fail))
/*     */     {
/* 177 */       last_error = null;
/*     */     }
/*     */     
/* 180 */     boolean trouble = last_error != null;
/*     */     
/* 182 */     if (trouble)
/*     */     {
/* 184 */       this.warningImage.setToolTip(last_error);
/*     */       
/* 186 */       this.warningImage.setImageID(auth_fail ? "image.sidebar.vitality.auth" : "image.sidebar.vitality.alert");
/*     */       
/* 188 */       this.warningImage.setVisible(true);
/*     */     }
/*     */     else
/*     */     {
/* 192 */       this.warningImage.setVisible(false);
/*     */       
/* 194 */       this.warningImage.setToolTip("");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getTitleInfoProperty(int propertyID)
/*     */   {
/* 212 */     switch (propertyID)
/*     */     {
/*     */ 
/*     */     case 5: 
/* 216 */       return this.subs.getName();
/*     */     
/*     */ 
/*     */     case 1: 
/* 220 */       long pop = this.subs.getCachedPopularity();
/*     */       
/* 222 */       String res = this.subs.getName();
/*     */       
/* 224 */       if (pop > 1L)
/*     */       {
/* 226 */         res = res + " (" + MessageText.getString("subscriptions.listwindow.popularity").toLowerCase() + "=" + pop + ")";
/*     */       }
/*     */       
/* 229 */       return res;
/*     */     
/*     */ 
/*     */     case 0: 
/* 233 */       SubscriptionMDIEntry mdi = (SubscriptionMDIEntry)this.subs.getUserData(SubscriptionManagerUI.SUB_ENTRYINFO_KEY);
/*     */       
/* 235 */       if (mdi != null)
/*     */       {
/* 237 */         mdi.setWarning();
/*     */       }
/*     */       
/* 240 */       if (this.subs.getHistory().getNumUnread() > 0)
/*     */       {
/* 242 */         return "" + this.subs.getHistory().getNumUnread();
/*     */       }
/*     */       
/* 245 */       return null;
/*     */     }
/*     */     
/*     */     
/* 249 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void removeWithConfirm()
/*     */   {
/* 255 */     SubscriptionManagerUI.removeWithConfirm(this.subs);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/subscriptions/SubscriptionMDIEntry.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */