/*     */ package com.aelitis.azureus.ui.swt.shells.opentorrent;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.swt.shells.main.UIFunctionsImpl;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectCheckbox;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectTextbox;
/*     */ import com.aelitis.azureus.ui.swt.uiupdater.UIUpdaterSWT;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog.SkinnedDialogClosedListener;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.StandardButtonsArea;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import org.eclipse.swt.dnd.Clipboard;
/*     */ import org.eclipse.swt.dnd.TextTransfer;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.layout.RowLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.DirectoryDialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.FileDialog;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.StringIterator;
/*     */ import org.gudy.azureus2.core3.config.StringList;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenOptions;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloader;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderCallBackInterface;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.plugins.utils.subscriptions.SubscriptionManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.TorrentUtil;
/*     */ import org.gudy.azureus2.ui.swt.UIConfigDefaultsSWT;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*     */ 
/*     */ public class OpenTorrentWindow implements TorrentDownloaderCallBackInterface, UIUpdatable
/*     */ {
/*  65 */   protected static String CONFIG_REFERRER_DEFAULT = "openUrl.referrer.default";
/*     */   
/*     */   private Shell shellForChildren;
/*     */   
/*     */   private Shell parent;
/*     */   
/*     */   private SkinnedDialog dlg;
/*     */   
/*     */   private StandardButtonsArea buttonsArea;
/*     */   
/*     */   private Button btnPasteOpen;
/*     */   
/*     */   private SWTSkinObjectTextbox soTextArea;
/*     */   
/*     */   private SWTSkinObject soReferArea;
/*     */   
/*     */   private Combo referrer_combo;
/*     */   
/*     */   private String last_referrer;
/*     */   
/*     */   private StringList referrers;
/*     */   
/*     */   private SWTSkinObjectCheckbox soShowAdvanced;
/*     */   
/*     */ 
/*     */   public OpenTorrentWindow(Shell parent)
/*     */   {
/*  92 */     this.parent = parent;
/*     */     
/*  94 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/*  96 */         OpenTorrentWindow.this.swt_createWindow();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void swt_createWindow() {
/* 102 */     this.dlg = new SkinnedDialog("skin3_dlg_opentorrent", "shell", 2160);
/*     */     
/*     */ 
/* 105 */     this.shellForChildren = this.dlg.getShell();
/* 106 */     SWTSkin skin = this.dlg.getSkin();
/* 107 */     SWTSkinObject soTopBar = skin.getSkinObject("add-buttons");
/* 108 */     if ((soTopBar instanceof SWTSkinObjectContainer)) {
/* 109 */       swt_addButtons(((SWTSkinObjectContainer)soTopBar).getComposite());
/*     */     }
/*     */     
/* 112 */     this.soTextArea = ((SWTSkinObjectTextbox)skin.getSkinObject("text-area"));
/* 113 */     Text tb = (Text)this.soTextArea.getControl();
/* 114 */     tb.setFocus();
/* 115 */     tb.addModifyListener(new ModifyListener() {
/*     */       public void modifyText(ModifyEvent e) {
/* 117 */         int userMode = COConfigurationManager.getIntParameter("User Mode");
/* 118 */         if ((userMode > 0) && 
/* 119 */           (OpenTorrentWindow.this.soReferArea != null)) {
/* 120 */           String text = ((Text)e.widget).getText();
/* 121 */           boolean hasURL = UrlUtils.parseTextForURL(text, false, true) != null;
/* 122 */           OpenTorrentWindow.this.soReferArea.setVisible(hasURL);
/*     */ 
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 129 */     });
/* 130 */     SWTSkinObject so = skin.getSkinObject("show-advanced");
/* 131 */     if ((so instanceof SWTSkinObjectCheckbox)) {
/* 132 */       this.soShowAdvanced = ((SWTSkinObjectCheckbox)so);
/* 133 */       this.soShowAdvanced.setChecked(COConfigurationManager.getBooleanParameter("ui.addtorrent.openoptions"));
/*     */     }
/*     */     
/* 136 */     this.soReferArea = skin.getSkinObject("refer-area");
/*     */     
/* 138 */     this.last_referrer = COConfigurationManager.getStringParameter(CONFIG_REFERRER_DEFAULT, "");
/*     */     
/*     */ 
/* 141 */     so = skin.getSkinObject("refer-combo");
/* 142 */     if ((so instanceof SWTSkinObjectContainer)) {
/* 143 */       this.referrer_combo = new Combo(((SWTSkinObjectContainer)so).getComposite(), 2048);
/*     */       
/* 145 */       this.referrer_combo.setLayoutData(Utils.getFilledFormData());
/* 146 */       this.referrers = COConfigurationManager.getStringListParameter("url_open_referrers");
/* 147 */       StringIterator iter = this.referrers.iterator();
/* 148 */       while (iter.hasNext()) {
/* 149 */         this.referrer_combo.add(iter.next());
/*     */       }
/*     */       
/* 152 */       if (this.last_referrer != null)
/*     */       {
/* 154 */         this.referrer_combo.setText(this.last_referrer);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 159 */     SWTSkinObject soButtonArea = skin.getSkinObject("button-area");
/* 160 */     if ((soButtonArea instanceof SWTSkinObjectContainer)) {
/* 161 */       this.buttonsArea = new StandardButtonsArea() {
/*     */         protected void clicked(int intValue) {
/* 163 */           String referrer = null;
/* 164 */           if (OpenTorrentWindow.this.referrer_combo != null) {
/* 165 */             referrer = OpenTorrentWindow.this.referrer_combo.getText().trim();
/*     */           }
/*     */           
/* 168 */           if (OpenTorrentWindow.this.dlg != null) {
/* 169 */             OpenTorrentWindow.this.dlg.close();
/*     */           }
/* 171 */           if ((intValue == 32) && (OpenTorrentWindow.this.soTextArea != null) && (OpenTorrentWindow.this.soTextArea.getText().length() > 0))
/*     */           {
/* 173 */             OpenTorrentWindow.this.openTorrent(OpenTorrentWindow.this.soTextArea.getText(), referrer);
/*     */           }
/*     */         }
/* 176 */       };
/* 177 */       this.buttonsArea.setButtonIDs(new String[] { MessageText.getString("Button.ok"), MessageText.getString("Button.cancel") });
/*     */       
/*     */ 
/*     */ 
/* 181 */       this.buttonsArea.setButtonVals(new Integer[] { Integer.valueOf(32), Integer.valueOf(256) });
/*     */       
/*     */ 
/*     */ 
/* 185 */       this.buttonsArea.swt_createButtons(((SWTSkinObjectContainer)soButtonArea).getComposite());
/*     */     }
/*     */     
/* 188 */     UIUpdaterSWT.getInstance().addUpdater(this);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 196 */     this.dlg.open("otw", false);
/*     */     
/* 198 */     this.dlg.addCloseListener(new SkinnedDialog.SkinnedDialogClosedListener() {
/*     */       public void skinDialogClosed(SkinnedDialog dialog) {
/* 200 */         OpenTorrentWindow.this.dispose();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void openTorrent(String text, String newReferrer) {
/* 206 */     if ((newReferrer != null) && (newReferrer.length() > 0))
/*     */     {
/* 208 */       if (!this.referrers.contains(newReferrer)) {
/* 209 */         this.referrers.add(newReferrer);
/* 210 */         COConfigurationManager.setParameter("url_open_referrers", this.referrers);
/* 211 */         COConfigurationManager.save();
/*     */       }
/*     */       
/* 214 */       COConfigurationManager.setParameter(CONFIG_REFERRER_DEFAULT, newReferrer);
/* 215 */       COConfigurationManager.save();
/*     */     }
/* 217 */     String[] splitters = { "\r\n", "\n", "\r", "\t" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 224 */     String[] lines = null;
/*     */     
/* 226 */     for (int i = 0; i < splitters.length; i++) {
/* 227 */       if (text.contains(splitters[i])) {
/* 228 */         lines = text.split(splitters[i]);
/* 229 */         break;
/*     */       }
/*     */     }
/*     */     
/* 233 */     if (lines == null) {
/* 234 */       lines = new String[] { text };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 239 */     TorrentOpener.openTorrentsFromStrings(new TorrentOpenOptions(), this.parent, null, lines, newReferrer, this, false);
/*     */   }
/*     */   
/*     */   protected void dispose()
/*     */   {
/* 244 */     UIUpdaterSWT.getInstance().removeUpdater(this);
/*     */   }
/*     */   
/*     */   private void swt_addButtons(Composite parent) {
/* 248 */     Composite cButtons = new Composite(parent, 0);
/* 249 */     RowLayout rLayout = new RowLayout(256);
/* 250 */     rLayout.marginBottom = 0;
/* 251 */     rLayout.marginLeft = 0;
/* 252 */     rLayout.marginRight = 0;
/* 253 */     rLayout.marginTop = 0;
/* 254 */     cButtons.setLayout(rLayout);
/* 255 */     cButtons.setLayoutData(Utils.getFilledFormData());
/*     */     
/*     */ 
/*     */ 
/* 259 */     Button browseTorrent = new Button(cButtons, 8);
/* 260 */     Messages.setLanguageText(browseTorrent, "OpenTorrentWindow.addFiles");
/* 261 */     browseTorrent.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 263 */         FileDialog fDialog = new FileDialog(OpenTorrentWindow.this.shellForChildren, 4098);
/*     */         
/* 265 */         fDialog.setFilterExtensions(new String[] { "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 270 */         fDialog.setFilterNames(new String[] { "*.torrent", "*.tor", Constants.FILE_WILDCARD });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 275 */         fDialog.setFilterPath(TorrentOpener.getFilterPathTorrent());
/* 276 */         fDialog.setText(MessageText.getString("MainWindow.dialog.choose.file"));
/* 277 */         String fileName = TorrentOpener.setFilterPathTorrent(fDialog.open());
/* 278 */         if (fileName != null) {
/* 279 */           OpenTorrentWindow.this.addTorrentsToWindow(fDialog.getFilterPath(), fDialog.getFileNames());
/*     */         }
/*     */         
/*     */       }
/* 283 */     });
/* 284 */     Button browseFolder = new Button(cButtons, 8);
/* 285 */     Messages.setLanguageText(browseFolder, "OpenTorrentWindow.addFiles.Folder");
/* 286 */     browseFolder.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 288 */         DirectoryDialog fDialog = new DirectoryDialog(OpenTorrentWindow.this.shellForChildren, 0);
/*     */         
/* 290 */         fDialog.setFilterPath(TorrentOpener.getFilterPathTorrent());
/* 291 */         fDialog.setMessage(MessageText.getString("MainWindow.dialog.choose.folder"));
/* 292 */         String path = TorrentOpener.setFilterPathTorrent(fDialog.open());
/* 293 */         if (path != null) {
/* 294 */           OpenTorrentWindow.this.addTorrentsToWindow(path, null);
/*     */         }
/*     */         
/*     */       }
/* 298 */     });
/* 299 */     this.btnPasteOpen = new Button(cButtons, 8);
/* 300 */     Messages.setLanguageText(this.btnPasteOpen, "OpenTorrentWindow.addFiles.Clipboard");
/*     */     
/* 302 */     this.btnPasteOpen.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 304 */         Clipboard clipboard = new Clipboard(OpenTorrentWindow.this.shellForChildren.getDisplay());
/*     */         
/* 306 */         String sClipText = (String)clipboard.getContents(TextTransfer.getInstance());
/* 307 */         if (sClipText != null) {
/* 308 */           OpenTorrentWindow.this.addTorrentsFromTextList(sClipText.trim(), false);
/*     */         }
/*     */         
/*     */       }
/* 312 */     });
/* 313 */     this.btnPasteOpen.setVisible(false);
/*     */   }
/*     */   
/*     */   private String ensureTrailingSeparator(String sPath) {
/* 317 */     if ((sPath == null) || (sPath.length() == 0) || (sPath.endsWith(File.separator)))
/* 318 */       return sPath;
/* 319 */     return sPath + File.separator;
/*     */   }
/*     */   
/*     */   private int addTorrentsToWindow(String sTorrentFilePath, String[] sTorrentFilenames)
/*     */   {
/* 324 */     String text = this.soTextArea.getText();
/*     */     
/* 326 */     sTorrentFilePath = ensureTrailingSeparator(sTorrentFilePath);
/*     */     
/*     */ 
/* 329 */     if ((sTorrentFilePath != null) && (sTorrentFilenames == null)) {
/* 330 */       File dir = new File(sTorrentFilePath);
/* 331 */       if (!dir.isDirectory()) {
/* 332 */         return 0;
/*     */       }
/* 334 */       File[] files = dir.listFiles(new FileFilter() {
/*     */         public boolean accept(File arg0) {
/* 336 */           if (FileUtil.getCanonicalFileName(arg0.getName()).endsWith(".torrent"))
/* 337 */             return true;
/* 338 */           if (FileUtil.getCanonicalFileName(arg0.getName()).endsWith(".tor"))
/* 339 */             return true;
/* 340 */           return false;
/*     */         }
/*     */       });
/*     */       
/* 344 */       if (files.length == 0) {
/* 345 */         return 0;
/*     */       }
/* 347 */       sTorrentFilenames = new String[files.length];
/* 348 */       for (int i = 0; i < files.length; i++) {
/* 349 */         sTorrentFilenames[i] = files[i].getName();
/*     */       }
/*     */     }
/* 352 */     int numAdded = 0;
/*     */     
/* 354 */     if (sTorrentFilenames != null) {
/* 355 */       for (int i = 0; i < sTorrentFilenames.length; i++) {
/* 356 */         if ((sTorrentFilenames[i] != null) && (sTorrentFilenames[i].length() != 0))
/*     */         {
/*     */ 
/*     */ 
/* 360 */           String sFileName = (sTorrentFilePath == null ? "" : sTorrentFilePath) + sTorrentFilenames[i];
/*     */           
/*     */ 
/* 363 */           File file = new File(sFileName);
/*     */           try
/*     */           {
/* 366 */             if ((UrlUtils.isURL(sFileName)) || ((file.exists()) && (TorrentUtils.isTorrentFile(sFileName))))
/*     */             {
/* 368 */               if (text.length() > 0) {
/* 369 */                 text = text + "\n";
/*     */               }
/* 371 */               text = text + sFileName;
/* 372 */               numAdded++;
/*     */             }
/*     */           }
/*     */           catch (FileNotFoundException e) {}catch (IOException e) {}
/*     */         }
/*     */       }
/*     */       
/* 379 */       if (numAdded > 0) {
/* 380 */         this.soTextArea.setText(text);
/*     */       }
/*     */     }
/*     */     
/* 384 */     return numAdded;
/*     */   }
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
/*     */   private int addTorrentsFromTextList(String sClipText, boolean bVerifyOnly)
/*     */   {
/* 398 */     String[] lines = null;
/* 399 */     int iNumFound = 0;
/*     */     
/* 401 */     int iNoTorrentLines = 0;
/*     */     
/* 403 */     int MAX_CONSECUTIVE_NONTORRENT_LINES = 100;
/*     */     
/* 405 */     String[] splitters = { "\r\n", "\n", "\r", "\t" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 412 */     for (int i = 0; i < splitters.length; i++) {
/* 413 */       if (sClipText.contains(splitters[i])) {
/* 414 */         lines = sClipText.split(splitters[i]);
/* 415 */         break;
/*     */       }
/*     */     }
/* 418 */     if (lines == null) {
/* 419 */       lines = new String[] { sClipText };
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 424 */     for (int i = 0; i < lines.length; i++) {
/* 425 */       String line = lines[i].trim();
/* 426 */       if ((line.startsWith("\"")) && (line.endsWith("\""))) {
/* 427 */         if (line.length() < 3) {
/* 428 */           line = "";
/*     */         } else {
/* 430 */           line = line.substring(1, line.length() - 2);
/*     */         }
/*     */       }
/*     */       
/*     */       boolean ok;
/*     */       boolean ok;
/* 436 */       if (line.length() == 0) {
/* 437 */         ok = false; } else { boolean ok;
/* 438 */         if (UrlUtils.isURL(line)) {
/* 439 */           ok = true;
/*     */         } else {
/* 441 */           File file = new File(line);
/*     */           boolean ok;
/* 443 */           if (!file.exists()) {
/* 444 */             ok = false; } else { boolean ok;
/* 445 */             if (file.isDirectory()) { boolean ok;
/* 446 */               if (bVerifyOnly)
/*     */               {
/*     */ 
/*     */ 
/* 450 */                 ok = true;
/*     */               } else {
/* 452 */                 iNumFound += addTorrentsToWindow(lines[i], null);
/* 453 */                 ok = false;
/*     */               }
/*     */             } else {
/* 456 */               ok = true;
/*     */             }
/*     */           }
/*     */         } }
/* 460 */       if (!ok) {
/* 461 */         iNoTorrentLines++;
/* 462 */         lines[i] = null;
/* 463 */         if (iNoTorrentLines > 100)
/*     */           break;
/*     */       } else {
/* 466 */         iNumFound++;
/* 467 */         iNoTorrentLines = 0;
/*     */       }
/*     */     }
/*     */     
/* 471 */     if (bVerifyOnly) {
/* 472 */       return iNumFound;
/*     */     }
/*     */     
/* 475 */     return addTorrentsToWindow(null, lines);
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 479 */     AzureusCore core = com.aelitis.azureus.core.AzureusCoreFactory.create();
/* 480 */     core.start();
/*     */     
/* 482 */     UIConfigDefaultsSWT.initialize();
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 489 */     Display display = Display.getDefault();
/*     */     
/* 491 */     Colors.getInstance();
/*     */     
/* 493 */     COConfigurationManager.setParameter("User Mode", 2);
/*     */     
/* 495 */     UIFunctionsImpl uiFunctions = new UIFunctionsImpl(null);
/* 496 */     UIFunctionsManager.setUIFunctions(uiFunctions);
/*     */     
/*     */ 
/* 499 */     OpenTorrentWindow window = new OpenTorrentWindow(null);
/* 500 */     while (!window.isDisposed()) {
/* 501 */       if (!display.readAndDispatch()) {
/* 502 */         display.sleep();
/*     */       }
/*     */     }
/* 505 */     core.stop();
/*     */   }
/*     */   
/*     */   private boolean isDisposed() {
/* 509 */     if (this.dlg == null) {
/* 510 */       return false;
/*     */     }
/* 512 */     return this.dlg.isDisposed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void TorrentDownloaderEvent(int state, TorrentDownloader inf)
/*     */   {
/* 521 */     if ((!inf.getDeleteFileOnCancel()) && ((state == 6) || (state == 4) || (state == 5) || (state == 3)))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 527 */       File file = inf.getFile();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 532 */       boolean done = false;
/*     */       
/* 534 */       if (org.gudy.azureus2.pluginsimpl.local.utils.xml.rss.RSSUtils.isRSSFeed(file)) {
/*     */         try
/*     */         {
/* 537 */           URL url = new URL(inf.getURL());
/*     */           
/* 539 */           UIManager ui_manager = StaticUtilities.getUIManager(10000L);
/*     */           
/* 541 */           if (ui_manager != null)
/*     */           {
/* 543 */             String details = MessageText.getString("subscription.request.add.message", new String[] { inf.getURL() });
/*     */             
/*     */ 
/*     */ 
/* 547 */             long res = ui_manager.showMessageBox("subscription.request.add.title", "!" + details + "!", 12L);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 552 */             if (res == 4L)
/*     */             {
/* 554 */               SubscriptionManager sm = PluginInitializer.getDefaultInterface().getUtilities().getSubscriptionManager();
/*     */               
/* 556 */               sm.requestSubscription(url);
/*     */               
/* 558 */               done = true;
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 563 */           org.gudy.azureus2.core3.util.Debug.out(e);
/*     */         }
/*     */       }
/*     */       
/* 567 */       if (!done) {
/* 568 */         TorrentUtil.isFileTorrent(inf.getURL(), file, inf.getURL(), true);
/*     */       }
/*     */       
/* 571 */       if (file.exists()) {
/* 572 */         file.delete();
/*     */       }
/*     */       
/* 575 */       return;
/*     */     }
/*     */     
/* 578 */     if (state != 0) {
/* 579 */       if (state == 3) {
/* 580 */         File file = inf.getFile();
/* 581 */         TorrentOpenOptions torrentOptions = new TorrentOpenOptions();
/* 582 */         if (!TorrentOpener.mergeFileIntoTorrentInfo(file.getAbsolutePath(), inf.getURL(), torrentOptions))
/*     */         {
/* 584 */           if (file.exists())
/* 585 */             file.delete();
/*     */         } else {
/* 587 */           UIFunctions uif = UIFunctionsManager.getUIFunctions();
/* 588 */           boolean b = uif.addTorrentWithOptions(false, torrentOptions);
/* 589 */           if ((!b) && (file.exists())) {
/* 590 */             file.delete();
/*     */           }
/*     */         }
/* 593 */       } else if ((state != 6) && (state != 4) && (state != 5))
/*     */       {
/*     */ 
/*     */ 
/* 597 */         if (state == 2) {
/* 598 */           int count = inf.getLastReadCount();
/* 599 */           int numRead = inf.getTotalRead();
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 606 */           if ((!inf.getDeleteFileOnCancel()) && (numRead >= 16384)) {
/* 607 */             inf.cancel();
/* 608 */           } else if ((numRead == count) && (count > 0)) {
/* 609 */             byte[] bytes = inf.getLastReadBytes();
/* 610 */             if ((bytes[0] != 100) && (bytes[0] != 60)) {
/* 611 */               inf.setDeleteFileOnCancel(false);
/*     */             }
/*     */           }
/*     */         } else {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void updateUI() {
/* 620 */     boolean bTorrentInClipboard = false;
/*     */     
/* 622 */     Clipboard clipboard = new Clipboard(Display.getDefault());
/*     */     
/* 624 */     String sClipText = (String)clipboard.getContents(TextTransfer.getInstance());
/* 625 */     if (sClipText != null) {
/* 626 */       bTorrentInClipboard = addTorrentsFromTextList(sClipText, true) > 0;
/*     */     }
/* 628 */     if ((this.btnPasteOpen != null) && (!this.btnPasteOpen.isDisposed()) && (this.btnPasteOpen.isVisible() != bTorrentInClipboard))
/*     */     {
/* 630 */       this.btnPasteOpen.setVisible(bTorrentInClipboard);
/* 631 */       if (bTorrentInClipboard) {
/* 632 */         this.btnPasteOpen.setToolTipText(sClipText);
/*     */       }
/*     */     }
/*     */     
/* 636 */     clipboard.dispose();
/*     */   }
/*     */   
/*     */   public String getUpdateUIName() {
/* 640 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/shells/opentorrent/OpenTorrentWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */