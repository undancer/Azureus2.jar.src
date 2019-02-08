/*     */ package org.gudy.azureus2.ui.swt.views.stats;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.config.impl.TransferSpeedValidator;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.Legend;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ActivityView
/*     */   implements ParameterListener, UISWTViewCoreEventListener
/*     */ {
/*     */   public static final String MSGID_PREFIX = "SpeedView";
/*  62 */   GlobalManager manager = null;
/*  63 */   GlobalManagerStats stats = null;
/*     */   
/*     */   Composite panel;
/*     */   
/*     */   Canvas downSpeedCanvas;
/*     */   
/*     */   SpeedGraphic downSpeedGraphic;
/*     */   Canvas upSpeedCanvas;
/*     */   SpeedGraphic upSpeedGraphic;
/*     */   private UISWTView swtView;
/*     */   
/*     */   public ActivityView()
/*     */   {
/*  76 */     this.downSpeedGraphic = SpeedGraphic.getInstance();
/*  77 */     this.upSpeedGraphic = SpeedGraphic.getInstance();
/*  78 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*  80 */         ActivityView.this.manager = core.getGlobalManager();
/*  81 */         ActivityView.this.stats = ActivityView.this.manager.getStats();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void periodicUpdate()
/*     */   {
/*  90 */     if ((this.manager == null) || (this.stats == null)) {
/*  91 */       return;
/*     */     }
/*     */     
/*  94 */     int swarms_peer_speed = (int)this.stats.getTotalSwarmsPeerRate(true, false);
/*     */     
/*  96 */     this.downSpeedGraphic.addIntsValue(new int[] { this.stats.getDataReceiveRate() + this.stats.getProtocolReceiveRate(), this.stats.getProtocolReceiveRate(), COConfigurationManager.getIntParameter("Max Download Speed KBs") * 1024, swarms_peer_speed });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 102 */     this.upSpeedGraphic.addIntsValue(new int[] { this.stats.getDataSendRate() + this.stats.getProtocolSendRate(), this.stats.getProtocolSendRate(), COConfigurationManager.getIntParameter(TransferSpeedValidator.getActiveUploadParameter(this.manager)) * 1024, swarms_peer_speed });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initialize(Composite composite)
/*     */   {
/* 110 */     this.panel = new Composite(composite, 0);
/* 111 */     this.panel.setLayout(new GridLayout());
/*     */     
/*     */ 
/* 114 */     Group gDownSpeed = new Group(this.panel, 0);
/* 115 */     Messages.setLanguageText(gDownSpeed, "SpeedView.downloadSpeed.title");
/* 116 */     GridData gridData = new GridData(1808);
/* 117 */     gDownSpeed.setLayoutData(gridData);
/* 118 */     gDownSpeed.setLayout(new GridLayout());
/*     */     
/* 120 */     this.downSpeedCanvas = new Canvas(gDownSpeed, 262144);
/* 121 */     gridData = new GridData(1808);
/* 122 */     this.downSpeedCanvas.setLayoutData(gridData);
/* 123 */     this.downSpeedGraphic.initialize(this.downSpeedCanvas);
/* 124 */     Color[] colors = this.downSpeedGraphic.colors;
/*     */     
/* 126 */     Group gUpSpeed = new Group(this.panel, 0);
/* 127 */     Messages.setLanguageText(gUpSpeed, "SpeedView.uploadSpeed.title");
/* 128 */     gridData = new GridData(1808);
/* 129 */     gUpSpeed.setLayoutData(gridData);
/* 130 */     gUpSpeed.setLayout(new GridLayout());
/*     */     
/* 132 */     this.upSpeedCanvas = new Canvas(gUpSpeed, 262144);
/* 133 */     gridData = new GridData(1808);
/* 134 */     this.upSpeedCanvas.setLayoutData(gridData);
/* 135 */     this.upSpeedGraphic.initialize(this.upSpeedCanvas);
/*     */     
/* 137 */     COConfigurationManager.addAndFireParameterListener("Stats Graph Dividers", this);
/*     */     
/* 139 */     this.upSpeedGraphic.setLineColors(colors);
/*     */     
/* 141 */     String[] colorConfigs = { "ActivityView.legend.peeraverage", "ActivityView.legend.achieved", "ActivityView.legend.overhead", "ActivityView.legend.limit", "ActivityView.legend.swarmaverage", "ActivityView.legend.trimmed" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 150 */     Legend.createLegendComposite(this.panel, colors, colorConfigs);
/*     */     
/* 152 */     this.panel.addListener(26, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/*     */ 
/* 160 */         ActivityView.this.refresh(true);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void delete() {
/* 166 */     Utils.disposeComposite(this.panel);
/* 167 */     this.downSpeedGraphic.dispose();
/* 168 */     this.upSpeedGraphic.dispose();
/* 169 */     COConfigurationManager.removeParameterListener("Stats Graph Dividers", this);
/*     */   }
/*     */   
/*     */   private Composite getComposite() {
/* 173 */     return this.panel;
/*     */   }
/*     */   
/*     */   private void refresh(boolean force) {
/* 177 */     this.downSpeedGraphic.refresh(force);
/* 178 */     this.upSpeedGraphic.refresh(force);
/*     */   }
/*     */   
/*     */   public void parameterChanged(String param_name) {
/* 182 */     boolean update_dividers = COConfigurationManager.getBooleanParameter("Stats Graph Dividers");
/* 183 */     int update_divider_width = update_dividers ? 60 : 0;
/* 184 */     this.downSpeedGraphic.setUpdateDividerWidth(update_divider_width);
/* 185 */     this.upSpeedGraphic.setUpdateDividerWidth(update_divider_width);
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 189 */     switch (event.getType()) {
/*     */     case 0: 
/* 191 */       this.swtView = event.getView();
/* 192 */       this.swtView.setTitle(MessageText.getString("SpeedView.title.full"));
/* 193 */       break;
/*     */     
/*     */     case 7: 
/* 196 */       delete();
/* 197 */       break;
/*     */     
/*     */     case 2: 
/* 200 */       initialize((Composite)event.getData());
/* 201 */       break;
/*     */     
/*     */     case 6: 
/* 204 */       Messages.updateLanguageForControl(getComposite());
/* 205 */       break;
/*     */     
/*     */     case 1: 
/*     */       break;
/*     */     
/*     */     case 3: 
/* 211 */       refresh(true);
/* 212 */       break;
/*     */     
/*     */     case 5: 
/* 215 */       refresh(false);
/* 216 */       break;
/*     */     
/*     */     case 256: 
/* 219 */       periodicUpdate();
/*     */     }
/*     */     
/*     */     
/* 223 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/stats/ActivityView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */