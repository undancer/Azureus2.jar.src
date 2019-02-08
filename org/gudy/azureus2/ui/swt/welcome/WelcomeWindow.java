/*     */ package org.gudy.azureus2.ui.swt.welcome;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.Locale;
/*     */ import org.eclipse.swt.custom.StyleRange;
/*     */ import org.eclipse.swt.custom.StyledText;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl;
/*     */ import org.gudy.azureus2.ui.swt.BrowserWrapper;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class WelcomeWindow
/*     */ {
/*  56 */   private static final String lineSeparator = System.getProperty("line.separator");
/*     */   
/*     */   Display display;
/*     */   Shell shell;
/*     */   Color black;
/*     */   Color white;
/*     */   Color light;
/*     */   Color grey;
/*     */   Color green;
/*     */   
/*     */   public WelcomeWindow(Shell parentShell)
/*     */   {
/*     */     try
/*     */     {
/*  70 */       init(parentShell);
/*     */     }
/*     */     catch (Throwable t) {}
/*     */   }
/*     */   
/*     */   public void init(Shell parentShell) {
/*  76 */     this.shell = ShellFactory.createShell(parentShell, 2160);
/*  77 */     Utils.setShellIcon(this.shell);
/*  78 */     if (Constants.isOSX) {
/*  79 */       this.monospace = new Font(this.shell.getDisplay(), "Courier", 12, 0);
/*     */     } else {
/*  81 */       this.monospace = new Font(this.shell.getDisplay(), "Courier New", 8, 0);
/*     */     }
/*  83 */     this.shell.setText(MessageText.getString("window.welcome.title", new String[] { "5.7.6.0" }));
/*     */     
/*  85 */     this.display = this.shell.getDisplay();
/*     */     
/*  87 */     GridLayout layout = new GridLayout();
/*  88 */     this.shell.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/*  92 */     this.cWhatsNew = new Composite(this.shell, 2048);
/*  93 */     GridData data = new GridData(1808);
/*  94 */     Utils.setLayoutData(this.cWhatsNew, data);
/*  95 */     this.cWhatsNew.setLayout(new FillLayout());
/*     */     
/*  97 */     Button bClose = new Button(this.shell, 8);
/*  98 */     bClose.setText(MessageText.getString("Button.close"));
/*  99 */     data = new GridData();
/* 100 */     data.widthHint = 70;
/* 101 */     data.horizontalAlignment = (Constants.isOSX ? 16777216 : 131072);
/* 102 */     Utils.setLayoutData(bClose, data);
/*     */     
/* 104 */     Listener closeListener = new Listener() {
/*     */       public void handleEvent(Event event) {
/* 106 */         WelcomeWindow.this.close();
/*     */       }
/*     */       
/* 109 */     };
/* 110 */     bClose.addListener(13, closeListener);
/* 111 */     this.shell.addListener(21, closeListener);
/*     */     
/* 113 */     this.shell.setDefaultButton(bClose);
/*     */     
/* 115 */     this.shell.addListener(31, new Listener() {
/*     */       public void handleEvent(Event e) {
/* 117 */         if (e.character == '\033') {
/* 118 */           WelcomeWindow.this.close();
/*     */         }
/*     */         
/*     */       }
/* 122 */     });
/* 123 */     this.shell.setSize(750, 500);
/* 124 */     Utils.centreWindow(this.shell);
/* 125 */     this.shell.layout();
/* 126 */     this.shell.open();
/* 127 */     pullWhatsNew(this.cWhatsNew);
/*     */   }
/*     */   
/*     */   private void pullWhatsNew(Composite cWhatsNew) {
/* 131 */     this.labelLoading = new Label(cWhatsNew, 16777216);
/* 132 */     this.labelLoading.setText(MessageText.getString("installPluginsWizard.details.loading"));
/* 133 */     this.shell.layout(true, true);
/* 134 */     this.shell.update();
/*     */     
/* 136 */     getWhatsNew(1);
/*     */   }
/*     */   
/*     */   public void setWhatsNew() {
/* 140 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 142 */         WelcomeWindow.this._setWhatsNew();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void _setWhatsNew()
/*     */   {
/* 149 */     if ((this.sWhatsNew.contains("<html")) || (this.sWhatsNew.contains("<HTML"))) {
/* 150 */       BrowserWrapper browser = Utils.createSafeBrowser(this.cWhatsNew, 0);
/* 151 */       if (browser != null) {
/* 152 */         browser.setText(this.sWhatsNew);
/*     */       } else {
/*     */         try {
/* 155 */           File tempFile = File.createTempFile("AZU", ".html");
/* 156 */           tempFile.deleteOnExit();
/* 157 */           FileUtil.writeBytesAsFile(tempFile.getAbsolutePath(), this.sWhatsNew.getBytes("utf8"));
/*     */           
/* 159 */           Utils.launch(tempFile.getAbsolutePath());
/* 160 */           this.shell.dispose();
/* 161 */           return;
/*     */         }
/*     */         catch (IOException e) {}
/*     */       }
/*     */     }
/*     */     else {
/* 167 */       StyledText helpPanel = new StyledText(this.cWhatsNew, 768);
/*     */       
/* 169 */       helpPanel.setEditable(false);
/*     */       try {
/* 171 */         helpPanel.setRedraw(false);
/* 172 */         helpPanel.setWordWrap(false);
/* 173 */         helpPanel.setFont(this.monospace);
/*     */         
/* 175 */         this.black = ColorCache.getColor(this.display, 0, 0, 0);
/* 176 */         this.white = ColorCache.getColor(this.display, 255, 255, 255);
/* 177 */         this.light = ColorCache.getColor(this.display, 200, 200, 200);
/* 178 */         this.grey = ColorCache.getColor(this.display, 50, 50, 50);
/* 179 */         this.green = ColorCache.getColor(this.display, 30, 80, 30);
/* 180 */         this.blue = ColorCache.getColor(this.display, 20, 20, 80);
/*     */         
/*     */ 
/*     */ 
/* 184 */         helpPanel.setForeground(this.grey);
/*     */         
/* 186 */         String[] lines = this.sWhatsNew.split("\\r?\\n");
/* 187 */         for (int i = 0; i < lines.length; i++) {
/* 188 */           String line = lines[i];
/*     */           
/* 190 */           boolean setStyle = false;
/* 191 */           this.fg = this.grey;
/* 192 */           this.bg = this.white;
/* 193 */           int style = 0;
/*     */           
/*     */           String text;
/*     */           char styleChar;
/*     */           String text;
/* 198 */           if (line.length() < 2) {
/* 199 */             char styleChar = ' ';
/* 200 */             text = " " + lineSeparator;
/*     */           } else {
/* 202 */             styleChar = line.charAt(0);
/* 203 */             text = line.substring(1) + lineSeparator;
/*     */           }
/*     */           
/* 206 */           switch (styleChar) {
/*     */           case '*': 
/* 208 */             text = "  * " + text;
/* 209 */             this.fg = this.green;
/* 210 */             setStyle = true;
/* 211 */             break;
/*     */           case '+': 
/* 213 */             text = "     " + text;
/* 214 */             this.fg = this.black;
/* 215 */             this.bg = this.light;
/* 216 */             style = 1;
/* 217 */             setStyle = true;
/* 218 */             break;
/*     */           case '!': 
/* 220 */             style = 1;
/* 221 */             setStyle = true;
/* 222 */             break;
/*     */           case '@': 
/* 224 */             this.fg = this.blue;
/* 225 */             setStyle = true;
/* 226 */             break;
/*     */           case '$': 
/* 228 */             this.bg = this.blue;
/* 229 */             this.fg = this.white;
/* 230 */             style = 1;
/* 231 */             setStyle = true;
/* 232 */             break;
/*     */           case ' ': 
/* 234 */             text = "  " + text;
/* 235 */             break;
/*     */           
/*     */           default: 
/* 238 */             text = styleChar + text;
/*     */           }
/*     */           
/* 241 */           helpPanel.append(text);
/*     */           
/* 243 */           if (setStyle) {
/* 244 */             int lineCount = helpPanel.getLineCount() - 1;
/* 245 */             int charCount = helpPanel.getCharCount();
/*     */             
/*     */ 
/* 248 */             int lineOfs = helpPanel.getOffsetAtLine(lineCount - 1);
/* 249 */             int lineLen = charCount - lineOfs;
/*     */             
/* 251 */             helpPanel.setStyleRange(new StyleRange(lineOfs, lineLen, this.fg, this.bg, style));
/*     */             
/* 253 */             helpPanel.setLineBackground(lineCount - 1, 1, this.bg);
/*     */           }
/*     */         }
/*     */         
/* 257 */         helpPanel.setRedraw(true);
/*     */       } catch (Exception e) {
/* 259 */         System.out.println("Unable to load help contents because:" + e);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 264 */     if ((this.labelLoading != null) && (!this.labelLoading.isDisposed())) {
/* 265 */       this.labelLoading.dispose();
/*     */     }
/* 267 */     this.shell.layout(true, true);
/*     */   }
/*     */   
/*     */   private void getWhatsNew(final int phase) {
/* 271 */     String helpFile = null;
/* 272 */     if (phase == 1) {
/* 273 */       helpFile = MessageText.getString("window.welcome.file");
/*     */       
/* 275 */       if (!helpFile.toLowerCase().startsWith("http://plugins.vuze.com/")) {
/* 276 */         getWhatsNew(2);
/*     */       }
/*     */     }
/*     */     else {
/* 280 */       helpFile = MessageText.getString("window.welcome.file");
/*     */       
/*     */ 
/* 283 */       InputStream stream = getClass().getResourceAsStream(helpFile);
/* 284 */       if (stream == null) {
/* 285 */         String helpFullPath = "/org/gudy/azureus2/internat/whatsnew/" + helpFile;
/* 286 */         stream = getClass().getResourceAsStream(helpFullPath);
/*     */       }
/* 288 */       if (stream == null) {
/* 289 */         stream = getClass().getResourceAsStream("/ChangeLog.txt");
/*     */       }
/* 291 */       if (stream == null) {
/* 292 */         this.sWhatsNew = ("Welcome Window: Error loading resource: " + helpFile);
/*     */       } else {
/*     */         try {
/* 295 */           this.sWhatsNew = FileUtil.readInputStreamAsString(stream, 65535, "utf8");
/* 296 */           stream.close();
/*     */         } catch (IOException e) {
/* 298 */           Debug.out(e);
/*     */         }
/*     */       }
/* 301 */       setWhatsNew();
/* 302 */       return;
/*     */     }
/*     */     
/* 305 */     final String url = helpFile;
/*     */     
/* 307 */     new AEThread2("getWhatsNew", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 312 */         ResourceDownloaderFactory rdf = ResourceDownloaderFactoryImpl.getSingleton();
/*     */         String s;
/* 314 */         try { ResourceDownloader rd = rdf.create(new URL(url));
/* 315 */           InputStream is = rd.download();
/* 316 */           int length = is.available();
/* 317 */           byte[] data = new byte[length];
/* 318 */           is.read(data);
/* 319 */           is.close();
/* 320 */           s = new String(data);
/*     */ 
/*     */         }
/*     */         catch (ResourceDownloaderException rde)
/*     */         {
/*     */ 
/* 326 */           s = "";
/*     */         } catch (Exception e) {
/* 328 */           Debug.out(e);
/* 329 */           s = "";
/*     */         }
/* 331 */         WelcomeWindow.this.sWhatsNew = s;
/*     */         
/* 333 */         if ((WelcomeWindow.this.sWhatsNew == null) || (WelcomeWindow.this.sWhatsNew.length() == 0)) {
/* 334 */           WelcomeWindow.this.getWhatsNew(phase + 1);
/* 335 */           return;
/*     */         }
/*     */         
/* 338 */         Utils.execSWTThread(new AERunnable() {
/*     */           public void runSupport() {
/* 340 */             if ((WelcomeWindow.this.cWhatsNew != null) && (!WelcomeWindow.this.cWhatsNew.isDisposed())) {
/* 341 */               WelcomeWindow.this.setWhatsNew();
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */   private void close()
/*     */   {
/* 351 */     this.monospace.dispose();
/* 352 */     this.shell.dispose(); }
/*     */   
/*     */   Color blue;
/*     */   Color fg;
/*     */   Color bg;
/*     */   
/* 358 */   public static void main(String[] args) { System.out.println(Locale.getDefault().getCountry());
/* 359 */     new WelcomeWindow(null);
/* 360 */     Display display = Display.getDefault();
/*     */     for (;;) {
/* 362 */       if (!display.readAndDispatch()) {
/* 363 */         display.sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   String sWhatsNew;
/*     */   Font monospace;
/*     */   private Composite cWhatsNew;
/*     */   private Label labelLoading;
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/welcome/WelcomeWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */