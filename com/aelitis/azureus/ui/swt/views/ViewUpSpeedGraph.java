/*     */ package com.aelitis.azureus.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.impl.TransferSpeedValidator;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.graphics.SpeedGraphic;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*     */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ViewUpSpeedGraph
/*     */   implements UISWTViewCoreEventListener
/*     */ {
/*  54 */   GlobalManager manager = null;
/*     */   
/*  56 */   GlobalManagerStats stats = null;
/*     */   
/*     */   Canvas upSpeedCanvas;
/*     */   
/*     */   SpeedGraphic upSpeedGraphic;
/*     */   
/*     */   TimerEventPeriodic timerEvent;
/*     */   
/*  64 */   private boolean everRefreshed = false;
/*     */   private UISWTView swtView;
/*     */   
/*     */   public ViewUpSpeedGraph()
/*     */   {
/*  69 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*  71 */         ViewUpSpeedGraph.this.manager = core.getGlobalManager();
/*  72 */         ViewUpSpeedGraph.this.stats = ViewUpSpeedGraph.this.manager.getStats();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   private void initialize(Composite composite)
/*     */   {
/*  80 */     composite.setLayout(new GridLayout());
/*  81 */     this.upSpeedCanvas = new Canvas(composite, 536870912);
/*  82 */     GridData gridData = new GridData(1808);
/*  83 */     this.upSpeedCanvas.setLayoutData(gridData);
/*  84 */     this.upSpeedGraphic = SpeedGraphic.getInstance();
/*  85 */     this.upSpeedGraphic.initialize(this.upSpeedCanvas);
/*     */   }
/*     */   
/*     */   private void periodicUpdate()
/*     */   {
/*  90 */     if ((this.stats == null) || (this.manager == null)) {
/*  91 */       return;
/*     */     }
/*     */     
/*  94 */     int swarms_peer_speed = (int)this.stats.getTotalSwarmsPeerRate(true, false);
/*     */     
/*  96 */     this.upSpeedGraphic.addIntsValue(new int[] { this.stats.getDataSendRate() + this.stats.getProtocolSendRate(), this.stats.getProtocolSendRate(), COConfigurationManager.getIntParameter(TransferSpeedValidator.getActiveUploadParameter(this.manager)) * 1024, swarms_peer_speed });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void delete()
/*     */   {
/* 105 */     Utils.disposeComposite(this.upSpeedCanvas);
/* 106 */     this.upSpeedGraphic.dispose();
/*     */   }
/*     */   
/*     */   private String getFullTitle() {
/* 110 */     return MessageText.getString("TableColumn.header.upspeed");
/*     */   }
/*     */   
/*     */   private Composite getComposite() {
/* 114 */     return this.upSpeedCanvas;
/*     */   }
/*     */   
/*     */   private void refresh() {
/* 118 */     if (!this.everRefreshed) {
/* 119 */       this.everRefreshed = true;
/* 120 */       this.timerEvent = SimpleTimer.addPeriodicEvent("TopBarSpeedGraphicView", 1000L, new TimerEventPerformer() {
/*     */         public void perform(TimerEvent event) {
/* 122 */           if (ViewUpSpeedGraph.this.upSpeedCanvas.isDisposed()) {
/* 123 */             ViewUpSpeedGraph.this.timerEvent.cancel();
/*     */           } else {
/* 125 */             ViewUpSpeedGraph.this.periodicUpdate();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/* 130 */     this.upSpeedGraphic.refresh(false);
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 134 */     switch (event.getType()) {
/*     */     case 0: 
/* 136 */       this.swtView = ((UISWTView)event.getData());
/* 137 */       this.swtView.setTitle(getFullTitle());
/* 138 */       break;
/*     */     
/*     */     case 7: 
/* 141 */       delete();
/* 142 */       break;
/*     */     
/*     */     case 2: 
/* 145 */       initialize((Composite)event.getData());
/* 146 */       break;
/*     */     
/*     */     case 6: 
/* 149 */       Messages.updateLanguageForControl(getComposite());
/* 150 */       this.swtView.setTitle(getFullTitle());
/* 151 */       break;
/*     */     
/*     */     case 5: 
/* 154 */       refresh();
/*     */     }
/*     */     
/*     */     
/* 158 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/ViewUpSpeedGraph.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */