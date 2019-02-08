/*     */ package org.gudy.azureus2.ui.swt.auth;
/*     */ 
/*     */ import java.security.Principal;
/*     */ import java.security.cert.X509Certificate;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.security.SECertificateListener;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CertificateTrustWindow
/*     */   implements SECertificateListener
/*     */ {
/*     */   public CertificateTrustWindow()
/*     */   {
/*  57 */     SESecurityManager.addCertificateListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean trustCertificate(String _resource, final X509Certificate cert)
/*     */   {
/*  65 */     final Display display = SWTThread.getInstance().getDisplay();
/*     */     
/*  67 */     if (display.isDisposed())
/*     */     {
/*  69 */       return false;
/*     */     }
/*     */     
/*  72 */     TOTorrent torrent = TorrentUtils.getTLSTorrent();
/*     */     
/*     */     String resource;
/*     */     final String resource;
/*  76 */     if (torrent != null)
/*     */     {
/*     */ 
/*  79 */       resource = TorrentUtils.getLocalisedName(torrent) + "\n" + _resource;
/*     */     }
/*     */     else {
/*  82 */       resource = _resource;
/*     */     }
/*     */     
/*  85 */     final trustDialog[] dialog = new trustDialog[1];
/*     */     try
/*     */     {
/*  88 */       Utils.execSWTThread(new AERunnable()
/*     */       {
/*     */ 
/*     */         public void runSupport() {
/*  92 */           dialog[0] = new CertificateTrustWindow.trustDialog(display, resource, cert); } }, false);
/*     */ 
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/*  97 */       Debug.printStackTrace(e);
/*     */       
/*  99 */       return false;
/*     */     }
/*     */     
/* 102 */     return dialog[0].getTrusted();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class trustDialog
/*     */   {
/*     */     protected Shell shell;
/*     */     
/*     */ 
/*     */     protected boolean trusted;
/*     */     
/*     */ 
/*     */ 
/*     */     protected trustDialog(Display display, String resource, X509Certificate cert)
/*     */     {
/* 118 */       if (display.isDisposed())
/*     */       {
/* 120 */         return;
/*     */       }
/*     */       
/* 123 */       this.shell = ShellFactory.createMainShell(67680);
/*     */       
/* 125 */       Utils.setShellIcon(this.shell);
/* 126 */       this.shell.setText(MessageText.getString("security.certtruster.title"));
/*     */       
/* 128 */       GridLayout layout = new GridLayout();
/* 129 */       layout.numColumns = 3;
/*     */       
/* 131 */       this.shell.setLayout(layout);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 137 */       Label info_label = new Label(this.shell, 0);
/* 138 */       Messages.setLanguageText(info_label, "security.certtruster.intro");
/* 139 */       GridData gridData = new GridData(1808);
/* 140 */       gridData.horizontalSpan = 3;
/* 141 */       Utils.setLayoutData(info_label, gridData);
/*     */       
/*     */ 
/*     */ 
/* 145 */       Label resource_label = new Label(this.shell, 0);
/* 146 */       Messages.setLanguageText(resource_label, "security.certtruster.resource");
/* 147 */       gridData = new GridData(1808);
/* 148 */       gridData.horizontalSpan = 1;
/* 149 */       Utils.setLayoutData(resource_label, gridData);
/*     */       
/* 151 */       Label resource_value = new Label(this.shell, 64);
/* 152 */       resource_value.setText(resource.replaceAll("&", "&&"));
/* 153 */       gridData = new GridData(1808);
/* 154 */       gridData.horizontalSpan = 2;
/* 155 */       Utils.setLayoutData(resource_value, gridData);
/*     */       
/*     */ 
/*     */ 
/* 159 */       Label issued_by_label = new Label(this.shell, 0);
/* 160 */       Messages.setLanguageText(issued_by_label, "security.certtruster.issuedby");
/* 161 */       gridData = new GridData(1808);
/* 162 */       gridData.horizontalSpan = 1;
/* 163 */       Utils.setLayoutData(issued_by_label, gridData);
/*     */       
/* 165 */       Label issued_by_value = new Label(this.shell, 0);
/* 166 */       issued_by_value.setText(extractCN(cert.getIssuerDN().getName()).replaceAll("&", "&&"));
/* 167 */       gridData = new GridData(1808);
/* 168 */       gridData.horizontalSpan = 2;
/* 169 */       Utils.setLayoutData(issued_by_value, gridData);
/*     */       
/*     */ 
/*     */ 
/* 173 */       Label issued_to_label = new Label(this.shell, 0);
/* 174 */       Messages.setLanguageText(issued_to_label, "security.certtruster.issuedto");
/* 175 */       gridData = new GridData(1808);
/* 176 */       gridData.horizontalSpan = 1;
/* 177 */       Utils.setLayoutData(issued_to_label, gridData);
/*     */       
/* 179 */       Label issued_to_value = new Label(this.shell, 0);
/* 180 */       issued_to_value.setText(extractCN(cert.getSubjectDN().getName()).replaceAll("&", "&&"));
/* 181 */       gridData = new GridData(1808);
/* 182 */       gridData.horizontalSpan = 2;
/* 183 */       Utils.setLayoutData(issued_to_value, gridData);
/*     */       
/*     */ 
/*     */ 
/* 187 */       Label prompt_label = new Label(this.shell, 0);
/* 188 */       Messages.setLanguageText(prompt_label, "security.certtruster.prompt");
/* 189 */       gridData = new GridData(1808);
/* 190 */       gridData.horizontalSpan = 3;
/* 191 */       Utils.setLayoutData(prompt_label, gridData);
/*     */       
/*     */ 
/*     */ 
/* 195 */       Label labelSeparator = new Label(this.shell, 258);
/* 196 */       gridData = new GridData(768);
/* 197 */       gridData.horizontalSpan = 3;
/* 198 */       Utils.setLayoutData(labelSeparator, gridData);
/*     */       
/*     */ 
/*     */ 
/* 202 */       new Label(this.shell, 0);
/*     */       
/* 204 */       Composite comp = new Composite(this.shell, 0);
/* 205 */       gridData = new GridData(896);
/* 206 */       gridData.grabExcessHorizontalSpace = true;
/* 207 */       gridData.horizontalSpan = 2;
/* 208 */       Utils.setLayoutData(comp, gridData);
/* 209 */       GridLayout layoutButtons = new GridLayout();
/* 210 */       layoutButtons.numColumns = 2;
/* 211 */       comp.setLayout(layoutButtons);
/*     */       
/*     */ 
/*     */ 
/* 215 */       Button bYes = new Button(comp, 8);
/* 216 */       bYes.setText(MessageText.getString("security.certtruster.yes"));
/* 217 */       gridData = new GridData(896);
/* 218 */       gridData.grabExcessHorizontalSpace = true;
/* 219 */       gridData.widthHint = 70;
/* 220 */       Utils.setLayoutData(bYes, gridData);
/* 221 */       bYes.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 223 */           CertificateTrustWindow.trustDialog.this.close(true);
/*     */         }
/*     */         
/* 226 */       });
/* 227 */       Button bNo = new Button(comp, 8);
/* 228 */       bNo.setText(MessageText.getString("security.certtruster.no"));
/* 229 */       gridData = new GridData(128);
/* 230 */       gridData.grabExcessHorizontalSpace = false;
/* 231 */       gridData.widthHint = 70;
/* 232 */       Utils.setLayoutData(bNo, gridData);
/* 233 */       bNo.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 235 */           CertificateTrustWindow.trustDialog.this.close(false);
/*     */         }
/*     */         
/* 238 */       });
/* 239 */       this.shell.setDefaultButton(bYes);
/*     */       
/* 241 */       this.shell.addListener(31, new Listener() {
/*     */         public void handleEvent(Event e) {
/* 243 */           if (e.character == '\033') {
/* 244 */             CertificateTrustWindow.trustDialog.this.close(false);
/*     */           }
/*     */           
/*     */         }
/*     */         
/* 249 */       });
/* 250 */       this.shell.pack();
/*     */       
/* 252 */       Utils.centreWindow(this.shell);
/*     */       
/* 254 */       this.shell.open();
/*     */       
/* 256 */       while (!this.shell.isDisposed()) {
/* 257 */         if (!this.shell.getDisplay().readAndDispatch()) {
/* 258 */           this.shell.getDisplay().sleep();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void close(boolean ok)
/*     */     {
/* 267 */       this.trusted = ok;
/*     */       
/* 269 */       this.shell.dispose();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected String extractCN(String dn)
/*     */     {
/* 276 */       int p1 = dn.indexOf("CN=");
/*     */       
/* 278 */       if (p1 == -1) {
/* 279 */         return dn;
/*     */       }
/*     */       
/* 282 */       int p2 = dn.indexOf(",", p1);
/*     */       
/* 284 */       if (p2 == -1)
/*     */       {
/* 286 */         return dn.substring(p1 + 3).trim();
/*     */       }
/*     */       
/* 289 */       return dn.substring(p1 + 3, p2).trim();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean getTrusted()
/*     */     {
/* 295 */       return this.trusted;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/auth/CertificateTrustWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */