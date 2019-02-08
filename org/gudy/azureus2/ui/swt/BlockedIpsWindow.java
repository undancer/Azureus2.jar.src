/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.ipfilter.BadIp;
/*     */ import org.gudy.azureus2.core3.ipfilter.BadIps;
/*     */ import org.gudy.azureus2.core3.ipfilter.BannedIp;
/*     */ import org.gudy.azureus2.core3.ipfilter.BlockedIp;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilter;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpFilterManager;
/*     */ import org.gudy.azureus2.core3.ipfilter.IpRange;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BlockedIpsWindow
/*     */ {
/*     */   static AzureusCore azureus_core;
/*     */   static Shell instance;
/*     */   
/*     */   public static void show(AzureusCore _azureus_core, Display display, String ipsBlocked, String ipsBanned)
/*     */   {
/*  64 */     if ((instance == null) || (instance.isDisposed()))
/*     */     {
/*  66 */       instance = create(_azureus_core, display, ipsBlocked, ipsBanned);
/*  67 */       instance.addDisposeListener(new DisposeListener() {
/*     */         public void widgetDisposed(DisposeEvent event) {
/*  69 */           BlockedIpsWindow.instance = null;
/*     */         }
/*     */       });
/*     */     }
/*     */     else
/*     */     {
/*  75 */       instance.open();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static Shell create(AzureusCore _azureus_core, Display display, String ipsBlocked, String ipsBanned)
/*     */   {
/*  86 */     azureus_core = _azureus_core;
/*     */     int styles;
/*     */     int styles;
/*  89 */     if (Constants.isOSX) {
/*  90 */       styles = 1264;
/*     */     }
/*     */     else {
/*  93 */       styles = 68720;
/*     */     }
/*     */     
/*  96 */     Shell window = ShellFactory.createShell(display, styles);
/*  97 */     Messages.setLanguageText(window, "ConfigView.section.ipfilter.list.title");
/*  98 */     Utils.setShellIcon(window);
/*  99 */     FormLayout layout = new FormLayout();
/*     */     try {
/* 101 */       layout.spacing = 5;
/*     */     }
/*     */     catch (NoSuchFieldError e) {}
/*     */     
/* 105 */     layout.marginHeight = 5;
/* 106 */     layout.marginWidth = 5;
/* 107 */     window.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 112 */     StyledText textBlocked = new StyledText(window, 2816);
/* 113 */     Button btnClear = new Button(window, 8);
/* 114 */     textBlocked.setEditable(false);
/*     */     
/* 116 */     StyledText textBanned = new StyledText(window, 2816);
/* 117 */     Button btnOk = new Button(window, 8);
/* 118 */     Button btnReset = new Button(window, 8);
/* 119 */     textBanned.setEditable(false);
/*     */     
/*     */ 
/* 122 */     FormData formData = new FormData();
/* 123 */     formData.left = new FormAttachment(0, 0);
/* 124 */     formData.right = new FormAttachment(100, 0);
/* 125 */     formData.top = new FormAttachment(0, 0);
/* 126 */     formData.bottom = new FormAttachment(40, 0);
/* 127 */     Utils.setLayoutData(textBlocked, formData);
/* 128 */     textBlocked.setText(ipsBlocked);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 133 */     Label blockedInfo = new Label(window, 0);
/* 134 */     Messages.setLanguageText(blockedInfo, "ConfigView.section.ipfilter.blockedinfo");
/* 135 */     formData = new FormData();
/* 136 */     formData.top = new FormAttachment(textBlocked);
/* 137 */     formData.right = new FormAttachment(btnClear);
/* 138 */     formData.left = new FormAttachment(0, 0);
/* 139 */     Utils.setLayoutData(blockedInfo, formData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 144 */     Messages.setLanguageText(btnClear, "Button.clear");
/* 145 */     formData = new FormData();
/* 146 */     formData.top = new FormAttachment(textBlocked);
/* 147 */     formData.right = new FormAttachment(95, 0);
/*     */     
/* 149 */     formData.width = 70;
/* 150 */     Utils.setLayoutData(btnClear, formData);
/* 151 */     btnClear.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event e)
/*     */       {
/* 155 */         BlockedIpsWindow.azureus_core.getIpFilterManager().getIPFilter().clearBlockedIPs();
/*     */         
/* 157 */         this.val$textBlocked.setText("");
/*     */ 
/*     */       }
/*     */       
/*     */ 
/* 162 */     });
/* 163 */     formData = new FormData();
/* 164 */     formData.left = new FormAttachment(0, 0);
/* 165 */     formData.right = new FormAttachment(100, 0);
/* 166 */     formData.top = new FormAttachment(btnClear);
/* 167 */     formData.bottom = new FormAttachment(btnOk);
/* 168 */     Utils.setLayoutData(textBanned, formData);
/* 169 */     textBanned.setText(ipsBanned);
/*     */     
/*     */ 
/*     */ 
/* 173 */     Label bannedInfo = new Label(window, 0);
/* 174 */     Messages.setLanguageText(bannedInfo, "ConfigView.section.ipfilter.bannedinfo");
/* 175 */     formData = new FormData();
/* 176 */     formData.right = new FormAttachment(btnReset);
/* 177 */     formData.left = new FormAttachment(0, 0);
/* 178 */     formData.bottom = new FormAttachment(100, 0);
/* 179 */     Utils.setLayoutData(bannedInfo, formData);
/*     */     
/*     */ 
/*     */ 
/* 183 */     Messages.setLanguageText(btnReset, "Button.reset");
/* 184 */     formData = new FormData();
/* 185 */     formData.right = new FormAttachment(btnOk);
/* 186 */     formData.bottom = new FormAttachment(100, 0);
/* 187 */     formData.width = 70;
/* 188 */     Utils.setLayoutData(btnReset, formData);
/* 189 */     btnReset.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event e) {
/* 192 */         BlockedIpsWindow.azureus_core.getIpFilterManager().getIPFilter().clearBannedIps();
/* 193 */         BlockedIpsWindow.azureus_core.getIpFilterManager().getBadIps().clearBadIps();
/*     */         
/* 195 */         this.val$textBanned.setText("");
/*     */       }
/*     */       
/*     */ 
/* 199 */     });
/* 200 */     Messages.setLanguageText(btnOk, "Button.ok");
/* 201 */     formData = new FormData();
/* 202 */     formData.right = new FormAttachment(95, 0);
/* 203 */     formData.bottom = new FormAttachment(100, 0);
/* 204 */     formData.width = 70;
/* 205 */     Utils.setLayoutData(btnOk, formData);
/* 206 */     btnOk.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event e) {
/* 209 */         this.val$window.dispose();
/*     */       }
/*     */       
/* 212 */     });
/* 213 */     window.setDefaultButton(btnOk);
/*     */     
/* 215 */     window.addListener(31, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 217 */         if (e.character == '\033') {
/* 218 */           this.val$window.dispose();
/*     */         }
/*     */       }
/*     */     });
/*     */     
/* 223 */     if (!Utils.linkShellMetricsToConfig(window, "BlockedIpsWindow")) {
/* 224 */       window.setSize(620, 450);
/* 225 */       if (!Constants.isOSX)
/* 226 */         Utils.centreWindow(window);
/*     */     }
/* 228 */     window.layout();
/* 229 */     window.open();
/* 230 */     return window;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void showBlockedIps(AzureusCore azureus_core, Shell mainWindow)
/*     */   {
/* 238 */     StringBuilder sbBlocked = new StringBuilder();
/* 239 */     StringBuilder sbBanned = new StringBuilder();
/* 240 */     BlockedIp[] blocked = azureus_core.getIpFilterManager().getIPFilter().getBlockedIps();
/* 241 */     String inRange = MessageText.getString("ConfigView.section.ipfilter.list.inrange");
/* 242 */     String notInRange = MessageText.getString("ConfigView.section.ipfilter.list.notinrange");
/* 243 */     String bannedMessage = MessageText.getString("ConfigView.section.ipfilter.list.banned");
/* 244 */     String badDataMessage = MessageText.getString("ConfigView.section.ipfilter.list.baddata");
/*     */     
/* 246 */     for (int i = 0; i < blocked.length; i++) {
/* 247 */       BlockedIp bIp = blocked[i];
/* 248 */       if (bIp.isLoggable())
/*     */       {
/*     */ 
/* 251 */         sbBlocked.append(DisplayFormatters.formatTimeStamp(bIp.getBlockedTime()));
/* 252 */         sbBlocked.append("\t[");
/* 253 */         sbBlocked.append(bIp.getTorrentName());
/* 254 */         sbBlocked.append("] \t");
/* 255 */         sbBlocked.append(bIp.getBlockedIp());
/* 256 */         IpRange range = bIp.getBlockingRange();
/* 257 */         if (range == null) {
/* 258 */           sbBlocked.append(' ');
/* 259 */           sbBlocked.append(notInRange);
/* 260 */           sbBlocked.append('\n');
/*     */         } else {
/* 262 */           sbBlocked.append(' ');
/* 263 */           sbBlocked.append(inRange);
/* 264 */           sbBlocked.append(range.toString());
/* 265 */           sbBlocked.append('\n');
/*     */         }
/*     */       }
/*     */     }
/* 269 */     BannedIp[] banned_ips = azureus_core.getIpFilterManager().getIPFilter().getBannedIps();
/*     */     
/* 271 */     for (int i = 0; i < banned_ips.length; i++) {
/* 272 */       BannedIp bIp = banned_ips[i];
/* 273 */       sbBanned.append(DisplayFormatters.formatTimeStamp(bIp.getBanningTime()));
/* 274 */       sbBanned.append("\t[");
/* 275 */       sbBanned.append(bIp.getTorrentName());
/* 276 */       sbBanned.append("] \t");
/* 277 */       sbBanned.append(bIp.getIp());
/* 278 */       sbBanned.append(" ");
/* 279 */       sbBanned.append(bannedMessage);
/* 280 */       sbBanned.append("\n");
/*     */     }
/*     */     
/* 283 */     BadIp[] bad_ips = azureus_core.getIpFilterManager().getBadIps().getBadIps();
/* 284 */     for (int i = 0; i < bad_ips.length; i++) {
/* 285 */       BadIp bIp = bad_ips[i];
/* 286 */       sbBanned.append(DisplayFormatters.formatTimeStamp(bIp.getLastTime()));
/* 287 */       sbBanned.append("\t");
/* 288 */       sbBanned.append(bIp.getIp());
/* 289 */       sbBanned.append(" ");
/* 290 */       sbBanned.append(badDataMessage);
/* 291 */       sbBanned.append(" ");
/* 292 */       sbBanned.append(bIp.getNumberOfWarnings());
/* 293 */       sbBanned.append("\n");
/*     */     }
/*     */     
/* 296 */     if ((mainWindow == null) || (mainWindow.isDisposed()))
/* 297 */       return;
/* 298 */     show(azureus_core, mainWindow.getDisplay(), sbBlocked.toString(), sbBanned.toString());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/BlockedIpsWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */