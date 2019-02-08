/*     */ package com.aelitis.azureus.ui.swt.devices;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.Device;
/*     */ import com.aelitis.azureus.core.devices.DeviceManager;
/*     */ import com.aelitis.azureus.core.devices.DeviceTemplate;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
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
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DevicesWizard
/*     */ {
/*     */   private DeviceManagerUI device_manager_ui;
/*     */   private Display display;
/*     */   private Shell shell;
/*     */   private Label title;
/*     */   private Font boldFont;
/*     */   private Font titleFont;
/*     */   private Font subTitleFont;
/*     */   private Font textInputFont;
/*     */   private Composite main;
/*     */   private ImageLoader imageLoader;
/*     */   
/*     */   public DevicesWizard(DeviceManagerUI dm_ui)
/*     */   {
/*  67 */     this.device_manager_ui = dm_ui;
/*     */     
/*  69 */     this.imageLoader = ImageLoader.getInstance();
/*     */     
/*  71 */     this.shell = ShellFactory.createMainShell(113);
/*     */     
/*     */ 
/*  74 */     this.shell.setSize(650, 400);
/*     */     
/*  76 */     Utils.centreWindow(this.shell);
/*     */     
/*  78 */     this.shell.setMinimumSize(550, 400);
/*     */     
/*  80 */     this.display = this.shell.getDisplay();
/*     */     
/*  82 */     Utils.setShellIcon(this.shell);
/*     */     
/*     */ 
/*  85 */     createFonts();
/*     */     
/*  87 */     this.shell.setText(MessageText.getString("wizard.device.title"));
/*     */     
/*  89 */     this.shell.addListener(12, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/*  92 */         DevicesWizard.this.imageLoader.releaseImage("wizard_header_bg");
/*     */         
/*  94 */         if ((DevicesWizard.this.titleFont != null) && (!DevicesWizard.this.titleFont.isDisposed())) {
/*  95 */           DevicesWizard.this.titleFont.dispose();
/*     */         }
/*     */         
/*  98 */         if ((DevicesWizard.this.textInputFont != null) && (!DevicesWizard.this.textInputFont.isDisposed())) {
/*  99 */           DevicesWizard.this.textInputFont.dispose();
/*     */         }
/*     */         
/* 102 */         if ((DevicesWizard.this.boldFont != null) && (!DevicesWizard.this.boldFont.isDisposed())) {
/* 103 */           DevicesWizard.this.boldFont.dispose();
/*     */         }
/*     */         
/* 106 */         if ((DevicesWizard.this.subTitleFont != null) && (!DevicesWizard.this.subTitleFont.isDisposed())) {
/* 107 */           DevicesWizard.this.subTitleFont.dispose();
/*     */         }
/*     */         
/*     */       }
/* 111 */     });
/* 112 */     Composite header = new Composite(this.shell, 0);
/* 113 */     header.setBackgroundMode(1);
/* 114 */     header.setBackgroundImage(this.imageLoader.getImage("wizard_header_bg"));
/* 115 */     Label topSeparator = new Label(this.shell, 258);
/* 116 */     this.main = new Composite(this.shell, 0);
/* 117 */     Label bottomSeparator = new Label(this.shell, 258);
/* 118 */     Composite footer = new Composite(this.shell, 0);
/*     */     
/* 120 */     FormLayout layout = new FormLayout();
/* 121 */     this.shell.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 125 */     FormData data = new FormData();
/* 126 */     data.top = new FormAttachment(0, 0);
/* 127 */     data.left = new FormAttachment(0, 0);
/* 128 */     data.right = new FormAttachment(100, 0);
/*     */     
/* 130 */     header.setLayoutData(data);
/*     */     
/* 132 */     data = new FormData();
/* 133 */     data.top = new FormAttachment(header, 0);
/* 134 */     data.left = new FormAttachment(0, 0);
/* 135 */     data.right = new FormAttachment(100, 0);
/* 136 */     topSeparator.setLayoutData(data);
/*     */     
/* 138 */     data = new FormData();
/* 139 */     data.top = new FormAttachment(topSeparator, 0);
/* 140 */     data.left = new FormAttachment(0, 0);
/* 141 */     data.right = new FormAttachment(100, 0);
/* 142 */     data.bottom = new FormAttachment(bottomSeparator, 0);
/* 143 */     this.main.setLayoutData(data);
/*     */     
/* 145 */     data = new FormData();
/* 146 */     data.left = new FormAttachment(0, 0);
/* 147 */     data.right = new FormAttachment(100, 0);
/* 148 */     data.bottom = new FormAttachment(footer, 0);
/* 149 */     bottomSeparator.setLayoutData(data);
/*     */     
/* 151 */     data = new FormData();
/* 152 */     data.bottom = new FormAttachment(100, 0);
/* 153 */     data.left = new FormAttachment(0, 0);
/* 154 */     data.right = new FormAttachment(100, 0);
/* 155 */     footer.setLayoutData(data);
/*     */     
/* 157 */     populateHeader(header);
/* 158 */     populateFooter(footer);
/*     */     
/* 160 */     this.shell.layout();
/* 161 */     this.shell.open();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private void populateHeader(Composite header)
/*     */   {
/* 168 */     header.setBackground(this.display.getSystemColor(1));
/*     */     
/* 170 */     this.title = new Label(header, 64);
/*     */     
/* 172 */     this.title.setFont(this.titleFont);
/*     */     
/* 174 */     this.title.setText(MessageText.getString("device.wizard.header"));
/*     */     
/* 176 */     FillLayout layout = new FillLayout();
/*     */     
/* 178 */     layout.marginHeight = 10;
/*     */     
/* 180 */     layout.marginWidth = 10;
/*     */     
/* 182 */     header.setLayout(layout);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void createFonts()
/*     */   {
/* 192 */     FontData[] fDatas = this.shell.getFont().getFontData();
/*     */     
/* 194 */     for (int i = 0; i < fDatas.length; i++) {
/* 195 */       fDatas[i].setStyle(1);
/*     */     }
/* 197 */     this.boldFont = new Font(this.display, fDatas);
/*     */     
/*     */ 
/* 200 */     for (int i = 0; i < fDatas.length; i++) {
/* 201 */       if (Constants.isOSX) {
/* 202 */         fDatas[i].setHeight(12);
/*     */       } else {
/* 204 */         fDatas[i].setHeight(10);
/*     */       }
/*     */     }
/* 207 */     this.subTitleFont = new Font(this.display, fDatas);
/*     */     
/* 209 */     for (int i = 0; i < fDatas.length; i++) {
/* 210 */       if (Constants.isOSX) {
/* 211 */         fDatas[i].setHeight(17);
/*     */       } else {
/* 213 */         fDatas[i].setHeight(14);
/*     */       }
/*     */     }
/* 216 */     this.titleFont = new Font(this.display, fDatas);
/*     */     
/*     */ 
/* 219 */     for (int i = 0; i < fDatas.length; i++) {
/* 220 */       if (Constants.isOSX) {
/* 221 */         fDatas[i].setHeight(14);
/*     */       } else {
/* 223 */         fDatas[i].setHeight(12);
/*     */       }
/* 225 */       fDatas[i].setStyle(0);
/*     */     }
/* 227 */     this.textInputFont = new Font(this.display, fDatas);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void populateFooter(Composite footer)
/*     */   {
/* 237 */     Button cancelButton = new Button(footer, 8);
/* 238 */     cancelButton.setText(MessageText.getString("Button.cancel"));
/*     */     
/* 240 */     Button createButton = new Button(footer, 8);
/* 241 */     createButton.setText(MessageText.getString("device.wizard.create"));
/*     */     
/*     */ 
/* 244 */     FormLayout layout = new FormLayout();
/* 245 */     layout.marginHeight = 5;
/* 246 */     layout.marginWidth = 5;
/* 247 */     layout.spacing = 5;
/*     */     
/* 249 */     footer.setLayout(layout);
/*     */     
/*     */ 
/* 252 */     FormData data = new FormData();
/* 253 */     data.right = new FormAttachment(100);
/* 254 */     data.width = 100;
/* 255 */     cancelButton.setLayoutData(data);
/*     */     
/* 257 */     data = new FormData();
/* 258 */     data.left = new FormAttachment(0);
/* 259 */     data.width = 175;
/* 260 */     createButton.setLayoutData(data);
/*     */     
/*     */ 
/*     */ 
/* 264 */     createButton.addListener(13, new Listener()
/*     */     {
/*     */       public void handleEvent(Event arg0) {
/*     */         try {
/* 268 */           DeviceTemplate[] templates = DevicesWizard.this.device_manager_ui.getDeviceManager().getDeviceTemplates(3);
/*     */           
/* 270 */           for (DeviceTemplate template : templates)
/*     */           {
/* 272 */             if (!template.isAuto())
/*     */             {
/* 274 */               Device device = template.createInstance(template.getName() + " test!");
/*     */               
/* 276 */               device.requestAttention();
/*     */               
/* 278 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 284 */           Debug.printStackTrace(e);
/*     */         }
/*     */         
/*     */       }
/* 288 */     });
/* 289 */     cancelButton.addListener(13, new Listener() {
/*     */       public void handleEvent(Event arg0) {
/* 291 */         DevicesWizard.this.shell.close();
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 302 */     DevicesWizard sw = new DevicesWizard(null);
/*     */     
/* 304 */     while (!sw.shell.isDisposed()) {
/* 305 */       if (!sw.display.readAndDispatch()) {
/* 306 */         sw.display.sleep();
/*     */       }
/*     */     }
/*     */     
/* 310 */     sw.display.dispose();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/DevicesWizard.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */