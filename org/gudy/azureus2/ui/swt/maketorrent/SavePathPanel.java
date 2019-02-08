/*     */ package org.gudy.azureus2.ui.swt.maketorrent;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.FileDialog;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.MessageBox;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentFactory;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
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
/*     */ public class SavePathPanel
/*     */   extends AbstractWizardPanel<NewTorrentWizard>
/*     */ {
/*     */   protected long file_size;
/*     */   protected long piece_size;
/*     */   protected long piece_count;
/*     */   
/*     */   public SavePathPanel(NewTorrentWizard wizard, AbstractWizardPanel<NewTorrentWizard> _previousPanel)
/*     */   {
/*  55 */     super(wizard, _previousPanel);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*     */     try
/*     */     {
/*  64 */       if (((NewTorrentWizard)this.wizard).create_mode == 3) {
/*  65 */         this.file_size = TOTorrentFactory.getTorrentDataSizeFromFileOrDir(((NewTorrentWizard)this.wizard).byo_desc_file, true);
/*     */       } else {
/*  67 */         this.file_size = TOTorrentFactory.getTorrentDataSizeFromFileOrDir(new File(((NewTorrentWizard)this.wizard).create_mode == 2 ? ((NewTorrentWizard)this.wizard).directoryPath : ((NewTorrentWizard)this.wizard).singlePath), false);
/*     */       }
/*  69 */       this.piece_size = TOTorrentFactory.getComputedPieceSize(this.file_size);
/*     */       
/*  71 */       this.piece_count = TOTorrentFactory.getPieceCount(this.file_size, this.piece_size);
/*     */     } catch (Throwable e) {
/*  73 */       Debug.printStackTrace(e);
/*     */     }
/*  75 */     ((NewTorrentWizard)this.wizard).setTitle(MessageText.getString("wizard.torrentFile"));
/*  76 */     ((NewTorrentWizard)this.wizard).setCurrentInfo(MessageText.getString("wizard.choosetorrent"));
/*  77 */     Composite panel = ((NewTorrentWizard)this.wizard).getPanel();
/*  78 */     GridLayout layout = new GridLayout();
/*  79 */     layout.numColumns = 3;
/*  80 */     panel.setLayout(layout);
/*     */     
/*     */ 
/*  83 */     final Text file = new Text(panel, 2048);
/*     */     
/*  85 */     file.addModifyListener(new ModifyListener()
/*     */     {
/*     */ 
/*     */       public void modifyText(ModifyEvent arg0)
/*     */       {
/*  90 */         String fName = file.getText();
/*  91 */         ((NewTorrentWizard)SavePathPanel.this.wizard).savePath = fName;
/*  92 */         String error = "";
/*  93 */         if (!fName.equals("")) {
/*  94 */           File f = new File(file.getText());
/*  95 */           if ((f.isDirectory()) || ((f.getParentFile() != null) && (!f.getParentFile().canWrite()))) {
/*  96 */             error = MessageText.getString("wizard.invalidfile");
/*     */           } else {
/*  98 */             String parent = f.getParent();
/*     */             
/* 100 */             if (parent != null)
/*     */             {
/* 102 */               ((NewTorrentWizard)SavePathPanel.this.wizard).setDefaultSaveDir(parent);
/*     */             }
/*     */           }
/*     */         }
/* 106 */         ((NewTorrentWizard)SavePathPanel.this.wizard).setErrorMessage(error);
/* 107 */         ((NewTorrentWizard)SavePathPanel.this.wizard).setFinishEnabled((!((NewTorrentWizard)SavePathPanel.this.wizard).savePath.equals("")) && (error.equals("")));
/*     */       }
/*     */       
/* 110 */     });
/* 111 */     String default_save = ((NewTorrentWizard)this.wizard).getDefaultSaveDir();
/*     */     
/*     */ 
/*     */     String target_file;
/*     */     
/*     */ 
/* 117 */     if (((NewTorrentWizard)this.wizard).create_mode == 3) {
/* 118 */       String target_file = "";
/*     */       
/* 120 */       if (((NewTorrentWizard)this.wizard).byo_map != null) {
/* 121 */         List list = (List)((NewTorrentWizard)this.wizard).byo_map.get("file_map");
/* 122 */         if (list != null) {
/* 123 */           Map map = (Map)list.get(0);
/* 124 */           if (map != null) {
/* 125 */             List path = (List)map.get("logical_path");
/* 126 */             if (path != null) {
/* 127 */               target_file = new File(COConfigurationManager.getStringParameter("General_sDefaultTorrent_Directory"), (String)path.get(0) + ".torrent").getAbsolutePath();
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/*     */       String target_file;
/* 135 */       if (((NewTorrentWizard)this.wizard).create_mode == 2)
/*     */       {
/* 137 */         target_file = ((NewTorrentWizard)this.wizard).directoryPath + ".torrent";
/*     */       }
/*     */       else
/*     */       {
/* 141 */         target_file = ((NewTorrentWizard)this.wizard).singlePath + ".torrent";
/*     */       }
/*     */     }
/*     */     
/* 145 */     if ((default_save.length() > 0) && (target_file.length() > 0))
/*     */     {
/* 147 */       File temp = new File(target_file);
/*     */       
/* 149 */       String existing_parent = temp.getParent();
/*     */       
/* 151 */       if (existing_parent != null)
/*     */       {
/* 153 */         target_file = new File(default_save, temp.getName()).toString();
/*     */       }
/*     */     }
/*     */     
/* 157 */     ((NewTorrentWizard)this.wizard).savePath = target_file;
/*     */     
/* 159 */     file.setText(((NewTorrentWizard)this.wizard).savePath);
/* 160 */     GridData gridData = new GridData(768);
/* 161 */     gridData.horizontalSpan = 2;
/* 162 */     Utils.setLayoutData(file, gridData);
/* 163 */     Button browse = new Button(panel, 8);
/* 164 */     browse.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/* 169 */         FileDialog fd = new FileDialog(((NewTorrentWizard)SavePathPanel.this.wizard).getWizardWindow(), 8192);
/* 170 */         String path = ((NewTorrentWizard)SavePathPanel.this.wizard).savePath;
/* 171 */         if ((((NewTorrentWizard)SavePathPanel.this.wizard).getErrorMessage().equals("")) && (!path.equals(""))) {
/* 172 */           File fsPath = new File(path);
/* 173 */           if (!path.endsWith(File.separator)) {
/* 174 */             fd.setFilterPath(fsPath.getParent());
/* 175 */             fd.setFileName(fsPath.getName());
/*     */           }
/*     */           else {
/* 178 */             fd.setFileName(path);
/*     */           }
/*     */         }
/* 181 */         String f = fd.open();
/* 182 */         if (f != null) {
/* 183 */           file.setText(f);
/*     */           
/* 185 */           File ff = new File(f);
/*     */           
/* 187 */           String parent = ff.getParent();
/*     */           
/* 189 */           if (parent != null) {
/* 190 */             ((NewTorrentWizard)SavePathPanel.this.wizard).setDefaultSaveDir(parent);
/*     */           }
/*     */         }
/*     */       }
/* 194 */     });
/* 195 */     Messages.setLanguageText(browse, "wizard.browse");
/*     */     
/*     */ 
/*     */ 
/* 199 */     Label label = new Label(panel, 258);
/* 200 */     gridData = new GridData(768);
/* 201 */     gridData.horizontalSpan = 3;
/* 202 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 204 */     Composite gFileStuff = new Composite(panel, 0);
/* 205 */     gridData = new GridData(272);
/* 206 */     gridData.horizontalSpan = 3;
/* 207 */     Utils.setLayoutData(gFileStuff, gridData);
/* 208 */     layout = new GridLayout();
/* 209 */     layout.numColumns = 4;
/* 210 */     gFileStuff.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 214 */     label = new Label(gFileStuff, 0);
/* 215 */     Messages.setLanguageText(label, "wizard.maketorrent.filesize");
/*     */     
/* 217 */     Label file_size_label = new Label(gFileStuff, 0);
/* 218 */     file_size_label.setText(DisplayFormatters.formatByteCountToKiBEtc(this.file_size));
/*     */     
/* 220 */     label = new Label(gFileStuff, 0);
/* 221 */     label = new Label(gFileStuff, 0);
/*     */     
/*     */ 
/*     */ 
/* 225 */     label = new Label(gFileStuff, 0);
/* 226 */     Messages.setLanguageText(label, "wizard.maketorrent.piececount");
/*     */     
/* 228 */     final Label piece_count_label = new Label(gFileStuff, 0);
/* 229 */     piece_count_label.setText("" + this.piece_count);
/* 230 */     label = new Label(gFileStuff, 0);
/* 231 */     label = new Label(gFileStuff, 0);
/*     */     
/*     */ 
/*     */ 
/* 235 */     label = new Label(gFileStuff, 0);
/* 236 */     Messages.setLanguageText(label, "wizard.maketorrent.piecesize");
/*     */     
/* 238 */     final Label piece_size_label = new Label(gFileStuff, 0);
/* 239 */     gridData = new GridData();
/* 240 */     gridData.widthHint = 75;
/* 241 */     Utils.setLayoutData(piece_size_label, gridData);
/* 242 */     piece_size_label.setText(DisplayFormatters.formatByteCountToKiBEtc(this.piece_size));
/*     */     
/* 244 */     final Combo manual = new Combo(gFileStuff, 12);
/*     */     
/* 246 */     final long[] sizes = TOTorrentFactory.STANDARD_PIECE_SIZES;
/*     */     
/* 248 */     manual.add(MessageText.getString("wizard.maketorrent.auto"));
/*     */     
/* 250 */     for (int i = 0; i < sizes.length; i++) {
/* 251 */       manual.add(DisplayFormatters.formatByteCountToKiBEtc(sizes[i]));
/*     */     }
/*     */     
/* 254 */     manual.select(0);
/*     */     
/* 256 */     manual.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event e)
/*     */       {
/* 261 */         int index = manual.getSelectionIndex();
/*     */         
/* 263 */         if (index == 0)
/*     */         {
/* 265 */           ((NewTorrentWizard)SavePathPanel.this.wizard).setPieceSizeComputed();
/*     */           
/* 267 */           SavePathPanel.this.piece_size = TOTorrentFactory.getComputedPieceSize(SavePathPanel.this.file_size);
/*     */         }
/*     */         else {
/* 270 */           SavePathPanel.this.piece_size = sizes[(index - 1)];
/*     */           
/* 272 */           ((NewTorrentWizard)SavePathPanel.this.wizard).setPieceSizeManual(SavePathPanel.this.piece_size);
/*     */         }
/*     */         
/* 275 */         SavePathPanel.this.piece_count = TOTorrentFactory.getPieceCount(SavePathPanel.this.file_size, SavePathPanel.this.piece_size);
/*     */         
/* 277 */         piece_size_label.setText(DisplayFormatters.formatByteCountToKiBEtc(SavePathPanel.this.piece_size));
/* 278 */         piece_count_label.setText("" + SavePathPanel.this.piece_count);
/*     */       }
/*     */       
/* 281 */     });
/* 282 */     label = new Label(gFileStuff, 0);
/*     */     
/*     */ 
/* 285 */     label = new Label(panel, 258);
/* 286 */     gridData = new GridData(768);
/* 287 */     gridData.horizontalSpan = 3;
/* 288 */     Utils.setLayoutData(label, gridData);
/*     */     
/* 290 */     final Button bAutoOpen = new Button(panel, 32);
/* 291 */     Messages.setLanguageText(bAutoOpen, "wizard.maketorrents.autoopen");
/* 292 */     gridData = new GridData(768);
/* 293 */     gridData.horizontalSpan = 3;
/* 294 */     Utils.setLayoutData(bAutoOpen, gridData);
/*     */     
/* 296 */     final Button bforce = new Button(panel, 32);
/* 297 */     Messages.setLanguageText(bforce, "wizard.maketorrents.force");
/* 298 */     gridData = new GridData(768);
/* 299 */     gridData.horizontalSpan = 3;
/* 300 */     Utils.setLayoutData(bforce, gridData);
/*     */     
/* 302 */     final Button bSuperSeed = new Button(panel, 32);
/* 303 */     Messages.setLanguageText(bSuperSeed, "wizard.maketorrents.superseed");
/* 304 */     gridData = new GridData(768);
/* 305 */     gridData.horizontalSpan = 3;
/* 306 */     Utils.setLayoutData(bSuperSeed, gridData);
/*     */     
/* 308 */     final Button bAutoHost = new Button(panel, 32);
/* 309 */     Messages.setLanguageText(bAutoHost, "wizard.maketorrents.autohost");
/* 310 */     gridData = new GridData(768);
/* 311 */     gridData.horizontalSpan = 3;
/* 312 */     Utils.setLayoutData(bAutoHost, gridData);
/*     */     
/* 314 */     label = new Label(panel, 0);
/* 315 */     Messages.setLanguageText(label, "wizard.maketorrents.init.tags");
/* 316 */     final Text tag_area = new Text(panel, 2048);
/* 317 */     gridData = new GridData(768);
/* 318 */     gridData.horizontalSpan = 2;
/* 319 */     Utils.setLayoutData(tag_area, gridData);
/*     */     
/* 321 */     bforce.setEnabled(false);
/* 322 */     tag_area.setEnabled(false);
/* 323 */     bSuperSeed.setEnabled(false);
/* 324 */     bAutoHost.setEnabled(false);
/*     */     
/* 326 */     bAutoOpen.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 328 */         boolean autoOpen = ((NewTorrentWizard)SavePathPanel.this.wizard).autoOpen = bAutoOpen.getSelection();
/*     */         
/* 330 */         boolean enable = (autoOpen) && (((NewTorrentWizard)SavePathPanel.this.wizard).getTrackerType() != 2);
/*     */         
/* 332 */         bforce.setEnabled(autoOpen);
/* 333 */         tag_area.setEnabled(autoOpen);
/* 334 */         bSuperSeed.setEnabled(autoOpen);
/* 335 */         bAutoHost.setEnabled(enable);
/*     */       }
/*     */       
/* 338 */     });
/* 339 */     bforce.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 341 */         ((NewTorrentWizard)SavePathPanel.this.wizard).forceStart = bforce.getSelection();
/*     */       }
/*     */       
/* 344 */     });
/* 345 */     tag_area.setText(((NewTorrentWizard)this.wizard).getInitialTags(false));
/* 346 */     tag_area.addModifyListener(new ModifyListener() {
/*     */       public void modifyText(ModifyEvent arg0) {
/* 348 */         ((NewTorrentWizard)SavePathPanel.this.wizard).setInitialTags(tag_area.getText().trim());
/*     */       }
/*     */       
/*     */ 
/* 352 */     });
/* 353 */     bSuperSeed.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 355 */         ((NewTorrentWizard)SavePathPanel.this.wizard).superseed = bSuperSeed.getSelection();
/*     */       }
/*     */       
/* 358 */     });
/* 359 */     bAutoHost.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 361 */         ((NewTorrentWizard)SavePathPanel.this.wizard).autoHost = bAutoHost.getSelection();
/*     */       }
/*     */       
/* 364 */     });
/* 365 */     final Button bPrivateTorrent = new Button(panel, 32);
/* 366 */     Messages.setLanguageText(bPrivateTorrent, "ConfigView.section.sharing.privatetorrent");
/* 367 */     gridData = new GridData(768);
/* 368 */     gridData.horizontalSpan = 3;
/* 369 */     Utils.setLayoutData(bPrivateTorrent, gridData);
/*     */     
/*     */ 
/* 372 */     final Button bAllowDHT = new Button(panel, 32);
/* 373 */     Messages.setLanguageText(bAllowDHT, "ConfigView.section.sharing.permitdht");
/* 374 */     gridData = new GridData(768);
/* 375 */     gridData.horizontalSpan = 3;
/* 376 */     Utils.setLayoutData(bAllowDHT, gridData);
/* 377 */     bAllowDHT.setSelection(true);
/*     */     
/* 379 */     bAllowDHT.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 381 */         ((NewTorrentWizard)SavePathPanel.this.wizard).permitDHT = bAllowDHT.getSelection();
/*     */       }
/*     */     });
/*     */     
/*     */ 
/*     */ 
/* 387 */     if (((NewTorrentWizard)this.wizard).getTrackerType() == 3)
/*     */     {
/* 389 */       ((NewTorrentWizard)this.wizard).setPrivateTorrent(false);
/*     */     }
/*     */     
/* 392 */     boolean privateTorrent = ((NewTorrentWizard)this.wizard).getPrivateTorrent();
/*     */     
/* 394 */     bAllowDHT.setEnabled(!privateTorrent);
/* 395 */     if (privateTorrent)
/*     */     {
/* 397 */       bAllowDHT.setSelection(false);
/* 398 */       ((NewTorrentWizard)this.wizard).permitDHT = false;
/*     */     }
/*     */     
/* 401 */     bPrivateTorrent.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 403 */         boolean privateTorrent = bPrivateTorrent.getSelection();
/*     */         
/* 405 */         ((NewTorrentWizard)SavePathPanel.this.wizard).setPrivateTorrent(privateTorrent);
/*     */         
/* 407 */         if (privateTorrent)
/*     */         {
/* 409 */           bAllowDHT.setSelection(false);
/* 410 */           ((NewTorrentWizard)SavePathPanel.this.wizard).permitDHT = false;
/*     */         }
/* 412 */         bAllowDHT.setEnabled(!privateTorrent);
/*     */       }
/*     */     });
/*     */     
/* 416 */     if (((NewTorrentWizard)this.wizard).getTrackerType() == 3)
/*     */     {
/* 418 */       bAllowDHT.setEnabled(false);
/* 419 */       bPrivateTorrent.setEnabled(false);
/*     */     } else {
/* 421 */       bPrivateTorrent.setSelection(privateTorrent);
/*     */     }
/*     */   }
/*     */   
/*     */   public IWizardPanel<NewTorrentWizard> getFinishPanel() {
/* 426 */     return new ProgressPanel((NewTorrentWizard)this.wizard, this);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isFinishSelectionOK()
/*     */   {
/* 432 */     String save_path = ((NewTorrentWizard)this.wizard).savePath;
/*     */     
/* 434 */     File f = new File(save_path);
/*     */     
/* 436 */     if (f.isFile()) {
/* 437 */       MessageBox mb = new MessageBox(((NewTorrentWizard)this.wizard).getWizardWindow(), 196);
/*     */       
/* 439 */       mb.setText(MessageText.getString("exportTorrentWizard.process.outputfileexists.title"));
/*     */       
/* 441 */       mb.setMessage(MessageText.getString("exportTorrentWizard.process.outputfileexists.message"));
/*     */       
/* 443 */       int result = mb.open();
/*     */       
/* 445 */       if (result == 128)
/*     */       {
/* 447 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 451 */     return true;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/maketorrent/SavePathPanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */