/*     */ package org.gudy.azureus2.ui.swt.shells;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.RememberedDecisionsManager;
/*     */ import java.io.File;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.layout.RowLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
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
/*     */ public class AdvRenameWindow
/*     */ {
/*     */   private DownloadManager dm;
/*     */   private Shell shell;
/*  45 */   private String newName = null;
/*     */   
/*     */   protected int renameDecisions;
/*     */   
/*     */   private static final int RENAME_DISPLAY = 1;
/*     */   
/*     */   private static final int RENAME_SAVEPATH = 2;
/*     */   private static final int RENAME_TORRENT = 4;
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/*  56 */     AdvRenameWindow window = new AdvRenameWindow();
/*  57 */     window.open(null);
/*  58 */     window.waitUntilDone();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void open(DownloadManager dm)
/*     */   {
/*  65 */     this.dm = dm;
/*  66 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/*  68 */         AdvRenameWindow.this.openInSWT();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void openInSWT() {
/*  74 */     this.shell = ShellFactory.createMainShell(2160);
/*  75 */     Utils.setShellIcon(this.shell);
/*  76 */     this.shell.addTraverseListener(new TraverseListener() {
/*     */       public void keyTraversed(TraverseEvent e) {
/*  78 */         if (e.detail == 2) {
/*  79 */           AdvRenameWindow.this.shell.dispose();
/*     */         }
/*     */         
/*     */       }
/*  83 */     });
/*  84 */     Messages.setLanguageText(this.shell, "AdvRenameWindow.title");
/*     */     
/*  86 */     Label lblMessage = new Label(this.shell, 64);
/*  87 */     Messages.setLanguageText(lblMessage, "AdvRenameWindow.message");
/*     */     
/*  89 */     final Text txtInput = new Text(this.shell, 2048);
/*  90 */     txtInput.setText(this.dm == null ? "" : this.dm.getDisplayName());
/*     */     
/*  92 */     final Button btnDisplayName = new Button(this.shell, 32);
/*  93 */     Messages.setLanguageText(btnDisplayName, "MyTorrentsView.menu.rename.displayed");
/*     */     
/*     */ 
/*  96 */     final Button btnSavePath = new Button(this.shell, 32);
/*  97 */     Messages.setLanguageText(btnSavePath, "MyTorrentsView.menu.rename.save_path");
/*     */     
/*     */ 
/* 100 */     final Button btnTorrent = new Button(this.shell, 32);
/* 101 */     Messages.setLanguageText(btnTorrent, "AdvRenameWindow.rename.torrent");
/*     */     
/* 103 */     Composite cButtons = new Composite(this.shell, 0);
/* 104 */     RowLayout rowLayout = new RowLayout(256);
/* 105 */     rowLayout.fill = true;
/* 106 */     rowLayout.spacing = 5;
/* 107 */     Utils.setLayout(cButtons, rowLayout);
/*     */     
/* 109 */     Button btnReset = new Button(cButtons, 8);
/* 110 */     Messages.setLanguageText(btnReset, "Button.reset");
/* 111 */     btnReset.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 113 */         txtInput.setText(TorrentUtils.getLocalisedName(AdvRenameWindow.this.dm.getTorrent()));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/* 119 */     });
/* 120 */     Button btnOk = new Button(cButtons, 8);
/* 121 */     Messages.setLanguageText(btnOk, "Button.ok");
/* 122 */     this.shell.setDefaultButton(btnOk);
/* 123 */     btnOk.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 125 */         AdvRenameWindow.this.newName = txtInput.getText();
/*     */         
/* 127 */         AdvRenameWindow.this.renameDecisions = 0;
/* 128 */         if (btnDisplayName.getSelection()) {
/* 129 */           AdvRenameWindow.this.renameDecisions |= 0x1;
/*     */         }
/* 131 */         if (btnSavePath.getSelection()) {
/* 132 */           AdvRenameWindow.this.renameDecisions |= 0x2;
/*     */         }
/* 134 */         if (btnTorrent.getSelection()) {
/* 135 */           AdvRenameWindow.this.renameDecisions |= 0x4;
/*     */         }
/* 137 */         RememberedDecisionsManager.setRemembered("adv.rename", AdvRenameWindow.this.renameDecisions);
/*     */         
/* 139 */         Utils.getOffOfSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 141 */             AdvRenameWindow.this.doRename();
/*     */           }
/*     */           
/* 144 */         });
/* 145 */         AdvRenameWindow.this.shell.dispose();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/* 151 */     });
/* 152 */     Button btnCancel = new Button(cButtons, 8);
/* 153 */     Messages.setLanguageText(btnCancel, "Button.cancel");
/* 154 */     btnCancel.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 156 */         AdvRenameWindow.this.shell.dispose();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/* 162 */     });
/* 163 */     this.shell.setLayout(new FormLayout());
/*     */     
/*     */ 
/* 166 */     FormData fd = new FormData();
/* 167 */     fd.top = new FormAttachment(0, 3);
/* 168 */     fd.left = new FormAttachment(0, 3);
/* 169 */     fd.right = new FormAttachment(100, -3);
/* 170 */     lblMessage.setLayoutData(fd);
/*     */     
/* 172 */     fd = new FormData();
/* 173 */     fd.top = new FormAttachment(lblMessage, 5);
/* 174 */     fd.left = new FormAttachment(0, 3);
/* 175 */     fd.right = new FormAttachment(100, -3);
/* 176 */     fd.width = 300;
/* 177 */     txtInput.setLayoutData(fd);
/*     */     
/* 179 */     fd = new FormData();
/* 180 */     fd.top = new FormAttachment(txtInput, 5);
/* 181 */     fd.left = new FormAttachment(0, 8);
/* 182 */     fd.right = new FormAttachment(100, -3);
/* 183 */     btnDisplayName.setLayoutData(fd);
/*     */     
/* 185 */     fd = new FormData();
/* 186 */     fd.top = new FormAttachment(btnDisplayName, 2);
/* 187 */     fd.left = new FormAttachment(0, 8);
/* 188 */     fd.right = new FormAttachment(100, -3);
/* 189 */     btnSavePath.setLayoutData(fd);
/*     */     
/* 191 */     fd = new FormData();
/* 192 */     fd.top = new FormAttachment(btnSavePath, 2);
/* 193 */     fd.left = new FormAttachment(0, 8);
/* 194 */     fd.right = new FormAttachment(100, -3);
/* 195 */     btnTorrent.setLayoutData(fd);
/*     */     
/* 197 */     int renameDecisions = RememberedDecisionsManager.getRememberedDecision("adv.rename");
/* 198 */     if ((renameDecisions & 0x1) > 0) {
/* 199 */       btnDisplayName.setSelection(true);
/*     */     }
/* 201 */     if ((renameDecisions & 0x2) > 0) {
/* 202 */       btnSavePath.setSelection(true);
/*     */     }
/* 204 */     if ((renameDecisions & 0x4) > 0) {
/* 205 */       btnTorrent.setSelection(true);
/*     */     }
/*     */     
/* 208 */     fd = new FormData();
/* 209 */     fd.top = new FormAttachment(btnTorrent, 5);
/* 210 */     fd.right = new FormAttachment(100, -3);
/* 211 */     fd.bottom = new FormAttachment(100, -3);
/* 212 */     cButtons.setLayoutData(fd);
/*     */     
/* 214 */     this.shell.pack();
/* 215 */     Utils.centreWindow(this.shell);
/* 216 */     this.shell.open();
/*     */   }
/*     */   
/*     */   private void waitUntilDone() {
/* 220 */     while ((this.shell != null) && (!this.shell.isDisposed())) {
/* 221 */       if (!this.shell.getDisplay().readAndDispatch()) {
/* 222 */         this.shell.getDisplay().sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void doRename() {
/* 228 */     if (this.dm == null) {
/* 229 */       return;
/*     */     }
/*     */     
/* 232 */     boolean saveLocationIsFolder = this.dm.getSaveLocation().isDirectory();
/*     */     
/* 234 */     String newDisplayName = this.newName;
/* 235 */     String newSavePath = FileUtil.convertOSSpecificChars(this.newName, saveLocationIsFolder);
/* 236 */     String newTorrentName = FileUtil.convertOSSpecificChars(this.newName, false);
/*     */     
/* 238 */     if ((this.renameDecisions & 0x1) > 0) {
/* 239 */       this.dm.getDownloadState().setDisplayName(newDisplayName);
/*     */     }
/* 241 */     if ((this.renameDecisions & 0x2) > 0) {
/*     */       try
/*     */       {
/*     */         try {
/* 245 */           if (this.dm.getTorrent().isSimpleTorrent())
/*     */           {
/* 247 */             String dnd_sf = this.dm.getDownloadState().getAttribute("incompfilesuffix");
/*     */             
/* 249 */             if (dnd_sf != null)
/*     */             {
/* 251 */               dnd_sf = dnd_sf.trim();
/*     */               
/* 253 */               String existing_name = this.dm.getSaveLocation().getName();
/*     */               
/* 255 */               if (existing_name.endsWith(dnd_sf))
/*     */               {
/* 257 */                 if (!newSavePath.endsWith(dnd_sf))
/*     */                 {
/* 259 */                   newSavePath = newSavePath + dnd_sf;
/*     */                 }
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/* 266 */         this.dm.renameDownload(newSavePath);
/*     */       } catch (Exception e) {
/* 268 */         Logger.log(new LogAlert(this.dm, true, "Download data rename operation failed", e));
/*     */       }
/*     */     }
/*     */     
/* 272 */     if ((this.renameDecisions & 0x4) > 0) {
/*     */       try {
/* 274 */         this.dm.renameTorrentSafe(newTorrentName);
/*     */       } catch (Exception e) {
/* 276 */         Logger.log(new LogAlert(this.dm, true, "Torrent rename operation failed", e));
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/shells/AdvRenameWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */