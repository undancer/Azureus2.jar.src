/*     */ package org.gudy.azureus2.core3.config.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.speedmanager.impl.v2.SpeedLimitConfidence;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager.ParameterVerifier;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.ConcurrentHashMapWrapper;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
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
/*     */ public class ConfigurationDefaults
/*     */ {
/*  63 */   private static final Long ZERO = new Long(0L);
/*  64 */   private static final Long ONE = new Long(1L);
/*  65 */   private static final Long SIXTY = new Long(60L);
/*     */   
/*  67 */   private static final Long FALSE = ZERO;
/*  68 */   private static final Long TRUE = ONE;
/*     */   
/*     */   private static ConfigurationDefaults configdefaults;
/*  71 */   private static final AEMonitor class_mon = new AEMonitor("ConfigDef");
/*     */   
/*  73 */   private ConcurrentHashMapWrapper<String, Object> def = null;
/*     */   
/*     */   public static final int def_int = 0;
/*     */   public static final long def_long = 0L;
/*     */   public static final float def_float = 0.0F;
/*     */   public static final int def_boolean = 0;
/*     */   public static final String def_String = "";
/*  80 */   public static final byte[] def_bytes = null;
/*     */   
/*     */ 
/*     */   public static final String DEFAULT_FILE_CONVERSION_CHARS = "\"='";
/*     */   
/*  85 */   private final Hashtable parameter_verifiers = new Hashtable();
/*     */   public static final String CFG_TORRENTADD_OPENOPTIONS_MANY = "many";
/*     */   public static final String CFG_TORRENTADD_OPENOPTIONS_ALWAYS = "always";
/*     */   public static final String CFG_TORRENTADD_OPENOPTIONS_NEVER = "never";
/*     */   public static final String CFG_TORRENTADD_OPENOPTIONS = "ui.addtorrent.openoptions";
/*     */   public static final String CFG_TORRENTADD_OPENOPTIONS_SEP = "ui.addtorrent.openoptions.sep";
/*     */   
/*     */   public static ConfigurationDefaults getInstance()
/*     */   {
/*     */     try
/*     */     {
/*  96 */       class_mon.enter();
/*     */       
/*  98 */       if (configdefaults == null) {
/*     */         try
/*     */         {
/* 101 */           configdefaults = new ConfigurationDefaults();
/*     */ 
/*     */ 
/*     */ 
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 111 */           System.out.println("Falling back to default defaults as environment is restricted");
/*     */           
/* 113 */           configdefaults = new ConfigurationDefaults(new HashMap());
/*     */         }
/*     */       }
/*     */       
/* 117 */       return configdefaults;
/*     */     }
/*     */     finally
/*     */     {
/* 121 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected ConfigurationDefaults()
/*     */   {
/* 129 */     this.def = new ConcurrentHashMapWrapper(2000, 0.75F, 8);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 134 */     this.def.put("Override Ip", "");
/* 135 */     this.def.put("Enable incremental file creation", FALSE);
/* 136 */     this.def.put("Enable reorder storage mode", FALSE);
/* 137 */     this.def.put("Reorder storage mode min MB", new Long(10L));
/*     */     
/* 139 */     this.def.put("TCP.Listen.Port", new Long(6881L));
/* 140 */     this.def.put("TCP.Listen.Port.Enable", TRUE);
/* 141 */     this.def.put("TCP.Listen.Port.Override", "");
/* 142 */     this.def.put("UDP.Listen.Port", new Long(6881L));
/* 143 */     this.def.put("UDP.Listen.Port.Enable", TRUE);
/* 144 */     this.def.put("UDP.NonData.Listen.Port", new Long(6881L));
/* 145 */     this.def.put("UDP.NonData.Listen.Port.Same", TRUE);
/* 146 */     this.def.put("HTTP.Data.Listen.Port", new Long(Constants.isWindows ? 80L : 8080L));
/* 147 */     this.def.put("HTTP.Data.Listen.Port.Override", ZERO);
/* 148 */     this.def.put("HTTP.Data.Listen.Port.Enable", FALSE);
/* 149 */     this.def.put("Listen.Port.Randomize.Enable", FALSE);
/* 150 */     this.def.put("Listen.Port.Randomize.Together", TRUE);
/* 151 */     this.def.put("Listen.Port.Randomize.Range", "10000-65535");
/* 152 */     this.def.put("webseed.activation.uses.availability", TRUE);
/* 153 */     this.def.put("IPV6 Enable Support", FALSE);
/* 154 */     this.def.put("IPV6 Prefer Addresses", FALSE);
/* 155 */     this.def.put("IPV4 Prefer Stack", FALSE);
/*     */     
/* 157 */     this.def.put("max active torrents", new Long(4L));
/* 158 */     this.def.put("max downloads", new Long(4L));
/* 159 */     this.def.put("min downloads", ONE);
/* 160 */     this.def.put("Newly Seeding Torrents Get First Priority", TRUE);
/*     */     
/* 162 */     this.def.put("Max.Peer.Connections.Per.Torrent", new Long(50L));
/* 163 */     this.def.put("Max.Peer.Connections.Per.Torrent.When.Seeding", new Long(25L));
/* 164 */     this.def.put("Max.Peer.Connections.Per.Torrent.When.Seeding.Enable", TRUE);
/* 165 */     this.def.put("Max.Peer.Connections.Total", new Long(250L));
/* 166 */     this.def.put("Non-Public Peer Extra Slots Per Torrent", Integer.valueOf(2));
/* 167 */     this.def.put("Non-Public Peer Extra Connections Per Torrent", Integer.valueOf(4));
/*     */     
/* 169 */     this.def.put("Peer.Fast.Initial.Unchoke.Enabled", FALSE);
/*     */     
/* 171 */     this.def.put("File Max Open", new Long(50L));
/* 172 */     this.def.put("Use Config File Backups", TRUE);
/*     */     
/* 174 */     this.def.put("Max Uploads", new Long(4L));
/* 175 */     this.def.put("Max Uploads Seeding", new Long(4L));
/* 176 */     this.def.put("enable.seedingonly.maxuploads", FALSE);
/* 177 */     this.def.put("max.uploads.when.busy.inc.min.secs", new Long(30L));
/* 178 */     this.def.put("Max Download Speed KBs", ZERO);
/* 179 */     this.def.put("Down Rate Limits Include Protocol", TRUE);
/* 180 */     this.def.put("Use Request Limiting", TRUE);
/* 181 */     this.def.put("Use Request Limiting Priorities", TRUE);
/* 182 */     this.def.put("Max Upload Speed KBs", ZERO);
/* 183 */     this.def.put("Max Upload Speed Seeding KBs", ZERO);
/* 184 */     this.def.put("Up Rate Limits Include Protocol", FALSE);
/* 185 */     this.def.put("enable.seedingonly.upload.rate", FALSE);
/* 186 */     this.def.put("Max Seeds Per Torrent", ZERO);
/*     */     
/*     */ 
/* 189 */     this.def.put("Auto Upload Speed Enabled", FALSE);
/* 190 */     this.def.put("Auto Upload Speed Seeding Enabled", FALSE);
/* 191 */     this.def.put("AutoSpeed Available", FALSE);
/* 192 */     this.def.put("AutoSpeed Min Upload KBs", ZERO);
/* 193 */     this.def.put("AutoSpeed Max Upload KBs", ZERO);
/* 194 */     this.def.put("AutoSpeed Max Increment KBs", ONE);
/* 195 */     this.def.put("AutoSpeed Max Decrement KBs", new Long(4L));
/* 196 */     this.def.put("AutoSpeed Choking Ping Millis", new Long(200L));
/* 197 */     this.def.put("AutoSpeed Download Adj Enable", FALSE);
/* 198 */     this.def.put("AutoSpeed Download Adj Ratio", "1.0");
/* 199 */     this.def.put("AutoSpeed Latency Factor", new Long(50L));
/* 200 */     this.def.put("AutoSpeed Forced Min KBs", new Long(4L));
/* 201 */     this.def.put("Auto Upload Speed Debug Enabled", FALSE);
/*     */     
/* 203 */     this.def.put("Auto Adjust Transfer Defaults", TRUE);
/*     */     
/* 205 */     this.def.put("Bias Upload Enable", TRUE);
/* 206 */     this.def.put("Bias Upload Slack KBs", new Long(5L));
/* 207 */     this.def.put("Bias Upload Handle No Limit", TRUE);
/*     */     
/* 209 */     this.def.put("ASN Autocheck Performed Time", ZERO);
/*     */     
/* 211 */     this.def.put("LAN Speed Enabled", TRUE);
/* 212 */     this.def.put("Max LAN Download Speed KBs", ZERO);
/* 213 */     this.def.put("Max LAN Upload Speed KBs", ZERO);
/*     */     
/* 215 */     this.def.put("Use Resume", TRUE);
/* 216 */     this.def.put("On Resume Recheck All", FALSE);
/* 217 */     this.def.put("Save Resume Interval", new Long(5L));
/* 218 */     this.def.put("Check Pieces on Completion", TRUE);
/* 219 */     this.def.put("Merge Same Size Files", TRUE);
/* 220 */     this.def.put("Merge Same Size Files Extended", FALSE);
/* 221 */     this.def.put("Stop Ratio", new Float(0.0F));
/* 222 */     this.def.put("Stop Peers Ratio", ZERO);
/* 223 */     this.def.put("Disconnect Seed", TRUE);
/* 224 */     this.def.put("Seeding Piece Check Recheck Enable", TRUE);
/* 225 */     this.def.put("priorityExtensions", "");
/* 226 */     this.def.put("priorityExtensionsIgnoreCase", FALSE);
/* 227 */     this.def.put("quick.view.exts", ".nfo;.txt;.rar;.gif;.jpg;.png;.bmp");
/* 228 */     this.def.put("quick.view.maxkb", new Long(512L));
/*     */     
/* 230 */     this.def.put("Rename Incomplete Files", FALSE);
/* 231 */     this.def.put("Rename Incomplete Files Extension", ".az!");
/*     */     
/* 233 */     this.def.put("Enable Subfolder for DND Files", FALSE);
/* 234 */     this.def.put("Subfolder for DND Files", ".dnd_az!");
/* 235 */     this.def.put("Max File Links Supported", Integer.valueOf(2048));
/*     */     
/* 237 */     this.def.put("Ip Filter Enabled", TRUE);
/* 238 */     this.def.put("Ip Filter Allow", FALSE);
/* 239 */     this.def.put("Ip Filter Enable Banning", TRUE);
/* 240 */     this.def.put("Ip Filter Ban Block Limit", new Long(4L));
/* 241 */     this.def.put("Ip Filter Ban Discard Ratio", "5.0");
/* 242 */     this.def.put("Ip Filter Ban Discard Min KB", new Long(128L));
/* 243 */     this.def.put("Ip Filter Banning Persistent", TRUE);
/* 244 */     this.def.put("Ip Filter Enable Description Cache", TRUE);
/* 245 */     this.def.put("Ip Filter Autoload File", "");
/* 246 */     this.def.put("Ip Filter Clear On Reload", TRUE);
/*     */     
/* 248 */     this.def.put("Allow Same IP Peers", FALSE);
/* 249 */     this.def.put("Use Super Seeding", FALSE);
/*     */     
/* 251 */     this.def.put("Start On Login", FALSE);
/* 252 */     this.def.put("Start In Low Resource Mode", FALSE);
/* 253 */     this.def.put("Auto Register App", FALSE);
/*     */     
/* 255 */     this.def.put("Pause Downloads On Exit", FALSE);
/* 256 */     this.def.put("Resume Downloads On Start", FALSE);
/* 257 */     this.def.put("On Downloading Complete Do", "Nothing");
/* 258 */     this.def.put("On Seeding Complete Do", "Nothing");
/* 259 */     this.def.put("Stop Triggers Auto Reset", TRUE);
/* 260 */     this.def.put("Prompt To Abort Shutdown", TRUE);
/* 261 */     this.def.put("Prevent Sleep Downloading", TRUE);
/* 262 */     this.def.put("Prevent Sleep FP Seeding", FALSE);
/* 263 */     this.def.put("Auto Restart When Idle", ZERO);
/*     */     
/* 265 */     this.def.put("Download History Enabled", TRUE);
/*     */     
/*     */ 
/*     */ 
/* 269 */     this.def.put("User Mode", ZERO);
/*     */     
/*     */ 
/* 272 */     this.def.put("ui.addtorrent.openoptions", "always");
/* 273 */     this.def.put("ui.addtorrent.openoptions.sep", TRUE);
/*     */     
/* 275 */     String docPath = SystemProperties.getDocPath();
/*     */     
/*     */     File f;
/*     */     File f;
/* 279 */     if (Constants.isAndroid) {
/* 280 */       f = new File(docPath, "Downloads");
/*     */     } else {
/* 282 */       f = new File(docPath, "Azureus Downloads");
/*     */       
/*     */ 
/* 285 */       if (!f.exists()) {
/* 286 */         f = new File(docPath, "Vuze Downloads");
/*     */       }
/*     */     }
/*     */     
/* 290 */     this.def.put("Default save path", f.getAbsolutePath());
/* 291 */     this.def.put("saveTo_list.max_entries", new Long(15L));
/*     */     
/* 293 */     this.def.put("update.start", TRUE);
/* 294 */     this.def.put("update.periodic", TRUE);
/* 295 */     this.def.put("update.opendialog", TRUE);
/* 296 */     this.def.put("update.autodownload", FALSE);
/* 297 */     this.def.put("update.anonymous", FALSE);
/*     */     
/* 299 */     this.def.put("Config Verify Frequency", new Long(1800000L));
/*     */     
/* 301 */     this.def.put("Send Version Info", TRUE);
/*     */     
/*     */ 
/* 304 */     this.def.put("Logger.Enabled", FALSE);
/* 305 */     this.def.put("Logging Enable", FALSE);
/* 306 */     this.def.put("Logging Dir", "");
/* 307 */     this.def.put("Logging Timestamp", "HH:mm:ss.SSS");
/* 308 */     this.def.put("Logging Max Size", new Long(5L));
/* 309 */     int[] logComponents = { 0, 1, 2, 4 };
/* 310 */     for (int i = 0; i < logComponents.length; i++)
/* 311 */       for (int j = 0; j <= 3; j++)
/* 312 */         this.def.put("bLog" + logComponents[i] + "-" + j, TRUE);
/* 313 */     this.def.put("Logger.DebugFiles.Enabled", TRUE);
/* 314 */     this.def.put("Logger.DebugFiles.Enabled.Force", FALSE);
/* 315 */     this.def.put("Logging Enable UDP Transport", FALSE);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 320 */     this.def.put("Enable.Proxy", FALSE);
/* 321 */     this.def.put("Enable.SOCKS", FALSE);
/* 322 */     this.def.put("Proxy.Host", "");
/* 323 */     this.def.put("Proxy.Port", "");
/* 324 */     this.def.put("Proxy.Username", "<none>");
/* 325 */     this.def.put("Proxy.Password", "");
/* 326 */     this.def.put("Proxy.Check.On.Start", TRUE);
/* 327 */     this.def.put("Proxy.SOCKS.ShowIcon", TRUE);
/* 328 */     this.def.put("Proxy.SOCKS.ShowIcon.FlagIncoming", TRUE);
/* 329 */     this.def.put("Proxy.SOCKS.Tracker.DNS.Disable", TRUE);
/* 330 */     this.def.put("Proxy.SOCKS.disable.plugin.proxies", TRUE);
/*     */     
/*     */ 
/* 333 */     this.def.put("Proxy.Data.Enable", FALSE);
/* 334 */     this.def.put("Proxy.Data.SOCKS.version", "V4");
/* 335 */     this.def.put("Proxy.Data.SOCKS.inform", TRUE);
/* 336 */     this.def.put("Proxy.Data.Same", TRUE);
/* 337 */     this.def.put("Proxy.Data.Host", "");
/* 338 */     this.def.put("Proxy.Data.Port", "");
/* 339 */     this.def.put("Proxy.Data.Username", "<none>");
/* 340 */     this.def.put("Proxy.Data.Password", "");
/*     */     
/* 342 */     this.def.put("DNS Alt Servers", "8.8.8.8");
/* 343 */     this.def.put("DNS Alt Servers SOCKS Enable", TRUE);
/*     */     
/*     */ 
/* 346 */     this.def.put("Start Num Peers", new Long(-1L));
/* 347 */     this.def.put("Max Upload Speed", new Long(-1L));
/* 348 */     this.def.put("Max Clients", new Long(-1L));
/* 349 */     this.def.put("Server.shared.port", TRUE);
/* 350 */     this.def.put("Low Port", new Long(6881L));
/* 351 */     this.def.put("Already_Migrated", FALSE);
/*     */     
/*     */ 
/* 354 */     this.def.put("ID", "");
/* 355 */     this.def.put("Play Download Finished", FALSE);
/* 356 */     this.def.put("Play Download Finished File", "");
/* 357 */     this.def.put("Watch Torrent Folder", FALSE);
/* 358 */     this.def.put("Watch Torrent Folder Interval", ONE);
/* 359 */     this.def.put("Watch Torrent Folder Interval Secs", SIXTY);
/* 360 */     this.def.put("Start Watched Torrents Stopped", FALSE);
/* 361 */     this.def.put("Watch Torrent Folder Path", "");
/* 362 */     this.def.put("Watch Torrent Folder Path Count", ONE);
/* 363 */     this.def.put("Prioritize First Piece", FALSE);
/* 364 */     this.def.put("Prioritize Most Completed Files", FALSE);
/* 365 */     this.def.put("Piece Picker Request Hint Enabled", TRUE);
/* 366 */     this.def.put("Use Lazy Bitfield", FALSE);
/* 367 */     this.def.put("Zero New", FALSE);
/* 368 */     this.def.put("XFS Allocation", FALSE);
/* 369 */     this.def.put("Copy And Delete Data Rather Than Move", FALSE);
/* 370 */     this.def.put("Move If On Same Drive", FALSE);
/* 371 */     this.def.put("File.save.peers.enable", TRUE);
/* 372 */     this.def.put("File.strict.locking", TRUE);
/* 373 */     this.def.put("Move Deleted Data To Recycle Bin", TRUE);
/* 374 */     this.def.put("Delete Partial Files On Library Removal", FALSE);
/* 375 */     this.def.put("Popup Download Finished", FALSE);
/* 376 */     this.def.put("Popup File Finished", FALSE);
/* 377 */     this.def.put("Popup Download Added", FALSE);
/* 378 */     this.def.put("Show Timestamp For Alerts", FALSE);
/* 379 */     this.def.put("Request Attention On New Download", TRUE);
/* 380 */     this.def.put("Activate Window On External Download", TRUE);
/*     */     
/* 382 */     this.def.put("Insufficient Space Download Restart Enable", FALSE);
/* 383 */     this.def.put("Insufficient Space Download Restart Period", Integer.valueOf(10));
/*     */     
/* 385 */     this.def.put("Play Download Error", FALSE);
/* 386 */     this.def.put("Play Download Error File", "");
/* 387 */     this.def.put("Play Download Error Announcement", FALSE);
/* 388 */     this.def.put("Play Download Error Announcement Text", "Download Error");
/* 389 */     this.def.put("Popup Download Error", FALSE);
/*     */     
/* 391 */     this.def.put("Play Notification Added Announcement", FALSE);
/* 392 */     this.def.put("Play Notification Added Announcement Text", "Notification Added");
/* 393 */     this.def.put("Play Notification Added", FALSE);
/* 394 */     this.def.put("Play Notification Added File", "");
/*     */     
/*     */ 
/*     */ 
/* 398 */     this.def.put("Save Torrent Files", TRUE);
/* 399 */     this.def.put("General_sDefaultTorrent_Directory", SystemProperties.getUserPath() + "torrents");
/* 400 */     this.def.put("Delete Original Torrent Files", FALSE);
/*     */     
/*     */ 
/* 403 */     this.def.put("Bind IP", "");
/* 404 */     this.def.put("Check Bind IP On Start", TRUE);
/* 405 */     this.def.put("Enforce Bind IP", FALSE);
/* 406 */     this.def.put("Show IP Bindings Icon", TRUE);
/*     */     
/* 408 */     this.def.put("Stats Export Peer Details", FALSE);
/* 409 */     this.def.put("Stats Export File Details", FALSE);
/* 410 */     this.def.put("Stats XSL File", "");
/* 411 */     this.def.put("Stats Enable", FALSE);
/* 412 */     this.def.put("Stats Period", new Long(30000L));
/* 413 */     this.def.put("Stats Dir", "");
/* 414 */     this.def.put("Stats File", "Azureus_Stats.xml");
/* 415 */     this.def.put("long.term.stats.enable", TRUE);
/* 416 */     this.def.put("Stats Smoothing Secs", new Long(120L));
/* 417 */     this.def.put("File.Torrent.AutoSkipExtensions", "");
/* 418 */     this.def.put("File.Torrent.AutoSkipMinSizeKB", ZERO);
/* 419 */     this.def.put("File.Torrent.IgnoreFiles", ".DS_Store;Thumbs.db;desktop.ini");
/* 420 */     this.def.put("File.save.peers.max", new Long(512L));
/* 421 */     this.def.put("File.Character.Conversions", "\"='");
/*     */     
/*     */ 
/*     */ 
/* 425 */     this.def.put("Tracker Compact Enable", TRUE);
/* 426 */     this.def.put("Tracker Key Enable Client", TRUE);
/* 427 */     this.def.put("Tracker Key Enable Server", TRUE);
/* 428 */     this.def.put("Tracker Separate Peer IDs", FALSE);
/* 429 */     this.def.put("Tracker Client Connect Timeout", new Long(120L));
/* 430 */     this.def.put("Tracker Client Read Timeout", SIXTY);
/* 431 */     this.def.put("Tracker Client Send OS and Java Version", TRUE);
/* 432 */     this.def.put("Tracker Client Show Warnings", TRUE);
/* 433 */     this.def.put("Tracker Client Min Announce Interval", ZERO);
/* 434 */     this.def.put("Tracker Client Numwant Limit", new Long(100L));
/* 435 */     this.def.put("Tracker Client No Port Announce", FALSE);
/* 436 */     this.def.put("Tracker Client Exclude LAN", TRUE);
/*     */     
/* 438 */     this.def.put("Tracker Public Enable", FALSE);
/* 439 */     this.def.put("Tracker Log Enable", FALSE);
/* 440 */     this.def.put("Tracker Port Enable", FALSE);
/* 441 */     this.def.put("Tracker Port", new Long(6969L));
/* 442 */     this.def.put("Tracker Port Backups", "");
/* 443 */     this.def.put("Tracker Port SSL Enable", FALSE);
/* 444 */     this.def.put("Tracker Port SSL", new Long(7000L));
/* 445 */     this.def.put("Tracker Port SSL Backups", "");
/* 446 */     this.def.put("Tracker Port Force External", FALSE);
/* 447 */     this.def.put("Tracker Host Add Our Announce URLs", TRUE);
/* 448 */     def_put("Tracker IP", "", new IPVerifier());
/*     */     
/* 450 */     this.def.put("Tracker Port UDP Enable", FALSE);
/* 451 */     this.def.put("Tracker Port UDP Version", new Long(2L));
/* 452 */     this.def.put("Tracker Send Peer IDs", TRUE);
/* 453 */     this.def.put("Tracker Max Peers Returned", new Long(100L));
/* 454 */     this.def.put("Tracker Scrape Cache", new Long(5000L));
/* 455 */     this.def.put("Tracker Announce Cache", new Long(500L));
/* 456 */     this.def.put("Tracker Announce Cache Min Peers", new Long(500L));
/* 457 */     this.def.put("Tracker Poll Interval Min", new Long(120L));
/* 458 */     this.def.put("Tracker Poll Interval Max", new Long(3600L));
/* 459 */     this.def.put("Tracker Poll Seed Interval Mult", new Long(1L));
/* 460 */     this.def.put("Tracker Scrape Retry Percentage", new Long(200L));
/* 461 */     this.def.put("Tracker Password Enable Web", FALSE);
/* 462 */     this.def.put("Tracker Password Web HTTPS Only", FALSE);
/* 463 */     this.def.put("Tracker Password Enable Torrent", FALSE);
/* 464 */     this.def.put("Tracker Username", "");
/* 465 */     this.def.put("Tracker Password", null);
/* 466 */     this.def.put("Tracker Poll Inc By", new Long(60L));
/* 467 */     this.def.put("Tracker Poll Inc Per", new Long(10L));
/* 468 */     this.def.put("Tracker NAT Check Enable", TRUE);
/* 469 */     this.def.put("Tracker NAT Check Timeout", new Long(15L));
/* 470 */     this.def.put("Tracker Max Seeds Retained", ZERO);
/* 471 */     this.def.put("Tracker Max Seeds", ZERO);
/* 472 */     this.def.put("Tracker Max GET Time", new Long(20L));
/* 473 */     this.def.put("Tracker Max POST Time Multiplier", ONE);
/* 474 */     this.def.put("Tracker Max Threads", new Long(48L));
/* 475 */     this.def.put("Tracker TCP NonBlocking", FALSE);
/* 476 */     this.def.put("Tracker TCP NonBlocking Restrict Request Types", TRUE);
/* 477 */     this.def.put("Tracker TCP NonBlocking Conc Max", new Long(2048L));
/* 478 */     this.def.put("Tracker TCP NonBlocking Immediate Close", FALSE);
/*     */     
/* 480 */     this.def.put("Tracker Client Scrape Enable", TRUE);
/* 481 */     this.def.put("Tracker Client Scrape Total Disable", FALSE);
/* 482 */     this.def.put("Tracker Client Scrape Stopped Enable", TRUE);
/* 483 */     this.def.put("Tracker Client Scrape Single Only", FALSE);
/* 484 */     this.def.put("Tracker Server Full Scrape Enable", TRUE);
/* 485 */     this.def.put("Tracker Server Not Found Redirect", "");
/* 486 */     this.def.put("Tracker Server Support Experimental Extensions", FALSE);
/*     */     
/* 488 */     this.def.put("Network Selection Prompt", FALSE);
/* 489 */     this.def.put("Network Selection Default.Public", TRUE);
/* 490 */     this.def.put("Network Selection Default.I2P", FALSE);
/* 491 */     this.def.put("Network Selection Default.Tor", FALSE);
/* 492 */     this.def.put("Tracker Network Selection Default.Public", TRUE);
/* 493 */     this.def.put("Tracker Network Selection Default.I2P", TRUE);
/* 494 */     this.def.put("Tracker Network Selection Default.Tor", TRUE);
/*     */     
/* 496 */     this.def.put("Peer Source Selection Default.Tracker", TRUE);
/* 497 */     this.def.put("Peer Source Selection Default.DHT", TRUE);
/* 498 */     this.def.put("Peer Source Selection Default.PeerExchange", TRUE);
/* 499 */     this.def.put("Peer Source Selection Default.Plugin", TRUE);
/* 500 */     this.def.put("Peer Source Selection Default.Incoming", TRUE);
/*     */     
/* 502 */     this.def.put("config.style.useSIUnits", FALSE);
/* 503 */     this.def.put("config.style.forceSIValues", Constants.isOSX_10_6_OrHigher ? FALSE : TRUE);
/* 504 */     this.def.put("config.style.useUnitsRateBits", FALSE);
/* 505 */     this.def.put("config.style.separateProtDataStats", FALSE);
/* 506 */     this.def.put("config.style.dataStatsOnly", FALSE);
/* 507 */     this.def.put("config.style.doNotUseGB", FALSE);
/*     */     
/* 509 */     this.def.put("Save Torrent Backup", FALSE);
/*     */     
/* 511 */     this.def.put("Sharing Protocol", "DHT");
/* 512 */     this.def.put("Sharing Add Hashes", FALSE);
/* 513 */     this.def.put("Sharing Rescan Enable", FALSE);
/* 514 */     this.def.put("Sharing Rescan Period", SIXTY);
/* 515 */     this.def.put("Sharing Torrent Comment", "");
/* 516 */     this.def.put("Sharing Permit DHT", TRUE);
/* 517 */     this.def.put("Sharing Torrent Private", FALSE);
/* 518 */     this.def.put("Sharing Is Persistent", FALSE);
/*     */     
/* 520 */     this.def.put("File.Decoder.Prompt", FALSE);
/* 521 */     this.def.put("File.Decoder.Default", "");
/* 522 */     this.def.put("File.Decoder.ShowLax", FALSE);
/* 523 */     this.def.put("File.Decoder.ShowAll", FALSE);
/* 524 */     this.def.put("Password enabled", FALSE);
/* 525 */     this.def.put("Password", null);
/* 526 */     this.def.put("config.interface.checkassoc", TRUE);
/* 527 */     this.def.put("confirmationOnExit", FALSE);
/* 528 */     this.def.put("locale", Locale.getDefault().toString());
/* 529 */     this.def.put("locale.set.complete.count", ZERO);
/* 530 */     this.def.put("Password Confirm", null);
/* 531 */     this.def.put("Auto Update", TRUE);
/* 532 */     this.def.put("Alert on close", FALSE);
/* 533 */     this.def.put("diskmanager.friendly.hashchecking", FALSE);
/* 534 */     this.def.put("diskmanager.hashchecking.smallestfirst", TRUE);
/* 535 */     this.def.put("Default Start Torrents Stopped", FALSE);
/* 536 */     this.def.put("Default Start Torrents Stopped Auto Pause", FALSE);
/* 537 */     this.def.put("Server Enable UDP", TRUE);
/* 538 */     this.def.put("Tracker UDP Probe Enable", TRUE);
/* 539 */     this.def.put("Tracker Client Enable TCP", TRUE);
/* 540 */     this.def.put("Tracker DNS Records Enable", TRUE);
/* 541 */     this.def.put("diskmanager.perf.cache.enable", TRUE);
/* 542 */     this.def.put("diskmanager.perf.cache.enable.read", FALSE);
/* 543 */     this.def.put("diskmanager.perf.cache.enable.write", TRUE);
/* 544 */     this.def.put("diskmanager.perf.cache.size", new Long(4L));
/* 545 */     this.def.put("diskmanager.perf.cache.notsmallerthan", new Long(1024L));
/* 546 */     this.def.put("diskmanager.perf.read.maxthreads", new Long(32L));
/* 547 */     this.def.put("diskmanager.perf.read.maxmb", new Long(5L));
/* 548 */     this.def.put("diskmanager.perf.write.maxthreads", new Long(32L));
/* 549 */     this.def.put("diskmanager.perf.write.maxmb", new Long(5L));
/* 550 */     this.def.put("diskmanager.perf.cache.trace", FALSE);
/* 551 */     this.def.put("diskmanager.perf.cache.flushpieces", TRUE);
/* 552 */     this.def.put("diskmanager.perf.read.aggregate.enable", FALSE);
/* 553 */     this.def.put("diskmanager.perf.read.aggregate.request.limit", ZERO);
/* 554 */     this.def.put("diskmanager.perf.read.aggregate.byte.limit", ZERO);
/* 555 */     this.def.put("diskmanager.perf.write.aggregate.enable", FALSE);
/* 556 */     this.def.put("diskmanager.perf.write.aggregate.request.limit", ZERO);
/* 557 */     this.def.put("diskmanager.perf.write.aggregate.byte.limit", ZERO);
/* 558 */     this.def.put("diskmanager.perf.checking.read.priority", FALSE);
/* 559 */     this.def.put("diskmanager.perf.checking.fully.async", FALSE);
/* 560 */     this.def.put("diskmanager.perf.queue.torrent.bias", TRUE);
/*     */     
/*     */ 
/*     */ 
/* 564 */     this.def.put("peercontrol.udp.fallback.connect.fail", TRUE);
/* 565 */     this.def.put("peercontrol.udp.fallback.connect.drop", TRUE);
/* 566 */     this.def.put("peercontrol.udp.probe.enable", FALSE);
/* 567 */     this.def.put("peercontrol.hide.piece", FALSE);
/* 568 */     this.def.put("peercontrol.scheduler.use.priorities", TRUE);
/* 569 */     this.def.put("peercontrol.prefer.udp", FALSE);
/*     */     
/* 571 */     this.def.put("File.truncate.if.too.large", FALSE);
/* 572 */     this.def.put("Enable System Tray", TRUE);
/* 573 */     this.def.put("Show Status In Window Title", FALSE);
/* 574 */     this.def.put("config.style.table.defaultSortOrder", ZERO);
/* 575 */     this.def.put("Ignore.peer.ports", "0");
/* 576 */     this.def.put("Security.JAR.tools.dir", "");
/* 577 */     this.def.put("security.cert.auto.install", TRUE);
/*     */     
/* 579 */     boolean tcp_half_open_limited = (Constants.isWindows) && (!Constants.isWindowsVistaSP2OrHigher) && (!Constants.isWindows7OrHigher);
/*     */     
/* 581 */     this.def.put("network.max.simultaneous.connect.attempts", new Long(tcp_half_open_limited ? 8L : 24L));
/* 582 */     this.def.put("network.tcp.max.connections.outstanding", new Long(2048L));
/* 583 */     this.def.put("network.tcp.connect.outbound.enable", TRUE);
/* 584 */     this.def.put("network.tcp.mtu.size", new Long(1500L));
/* 585 */     this.def.put("network.udp.mtu.size", new Long(1500L));
/* 586 */     this.def.put("network.udp.poll.time", new Long(100L));
/* 587 */     this.def.put("network.tcp.socket.SO_SNDBUF", ZERO);
/* 588 */     this.def.put("network.tcp.socket.SO_RCVBUF", ZERO);
/* 589 */     this.def.put("network.tcp.socket.IPDiffServ", "");
/* 590 */     this.def.put("network.tcp.read.select.time", new Long(25L));
/* 591 */     this.def.put("network.tcp.read.select.min.time", ZERO);
/* 592 */     this.def.put("network.tcp.write.select.time", new Long(25L));
/* 593 */     this.def.put("network.tcp.write.select.min.time", ZERO);
/* 594 */     this.def.put("network.tcp.connect.select.time", new Long(100L));
/* 595 */     this.def.put("network.tcp.connect.select.min.time", ZERO);
/*     */     
/* 597 */     this.def.put("network.tracker.tcp.select.time", new Long(100L));
/*     */     
/* 599 */     this.def.put("network.control.write.idle.time", new Long(50L));
/* 600 */     this.def.put("network.control.write.aggressive", FALSE);
/* 601 */     this.def.put("network.control.read.idle.time", new Long(50L));
/* 602 */     this.def.put("network.control.read.aggressive", FALSE);
/* 603 */     this.def.put("network.control.read.processor.count", new Long(1L));
/* 604 */     this.def.put("network.control.write.processor.count", new Long(1L));
/* 605 */     this.def.put("peermanager.schedule.time", new Long(100L));
/* 606 */     this.def.put("enable_small_osx_fonts", TRUE);
/* 607 */     this.def.put("Play Download Finished Announcement", FALSE);
/* 608 */     this.def.put("Play Download Finished Announcement Text", "Download Complete");
/* 609 */     this.def.put("Play File Finished", FALSE);
/* 610 */     this.def.put("Play File Finished File", "");
/* 611 */     this.def.put("Play File Finished Announcement", FALSE);
/* 612 */     this.def.put("Play File Finished Announcement Text", "File Complete");
/*     */     
/* 614 */     this.def.put("filechannel.rt.buffer.millis", new Long(60000L));
/* 615 */     this.def.put("filechannel.rt.buffer.pieces", new Long(5L));
/*     */     
/* 617 */     this.def.put("BT Request Max Block Size", new Long(65536L));
/* 618 */     this.def.put("network.tcp.enable_safe_selector_mode", FALSE);
/* 619 */     this.def.put("network.tcp.safe_selector_mode.chunk_size", SIXTY);
/*     */     
/* 621 */     this.def.put("network.transport.encrypted.require", FALSE);
/* 622 */     this.def.put("network.transport.encrypted.min_level", "RC4");
/* 623 */     this.def.put("network.transport.encrypted.fallback.outgoing", FALSE);
/* 624 */     this.def.put("network.transport.encrypted.fallback.incoming", FALSE);
/* 625 */     this.def.put("network.transport.encrypted.use.crypto.port", FALSE);
/* 626 */     this.def.put("network.transport.encrypted.allow.incoming", TRUE);
/*     */     
/* 628 */     this.def.put("network.bind.local.port", ZERO);
/*     */     
/* 630 */     this.def.put("network.admin.maybe.vpn.enable", TRUE);
/*     */     
/* 632 */     this.def.put("crypto.keys.system.managed", FALSE);
/*     */     
/* 634 */     this.def.put("peer.nat.traversal.request.conc.max", new Long(3L));
/*     */     
/*     */ 
/*     */ 
/* 638 */     this.def.put("memory.slice.limit.multiplier", new Long(1L));
/*     */     
/*     */ 
/* 641 */     this.def.put("Move Completed When Done", FALSE);
/* 642 */     this.def.put("Completed Files Directory", "");
/* 643 */     this.def.put("Move Only When In Default Save Dir", TRUE);
/* 644 */     this.def.put("Move Torrent When Done", TRUE);
/* 645 */     this.def.put("Move Torrent When Done Directory", "");
/* 646 */     this.def.put("File.move.subdir_is_default", TRUE);
/*     */     
/*     */ 
/*     */ 
/* 650 */     this.def.put("Set Completion Flag For Completed Downloads On Start", TRUE);
/*     */     
/*     */ 
/* 653 */     this.def.put("File.move.download.removed.enabled", FALSE);
/* 654 */     this.def.put("File.move.download.removed.path", "");
/* 655 */     this.def.put("File.move.download.removed.only_in_default", TRUE);
/* 656 */     this.def.put("File.move.download.removed.move_torrent", TRUE);
/* 657 */     this.def.put("File.move.download.removed.move_torrent_path", "");
/* 658 */     this.def.put("File.move.download.removed.move_partial", FALSE);
/*     */     
/* 660 */     this.def.put("File.delete.include_files_outside_save_dir", FALSE);
/*     */     
/* 662 */     this.def.put("FilesView.show.full.path", FALSE);
/*     */     
/* 664 */     this.def.put("MyTorrentsView.menu.show_parent_folder_enabled", FALSE);
/* 665 */     this.def.put("FileBrowse.usePathFinder", FALSE);
/*     */     
/* 667 */     this.def.put("Beta Programme Enabled", FALSE);
/* 668 */     this.def.put("def.deletetorrent", TRUE);
/* 669 */     this.def.put("tb.confirm.delete.content", Long.valueOf(0L));
/*     */     
/* 671 */     this.def.put("br.backup.auto.enable", FALSE);
/* 672 */     this.def.put("br.backup.auto.everydays", Long.valueOf(1L));
/* 673 */     this.def.put("br.backup.auto.retain", Long.valueOf(5L));
/*     */     
/*     */ 
/*     */     try
/*     */     {
/* 678 */       this.def.put("SpeedManagerAlgorithmProviderV2.setting.download.max.limit", new Long(61440L));
/* 679 */       this.def.put("SpeedManagerAlgorithmProviderV2.setting.upload.max.limit", new Long(30720L));
/*     */       
/* 681 */       this.def.put("SpeedManagerAlgorithmProviderV2.setting.dht.good.setpoint", new Long(50L));
/* 682 */       this.def.put("SpeedManagerAlgorithmProviderV2.setting.dht.good.tolerance", new Long(100L));
/* 683 */       this.def.put("SpeedManagerAlgorithmProviderV2.setting.dht.bad.setpoint", new Long(900L));
/* 684 */       this.def.put("SpeedManagerAlgorithmProviderV2.setting.dht.bad.tolerance", new Long(500L));
/*     */       
/*     */ 
/* 687 */       this.def.put("Auto Upload Speed Version", new Long(2L));
/*     */       
/* 689 */       this.def.put("SpeedLimitMonitor.setting.download.limit.conf", SpeedLimitConfidence.NONE.getString());
/* 690 */       this.def.put("SpeedLimitMonitor.setting.upload.limit.conf", SpeedLimitConfidence.NONE.getString());
/* 691 */       this.def.put("SpeedLimitMonitor.setting.choke.ping.count", new Long(1L));
/*     */       
/*     */ 
/* 694 */       this.def.put("SpeedLimitMonitor.setting.upload.used.seeding.mode", new Long(90L));
/* 695 */       this.def.put("SpeedLimitMonitor.setting.upload.used.download.mode", SIXTY);
/*     */       
/* 697 */       this.def.put("SpeedManagerAlgorithmProviderV2.setting.wait.after.adjust", TRUE);
/* 698 */       this.def.put("SpeedManagerAlgorithmProviderV2.intervals.between.adjust", new Long(2L));
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 706 */     this.def.put("subscriptions.max.non.deleted.results", new Long(512L));
/* 707 */     this.def.put("subscriptions.auto.start.downloads", TRUE);
/* 708 */     this.def.put("subscriptions.auto.start.min.mb", ZERO);
/* 709 */     this.def.put("subscriptions.auto.start.max.mb", ZERO);
/* 710 */     this.def.put("subscriptions.auto.dl.mark.read.days", ZERO);
/*     */     
/* 712 */     this.def.put("Show Side Bar", TRUE);
/* 713 */     this.def.put("Side Bar Top Level Gap", ONE);
/* 714 */     this.def.put("Show Options In Side Bar", FALSE);
/*     */     
/* 716 */     this.def.put("Share Ratio Progress Interval", Long.valueOf(1000L));
/*     */     
/* 718 */     this.def.put("installer.mode", "");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected ConfigurationDefaults(Map _def)
/*     */   {
/* 725 */     this.def = new ConcurrentHashMapWrapper(_def);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void def_put(String key, String key_def, COConfigurationManager.ParameterVerifier verifier)
/*     */   {
/* 734 */     this.def.put(key, key_def);
/*     */     
/* 736 */     List l = (List)this.parameter_verifiers.get(key);
/*     */     
/* 738 */     if (l == null)
/*     */     {
/* 740 */       l = new ArrayList(1);
/*     */       
/* 742 */       this.parameter_verifiers.put(key, l);
/*     */     }
/*     */     
/* 745 */     l.add(verifier);
/*     */   }
/*     */   
/*     */   private void checkParameterExists(String p) throws ConfigurationParameterNotFoundException {
/* 749 */     if (!this.def.containsKey(p)) {
/* 750 */       ConfigurationParameterNotFoundException cpnfe = new ConfigurationParameterNotFoundException(p);
/*     */       
/*     */ 
/* 753 */       throw cpnfe;
/*     */     }
/*     */   }
/*     */   
/*     */   public String getStringParameter(String p) throws ConfigurationParameterNotFoundException {
/* 758 */     checkParameterExists(p);
/* 759 */     Object o = this.def.get(p);
/* 760 */     if ((o instanceof Number))
/* 761 */       return ((Number)o).toString();
/* 762 */     return (String)o;
/*     */   }
/*     */   
/*     */   public int getIntParameter(String p) throws ConfigurationParameterNotFoundException {
/* 766 */     checkParameterExists(p);
/* 767 */     return ((Number)this.def.get(p)).intValue();
/*     */   }
/*     */   
/*     */   public long getLongParameter(String p) throws ConfigurationParameterNotFoundException {
/* 771 */     checkParameterExists(p);
/* 772 */     return ((Long)this.def.get(p)).longValue();
/*     */   }
/*     */   
/*     */   public float getFloatParameter(String p) throws ConfigurationParameterNotFoundException {
/* 776 */     checkParameterExists(p);
/* 777 */     return ((Float)this.def.get(p)).floatValue();
/*     */   }
/*     */   
/*     */   public byte[] getByteParameter(String p) throws ConfigurationParameterNotFoundException {
/* 781 */     checkParameterExists(p);
/* 782 */     return (byte[])this.def.get(p);
/*     */   }
/*     */   
/*     */   public boolean getBooleanParameter(String p) throws ConfigurationParameterNotFoundException {
/* 786 */     checkParameterExists(p);
/* 787 */     return ((Long)this.def.get(p)).equals(TRUE);
/*     */   }
/*     */   
/*     */   public boolean hasParameter(String p) {
/* 791 */     return this.def.containsKey(p);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object getDefaultValueAsObject(String key)
/*     */   {
/* 801 */     return this.def.get(key);
/*     */   }
/*     */   
/*     */   public Set<String> getAllowedParameters() {
/* 805 */     return this.def.keySet();
/*     */   }
/*     */   
/*     */   public void addParameter(String sKey, String sParameter) {
/* 809 */     this.def.put(sKey, sParameter);
/*     */   }
/*     */   
/*     */   public void addParameter(String sKey, int iParameter) {
/* 813 */     this.def.put(sKey, new Long(iParameter));
/*     */   }
/*     */   
/* 816 */   public void addParameter(String sKey, byte[] bParameter) { this.def.put(sKey, bParameter); }
/*     */   
/*     */   public void addParameter(String sKey, boolean bParameter)
/*     */   {
/* 820 */     Long lParameter = new Long(bParameter ? 1L : 0L);
/* 821 */     this.def.put(sKey, lParameter);
/*     */   }
/*     */   
/*     */   public void addParameter(String sKey, long lParameter) {
/* 825 */     this.def.put(sKey, new Long(lParameter));
/*     */   }
/*     */   
/*     */   public void addParameter(String sKey, float fParameter) {
/* 829 */     this.def.put(sKey, new Float(fParameter));
/*     */   }
/*     */   
/*     */   public void registerExternalDefaults(Map addmap) {
/* 833 */     this.def.putAll(addmap);
/*     */   }
/*     */   
/*     */   public boolean doesParameterDefaultExist(String p) {
/* 837 */     return this.def.containsKey(p);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getParameter(String key)
/*     */   {
/* 844 */     return this.def.get(key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List getVerifiers(String key)
/*     */   {
/* 851 */     return (List)this.parameter_verifiers.get(key);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void runVerifiers()
/*     */   {
/* 857 */     Iterator it = this.parameter_verifiers.entrySet().iterator();
/*     */     
/* 859 */     while (it.hasNext())
/*     */     {
/* 861 */       Map.Entry entry = (Map.Entry)it.next();
/*     */       
/* 863 */       String key = (String)entry.getKey();
/* 864 */       List verifiers = (List)entry.getValue();
/*     */       
/* 866 */       for (int i = 0; i < verifiers.size(); i++)
/*     */       {
/* 868 */         COConfigurationManager.ParameterVerifier verifier = (COConfigurationManager.ParameterVerifier)verifiers.get(i);
/*     */         
/* 870 */         Object val_def = getDefaultValueAsObject(key);
/*     */         
/*     */ 
/* 873 */         if (val_def != null)
/*     */         {
/*     */           Object val;
/*     */           
/*     */ 
/* 878 */           if ((val_def instanceof String))
/*     */           {
/* 880 */             val = COConfigurationManager.getStringParameter(key);
/*     */           }
/*     */           else
/*     */           {
/* 884 */             Debug.out("Unsupported verifier type for parameter '" + key + "' - " + val_def);
/*     */             
/* 886 */             continue;
/*     */           }
/*     */           Object val;
/* 889 */           if (val != null)
/*     */           {
/*     */ 
/*     */ 
/*     */ 
/* 894 */             if (!verifier.verify(key, val))
/*     */             {
/* 896 */               Debug.out("Parameter '" + key + "', value '" + val + "' failed verification - setting back to default '" + val_def + "'");
/*     */               
/* 898 */               COConfigurationManager.removeParameter(key);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class IPVerifier
/*     */     implements COConfigurationManager.ParameterVerifier
/*     */   {
/*     */     public boolean verify(String parameter, Object _value)
/*     */     {
/* 913 */       String value = (String)_value;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 919 */       for (int i = 0; i < value.length(); i++)
/*     */       {
/* 921 */         char c = value.charAt(i);
/*     */         
/*     */ 
/*     */ 
/* 925 */         if ((!Character.isLetterOrDigit(c)) && (c != '.') && (c != '-') && (c != ':') && (c != '~'))
/*     */         {
/*     */ 
/*     */ 
/* 929 */           return false;
/*     */         }
/*     */       }
/*     */       
/* 933 */       return true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/config/impl/ConfigurationDefaults.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */