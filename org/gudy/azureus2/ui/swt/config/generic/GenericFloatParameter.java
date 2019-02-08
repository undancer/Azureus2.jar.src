/*     */ package org.gudy.azureus2.ui.swt.config.generic;
/*     */ 
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
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
/*     */ public class GenericFloatParameter
/*     */ {
/*     */   Text inputField;
/*  32 */   float fMinValue = 0.0F;
/*  33 */   float fMaxValue = -1.0F;
/*  34 */   boolean allowZero = false;
/*     */   private GenericParameterAdapter adapter;
/*     */   private String name;
/*     */   
/*     */   public GenericFloatParameter(GenericParameterAdapter adapter, Composite composite, String name) {
/*  39 */     adapter.getFloatValue(name);
/*  40 */     initialize(adapter, composite, name);
/*     */   }
/*     */   
/*     */ 
/*     */   public GenericFloatParameter(GenericParameterAdapter adapter, Composite composite, String name, float minValue, float maxValue, boolean allowZero, int digitsAfterDecimal)
/*     */   {
/*  46 */     adapter.getFloatValue(name);
/*  47 */     initialize(adapter, composite, name);
/*  48 */     this.fMinValue = minValue;
/*  49 */     this.fMaxValue = maxValue;
/*  50 */     this.allowZero = allowZero;
/*     */   }
/*     */   
/*     */ 
/*     */   public void initialize(final GenericParameterAdapter adapter, Composite composite, final String name)
/*     */   {
/*  56 */     this.adapter = adapter;
/*  57 */     this.name = name;
/*  58 */     this.inputField = new Text(composite, 133120);
/*  59 */     float value = adapter.getFloatValue(name);
/*  60 */     this.inputField.setText(String.valueOf(value));
/*  61 */     this.inputField.addListener(25, new Listener() {
/*     */       public void handleEvent(Event e) {
/*  63 */         String text = e.text;
/*  64 */         char[] chars = new char[text.length()];
/*  65 */         text.getChars(0, chars.length, chars, 0);
/*  66 */         for (int i = 0; i < chars.length; i++) {
/*  67 */           if (((chars[i] < '0') || (chars[i] > '9')) && (chars[i] != '.')) {
/*  68 */             e.doit = false;
/*  69 */             return;
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*  74 */     });
/*  75 */     this.inputField.addListener(24, new Listener() {
/*     */       public void handleEvent(Event event) {
/*     */         try {
/*  78 */           float val = Float.parseFloat(GenericFloatParameter.this.inputField.getText());
/*  79 */           if ((val < GenericFloatParameter.this.fMinValue) && (
/*  80 */             (!GenericFloatParameter.this.allowZero) || (val != 0.0F))) {
/*  81 */             val = GenericFloatParameter.this.fMinValue;
/*     */           }
/*     */           
/*  84 */           if ((val > GenericFloatParameter.this.fMaxValue) && 
/*  85 */             (GenericFloatParameter.this.fMaxValue > -1.0F)) {
/*  86 */             val = GenericFloatParameter.this.fMaxValue;
/*     */           }
/*     */           
/*     */ 
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*     */       
/*     */ 
/*  95 */     });
/*  96 */     this.inputField.addListener(16, new Listener() {
/*     */       public void handleEvent(Event event) {
/*     */         try {
/*  99 */           float val = Float.parseFloat(GenericFloatParameter.this.inputField.getText());
/* 100 */           if ((val < GenericFloatParameter.this.fMinValue) && (
/* 101 */             (!GenericFloatParameter.this.allowZero) || (val != 0.0F))) {
/* 102 */             GenericFloatParameter.this.inputField.setText(String.valueOf(GenericFloatParameter.this.fMinValue));
/* 103 */             val = GenericFloatParameter.this.fMinValue;
/*     */           }
/*     */           
/* 106 */           if ((val > GenericFloatParameter.this.fMaxValue) && 
/* 107 */             (GenericFloatParameter.this.fMaxValue > -1.0F)) {
/* 108 */             GenericFloatParameter.this.inputField.setText(String.valueOf(GenericFloatParameter.this.fMaxValue));
/* 109 */             val = GenericFloatParameter.this.fMaxValue;
/*     */           }
/*     */           
/* 112 */           adapter.setFloatValue(name, val);
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setValue(final float value)
/*     */   {
/* 123 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 125 */         if (!GenericFloatParameter.this.inputField.isDisposed()) {
/* 126 */           GenericFloatParameter.this.inputField.setText(String.valueOf(value));
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public float getValue()
/*     */   {
/* 135 */     return this.adapter.getFloatValue(this.name);
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh()
/*     */   {
/* 141 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 143 */         if (!GenericFloatParameter.this.inputField.isDisposed()) {
/* 144 */           GenericFloatParameter.this.inputField.setText(String.valueOf(GenericFloatParameter.this.adapter.getFloatValue(GenericFloatParameter.this.name)));
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object layoutData) {
/* 151 */     Utils.adjustPXForDPI(layoutData);
/* 152 */     this.inputField.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */ 
/*     */   public Control getControl()
/*     */   {
/* 158 */     return this.inputField;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/generic/GenericFloatParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */