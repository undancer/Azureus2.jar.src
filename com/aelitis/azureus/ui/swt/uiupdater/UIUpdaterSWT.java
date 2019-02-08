/*     */ package com.aelitis.azureus.ui.swt.uiupdater;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatableAlways;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater.UIUpdaterListener;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.IMainStatusBar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class UIUpdaterSWT
/*     */   extends AEThread2
/*     */   implements ParameterListener, UIUpdater
/*     */ {
/*  49 */   private static final LogIDs LOGID = LogIDs.UI3;
/*     */   
/*     */ 
/*     */   private static final String CFG_REFRESH_INTERVAL = "GUI Refresh";
/*     */   
/*     */   private static final String CFG_REFRESH_INACTIVE_FACTOR = "Refresh When Inactive";
/*     */   
/*  56 */   private static final boolean DEBUG_TIMER = Constants.isCVSVersion();
/*     */   
/*  58 */   private static final boolean DEBUG_UPDATEABLES = Constants.IS_CVS_VERSION;
/*     */   
/*  60 */   private static UIUpdater updater = null;
/*     */   
/*     */   private int waitTimeMS;
/*     */   
/*  64 */   private boolean finished = false;
/*     */   
/*  66 */   private CopyOnWriteList<UIUpdatable> updateables = new CopyOnWriteList();
/*     */   
/*     */   private Map<UIUpdatable, String> debug_Updateables;
/*     */   
/*  70 */   private CopyOnWriteList<UIUpdatable> alwaysUpdateables = new CopyOnWriteList();
/*     */   
/*  72 */   private AEMonitor updateables_mon = new AEMonitor("updateables");
/*     */   
/*     */   private int inactiveFactor;
/*     */   
/*     */   private int inactiveTicks;
/*     */   
/*  78 */   Map averageTimes = DEBUG_TIMER ? new HashMap() : null;
/*     */   
/*  80 */   private int update_count = 0;
/*  81 */   private CopyOnWriteList<UIUpdater.UIUpdaterListener> listeners = new CopyOnWriteList();
/*     */   
/*     */   public static UIUpdater getInstance() {
/*  84 */     synchronized (UIUpdaterSWT.class) {
/*  85 */       if (updater == null) {
/*  86 */         updater = new UIUpdaterSWT();
/*  87 */         updater.start();
/*     */       }
/*     */       
/*  90 */       return updater;
/*     */     }
/*     */   }
/*     */   
/*     */   public UIUpdaterSWT() {
/*  95 */     super("UI Updater", true);
/*     */     
/*  97 */     if (DEBUG_UPDATEABLES) {
/*  98 */       this.debug_Updateables = new HashMap();
/*     */     }
/* 100 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "GUI Refresh", "Refresh When Inactive" }, this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void run()
/*     */   {
/* 108 */     final AESemaphore sem = new AESemaphore("UI Updater");
/*     */     
/* 110 */     while (!this.finished) {
/* 111 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/*     */           try {
/* 114 */             Display display = Utils.getDisplay();
/* 115 */             if (display == null) { Iterator i$;
/*     */               UIUpdater.UIUpdaterListener l;
/*     */               return;
/*     */             }
/* 119 */             if (display.getActiveShell() == null) {
/* 120 */               Shell[] shells = display.getShells();
/* 121 */               boolean noneVisible = true;
/* 122 */               for (int i = 0; i < shells.length; i++) {
/* 123 */                 if ((shells[i].isVisible()) && (!shells[i].getMinimized())) {
/* 124 */                   noneVisible = false;
/* 125 */                   break;
/*     */                 }
/*     */               }
/* 128 */               if (noneVisible)
/*     */               {
/* 130 */                 if (UIUpdaterSWT.this.alwaysUpdateables.size() > 0) {
/* 131 */                   UIUpdaterSWT.this.update(UIUpdaterSWT.this.alwaysUpdateables, false);
/*     */                 }
/*     */                 
/*     */                 Iterator i$;
/*     */                 
/*     */                 UIUpdater.UIUpdaterListener l;
/*     */                 return;
/*     */               }
/* 139 */               if (UIUpdaterSWT.access$208(UIUpdaterSWT.this) % UIUpdaterSWT.this.inactiveFactor != 0) { Iterator i$;
/*     */                 UIUpdater.UIUpdaterListener l;
/*     */                 return;
/*     */               }
/*     */             }
/* 144 */             UIUpdaterSWT.this.update(UIUpdaterSWT.this.updateables, true); } catch (Exception e) { Iterator i$;
/*     */             UIUpdater.UIUpdaterListener l;
/* 146 */             Logger.log(new LogEvent(UIUpdaterSWT.LOGID, "Error while trying to update GUI", e));
/*     */           } finally {
/*     */             try { Iterator i$;
/*     */               UIUpdater.UIUpdaterListener l;
/* 150 */               for (UIUpdater.UIUpdaterListener l : UIUpdaterSWT.this.listeners) {
/*     */                 try
/*     */                 {
/* 153 */                   l.updateComplete(UIUpdaterSWT.access$704(UIUpdaterSWT.this));
/*     */                 } catch (Throwable e) {
/* 155 */                   Debug.out(e);
/*     */                 }
/*     */               }
/*     */             } finally {
/* 159 */               sem.release();
/*     */             }
/*     */             
/*     */           }
/*     */         }
/* 164 */       });
/* 165 */       long start = SystemTime.getHighPrecisionCounter();
/*     */       
/* 167 */       sem.reserve();
/*     */       
/* 169 */       long elapsed = SystemTime.getHighPrecisionCounter() - start;
/*     */       
/* 171 */       long to_sleep = this.waitTimeMS - elapsed / 1000000L;
/*     */       
/* 173 */       if (to_sleep < 10L) {
/* 174 */         to_sleep = 10L;
/* 175 */       } else if (to_sleep > 25000L) {
/* 176 */         to_sleep = 25000L;
/*     */       }
/*     */       try
/*     */       {
/* 180 */         Thread.sleep(to_sleep);
/*     */       } catch (Exception e) {
/* 182 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void parameterChanged(String parameterName)
/*     */   {
/* 189 */     this.waitTimeMS = COConfigurationManager.getIntParameter("GUI Refresh");
/* 190 */     this.inactiveFactor = COConfigurationManager.getIntParameter("Refresh When Inactive");
/* 191 */     if (this.inactiveFactor == 0) {
/* 192 */       this.inactiveFactor = 1;
/*     */     }
/*     */   }
/*     */   
/*     */   public void addUpdater(UIUpdatable updateable)
/*     */   {
/* 198 */     this.updateables_mon.enter();
/*     */     try {
/* 200 */       if (((updateable instanceof UIUpdatableAlways)) && 
/* 201 */         (!this.alwaysUpdateables.contains(updateable))) {
/* 202 */         this.alwaysUpdateables.add(updateable);
/*     */       }
/*     */       
/*     */ 
/* 206 */       if (!this.updateables.contains(updateable)) {
/* 207 */         this.updateables.add(updateable);
/* 208 */         if (DEBUG_UPDATEABLES) {
/* 209 */           this.debug_Updateables.put(updateable, Debug.getCompressedStackTrace() + "\n");
/*     */         }
/*     */       }
/* 212 */       else if (DEBUG_UPDATEABLES) {
/* 213 */         System.out.println("WARNING: already added UIUpdatable " + updateable + "\n\t" + (String)this.debug_Updateables.get(updateable) + "\t" + Debug.getCompressedStackTrace());
/*     */       } else {
/* 215 */         System.out.println("WARNING: already added UIUpdatable " + updateable);
/*     */       }
/*     */     }
/*     */     finally {
/* 219 */       this.updateables_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isAdded(UIUpdatable updateable)
/*     */   {
/* 227 */     this.updateables_mon.enter();
/*     */     try {
/* 229 */       return this.updateables.contains(updateable);
/*     */     } finally {
/* 231 */       this.updateables_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeUpdater(UIUpdatable updateable)
/*     */   {
/* 237 */     this.updateables_mon.enter();
/*     */     try {
/* 239 */       this.updateables.remove(updateable);
/* 240 */       if ((updateable instanceof UIUpdatableAlways)) {
/* 241 */         this.alwaysUpdateables.remove(updateable);
/*     */       }
/*     */     } finally {
/* 244 */       this.updateables_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void stopIt()
/*     */   {
/* 250 */     this.finished = true;
/* 251 */     COConfigurationManager.removeParameterListener("GUI Refresh", this);
/*     */   }
/*     */   
/*     */   private void update(CopyOnWriteList<UIUpdatable> updateables, boolean is_visible) {
/* 255 */     long start = 0L;
/* 256 */     Map mapTimeMap = DEBUG_TIMER ? new HashMap() : null;
/*     */     
/* 258 */     Display display = Utils.getDisplay();
/* 259 */     if ((display == null) || (display.isDisposed())) {
/* 260 */       return;
/*     */     }
/*     */     
/* 263 */     for (UIUpdatable updateable : updateables) {
/*     */       try {
/* 265 */         if (DEBUG_TIMER) {
/* 266 */           start = SystemTime.getCurrentTime();
/*     */         }
/* 268 */         if ((updateable instanceof UIUpdatableAlways)) {
/* 269 */           ((UIUpdatableAlways)updateable).updateUI(is_visible);
/*     */         } else {
/* 271 */           updateable.updateUI();
/*     */         }
/*     */         
/* 274 */         if (DEBUG_TIMER) {
/* 275 */           long diff = SystemTime.getCurrentTime() - start;
/* 276 */           if (diff > 0L) {
/* 277 */             mapTimeMap.put(updateable, new Long(diff));
/*     */           }
/*     */         }
/*     */       } catch (Throwable t) {
/* 281 */         Logger.log(new LogEvent(LOGID, "Error while trying to update UI Element " + updateable.getUpdateUIName(), t));
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 286 */     if (DEBUG_TIMER) {
/* 287 */       makeDebugToolTip(mapTimeMap);
/*     */     }
/*     */   }
/*     */   
/*     */   private void makeDebugToolTip(Map timeMap) {
/* 292 */     int IDX_AVG = 0;
/* 293 */     int IDX_SIZE = 1;
/* 294 */     int IDX_MAX = 2;
/* 295 */     int IDX_LAST = 3;
/* 296 */     int IDX_TIME = 4;
/*     */     
/* 298 */     long ttl = 0L;
/* 299 */     for (Iterator iter = timeMap.keySet().iterator(); iter.hasNext();) {
/* 300 */       Object key = iter.next();
/*     */       
/* 302 */       if (!this.averageTimes.containsKey(key)) {
/* 303 */         this.averageTimes.put(key, new Object[] { new Long(0L), new Long(0L), new Long(0L), new Long(0L), new Long(System.currentTimeMillis()) });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 311 */       Object[] average = (Object[])this.averageTimes.get(key);
/*     */       
/* 313 */       long diff = ((Long)timeMap.get(key)).longValue();
/* 314 */       if (diff > 0L) {
/* 315 */         long count = ((Long)average[1]).longValue();
/*     */         
/*     */ 
/* 318 */         if (count >= 20L)
/* 319 */           count = 19L;
/* 320 */         long lNewAverage = (((Long)average[0]).longValue() * count + diff) / (count + 1L);
/*     */         
/* 322 */         average[0] = new Long(lNewAverage);
/* 323 */         average[1] = new Long(count + 1L);
/* 324 */         if (diff > ((Long)average[2]).longValue())
/* 325 */           average[2] = new Long(diff);
/* 326 */         average[3] = new Long(diff);
/* 327 */         average[4] = new Long(System.currentTimeMillis());
/*     */       } else {
/* 329 */         average[3] = new Long(diff);
/*     */       }
/* 331 */       ttl += diff;
/* 332 */       this.averageTimes.put(key, average);
/*     */     }
/*     */     
/*     */ 
/* 336 */     UIFunctionsSWT uiFunctionsSWT = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 337 */     IMainStatusBar mainStatusBar = uiFunctionsSWT == null ? null : uiFunctionsSWT.getMainStatusBar();
/* 338 */     if ((mainStatusBar != null) && (mainStatusBar.isMouseOver())) {
/* 339 */       StringBuilder sb = new StringBuilder();
/* 340 */       for (Iterator iter = this.averageTimes.keySet().iterator(); iter.hasNext();) {
/* 341 */         Object key = iter.next();
/* 342 */         Object[] average = (Object[])this.averageTimes.get(key);
/*     */         
/* 344 */         long lLastUpdated = ((Long)average[4]).longValue();
/* 345 */         if (System.currentTimeMillis() - lLastUpdated > 10000L) {
/* 346 */           iter.remove();
/*     */         }
/*     */         else
/*     */         {
/* 350 */           long lTime = ((Long)average[0]).longValue();
/* 351 */           if (lTime > 0L) {
/* 352 */             if (sb.length() > 0)
/* 353 */               sb.append("\n");
/* 354 */             sb.append(lTime * 100L / this.waitTimeMS);
/* 355 */             sb.append("% ");
/* 356 */             sb.append(lTime).append("ms avg: ");
/* 357 */             sb.append("[").append(((UIUpdatable)key).getUpdateUIName()).append("]");
/* 358 */             sb.append(average[1]).append(" samples");
/* 359 */             sb.append("; max:").append(average[2]);
/* 360 */             sb.append("; last:").append(average[3]);
/*     */           }
/*     */         }
/*     */       }
/* 364 */       mainStatusBar.setDebugInfo(sb.toString());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(UIUpdater.UIUpdaterListener listener)
/*     */   {
/* 372 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(UIUpdater.UIUpdaterListener listener)
/*     */   {
/* 379 */     this.listeners.remove(listener);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/uiupdater/UIUpdaterSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */