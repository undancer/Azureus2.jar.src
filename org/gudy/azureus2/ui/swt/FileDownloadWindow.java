/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import java.io.File;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.eclipse.swt.widgets.DirectoryDialog;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.torrent.impl.TorrentOpenOptions;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloader;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderCallBackInterface;
/*     */ import org.gudy.azureus2.core3.torrentdownloader.TorrentDownloaderFactory;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.TorrentOpener;
/*     */ import org.gudy.azureus2.ui.swt.progress.IProgressReport;
/*     */ import org.gudy.azureus2.ui.swt.progress.IProgressReportConstants;
/*     */ import org.gudy.azureus2.ui.swt.progress.IProgressReporter;
/*     */ import org.gudy.azureus2.ui.swt.progress.IProgressReporterListener;
/*     */ import org.gudy.azureus2.ui.swt.progress.ProgressReporterWindow;
/*     */ import org.gudy.azureus2.ui.swt.progress.ProgressReportingManager;
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
/*     */ public class FileDownloadWindow
/*     */   implements TorrentDownloaderCallBackInterface, IProgressReportConstants
/*     */ {
/*     */   TorrentDownloader downloader;
/*     */   TorrentDownloaderCallBackInterface listener;
/*     */   boolean force_dialog;
/*     */   private final Runnable callOnError;
/*     */   IProgressReporter pReporter;
/*     */   Shell parent;
/*     */   String original_url;
/*     */   String decoded_url;
/*     */   String referrer;
/*     */   Map request_properties;
/*  78 */   String dirName = null;
/*     */   
/*  80 */   String shortURL = null;
/*     */   
/*     */   TorrentOpenOptions torrentOptions;
/*     */   
/*  84 */   private int lastState = -1;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public FileDownloadWindow(Shell parent, String url, String referrer, Map request_properties, Runnable runOnError)
/*     */   {
/*  96 */     this(parent, url, referrer, request_properties, null, null, runOnError);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public FileDownloadWindow(Shell parent, String url, String referrer, Map request_properties, TorrentOpenOptions torrentOptions, TorrentDownloaderCallBackInterface listener)
/*     */   {
/* 116 */     this(parent, url, referrer, request_properties, torrentOptions, listener, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private FileDownloadWindow(Shell parent, String url, String referrer, Map request_properties, TorrentOpenOptions torrentOptions, TorrentDownloaderCallBackInterface listener, Runnable callOnError)
/*     */   {
/* 124 */     this.parent = parent;
/* 125 */     this.original_url = url;
/* 126 */     this.referrer = referrer;
/* 127 */     this.torrentOptions = torrentOptions;
/* 128 */     this.listener = listener;
/* 129 */     this.request_properties = request_properties;
/* 130 */     this.callOnError = callOnError;
/*     */     
/* 132 */     this.decoded_url = UrlUtils.decodeIfNeeded(this.original_url);
/*     */     
/* 134 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 136 */         FileDownloadWindow.this.init();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public FileDownloadWindow(Shell parent, String url, String referrer, Map request_properties, boolean force_dialog)
/*     */   {
/* 145 */     this.parent = parent;
/* 146 */     this.original_url = url;
/* 147 */     this.referrer = referrer;
/* 148 */     this.force_dialog = force_dialog;
/* 149 */     this.request_properties = request_properties;
/* 150 */     this.callOnError = null;
/*     */     
/* 152 */     this.decoded_url = UrlUtils.decodeIfNeeded(this.original_url);
/*     */     
/* 154 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 156 */         FileDownloadWindow.this.init();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   private void init()
/*     */   {
/* 163 */     if (COConfigurationManager.getBooleanParameter("Save Torrent Files")) {
/*     */       try {
/* 165 */         this.dirName = COConfigurationManager.getDirectoryParameter("General_sDefaultTorrent_Directory");
/*     */         
/* 167 */         if (this.dirName != null) {
/* 168 */           File f = new File(this.dirName);
/* 169 */           if (!f.isDirectory())
/*     */           {
/* 171 */             if (f.exists()) {
/* 172 */               this.dirName = null;
/*     */             }
/* 174 */             else if (!f.mkdirs()) {
/* 175 */               this.dirName = null;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Exception egnore) {}
/*     */     }
/*     */     
/* 183 */     if (this.dirName == null) {
/* 184 */       DirectoryDialog dd = new DirectoryDialog(this.parent == null ? Utils.findAnyShell() : this.parent, 0);
/*     */       
/* 186 */       dd.setText(MessageText.getString("fileDownloadWindow.saveTorrentIn"));
/* 187 */       this.dirName = dd.open();
/*     */     }
/* 189 */     if (this.dirName == null) {
/* 190 */       return;
/*     */     }
/* 192 */     this.pReporter = ProgressReportingManager.getInstance().addReporter();
/* 193 */     setupAndShowDialog();
/*     */     
/* 195 */     this.downloader = TorrentDownloaderFactory.create(this, this.original_url, this.referrer, this.request_properties, this.dirName);
/*     */     
/* 197 */     this.downloader.setIgnoreReponseCode(true);
/* 198 */     this.downloader.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void setupAndShowDialog()
/*     */   {
/* 205 */     if (null != this.pReporter) {
/* 206 */       this.pReporter.setName(MessageText.getString("fileDownloadWindow.state_downloading") + ": " + getFileName(this.decoded_url));
/*     */       
/* 208 */       this.pReporter.appendDetailMessage(MessageText.getString("fileDownloadWindow.downloading") + getShortURL(this.decoded_url));
/*     */       
/* 210 */       this.pReporter.setTitle(MessageText.getString("fileDownloadWindow.title"));
/* 211 */       this.pReporter.setIndeterminate(true);
/* 212 */       this.pReporter.setCancelAllowed(true);
/* 213 */       this.pReporter.setRetryAllowed(true);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 218 */       this.pReporter.addListener(new IProgressReporterListener()
/*     */       {
/*     */         public int report(IProgressReport pReport)
/*     */         {
/* 222 */           switch (pReport.getReportType()) {
/*     */           case 1: 
/* 224 */             if (null != FileDownloadWindow.this.downloader) {
/* 225 */               FileDownloadWindow.this.downloader.cancel();
/*     */               
/*     */ 
/* 228 */               Logger.log(new LogEvent(LogIDs.LOGGER, MessageText.getString("FileDownload.canceled", new String[] { FileDownloadWindow.this.getShortURL(FileDownloadWindow.this.decoded_url) })));
/*     */             }
/*     */             
/*     */ 
/*     */ 
/*     */             break;
/*     */           case 2: 
/* 235 */             return 1;
/*     */           case 5: 
/* 237 */             if (pReport.isRetryAllowed()) {
/* 238 */               FileDownloadWindow.this.downloader.cancel();
/* 239 */               FileDownloadWindow.this.downloader = TorrentDownloaderFactory.create(FileDownloadWindow.this, FileDownloadWindow.this.original_url, FileDownloadWindow.this.referrer, FileDownloadWindow.this.request_properties, FileDownloadWindow.this.dirName);
/*     */               
/*     */ 
/* 242 */               FileDownloadWindow.this.downloader.setIgnoreReponseCode(true);
/* 243 */               FileDownloadWindow.this.downloader.start();
/*     */             }
/*     */             
/*     */             break;
/*     */           }
/*     */           
/*     */           
/* 250 */           return 0;
/*     */         }
/*     */       });
/*     */       
/*     */ 
/* 255 */       if (!COConfigurationManager.getBooleanParameter("suppress_file_download_dialog"))
/*     */       {
/* 257 */         ProgressReporterWindow.open(this.pReporter, 2);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void TorrentDownloaderEvent(int state, TorrentDownloader inf) {
/* 263 */     if (this.listener != null)
/* 264 */       this.listener.TorrentDownloaderEvent(state, inf);
/* 265 */     update();
/*     */   }
/*     */   
/*     */   private void update() {
/*     */     int localLastState;
/*     */     int state;
/* 271 */     synchronized (this) {
/* 272 */       localLastState = this.lastState;
/* 273 */       state = this.downloader.getDownloadState();
/*     */       
/*     */ 
/* 276 */       this.lastState = state;
/*     */     }
/* 278 */     int percentDone = this.downloader.getPercentDone();
/*     */     
/* 280 */     IProgressReport pReport = this.pReporter.getProgressReport();
/* 281 */     switch (state) {
/*     */     case 6: 
/* 283 */       if (localLastState == state) {
/* 284 */         return;
/*     */       }
/* 286 */       if (!pReport.isCanceled()) {
/* 287 */         this.pReporter.cancel();
/*     */       }
/* 289 */       return;
/*     */     case 2: 
/* 291 */       this.pReporter.setPercentage(percentDone, this.downloader.getStatus());
/* 292 */       break;
/*     */     case 4: 
/* 294 */       if (localLastState == state) {
/* 295 */         return;
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 301 */       if (pReport.isCanceled()) {
/* 302 */         return;
/*     */       }
/*     */       
/* 305 */       if ((this.torrentOptions != null) && (this.torrentOptions.getHideErrors())) {
/* 306 */         this.pReporter.setCancelCloses(true);
/* 307 */         this.pReporter.cancel();
/*     */       } else {
/* 309 */         this.pReporter.setErrorMessage(MessageText.getString("fileDownloadWindow.state_error") + this.downloader.getError());
/*     */       }
/*     */       
/*     */ 
/* 313 */       if (this.callOnError != null)
/*     */       {
/* 315 */         this.callOnError.run();
/*     */       }
/* 317 */       return;
/*     */     case 3: 
/* 319 */       if (localLastState == state) {
/* 320 */         return;
/*     */       }
/* 322 */       this.pReporter.setDone();
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 328 */       if (this.listener == null)
/*     */       {
/* 330 */         if (this.torrentOptions == null) {
/* 331 */           this.torrentOptions = new TorrentOpenOptions();
/*     */         }
/* 333 */         if (TorrentOpener.mergeFileIntoTorrentInfo(this.downloader.getFile().getAbsolutePath(), this.original_url, this.torrentOptions))
/*     */         {
/*     */ 
/* 336 */           UIFunctionsManager.getUIFunctions().addTorrentWithOptions(this.force_dialog, this.torrentOptions);
/*     */         }
/*     */       }
/*     */       
/* 340 */       return;
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String getShortURL(String url)
/*     */   {
/* 352 */     if (null == this.shortURL) {
/* 353 */       this.shortURL = url;
/*     */       
/*     */ 
/* 356 */       int trunc_pos = this.shortURL.indexOf('&');
/* 357 */       if (trunc_pos == -1)
/*     */       {
/*     */ 
/*     */ 
/* 361 */         trunc_pos = this.shortURL.indexOf('?');
/*     */         
/* 363 */         if ((trunc_pos > 0) && (this.shortURL.charAt(trunc_pos - 1) == ':'))
/*     */         {
/* 365 */           trunc_pos = -1;
/*     */         }
/*     */       }
/* 368 */       if (trunc_pos != -1) {
/* 369 */         this.shortURL = (this.shortURL.substring(0, trunc_pos + 1) + "...");
/*     */       }
/* 371 */       this.shortURL = this.shortURL.replaceAll("&", "&&");
/*     */     }
/*     */     
/* 374 */     return this.shortURL;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String getFileName(String url)
/*     */   {
/*     */     try
/*     */     {
/* 388 */       String[] titles = { "title", "dn" };
/*     */       
/*     */ 
/*     */ 
/* 392 */       for (String toMatch : titles) {
/* 393 */         Matcher matcher = Pattern.compile("[?&]" + toMatch + "=([^&]*)", 2).matcher(url);
/*     */         
/* 395 */         if (matcher.find())
/*     */         {
/* 397 */           String file_name = matcher.group(1);
/*     */           
/* 399 */           return UrlUtils.decode(file_name);
/*     */         }
/*     */       }
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
/* 414 */       url = getShortURL(url);
/*     */       
/* 416 */       String lc_url = url.toLowerCase(MessageText.LOCALE_ENGLISH);
/*     */       
/* 418 */       if ((lc_url.startsWith("magnet:")) || (lc_url.startsWith("maggot:")) || (lc_url.startsWith("dht:")) || (lc_url.startsWith("bc:")) || (lc_url.startsWith("bctp:")))
/*     */       {
/* 420 */         return url;
/*     */       }
/*     */       
/* 423 */       String tmp = url.substring(url.lastIndexOf('/') + 1);
/*     */       
/* 425 */       int pos = tmp.toLowerCase(MessageText.LOCALE_ENGLISH).lastIndexOf(".vuze");
/*     */       
/* 427 */       if (pos > 0) {
/* 428 */         return tmp.substring(0, pos + 5);
/*     */       }
/*     */       
/* 431 */       pos = tmp.toLowerCase(MessageText.LOCALE_ENGLISH).lastIndexOf(".torrent");
/*     */       
/* 433 */       if (pos > 0) {
/* 434 */         tmp = tmp.substring(0, pos);
/*     */       }
/* 436 */       return tmp + ".torrent";
/*     */     }
/*     */     catch (Exception t) {}
/*     */     
/*     */ 
/*     */ 
/* 442 */     return url;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/FileDownloadWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */