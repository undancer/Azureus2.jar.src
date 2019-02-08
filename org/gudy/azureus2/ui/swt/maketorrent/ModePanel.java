/*     */ package org.gudy.azureus2.ui.swt.maketorrent;
/*     */ 
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.tracker.util.TRTrackerUtils;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.TrackersUtil;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
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
/*     */ 
/*     */ 
/*     */ public class ModePanel
/*     */   extends AbstractWizardPanel<NewTorrentWizard>
/*     */ {
/*     */   private Combo tracker;
/*     */   
/*     */   public ModePanel(NewTorrentWizard wizard, AbstractWizardPanel previous)
/*     */   {
/*  55 */     super(wizard, previous);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void show()
/*     */   {
/*  64 */     final NewTorrentWizard wizard = (NewTorrentWizard)this.wizard;
/*  65 */     wizard.setTitle(MessageText.getString("wizard.mode"));
/*  66 */     wizard.setCurrentInfo(MessageText.getString("wizard.singlefile.help"));
/*  67 */     Composite rootPanel = wizard.getPanel();
/*  68 */     GridLayout layout = new GridLayout();
/*  69 */     layout.numColumns = 1;
/*  70 */     rootPanel.setLayout(layout);
/*     */     
/*  72 */     Composite panel = new Composite(rootPanel, 4194304);
/*  73 */     GridData gridData = new GridData(772);
/*  74 */     Utils.setLayoutData(panel, gridData);
/*  75 */     layout = new GridLayout();
/*  76 */     layout.numColumns = 4;
/*  77 */     panel.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  82 */     final Button btnLocalTracker = new Button(panel, 16);
/*  83 */     Messages.setLanguageText(btnLocalTracker, "wizard.tracker.local");
/*  84 */     gridData = new GridData();
/*  85 */     gridData.horizontalSpan = 2;
/*  86 */     Utils.setLayoutData(btnLocalTracker, gridData);
/*     */     
/*  88 */     final Button btnSSL = new Button(panel, 32);
/*  89 */     Messages.setLanguageText(btnSSL, "wizard.tracker.ssl");
/*  90 */     gridData = new GridData(128);
/*  91 */     gridData.horizontalSpan = 2;
/*  92 */     Utils.setLayoutData(btnSSL, gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  97 */     final String localTrackerHost = COConfigurationManager.getStringParameter("Tracker IP", "");
/*  98 */     final int localTrackerPort = COConfigurationManager.getIntParameter("Tracker Port", 6969);
/*  99 */     final int localTrackerPortSSL = COConfigurationManager.getIntParameter("Tracker Port SSL", 7000);
/* 100 */     final boolean SSLEnabled = COConfigurationManager.getBooleanParameter("Tracker Port SSL Enable", false);
/*     */     
/* 102 */     final String[] localTrackerUrl = new String[1];
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 107 */     boolean showLocal = TRTrackerUtils.isTrackerEnabled();
/*     */     
/* 109 */     final Label labelLocalAnnounce = showLocal ? new Label(panel, 0) : null;
/*     */     
/* 111 */     final Label localTrackerValue = new Label(panel, 0);
/*     */     
/* 113 */     if (showLocal)
/*     */     {
/* 115 */       Messages.setLanguageText(labelLocalAnnounce, "wizard.announceUrl");
/*     */       
/* 117 */       localTrackerUrl[0] = ("http://" + UrlUtils.convertIPV6Host(localTrackerHost) + ":" + localTrackerPort + "/announce");
/* 118 */       localTrackerValue.setText(localTrackerUrl[0]);
/* 119 */       btnSSL.setEnabled(SSLEnabled);
/*     */       
/* 121 */       gridData = new GridData();
/* 122 */       gridData.horizontalSpan = 3;
/*     */     }
/*     */     else
/*     */     {
/* 126 */       localTrackerUrl[0] = "";
/* 127 */       Messages.setLanguageText(localTrackerValue, "wizard.tracker.howToLocal");
/* 128 */       btnLocalTracker.setSelection(false);
/* 129 */       btnSSL.setEnabled(false);
/* 130 */       btnLocalTracker.setEnabled(false);
/* 131 */       localTrackerValue.setEnabled(true);
/*     */       
/* 133 */       if (wizard.getTrackerType() == 1)
/*     */       {
/* 135 */         wizard.setTrackerType(2);
/*     */       }
/*     */       
/* 138 */       gridData = new GridData();
/* 139 */       gridData.horizontalSpan = 4;
/*     */     }
/*     */     
/* 142 */     Utils.setLayoutData(localTrackerValue, gridData);
/*     */     
/* 144 */     int tracker_type = wizard.getTrackerType();
/*     */     
/* 146 */     if (tracker_type == 1)
/*     */     {
/* 148 */       setTrackerUrl(localTrackerUrl[0]);
/*     */     }
/* 150 */     else if (tracker_type == 2)
/*     */     {
/* 152 */       setTrackerUrl("http://");
/*     */     }
/*     */     else
/*     */     {
/* 156 */       setTrackerUrl(NewTorrentWizard.TT_DECENTRAL_DEFAULT);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 162 */     final Button btnExternalTracker = new Button(panel, 16);
/* 163 */     Messages.setLanguageText(btnExternalTracker, "wizard.tracker.external");
/* 164 */     gridData = new GridData();
/* 165 */     gridData.horizontalSpan = 4;
/* 166 */     Utils.setLayoutData(btnExternalTracker, gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 171 */     final Label labelExternalAnnounce = new Label(panel, 0);
/* 172 */     Messages.setLanguageText(labelExternalAnnounce, "wizard.announceUrl");
/*     */     
/* 174 */     btnLocalTracker.setSelection(tracker_type == 1);
/* 175 */     if (showLocal) localTrackerValue.setEnabled(tracker_type == 1);
/* 176 */     btnSSL.setEnabled((SSLEnabled) && (tracker_type == 1));
/*     */     
/* 178 */     btnExternalTracker.setSelection(tracker_type == 2);
/* 179 */     labelExternalAnnounce.setEnabled(tracker_type == 2);
/*     */     
/*     */ 
/*     */ 
/* 183 */     this.tracker = new Combo(panel, 0);
/* 184 */     gridData = new GridData(768);
/* 185 */     gridData.horizontalSpan = 3;
/* 186 */     Utils.setLayoutData(this.tracker, gridData);
/* 187 */     List trackers = TrackersUtil.getInstance().getTrackersList();
/* 188 */     Iterator iter = trackers.iterator();
/* 189 */     while (iter.hasNext()) {
/* 190 */       this.tracker.add((String)iter.next());
/*     */     }
/*     */     
/* 193 */     this.tracker.addModifyListener(new ModifyListener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void modifyText(ModifyEvent arg0)
/*     */       {
/*     */ 
/* 200 */         String text = ModePanel.this.tracker.getText();
/* 201 */         ModePanel.this.setTrackerUrl(text);
/*     */         
/* 203 */         boolean valid = true;
/* 204 */         String errorMessage = "";
/*     */         try {
/* 206 */           new URL(text);
/*     */         } catch (MalformedURLException e) {
/* 208 */           valid = false;
/* 209 */           errorMessage = MessageText.getString("wizard.invalidurl");
/*     */         }
/* 211 */         wizard.setErrorMessage(errorMessage);
/* 212 */         wizard.setNextEnabled(valid);
/*     */       }
/*     */       
/*     */ 
/* 216 */     });
/* 217 */     this.tracker.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 219 */         String text = ModePanel.this.tracker.getText();
/* 220 */         ModePanel.this.setTrackerUrl(text);
/*     */         
/* 222 */         boolean valid = true;
/* 223 */         String errorMessage = "";
/*     */         try {
/* 225 */           new URL(text);
/*     */         } catch (MalformedURLException ex) {
/* 227 */           valid = false;
/* 228 */           errorMessage = MessageText.getString("wizard.invalidurl");
/*     */         }
/* 230 */         wizard.setErrorMessage(errorMessage);
/* 231 */         wizard.setNextEnabled(valid);
/*     */       }
/*     */       
/* 234 */     });
/* 235 */     updateTrackerURL();
/*     */     
/* 237 */     this.tracker.setEnabled(tracker_type == 2);
/*     */     
/* 239 */     new Label(panel, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 245 */     final Button btnDHTTracker = new Button(panel, 16);
/* 246 */     Messages.setLanguageText(btnDHTTracker, "wizard.tracker.dht");
/* 247 */     gridData = new GridData();
/* 248 */     gridData.horizontalSpan = 4;
/* 249 */     Utils.setLayoutData(btnDHTTracker, gridData);
/*     */     
/* 251 */     btnDHTTracker.setSelection(tracker_type == 3);
/*     */     
/*     */ 
/*     */ 
/* 255 */     panel = new Composite(rootPanel, 4194304);
/* 256 */     gridData = new GridData(772);
/* 257 */     Utils.setLayoutData(panel, gridData);
/* 258 */     layout = new GridLayout();
/* 259 */     layout.numColumns = 4;
/* 260 */     panel.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 265 */     Label label = new Label(panel, 258);
/* 266 */     gridData = new GridData(768);
/* 267 */     gridData.horizontalSpan = 4;
/* 268 */     Utils.setLayoutData(label, gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 273 */     final Button btnMultiTracker = new Button(panel, 32);
/* 274 */     Messages.setLanguageText(btnMultiTracker, "wizard.multitracker");
/* 275 */     gridData = new GridData();
/* 276 */     gridData.horizontalSpan = 4;
/* 277 */     Utils.setLayoutData(btnMultiTracker, gridData);
/* 278 */     btnMultiTracker.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event arg0) {
/* 281 */         wizard.useMultiTracker = btnMultiTracker.getSelection();
/*     */       }
/* 283 */     });
/* 284 */     btnMultiTracker.setSelection(wizard.useMultiTracker);
/*     */     
/* 286 */     btnMultiTracker.setEnabled(tracker_type != 3);
/*     */     
/* 288 */     final Button btnWebSeed = new Button(panel, 32);
/* 289 */     Messages.setLanguageText(btnWebSeed, "wizard.webseed");
/* 290 */     gridData = new GridData();
/* 291 */     gridData.horizontalSpan = 4;
/* 292 */     Utils.setLayoutData(btnWebSeed, gridData);
/* 293 */     btnWebSeed.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event arg0) {
/* 296 */         wizard.useWebSeed = btnWebSeed.getSelection();
/*     */       }
/* 298 */     });
/* 299 */     btnWebSeed.setSelection(wizard.useWebSeed);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 304 */     final Button btnExtraHashes = new Button(panel, 32);
/* 305 */     Messages.setLanguageText(btnExtraHashes, "wizard.createtorrent.extrahashes");
/* 306 */     gridData = new GridData();
/* 307 */     gridData.horizontalSpan = 4;
/* 308 */     Utils.setLayoutData(btnExtraHashes, gridData);
/* 309 */     btnExtraHashes.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event arg0) {
/* 312 */         wizard.setAddOtherHashes(btnExtraHashes.getSelection());
/*     */       }
/* 314 */     });
/* 315 */     btnExtraHashes.setSelection(wizard.getAddOtherHashes());
/*     */     
/*     */ 
/*     */ 
/* 319 */     panel = new Composite(rootPanel, 0);
/* 320 */     gridData = new GridData(772);
/* 321 */     Utils.setLayoutData(panel, gridData);
/* 322 */     layout = new GridLayout();
/* 323 */     layout.numColumns = 6;
/* 324 */     panel.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 329 */     Label label1 = new Label(panel, 258);
/* 330 */     gridData = new GridData(768);
/* 331 */     gridData.horizontalSpan = 6;
/* 332 */     label1.setLayoutData(gridData);
/*     */     
/* 334 */     activateMode(3);
/*     */     
/* 336 */     btnSSL.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/*     */         String url;
/*     */         String url;
/* 340 */         if (btnSSL.getSelection()) {
/* 341 */           url = "https://" + UrlUtils.convertIPV6Host(localTrackerHost) + ":" + localTrackerPortSSL + "/announce";
/*     */         } else {
/* 343 */           url = "http://" + UrlUtils.convertIPV6Host(localTrackerHost) + ":" + localTrackerPort + "/announce";
/*     */         }
/*     */         
/* 346 */         localTrackerValue.setText(url);
/*     */         
/* 348 */         localTrackerUrl[0] = url;
/*     */         
/* 350 */         ModePanel.this.setTrackerUrl(url);
/*     */       }
/*     */       
/*     */ 
/* 354 */     });
/* 355 */     btnLocalTracker.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 357 */         wizard.setTrackerType(1);
/* 358 */         ModePanel.this.setTrackerUrl(localTrackerUrl[0]);
/* 359 */         ModePanel.this.updateTrackerURL();
/* 360 */         btnExternalTracker.setSelection(false);
/* 361 */         btnLocalTracker.setSelection(true);
/* 362 */         btnDHTTracker.setSelection(false);
/* 363 */         ModePanel.this.tracker.setEnabled(false);
/* 364 */         btnSSL.setEnabled(SSLEnabled);
/* 365 */         if (labelLocalAnnounce != null) labelLocalAnnounce.setEnabled(true);
/* 366 */         localTrackerValue.setEnabled(true);
/* 367 */         labelExternalAnnounce.setEnabled(false);
/* 368 */         btnMultiTracker.setEnabled(true);
/*     */       }
/*     */       
/* 371 */     });
/* 372 */     btnExternalTracker.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 374 */         wizard.setTrackerType(2);
/* 375 */         ModePanel.this.setTrackerUrl("http://");
/* 376 */         ModePanel.this.updateTrackerURL();
/* 377 */         btnLocalTracker.setSelection(false);
/* 378 */         btnExternalTracker.setSelection(true);
/* 379 */         btnDHTTracker.setSelection(false);
/* 380 */         ModePanel.this.tracker.setEnabled(true);
/* 381 */         btnSSL.setEnabled(false);
/* 382 */         if (labelLocalAnnounce != null) labelLocalAnnounce.setEnabled(false);
/* 383 */         localTrackerValue.setEnabled(false);
/* 384 */         labelExternalAnnounce.setEnabled(true);
/* 385 */         btnMultiTracker.setEnabled(true);
/*     */       }
/*     */       
/* 388 */     });
/* 389 */     btnDHTTracker.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 391 */         wizard.setTrackerType(3);
/* 392 */         ModePanel.this.setTrackerUrl(NewTorrentWizard.TT_DECENTRAL_DEFAULT);
/* 393 */         ModePanel.this.updateTrackerURL();
/* 394 */         btnLocalTracker.setSelection(false);
/* 395 */         btnExternalTracker.setSelection(false);
/* 396 */         btnDHTTracker.setSelection(true);
/* 397 */         ModePanel.this.tracker.setEnabled(false);
/* 398 */         btnSSL.setEnabled(false);
/* 399 */         if (labelLocalAnnounce != null) labelLocalAnnounce.setEnabled(false);
/* 400 */         localTrackerValue.setEnabled(false);
/* 401 */         labelExternalAnnounce.setEnabled(false);
/* 402 */         btnMultiTracker.setEnabled(false);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 408 */     });
/* 409 */     label = new Label(panel, 0);
/* 410 */     Messages.setLanguageText(label, "wizard.comment");
/*     */     
/* 412 */     final Text comment = new Text(panel, 2048);
/* 413 */     gridData = new GridData(768);
/* 414 */     gridData.horizontalSpan = 5;
/* 415 */     Utils.setLayoutData(comment, gridData);
/* 416 */     comment.setText(wizard.getComment());
/*     */     
/* 418 */     comment.addListener(24, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 420 */         wizard.setComment(comment.getText());
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IWizardPanel<NewTorrentWizard> getNextPanel()
/*     */   {
/* 434 */     if (Constants.isOSX)
/*     */     {
/*     */ 
/* 437 */       if (((NewTorrentWizard)this.wizard).getTrackerType() == 2) {
/* 438 */         setTrackerUrl(this.tracker.getText());
/*     */       }
/*     */     }
/*     */     
/* 442 */     if (((NewTorrentWizard)this.wizard).useMultiTracker) {
/* 443 */       return new MultiTrackerPanel((NewTorrentWizard)this.wizard, this);
/*     */     }
/*     */     
/* 446 */     if (((NewTorrentWizard)this.wizard).useWebSeed) {
/* 447 */       return new WebSeedPanel((NewTorrentWizard)this.wizard, this);
/*     */     }
/*     */     
/* 450 */     return ((NewTorrentWizard)this.wizard).getNextPanelForMode(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isNextEnabled()
/*     */   {
/* 459 */     return true;
/*     */   }
/*     */   
/*     */   void activateMode(int mode) {
/* 463 */     ((NewTorrentWizard)this.wizard).setCurrentInfo(MessageText.getString(mode == 2 ? "wizard.directory.help" : mode == 1 ? "wizard.singlefile.help" : "wizard.newtorrent.byo.help"));
/* 464 */     ((NewTorrentWizard)this.wizard).create_mode = mode;
/*     */   }
/*     */   
/*     */   void updateTrackerURL() {
/* 468 */     this.tracker.setText(((NewTorrentWizard)this.wizard).trackerURL);
/*     */   }
/*     */   
/*     */   void setTrackerUrl(String url) {
/* 472 */     ((NewTorrentWizard)this.wizard).trackerURL = url;
/* 473 */     String config = ((NewTorrentWizard)this.wizard).multiTrackerConfig;
/* 474 */     if (config.equals("")) {
/* 475 */       List list = (List)((NewTorrentWizard)this.wizard).trackers.get(0);
/* 476 */       if (list.size() > 0)
/* 477 */         list.remove(0);
/* 478 */       list.add(url);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/maketorrent/ModePanel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */