/*     */ package com.aelitis.azureus.ui.swt.devices;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.Device;
/*     */ import com.aelitis.azureus.core.devices.DeviceManager;
/*     */ import com.aelitis.azureus.core.devices.DeviceManagerFactory;
/*     */ import com.aelitis.azureus.core.devices.DeviceMediaRenderer;
/*     */ import com.aelitis.azureus.core.devices.DeviceTemplate;
/*     */ import com.aelitis.azureus.core.devices.TranscodeException;
/*     */ import com.aelitis.azureus.core.devices.TranscodeProfile;
/*     */ import com.aelitis.azureus.core.devices.TranscodeTarget;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader.ImageDownloaderListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinButtonUtility.ButtonListenerAdapter;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinFactory;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectButton;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectListener;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.events.TraverseEvent;
/*     */ import org.eclipse.swt.events.TraverseListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Pattern;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.layout.RowLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Combo;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Widget;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.components.shell.ShellFactory;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ 
/*     */ 
/*     */ public abstract class TranscodeChooser
/*     */ {
/*     */   private static final String skinFile = "skin3_transcodechooser";
/*     */   private static final String shellSkinObjectID = "shell";
/*     */   private Shell shell;
/*     */   private SWTSkin skin;
/*     */   private Font fontDevice;
/*     */   protected TranscodeTarget selectedTranscodeTarget;
/*     */   protected TranscodeProfile selectedProfile;
/*     */   protected DeviceTemplate selectedDeviceTemplate;
/*     */   private SWTSkinObjectContainer soList;
/*     */   private Shell mainShell;
/*     */   private SWTSkinObjectContainer soBottomContainer;
/*     */   private Button btnNoPrompt;
/*     */   private int transcodeRequirement;
/*  85 */   private List<String> listImageIDsToRelease = new ArrayList();
/*     */   
/*     */   private SWTSkinObjectText soInfoTitle;
/*     */   
/*     */   private SWTSkinObjectText soInfoText;
/*     */   
/*     */   private Font fontDeviceDesc;
/*     */   private TranscodeProfile[] transcodeProfiles;
/*     */   
/*     */   public TranscodeChooser()
/*     */   {
/*  96 */     this((TranscodeTarget)null);
/*     */   }
/*     */   
/*     */   public TranscodeChooser(TranscodeTarget tt) {
/* 100 */     this.selectedTranscodeTarget = tt;
/*     */   }
/*     */   
/*     */   public TranscodeChooser(TranscodeProfile[] transcodeProfiles) {
/* 104 */     this.transcodeProfiles = transcodeProfiles;
/*     */   }
/*     */   
/*     */   public void show(Runnable fire_on_install)
/*     */   {
/* 109 */     if (!DevicesFTUX.ensureInstalled(fire_on_install)) {
/* 110 */       return;
/*     */     }
/*     */     
/* 113 */     this.mainShell = UIFunctionsManagerSWT.getUIFunctionsSWT().getMainShell();
/* 114 */     this.shell = ShellFactory.createShell(this.mainShell, 2160);
/*     */     
/* 116 */     Utils.setShellIcon(this.shell);
/*     */     
/* 118 */     this.skin = SWTSkinFactory.getNonPersistentInstance(SkinnedDialog.class.getClassLoader(), "com/aelitis/azureus/ui/skin/", "skin3_transcodechooser.properties");
/*     */     
/*     */ 
/*     */ 
/* 122 */     this.skin.initialize(this.shell, "shell");
/*     */     
/* 124 */     this.shell.addTraverseListener(new TraverseListener() {
/*     */       public void keyTraversed(TraverseEvent e) {
/* 126 */         if (e.detail == 2) {
/* 127 */           TranscodeChooser.this.shell.close();
/*     */         }
/*     */         
/*     */       }
/* 131 */     });
/* 132 */     this.shell.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 134 */         TranscodeChooser.this.closed();
/*     */       }
/*     */       
/* 137 */     });
/* 138 */     this.skin.layout();
/*     */     
/* 140 */     SWTSkinObject soBottom = this.skin.getSkinObject("bottom");
/* 141 */     if ((soBottom instanceof SWTSkinObjectContainer)) {
/* 142 */       this.soBottomContainer = ((SWTSkinObjectContainer)soBottom);
/*     */       
/* 144 */       this.soBottomContainer.addListener(new SWTSkinObjectListener()
/*     */       {
/*     */         public Object eventOccured(SWTSkinObject skinObject, int eventType, Object params)
/*     */         {
/* 148 */           if (eventType == 0) {
/* 149 */             skinObject.removeListener(this);
/* 150 */             TranscodeChooser.this.initBottom();
/*     */           }
/* 152 */           return null;
/*     */         }
/* 154 */       });
/* 155 */       this.soBottomContainer.setVisible(this.selectedTranscodeTarget != null);
/*     */     }
/*     */     
/* 158 */     this.soList = ((SWTSkinObjectContainer)this.skin.getSkinObject("list"));
/* 159 */     if (this.soList != null) {
/* 160 */       if (this.transcodeProfiles != null) {
/* 161 */         createProfileList(this.soList);
/* 162 */       } else if (this.selectedTranscodeTarget == null) {
/* 163 */         createDeviceList(this.soList);
/*     */       } else {
/* 165 */         this.transcodeProfiles = this.selectedTranscodeTarget.getTranscodeProfiles();
/* 166 */         createProfileList(this.soList);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 172 */     if (this.shell.isDisposed()) {
/* 173 */       return;
/*     */     }
/*     */     
/* 176 */     this.shell.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/* 178 */         Utils.disposeSWTObjects(new Object[] { TranscodeChooser.this.fontDevice, TranscodeChooser.this.fontDeviceDesc });
/*     */         
/*     */ 
/*     */ 
/* 182 */         for (String id : TranscodeChooser.this.listImageIDsToRelease) {
/* 183 */           ImageLoader.getInstance().releaseImage(id);
/*     */         }
/*     */       }
/* 186 */     });
/* 187 */     Utils.verifyShellRect(this.shell, true);
/* 188 */     this.shell.open();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void initBottom()
/*     */   {
/* 197 */     Composite composite = this.soBottomContainer.getComposite();
/* 198 */     this.btnNoPrompt = new Button(composite, 32);
/* 199 */     Messages.setLanguageText(this.btnNoPrompt, "option.rememberthis");
/*     */     
/* 201 */     Label lblXCode = new Label(composite, 0);
/* 202 */     lblXCode.setText(MessageText.getString("device.xcode"));
/*     */     
/* 204 */     final Combo cmbXCode = new Combo(composite, 12);
/*     */     
/* 206 */     cmbXCode.add(MessageText.getString("device.xcode.whenreq"));
/* 207 */     cmbXCode.add(MessageText.getString("device.xcode.always"));
/* 208 */     cmbXCode.add(MessageText.getString("device.xcode.never"));
/* 209 */     this.transcodeRequirement = this.selectedTranscodeTarget.getTranscodeRequirement();
/* 210 */     switch (this.transcodeRequirement) {
/*     */     case 3: 
/* 212 */       cmbXCode.select(1);
/* 213 */       break;
/*     */     
/*     */     case 1: 
/* 216 */       cmbXCode.select(2);
/* 217 */       break;
/*     */     
/*     */     case 2: 
/*     */     default: 
/* 221 */       cmbXCode.select(0);
/*     */     }
/*     */     
/*     */     
/* 225 */     cmbXCode.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 227 */         int i = cmbXCode.getSelectionIndex();
/* 228 */         switch (i) {
/*     */         case 0: 
/* 230 */           TranscodeChooser.this.transcodeRequirement = 2;
/* 231 */           break;
/*     */         
/*     */         case 1: 
/* 234 */           TranscodeChooser.this.transcodeRequirement = 3;
/* 235 */           break;
/*     */         
/*     */         case 2: 
/* 238 */           TranscodeChooser.this.transcodeRequirement = 1;
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/* 248 */     });
/* 249 */     FormData fd = new FormData();
/* 250 */     fd.left = new FormAttachment(0, 10);
/* 251 */     fd.top = new FormAttachment(cmbXCode, 0, 16777216);
/* 252 */     Utils.setLayoutData(this.btnNoPrompt, fd);
/*     */     
/* 254 */     fd = new FormData();
/* 255 */     fd.right = new FormAttachment(100, -10);
/* 256 */     fd.top = new FormAttachment(0, 5);
/* 257 */     fd.bottom = new FormAttachment(100, -5);
/* 258 */     Utils.setLayoutData(cmbXCode, fd);
/*     */     
/* 260 */     fd = new FormData();
/* 261 */     fd.right = new FormAttachment(cmbXCode, -5);
/* 262 */     fd.top = new FormAttachment(cmbXCode, 0, 16777216);
/* 263 */     Utils.setLayoutData(lblXCode, fd);
/*     */     
/* 265 */     int userMode = COConfigurationManager.getIntParameter("User Mode");
/* 266 */     if (userMode == 0) {
/* 267 */       lblXCode.setVisible(false);
/* 268 */       cmbXCode.setVisible(false);
/*     */     }
/*     */     
/* 271 */     Point computeSize = this.shell.computeSize(this.shell.getClientArea().width, -1, true);
/*     */     
/* 273 */     this.shell.setSize(computeSize);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void createProfileList(SWTSkinObjectContainer soList)
/*     */   {
/* 282 */     if ((this.selectedTranscodeTarget == null) && (this.selectedDeviceTemplate == null)) {
/* 283 */       new MessageBoxShell(32, "No Device", "No Device Selected!?").open(null);
/* 284 */       this.shell.dispose();
/* 285 */       return;
/*     */     }
/*     */     
/* 288 */     if (this.selectedTranscodeTarget != null) {
/*     */       try {
/* 290 */         TranscodeProfile defaultProfile = this.selectedTranscodeTarget.getDefaultTranscodeProfile();
/* 291 */         if (defaultProfile != null)
/*     */         {
/*     */ 
/* 294 */           if (this.selectedTranscodeTarget.getTranscodeRequirement() == 1)
/*     */           {
/* 296 */             this.selectedProfile = this.selectedTranscodeTarget.getBlankProfile();
/*     */           } else {
/* 298 */             this.selectedProfile = defaultProfile;
/*     */           }
/* 300 */           this.shell.dispose();
/* 301 */           return;
/*     */         }
/*     */       }
/*     */       catch (TranscodeException e) {}
/*     */     }
/*     */     
/* 307 */     if ((this.transcodeProfiles.length == 0) || ((this.selectedTranscodeTarget != null) && (this.selectedTranscodeTarget.getTranscodeRequirement() == 1))) {
/* 308 */       if (this.selectedTranscodeTarget != null) {
/* 309 */         this.selectedProfile = this.selectedTranscodeTarget.getBlankProfile();
/* 310 */         this.shell.dispose();
/* 311 */         return;
/*     */       }
/* 313 */       new MessageBoxShell(32, "No Profiles", "No Profiles for device!").open(null);
/* 314 */       this.shell.dispose();
/* 315 */       return;
/*     */     }
/*     */     
/* 318 */     if (this.transcodeProfiles.length == 1) {
/* 319 */       this.selectedProfile = this.transcodeProfiles[0];
/*     */       
/* 321 */       this.shell.dispose();
/* 322 */       return;
/*     */     }
/*     */     
/* 325 */     Arrays.sort(this.transcodeProfiles, new Comparator() {
/*     */       public int compare(TranscodeProfile o1, TranscodeProfile o2) {
/* 327 */         int i1 = o1.getIconIndex();
/* 328 */         int i2 = o2.getIconIndex();
/*     */         
/* 330 */         if (i1 == i2)
/*     */         {
/* 332 */           return o1.getName().compareToIgnoreCase(o2.getName());
/*     */         }
/*     */         
/* 335 */         return i1 - i2;
/*     */       }
/*     */       
/*     */ 
/* 339 */     });
/* 340 */     Composite parent = soList.getComposite();
/* 341 */     if (parent.getChildren().length > 0) {
/* 342 */       Utils.disposeComposite(parent, false);
/*     */     }
/*     */     
/* 345 */     this.soInfoTitle = ((SWTSkinObjectText)this.skin.getSkinObject("info-title"));
/* 346 */     this.soInfoText = ((SWTSkinObjectText)this.skin.getSkinObject("info-text"));
/* 347 */     resetProfileInfoBox(false);
/*     */     
/* 349 */     RowLayout layout = new RowLayout(256);
/* 350 */     layout.spacing = 0;
/* 351 */     layout.marginLeft = (layout.marginRight = 0);
/* 352 */     layout.wrap = true;
/* 353 */     layout.justify = true;
/* 354 */     layout.fill = true;
/* 355 */     parent.setLayout(layout);
/*     */     
/* 357 */     Listener listenerMouseInout = new Listener() {
/*     */       public void handleEvent(Event event) {
/* 359 */         Widget widget = (event.widget instanceof Canvas) ? ((Canvas)event.widget).getParent() : event.widget;
/*     */         
/*     */ 
/* 362 */         Composite c = TranscodeChooser.this.soList.getComposite();
/* 363 */         Rectangle bounds = c.getClientArea();
/* 364 */         c.redraw(bounds.x, bounds.y, bounds.width, bounds.height, true);
/*     */         
/* 366 */         TranscodeProfile profile = (TranscodeProfile)widget.getData("obj");
/* 367 */         if (profile == null) {
/* 368 */           return;
/*     */         }
/* 370 */         if (event.type == 6) {
/* 371 */           String description = profile.getDescription();
/* 372 */           if ((TranscodeChooser.this.selectedTranscodeTarget != null) && 
/* 373 */             (profile == TranscodeChooser.this.selectedTranscodeTarget.getBlankProfile())) {
/* 374 */             description = null;
/*     */           }
/*     */           
/*     */ 
/* 378 */           if ((description == null) || (description.length() == 0)) {
/* 379 */             TranscodeChooser.this.resetProfileInfoBox(true);
/*     */           } else {
/* 381 */             if (TranscodeChooser.this.soInfoTitle != null) {
/* 382 */               TranscodeChooser.this.soInfoTitle.setTextID("devices.choose.profile.info.title.selected", new String[] { profile.getName() });
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 387 */             if (TranscodeChooser.this.soInfoText != null) {
/* 388 */               TranscodeChooser.this.soInfoText.setText(description);
/* 389 */               Point computeSize = TranscodeChooser.this.shell.computeSize(TranscodeChooser.this.shell.getClientArea().width, -1, true);
/*     */               
/* 391 */               if (computeSize.y > TranscodeChooser.this.shell.getSize().y) {
/* 392 */                 TranscodeChooser.this.shell.setSize(computeSize);
/*     */               }
/*     */               
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/* 399 */     };
/* 400 */     parent.addListener(6, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 402 */         TranscodeChooser.this.resetProfileInfoBox(true);
/*     */       }
/*     */       
/* 405 */     });
/* 406 */     Listener clickListener = new Listener() {
/* 407 */       boolean down = false;
/*     */       
/*     */       public void handleEvent(Event event) {
/* 410 */         if (event.type == 3) {
/* 411 */           this.down = true;
/* 412 */         } else if ((event.type == 4) && (this.down)) {
/* 413 */           Widget widget = (event.widget instanceof Label) ? ((Label)event.widget).getParent() : event.widget;
/*     */           
/* 415 */           TranscodeChooser.this.selectedProfile = ((TranscodeProfile)widget.getData("obj"));
/* 416 */           if ((TranscodeChooser.this.selectedTranscodeTarget != null) && (TranscodeChooser.this.selectedProfile == TranscodeChooser.this.selectedTranscodeTarget.getBlankProfile())) {
/* 417 */             TranscodeChooser.this.transcodeRequirement = 1;
/*     */           }
/* 419 */           if (TranscodeChooser.this.selectedProfile == null) {
/* 420 */             Debug.out("profile is null!");
/*     */           }
/* 422 */           else if ((TranscodeChooser.this.btnNoPrompt != null) && 
/* 423 */             (TranscodeChooser.this.btnNoPrompt.getSelection())) {
/* 424 */             if (TranscodeChooser.this.transcodeRequirement == 1) {
/* 425 */               TranscodeChooser.this.selectedTranscodeTarget.setTranscodeRequirement(1);
/*     */             } else {
/* 427 */               TranscodeChooser.this.selectedTranscodeTarget.setDefaultTranscodeProfile(TranscodeChooser.this.selectedProfile);
/*     */             }
/*     */           }
/*     */           
/*     */ 
/* 432 */           TranscodeChooser.this.shell.dispose();
/* 433 */           this.down = false;
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 439 */     };
/* 440 */     int total_images = 0;
/*     */     
/* 442 */     for (TranscodeProfile profile : this.transcodeProfiles) {
/* 443 */       addImageBox(parent, clickListener, listenerMouseInout, profile, profile.getIconURL(), profile.getName());
/*     */       
/*     */ 
/* 446 */       total_images++;
/*     */     }
/* 448 */     if (this.selectedTranscodeTarget != null) {
/* 449 */       addImageBox(parent, clickListener, listenerMouseInout, this.selectedTranscodeTarget.getBlankProfile(), "", "Do not transcode");
/*     */       
/*     */ 
/* 452 */       total_images++;
/*     */     }
/* 454 */     SWTSkinObjectText soTitle = (SWTSkinObjectText)this.skin.getSkinObject("title");
/* 455 */     if (soTitle != null) {
/* 456 */       soTitle.setTextID("devices.choose.profile.title");
/*     */     }
/*     */     
/* 459 */     SWTSkinObjectText soSubTitle = (SWTSkinObjectText)this.skin.getSkinObject("subtitle");
/* 460 */     if (soSubTitle != null) {
/* 461 */       soSubTitle.setTextID("label.clickone");
/*     */     }
/*     */     
/* 464 */     if (this.soBottomContainer != null) {
/* 465 */       this.soBottomContainer.setVisible(true);
/*     */     }
/*     */     
/* 468 */     SWTSkinObjectContainer soButtonBottomArea = (SWTSkinObjectContainer)this.skin.getSkinObject("button-bottom");
/* 469 */     if (soButtonBottomArea != null) {
/* 470 */       soButtonBottomArea.setVisible(false);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 476 */     Point computeSize = this.shell.computeSize(total_images > 12 ? 800 : 600, -1, true);
/* 477 */     this.shell.setSize(computeSize);
/* 478 */     Utils.centerWindowRelativeTo(this.shell, this.mainShell);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void addImageBox(Composite parent, Listener clickListener, Listener listenerMouseInout, Object obj, String iconURL, String name)
/*     */   {
/* 489 */     final Shell shell = parent.getShell();
/* 490 */     final Composite c = new Composite(parent, 0);
/* 491 */     GridLayout clayout = new GridLayout();
/* 492 */     clayout.marginWidth = (clayout.horizontalSpacing = 0);
/* 493 */     c.setLayout(clayout);
/* 494 */     c.setCursor(c.getDisplay().getSystemCursor(21));
/* 495 */     c.addListener(4, clickListener);
/* 496 */     c.addListener(3, clickListener);
/* 497 */     c.setData("obj", obj);
/*     */     
/* 499 */     if (listenerMouseInout != null) {
/* 500 */       c.addListener(6, listenerMouseInout);
/* 501 */       c.addListener(7, listenerMouseInout);
/*     */     }
/*     */     
/* 504 */     Canvas lblImage = new Canvas(c, 536870912);
/* 505 */     if (listenerMouseInout != null) {
/* 506 */       lblImage.addListener(6, listenerMouseInout);
/* 507 */       lblImage.addListener(7, listenerMouseInout);
/*     */     }
/* 509 */     lblImage.addListener(4, clickListener);
/* 510 */     lblImage.addListener(3, clickListener);
/* 511 */     lblImage.setData("obj", obj);
/* 512 */     lblImage.addListener(9, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 514 */         Rectangle area = this.val$lblImage.getBounds();
/* 515 */         Rectangle carea = c.getBounds();
/*     */         
/* 517 */         Point ptInDisplay = c.toDisplay(0, 0);
/*     */         
/* 519 */         event.gc.setAdvanced(true);
/* 520 */         event.gc.setAntialias(1);
/* 521 */         event.gc.setLineWidth(2);
/*     */         
/* 523 */         if (new Rectangle(ptInDisplay.x, ptInDisplay.y, carea.width, carea.height).contains(event.display.getCursorLocation()))
/*     */         {
/*     */ 
/*     */ 
/* 527 */           Color color1 = ColorCache.getColor(event.gc.getDevice(), 252, 253, 255);
/*     */           
/* 529 */           Color color2 = ColorCache.getColor(event.gc.getDevice(), 169, 195, 252);
/*     */           
/* 531 */           Pattern pattern = new Pattern(event.gc.getDevice(), 0.0F, 0.0F, 0.0F, area.height, color1, 0, color2, 200);
/*     */           
/* 533 */           event.gc.setBackgroundPattern(pattern);
/*     */           
/* 535 */           event.gc.fillRoundRectangle(0, 0, area.width - 1, area.height - 1, 20, 20);
/*     */           
/*     */ 
/* 538 */           event.gc.setBackgroundPattern(null);
/* 539 */           pattern.dispose();
/*     */           
/* 541 */           pattern = new Pattern(event.gc.getDevice(), 0.0F, 0.0F, 0.0F, area.height, color2, 50, color2, 255);
/*     */           
/* 543 */           event.gc.setForegroundPattern(pattern);
/*     */           
/* 545 */           event.gc.drawRoundRectangle(0, 0, area.width - 1, area.height - 1, 20, 20);
/*     */           
/*     */ 
/* 548 */           event.gc.setForegroundPattern(null);
/* 549 */           pattern.dispose();
/*     */         }
/*     */         
/* 552 */         Image image = (Image)this.val$lblImage.getData("Image");
/* 553 */         if (image != null) {
/* 554 */           Rectangle bounds = image.getBounds();
/* 555 */           event.gc.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, 8, 5, bounds.width, bounds.height);
/*     */         }
/*     */         else {
/* 558 */           Rectangle ca = this.val$lblImage.getClientArea();
/* 559 */           event.gc.setAdvanced(true);
/* 560 */           event.gc.setAntialias(1);
/* 561 */           event.gc.setAlpha(50);
/* 562 */           event.gc.setBackground(event.gc.getForeground());
/* 563 */           event.gc.fillRoundRectangle(ca.x + 10, ca.y + 5, ca.width - 21, ca.height - 11, 20, 20);
/*     */         }
/*     */         
/*     */       }
/* 567 */     });
/* 568 */     GridData gridData = new GridData(1040);
/* 569 */     gridData.heightHint = 50;
/* 570 */     gridData.widthHint = 100;
/* 571 */     if ((iconURL != null) && (iconURL.length() > 0)) {
/* 572 */       ImageLoader imageLoader = ImageLoader.getInstance();
/* 573 */       Image image = imageLoader.getUrlImage(iconURL, new ImageLoader.ImageDownloaderListener()
/*     */       {
/*     */         public void imageDownloaded(Image image, boolean returnedImmediately) {
/* 576 */           if (!returnedImmediately) {
/* 577 */             if (this.val$lblImage.isDisposed()) {
/* 578 */               return;
/*     */             }
/* 580 */             this.val$lblImage.setData("Image", image);
/* 581 */             Rectangle bounds = image.getBounds();
/* 582 */             GridData gridData = (GridData)this.val$lblImage.getLayoutData();
/* 583 */             gridData.heightHint = (bounds.height + 10);
/* 584 */             gridData.widthHint = (bounds.width + 16);
/* 585 */             Utils.setLayoutData(this.val$lblImage, gridData);
/* 586 */             this.val$lblImage.getShell().layout(new Control[] { this.val$lblImage });
/*     */             
/*     */ 
/* 589 */             Point computeSize = shell.computeSize(600, -1, true);
/* 590 */             shell.setSize(computeSize);
/*     */           }
/*     */         }
/*     */       });
/* 594 */       if (image != null) {
/* 595 */         lblImage.setData("Image", image);
/* 596 */         Rectangle bounds = image.getBounds();
/* 597 */         gridData.heightHint = (bounds.height + 10);
/* 598 */         gridData.widthHint = (bounds.width + 16);
/*     */       }
/*     */     }
/* 601 */     Utils.setLayoutData(lblImage, gridData);
/*     */     
/* 603 */     Label label = new Label(c, 16777280);
/* 604 */     if (listenerMouseInout != null) {
/* 605 */       label.addListener(6, listenerMouseInout);
/*     */     }
/* 607 */     label.addListener(4, clickListener);
/* 608 */     label.addListener(3, clickListener);
/* 609 */     gridData = new GridData(768);
/* 610 */     Utils.setLayoutData(label, gridData);
/* 611 */     String s = name;
/*     */     
/* 613 */     label.setText(s);
/* 614 */     label.setCursor(c.getDisplay().getSystemCursor(21));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void resetProfileInfoBox(boolean layout)
/*     */   {
/* 624 */     if (this.soInfoTitle != null) {
/* 625 */       this.soInfoTitle.setTextID("devices.choose.profile.info.title");
/*     */     }
/* 627 */     if (this.soInfoText != null) {
/* 628 */       this.soInfoText.setTextID("devices.choose.profile.info.text");
/* 629 */       if (layout) {
/* 630 */         Point computeSize = this.shell.computeSize(this.shell.getClientArea().width, -1, true);
/*     */         
/* 632 */         this.shell.setSize(computeSize);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void createDeviceList(SWTSkinObjectContainer soDeviceList) {
/* 638 */     Composite parent = soDeviceList.getComposite();
/* 639 */     parent.setBackgroundMode(2);
/* 640 */     FormLayout layout = new FormLayout();
/* 641 */     layout.marginLeft = 10;
/* 642 */     layout.marginHeight = 15;
/* 643 */     parent.setLayout(layout);
/*     */     
/* 645 */     DeviceManager device_manager = DeviceManagerFactory.getSingleton();
/* 646 */     Device[] devices = device_manager.getDevices();
/*     */     
/* 648 */     if (devices.length == 0) {
/* 649 */       noDevices();
/* 650 */       return;
/*     */     }
/*     */     
/* 653 */     Arrays.sort(devices, new Comparator() {
/*     */       public int compare(Device o1, Device o2) {
/* 655 */         return o1.getName().compareToIgnoreCase(o2.getName());
/*     */       }
/*     */       
/* 658 */     });
/* 659 */     this.fontDevice = FontUtils.getFontWithHeight(parent.getFont(), null, 16, 1);
/* 660 */     this.fontDeviceDesc = FontUtils.getFontWithHeight(parent.getFont(), null, 16, 0);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 676 */     boolean hide_generic = COConfigurationManager.getBooleanParameter("device.sidebar.ui.rend.hidegeneric", true);
/*     */     
/*     */ 
/* 679 */     boolean show_only_tagged = COConfigurationManager.getBooleanParameter("device.sidebar.ui.rend.showonlytagged", false);
/*     */     
/*     */ 
/* 682 */     int numDevices = 0;
/* 683 */     Button lastButton = null;
/* 684 */     for (Device device : devices)
/* 685 */       if ((device.getType() == 3) && (!device.isHidden()) && ((device instanceof DeviceMediaRenderer)))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 690 */         DeviceMediaRenderer renderer = (DeviceMediaRenderer)device;
/*     */         
/* 692 */         if ((!hide_generic) || (!renderer.isNonSimple()))
/*     */         {
/*     */ 
/*     */ 
/* 696 */           if ((!show_only_tagged) || (renderer.isTagged()))
/*     */           {
/*     */ 
/*     */ 
/* 700 */             TranscodeTarget transcodeTarget = (TranscodeTarget)device;
/*     */             
/* 702 */             if ((transcodeTarget.getTranscodeProfiles().length != 0) || 
/*     */             
/* 704 */               (transcodeTarget.getTranscodeRequirement() == 1))
/*     */             {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 710 */               String imageID = "image.sidebar.device." + DeviceManagerUI.getDeviceImageID(device) + ".big";
/*     */               
/* 712 */               lastButton = createDeviceButton(parent, device, device.getName(), device.getShortDescription(), imageID, lastButton);
/*     */               
/* 714 */               numDevices++;
/*     */             }
/*     */           } } }
/* 717 */     if (numDevices == 0) {
/* 718 */       noDevices();
/* 719 */       return;
/*     */     }
/*     */     
/* 722 */     SWTSkinObjectText soTitle = (SWTSkinObjectText)this.skin.getSkinObject("title");
/* 723 */     if (soTitle != null) {
/* 724 */       soTitle.setTextID("devices.choose.device.title");
/*     */     }
/*     */     
/* 727 */     SWTSkinObjectText soSubTitle = (SWTSkinObjectText)this.skin.getSkinObject("subtitle");
/* 728 */     if (soSubTitle != null) {
/* 729 */       soSubTitle.setText("");
/*     */     }
/*     */     
/* 732 */     SWTSkinObjectContainer soButtonBottomArea = (SWTSkinObjectContainer)this.skin.getSkinObject("button-bottom");
/* 733 */     if (soButtonBottomArea != null) {
/* 734 */       soButtonBottomArea.setVisible(true);
/*     */       
/* 736 */       SWTSkinObjectButton soOk = (SWTSkinObjectButton)this.skin.getSkinObject("ok");
/* 737 */       if (soOk != null) {
/* 738 */         this.shell.setDefaultButton(soOk.getButton());
/* 739 */         soOk.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */         {
/*     */           public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/* 742 */             TranscodeChooser.this.transcodeProfiles = TranscodeChooser.this.selectedTranscodeTarget.getTranscodeProfiles();
/* 743 */             TranscodeChooser.this.createProfileList(TranscodeChooser.this.soList);
/*     */           }
/*     */         });
/*     */       }
/*     */       
/* 748 */       SWTSkinObjectButton soCancel = (SWTSkinObjectButton)this.skin.getSkinObject("cancel");
/* 749 */       if (soCancel != null) {
/* 750 */         soCancel.addSelectionListener(new SWTSkinButtonUtility.ButtonListenerAdapter()
/*     */         {
/*     */           public void pressed(SWTSkinButtonUtility buttonUtility, SWTSkinObject skinObject, int stateMask) {
/* 753 */             TranscodeChooser.this.shell.close();
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */     
/* 759 */     if (this.soBottomContainer != null) {
/* 760 */       this.soBottomContainer.setVisible(false);
/*     */     }
/*     */     
/*     */ 
/* 764 */     Point computeSize = this.shell.computeSize(400, -1, true);
/* 765 */     this.shell.setSize(computeSize);
/* 766 */     this.shell.layout(true);
/* 767 */     Utils.centerWindowRelativeTo(this.shell, this.mainShell);
/*     */   }
/*     */   
/*     */   private Button createDeviceButton(Composite parent, Object deviceObj, String name, String shortDescription, String imageID, Button lastButton)
/*     */   {
/* 772 */     Button button = new Button(parent, 16400);
/* 773 */     StringBuilder sb = new StringBuilder(name);
/* 774 */     button.setFont(this.fontDevice);
/* 775 */     button.setData("Device", deviceObj);
/* 776 */     button.addSelectionListener(new SelectionListener()
/*     */     {
/*     */       public void widgetSelected(SelectionEvent e) {
/* 779 */         Object device = e.widget.getData("Device");
/* 780 */         if ((device instanceof TranscodeTarget)) {
/* 781 */           TranscodeChooser.this.selectedTranscodeTarget = ((TranscodeTarget)device);
/* 782 */         } else if ((device instanceof DeviceTemplate)) {
/* 783 */           TranscodeChooser.this.selectedDeviceTemplate = ((DeviceTemplate)device);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/*     */     });
/* 790 */     if (lastButton == null) {
/* 791 */       button.setSelection(true);
/* 792 */       if ((deviceObj instanceof TranscodeTarget)) {
/* 793 */         this.selectedTranscodeTarget = ((TranscodeTarget)deviceObj);
/*     */       }
/*     */     }
/*     */     
/* 797 */     Image imgRenderer = null;
/* 798 */     if (imageID != null) {
/* 799 */       this.listImageIDsToRelease.add(imageID);
/* 800 */       imgRenderer = ImageLoader.getInstance().getImage(imageID);
/*     */     }
/*     */     
/* 803 */     if (ImageLoader.isRealImage(imgRenderer)) {
/* 804 */       button.setImage(imgRenderer);
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/* 812 */       sb.insert(0, ' ');
/*     */     }
/*     */     
/* 815 */     button.setText(sb.toString());
/*     */     
/* 817 */     FormData fd = new FormData();
/* 818 */     fd.left = new FormAttachment(0, 0);
/* 819 */     if (lastButton == null) {
/* 820 */       fd.top = new FormAttachment(0, 0);
/*     */     } else {
/* 822 */       fd.top = new FormAttachment(lastButton, 15);
/*     */     }
/* 824 */     Utils.setLayoutData(button, fd);
/*     */     
/* 826 */     if ((shortDescription != null) && (shortDescription.length() > 0)) {
/* 827 */       Label label = new Label(parent, 0);
/* 828 */       label.setText("(" + shortDescription + ")");
/*     */       
/* 830 */       fd = new FormData();
/* 831 */       fd.top = new FormAttachment(button, 0, 16777216);
/* 832 */       fd.left = new FormAttachment(button, 5);
/* 833 */       Utils.setLayoutData(label, fd);
/*     */     }
/*     */     
/* 836 */     return button;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void noDevices()
/*     */   {
/* 845 */     new MessageBoxShell(32, "No Devices Found", "We couldn't find any devices.  Maybe you didn't install the Vuze Transcoder Plugin?").open(null);
/*     */     
/*     */ 
/*     */ 
/* 849 */     this.shell.dispose();
/*     */   }
/*     */   
/*     */   public abstract void closed();
/*     */   
/*     */   public int getTranscodeRequirement() {
/* 855 */     return this.transcodeRequirement;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/TranscodeChooser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */