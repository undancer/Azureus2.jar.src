/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.eclipse.swt.events.SelectionEvent;
/*     */ import org.eclipse.swt.events.SelectionListener;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SWTSkinObjectToggle
/*     */   extends SWTSkinObjectBasic
/*     */ {
/*     */   private Button button;
/*     */   private boolean isToggled;
/*  51 */   private List<SWTSkinToggleListener> buttonListeners = new CopyOnWriteArrayList();
/*     */   
/*     */   public SWTSkinObjectToggle(SWTSkin skin, SWTSkinProperties properties, String id, String configID, SWTSkinObject parentSkinObject)
/*     */   {
/*  55 */     super(skin, properties, id, configID, "toggle", parentSkinObject);
/*     */     Composite createOn;
/*     */     Composite createOn;
/*  58 */     if (this.parent == null) {
/*  59 */       createOn = skin.getShell();
/*     */     } else {
/*  61 */       createOn = (Composite)this.parent.getControl();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  66 */     createOn.setBackgroundMode(2);
/*     */     
/*  68 */     this.button = new Button(createOn, 2);
/*  69 */     this.isToggled = false;
/*     */     
/*  71 */     this.button.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/*  73 */         SWTSkinObjectToggle.this.isToggled = SWTSkinObjectToggle.this.button.getSelection();
/*  74 */         for (SWTSkinToggleListener l : SWTSkinObjectToggle.this.buttonListeners) {
/*     */           try {
/*  76 */             l.toggleChanged(SWTSkinObjectToggle.this, SWTSkinObjectToggle.this.isToggled);
/*     */           } catch (Exception ex) {
/*  78 */             Debug.out(ex);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/*  85 */     });
/*  86 */     setControl(this.button);
/*     */   }
/*     */   
/*     */ 
/*     */   public String switchSuffix(String suffix, int level, boolean walkUp, boolean walkDown)
/*     */   {
/*  92 */     suffix = super.switchSuffix(suffix, level, walkUp, walkDown);
/*     */     
/*  94 */     if (suffix == null) {
/*  95 */       return null;
/*     */     }
/*     */     
/*  98 */     String sPrefix = this.sConfigID + ".text";
/*  99 */     String text = this.properties.getStringValue(sPrefix + suffix);
/* 100 */     if (text != null) {
/* 101 */       setText(text);
/*     */     }
/*     */     
/* 104 */     return suffix;
/*     */   }
/*     */   
/*     */   public void addSelectionListener(SWTSkinToggleListener listener) {
/* 108 */     if (this.buttonListeners.contains(listener)) {
/* 109 */       return;
/*     */     }
/* 111 */     this.buttonListeners.add(listener);
/*     */   }
/*     */   
/*     */   public void setText(final String text) {
/* 115 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 117 */         if ((SWTSkinObjectToggle.this.button != null) && (!SWTSkinObjectToggle.this.button.isDisposed())) {
/* 118 */           SWTSkinObjectToggle.this.button.setText(text);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public boolean isToggled()
/*     */   {
/* 126 */     return this.isToggled;
/*     */   }
/*     */   
/*     */   public void setToggled(boolean b) {
/* 130 */     this.isToggled = b;
/* 131 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 133 */         if ((SWTSkinObjectToggle.this.button != null) && (!SWTSkinObjectToggle.this.button.isDisposed())) {
/* 134 */           SWTSkinObjectToggle.this.button.setSelection(SWTSkinObjectToggle.this.isToggled);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectToggle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */