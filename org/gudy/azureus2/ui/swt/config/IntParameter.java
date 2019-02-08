/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.gudy.azureus2.ui.swt.config.generic.GenericIntParameter;
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
/*     */ public class IntParameter
/*     */   extends Parameter
/*     */ {
/*     */   protected GenericIntParameter delegate;
/*     */   
/*     */   public IntParameter(Composite composite, String name)
/*     */   {
/*  38 */     super(name);
/*  39 */     this.delegate = new GenericIntParameter(this.config_adapter, composite, name);
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public IntParameter(Composite composite, String name, int defaultValue) {
/*  46 */     super(name);
/*  47 */     this.delegate = new GenericIntParameter(this.config_adapter, composite, name, defaultValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public IntParameter(Composite composite, String name, int minValue, int maxValue)
/*     */   {
/*  56 */     super(name);
/*  57 */     this.delegate = new GenericIntParameter(this.config_adapter, composite, name, minValue, maxValue);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isInitialised()
/*     */   {
/*  65 */     return this.delegate != null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMinimumValue(int value)
/*     */   {
/*  72 */     this.delegate.setMinimumValue(value);
/*     */   }
/*     */   
/*     */ 
/*     */   public void setMaximumValue(int value)
/*     */   {
/*  78 */     this.delegate.setMaximumValue(value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setValue(int value)
/*     */   {
/*  85 */     this.delegate.setValue(value);
/*     */   }
/*     */   
/*     */ 
/*     */   public void resetToDefault()
/*     */   {
/*  91 */     this.delegate.resetToDefault();
/*     */   }
/*     */   
/*     */ 
/*     */   public int getValue()
/*     */   {
/*  97 */     return this.delegate.getValue();
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object layoutData)
/*     */   {
/* 102 */     this.delegate.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Control getControl()
/*     */   {
/* 109 */     return this.delegate.getControl();
/*     */   }
/*     */   
/*     */   public boolean isGeneratingIntermediateEvents() {
/* 113 */     return this.delegate.isGeneratingIntermediateEvents();
/*     */   }
/*     */   
/*     */   public void setGenerateIntermediateEvents(boolean generateIntermediateEvents) {
/* 117 */     this.delegate.setGenerateIntermediateEvents(generateIntermediateEvents);
/*     */   }
/*     */   
/*     */   public void setValue(Object value)
/*     */   {
/* 122 */     if ((value instanceof Number)) {
/* 123 */       setValue(((Number)value).intValue());
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getValueObject()
/*     */   {
/* 129 */     return new Integer(getValue());
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/IntParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */