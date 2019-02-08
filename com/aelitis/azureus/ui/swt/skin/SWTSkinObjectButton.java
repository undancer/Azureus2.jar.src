/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.util.ArrayList;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ public class SWTSkinObjectButton
/*     */   extends SWTSkinObjectBasic
/*     */ {
/*     */   private Button button;
/*  52 */   private ArrayList<SWTSkinButtonUtility.ButtonListenerAdapter> buttonListeners = new ArrayList(1);
/*     */   private boolean textOverride;
/*     */   private String imageID;
/*     */   
/*     */   public SWTSkinObjectButton(SWTSkin skin, SWTSkinProperties properties, String id, String configID, SWTSkinObject parentSkinObject)
/*     */   {
/*  58 */     super(skin, properties, id, configID, "button", parentSkinObject);
/*     */     Composite createOn;
/*     */     Composite createOn;
/*  61 */     if (this.parent == null) {
/*  62 */       createOn = skin.getShell();
/*     */     } else {
/*  64 */       createOn = (Composite)this.parent.getControl();
/*     */     }
/*     */     
/*  67 */     Control c = null;
/*     */     
/*  69 */     if (Constants.isWindows)
/*     */     {
/*     */ 
/*     */ 
/*  73 */       createOn = new Composite(createOn, 0);
/*  74 */       createOn.setLayout(new FormLayout());
/*  75 */       createOn.setBackgroundMode(2);
/*  76 */       c = createOn;
/*     */     }
/*     */     
/*  79 */     this.button = new Button(createOn, 8);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  84 */     if (Constants.isWindows) {
/*  85 */       this.button.setLayoutData(Utils.getFilledFormData());
/*     */     } else {
/*  87 */       c = this.button;
/*     */     }
/*     */     
/*  90 */     this.button.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/*  92 */         Object[] listeners = SWTSkinObjectButton.this.buttonListeners.toArray();
/*  93 */         for (int i = 0; i < listeners.length; i++) {
/*  94 */           SWTSkinButtonUtility.ButtonListenerAdapter l = (SWTSkinButtonUtility.ButtonListenerAdapter)listeners[i];
/*  95 */           l.pressed(null, SWTSkinObjectButton.this, e.stateMask);
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/* 101 */     });
/* 102 */     setControl(c);
/*     */   }
/*     */   
/*     */   public String switchSuffix(String suffix, int level, boolean walkUp, boolean walkDown)
/*     */   {
/* 107 */     suffix = super.switchSuffix(suffix, level, walkUp, walkDown);
/*     */     
/* 109 */     if (suffix == null) {
/* 110 */       return null;
/*     */     }
/*     */     
/* 113 */     String sPrefix = this.sConfigID + ".text";
/* 114 */     String text = this.properties.getStringValue(sPrefix + suffix);
/* 115 */     if (text != null) {
/* 116 */       setText(text, true);
/*     */     }
/*     */     
/* 119 */     String fSuffix = suffix;
/* 120 */     String oldImageID = this.imageID;
/* 121 */     this.imageID = (this.sConfigID + ".image" + fSuffix);
/* 122 */     String imageVal = this.properties.getStringValue(this.imageID);
/* 123 */     if (imageVal != null) {
/* 124 */       if (oldImageID != null) {
/* 125 */         ImageLoader imageLoader = this.skin.getImageLoader(this.properties);
/* 126 */         imageLoader.releaseImage(oldImageID);
/*     */       }
/* 128 */       Utils.execSWTThread(new AERunnable() {
/*     */         public void runSupport() {
/* 130 */           if ((SWTSkinObjectButton.this.button != null) && (!SWTSkinObjectButton.this.button.isDisposed()) && (SWTSkinObjectButton.this.imageID != null)) {
/* 131 */             ImageLoader imageLoader = SWTSkinObjectButton.this.skin.getImageLoader(SWTSkinObjectButton.this.properties);
/* 132 */             Image image = imageLoader.getImage(SWTSkinObjectButton.this.imageID);
/* 133 */             if (ImageLoader.isRealImage(image)) {
/* 134 */               SWTSkinObjectButton.this.button.setImage(image);
/*     */             } else {
/* 136 */               SWTSkinObjectButton.this.button.setImage(null);
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */     
/* 143 */     return suffix;
/*     */   }
/*     */   
/*     */   public void addSelectionListener(SWTSkinButtonUtility.ButtonListenerAdapter listener) {
/* 147 */     if (this.buttonListeners.contains(listener)) {
/* 148 */       return;
/*     */     }
/* 150 */     this.buttonListeners.add(listener);
/*     */   }
/*     */   
/*     */   public void setText(String text) {
/* 154 */     setText(text, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setText(final String text, boolean auto)
/*     */   {
/* 163 */     if (!auto) {
/* 164 */       this.textOverride = true;
/* 165 */     } else if (this.textOverride) {
/* 166 */       return;
/*     */     }
/*     */     
/* 169 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 171 */         if ((SWTSkinObjectButton.this.button != null) && (!SWTSkinObjectButton.this.button.isDisposed())) {
/* 172 */           SWTSkinObjectButton.this.button.setText(text);
/* 173 */           int width = SWTSkinObjectButton.this.properties.getIntValue(SWTSkinObjectButton.this.sConfigID + ".width", -1);
/* 174 */           if (width == -1) {
/* 175 */             int minWidth = SWTSkinObjectButton.this.properties.getIntValue(SWTSkinObjectButton.this.sConfigID + ".minwidth", -1);
/* 176 */             if (minWidth >= 0) {
/* 177 */               minWidth = Utils.adjustPXForDPI(minWidth);
/* 178 */               FormData fd = (FormData)SWTSkinObjectButton.this.button.getLayoutData();
/* 179 */               if (fd == null) {
/* 180 */                 fd = new FormData();
/*     */               }
/* 182 */               Point size = SWTSkinObjectButton.this.button.computeSize(-1, -1);
/* 183 */               if (size.x < minWidth) {
/* 184 */                 fd.width = minWidth;
/*     */               } else {
/* 186 */                 fd.width = -1;
/*     */               }
/* 188 */               SWTSkinObjectButton.this.button.setLayoutData(fd);
/* 189 */               Utils.relayout(SWTSkinObjectButton.this.control);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public Button getButton()
/*     */   {
/* 199 */     return this.button;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */