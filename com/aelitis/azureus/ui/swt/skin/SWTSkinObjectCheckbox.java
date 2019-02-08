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
/*     */ 
/*     */ public class SWTSkinObjectCheckbox
/*     */   extends SWTSkinObjectBasic
/*     */ {
/*     */   private Button button;
/*     */   private boolean checked;
/*  52 */   private List<SWTSkinCheckboxListener> buttonListeners = new CopyOnWriteArrayList();
/*     */   
/*     */   public SWTSkinObjectCheckbox(SWTSkin skin, SWTSkinProperties properties, String id, String configID, SWTSkinObject parentSkinObject)
/*     */   {
/*  56 */     super(skin, properties, id, configID, "checkbox", parentSkinObject);
/*     */     Composite createOn;
/*     */     Composite createOn;
/*  59 */     if (this.parent == null) {
/*  60 */       createOn = skin.getShell();
/*     */     } else {
/*  62 */       createOn = (Composite)this.parent.getControl();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  67 */     createOn.setBackgroundMode(2);
/*     */     
/*  69 */     int style = 96;
/*  70 */     String[] styles = properties.getStringArray(configID + ".style");
/*  71 */     if (styles != null) {
/*  72 */       for (String s : styles) {
/*  73 */         if (s.toLowerCase().equals("radio")) {
/*  74 */           style = 0x10 | style & 0xFFFFFFDF;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*  79 */     this.button = new Button(createOn, style);
/*  80 */     this.checked = false;
/*     */     
/*  82 */     this.button.addSelectionListener(new SelectionListener() {
/*     */       public void widgetSelected(SelectionEvent e) {
/*  84 */         SWTSkinObjectCheckbox.this.checked = SWTSkinObjectCheckbox.this.button.getSelection();
/*  85 */         for (SWTSkinCheckboxListener l : SWTSkinObjectCheckbox.this.buttonListeners) {
/*     */           try {
/*  87 */             l.checkboxChanged(SWTSkinObjectCheckbox.this, SWTSkinObjectCheckbox.this.checked);
/*     */           } catch (Exception ex) {
/*  89 */             Debug.out(ex);
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */       public void widgetDefaultSelected(SelectionEvent e) {}
/*  96 */     });
/*  97 */     setControl(this.button);
/*     */   }
/*     */   
/*     */ 
/*     */   public String switchSuffix(String suffix, int level, boolean walkUp, boolean walkDown)
/*     */   {
/* 103 */     suffix = super.switchSuffix(suffix, level, walkUp, walkDown);
/*     */     
/* 105 */     if (suffix == null) {
/* 106 */       return null;
/*     */     }
/*     */     
/* 109 */     String sPrefix = this.sConfigID + ".text";
/* 110 */     String text = this.properties.getStringValue(sPrefix + suffix);
/* 111 */     if (text != null) {
/* 112 */       setText(text);
/*     */     }
/*     */     
/* 115 */     return suffix;
/*     */   }
/*     */   
/*     */   public void addSelectionListener(SWTSkinCheckboxListener listener) {
/* 119 */     if (this.buttonListeners.contains(listener)) {
/* 120 */       return;
/*     */     }
/* 122 */     this.buttonListeners.add(listener);
/*     */   }
/*     */   
/*     */   public void setText(final String text) {
/* 126 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 128 */         if ((SWTSkinObjectCheckbox.this.button != null) && (!SWTSkinObjectCheckbox.this.button.isDisposed())) {
/* 129 */           SWTSkinObjectCheckbox.this.button.setText(text);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public boolean isChecked()
/*     */   {
/* 137 */     return this.checked;
/*     */   }
/*     */   
/*     */   public void setChecked(boolean b) {
/* 141 */     this.checked = b;
/* 142 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 144 */         if ((SWTSkinObjectCheckbox.this.button != null) && (!SWTSkinObjectCheckbox.this.button.isDisposed())) {
/* 145 */           SWTSkinObjectCheckbox.this.button.setSelection(SWTSkinObjectCheckbox.this.checked);
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectCheckbox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */