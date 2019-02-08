/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.ui.swt.config.generic.GenericBooleanParameter;
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
/*     */ public class BooleanParameter
/*     */   extends Parameter
/*     */ {
/*     */   protected GenericBooleanParameter delegate;
/*     */   
/*     */   public BooleanParameter(Composite composite, String name)
/*     */   {
/*  33 */     super(name);
/*  34 */     this.delegate = new GenericBooleanParameter(this.config_adapter, composite, name, Boolean.valueOf(COConfigurationManager.getBooleanParameter(name)), null, null);
/*     */   }
/*     */   
/*     */   public BooleanParameter(Composite composite, String name, String textKey) {
/*  38 */     super(name);
/*  39 */     this.delegate = new GenericBooleanParameter(this.config_adapter, composite, name, Boolean.valueOf(COConfigurationManager.getBooleanParameter(name)), textKey, null);
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public BooleanParameter(Composite composite, String name, boolean defaultValue, String textKey)
/*     */   {
/*  47 */     super(name);
/*  48 */     this.delegate = new GenericBooleanParameter(this.config_adapter, composite, name, Boolean.valueOf(defaultValue), textKey, null);
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public BooleanParameter(Composite composite, String name, boolean defaultValue) {
/*  55 */     super(name);
/*  56 */     this.delegate = new GenericBooleanParameter(this.config_adapter, composite, name, Boolean.valueOf(defaultValue), null, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public BooleanParameter(Composite composite, String _name, boolean _defaultValue, String textKey, IAdditionalActionPerformer actionPerformer)
/*     */   {
/*  70 */     super(_name);
/*  71 */     this.delegate = new GenericBooleanParameter(this.config_adapter, composite, _name, Boolean.valueOf(_defaultValue), textKey, actionPerformer);
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isInitialised()
/*     */   {
/*  77 */     return this.delegate != null;
/*     */   }
/*     */   
/*     */   public void setLayoutData(Object layoutData) {
/*  81 */     this.delegate.setLayoutData(layoutData);
/*     */   }
/*     */   
/*     */   public void setAdditionalActionPerformer(IAdditionalActionPerformer actionPerformer) {
/*  85 */     this.delegate.setAdditionalActionPerformer(actionPerformer);
/*     */   }
/*     */   
/*     */   public Control getControl() {
/*  89 */     return this.delegate.getControl();
/*     */   }
/*     */   
/*     */   public String getName() {
/*  93 */     return this.delegate.getName();
/*     */   }
/*     */   
/*     */   public void setName(String newName) {
/*  97 */     this.delegate.setName(newName);
/*     */   }
/*     */   
/*     */ 
/*     */   public Boolean isSelected()
/*     */   {
/* 103 */     return this.delegate.isSelected();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSelected(boolean selected)
/*     */   {
/* 110 */     this.delegate.setSelected(selected);
/*     */   }
/*     */   
/*     */   public void setValue(Object value) {
/* 114 */     if ((value instanceof Boolean)) {
/* 115 */       setSelected(((Boolean)value).booleanValue());
/*     */     }
/*     */   }
/*     */   
/*     */   public Object getValueObject() {
/* 120 */     return isSelected();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/BooleanParameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */