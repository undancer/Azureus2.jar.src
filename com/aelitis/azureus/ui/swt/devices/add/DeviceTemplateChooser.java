/*     */ package com.aelitis.azureus.ui.swt.devices.add;
/*     */ 
/*     */ import com.aelitis.azureus.core.devices.DeviceManager.DeviceManufacturer;
/*     */ import com.aelitis.azureus.core.devices.DeviceTemplate;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsManagerSWT;
/*     */ import com.aelitis.azureus.ui.swt.UIFunctionsSWT;
/*     */ import com.aelitis.azureus.ui.swt.devices.TranscodeChooser;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkin;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObject;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectContainer;
/*     */ import com.aelitis.azureus.ui.swt.skin.SWTSkinObjectText;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog;
/*     */ import com.aelitis.azureus.ui.swt.views.skin.SkinnedDialog.SkinnedDialogClosedListener;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.RowLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Widget;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DeviceTemplateChooser
/*     */ {
/*     */   private DeviceTemplateClosedListener listener;
/*     */   private SkinnedDialog skinnedDialog;
/*     */   private DeviceTemplate selectedDeviceTemplate;
/*     */   private DeviceManager.DeviceManufacturer mf;
/*     */   private SWTSkinObjectContainer soList;
/*     */   
/*     */   public DeviceTemplateChooser(DeviceManager.DeviceManufacturer mf)
/*     */   {
/*  61 */     this.mf = mf;
/*     */   }
/*     */   
/*     */   public void open(DeviceTemplateClosedListener l) {
/*  65 */     this.listener = l;
/*  66 */     this.skinnedDialog = new SkinnedDialog("skin3_dlg_deviceadd_mfchooser", "shell", 2080);
/*     */     
/*     */ 
/*  69 */     this.skinnedDialog.addCloseListener(new SkinnedDialog.SkinnedDialogClosedListener() {
/*     */       public void skinDialogClosed(SkinnedDialog dialog) {
/*  71 */         if (DeviceTemplateChooser.this.listener != null) {
/*  72 */           DeviceTemplateChooser.this.listener.deviceTemplateChooserClosed(DeviceTemplateChooser.this.selectedDeviceTemplate);
/*     */         }
/*     */         
/*     */       }
/*  76 */     });
/*  77 */     SWTSkin skin = this.skinnedDialog.getSkin();
/*  78 */     SWTSkinObject so = skin.getSkinObject("list");
/*  79 */     if ((so instanceof SWTSkinObjectContainer)) {
/*  80 */       this.soList = ((SWTSkinObjectContainer)so);
/*     */       
/*  82 */       createDeviceTemplateList2(this.soList);
/*     */     }
/*     */     
/*  85 */     this.skinnedDialog.open();
/*     */   }
/*     */   
/*     */   private void createDeviceTemplateList2(SWTSkinObjectContainer soList) {
/*  89 */     DeviceTemplate[] devices = this.mf.getDeviceTemplates();
/*     */     
/*  91 */     if (devices.length == 0) {
/*  92 */       noDevices();
/*  93 */       return;
/*     */     }
/*     */     
/*  96 */     Arrays.sort(devices, new Comparator() {
/*     */       public int compare(DeviceTemplate o1, DeviceTemplate o2) {
/*  98 */         return o1.getName().compareToIgnoreCase(o2.getName());
/*     */       }
/*     */       
/* 101 */     });
/* 102 */     Composite parent = soList.getComposite();
/* 103 */     if (parent.getChildren().length > 0) {
/* 104 */       Utils.disposeComposite(parent, false);
/*     */     }
/*     */     
/* 107 */     SWTSkin skin = this.skinnedDialog.getSkin();
/*     */     
/* 109 */     SWTSkinObjectText soInfoTitle = (SWTSkinObjectText)skin.getSkinObject("info-title");
/* 110 */     SWTSkinObjectText soInfoText = (SWTSkinObjectText)skin.getSkinObject("info-text");
/*     */     
/* 112 */     RowLayout layout = new RowLayout(256);
/* 113 */     layout.spacing = 0;
/* 114 */     layout.marginLeft = (layout.marginRight = 0);
/* 115 */     layout.wrap = true;
/* 116 */     layout.justify = true;
/* 117 */     layout.fill = true;
/* 118 */     parent.setLayout(layout);
/*     */     
/*     */ 
/* 121 */     Listener clickListener = new Listener() {
/* 122 */       boolean down = false;
/*     */       
/*     */       public void handleEvent(Event event) {
/* 125 */         if (event.type == 3) {
/* 126 */           this.down = true;
/* 127 */         } else if ((event.type == 4) && (this.down)) {
/* 128 */           Widget widget = (event.widget instanceof Label) ? ((Label)event.widget).getParent() : event.widget;
/*     */           
/* 130 */           DeviceTemplateChooser.this.selectedDeviceTemplate = ((DeviceTemplate)widget.getData("obj"));
/* 131 */           if (DeviceTemplateChooser.this.selectedDeviceTemplate == null) {
/* 132 */             Debug.out("selectedDeviceTemplate is null!");
/*     */           }
/* 134 */           DeviceTemplateChooser.this.skinnedDialog.close();
/* 135 */           this.down = false;
/*     */         }
/*     */       }
/*     */     };
/*     */     
/*     */ 
/* 141 */     for (DeviceTemplate deviceTemplate : devices) {
/* 142 */       if (!deviceTemplate.isAuto())
/*     */       {
/*     */ 
/* 145 */         String iconURL = null;
/* 146 */         TranscodeChooser.addImageBox(parent, clickListener, null, deviceTemplate, iconURL, deviceTemplate.getName());
/*     */       }
/*     */     }
/*     */     
/* 150 */     SWTSkinObjectText soTitle = (SWTSkinObjectText)skin.getSkinObject("title");
/* 151 */     if (soTitle != null) {
/* 152 */       soTitle.setTextID("devices.choose.device.title");
/*     */     }
/*     */     
/* 155 */     SWTSkinObjectText soSubTitle = (SWTSkinObjectText)skin.getSkinObject("subtitle");
/* 156 */     if (soSubTitle != null) {
/* 157 */       soSubTitle.setTextID("label.clickone");
/*     */     }
/*     */     
/* 160 */     Shell shell = this.skinnedDialog.getShell();
/* 161 */     Point computeSize = shell.computeSize(shell.getSize().x, -1, true);
/* 162 */     shell.setSize(computeSize);
/* 163 */     Shell mainShell = UIFunctionsManagerSWT.getUIFunctionsSWT().getMainShell();
/* 164 */     Utils.centerWindowRelativeTo(shell, mainShell);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void noDevices()
/*     */   {
/* 173 */     new MessageBoxShell(32, "No Devices Found", "We couldn't find any devices.  Maybe you didn't install the Vuze Transcoder Plugin?").open(null);
/*     */     
/*     */ 
/*     */ 
/* 177 */     this.skinnedDialog.close();
/*     */   }
/*     */   
/*     */   public static abstract interface DeviceTemplateClosedListener
/*     */   {
/*     */     public abstract void deviceTemplateChooserClosed(DeviceTemplate paramDeviceTemplate);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/devices/add/DeviceTemplateChooser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */