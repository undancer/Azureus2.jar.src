/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.io.File;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.FileDialog;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.ipfilter.BannedIp;
/*     */ import org.gudy.azureus2.core3.ipfilter.IPFilterListener;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpRange;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.FloatParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IpFilterEditor;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
/*     */ 
/*     */ public class ConfigSectionIPFilter
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   IpFilter filter;
/*     */   Table table;
/*     */   boolean noChange;
/*     */   FilterComparator comparator;
/*  61 */   private boolean bIsCachingDescriptions = false;
/*     */   IpRange[] ipRanges;
/*     */   
/*     */   static class FilterComparator implements Comparator {
/*  65 */     boolean ascending = true;
/*     */     
/*     */     static final int FIELD_NAME = 0;
/*     */     
/*     */     static final int FIELD_START_IP = 1;
/*     */     static final int FIELD_END_IP = 2;
/*  71 */     int field = 1;
/*     */     
/*     */     public int compare(Object arg0, Object arg1)
/*     */     {
/*  75 */       IpRange range0 = (IpRange)arg0;
/*  76 */       IpRange range1 = (IpRange)arg1;
/*  77 */       if (this.field == 0) {
/*  78 */         return (this.ascending ? 1 : -1) * range0.compareDescription(range1);
/*     */       }
/*  80 */       if (this.field == 1) {
/*  81 */         return (this.ascending ? 1 : -1) * range0.compareStartIpTo(range1);
/*     */       }
/*  83 */       if (this.field == 2) {
/*  84 */         return (this.ascending ? 1 : -1) * range0.compareEndIpTo(range1);
/*     */       }
/*  86 */       return 0;
/*     */     }
/*     */     
/*     */     public void setField(int newField) {
/*  90 */       if (this.field == newField) this.ascending = (!this.ascending);
/*  91 */       this.field = newField;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   Label percentage_blocked;
/*     */   
/*     */ 
/*     */   private IPFilterListener filterListener;
/*     */   
/*     */ 
/*     */   public ConfigSectionIPFilter()
/*     */   {
/* 105 */     this.comparator = new FilterComparator();
/*     */   }
/*     */   
/*     */   public String configSectionGetParentSection() {
/* 109 */     return "root";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/* 113 */     return "ipfilter";
/*     */   }
/*     */   
/*     */   public void configSectionSave() {
/*     */     try {
/* 118 */       if (this.filter != null)
/* 119 */         this.filter.save();
/*     */     } catch (Exception e) {
/* 121 */       Logger.log(new LogAlert(false, "Save of filter file fails", e));
/*     */     }
/*     */   }
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/* 127 */     return 1;
/*     */   }
/*     */   
/*     */   public void configSectionDelete()
/*     */   {
/* 132 */     if (this.bIsCachingDescriptions) {
/* 133 */       IpFilterManager ipFilterManager = AzureusCoreFactory.getSingleton().getIpFilterManager();
/* 134 */       ipFilterManager.clearDescriptionCache();
/* 135 */       this.bIsCachingDescriptions = false;
/*     */     }
/*     */     
/* 138 */     if (this.filter != null) {
/* 139 */       this.filter.removeListener(this.filterListener);
/*     */     }
/* 141 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 142 */     imageLoader.releaseImage("openFolderButton");
/* 143 */     imageLoader.releaseImage("subitem");
/*     */   }
/*     */   
/*     */   public Composite configSectionCreate(final Composite parent)
/*     */   {
/* 148 */     if (!AzureusCoreFactory.isCoreRunning()) {
/* 149 */       Composite cSection = new Composite(parent, 0);
/* 150 */       cSection.setLayout(new FillLayout());
/* 151 */       Label lblNotAvail = new Label(cSection, 64);
/* 152 */       Messages.setLanguageText(lblNotAvail, "core.not.available");
/* 153 */       return cSection;
/*     */     }
/*     */     
/* 156 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 157 */     Image imgOpenFolder = imageLoader.getImage("openFolderButton");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 163 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/* 165 */     final IpFilterManager ipFilterManager = AzureusCoreFactory.getSingleton().getIpFilterManager();
/* 166 */     this.filter = ipFilterManager.getIPFilter();
/*     */     
/* 168 */     Composite gFilter = new Composite(parent, 0);
/* 169 */     GridLayout layout = new GridLayout();
/* 170 */     layout.marginHeight = 0;
/* 171 */     layout.marginWidth = 0;
/* 172 */     gFilter.setLayout(layout);
/* 173 */     GridData gridData = new GridData(272);
/* 174 */     Utils.setLayoutData(gFilter, gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 181 */     gridData = new GridData();
/*     */     
/* 183 */     BooleanParameter enabled = new BooleanParameter(gFilter, "Ip Filter Enabled");
/* 184 */     enabled.setLayoutData(gridData);
/* 185 */     Messages.setLanguageText(enabled.getControl(), "ConfigView.section.ipfilter.enable");
/*     */     
/* 187 */     gridData = new GridData();
/*     */     
/* 189 */     BooleanParameter deny = new BooleanParameter(gFilter, "Ip Filter Allow");
/* 190 */     deny.setLayoutData(gridData);
/* 191 */     Messages.setLanguageText(deny.getControl(), "ConfigView.section.ipfilter.allow");
/*     */     
/* 193 */     deny.addChangeListener(new ParameterChangeAdapter()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void parameterChanged(Parameter p, boolean caused_internally)
/*     */       {
/*     */ 
/*     */ 
/* 202 */         ConfigSectionIPFilter.this.setPercentageBlocked();
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 207 */     });
/* 208 */     gridData = new GridData();
/*     */     
/* 210 */     BooleanParameter persist_bad_data_banning = new BooleanParameter(gFilter, "Ip Filter Banning Persistent");
/* 211 */     persist_bad_data_banning.setLayoutData(gridData);
/* 212 */     Messages.setLanguageText(persist_bad_data_banning.getControl(), "ConfigView.section.ipfilter.persistblocking");
/*     */     
/* 214 */     Group gBlockBanning = new Group(gFilter, 0);
/* 215 */     Messages.setLanguageText(gBlockBanning, "ConfigView.section.ipfilter.peerblocking.group");
/* 216 */     layout = new GridLayout();
/* 217 */     layout.numColumns = 2;
/* 218 */     gBlockBanning.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 223 */     BooleanParameter enable_bad_data_banning = new BooleanParameter(gBlockBanning, "Ip Filter Enable Banning", "ConfigView.section.ipfilter.enablebanning");
/*     */     
/*     */ 
/* 226 */     gridData = new GridData();
/* 227 */     gridData.horizontalSpan = 2;
/* 228 */     enable_bad_data_banning.setLayoutData(gridData);
/*     */     
/* 230 */     Label discard_label = new Label(gBlockBanning, 0);
/* 231 */     Messages.setLanguageText(discard_label, "ConfigView.section.ipfilter.discardbanning");
/*     */     
/*     */ 
/* 234 */     FloatParameter discard_ratio = new FloatParameter(gBlockBanning, "Ip Filter Ban Discard Ratio");
/* 235 */     gridData = new GridData();
/* 236 */     discard_ratio.setLayoutData(gridData);
/*     */     
/*     */ 
/* 239 */     Composite cIndent = new Composite(gBlockBanning, 0);
/* 240 */     gridData = new GridData(1808);
/* 241 */     gridData.horizontalSpan = 2;
/* 242 */     gridData.horizontalIndent = 15;
/* 243 */     Utils.setLayoutData(cIndent, gridData);
/* 244 */     layout = new GridLayout(3, false);
/* 245 */     layout.marginHeight = 0;
/* 246 */     layout.marginWidth = 0;
/* 247 */     cIndent.setLayout(layout);
/*     */     
/* 249 */     Image img = imageLoader.getImage("subitem");
/* 250 */     Label label = new Label(cIndent, 0);
/* 251 */     gridData = new GridData(2);
/* 252 */     Utils.setLayoutData(label, gridData);
/* 253 */     label.setImage(img);
/*     */     
/*     */ 
/* 256 */     Label discard_min_label = new Label(cIndent, 0);
/* 257 */     Messages.setLanguageText(discard_min_label, "ConfigView.section.ipfilter.discardminkb", new String[] { DisplayFormatters.getUnit(1) });
/*     */     
/*     */ 
/* 260 */     IntParameter discard_min = new IntParameter(cIndent, "Ip Filter Ban Discard Min KB");
/* 261 */     gridData = new GridData();
/* 262 */     discard_min.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 266 */     Label block_label = new Label(gBlockBanning, 0);
/* 267 */     Messages.setLanguageText(block_label, "ConfigView.section.ipfilter.blockbanning");
/*     */     
/*     */ 
/* 270 */     IntParameter block_banning = new IntParameter(gBlockBanning, "Ip Filter Ban Block Limit", 0, 256);
/*     */     
/* 272 */     gridData = new GridData();
/* 273 */     block_banning.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 277 */     enable_bad_data_banning.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Control[] { block_banning.getControl(), block_label, discard_ratio.getControl(), discard_label, discard_min.getControl(), discard_min_label }));
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
/* 292 */     Group gAutoLoad = new Group(gFilter, 0);
/* 293 */     Messages.setLanguageText(gAutoLoad, "ConfigView.section.ipfilter.autoload.group");
/* 294 */     FormLayout flayout = new FormLayout();
/* 295 */     flayout.marginHeight = (flayout.marginWidth = 5);
/* 296 */     gAutoLoad.setLayout(flayout);
/* 297 */     gridData = new GridData(256);
/* 298 */     gridData.widthHint = 500;
/* 299 */     Utils.setLayoutData(gAutoLoad, gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 304 */     String sCurConfigID = "Ip Filter Autoload File";
/*     */     
/* 306 */     Label lblDefaultDir = new Label(gAutoLoad, 0);
/* 307 */     Messages.setLanguageText(lblDefaultDir, "ConfigView.section.ipfilter.autoload.file");
/* 308 */     FormData fd = new FormData();
/* 309 */     Utils.setLayoutData(lblDefaultDir, fd);
/*     */     
/* 311 */     final StringParameter pathParameter = new StringParameter(gAutoLoad, sCurConfigID);
/*     */     
/* 313 */     Button browse = new Button(gAutoLoad, 8);
/* 314 */     browse.setImage(imgOpenFolder);
/* 315 */     imgOpenFolder.setBackground(browse.getBackground());
/*     */     
/* 317 */     browse.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 319 */         FileDialog dialog = new FileDialog(parent.getShell(), 65536);
/* 320 */         dialog.setFilterPath(pathParameter.getValue());
/* 321 */         dialog.setText(MessageText.getString("ConfigView.section.ipfilter.autoload.file"));
/* 322 */         dialog.setFilterExtensions(new String[] { "*.dat" + File.pathSeparator + "*.p2p" + File.pathSeparator + "*.p2b" + File.pathSeparator + "*.txt", "*.*" });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 327 */         dialog.setFileName("ipfilter.dat");
/* 328 */         String file = dialog.open();
/* 329 */         if (file != null) {
/* 330 */           pathParameter.setValue(file);
/*     */         }
/*     */         
/*     */       }
/* 334 */     });
/* 335 */     final Button btnLoadNow = new Button(gAutoLoad, 8);
/* 336 */     Messages.setLanguageText(btnLoadNow, "ConfigView.section.ipfilter.autoload.loadnow");
/* 337 */     btnLoadNow.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 339 */         btnLoadNow.setEnabled(false);
/* 340 */         COConfigurationManager.setParameter("Ip Filter Autoload Last Date", 0);
/*     */         
/* 342 */         Utils.getOffOfSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/*     */             try {
/* 345 */               ConfigSectionIPFilter.this.filter.reloadSync();
/*     */             } catch (Exception e) {
/* 347 */               e.printStackTrace();
/*     */             }
/* 349 */             Utils.execSWTThread(new AERunnable() {
/*     */               public void runSupport() {
/* 351 */                 if (!ConfigSectionIPFilter.3.this.val$btnLoadNow.isDisposed()) {
/* 352 */                   ConfigSectionIPFilter.3.this.val$btnLoadNow.setEnabled(true);
/*     */                 }
/*     */                 
/*     */               }
/*     */             });
/*     */           }
/*     */         });
/*     */       }
/* 360 */     });
/* 361 */     fd = new FormData();
/* 362 */     fd.right = new FormAttachment(100, 0);
/* 363 */     Utils.setLayoutData(btnLoadNow, fd);
/*     */     
/* 365 */     fd = new FormData();
/* 366 */     fd.right = new FormAttachment(btnLoadNow, -5);
/* 367 */     Utils.setLayoutData(browse, fd);
/*     */     
/* 369 */     fd = new FormData();
/* 370 */     fd.left = new FormAttachment(lblDefaultDir, 5);
/* 371 */     fd.right = new FormAttachment(browse, -5);
/* 372 */     pathParameter.setLayoutData(fd);
/*     */     
/* 374 */     Label lblAutoLoadInfo = new Label(gAutoLoad, 64);
/* 375 */     Messages.setLanguageText(lblAutoLoadInfo, "ConfigView.section.ipfilter.autoload.info");
/* 376 */     fd = new FormData();
/* 377 */     fd.top = new FormAttachment(btnLoadNow, 3);
/* 378 */     fd.left = new FormAttachment(0, 0);
/* 379 */     fd.right = new FormAttachment(100, 0);
/* 380 */     Utils.setLayoutData(lblAutoLoadInfo, fd);
/*     */     
/* 382 */     BooleanParameter clear_on_reload = new BooleanParameter(gAutoLoad, "Ip Filter Clear On Reload");
/* 383 */     fd = new FormData();
/* 384 */     fd.top = new FormAttachment(lblAutoLoadInfo, 3);
/* 385 */     fd.left = new FormAttachment(0, 0);
/* 386 */     fd.right = new FormAttachment(100, 0);
/* 387 */     clear_on_reload.setLayoutData(fd);
/* 388 */     Messages.setLanguageText(clear_on_reload.getControl(), "ConfigView.section.ipfilter.clear.on.reload");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 393 */     if (userMode > 0) {
/* 394 */       gridData = new GridData();
/* 395 */       BooleanParameter enableDesc = new BooleanParameter(gFilter, "Ip Filter Enable Description Cache");
/*     */       
/* 397 */       enableDesc.setLayoutData(gridData);
/* 398 */       Messages.setLanguageText(enableDesc.getControl(), "ConfigView.section.ipfilter.enable.descriptionCache");
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 405 */     this.table = new Table(gFilter, 268503044);
/* 406 */     String[] headers = { "ConfigView.section.ipfilter.description", "ConfigView.section.ipfilter.start", "ConfigView.section.ipfilter.end" };
/* 407 */     int[] sizes = { 110, 110, 110 };
/* 408 */     int[] aligns = { 16384, 16777216, 16777216 };
/* 409 */     for (int i = 0; i < headers.length; i++) {
/* 410 */       TableColumn tc = new TableColumn(this.table, aligns[i]);
/* 411 */       tc.setText(headers[i]);
/* 412 */       tc.setWidth(Utils.adjustPXForDPI(sizes[i]));
/* 413 */       Messages.setLanguageText(tc, headers[i]);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 418 */     TableColumn[] columns = this.table.getColumns();
/* 419 */     columns[0].setData(new Integer(0));
/* 420 */     columns[1].setData(new Integer(1));
/* 421 */     columns[2].setData(new Integer(2));
/*     */     
/* 423 */     Listener listener = new Listener() {
/*     */       public void handleEvent(Event e) {
/* 425 */         TableColumn tc = (TableColumn)e.widget;
/* 426 */         int field = ((Integer)tc.getData()).intValue();
/* 427 */         ConfigSectionIPFilter.this.comparator.setField(field);
/*     */         
/* 429 */         if ((field == 0) && (!ConfigSectionIPFilter.this.bIsCachingDescriptions)) {
/* 430 */           ipFilterManager.cacheAllDescriptions();
/* 431 */           ConfigSectionIPFilter.this.bIsCachingDescriptions = true;
/*     */         }
/* 433 */         ConfigSectionIPFilter.this.ipRanges = ConfigSectionIPFilter.this.getSortedRanges(ConfigSectionIPFilter.this.filter.getRanges());
/* 434 */         ConfigSectionIPFilter.this.table.setItemCount(ConfigSectionIPFilter.this.ipRanges.length);
/* 435 */         ConfigSectionIPFilter.this.table.clearAll();
/*     */         
/* 437 */         ConfigSectionIPFilter.this.table.redraw();
/*     */       }
/*     */       
/* 440 */     };
/* 441 */     columns[0].addListener(13, listener);
/* 442 */     columns[1].addListener(13, listener);
/* 443 */     columns[2].addListener(13, listener);
/*     */     
/* 445 */     this.table.setHeaderVisible(true);
/*     */     
/* 447 */     gridData = new GridData(1808);
/* 448 */     gridData.heightHint = (this.table.getHeaderHeight() * 3);
/* 449 */     gridData.widthHint = 200;
/* 450 */     Utils.setLayoutData(this.table, gridData);
/*     */     
/* 452 */     Composite cArea = new Composite(gFilter, 0);
/* 453 */     layout = new GridLayout();
/* 454 */     layout.marginHeight = 0;
/* 455 */     layout.marginWidth = 0;
/* 456 */     layout.numColumns = 4;
/* 457 */     cArea.setLayout(layout);
/* 458 */     gridData = new GridData(768);
/* 459 */     Utils.setLayoutData(cArea, gridData);
/*     */     
/* 461 */     Button add = new Button(cArea, 8);
/* 462 */     gridData = new GridData(2);
/* 463 */     gridData.widthHint = 100;
/* 464 */     Utils.setLayoutData(add, gridData);
/* 465 */     Messages.setLanguageText(add, "ConfigView.section.ipfilter.add");
/* 466 */     add.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 468 */         ConfigSectionIPFilter.this.addRange();
/*     */       }
/*     */       
/* 471 */     });
/* 472 */     Button remove = new Button(cArea, 8);
/* 473 */     gridData = new GridData(2);
/* 474 */     gridData.widthHint = 100;
/* 475 */     Utils.setLayoutData(remove, gridData);
/* 476 */     Messages.setLanguageText(remove, "ConfigView.section.ipfilter.remove");
/* 477 */     remove.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 479 */         TableItem[] selection = ConfigSectionIPFilter.this.table.getSelection();
/* 480 */         if (selection.length == 0)
/* 481 */           return;
/* 482 */         ConfigSectionIPFilter.this.removeRange((IpRange)selection[0].getData());
/* 483 */         ConfigSectionIPFilter.this.ipRanges = ConfigSectionIPFilter.this.getSortedRanges(ConfigSectionIPFilter.this.filter.getRanges());
/* 484 */         ConfigSectionIPFilter.this.table.setItemCount(ConfigSectionIPFilter.this.ipRanges.length);
/* 485 */         ConfigSectionIPFilter.this.table.clearAll();
/* 486 */         ConfigSectionIPFilter.this.table.redraw();
/*     */       }
/*     */       
/* 489 */     });
/* 490 */     Button edit = new Button(cArea, 8);
/* 491 */     gridData = new GridData(2);
/* 492 */     gridData.widthHint = 100;
/* 493 */     Utils.setLayoutData(edit, gridData);
/* 494 */     Messages.setLanguageText(edit, "ConfigView.section.ipfilter.edit");
/* 495 */     edit.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 497 */         TableItem[] selection = ConfigSectionIPFilter.this.table.getSelection();
/* 498 */         if (selection.length == 0)
/* 499 */           return;
/* 500 */         ConfigSectionIPFilter.this.editRange((IpRange)selection[0].getData());
/*     */       }
/*     */       
/* 503 */     });
/* 504 */     this.percentage_blocked = new Label(cArea, 131136);
/* 505 */     gridData = new GridData(784);
/* 506 */     Utils.setLayoutData(this.percentage_blocked, gridData);
/* 507 */     Utils.setLayoutData(this.percentage_blocked, Utils.getWrappableLabelGridData(1, 256));
/* 508 */     setPercentageBlocked();
/*     */     
/*     */ 
/*     */ 
/* 512 */     this.table.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDoubleClick(MouseEvent arg0) {
/* 514 */         TableItem[] selection = ConfigSectionIPFilter.this.table.getSelection();
/* 515 */         if (selection.length == 0)
/* 516 */           return;
/* 517 */         ConfigSectionIPFilter.this.editRange((IpRange)selection[0].getData());
/*     */       }
/*     */       
/* 520 */     });
/* 521 */     Control[] controls = new Control[3];
/* 522 */     controls[0] = add;
/* 523 */     controls[1] = remove;
/* 524 */     controls[2] = edit;
/* 525 */     IAdditionalActionPerformer enabler = new ChangeSelectionActionPerformer(controls);
/* 526 */     enabled.setAdditionalActionPerformer(enabler);
/*     */     
/* 528 */     this.ipRanges = getSortedRanges(this.filter.getRanges());
/*     */     
/* 530 */     this.table.addListener(36, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 532 */         TableItem item = (TableItem)event.item;
/* 533 */         int index = ConfigSectionIPFilter.this.table.indexOf(item);
/*     */         
/*     */ 
/*     */ 
/* 537 */         if ((index < 0) || (index >= ConfigSectionIPFilter.this.ipRanges.length)) {
/* 538 */           return;
/*     */         }
/* 540 */         IpRange range = ConfigSectionIPFilter.this.ipRanges[index];
/* 541 */         item.setText(0, range.getDescription());
/* 542 */         item.setText(1, range.getStartIp());
/* 543 */         item.setText(2, range.getEndIp());
/* 544 */         item.setData(range);
/*     */       }
/*     */       
/* 547 */     });
/* 548 */     this.table.setItemCount(this.ipRanges.length);
/* 549 */     this.table.clearAll();
/*     */     
/* 551 */     this.table.redraw();
/*     */     
/* 553 */     this.table.addListener(11, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 555 */         ConfigSectionIPFilter.this.resizeTable();
/*     */       }
/*     */       
/* 558 */     });
/* 559 */     gFilter.addListener(11, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 561 */         ConfigSectionIPFilter.this.resizeTable();
/*     */       }
/*     */       
/*     */ 
/* 565 */     });
/* 566 */     this.filterListener = new IPFilterListener()
/*     */     {
/*     */       public void IPFilterEnabledChanged(boolean is_enabled) {}
/*     */       
/*     */ 
/*     */ 
/*     */       public boolean canIPBeBanned(String ip)
/*     */       {
/* 574 */         return true;
/*     */       }
/*     */       
/*     */       public void IPBanned(BannedIp ip) {}
/*     */       
/*     */       public void IPBlockedListChanged(final IpFilter filter)
/*     */       {
/* 581 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 583 */             if (ConfigSectionIPFilter.this.table.isDisposed()) {
/* 584 */               filter.removeListener(ConfigSectionIPFilter.this.filterListener);
/* 585 */               return;
/*     */             }
/* 587 */             ConfigSectionIPFilter.this.ipRanges = ConfigSectionIPFilter.this.getSortedRanges(filter.getRanges());
/* 588 */             ConfigSectionIPFilter.this.table.setItemCount(ConfigSectionIPFilter.this.ipRanges.length);
/* 589 */             ConfigSectionIPFilter.this.table.clearAll();
/* 590 */             ConfigSectionIPFilter.this.table.redraw();
/*     */           }
/*     */         });
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */       public boolean canIPBeBlocked(String ip, byte[] torrent_hash)
/*     */       {
/* 600 */         return true;
/*     */       }
/* 602 */     };
/* 603 */     this.filter.addListener(this.filterListener);
/*     */     
/* 605 */     return gFilter;
/*     */   }
/*     */   
/*     */   private void resizeTable() {
/* 609 */     int iNewWidth = this.table.getClientArea().width - this.table.getColumn(1).getWidth() - this.table.getColumn(2).getWidth() - 20;
/*     */     
/*     */ 
/* 612 */     if (iNewWidth > 50) {
/* 613 */       this.table.getColumn(0).setWidth(iNewWidth);
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeRange(IpRange range)
/*     */   {
/* 619 */     this.filter.removeRange(range);
/*     */   }
/*     */   
/*     */ 
/*     */   public void editRange(IpRange range)
/*     */   {
/* 625 */     new IpFilterEditor(AzureusCoreFactory.getSingleton(), this.table.getShell(), range);
/* 626 */     this.noChange = false;
/*     */   }
/*     */   
/*     */   public void addRange()
/*     */   {
/* 631 */     new IpFilterEditor(AzureusCoreFactory.getSingleton(), this.table.getShell(), null);
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh()
/*     */   {
/* 637 */     if ((this.table == null) || (this.table.isDisposed()) || (this.noChange))
/* 638 */       return;
/* 639 */     this.noChange = true;
/* 640 */     TableItem[] items = this.table.getItems();
/* 641 */     for (int i = 0; i < items.length; i++) {
/* 642 */       if ((items[i] != null) && (!items[i].isDisposed()))
/*     */       {
/* 644 */         String tmp = items[i].getText(0);
/* 645 */         IpRange range = (IpRange)items[i].getData();
/*     */         
/* 647 */         String desc = range.getDescription();
/*     */         
/* 649 */         if ((desc != null) && (!desc.equals(tmp))) {
/* 650 */           items[i].setText(0, desc);
/*     */         }
/* 652 */         tmp = items[i].getText(1);
/* 653 */         if ((range.getStartIp() != null) && (!range.getStartIp().equals(tmp))) {
/* 654 */           items[i].setText(1, range.getStartIp());
/*     */         }
/* 656 */         tmp = items[i].getText(2);
/* 657 */         if ((range.getEndIp() != null) && (!range.getEndIp().equals(tmp))) {
/* 658 */           items[i].setText(2, range.getEndIp());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected IpRange[] getSortedRanges(IpRange[] ranges)
/*     */   {
/* 667 */     Arrays.sort(ranges, this.comparator);
/*     */     
/*     */ 
/*     */ 
/* 671 */     return ranges;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setPercentageBlocked()
/*     */   {
/* 678 */     long nbIPsBlocked = this.filter.getTotalAddressesInRange();
/*     */     
/* 680 */     if (COConfigurationManager.getBooleanParameter("Ip Filter Allow"))
/*     */     {
/* 682 */       nbIPsBlocked = 4294967296L - nbIPsBlocked;
/*     */     }
/*     */     
/* 685 */     int percentIPsBlocked = (int)(nbIPsBlocked * 1000L / 4294967296L);
/*     */     
/* 687 */     String nbIps = "" + nbIPsBlocked;
/* 688 */     String percentIps = DisplayFormatters.formatPercentFromThousands(percentIPsBlocked);
/*     */     
/* 690 */     Messages.setLanguageText(this.percentage_blocked, "ConfigView.section.ipfilter.totalIPs", new String[] { nbIps, percentIps });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionIPFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */