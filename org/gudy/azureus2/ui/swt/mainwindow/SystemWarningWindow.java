/*     */ package org.gudy.azureus2.ui.swt.mainwindow;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseListener;
/*     */ import org.eclipse.swt.events.MouseMoveListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Alerts;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter.URLInfo;
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
/*     */ public class SystemWarningWindow
/*     */ {
/*  53 */   private int WIDTH = 230;
/*     */   
/*  55 */   private int BORDER_X = 12;
/*     */   
/*  57 */   private int BORDER_Y0 = 10;
/*     */   
/*  59 */   private int BORDER_Y1 = 6;
/*     */   
/*  61 */   private int GAP_Y = 5;
/*     */   
/*  63 */   private int GAP_BUTTON_Y = 20;
/*     */   
/*  65 */   private int GAP_Y_TITLE_COUNT = 3;
/*     */   
/*     */   private final LogAlert logAlert;
/*     */   
/*     */   private final Point ptBottomRight;
/*     */   
/*     */   private final Shell parent;
/*     */   
/*     */   private Shell shell;
/*     */   
/*     */   private Image imgClose;
/*     */   
/*     */   private Rectangle boundsClose;
/*     */   
/*     */   private GCStringPrinter spText;
/*     */   
/*     */   private GCStringPrinter spTitle;
/*     */   
/*     */   private GCStringPrinter spCount;
/*     */   
/*     */   private Point sizeTitle;
/*     */   
/*     */   private Point sizeText;
/*     */   
/*     */   private Point sizeCount;
/*     */   
/*     */   private Font fontTitle;
/*     */   
/*     */   private Font fontCount;
/*     */   
/*     */   private int height;
/*     */   
/*     */   private Rectangle rectX;
/*     */   
/*     */   private int historyPosition;
/*     */   
/*     */   private String title;
/*     */   
/*     */   private String text;
/*     */   
/* 105 */   public static int numWarningWindowsOpen = 0;
/*     */   
/*     */   public SystemWarningWindow(LogAlert logAlert, Point ptBottomRight, Shell parent, int historyPosition)
/*     */   {
/* 109 */     this.logAlert = logAlert;
/* 110 */     this.ptBottomRight = ptBottomRight;
/* 111 */     this.parent = parent;
/* 112 */     this.historyPosition = historyPosition;
/*     */     
/* 114 */     this.WIDTH = Utils.adjustPXForDPI(this.WIDTH);
/*     */     
/* 116 */     this.BORDER_X = Utils.adjustPXForDPI(this.BORDER_X);
/*     */     
/* 118 */     this.BORDER_Y0 = Utils.adjustPXForDPI(this.BORDER_Y0);
/*     */     
/* 120 */     this.BORDER_Y1 = Utils.adjustPXForDPI(this.BORDER_Y1);
/*     */     
/* 122 */     this.GAP_Y = Utils.adjustPXForDPI(this.GAP_Y);
/*     */     
/* 124 */     this.GAP_BUTTON_Y = Utils.adjustPXForDPI(this.GAP_BUTTON_Y);
/*     */     
/* 126 */     this.GAP_Y_TITLE_COUNT = Utils.adjustPXForDPI(this.GAP_Y_TITLE_COUNT);
/*     */     
/*     */     String amb_key_suffix;
/* 129 */     switch (logAlert.entryType) {
/*     */     case 3: 
/* 131 */       amb_key_suffix = "error";
/* 132 */       break;
/*     */     case 0: 
/* 134 */       amb_key_suffix = "information";
/* 135 */       break;
/*     */     case 1: 
/* 137 */       amb_key_suffix = "warning";
/* 138 */       break;
/*     */     case 2: default: 
/* 140 */       amb_key_suffix = null;
/*     */     }
/*     */     
/* 143 */     this.title = (amb_key_suffix == null ? Constants.APP_NAME : MessageText.getString("AlertMessageBox." + amb_key_suffix));
/*     */     
/*     */ 
/* 146 */     if (logAlert.text.startsWith("{")) {
/* 147 */       this.text = MessageText.expandValue(logAlert.text);
/*     */     } else {
/* 149 */       this.text = logAlert.text;
/*     */     }
/*     */     
/* 152 */     if (logAlert.err != null) {
/* 153 */       this.text = (this.text + "\n" + Debug.getExceptionMessage(logAlert.err));
/*     */     }
/*     */     
/* 156 */     if (logAlert.details != null) {
/* 157 */       this.text = (this.text + "\n<A HREF=\"details\">" + MessageText.getString("v3.MainWindow.button.viewdetails") + "</A>");
/*     */     }
/*     */     
/* 160 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 162 */         SystemWarningWindow.this.openWindow();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void openWindow() {
/* 168 */     Display display = this.parent.getDisplay();
/*     */     
/*     */ 
/* 171 */     this.shell = new Shell(this.parent, 4);
/* 172 */     this.shell.setLayout(new FormLayout());
/* 173 */     this.shell.setBackground(display.getSystemColor(29));
/* 174 */     this.shell.setForeground(display.getSystemColor(28));
/*     */     
/* 176 */     Menu menu = new Menu(this.shell);
/* 177 */     MenuItem menuItem = new MenuItem(menu, 8);
/* 178 */     Messages.setLanguageText(menuItem, "MyTorrentsView.menu.thisColumn.toClipboard");
/* 179 */     menuItem.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 181 */         ClipboardCopy.copyToClipBoard(SystemWarningWindow.this.logAlert.text + (SystemWarningWindow.this.logAlert.details == null ? "" : new StringBuilder().append("\n").append(SystemWarningWindow.this.logAlert.details).toString()));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/* 187 */     });
/* 188 */     this.shell.setMenu(menu);
/*     */     
/*     */ 
/* 191 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 192 */     this.imgClose = imageLoader.getImage("image.systemwarning.closeitem");
/* 193 */     this.boundsClose = this.imgClose.getBounds();
/*     */     
/* 195 */     GC gc = new GC(this.shell);
/*     */     
/* 197 */     FontData[] fontdata = gc.getFont().getFontData();
/* 198 */     fontdata[0].setHeight(fontdata[0].getHeight() + 1);
/* 199 */     fontdata[0].setStyle(1);
/* 200 */     this.fontTitle = new Font(display, fontdata);
/*     */     
/* 202 */     fontdata = gc.getFont().getFontData();
/* 203 */     fontdata[0].setHeight(fontdata[0].getHeight() - 1);
/* 204 */     this.fontCount = new Font(display, fontdata);
/*     */     
/* 206 */     this.shell.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 208 */         Utils.disposeSWTObjects(new Object[] { SystemWarningWindow.this.fontTitle, SystemWarningWindow.this.fontCount });
/*     */         
/*     */ 
/*     */ 
/* 212 */         SystemWarningWindow.numWarningWindowsOpen -= 1;
/*     */       }
/*     */       
/* 215 */     });
/* 216 */     Rectangle printArea = new Rectangle(this.BORDER_X, 0, this.WIDTH - this.BORDER_X * 2, 5000);
/*     */     
/* 218 */     this.spText = new GCStringPrinter(gc, this.text, printArea, true, false, 64);
/* 219 */     this.spText.setUrlColor(Colors.blues[9]);
/* 220 */     this.spText.calculateMetrics();
/*     */     
/* 222 */     gc.setFont(this.fontCount);
/* 223 */     String sCount = MessageText.getString("OpenTorrentWindow.xOfTotal", new String[] { "" + this.historyPosition + 1, "" + getWarningCount() });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 228 */     this.spCount = new GCStringPrinter(gc, sCount, printArea, true, false, 64);
/* 229 */     this.spCount.calculateMetrics();
/*     */     
/* 231 */     gc.setFont(this.fontTitle);
/* 232 */     this.spTitle = new GCStringPrinter(gc, this.title, printArea, true, false, 64);
/* 233 */     this.spTitle.calculateMetrics();
/*     */     
/* 235 */     gc.dispose();
/* 236 */     this.sizeText = this.spText.getCalculatedSize();
/* 237 */     this.sizeTitle = this.spTitle.getCalculatedSize();
/* 238 */     this.sizeCount = this.spCount.getCalculatedSize();
/*     */     
/*     */ 
/*     */ 
/* 242 */     Button btnDismiss = new Button(this.shell, 8);
/* 243 */     Messages.setLanguageText(btnDismiss, "Button.dismiss");
/* 244 */     final int btnHeight = btnDismiss.computeSize(-1, -1).y;
/*     */     
/* 246 */     Button btnPrev = new Button(this.shell, 8);
/* 247 */     btnPrev.setText("<");
/*     */     
/* 249 */     Button btnNext = new Button(this.shell, 8);
/* 250 */     btnNext.setText(">");
/*     */     
/* 252 */     FormData fd = new FormData();
/* 253 */     fd.bottom = new FormAttachment(100, -this.BORDER_Y1);
/* 254 */     fd.right = new FormAttachment(100, -this.BORDER_X);
/* 255 */     btnNext.setLayoutData(fd);
/*     */     
/* 257 */     fd = new FormData();
/* 258 */     fd.bottom = new FormAttachment(100, -this.BORDER_Y1);
/* 259 */     fd.right = new FormAttachment(btnNext, -this.BORDER_X);
/* 260 */     btnPrev.setLayoutData(fd);
/*     */     
/* 262 */     fd = new FormData();
/* 263 */     fd.bottom = new FormAttachment(100, -this.BORDER_Y1);
/* 264 */     fd.right = new FormAttachment(btnPrev, -this.BORDER_X);
/* 265 */     btnDismiss.setLayoutData(fd);
/*     */     
/* 267 */     this.height = (this.BORDER_Y0 + this.sizeTitle.y + this.GAP_Y + this.sizeText.y + this.GAP_Y_TITLE_COUNT + this.sizeCount.y + this.GAP_BUTTON_Y + btnHeight + this.BORDER_Y1);
/*     */     
/*     */ 
/* 270 */     Rectangle area = this.shell.computeTrim(this.ptBottomRight.x - this.WIDTH, this.ptBottomRight.y - this.height, this.WIDTH, this.height);
/*     */     
/* 272 */     this.shell.setBounds(area);
/* 273 */     this.shell.setLocation(this.ptBottomRight.x - area.width, this.ptBottomRight.y - area.height - 2);
/*     */     
/*     */ 
/* 276 */     this.rectX = new Rectangle(area.width - this.BORDER_X - this.boundsClose.width, this.BORDER_Y0, this.boundsClose.width, this.boundsClose.height);
/*     */     
/*     */ 
/* 279 */     this.shell.addMouseMoveListener(new MouseMoveListener() {
/* 280 */       int lastCursor = 0;
/*     */       
/*     */       public void mouseMove(MouseEvent e) {
/* 283 */         if ((SystemWarningWindow.this.shell == null) || (SystemWarningWindow.this.shell.isDisposed())) {
/* 284 */           return;
/*     */         }
/* 286 */         GCStringPrinter.URLInfo hitUrl = SystemWarningWindow.this.spText.getHitUrl(e.x, e.y);
/*     */         
/* 288 */         int cursor = (SystemWarningWindow.this.rectX.contains(e.x, e.y)) || (hitUrl != null) ? 21 : 0;
/*     */         
/* 290 */         if (cursor != this.lastCursor) {
/* 291 */           this.lastCursor = cursor;
/* 292 */           SystemWarningWindow.this.shell.setCursor(e.display.getSystemCursor(cursor));
/*     */         }
/*     */         
/*     */       }
/* 296 */     });
/* 297 */     this.shell.addMouseListener(new MouseListener() {
/*     */       public void mouseUp(MouseEvent e) {
/* 299 */         if ((SystemWarningWindow.this.shell == null) || (SystemWarningWindow.this.shell.isDisposed())) {
/* 300 */           return;
/*     */         }
/* 302 */         if (SystemWarningWindow.this.rectX.contains(e.x, e.y)) {
/* 303 */           SystemWarningWindow.this.shell.dispose();
/*     */         }
/* 305 */         GCStringPrinter.URLInfo hitUrl = SystemWarningWindow.this.spText.getHitUrl(e.x, e.y);
/* 306 */         if (hitUrl != null) {
/* 307 */           if (hitUrl.url.equals("details")) {
/* 308 */             MessageBoxShell mb = new MessageBoxShell(Constants.APP_NAME, SystemWarningWindow.this.logAlert.details, new String[] { MessageText.getString("Button.ok") }, 0);
/*     */             
/*     */ 
/*     */ 
/* 312 */             mb.setUseTextBox(true);
/* 313 */             mb.setParent(Utils.findAnyShell());
/* 314 */             mb.open(null);
/*     */           } else {
/* 316 */             Utils.launch(hitUrl.url);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void mouseDown(MouseEvent e) {}
/*     */       
/*     */ 
/*     */       public void mouseDoubleClick(MouseEvent e) {}
/* 327 */     });
/* 328 */     this.shell.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/* 330 */         e.gc.drawImage(SystemWarningWindow.this.imgClose, SystemWarningWindow.this.WIDTH - SystemWarningWindow.this.BORDER_X - SystemWarningWindow.this.boundsClose.width, SystemWarningWindow.this.BORDER_Y0);
/*     */         
/*     */ 
/*     */ 
/* 334 */         Rectangle printArea = new Rectangle(SystemWarningWindow.this.BORDER_X, SystemWarningWindow.this.BORDER_Y0 + SystemWarningWindow.this.sizeTitle.y + SystemWarningWindow.this.GAP_Y_TITLE_COUNT, SystemWarningWindow.this.WIDTH, 100);
/*     */         
/* 336 */         String sCount = MessageText.getString("OpenTorrentWindow.xOfTotal", new String[] { "" + (SystemWarningWindow.this.historyPosition + 1), "" + SystemWarningWindow.this.getWarningCount() });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 341 */         e.gc.setAlpha(180);
/* 342 */         Font lastFont = e.gc.getFont();
/* 343 */         e.gc.setFont(SystemWarningWindow.this.fontCount);
/* 344 */         SystemWarningWindow.this.spCount = new GCStringPrinter(e.gc, sCount, printArea, true, false, 192);
/*     */         
/* 346 */         SystemWarningWindow.this.spCount.printString();
/* 347 */         e.gc.setAlpha(255);
/* 348 */         SystemWarningWindow.this.sizeCount = SystemWarningWindow.this.spCount.getCalculatedSize();
/*     */         
/* 350 */         e.gc.setFont(lastFont);
/* 351 */         SystemWarningWindow.this.spText.printString(e.gc, new Rectangle(SystemWarningWindow.this.BORDER_X, SystemWarningWindow.this.BORDER_Y0 + SystemWarningWindow.this.sizeTitle.y + SystemWarningWindow.this.GAP_Y_TITLE_COUNT + SystemWarningWindow.this.sizeCount.y + SystemWarningWindow.this.GAP_Y, SystemWarningWindow.this.WIDTH - SystemWarningWindow.this.BORDER_X - SystemWarningWindow.this.BORDER_X, 5000), 192);
/*     */         
/*     */ 
/*     */ 
/* 355 */         e.gc.setFont(SystemWarningWindow.this.fontTitle);
/*     */         
/* 357 */         e.gc.setForeground(ColorCache.getColor(e.gc.getDevice(), "#54728c"));
/* 358 */         SystemWarningWindow.this.spTitle.printString(e.gc, new Rectangle(SystemWarningWindow.this.BORDER_X, SystemWarningWindow.this.BORDER_Y0, SystemWarningWindow.this.WIDTH - SystemWarningWindow.this.BORDER_X - SystemWarningWindow.this.BORDER_X, 5000), 192);
/*     */         
/*     */ 
/* 361 */         e.gc.setLineStyle(3);
/* 362 */         e.gc.setLineWidth(1);
/* 363 */         e.gc.setAlpha(180);
/* 364 */         e.gc.drawLine(SystemWarningWindow.this.BORDER_X, SystemWarningWindow.this.height - btnHeight - SystemWarningWindow.this.GAP_BUTTON_Y / 2 - SystemWarningWindow.this.BORDER_Y1, SystemWarningWindow.this.WIDTH - SystemWarningWindow.this.BORDER_X, SystemWarningWindow.this.height - btnHeight - SystemWarningWindow.this.GAP_BUTTON_Y / 2 - SystemWarningWindow.this.BORDER_Y1);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 370 */     });
/* 371 */     this.shell.addTraverseListener(new TraverseListener() {
/*     */       public void keyTraversed(TraverseEvent e) {
/* 373 */         if (e.detail == 2) {
/* 374 */           SystemWarningWindow.this.shell.dispose();
/* 375 */           return;
/*     */         }
/*     */         
/*     */       }
/* 379 */     });
/* 380 */     btnPrev.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 382 */         ArrayList<LogAlert> alerts = Alerts.getUnviewedLogAlerts();
/* 383 */         int pos = SystemWarningWindow.this.historyPosition - 1;
/* 384 */         if ((pos < 0) || (pos >= alerts.size())) {
/* 385 */           return;
/*     */         }
/*     */         
/* 388 */         new SystemWarningWindow((LogAlert)alerts.get(pos), SystemWarningWindow.this.ptBottomRight, SystemWarningWindow.this.parent, pos);
/* 389 */         SystemWarningWindow.this.shell.dispose();
/*     */       }
/*     */       
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/* 394 */     });
/* 395 */     btnPrev.setEnabled(this.historyPosition > 0);
/*     */     
/* 397 */     btnNext.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 399 */         ArrayList<LogAlert> alerts = Alerts.getUnviewedLogAlerts();
/* 400 */         int pos = SystemWarningWindow.this.historyPosition + 1;
/* 401 */         if (pos >= alerts.size()) {
/* 402 */           return;
/*     */         }
/*     */         
/* 405 */         new SystemWarningWindow((LogAlert)alerts.get(pos), SystemWarningWindow.this.ptBottomRight, SystemWarningWindow.this.parent, pos);
/* 406 */         SystemWarningWindow.this.shell.dispose();
/*     */       }
/*     */       
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/* 411 */     });
/* 412 */     ArrayList<LogAlert> alerts = Alerts.getUnviewedLogAlerts();
/* 413 */     btnNext.setEnabled(alerts.size() != this.historyPosition + 1);
/*     */     
/* 415 */     btnDismiss.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 417 */         ArrayList<LogAlert> alerts = Alerts.getUnviewedLogAlerts();
/* 418 */         for (int i = 0; (i < alerts.size()) && (i <= SystemWarningWindow.this.historyPosition); i++) {
/* 419 */           Alerts.markAlertAsViewed((LogAlert)alerts.get(i));
/*     */         }
/* 421 */         SystemWarningWindow.this.shell.dispose();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/* 427 */     });
/* 428 */     this.shell.open();
/* 429 */     numWarningWindowsOpen += 1;
/*     */   }
/*     */   
/*     */   private int getWarningCount() {
/* 433 */     ArrayList<LogAlert> historyList = Alerts.getUnviewedLogAlerts();
/* 434 */     return historyList.size();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/mainwindow/SystemWarningWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */