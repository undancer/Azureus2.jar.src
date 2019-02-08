/*      */ package org.gudy.azureus2.ui.swt.views.configsections;
/*      */ 
/*      */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*      */ import java.util.ArrayList;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.layout.GridData;
/*      */ import org.eclipse.swt.layout.GridLayout;
/*      */ import org.eclipse.swt.layout.RowLayout;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Composite;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.DirectoryDialog;
/*      */ import org.eclipse.swt.widgets.Event;
/*      */ import org.eclipse.swt.widgets.Group;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.eclipse.swt.widgets.Listener;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.config.StringList;
/*      */ import org.gudy.azureus2.core3.config.impl.StringListImpl;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.Constants;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.platform.PlatformManager;
/*      */ import org.gudy.azureus2.platform.PlatformManagerCapabilities;
/*      */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*      */ import org.gudy.azureus2.ui.swt.Messages;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*      */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*      */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*      */ import org.gudy.azureus2.ui.swt.config.ExclusiveSelectionActionPerformer;
/*      */ import org.gudy.azureus2.ui.swt.config.GenericActionPerformer;
/*      */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
/*      */ import org.gudy.azureus2.ui.swt.config.IntListParameter;
/*      */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*      */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*      */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*      */ import org.gudy.azureus2.ui.swt.config.ParameterChangeListener;
/*      */ import org.gudy.azureus2.ui.swt.config.StringListParameter;
/*      */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*      */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
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
/*      */ public class ConfigSectionFile
/*      */   implements UISWTConfigSection
/*      */ {
/*      */   public String configSectionGetParentSection()
/*      */   {
/*   59 */     return "root";
/*      */   }
/*      */   
/*      */   public String configSectionGetName() {
/*   63 */     return "files";
/*      */   }
/*      */   
/*      */   public void configSectionSave() {}
/*      */   
/*      */   public int maxUserMode()
/*      */   {
/*   70 */     return 2;
/*      */   }
/*      */   
/*      */   public void configSectionDelete() {
/*   74 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*   75 */     imageLoader.releaseImage("openFolderButton");
/*      */   }
/*      */   
/*      */   public Composite configSectionCreate(final Composite parent) {
/*   79 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*   80 */     Image imgOpenFolder = imageLoader.getImage("openFolderButton");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*   85 */     ArrayList<String> allConfigIDs = new ArrayList();
/*      */     
/*   87 */     Composite gFile = new Composite(parent, 0);
/*      */     
/*   89 */     GridLayout layout = new GridLayout();
/*   90 */     layout.numColumns = 2;
/*   91 */     layout.marginHeight = 0;
/*   92 */     gFile.setLayout(layout);
/*      */     
/*   94 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*      */     
/*      */ 
/*   97 */     final Group gDefaultDir = new Group(gFile, 0);
/*   98 */     Messages.setLanguageText(gDefaultDir, "ConfigView.section.file.defaultdir.section");
/*      */     
/*  100 */     layout = new GridLayout();
/*  101 */     layout.numColumns = 3;
/*  102 */     layout.marginHeight = 2;
/*  103 */     gDefaultDir.setLayout(layout);
/*  104 */     GridData gridData = new GridData(768);
/*  105 */     gridData.horizontalSpan = 2;
/*  106 */     Utils.setLayoutData(gDefaultDir, gridData);
/*      */     
/*      */ 
/*  109 */     String sCurConfigID = "Default save path";
/*  110 */     allConfigIDs.add(sCurConfigID);
/*  111 */     Label lblDefaultDir = new Label(gDefaultDir, 0);
/*  112 */     Messages.setLanguageText(lblDefaultDir, "ConfigView.section.file.defaultdir.ask");
/*      */     
/*  114 */     Utils.setLayoutData(lblDefaultDir, new GridData());
/*      */     
/*  116 */     gridData = new GridData(768);
/*  117 */     final StringParameter pathParameter = new StringParameter(gDefaultDir, sCurConfigID);
/*      */     
/*  119 */     pathParameter.setLayoutData(gridData);
/*      */     
/*  121 */     Button browse = new Button(gDefaultDir, 8);
/*  122 */     browse.setImage(imgOpenFolder);
/*  123 */     imgOpenFolder.setBackground(browse.getBackground());
/*  124 */     browse.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/*      */     
/*  126 */     browse.addListener(13, new Listener()
/*      */     {
/*      */ 
/*      */       public void handleEvent(Event event)
/*      */       {
/*  131 */         DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), 65536);
/*      */         
/*  133 */         dialog.setFilterPath(pathParameter.getValue());
/*  134 */         dialog.setMessage(MessageText.getString("ConfigView.dialog.choosedefaultsavepath"));
/*  135 */         dialog.setText(MessageText.getString("ConfigView.section.file.defaultdir.ask"));
/*  136 */         String path = dialog.open();
/*  137 */         if (path != null) {
/*  138 */           pathParameter.setValue(path);
/*      */         }
/*      */         
/*      */       }
/*      */       
/*  143 */     });
/*  144 */     sCurConfigID = "ui.addtorrent.openoptions";
/*  145 */     allConfigIDs.add(sCurConfigID);
/*  146 */     Composite cOpenOptions = new Composite(gDefaultDir, 0);
/*  147 */     gridData = new GridData(768);
/*  148 */     gridData.horizontalSpan = 3;
/*  149 */     Utils.setLayoutData(cOpenOptions, gridData);
/*  150 */     RowLayout rowLayout = new RowLayout();
/*  151 */     rowLayout.marginBottom = (rowLayout.marginLeft = rowLayout.marginRight = rowLayout.marginTop = 0);
/*  152 */     rowLayout.center = true;
/*  153 */     cOpenOptions.setLayout(rowLayout);
/*      */     
/*  155 */     Label label = new Label(cOpenOptions, 0);
/*  156 */     Messages.setLanguageText(label, "ConfigView.section.file.showopentorrentoptions");
/*  157 */     String[] openValues = { "never", "always", "many" };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  162 */     String[] openLabels = { MessageText.getString("OpenTorrentOptions.show.never"), MessageText.getString("OpenTorrentOptions.show.always"), MessageText.getString("OpenTorrentOptions.show.many") };
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  167 */     new StringListParameter(cOpenOptions, sCurConfigID, openLabels, openValues);
/*      */     
/*  169 */     label = new Label(cOpenOptions, 0);
/*  170 */     label.setText("    ");
/*  171 */     sCurConfigID = "ui.addtorrent.openoptions.sep";
/*  172 */     new BooleanParameter(cOpenOptions, sCurConfigID, "ConfigView.section.file.showopentorrentoptions.sep");
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  177 */     if (userMode > 0)
/*      */     {
/*  179 */       sCurConfigID = "DefaultDir.AutoSave.AutoRename";
/*  180 */       allConfigIDs.add(sCurConfigID);
/*  181 */       BooleanParameter autoSaveAutoRename = new BooleanParameter(gDefaultDir, sCurConfigID, "ConfigView.section.file.defaultdir.autorename");
/*      */       
/*  183 */       gridData = new GridData(768);
/*  184 */       gridData.horizontalSpan = 3;
/*  185 */       autoSaveAutoRename.setLayoutData(gridData);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  190 */       sCurConfigID = "DefaultDir.BestGuess";
/*  191 */       allConfigIDs.add(sCurConfigID);
/*  192 */       final BooleanParameter bestGuess = new BooleanParameter(gDefaultDir, sCurConfigID, "ConfigView.section.file.defaultdir.bestguess");
/*      */       
/*  194 */       gridData = new GridData(768);
/*  195 */       gridData.horizontalSpan = 3;
/*  196 */       bestGuess.setLayoutData(gridData);
/*      */       
/*      */ 
/*  199 */       sCurConfigID = "DefaultDir.BestGuess.Default";
/*  200 */       allConfigIDs.add(sCurConfigID);
/*  201 */       final Label lblBestGuessDefaultDir = new Label(gDefaultDir, 0);
/*  202 */       Messages.setLanguageText(lblBestGuessDefaultDir, "ConfigView.section.file.bgdefaultdir.ask");
/*      */       
/*  204 */       gridData = new GridData();
/*  205 */       gridData.horizontalIndent = 25;
/*  206 */       Utils.setLayoutData(lblBestGuessDefaultDir, gridData);
/*      */       
/*  208 */       gridData = new GridData(768);
/*  209 */       final StringParameter bestGuessPathParameter = new StringParameter(gDefaultDir, sCurConfigID);
/*      */       
/*  211 */       bestGuessPathParameter.setLayoutData(gridData);
/*      */       
/*  213 */       final Button bestGuessBrowse = new Button(gDefaultDir, 8);
/*  214 */       bestGuessBrowse.setImage(imgOpenFolder);
/*  215 */       bestGuessBrowse.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/*      */       
/*  217 */       bestGuessBrowse.addListener(13, new Listener()
/*      */       {
/*      */ 
/*      */         public void handleEvent(Event event)
/*      */         {
/*  222 */           DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), 65536);
/*      */           
/*  224 */           dialog.setFilterPath(pathParameter.getValue());
/*  225 */           dialog.setMessage(MessageText.getString("ConfigView.dialog.choosedefaultsavepath"));
/*  226 */           dialog.setText(MessageText.getString("ConfigView.section.file.defaultdir.ask"));
/*  227 */           String path = dialog.open();
/*  228 */           if (path != null) {
/*  229 */             bestGuessPathParameter.setValue(path);
/*      */           }
/*      */           
/*      */         }
/*  233 */       });
/*  234 */       COConfigurationManager.addAndFireParameterListener("Default save path", new ParameterListener()
/*      */       {
/*      */ 
/*      */         public void parameterChanged(String parameterName)
/*      */         {
/*      */ 
/*  240 */           if (gDefaultDir.isDisposed())
/*      */           {
/*  242 */             COConfigurationManager.removeParameterListener(parameterName, this);
/*      */           }
/*      */           else
/*      */           {
/*  246 */             String dsp = COConfigurationManager.getStringParameter(parameterName);
/*      */             
/*  248 */             boolean enable = (dsp == null) || (dsp.trim().length() == 0);
/*      */             
/*  250 */             bestGuess.setEnabled(enable);
/*  251 */             lblBestGuessDefaultDir.setEnabled(enable);
/*  252 */             bestGuessPathParameter.setEnabled(enable);
/*  253 */             bestGuessBrowse.setEnabled(enable);
/*      */           }
/*      */           
/*      */         }
/*  257 */       });
/*  258 */       Composite cHistory = new Composite(gDefaultDir, 0);
/*      */       
/*  260 */       layout = new GridLayout();
/*  261 */       layout.numColumns = 6;
/*      */       
/*  263 */       cHistory.setLayout(layout);
/*  264 */       gridData = new GridData(768);
/*  265 */       gridData.horizontalSpan = 3;
/*  266 */       Utils.setLayoutData(cHistory, gridData);
/*      */       
/*      */ 
/*  269 */       sCurConfigID = "DefaultDir.AutoUpdate";
/*  270 */       allConfigIDs.add(sCurConfigID);
/*  271 */       BooleanParameter autoUpdateSaveDir = new BooleanParameter(cHistory, sCurConfigID, "ConfigView.section.file.defaultdir.lastused");
/*      */       
/*      */ 
/*  274 */       Label padLabel = new Label(cHistory, 0);
/*  275 */       gridData = new GridData(768);
/*  276 */       Utils.setLayoutData(padLabel, gridData);
/*      */       
/*  278 */       sCurConfigID = "saveTo_list.max_entries";
/*  279 */       allConfigIDs.add(sCurConfigID);
/*  280 */       Label historyMax = new Label(cHistory, 0);
/*  281 */       Messages.setLanguageText(historyMax, "ConfigView.label.save_list.max_entries");
/*      */       
/*  283 */       IntParameter paramhistoryMax = new IntParameter(cHistory, sCurConfigID);
/*      */       
/*  285 */       Label historyReset = new Label(cHistory, 0);
/*  286 */       Messages.setLanguageText(historyReset, "ConfigView.label.save_list.clear");
/*      */       
/*  288 */       final Button clear_history_button = new Button(cHistory, 8);
/*  289 */       Messages.setLanguageText(clear_history_button, "Button.clear");
/*      */       
/*  291 */       clear_history_button.addListener(13, new Listener() {
/*      */         public void handleEvent(Event event) {
/*  293 */           COConfigurationManager.setParameter("saveTo_list", new StringListImpl());
/*  294 */           clear_history_button.setEnabled(false);
/*      */         }
/*      */         
/*  297 */       });
/*  298 */       StringList dirList = COConfigurationManager.getStringListParameter("saveTo_list");
/*      */       
/*  300 */       clear_history_button.setEnabled(dirList.size() > 0);
/*      */     }
/*      */     
/*  303 */     label = new Label(gFile, 0);
/*  304 */     gridData = new GridData();
/*  305 */     gridData.horizontalSpan = 2;
/*  306 */     label.setLayoutData(gridData);
/*      */     
/*      */ 
/*      */ 
/*  310 */     sCurConfigID = "XFS Allocation";
/*  311 */     allConfigIDs.add(sCurConfigID);
/*  312 */     if ((userMode > 0) && (!Constants.isWindows)) {
/*  313 */       BooleanParameter xfsAllocation = new BooleanParameter(gFile, sCurConfigID, "ConfigView.label.xfs.allocation");
/*      */       
/*  315 */       gridData = new GridData();
/*  316 */       gridData.horizontalSpan = 2;
/*  317 */       xfsAllocation.setLayoutData(gridData);
/*      */     }
/*      */     
/*  320 */     BooleanParameter zeroNew = null;
/*      */     
/*  322 */     sCurConfigID = "Zero New";
/*  323 */     allConfigIDs.add(sCurConfigID);
/*  324 */     if (userMode > 0)
/*      */     {
/*  326 */       zeroNew = new BooleanParameter(gFile, sCurConfigID, "ConfigView.label.zeronewfiles");
/*      */       
/*  328 */       gridData = new GridData();
/*  329 */       gridData.horizontalSpan = 2;
/*  330 */       zeroNew.setLayoutData(gridData);
/*      */     }
/*      */     
/*  333 */     BooleanParameter pieceReorder = null;
/*      */     
/*  335 */     sCurConfigID = "Enable reorder storage mode";
/*  336 */     allConfigIDs.add(sCurConfigID);
/*  337 */     if (userMode > 0)
/*      */     {
/*  339 */       pieceReorder = new BooleanParameter(gFile, sCurConfigID, "ConfigView.label.piecereorder");
/*      */       
/*  341 */       gridData = new GridData();
/*  342 */       gridData.horizontalSpan = 2;
/*  343 */       pieceReorder.setLayoutData(gridData);
/*      */       
/*      */ 
/*  346 */       Button[] btnReorder = { (Button)pieceReorder.getControl() };
/*      */       
/*      */ 
/*  349 */       zeroNew.setAdditionalActionPerformer(new ExclusiveSelectionActionPerformer(btnReorder));
/*      */       
/*      */ 
/*      */ 
/*  353 */       Button[] btnZeroNew = { (Button)zeroNew.getControl() };
/*      */       
/*      */ 
/*  356 */       pieceReorder.setAdditionalActionPerformer(new ExclusiveSelectionActionPerformer(btnZeroNew));
/*      */     }
/*      */     
/*      */ 
/*  360 */     sCurConfigID = "Reorder storage mode min MB";
/*  361 */     allConfigIDs.add(sCurConfigID);
/*      */     
/*  363 */     if (userMode > 0) {
/*  364 */       Label lblMinMB = new Label(gFile, 0);
/*  365 */       Messages.setLanguageText(lblMinMB, "ConfigView.label.piecereorderminmb");
/*  366 */       gridData = new GridData();
/*  367 */       gridData.horizontalIndent = 25;
/*  368 */       Utils.setLayoutData(lblMinMB, gridData);
/*      */       
/*  370 */       IntParameter minMB = new IntParameter(gFile, sCurConfigID);
/*  371 */       gridData = new GridData();
/*  372 */       minMB.setLayoutData(gridData);
/*      */       
/*  374 */       pieceReorder.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(lblMinMB));
/*      */       
/*  376 */       pieceReorder.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(minMB));
/*      */     }
/*      */     
/*      */ 
/*  380 */     sCurConfigID = "Enable incremental file creation";
/*  381 */     allConfigIDs.add(sCurConfigID);
/*  382 */     if (userMode > 0)
/*      */     {
/*  384 */       BooleanParameter incremental = new BooleanParameter(gFile, sCurConfigID, "ConfigView.label.incrementalfile");
/*      */       
/*  386 */       gridData = new GridData();
/*  387 */       gridData.horizontalSpan = 2;
/*  388 */       incremental.setLayoutData(gridData);
/*      */       
/*      */ 
/*  391 */       Button[] btnIncremental = { (Button)incremental.getControl() };
/*      */       
/*      */ 
/*  394 */       zeroNew.setAdditionalActionPerformer(new ExclusiveSelectionActionPerformer(btnIncremental));
/*      */       
/*      */ 
/*      */ 
/*  398 */       Button[] btnZeroNew = { (Button)zeroNew.getControl() };
/*      */       
/*      */ 
/*  401 */       incremental.setAdditionalActionPerformer(new ExclusiveSelectionActionPerformer(btnZeroNew));
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  407 */     sCurConfigID = "File.truncate.if.too.large";
/*  408 */     allConfigIDs.add(sCurConfigID);
/*  409 */     if (userMode > 0)
/*      */     {
/*  411 */       BooleanParameter truncateLarge = new BooleanParameter(gFile, sCurConfigID, "ConfigView.section.file.truncate.too.large");
/*      */       
/*  413 */       gridData = new GridData();
/*  414 */       gridData.horizontalSpan = 2;
/*  415 */       truncateLarge.setLayoutData(gridData);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  420 */     sCurConfigID = "Merge Same Size Files";
/*  421 */     allConfigIDs.add(sCurConfigID);
/*  422 */     BooleanParameter mergeSameSize = null;
/*      */     
/*  424 */     if (userMode > 0) {
/*  425 */       mergeSameSize = new BooleanParameter(gFile, sCurConfigID, "ConfigView.section.file.merge.same.size");
/*      */       
/*  427 */       gridData = new GridData();
/*  428 */       gridData.horizontalSpan = 2;
/*  429 */       mergeSameSize.setLayoutData(gridData);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  434 */     sCurConfigID = "Merge Same Size Files Extended";
/*  435 */     allConfigIDs.add(sCurConfigID);
/*  436 */     if (mergeSameSize != null) {
/*  437 */       BooleanParameter mergeSameSizeExt = new BooleanParameter(gFile, sCurConfigID, "ConfigView.section.file.merge.same.size.extended");
/*      */       
/*  439 */       gridData = new GridData();
/*  440 */       gridData.horizontalIndent = 25;
/*  441 */       gridData.horizontalSpan = 2;
/*  442 */       mergeSameSizeExt.setLayoutData(gridData);
/*      */       
/*  444 */       IAdditionalActionPerformer mergeAP = new ChangeSelectionActionPerformer(mergeSameSizeExt.getControls(), false);
/*      */       
/*  446 */       mergeSameSize.setAdditionalActionPerformer(mergeAP);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  451 */     sCurConfigID = "Check Pieces on Completion";
/*  452 */     allConfigIDs.add(sCurConfigID);
/*  453 */     if (userMode > 0)
/*      */     {
/*  455 */       BooleanParameter checkOnComp = new BooleanParameter(gFile, sCurConfigID, "ConfigView.label.checkOncompletion");
/*      */       
/*  457 */       gridData = new GridData();
/*  458 */       gridData.horizontalSpan = 2;
/*  459 */       checkOnComp.setLayoutData(gridData);
/*      */     }
/*      */     
/*  462 */     sCurConfigID = "Seeding Piece Check Recheck Enable";
/*  463 */     allConfigIDs.add(sCurConfigID);
/*  464 */     if (userMode > 0)
/*      */     {
/*  466 */       BooleanParameter checkOnSeeding = new BooleanParameter(gFile, sCurConfigID, "ConfigView.label.checkOnSeeding");
/*      */       
/*  468 */       gridData = new GridData();
/*  469 */       gridData.horizontalSpan = 2;
/*  470 */       checkOnSeeding.setLayoutData(gridData);
/*      */     }
/*      */     
/*  473 */     sCurConfigID = "File.strict.locking";
/*  474 */     allConfigIDs.add(sCurConfigID);
/*  475 */     if (userMode > 1)
/*      */     {
/*  477 */       BooleanParameter strictLocking = new BooleanParameter(gFile, sCurConfigID, "ConfigView.label.strictfilelocking");
/*      */       
/*  479 */       gridData = new GridData();
/*  480 */       gridData.horizontalSpan = 2;
/*  481 */       strictLocking.setLayoutData(gridData);
/*      */     }
/*      */     
/*  484 */     if (userMode == 0) {
/*  485 */       allConfigIDs.add("Use Resume");
/*  486 */       sCurConfigID = "Save Resume Interval";
/*  487 */       allConfigIDs.add(sCurConfigID);
/*  488 */       sCurConfigID = "On Resume Recheck All";
/*  489 */       allConfigIDs.add(sCurConfigID);
/*  490 */       sCurConfigID = "File.save.peers.enable";
/*  491 */       allConfigIDs.add(sCurConfigID);
/*  492 */       sCurConfigID = "File.save.peers.max";
/*  493 */       allConfigIDs.add(sCurConfigID);
/*      */ 
/*      */     }
/*      */     else
/*      */     {
/*      */ 
/*  499 */       Composite maxLinksGroup = new Composite(gFile, 0);
/*  500 */       layout = new GridLayout();
/*  501 */       layout.marginHeight = 0;
/*  502 */       layout.marginWidth = 0;
/*  503 */       layout.numColumns = 3;
/*  504 */       maxLinksGroup.setLayout(layout);
/*  505 */       gridData = new GridData(768);
/*  506 */       gridData.horizontalSpan = 2;
/*  507 */       Utils.setLayoutData(maxLinksGroup, gridData);
/*      */       
/*  509 */       Label maxLinks = new Label(maxLinksGroup, 0);
/*  510 */       Messages.setLanguageText(maxLinks, "ConfigView.label.max.file.links");
/*      */       
/*  512 */       sCurConfigID = "Max File Links Supported";
/*  513 */       allConfigIDs.add(sCurConfigID);
/*      */       
/*  515 */       new IntParameter(maxLinksGroup, sCurConfigID, 8, Integer.MAX_VALUE);
/*      */       
/*      */ 
/*  518 */       Label maxLinksWarning = new Label(maxLinksGroup, 0);
/*  519 */       Messages.setLanguageText(maxLinksWarning, "ConfigView.label.max.file.links.warning");
/*  520 */       gridData = new GridData(768);
/*  521 */       maxLinksWarning.setLayoutData(gridData);
/*      */       
/*      */ 
/*      */ 
/*  525 */       sCurConfigID = "Insufficient Space Download Restart Enable";
/*  526 */       allConfigIDs.add(sCurConfigID);
/*      */       
/*  528 */       final BooleanParameter OOSDRE = new BooleanParameter(gFile, sCurConfigID, "ConfigView.label.restart.no.space.dls");
/*      */       
/*  530 */       gridData = new GridData(2);
/*  531 */       gridData.horizontalSpan = 2;
/*  532 */       OOSDRE.setLayoutData(gridData);
/*      */       
/*  534 */       Composite cOOSDGroup = new Composite(gFile, 0);
/*  535 */       layout = new GridLayout();
/*  536 */       layout.marginHeight = 0;
/*  537 */       layout.marginWidth = 4;
/*  538 */       layout.numColumns = 3;
/*  539 */       cOOSDGroup.setLayout(layout);
/*  540 */       gridData = new GridData(768);
/*  541 */       gridData.horizontalIndent = 25;
/*  542 */       gridData.horizontalSpan = 2;
/*  543 */       Utils.setLayoutData(cOOSDGroup, gridData);
/*      */       
/*  545 */       sCurConfigID = "Insufficient Space Download Restart Period";
/*  546 */       allConfigIDs.add(sCurConfigID);
/*  547 */       Label lblOOSDRInterval = new Label(cOOSDGroup, 0);
/*  548 */       Messages.setLanguageText(lblOOSDRInterval, "ConfigView.label.restart.no.space.dls.interval");
/*      */       
/*      */ 
/*  551 */       IntParameter paramOOSDRInterval = new IntParameter(cOOSDGroup, sCurConfigID, 1, Integer.MAX_VALUE);
/*      */       
/*  553 */       gridData = new GridData();
/*  554 */       paramOOSDRInterval.setLayoutData(gridData);
/*      */       
/*  556 */       Label lblOOSDRMinutes = new Label(cOOSDGroup, 0);
/*  557 */       Messages.setLanguageText(lblOOSDRMinutes, "ConfigView.text.minutes");
/*      */       
/*  559 */       Control[] OOSDRContrls = { cOOSDGroup };
/*      */       
/*  561 */       IAdditionalActionPerformer OOSDREnabler = new GenericActionPerformer(OOSDRContrls)
/*      */       {
/*      */         public void performAction()
/*      */         {
/*  565 */           controlsSetEnabled(this.controls, OOSDRE.isSelected().booleanValue());
/*      */         }
/*      */         
/*  568 */       };
/*  569 */       OOSDRE.setAdditionalActionPerformer(OOSDREnabler);
/*      */       
/*      */ 
/*      */ 
/*  573 */       sCurConfigID = "Use Resume";
/*  574 */       allConfigIDs.add(sCurConfigID);
/*      */       
/*  576 */       final BooleanParameter bpUseResume = new BooleanParameter(gFile, sCurConfigID, "ConfigView.label.usefastresume");
/*      */       
/*  578 */       bpUseResume.setLayoutData(new GridData(2));
/*      */       
/*  580 */       Composite cResumeGroup = new Composite(gFile, 0);
/*  581 */       layout = new GridLayout();
/*  582 */       layout.marginHeight = 0;
/*  583 */       layout.marginWidth = 4;
/*  584 */       layout.numColumns = 3;
/*  585 */       cResumeGroup.setLayout(layout);
/*  586 */       gridData = new GridData(768);
/*  587 */       gridData.horizontalIndent = 25;
/*  588 */       gridData.horizontalSpan = 2;
/*  589 */       Utils.setLayoutData(cResumeGroup, gridData);
/*      */       
/*  591 */       sCurConfigID = "Save Resume Interval";
/*  592 */       allConfigIDs.add(sCurConfigID);
/*  593 */       Label lblSaveResumeInterval = new Label(cResumeGroup, 0);
/*  594 */       Messages.setLanguageText(lblSaveResumeInterval, "ConfigView.label.saveresumeinterval");
/*      */       
/*      */ 
/*  597 */       IntParameter paramSaveInterval = new IntParameter(cResumeGroup, sCurConfigID);
/*      */       
/*  599 */       gridData = new GridData();
/*  600 */       paramSaveInterval.setLayoutData(gridData);
/*      */       
/*  602 */       Label lblMinutes = new Label(cResumeGroup, 0);
/*  603 */       Messages.setLanguageText(lblMinutes, "ConfigView.text.minutes");
/*      */       
/*      */ 
/*      */ 
/*  607 */       sCurConfigID = "On Resume Recheck All";
/*  608 */       allConfigIDs.add(sCurConfigID);
/*  609 */       BooleanParameter recheck_all = new BooleanParameter(cResumeGroup, sCurConfigID, "ConfigView.section.file.resume.recheck.all");
/*      */       
/*  611 */       gridData = new GridData();
/*  612 */       gridData.horizontalSpan = 3;
/*  613 */       recheck_all.setLayoutData(gridData);
/*      */       
/*      */ 
/*  616 */       sCurConfigID = "File.save.peers.enable";
/*  617 */       allConfigIDs.add(sCurConfigID);
/*  618 */       final BooleanParameter save_peers = new BooleanParameter(cResumeGroup, sCurConfigID, "ConfigView.section.file.save.peers.enable");
/*      */       
/*  620 */       gridData = new GridData();
/*  621 */       gridData.horizontalSpan = 3;
/*  622 */       save_peers.setLayoutData(gridData);
/*      */       
/*      */ 
/*      */ 
/*  626 */       sCurConfigID = "File.save.peers.max";
/*  627 */       allConfigIDs.add(sCurConfigID);
/*  628 */       final Label lblSavePeersMax = new Label(cResumeGroup, 0);
/*  629 */       Messages.setLanguageText(lblSavePeersMax, "ConfigView.section.file.save.peers.max");
/*      */       
/*  631 */       final IntParameter savePeersMax = new IntParameter(cResumeGroup, sCurConfigID);
/*      */       
/*  633 */       gridData = new GridData();
/*  634 */       savePeersMax.setLayoutData(gridData);
/*  635 */       final Label lblPerTorrent = new Label(cResumeGroup, 0);
/*  636 */       Messages.setLanguageText(lblPerTorrent, "ConfigView.section.file.save.peers.pertorrent");
/*      */       
/*      */ 
/*  639 */       Control[] controls = { cResumeGroup };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  648 */       IAdditionalActionPerformer f_enabler = new GenericActionPerformer(controls)
/*      */       {
/*      */         public void performAction() {
/*  651 */           controlsSetEnabled(this.controls, bpUseResume.isSelected().booleanValue());
/*      */           
/*  653 */           if (bpUseResume.isSelected().booleanValue()) {
/*  654 */             lblSavePeersMax.setEnabled(save_peers.isSelected().booleanValue());
/*  655 */             savePeersMax.getControl().setEnabled(save_peers.isSelected().booleanValue());
/*  656 */             lblPerTorrent.setEnabled(save_peers.isSelected().booleanValue());
/*      */           }
/*      */           
/*      */         }
/*  660 */       };
/*  661 */       bpUseResume.setAdditionalActionPerformer(f_enabler);
/*  662 */       save_peers.setAdditionalActionPerformer(f_enabler);
/*      */     }
/*      */     
/*      */ 
/*  666 */     if (userMode > 0) {
/*  667 */       sCurConfigID = "priorityExtensions";
/*  668 */       allConfigIDs.add(sCurConfigID);
/*      */       
/*      */ 
/*  671 */       label = new Label(gFile, 64);
/*  672 */       gridData = new GridData();
/*  673 */       gridData.widthHint = 180;
/*  674 */       Utils.setLayoutData(label, gridData);
/*  675 */       Messages.setLanguageText(label, "ConfigView.label.priorityExtensions");
/*      */       
/*  677 */       Composite cExtensions = new Composite(gFile, 0);
/*  678 */       gridData = new GridData(768);
/*  679 */       Utils.setLayoutData(cExtensions, gridData);
/*  680 */       layout = new GridLayout();
/*  681 */       layout.marginHeight = 0;
/*  682 */       layout.marginWidth = 0;
/*  683 */       layout.numColumns = 3;
/*  684 */       cExtensions.setLayout(layout);
/*      */       
/*  686 */       gridData = new GridData(768);
/*  687 */       new StringParameter(cExtensions, sCurConfigID).setLayoutData(gridData);
/*      */       
/*  689 */       sCurConfigID = "priorityExtensionsIgnoreCase";
/*  690 */       allConfigIDs.add(sCurConfigID);
/*  691 */       new BooleanParameter(cExtensions, sCurConfigID, "ConfigView.label.ignoreCase");
/*      */     }
/*      */     else {
/*  694 */       sCurConfigID = "priorityExtensions";
/*  695 */       allConfigIDs.add(sCurConfigID);
/*  696 */       sCurConfigID = "priorityExtensionsIgnoreCase";
/*  697 */       allConfigIDs.add(sCurConfigID);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  702 */     sCurConfigID = "quick.view.exts";
/*  703 */     allConfigIDs.add(sCurConfigID);
/*      */     
/*  705 */     label = new Label(gFile, 64);
/*  706 */     gridData = new GridData();
/*  707 */     gridData.widthHint = 180;
/*  708 */     Utils.setLayoutData(label, gridData);
/*  709 */     Messages.setLanguageText(label, "ConfigView.label.quickviewexts");
/*      */     
/*  711 */     Composite cQuickView = new Composite(gFile, 0);
/*  712 */     gridData = new GridData(768);
/*  713 */     Utils.setLayoutData(cQuickView, gridData);
/*  714 */     layout = new GridLayout();
/*  715 */     layout.marginHeight = 0;
/*  716 */     layout.marginWidth = 0;
/*  717 */     layout.numColumns = 3;
/*  718 */     cQuickView.setLayout(layout);
/*      */     
/*  720 */     gridData = new GridData(768);
/*  721 */     new StringParameter(cQuickView, sCurConfigID).setLayoutData(gridData);
/*      */     
/*  723 */     label = new Label(cQuickView, 0);
/*  724 */     Messages.setLanguageText(label, "ConfigView.label.quickviewmaxkb");
/*      */     
/*  726 */     sCurConfigID = "quick.view.maxkb";
/*  727 */     allConfigIDs.add(sCurConfigID);
/*  728 */     IntParameter qvmax = new IntParameter(cQuickView, sCurConfigID, 1, 9999);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  733 */     if (userMode > 0)
/*      */     {
/*      */ 
/*      */ 
/*  737 */       sCurConfigID = "Rename Incomplete Files";
/*  738 */       allConfigIDs.add(sCurConfigID);
/*      */       
/*  740 */       gridData = new GridData();
/*  741 */       gridData.horizontalSpan = 1;
/*  742 */       final BooleanParameter rename_incomplete = new BooleanParameter(gFile, sCurConfigID, "ConfigView.section.file.rename.incomplete");
/*      */       
/*  744 */       rename_incomplete.setLayoutData(gridData);
/*      */       
/*  746 */       sCurConfigID = "Rename Incomplete Files Extension";
/*  747 */       allConfigIDs.add(sCurConfigID);
/*  748 */       gridData = new GridData(768);
/*  749 */       StringParameter rename_incomplete_ext = new StringParameter(gFile, sCurConfigID);
/*      */       
/*  751 */       rename_incomplete_ext.setLayoutData(gridData);
/*      */       
/*  753 */       IAdditionalActionPerformer incompFileAP = new ChangeSelectionActionPerformer(rename_incomplete_ext.getControls(), false);
/*      */       
/*  755 */       rename_incomplete.setAdditionalActionPerformer(incompFileAP);
/*      */       
/*      */ 
/*      */ 
/*  759 */       sCurConfigID = "Enable Subfolder for DND Files";
/*  760 */       allConfigIDs.add(sCurConfigID);
/*      */       
/*  762 */       gridData = new GridData();
/*  763 */       gridData.horizontalSpan = 1;
/*  764 */       final BooleanParameter enable_subfolder = new BooleanParameter(gFile, sCurConfigID, "ConfigView.section.file.subfolder.dnd");
/*      */       
/*  766 */       rename_incomplete.setLayoutData(gridData);
/*      */       
/*  768 */       sCurConfigID = "Subfolder for DND Files";
/*  769 */       allConfigIDs.add(sCurConfigID);
/*  770 */       gridData = new GridData(768);
/*  771 */       StringParameter subfolder_name = new StringParameter(gFile, sCurConfigID);
/*      */       
/*  773 */       subfolder_name.setLayoutData(gridData);
/*      */       
/*  775 */       IAdditionalActionPerformer subfolderAP = new ChangeSelectionActionPerformer(subfolder_name.getControls(), false);
/*      */       
/*  777 */       enable_subfolder.setAdditionalActionPerformer(subfolderAP);
/*      */       
/*      */ 
/*      */ 
/*  781 */       sCurConfigID = "Use Incomplete File Prefix";
/*  782 */       allConfigIDs.add(sCurConfigID);
/*      */       
/*  784 */       gridData = new GridData();
/*  785 */       gridData.horizontalSpan = 2;
/*  786 */       gridData.horizontalIndent = 25;
/*  787 */       final BooleanParameter enable_dndprefix = new BooleanParameter(gFile, sCurConfigID, "ConfigView.section.file.dnd.prefix.enable");
/*      */       
/*  789 */       enable_dndprefix.setLayoutData(gridData);
/*      */       
/*  791 */       ParameterChangeListener listener = new ParameterChangeAdapter()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void parameterChanged(Parameter p, boolean caused_internally)
/*      */         {
/*      */ 
/*      */ 
/*  799 */           enable_dndprefix.setEnabled((enable_subfolder.isSelected().booleanValue()) || (rename_incomplete.isSelected().booleanValue()));
/*      */         }
/*      */         
/*  802 */       };
/*  803 */       enable_subfolder.addChangeListener(listener);
/*  804 */       rename_incomplete.addChangeListener(listener);
/*      */       
/*  806 */       listener.parameterChanged(null, true);
/*      */     }
/*      */     
/*      */ 
/*  810 */     sCurConfigID = "Download History Enabled";
/*  811 */     allConfigIDs.add(sCurConfigID);
/*  812 */     BooleanParameter recordDLHistory = new BooleanParameter(gFile, sCurConfigID, "ConfigView.label.record.dl.history");
/*      */     
/*  814 */     gridData = new GridData();
/*  815 */     gridData.horizontalSpan = 2;
/*  816 */     recordDLHistory.setLayoutData(gridData);
/*      */     
/*  818 */     if (userMode > 0)
/*      */     {
/*  820 */       Group gIgnoredFiles = new Group(gFile, 0);
/*  821 */       Messages.setLanguageText(gIgnoredFiles, "ConfigView.section.file.ignore.section");
/*      */       
/*  823 */       layout = new GridLayout();
/*  824 */       layout.numColumns = 2;
/*  825 */       layout.marginHeight = 5;
/*  826 */       gIgnoredFiles.setLayout(layout);
/*  827 */       gridData = new GridData(768);
/*  828 */       gridData.horizontalSpan = 2;
/*  829 */       Utils.setLayoutData(gIgnoredFiles, gridData);
/*      */       
/*      */ 
/*      */ 
/*  833 */       Label lSkipFiles = new Label(gIgnoredFiles, 0);
/*  834 */       Messages.setLanguageText(lSkipFiles, "ConfigView.section.file.torrent.autoskipfiles");
/*      */       
/*      */ 
/*  837 */       gridData = new GridData(768);
/*  838 */       new StringParameter(gIgnoredFiles, "File.Torrent.AutoSkipExtensions").setLayoutData(gridData);
/*      */       
/*      */ 
/*      */ 
/*  842 */       Label lSkipFilesMinSize = new Label(gIgnoredFiles, 0);
/*  843 */       Messages.setLanguageText(lSkipFilesMinSize, "ConfigView.section.file.torrent.autoskipfilesminsize");
/*      */       
/*      */ 
/*  846 */       new IntParameter(gIgnoredFiles, "File.Torrent.AutoSkipMinSizeKB", 0, Integer.MAX_VALUE);
/*      */       
/*      */ 
/*      */ 
/*  850 */       Label lIgnoreFiles = new Label(gIgnoredFiles, 0);
/*  851 */       Messages.setLanguageText(lIgnoreFiles, "ConfigView.section.file.torrent.ignorefiles");
/*      */       
/*      */ 
/*  854 */       gridData = new GridData(768);
/*  855 */       new StringParameter(gIgnoredFiles, "File.Torrent.IgnoreFiles", ".DS_Store;Thumbs.db;desktop.ini").setLayoutData(gridData);
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  861 */       if (userMode > 1) {
/*  862 */         Label lFileCharConv = new Label(gFile, 0);
/*  863 */         Messages.setLanguageText(lFileCharConv, "ConfigView.section.file.char.conversions");
/*      */         
/*      */ 
/*  866 */         gridData = new GridData(768);
/*  867 */         new StringParameter(gFile, "File.Character.Conversions", "\"='").setLayoutData(gridData);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  874 */     Group gDeletion = new Group(gFile, 0);
/*  875 */     Messages.setLanguageText(gDeletion, "ConfigView.section.file.deletion.section");
/*      */     
/*  877 */     layout = new GridLayout();
/*  878 */     layout.numColumns = 2;
/*  879 */     layout.marginHeight = 5;
/*  880 */     gDeletion.setLayout(layout);
/*  881 */     gridData = new GridData(768);
/*  882 */     gridData.horizontalSpan = 2;
/*  883 */     Utils.setLayoutData(gDeletion, gridData);
/*      */     
/*  885 */     if (userMode > 0) {
/*  886 */       Composite c = new Composite(gDeletion, 0);
/*  887 */       layout = new GridLayout();
/*  888 */       layout.numColumns = 2;
/*  889 */       layout.marginHeight = 0;
/*  890 */       layout.marginWidth = 0;
/*  891 */       c.setLayout(layout);
/*  892 */       gridData = new GridData(768);
/*  893 */       gridData.horizontalSpan = 2;
/*  894 */       Utils.setLayoutData(c, gridData);
/*      */       
/*  896 */       sCurConfigID = "tb.confirm.delete.content";
/*  897 */       label = new Label(c, 0);
/*  898 */       Messages.setLanguageText(label, "ConfigView.section.file.tb.delete");
/*  899 */       int[] values = { 0, 1, 2 };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  904 */       String[] labels = { MessageText.getString("ConfigView.tb.delete.ask"), MessageText.getString("ConfigView.tb.delete.content"), MessageText.getString("ConfigView.tb.delete.torrent") };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*  909 */       new IntListParameter(c, sCurConfigID, labels, values);
/*      */       
/*      */ 
/*  912 */       sCurConfigID = "def.deletetorrent";
/*  913 */       allConfigIDs.add(sCurConfigID);
/*  914 */       gridData = new GridData();
/*  915 */       gridData.horizontalSpan = 2;
/*  916 */       new BooleanParameter(gDeletion, sCurConfigID, "ConfigView.section.file.delete.torrent").setLayoutData(gridData);
/*      */     }
/*      */     
/*      */     try
/*      */     {
/*  921 */       PlatformManager platform = PlatformManagerFactory.getPlatformManager();
/*      */       
/*  923 */       if (platform.hasCapability(PlatformManagerCapabilities.RecoverableFileDelete)) {
/*  924 */         sCurConfigID = "Move Deleted Data To Recycle Bin";
/*  925 */         allConfigIDs.add(sCurConfigID);
/*      */         
/*  927 */         gridData = new GridData();
/*  928 */         gridData.horizontalSpan = 2;
/*  929 */         new BooleanParameter(gDeletion, sCurConfigID, "ConfigView.section.file.nativedelete").setLayoutData(gridData);
/*      */       }
/*      */     }
/*      */     catch (Throwable e) {}
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  937 */     if (userMode > 0) {
/*  938 */       sCurConfigID = "File.delete.include_files_outside_save_dir";
/*  939 */       allConfigIDs.add(sCurConfigID);
/*      */       
/*  941 */       gridData = new GridData();
/*  942 */       gridData.horizontalSpan = 2;
/*  943 */       new BooleanParameter(gDeletion, sCurConfigID, "ConfigView.section.file.delete.include_files_outside_save_dir").setLayoutData(gridData);
/*      */       
/*      */ 
/*  946 */       sCurConfigID = "Delete Partial Files On Library Removal";
/*  947 */       allConfigIDs.add(sCurConfigID);
/*      */       
/*  949 */       gridData = new GridData();
/*  950 */       gridData.horizontalSpan = 2;
/*  951 */       new BooleanParameter(gDeletion, sCurConfigID, "delete.partial.files").setLayoutData(gridData);
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  956 */     if (userMode > 0) {
/*  957 */       Group gConfigSettings = new Group(gFile, 0);
/*  958 */       Messages.setLanguageText(gConfigSettings, "ConfigView.section.file.config.section");
/*      */       
/*  960 */       layout = new GridLayout();
/*  961 */       layout.numColumns = 2;
/*  962 */       layout.marginHeight = 5;
/*  963 */       gConfigSettings.setLayout(layout);
/*  964 */       gridData = new GridData(768);
/*  965 */       gridData.horizontalSpan = 2;
/*  966 */       Utils.setLayoutData(gConfigSettings, gridData);
/*      */       
/*      */ 
/*  969 */       Label config_label = new Label(gConfigSettings, 0);
/*  970 */       Messages.setLanguageText(config_label, "ConfigView.section.file.config.currentdir");
/*      */       
/*  972 */       Utils.setLayoutData(config_label, new GridData());
/*  973 */       Label config_link = new Label(gConfigSettings, 0);
/*  974 */       config_link.setText(SystemProperties.getUserPath());
/*  975 */       Utils.setLayoutData(config_link, new GridData());
/*  976 */       LinkLabel.makeLinkedLabel(config_link, SystemProperties.getUserPath());
/*      */       
/*  978 */       sCurConfigID = "Use Config File Backups";
/*  979 */       allConfigIDs.add(sCurConfigID);
/*      */       
/*      */ 
/*  982 */       BooleanParameter backupConfig = new BooleanParameter(gConfigSettings, sCurConfigID, "ConfigView.label.backupconfigfiles");
/*      */       
/*  984 */       gridData = new GridData();
/*  985 */       gridData.horizontalSpan = 2;
/*  986 */       backupConfig.setLayoutData(gridData);
/*      */     }
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
/* 1003 */     return gFile;
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionFile.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */