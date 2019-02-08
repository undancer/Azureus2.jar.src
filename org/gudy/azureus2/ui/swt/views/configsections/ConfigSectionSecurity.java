/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.core.security.CryptoHandler;
/*     */ import com.aelitis.azureus.core.security.CryptoManager;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerFactory;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerKeyListener;
/*     */ import com.aelitis.azureus.core.security.CryptoManagerPasswordException;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.LineNumberReader;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.dnd.Clipboard;
/*     */ import org.eclipse.swt.dnd.TextTransfer;
/*     */ import org.eclipse.swt.dnd.Transfer;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.DirectoryDialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.FileDialog;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.security.SESecurityManager;
/*     */ import org.gudy.azureus2.core3.util.Base32;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.auth.CertificateCreatorWindow;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
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
/*     */ public class ConfigSectionSecurity
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  68 */     return "root";
/*     */   }
/*     */   
/*     */ 
/*     */   public String configSectionGetName()
/*     */   {
/*  74 */     return "security";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */ 
/*     */ 
/*     */   public void configSectionDelete()
/*     */   {
/*  85 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  86 */     imageLoader.releaseImage("openFolderButton");
/*     */   }
/*     */   
/*     */   public int maxUserMode() {
/*  90 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(final Composite parent)
/*     */   {
/*  97 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*  99 */     List<Button> buttons = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/* 103 */     Composite gSecurity = new Composite(parent, 0);
/* 104 */     GridData gridData = new GridData(272);
/* 105 */     gSecurity.setLayoutData(gridData);
/* 106 */     GridLayout layout = new GridLayout();
/* 107 */     layout.numColumns = 3;
/* 108 */     gSecurity.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 112 */     Label cert_label = new Label(gSecurity, 0);
/* 113 */     Messages.setLanguageText(cert_label, "ConfigView.section.tracker.createcert");
/*     */     
/* 115 */     Button cert_button = new Button(gSecurity, 8);
/* 116 */     buttons.add(cert_button);
/*     */     
/* 118 */     Messages.setLanguageText(cert_button, "ConfigView.section.tracker.createbutton");
/*     */     
/* 120 */     cert_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 126 */         new CertificateCreatorWindow();
/*     */       }
/*     */       
/* 129 */     });
/* 130 */     new Label(gSecurity, 0);
/*     */     
/*     */ 
/*     */ 
/* 134 */     Label reset_certs_label = new Label(gSecurity, 0);
/* 135 */     Messages.setLanguageText(reset_certs_label, "ConfigView.section.security.resetcerts");
/*     */     
/* 137 */     Button reset_certs_button = new Button(gSecurity, 8);
/* 138 */     buttons.add(reset_certs_button);
/*     */     
/* 140 */     Messages.setLanguageText(reset_certs_button, "Button.reset");
/*     */     
/* 142 */     reset_certs_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 148 */         MessageBoxShell mb = new MessageBoxShell(296, MessageText.getString("ConfigView.section.security.resetcerts.warning.title"), MessageText.getString("ConfigView.section.security.resetcerts.warning.msg"));
/*     */         
/*     */ 
/*     */ 
/* 152 */         mb.setDefaultButtonUsingStyle(256);
/* 153 */         mb.setParent(parent.getShell());
/*     */         
/* 155 */         mb.open(new UserPrompterResultListener() {
/*     */           public void prompterClosed(int returnVal) {
/* 157 */             if (returnVal != 32) {
/* 158 */               return;
/*     */             }
/*     */             
/* 161 */             if (SESecurityManager.resetTrustStore(false))
/*     */             {
/* 163 */               MessageBoxShell mb = new MessageBoxShell(34, MessageText.getString("ConfigView.section.security.restart.title"), MessageText.getString("ConfigView.section.security.restart.msg"));
/*     */               
/*     */ 
/*     */ 
/* 167 */               mb.setParent(ConfigSectionSecurity.2.this.val$parent.getShell());
/* 168 */               mb.open(null);
/*     */               
/*     */ 
/* 171 */               UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*     */               
/* 173 */               if (uiFunctions != null)
/*     */               {
/* 175 */                 uiFunctions.dispose(true, false);
/*     */               }
/*     */             }
/*     */             else
/*     */             {
/* 180 */               MessageBoxShell mb = new MessageBoxShell(33, MessageText.getString("ConfigView.section.security.resetcerts.error.title"), MessageText.getString("ConfigView.section.security.resetcerts.error.msg"));
/*     */               
/*     */ 
/*     */ 
/* 184 */               mb.setParent(ConfigSectionSecurity.2.this.val$parent.getShell());
/* 185 */               mb.open(null);
/*     */             }
/*     */             
/*     */           }
/*     */           
/*     */         });
/*     */       }
/* 192 */     });
/* 193 */     reset_certs_button.setEnabled(SESecurityManager.resetTrustStore(true));
/*     */     
/* 195 */     new Label(gSecurity, 0);
/*     */     
/*     */ 
/*     */ 
/* 199 */     gridData = new GridData();
/* 200 */     gridData.horizontalSpan = 3;
/* 201 */     new BooleanParameter(gSecurity, "security.cert.auto.install", "security.cert.auto.install").setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 208 */     Label info_label = new Label(gSecurity, 64);
/* 209 */     Messages.setLanguageText(info_label, "ConfigView.section.security.toolsinfo");
/* 210 */     info_label.setLayoutData(Utils.getWrappableLabelGridData(3, 0));
/*     */     
/*     */ 
/*     */ 
/* 214 */     Label lStatsPath = new Label(gSecurity, 0);
/*     */     
/* 216 */     Messages.setLanguageText(lStatsPath, "ConfigView.section.security.toolsdir");
/*     */     
/* 218 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 219 */     Image imgOpenFolder = imageLoader.getImage("openFolderButton");
/*     */     
/* 221 */     gridData = new GridData();
/*     */     
/* 223 */     gridData.widthHint = 150;
/*     */     
/* 225 */     final StringParameter pathParameter = new StringParameter(gSecurity, "Security.JAR.tools.dir", "");
/*     */     
/* 227 */     pathParameter.setLayoutData(gridData);
/*     */     
/* 229 */     Button browse = new Button(gSecurity, 8);
/*     */     
/* 231 */     browse.setImage(imgOpenFolder);
/*     */     
/* 233 */     imgOpenFolder.setBackground(browse.getBackground());
/*     */     
/* 235 */     browse.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/*     */     
/* 237 */     browse.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 239 */         DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), 65536);
/*     */         
/* 241 */         dialog.setFilterPath(pathParameter.getValue());
/*     */         
/* 243 */         dialog.setText(MessageText.getString("ConfigView.section.security.choosetoolssavedir"));
/*     */         
/* 245 */         String path = dialog.open();
/*     */         
/* 247 */         if (path != null) {
/* 248 */           pathParameter.setValue(path);
/*     */ 
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 255 */     });
/* 256 */     Label pw_label = new Label(gSecurity, 0);
/* 257 */     Messages.setLanguageText(pw_label, "ConfigView.section.security.clearpasswords");
/*     */     
/* 259 */     Button pw_button = new Button(gSecurity, 8);
/* 260 */     buttons.add(pw_button);
/*     */     
/* 262 */     Messages.setLanguageText(pw_button, "ConfigView.section.security.clearpasswords.button");
/*     */     
/* 264 */     pw_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 270 */         SESecurityManager.clearPasswords();
/*     */         
/* 272 */         CryptoManagerFactory.getSingleton().clearPasswords();
/*     */       }
/*     */       
/* 275 */     });
/* 276 */     new Label(gSecurity, 0);
/*     */     
/* 278 */     if (userMode >= 2)
/*     */     {
/* 280 */       final CryptoManager crypt_man = CryptoManagerFactory.getSingleton();
/*     */       
/* 282 */       final Group crypto_group = new Group(gSecurity, 0);
/* 283 */       gridData = new GridData(784);
/* 284 */       gridData.horizontalSpan = 3;
/* 285 */       crypto_group.setLayoutData(gridData);
/* 286 */       layout = new GridLayout();
/* 287 */       layout.numColumns = 3;
/* 288 */       crypto_group.setLayout(layout);
/*     */       
/* 290 */       Messages.setLanguageText(crypto_group, "ConfigView.section.security.group.crypto");
/*     */       
/*     */ 
/*     */ 
/* 294 */       Label linkLabel = new Label(crypto_group, 0);
/* 295 */       linkLabel.setText(MessageText.getString("ConfigView.label.please.visit.here"));
/* 296 */       linkLabel.setData("http://wiki.vuze.com/w/Public_Private_Keys");
/* 297 */       linkLabel.setCursor(linkLabel.getDisplay().getSystemCursor(21));
/* 298 */       linkLabel.setForeground(Colors.blue);
/* 299 */       gridData = new GridData();
/* 300 */       gridData.horizontalSpan = 3;
/* 301 */       linkLabel.setLayoutData(gridData);
/* 302 */       linkLabel.addMouseListener(new MouseAdapter() {
/*     */         public void mouseDoubleClick(MouseEvent arg0) {
/* 304 */           Utils.launch((String)((Label)arg0.widget).getData());
/*     */         }
/*     */         
/*     */         public void mouseDown(MouseEvent arg0) {
/* 308 */           Utils.launch((String)((Label)arg0.widget).getData());
/*     */         }
/* 310 */       });
/* 311 */       ClipboardCopy.addCopyToClipMenu(linkLabel);
/*     */       
/*     */ 
/*     */ 
/* 315 */       byte[] public_key = crypt_man.getECCHandler().peekPublicKey();
/*     */       
/* 317 */       Label public_key_label = new Label(crypto_group, 0);
/* 318 */       Messages.setLanguageText(public_key_label, "ConfigView.section.security.publickey");
/*     */       
/* 320 */       final Label public_key_value = new Label(crypto_group, 0);
/*     */       
/* 322 */       if (public_key == null)
/*     */       {
/* 324 */         Messages.setLanguageText(public_key_value, "ConfigView.section.security.publickey.undef");
/*     */       }
/*     */       else
/*     */       {
/* 328 */         public_key_value.setText(Base32.encode(public_key));
/*     */       }
/*     */       
/* 331 */       Messages.setLanguageText(public_key_value, "ConfigView.copy.to.clipboard.tooltip", true);
/*     */       
/* 333 */       public_key_value.setCursor(public_key_value.getDisplay().getSystemCursor(21));
/* 334 */       public_key_value.setForeground(Colors.blue);
/* 335 */       public_key_value.addMouseListener(new MouseAdapter() {
/*     */         public void mouseDoubleClick(MouseEvent arg0) {
/* 337 */           copyToClipboard();
/*     */         }
/*     */         
/* 340 */         public void mouseDown(MouseEvent arg0) { copyToClipboard(); }
/*     */         
/*     */ 
/*     */         protected void copyToClipboard()
/*     */         {
/* 345 */           new Clipboard(parent.getDisplay()).setContents(new Object[] { public_key_value.getText() }, new Transfer[] { TextTransfer.getInstance() });
/*     */         }
/*     */         
/* 348 */       });
/* 349 */       crypt_man.addKeyListener(new CryptoManagerKeyListener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void keyChanged(final CryptoHandler handler)
/*     */         {
/*     */ 
/* 356 */           final CryptoManagerKeyListener me = this;
/*     */           
/* 358 */           Utils.execSWTThread(new Runnable()
/*     */           {
/*     */ 
/*     */             public void run()
/*     */             {
/*     */ 
/* 364 */               if (ConfigSectionSecurity.7.this.val$public_key_value.isDisposed())
/*     */               {
/* 366 */                 ConfigSectionSecurity.7.this.val$crypt_man.removeKeyListener(me);
/*     */ 
/*     */               }
/* 369 */               else if (handler.getType() == 1)
/*     */               {
/* 371 */                 byte[] public_key = handler.peekPublicKey();
/*     */                 
/* 373 */                 if (public_key == null)
/*     */                 {
/* 375 */                   Messages.setLanguageText(ConfigSectionSecurity.7.this.val$public_key_value, "ConfigView.section.security.publickey.undef");
/*     */                 }
/*     */                 else
/*     */                 {
/* 379 */                   ConfigSectionSecurity.7.this.val$public_key_value.setText(Base32.encode(public_key));
/*     */                 }
/*     */                 
/* 382 */                 ConfigSectionSecurity.7.this.val$crypto_group.layout();
/*     */               }
/*     */             }
/*     */           });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void keyLockStatusChanged(CryptoHandler handler) {}
/* 395 */       });
/* 396 */       new Label(crypto_group, 0);
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
/* 486 */       Label reset_key_label = new Label(crypto_group, 0);
/* 487 */       Messages.setLanguageText(reset_key_label, "ConfigView.section.security.resetkey");
/*     */       
/* 489 */       Button reset_key_button = new Button(crypto_group, 8);
/* 490 */       buttons.add(reset_key_button);
/*     */       
/* 492 */       Messages.setLanguageText(reset_key_button, "ConfigView.section.security.clearpasswords.button");
/*     */       
/* 494 */       reset_key_button.addListener(13, new Listener()
/*     */       {
/*     */ 
/*     */         public void handleEvent(Event event)
/*     */         {
/*     */ 
/* 500 */           MessageBoxShell mb = new MessageBoxShell(296, MessageText.getString("ConfigView.section.security.resetkey.warning.title"), MessageText.getString("ConfigView.section.security.resetkey.warning"));
/*     */           
/*     */ 
/*     */ 
/* 504 */           mb.setDefaultButtonUsingStyle(256);
/* 505 */           mb.setParent(parent.getShell());
/*     */           
/* 507 */           mb.open(new UserPrompterResultListener() {
/*     */             public void prompterClosed(int returnVal) {
/* 509 */               if (returnVal != 32) {
/* 510 */                 return;
/*     */               }
/*     */               try
/*     */               {
/* 514 */                 ConfigSectionSecurity.8.this.val$crypt_man.getECCHandler().resetKeys("Manual key reset");
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 518 */                 MessageBoxShell mb = new MessageBoxShell(33, MessageText.getString("ConfigView.section.security.resetkey.error.title"), ConfigSectionSecurity.this.getError(e));
/*     */                 
/*     */ 
/*     */ 
/* 522 */                 mb.setParent(ConfigSectionSecurity.8.this.val$parent.getShell());
/* 523 */                 mb.open(null);
/*     */               }
/*     */               
/*     */             }
/*     */           });
/*     */         }
/* 529 */       });
/* 530 */       new Label(crypto_group, 0);
/*     */       
/*     */ 
/*     */ 
/* 534 */       Label priv_key_label = new Label(crypto_group, 0);
/* 535 */       Messages.setLanguageText(priv_key_label, "ConfigView.section.security.unlockkey");
/*     */       
/* 537 */       Button priv_key_button = new Button(crypto_group, 8);
/* 538 */       buttons.add(priv_key_button);
/*     */       
/* 540 */       Messages.setLanguageText(priv_key_button, "ConfigView.section.security.unlockkey.button");
/*     */       
/* 542 */       priv_key_button.addListener(13, new Listener()
/*     */       {
/*     */ 
/*     */         public void handleEvent(Event event)
/*     */         {
/*     */           try
/*     */           {
/* 549 */             crypt_man.getECCHandler().getEncryptedPrivateKey("Manual unlock");
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 553 */             MessageBoxShell mb = new MessageBoxShell(33, MessageText.getString("ConfigView.section.security.resetkey.error.title"), ConfigSectionSecurity.this.getError(e));
/*     */             
/*     */ 
/*     */ 
/* 557 */             mb.setParent(parent.getShell());
/* 558 */             mb.open(null);
/*     */           }
/*     */           
/*     */         }
/*     */         
/* 563 */       });
/* 564 */       new Label(crypto_group, 0);
/*     */       
/*     */ 
/*     */ 
/* 568 */       Label backup_keys_label = new Label(crypto_group, 0);
/* 569 */       Messages.setLanguageText(backup_keys_label, "ConfigView.section.security.backupkeys");
/*     */       
/* 571 */       final Button backup_keys_button = new Button(crypto_group, 8);
/* 572 */       buttons.add(backup_keys_button);
/*     */       
/* 574 */       Messages.setLanguageText(backup_keys_button, "ConfigView.section.security.backupkeys.button");
/*     */       
/* 576 */       backup_keys_button.addListener(13, new Listener()
/*     */       {
/*     */ 
/*     */         public void handleEvent(Event event)
/*     */         {
/*     */ 
/* 582 */           FileDialog dialog = new FileDialog(backup_keys_button.getShell(), 65536);
/*     */           
/* 584 */           String target = dialog.open();
/*     */           
/* 586 */           if (target != null) {
/*     */             try
/*     */             {
/* 589 */               String keys = crypt_man.getECCHandler().exportKeys();
/*     */               
/* 591 */               PrintWriter pw = new PrintWriter(new FileWriter(target));
/*     */               
/* 593 */               pw.println(keys);
/*     */               
/* 595 */               pw.close();
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 599 */               MessageBoxShell mb = new MessageBoxShell(33, MessageText.getString("ConfigView.section.security.op.error.title"), MessageText.getString("ConfigView.section.security.op.error", new String[] { ConfigSectionSecurity.this.getError(e) }));
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 604 */               mb.setParent(parent.getShell());
/* 605 */               mb.open(null);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/* 610 */       });
/* 611 */       new Label(crypto_group, 0);
/*     */       
/*     */ 
/*     */ 
/* 615 */       Label restore_keys_label = new Label(crypto_group, 0);
/* 616 */       Messages.setLanguageText(restore_keys_label, "ConfigView.section.security.restorekeys");
/*     */       
/* 618 */       Button restore_keys_button = new Button(crypto_group, 8);
/* 619 */       buttons.add(restore_keys_button);
/*     */       
/* 621 */       Messages.setLanguageText(restore_keys_button, "ConfigView.section.security.restorekeys.button");
/*     */       
/* 623 */       restore_keys_button.addListener(13, new Listener()
/*     */       {
/*     */ 
/*     */         public void handleEvent(Event event)
/*     */         {
/*     */ 
/* 629 */           FileDialog dialog = new FileDialog(backup_keys_button.getShell(), 65536);
/*     */           
/* 631 */           String target = dialog.open();
/*     */           
/* 633 */           if (target != null) {
/*     */             try
/*     */             {
/* 636 */               LineNumberReader reader = new LineNumberReader(new FileReader(target));
/*     */               
/* 638 */               String str = "";
/*     */               try
/*     */               {
/*     */                 for (;;)
/*     */                 {
/* 643 */                   String line = reader.readLine();
/*     */                   
/* 645 */                   if (line == null) {
/*     */                     break;
/*     */                   }
/*     */                   
/*     */ 
/* 650 */                   str = str + line + "\r\n";
/*     */                 }
/*     */               }
/*     */               finally {
/* 654 */                 reader.close();
/*     */               }
/*     */               
/* 657 */               boolean restart = crypt_man.getECCHandler().importKeys(str);
/*     */               
/* 659 */               if (restart)
/*     */               {
/* 661 */                 MessageBoxShell mb = new MessageBoxShell(34, MessageText.getString("ConfigView.section.security.restart.title"), MessageText.getString("ConfigView.section.security.restart.msg"));
/*     */                 
/*     */ 
/*     */ 
/* 665 */                 mb.setParent(parent.getShell());
/* 666 */                 mb.open(null);
/*     */                 
/*     */ 
/* 669 */                 UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*     */                 
/* 671 */                 if (uiFunctions != null)
/*     */                 {
/* 673 */                   uiFunctions.dispose(true, false);
/*     */                 }
/*     */               }
/*     */             }
/*     */             catch (Throwable e) {
/* 678 */               MessageBoxShell mb = new MessageBoxShell(33, MessageText.getString("ConfigView.section.security.op.error.title"), MessageText.getString("ConfigView.section.security.op.error", new String[] { ConfigSectionSecurity.this.getError(e) }));
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 683 */               mb.setParent(parent.getShell());
/* 684 */               mb.open(null);
/*     */             }
/*     */             
/*     */           }
/*     */         }
/* 689 */       });
/* 690 */       new Label(crypto_group, 0);
/*     */     }
/*     */     
/* 693 */     Utils.makeButtonsEqualWidth(buttons);
/*     */     
/* 695 */     return gSecurity;
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getError(Throwable e)
/*     */   {
/*     */     String error;
/*     */     
/*     */     String error;
/* 704 */     if ((e instanceof CryptoManagerPasswordException)) {
/*     */       String error;
/* 706 */       if (((CryptoManagerPasswordException)e).wasIncorrect())
/*     */       {
/* 708 */         error = MessageText.getString("ConfigView.section.security.unlockkey.error");
/*     */       }
/*     */       else
/*     */       {
/* 712 */         CryptoManager crypto_man = CryptoManagerFactory.getSingleton();
/* 713 */         CryptoHandler ecc_handler = crypto_man.getECCHandler();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 721 */         error = MessageText.getString("ConfigView.section.security.nopw");
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 726 */       error = MessageText.getString("ConfigView.section.security.resetkey.error") + ": " + Debug.getNestedExceptionMessage(e);
/*     */     }
/*     */     
/* 729 */     return error;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionSecurity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */