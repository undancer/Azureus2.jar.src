/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
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
/*     */ public class FloatParameter
/*     */ {
/*     */   Text inputField;
/*  32 */   float fMinValue = 0.0F;
/*  33 */   float fMaxValue = -1.0F;
/*     */   float fDefaultValue;
/*  35 */   int iDigitsAfterDecimal = 1;
/*     */   String sParamName;
/*  37 */   boolean allowZero = false;
/*     */   
/*     */   public FloatParameter(Composite composite, String name) {
/*  40 */     this.fDefaultValue = COConfigurationManager.getFloatParameter(name);
/*  41 */     initialize(composite, name);
/*     */   }
/*     */   
/*     */ 
/*     */   public FloatParameter(Composite composite, String name, float minValue, float maxValue, boolean allowZero, int digitsAfterDecimal)
/*     */   {
/*  47 */     this.fDefaultValue = COConfigurationManager.getFloatParameter(name);
/*  48 */     initialize(composite, name);
/*  49 */     this.fMinValue = minValue;
/*  50 */     this.fMaxValue = maxValue;
/*  51 */     this.allowZero = allowZero;
/*  52 */     this.iDigitsAfterDecimal = digitsAfterDecimal;
/*     */   }
/*     */   
/*     */   public void initialize(Composite composite, final String name)
/*     */   {
/*  57 */     this.sParamName = name;
/*     */     
/*  59 */     this.inputField = new Text(composite, 133120);
/*  60 */     float value = COConfigurationManager.getFloatParameter(name);
/*  61 */     this.inputField.setText(String.valueOf(value));
/*  62 */     this.inputField.addListener(25, new Listener() {
/*     */       public void handleEvent(Event e) {
/*  64 */         String text = e.text;
/*  65 */         char[] chars = new char[text.length()];
/*  66 */         text.getChars(0, chars.length, chars, 0);
/*  67 */         for (int i = 0; i < chars.length; i++) {
/*  68 */           if (((chars[i] < '0') || (chars[i] > '9')) && (chars[i] != '.')) {
/*  69 */             e.doit = false;
/*  70 */             return;
/*     */           }
/*     */           
/*     */         }
/*     */       }
/*  75 */     });
/*  76 */     this.inputField.addListener(24, new Listener() {
/*     */       public void handleEvent(Event event) {
/*     */         try {
/*  79 */           float val = Float.parseFloat(FloatParameter.this.inputField.getText());
/*  80 */           if ((val < FloatParameter.this.fMinValue) && (
/*  81 */             (!FloatParameter.this.allowZero) || (val != 0.0F))) {
/*  82 */             val = FloatParameter.this.fMinValue;
/*     */           }
/*     */           
/*  85 */           if ((val > FloatParameter.this.fMaxValue) && 
/*  86 */             (FloatParameter.this.fMaxValue > -1.0F)) {
/*  87 */             val = FloatParameter.this.fMaxValue;
/*     */           }
/*     */           
/*  90 */           COConfigurationManager.setParameter(name, val);
/*     */ 
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*  95 */     });
/*  96 */     this.inputField.addListener(16, new Listener() {
/*     */       public void handleEvent(Event event) {
/*     */         try {
/*  99 */           float val = Float.parseFloat(FloatParameter.this.inputField.getText());
/* 100 */           if ((val < FloatParameter.this.fMinValue) && (
/* 101 */             (!FloatParameter.this.allowZero) || (val != 0.0F))) {
/* 102 */             FloatParameter.this.inputField.setText(String.valueOf(FloatParameter.this.fMinValue));
/* 103 */             COConfigurationManager.setParameter(name, FloatParameter.this.fMinValue);
/*     */           }
/*     */           
/* 106 */           if ((val > FloatParameter.this.fMaxValue) && 
/* 107 */             (FloatParameter.this.fMaxValue > -1.0F)) {
/* 108 */             FloatParameter.this.inputField.setText(String.valueOf(FloatParameter.this.fMaxValue));
/* 109 */             COConfigurationManager.setParameter(name, FloatParameter.this.fMaxValue);
/*     */           }
/*     */         }
/*     */         catch (Exception e) {}
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public void setLayoutData(Object layoutData)
/*     */   {
/* 120 */     Utils.adjustPXForDPI(layoutData);
/* 121 */     this.inputField.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */ 
/*     */   public Control getControl()
/*     */   {
/* 127 */     return this.inputField;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/FloatParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */