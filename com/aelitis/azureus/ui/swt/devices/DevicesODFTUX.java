/*     */ package com.aelitis.azureus.ui.swt.devices;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.devices.DeviceManagerException;
/*     */ import com.aelitis.azureus.core.devices.DeviceOfflineDownloader;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.util.ConstantsVuze;
/*     */ import java.net.URLEncoder;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
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
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.LinkLabel;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DevicesODFTUX
/*     */ {
/*     */   private static final String URL_LEARN_MORE = "/devices/offlinedownloader.start";
/*     */   private DeviceOfflineDownloader device;
/*     */   private Display display;
/*     */   private Shell shell;
/*     */   private Font boldFont;
/*     */   private Font titleFont;
/*     */   private Font subTitleFont;
/*     */   private Font textInputFont;
/*     */   private Button turnOnButton;
/*     */   private Label noSpaceWarning;
/*     */   private String dev_image_key;
/*     */   private ImageLoader imageLoader;
/*     */   
/*     */   protected DevicesODFTUX(DeviceOfflineDownloader _device)
/*     */     throws DeviceManagerException
/*     */   {
/*  83 */     this.device = _device;
/*     */     
/*  85 */     final long avail = this.device.getSpaceAvailable(false);
/*     */     
/*  87 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/*     */ 
/*     */       public void runSupport()
/*     */       {
/*     */ 
/*  93 */         DevicesODFTUX.this.open(avail == 0L);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void open(boolean no_space_available)
/*     */   {
/* 103 */     this.imageLoader = ImageLoader.getInstance();
/*     */     
/* 105 */     this.shell = ShellFactory.createMainShell(112);
/*     */     
/* 107 */     this.shell.setSize(650, 400);
/*     */     
/* 109 */     Utils.centreWindow(this.shell);
/*     */     
/* 111 */     this.shell.setMinimumSize(550, 400);
/*     */     
/* 113 */     this.display = this.shell.getDisplay();
/*     */     
/* 115 */     Utils.setShellIcon(this.shell);
/*     */     
/* 117 */     createFonts();
/*     */     
/* 119 */     this.shell.setText(MessageText.getString("devices.activation"));
/*     */     
/* 121 */     this.shell.addListener(12, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 124 */         DevicesODFTUX.this.imageLoader.releaseImage("wizard_header_bg");
/*     */         
/* 126 */         if (DevicesODFTUX.this.dev_image_key != null)
/*     */         {
/* 128 */           DevicesODFTUX.this.imageLoader.releaseImage(DevicesODFTUX.this.dev_image_key);
/*     */         }
/*     */         
/* 131 */         if ((DevicesODFTUX.this.titleFont != null) && (!DevicesODFTUX.this.titleFont.isDisposed())) {
/* 132 */           DevicesODFTUX.this.titleFont.dispose();
/*     */         }
/*     */         
/* 135 */         if ((DevicesODFTUX.this.textInputFont != null) && (!DevicesODFTUX.this.textInputFont.isDisposed())) {
/* 136 */           DevicesODFTUX.this.textInputFont.dispose();
/*     */         }
/*     */         
/* 139 */         if ((DevicesODFTUX.this.boldFont != null) && (!DevicesODFTUX.this.boldFont.isDisposed())) {
/* 140 */           DevicesODFTUX.this.boldFont.dispose();
/*     */         }
/*     */         
/* 143 */         if ((DevicesODFTUX.this.subTitleFont != null) && (!DevicesODFTUX.this.subTitleFont.isDisposed())) {
/* 144 */           DevicesODFTUX.this.subTitleFont.dispose();
/*     */         }
/*     */         
/*     */       }
/* 148 */     });
/* 149 */     Composite header = new Composite(this.shell, 0);
/* 150 */     header.setBackgroundMode(1);
/* 151 */     header.setBackgroundImage(this.imageLoader.getImage("wizard_header_bg"));
/*     */     
/* 153 */     Label topSeparator = new Label(this.shell, 258);
/*     */     
/* 155 */     Composite main = new Composite(this.shell, 0);
/*     */     
/* 157 */     main.setBackground(Colors.white);
/*     */     
/* 159 */     Label bottomSeparator = new Label(this.shell, 258);
/*     */     
/* 161 */     Composite footer = new Composite(this.shell, 0);
/*     */     
/* 163 */     FormLayout layout = new FormLayout();
/* 164 */     this.shell.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 168 */     FormData data = new FormData();
/* 169 */     data.top = new FormAttachment(0, 0);
/* 170 */     data.left = new FormAttachment(0, 0);
/* 171 */     data.right = new FormAttachment(100, 0);
/*     */     
/* 173 */     header.setLayoutData(data);
/*     */     
/* 175 */     data = new FormData();
/* 176 */     data.top = new FormAttachment(header, 0);
/* 177 */     data.left = new FormAttachment(0, 0);
/* 178 */     data.right = new FormAttachment(100, 0);
/* 179 */     topSeparator.setLayoutData(data);
/*     */     
/* 181 */     data = new FormData();
/* 182 */     data.top = new FormAttachment(topSeparator, 0);
/* 183 */     data.left = new FormAttachment(0, 0);
/* 184 */     data.right = new FormAttachment(100, 0);
/* 185 */     data.bottom = new FormAttachment(bottomSeparator, 0);
/* 186 */     main.setLayoutData(data);
/*     */     
/* 188 */     data = new FormData();
/* 189 */     data.left = new FormAttachment(0, 0);
/* 190 */     data.right = new FormAttachment(100, 0);
/* 191 */     data.bottom = new FormAttachment(footer, 0);
/* 192 */     bottomSeparator.setLayoutData(data);
/*     */     
/* 194 */     data = new FormData();
/* 195 */     data.bottom = new FormAttachment(100, 0);
/* 196 */     data.left = new FormAttachment(0, 0);
/* 197 */     data.right = new FormAttachment(100, 0);
/* 198 */     footer.setLayoutData(data);
/*     */     
/* 200 */     populateHeader(header);
/*     */     
/* 202 */     populateMain(main, no_space_available);
/*     */     
/* 204 */     populateFooter(footer, no_space_available);
/*     */     
/* 206 */     this.shell.setDefaultButton(this.turnOnButton);
/*     */     
/* 208 */     this.shell.layout();
/*     */     
/* 210 */     Utils.centreWindow(this.shell);
/*     */     
/* 212 */     this.turnOnButton.setFocus();
/*     */     
/* 214 */     this.shell.open();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void populateHeader(Composite header)
/*     */   {
/* 221 */     header.setBackground(this.display.getSystemColor(1));
/*     */     
/* 223 */     Label title = new Label(header, 64);
/*     */     
/* 225 */     title.setFont(this.titleFont);
/*     */     
/* 227 */     title.setText(MessageText.getString("devices.turnon.title"));
/*     */     
/* 229 */     FillLayout layout = new FillLayout();
/*     */     
/* 231 */     layout.marginHeight = 10;
/*     */     
/* 233 */     layout.marginWidth = 10;
/*     */     
/* 235 */     header.setLayout(layout);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void populateMain(Composite main, boolean no_space_available)
/*     */   {
/* 244 */     String manufacturer = this.device.getManufacturer();
/*     */     
/* 246 */     boolean is_belkin = manufacturer.toLowerCase().contains("belkin");
/*     */     
/* 248 */     Label image_area = new Label(main, 0);
/*     */     
/*     */     String router_text;
/*     */     String router_text;
/* 252 */     if (is_belkin)
/*     */     {
/* 254 */       this.dev_image_key = "image.device.logo.belkin";
/*     */       
/* 256 */       router_text = MessageText.getString("devices.router");
/*     */     }
/*     */     else {
/* 259 */       router_text = MessageText.getString("devices.od");
/*     */     }
/*     */     
/* 262 */     if (this.dev_image_key != null) {
/* 263 */       image_area.setImage(this.imageLoader.getImage(this.dev_image_key));
/*     */     }
/*     */     
/* 266 */     Label text1 = new Label(main, 64);
/* 267 */     text1.setBackground(Colors.white);
/* 268 */     text1.setFont(this.textInputFont);
/* 269 */     text1.setText(MessageText.getString("devices.od.turnon.text1", new String[] { (is_belkin ? "Belkin" : "Vuze") + " " + router_text }));
/*     */     
/* 271 */     Label text2 = new Label(main, 64);
/* 272 */     text2.setBackground(Colors.white);
/* 273 */     text2.setFont(this.textInputFont);
/* 274 */     text2.setText(MessageText.getString("devices.od.turnon.text2", new String[] { router_text }));
/*     */     
/* 276 */     this.noSpaceWarning = new Label(main, 64);
/* 277 */     this.noSpaceWarning.setBackground(Colors.white);
/* 278 */     this.noSpaceWarning.setFont(this.textInputFont);
/* 279 */     this.noSpaceWarning.setText(MessageText.getString("devices.od.turnon.text3", new String[] { router_text }));
/* 280 */     this.noSpaceWarning.setForeground(Colors.red);
/* 281 */     this.noSpaceWarning.setVisible(no_space_available);
/*     */     
/* 283 */     Label link = new Label(main, 64);
/* 284 */     link.setBackground(Colors.white);
/* 285 */     link.setFont(this.textInputFont);
/* 286 */     link.setText(MessageText.getString("devices.od.turnon.learn"));
/*     */     
/* 288 */     String url = "/devices/offlinedownloader.start";
/*     */     try
/*     */     {
/* 291 */       url = url + "?man=" + URLEncoder.encode(manufacturer, "UTF-8");
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 295 */       Debug.out(e);
/*     */     }
/*     */     
/* 298 */     url = ConstantsVuze.getDefaultContentNetwork().getExternalSiteRelativeURL(url, true);
/*     */     
/* 300 */     LinkLabel.makeLinkedLabel(link, url);
/*     */     
/*     */ 
/* 303 */     FormLayout layout = new FormLayout();
/* 304 */     layout.marginHeight = 5;
/* 305 */     layout.marginWidth = 50;
/* 306 */     layout.spacing = 5;
/*     */     
/* 308 */     main.setLayout(layout);
/*     */     
/*     */ 
/* 311 */     FormData data = new FormData();
/* 312 */     data.top = new FormAttachment(0, 20);
/* 313 */     data.left = new FormAttachment(0);
/* 314 */     image_area.setLayoutData(data);
/*     */     
/* 316 */     data = new FormData();
/* 317 */     data.top = new FormAttachment(image_area, 10);
/* 318 */     data.left = new FormAttachment(0);
/* 319 */     data.right = new FormAttachment(100);
/* 320 */     text1.setLayoutData(data);
/*     */     
/* 322 */     data = new FormData();
/* 323 */     data.top = new FormAttachment(text1, 10);
/* 324 */     data.left = new FormAttachment(0);
/* 325 */     data.right = new FormAttachment(100);
/* 326 */     text2.setLayoutData(data);
/*     */     
/* 328 */     data = new FormData();
/* 329 */     data.top = new FormAttachment(text2, 10);
/* 330 */     data.left = new FormAttachment(0);
/* 331 */     data.right = new FormAttachment(100);
/* 332 */     this.noSpaceWarning.setLayoutData(data);
/*     */     
/* 334 */     data = new FormData();
/* 335 */     data.top = new FormAttachment(this.noSpaceWarning, 10);
/* 336 */     data.left = new FormAttachment(0);
/* 337 */     link.setLayoutData(data);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void createFonts()
/*     */   {
/* 345 */     FontData[] fDatas = this.shell.getFont().getFontData();
/*     */     
/* 347 */     for (int i = 0; i < fDatas.length; i++) {
/* 348 */       fDatas[i].setStyle(1);
/*     */     }
/* 350 */     this.boldFont = new Font(this.display, fDatas);
/*     */     
/*     */ 
/* 353 */     for (int i = 0; i < fDatas.length; i++) {
/* 354 */       if (Constants.isOSX) {
/* 355 */         fDatas[i].setHeight(12);
/*     */       } else {
/* 357 */         fDatas[i].setHeight(10);
/*     */       }
/*     */     }
/* 360 */     this.subTitleFont = new Font(this.display, fDatas);
/*     */     
/* 362 */     for (int i = 0; i < fDatas.length; i++) {
/* 363 */       if (Constants.isOSX) {
/* 364 */         fDatas[i].setHeight(17);
/*     */       } else {
/* 366 */         fDatas[i].setHeight(14);
/*     */       }
/*     */     }
/* 369 */     this.titleFont = new Font(this.display, fDatas);
/*     */     
/*     */ 
/* 372 */     for (int i = 0; i < fDatas.length; i++) {
/* 373 */       if (Constants.isOSX) {
/* 374 */         fDatas[i].setHeight(14);
/*     */       } else {
/* 376 */         fDatas[i].setHeight(12);
/*     */       }
/* 378 */       fDatas[i].setStyle(0);
/*     */     }
/* 380 */     this.textInputFont = new Font(this.display, fDatas);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private void populateFooter(Composite footer, final boolean no_space_available)
/*     */   {
/* 388 */     final Button dont_ask_again = new Button(footer, 32);
/* 389 */     dont_ask_again.setText(MessageText.getString("general.dont.ask.again"));
/* 390 */     dont_ask_again.setSelection(true);
/*     */     
/* 392 */     Button cancelButton = new Button(footer, 8);
/* 393 */     cancelButton.setText(MessageText.getString("button.nothanks"));
/*     */     
/* 395 */     this.turnOnButton = new Button(footer, 8);
/* 396 */     this.turnOnButton.setText(MessageText.getString("Button.turnon"));
/*     */     
/*     */ 
/* 399 */     FormLayout layout = new FormLayout();
/* 400 */     layout.marginHeight = 5;
/* 401 */     layout.marginWidth = 5;
/* 402 */     layout.spacing = 5;
/*     */     
/* 404 */     footer.setLayout(layout);
/*     */     
/*     */ 
/* 407 */     FormData data = new FormData();
/* 408 */     data.left = new FormAttachment(0, 45);
/* 409 */     data.right = new FormAttachment(this.turnOnButton);
/* 410 */     dont_ask_again.setLayoutData(data);
/*     */     
/* 412 */     data = new FormData();
/* 413 */     data.right = new FormAttachment(100);
/* 414 */     data.width = 100;
/* 415 */     cancelButton.setLayoutData(data);
/*     */     
/* 417 */     data = new FormData();
/* 418 */     data.right = new FormAttachment(cancelButton);
/* 419 */     data.width = 100;
/* 420 */     this.turnOnButton.setLayoutData(data);
/*     */     
/*     */ 
/*     */ 
/* 424 */     this.turnOnButton.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/*     */ 
/*     */ 
/* 433 */         DevicesODFTUX.this.device.setEnabled(true);
/*     */         
/* 435 */         DevicesODFTUX.this.device.setShownFTUX();
/*     */         
/* 437 */         DevicesODFTUX.this.shell.close();
/*     */       }
/*     */       
/* 440 */     });
/* 441 */     this.turnOnButton.setEnabled(!no_space_available);
/*     */     
/* 443 */     new AEThread2("scanner", true)
/*     */     {
/* 445 */       private long last_avail = no_space_available ? 0L : Long.MAX_VALUE;
/*     */       
/*     */ 
/*     */       public void run()
/*     */       {
/* 450 */         while (!DevicesODFTUX.this.shell.isDisposed()) {
/*     */           try
/*     */           {
/* 453 */             Thread.sleep(10000L);
/*     */             
/* 455 */             final long avail = DevicesODFTUX.this.device.getSpaceAvailable(true);
/*     */             
/* 457 */             if (avail != this.last_avail)
/*     */             {
/* 459 */               this.last_avail = avail;
/*     */               
/* 461 */               Utils.execSWTThread(new AERunnable()
/*     */               {
/*     */ 
/*     */                 public void runSupport()
/*     */                 {
/*     */ 
/* 467 */                   if (!DevicesODFTUX.this.turnOnButton.isDisposed())
/*     */                   {
/* 469 */                     DevicesODFTUX.this.turnOnButton.setEnabled(avail > 0L);
/*     */                   }
/*     */                   
/* 472 */                   if (!DevicesODFTUX.this.noSpaceWarning.isDisposed())
/*     */                   {
/* 474 */                     DevicesODFTUX.this.noSpaceWarning.setVisible(avail <= 0L);
/*     */                   }
/*     */                   
/*     */                 }
/*     */               });
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {}
/*     */         }
/*     */       }
/* 484 */     }.start();
/* 485 */     cancelButton.addListener(13, new Listener()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void handleEvent(Event arg0)
/*     */       {
/*     */ 
/*     */ 
/* 493 */         DevicesODFTUX.this.device.setEnabled(false);
/*     */         
/* 495 */         if (dont_ask_again.getSelection())
/*     */         {
/* 497 */           DevicesODFTUX.this.device.setShownFTUX();
/*     */         }
/*     */         
/* 500 */         DevicesODFTUX.this.shell.close();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   protected void close() {
/* 506 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 508 */         if ((DevicesODFTUX.this.shell != null) && (!DevicesODFTUX.this.shell.isDisposed())) {
/* 509 */           DevicesODFTUX.this.shell.dispose();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/DevicesODFTUX.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */