/*     */ package org.gudy.azureus2.ui.swt.views;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.impl.CoreTableColumn;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.eclipse.swt.custom.ScrolledComposite;
/*     */ import org.eclipse.swt.events.SelectionAdapter;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MessageBox;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiver;
/*     */ import org.gudy.azureus2.plugins.ui.UIInputReceiverListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.SimpleTextEntryWindow;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class ViewUtils
/*     */ {
/*  66 */   private static SimpleDateFormat formatOverride = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String SM_PROP_PERMIT_UPLOAD_DISABLE = "enable_upload_disable";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static final String SM_PROP_PERMIT_DOWNLOAD_DISABLE = "enable_download_disable";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static final Map<String, Object> SM_DEFAULTS;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String formatETA(long value, boolean absolute, SimpleDateFormat override)
/*     */   {
/* 103 */     SimpleDateFormat df = override != null ? override : formatOverride;
/*     */     
/* 105 */     if ((absolute) && (df != null) && (value > 0L) && (value != 31536000L) && (value < 1827387392L))
/*     */     {
/*     */ 
/*     */       try
/*     */       {
/*     */ 
/* 111 */         return df.format(new Date(SystemTime.getCurrentTime() + 1000L * value));
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/* 117 */     return DisplayFormatters.formatETA(value, absolute);
/*     */   }
/*     */   
/*     */ 
/*     */   public static class CustomDateFormat
/*     */   {
/*     */     private CoreTableColumn column;
/*     */     
/*     */     private TableContextMenuItem custom_date_menu;
/*     */     
/*     */     private SimpleDateFormat custom_date_format;
/*     */     
/*     */ 
/*     */     private CustomDateFormat(CoreTableColumn _column)
/*     */     {
/* 132 */       this.column = _column;
/*     */       
/* 134 */       this.custom_date_menu = this.column.addContextMenuItem("label.date.format", 1);
/*     */       
/* 136 */       this.custom_date_menu.setStyle(1);
/*     */       
/* 138 */       this.custom_date_menu.addListener(new MenuItemListener()
/*     */       {
/*     */         public void selected(org.gudy.azureus2.plugins.ui.menus.MenuItem menu, Object target) {
/* 141 */           Object existing_o = ViewUtils.CustomDateFormat.this.column.getUserData("CustomDate");
/*     */           
/* 143 */           String existing_text = "";
/*     */           
/* 145 */           if ((existing_o instanceof String)) {
/* 146 */             existing_text = (String)existing_o;
/* 147 */           } else if ((existing_o instanceof byte[])) {
/*     */             try {
/* 149 */               existing_text = new String((byte[])existing_o, "UTF-8");
/*     */             }
/*     */             catch (Throwable e) {}
/*     */           }
/* 153 */           SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow("ConfigView.section.style.customDateFormat", "label.date.format");
/*     */           
/*     */ 
/*     */ 
/* 157 */           entryWindow.setPreenteredText(existing_text, false);
/*     */           
/* 159 */           entryWindow.prompt(new UIInputReceiverListener() {
/*     */             public void UIInputReceiverClosed(UIInputReceiver entryWindow) {
/* 161 */               if (!entryWindow.hasSubmittedInput()) {
/* 162 */                 return;
/*     */               }
/* 164 */               String date_format = entryWindow.getSubmittedInput();
/*     */               
/* 166 */               if (date_format == null) {
/* 167 */                 return;
/*     */               }
/*     */               
/* 170 */               date_format = date_format.trim();
/*     */               
/* 172 */               ViewUtils.CustomDateFormat.this.column.setUserData("CustomDate", date_format);
/*     */               
/* 174 */               ViewUtils.CustomDateFormat.this.column.invalidateCells();
/*     */               
/* 176 */               ViewUtils.CustomDateFormat.this.update();
/*     */             }
/*     */           });
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*     */ 
/*     */     public void update()
/*     */     {
/* 186 */       Object cd = this.column.getUserData("CustomDate");
/*     */       
/* 188 */       String format = null;
/*     */       
/* 190 */       if ((cd instanceof byte[])) {
/*     */         try
/*     */         {
/* 193 */           cd = new String((byte[])cd, "UTF-8");
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 200 */       if ((cd instanceof String))
/*     */       {
/* 202 */         String str = (String)cd;
/*     */         
/* 204 */         str = str.trim();
/*     */         
/* 206 */         if (str.length() > 0)
/*     */         {
/* 208 */           format = str;
/*     */         }
/*     */       }
/*     */       
/* 212 */       if (format == null)
/*     */       {
/* 214 */         format = MessageText.getString("label.table.default");
/*     */         
/* 216 */         this.custom_date_format = null;
/*     */       }
/*     */       else
/*     */       {
/*     */         try {
/* 221 */           this.custom_date_format = new SimpleDateFormat(format);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 225 */           Debug.out(e);
/*     */         }
/*     */       }
/*     */       
/* 229 */       this.custom_date_menu.setText(MessageText.getString("label.date.format") + " <" + format + "> ...");
/*     */     }
/*     */     
/*     */ 
/*     */     public SimpleDateFormat getDateFormat()
/*     */     {
/* 235 */       return this.custom_date_format;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static CustomDateFormat addCustomDateFormat(CoreTableColumn column)
/*     */   {
/* 243 */     return new CustomDateFormat(column, null);
/*     */   }
/*     */   
/*     */   static
/*     */   {
/*  69 */     COConfigurationManager.addAndFireParameterListener("Table.column.dateformat", new ParameterListener()
/*     */     {
/*     */       public void parameterChanged(String parameterName) {
/*  72 */         String temp = COConfigurationManager.getStringParameter("Table.column.dateformat", "");
/*     */         
/*     */ 
/*  75 */         if ((temp == null) || (temp.trim().length() == 0))
/*     */         {
/*  77 */           ViewUtils.access$002(null);
/*     */         }
/*     */         else {
/*     */           try
/*     */           {
/*  82 */             SimpleDateFormat format = new SimpleDateFormat(temp.trim());
/*     */             
/*  84 */             format.format(new Date());
/*     */             
/*  86 */             ViewUtils.access$002(format);
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/*  90 */             ViewUtils.access$002(null);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           }
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
/*     */         }
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
/* 248 */     });
/* 249 */     SM_DEFAULTS = new HashMap();
/*     */     
/*     */ 
/* 252 */     SM_DEFAULTS.put("enable_upload_disable", Boolean.valueOf(false));
/* 253 */     SM_DEFAULTS.put("enable_download_disable", Boolean.valueOf(false));
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
/*     */   public static void addSpeedMenu(Shell shell, Menu menuAdvanced, boolean doUpMenu, boolean doDownMenu, boolean isTorrentContext, boolean hasSelection, boolean downSpeedDisabled, boolean downSpeedUnlimited, long totalDownSpeed, long downSpeedSetMax, long maxDownload, boolean upSpeedDisabled, boolean upSpeedUnlimited, long totalUpSpeed, long upSpeedSetMax, long maxUpload, int num_entries, Map<String, Object> _properties, SpeedAdapter adapter)
/*     */   {
/* 278 */     if (doDownMenu)
/*     */     {
/* 280 */       org.eclipse.swt.widgets.MenuItem itemDownSpeed = new org.eclipse.swt.widgets.MenuItem(menuAdvanced, 64);
/* 281 */       Messages.setLanguageText(itemDownSpeed, "MyTorrentsView.menu.setDownSpeed");
/*     */       
/* 283 */       Utils.setMenuItemImage(itemDownSpeed, "speed");
/*     */       
/* 285 */       Menu menuDownSpeed = new Menu(shell, 4);
/* 286 */       itemDownSpeed.setMenu(menuDownSpeed);
/*     */       
/* 288 */       addSpeedMenuDown(shell, menuDownSpeed, isTorrentContext, hasSelection, downSpeedDisabled, downSpeedUnlimited, totalDownSpeed, downSpeedSetMax, maxDownload, num_entries, _properties, adapter);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 293 */     if (doUpMenu)
/*     */     {
/* 295 */       org.eclipse.swt.widgets.MenuItem itemUpSpeed = new org.eclipse.swt.widgets.MenuItem(menuAdvanced, 64);
/* 296 */       Messages.setLanguageText(itemUpSpeed, "MyTorrentsView.menu.setUpSpeed");
/* 297 */       Utils.setMenuItemImage(itemUpSpeed, "speed");
/*     */       
/* 299 */       Menu menuUpSpeed = new Menu(shell, 4);
/* 300 */       itemUpSpeed.setMenu(menuUpSpeed);
/* 301 */       addSpeedMenuUp(shell, menuUpSpeed, isTorrentContext, hasSelection, upSpeedDisabled, upSpeedUnlimited, totalUpSpeed, upSpeedSetMax, maxUpload, num_entries, _properties, adapter);
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addSpeedMenuUp(Shell shell, Menu menuSpeed, boolean isTorrentContext, boolean hasSelection, boolean upSpeedDisabled, boolean upSpeedUnlimited, long totalUpSpeed, long upSpeedSetMax, long maxUpload, final int num_entries, Map<String, Object> _properties, final SpeedAdapter adapter)
/*     */   {
/* 322 */     Map<String, Object> properties = new HashMap(SM_DEFAULTS);
/* 323 */     if (_properties != null) {
/* 324 */       properties.putAll(_properties);
/*     */     }
/*     */     
/* 327 */     String menu_key = "MyTorrentsView.menu.manual";
/* 328 */     if (num_entries > 1) { menu_key = menu_key + (isTorrentContext ? ".per_torrent" : ".per_peer");
/*     */     }
/* 330 */     if (menuSpeed != null)
/*     */     {
/* 332 */       org.eclipse.swt.widgets.MenuItem itemCurrentUpSpeed = new org.eclipse.swt.widgets.MenuItem(menuSpeed, 8);
/* 333 */       itemCurrentUpSpeed.setEnabled(false);
/* 334 */       String separator = "";
/* 335 */       StringBuilder speedText = new StringBuilder();
/*     */       
/* 337 */       if (upSpeedDisabled) {
/* 338 */         speedText.append(MessageText.getString("MyTorrentsView.menu.setSpeed.disabled"));
/*     */         
/* 340 */         separator = " / ";
/*     */       }
/* 342 */       if (upSpeedUnlimited) {
/* 343 */         speedText.append(separator);
/* 344 */         speedText.append(MessageText.getString("MyTorrentsView.menu.setSpeed.unlimited"));
/*     */         
/* 346 */         separator = " / ";
/*     */       }
/* 348 */       if (totalUpSpeed > 0L) {
/* 349 */         speedText.append(separator);
/* 350 */         speedText.append(DisplayFormatters.formatByteCountToKiBEtcPerSec(totalUpSpeed));
/*     */       }
/*     */       
/* 353 */       itemCurrentUpSpeed.setText(speedText.toString());
/*     */       
/*     */ 
/* 356 */       new org.eclipse.swt.widgets.MenuItem(menuSpeed, 2);
/*     */       
/* 358 */       Listener itemsUpSpeedListener = new Listener() {
/*     */         public void handleEvent(Event e) {
/* 360 */           if ((e.widget != null) && ((e.widget instanceof org.eclipse.swt.widgets.MenuItem))) {
/* 361 */             org.eclipse.swt.widgets.MenuItem item = (org.eclipse.swt.widgets.MenuItem)e.widget;
/* 362 */             int speed = item.getData("maxul") == null ? 0 : ((Integer)item.getData("maxul")).intValue();
/*     */             
/* 364 */             this.val$adapter.setUpSpeed(speed);
/*     */           }
/*     */         }
/*     */       };
/*     */       
/* 369 */       if ((num_entries > 1) || (!upSpeedUnlimited)) {
/* 370 */         org.eclipse.swt.widgets.MenuItem mi = new org.eclipse.swt.widgets.MenuItem(menuSpeed, 8);
/* 371 */         Messages.setLanguageText(mi, "MyTorrentsView.menu.setSpeed.unlimit");
/*     */         
/* 373 */         mi.setData("maxul", new Integer(0));
/* 374 */         mi.addListener(13, itemsUpSpeedListener);
/*     */       }
/*     */       
/* 377 */       boolean allowDisable = ((Boolean)properties.get("enable_upload_disable")).booleanValue();
/*     */       
/* 379 */       if ((allowDisable) && (!upSpeedDisabled)) {
/* 380 */         org.eclipse.swt.widgets.MenuItem mi = new org.eclipse.swt.widgets.MenuItem(menuSpeed, 8);
/* 381 */         Messages.setLanguageText(mi, "MyTorrentsView.menu.setSpeed.disable");
/*     */         
/* 383 */         mi.setData("maxul", new Integer(-1));
/* 384 */         mi.addListener(13, itemsUpSpeedListener);
/*     */       }
/*     */       
/* 387 */       int kInB = DisplayFormatters.getKinB();
/*     */       
/* 389 */       if (hasSelection)
/*     */       {
/* 391 */         if (maxUpload == 0L) {
/* 392 */           maxUpload = 75 * kInB;
/*     */         }
/* 394 */         else if (upSpeedSetMax <= 0L) {
/* 395 */           maxUpload = 200 * kInB;
/*     */         } else {
/* 397 */           maxUpload = 4L * (upSpeedSetMax / kInB) * kInB;
/*     */         }
/*     */         
/* 400 */         for (int i = 0; i < 10; i++) {
/* 401 */           org.eclipse.swt.widgets.MenuItem mi = new org.eclipse.swt.widgets.MenuItem(menuSpeed, 8);
/* 402 */           mi.addListener(13, itemsUpSpeedListener);
/*     */           
/* 404 */           int limit = (int)(maxUpload / (10 * num_entries) * (10 - i));
/* 405 */           String speed = DisplayFormatters.formatByteCountToKiBEtcPerSec(limit * num_entries);
/*     */           
/* 407 */           if (num_entries > 1) {
/* 408 */             speed = MessageText.getString("MyTorrentsView.menu.setSpeed.multi", new String[] { speed, String.valueOf(num_entries), DisplayFormatters.formatByteCountToKiBEtcPerSec(limit) });
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 416 */           mi.setText(speed);
/* 417 */           mi.setData("maxul", new Integer(limit));
/*     */         }
/*     */       }
/*     */       
/* 421 */       new org.eclipse.swt.widgets.MenuItem(menuSpeed, 2);
/*     */       
/* 423 */       org.eclipse.swt.widgets.MenuItem itemUpSpeedManualSingle = new org.eclipse.swt.widgets.MenuItem(menuSpeed, 8);
/* 424 */       Messages.setLanguageText(itemUpSpeedManualSingle, menu_key);
/* 425 */       itemUpSpeedManualSingle.addSelectionListener(new SelectionAdapter() {
/*     */         public void widgetSelected(SelectionEvent e) {
/* 427 */           int speed_value = ViewUtils.getManualSpeedValue(this.val$shell, false);
/* 428 */           if (speed_value > 0) { adapter.setUpSpeed(speed_value);
/*     */           }
/*     */         }
/*     */       });
/* 432 */       if (num_entries > 1) {
/* 433 */         org.eclipse.swt.widgets.MenuItem itemUpSpeedManualShared = new org.eclipse.swt.widgets.MenuItem(menuSpeed, 8);
/* 434 */         Messages.setLanguageText(itemUpSpeedManualShared, isTorrentContext ? "MyTorrentsView.menu.manual.shared_torrents" : "MyTorrentsView.menu.manual.shared_peers");
/* 435 */         itemUpSpeedManualShared.addSelectionListener(new SelectionAdapter() {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 437 */             int speed_value = ViewUtils.getManualSharedSpeedValue(this.val$shell, false, num_entries);
/* 438 */             if (speed_value > 0) { adapter.setUpSpeed(speed_value);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addSpeedMenuDown(Shell shell, Menu menuSpeed, boolean isTorrentContext, boolean hasSelection, boolean downSpeedDisabled, boolean downSpeedUnlimited, long totalDownSpeed, long downSpeedSetMax, long maxDownload, final int num_entries, Map<String, Object> _properties, final SpeedAdapter adapter)
/*     */   {
/* 460 */     Map<String, Object> properties = new HashMap(SM_DEFAULTS);
/* 461 */     if (_properties != null) {
/* 462 */       properties.putAll(_properties);
/*     */     }
/*     */     
/* 465 */     String menu_key = "MyTorrentsView.menu.manual";
/* 466 */     if (num_entries > 1) { menu_key = menu_key + (isTorrentContext ? ".per_torrent" : ".per_peer");
/*     */     }
/* 468 */     if (menuSpeed != null) {
/* 469 */       org.eclipse.swt.widgets.MenuItem itemCurrentDownSpeed = new org.eclipse.swt.widgets.MenuItem(menuSpeed, 8);
/* 470 */       itemCurrentDownSpeed.setEnabled(false);
/* 471 */       StringBuilder speedText = new StringBuilder();
/* 472 */       String separator = "";
/*     */       
/* 474 */       if (downSpeedDisabled) {
/* 475 */         speedText.append(MessageText.getString("MyTorrentsView.menu.setSpeed.disabled"));
/*     */         
/* 477 */         separator = " / ";
/*     */       }
/* 479 */       if (downSpeedUnlimited) {
/* 480 */         speedText.append(separator);
/* 481 */         speedText.append(MessageText.getString("MyTorrentsView.menu.setSpeed.unlimited"));
/*     */         
/* 483 */         separator = " / ";
/*     */       }
/* 485 */       if (totalDownSpeed > 0L) {
/* 486 */         speedText.append(separator);
/* 487 */         speedText.append(DisplayFormatters.formatByteCountToKiBEtcPerSec(totalDownSpeed));
/*     */       }
/*     */       
/* 490 */       itemCurrentDownSpeed.setText(speedText.toString());
/*     */       
/* 492 */       new org.eclipse.swt.widgets.MenuItem(menuSpeed, 2);
/*     */       
/* 494 */       Listener itemsDownSpeedListener = new Listener() {
/*     */         public void handleEvent(Event e) {
/* 496 */           if ((e.widget != null) && ((e.widget instanceof org.eclipse.swt.widgets.MenuItem))) {
/* 497 */             org.eclipse.swt.widgets.MenuItem item = (org.eclipse.swt.widgets.MenuItem)e.widget;
/* 498 */             int speed = item.getData("maxdl") == null ? 0 : ((Integer)item.getData("maxdl")).intValue();
/*     */             
/* 500 */             this.val$adapter.setDownSpeed(speed);
/*     */           }
/*     */         }
/*     */       };
/*     */       
/* 505 */       if ((num_entries > 1) || (!downSpeedUnlimited)) {
/* 506 */         org.eclipse.swt.widgets.MenuItem mi = new org.eclipse.swt.widgets.MenuItem(menuSpeed, 8);
/* 507 */         Messages.setLanguageText(mi, "MyTorrentsView.menu.setSpeed.unlimit");
/*     */         
/* 509 */         mi.setData("maxdl", new Integer(0));
/* 510 */         mi.addListener(13, itemsDownSpeedListener);
/*     */       }
/*     */       
/* 513 */       boolean allowDisable = ((Boolean)properties.get("enable_download_disable")).booleanValue();
/*     */       
/* 515 */       if ((allowDisable) && (!downSpeedDisabled)) {
/* 516 */         org.eclipse.swt.widgets.MenuItem mi = new org.eclipse.swt.widgets.MenuItem(menuSpeed, 8);
/* 517 */         Messages.setLanguageText(mi, "MyTorrentsView.menu.setSpeed.down.disable");
/*     */         
/* 519 */         mi.setData("maxdl", new Integer(-1));
/* 520 */         mi.addListener(13, itemsDownSpeedListener);
/*     */       }
/*     */       
/* 523 */       if (hasSelection)
/*     */       {
/*     */ 
/*     */ 
/* 527 */         int kInB = DisplayFormatters.getKinB();
/*     */         
/* 529 */         if (maxDownload == 0L) {
/* 530 */           if (downSpeedSetMax <= 0L) {
/* 531 */             maxDownload = 200 * kInB;
/*     */           } else {
/* 533 */             maxDownload = 4L * (downSpeedSetMax / kInB) * kInB;
/*     */           }
/*     */         }
/*     */         
/* 537 */         for (int i = 0; i < 10; i++) {
/* 538 */           org.eclipse.swt.widgets.MenuItem mi = new org.eclipse.swt.widgets.MenuItem(menuSpeed, 8);
/* 539 */           mi.addListener(13, itemsDownSpeedListener);
/*     */           
/*     */ 
/* 542 */           int limit = (int)(maxDownload / (10 * num_entries) * (10 - i));
/* 543 */           String speed = DisplayFormatters.formatByteCountToKiBEtcPerSec(limit * num_entries);
/*     */           
/* 545 */           if (num_entries > 1) {
/* 546 */             speed = MessageText.getString("MyTorrentsView.menu.setSpeed.multi", new String[] { speed, String.valueOf(num_entries), DisplayFormatters.formatByteCountToKiBEtcPerSec(limit) });
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 552 */           mi.setText(speed);
/* 553 */           mi.setData("maxdl", new Integer(limit));
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 558 */       new org.eclipse.swt.widgets.MenuItem(menuSpeed, 2);
/*     */       
/* 560 */       org.eclipse.swt.widgets.MenuItem itemDownSpeedManualSingle = new org.eclipse.swt.widgets.MenuItem(menuSpeed, 8);
/* 561 */       Messages.setLanguageText(itemDownSpeedManualSingle, menu_key);
/* 562 */       itemDownSpeedManualSingle.addSelectionListener(new SelectionAdapter() {
/*     */         public void widgetSelected(SelectionEvent e) {
/* 564 */           int speed_value = ViewUtils.getManualSpeedValue(this.val$shell, true);
/* 565 */           if (speed_value > 0) { adapter.setDownSpeed(speed_value);
/*     */           }
/*     */         }
/*     */       });
/* 569 */       if (num_entries > 1) {
/* 570 */         org.eclipse.swt.widgets.MenuItem itemDownSpeedManualShared = new org.eclipse.swt.widgets.MenuItem(menuSpeed, 8);
/* 571 */         Messages.setLanguageText(itemDownSpeedManualShared, isTorrentContext ? "MyTorrentsView.menu.manual.shared_torrents" : "MyTorrentsView.menu.manual.shared_peers");
/* 572 */         itemDownSpeedManualShared.addSelectionListener(new SelectionAdapter() {
/*     */           public void widgetSelected(SelectionEvent e) {
/* 574 */             int speed_value = ViewUtils.getManualSharedSpeedValue(this.val$shell, true, num_entries);
/* 575 */             if (speed_value > 0) adapter.setDownSpeed(speed_value);
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static int getManualSpeedValue(Shell shell, boolean for_download) {
/* 583 */     String kbps_str = MessageText.getString("MyTorrentsView.dialog.setNumber.inKbps", new String[] { DisplayFormatters.getRateUnit(1) });
/*     */     
/*     */ 
/* 586 */     String set_num_str = MessageText.getString("MyTorrentsView.dialog.setNumber." + (for_download ? "download" : "upload"));
/*     */     
/*     */ 
/* 589 */     SimpleTextEntryWindow entryWindow = new SimpleTextEntryWindow();
/* 590 */     entryWindow.initTexts("MyTorrentsView.dialog.setSpeed.title", new String[] { set_num_str }, "MyTorrentsView.dialog.setNumber.text", new String[] { kbps_str, set_num_str });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 599 */     entryWindow.prompt();
/* 600 */     if (!entryWindow.hasSubmittedInput()) {
/* 601 */       return -1;
/*     */     }
/* 603 */     String sReturn = entryWindow.getSubmittedInput();
/*     */     
/* 605 */     if (sReturn == null) {
/* 606 */       return -1;
/*     */     }
/*     */     try {
/* 609 */       int result = (int)(Double.valueOf(sReturn).doubleValue() * DisplayFormatters.getKinB());
/*     */       
/* 611 */       if (DisplayFormatters.isRateUsingBits())
/*     */       {
/* 613 */         result /= 8;
/*     */       }
/*     */       
/* 616 */       if (result <= 0) throw new NumberFormatException("non-positive number entered");
/* 617 */       return result;
/*     */     } catch (NumberFormatException er) {
/* 619 */       MessageBox mb = new MessageBox(shell, 33);
/* 620 */       mb.setText(MessageText.getString("MyTorrentsView.dialog.NumberError.title"));
/*     */       
/* 622 */       mb.setMessage(MessageText.getString("MyTorrentsView.dialog.NumberError.text"));
/*     */       
/*     */ 
/* 625 */       mb.open(); }
/* 626 */     return -1;
/*     */   }
/*     */   
/*     */   public static int getManualSharedSpeedValue(Shell shell, boolean for_download, int num_entries)
/*     */   {
/* 631 */     int result = getManualSpeedValue(shell, for_download);
/* 632 */     if (result == -1) return -1;
/* 633 */     result /= num_entries;
/* 634 */     if (result == 0) result = 1;
/* 635 */     return result;
/*     */   }
/*     */   
/*     */   public static void setViewRequiresOneDownload(Composite genComposite) {
/* 639 */     if ((genComposite == null) || (genComposite.isDisposed())) {
/* 640 */       return;
/*     */     }
/* 642 */     Utils.disposeComposite(genComposite, false);
/*     */     
/* 644 */     Label lab = new Label(genComposite, 0);
/* 645 */     GridData gridData = new GridData(16777216, 16777216, true, true);
/* 646 */     gridData.verticalIndent = 10;
/* 647 */     lab.setLayoutData(gridData);
/* 648 */     Messages.setLanguageText(lab, "view.one.download.only");
/*     */     
/* 650 */     genComposite.layout(true);
/*     */     
/* 652 */     Composite parent = genComposite.getParent();
/* 653 */     if ((parent instanceof ScrolledComposite)) {
/* 654 */       ScrolledComposite scrolled_comp = (ScrolledComposite)parent;
/*     */       
/* 656 */       Rectangle r = scrolled_comp.getClientArea();
/* 657 */       scrolled_comp.setMinSize(genComposite.computeSize(r.width, -1));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static DownloadManager getDownloadManagerFromDataSource(Object dataSource)
/*     */   {
/* 666 */     DownloadManager manager = null;
/* 667 */     if ((dataSource instanceof Object[])) {
/* 668 */       Object[] newDataSources = (Object[])dataSource;
/* 669 */       if (newDataSources.length == 1) {
/* 670 */         Object temp = ((Object[])(Object[])dataSource)[0];
/* 671 */         if ((temp instanceof DownloadManager)) {
/* 672 */           manager = (DownloadManager)temp;
/* 673 */         } else if ((temp instanceof DiskManagerFileInfo)) {
/* 674 */           manager = ((DiskManagerFileInfo)temp).getDownloadManager();
/*     */         }
/*     */       } else {
/* 677 */         for (Object o : newDataSources) {
/* 678 */           if ((o instanceof DownloadManager)) {
/* 679 */             if (manager == null) {
/* 680 */               manager = (DownloadManager)o;
/* 681 */             } else if (manager != o) {
/* 682 */               manager = null;
/* 683 */               break;
/*     */             }
/* 685 */           } else if ((o instanceof DiskManagerFileInfo)) {
/* 686 */             DownloadManager temp = ((DiskManagerFileInfo)o).getDownloadManager();
/* 687 */             if (manager == null) {
/* 688 */               manager = temp;
/* 689 */             } else if (manager != temp) {
/* 690 */               manager = null;
/* 691 */               break;
/*     */             }
/*     */           } else {
/* 694 */             manager = null;
/* 695 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 700 */     else if ((dataSource instanceof DownloadManager)) {
/* 701 */       manager = (DownloadManager)dataSource;
/* 702 */     } else if ((dataSource instanceof DiskManagerFileInfo)) {
/* 703 */       manager = ((DiskManagerFileInfo)dataSource).getDownloadManager();
/*     */     }
/*     */     
/* 706 */     return manager;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static List<DownloadManager> getDownloadManagersFromDataSource(Object dataSource)
/*     */   {
/* 713 */     Set<DownloadManager> managers = new HashSet();
/* 714 */     if ((dataSource instanceof Object[])) {
/* 715 */       Object[] newDataSources = (Object[])dataSource;
/* 716 */       if (newDataSources.length == 1) {
/* 717 */         Object temp = ((Object[])(Object[])dataSource)[0];
/* 718 */         if ((temp instanceof DownloadManager)) {
/* 719 */           managers.add((DownloadManager)temp);
/* 720 */         } else if ((temp instanceof DiskManagerFileInfo)) {
/* 721 */           managers.add(((DiskManagerFileInfo)temp).getDownloadManager());
/*     */         }
/*     */       } else {
/* 724 */         for (Object o : newDataSources) {
/* 725 */           if ((o instanceof DownloadManager)) {
/* 726 */             managers.add((DownloadManager)o);
/* 727 */           } else if ((o instanceof DiskManagerFileInfo)) {
/* 728 */             DownloadManager temp = ((DiskManagerFileInfo)o).getDownloadManager();
/* 729 */             managers.add(temp);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/* 734 */     else if ((dataSource instanceof DownloadManager)) {
/* 735 */       managers.add((DownloadManager)dataSource);
/* 736 */     } else if ((dataSource instanceof DiskManagerFileInfo)) {
/* 737 */       managers.add(((DiskManagerFileInfo)dataSource).getDownloadManager());
/*     */     }
/*     */     
/* 740 */     return new ArrayList(managers);
/*     */   }
/*     */   
/*     */   public static abstract interface SpeedAdapter
/*     */   {
/*     */     public abstract void setUpSpeed(int paramInt);
/*     */     
/*     */     public abstract void setDownSpeed(int paramInt);
/*     */   }
/*     */   
/*     */   public static abstract interface ViewTitleExtraInfo
/*     */   {
/*     */     public abstract void update(Composite paramComposite, int paramInt1, int paramInt2);
/*     */     
/*     */     public abstract void setEnabled(Composite paramComposite, boolean paramBoolean);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/ViewUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */