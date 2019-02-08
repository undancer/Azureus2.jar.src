/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.DirectoryDialog;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntListParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
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
/*     */ public class ConfigSectionFileTorrents
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  44 */     return "files";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  48 */     return "torrents";
/*     */   }
/*     */   
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete()
/*     */   {
/*  55 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  56 */     imageLoader.releaseImage("openFolderButton");
/*     */   }
/*     */   
/*     */   public int maxUserMode() {
/*  60 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Composite configSectionCreate(Composite parent)
/*     */   {
/*  69 */     Composite cTorrent = new Composite(parent, 0);
/*     */     
/*  71 */     configSectionCreateSupport(cTorrent);
/*     */     
/*  73 */     return cTorrent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void configSectionCreateSupport(final Composite cTorrent)
/*     */   {
/*  80 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  81 */     Image imgOpenFolder = imageLoader.getImage("openFolderButton");
/*     */     
/*  83 */     GridData gridData = new GridData(272);
/*  84 */     Utils.setLayoutData(cTorrent, gridData);
/*  85 */     GridLayout layout = new GridLayout();
/*  86 */     layout.numColumns = 2;
/*  87 */     cTorrent.setLayout(layout);
/*     */     
/*  89 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*     */ 
/*     */ 
/*  93 */     BooleanParameter saveTorrents = new BooleanParameter(cTorrent, "Save Torrent Files", "ConfigView.label.savetorrents");
/*     */     
/*     */ 
/*  96 */     Composite gSaveTorrents = new Composite(cTorrent, 0);
/*  97 */     gridData = new GridData(768);
/*  98 */     gridData.horizontalIndent = 25;
/*  99 */     gridData.horizontalSpan = 2;
/* 100 */     Utils.setLayoutData(gSaveTorrents, gridData);
/* 101 */     layout = new GridLayout();
/* 102 */     layout.marginHeight = 0;
/* 103 */     layout.marginWidth = 0;
/* 104 */     layout.numColumns = 3;
/* 105 */     gSaveTorrents.setLayout(layout);
/*     */     
/* 107 */     Label lSaveDir = new Label(gSaveTorrents, 0);
/* 108 */     Messages.setLanguageText(lSaveDir, "ConfigView.label.savedirectory");
/*     */     
/* 110 */     gridData = new GridData(768);
/* 111 */     final StringParameter torrentPathParameter = new StringParameter(gSaveTorrents, "General_sDefaultTorrent_Directory");
/*     */     
/* 113 */     torrentPathParameter.setLayoutData(gridData);
/*     */     
/* 115 */     Button browse2 = new Button(gSaveTorrents, 8);
/* 116 */     browse2.setImage(imgOpenFolder);
/* 117 */     imgOpenFolder.setBackground(browse2.getBackground());
/* 118 */     browse2.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/*     */     
/* 120 */     browse2.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/* 125 */         DirectoryDialog dialog = new DirectoryDialog(cTorrent.getShell(), 65536);
/* 126 */         dialog.setFilterPath(torrentPathParameter.getValue());
/* 127 */         dialog.setText(MessageText.getString("ConfigView.dialog.choosedefaulttorrentpath"));
/* 128 */         String path = dialog.open();
/* 129 */         if (path != null) {
/* 130 */           torrentPathParameter.setValue(path);
/*     */         }
/*     */       }
/* 133 */     });
/* 134 */     Utils.setLayoutData(browse2, new GridData());
/*     */     
/* 136 */     gridData = new GridData();
/* 137 */     gridData.horizontalSpan = 2;
/* 138 */     new BooleanParameter(gSaveTorrents, "Save Torrent Backup", "ConfigView.label.savetorrentbackup").setLayoutData(gridData);
/*     */     
/*     */ 
/* 141 */     Control[] controls = { gSaveTorrents };
/* 142 */     IAdditionalActionPerformer grayPathAndButton1 = new ChangeSelectionActionPerformer(controls);
/* 143 */     saveTorrents.setAdditionalActionPerformer(grayPathAndButton1);
/*     */     
/*     */ 
/*     */ 
/* 147 */     BooleanParameter deleteTorrents = new BooleanParameter(cTorrent, "Delete Original Torrent Files", "ConfigView.label.deletetorrents");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 153 */     gridData = new GridData();
/* 154 */     gridData.horizontalSpan = 2;
/* 155 */     BooleanParameter add_stopped = new BooleanParameter(cTorrent, "Default Start Torrents Stopped", "ConfigView.label.defaultstarttorrentsstopped");
/*     */     
/*     */ 
/*     */ 
/* 159 */     add_stopped.setLayoutData(gridData);
/*     */     
/* 161 */     gridData = new GridData();
/* 162 */     gridData.horizontalSpan = 2;
/* 163 */     BooleanParameter stop_and_pause = new BooleanParameter(cTorrent, "Default Start Torrents Stopped Auto Pause", "ConfigView.label.defaultstarttorrentsstoppedandpause");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 168 */     stop_and_pause.setLayoutData(gridData);
/*     */     
/*     */ 
/* 171 */     BooleanParameter watchFolder = new BooleanParameter(cTorrent, "Watch Torrent Folder", "ConfigView.label.watchtorrentfolder");
/*     */     
/*     */ 
/* 174 */     Composite gWatchFolder = new Composite(cTorrent, 0);
/* 175 */     gridData = new GridData(768);
/* 176 */     gridData.horizontalIndent = 25;
/* 177 */     gridData.horizontalSpan = 2;
/* 178 */     Utils.setLayoutData(gWatchFolder, gridData);
/* 179 */     layout = new GridLayout();
/* 180 */     layout.marginHeight = 0;
/* 181 */     layout.marginWidth = 0;
/* 182 */     layout.numColumns = 5;
/* 183 */     gWatchFolder.setLayout(layout);
/*     */     
/* 185 */     int num_folders = COConfigurationManager.getIntParameter("Watch Torrent Folder Path Count", 1);
/*     */     
/* 187 */     for (int i = 0; i < num_folders; i++) {
/* 188 */       Label lImportDir = new Label(gWatchFolder, 0);
/* 189 */       Messages.setLanguageText(lImportDir, "ConfigView.label.importdirectory");
/*     */       
/* 191 */       gridData = new GridData(768);
/* 192 */       final StringParameter watchFolderPathParameter = new StringParameter(gWatchFolder, "Watch Torrent Folder Path" + (i == 0 ? "" : new StringBuilder().append(" ").append(i).toString()), "");
/*     */       
/* 194 */       watchFolderPathParameter.setLayoutData(gridData);
/*     */       
/* 196 */       Button browse4 = new Button(gWatchFolder, 8);
/* 197 */       browse4.setImage(imgOpenFolder);
/* 198 */       imgOpenFolder.setBackground(browse4.getBackground());
/* 199 */       browse4.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/*     */       
/* 201 */       browse4.addListener(13, new Listener() {
/*     */         public void handleEvent(Event event) {
/* 203 */           DirectoryDialog dialog = new DirectoryDialog(cTorrent.getShell(), 65536);
/* 204 */           dialog.setFilterPath(watchFolderPathParameter.getValue());
/* 205 */           dialog.setText(MessageText.getString("ConfigView.dialog.choosewatchtorrentfolderpath"));
/* 206 */           String path = dialog.open();
/* 207 */           if (path != null) {
/* 208 */             watchFolderPathParameter.setValue(path);
/*     */           }
/*     */           
/*     */         }
/* 212 */       });
/* 213 */       Label lTag = new Label(gWatchFolder, 0);
/* 214 */       Messages.setLanguageText(lTag, "label.assign.to.tag");
/*     */       
/* 216 */       StringParameter tagParam = new StringParameter(gWatchFolder, "Watch Torrent Folder Tag" + (i == 0 ? "" : new StringBuilder().append(" ").append(i).toString()), "");
/*     */       
/* 218 */       gridData = new GridData();
/* 219 */       gridData.widthHint = 60;
/* 220 */       tagParam.setLayoutData(gridData);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 225 */     Label addAnother = new Label(gWatchFolder, 0);
/* 226 */     Messages.setLanguageText(addAnother, "ConfigView.label.addanotherfolder");
/*     */     
/* 228 */     Composite gAddButton = new Composite(gWatchFolder, 0);
/* 229 */     gridData = new GridData(768);
/* 230 */     gridData.horizontalSpan = 4;
/* 231 */     Utils.setLayoutData(gAddButton, gridData);
/* 232 */     layout = new GridLayout();
/* 233 */     layout.marginHeight = 0;
/* 234 */     layout.marginWidth = 0;
/* 235 */     layout.numColumns = 2;
/* 236 */     gAddButton.setLayout(layout);
/* 237 */     Button addButton = new Button(gAddButton, 8);
/* 238 */     Messages.setLanguageText(addButton, "ConfigView.section.ipfilter.add");
/*     */     
/* 240 */     addButton.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 243 */         int num = COConfigurationManager.getIntParameter("Watch Torrent Folder Path Count", 1);
/*     */         
/* 245 */         COConfigurationManager.setParameter("Watch Torrent Folder Path Count", num + 1);
/*     */         
/* 247 */         Utils.disposeComposite(cTorrent, false);
/*     */         
/* 249 */         ConfigSectionFileTorrents.this.configSectionCreateSupport(cTorrent);
/*     */         
/* 251 */         cTorrent.layout(true, true);
/* 252 */       } });
/* 253 */     Label pad = new Label(gAddButton, 0);
/* 254 */     gridData = new GridData(768);
/* 255 */     pad.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 259 */     Label lWatchTorrentFolderInterval = new Label(gWatchFolder, 0);
/* 260 */     Messages.setLanguageText(lWatchTorrentFolderInterval, "ConfigView.label.watchtorrentfolderinterval");
/* 261 */     String sec = " " + MessageText.getString("ConfigView.section.stats.seconds");
/* 262 */     String min = " " + MessageText.getString("ConfigView.section.stats.minutes");
/* 263 */     String hr = " " + MessageText.getString("ConfigView.section.stats.hours");
/*     */     
/* 265 */     int[] watchTorrentFolderIntervalValues = { 1, 2, 3, 4, 5, 10, 30, 60, 120, 180, 240, 300, 600, 900, 1800, 3600, 7200, 14400, 21600, 28800, 43200, 57600, 72000, 86400 };
/*     */     
/*     */ 
/* 268 */     String[] watchTorrentFolderIntervalLabels = new String[watchTorrentFolderIntervalValues.length];
/*     */     
/* 270 */     for (int i = 0; i < watchTorrentFolderIntervalValues.length; i++) {
/* 271 */       int secs = watchTorrentFolderIntervalValues[i];
/* 272 */       int mins = secs / 60;
/* 273 */       int hrs = mins / 60;
/*     */       
/* 275 */       watchTorrentFolderIntervalLabels[i] = (" " + (hrs == 0 ? mins + min : secs < 60 ? secs + sec : new StringBuilder().append(hrs).append(hr).toString()));
/*     */     }
/*     */     
/* 278 */     gridData = new GridData();
/* 279 */     gridData.horizontalSpan = 4;
/* 280 */     new IntListParameter(gWatchFolder, "Watch Torrent Folder Interval Secs", watchTorrentFolderIntervalLabels, watchTorrentFolderIntervalValues).setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 286 */     gridData = new GridData();
/* 287 */     gridData.horizontalSpan = 5;
/* 288 */     new BooleanParameter(gWatchFolder, "Start Watched Torrents Stopped", "ConfigView.label.startwatchedtorrentsstopped").setLayoutData(gridData);
/*     */     
/*     */ 
/* 291 */     controls = new Control[] { gWatchFolder };
/* 292 */     watchFolder.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(controls));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionFileTorrents.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */