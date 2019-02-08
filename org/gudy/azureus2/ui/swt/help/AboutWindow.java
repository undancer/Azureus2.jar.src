/*     */ package org.gudy.azureus2.ui.swt.help;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import org.eclipse.swt.SWT;
/*     */ import org.eclipse.swt.custom.CLabel;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Caret;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Group;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.ClipboardCopy;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThread;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.SWTThreadAlreadyInstanciatedException;
/*     */ import org.gudy.azureus2.update.CorePatchLevel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AboutWindow
/*     */ {
/*     */   private static final String IMG_SPLASH = "azureus_splash";
/*     */   static Image image;
/*  51 */   static AEMonitor class_mon = new AEMonitor("AboutWindow");
/*     */   private static Shell instance;
/*     */   private static Image imgSrc;
/*  54 */   private static int paintColorTo = 0;
/*  55 */   private static int paintColorDir = 2;
/*     */   private static Image imageToDispose;
/*     */   
/*     */   public static void show()
/*     */   {
/*  60 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */       public void runSupport() {}
/*     */     });
/*     */   }
/*     */   
/*     */   private static void _show()
/*     */   {
/*  68 */     if (instance != null)
/*     */     {
/*  70 */       instance.open();
/*  71 */       return;
/*     */     }
/*     */     
/*  74 */     paintColorTo = 0;
/*     */     
/*  76 */     Shell window = ShellFactory.createMainShell(2144);
/*  77 */     Utils.setShellIcon(window);
/*  78 */     final Display display = window.getDisplay();
/*     */     
/*  80 */     window.setText(MessageText.getString("MainWindow.about.title") + " " + Constants.getCurrentVersion());
/*     */     
/*  82 */     window.setLayout(new GridLayout(2, false));
/*     */     
/*  84 */     ImageLoader imageLoader = ImageLoader.getInstance();
/*  85 */     imgSrc = imageLoader.getImage("azureus_splash");
/*  86 */     if (imgSrc != null) {
/*  87 */       int w = imgSrc.getBounds().width;
/*  88 */       int ow = w;
/*  89 */       int h = imgSrc.getBounds().height;
/*     */       
/*  91 */       Image imgGray = new Image(display, imageLoader.getImage("azureus_splash"), 2);
/*     */       
/*  93 */       imageLoader.releaseImage("azureus_splash");
/*  94 */       GC gc = new GC(imgGray);
/*  95 */       if (Constants.isOSX) {
/*  96 */         gc.drawImage(imgGray, (w - ow) / 2, 0);
/*     */       } else {
/*  98 */         gc.copyArea(0, 0, ow, h, (w - ow) / 2, 0);
/*     */       }
/* 100 */       gc.dispose();
/*     */       
/* 102 */       Image image2 = new Image(display, w, h);
/* 103 */       gc = new GC(image2);
/* 104 */       gc.setBackground(window.getBackground());
/* 105 */       gc.fillRectangle(image2.getBounds());
/* 106 */       gc.dispose();
/* 107 */       imageToDispose = image = Utils.renderTransparency(display, image2, imgGray, new Point(0, 0), 180);
/* 108 */       image2.dispose();
/* 109 */       imgGray.dispose();
/*     */     }
/*     */     
/* 112 */     Canvas labelImage = new Canvas(window, 536870912);
/*     */     
/* 114 */     GridData gridData = new GridData(272);
/* 115 */     gridData.horizontalSpan = 2;
/* 116 */     gridData.horizontalIndent = (gridData.verticalIndent = 0);
/* 117 */     final Rectangle imgBounds = image.getBounds();
/* 118 */     final Rectangle boundsColor = imgSrc.getBounds();
/* 119 */     gridData.widthHint = Utils.adjustPXForDPI(300);
/* 120 */     gridData.heightHint = (imgBounds.height + imgBounds.y + 20);
/* 121 */     labelImage.setLayoutData(gridData);
/* 122 */     labelImage.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/*     */         try {
/* 125 */           Rectangle clipping = e.gc.getClipping();
/* 126 */           int ofs = (this.val$labelImage.getSize().x - boundsColor.width) / 2;
/* 127 */           if (AboutWindow.paintColorTo > 0) {
/* 128 */             e.gc.drawImage(AboutWindow.imgSrc, 0, 0, AboutWindow.paintColorTo, boundsColor.height, ofs, 10, AboutWindow.paintColorTo, boundsColor.height);
/*     */           }
/*     */           
/* 131 */           if ((clipping.x + clipping.width > ofs + AboutWindow.paintColorTo) && (imgBounds.width - AboutWindow.paintColorTo - 1 > 0)) {
/* 132 */             e.gc.drawImage(AboutWindow.image, AboutWindow.paintColorTo + 1, 0, imgBounds.width - AboutWindow.paintColorTo - 1, imgBounds.height, AboutWindow.paintColorTo + 1 + ofs, 10, imgBounds.width - AboutWindow.paintColorTo - 1, imgBounds.height);
/*     */ 
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         catch (Throwable f) {}
/*     */       }
/*     */       
/*     */ 
/* 142 */     });
/* 143 */     Group gInternet = new Group(window, 0);
/* 144 */     GridLayout gridLayout = new GridLayout();
/* 145 */     gridLayout.numColumns = 2;
/* 146 */     gridLayout.makeColumnsEqualWidth = true;
/* 147 */     gInternet.setLayout(gridLayout);
/* 148 */     Messages.setLanguageText(gInternet, "MainWindow.about.section.internet");
/* 149 */     gridData = new GridData(272);
/* 150 */     Utils.setLayoutData(gInternet, gridData);
/*     */     
/* 152 */     Group gSys = new Group(window, 0);
/* 153 */     gSys.setLayout(new GridLayout());
/* 154 */     Messages.setLanguageText(gSys, "MainWindow.about.section.system");
/* 155 */     gridData = new GridData(272);
/* 156 */     gridData.verticalSpan = 1;
/* 157 */     Utils.setLayoutData(gSys, gridData);
/*     */     
/* 159 */     String swt = "";
/* 160 */     if (Utils.isGTK) {
/*     */       try {
/* 162 */         swt = "/" + System.getProperty("org.eclipse.swt.internal.gtk.version");
/*     */       }
/*     */       catch (Throwable e1) {}
/*     */     }
/*     */     
/*     */ 
/* 168 */     Text txtSysInfo = new Text(gSys, 74);
/* 169 */     txtSysInfo.setBackground(display.getSystemColor(22));
/*     */     
/* 171 */     String about_text = "Java " + System.getProperty("java.version") + " (" + (Constants.is64Bit ? 64 : 32) + " bit)\n  " + System.getProperty("java.vendor") + "\n" + System.getProperty("java.home") + "\n\n" + "SWT v" + SWT.getVersion() + ", " + SWT.getPlatform() + swt + "\n" + System.getProperty("os.name") + " v" + System.getProperty("os.version") + ", " + System.getProperty("os.arch") + " (" + (Constants.isOS64Bit ? 64 : 32) + " bit)\n" + Constants.APP_NAME.charAt(0) + Constants.getCurrentVersion() + ("".length() == 0 ? "" : "-") + "/" + CorePatchLevel.getCurrentPatchLevel() + " " + COConfigurationManager.getStringParameter("ui");
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 183 */     txtSysInfo.setText(about_text);
/* 184 */     Utils.setLayoutData(txtSysInfo, gridData = new GridData(1808));
/* 185 */     if (window.getCaret() != null) {
/* 186 */       window.getCaret().setVisible(false);
/*     */     }
/* 188 */     String[][] link = { { "homepage", "bugreports", "forumdiscussion", "wiki", "!Vuze Wiki Hidden Service (I2P)", "!Vuze Wiki Hidden Service (Tor)", "contributors", "!EULA", "!Privacy Policy", "!Legal", "!FOSS Licenses" }, { "https://www.vuze.com", "http://www.vuze.com/forums/open-development", "http://forum.vuze.com", "http://wiki.vuze.com/w/", "http://que23xpe7o3lzq6auv6stb4bha7ddavrlgqdv2cuhgd36fgfmp6q.b32.i2p/", "http://dr5aamfveql2b34p.onion/", "http://wiki.vuze.com/w/Contributors", "https://www.vuze.com/corp/terms.php", "https://www.vuze.com/corp/privacy.php", "https://www.vuze.com/corp/legal", "http://wiki.vuze.com/w/Vuze_Client_FOSS_Licenses" } };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 217 */     for (int i = 0; i < link[0].length; i++) {
/* 218 */       CLabel linkLabel = new CLabel(gInternet, 0);
/* 219 */       if (link[0][i].startsWith("!")) {
/* 220 */         linkLabel.setText(link[0][i].substring(1));
/*     */       } else {
/* 222 */         linkLabel.setText(MessageText.getString("MainWindow.about.internet." + link[0][i]));
/*     */       }
/* 224 */       linkLabel.setData(link[1][i]);
/* 225 */       linkLabel.setCursor(display.getSystemCursor(21));
/* 226 */       linkLabel.setForeground(Colors.blue);
/* 227 */       gridData = new GridData(768);
/* 228 */       gridData.horizontalSpan = 1;
/* 229 */       Utils.setLayoutData(linkLabel, gridData);
/* 230 */       linkLabel.addMouseListener(new MouseAdapter() {
/*     */         public void mouseDoubleClick(MouseEvent arg0) {
/* 232 */           Utils.launch((String)((CLabel)arg0.widget).getData());
/*     */         }
/*     */         
/* 235 */         public void mouseUp(MouseEvent arg0) { Utils.launch((String)((CLabel)arg0.widget).getData());
/*     */         }
/* 237 */       });
/* 238 */       ClipboardCopy.addCopyToClipMenu(linkLabel);
/*     */     }
/*     */     
/* 241 */     Label labelOwner = new Label(window, 16777280);
/* 242 */     gridData = new GridData(272);
/* 243 */     gridData.horizontalSpan = 2;
/* 244 */     gridData.horizontalIndent = (gridData.verticalIndent = 0);
/* 245 */     Utils.setLayoutData(labelOwner, gridData);
/* 246 */     labelOwner.setText(MessageText.getString("MainWindow.about.product.info"));
/*     */     
/*     */ 
/* 249 */     Listener keyListener = new Listener() {
/*     */       public void handleEvent(Event e) {
/* 251 */         if (e.character == '\033') {
/* 252 */           this.val$window.dispose();
/*     */         }
/*     */         
/*     */       }
/* 256 */     };
/* 257 */     window.addListener(2, keyListener);
/*     */     
/* 259 */     window.pack();
/* 260 */     txtSysInfo.setFocus();
/* 261 */     Utils.centreWindow(window);
/* 262 */     window.open();
/*     */     
/* 264 */     instance = window;
/* 265 */     window.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent event) {
/* 267 */         AboutWindow.access$302(null);
/* 268 */         AboutWindow.disposeImage();
/*     */       }
/*     */       
/* 271 */     });
/* 272 */     final int maxX = image.getBounds().width;
/* 273 */     final int maxY = image.getBounds().height;
/* 274 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 276 */         if ((AboutWindow.image == null) || (AboutWindow.image.isDisposed()) || (this.val$labelImage.isDisposed())) {
/* 277 */           return;
/*     */         }
/* 279 */         if (display.isDisposed()) {
/* 280 */           return;
/*     */         }
/* 282 */         AboutWindow.access$112(AboutWindow.paintColorDir);
/*     */         
/* 284 */         Utils.execSWTThreadLater(7 * AboutWindow.paintColorDir, this);
/*     */         
/* 286 */         int ofs = (this.val$labelImage.getSize().x - boundsColor.width) / 2;
/* 287 */         this.val$labelImage.redraw(AboutWindow.paintColorTo - AboutWindow.paintColorDir + ofs, 10, AboutWindow.paintColorDir, maxY, true);
/*     */         
/* 289 */         if ((AboutWindow.paintColorTo >= maxX) || (AboutWindow.paintColorTo <= 0)) {
/* 290 */           AboutWindow.access$102(0);
/*     */           
/* 292 */           Image tmp = AboutWindow.image;
/* 293 */           AboutWindow.image = AboutWindow.imgSrc;
/* 294 */           AboutWindow.access$202(tmp);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public static void disposeImage()
/*     */   {
/*     */     try
/*     */     {
/* 305 */       class_mon.enter();
/* 306 */       Utils.disposeSWTObjects(new Object[] { imageToDispose });
/*     */       
/*     */ 
/* 309 */       ImageLoader imageLoader = ImageLoader.getInstance();
/* 310 */       imageLoader.releaseImage("azureus_splash");
/* 311 */       image = null;
/* 312 */       imgSrc = null;
/*     */     }
/*     */     finally {
/* 315 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/*     */     try {
/* 321 */       Display display = new Display();
/* 322 */       Colors.getInstance();
/* 323 */       SWTThread.createInstance(null);
/* 324 */       show();
/*     */       
/* 326 */       while ((!display.isDisposed()) && (instance != null) && (!instance.isDisposed())) {
/* 327 */         if (!display.readAndDispatch()) {
/* 328 */           display.sleep();
/*     */         }
/*     */       }
/*     */       
/* 332 */       if (!display.isDisposed()) {
/* 333 */         display.dispose();
/*     */       }
/*     */     }
/*     */     catch (SWTThreadAlreadyInstanciatedException e) {
/* 337 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/help/AboutWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */