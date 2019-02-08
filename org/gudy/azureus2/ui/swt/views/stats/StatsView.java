/*     */ package org.gudy.azureus2.ui.swt.views.stats;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.MdiEntrySWT;
/*     */ import com.aelitis.azureus.ui.swt.mdi.TabbedMdiInterface;
/*     */ import org.eclipse.swt.custom.CTabFolder;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTInstance.UISWTViewEventListenerWrapper;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListenerEx;
/*     */ import org.gudy.azureus2.ui.swt.views.IViewAlwaysInitialize;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StatsView
/*     */   implements IViewAlwaysInitialize, UISWTViewCoreEventListenerEx
/*     */ {
/*  54 */   public static String VIEW_ID = "StatsView";
/*     */   
/*     */   public static final int EVENT_PERIODIC_UPDATE = 256;
/*     */   
/*     */   private TabbedMdiInterface tabbedMDI;
/*     */   
/*     */   private UpdateThread updateThread;
/*     */   
/*     */   private Object dataSource;
/*     */   
/*     */   private UISWTView swtView;
/*     */   
/*     */   private Composite parent;
/*     */   private static boolean registeredCoreSubViews;
/*     */   
/*     */   private class UpdateThread
/*     */     extends Thread
/*     */   {
/*     */     boolean bContinue;
/*     */     
/*     */     public UpdateThread()
/*     */     {
/*  76 */       super();
/*     */     }
/*     */     
/*     */     public void run() {
/*  80 */       this.bContinue = true;
/*     */       
/*  82 */       while (this.bContinue)
/*     */       {
/*  84 */         MdiEntry[] entries = StatsView.this.tabbedMDI.getEntries();
/*  85 */         for (MdiEntry entry : entries) {
/*     */           try {
/*  87 */             ((MdiEntrySWT)entry).triggerEvent(256, null);
/*     */           } catch (Exception e) {
/*  89 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */         try
/*     */         {
/*  94 */           Thread.sleep(1000L);
/*     */         }
/*     */         catch (Throwable e) {
/*  97 */           Debug.out(e);
/*  98 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */     public void stopIt() {
/* 104 */       this.bContinue = false;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isCloneable()
/*     */   {
/* 116 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public UISWTViewCoreEventListener getClone()
/*     */   {
/* 122 */     return new StatsView();
/*     */   }
/*     */   
/*     */   private void initialize(Composite composite) {
/* 126 */     this.parent = composite;
/*     */     
/*     */ 
/* 129 */     UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/* 130 */     if (uiFunctions != null) {
/* 131 */       this.tabbedMDI = uiFunctions.createTabbedMDI(composite, VIEW_ID);
/*     */       
/* 133 */       CTabFolder folder = this.tabbedMDI.getTabFolder();
/* 134 */       Label lblClose = new Label(folder, 64);
/* 135 */       lblClose.setText("x");
/* 136 */       lblClose.addListener(4, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 138 */           StatsView.this.delete();
/*     */         }
/* 140 */       });
/* 141 */       folder.setTopRight(lblClose);
/*     */       
/*     */ 
/* 144 */       UISWTInstance pluginUI = uiFunctions.getUISWTInstance();
/*     */       
/* 146 */       if ((pluginUI != null) && (!registeredCoreSubViews)) {
/* 147 */         pluginUI.addView("StatsView", "SpeedView", ActivityView.class, null);
/*     */         
/*     */ 
/* 150 */         pluginUI.addView("StatsView", "TransferStatsView", TransferStatsView.class, null);
/*     */         
/*     */ 
/* 153 */         pluginUI.addView("StatsView", "CacheView", CacheView.class, null);
/*     */         
/*     */ 
/* 156 */         pluginUI.addView("StatsView", "DHTView", DHTView.class, Integer.valueOf(0));
/*     */         
/*     */ 
/* 159 */         pluginUI.addView("StatsView", "DHTOpsView", DHTOpsView.class, Integer.valueOf(0));
/*     */         
/*     */ 
/*     */ 
/* 163 */         pluginUI.addView("StatsView", "VivaldiView", VivaldiView.class, Integer.valueOf(0));
/*     */         
/*     */ 
/*     */ 
/* 167 */         if (NetworkAdmin.getSingleton().hasDHTIPV6()) {
/* 168 */           pluginUI.addView("StatsView", "DHTView.6", DHTView.class, Integer.valueOf(3));
/*     */           
/* 170 */           pluginUI.addView("StatsView", "VivaldiView.6", VivaldiView.class, Integer.valueOf(3));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 175 */         if (Constants.isCVSVersion()) {
/* 176 */           pluginUI.addView("StatsView", "DHTView.cvs", DHTView.class, Integer.valueOf(1));
/*     */           
/* 178 */           pluginUI.addView("StatsView", "VivaldiView.cvs", VivaldiView.class, Integer.valueOf(1));
/*     */         }
/*     */         
/*     */ 
/*     */ 
/* 183 */         pluginUI.addView("StatsView", "TagStatsView", TagStatsView.class, null);
/*     */         
/*     */ 
/*     */ 
/* 187 */         registeredCoreSubViews = true;
/*     */       }
/*     */       
/* 190 */       if (pluginUI != null) {
/* 191 */         UISWTInstance.UISWTViewEventListenerWrapper[] pluginViews = pluginUI.getViewListeners("StatsView");
/* 192 */         for (int i = 0; i < pluginViews.length; i++) {
/* 193 */           UISWTInstance.UISWTViewEventListenerWrapper l = pluginViews[i];
/* 194 */           String name = l.getViewID();
/*     */           try
/*     */           {
/* 197 */             MdiEntrySWT entry = (MdiEntrySWT)this.tabbedMDI.createEntryFromEventListener("StatsView", l, name, false, null, null);
/*     */             
/* 199 */             entry.setDestroyOnDeactivate(false);
/* 200 */             if (((this.dataSource == null) && (i == 0)) || (name.equals(this.dataSource))) {
/* 201 */               this.tabbedMDI.showEntry(entry);
/*     */             }
/*     */           }
/*     */           catch (Exception e) {}
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 210 */     this.updateThread = new UpdateThread();
/* 211 */     this.updateThread.setDaemon(true);
/* 212 */     this.updateThread.start();
/*     */     
/* 214 */     dataSourceChanged(this.dataSource);
/*     */   }
/*     */   
/*     */   private void refresh()
/*     */   {
/* 219 */     if ((this.tabbedMDI == null) || (this.tabbedMDI.isDisposed())) {
/* 220 */       return;
/*     */     }
/* 222 */     MdiEntrySWT entry = this.tabbedMDI.getCurrentEntrySWT();
/* 223 */     if (entry != null) {
/* 224 */       entry.updateUI();
/*     */     }
/*     */   }
/*     */   
/*     */   private String getFullTitle() {
/* 229 */     return MessageText.getString("Stats.title.full");
/*     */   }
/*     */   
/*     */   private void delete() {
/* 233 */     if (this.updateThread != null) {
/* 234 */       this.updateThread.stopIt();
/*     */     }
/*     */     
/* 237 */     Utils.disposeSWTObjects(new Object[] { this.parent });
/*     */   }
/*     */   
/*     */ 
/*     */   private void dataSourceChanged(Object newDataSource)
/*     */   {
/* 243 */     this.dataSource = newDataSource;
/*     */     
/*     */ 
/* 246 */     if (this.tabbedMDI == null) {
/* 247 */       return;
/*     */     }
/*     */     
/* 250 */     if ((newDataSource instanceof String)) {
/* 251 */       this.tabbedMDI.showEntryByID((String)newDataSource);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean eventOccurred(UISWTViewEvent event)
/*     */   {
/* 259 */     switch (event.getType()) {
/*     */     case 0: 
/* 261 */       this.swtView = ((UISWTView)event.getData());
/* 262 */       this.swtView.setTitle(getFullTitle());
/* 263 */       this.swtView.setDestroyOnDeactivate(false);
/* 264 */       break;
/*     */     
/*     */     case 7: 
/* 267 */       delete();
/* 268 */       break;
/*     */     
/*     */     case 2: 
/* 271 */       initialize((Composite)event.getData());
/* 272 */       break;
/*     */     
/*     */     case 6: 
/* 275 */       this.swtView.setTitle(getFullTitle());
/* 276 */       break;
/*     */     
/*     */     case 1: 
/* 279 */       dataSourceChanged(event.getData());
/* 280 */       break;
/*     */     
/*     */     case 3: 
/*     */       break;
/*     */     
/*     */     case 5: 
/* 286 */       refresh();
/*     */     }
/*     */     
/*     */     
/* 290 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/stats/StatsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */