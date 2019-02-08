/*     */ package org.gudy.azureus2.ui.swt.config;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
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
/*     */ public class ChangeSelectionActionPerformer
/*     */   implements IAdditionalActionPerformer
/*     */ {
/*  38 */   boolean selected = false;
/*  39 */   boolean reverse_sense = false;
/*     */   Control[] controls;
/*     */   
/*     */   public ChangeSelectionActionPerformer(Control[] controls)
/*     */   {
/*  44 */     this.controls = controls;
/*     */   }
/*     */   
/*     */   public ChangeSelectionActionPerformer(Control control) {
/*  48 */     this.controls = new Control[] { control };
/*     */   }
/*     */   
/*     */   public ChangeSelectionActionPerformer(Parameter p) {
/*  52 */     this.controls = p.getControls();
/*     */   }
/*     */   
/*  55 */   public ChangeSelectionActionPerformer(Parameter p1, Parameter p2) { this(new Parameter[] { p1, p2 }); }
/*     */   
/*     */   public ChangeSelectionActionPerformer(Parameter[] params)
/*     */   {
/*  59 */     List c = new ArrayList();
/*     */     
/*  61 */     for (int i = 0; i < params.length; i++) {
/*  62 */       Control[] x = params[i].getControls();
/*     */       
/*  64 */       Collections.addAll(c, x);
/*     */     }
/*     */     
/*  67 */     this.controls = new Control[c.size()];
/*     */     
/*  69 */     c.toArray(this.controls);
/*     */   }
/*     */   
/*     */   public ChangeSelectionActionPerformer(Control[] controls, boolean _reverse_sense) {
/*  73 */     this.controls = controls;
/*  74 */     this.reverse_sense = _reverse_sense;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void performAction()
/*     */   {
/*  81 */     if (this.controls == null)
/*  82 */       return;
/*  83 */     controlsSetEnabled(this.controls, this.reverse_sense ? false : !this.selected ? true : this.selected);
/*     */   }
/*     */   
/*     */   private void controlsSetEnabled(Control[] controls, boolean bEnabled) {
/*  87 */     for (int i = 0; i < controls.length; i++) {
/*  88 */       if ((controls[i] instanceof Composite))
/*  89 */         controlsSetEnabled(((Composite)controls[i]).getChildren(), bEnabled);
/*  90 */       controls[i].setEnabled(bEnabled);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setIntValue(int value) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setSelected(boolean selected)
/*     */   {
/* 104 */     this.selected = selected;
/*     */   }
/*     */   
/*     */   public void setStringValue(String value) {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/config/ChangeSelectionActionPerformer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */