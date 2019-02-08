/*     */ package org.gudy.azureus2.ui.swt.donations;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.security.CryptoManager;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerFactory;
/*     */ import java.util.Locale;
/*     */ import org.eclipse.swt.browser.LocationEvent;
/*     */ import org.eclipse.swt.browser.LocationListener;
/*     */ import org.eclipse.swt.browser.StatusTextEvent;
/*     */ import org.eclipse.swt.browser.StatusTextListener;
/*     */ import org.eclipse.swt.browser.TitleEvent;
/*     */ import org.eclipse.swt.browser.TitleListener;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Widget;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.stats.transfer.OverallStats;
/*     */ import org.gudy.azureus2.core3.stats.transfer.StatsFactory;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager.FeatureDetails;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper.BrowserFunction;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DonationWindow
/*     */ {
/*  53 */   public static boolean DEBUG = System.getProperty("donations.debug", "0").equals("1");
/*     */   
/*     */ 
/*  56 */   private static int reAskEveryHours = 96;
/*     */   
/*  58 */   private static int initialAskHours = 48;
/*     */   
/*  60 */   private static boolean pageLoadedOk = false;
/*     */   
/*  62 */   private static Shell shell = null;
/*     */   
/*     */   private static BrowserWrapper browser;
/*     */   private static BrowserWrapper.BrowserFunction browserFunction;
/*     */   
/*     */   public static void checkForDonationPopup()
/*     */   {
/*  69 */     if (shell != null) {
/*  70 */       if (DEBUG) {
/*  71 */         new MessageBoxShell(32, "Donations Test", "Already Open").open(null);
/*     */       }
/*  73 */       return;
/*     */     }
/*     */     
/*  76 */     FeatureManager fm = PluginInitializer.getDefaultInterface().getUtilities().getFeatureManager();
/*     */     
/*  78 */     FeatureManager.FeatureDetails[] fds = fm.getFeatureDetails("core");
/*     */     
/*  80 */     for (FeatureManager.FeatureDetails fd : fds)
/*     */     {
/*  82 */       if (!fd.hasExpired())
/*     */       {
/*  84 */         return;
/*     */       }
/*     */     }
/*     */     
/*  88 */     fds = fm.getFeatureDetails("no_ads");
/*     */     
/*  90 */     for (FeatureManager.FeatureDetails fd : fds)
/*     */     {
/*  92 */       if (!fd.hasExpired())
/*     */       {
/*  94 */         return;
/*     */       }
/*     */     }
/*     */     
/*  98 */     long maxDate = COConfigurationManager.getLongParameter("donations.maxDate", 0L);
/*  99 */     boolean force = (maxDate > 0L) && (SystemTime.getCurrentTime() > maxDate);
/*     */     
/*     */ 
/* 102 */     boolean alreadyDonated = COConfigurationManager.getBooleanParameter("donations.donated", false);
/*     */     
/* 104 */     if ((alreadyDonated) && (!force)) {
/* 105 */       if (DEBUG) {
/* 106 */         new MessageBoxShell(32, "Donations Test", "Already Donated! I like you.").open(null);
/*     */       }
/*     */       
/* 109 */       return;
/*     */     }
/*     */     
/* 112 */     OverallStats stats = StatsFactory.getStats();
/* 113 */     if (stats == null) {
/* 114 */       return;
/*     */     }
/*     */     
/* 117 */     long upTime = stats.getTotalUpTime();
/* 118 */     int hours = (int)(upTime / 3600L);
/*     */     
/*     */ 
/* 121 */     int nextAsk = COConfigurationManager.getIntParameter("donations.nextAskHours", 0);
/*     */     
/*     */ 
/* 124 */     if (nextAsk == 0)
/*     */     {
/* 126 */       COConfigurationManager.setParameter("donations.nextAskHours", hours + initialAskHours);
/*     */       
/* 128 */       COConfigurationManager.save();
/* 129 */       if (DEBUG) {
/* 130 */         new MessageBoxShell(32, "Donations Test", "Newbie. You're active for " + hours + ".").open(null);
/*     */       }
/*     */       
/* 133 */       return;
/*     */     }
/*     */     
/* 136 */     if ((hours < nextAsk) && (!force)) {
/* 137 */       if (DEBUG) {
/* 138 */         new MessageBoxShell(32, "Donations Test", "Wait " + (nextAsk - hours) + ".").open(null);
/*     */       }
/*     */       
/* 141 */       return;
/*     */     }
/*     */     
/* 144 */     long minDate = COConfigurationManager.getLongParameter("donations.minDate", 0L);
/*     */     
/* 146 */     if ((minDate > 0L) && (minDate > SystemTime.getCurrentTime())) {
/* 147 */       if (DEBUG) {
/* 148 */         new MessageBoxShell(32, "Donation Test", "Wait " + (SystemTime.getCurrentTime() - minDate) / 1000L / 3600L / 24L + " days").open(null);
/*     */       }
/*     */       
/*     */ 
/* 152 */       return;
/*     */     }
/*     */     
/* 155 */     COConfigurationManager.setParameter("donations.nextAskHours", hours + reAskEveryHours);
/*     */     
/* 157 */     COConfigurationManager.save();
/*     */     
/* 159 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 161 */         DonationWindow.open(false, "check");
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static void open(boolean showNoLoad, final String sourceRef) {
/* 167 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 169 */         DonationWindow._open(this.val$showNoLoad, sourceRef);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public static void _open(final boolean showNoLoad, String sourceRef) {
/* 175 */     if ((shell != null) && (!shell.isDisposed())) {
/* 176 */       return;
/*     */     }
/* 178 */     Shell parentShell = Utils.findAnyShell();
/* 179 */     shell = ShellFactory.createShell(parentShell, 67680);
/*     */     
/* 181 */     shell.setLayout(new FillLayout());
/* 182 */     if (parentShell != null) {
/* 183 */       parentShell.setCursor(shell.getDisplay().getSystemCursor(1));
/*     */     }
/*     */     
/* 186 */     shell.addTraverseListener(new TraverseListener() {
/*     */       public void keyTraversed(TraverseEvent e) {
/* 188 */         if (e.detail == 2) {
/* 189 */           e.widget.dispose();
/* 190 */           e.doit = false;
/*     */         }
/*     */         
/*     */       }
/* 194 */     });
/* 195 */     shell.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 197 */         if (this.val$parentShell != null) {
/* 198 */           this.val$parentShell.setCursor(e.display.getSystemCursor(0));
/*     */         }
/* 200 */         if ((DonationWindow.browserFunction != null) && (!DonationWindow.browserFunction.isDisposed())) {
/* 201 */           DonationWindow.browserFunction.dispose();
/*     */         }
/* 203 */         DonationWindow.access$102(null);
/*     */       }
/*     */       
/* 206 */     });
/* 207 */     browser = Utils.createSafeBrowser(shell, 0);
/* 208 */     if (browser == null) {
/* 209 */       shell.dispose();
/* 210 */       return;
/*     */     }
/*     */     
/* 213 */     browser.addTitleListener(new TitleListener() {
/*     */       public void changed(TitleEvent event) {
/* 215 */         if ((DonationWindow.shell == null) || (DonationWindow.shell.isDisposed())) {
/* 216 */           return;
/*     */         }
/* 218 */         DonationWindow.shell.setText(event.title);
/*     */       }
/*     */       
/* 221 */     });
/* 222 */     browserFunction = browser.addBrowserFunction("sendDonationEvent", new BrowserWrapper.BrowserFunction()
/*     */     {
/*     */ 
/*     */       public Object function(Object[] arguments)
/*     */       {
/*     */ 
/* 228 */         if ((DonationWindow.shell == null) || (DonationWindow.shell.isDisposed())) {
/* 229 */           return null;
/*     */         }
/*     */         
/* 232 */         if (arguments == null) {
/* 233 */           Debug.out("Invalid sendDonationEvent null ");
/* 234 */           return null;
/*     */         }
/* 236 */         if (arguments.length < 1) {
/* 237 */           Debug.out("Invalid sendDonationEvent length " + arguments.length + " not 1");
/* 238 */           return null;
/*     */         }
/* 240 */         if (!(arguments[0] instanceof String)) {
/* 241 */           Debug.out("Invalid sendDonationEvent " + (arguments[0] == null ? "NULL" : arguments.getClass().getSimpleName()) + " not String");
/*     */           
/*     */ 
/* 244 */           return null;
/*     */         }
/*     */         
/* 247 */         String text = (String)arguments[0];
/* 248 */         if (text.contains("page-loaded")) {
/* 249 */           DonationWindow.access$202(true);
/* 250 */           COConfigurationManager.setParameter("donations.count", COConfigurationManager.getLongParameter("donations.count", 1L) + 1L);
/*     */           
/* 252 */           Utils.centreWindow(DonationWindow.shell);
/* 253 */           if (this.val$parentShell != null) {
/* 254 */             this.val$parentShell.setCursor(DonationWindow.shell.getDisplay().getSystemCursor(0));
/*     */           }
/* 256 */           DonationWindow.shell.open();
/* 257 */         } else if (text.contains("reset-ask-time")) {
/* 258 */           int time = DonationWindow.reAskEveryHours;
/* 259 */           String[] strings = text.split(" ");
/* 260 */           if (strings.length > 1) {
/*     */             try {
/* 262 */               time = Integer.parseInt(strings[1]);
/*     */             }
/*     */             catch (Throwable t) {}
/*     */           }
/* 266 */           DonationWindow.resetAskTime(time);
/* 267 */         } else if (text.contains("never-ask-again")) {
/* 268 */           DonationWindow.neverAskAgain();
/* 269 */         } else if (text.contains("close")) {
/* 270 */           Utils.execSWTThreadLater(0, new AERunnable() {
/*     */             public void runSupport() {
/* 272 */               if ((DonationWindow.shell != null) && (!DonationWindow.shell.isDisposed())) {
/* 273 */                 DonationWindow.shell.dispose();
/*     */               }
/*     */             }
/*     */           });
/* 277 */         } else if (text.startsWith("open-url")) {
/* 278 */           String url = text.substring(9);
/* 279 */           Utils.launch(url);
/* 280 */         } else if (text.startsWith("set-size")) {
/* 281 */           String[] strings = text.split(" ");
/* 282 */           if (strings.length > 2) {
/*     */             try {
/* 284 */               int w = Integer.parseInt(strings[1]);
/* 285 */               int h = Integer.parseInt(strings[2]);
/*     */               
/* 287 */               Rectangle computeTrim = DonationWindow.shell.computeTrim(0, 0, w, h);
/* 288 */               DonationWindow.shell.setSize(computeTrim.width, computeTrim.height);
/*     */             }
/*     */             catch (Exception e) {}
/*     */           }
/*     */         }
/* 293 */         return null;
/*     */       }
/*     */       
/* 296 */     });
/* 297 */     browser.addStatusTextListener(new StatusTextListener() {
/* 298 */       String last = null;
/*     */       
/*     */       public void changed(StatusTextEvent event) {
/* 301 */         String text = event.text.toLowerCase();
/* 302 */         if ((this.last != null) && (this.last.equals(text))) {
/* 303 */           return;
/*     */         }
/* 305 */         this.last = text;
/* 306 */         DonationWindow.browserFunction.function(new Object[] { text });
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 311 */     });
/* 312 */     browser.addLocationListener(new LocationListener()
/*     */     {
/*     */       public void changing(LocationEvent event) {}
/*     */       
/*     */ 
/*     */ 
/*     */       public void changed(LocationEvent event) {}
/* 319 */     });
/* 320 */     long upTime = StatsFactory.getStats().getTotalUpTime();
/* 321 */     int upHours = (int)(upTime / 3600L);
/* 322 */     String azid = Base32.encode(CryptoManagerFactory.getSingleton().getSecureID());
/* 323 */     String url = "http://" + System.getProperty("platform_address", "www.vuze.com") + ":" + System.getProperty("platform_port", "80") + "/" + "donate.start?locale=" + MessageText.getCurrentLocale().toString() + "&azv=" + "5.7.6.0" + "&count=" + COConfigurationManager.getLongParameter("donations.count", 1L) + "&uphours=" + upHours + "&azid=" + azid + "&sourceref=" + UrlUtils.encode(sourceRef);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 332 */     if (!browser.isFake())
/*     */     {
/* 334 */       SimpleTimer.addEvent("donation.pageload", SystemTime.getOffsetTime(6000L), new TimerEventPerformer()
/*     */       {
/*     */         public void perform(TimerEvent event) {
/* 337 */           if (!DonationWindow.pageLoadedOk) {
/* 338 */             Utils.execSWTThread(new AERunnable() {
/*     */               public void runSupport() {
/* 340 */                 Debug.out("Page Didn't Load:" + DonationWindow.9.this.val$url);
/* 341 */                 DonationWindow.shell.dispose();
/* 342 */                 if (DonationWindow.9.this.val$showNoLoad) {
/* 343 */                   new MessageBoxShell(32, MessageText.getString("DonationWindow.noload.title"), MessageText.getString("DonationWindow.noload.text", new String[] { DonationWindow.9.this.val$url })).open(null);
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 357 */     browser.setUrl(url);
/*     */     
/* 359 */     if (browser.isFake())
/*     */     {
/* 361 */       browser.setUrl("http://www.vuze.com/donation/donate.php");
/*     */       
/* 363 */       browser.setText("Please follow the link to donate via an external browser");
/*     */       
/* 365 */       shell.setSize(400, 500);
/*     */       
/* 367 */       Utils.centreWindow(shell);
/*     */       
/* 369 */       if (parentShell != null) {
/* 370 */         parentShell.setCursor(shell.getDisplay().getSystemCursor(0));
/*     */       }
/*     */       
/* 373 */       shell.open();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void neverAskAgain()
/*     */   {
/* 383 */     COConfigurationManager.setParameter("donations.donated", true);
/* 384 */     updateMinDate();
/* 385 */     COConfigurationManager.save();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void resetAskTime()
/*     */   {
/* 394 */     resetAskTime(reAskEveryHours);
/*     */   }
/*     */   
/*     */   public static void resetAskTime(int askEveryHours) {
/* 398 */     long upTime = StatsFactory.getStats().getTotalUpTime();
/* 399 */     int hours = (int)(upTime / 3600L);
/* 400 */     int nextAsk = hours + askEveryHours;
/* 401 */     COConfigurationManager.setParameter("donations.nextAskHours", nextAsk);
/* 402 */     COConfigurationManager.setParameter("donations.lastVersion", "5.7.6.0");
/* 403 */     updateMinDate();
/* 404 */     COConfigurationManager.save();
/*     */   }
/*     */   
/*     */   public static void updateMinDate() {
/* 408 */     COConfigurationManager.setParameter("donations.minDate", SystemTime.getOffsetTime(2592000000L));
/* 409 */     COConfigurationManager.setParameter("donations.maxDate", SystemTime.getOffsetTime(10368000000L));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static int getInitialAskHours()
/*     */   {
/* 420 */     return initialAskHours;
/*     */   }
/*     */   
/*     */   public static void setInitialAskHours(int i) {
/* 424 */     initialAskHours = i;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/* 429 */       AzureusCoreFactory.create().start();
/*     */       
/* 431 */       open(true, "test");
/*     */     } catch (Exception e) {
/* 433 */       e.printStackTrace();
/*     */     }
/* 435 */     Display d = Display.getDefault();
/*     */     for (;;) {
/* 437 */       if (!d.readAndDispatch()) {
/* 438 */         d.sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/donations/DonationWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */