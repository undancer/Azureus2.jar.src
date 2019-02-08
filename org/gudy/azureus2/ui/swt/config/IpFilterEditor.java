/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpRange;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
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
/*     */ public class IpFilterEditor
/*     */ {
/*     */   AzureusCore azureus_core;
/*     */   IpRange range;
/*     */   boolean newRange;
/*     */   
/*     */   public IpFilterEditor(AzureusCore _azureus_core, Shell parent, IpRange _range)
/*     */   {
/*  57 */     this.azureus_core = _azureus_core;
/*  58 */     this.range = _range;
/*  59 */     if (this.range == null) {
/*  60 */       this.newRange = true;
/*  61 */       this.range = this.azureus_core.getIpFilterManager().getIPFilter().createRange(false);
/*     */     }
/*     */     
/*  64 */     final Shell shell = ShellFactory.createShell(parent, 67680);
/*  65 */     Messages.setLanguageText(shell, "ConfigView.section.ipfilter.editFilter");
/*  66 */     Utils.setShellIcon(shell);
/*  67 */     GridLayout layout = new GridLayout();
/*  68 */     layout.numColumns = 2;
/*  69 */     shell.setLayout(layout);
/*     */     
/*  71 */     Label label = new Label(shell, 0);
/*  72 */     Messages.setLanguageText(label, "ConfigView.section.ipfilter.description");
/*     */     
/*  74 */     final Text textDescription = new Text(shell, 2048);
/*  75 */     GridData gridData = new GridData();
/*  76 */     gridData.widthHint = 300;
/*  77 */     Utils.setLayoutData(textDescription, gridData);
/*     */     
/*  79 */     label = new Label(shell, 0);
/*  80 */     Messages.setLanguageText(label, "ConfigView.section.ipfilter.start");
/*     */     
/*  82 */     final Text textStartIp = new Text(shell, 2048);
/*  83 */     gridData = new GridData();
/*  84 */     gridData.widthHint = 120;
/*  85 */     Utils.setLayoutData(textStartIp, gridData);
/*     */     
/*  87 */     label = new Label(shell, 0);
/*  88 */     Messages.setLanguageText(label, "ConfigView.section.ipfilter.end");
/*     */     
/*  90 */     final Text textEndIp = new Text(shell, 2048);
/*  91 */     gridData = new GridData();
/*  92 */     gridData.widthHint = 120;
/*  93 */     Utils.setLayoutData(textEndIp, gridData);
/*     */     
/*  95 */     final Button ok = new Button(shell, 8);
/*  96 */     Messages.setLanguageText(ok, "Button.ok");
/*  97 */     shell.setDefaultButton(ok);
/*     */     
/*  99 */     gridData = new GridData(896);
/* 100 */     gridData.horizontalSpan = 2;
/* 101 */     gridData.widthHint = 100;
/* 102 */     Utils.setLayoutData(ok, gridData);
/* 103 */     ok.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 105 */         IpFilterEditor.this.range.setDescription(textDescription.getText());
/* 106 */         IpFilterEditor.this.range.setStartIp(textStartIp.getText());
/* 107 */         IpFilterEditor.this.range.setEndIp(textEndIp.getText());
/* 108 */         IpFilterEditor.this.range.checkValid();
/* 109 */         if (IpFilterEditor.this.newRange) {
/* 110 */           IpFilterEditor.this.azureus_core.getIpFilterManager().getIPFilter().addRange(IpFilterEditor.this.range);
/*     */         }
/* 112 */         shell.dispose();
/*     */       }
/*     */       
/* 115 */     });
/* 116 */     textStartIp.addModifyListener(new ModifyListener() {
/*     */       public void modifyText(ModifyEvent event) {
/* 118 */         IpFilterEditor.this.range.setStartIp(textStartIp.getText());
/* 119 */         IpFilterEditor.this.range.checkValid();
/* 120 */         if (IpFilterEditor.this.range.isValid()) {
/* 121 */           ok.setEnabled(true);
/*     */         } else {
/* 123 */           ok.setEnabled(false);
/*     */         }
/*     */       }
/* 126 */     });
/* 127 */     textEndIp.addModifyListener(new ModifyListener() {
/*     */       public void modifyText(ModifyEvent event) {
/* 129 */         IpFilterEditor.this.range.setEndIp(textEndIp.getText());
/* 130 */         IpFilterEditor.this.range.checkValid();
/* 131 */         if (IpFilterEditor.this.range.isValid()) {
/* 132 */           ok.setEnabled(true);
/*     */         } else {
/* 134 */           ok.setEnabled(false);
/*     */         }
/*     */       }
/*     */     });
/* 138 */     if (this.range != null) {
/* 139 */       textDescription.setText(this.range.getDescription());
/* 140 */       textStartIp.setText(this.range.getStartIp());
/* 141 */       textEndIp.setText(this.range.getEndIp());
/*     */     }
/*     */     
/* 144 */     shell.pack();
/* 145 */     Utils.centerWindowRelativeTo(shell, parent);
/* 146 */     shell.open();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/IpFilterEditor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */