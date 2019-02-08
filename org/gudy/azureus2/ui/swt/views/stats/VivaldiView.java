/*     */ package org.gudy.azureus2.ui.swt.views.stats;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.dht.DHT;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControl;
/*     */ import com.aelitis.azureus.core.dht.control.DHTControlContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransport;
/*     */ import com.aelitis.azureus.plugins.dht.DHTPlugin;
/*     */ import java.util.List;
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
/*     */ public class VivaldiView
/*     */   implements UISWTViewEventListener
/*     */ {
/*     */   public static final int DHT_TYPE_MAIN = 0;
/*     */   public static final int DHT_TYPE_CVS = 1;
/*     */   public static final int DHT_TYPE_MAIN_V6 = 3;
/*     */   public static final String MSGID_PREFIX = "VivaldiView";
/*     */   DHT dht;
/*     */   Composite panel;
/*     */   VivaldiPanel drawPanel;
/*     */   private final boolean autoAlpha;
/*     */   private int dht_type;
/*     */   private AzureusCore core;
/*     */   private UISWTView swtView;
/*     */   
/*     */   public VivaldiView(boolean autoAlpha)
/*     */   {
/*  62 */     this.autoAlpha = autoAlpha;
/*     */   }
/*     */   
/*     */   public VivaldiView()
/*     */   {
/*  67 */     this(false);
/*     */   }
/*     */   
/*     */   private void init(AzureusCore core) {
/*     */     try {
/*  72 */       PluginInterface dht_pi = core.getPluginManager().getPluginInterfaceByClass(DHTPlugin.class);
/*     */       
/*  74 */       if (dht_pi == null)
/*     */       {
/*  76 */         return;
/*     */       }
/*     */       
/*  79 */       DHT[] dhts = ((DHTPlugin)dht_pi.getPlugin()).getDHTs();
/*     */       
/*  81 */       for (int i = 0; i < dhts.length; i++) {
/*  82 */         if (dhts[i].getTransport().getNetwork() == this.dht_type) {
/*  83 */           this.dht = dhts[i];
/*  84 */           break;
/*     */         }
/*     */       }
/*     */       
/*  88 */       if (this.dht == null) {
/*  89 */         return;
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/*  93 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */   private void initialize(Composite composite) {
/*  98 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener()
/*     */     {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/* 101 */         VivaldiView.this.core = core;
/* 102 */         VivaldiView.this.init(core);
/*     */       }
/*     */       
/* 105 */     });
/* 106 */     this.panel = new Composite(composite, 0);
/* 107 */     this.panel.setLayout(new FillLayout());
/* 108 */     this.drawPanel = new VivaldiPanel(this.panel);
/* 109 */     this.drawPanel.setAutoAlpha(this.autoAlpha);
/*     */   }
/*     */   
/*     */   private Composite getComposite() {
/* 113 */     return this.panel;
/*     */   }
/*     */   
/*     */   private void refresh() {
/* 117 */     if (this.dht == null) {
/* 118 */       if (this.core != null)
/*     */       {
/* 120 */         init(this.core);
/*     */       } else {
/* 122 */         return;
/*     */       }
/*     */     }
/*     */     
/* 126 */     if (this.dht != null) {
/* 127 */       List<DHTControlContact> l = this.dht.getControl().getContacts();
/* 128 */       this.drawPanel.refreshContacts(l, this.dht.getControl().getTransport().getLocalContact());
/*     */     }
/*     */   }
/*     */   
/*     */   private String getTitleID() {
/* 133 */     if (this.dht_type == 0)
/*     */     {
/* 135 */       return "VivaldiView.title.full";
/*     */     }
/* 137 */     if (this.dht_type == 1)
/*     */     {
/* 139 */       return "VivaldiView.title.fullcvs";
/*     */     }
/*     */     
/* 142 */     return "VivaldiView.title.full_v6";
/*     */   }
/*     */   
/*     */   private void delete()
/*     */   {
/* 147 */     if (this.drawPanel != null) {
/* 148 */       this.drawPanel.delete();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 153 */     switch (event.getType()) {
/*     */     case 0: 
/* 155 */       this.swtView = ((UISWTView)event.getData());
/* 156 */       this.swtView.setTitle(MessageText.getString(getTitleID()));
/* 157 */       break;
/*     */     
/*     */     case 7: 
/* 160 */       delete();
/* 161 */       break;
/*     */     
/*     */     case 2: 
/* 164 */       initialize((Composite)event.getData());
/* 165 */       break;
/*     */     
/*     */     case 6: 
/* 168 */       Messages.updateLanguageForControl(getComposite());
/* 169 */       if (this.swtView != null) {
/* 170 */         this.swtView.setTitle(MessageText.getString(getTitleID()));
/*     */       }
/*     */       
/*     */       break;
/*     */     case 1: 
/* 175 */       if ((event.getData() instanceof Number)) {
/* 176 */         this.dht_type = ((Number)event.getData()).intValue();
/* 177 */         if (this.swtView != null) {
/* 178 */           this.swtView.setTitle(MessageText.getString(getTitleID()));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       break;
/*     */     case 3: 
/*     */       break;
/*     */     case 5: 
/* 187 */       refresh();
/*     */     }
/*     */     
/*     */     
/* 191 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/stats/VivaldiView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */