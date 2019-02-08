/*     */ package org.gudy.azureus2.ui.swt.auth;
/*     */ 
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
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
/*     */ public class CertificateCreatorWindow
/*     */ {
/*     */   public CertificateCreatorWindow()
/*     */   {
/*  51 */     createCertificate();
/*     */   }
/*     */   
/*     */ 
/*     */   public void createCertificate()
/*     */   {
/*  57 */     final Display display = SWTThread.getInstance().getDisplay();
/*     */     
/*  59 */     if (display.isDisposed())
/*     */     {
/*  61 */       return;
/*     */     }
/*     */     try
/*     */     {
/*  65 */       display.asyncExec(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport()
/*     */         {
/*     */ 
/*  71 */           new CertificateCreatorWindow.createDialog(display);
/*     */         }
/*     */       });
/*     */     }
/*     */     catch (Throwable e) {
/*  76 */       Debug.printStackTrace(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class createDialog
/*     */   {
/*     */     protected Shell shell;
/*     */     
/*     */ 
/*     */     protected createDialog(Display display)
/*     */     {
/*  89 */       if (display.isDisposed())
/*     */       {
/*  91 */         return;
/*     */       }
/*     */       
/*  94 */       this.shell = ShellFactory.createMainShell(67680);
/*     */       
/*  96 */       Utils.setShellIcon(this.shell);
/*  97 */       Messages.setLanguageText(this.shell, "security.certcreate.title");
/*     */       
/*  99 */       GridLayout layout = new GridLayout();
/* 100 */       layout.numColumns = 3;
/*     */       
/* 102 */       this.shell.setLayout(layout);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 108 */       Label info_label = new Label(this.shell, 0);
/* 109 */       Messages.setLanguageText(info_label, "security.certcreate.intro");
/* 110 */       GridData gridData = new GridData(1808);
/* 111 */       gridData.horizontalSpan = 3;
/* 112 */       Utils.setLayoutData(info_label, gridData);
/*     */       
/*     */ 
/*     */ 
/* 116 */       Label alias_label = new Label(this.shell, 0);
/* 117 */       Messages.setLanguageText(alias_label, "security.certcreate.alias");
/* 118 */       gridData = new GridData(1808);
/* 119 */       gridData.horizontalSpan = 1;
/* 120 */       Utils.setLayoutData(alias_label, gridData);
/*     */       
/* 122 */       final Text alias_field = new Text(this.shell, 2048);
/*     */       
/* 124 */       alias_field.setText("Azureus");
/*     */       
/* 126 */       gridData = new GridData(1808);
/* 127 */       gridData.horizontalSpan = 2;
/* 128 */       Utils.setLayoutData(alias_field, gridData);
/*     */       
/*     */ 
/*     */ 
/* 132 */       Label strength_label = new Label(this.shell, 0);
/* 133 */       Messages.setLanguageText(strength_label, "security.certcreate.strength");
/* 134 */       gridData = new GridData(1808);
/* 135 */       gridData.horizontalSpan = 1;
/* 136 */       Utils.setLayoutData(strength_label, gridData);
/*     */       
/* 138 */       final Combo strength_combo = new Combo(this.shell, 12);
/*     */       
/* 140 */       final int[] strengths = { 512, 1024, 1536, 2048 };
/*     */       
/* 142 */       for (int i = 0; i < strengths.length; i++)
/*     */       {
/* 144 */         strength_combo.add("" + strengths[i]);
/*     */       }
/*     */       
/* 147 */       strength_combo.select(1);
/*     */       
/* 149 */       new Label(this.shell, 0);
/*     */       
/*     */ 
/*     */ 
/* 153 */       String[] field_names = { "security.certcreate.firstlastname", "security.certcreate.orgunit", "security.certcreate.org", "security.certcreate.city", "security.certcreate.state", "security.certcreate.country" };
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 162 */       final String[] field_rns = { "CN", "OU", "O", "L", "ST", "C" };
/*     */       
/* 164 */       final Text[] fields = new Text[field_names.length];
/*     */       
/* 166 */       for (int i = 0; i < fields.length; i++)
/*     */       {
/* 168 */         Label resource_label = new Label(this.shell, 0);
/* 169 */         Messages.setLanguageText(resource_label, field_names[i]);
/* 170 */         gridData = new GridData(1808);
/* 171 */         gridData.horizontalSpan = 1;
/* 172 */         Utils.setLayoutData(resource_label, gridData);
/*     */         
/* 174 */         Text field = fields[i] = new Text(this.shell, 2048);
/* 175 */         gridData = new GridData(1808);
/* 176 */         gridData.horizontalSpan = 2;
/* 177 */         Utils.setLayoutData(field, gridData);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 182 */       Label labelSeparator = new Label(this.shell, 258);
/* 183 */       gridData = new GridData(768);
/* 184 */       gridData.horizontalSpan = 3;
/* 185 */       Utils.setLayoutData(labelSeparator, gridData);
/*     */       
/*     */ 
/*     */ 
/* 189 */       new Label(this.shell, 0);
/*     */       
/* 191 */       Composite comp = new Composite(this.shell, 0);
/* 192 */       gridData = new GridData(896);
/* 193 */       gridData.grabExcessHorizontalSpace = true;
/* 194 */       gridData.horizontalSpan = 2;
/* 195 */       Utils.setLayoutData(comp, gridData);
/* 196 */       GridLayout layoutButtons = new GridLayout();
/* 197 */       layoutButtons.numColumns = 2;
/* 198 */       comp.setLayout(layoutButtons);
/*     */       
/*     */ 
/*     */ 
/* 202 */       Button bYes = new Button(comp, 8);
/* 203 */       Messages.setLanguageText(bYes, "security.certcreate.ok");
/* 204 */       gridData = new GridData(896);
/* 205 */       gridData.grabExcessHorizontalSpace = true;
/* 206 */       gridData.widthHint = 70;
/* 207 */       Utils.setLayoutData(bYes, gridData);
/* 208 */       bYes.addListener(13, new Listener()
/*     */       {
/*     */         public void handleEvent(Event e) {
/* 211 */           String alias = alias_field.getText().trim();
/*     */           
/* 213 */           int strength = strengths[strength_combo.getSelectionIndex()];
/*     */           
/* 215 */           String dn = "";
/*     */           
/* 217 */           for (int i = 0; i < fields.length; i++)
/*     */           {
/* 219 */             String rn = fields[i].getText().trim();
/*     */             
/* 221 */             if (rn.length() == 0)
/*     */             {
/* 223 */               rn = "Unknown";
/*     */             }
/*     */             
/* 226 */             dn = dn + (dn.length() == 0 ? "" : ",") + field_rns[i] + "=" + rn;
/*     */           }
/*     */           try
/*     */           {
/* 230 */             SESecurityManager.createSelfSignedCertificate(alias, dn, strength);
/*     */             
/* 232 */             CertificateCreatorWindow.createDialog.this.close(true);
/*     */             
/* 234 */             Logger.log(new LogAlert(false, 0, MessageText.getString("security.certcreate.createok") + "\n" + alias + ":" + strength + "\n" + dn + "\n" + SystemTime.getCurrentTime()));
/*     */ 
/*     */ 
/*     */           }
/*     */           catch (Throwable f)
/*     */           {
/*     */ 
/*     */ 
/* 242 */             Logger.log(new LogAlert(false, MessageText.getString("security.certcreate.createfail") + "\n" + SystemTime.getCurrentTime(), f));
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */ 
/* 248 */       });
/* 249 */       Button bNo = new Button(comp, 8);
/* 250 */       Messages.setLanguageText(bNo, "security.certcreate.cancel");
/* 251 */       gridData = new GridData(128);
/* 252 */       gridData.grabExcessHorizontalSpace = false;
/* 253 */       gridData.widthHint = 70;
/* 254 */       Utils.setLayoutData(bNo, gridData);
/* 255 */       bNo.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 257 */           CertificateCreatorWindow.createDialog.this.close(false);
/*     */         }
/*     */         
/* 260 */       });
/* 261 */       this.shell.setDefaultButton(bYes);
/*     */       
/* 263 */       this.shell.addListener(31, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 265 */           if (e.character == '\033') {
/* 266 */             CertificateCreatorWindow.createDialog.this.close(false);
/*     */           }
/*     */           
/*     */         }
/*     */         
/* 271 */       });
/* 272 */       this.shell.pack();
/*     */       
/* 274 */       Utils.centreWindow(this.shell);
/*     */       
/* 276 */       this.shell.open();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void close(boolean ok)
/*     */     {
/* 283 */       this.shell.dispose();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/auth/CertificateCreatorWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */