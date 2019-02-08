/*      */ package org.gudy.azureus2.ui.swt.views;
/*      */ 
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import java.util.Collection;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import org.eclipse.swt.graphics.Font;
/*      */ import org.eclipse.swt.graphics.FontData;
/*      */ import org.eclipse.swt.layout.FormLayout;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Group;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Layout;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManager;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerState;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStateAttributeListener;
/*      */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.BufferedLabel;
/*      */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*      */ import org.gudy.azureus2.ui.swt.config.generic.GenericBooleanParameter;
/*      */ import org.gudy.azureus2.ui.swt.config.generic.GenericFloatParameter;
/*      */ import org.gudy.azureus2.ui.swt.config.generic.GenericIntParameter;
/*      */ import org.gudy.azureus2.ui.swt.config.generic.GenericParameterAdapter;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTView;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTViewEvent;
/*      */ import org.gudy.azureus2.ui.swt.pluginsimpl.UISWTViewCoreEventListener;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class TorrentOptionsView
/*      */   implements DownloadManagerStateAttributeListener, UISWTViewCoreEventListener
/*      */ {
/*      */   private static final String TEXT_PREFIX = "TorrentOptionsView.param.";
/*      */   private static final String MAX_UPLOAD = "max.upload";
/*      */   private static final String MAX_DOWNLOAD = "max.download";
/*      */   public static final String MSGID_PREFIX = "TorrentOptionsView";
/*      */   private boolean multi_view;
/*      */   private DownloadManager[] managers;
/*   72 */   private GenericParameterAdapter ds_param_adapter = new downloadStateParameterAdapter();
/*   73 */   private GenericParameterAdapter adhoc_param_adapter = new adhocParameterAdapter();
/*      */   
/*   75 */   private Map<String, Object> adhoc_parameters = new HashMap();
/*   76 */   private Map<String, Object> ds_parameters = new HashMap();
/*      */   
/*      */   private Composite panel;
/*      */   
/*      */   private Font headerFont;
/*      */   
/*      */   private BufferedLabel agg_size;
/*      */   
/*      */   private BufferedLabel agg_remaining;
/*      */   
/*      */   private BufferedLabel agg_uploaded;
/*      */   
/*      */   private BufferedLabel agg_downloaded;
/*      */   
/*      */   private BufferedLabel agg_share_ratio;
/*      */   
/*      */   private BufferedLabel agg_upload_speed;
/*      */   
/*      */   private BufferedLabel agg_download_speed;
/*      */   
/*      */   private Composite parent;
/*      */   
/*      */   private UISWTView swtView;
/*      */   
/*      */ 
/*      */   public TorrentOptionsView() {}
/*      */   
/*      */   public TorrentOptionsView(DownloadManager[] managers2)
/*      */   {
/*  105 */     dataSourceChanged(managers2);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   private void initialize(Composite composite)
/*      */   {
/*  112 */     this.parent = composite;
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  119 */     if ((this.panel != null) && (!this.panel.isDisposed())) {
/*  120 */       Utils.disposeComposite(this.panel, false);
/*      */     } else {
/*  122 */       this.panel = new Composite(composite, 0);
/*      */       
/*  124 */       GridLayout layout = new GridLayout();
/*  125 */       layout.marginHeight = 0;
/*  126 */       layout.marginWidth = 0;
/*  127 */       layout.numColumns = 1;
/*  128 */       this.panel.setLayout(layout);
/*      */       
/*  130 */       Layout parentLayout = this.parent.getLayout();
/*  131 */       if ((parentLayout instanceof FormLayout)) {
/*  132 */         Utils.setLayoutData(this.panel, Utils.getFilledFormData());
/*      */       } else {
/*  134 */         Utils.setLayoutData(this.panel, new GridData(1808));
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  139 */     if (this.managers == null) {
/*  140 */       return;
/*      */     }
/*      */     
/*  143 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*      */     
/*      */ 
/*      */ 
/*  147 */     Composite cHeader = new Composite(this.panel, 2048);
/*  148 */     GridLayout configLayout = new GridLayout();
/*  149 */     configLayout.marginHeight = 3;
/*  150 */     configLayout.marginWidth = 0;
/*  151 */     cHeader.setLayout(configLayout);
/*  152 */     GridData gridData = new GridData(772);
/*  153 */     Utils.setLayoutData(cHeader, gridData);
/*      */     
/*  155 */     Display d = this.panel.getDisplay();
/*  156 */     cHeader.setBackground(d.getSystemColor(26));
/*  157 */     cHeader.setForeground(d.getSystemColor(27));
/*      */     
/*  159 */     Label lHeader = new Label(cHeader, 0);
/*  160 */     lHeader.setBackground(d.getSystemColor(26));
/*  161 */     lHeader.setForeground(d.getSystemColor(27));
/*  162 */     FontData[] fontData = lHeader.getFont().getFontData();
/*  163 */     fontData[0].setStyle(1);
/*  164 */     int fontHeight = (int)(fontData[0].getHeight() * 1.2D);
/*  165 */     fontData[0].setHeight(fontHeight);
/*  166 */     this.headerFont = new Font(d, fontData);
/*  167 */     lHeader.setFont(this.headerFont);
/*      */     
/*  169 */     if (this.managers.length == 1) {
/*  170 */       lHeader.setText(" " + MessageText.getString("authenticator.torrent") + " : " + this.managers[0].getDisplayName().replaceAll("&", "&&"));
/*      */     } else {
/*  172 */       String str = "";
/*      */       
/*  174 */       for (int i = 0; i < Math.min(3, this.managers.length); i++)
/*      */       {
/*  176 */         str = str + (i == 0 ? "" : ", ") + this.managers[i].getDisplayName().replaceAll("&", "&&");
/*      */       }
/*      */       
/*  179 */       if (this.managers.length > 3)
/*      */       {
/*  181 */         str = str + "...";
/*      */       }
/*      */       
/*  184 */       lHeader.setText(" " + this.managers.length + " " + MessageText.getString("ConfigView.section.torrents") + " : " + str);
/*      */     }
/*      */     
/*  187 */     gridData = new GridData(772);
/*  188 */     Utils.setLayoutData(lHeader, gridData);
/*      */     
/*  190 */     Group gTorrentOptions = new Group(this.panel, 0);
/*  191 */     Messages.setLanguageText(gTorrentOptions, "ConfigView.section.transfer");
/*  192 */     gridData = new GridData(272);
/*  193 */     Utils.setLayoutData(gTorrentOptions, gridData);
/*  194 */     GridLayout layout = new GridLayout();
/*  195 */     layout.numColumns = 2;
/*  196 */     gTorrentOptions.setLayout(layout);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  201 */     String k_unit = DisplayFormatters.getRateUnitBase10(1).trim();
/*      */     
/*      */ 
/*      */ 
/*  205 */     Label label = new Label(gTorrentOptions, 0);
/*  206 */     gridData = new GridData();
/*  207 */     Utils.setLayoutData(label, gridData);
/*  208 */     label.setText(k_unit + " " + MessageText.getString("GeneralView.label.maxuploadspeed.tooltip"));
/*      */     
/*  210 */     GenericIntParameter max_upload = new GenericIntParameter(this.adhoc_param_adapter, gTorrentOptions, "max.upload");
/*      */     
/*  212 */     this.adhoc_parameters.put("max.upload", max_upload);
/*  213 */     gridData = new GridData();
/*  214 */     max_upload.setLayoutData(gridData);
/*      */     
/*  216 */     if (userMode > 0)
/*      */     {
/*      */ 
/*      */ 
/*  220 */       label = new Label(gTorrentOptions, 0);
/*  221 */       gridData = new GridData();
/*  222 */       Utils.setLayoutData(label, gridData);
/*  223 */       Messages.setLanguageText(label, "TorrentOptionsView.param.max.uploads.when.busy");
/*      */       
/*  225 */       GenericIntParameter max_upload_when_busy = new GenericIntParameter(this.ds_param_adapter, gTorrentOptions, "max.upload.when.busy");
/*      */       
/*      */ 
/*  228 */       this.ds_parameters.put("max.upload.when.busy", max_upload_when_busy);
/*  229 */       gridData = new GridData();
/*  230 */       max_upload_when_busy.setLayoutData(gridData);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  235 */     label = new Label(gTorrentOptions, 0);
/*  236 */     gridData = new GridData();
/*  237 */     Utils.setLayoutData(label, gridData);
/*  238 */     label.setText(k_unit + " " + MessageText.getString("GeneralView.label.maxdownloadspeed.tooltip"));
/*      */     
/*  240 */     GenericIntParameter max_download = new GenericIntParameter(this.adhoc_param_adapter, gTorrentOptions, "max.download");
/*      */     
/*  242 */     this.adhoc_parameters.put("max.download", max_download);
/*  243 */     gridData = new GridData();
/*  244 */     max_download.setLayoutData(gridData);
/*      */     
/*      */ 
/*      */ 
/*  248 */     if (userMode > 0) {
/*  249 */       label = new Label(gTorrentOptions, 0);
/*  250 */       gridData = new GridData();
/*  251 */       Utils.setLayoutData(label, gridData);
/*  252 */       Messages.setLanguageText(label, "TorrentOptionsView.param.max.uploads");
/*      */       
/*  254 */       GenericIntParameter max_uploads = new GenericIntParameter(this.ds_param_adapter, gTorrentOptions, "max.uploads");
/*      */       
/*      */ 
/*  257 */       this.ds_parameters.put("max.uploads", max_uploads);
/*  258 */       max_uploads.setMinimumValue(2);
/*  259 */       gridData = new GridData();
/*  260 */       max_uploads.setLayoutData(gridData);
/*      */       
/*      */ 
/*      */ 
/*  264 */       Composite cMaxUploadsOptionsArea = new Composite(gTorrentOptions, 0);
/*  265 */       layout = new GridLayout();
/*  266 */       layout.numColumns = 3;
/*  267 */       layout.marginWidth = 0;
/*  268 */       layout.marginHeight = 0;
/*  269 */       cMaxUploadsOptionsArea.setLayout(layout);
/*  270 */       gridData = new GridData();
/*  271 */       gridData.horizontalIndent = 15;
/*  272 */       gridData.horizontalSpan = 2;
/*  273 */       Utils.setLayoutData(cMaxUploadsOptionsArea, gridData);
/*      */       
/*  275 */       label = new Label(cMaxUploadsOptionsArea, 0);
/*  276 */       ImageLoader.getInstance().setLabelImage(label, "subitem");
/*  277 */       gridData = new GridData(2);
/*  278 */       Utils.setLayoutData(label, gridData);
/*      */       
/*  280 */       gridData = new GridData();
/*  281 */       GenericBooleanParameter max_uploads_when_seeding_enabled = new GenericBooleanParameter(this.ds_param_adapter, cMaxUploadsOptionsArea, "max.uploads.when.seeding.enabled", Boolean.valueOf(false), "TorrentOptionsView.param.alternative.value.enable");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  288 */       this.ds_parameters.put("max.uploads.when.seeding.enabled", max_uploads_when_seeding_enabled);
/*  289 */       max_uploads_when_seeding_enabled.setLayoutData(gridData);
/*      */       
/*      */ 
/*  292 */       GenericIntParameter max_uploads_when_seeding = new GenericIntParameter(this.ds_param_adapter, cMaxUploadsOptionsArea, "max.uploads.when.seeding");
/*      */       
/*      */ 
/*  295 */       this.ds_parameters.put("max.uploads.when.seeding", max_uploads_when_seeding);
/*  296 */       gridData = new GridData();
/*  297 */       max_uploads_when_seeding.setMinimumValue(2);
/*  298 */       max_uploads_when_seeding.setLayoutData(gridData);
/*      */       
/*  300 */       max_uploads_when_seeding_enabled.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(max_uploads_when_seeding.getControl()));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  305 */       label = new Label(gTorrentOptions, 0);
/*  306 */       gridData = new GridData();
/*  307 */       Utils.setLayoutData(label, gridData);
/*  308 */       Messages.setLanguageText(label, "TorrentOptionsView.param.max.peers");
/*      */       
/*  310 */       GenericIntParameter max_peers = new GenericIntParameter(this.ds_param_adapter, gTorrentOptions, "max.peers");
/*      */       
/*  312 */       this.ds_parameters.put("max.peers", max_peers);
/*  313 */       gridData = new GridData();
/*  314 */       max_peers.setLayoutData(gridData);
/*      */       
/*      */ 
/*      */ 
/*  318 */       Composite cMaxPeersOptionsArea = new Composite(gTorrentOptions, 0);
/*  319 */       layout = new GridLayout();
/*  320 */       layout.numColumns = 3;
/*  321 */       layout.marginWidth = 0;
/*  322 */       layout.marginHeight = 0;
/*  323 */       cMaxPeersOptionsArea.setLayout(layout);
/*  324 */       gridData = new GridData();
/*  325 */       gridData.horizontalIndent = 15;
/*  326 */       gridData.horizontalSpan = 2;
/*  327 */       Utils.setLayoutData(cMaxPeersOptionsArea, gridData);
/*      */       
/*  329 */       label = new Label(cMaxPeersOptionsArea, 0);
/*  330 */       ImageLoader.getInstance().setLabelImage(label, "subitem");
/*  331 */       gridData = new GridData(2);
/*  332 */       Utils.setLayoutData(label, gridData);
/*      */       
/*  334 */       gridData = new GridData();
/*  335 */       GenericBooleanParameter max_peers_when_seeding_enabled = new GenericBooleanParameter(this.ds_param_adapter, cMaxPeersOptionsArea, "max.peers.when.seeding.enabled", Boolean.valueOf(false), "TorrentOptionsView.param.alternative.value.enable");
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  342 */       this.ds_parameters.put("max.peers.when.seeding.enabled", max_peers_when_seeding_enabled);
/*  343 */       max_peers_when_seeding_enabled.setLayoutData(gridData);
/*      */       
/*      */ 
/*  346 */       GenericIntParameter max_peers_when_seeding = new GenericIntParameter(this.ds_param_adapter, cMaxPeersOptionsArea, "max.peers.when.seeding");
/*      */       
/*      */ 
/*  349 */       this.ds_parameters.put("max.peers.when.seeding", max_peers_when_seeding);
/*  350 */       gridData = new GridData();
/*  351 */       max_peers_when_seeding.setLayoutData(gridData);
/*      */       
/*  353 */       max_peers_when_seeding_enabled.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(max_peers_when_seeding.getControl()));
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  359 */       label = new Label(gTorrentOptions, 0);
/*  360 */       gridData = new GridData();
/*  361 */       Utils.setLayoutData(label, gridData);
/*  362 */       Messages.setLanguageText(label, "TorrentOptionsView.param.max.seeds");
/*      */       
/*  364 */       GenericIntParameter max_seeds = new GenericIntParameter(this.ds_param_adapter, gTorrentOptions, "max.seeds");
/*      */       
/*      */ 
/*  367 */       this.ds_parameters.put("max.seeds", max_seeds);
/*  368 */       gridData = new GridData();
/*  369 */       max_seeds.setLayoutData(gridData);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  374 */     if (userMode > 0)
/*      */     {
/*  376 */       label = new Label(gTorrentOptions, 0);
/*  377 */       gridData = new GridData();
/*  378 */       Utils.setLayoutData(label, gridData);
/*  379 */       Messages.setLanguageText(label, "TorrentOptionsView.param.upload.priority");
/*      */       
/*  381 */       gridData = new GridData();
/*  382 */       GenericIntParameter upload_priority_enabled = new GenericIntParameter(this.ds_param_adapter, gTorrentOptions, "up.pri", 0, 1);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  388 */       this.ds_parameters.put("up.pri", upload_priority_enabled);
/*  389 */       upload_priority_enabled.setLayoutData(gridData);
/*      */       
/*      */ 
/*      */ 
/*  393 */       label = new Label(gTorrentOptions, 0);
/*  394 */       gridData = new GridData();
/*  395 */       Utils.setLayoutData(label, gridData);
/*  396 */       Messages.setLanguageText(label, "TableColumn.header.min_sr");
/*      */       
/*  398 */       gridData = new GridData();
/*  399 */       gridData.widthHint = 50;
/*  400 */       GenericFloatParameter min_sr = new GenericFloatParameter(this.ds_param_adapter, gTorrentOptions, "sr.min", 0.0F, Float.MAX_VALUE, true, 3);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  406 */       this.ds_parameters.put("sr.min", min_sr);
/*  407 */       min_sr.setLayoutData(gridData);
/*      */       
/*      */ 
/*      */ 
/*  411 */       label = new Label(gTorrentOptions, 0);
/*  412 */       gridData = new GridData();
/*  413 */       Utils.setLayoutData(label, gridData);
/*  414 */       Messages.setLanguageText(label, "TableColumn.header.max_sr");
/*      */       
/*  416 */       gridData = new GridData();
/*  417 */       gridData.widthHint = 50;
/*  418 */       GenericFloatParameter max_sr = new GenericFloatParameter(this.ds_param_adapter, gTorrentOptions, "sr.max", 0.0F, Float.MAX_VALUE, true, 3);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  424 */       this.ds_parameters.put("sr.max", max_sr);
/*  425 */       max_sr.setLayoutData(gridData);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  430 */     Label reset_label = new Label(gTorrentOptions, 0);
/*  431 */     Messages.setLanguageText(reset_label, "TorrentOptionsView.param.reset.to.default");
/*      */     
/*  433 */     Button reset_button = new Button(gTorrentOptions, 8);
/*      */     
/*  435 */     Messages.setLanguageText(reset_button, "TorrentOptionsView.param.reset.button");
/*      */     
/*  437 */     reset_button.addListener(13, new Listener()
/*      */     {
/*      */ 
/*      */       public void handleEvent(Event event)
/*      */       {
/*      */ 
/*  443 */         TorrentOptionsView.this.setDefaults();
/*      */       }
/*      */     });
/*      */     
/*  447 */     for (int i = 0; i < this.managers.length; i++) {
/*  448 */       this.managers[i].getDownloadState().addListener(this, "parameters", 1);
/*      */     }
/*      */     
/*      */ 
/*  452 */     Group gTorrentInfo = new Group(this.panel, 0);
/*  453 */     Messages.setLanguageText(gTorrentInfo, "label.aggregate.info");
/*  454 */     gridData = new GridData(272);
/*  455 */     Utils.setLayoutData(gTorrentInfo, gridData);
/*  456 */     layout = new GridLayout();
/*  457 */     layout.numColumns = 2;
/*  458 */     gTorrentInfo.setLayout(layout);
/*      */     
/*      */ 
/*      */ 
/*  462 */     label = new Label(gTorrentInfo, 0);
/*  463 */     label.setText(MessageText.getString("TableColumn.header.size") + ": ");
/*      */     
/*  465 */     this.agg_size = new BufferedLabel(gTorrentInfo, 536887296);
/*  466 */     gridData = new GridData(768);
/*  467 */     this.agg_size.setLayoutData(gridData);
/*      */     
/*      */ 
/*      */ 
/*  471 */     label = new Label(gTorrentInfo, 0);
/*  472 */     label.setText(MessageText.getString("TableColumn.header.remaining") + ": ");
/*      */     
/*  474 */     this.agg_remaining = new BufferedLabel(gTorrentInfo, 536887296);
/*  475 */     gridData = new GridData(768);
/*  476 */     this.agg_remaining.setLayoutData(gridData);
/*      */     
/*      */ 
/*      */ 
/*  480 */     label = new Label(gTorrentInfo, 0);
/*  481 */     label.setText(MessageText.getString("MyTrackerView.uploaded") + ": ");
/*      */     
/*  483 */     this.agg_uploaded = new BufferedLabel(gTorrentInfo, 536887296);
/*  484 */     gridData = new GridData(768);
/*  485 */     this.agg_uploaded.setLayoutData(gridData);
/*      */     
/*      */ 
/*      */ 
/*  489 */     label = new Label(gTorrentInfo, 0);
/*  490 */     label.setText(MessageText.getString("MyTrackerView.downloaded") + ": ");
/*      */     
/*  492 */     this.agg_downloaded = new BufferedLabel(gTorrentInfo, 536887296);
/*  493 */     gridData = new GridData(768);
/*  494 */     this.agg_downloaded.setLayoutData(gridData);
/*      */     
/*      */ 
/*      */ 
/*  498 */     label = new Label(gTorrentInfo, 0);
/*  499 */     label.setText(MessageText.getString("SpeedView.uploadSpeed.title") + ": ");
/*      */     
/*  501 */     this.agg_upload_speed = new BufferedLabel(gTorrentInfo, 536887296);
/*  502 */     gridData = new GridData(768);
/*  503 */     this.agg_upload_speed.setLayoutData(gridData);
/*      */     
/*      */ 
/*      */ 
/*  507 */     label = new Label(gTorrentInfo, 0);
/*  508 */     label.setText(MessageText.getString("SpeedView.downloadSpeed.title") + ": ");
/*      */     
/*  510 */     this.agg_download_speed = new BufferedLabel(gTorrentInfo, 536887296);
/*  511 */     gridData = new GridData(768);
/*  512 */     this.agg_download_speed.setLayoutData(gridData);
/*      */     
/*      */ 
/*      */ 
/*  516 */     label = new Label(gTorrentInfo, 0);
/*  517 */     label.setText(MessageText.getString("TableColumn.header.shareRatio") + ": ");
/*      */     
/*  519 */     this.agg_share_ratio = new BufferedLabel(gTorrentInfo, 536887296);
/*  520 */     gridData = new GridData(768);
/*  521 */     this.agg_share_ratio.setLayoutData(gridData);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  526 */     Label stats_reset_label = new Label(gTorrentInfo, 0);
/*  527 */     Messages.setLanguageText(stats_reset_label, "TorrentOptionsView.param.reset.stats");
/*      */     
/*  529 */     Button stats_reset_button = new Button(gTorrentInfo, 8);
/*      */     
/*  531 */     Messages.setLanguageText(stats_reset_button, "TorrentOptionsView.param.reset.button");
/*      */     
/*  533 */     stats_reset_button.addListener(13, new Listener()
/*      */     {
/*      */ 
/*      */       public void handleEvent(Event event)
/*      */       {
/*      */ 
/*  539 */         for (DownloadManager dm : TorrentOptionsView.this.managers)
/*      */         {
/*  541 */           dm.getStats().resetTotalBytesSentReceived(0L, 0L);
/*      */         }
/*      */         
/*      */       }
/*  545 */     });
/*  546 */     this.panel.layout(true, true);
/*      */   }
/*      */   
/*      */ 
/*      */   private void refresh()
/*      */   {
/*  552 */     if (this.agg_size == null)
/*      */     {
/*      */ 
/*      */ 
/*  556 */       return;
/*      */     }
/*      */     
/*  559 */     long total_size = 0L;
/*  560 */     long total_remaining = 0L;
/*  561 */     long total_good_downloaded = 0L;
/*  562 */     long total_downloaded = 0L;
/*  563 */     long total_uploaded = 0L;
/*      */     
/*  565 */     long total_data_up_speed = 0L;
/*  566 */     long total_prot_up_speed = 0L;
/*      */     
/*  568 */     long total_data_down_speed = 0L;
/*  569 */     long total_prot_down_speed = 0L;
/*      */     
/*  571 */     for (int i = 0; i < this.managers.length; i++)
/*      */     {
/*  573 */       DownloadManager dm = this.managers[i];
/*      */       
/*  575 */       DownloadManagerStats stats = dm.getStats();
/*      */       
/*  577 */       total_size += stats.getSizeExcludingDND();
/*      */       
/*  579 */       total_remaining += stats.getRemainingExcludingDND();
/*      */       
/*  581 */       long good_received = stats.getTotalGoodDataBytesReceived();
/*  582 */       long received = stats.getTotalDataBytesReceived();
/*  583 */       long sent = stats.getTotalDataBytesSent();
/*      */       
/*  585 */       total_good_downloaded += good_received;
/*  586 */       total_downloaded += received;
/*  587 */       total_uploaded += sent;
/*      */       
/*  589 */       total_data_up_speed += stats.getDataSendRate();
/*  590 */       total_prot_up_speed += stats.getProtocolSendRate();
/*      */       
/*  592 */       total_data_down_speed += stats.getDataReceiveRate();
/*  593 */       total_prot_down_speed += stats.getProtocolReceiveRate();
/*      */     }
/*      */     
/*  596 */     this.agg_size.setText(DisplayFormatters.formatByteCountToKiBEtc(total_size));
/*  597 */     this.agg_remaining.setText(DisplayFormatters.formatByteCountToKiBEtc(total_remaining));
/*  598 */     this.agg_uploaded.setText(DisplayFormatters.formatByteCountToKiBEtc(total_uploaded));
/*  599 */     this.agg_downloaded.setText(DisplayFormatters.formatByteCountToKiBEtc(total_downloaded));
/*      */     
/*  601 */     this.agg_upload_speed.setText(DisplayFormatters.formatDataProtByteCountToKiBEtc(total_data_up_speed, total_prot_up_speed));
/*  602 */     this.agg_download_speed.setText(DisplayFormatters.formatDataProtByteCountToKiBEtc(total_data_down_speed, total_prot_down_speed));
/*      */     
/*      */     long sr;
/*      */     long sr;
/*  606 */     if (total_good_downloaded == 0L) {
/*      */       long sr;
/*  608 */       if (total_uploaded == 0L)
/*      */       {
/*  610 */         sr = 1000L;
/*      */       }
/*      */       else {
/*  613 */         sr = -1L;
/*      */       }
/*      */     }
/*      */     else {
/*  617 */       sr = 1000L * total_uploaded / total_good_downloaded;
/*      */     }
/*      */     
/*      */     String share_ratio_str;
/*      */     String share_ratio_str;
/*  622 */     if (sr == -1L)
/*      */     {
/*  624 */       share_ratio_str = "∞";
/*      */     }
/*      */     else
/*      */     {
/*  628 */       String partial = "" + sr % 1000L;
/*      */       
/*  630 */       while (partial.length() < 3)
/*      */       {
/*  632 */         partial = "0" + partial;
/*      */       }
/*      */       
/*  635 */       share_ratio_str = sr / 1000L + "." + partial;
/*      */     }
/*      */     
/*  638 */     this.agg_share_ratio.setText(share_ratio_str);
/*      */   }
/*      */   
/*      */ 
/*      */   protected void setDefaults()
/*      */   {
/*  644 */     Iterator<?> it = this.ds_parameters.keySet().iterator();
/*      */     
/*  646 */     while (it.hasNext())
/*      */     {
/*  648 */       String key = (String)it.next();
/*      */       
/*  650 */       for (int i = 0; i < this.managers.length; i++)
/*      */       {
/*  652 */         this.managers[i].getDownloadState().setParameterDefault(key);
/*      */       }
/*      */     }
/*      */     
/*  656 */     it = this.adhoc_parameters.values().iterator();
/*      */     
/*  658 */     while (it.hasNext())
/*      */     {
/*  660 */       Object param = it.next();
/*      */       
/*  662 */       if ((param instanceof GenericIntParameter))
/*      */       {
/*  664 */         GenericIntParameter int_param = (GenericIntParameter)param;
/*      */         
/*  666 */         int_param.setValue(0, true);
/*      */       }
/*      */       else {
/*  669 */         Debug.out("Unknown parameter type: " + param.getClass());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void attributeEventOccurred(DownloadManager dm, String attribute_name, int event_type)
/*      */   {
/*  678 */     final DownloadManagerState state = dm.getDownloadState();
/*  679 */     Utils.execSWTThread(new Runnable() {
/*      */       public void run() {
/*  681 */         Iterator<Map.Entry<String, Object>> it = TorrentOptionsView.this.ds_parameters.entrySet().iterator();
/*  682 */         while (it.hasNext()) {
/*  683 */           Map.Entry<String, Object> entry = (Map.Entry)it.next();
/*  684 */           String key = (String)entry.getKey();
/*  685 */           Object param = entry.getValue();
/*      */           
/*  687 */           if ((param instanceof GenericIntParameter)) {
/*  688 */             GenericIntParameter int_param = (GenericIntParameter)param;
/*  689 */             int value = state.getIntParameter(key);
/*  690 */             int_param.setValue(value);
/*  691 */           } else if ((param instanceof GenericBooleanParameter)) {
/*  692 */             GenericBooleanParameter bool_param = (GenericBooleanParameter)param;
/*  693 */             boolean value = state.getBooleanParameter(key);
/*  694 */             bool_param.setSelected(value);
/*  695 */           } else if ((param instanceof GenericFloatParameter)) {
/*  696 */             GenericFloatParameter float_param = (GenericFloatParameter)param;
/*  697 */             float value = state.getIntParameter(key) / 1000.0F;
/*  698 */             float_param.setValue(value);
/*      */           } else {
/*  700 */             Debug.out("Unknown parameter type: " + param.getClass()); } } } }, true);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Composite getComposite()
/*      */   {
/*  710 */     return this.panel;
/*      */   }
/*      */   
/*      */ 
/*      */   private String getFullTitle()
/*      */   {
/*  716 */     return MessageText.getString(this.multi_view ? "TorrentOptionsView.multi.title.full" : "TorrentOptionsView.title.full");
/*      */   }
/*      */   
/*      */ 
/*      */   private void delete()
/*      */   {
/*  722 */     if (this.headerFont != null)
/*      */     {
/*  724 */       this.headerFont.dispose();
/*      */     }
/*      */     
/*  727 */     if (this.managers != null) {
/*  728 */       for (int i = 0; i < this.managers.length; i++) {
/*  729 */         this.managers[i].getDownloadState().removeListener(this, "parameters", 1);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected class adhocParameterAdapter
/*      */     extends GenericParameterAdapter
/*      */   {
/*      */     protected adhocParameterAdapter() {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public int getIntValue(String key)
/*      */     {
/*  747 */       return getIntValue(key, 0);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int getIntValue(String key, int def)
/*      */     {
/*  756 */       if (key == "max.upload") {
/*  757 */         int result = def;
/*      */         
/*  759 */         for (int i = 0; i < TorrentOptionsView.this.managers.length; i++) {
/*  760 */           int val = TorrentOptionsView.this.managers[i].getStats().getUploadRateLimitBytesPerSecond() / DisplayFormatters.getKinB();
/*      */           
/*  762 */           if (i == 0) {
/*  763 */             result = val;
/*  764 */           } else if (result != val) {
/*  765 */             return def;
/*      */           }
/*      */         }
/*      */         
/*  769 */         return result;
/*      */       }
/*  771 */       if (key == "max.download") {
/*  772 */         int result = def;
/*      */         
/*  774 */         for (int i = 0; i < TorrentOptionsView.this.managers.length; i++) {
/*  775 */           int val = TorrentOptionsView.this.managers[i].getStats().getDownloadRateLimitBytesPerSecond() / DisplayFormatters.getKinB();
/*      */           
/*  777 */           if (i == 0) {
/*  778 */             result = val;
/*  779 */           } else if (result != val) {
/*  780 */             return def;
/*      */           }
/*      */         }
/*      */         
/*  784 */         return result;
/*      */       }
/*  786 */       Debug.out("Unknown key '" + key + "'");
/*  787 */       return 0;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setIntValue(String key, int value)
/*      */     {
/*  797 */       if (key == "max.upload") {
/*  798 */         for (int i = 0; i < TorrentOptionsView.this.managers.length; i++)
/*      */         {
/*  800 */           DownloadManager manager = TorrentOptionsView.this.managers[i];
/*      */           
/*  802 */           if (value != manager.getStats().getUploadRateLimitBytesPerSecond() / DisplayFormatters.getKinB())
/*      */           {
/*  804 */             manager.getStats().setUploadRateLimitBytesPerSecond(value * DisplayFormatters.getKinB());
/*      */           }
/*      */         }
/*  807 */       } else if (key == "max.download") {
/*  808 */         for (int i = 0; i < TorrentOptionsView.this.managers.length; i++)
/*      */         {
/*  810 */           DownloadManager manager = TorrentOptionsView.this.managers[i];
/*      */           
/*  812 */           if (value != manager.getStats().getDownloadRateLimitBytesPerSecond() / DisplayFormatters.getKinB())
/*      */           {
/*  814 */             manager.getStats().setDownloadRateLimitBytesPerSecond(value * DisplayFormatters.getKinB());
/*      */           }
/*      */         }
/*      */       } else {
/*  818 */         Debug.out("Unknown key '" + key + "'");
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected class downloadStateParameterAdapter
/*      */     extends GenericParameterAdapter
/*      */   {
/*      */     protected downloadStateParameterAdapter() {}
/*      */     
/*      */ 
/*      */     public int getIntValue(String key)
/*      */     {
/*  832 */       return getIntValue(key, 0);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public int getIntValue(String key, int def)
/*      */     {
/*  841 */       int result = def;
/*      */       
/*  843 */       for (int i = 0; i < TorrentOptionsView.this.managers.length; i++) {
/*  844 */         int val = TorrentOptionsView.this.managers[i].getDownloadState().getIntParameter(key);
/*      */         
/*  846 */         if (i == 0) {
/*  847 */           result = val;
/*  848 */         } else if (result != val) {
/*  849 */           return def;
/*      */         }
/*      */       }
/*      */       
/*  853 */       return result;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setIntValue(String key, int value)
/*      */     {
/*  862 */       for (int i = 0; i < TorrentOptionsView.this.managers.length; i++)
/*      */       {
/*  864 */         DownloadManager manager = TorrentOptionsView.this.managers[i];
/*      */         
/*  866 */         if (value != manager.getDownloadState().getIntParameter(key))
/*      */         {
/*  868 */           manager.getDownloadState().setIntParameter(key, value);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public Boolean getBooleanValue(String key)
/*      */     {
/*  878 */       return getBooleanValue(key, Boolean.valueOf(false));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public Boolean getBooleanValue(String key, Boolean def)
/*      */     {
/*  887 */       boolean result = def.booleanValue();
/*      */       
/*  889 */       for (int i = 0; i < TorrentOptionsView.this.managers.length; i++) {
/*  890 */         boolean val = TorrentOptionsView.this.managers[i].getDownloadState().getBooleanParameter(key);
/*      */         
/*  892 */         if (i == 0) {
/*  893 */           result = val;
/*  894 */         } else if (result != val) {
/*  895 */           return def;
/*      */         }
/*      */       }
/*      */       
/*  899 */       return Boolean.valueOf(result);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setBooleanValue(String key, boolean value)
/*      */     {
/*  908 */       for (int i = 0; i < TorrentOptionsView.this.managers.length; i++)
/*      */       {
/*  910 */         DownloadManager manager = TorrentOptionsView.this.managers[i];
/*      */         
/*  912 */         if (value != manager.getDownloadState().getBooleanParameter(key))
/*      */         {
/*  914 */           manager.getDownloadState().setBooleanParameter(key, value);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */     public float getFloatValue(String key)
/*      */     {
/*  924 */       int result = 0;
/*      */       
/*  926 */       for (int i = 0; i < TorrentOptionsView.this.managers.length; i++) {
/*  927 */         int val = TorrentOptionsView.this.managers[i].getDownloadState().getIntParameter(key);
/*      */         
/*  929 */         if (i == 0) {
/*  930 */           result = val;
/*  931 */         } else if (result != val) {
/*  932 */           return 0.0F;
/*      */         }
/*      */       }
/*      */       
/*  936 */       return result / 1000.0F;
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     public void setFloatValue(String key, float _value)
/*      */     {
/*  945 */       int value = (int)(_value * 1000.0F);
/*      */       
/*  947 */       for (int i = 0; i < TorrentOptionsView.this.managers.length; i++)
/*      */       {
/*  949 */         DownloadManager manager = TorrentOptionsView.this.managers[i];
/*      */         
/*  951 */         if (value != manager.getDownloadState().getIntParameter(key))
/*      */         {
/*  953 */           manager.getDownloadState().setIntParameter(key, value);
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void dataSourceChanged(Object newDataSource) {
/*  960 */     DownloadManager[] old_managers = this.managers;
/*  961 */     if (old_managers != null) {
/*  962 */       for (int i = 0; i < old_managers.length; i++) {
/*  963 */         old_managers[i].getDownloadState().removeListener(this, "parameters", 1);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  968 */     if ((newDataSource instanceof DownloadManager)) {
/*  969 */       this.multi_view = false;
/*  970 */       this.managers = new DownloadManager[] { (DownloadManager)newDataSource };
/*  971 */     } else if ((newDataSource instanceof DownloadManager[])) {
/*  972 */       this.multi_view = true;
/*  973 */       this.managers = ((DownloadManager[])newDataSource);
/*  974 */     } else if ((newDataSource instanceof Object[])) {
/*  975 */       Object[] objs = (Object[])newDataSource;
/*  976 */       if ((objs.length > 0) && 
/*  977 */         ((objs[0] instanceof DownloadManager))) {
/*  978 */         this.managers = new DownloadManager[objs.length];
/*  979 */         for (int i = 0; i < objs.length; i++) {
/*  980 */           this.managers[i] = ((DownloadManager)objs[i]);
/*      */         }
/*  982 */         this.multi_view = true;
/*      */       }
/*      */     }
/*      */     
/*  986 */     if ((this.parent != null) && (!this.parent.isDisposed())) {
/*  987 */       Utils.execSWTThread(new AERunnable() {
/*      */         public void runSupport() {
/*  989 */           if (!TorrentOptionsView.this.parent.isDisposed())
/*      */           {
/*  991 */             TorrentOptionsView.this.initialize(TorrentOptionsView.this.parent);
/*      */           }
/*      */         }
/*      */       });
/*      */     }
/*      */   }
/*      */   
/*      */   public boolean eventOccurred(UISWTViewEvent event) {
/*  999 */     switch (event.getType()) {
/*      */     case 0: 
/* 1001 */       this.swtView = ((UISWTView)event.getData());
/* 1002 */       this.swtView.setTitle(getFullTitle());
/* 1003 */       break;
/*      */     
/*      */     case 7: 
/* 1006 */       delete();
/* 1007 */       break;
/*      */     
/*      */     case 2: 
/* 1010 */       initialize((Composite)event.getData());
/* 1011 */       break;
/*      */     
/*      */     case 6: 
/* 1014 */       Messages.updateLanguageForControl(getComposite());
/* 1015 */       this.swtView.setTitle(getFullTitle());
/* 1016 */       break;
/*      */     
/*      */     case 1: 
/* 1019 */       dataSourceChanged(event.getData());
/* 1020 */       break;
/*      */     
/*      */     case 3: 
/*      */       break;
/*      */     
/*      */     case 5: 
/* 1026 */       refresh();
/*      */     }
/*      */     
/*      */     
/* 1030 */     return true;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/TorrentOptionsView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */