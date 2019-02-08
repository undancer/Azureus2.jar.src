/*     */ package com.aelitis.azureus.ui.swt.feature;
/*     */ 
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesEntry;
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesLoadedListener;
/*     */ import com.aelitis.azureus.activities.VuzeActivitiesManager;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SBC_BurnFTUX;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SBC_PlusFTUX;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinView;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinViewManager;
/*     */ import com.aelitis.azureus.util.FeatureUtils;
/*     */ import com.aelitis.azureus.util.FeatureUtils.licenceDetails;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager.FeatureManagerListener;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager.Licence;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager.Licence.LicenceInstallationListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FeatureManagerUIListener
/*     */   implements FeatureManager.FeatureManagerListener
/*     */ {
/*  59 */   private static final boolean DEBUG = Constants.IS_CVS_VERSION;
/*     */   
/*     */   private static final String ID_ACTIVITY_EXPIRING = "ExpiringEntry";
/*     */   
/*     */   private static final String ID_ACTIVITY_OFFLINE = "OfflineExpiredEntry";
/*     */   
/*     */   private static final String ID_ACTIVITY_EXPIRED = "ExpiredEntry";
/*     */   
/*     */   private final FeatureManager featman;
/*     */   private String pendingAuthForKey;
/*  69 */   private Map<String, Object[]> licence_map = new HashMap();
/*     */   
/*     */   public FeatureManagerUIListener(FeatureManager featman) {
/*  72 */     if (DEBUG) {
/*  73 */       System.out.println("FEAT:");
/*     */     }
/*  75 */     this.featman = featman;
/*     */   }
/*     */   
/*     */   public void licenceAdded(FeatureManager.Licence licence) {
/*  79 */     updateUI();
/*     */     
/*  81 */     mapLicence(licence);
/*     */     
/*  83 */     if (DEBUG) {
/*  84 */       System.out.println("FEAT: Licence " + licence.getKey() + " Added with state " + licence.getState());
/*     */     }
/*     */     
/*  87 */     if (licence.getState() == 1) {
/*  88 */       this.pendingAuthForKey = licence.getKey();
/*  89 */       FeatureManagerUI.openLicenceValidatingWindow();
/*     */     }
/*     */     
/*  92 */     if (licence.isFullyInstalled()) {
/*  93 */       return;
/*     */     }
/*  95 */     licence.retryInstallation();
/*     */   }
/*     */   
/*     */   public void licenceChanged(FeatureManager.Licence licence)
/*     */   {
/* 100 */     int state = licence.getState();
/*     */     
/* 102 */     boolean stateChanged = true;
/*     */     
/* 104 */     FeatureManager.Licence lastLicence = mapLicence(licence);
/*     */     
/* 106 */     if (lastLicence != null) {
/* 107 */       stateChanged = lastLicence.getState() != licence.getState();
/*     */       
/* 109 */       if ((!stateChanged) && (licence.getState() == 2) && (lastLicence.isFullyInstalled() != licence.isFullyInstalled()))
/*     */       {
/*     */ 
/*     */ 
/* 113 */         stateChanged = true;
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */     }
/* 119 */     else if ((state == 4) || (state == 5) || (state == 6))
/*     */     {
/* 121 */       stateChanged = false;
/*     */     }
/*     */     
/*     */ 
/* 125 */     updateUI();
/* 126 */     if (DEBUG) {
/* 127 */       System.out.println("FEAT: License " + licence.getKey() + " State Changed: " + state + "; changed? " + stateChanged);
/*     */     }
/*     */     
/*     */ 
/* 131 */     if (!stateChanged) {
/* 132 */       return;
/*     */     }
/*     */     
/* 135 */     if (state == 1) {
/* 136 */       this.pendingAuthForKey = licence.getKey();
/* 137 */       FeatureManagerUI.openLicenceValidatingWindow();
/*     */     } else {
/* 139 */       FeatureManagerUI.closeLicenceValidatingWindow();
/* 140 */       if (state == 2) {
/* 141 */         if ((licence.getKey().equals(this.pendingAuthForKey)) && 
/* 142 */           (licence.isFullyInstalled())) {
/* 143 */           this.pendingAuthForKey = null;
/* 144 */           FeatureManagerUI.openLicenceSuccessWindow();
/*     */         }
/*     */       }
/* 147 */       else if (state == 3) {
/* 148 */         FeatureManagerUI.openLicenceFailedWindow(state, licence.getKey());
/* 149 */         if (licence.getKey().equals(this.pendingAuthForKey)) {
/* 150 */           this.pendingAuthForKey = null;
/*     */         }
/* 152 */       } else if (state == 5) {
/* 153 */         FeatureManagerUI.openLicenceRevokedWindow(licence);
/* 154 */       } else if (state == 6) {
/* 155 */         FeatureManagerUI.openLicenceActivationDeniedWindow(licence);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private FeatureManager.Licence mapLicence(FeatureManager.Licence licence)
/*     */   {
/* 166 */     FeatureManager.Licence.LicenceInstallationListener new_listener = null;
/*     */     FeatureManager.Licence existing_licence;
/* 168 */     synchronized (this.licence_map)
/*     */     {
/* 170 */       String key = licence.getKey();
/*     */       
/* 172 */       Object[] entry = (Object[])this.licence_map.get(key);
/*     */       
/* 174 */       if (entry == null)
/*     */       {
/* 176 */         FeatureManager.Licence existing_licence = null;
/*     */         
/* 178 */         new_listener = new FeatureManager.Licence.LicenceInstallationListener()
/*     */         {
/*     */ 
/* 181 */           FeatureManagerInstallWindow install_window = null;
/*     */           
/*     */           public void start(String licence_key) {
/* 184 */             if (FeatureManagerUIListener.DEBUG) {
/* 185 */               System.out.println("FEATINST: START! " + licence_key);
/*     */             }
/*     */             try {
/* 188 */               FeatureManager.Licence licence = FeatureManagerUIListener.this.featman.addLicence(licence_key);
/*     */               
/* 190 */               this.install_window = new FeatureManagerInstallWindow(licence);
/*     */               
/* 192 */               this.install_window.open();
/*     */             }
/*     */             catch (PluginException e) {
/* 195 */               Debug.out(e);
/*     */             }
/*     */           }
/*     */           
/*     */           public void reportProgress(String licenceKey, String install, int percent) {
/* 200 */             if (FeatureManagerUIListener.DEBUG) {
/* 201 */               System.out.println("FEATINST: " + install + ": " + percent);
/*     */             }
/*     */           }
/*     */           
/*     */           public void reportActivity(String licenceKey, String install, String activity)
/*     */           {
/* 207 */             if (FeatureManagerUIListener.DEBUG) {
/* 208 */               System.out.println("FEAT: ACTIVITY: " + install + ": " + activity);
/*     */             }
/*     */           }
/*     */           
/* 212 */           public boolean alreadyFailing = false;
/*     */           
/* 214 */           public void failed(String licenceKey, PluginException error) { if (FeatureManagerUIListener.DEBUG) {
/* 215 */               System.out.println("FEAT: FAIL: " + licenceKey + ": " + error.toString());
/*     */             }
/*     */             
/* 218 */             if (this.install_window != null)
/*     */             {
/* 220 */               this.install_window.close();
/*     */             }
/*     */             
/* 223 */             if (licenceKey.equals(FeatureManagerUIListener.this.pendingAuthForKey))
/*     */             {
/* 225 */               FeatureManagerUIListener.this.pendingAuthForKey = null;
/*     */             }
/*     */             
/* 228 */             if (this.alreadyFailing) {
/* 229 */               return;
/*     */             }
/* 231 */             this.alreadyFailing = true;
/*     */             
/* 233 */             String s = Debug.getNestedExceptionMessage(error);
/*     */             
/* 235 */             MessageBoxShell mb = new MessageBoxShell(33, "License Addition Error for " + licenceKey, s);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 240 */             mb.open(new UserPrompterResultListener() {
/*     */               public void prompterClosed(int result) {
/* 242 */                 FeatureManagerUIListener.1.this.alreadyFailing = false;
/*     */               }
/*     */             });
/*     */           }
/*     */           
/*     */           public void complete(String licenceKey)
/*     */           {
/* 249 */             if (licenceKey.equals(FeatureManagerUIListener.this.pendingAuthForKey))
/*     */             {
/* 251 */               FeatureManagerUIListener.this.pendingAuthForKey = null;
/*     */               
/* 253 */               FeatureManagerUI.openLicenceSuccessWindow();
/*     */             }
/*     */             
/*     */           }
/*     */           
/* 258 */         };
/* 259 */         this.licence_map.put(key, new Object[] { licence, new_listener });
/*     */       }
/*     */       else
/*     */       {
/* 263 */         existing_licence = (FeatureManager.Licence)entry[0];
/*     */         
/* 265 */         entry[0] = licence;
/*     */       }
/*     */     }
/*     */     
/* 269 */     if (new_listener != null)
/*     */     {
/* 271 */       licence.addInstallationListener(new_listener);
/*     */     }
/*     */     
/* 274 */     return existing_licence;
/*     */   }
/*     */   
/*     */   private void updateUI() {
/* 278 */     PluginInterface plugin_interface = PluginInitializer.getDefaultInterface();
/*     */     
/* 280 */     UIManager ui_manager = plugin_interface.getUIManager();
/*     */     
/* 282 */     ui_manager.addUIListener(new UIManagerListener()
/*     */     {
/*     */       public void UIDetached(UIInstance instance) {}
/*     */       
/*     */       public void UIAttached(UIInstance instance)
/*     */       {
/* 288 */         if ((instance instanceof UISWTInstance)) {
/* 289 */           FeatureManagerUIListener.this._updateUI();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void _updateUI() {
/* 296 */     final boolean hasFullLicence = FeatureUtils.hasPlusLicence();
/*     */     try
/*     */     {
/* 299 */       buildNotifications();
/*     */     } catch (Exception e) {
/* 301 */       Debug.out(e);
/*     */     }
/*     */     
/* 304 */     SWTSkin skin = SWTSkinFactory.getInstance();
/* 305 */     if (skin != null) {
/* 306 */       SWTSkinObject soHeader = skin.getSkinObject("plus-header");
/* 307 */       if (soHeader != null) {
/* 308 */         soHeader.setVisible(hasFullLicence);
/*     */       }
/* 310 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 312 */           UIFunctionsSWT uif = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 313 */           uif.getMainShell().setText(hasFullLicence ? UIFunctions.MAIN_WINDOW_NAME_PLUS : UIFunctions.MAIN_WINDOW_NAME);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 318 */     UIFunctions uif = UIFunctionsManager.getUIFunctions();
/* 319 */     MultipleDocumentInterface mdi = uif.getMDI();
/* 320 */     if (mdi != null) {
/* 321 */       MdiEntry entry = mdi.getEntry("Plus");
/* 322 */       if (entry != null) {
/* 323 */         entry.setTitleID(hasFullLicence ? "mdi.entry.plus.full" : "mdi.entry.plus.free");
/*     */         
/* 325 */         SBC_PlusFTUX view = (SBC_PlusFTUX)SkinViewManager.getByClass(SBC_PlusFTUX.class);
/* 326 */         if (view != null) {
/* 327 */           view.updateLicenceInfo();
/*     */         }
/* 329 */         SkinView[] views = SkinViewManager.getMultiByClass(SBC_BurnFTUX.class);
/* 330 */         if (views != null) {
/* 331 */           for (SkinView bview : views) {
/* 332 */             ((SBC_BurnFTUX)bview).updateLicenceInfo();
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void buildNotifications() {
/* 340 */     VuzeActivitiesManager.addListener(new VuzeActivitiesLoadedListener()
/*     */     {
/*     */       public void vuzeActivitiesLoaded() {}
/*     */     });
/*     */   }
/*     */   
/*     */   private static void _buildNotifications()
/*     */   {
/* 348 */     FeatureUtils.licenceDetails details = FeatureUtils.getPlusOrNoAdFeatureDetails();
/*     */     
/* 350 */     if (details == null) {
/* 351 */       return;
/*     */     }
/*     */     
/* 354 */     long displayExpiryTimeStamp = details.getExpiryDisplayTimeStamp();
/* 355 */     long expiryTimeStamp = details.getExpiryTimeStamp();
/*     */     
/* 357 */     if (expiryTimeStamp <= 0L) {
/* 358 */       return;
/*     */     }
/*     */     
/* 361 */     long msDisplayLeft = displayExpiryTimeStamp - SystemTime.getCurrentTime();
/* 362 */     long daysDisplayLeft = Math.ceil(msDisplayLeft / 8.64E7D);
/* 363 */     long msLeft = expiryTimeStamp - SystemTime.getCurrentTime();
/* 364 */     long daysLeft = Math.ceil(msLeft / 8.64E7D);
/*     */     
/* 366 */     if ((daysLeft > 30L) || (daysDisplayLeft > 30L)) {
/* 367 */       VuzeActivitiesEntry entry1 = VuzeActivitiesManager.getEntryByID("ExpiredEntry");
/* 368 */       VuzeActivitiesEntry entry2 = VuzeActivitiesManager.getEntryByID("ExpiringEntry");
/* 369 */       VuzeActivitiesEntry entry3 = VuzeActivitiesManager.getEntryByID("OfflineExpiredEntry");
/* 370 */       if ((entry1 != null) || (entry2 != null) || (entry3 != null)) {
/* 371 */         VuzeActivitiesManager.removeEntries(new VuzeActivitiesEntry[] { entry1, entry2, entry3 }, true);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 378 */       return;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 384 */     String ref = "plus_note_" + (daysDisplayLeft >= 0L ? "expiring_" : "expired_") + Math.abs(daysDisplayLeft);
/*     */     
/* 386 */     String strA = "TARGET=\"Plus\" HREF=\"#" + ref + "\"";
/*     */     String id;
/*     */     String s;
/* 389 */     String id; if ((daysLeft < 0L) && (daysDisplayLeft > 0L))
/*     */     {
/*     */ 
/* 392 */       VuzeActivitiesEntry entry1 = VuzeActivitiesManager.getEntryByID("ExpiredEntry");
/* 393 */       VuzeActivitiesEntry entry2 = VuzeActivitiesManager.getEntryByID("ExpiringEntry");
/* 394 */       if ((entry1 != null) || (entry2 != null)) {
/* 395 */         VuzeActivitiesManager.removeEntries(new VuzeActivitiesEntry[] { entry1, entry2 }, true);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 401 */       String msgID = "plus.notificaiton.OfflineExpiredEntry";
/* 402 */       String s = MessageText.getString(msgID, new String[] { "" + daysDisplayLeft });
/*     */       
/*     */ 
/* 405 */       id = "OfflineExpiredEntry";
/*     */     }
/*     */     else {
/* 408 */       VuzeActivitiesEntry entry3 = VuzeActivitiesManager.getEntryByID("OfflineExpiredEntry");
/* 409 */       if (entry3 != null) {
/* 410 */         VuzeActivitiesManager.removeEntries(new VuzeActivitiesEntry[] { entry3 }, true);
/*     */       }
/*     */       
/*     */       String id;
/*     */       
/* 415 */       if (daysDisplayLeft > 0L) {
/* 416 */         String msgID = "plus.notificaiton.ExpiringEntry" + (daysDisplayLeft == 1L ? ".s" : ".p");
/*     */         
/* 418 */         String s = MessageText.getString(msgID, new String[] { "" + daysDisplayLeft, strA });
/*     */         
/*     */ 
/*     */ 
/* 422 */         id = "ExpiringEntry";
/*     */       } else {
/* 424 */         String msgID = "plus.notificaiton.ExpiredEntry" + (daysDisplayLeft == -1L ? ".s" : ".p");
/*     */         
/* 426 */         s = MessageText.getString(msgID, new String[] { "" + -daysDisplayLeft, strA });
/*     */         
/*     */ 
/*     */ 
/* 430 */         id = "ExpiredEntry";
/*     */       }
/*     */     }
/* 433 */     VuzeActivitiesEntry entry = VuzeActivitiesManager.getEntryByID(id);
/* 434 */     if (entry == null) {
/* 435 */       boolean existed = VuzeActivitiesManager.isEntryIdRemoved(id);
/* 436 */       if (existed) {
/* 437 */         return;
/*     */       }
/*     */       
/* 440 */       entry = new VuzeActivitiesEntry(SystemTime.getCurrentTime(), s, "VUZE_NEWS_ITEM");
/*     */       
/* 442 */       entry.setID(id);
/* 443 */       entry.setIconID("image.sidebar.plus");
/*     */       
/* 445 */       if ((daysLeft < 0L) && (daysDisplayLeft < 0L)) {
/* 446 */         UIFunctionsManager.getUIFunctions().getMDI().showEntryByID("Plus");
/*     */       }
/*     */     }
/*     */     else {
/* 450 */       entry.setText(s);
/* 451 */       entry.setTimestamp(SystemTime.getCurrentTime());
/*     */     }
/* 453 */     VuzeActivitiesManager.addEntries(new VuzeActivitiesEntry[] { entry });
/*     */   }
/*     */   
/*     */ 
/*     */   public void licenceRemoved(FeatureManager.Licence licence)
/*     */   {
/*     */     Object[] entry;
/*     */     
/* 461 */     synchronized (this.licence_map)
/*     */     {
/* 463 */       entry = (Object[])this.licence_map.remove(licence.getKey());
/*     */     }
/*     */     
/* 466 */     if (entry != null)
/*     */     {
/* 468 */       licence.removeInstallationListener((FeatureManager.Licence.LicenceInstallationListener)entry[1]);
/*     */     }
/*     */     
/* 471 */     updateUI();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/feature/FeatureManagerUIListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */