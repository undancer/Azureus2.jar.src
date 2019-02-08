/*     */ package org.gudy.azureus2.ui.swt.mainwindow;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TreeSet;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.MessageBox;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.impl.TransferSpeedValidator;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerStats;
/*     */ import org.gudy.azureus2.core3.global.GlobalManager;
/*     */ import org.gudy.azureus2.core3.global.GlobalManagerStats;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiverListener;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*     */ import org.gudy.azureus2.ui.swt.shells.SpeedScaleShell;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SelectableSpeedMenu
/*     */ {
/*  43 */   private static final int[] increases = { 5, 10, 35, 50, 50, 50, 100 };
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
/*     */   public static void generateMenuItems(final Menu parent, AzureusCore core, final GlobalManager globalManager, boolean up_menu)
/*     */   {
/*  56 */     int kInB = 1024;
/*     */     
/*  58 */     MenuItem[] oldItems = parent.getItems();
/*  59 */     for (int i = 0; i < oldItems.length; i++)
/*     */     {
/*  61 */       oldItems[i].dispose();
/*     */     }
/*     */     
/*  64 */     final String configKey = up_menu ? TransferSpeedValidator.getActiveUploadParameter(globalManager) : "Max Download Speed KBs";
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  69 */     int speedPartitions = 12;
/*     */     
/*  71 */     int maxBandwidth = COConfigurationManager.getIntParameter(configKey);
/*  72 */     boolean unlim = maxBandwidth == 0;
/*  73 */     maxBandwidth = adjustMaxBandWidth(maxBandwidth, globalManager, up_menu, 1024);
/*     */     
/*  75 */     boolean auto = false;
/*     */     
/*  77 */     if (up_menu)
/*     */     {
/*  79 */       String configAutoKey = TransferSpeedValidator.getActiveAutoUploadParameter(globalManager);
/*     */       
/*     */ 
/*  82 */       auto = TransferSpeedValidator.isAutoSpeedActive(globalManager);
/*     */       
/*     */ 
/*  85 */       final MenuItem auto_item = new MenuItem(parent, 32);
/*  86 */       auto_item.setText(MessageText.getString("ConfigView.auto"));
/*  87 */       auto_item.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/*  89 */           COConfigurationManager.setParameter(this.val$configAutoKey, auto_item.getSelection());
/*  90 */           COConfigurationManager.save();
/*     */         }
/*     */       });
/*     */       
/*  94 */       if (auto) auto_item.setSelection(true);
/*  95 */       auto_item.setEnabled(TransferSpeedValidator.isAutoUploadAvailable(core));
/*     */       
/*  97 */       new MenuItem(parent, 2);
/*     */     }
/*     */     
/* 100 */     MenuItem item = new MenuItem(parent, 16);
/* 101 */     item.setText(MessageText.getString("MyTorrentsView.menu.setSpeed.unlimited"));
/* 102 */     item.setData("maxkb", new Integer(0));
/* 103 */     item.setSelection((unlim) && (!auto));
/* 104 */     item.addListener(13, getLimitMenuItemListener(up_menu, parent, globalManager, configKey));
/*     */     
/* 106 */     Integer[] speed_limits = null;
/*     */     
/* 108 */     String config_prefix = "config.ui.speed.partitions.manual." + (up_menu ? "upload" : "download") + ".";
/* 109 */     if (COConfigurationManager.getBooleanParameter(config_prefix + "enabled", false)) {
/* 110 */       speed_limits = parseSpeedPartitionString(COConfigurationManager.getStringParameter(config_prefix + "values", ""));
/*     */     }
/*     */     
/* 113 */     if (speed_limits == null) {
/* 114 */       speed_limits = getGenericSpeedList(12, maxBandwidth);
/*     */     }
/*     */     
/* 117 */     for (int i = 0; i < speed_limits.length; i++) {
/* 118 */       Integer i_value = speed_limits[i];
/* 119 */       int value = i_value.intValue();
/* 120 */       if (value >= 5) {
/* 121 */         item = new MenuItem(parent, 16);
/* 122 */         item.setText(DisplayFormatters.formatByteCountToKiBEtcPerSec(value * 1024, true));
/* 123 */         item.setData("maxkb", i_value);
/* 124 */         item.addListener(13, getLimitMenuItemListener(up_menu, parent, globalManager, configKey));
/* 125 */         item.setSelection((!unlim) && (value == maxBandwidth) && (!auto));
/*     */       }
/*     */     }
/* 128 */     new MenuItem(parent, 2);
/*     */     
/* 130 */     MenuItem itemDownSpeedManual = new MenuItem(parent, 8);
/* 131 */     Messages.setLanguageText(itemDownSpeedManual, "MyTorrentsView.menu.manual");
/* 132 */     itemDownSpeedManual.addSelectionListener(new SelectionAdapter() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 134 */         String kbps_str = MessageText.getString("MyTorrentsView.dialog.setNumber.inKbps", new String[] { DisplayFormatters.getRateUnit(1) });
/*     */         
/*     */ 
/* 137 */         SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow();
/* 138 */         entryWindow.initTexts("MyTorrentsView.dialog.setSpeed.title", new String[] { MessageText.getString(this.val$up_menu ? "MyTorrentsView.dialog.setNumber.upload" : "MyTorrentsView.dialog.setNumber.download") }, "MyTorrentsView.dialog.setNumber.text", new String[] { kbps_str, MessageText.getString(this.val$up_menu ? "MyTorrentsView.dialog.setNumber.upload" : "MyTorrentsView.dialog.setNumber.download") });
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
/* 150 */         entryWindow.prompt(new UIInputReceiverListener() {
/*     */           public void UIInputReceiverClosed(UIInputReceiver entryWindow) {
/* 152 */             if (!entryWindow.hasSubmittedInput()) {
/* 153 */               return;
/*     */             }
/* 155 */             String sReturn = entryWindow.getSubmittedInput();
/*     */             
/* 157 */             if (sReturn == null) {
/*     */               return;
/*     */             }
/*     */             int newSpeed;
/*     */             try {
/* 162 */               newSpeed = (int)Double.valueOf(sReturn).doubleValue();
/*     */             } catch (NumberFormatException er) {
/* 164 */               MessageBox mb = new MessageBox(SelectableSpeedMenu.2.this.val$parent.getShell(), 33);
/*     */               
/* 166 */               mb.setText(MessageText.getString("MyTorrentsView.dialog.NumberError.title"));
/* 167 */               mb.setMessage(MessageText.getString("MyTorrentsView.dialog.NumberError.text"));
/*     */               
/* 169 */               mb.open();
/* 170 */               return;
/*     */             }
/*     */             
/* 173 */             if (SelectableSpeedMenu.2.this.val$up_menu)
/*     */             {
/* 175 */               String configAutoKey = TransferSpeedValidator.getActiveAutoUploadParameter(SelectableSpeedMenu.2.this.val$globalManager);
/*     */               
/* 177 */               COConfigurationManager.setParameter(configAutoKey, false);
/*     */             }
/*     */             
/* 180 */             int cValue = ((Integer)new TransferSpeedValidator(SelectableSpeedMenu.2.this.val$configKey, new Integer(newSpeed)).getValue()).intValue();
/*     */             
/*     */ 
/* 183 */             COConfigurationManager.setParameter(SelectableSpeedMenu.2.this.val$configKey, cValue);
/*     */             
/* 185 */             COConfigurationManager.save();
/*     */           }
/*     */         });
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
/*     */   private static int adjustMaxBandWidth(int maxBandwidth, GlobalManager globalManager, boolean up_menu, int kInB)
/*     */   {
/* 200 */     if ((maxBandwidth == 0) && (!up_menu))
/*     */     {
/* 202 */       GlobalManagerStats stats = globalManager.getStats();
/* 203 */       int dataReceive = stats.getDataReceiveRate();
/* 204 */       if (dataReceive < kInB) {
/* 205 */         maxBandwidth = 275;
/*     */       } else {
/* 207 */         maxBandwidth = dataReceive / kInB;
/*     */       }
/*     */     }
/* 210 */     return maxBandwidth;
/*     */   }
/*     */   
/* 213 */   private static Map parseSpeedPartitionStringCache = new HashMap();
/*     */   
/* 215 */   private static synchronized Integer[] parseSpeedPartitionString(String s) { Integer[] result = (Integer[])parseSpeedPartitionStringCache.get(s);
/* 216 */     if (result == null) {
/* 217 */       try { result = parseSpeedPartitionString0(s);
/* 218 */       } catch (NumberFormatException nfe) { result = new Integer[0]; }
/* 219 */       parseSpeedPartitionStringCache.put(s, result);
/*     */     }
/* 221 */     if (result.length == 0) return null;
/* 222 */     return result;
/*     */   }
/*     */   
/*     */   private static Integer[] parseSpeedPartitionString0(String s) {
/* 226 */     StringTokenizer tokeniser = new StringTokenizer(s.trim(), ",");
/* 227 */     TreeSet values = new TreeSet();
/* 228 */     while (tokeniser.hasMoreTokens()) {
/* 229 */       values.add(new Integer(Integer.parseInt(tokeniser.nextToken().trim())));
/*     */     }
/* 231 */     return (Integer[])values.toArray(new Integer[values.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final Listener getLimitMenuItemListener(final boolean up_menu, Menu parent, final GlobalManager globalManager, final String configKey)
/*     */   {
/* 244 */     new Listener() {
/*     */       public void handleEvent(Event event) {
/* 246 */         MenuItem[] items = this.val$parent.getItems();
/* 247 */         for (int i = 0; i < items.length; i++) {
/* 248 */           if (items[i] == event.widget)
/*     */           {
/* 250 */             items[i].setSelection(true);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 255 */             if (up_menu)
/*     */             {
/* 257 */               String configAutoKey = TransferSpeedValidator.getActiveAutoUploadParameter(globalManager);
/*     */               
/*     */ 
/* 260 */               COConfigurationManager.setParameter(configAutoKey, false);
/*     */             }
/*     */             
/* 263 */             int cValue = ((Integer)new TransferSpeedValidator(configKey, (Number)items[i].getData("maxkb")).getValue()).intValue();
/* 264 */             COConfigurationManager.setParameter(configKey, cValue);
/*     */             
/*     */ 
/* 267 */             COConfigurationManager.save();
/*     */           }
/*     */           else {
/* 270 */             items[i].setSelection(false);
/*     */           }
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */   
/*     */ 
/*     */   public static Integer[] getGenericSpeedList(int speedPartitions, int maxBandwidth)
/*     */   {
/* 280 */     List l = new ArrayList();
/* 281 */     int delta = 0;
/* 282 */     int increaseLevel = 0;
/* 283 */     for (int i = 0; i < speedPartitions; i++) { int[] valuePair;
/*     */       int[] valuePair;
/* 285 */       if (delta == 0) {
/* 286 */         valuePair = new int[] { maxBandwidth };
/*     */       }
/*     */       else
/*     */       {
/* 290 */         valuePair = new int[] { maxBandwidth - delta * (maxBandwidth <= 1024 ? 1 : 1024), maxBandwidth + delta * (maxBandwidth < 1024 ? 1 : 1024) };
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 296 */       for (int j = 0; j < valuePair.length; j++) {
/* 297 */         if (j == 0) {
/* 298 */           l.add(0, new Integer(valuePair[j]));
/*     */         } else {
/* 300 */           l.add(new Integer(valuePair[j]));
/*     */         }
/*     */       }
/*     */       
/* 304 */       delta += increases[increaseLevel];
/* 305 */       if (increaseLevel < increases.length - 1) {
/* 306 */         increaseLevel++;
/*     */       }
/*     */     }
/* 309 */     return (Integer[])l.toArray(new Integer[l.size()]);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void invokeSlider(Control cClickedFrom, AzureusCore core, boolean isUpSpeed)
/*     */   {
/* 317 */     String prefix = MessageText.getString(isUpSpeed ? "GeneralView.label.maxuploadspeed" : "GeneralView.label.maxdownloadspeed");
/*     */     
/*     */ 
/*     */ 
/* 321 */     GlobalManager gm = core.getGlobalManager();
/*     */     
/* 323 */     String configAutoKey = TransferSpeedValidator.getActiveAutoUploadParameter(gm);
/* 324 */     boolean auto = COConfigurationManager.getBooleanParameter(configAutoKey);
/*     */     
/* 326 */     String configKey = isUpSpeed ? TransferSpeedValidator.getActiveUploadParameter(gm) : "Max Download Speed KBs";
/*     */     
/*     */ 
/* 329 */     int maxBandwidth = COConfigurationManager.getIntParameter(configKey);
/* 330 */     boolean unlim = maxBandwidth == 0;
/* 331 */     if ((unlim) && (!isUpSpeed)) {
/* 332 */       GlobalManagerStats stats = gm.getStats();
/* 333 */       int dataReceive = stats.getDataReceiveRate();
/* 334 */       if (dataReceive >= 1024) {
/* 335 */         maxBandwidth = dataReceive / 1024;
/*     */       }
/*     */     }
/*     */     
/* 339 */     SpeedScaleShell speedScale = new SpeedScaleShell() {
/*     */       public String getStringValue(int value, String sValue) {
/* 341 */         if (sValue != null) {
/* 342 */           return this.val$prefix + ": " + sValue;
/*     */         }
/* 344 */         if (value == 0) {
/* 345 */           return MessageText.getString("MyTorrentsView.menu.setSpeed.unlimited");
/*     */         }
/* 347 */         if (value == -1) {
/* 348 */           return MessageText.getString("ConfigView.auto");
/*     */         }
/* 350 */         return this.val$prefix + ": " + DisplayFormatters.formatByteCountToKiBEtcPerSec(getValue() * 1024, true);
/*     */       }
/*     */       
/*     */ 
/* 354 */     };
/* 355 */     int max = unlim ? 800 : isUpSpeed ? 100 : maxBandwidth * 5;
/* 356 */     if (max < 50) {
/* 357 */       max = 50;
/*     */     }
/* 359 */     speedScale.setMaxValue(max);
/* 360 */     speedScale.setMaxTextValue(9999999);
/*     */     
/* 362 */     String config_prefix = "config.ui.speed.partitions.manual." + (isUpSpeed ? "upload" : "download") + ".";
/*     */     
/* 364 */     int lastValue = COConfigurationManager.getIntParameter(config_prefix + "last", -10);
/*     */     
/*     */     Integer[] speed_limits;
/*     */     Integer[] speed_limits;
/* 368 */     if (COConfigurationManager.getBooleanParameter(config_prefix + "enabled", false))
/*     */     {
/* 370 */       speed_limits = parseSpeedPartitionString(COConfigurationManager.getStringParameter(config_prefix + "values", ""));
/*     */     }
/*     */     else {
/* 373 */       speed_limits = getGenericSpeedList(6, maxBandwidth);
/*     */     }
/* 375 */     if (speed_limits != null) {
/* 376 */       for (int i = 0; i < speed_limits.length; i++) {
/* 377 */         int value = speed_limits[i].intValue();
/* 378 */         if (value > 0) {
/* 379 */           speedScale.addOption(DisplayFormatters.formatByteCountToKiBEtcPerSec(value * 1024, true), value);
/*     */           
/* 381 */           if (value == lastValue) {
/* 382 */             lastValue = -10;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 387 */     speedScale.addOption(MessageText.getString("MyTorrentsView.menu.setSpeed.unlimited"), 0);
/*     */     
/* 389 */     speedScale.addOption(MessageText.getString("ConfigView.auto"), -1);
/*     */     
/* 391 */     if (lastValue > 0) {
/* 392 */       speedScale.addOption(DisplayFormatters.formatByteCountToKiBEtcPerSec(lastValue * 1024, true), lastValue);
/*     */     }
/*     */     
/*     */ 
/* 396 */     if (speedScale.open(cClickedFrom, auto ? -1 : maxBandwidth, true)) {
/* 397 */       int value = speedScale.getValue();
/*     */       
/* 399 */       if ((!speedScale.wasMenuChosen()) || (lastValue == value)) {
/* 400 */         COConfigurationManager.setParameter(config_prefix + "last", maxBandwidth);
/*     */       }
/*     */       
/*     */ 
/* 404 */       if (value >= 0) {
/* 405 */         if (auto) {
/* 406 */           COConfigurationManager.setParameter(configAutoKey, false);
/*     */         }
/* 408 */         COConfigurationManager.setParameter(configKey, value);
/* 409 */         COConfigurationManager.save();
/*     */       }
/*     */       else {
/* 412 */         COConfigurationManager.setParameter(configAutoKey, true);
/* 413 */         COConfigurationManager.save();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static void invokeSlider(Control cClickedFrom, AzureusCore core, DownloadManager[] dms, boolean isUpSpeed, Shell parentShell)
/*     */   {
/* 420 */     String prefix = MessageText.getString(isUpSpeed ? "GeneralView.label.maxuploadspeed" : "GeneralView.label.maxdownloadspeed");
/*     */     
/*     */ 
/*     */ 
/* 424 */     GlobalManager gm = core.getGlobalManager();
/*     */     
/* 426 */     final int kInB = DisplayFormatters.getKinB();
/*     */     
/* 428 */     int maxBandwidth = 0;
/* 429 */     for (DownloadManager dm : dms) {
/* 430 */       int bandwidth = (isUpSpeed ? dm.getStats().getUploadRateLimitBytesPerSecond() : dm.getStats().getDownloadRateLimitBytesPerSecond()) / kInB;
/*     */       
/*     */ 
/* 433 */       if ((bandwidth > maxBandwidth) || (bandwidth == 0)) {
/* 434 */         maxBandwidth = bandwidth;
/*     */       }
/*     */     }
/* 437 */     boolean unlim = maxBandwidth == 0;
/* 438 */     final int num_entries = dms.length;
/*     */     
/* 440 */     SpeedScaleShell speedScale = new SpeedScaleShell() {
/*     */       public String getStringValue(int value, String sValue) {
/* 442 */         if (sValue != null) {
/* 443 */           return this.val$prefix + ": " + sValue;
/*     */         }
/* 445 */         if (value == 0) {
/* 446 */           return MessageText.getString("MyTorrentsView.menu.setSpeed.unlimited");
/*     */         }
/* 448 */         if (value == -1) {
/* 449 */           return MessageText.getString("ConfigView.auto");
/*     */         }
/*     */         
/* 452 */         String speed = DisplayFormatters.formatByteCountToKiBEtcPerSec(value * kInB, true);
/*     */         
/* 454 */         if (num_entries > 1) {
/* 455 */           speed = MessageText.getString("MyTorrentsView.menu.setSpeed.multi", new String[] { DisplayFormatters.formatByteCountToKiBEtcPerSec(value * kInB * num_entries), String.valueOf(num_entries), speed });
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 465 */         return this.val$prefix + ": " + speed;
/*     */       }
/* 467 */     };
/* 468 */     int max = unlim ? 800 : isUpSpeed ? 100 : maxBandwidth * 5;
/* 469 */     if (max < 50) {
/* 470 */       max = 50;
/*     */     }
/* 472 */     speedScale.setMaxValue(max);
/* 473 */     speedScale.setMaxTextValue(9999999);
/* 474 */     speedScale.setParentShell(parentShell);
/*     */     
/* 476 */     String config_prefix = "config.ui.speed.partitions.manual." + (isUpSpeed ? "upload" : "download") + ".";
/*     */     
/* 478 */     int lastValue = COConfigurationManager.getIntParameter(config_prefix + "last", -10);
/*     */     
/*     */     Integer[] speed_limits;
/*     */     Integer[] speed_limits;
/* 482 */     if (COConfigurationManager.getBooleanParameter(config_prefix + "enabled", false))
/*     */     {
/* 484 */       speed_limits = parseSpeedPartitionString(COConfigurationManager.getStringParameter(config_prefix + "values", ""));
/*     */     }
/*     */     else {
/* 487 */       speed_limits = getGenericSpeedList(6, maxBandwidth);
/*     */     }
/* 489 */     if (speed_limits != null) {
/* 490 */       for (int i = 0; i < speed_limits.length; i++) {
/* 491 */         int value = speed_limits[i].intValue();
/* 492 */         if (value > 0) {
/* 493 */           int total = value * num_entries;
/* 494 */           String speed = DisplayFormatters.formatByteCountToKiBEtcPerSec(total * kInB, true);
/*     */           
/* 496 */           if (num_entries > 1) {
/* 497 */             speed = MessageText.getString("MyTorrentsView.menu.setSpeed.multi", new String[] { speed, String.valueOf(num_entries), DisplayFormatters.formatByteCountToKiBEtcPerSec(value * kInB) });
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 505 */           speedScale.addOption(speed, value);
/* 506 */           if (value == lastValue) {
/* 507 */             lastValue = -10;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 512 */     speedScale.addOption(MessageText.getString("MyTorrentsView.menu.setSpeed.unlimited"), 0);
/*     */     
/*     */ 
/* 515 */     if (lastValue > 0) {
/* 516 */       speedScale.addOption(DisplayFormatters.formatByteCountToKiBEtcPerSec(lastValue * kInB, true), lastValue);
/*     */     }
/*     */     
/*     */ 
/* 520 */     if (speedScale.open(cClickedFrom, maxBandwidth, true)) {
/* 521 */       int value = speedScale.getValue();
/*     */       
/* 523 */       if ((!speedScale.wasMenuChosen()) || (lastValue == value)) {
/* 524 */         COConfigurationManager.setParameter(config_prefix + "last", maxBandwidth);
/*     */       }
/*     */       
/*     */ 
/* 528 */       if (value >= 0) {
/* 529 */         for (DownloadManager dm : dms) {
/* 530 */           if (isUpSpeed) {
/* 531 */             dm.getStats().setUploadRateLimitBytesPerSecond(value * kInB);
/*     */           } else {
/* 533 */             dm.getStats().setDownloadRateLimitBytesPerSecond(value * kInB);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/SelectableSpeedMenu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */