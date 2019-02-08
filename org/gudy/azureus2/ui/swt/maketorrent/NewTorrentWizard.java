/*     */ package org.gudy.azureus2.ui.swt.maketorrent;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.dnd.DropTarget;
/*     */ import org.eclipse.swt.dnd.DropTargetAdapter;
/*     */ import org.eclipse.swt.dnd.DropTargetEvent;
/*     */ import org.eclipse.swt.dnd.FileTransfer;
/*     */ import org.eclipse.swt.dnd.Transfer;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.torrent.TOTorrentCreator;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.ui.swt.URLTransfer;
/*     */ import org.gudy.azureus2.ui.swt.URLTransfer.URLType;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.wizard.AbstractWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.IWizardPanel;
/*     */ import org.gudy.azureus2.ui.swt.wizard.Wizard;
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
/*     */ public class NewTorrentWizard
/*     */   extends Wizard
/*     */ {
/*     */   static final int TT_LOCAL = 1;
/*     */   static final int TT_EXTERNAL = 2;
/*     */   static final int TT_DECENTRAL = 3;
/*     */   static final String TT_EXTERNAL_DEFAULT = "http://";
/*  53 */   static final String TT_DECENTRAL_DEFAULT = TorrentUtils.getDecentralisedEmptyURL().toString();
/*     */   
/*  55 */   private static String default_open_dir = COConfigurationManager.getStringParameter("CreateTorrent.default.open", "");
/*  56 */   private static String default_save_dir = COConfigurationManager.getStringParameter("CreateTorrent.default.save", "");
/*  57 */   private static String comment = COConfigurationManager.getStringParameter("CreateTorrent.default.comment", "");
/*  58 */   private static int tracker_type = COConfigurationManager.getIntParameter("CreateTorrent.default.trackertype", 1);
/*     */   protected static final int MODE_SINGLE_FILE = 1;
/*     */   protected static final int MODE_DIRECTORY = 2;
/*     */   protected static final int MODE_BYO = 3;
/*     */   
/*  63 */   static { if (default_save_dir.length() == 0)
/*     */     {
/*  65 */       default_save_dir = COConfigurationManager.getStringParameter("General_sDefaultTorrent_Directory", "");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  74 */   int create_mode = 3;
/*  75 */   String singlePath = "";
/*  76 */   String directoryPath = "";
/*  77 */   String savePath = "";
/*     */   
/*     */   File byo_desc_file;
/*     */   
/*     */   Map byo_map;
/*  82 */   String trackerURL = "http://";
/*     */   
/*  84 */   boolean computed_piece_size = true;
/*     */   
/*     */   long manual_piece_size;
/*  87 */   boolean useMultiTracker = false;
/*  88 */   boolean useWebSeed = false;
/*     */   
/*  90 */   private boolean addOtherHashes = COConfigurationManager.getBooleanParameter("CreateTorrent.default.addhashes", false);
/*     */   
/*     */ 
/*  93 */   String multiTrackerConfig = "";
/*  94 */   List trackers = new ArrayList();
/*     */   
/*  96 */   String webSeedConfig = "";
/*  97 */   Map webseeds = new HashMap();
/*     */   
/*  99 */   boolean autoOpen = false;
/* 100 */   boolean autoHost = false;
/* 101 */   boolean forceStart = false;
/* 102 */   String initialTags = COConfigurationManager.getStringParameter("CreateTorrent.default.initialTags", "");
/* 103 */   boolean superseed = false;
/* 104 */   boolean permitDHT = true;
/*     */   
/* 106 */   TOTorrentCreator creator = null;
/*     */   
/*     */ 
/*     */ 
/*     */   public NewTorrentWizard(Display display)
/*     */   {
/* 112 */     super("wizard.title");
/*     */     
/* 114 */     this.cancel.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 116 */         if (NewTorrentWizard.this.creator != null) { NewTorrentWizard.this.creator.cancel();
/*     */         }
/*     */       }
/* 119 */     });
/* 120 */     this.trackers.add(new ArrayList());
/* 121 */     this.trackerURL = Utils.getLinkFromClipboard(display, false, false);
/* 122 */     ModePanel panel = new ModePanel(this, null);
/* 123 */     createDropTarget(getWizardWindow());
/* 124 */     setFirstPanel(panel);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected int getTrackerType()
/*     */   {
/* 131 */     return tracker_type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setTrackerType(int type)
/*     */   {
/* 138 */     tracker_type = type;
/*     */     
/* 140 */     COConfigurationManager.setParameter("CreateTorrent.default.trackertype", tracker_type);
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getDefaultOpenDir()
/*     */   {
/* 146 */     return default_open_dir;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setDefaultOpenDir(String d)
/*     */   {
/* 153 */     default_open_dir = d;
/*     */     
/* 155 */     COConfigurationManager.setParameter("CreateTorrent.default.open", default_open_dir);
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getDefaultSaveDir()
/*     */   {
/* 161 */     return default_save_dir;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setDefaultSaveDir(String d)
/*     */   {
/* 168 */     default_save_dir = d;
/*     */     
/* 170 */     COConfigurationManager.setParameter("CreateTorrent.default.save", default_save_dir);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String getInitialTags(boolean save)
/*     */   {
/* 177 */     if (save) {
/* 178 */       COConfigurationManager.setParameter("CreateTorrent.default.initialTags", this.initialTags);
/*     */     }
/* 180 */     return this.initialTags;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setInitialTags(String tags)
/*     */   {
/* 187 */     this.initialTags = tags;
/*     */   }
/*     */   
/*     */   void setComment(String s) {
/* 191 */     comment = s;
/*     */     
/* 193 */     COConfigurationManager.setParameter("CreateTorrent.default.comment", comment);
/*     */   }
/*     */   
/*     */ 
/* 197 */   String getComment() { return comment; }
/*     */   
/*     */   private void createDropTarget(Control control) {
/* 200 */     DropTarget dropTarget = new DropTarget(control, 23);
/* 201 */     dropTarget.setTransfer(new Transfer[] { URLTransfer.getInstance(), FileTransfer.getInstance() });
/* 202 */     dropTarget.addDropListener(new DropTargetAdapter() {
/*     */       public void dragOver(DropTargetEvent event) {
/* 204 */         if (URLTransfer.getInstance().isSupportedType(event.currentDataType))
/* 205 */           event.detail = ((NewTorrentWizard.this.getCurrentPanel() instanceof ModePanel) ? 4 : 0);
/*     */       }
/*     */       
/*     */       public void drop(DropTargetEvent event) {
/* 209 */         if ((event.data instanceof String[])) {
/* 210 */           String[] sourceNames = (String[])event.data;
/* 211 */           if (sourceNames == null)
/* 212 */             event.detail = 0;
/* 213 */           if (event.detail == 0) {
/* 214 */             return;
/*     */           }
/* 216 */           for (String droppedFileStr : sourceNames) {
/* 217 */             File droppedFile = new File(droppedFileStr);
/* 218 */             if ((NewTorrentWizard.this.getCurrentPanel() instanceof ModePanel)) break;
/* 219 */             if ((NewTorrentWizard.this.getCurrentPanel() instanceof DirectoryPanel)) {
/* 220 */               if (!droppedFile.isDirectory()) break;
/* 221 */               ((DirectoryPanel)NewTorrentWizard.this.getCurrentPanel()).setFilename(droppedFile.getAbsolutePath()); break; }
/* 222 */             if ((NewTorrentWizard.this.getCurrentPanel() instanceof SingleFilePanel)) {
/* 223 */               if (!droppedFile.isFile()) break;
/* 224 */               ((SingleFilePanel)NewTorrentWizard.this.getCurrentPanel()).setFilename(droppedFile.getAbsolutePath()); break; }
/* 225 */             if (!(NewTorrentWizard.this.getCurrentPanel() instanceof BYOPanel)) break;
/* 226 */             ((BYOPanel)NewTorrentWizard.this.getCurrentPanel()).addFilename(droppedFile);
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */         }
/* 232 */         else if ((NewTorrentWizard.this.getCurrentPanel() instanceof ModePanel)) {
/* 233 */           NewTorrentWizard.this.trackerURL = ((URLTransfer.URLType)event.data).linkURL;
/* 234 */           ((ModePanel)NewTorrentWizard.this.getCurrentPanel()).updateTrackerURL();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setPieceSizeComputed()
/*     */   {
/* 243 */     this.computed_piece_size = true;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean getPieceSizeComputed()
/*     */   {
/* 249 */     return this.computed_piece_size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setPieceSizeManual(long _value)
/*     */   {
/* 256 */     this.computed_piece_size = false;
/* 257 */     this.manual_piece_size = _value;
/*     */   }
/*     */   
/*     */ 
/*     */   protected long getPieceSizeManual()
/*     */   {
/* 263 */     return this.manual_piece_size;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setAddOtherHashes(boolean o)
/*     */   {
/* 270 */     this.addOtherHashes = o;
/*     */     
/* 272 */     COConfigurationManager.setParameter("CreateTorrent.default.addhashes", this.addOtherHashes);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected boolean getPrivateTorrent()
/*     */   {
/* 279 */     return COConfigurationManager.getBooleanParameter("CreateTorrent.default.privatetorrent", false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setPrivateTorrent(boolean privateTorrent)
/*     */   {
/* 286 */     COConfigurationManager.setParameter("CreateTorrent.default.privatetorrent", privateTorrent);
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean getAddOtherHashes()
/*     */   {
/* 292 */     return this.addOtherHashes;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected IWizardPanel<NewTorrentWizard> getNextPanelForMode(AbstractWizardPanel<NewTorrentWizard> prev)
/*     */   {
/* 299 */     switch (this.create_mode) {
/*     */     case 2: 
/* 301 */       return new DirectoryPanel(this, prev);
/*     */     case 1: 
/* 303 */       return new SingleFilePanel(this, prev);
/*     */     }
/* 305 */     return new BYOPanel(this, prev);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/maketorrent/NewTorrentWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */