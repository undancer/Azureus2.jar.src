/*     */ package org.gudy.azureus2.ui.swt.views.configsections;
/*     */ 
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.DirectoryDialog;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.stats.transfer.OverallStats;
/*     */ import org.gudy.azureus2.core3.stats.transfer.StatsFactory;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.config.BooleanParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.ChangeSelectionActionPerformer;
/*     */ import org.gudy.azureus2.ui.swt.config.IntListParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.IntParameter;
/*     */ import org.gudy.azureus2.ui.swt.config.StringParameter;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTConfigSection;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
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
/*     */ public class ConfigSectionStats
/*     */   implements UISWTConfigSection
/*     */ {
/*     */   private static final int defaultStatsPeriod = 30;
/*  66 */   private static final int[] statsPeriods = { 1, 2, 3, 4, 5, 10, 15, 20, 25, 30, 40, 50, 60, 120, 180, 240, 300, 360, 420, 480, 540, 600, 900, 1200, 1800, 2400, 3000, 3600, 7200, 10800, 14400, 21600, 43200, 86400 };
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String configSectionGetParentSection()
/*     */   {
/*  75 */     return "root";
/*     */   }
/*     */   
/*     */   public String configSectionGetName() {
/*  79 */     return "stats";
/*     */   }
/*     */   
/*     */   public void configSectionSave() {}
/*     */   
/*     */   public void configSectionDelete()
/*     */   {
/*  86 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  87 */     imageLoader.releaseImage("openFolderButton");
/*     */   }
/*     */   
/*     */   public int maxUserMode() {
/*  91 */     return 0;
/*     */   }
/*     */   
/*     */ 
/*     */   public Composite configSectionCreate(final Composite parent)
/*     */   {
/*  97 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  98 */     Image imgOpenFolder = imageLoader.getImage("openFolderButton");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 103 */     Composite gOutter = new Composite(parent, 0);
/* 104 */     GridData gridData = new GridData(272);
/* 105 */     gOutter.setLayoutData(gridData);
/* 106 */     GridLayout layout = new GridLayout();
/* 107 */     layout.numColumns = 1;
/* 108 */     gOutter.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 112 */     Group gGeneral = new Group(gOutter, 0);
/* 113 */     Messages.setLanguageText(gGeneral, "ConfigView.section.general");
/* 114 */     layout = new GridLayout(2, false);
/* 115 */     gGeneral.setLayout(layout);
/* 116 */     gGeneral.setLayoutData(new GridData(768));
/*     */     
/* 118 */     Label lSmooth = new Label(gGeneral, 0);
/* 119 */     Messages.setLanguageText(lSmooth, "stats.general.smooth_secs");
/*     */     
/* 121 */     IntParameter smooth_secs = new IntParameter(gGeneral, "Stats Smoothing Secs", 30, 1800);
/*     */     
/*     */ 
/*     */ 
/* 125 */     Group gDisplay = new Group(gOutter, 0);
/* 126 */     Messages.setLanguageText(gDisplay, "stats.display.group");
/* 127 */     layout = new GridLayout(1, false);
/* 128 */     gDisplay.setLayout(layout);
/* 129 */     gDisplay.setLayoutData(new GridData(768));
/*     */     
/* 131 */     gridData = new GridData();
/*     */     
/* 133 */     BooleanParameter graph_dividers = new BooleanParameter(gDisplay, "Stats Graph Dividers", "ConfigView.section.stats.graph_update_dividers");
/* 134 */     graph_dividers.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 140 */     Group gSnap = new Group(gOutter, 0);
/* 141 */     Messages.setLanguageText(gSnap, "stats.snapshot.group");
/* 142 */     layout = new GridLayout(3, false);
/* 143 */     gSnap.setLayout(layout);
/* 144 */     gSnap.setLayoutData(new GridData(768));
/*     */     
/*     */ 
/*     */ 
/* 148 */     gridData = new GridData();
/* 149 */     gridData.horizontalSpan = 3;
/* 150 */     BooleanParameter enableStats = new BooleanParameter(gSnap, "Stats Enable", "ConfigView.section.stats.enable");
/*     */     
/*     */ 
/* 153 */     enableStats.setLayoutData(gridData);
/*     */     
/* 155 */     Control[] controls = new Control[13];
/*     */     
/*     */ 
/*     */ 
/* 159 */     Label lStatsPath = new Label(gSnap, 0);
/* 160 */     Messages.setLanguageText(lStatsPath, "ConfigView.section.stats.defaultsavepath");
/*     */     
/* 162 */     gridData = new GridData();
/* 163 */     gridData.widthHint = 150;
/* 164 */     final StringParameter pathParameter = new StringParameter(gSnap, "Stats Dir", "");
/* 165 */     pathParameter.setLayoutData(gridData);
/* 166 */     controls[0] = lStatsPath;
/* 167 */     controls[1] = pathParameter.getControl();
/* 168 */     Button browse = new Button(gSnap, 8);
/* 169 */     browse.setImage(imgOpenFolder);
/* 170 */     imgOpenFolder.setBackground(browse.getBackground());
/* 171 */     browse.setToolTipText(MessageText.getString("ConfigView.button.browse"));
/* 172 */     controls[2] = browse;
/* 173 */     browse.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/* 178 */         DirectoryDialog dialog = new DirectoryDialog(parent.getShell(), 65536);
/* 179 */         dialog.setFilterPath(pathParameter.getValue());
/* 180 */         dialog.setText(MessageText.getString("ConfigView.section.stats.choosedefaultsavepath"));
/* 181 */         String path = dialog.open();
/* 182 */         if (path != null) {
/* 183 */           pathParameter.setValue(path);
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 189 */     });
/* 190 */     Label lSaveFile = new Label(gSnap, 0);
/* 191 */     Messages.setLanguageText(lSaveFile, "ConfigView.section.stats.savefile");
/* 192 */     controls[3] = lSaveFile;
/*     */     
/* 194 */     gridData = new GridData();
/* 195 */     gridData.widthHint = 150;
/* 196 */     StringParameter fileParameter = new StringParameter(gSnap, "Stats File", "Azureus_Stats.xml");
/* 197 */     fileParameter.setLayoutData(gridData);
/* 198 */     controls[4] = fileParameter.getControl();
/* 199 */     new Label(gSnap, 0);
/*     */     
/*     */ 
/*     */ 
/* 203 */     Label lxslFile = new Label(gSnap, 0);
/* 204 */     Messages.setLanguageText(lxslFile, "ConfigView.section.stats.xslfile");
/* 205 */     controls[5] = lxslFile;
/*     */     
/* 207 */     gridData = new GridData();
/* 208 */     gridData.widthHint = 150;
/* 209 */     StringParameter xslParameter = new StringParameter(gSnap, "Stats XSL File", "");
/* 210 */     xslParameter.setLayoutData(gridData);
/* 211 */     controls[6] = xslParameter.getControl();
/* 212 */     Label lxslDetails = new Label(gSnap, 0);
/* 213 */     Messages.setLanguageText(lxslDetails, "ConfigView.section.stats.xslfiledetails");
/* 214 */     String linkFAQ = "http://plugins.vuze.com/faq.php#20";
/* 215 */     lxslDetails.setCursor(lxslDetails.getDisplay().getSystemCursor(21));
/* 216 */     lxslDetails.setForeground(Colors.blue);
/* 217 */     lxslDetails.setData("http://plugins.vuze.com/faq.php#20");
/* 218 */     lxslDetails.addMouseListener(new MouseAdapter() {
/*     */       public void mouseDoubleClick(MouseEvent arg0) {
/* 220 */         Utils.launch("http://plugins.vuze.com/faq.php#20");
/*     */       }
/*     */       
/* 223 */       public void mouseDown(MouseEvent arg0) { Utils.launch("http://plugins.vuze.com/faq.php#20");
/*     */       }
/* 225 */     });
/* 226 */     ClipboardCopy.addCopyToClipMenu(lxslDetails);
/* 227 */     controls[7] = lxslDetails;
/*     */     
/*     */ 
/*     */ 
/* 231 */     Label lSaveFreq = new Label(gSnap, 0);
/*     */     
/* 233 */     Messages.setLanguageText(lSaveFreq, "ConfigView.section.stats.savefreq");
/* 234 */     controls[8] = lSaveFreq;
/*     */     
/* 236 */     String[] spLabels = new String[statsPeriods.length];
/* 237 */     int[] spValues = new int[statsPeriods.length];
/* 238 */     for (int i = 0; i < statsPeriods.length; i++) {
/* 239 */       int num = statsPeriods[i];
/*     */       
/* 241 */       if (num % 3600 == 0) {
/* 242 */         spLabels[i] = (" " + statsPeriods[i] / 3600 + " " + MessageText.getString("ConfigView.section.stats.hours"));
/*     */ 
/*     */       }
/* 245 */       else if (num % 60 == 0) {
/* 246 */         spLabels[i] = (" " + statsPeriods[i] / 60 + " " + MessageText.getString("ConfigView.section.stats.minutes"));
/*     */       }
/*     */       else
/*     */       {
/* 250 */         spLabels[i] = (" " + statsPeriods[i] + " " + MessageText.getString("ConfigView.section.stats.seconds"));
/*     */       }
/*     */       
/* 253 */       spValues[i] = statsPeriods[i];
/*     */     }
/*     */     
/* 256 */     controls[9] = lSaveFreq;
/* 257 */     controls[10] = new IntListParameter(gSnap, "Stats Period", 30, spLabels, spValues).getControl();
/* 258 */     new Label(gSnap, 0);
/*     */     
/*     */ 
/*     */ 
/* 262 */     gridData = new GridData();
/* 263 */     gridData.horizontalSpan = 3;
/* 264 */     BooleanParameter exportPeers = new BooleanParameter(gSnap, "Stats Export Peer Details", "ConfigView.section.stats.exportpeers");
/*     */     
/*     */ 
/* 267 */     exportPeers.setLayoutData(gridData);
/*     */     
/* 269 */     controls[11] = exportPeers.getControl();
/*     */     
/*     */ 
/*     */ 
/* 273 */     gridData = new GridData();
/* 274 */     gridData.horizontalSpan = 3;
/* 275 */     BooleanParameter exportFiles = new BooleanParameter(gSnap, "Stats Export File Details", "ConfigView.section.stats.exportfiles");
/*     */     
/*     */ 
/* 278 */     exportFiles.setLayoutData(gridData);
/*     */     
/* 280 */     controls[12] = exportFiles.getControl();
/*     */     
/*     */ 
/*     */ 
/* 284 */     enableStats.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(controls));
/*     */     
/*     */ 
/*     */ 
/* 288 */     Group gXfer = new Group(gOutter, 0);
/* 289 */     Messages.setLanguageText(gXfer, "ConfigView.section.transfer");
/* 290 */     layout = new GridLayout(3, false);
/* 291 */     gXfer.setLayout(layout);
/* 292 */     gXfer.setLayoutData(new GridData(768));
/*     */     
/* 294 */     List<Button> buttons = new ArrayList();
/*     */     
/*     */ 
/*     */ 
/* 298 */     Label set_mark_label = new Label(gXfer, 0);
/* 299 */     Messages.setLanguageText(set_mark_label, "ConfigView.section.transfer.setmark");
/*     */     
/* 301 */     Button set_mark_button = new Button(gXfer, 8);
/*     */     
/* 303 */     buttons.add(set_mark_button);
/*     */     
/* 305 */     Messages.setLanguageText(set_mark_button, "Button.set");
/*     */     
/* 307 */     set_mark_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 313 */         OverallStats stats = StatsFactory.getStats();
/*     */         
/* 315 */         stats.setMark();
/*     */       }
/*     */       
/* 318 */     });
/* 319 */     Button clear_mark_button = new Button(gXfer, 8);
/*     */     
/* 321 */     buttons.add(clear_mark_button);
/*     */     
/* 323 */     Messages.setLanguageText(clear_mark_button, "Button.clear");
/*     */     
/* 325 */     clear_mark_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 331 */         OverallStats stats = StatsFactory.getStats();
/*     */         
/* 333 */         stats.clearMark();
/*     */       }
/*     */       
/*     */ 
/* 337 */     });
/* 338 */     Group gLong = new Group(gOutter, 0);
/* 339 */     Messages.setLanguageText(gLong, "stats.longterm.group");
/* 340 */     layout = new GridLayout(2, false);
/* 341 */     gLong.setLayout(layout);
/* 342 */     gLong.setLayoutData(new GridData(768));
/*     */     
/*     */ 
/*     */ 
/* 346 */     gridData = new GridData();
/* 347 */     gridData.horizontalSpan = 2;
/* 348 */     BooleanParameter enableLongStats = new BooleanParameter(gLong, "long.term.stats.enable", "ConfigView.section.stats.enable");
/*     */     
/*     */ 
/* 351 */     enableLongStats.setLayoutData(gridData);
/*     */     
/*     */ 
/*     */ 
/* 355 */     Label lWeekStart = new Label(gLong, 0);
/* 356 */     Messages.setLanguageText(lWeekStart, "stats.long.weekstart");
/*     */     
/* 358 */     String[] wsLabels = new String[7];
/* 359 */     int[] wsValues = new int[7];
/*     */     
/* 361 */     Calendar cal = new GregorianCalendar();
/* 362 */     SimpleDateFormat format = new SimpleDateFormat("E");
/*     */     
/* 364 */     for (int i = 0; i < 7; i++) {
/* 365 */       int dow = i + 1;
/* 366 */       cal.set(7, dow);
/* 367 */       wsLabels[i] = format.format(cal.getTime());
/* 368 */       wsValues[i] = (i + 1);
/*     */     }
/*     */     
/* 371 */     IntListParameter week_start = new IntListParameter(gLong, "long.term.stats.weekstart", 1, wsLabels, wsValues);
/*     */     
/*     */ 
/*     */ 
/* 375 */     Label lMonthStart = new Label(gLong, 0);
/* 376 */     Messages.setLanguageText(lMonthStart, "stats.long.monthstart");
/*     */     
/* 378 */     IntParameter month_start = new IntParameter(gLong, "long.term.stats.monthstart", 1, 28);
/*     */     
/* 380 */     enableLongStats.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(new Control[] { lWeekStart, lMonthStart }));
/* 381 */     enableLongStats.setAdditionalActionPerformer(new ChangeSelectionActionPerformer(week_start, month_start));
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 386 */     Label lt_reset_label = new Label(gLong, 0);
/* 387 */     Messages.setLanguageText(lt_reset_label, "ConfigView.section.transfer.lts.reset");
/*     */     
/* 389 */     Button lt_reset_button = new Button(gLong, 8);
/*     */     
/* 391 */     buttons.add(lt_reset_button);
/*     */     
/* 393 */     Messages.setLanguageText(lt_reset_button, "Button.clear");
/*     */     
/* 395 */     lt_reset_button.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */       public void handleEvent(Event event)
/*     */       {
/*     */ 
/* 401 */         MessageBoxShell mb = new MessageBoxShell(296, MessageText.getString("ConfigView.section.security.resetcerts.warning.title"), MessageText.getString("ConfigView.section.transfer.ltsreset.warning.msg"));
/*     */         
/*     */ 
/*     */ 
/* 405 */         mb.setDefaultButtonUsingStyle(256);
/* 406 */         mb.setParent(parent.getShell());
/*     */         
/* 408 */         mb.open(new UserPrompterResultListener() {
/*     */           public void prompterClosed(int returnVal) {
/* 410 */             if (returnVal != 32) {
/* 411 */               return;
/*     */             }
/*     */             
/* 414 */             Utils.getOffOfSWTThread(new AERunnable()
/*     */             {
/*     */ 
/*     */               public void runSupport() {}
/*     */ 
/*     */             });
/*     */           }
/*     */           
/*     */ 
/*     */         });
/*     */       }
/*     */       
/*     */ 
/* 427 */     });
/* 428 */     Utils.makeButtonsEqualWidth(buttons);
/*     */     
/* 430 */     return gOutter;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/configsections/ConfigSectionStats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */