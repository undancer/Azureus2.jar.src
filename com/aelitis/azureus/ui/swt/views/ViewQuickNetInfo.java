/*     */ package com.aelitis.azureus.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.speedmanager.SpeedManager;
/*     */ import java.net.InetAddress;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
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
/*     */ public class ViewQuickNetInfo
/*     */   implements UISWTViewCoreEventListener
/*     */ {
/*     */   private UISWTView swtView;
/*     */   private Composite composite;
/*     */   private BufferedLabel asn;
/*     */   private BufferedLabel current_ip;
/*     */   private SpeedManager speed_manager;
/*     */   
/*     */   public ViewQuickNetInfo()
/*     */   {
/*  59 */     AzureusCoreFactory.addCoreRunningListener(new AzureusCoreRunningListener() {
/*     */       public void azureusCoreRunning(AzureusCore core) {
/*  61 */         ViewQuickNetInfo.this.speed_manager = core.getSpeedManager();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void initialize(Composite parent)
/*     */   {
/*  71 */     parent.setLayout(new GridLayout());
/*     */     
/*  73 */     this.composite = new Composite(parent, 2048);
/*     */     
/*  75 */     GridData gridData = new GridData(1808);
/*     */     
/*  77 */     Utils.setLayoutData(this.composite, gridData);
/*     */     
/*  79 */     GridLayout layout = new GridLayout(4, false);
/*     */     
/*  81 */     this.composite.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*  85 */     Label label = new Label(this.composite, 0);
/*  86 */     Messages.setLanguageText(label, "SpeedView.stats.asn");
/*  87 */     this.asn = new BufferedLabel(this.composite, 0);
/*  88 */     gridData = new GridData(768);
/*  89 */     Utils.setLayoutData(this.asn, gridData);
/*     */     
/*     */ 
/*     */ 
/*  93 */     label = new Label(this.composite, 0);
/*  94 */     Messages.setLanguageText(label, "label.current_ip");
/*  95 */     this.current_ip = new BufferedLabel(this.composite, 0);
/*  96 */     gridData = new GridData(768);
/*  97 */     Utils.setLayoutData(this.current_ip, gridData);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void delete()
/*     */   {
/* 105 */     Utils.disposeComposite(this.composite);
/*     */   }
/*     */   
/*     */ 
/*     */   private String getFullTitle()
/*     */   {
/* 111 */     return MessageText.getString("label.quick.net.info");
/*     */   }
/*     */   
/*     */ 
/*     */   private Composite getComposite()
/*     */   {
/* 117 */     return this.composite;
/*     */   }
/*     */   
/*     */   private void refresh()
/*     */   {
/* 122 */     if (this.speed_manager != null)
/*     */     {
/* 124 */       this.asn.setText(this.speed_manager.getASN());
/*     */     }
/*     */     
/*     */ 
/* 128 */     InetAddress ip = NetworkAdmin.getSingleton().getDefaultPublicAddress();
/*     */     
/* 130 */     InetAddress ip_v6 = NetworkAdmin.getSingleton().getDefaultPublicAddressV6();
/*     */     
/* 132 */     String str = ip == null ? "" : ip.getHostAddress();
/*     */     
/* 134 */     if ((ip_v6 != null) && (!ip_v6.equals(ip)))
/*     */     {
/* 136 */       str = str + (str.isEmpty() ? "" : ", ") + ip_v6.getHostAddress();
/*     */     }
/*     */     
/* 139 */     this.current_ip.setText(str);
/*     */   }
/*     */   
/*     */   public boolean eventOccurred(UISWTViewEvent event) {
/* 143 */     switch (event.getType()) {
/*     */     case 0: 
/* 145 */       this.swtView = event.getView();
/* 146 */       this.swtView.setTitle(getFullTitle());
/* 147 */       break;
/*     */     
/*     */     case 7: 
/* 150 */       delete();
/* 151 */       break;
/*     */     
/*     */     case 2: 
/* 154 */       initialize((Composite)event.getData());
/* 155 */       break;
/*     */     
/*     */     case 6: 
/* 158 */       Messages.updateLanguageForControl(getComposite());
/* 159 */       this.swtView.setTitle(getFullTitle());
/* 160 */       break;
/*     */     
/*     */     case 5: 
/* 163 */       refresh();
/* 164 */       break;
/*     */     case 3: 
/* 166 */       this.composite.traverse(16);
/*     */     }
/*     */     
/*     */     
/*     */ 
/* 171 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/ViewQuickNetInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */