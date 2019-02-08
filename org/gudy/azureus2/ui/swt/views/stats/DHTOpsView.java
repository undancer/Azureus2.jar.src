/*     */ package org.gudy.azureus2.ui.swt.views.stats;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.dht.DHT;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTOpsView
/*     */   implements UISWTViewEventListener
/*     */ {
/*     */   public static final int DHT_TYPE_MAIN = 0;
/*     */   public static final String MSGID_PREFIX = "DHTOpsView";
/*     */   DHT dht;
/*     */   Composite panel;
/*     */   DHTOpsPanel drawPanel;
/*     */   private final boolean autoAlpha;
/*     */   private final boolean autoDHT;
/*     */   private int dht_type;
/*     */   private AzureusCore core;
/*     */   private UISWTView swtView;
/*     */   
/*     */   public DHTOpsView()
/*     */   {
/*  60 */     this(false);
/*     */   }
/*     */   
/*     */   public DHTOpsView(boolean autoAlpha) {
/*  64 */     this(autoAlpha, true);
/*     */   }
/*     */   
/*     */   public DHTOpsView(boolean autoAlpha, boolean autoDHT) {
/*  68 */     this.autoAlpha = autoAlpha;
/*  69 */     this.autoDHT = autoDHT;
/*     */   }
/*     */   
/*     */   private void init(AzureusCore core) {
/*     */     try {
/*  74 */       PluginInterface dht_pi = core.getPluginManager().getPluginInterfaceByClass(DHTPlugin.class);
/*     */       
/*  76 */       if (dht_pi == null)
/*     */       {
/*  78 */         if (this.drawPanel != null)
/*     */         {
/*  80 */           this.drawPanel.setUnavailable();
/*     */         }
/*     */         
/*  83 */         return;
/*     */       }
/*     */       
/*  86 */       DHTPlugin dht_plugin = (DHTPlugin)dht_pi.getPlugin();
/*     */       
/*  88 */       DHT[] dhts = dht_plugin.getDHTs();
/*     */       
/*  90 */       for (int i = 0; i < dhts.length; i++) {
/*  91 */         if (dhts[i].getTransport().getNetwork() == this.dht_type) {
/*  92 */           this.dht = dhts[i];
/*  93 */           break;
/*     */         }
/*     */       }
/*     */       
/*  97 */       if (this.drawPanel != null)
/*     */       {
/*  99 */         if ((this.dht == null) && (!dht_plugin.isInitialising()))
/*     */         {
/*     */ 
/* 102 */           this.drawPanel.setUnavailable();
/*     */         }
/*     */       }
/*     */       
/* 106 */       if (this.dht == null) {
/* 107 */         return;
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 111 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDHT(DHT _dht)
/*     */   {
/* 119 */     this.dht = _dht;
/*     */   }
/*     */   
/*     */   public void initialize(Composite composite) {
/* 123 */     if (this.autoDHT) {
/* 124 */       AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */       {
/*     */         public void azureusCoreRunning(AzureusCore core) {
/* 127 */           DHTOpsView.this.core = core;
/* 128 */           DHTOpsView.this.init(core);
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 133 */     this.panel = new Composite(composite, 0);
/* 134 */     this.panel.setLayout(new FillLayout());
/* 135 */     this.drawPanel = new DHTOpsPanel(this.panel);
/* 136 */     this.drawPanel.setAutoAlpha(this.autoAlpha);
/*     */   }
/*     */   
/*     */   private Composite getComposite() {
/* 140 */     return this.panel;
/*     */   }
/*     */   
/*     */   private void refresh() {
/* 144 */     if (this.dht == null) {
/* 145 */       if (this.core != null)
/*     */       {
/* 147 */         init(this.core);
/*     */       } else {
/* 149 */         return;
/*     */       }
/*     */     }
/*     */     
/* 153 */     if (this.dht != null) {
/* 154 */       this.drawPanel.refreshView(this.dht);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private String getTitleID()
/*     */   {
/* 161 */     return "DHTOpsView.title.full";
/*     */   }
/*     */   
/*     */ 
/*     */   public void delete()
/*     */   {
/* 167 */     if (this.drawPanel != null) {
/* 168 */       this.drawPanel.delete();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 173 */     switch (event.getType()) {
/*     */     case 0: 
/* 175 */       this.swtView = ((UISWTView)event.getData());
/* 176 */       this.swtView.setTitle(MessageText.getString(getTitleID()));
/* 177 */       break;
/*     */     
/*     */     case 7: 
/* 180 */       delete();
/* 181 */       break;
/*     */     
/*     */     case 2: 
/* 184 */       initialize((Composite)event.getData());
/* 185 */       break;
/*     */     
/*     */     case 6: 
/* 188 */       Messages.updateLanguageForControl(getComposite());
/* 189 */       if (this.swtView != null) {
/* 190 */         this.swtView.setTitle(MessageText.getString(getTitleID()));
/*     */       }
/*     */       
/*     */       break;
/*     */     case 1: 
/* 195 */       if ((event.getData() instanceof Number)) {
/* 196 */         this.dht_type = ((Number)event.getData()).intValue();
/* 197 */         if (this.swtView != null) {
/* 198 */           this.swtView.setTitle(MessageText.getString(getTitleID()));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       break;
/*     */     case 3: 
/*     */       break;
/*     */     case 5: 
/* 207 */       refresh();
/*     */     }
/*     */     
/*     */     
/* 211 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/stats/DHTOpsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */