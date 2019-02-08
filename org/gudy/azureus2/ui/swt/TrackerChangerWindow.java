/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrent;
/*     */ import org.gudy.azureus2.core3.tracker.client.TRTrackerAnnouncer;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
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
/*     */ public class TrackerChangerWindow
/*     */ {
/*     */   public TrackerChangerWindow(final DownloadManager[] dms)
/*     */   {
/*  47 */     final Shell shell = ShellFactory.createMainShell(2144);
/*  48 */     shell.setText(MessageText.getString("TrackerChangerWindow.title"));
/*  49 */     Utils.setShellIcon(shell);
/*  50 */     GridLayout layout = new GridLayout();
/*  51 */     shell.setLayout(layout);
/*     */     
/*  53 */     Label label = new Label(shell, 0);
/*  54 */     Messages.setLanguageText(label, "TrackerChangerWindow.newtracker");
/*  55 */     GridData gridData = new GridData();
/*  56 */     gridData.widthHint = 400;
/*  57 */     Utils.setLayoutData(label, gridData);
/*     */     
/*  59 */     final Text url = new Text(shell, 2048);
/*  60 */     gridData = new GridData(768);
/*  61 */     gridData.widthHint = 400;
/*  62 */     Utils.setLayoutData(url, gridData);
/*  63 */     Utils.setTextLinkFromClipboard(shell, url, false, false);
/*     */     
/*  65 */     Label labelSeparator = new Label(shell, 258);
/*  66 */     gridData = new GridData(768);
/*  67 */     Utils.setLayoutData(labelSeparator, gridData);
/*     */     
/*  69 */     Composite panel = new Composite(shell, 0);
/*  70 */     gridData = new GridData(768);
/*  71 */     Utils.setLayoutData(panel, gridData);
/*  72 */     layout = new GridLayout();
/*  73 */     layout.numColumns = 3;
/*  74 */     panel.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*  78 */     label = new Label(panel, 0);
/*  79 */     gridData = new GridData(768);
/*  80 */     Utils.setLayoutData(label, gridData);
/*     */     
/*  82 */     Button ok = new Button(panel, 8);
/*  83 */     ok.setText(MessageText.getString("Button.ok"));
/*  84 */     gridData = new GridData();
/*  85 */     gridData.widthHint = 70;
/*  86 */     gridData.horizontalAlignment = 3;
/*  87 */     Utils.setLayoutData(ok, gridData);
/*  88 */     shell.setDefaultButton(ok);
/*  89 */     ok.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/*     */         try {
/*  93 */           String[] _urls = url.getText().split(",");
/*     */           
/*  95 */           List<String> urls = new ArrayList();
/*     */           
/*  97 */           for (String url : _urls)
/*     */           {
/*  99 */             url = url.trim();
/*     */             
/* 101 */             if (url.length() > 0) {
/*     */               try
/*     */               {
/* 104 */                 new URL(url);
/*     */                 
/* 106 */                 urls.add(0, url);
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 110 */                 Debug.out("Invalid URL: " + url);
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 115 */           for (DownloadManager dm : dms)
/*     */           {
/* 117 */             TOTorrent torrent = dm.getTorrent();
/*     */             
/* 119 */             if (torrent != null)
/*     */             {
/* 121 */               for (String url : urls)
/*     */               {
/* 123 */                 TorrentUtils.announceGroupsInsertFirst(torrent, url);
/*     */               }
/*     */               
/* 126 */               TorrentUtils.writeToFile(torrent);
/*     */               
/* 128 */               TRTrackerAnnouncer announcer = dm.getTrackerClient();
/*     */               
/* 130 */               if (announcer != null)
/*     */               {
/* 132 */                 announcer.resetTrackerUrl(false);
/*     */               }
/*     */             }
/*     */           }
/*     */           
/* 137 */           shell.dispose();
/*     */         }
/*     */         catch (Exception e) {
/* 140 */           Debug.printStackTrace(e);
/*     */         }
/*     */         
/*     */       }
/* 144 */     });
/* 145 */     Button cancel = new Button(panel, 8);
/* 146 */     cancel.setText(MessageText.getString("Button.cancel"));
/* 147 */     gridData = new GridData();
/* 148 */     gridData.widthHint = 70;
/* 149 */     gridData.horizontalAlignment = 3;
/* 150 */     Utils.setLayoutData(cancel, gridData);
/* 151 */     cancel.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 154 */         shell.dispose();
/*     */       }
/*     */       
/* 157 */     });
/* 158 */     shell.pack();
/* 159 */     Utils.centreWindow(shell);
/* 160 */     Utils.createURLDropTarget(shell, url);
/* 161 */     shell.open();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/TrackerChangerWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */