/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.backup.BackupManager;
/*     */ import com.aelitis.azureus.core.backup.BackupManager.BackupListener;
/*     */ import com.aelitis.azureus.core.backup.BackupManagerFactory;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.io.File;
/*     */ import java.util.Date;
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
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.TextViewerWindow;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigSectionBackupRestore
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  65 */     return "root";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  69 */     return "backuprestore";
/*     */   }
/*     */   
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete()
/*     */   {
/*  76 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  77 */     imageLoader.releaseImage("openFolderButton");
/*     */   }
/*     */   
/*     */   public int maxUserMode() {
/*  81 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(final Composite parent)
/*     */   {
/*  89 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  90 */     Image imgOpenFolder = imageLoader.getImage("openFolderButton");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  95 */     Composite cBR = new Composite(parent, 0);
/*     */     
/*  97 */     GridData gridData = new GridData(272);
/*  98 */     cBR.setLayoutData(gridData);
/*  99 */     GridLayout layout = new GridLayout();
/* 100 */     layout.numColumns = 1;
/* 101 */     cBR.setLayout(layout);
/*     */     
/* 103 */     Label info_label = new Label(cBR, 64);
/* 104 */     Messages.setLanguageText(info_label, "ConfigView.section.br.overview");
/* 105 */     gridData = Utils.getWrappableLabelGridData(1, 256);
/*     */     
/* 107 */     info_label.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 112 */     Label linkLabel = new Label(cBR, 0);
/* 113 */     linkLabel.setText(MessageText.getString("ConfigView.label.please.visit.here"));
/* 114 */     linkLabel.setData("http://wiki.vuze.com/w/Backup_And_Restore");
/* 115 */     linkLabel.setCursor(linkLabel.getDisplay().getSystemCursor(21));
/* 116 */     linkLabel.setForeground(Colors.blue);
/* 117 */     gridData = Utils.getWrappableLabelGridData(1, 0);
/* 118 */     linkLabel.setLayoutData(gridData);
/* 119 */     linkLabel.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDoubleClick(MouseEvent arg0) {
/* 121 */         Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/*     */       
/*     */       public void mouseDown(MouseEvent arg0) {
/* 125 */         Utils.launch((String)((Label)arg0.widget).getData());
/*     */       }
/* 127 */     });
/* 128 */     ClipboardCopy.addCopyToClipMenu(linkLabel);
/*     */     
/* 130 */     final BackupManager backup_manager = BackupManagerFactory.getManager(AzureusCoreFactory.getSingleton());
/*     */     
/*     */ 
/*     */ 
/* 134 */     Group gBackup = new Group(cBR, 0);
/* 135 */     Messages.setLanguageText(gBackup, "br.backup");
/* 136 */     layout = new GridLayout(2, false);
/* 137 */     gBackup.setLayout(layout);
/* 138 */     gBackup.setLayoutData(new GridData(768));
/*     */     
/*     */ 
/*     */ 
/* 142 */     Label last_backup_label = new Label(gBackup, 0);
/* 143 */     Messages.setLanguageText(last_backup_label, "br.backup.last.time");
/*     */     
/* 145 */     final Label last_backup_time = new Label(gBackup, 0);
/*     */     
/* 147 */     Label last_backup_error_label = new Label(gBackup, 0);
/* 148 */     Messages.setLanguageText(last_backup_error_label, "br.backup.last.error");
/*     */     
/* 150 */     final Label last_backup_error = new Label(gBackup, 0);
/*     */     
/* 152 */     final Runnable stats_updater = new Runnable()
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/*     */ 
/* 158 */         long backup_time = backup_manager.getLastBackupTime();
/*     */         
/* 160 */         last_backup_time.setText(backup_time == 0L ? "" : String.valueOf(new Date(backup_time)));
/*     */         
/* 162 */         last_backup_error.setText(backup_manager.getLastBackupError());
/*     */       }
/*     */       
/*     */ 
/* 166 */     };
/* 167 */     stats_updater.run();
/*     */     
/*     */ 
/*     */ 
/* 171 */     Label backup_manual_label = new Label(gBackup, 0);
/* 172 */     Messages.setLanguageText(backup_manual_label, "br.backup.manual.info");
/*     */     
/* 174 */     Button backup_button = new Button(gBackup, 8);
/* 175 */     Messages.setLanguageText(backup_button, "br.backup");
/*     */     
/* 177 */     backup_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 183 */         String def_dir = COConfigurationManager.getStringParameter("br.backup.folder.default");
/*     */         
/* 185 */         DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), 65536);
/*     */         
/* 187 */         if (def_dir != null) {
/* 188 */           dialog.setFilterPath(def_dir);
/*     */         }
/*     */         
/* 191 */         dialog.setMessage(MessageText.getString("br.backup.folder.info"));
/* 192 */         dialog.setText(MessageText.getString("br.backup.folder.title"));
/*     */         
/* 194 */         String path = dialog.open();
/*     */         
/* 196 */         if (path != null)
/*     */         {
/* 198 */           COConfigurationManager.setParameter("br.backup.folder.default", path);
/*     */           
/* 200 */           ConfigSectionBackupRestore.this.runBackup(backup_manager, path, stats_updater);
/*     */         }
/*     */         
/*     */       }
/* 204 */     });
/* 205 */     BooleanParameter auto_backup_enable = new BooleanParameter(gBackup, "br.backup.auto.enable", "br.backup.auto.enable");
/* 206 */     gridData = new GridData();
/* 207 */     gridData.horizontalSpan = 2;
/* 208 */     auto_backup_enable.setLayoutData(gridData);
/*     */     
/* 210 */     Composite gDefaultDir = new Composite(gBackup, 0);
/* 211 */     layout = new GridLayout();
/* 212 */     layout.numColumns = 3;
/* 213 */     layout.marginHeight = 2;
/* 214 */     gDefaultDir.setLayout(layout);
/* 215 */     gridData = new GridData(768);
/* 216 */     gridData.horizontalSpan = 2;
/* 217 */     gDefaultDir.setLayoutData(gridData);
/*     */     
/* 219 */     Label lblDefaultDir = new Label(gDefaultDir, 0);
/* 220 */     Messages.setLanguageText(lblDefaultDir, "ConfigView.section.file.defaultdir.ask");
/* 221 */     lblDefaultDir.setLayoutData(new GridData());
/*     */     
/*     */ 
/* 224 */     gridData = new GridData(768);
/* 225 */     final StringParameter pathParameter = new StringParameter(gDefaultDir, "br.backup.auto.dir", "");
/* 226 */     pathParameter.setLayoutData(gridData);
/*     */     
/* 228 */     if (pathParameter.getValue().length() == 0) {
/* 229 */       String def_dir = COConfigurationManager.getStringParameter("br.backup.folder.default");
/*     */       
/* 231 */       pathParameter.setValue(def_dir);
/*     */     }
/*     */     
/* 234 */     Button browse = new Button(gDefaultDir, 8);
/* 235 */     browse.setImage(imgOpenFolder);
/* 236 */     imgOpenFolder.setBackground(browse.getBackground());
/* 237 */     browse.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/*     */     
/* 239 */     browse.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/* 244 */         DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), 65536);
/*     */         
/* 246 */         dialog.setFilterPath(pathParameter.getValue());
/* 247 */         dialog.setMessage(MessageText.getString("br.backup.auto.dir.select"));
/* 248 */         dialog.setText(MessageText.getString("ConfigView.section.file.defaultdir.ask"));
/* 249 */         String path = dialog.open();
/* 250 */         if (path != null) {
/* 251 */           pathParameter.setValue(path);
/*     */           
/* 253 */           COConfigurationManager.setParameter("br.backup.folder.default", path);
/*     */         }
/*     */         
/*     */       }
/*     */       
/* 258 */     });
/* 259 */     Label lbl_backup_days = new Label(gDefaultDir, 0);
/* 260 */     Messages.setLanguageText(lbl_backup_days, "br.backup.auto.everydays");
/*     */     
/* 262 */     IntParameter backup_everydays = new IntParameter(gDefaultDir, "br.backup.auto.everydays", 1, Integer.MAX_VALUE);
/* 263 */     gridData = new GridData();
/* 264 */     gridData.horizontalSpan = 2;
/* 265 */     backup_everydays.setLayoutData(gridData);
/*     */     
/* 267 */     Label lbl_backup_retain = new Label(gDefaultDir, 0);
/* 268 */     Messages.setLanguageText(lbl_backup_retain, "br.backup.auto.retain");
/*     */     
/* 270 */     IntParameter backup_retain = new IntParameter(gDefaultDir, "br.backup.auto.retain", 1, Integer.MAX_VALUE);
/* 271 */     gridData = new GridData();
/* 272 */     gridData.horizontalSpan = 2;
/* 273 */     backup_retain.setLayoutData(gridData);
/*     */     
/* 275 */     BooleanParameter chkNotify = new BooleanParameter(gDefaultDir, "br.backup.notify", "br.backup.notify");
/* 276 */     gridData = new GridData(768);
/* 277 */     gridData.horizontalSpan = 3;
/* 278 */     chkNotify.setLayoutData(gridData);
/*     */     
/*     */ 
/* 281 */     Label backup_auto_label = new Label(gDefaultDir, 0);
/* 282 */     Messages.setLanguageText(backup_auto_label, "br.backup.auto.now");
/*     */     
/* 284 */     Button backup_auto_button = new Button(gDefaultDir, 8);
/* 285 */     Messages.setLanguageText(backup_auto_button, "br.test");
/*     */     
/* 287 */     backup_auto_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 293 */         ConfigSectionBackupRestore.this.runBackup(backup_manager, null, stats_updater);
/*     */       }
/*     */       
/* 296 */     });
/* 297 */     auto_backup_enable.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(gDefaultDir));
/*     */     
/*     */ 
/*     */ 
/* 301 */     Group gRestore = new Group(cBR, 0);
/* 302 */     Messages.setLanguageText(gRestore, "br.restore");
/* 303 */     layout = new GridLayout(2, false);
/* 304 */     gRestore.setLayout(layout);
/* 305 */     gRestore.setLayoutData(new GridData(768));
/*     */     
/* 307 */     Label restore_label = new Label(gRestore, 0);
/* 308 */     Messages.setLanguageText(restore_label, "br.restore.info");
/*     */     
/* 310 */     Button restore_button = new Button(gRestore, 8);
/* 311 */     Messages.setLanguageText(restore_button, "br.restore");
/*     */     
/* 313 */     restore_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 319 */         String def_dir = COConfigurationManager.getStringParameter("br.backup.folder.default");
/*     */         
/* 321 */         DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), 65536);
/*     */         
/* 323 */         if (def_dir != null) {
/* 324 */           dialog.setFilterPath(def_dir);
/*     */         }
/*     */         
/* 327 */         dialog.setMessage(MessageText.getString("br.restore.folder.info"));
/*     */         
/* 329 */         dialog.setText(MessageText.getString("br.restore.folder.title"));
/*     */         
/* 331 */         final String path = dialog.open();
/*     */         
/* 333 */         if (path != null)
/*     */         {
/* 335 */           MessageBoxShell mb = new MessageBoxShell(296, MessageText.getString("br.restore.warning.title"), MessageText.getString("br.restore.warning.info"));
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 340 */           mb.setDefaultButtonUsingStyle(256);
/* 341 */           mb.setParent(parent.getShell());
/*     */           
/* 343 */           mb.open(new UserPrompterResultListener() {
/*     */             public void prompterClosed(int returnVal) {
/* 345 */               if (returnVal != 32) {
/* 346 */                 return;
/*     */               }
/*     */               
/* 349 */               final TextViewerWindow viewer = new TextViewerWindow(MessageText.getString("br.backup.progress"), null, "", true, true);
/*     */               
/*     */ 
/*     */ 
/*     */ 
/* 354 */               viewer.setEditable(false);
/*     */               
/* 356 */               viewer.setOKEnabled(false);
/*     */               
/* 358 */               ConfigSectionBackupRestore.6.this.val$backup_manager.restore(new File(path), new BackupManager.BackupListener()
/*     */               {
/*     */ 
/*     */ 
/*     */                 public boolean reportProgress(String str)
/*     */                 {
/*     */ 
/*     */ 
/* 366 */                   return append(str, false);
/*     */                 }
/*     */                 
/*     */ 
/*     */                 public void reportComplete()
/*     */                 {
/* 372 */                   append("Restore Complete!", true);
/*     */                   
/* 374 */                   Utils.execSWTThread(new AERunnable()
/*     */                   {
/*     */ 
/*     */                     public void runSupport()
/*     */                     {
/*     */ 
/* 380 */                       MessageBoxShell mb = new MessageBoxShell(34, MessageText.getString("ConfigView.section.security.restart.title"), MessageText.getString("ConfigView.section.security.restart.msg"));
/*     */                       
/*     */ 
/*     */ 
/* 384 */                       mb.setParent(ConfigSectionBackupRestore.6.this.val$parent.getShell());
/* 385 */                       mb.open(new UserPrompterResultListener()
/*     */                       {
/*     */ 
/*     */ 
/*     */                         public void prompterClosed(int returnVal)
/*     */                         {
/*     */ 
/* 392 */                           UIFunctionsSWT uiFunctions = UIFunctionsManagerSWT.getUIFunctionsSWT();
/*     */                           
/* 394 */                           if (uiFunctions != null)
/*     */                           {
/* 396 */                             uiFunctions.dispose(true, false);
/*     */                           }
/*     */                         }
/*     */                       });
/*     */                     }
/*     */                   });
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */                 public void reportError(Throwable error)
/*     */                 {
/* 408 */                   append("Restore Failed: " + Debug.getNestedExceptionMessage(error), true);
/*     */                 }
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/*     */                 private boolean append(final String str, final boolean complete)
/*     */                 {
/* 416 */                   if (viewer.isDisposed())
/*     */                   {
/* 418 */                     return false;
/*     */                   }
/*     */                   
/* 421 */                   Utils.execSWTThread(new AERunnable()
/*     */                   {
/*     */ 
/*     */                     public void runSupport()
/*     */                     {
/*     */ 
/* 427 */                       if (str.endsWith("..."))
/*     */                       {
/* 429 */                         ConfigSectionBackupRestore.6.1.1.this.val$viewer.append(str);
/*     */                       }
/*     */                       else
/*     */                       {
/* 433 */                         ConfigSectionBackupRestore.6.1.1.this.val$viewer.append(str + "\r\n");
/*     */                       }
/*     */                       
/* 436 */                       if (complete)
/*     */                       {
/* 438 */                         ConfigSectionBackupRestore.6.1.1.this.val$viewer.setOKEnabled(true);
/*     */                       }
/*     */                       
/*     */                     }
/* 442 */                   });
/* 443 */                   return true;
/*     */                 }
/*     */                 
/* 446 */               });
/* 447 */               viewer.goModal();
/*     */             }
/*     */             
/*     */ 
/*     */           });
/*     */         }
/*     */       }
/* 454 */     });
/* 455 */     return cBR;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void runBackup(BackupManager backup_manager, String path, final Runnable stats_updater)
/*     */   {
/* 465 */     final TextViewerWindow viewer = new TextViewerWindow(MessageText.getString("br.backup.progress"), null, "", true, true);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 470 */     viewer.setEditable(false);
/*     */     
/* 472 */     viewer.setOKEnabled(false);
/*     */     
/* 474 */     BackupManager.BackupListener listener = new BackupManager.BackupListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public boolean reportProgress(String str)
/*     */       {
/*     */ 
/* 481 */         return append(str, false);
/*     */       }
/*     */       
/*     */ 
/*     */       public void reportComplete()
/*     */       {
/* 487 */         append("Backup Complete!", true);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void reportError(Throwable error)
/*     */       {
/* 494 */         append("Backup Failed: " + Debug.getNestedExceptionMessage(error), true);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       private boolean append(final String str, final boolean complete)
/*     */       {
/* 502 */         if (viewer.isDisposed())
/*     */         {
/* 504 */           return false;
/*     */         }
/*     */         
/* 507 */         Utils.execSWTThread(new AERunnable()
/*     */         {
/*     */ 
/*     */           public void runSupport()
/*     */           {
/*     */ 
/* 513 */             if (str.endsWith("..."))
/*     */             {
/* 515 */               ConfigSectionBackupRestore.7.this.val$viewer.append(str);
/*     */             }
/*     */             else
/*     */             {
/* 519 */               ConfigSectionBackupRestore.7.this.val$viewer.append(str + "\r\n");
/*     */             }
/*     */             
/* 522 */             if (complete)
/*     */             {
/* 524 */               ConfigSectionBackupRestore.7.this.val$viewer.setOKEnabled(true);
/*     */               
/* 526 */               ConfigSectionBackupRestore.7.this.val$stats_updater.run();
/*     */             }
/*     */             
/*     */           }
/* 530 */         });
/* 531 */         return true;
/*     */       }
/*     */     };
/*     */     
/* 535 */     if (path == null)
/*     */     {
/* 537 */       backup_manager.runAutoBackup(listener);
/*     */     }
/*     */     else
/*     */     {
/* 541 */       backup_manager.backup(new File(path), listener);
/*     */     }
/*     */     
/* 544 */     viewer.goModal();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionBackupRestore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */