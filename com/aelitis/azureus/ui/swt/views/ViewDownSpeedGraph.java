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
/*     */ public class ViewDownSpeedGraph
/*     */   implements UISWTViewCoreEventListener
/*     */ {
/*  53 */   GlobalManager manager = null;
/*     */   
/*  55 */   GlobalManagerStats stats = null;
/*     */   
/*     */   Canvas downSpeedCanvas;
/*     */   
/*     */   SpeedGraphic downSpeedGraphic;
/*     */   
/*     */   TimerEventPeriodic timerEvent;
/*     */   
/*  63 */   private boolean everRefreshed = false;
/*     */   private UISWTView swtView;
/*     */   
/*     */   public ViewDownSpeedGraph()
/*     */   {
/*  68 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*  70 */         ViewDownSpeedGraph.this.manager = core.getGlobalManager();
/*  71 */         ViewDownSpeedGraph.this.stats = ViewDownSpeedGraph.this.manager.getStats();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void periodicUpdate() {
/*  77 */     if ((this.manager == null) || (this.stats == null)) {
/*  78 */       return;
/*     */     }
/*     */     
/*  81 */     int swarms_peer_speed = (int)this.stats.getTotalSwarmsPeerRate(true, false);
/*     */     
/*  83 */     this.downSpeedGraphic.addIntsValue(new int[] { this.stats.getDataReceiveRate() + this.stats.getProtocolReceiveRate(), this.stats.getProtocolReceiveRate(), COConfigurationManager.getIntParameter("Max Download Speed KBs") * 1024, swarms_peer_speed });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initialize(Composite composite)
/*     */   {
/*  93 */     composite.setLayout(new GridLayout());
/*  94 */     this.downSpeedCanvas = new Canvas(composite, 536870912);
/*  95 */     GridData gridData = new GridData(1808);
/*  96 */     this.downSpeedCanvas.setLayoutData(gridData);
/*  97 */     this.downSpeedGraphic = SpeedGraphic.getInstance();
/*  98 */     this.downSpeedGraphic.initialize(this.downSpeedCanvas);
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
/*     */ 
/*     */   private void delete()
/*     */   {
/* 116 */     Utils.disposeComposite(this.downSpeedCanvas);
/* 117 */     this.downSpeedGraphic.dispose();
/*     */   }
/*     */   
/*     */   private String getFullTitle() {
/* 121 */     return MessageText.getString("TableColumn.header.downspeed");
/*     */   }
/*     */   
/*     */ 
/*     */   private Composite getComposite()
/*     */   {
/* 127 */     return this.downSpeedCanvas;
/*     */   }
/*     */   
/*     */   private void refresh() {
/* 131 */     if (!this.everRefreshed) {
/* 132 */       this.everRefreshed = true;
/* 133 */       this.timerEvent = SimpleTimer.addPeriodicEvent("TopBarSpeedGraphicView", 1000L, new TimerEventPerformer() {
/*     */         public void perform(TimerEvent event) {
/* 135 */           if (ViewDownSpeedGraph.this.downSpeedCanvas.isDisposed()) {
/* 136 */             ViewDownSpeedGraph.this.timerEvent.cancel();
/*     */           } else {
/* 138 */             ViewDownSpeedGraph.this.periodicUpdate();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/* 143 */     this.downSpeedGraphic.refresh(false);
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 147 */     switch (event.getType()) {
/*     */     case 0: 
/* 149 */       this.swtView = event.getView();
/* 150 */       this.swtView.setTitle(getFullTitle());
/* 151 */       break;
/*     */     
/*     */     case 7: 
/* 154 */       delete();
/* 155 */       break;
/*     */     
/*     */     case 2: 
/* 158 */       initialize((Composite)event.getData());
/* 159 */       break;
/*     */     
/*     */     case 6: 
/* 162 */       Messages.updateLanguageForControl(getComposite());
/* 163 */       this.swtView.setTitle(getFullTitle());
/* 164 */       break;
/*     */     
/*     */     case 5: 
/* 167 */       refresh();
/*     */     }
/*     */     
/*     */     
/* 171 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/ViewDownSpeedGraph.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */