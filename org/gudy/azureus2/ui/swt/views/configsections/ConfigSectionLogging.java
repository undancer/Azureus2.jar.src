/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.core.networkmanager.admin.NetworkAdmin;
/*     */ import com.aelitis.azureus.core.stats.AzureusCoreStats;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.DirectoryDialog;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.List;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.logging.impl.FileLogging;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntListParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
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
/*     */ public class ConfigSectionLogging
/*     */   implements UISWTConfigSection
/*     */ {
/*  66 */   private static final LogIDs LOGID = LogIDs.GUI;
/*     */   private static final String CFG_PREFIX = "ConfigView.section.logging.";
/*  68 */   private static final int[] logFileSizes = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25, 30, 40, 50, 75, 100, 200, 300, 500 };
/*     */   
/*     */ 
/*     */ 
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  74 */     return "root";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  78 */     return "logging";
/*     */   }
/*     */   
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete()
/*     */   {
/*  85 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  86 */     imageLoader.releaseImage("openFolderButton");
/*     */   }
/*     */   
/*     */   public int maxUserMode() {
/*  90 */     return 2;
/*     */   }
/*     */   
/*     */ 
/*     */   public Composite configSectionCreate(final Composite parent)
/*     */   {
/*  96 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  97 */     Image imgOpenFolder = imageLoader.getImage("openFolderButton");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 102 */     Composite gLogging = new Composite(parent, 0);
/* 103 */     GridData gridData = new GridData(272);
/* 104 */     Utils.setLayoutData(gLogging, gridData);
/* 105 */     GridLayout layout = new GridLayout();
/* 106 */     layout.numColumns = 2;
/* 107 */     gLogging.setLayout(layout);
/*     */     
/* 109 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*     */     
/*     */ 
/* 112 */     BooleanParameter enable_logger = new BooleanParameter(gLogging, "Logger.Enabled", "ConfigView.section.logging.loggerenable");
/* 113 */     gridData = new GridData();
/* 114 */     gridData.horizontalSpan = 2;
/* 115 */     enable_logger.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 119 */     final BooleanParameter enableLogging = new BooleanParameter(gLogging, "Logging Enable", "ConfigView.section.logging.enable");
/*     */     
/*     */ 
/*     */ 
/* 123 */     gridData = new GridData();
/* 124 */     gridData.horizontalSpan = 2;
/* 125 */     enableLogging.setLayoutData(gridData);
/*     */     
/* 127 */     Composite cArea = new Composite(gLogging, 0);
/* 128 */     layout = new GridLayout();
/* 129 */     layout.marginHeight = 0;
/* 130 */     layout.marginWidth = 0;
/* 131 */     layout.numColumns = 3;
/* 132 */     cArea.setLayout(layout);
/* 133 */     gridData = new GridData(768);
/* 134 */     gridData.horizontalSpan = 2;
/* 135 */     cArea.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 140 */     Label lStatsPath = new Label(cArea, 0);
/* 141 */     Messages.setLanguageText(lStatsPath, "ConfigView.section.logging.logdir");
/*     */     
/* 143 */     gridData = new GridData();
/* 144 */     gridData.widthHint = 150;
/* 145 */     final StringParameter pathParameter = new StringParameter(cArea, "Logging Dir");
/* 146 */     pathParameter.setLayoutData(gridData);
/* 147 */     Button browse = new Button(cArea, 8);
/* 148 */     browse.setImage(imgOpenFolder);
/* 149 */     imgOpenFolder.setBackground(browse.getBackground());
/* 150 */     browse.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/* 151 */     browse.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/* 156 */         DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), 65536);
/* 157 */         dialog.setFilterPath(pathParameter.getValue());
/* 158 */         dialog.setText(MessageText.getString("ConfigView.section.logging.choosedefaultsavepath"));
/* 159 */         String path = dialog.open();
/* 160 */         if (path != null) {
/* 161 */           pathParameter.setValue(path);
/*     */         }
/*     */         
/*     */       }
/* 165 */     });
/* 166 */     Label lMaxLog = new Label(cArea, 0);
/*     */     
/* 168 */     Messages.setLanguageText(lMaxLog, "ConfigView.section.logging.maxsize");
/* 169 */     String[] lmLabels = new String[logFileSizes.length];
/* 170 */     int[] lmValues = new int[logFileSizes.length];
/* 171 */     for (int i = 0; i < logFileSizes.length; i++) {
/* 172 */       int num = logFileSizes[i];
/* 173 */       lmLabels[i] = (" " + num + " MB");
/* 174 */       lmValues[i] = num;
/*     */     }
/*     */     
/* 177 */     IntListParameter paramMaxSize = new IntListParameter(cArea, "Logging Max Size", lmLabels, lmValues);
/* 178 */     gridData = new GridData();
/* 179 */     gridData.horizontalSpan = 2;
/* 180 */     paramMaxSize.setLayoutData(gridData);
/*     */     
/* 182 */     if (userMode > 1)
/*     */     {
/* 184 */       Label timeStampLbl = new Label(cArea, 0);
/* 185 */       Messages.setLanguageText(timeStampLbl, "ConfigView.section.logging.timestamp");
/* 186 */       Utils.setLayoutData(timeStampLbl, new GridData());
/* 187 */       StringParameter timeStamp = new StringParameter(cArea, "Logging Timestamp");
/* 188 */       gridData = new GridData();
/* 189 */       gridData.horizontalSpan = 2;
/* 190 */       gridData.widthHint = 150;
/* 191 */       timeStamp.setLayoutData(gridData);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 199 */     String sFilterPrefix = "ConfigView.section.logging.filter";
/* 200 */     Group gLogIDs = new Group(gLogging, 0);
/* 201 */     Messages.setLanguageText(gLogIDs, "ConfigView.section.logging.filter");
/* 202 */     layout = new GridLayout();
/* 203 */     layout.numColumns = 2;
/* 204 */     gLogIDs.setLayout(layout);
/* 205 */     gridData = new GridData(1, 1, true, true);
/* 206 */     gridData.horizontalSpan = 2;
/* 207 */     Utils.setLayoutData(gLogIDs, gridData);
/*     */     
/* 209 */     final List listLogTypes = new List(gLogIDs, 2564);
/*     */     
/* 211 */     gridData = new GridData(0, 1, false, false);
/* 212 */     listLogTypes.setLayoutData(gridData);
/*     */     
/* 214 */     final int[] logTypes = { 0, 1, 3 };
/*     */     
/* 216 */     for (int i = 0; i < logTypes.length; i++)
/* 217 */       listLogTypes.add(MessageText.getString("ConfigView.section.logging.log" + i + "type"));
/* 218 */     listLogTypes.select(0);
/*     */     
/* 220 */     LogIDs[] logIDs = FileLogging.configurableLOGIDs;
/*     */     
/* 222 */     final Table tableLogIDs = new Table(gLogIDs, 67620);
/*     */     
/* 224 */     gridData = new GridData(1808);
/* 225 */     tableLogIDs.setLayoutData(gridData);
/* 226 */     tableLogIDs.setLinesVisible(false);
/* 227 */     tableLogIDs.setHeaderVisible(false);
/* 228 */     TableColumn column = new TableColumn(tableLogIDs, 0);
/*     */     
/* 230 */     for (int i = 0; i < logIDs.length; i++) {
/* 231 */       TableItem item = new TableItem(tableLogIDs, 0);
/* 232 */       item.setText(0, MessageText.getString("ConfigView.section.logging.filter." + logIDs[i], logIDs[i].toString()));
/*     */       
/* 234 */       item.setData(logIDs[i]);
/* 235 */       boolean checked = COConfigurationManager.getBooleanParameter("bLog." + logTypes[0] + "." + logIDs[i], true);
/*     */       
/* 237 */       item.setChecked(checked);
/*     */     }
/* 239 */     column.pack();
/*     */     
/*     */ 
/* 242 */     listLogTypes.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 244 */         int index = listLogTypes.getSelectionIndex();
/* 245 */         if ((index < 0) || (index >= logTypes.length))
/* 246 */           return;
/* 247 */         TableItem[] items = tableLogIDs.getItems();
/* 248 */         for (int i = 0; i < items.length; i++) {
/* 249 */           boolean checked = COConfigurationManager.getBooleanParameter("bLog." + logTypes[index] + "." + items[i].getData(), true);
/*     */           
/*     */ 
/* 252 */           items[i].setChecked(checked);
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 258 */     });
/* 259 */     tableLogIDs.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 261 */         if (e.detail != 32)
/* 262 */           return;
/* 263 */         int index = listLogTypes.getSelectionIndex();
/* 264 */         if ((index < 0) || (index >= logTypes.length))
/* 265 */           return;
/* 266 */         TableItem item = (TableItem)e.item;
/* 267 */         COConfigurationManager.setParameter("bLog." + logTypes[index] + "." + item.getData(), item.getChecked());
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 272 */     });
/* 273 */     Control[] controls_main = { cArea, gLogIDs };
/* 274 */     ChangeSelectionActionPerformer perf2 = new ChangeSelectionActionPerformer(controls_main);
/*     */     
/* 276 */     enableLogging.setAdditionalActionPerformer(perf2);
/*     */     
/* 278 */     enable_logger.setAdditionalActionPerformer(new IAdditionalActionPerformer()
/*     */     {
/* 280 */       ChangeSelectionActionPerformer p1 = new ChangeSelectionActionPerformer(new Control[] { enableLogging.getControl() });
/*     */       
/*     */ 
/* 283 */       public void performAction() { this.p1.performAction(); }
/*     */       
/*     */       public void setSelected(boolean selected) {
/* 286 */         this.p1.setSelected(selected);
/* 287 */         if ((!selected) && (enableLogging.isSelected().booleanValue())) { enableLogging.setSelected(false);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void setIntValue(int value) {}
/*     */       
/*     */ 
/*     */ 
/*     */       public void setStringValue(String value) {}
/*     */     });
/*     */     
/*     */ 
/* 301 */     if (userMode > 1)
/*     */     {
/*     */ 
/*     */ 
/* 305 */       Group cAO = new Group(gLogging, 0);
/* 306 */       cAO.setText(MessageText.getString("dht.advanced.group"));
/* 307 */       layout = new GridLayout();
/* 308 */       layout.marginHeight = 0;
/* 309 */       layout.marginWidth = 0;
/* 310 */       layout.numColumns = 5;
/* 311 */       cAO.setLayout(layout);
/* 312 */       gridData = new GridData(768);
/* 313 */       gridData.horizontalSpan = 2;
/* 314 */       cAO.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 318 */       Label aoName = new Label(cAO, 0);
/* 319 */       Messages.setLanguageText(aoName, "TableColumn.header.name");
/* 320 */       aoName.setLayoutData(new GridData());
/* 321 */       final StringParameter name = new StringParameter(cAO, "Advanced Option Name");
/* 322 */       gridData = new GridData();
/* 323 */       gridData.widthHint = 150;
/* 324 */       name.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 328 */       Label aoValue = new Label(cAO, 0);
/* 329 */       Messages.setLanguageText(aoValue, "ConfigView.label.seeding.ignore.header.value");
/* 330 */       aoName.setLayoutData(new GridData());
/* 331 */       final StringParameter value = new StringParameter(cAO, "Advanced Option Value");
/* 332 */       gridData = new GridData();
/* 333 */       gridData.widthHint = 150;
/* 334 */       value.setLayoutData(gridData);
/*     */       
/*     */ 
/*     */ 
/* 338 */       Button set_option = new Button(cAO, 8);
/* 339 */       Messages.setLanguageText(set_option, "Button.set");
/*     */       
/* 341 */       set_option.addListener(13, new Listener()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void handleEvent(Event event)
/*     */         {
/*     */ 
/* 348 */           String key = name.getValue().trim();
/*     */           
/* 350 */           if (((key.startsWith("'")) && (key.endsWith("'"))) || ((key.startsWith("\"")) && (key.endsWith("\""))))
/*     */           {
/*     */ 
/* 353 */             key = key.substring(1, key.length() - 1);
/*     */           }
/*     */           
/* 356 */           if (key.length() > 0)
/*     */           {
/* 358 */             if (key.startsWith("!")) {
/* 359 */               key = key.substring(1);
/*     */             } else {
/* 361 */               key = "adv.setting." + key;
/*     */             }
/*     */             
/* 364 */             String val = value.getValue().trim();
/*     */             
/* 366 */             boolean is_string = false;
/*     */             
/* 368 */             if (((val.startsWith("'")) && (val.endsWith("'"))) || ((val.startsWith("\"")) && (val.endsWith("\""))))
/*     */             {
/*     */ 
/* 371 */               val = val.substring(1, val.length() - 1);
/*     */               
/* 373 */               is_string = true;
/*     */             }
/*     */             
/* 376 */             if (val.length() == 0)
/*     */             {
/* 378 */               COConfigurationManager.removeParameter(key);
/*     */ 
/*     */ 
/*     */             }
/* 382 */             else if (is_string) {
/* 383 */               COConfigurationManager.setParameter(key, val);
/*     */             } else {
/* 385 */               String lc_val = val.toLowerCase(Locale.US);
/*     */               
/* 387 */               if ((lc_val.equals("false")) || (lc_val.equals("true"))) {
/* 388 */                 COConfigurationManager.setParameter(key, lc_val.startsWith("t"));
/*     */               } else {
/*     */                 try {
/* 391 */                   long l = Long.parseLong(val);
/* 392 */                   COConfigurationManager.setParameter(key, l);
/*     */                 } catch (Throwable e) {
/* 394 */                   COConfigurationManager.setParameter(key, val);
/*     */                 }
/*     */               }
/*     */             }
/*     */             
/*     */ 
/* 400 */             COConfigurationManager.save();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 408 */     Label generate_net_info = new Label(gLogging, 0);
/* 409 */     Messages.setLanguageText(generate_net_info, "ConfigView.section.logging.netinfo");
/*     */     
/* 411 */     Button generate_net_button = new Button(gLogging, 8);
/* 412 */     Messages.setLanguageText(generate_net_button, "ConfigView.section.logging.generatediagnostics");
/*     */     
/* 414 */     generate_net_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 421 */         new AEThread2("GenerateNetDiag", true)
/*     */         {
/*     */           public void run() {
/* 424 */             StringWriter sw = new StringWriter();
/*     */             
/* 426 */             PrintWriter pw = new PrintWriter(sw);
/*     */             
/* 428 */             IndentWriter iw = new IndentWriter(pw);
/*     */             
/* 430 */             NetworkAdmin admin = NetworkAdmin.getSingleton();
/*     */             
/* 432 */             admin.generateDiagnostics(iw);
/*     */             
/* 434 */             pw.close();
/*     */             
/* 436 */             final String info = sw.toString();
/*     */             
/* 438 */             Logger.log(new LogEvent(ConfigSectionLogging.LOGID, "Network Info:\n" + info));
/*     */             
/* 440 */             Utils.execSWTThread(new Runnable()
/*     */             {
/*     */ 
/*     */               public void run()
/*     */               {
/*     */ 
/* 446 */                 ClipboardCopy.copyToClipBoard(info);
/*     */               }
/*     */               
/*     */ 
/*     */             });
/*     */           }
/*     */           
/*     */         }.start();
/*     */       }
/* 455 */     });
/* 456 */     Label generate_stats_info = new Label(gLogging, 0);
/* 457 */     Messages.setLanguageText(generate_stats_info, "ConfigView.section.logging.statsinfo");
/*     */     
/* 459 */     Button generate_stats_button = new Button(gLogging, 8);
/* 460 */     Messages.setLanguageText(generate_stats_button, "ConfigView.section.logging.generatediagnostics");
/*     */     
/*     */ 
/* 463 */     generate_stats_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 470 */         Set types = new HashSet();
/*     */         
/* 472 */         types.add(".*");
/*     */         
/* 474 */         Map reply = AzureusCoreStats.getStats(types);
/*     */         
/* 476 */         Iterator it = reply.entrySet().iterator();
/*     */         
/* 478 */         StringBuilder buffer = new StringBuilder(16000);
/*     */         
/* 480 */         while (it.hasNext())
/*     */         {
/* 482 */           Map.Entry entry = (Map.Entry)it.next();
/*     */           
/* 484 */           buffer.append(entry.getKey()).append(" -> ").append(entry.getValue()).append("\r\n");
/*     */         }
/*     */         
/* 487 */         String str = buffer.toString();
/*     */         
/* 489 */         ClipboardCopy.copyToClipBoard(str);
/*     */         
/* 491 */         Logger.log(new LogEvent(ConfigSectionLogging.LOGID, "Stats Info:\n" + str));
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 497 */     });
/* 498 */     Label generate_info = new Label(gLogging, 0);
/*     */     
/* 500 */     Messages.setLanguageText(generate_info, "ConfigView.section.logging.generatediagnostics.info");
/*     */     
/* 502 */     Button generate_button = new Button(gLogging, 8);
/*     */     
/* 504 */     Messages.setLanguageText(generate_button, "ConfigView.section.logging.generatediagnostics");
/*     */     
/* 506 */     generate_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 513 */         StringWriter sw = new StringWriter();
/*     */         
/* 515 */         PrintWriter pw = new PrintWriter(sw);
/*     */         
/* 517 */         AEDiagnostics.generateEvidence(pw);
/*     */         
/* 519 */         pw.close();
/*     */         
/* 521 */         String evidence = sw.toString();
/*     */         
/* 523 */         ClipboardCopy.copyToClipBoard(evidence);
/*     */         
/* 525 */         Logger.log(new LogEvent(ConfigSectionLogging.LOGID, "Evidence Generation:\n" + evidence));
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
/* 583 */     });
/* 584 */     return gLogging;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionLogging.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */