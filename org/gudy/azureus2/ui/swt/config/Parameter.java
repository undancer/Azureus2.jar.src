/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
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
/*     */ public abstract class Parameter
/*     */   implements IParameter
/*     */ {
/*     */   protected ConfigParameterAdapter config_adapter;
/*     */   protected List change_listeners;
/*  41 */   private static AEMonitor class_mon = new AEMonitor("Parameter:class");
/*     */   
/*     */   public Parameter(String sConfigID) {
/*  44 */     if (sConfigID != null) {
/*  45 */       this.config_adapter = new ConfigParameterAdapter(this, sConfigID);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isInitialised()
/*     */   {
/*  52 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */   public Control[] getControls()
/*     */   {
/*  58 */     return new Control[] { getControl() };
/*     */   }
/*     */   
/*     */ 
/*     */   public void addChangeListener(ParameterChangeListener l)
/*     */   {
/*     */     try
/*     */     {
/*  66 */       class_mon.enter();
/*     */       
/*  68 */       if (this.change_listeners == null)
/*     */       {
/*  70 */         this.change_listeners = new ArrayList(1);
/*     */       }
/*     */       
/*  73 */       this.change_listeners.add(l);
/*     */     }
/*     */     finally
/*     */     {
/*  77 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeChangeListener(ParameterChangeListener l)
/*     */   {
/*     */     try
/*     */     {
/*  86 */       class_mon.enter();
/*     */       
/*  88 */       this.change_listeners.remove(l);
/*     */     }
/*     */     finally
/*     */     {
/*  92 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnabled(boolean enabled)
/*     */   {
/* 100 */     for (Control c : getControls())
/*     */     {
/* 102 */       c.setEnabled(enabled);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDisposed()
/*     */   {
/* 109 */     return getControl().isDisposed();
/*     */   }
/*     */   
/*     */   public abstract void setValue(Object paramObject);
/*     */   
/*     */   public Object getValueObject() {
/* 115 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/Parameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */