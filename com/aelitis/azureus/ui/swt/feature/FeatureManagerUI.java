/*     */ package com.aelitis.azureus.ui.swt.feature;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryCreationListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText_UrlClickedListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectTextbox;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SBC_PlusFTUX;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.VuzeMessageBox;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.VuzeMessageBoxListener;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import com.aelitis.azureus.util.FeatureUtils;
/*     */ import com.aelitis.azureus.util.FeatureUtils.licenceDetails;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationChecker;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuManager;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager.Licence;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter.URLInfo;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ 
/*     */ public class FeatureManagerUI
/*     */ {
/*  60 */   private static final Integer BUTTON_UPGRADE = Integer.valueOf(4096);
/*     */   
/*  62 */   public static boolean enabled = (!Constants.isUnix) || (System.getProperty("fm.ui", "0").equals("1"));
/*     */   
/*     */   private static FeatureManager featman;
/*     */   
/*     */   private static VuzeMessageBox validatingBox;
/*     */   
/*     */   private static VuzeMessageBox entryWindow;
/*     */   
/*     */   private static FeatureManagerUIListener fml;
/*     */   
/*     */ 
/*     */   public static void registerWithFeatureManager()
/*     */   {
/*  75 */     if (!enabled) {
/*  76 */       return;
/*     */     }
/*  78 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */     {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*  81 */         PluginInterface pi = core.getPluginManager().getDefaultPluginInterface();
/*  82 */         FeatureManagerUI.access$002(pi.getUtilities().getFeatureManager());
/*     */         
/*  84 */         FeatureManagerUI.access$102(new FeatureManagerUIListener(FeatureManagerUI.featman));
/*  85 */         FeatureManagerUI.featman.addListener(FeatureManagerUI.fml);
/*  86 */         FeatureManager.Licence[] licences = FeatureManagerUI.featman.getLicences();
/*  87 */         for (FeatureManager.Licence licence : licences) {
/*  88 */           FeatureManagerUI.fml.licenceAdded(licence);
/*     */         }
/*     */         
/*     */ 
/*  92 */         UIManager ui_manager = pi.getUIManager();
/*     */         
/*  94 */         ui_manager.addUIListener(new UIManagerListener()
/*     */         {
/*     */           public void UIDetached(UIInstance instance) {}
/*     */           
/*     */           public void UIAttached(UIInstance instance) {
/*  99 */             if (!(instance instanceof UISWTInstance)) {
/* 100 */               return;
/*     */             }
/*     */             
/* 103 */             if (!Utils.isAZ2UI()) {
/* 104 */               FeatureManagerUI.access$200();
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private static void addFreeBurnUI() {
/* 113 */     MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/*     */     
/* 115 */     mdi.registerEntry("Plus", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id) {
/* 118 */         String title = FeatureUtils.hasPlusLicence() ? "{mdi.entry.plus.full}" : "{mdi.entry.plus.free}";
/*     */         
/* 120 */         String placeBelow = this.val$mdi.getEntry("Welcome") == null ? "" : "Welcome";
/*     */         
/*     */ 
/* 123 */         MdiEntry entry = this.val$mdi.createEntryFromSkinRef("header.vuze", "Plus", "main.area.plus", title, null, null, true, placeBelow);
/*     */         
/*     */ 
/*     */ 
/* 127 */         entry.setImageLeftID("image.sidebar.plus");
/* 128 */         return entry;
/*     */       }
/*     */       
/* 131 */     });
/* 132 */     MdiEntry existingEntry = mdi.getEntry("header.dvd");
/* 133 */     if (existingEntry != null)
/*     */     {
/* 135 */       return;
/*     */     }
/* 137 */     mdi.registerEntry("BurnInfo", new MdiEntryCreationListener()
/*     */     {
/*     */       public MdiEntry createMDiEntry(String id)
/*     */       {
/* 141 */         MdiEntry entryAbout = this.val$mdi.createEntryFromSkinRef("header.dvd", "BurnInfo", "main.burn.ftux", "{mdi.entry.about.dvdburn}", null, null, false, null);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 146 */         entryAbout.setImageLeftID("image.sidebar.dvdburn");
/* 147 */         entryAbout.setExpanded(true);
/*     */         
/* 149 */         entryAbout.addListener(new com.aelitis.azureus.ui.mdi.MdiEntryDropListener() {
/*     */           public boolean mdiEntryDrop(MdiEntry entry, Object droppedObject) {
/* 151 */             FeatureManagerUI.openTrialAskWindow();
/* 152 */             return true;
/*     */           }
/*     */           
/* 155 */         });
/* 156 */         MenuManager menuManager = org.gudy.azureus2.pluginsimpl.local.PluginInitializer.getDefaultInterface().getUIManager().getMenuManager();
/* 157 */         MenuItem menuHide = menuManager.addMenuItem("Sidebar.BurnInfo", "popup.error.hide");
/*     */         
/*     */ 
/* 160 */         menuHide.addListener(new MenuItemListener() {
/*     */           public void selected(MenuItem menu, Object target) {
/* 162 */             FeatureManagerUI.3.this.val$mdi.closeEntry("BurnInfo");
/*     */           }
/*     */           
/* 165 */         });
/* 166 */         return entryAbout;
/*     */       }
/*     */       
/* 169 */     });
/* 170 */     mdi.addListener(new com.aelitis.azureus.ui.mdi.MdiEntryLoadedListener() {
/*     */       public void mdiEntryLoaded(MdiEntry entry) {
/* 172 */         if (!entry.getId().equals("header.dvd")) {
/* 173 */           return;
/*     */         }
/* 175 */         MdiEntryVitalityImage addSub = entry.addVitalityImage("image.sidebar.subs.add");
/* 176 */         addSub.addListener(new com.aelitis.azureus.ui.mdi.MdiEntryVitalityImageListener()
/*     */         {
/*     */           public void mdiEntryVitalityImage_clicked(int x, int y) {}
/*     */         });
/*     */       }
/*     */     });
/*     */     
/*     */ 
/* 184 */     if ((ConfigurationChecker.isNewVersion()) && (!ConfigurationChecker.isNewInstall()) && (!FeatureUtils.hasPlusLicence()))
/*     */     {
/* 186 */       SBC_PlusFTUX.setSourceRef("startup");
/* 187 */       mdi.showEntryByID("Plus");
/*     */     }
/*     */   }
/*     */   
/*     */   public static void openTrialAskWindow() {
/* 192 */     VuzeMessageBox box = new VuzeMessageBox(MessageText.getString("dlg.try.trial.title"), MessageText.getString("dlg.try.trial.text"), new String[] { MessageText.getString("Button.turnon"), MessageText.getString("Button.cancel") }, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 198 */     box.setButtonVals(new Integer[] { Integer.valueOf(32), Integer.valueOf(256) });
/*     */     
/*     */ 
/*     */ 
/* 202 */     box.addResourceBundle(FeatureManagerUI.class, "com/aelitis/azureus/ui/skin/", "skin3_dlg_register");
/*     */     
/* 204 */     box.setIconResource("image.burn.dlg.header");
/*     */     
/* 206 */     box.setListener(new VuzeMessageBoxListener() {
/*     */       public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/* 208 */         SWTSkin skin = soExtra.getSkin();
/* 209 */         String id = "dlg.register.trialask";
/* 210 */         SWTSkinObject so = skin.createSkinObject(id, id, soExtra);
/*     */         
/* 212 */         SWTSkinObjectText soLink = (SWTSkinObjectText)skin.getSkinObject("link", so);
/*     */         
/* 214 */         if (soLink != null) {
/* 215 */           soLink.addUrlClickedListener(new SWTSkinObjectText_UrlClickedListener() {
/*     */             public boolean urlClicked(GCStringPrinter.URLInfo urlInfo) {
/* 217 */               String url = ConstantsVuze.getDefaultContentNetwork().getExternalSiteRelativeURL("plus_tos.start", true);
/*     */               
/* 219 */               Utils.launch(url);
/* 220 */               return true;
/*     */             }
/*     */             
/*     */           });
/*     */         }
/*     */       }
/* 226 */     });
/* 227 */     box.open(new UserPrompterResultListener() {
/*     */       public void prompterClosed(int result) {
/* 229 */         if (result == 32) {
/* 230 */           SimpleTimer.addEvent("createTrial", SystemTime.getCurrentTime(), new TimerEventPerformer()
/*     */           {
/*     */             public void perform(TimerEvent event) {}
/*     */           });
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static void createTrial()
/*     */   {
/*     */     try
/*     */     {
/* 243 */       trial = featman.createLicences(new String[] { "dvdburn_trial" });
/*     */     }
/*     */     catch (Throwable e) {
/*     */       FeatureManager.Licence[] trial;
/* 247 */       String s = "Creating Trial: " + Debug.getNestedExceptionMessage(e);
/* 248 */       new MessageBoxShell("Trial Error", s).open(null);
/* 249 */       Logger.log(new LogAlert(true, s, e));
/*     */     }
/*     */   }
/*     */   
/*     */   public static void openLicenceEntryWindow(boolean trytwo, final String prefillWith)
/*     */   {
/* 255 */     synchronized (FeatureManagerUI.class) {
/* 256 */       if (!enabled) {
/* 257 */         return;
/*     */       }
/*     */       
/* 260 */       if (entryWindow != null) {
/* 261 */         return;
/*     */       }
/*     */       try
/*     */       {
/* 265 */         String tryNo = trytwo ? "2" : "1";
/* 266 */         final SWTSkinObjectTextbox[] key = new SWTSkinObjectTextbox[1];
/* 267 */         entryWindow = new VuzeMessageBox(MessageText.getString("dlg.auth.title"), MessageText.getString("dlg.auth.enter.line.try." + tryNo), new String[] { MessageText.getString("Button.agree"), MessageText.getString("Button.cancel") }, 0);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 274 */         entryWindow.setButtonVals(new Integer[] { Integer.valueOf(32), Integer.valueOf(256) });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 279 */         entryWindow.setSubTitle(MessageText.getString("dlg.auth.enter.subtitle.try." + tryNo));
/*     */         
/* 281 */         entryWindow.addResourceBundle(FeatureManagerUI.class, "com/aelitis/azureus/ui/skin/", "skin3_dlg_register");
/*     */         
/* 283 */         entryWindow.setIconResource("image.vp");
/* 284 */         if (trytwo) {
/* 285 */           entryWindow.setTextIconResource("image.warn.big");
/*     */         }
/*     */         
/* 288 */         entryWindow.setListener(new VuzeMessageBoxListener() {
/*     */           public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/* 290 */             SWTSkin skin = soExtra.getSkin();
/* 291 */             skin.createSkinObject("dlg.register", "dlg.register", soExtra);
/*     */             
/* 293 */             SWTSkinObjectText link = (SWTSkinObjectText)skin.getSkinObject("register-link", soExtra);
/*     */             
/* 295 */             link.setText(MessageText.getString(this.val$trytwo ? "dlg.auth.enter.link.try.2" : "dlg.auth.enter.link.try.1"));
/* 296 */             link.addUrlClickedListener(new SWTSkinObjectText_UrlClickedListener() {
/*     */               public boolean urlClicked(GCStringPrinter.URLInfo urlInfo) {
/* 298 */                 if (FeatureManagerUI.7.this.val$trytwo) {
/* 299 */                   String url = ConstantsVuze.getDefaultContentNetwork().getExternalSiteRelativeURL("upgrade.start", true);
/*     */                   
/* 301 */                   Utils.launch(url);
/*     */                 } else {
/* 303 */                   SBC_PlusFTUX.setSourceRef("dlg-activation");
/*     */                   
/* 305 */                   MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 306 */                   mdi.showEntryByID("Plus");
/* 307 */                   FeatureManagerUI.entryWindow.close(-2);
/*     */                 }
/* 309 */                 return true;
/*     */               }
/*     */               
/* 312 */             });
/* 313 */             SWTSkinObjectText linkTOS = (SWTSkinObjectText)skin.getSkinObject("tos-link", soExtra);
/*     */             
/* 315 */             if (linkTOS != null) {
/* 316 */               linkTOS.addUrlClickedListener(new SWTSkinObjectText_UrlClickedListener() {
/*     */                 public boolean urlClicked(GCStringPrinter.URLInfo urlInfo) {
/* 318 */                   String url = ConstantsVuze.getDefaultContentNetwork().getExternalSiteRelativeURL("plus_tos.start", true);
/*     */                   
/* 320 */                   Utils.launch(url);
/* 321 */                   return true;
/*     */                 }
/*     */               });
/*     */             }
/*     */             
/* 326 */             key[0] = ((SWTSkinObjectTextbox)skin.getSkinObject("key", soExtra));
/* 327 */             if (key[0] != null) {
/* 328 */               if (prefillWith != null) {
/* 329 */                 key[0].setText(prefillWith);
/* 330 */               } else if (!this.val$trytwo) {
/* 331 */                 FeatureUtils.licenceDetails details = FeatureUtils.getPlusOrNoAdFeatureDetails();
/* 332 */                 if ((details != null) && (details.getLicence().getState() != 3)) {
/* 333 */                   key[0].setText(details.getLicence().getKey());
/* 334 */                   if ((key[0].getControl() instanceof Text)) {
/* 335 */                     ((Text)key[0].getControl()).selectAll();
/*     */                   }
/* 337 */                   final SWTSkinObjectText soExpirey = (SWTSkinObjectText)skin.getSkinObject("register-expirey");
/* 338 */                   if (soExpirey != null) {
/* 339 */                     key[0].getControl().addListener(24, new Listener() {
/*     */                       public void handleEvent(Event event) {
/* 341 */                         soExpirey.setText("");
/*     */                       }
/*     */                       
/* 344 */                     });
/* 345 */                     int state = details.getLicence().getState();
/* 346 */                     if (state == 4) {
/* 347 */                       soExpirey.setText(MessageText.getString("dlg.auth.enter.cancelled"));
/* 348 */                     } else if (state == 5) {
/* 349 */                       soExpirey.setText(MessageText.getString("dlg.auth.enter.revoked"));
/* 350 */                     } else if (state == 6) {
/* 351 */                       soExpirey.setText(MessageText.getString("dlg.auth.enter.denied"));
/*     */                     } else {
/* 353 */                       long now = SystemTime.getCurrentTime();
/* 354 */                       if ((details.getExpiryTimeStamp() < now) && (details.getExpiryDisplayTimeStamp() > now)) {
/* 355 */                         soExpirey.setText(MessageText.getString("plus.notificaiton.OfflineExpiredEntry"));
/*     */                       } else {
/* 357 */                         soExpirey.setText(MessageText.getString("dlg.auth.enter.expiry", new String[] { DisplayFormatters.formatCustomDateOnly(details.getExpiryDisplayTimeStamp()) }));
/*     */                       }
/*     */                       
/*     */                     }
/*     */                     
/*     */                   }
/*     */                   
/*     */                 }
/*     */                 
/*     */               }
/*     */             }
/*     */           }
/* 369 */         });
/* 370 */         entryWindow.open(new UserPrompterResultListener() {
/*     */           public void prompterClosed(int result) {
/* 372 */             synchronized (FeatureManagerUI.class) {
/* 373 */               FeatureManagerUI.access$302(null);
/*     */             }
/* 375 */             if (result == 32) {
/*     */               try {
/* 377 */                 FeatureManager.Licence licence = FeatureManagerUI.featman.addLicence(this.val$key[0].getText());
/* 378 */                 int initialState = licence.getState();
/* 379 */                 if (initialState == 2) {
/* 380 */                   if (!licence.isFullyInstalled()) {
/* 381 */                     FeatureManagerUI.fml.licenceAdded(licence);
/*     */                   } else {
/* 383 */                     FeatureManagerUI.openLicenceSuccessWindow();
/*     */                   }
/* 385 */                 } else if (initialState == 1) {
/* 386 */                   FeatureManagerUI.fml.licenceAdded(licence);
/* 387 */                 } else if (initialState == 3) {
/* 388 */                   FeatureManagerUI.openLicenceFailedWindow(initialState, this.val$key[0].getText());
/* 389 */                 } else if (initialState == 6) {
/* 390 */                   FeatureManagerUI.openLicenceActivationDeniedWindow(licence);
/* 391 */                 } else if (initialState == 4) {
/* 392 */                   FeatureManagerUI.openLicenceCancelledWindow(licence);
/* 393 */                 } else if (initialState == 5) {
/* 394 */                   FeatureManagerUI.openLicenceRevokedWindow(licence);
/*     */                 }
/*     */               }
/*     */               catch (Throwable e) {
/* 398 */                 String s = Debug.getNestedExceptionMessage(e);
/*     */                 
/* 400 */                 MessageBoxShell mb = new MessageBoxShell(33, "Licence Addition Error", s);
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 405 */                 mb.open();
/*     */                 
/* 407 */                 Logger.log(new LogAlert(true, 3, "Adding Licence", e));
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */       catch (Exception e) {
/* 414 */         entryWindow = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void openLicenceSuccessWindow() {
/* 420 */     if (!enabled) {
/* 421 */       return;
/*     */     }
/*     */     
/* 424 */     if (FeatureUtils.hasPlusLicence()) {
/* 425 */       openFullLicenceSuccessWindow();
/* 426 */     } else if (FeatureUtils.hasTrialLicence()) {
/* 427 */       openTrialLicenceSuccessWindow();
/*     */     } else {
/* 429 */       openNoAdsLicenceSuccessWindow();
/*     */     }
/*     */   }
/*     */   
/*     */   private static void openNoAdsLicenceSuccessWindow() {
/* 434 */     VuzeMessageBox box = new VuzeMessageBox(MessageText.getString("dlg.auth.title"), MessageText.getString("dlg.auth.noads.success.line1"), new String[] { MessageText.getString("Button.ok") }, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 440 */     box.setSubTitle(MessageText.getString("dlg.auth.noads.success.subtitle"));
/* 441 */     box.open(new UserPrompterResultListener()
/*     */     {
/*     */       public void prompterClosed(int result) {}
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private static void openTrialLicenceSuccessWindow()
/*     */   {
/* 450 */     VuzeMessageBox box = new VuzeMessageBox(MessageText.getString("dlg.auth.trial.success.subtitle"), MessageText.getString("dlg.auth.trial.success.line1"), new String[] { MessageText.getString("Button.goLibrary") }, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 455 */     box.addResourceBundle(FeatureManagerUI.class, "com/aelitis/azureus/ui/skin/", "skin3_dlg_register");
/*     */     
/* 457 */     box.setIconResource("image.burn.dlg.header");
/*     */     
/* 459 */     box.setListener(new VuzeMessageBoxListener() {
/*     */       public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/* 461 */         SWTSkin skin = soExtra.getSkin();
/* 462 */         skin.createSkinObject("dlg.register.trial.success", "dlg.register.trial.success", soExtra);
/*     */       }
/*     */       
/*     */ 
/* 466 */     });
/* 467 */     box.open(new UserPrompterResultListener() {
/*     */       public void prompterClosed(int result) {
/* 469 */         if (result == 0) {
/* 470 */           SBC_PlusFTUX.setSourceRef("dlg-trial-installed");
/*     */           
/* 472 */           MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 473 */           mdi.showEntryByID("Library");
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private static void openFullLicenceSuccessWindow() {
/* 480 */     VuzeMessageBox box = new VuzeMessageBox(MessageText.getString("dlg.auth.title"), MessageText.getString("dlg.auth.success.line1"), new String[] { MessageText.getString("Button.getstarted") }, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 485 */     box.setSubTitle(MessageText.getString("dlg.auth.success.subtitle"));
/* 486 */     box.addResourceBundle(FeatureManagerUI.class, "com/aelitis/azureus/ui/skin/", "skin3_dlg_register");
/*     */     
/* 488 */     box.setIconResource("image.vp");
/*     */     
/* 490 */     box.setListener(new VuzeMessageBoxListener() {
/*     */       public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/* 492 */         SWTSkin skin = soExtra.getSkin();
/* 493 */         skin.createSkinObject("dlg.register.success", "dlg.register.success", soExtra);
/*     */       }
/*     */       
/*     */ 
/* 497 */     });
/* 498 */     box.open(new UserPrompterResultListener() {
/*     */       public void prompterClosed(int result) {
/* 500 */         if (result == 0) {
/* 501 */           SBC_PlusFTUX.setSourceRef("dlg-plus-installed");
/*     */           
/* 503 */           MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 504 */           mdi.showEntryByID("Plus");
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static void openLicenceRevokedWindow(FeatureManager.Licence licence) {
/* 511 */     VuzeMessageBox box = new VuzeMessageBox(MessageText.getString("dlg.auth.revoked"), MessageText.getString("dlg.auth.revoked.line1"), new String[] { MessageText.getString("Button.close") }, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 516 */     box.addResourceBundle(FeatureManagerUI.class, "com/aelitis/azureus/ui/skin/", "skin3_dlg_register");
/*     */     
/* 518 */     box.setIconResource("image.vp");
/* 519 */     box.setTextIconResource("image.warn.big");
/*     */     
/* 521 */     box.setListener(new VuzeMessageBoxListener() {
/*     */       public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/* 523 */         SWTSkin skin = soExtra.getSkin();
/* 524 */         SWTSkinObject so = skin.createSkinObject("dlg.register.revoked", "dlg.register.revoked", soExtra);
/*     */         
/*     */ 
/* 527 */         SWTSkinObjectText soLink = (SWTSkinObjectText)skin.getSkinObject("link", so);
/*     */         
/* 529 */         if (soLink != null) {
/* 530 */           soLink.addUrlClickedListener(new SWTSkinObjectText_UrlClickedListener() {
/*     */             public boolean urlClicked(GCStringPrinter.URLInfo urlInfo) {
/* 532 */               String url = ConstantsVuze.getDefaultContentNetwork().getExternalSiteRelativeURL("licence_revoked.start?key=" + UrlUtils.encode(FeatureManagerUI.14.this.val$licence.getKey()), true);
/*     */               
/*     */ 
/* 535 */               Utils.launch(url);
/* 536 */               return true;
/*     */             }
/*     */             
/*     */           });
/*     */         }
/*     */       }
/* 542 */     });
/* 543 */     box.open(null);
/*     */   }
/*     */   
/*     */   public static void openLicenceActivationDeniedWindow(FeatureManager.Licence licence) {
/* 547 */     VuzeMessageBox box = new VuzeMessageBox(MessageText.getString("dlg.auth.denied"), MessageText.getString("dlg.auth.denied.line1"), new String[] { MessageText.getString("Button.close") }, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 552 */     box.addResourceBundle(FeatureManagerUI.class, "com/aelitis/azureus/ui/skin/", "skin3_dlg_register");
/*     */     
/* 554 */     box.setIconResource("image.vp");
/* 555 */     box.setTextIconResource("image.warn.big");
/*     */     
/* 557 */     box.setListener(new VuzeMessageBoxListener() {
/*     */       public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/* 559 */         SWTSkin skin = soExtra.getSkin();
/* 560 */         SWTSkinObject so = skin.createSkinObject("dlg.register.denied", "dlg.register.denied", soExtra);
/*     */         
/*     */ 
/* 563 */         SWTSkinObjectText soLink = (SWTSkinObjectText)skin.getSkinObject("link", so);
/*     */         
/* 565 */         if (soLink != null) {
/* 566 */           soLink.addUrlClickedListener(new SWTSkinObjectText_UrlClickedListener() {
/*     */             public boolean urlClicked(GCStringPrinter.URLInfo urlInfo) {
/* 568 */               String url = ConstantsVuze.getDefaultContentNetwork().getExternalSiteRelativeURL("licence_denied.start?key=" + UrlUtils.encode(FeatureManagerUI.15.this.val$licence.getKey()), true);
/*     */               
/*     */ 
/* 571 */               Utils.launch(url);
/* 572 */               return true;
/*     */             }
/*     */             
/*     */           });
/*     */         }
/*     */       }
/* 578 */     });
/* 579 */     box.open(null);
/*     */   }
/*     */   
/*     */   public static void openLicenceCancelledWindow(FeatureManager.Licence licence) {
/* 583 */     VuzeMessageBox box = new VuzeMessageBox(MessageText.getString("dlg.auth.cancelled"), MessageText.getString("dlg.auth.cancelled.line1"), new String[] { MessageText.getString("Button.close") }, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 588 */     box.addResourceBundle(FeatureManagerUI.class, "com/aelitis/azureus/ui/skin/", "skin3_dlg_register");
/*     */     
/* 590 */     box.setIconResource("image.vp");
/* 591 */     box.setTextIconResource("image.warn.big");
/*     */     
/* 593 */     box.setListener(new VuzeMessageBoxListener() {
/*     */       public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/* 595 */         SWTSkin skin = soExtra.getSkin();
/* 596 */         SWTSkinObject so = skin.createSkinObject("dlg.register.cancelled", "dlg.register.cancelled", soExtra);
/*     */       }
/*     */       
/*     */ 
/* 600 */     });
/* 601 */     box.open(null);
/*     */   }
/*     */   
/*     */   protected static void openLicenceFailedWindow(int licenceState, String code)
/*     */   {
/* 606 */     openLicenceEntryWindow(true, code);
/*     */   }
/*     */   
/*     */   public static void openLicenceValidatingWindow() {
/* 610 */     synchronized (FeatureManagerUI.class) {
/* 611 */       if ((!enabled) || (validatingBox != null)) {
/* 612 */         return;
/*     */       }
/*     */       
/* 615 */       validatingBox = new VuzeMessageBox(MessageText.getString("dlg.auth.validating.subtitle"), null, null, 0);
/*     */       
/* 617 */       validatingBox.addResourceBundle(FeatureManagerUI.class, "com/aelitis/azureus/ui/skin/", "skin3_dlg_register");
/*     */       
/* 619 */       validatingBox.setIconResource("image.vp");
/*     */       
/* 621 */       validatingBox.setListener(new VuzeMessageBoxListener() {
/*     */         public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/* 623 */           SWTSkin skin = soExtra.getSkin();
/* 624 */           skin.createSkinObject("dlg.register.validating", "dlg.register.validating", soExtra);
/*     */         }
/*     */         
/*     */ 
/* 628 */       });
/* 629 */       validatingBox.open(new UserPrompterResultListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void prompterClosed(int result)
/*     */         {
/*     */ 
/* 636 */           synchronized (FeatureManagerUI.class)
/*     */           {
/* 638 */             FeatureManagerUI.access$402(null);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public static void closeLicenceValidatingWindow() {
/* 646 */     synchronized (FeatureManagerUI.class) {
/* 647 */       if (validatingBox != null) {
/* 648 */         validatingBox.close(0);
/* 649 */         validatingBox = null;
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void openStreamPlusWindow(String referal)
/*     */   {
/* 657 */     FeatureUtils.licenceDetails details = FeatureUtils.getPlusFeatureDetails();
/* 658 */     long plusExpiryTimeStamp = details == null ? 0L : details.getExpiryTimeStamp();
/* 659 */     String buttonID; String buttonID; String msgidPrefix; if ((plusExpiryTimeStamp <= 0L) || (plusExpiryTimeStamp >= SystemTime.getCurrentTime()))
/*     */     {
/* 661 */       String msgidPrefix = "dlg.stream.plus.";
/* 662 */       buttonID = "Button.upgrade";
/*     */     } else {
/* 664 */       buttonID = "Button.renew";
/* 665 */       msgidPrefix = "dlg.stream.plus.renew.";
/* 666 */       if (!MessageText.keyExistsForDefaultLocale(msgidPrefix + "text")) {
/* 667 */         msgidPrefix = "dlg.stream.plus.";
/*     */       }
/*     */     }
/* 670 */     String f_msgidPrefix = msgidPrefix;
/* 671 */     VuzeMessageBox box = new VuzeMessageBox(MessageText.getString(msgidPrefix + "title"), MessageText.getString(msgidPrefix + "text"), new String[] { MessageText.getString(buttonID), MessageText.getString("Button.cancel") }, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 677 */     box.setButtonVals(new Integer[] { BUTTON_UPGRADE, Integer.valueOf(256) });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 682 */     box.setSubTitle(MessageText.getString(msgidPrefix + "subtitle"));
/* 683 */     box.addResourceBundle(FeatureManagerUI.class, "com/aelitis/azureus/ui/skin/", "skin3_dlg_streamplus");
/*     */     
/* 685 */     box.setIconResource("image.header.streamplus");
/*     */     
/* 687 */     box.setListener(new VuzeMessageBoxListener() {
/*     */       public void shellReady(Shell shell, SWTSkinObjectContainer soExtra) {
/* 689 */         SWTSkin skin = soExtra.getSkin();
/* 690 */         skin.createSkinObject("dlg.stream.plus", "dlg.stream.plus", soExtra);
/* 691 */         SWTSkinObject soSubText = skin.getSkinObject("trial-info", soExtra);
/* 692 */         if ((soSubText instanceof SWTSkinObjectText)) {
/* 693 */           ((SWTSkinObjectText)soSubText).setTextID(this.val$f_msgidPrefix + "subtext");
/*     */         }
/*     */         
/*     */       }
/* 697 */     });
/* 698 */     box.open(new UserPrompterResultListener() {
/*     */       public void prompterClosed(int result) {
/* 700 */         if (result == FeatureManagerUI.BUTTON_UPGRADE.intValue()) {
/* 701 */           SBC_PlusFTUX.setSourceRef("dlg-stream" + (this.val$referal == null ? "" : new StringBuilder().append("-").append(this.val$referal).toString()));
/*     */           
/* 703 */           MultipleDocumentInterface mdi = UIFunctionsManager.getUIFunctions().getMDI();
/* 704 */           mdi.showEntryByID("Plus");
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String appendFeatureManagerURLParams(String url)
/*     */   {
/* 714 */     long remainingUses = FeatureUtils.getRemaining();
/* 715 */     FeatureUtils.licenceDetails details = FeatureUtils.getPlusFeatureDetails();
/*     */     
/*     */     String plusRenewalCode;
/*     */     long plusExpiryTimeStamp;
/*     */     String plusRenewalCode;
/* 720 */     if (details == null) {
/* 721 */       long plusExpiryTimeStamp = 0L;
/* 722 */       plusRenewalCode = null;
/*     */     } else {
/* 724 */       plusExpiryTimeStamp = details.getExpiryDisplayTimeStamp();
/* 725 */       plusRenewalCode = details.getRenewalCode();
/*     */     }
/*     */     
/*     */ 
/* 729 */     String newURL = url + (url.contains("?") ? "&" : "?");
/* 730 */     newURL = newURL + "mode=" + FeatureUtils.getPlusMode();
/* 731 */     if (plusExpiryTimeStamp != 0L) {
/* 732 */       newURL = newURL + "&remaining_plus=" + (plusExpiryTimeStamp - SystemTime.getCurrentTime());
/*     */     }
/*     */     
/* 735 */     newURL = newURL + "&remaining=" + remainingUses;
/* 736 */     if (plusRenewalCode != null) {
/* 737 */       newURL = newURL + "&renewal_code=" + plusRenewalCode;
/*     */     }
/*     */     
/* 740 */     return newURL;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long getPlusExpiryTimeStamp()
/*     */   {
/* 750 */     FeatureUtils.licenceDetails details = FeatureUtils.getPlusFeatureDetails();
/*     */     
/* 752 */     if (details == null)
/*     */     {
/* 754 */       return 0L;
/*     */     }
/*     */     
/* 757 */     return details.getExpiryTimeStamp();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/feature/FeatureManagerUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */