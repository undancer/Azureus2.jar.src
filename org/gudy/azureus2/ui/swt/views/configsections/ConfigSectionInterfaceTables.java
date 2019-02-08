/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.FileParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntListParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.Parameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ParameterChangeAdapter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringListParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*     */ public class ConfigSectionInterfaceTables
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final String MSG_PREFIX = "ConfigView.section.style.";
/*     */   
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  52 */     return "style";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  56 */     return "tables";
/*     */   }
/*     */   
/*     */ 
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete() {}
/*     */   
/*     */   public int maxUserMode()
/*     */   {
/*  66 */     return 2;
/*     */   }
/*     */   
/*     */   public Composite configSectionCreate(Composite parent) {
/*  70 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/*  71 */     boolean isAZ3 = COConfigurationManager.getStringParameter("ui").equals("az3");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  81 */     Composite cSection = new Composite(parent, 0);
/*  82 */     Utils.setLayoutData(cSection, new GridData(1808));
/*  83 */     GridLayout layout = new GridLayout();
/*  84 */     layout.numColumns = 1;
/*  85 */     cSection.setLayout(layout);
/*     */     
/*     */ 
/*  88 */     Group cGeneral = new Group(cSection, 0);
/*  89 */     Messages.setLanguageText(cGeneral, "ConfigView.section.global");
/*  90 */     layout = new GridLayout();
/*  91 */     layout.numColumns = 2;
/*  92 */     cGeneral.setLayout(layout);
/*  93 */     Utils.setLayoutData(cGeneral, new GridData(768));
/*     */     
/*  95 */     Label label = new Label(cGeneral, 0);
/*  96 */     Messages.setLanguageText(label, "ConfigView.section.style.defaultSortOrder");
/*  97 */     int[] sortOrderValues = { 0, 1, 2 };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 102 */     String[] sortOrderLabels = { MessageText.getString("ConfigView.section.style.defaultSortOrder.asc"), MessageText.getString("ConfigView.section.style.defaultSortOrder.desc"), MessageText.getString("ConfigView.section.style.defaultSortOrder.flip") };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 107 */     new IntListParameter(cGeneral, "config.style.table.defaultSortOrder", sortOrderLabels, sortOrderValues);
/*     */     
/*     */ 
/* 110 */     if (userMode > 0) {
/* 111 */       label = new Label(cGeneral, 0);
/* 112 */       Messages.setLanguageText(label, "ConfigView.section.style.guiUpdate");
/* 113 */       int[] values = { 10, 25, 50, 100, 250, 500, 1000, 2000, 5000, 10000, 15000 };
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
/* 126 */       String[] labels = { "10 ms", "25 ms", "50 ms", "100 ms", "250 ms", "500 ms", "1 s", "2 s", "5 s", "10 s", "15 s" };
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
/* 139 */       new IntListParameter(cGeneral, "GUI Refresh", 1000, labels, values);
/*     */       
/* 141 */       label = new Label(cGeneral, 0);
/* 142 */       Messages.setLanguageText(label, "ConfigView.section.style.graphicsUpdate");
/* 143 */       GridData gridData = new GridData();
/* 144 */       IntParameter graphicUpdate = new IntParameter(cGeneral, "Graphics Update", 1, Integer.MAX_VALUE);
/*     */       
/* 146 */       graphicUpdate.setLayoutData(gridData);
/*     */       
/* 148 */       label = new Label(cGeneral, 0);
/* 149 */       Messages.setLanguageText(label, "ConfigView.section.style.reOrderDelay");
/* 150 */       gridData = new GridData();
/* 151 */       IntParameter reorderDelay = new IntParameter(cGeneral, "ReOrder Delay");
/* 152 */       reorderDelay.setLayoutData(gridData);
/*     */       
/* 154 */       new BooleanParameter(cGeneral, "NameColumn.showProgramIcon", "ConfigView.section.style.showProgramIcon").setLayoutData(new GridData(4, 16384, true, false, 2, 1));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 159 */       new BooleanParameter(cGeneral, "Table.extendedErase", "ConfigView.section.style.extendedErase").setLayoutData(new GridData(4, 16384, true, false, 2, 1));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 165 */       boolean hhEnabled = COConfigurationManager.getIntParameter("Table.headerHeight") > 0;
/*     */       
/* 167 */       Button chkHeaderHeight = new Button(cGeneral, 32);
/* 168 */       Messages.setLanguageText(chkHeaderHeight, "ConfigView.section.style.enableHeaderHeight");
/* 169 */       chkHeaderHeight.setSelection(hhEnabled);
/*     */       
/* 171 */       final IntParameter paramHH = new IntParameter(cGeneral, "Table.headerHeight", 0, 100);
/* 172 */       paramHH.setEnabled(hhEnabled);
/*     */       
/* 174 */       chkHeaderHeight.addSelectionListener(new SelectionListener() {
/*     */         public void widgetSelected(SelectionEvent e) {
/* 176 */           if (((Button)e.widget).getSelection()) {
/* 177 */             COConfigurationManager.setParameter("Table.headerHeight", 16);
/* 178 */             paramHH.setEnabled(true);
/*     */           } else {
/* 180 */             COConfigurationManager.setParameter("Table.headerHeight", 0);
/* 181 */             paramHH.setEnabled(false);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         public void widgetDefaultSelected(SelectionEvent e) {}
/* 190 */       });
/* 191 */       boolean cdEnabled = COConfigurationManager.getStringParameter("Table.column.dateformat", "").length() > 0;
/*     */       
/* 193 */       Button chkCustomDate = new Button(cGeneral, 32);
/* 194 */       Messages.setLanguageText(chkCustomDate, "ConfigView.section.style.customDateFormat");
/* 195 */       chkCustomDate.setSelection(cdEnabled);
/*     */       
/* 197 */       final StringParameter paramCustomDate = new StringParameter(cGeneral, "Table.column.dateformat", "");
/* 198 */       paramCustomDate.setLayoutData(new GridData(4, 4, true, false));
/* 199 */       paramCustomDate.setEnabled(cdEnabled);
/* 200 */       paramCustomDate.addChangeListener(new ParameterChangeAdapter()
/*     */       {
/*     */         public void parameterChanged(Parameter p, boolean caused_internally) {
/* 203 */           String s = (String)p.getValueObject();
/* 204 */           boolean ok = false;
/*     */           try {
/* 206 */             SimpleDateFormat temp = new SimpleDateFormat(s);
/* 207 */             temp.format(new Date());
/* 208 */             ok = true;
/*     */           }
/*     */           catch (Exception e) {}
/*     */           
/* 212 */           p.getControl().setBackground(ok ? null : Colors.colorErrorBG);
/*     */         }
/*     */         
/*     */ 
/* 216 */       });
/* 217 */       chkCustomDate.addSelectionListener(new SelectionListener() {
/*     */         public void widgetSelected(SelectionEvent e) {
/* 219 */           if (((Button)e.widget).getSelection()) {
/* 220 */             COConfigurationManager.setParameter("Table.column.dateformat", "yyyy/MM/dd");
/* 221 */             paramCustomDate.setEnabled(true);
/*     */           } else {
/* 223 */             COConfigurationManager.setParameter("Table.column.dateformat", "");
/* 224 */             paramCustomDate.setEnabled(false);
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */         public void widgetDefaultSelected(SelectionEvent e) {}
/*     */       });
/*     */     }
/*     */     
/*     */ 
/* 235 */     Group cLibrary = new Group(cSection, 0);
/* 236 */     Messages.setLanguageText(cLibrary, "ConfigView.section.style.library");
/* 237 */     layout = new GridLayout();
/* 238 */     layout.numColumns = 2;
/* 239 */     cLibrary.setLayout(layout);
/* 240 */     Utils.setLayoutData(cLibrary, new GridData(768));
/*     */     
/*     */ 
/*     */ 
/* 244 */     new BooleanParameter(cLibrary, "Table.useTree", "ConfigView.section.style.useTree").setLayoutData(new GridData(4, 16384, true, false, 2, 1));
/*     */     
/*     */ 
/*     */ 
/* 248 */     if (userMode > 1) {
/* 249 */       new BooleanParameter(cLibrary, "DND Always In Incomplete", "ConfigView.section.style.DNDalwaysInIncomplete").setLayoutData(new GridData(4, 16384, true, false, 2, 1));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 254 */     if (isAZ3)
/*     */     {
/* 256 */       new BooleanParameter(cLibrary, "Library.EnableSimpleView", "ConfigView.section.style.EnableSimpleView").setLayoutData(new GridData(4, 16384, true, false, 2, 1));
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 261 */       new BooleanParameter(cLibrary, "Library.CatInSideBar", "ConfigView.section.style.CatInSidebar").setLayoutData(new GridData(4, 16384, true, false, 2, 1));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 266 */     new BooleanParameter(cLibrary, "Library.ShowCatButtons", "ConfigView.section.style.ShowCatButtons").setLayoutData(new GridData(4, 16384, true, false, 2, 1));
/*     */     
/*     */ 
/*     */ 
/* 270 */     if (isAZ3)
/*     */     {
/* 272 */       new BooleanParameter(cLibrary, "Library.TagInSideBar", "ConfigView.section.style.TagInSidebar").setLayoutData(new GridData(4, 16384, true, false, 2, 1));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 277 */     BooleanParameter show_tag = new BooleanParameter(cLibrary, "Library.ShowTagButtons", "ConfigView.section.style.ShowTagButtons");
/*     */     
/*     */ 
/* 280 */     show_tag.setLayoutData(new GridData(4, 16384, true, false, 2, 1));
/*     */     
/* 282 */     BooleanParameter show_tag_comp_only = new BooleanParameter(cLibrary, "Library.ShowTagButtons.CompOnly", "ConfigView.section.style.ShowTagButtons.CompOnly");
/*     */     
/*     */ 
/* 285 */     GridData gridData = new GridData(4, 16384, true, false, 2, 1);
/* 286 */     gridData.horizontalIndent = 25;
/* 287 */     show_tag_comp_only.setLayoutData(gridData);
/*     */     
/* 289 */     show_tag.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(show_tag_comp_only));
/*     */     
/* 291 */     if (isAZ3)
/*     */     {
/* 293 */       new BooleanParameter(cLibrary, "Library.ShowTabsInTorrentView", "ConfigView.section.style.ShowTabsInTorrentView").setLayoutData(new GridData(4, 16384, true, false, 2, 1));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 298 */     new BooleanParameter(cLibrary, "Library.showFancyMenu", true, "ConfigView.section.style.ShowFancyMenu").setLayoutData(new GridData(4, 16384, true, false, 2, 1));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 306 */     label = new Label(cLibrary, 0);
/* 307 */     Messages.setLanguageText(label, "ConfigView.label.dm.dblclick");
/*     */     
/* 309 */     String[] dblclickOptions = { "ConfigView.option.dm.dblclick.play", "ConfigView.option.dm.dblclick.details", "ConfigView.option.dm.dblclick.show", "ConfigView.option.dm.dblclick.launch", "ConfigView.option.dm.dblclick.launch.qv", "ConfigView.option.dm.dblclick.open.browser" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 318 */     String[] dblclickLabels = new String[dblclickOptions.length];
/* 319 */     String[] dblclickValues = new String[dblclickOptions.length];
/*     */     
/* 321 */     for (int i = 0; i < dblclickOptions.length; i++)
/*     */     {
/* 323 */       dblclickLabels[i] = MessageText.getString(dblclickOptions[i]);
/* 324 */       dblclickValues[i] = ("" + i);
/*     */     }
/* 326 */     new StringListParameter(cLibrary, "list.dm.dblclick", dblclickLabels, dblclickValues);
/*     */     
/*     */ 
/*     */ 
/* 330 */     Composite cLaunchWeb = new Composite(cLibrary, 0);
/* 331 */     layout = new GridLayout();
/* 332 */     layout.numColumns = 4;
/* 333 */     cLaunchWeb.setLayout(layout);
/* 334 */     gridData = new GridData(768);
/* 335 */     gridData.horizontalSpan = 2;
/* 336 */     gridData.horizontalIndent = 25;
/* 337 */     Utils.setLayoutData(cLaunchWeb, gridData);
/*     */     
/* 339 */     BooleanParameter web_in_browser = new BooleanParameter(cLaunchWeb, "Library.LaunchWebsiteInBrowser", "library.launch.web.in.browser");
/*     */     
/*     */ 
/* 342 */     BooleanParameter web_in_browser_anon = new BooleanParameter(cLaunchWeb, "Library.LaunchWebsiteInBrowserAnon", "library.launch.web.in.browser.anon");
/*     */     
/*     */ 
/* 345 */     web_in_browser.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(web_in_browser_anon));
/*     */     
/*     */ 
/*     */ 
/* 349 */     Group cLaunch = new Group(cLibrary, 0);
/* 350 */     Messages.setLanguageText(cLaunch, "ConfigView.section.style.launch");
/* 351 */     layout = new GridLayout();
/* 352 */     layout.numColumns = 5;
/* 353 */     cLaunch.setLayout(layout);
/* 354 */     gridData = new GridData(768);
/* 355 */     gridData.horizontalSpan = 2;
/* 356 */     Utils.setLayoutData(cLaunch, gridData);
/*     */     
/* 358 */     Label info_label = new Label(cLaunch, 64);
/* 359 */     Messages.setLanguageText(info_label, "ConfigView.label.lh.info");
/* 360 */     gridData = Utils.getWrappableLabelGridData(5, 256);
/* 361 */     Utils.setLayoutData(info_label, gridData);
/*     */     
/* 363 */     for (int i = 0; i < 4; i++)
/*     */     {
/* 365 */       label = new Label(cLaunch, 0);
/* 366 */       Messages.setLanguageText(label, "ConfigView.label.lh.ext");
/*     */       
/* 368 */       StringParameter exts = new StringParameter(cLaunch, "Table.lh" + i + ".exts", "");
/* 369 */       gridData = new GridData();
/* 370 */       gridData.widthHint = 200;
/* 371 */       exts.setLayoutData(gridData);
/*     */       
/* 373 */       label = new Label(cLaunch, 0);
/* 374 */       Messages.setLanguageText(label, "ConfigView.label.lh.prog");
/*     */       
/* 376 */       final FileParameter prog = new FileParameter(cLaunch, "Table.lh" + i + ".prog", "", new String[0]);
/*     */       
/* 378 */       gridData = new GridData();
/* 379 */       gridData.widthHint = 400;
/* 380 */       prog.getControls()[0].setLayoutData(gridData);
/*     */       
/* 382 */       if (Constants.isOSX) {
/* 383 */         COConfigurationManager.addParameterListener("Table.lh" + i + ".prog", new ParameterListener()
/*     */         {
/*     */ 
/*     */ 
/* 387 */           private boolean changing = false;
/* 388 */           private String last_changed = "";
/*     */           
/*     */ 
/*     */ 
/*     */           public void parameterChanged(String parameter_name)
/*     */           {
/* 394 */             if (prog.isDisposed())
/*     */             {
/* 396 */               COConfigurationManager.removeParameterListener(parameter_name, this);
/*     */             } else {
/* 398 */               if (this.changing)
/*     */               {
/* 400 */                 return;
/*     */               }
/*     */               
/*     */ 
/* 404 */               final String value = COConfigurationManager.getStringParameter(parameter_name);
/*     */               
/* 406 */               if (value.equals(this.last_changed))
/*     */               {
/* 408 */                 return;
/*     */               }
/*     */               
/* 411 */               if (value.endsWith(".app"))
/*     */               {
/* 413 */                 Utils.execSWTThreadLater(1, new Runnable()
/*     */                 {
/*     */ 
/*     */ 
/*     */                   public void run()
/*     */                   {
/*     */ 
/* 420 */                     ConfigSectionInterfaceTables.4.this.last_changed = value;
/*     */                     try
/*     */                     {
/* 423 */                       ConfigSectionInterfaceTables.4.this.changing = true;
/*     */                       
/* 425 */                       File file = new File(value);
/*     */                       
/* 427 */                       String app_name = file.getName();
/*     */                       
/* 429 */                       int pos = app_name.lastIndexOf(".");
/*     */                       
/* 431 */                       app_name = app_name.substring(0, pos);
/*     */                       
/* 433 */                       String new_value = value + "/Contents/MacOS/" + app_name;
/*     */                       
/* 435 */                       if (new File(new_value).exists())
/*     */                       {
/* 437 */                         ConfigSectionInterfaceTables.4.this.val$prog.setValue(new_value);
/*     */                       }
/*     */                     }
/*     */                     finally {
/* 441 */                       ConfigSectionInterfaceTables.4.this.changing = false;
/*     */                     }
/*     */                   }
/*     */                 });
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 454 */     Group cSeachSubs = new Group(cSection, 0);
/* 455 */     Messages.setLanguageText(cSeachSubs, "ConfigView.section.style.searchsubs");
/* 456 */     layout = new GridLayout();
/* 457 */     layout.numColumns = 2;
/* 458 */     cSeachSubs.setLayout(layout);
/* 459 */     Utils.setLayoutData(cSeachSubs, new GridData(768));
/*     */     
/*     */ 
/* 462 */     new BooleanParameter(cSeachSubs, "Search View Is Web View", "ConfigView.section.style.search.is.web.view").setLayoutData(new GridData(4, 16384, true, false, 2, 1));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 467 */     new BooleanParameter(cSeachSubs, "Search View Switch Hidden", "ConfigView.section.style.search.hide.view.switch").setLayoutData(new GridData(4, 16384, true, false, 2, 1));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 472 */     label = new Label(cSeachSubs, 0);
/* 473 */     Messages.setLanguageText(label, "ConfigView.section.style.searchsubs.row.height");
/* 474 */     gridData = new GridData();
/* 475 */     IntParameter graphicUpdate = new IntParameter(cSeachSubs, "Search Subs Row Height", 16, 64);
/*     */     
/* 477 */     graphicUpdate.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 481 */     return cSection;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionInterfaceTables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */