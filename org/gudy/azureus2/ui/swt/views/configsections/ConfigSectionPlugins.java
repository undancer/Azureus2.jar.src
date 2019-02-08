/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.ui.UIFunctions;
/*     */ import com.aelitis.azureus.ui.UIFunctionsManager;
/*     */ import com.aelitis.azureus.ui.mdi.MultipleDocumentInterface;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Table;
/*     */ import org.eclipse.swt.widgets.TableColumn;
/*     */ import org.eclipse.swt.widgets.TableItem;
/*     */ import org.eclipse.swt.widgets.TreeItem;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AESemaphore;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.plugins.PluginException;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.PluginState;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstallationListener;
/*     */ import org.gudy.azureus2.plugins.installer.PluginInstaller;
/*     */ import org.gudy.azureus2.plugins.ui.UIManager;
/*     */ import org.gudy.azureus2.plugins.ui.config.Parameter;
/*     */ import org.gudy.azureus2.plugins.ui.model.BasicPluginConfigModel;
/*     */ import org.gudy.azureus2.plugins.ui.model.PluginConfigModel;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInterfaceImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.BooleanParameterImpl;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.config.ParameterRepository;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.DualChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IAdditionalActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.plugins.PluginParameter;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
/*     */ import org.gudy.azureus2.ui.swt.pluginsinstaller.InstallPluginWizard;
/*     */ import org.gudy.azureus2.ui.swt.views.ConfigView;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConfigSectionPlugins
/*     */   implements UISWTConfigSection, ParameterListener
/*     */ {
/*     */   private static final String HEADER_PREFIX = "ConfigView.pluginlist.column.";
/*  81 */   private static final String[] COLUMN_HEADERS = { "loadAtStartup", "type", "name", "version", "directory", "unloadable" };
/*     */   
/*     */ 
/*  84 */   private static final int[] COLUMN_SIZES = { 180, 70, 250, 100, 100, 50 };
/*     */   
/*  86 */   private static final int[] COLUMN_ALIGNS = { 16777216, 16384, 16384, 131072, 16384, 16777216 };
/*     */   
/*     */   private ConfigView configView;
/*     */   
/*     */   FilterComparator comparator;
/*     */   
/*     */   List pluginIFs;
/*     */   
/*     */   private Table table;
/*     */   
/*     */   private Image imgRedLed;
/*     */   private Image imgGreenLed;
/*     */   
/*     */   static class FilterComparator
/*     */     implements Comparator
/*     */   {
/* 102 */     boolean ascending = true;
/*     */     
/*     */     static final int FIELD_LOAD = 0;
/*     */     
/*     */     static final int FIELD_TYPE = 1;
/*     */     
/*     */     static final int FIELD_NAME = 2;
/*     */     
/*     */     static final int FIELD_VERSION = 3;
/*     */     
/*     */     static final int FIELD_DIRECTORY = 4;
/*     */     
/*     */     static final int FIELD_UNLOADABLE = 5;
/*     */     
/* 116 */     int field = 2;
/*     */     
/*     */     String sUserPluginDir;
/*     */     String sAppPluginDir;
/*     */     
/*     */     public FilterComparator()
/*     */     {
/* 123 */       String sep = System.getProperty("file.separator");
/*     */       
/* 125 */       this.sUserPluginDir = FileUtil.getUserFile("plugins").toString();
/* 126 */       if (!this.sUserPluginDir.endsWith(sep)) {
/* 127 */         this.sUserPluginDir += sep;
/*     */       }
/* 129 */       this.sAppPluginDir = FileUtil.getApplicationFile("plugins").toString();
/* 130 */       if (!this.sAppPluginDir.endsWith(sep))
/* 131 */         this.sAppPluginDir += sep;
/*     */     }
/*     */     
/*     */     public int compare(Object arg0, Object arg1) {
/* 135 */       PluginInterface if0 = (PluginInterface)arg0;
/* 136 */       PluginInterfaceImpl if1 = (PluginInterfaceImpl)arg1;
/* 137 */       int result = 0;
/*     */       
/* 139 */       switch (this.field) {
/*     */       case 0: 
/* 141 */         boolean b0 = if0.getPluginState().isLoadedAtStartup();
/* 142 */         boolean b1 = if1.getPluginState().isLoadedAtStartup();
/* 143 */         result = b0 ? -1 : b0 == b1 ? 0 : 1;
/*     */         
/*     */ 
/* 146 */         if (result == 0) {
/* 147 */           result = if0.getPluginID().compareToIgnoreCase(if1.getPluginID());
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         break;
/*     */       case 1: 
/*     */       case 4: 
/* 155 */         result = getFieldValue(this.field, if0).compareToIgnoreCase(getFieldValue(this.field, if1));
/*     */         
/* 157 */         break;
/*     */       
/*     */ 
/*     */       case 3: 
/* 161 */         String s0 = if0.getPluginVersion();
/* 162 */         String s1 = if1.getPluginVersion();
/* 163 */         if (s0 == null)
/* 164 */           s0 = "";
/* 165 */         if (s1 == null)
/* 166 */           s1 = "";
/* 167 */         result = s0.compareToIgnoreCase(s1);
/* 168 */         break;
/*     */       
/*     */ 
/*     */       case 5: 
/* 172 */         boolean b0 = if0.getPluginState().isUnloadable();
/* 173 */         boolean b1 = if1.getPluginState().isUnloadable();
/* 174 */         result = b0 ? -1 : b0 == b1 ? 0 : 1;
/* 175 */         break;
/*     */       }
/*     */       
/*     */       
/* 179 */       if (result == 0) {
/* 180 */         result = if0.getPluginName().compareToIgnoreCase(if1.getPluginName());
/*     */       }
/* 182 */       if (!this.ascending) {
/* 183 */         result *= -1;
/*     */       }
/* 185 */       return result;
/*     */     }
/*     */     
/*     */     public boolean setField(int newField) {
/* 189 */       if (this.field == newField) {
/* 190 */         this.ascending = (!this.ascending);
/*     */       } else
/* 192 */         this.ascending = true;
/* 193 */       this.field = newField;
/* 194 */       return this.ascending;
/*     */     }
/*     */     
/*     */     public String getFieldValue(int iField, PluginInterface pluginIF) {
/* 198 */       switch (iField) {
/*     */       case 0: 
/* 200 */         return pluginIF.getPluginID();
/*     */       
/*     */ 
/*     */       case 4: 
/* 204 */         String sDirName = pluginIF.getPluginDirectoryName();
/*     */         
/* 206 */         if ((sDirName.length() > this.sUserPluginDir.length()) && (sDirName.substring(0, this.sUserPluginDir.length()).equals(this.sUserPluginDir)))
/*     */         {
/*     */ 
/* 209 */           return sDirName.substring(this.sUserPluginDir.length());
/*     */         }
/* 211 */         if ((sDirName.length() > this.sAppPluginDir.length()) && (sDirName.substring(0, this.sAppPluginDir.length()).equals(this.sAppPluginDir)))
/*     */         {
/*     */ 
/* 214 */           return sDirName.substring(this.sAppPluginDir.length());
/*     */         }
/* 216 */         return sDirName;
/*     */       
/*     */ 
/*     */       case 2: 
/* 220 */         return pluginIF.getPluginName();
/*     */       
/*     */ 
/*     */       case 1: 
/* 224 */         String sDirName = pluginIF.getPluginDirectoryName();
/*     */         String sKey;
/*     */         String sKey;
/* 227 */         if ((sDirName.length() > this.sUserPluginDir.length()) && (sDirName.substring(0, this.sUserPluginDir.length()).equals(this.sUserPluginDir)))
/*     */         {
/*     */ 
/* 230 */           sKey = "perUser";
/*     */         } else { String sKey;
/* 232 */           if ((sDirName.length() > this.sAppPluginDir.length()) && (sDirName.substring(0, this.sAppPluginDir.length()).equals(this.sAppPluginDir)))
/*     */           {
/*     */ 
/* 235 */             sKey = "shared";
/*     */           } else {
/* 237 */             sKey = "builtIn";
/*     */           }
/*     */         }
/* 240 */         return MessageText.getString("ConfigView.pluginlist.column.type." + sKey);
/*     */       
/*     */ 
/*     */       case 3: 
/* 244 */         return pluginIF.getPluginVersion();
/*     */       
/*     */ 
/*     */       case 5: 
/* 248 */         return MessageText.getString("Button." + (pluginIF.getPluginState().isUnloadable() ? "yes" : "no")).replaceAll("&", "");
/*     */       }
/*     */       
/*     */       
/*     */ 
/* 253 */       return "";
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ConfigSectionPlugins(ConfigView _configView)
/*     */   {
/* 262 */     this.configView = _configView;
/* 263 */     this.comparator = new FilterComparator();
/*     */   }
/*     */   
/*     */   public String configSectionGetParentSection() {
/* 267 */     return "root";
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String configSectionGetName()
/*     */   {
/* 274 */     return "plugins";
/*     */   }
/*     */   
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/* 281 */     return 0;
/*     */   }
/*     */   
/*     */   public void configSectionDelete()
/*     */   {
/* 286 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 287 */     imageLoader.releaseImage("redled");
/* 288 */     imageLoader.releaseImage("greenled");
/*     */   }
/*     */   
/*     */   public Composite configSectionCreate(Composite parent) {
/* 292 */     if (!AzureusCoreFactory.isCoreRunning()) {
/* 293 */       Composite cSection = new Composite(parent, 0);
/* 294 */       cSection.setLayout(new FillLayout());
/* 295 */       Label lblNotAvail = new Label(cSection, 64);
/* 296 */       Messages.setLanguageText(lblNotAvail, "core.not.available");
/* 297 */       return cSection;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 305 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 306 */     this.imgRedLed = imageLoader.getImage("redled");
/* 307 */     this.imgGreenLed = imageLoader.getImage("greenled");
/*     */     
/* 309 */     Composite infoGroup = new Composite(parent, 0);
/* 310 */     GridData gridData = new GridData(272);
/*     */     
/* 312 */     Utils.setLayoutData(infoGroup, gridData);
/* 313 */     GridLayout layout = new GridLayout();
/* 314 */     layout.numColumns = 1;
/* 315 */     layout.marginWidth = 0;
/* 316 */     layout.marginHeight = 0;
/* 317 */     infoGroup.setLayout(layout);
/*     */     
/* 319 */     infoGroup.setLayout(new GridLayout());
/*     */     
/* 321 */     String sep = System.getProperty("file.separator");
/*     */     
/* 323 */     File fUserPluginDir = FileUtil.getUserFile("plugins");
/*     */     
/*     */     String sUserPluginDir;
/*     */     try
/*     */     {
/* 328 */       sUserPluginDir = fUserPluginDir.getCanonicalPath();
/*     */     } catch (Throwable e) {
/* 330 */       sUserPluginDir = fUserPluginDir.toString();
/*     */     }
/*     */     
/* 333 */     if (!sUserPluginDir.endsWith(sep)) {
/* 334 */       sUserPluginDir = sUserPluginDir + sep;
/*     */     }
/*     */     
/* 337 */     File fAppPluginDir = FileUtil.getApplicationFile("plugins");
/*     */     
/*     */     String sAppPluginDir;
/*     */     try
/*     */     {
/* 342 */       sAppPluginDir = fAppPluginDir.getCanonicalPath();
/*     */     } catch (Throwable e) {
/* 344 */       sAppPluginDir = fAppPluginDir.toString();
/*     */     }
/*     */     
/* 347 */     if (!sAppPluginDir.endsWith(sep)) {
/* 348 */       sAppPluginDir = sAppPluginDir + sep;
/*     */     }
/*     */     
/* 351 */     Label label = new Label(infoGroup, 64);
/* 352 */     Utils.setLayoutData(label, new GridData(768));
/* 353 */     Messages.setLanguageText(label, "ConfigView.pluginlist.whereToPut");
/*     */     
/* 355 */     label = new Label(infoGroup, 64);
/* 356 */     gridData = new GridData(768);
/* 357 */     gridData.horizontalIndent = 10;
/* 358 */     Utils.setLayoutData(label, gridData);
/* 359 */     label.setText(sUserPluginDir.replaceAll("&", "&&"));
/* 360 */     label.setForeground(Colors.blue);
/* 361 */     label.setCursor(label.getDisplay().getSystemCursor(21));
/*     */     
/* 363 */     final String _sUserPluginDir = sUserPluginDir;
/*     */     
/* 365 */     label.addMouseListener(new MouseAdapter() {
/*     */       public void mouseUp(MouseEvent arg0) {
/* 367 */         if ((_sUserPluginDir.endsWith("/plugins/")) || (_sUserPluginDir.endsWith("\\plugins\\")))
/*     */         {
/* 369 */           File f = new File(_sUserPluginDir);
/* 370 */           String dir = _sUserPluginDir;
/* 371 */           if ((!f.exists()) || (!f.isDirectory())) {
/* 372 */             dir = _sUserPluginDir.substring(0, _sUserPluginDir.length() - 9);
/*     */           }
/*     */           
/* 375 */           Utils.launch(dir);
/*     */         }
/*     */         
/*     */       }
/* 379 */     });
/* 380 */     label = new Label(infoGroup, 64);
/* 381 */     Utils.setLayoutData(label, new GridData(768));
/* 382 */     Messages.setLanguageText(label, "ConfigView.pluginlist.whereToPutOr");
/*     */     
/* 384 */     label = new Label(infoGroup, 64);
/* 385 */     gridData = new GridData(768);
/* 386 */     gridData.horizontalIndent = 10;
/* 387 */     Utils.setLayoutData(label, gridData);
/* 388 */     label.setText(sAppPluginDir.replaceAll("&", "&&"));
/* 389 */     label.setForeground(Colors.blue);
/* 390 */     label.setCursor(label.getDisplay().getSystemCursor(21));
/*     */     
/* 392 */     final String _sAppPluginDir = sAppPluginDir;
/*     */     
/*     */ 
/* 395 */     label.addMouseListener(new MouseAdapter() {
/*     */       public void mouseUp(MouseEvent arg0) {
/* 397 */         if ((_sAppPluginDir.endsWith("/plugins/")) || (_sAppPluginDir.endsWith("\\plugins\\")))
/*     */         {
/* 399 */           File f = new File(_sAppPluginDir);
/* 400 */           if ((f.exists()) && (f.isDirectory())) {
/* 401 */             Utils.launch(_sAppPluginDir);
/*     */           } else {
/* 403 */             String azureusDir = _sAppPluginDir.substring(0, _sAppPluginDir.length() - 9);
/*     */             
/* 405 */             System.out.println(azureusDir);
/* 406 */             Utils.launch(azureusDir);
/*     */           }
/*     */           
/*     */         }
/*     */       }
/* 411 */     });
/* 412 */     this.pluginIFs = rebuildPluginIFs();
/*     */     
/* 414 */     Collections.sort(this.pluginIFs, new Comparator() {
/*     */       public int compare(Object o1, Object o2) {
/* 416 */         return ((PluginInterface)o1).getPluginName().compareToIgnoreCase(((PluginInterface)o2).getPluginName());
/*     */       }
/*     */       
/*     */ 
/* 420 */     });
/* 421 */     Label labelInfo = new Label(infoGroup, 64);
/* 422 */     Utils.setLayoutData(labelInfo, new GridData(768));
/* 423 */     Messages.setLanguageText(labelInfo, "ConfigView.pluginlist.info");
/*     */     
/* 425 */     this.table = new Table(infoGroup, 268503076);
/*     */     
/* 427 */     gridData = new GridData(1808);
/* 428 */     gridData.heightHint = 200;
/* 429 */     gridData.widthHint = 200;
/* 430 */     Utils.setLayoutData(this.table, gridData);
/* 431 */     for (int i = 0; i < COLUMN_HEADERS.length; i++) {
/* 432 */       final TableColumn tc = new TableColumn(this.table, COLUMN_ALIGNS[i]);
/* 433 */       tc.setWidth(Utils.adjustPXForDPI(COLUMN_SIZES[i]));
/* 434 */       tc.addSelectionListener(new SelectionAdapter() {
/*     */         public void widgetSelected(SelectionEvent e) {
/* 436 */           boolean ascending = ConfigSectionPlugins.this.comparator.setField(ConfigSectionPlugins.this.table.indexOf(tc));
/*     */           try {
/* 438 */             ConfigSectionPlugins.this.table.setSortColumn(tc);
/* 439 */             ConfigSectionPlugins.this.table.setSortDirection(ascending ? 128 : 1024);
/*     */           }
/*     */           catch (NoSuchMethodError ignore) {}
/*     */           
/* 443 */           Collections.sort(ConfigSectionPlugins.this.pluginIFs, ConfigSectionPlugins.this.comparator);
/* 444 */           ConfigSectionPlugins.this.table.clearAll();
/*     */         }
/* 446 */       });
/* 447 */       Messages.setLanguageText(tc, "ConfigView.pluginlist.column." + COLUMN_HEADERS[i]);
/*     */     }
/* 449 */     this.table.setHeaderVisible(true);
/*     */     
/* 451 */     Composite cButtons = new Composite(infoGroup, 0);
/* 452 */     layout = new GridLayout();
/* 453 */     layout.marginHeight = 0;
/* 454 */     layout.marginWidth = 0;
/* 455 */     layout.numColumns = 5;
/* 456 */     cButtons.setLayout(layout);
/* 457 */     Utils.setLayoutData(cButtons, new GridData());
/*     */     
/* 459 */     final Button btnUnload = new Button(cButtons, 8);
/* 460 */     Utils.setLayoutData(btnUnload, new GridData());
/* 461 */     Messages.setLanguageText(btnUnload, "ConfigView.pluginlist.unloadSelected");
/* 462 */     btnUnload.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 464 */         final int[] items = ConfigSectionPlugins.this.table.getSelectionIndices();
/*     */         
/* 466 */         new AEThread2("unload")
/*     */         {
/*     */           public void run()
/*     */           {
/* 470 */             for (int i = 0; i < items.length; i++) {
/* 471 */               int index = items[i];
/* 472 */               if ((index >= 0) && (index < ConfigSectionPlugins.this.pluginIFs.size())) {
/* 473 */                 PluginInterface pluginIF = (PluginInterface)ConfigSectionPlugins.this.pluginIFs.get(index);
/* 474 */                 if ((pluginIF.getPluginState().isOperational()) && 
/* 475 */                   (pluginIF.getPluginState().isUnloadable())) {
/*     */                   try {
/* 477 */                     pluginIF.getPluginState().unload();
/*     */                   }
/*     */                   catch (PluginException e1) {
/* 480 */                     e1.printStackTrace();
/*     */                   }
/*     */                 }
/*     */                 
/*     */ 
/* 485 */                 Utils.execSWTThread(new Runnable()
/*     */                 {
/*     */ 
/*     */                   public void run()
/*     */                   {
/*     */ 
/* 491 */                     ConfigSectionPlugins.this.pluginIFs = ConfigSectionPlugins.this.rebuildPluginIFs();
/* 492 */                     ConfigSectionPlugins.this.table.setItemCount(ConfigSectionPlugins.this.pluginIFs.size());
/* 493 */                     Collections.sort(ConfigSectionPlugins.this.pluginIFs, ConfigSectionPlugins.this.comparator);
/* 494 */                     ConfigSectionPlugins.this.table.clearAll();
/*     */                   }
/*     */                 });
/*     */               }
/*     */             }
/*     */           }
/*     */         }.start();
/*     */       }
/* 502 */     });
/* 503 */     btnUnload.setEnabled(false);
/*     */     
/* 505 */     final Button btnLoad = new Button(cButtons, 8);
/* 506 */     Utils.setLayoutData(btnUnload, new GridData());
/* 507 */     Messages.setLanguageText(btnLoad, "ConfigView.pluginlist.loadSelected");
/* 508 */     btnLoad.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 510 */         final int[] items = ConfigSectionPlugins.this.table.getSelectionIndices();
/*     */         
/* 512 */         new AEThread2("load")
/*     */         {
/*     */           public void run()
/*     */           {
/* 516 */             for (int i = 0; i < items.length; i++) {
/* 517 */               int index = items[i];
/* 518 */               if ((index >= 0) && (index < ConfigSectionPlugins.this.pluginIFs.size()))
/*     */               {
/* 520 */                 PluginInterface pluginIF = (PluginInterface)ConfigSectionPlugins.this.pluginIFs.get(index);
/* 521 */                 if (!pluginIF.getPluginState().isOperational())
/*     */                 {
/*     */ 
/*     */ 
/* 525 */                   if (pluginIF.getPluginState().isDisabled()) {
/* 526 */                     if (!pluginIF.getPluginState().hasFailed()) {
/* 527 */                       pluginIF.getPluginState().setDisabled(false);
/*     */                     }
/*     */                   } else {
/*     */                     try {
/* 531 */                       pluginIF.getPluginState().reload();
/*     */                     }
/*     */                     catch (PluginException e1) {
/* 534 */                       Debug.printStackTrace(e1);
/*     */                     }
/*     */                     
/* 537 */                     Utils.execSWTThread(new Runnable()
/*     */                     {
/*     */ 
/*     */                       public void run()
/*     */                       {
/*     */ 
/* 543 */                         if ((ConfigSectionPlugins.this.table == null) || (ConfigSectionPlugins.this.table.isDisposed())) {
/* 544 */                           return;
/*     */                         }
/* 546 */                         ConfigSectionPlugins.this.pluginIFs = ConfigSectionPlugins.this.rebuildPluginIFs();
/* 547 */                         ConfigSectionPlugins.this.table.setItemCount(ConfigSectionPlugins.this.pluginIFs.size());
/* 548 */                         Collections.sort(ConfigSectionPlugins.this.pluginIFs, ConfigSectionPlugins.this.comparator);
/* 549 */                         ConfigSectionPlugins.this.table.clearAll();
/*     */                       }
/*     */                     });
/*     */                   } }
/*     */               }
/*     */             }
/*     */           }
/*     */         }.start();
/* 557 */       } });
/* 558 */     btnLoad.setEnabled(false);
/*     */     
/*     */ 
/*     */ 
/* 562 */     Button btnScan = new Button(cButtons, 8);
/* 563 */     Utils.setLayoutData(btnScan, new GridData());
/* 564 */     Messages.setLanguageText(btnScan, "ConfigView.pluginlist.scan");
/* 565 */     btnScan.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 567 */         AzureusCoreFactory.getSingleton().getPluginManager().refreshPluginList(false);
/* 568 */         ConfigSectionPlugins.this.pluginIFs = ConfigSectionPlugins.this.rebuildPluginIFs();
/* 569 */         ConfigSectionPlugins.this.table.setItemCount(ConfigSectionPlugins.this.pluginIFs.size());
/* 570 */         Collections.sort(ConfigSectionPlugins.this.pluginIFs, ConfigSectionPlugins.this.comparator);
/* 571 */         ConfigSectionPlugins.this.table.clearAll();
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 576 */     });
/* 577 */     final Button btnUninstall = new Button(cButtons, 8);
/* 578 */     Utils.setLayoutData(btnUninstall, new GridData());
/* 579 */     Messages.setLanguageText(btnUninstall, "ConfigView.pluginlist.uninstallSelected");
/* 580 */     btnUninstall.addSelectionListener(new SelectionAdapter()
/*     */     {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 583 */         btnUninstall.setEnabled(false);
/*     */         
/* 585 */         final int[] items = ConfigSectionPlugins.this.table.getSelectionIndices();
/*     */         
/* 587 */         new AEThread2("uninstall")
/*     */         {
/*     */           public void run()
/*     */           {
/*     */             try
/*     */             {
/* 593 */               List<PluginInterface> pis = new ArrayList();
/*     */               
/* 595 */               for (int i = 0; i < items.length; i++) {
/* 596 */                 int index = items[i];
/* 597 */                 if ((index >= 0) && (index < ConfigSectionPlugins.this.pluginIFs.size())) {
/* 598 */                   PluginInterface pluginIF = (PluginInterface)ConfigSectionPlugins.this.pluginIFs.get(index);
/*     */                   
/* 600 */                   pis.add(pluginIF);
/*     */                 }
/*     */               }
/*     */               
/* 604 */               if (pis.size() > 0)
/*     */               {
/* 606 */                 PluginInterface[] ps = new PluginInterface[pis.size()];
/*     */                 
/* 608 */                 pis.toArray(ps);
/*     */                 
/*     */                 try
/*     */                 {
/* 612 */                   final AESemaphore wait_sem = new AESemaphore("unist:wait");
/*     */                   
/* 614 */                   ps[0].getPluginManager().getPluginInstaller().uninstall(ps, new PluginInstallationListener()
/*     */                   {
/*     */ 
/*     */ 
/*     */                     public void completed()
/*     */                     {
/*     */ 
/* 621 */                       wait_sem.release();
/*     */                     }
/*     */                     
/*     */ 
/*     */                     public void cancelled()
/*     */                     {
/* 627 */                       wait_sem.release();
/*     */                     }
/*     */                     
/*     */ 
/*     */ 
/*     */                     public void failed(PluginException e)
/*     */                     {
/* 634 */                       wait_sem.release();
/*     */                     }
/*     */                     
/* 637 */                   });
/* 638 */                   wait_sem.reserve();
/*     */                 }
/*     */                 catch (Exception e)
/*     */                 {
/* 642 */                   Debug.printStackTrace(e);
/*     */                 }
/*     */               }
/*     */             }
/*     */             finally {
/* 647 */               Utils.execSWTThread(new Runnable()
/*     */               {
/*     */ 
/*     */                 public void run()
/*     */                 {
/*     */ 
/* 653 */                   ConfigSectionPlugins.this.pluginIFs = ConfigSectionPlugins.this.rebuildPluginIFs();
/* 654 */                   ConfigSectionPlugins.this.table.setItemCount(ConfigSectionPlugins.this.pluginIFs.size());
/* 655 */                   Collections.sort(ConfigSectionPlugins.this.pluginIFs, ConfigSectionPlugins.this.comparator);
/* 656 */                   ConfigSectionPlugins.this.table.clearAll();
/* 657 */                   ConfigSectionPlugins.this.table.setSelection(new int[0]);
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */         }.start();
/*     */       }
/* 664 */     });
/* 665 */     btnUninstall.setEnabled(false);
/*     */     
/*     */ 
/*     */ 
/* 669 */     Button btnInstall = new Button(cButtons, 8);
/* 670 */     Utils.setLayoutData(btnInstall, new GridData());
/* 671 */     btnInstall.setText(MessageText.getString("UpdateWindow.columns.install") + "...");
/* 672 */     btnInstall.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 674 */         new InstallPluginWizard();
/*     */       }
/*     */       
/* 677 */     });
/* 678 */     this.table.addListener(36, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 680 */         TableItem item = (TableItem)event.item;
/* 681 */         int index = ConfigSectionPlugins.this.table.indexOf(item);
/* 682 */         PluginInterface pluginIF = (PluginInterface)ConfigSectionPlugins.this.pluginIFs.get(index);
/*     */         
/* 684 */         for (int i = 0; i < ConfigSectionPlugins.COLUMN_HEADERS.length; i++) {
/* 685 */           if (i == 2) {
/* 686 */             item.setImage(i, pluginIF.getPluginState().isOperational() ? ConfigSectionPlugins.this.imgGreenLed : ConfigSectionPlugins.this.imgRedLed);
/*     */           }
/*     */           
/* 689 */           String sText = ConfigSectionPlugins.this.comparator.getFieldValue(i, pluginIF);
/* 690 */           if (sText == null)
/* 691 */             sText = "";
/* 692 */           item.setText(i, sText);
/*     */         }
/*     */         
/* 695 */         item.setGrayed(pluginIF.getPluginState().isMandatory());
/* 696 */         boolean bEnabled = pluginIF.getPluginState().isLoadedAtStartup();
/* 697 */         Utils.setCheckedInSetData(item, bEnabled);
/* 698 */         item.setData("PluginID", pluginIF.getPluginID());
/* 699 */         Utils.alternateRowBackground(item);
/*     */       }
/*     */       
/* 702 */     });
/* 703 */     this.table.addMouseListener(new MouseAdapter()
/*     */     {
/*     */       public void mouseDoubleClick(MouseEvent e)
/*     */       {
/* 707 */         TableItem[] items = ConfigSectionPlugins.this.table.getSelection();
/*     */         
/* 709 */         if (items.length == 1)
/*     */         {
/* 711 */           int index = ConfigSectionPlugins.this.table.indexOf(items[0]);
/*     */           
/* 713 */           PluginInterface pluginIF = (PluginInterface)ConfigSectionPlugins.this.pluginIFs.get(index);
/*     */           
/* 715 */           PluginConfigModel[] models = pluginIF.getUIManager().getPluginConfigModels();
/*     */           
/* 717 */           for (PluginConfigModel model : models)
/*     */           {
/* 719 */             if (model.getPluginInterface() == pluginIF)
/*     */             {
/* 721 */               if ((model instanceof BasicPluginConfigModel))
/*     */               {
/* 723 */                 String id = ((BasicPluginConfigModel)model).getSection();
/*     */                 
/* 725 */                 UIFunctions uiFunctions = UIFunctionsManager.getUIFunctions();
/*     */                 
/* 727 */                 if (uiFunctions != null)
/*     */                 {
/* 729 */                   uiFunctions.getMDI().showEntryByID("ConfigView", id);
/*     */                 }
/*     */                 
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 737 */     });
/* 738 */     this.table.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 740 */         TableItem item = (TableItem)e.item;
/* 741 */         int index = ConfigSectionPlugins.this.table.indexOf(item);
/* 742 */         PluginInterface pluginIF = (PluginInterface)ConfigSectionPlugins.this.pluginIFs.get(index);
/*     */         
/* 744 */         if (e.detail == 32)
/*     */         {
/* 746 */           if (item.getGrayed()) {
/* 747 */             if (!item.getChecked())
/* 748 */               item.setChecked(true);
/* 749 */             return;
/*     */           }
/*     */           
/* 752 */           pluginIF.getPluginState().setDisabled(!item.getChecked());
/* 753 */           pluginIF.getPluginState().setLoadedAtStartup(item.getChecked());
/*     */         }
/*     */         
/* 756 */         btnUnload.setEnabled((pluginIF.getPluginState().isOperational()) && (pluginIF.getPluginState().isUnloadable()));
/* 757 */         btnLoad.setEnabled((!pluginIF.getPluginState().isOperational()) && (!pluginIF.getPluginState().hasFailed()));
/* 758 */         btnUninstall.setEnabled((!pluginIF.getPluginState().isBuiltIn()) && (!pluginIF.getPluginState().isMandatory()));
/*     */       }
/*     */       
/* 761 */     });
/* 762 */     this.table.setItemCount(this.pluginIFs.size());
/*     */     
/*     */ 
/* 765 */     return infoGroup;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private List rebuildPluginIFs()
/*     */   {
/* 774 */     List pluginIFs = Arrays.asList(AzureusCoreFactory.getSingleton().getPluginManager().getPlugins());
/* 775 */     for (Iterator iter = pluginIFs.iterator(); iter.hasNext();) {
/* 776 */       PluginInterface pi = (PluginInterface)iter.next();
/*     */       
/* 778 */       COConfigurationManager.addParameterListener("PluginInfo." + pi.getPluginID() + ".enabled", this);
/*     */     }
/*     */     
/* 781 */     return pluginIFs;
/*     */   }
/*     */   
/*     */   public void parameterChanged(String parameterName)
/*     */   {
/* 786 */     if (this.table != null) {
/* 787 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 789 */           if ((ConfigSectionPlugins.this.table != null) && (!ConfigSectionPlugins.this.table.isDisposed())) {
/* 790 */             ConfigSectionPlugins.this.table.clearAll();
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initPluginSubSections()
/*     */   {
/* 801 */     TreeItem treePlugins = this.configView.findTreeItem("plugins");
/*     */     
/* 803 */     ParameterRepository repository = ParameterRepository.getInstance();
/*     */     
/* 805 */     String[] names = repository.getNames();
/*     */     
/* 807 */     Arrays.sort(names);
/*     */     
/* 809 */     for (int i = 0; i < names.length; i++) {
/* 810 */       String pluginName = names[i];
/* 811 */       Parameter[] parameters = repository.getParameterBlock(pluginName);
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
/* 822 */       boolean bUsePrefix = MessageText.keyExists("ConfigView.section.plugins." + pluginName);
/*     */       
/* 824 */       Composite pluginGroup = this.configView.createConfigSection(treePlugins, pluginName, -2, bUsePrefix);
/*     */       
/* 826 */       GridLayout pluginLayout = new GridLayout();
/* 827 */       pluginLayout.numColumns = 3;
/* 828 */       pluginGroup.setLayout(pluginLayout);
/*     */       
/* 830 */       Map parameterToPluginParameter = new HashMap();
/*     */       
/* 832 */       for (int j = 0; j < parameters.length; j++) {
/* 833 */         Parameter parameter = parameters[j];
/* 834 */         parameterToPluginParameter.put(parameter, new PluginParameter(pluginGroup, parameter));
/*     */       }
/*     */       
/*     */ 
/* 838 */       for (int j = 0; j < parameters.length; j++) {
/* 839 */         Parameter parameter = parameters[j];
/* 840 */         if ((parameter instanceof BooleanParameterImpl)) {
/* 841 */           List parametersToEnable = ((BooleanParameterImpl)parameter).getEnabledOnSelectionParameters();
/*     */           
/* 843 */           List controlsToEnable = new ArrayList();
/* 844 */           Iterator iter = parametersToEnable.iterator();
/* 845 */           while (iter.hasNext()) {
/* 846 */             Parameter parameterToEnable = (Parameter)iter.next();
/* 847 */             PluginParameter pp = (PluginParameter)parameterToPluginParameter.get(parameterToEnable);
/*     */             
/* 849 */             Control[] controls = pp.getControls();
/* 850 */             Collections.addAll(controlsToEnable, controls);
/*     */           }
/*     */           
/* 853 */           List parametersToDisable = ((BooleanParameterImpl)parameter).getDisabledOnSelectionParameters();
/*     */           
/* 855 */           List controlsToDisable = new ArrayList();
/* 856 */           iter = parametersToDisable.iterator();
/* 857 */           while (iter.hasNext()) {
/* 858 */             Parameter parameterToDisable = (Parameter)iter.next();
/* 859 */             PluginParameter pp = (PluginParameter)parameterToPluginParameter.get(parameterToDisable);
/*     */             
/* 861 */             Control[] controls = pp.getControls();
/* 862 */             Collections.addAll(controlsToDisable, controls);
/*     */           }
/*     */           
/* 865 */           Control[] ce = new Control[controlsToEnable.size()];
/* 866 */           Control[] cd = new Control[controlsToDisable.size()];
/*     */           
/* 868 */           if (ce.length + cd.length > 0) {
/* 869 */             IAdditionalActionPerformer ap = new DualChangeSelectionActionPerformer((Control[])controlsToEnable.toArray(ce), (Control[])controlsToDisable.toArray(cd));
/*     */             
/*     */ 
/* 872 */             PluginParameter pp = (PluginParameter)parameterToPluginParameter.get(parameter);
/*     */             
/* 874 */             pp.setAdditionalActionPerfomer(ap);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionPlugins.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */