/*     */ package com.aelitis.azureus.ui.swt.devices.add;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.DeviceManager;
/*     */ import com.aelitis.azureus.core.devices.DeviceManager.DeviceManufacturer;
/*     */ import com.aelitis.azureus.core.devices.DeviceManagerFactory;
/*     */ import com.aelitis.azureus.core.devices.DeviceTemplate;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog.SkinnedDialogClosedListener;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.RowLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Widget;
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
/*     */ public class ManufacturerChooser
/*     */ {
/*     */   private SkinnedDialog skinnedDialog;
/*     */   private ClosedListener listener;
/*     */   protected DeviceManager.DeviceManufacturer chosenMF;
/*     */   
/*     */   public void open(ClosedListener l)
/*     */   {
/*  51 */     this.listener = l;
/*  52 */     this.skinnedDialog = new SkinnedDialog("skin3_dlg_deviceadd_mfchooser", "shell", 2080);
/*     */     
/*     */ 
/*  55 */     this.skinnedDialog.addCloseListener(new SkinnedDialog.SkinnedDialogClosedListener() {
/*     */       public void skinDialogClosed(SkinnedDialog dialog) {
/*  57 */         if (ManufacturerChooser.this.listener != null) {
/*  58 */           ManufacturerChooser.this.listener.MfChooserClosed(ManufacturerChooser.this.chosenMF);
/*     */         }
/*     */         
/*     */       }
/*  62 */     });
/*  63 */     SWTSkin skin = this.skinnedDialog.getSkin();
/*  64 */     SWTSkinObject so = skin.getSkinObject("list");
/*  65 */     if ((so instanceof SWTSkinObjectContainer)) {
/*  66 */       SWTSkinObjectContainer soList = (SWTSkinObjectContainer)so;
/*     */       
/*  68 */       Composite parent = soList.getComposite();
/*     */       
/*  70 */       Canvas centerCanvas = new Canvas(parent, 0);
/*  71 */       FormData fd = Utils.getFilledFormData();
/*  72 */       fd.bottom = null;
/*  73 */       fd.height = 0;
/*  74 */       centerCanvas.setLayoutData(fd);
/*     */       
/*     */ 
/*  77 */       Composite area = new Composite(parent, 0);
/*  78 */       RowLayout rowLayout = new RowLayout(512);
/*  79 */       rowLayout.fill = true;
/*  80 */       Utils.setLayout(area, rowLayout);
/*  81 */       fd = Utils.getFilledFormData();
/*  82 */       fd.left = new FormAttachment(centerCanvas, 50, 16777216);
/*  83 */       fd.right = null;
/*  84 */       area.setLayoutData(fd);
/*     */       
/*  86 */       Listener btnListener = new Listener() {
/*     */         public void handleEvent(Event event) {
/*  88 */           ManufacturerChooser.this.chosenMF = ((DeviceManager.DeviceManufacturer)event.widget.getData("mf"));
/*  89 */           ManufacturerChooser.this.skinnedDialog.close();
/*     */         }
/*     */         
/*  92 */       };
/*  93 */       DeviceManager deviceManager = DeviceManagerFactory.getSingleton();
/*  94 */       DeviceManager.DeviceManufacturer[] mfs = deviceManager.getDeviceManufacturers(3);
/*  95 */       for (DeviceManager.DeviceManufacturer mf : mfs) {
/*  96 */         DeviceTemplate[] deviceTemplates = mf.getDeviceTemplates();
/*  97 */         boolean hasNonAuto = false;
/*  98 */         for (DeviceTemplate deviceTemplate : deviceTemplates) {
/*  99 */           if (!deviceTemplate.isAuto()) {
/* 100 */             hasNonAuto = true;
/* 101 */             break;
/*     */           }
/*     */         }
/* 104 */         if (hasNonAuto)
/*     */         {
/*     */ 
/* 107 */           Button button = new Button(area, 8);
/* 108 */           button.setText(mf.getName());
/* 109 */           button.setData("mf", mf);
/* 110 */           button.addListener(4, btnListener);
/*     */         }
/*     */       }
/*     */     }
/* 114 */     this.skinnedDialog.getShell().pack();
/* 115 */     this.skinnedDialog.open();
/*     */   }
/*     */   
/*     */   public static abstract interface ClosedListener
/*     */   {
/*     */     public abstract void MfChooserClosed(DeviceManager.DeviceManufacturer paramDeviceManufacturer);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/add/ManufacturerChooser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */