/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import org.eclipse.swt.custom.StackLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.ProgressBar;
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
/*     */ public class AZProgressBar
/*     */   extends Composite
/*     */ {
/*  35 */   private ProgressBar incrementalProgressBar = null;
/*     */   
/*  37 */   private ProgressBar indeterminateProgressBar = null;
/*     */   
/*  39 */   private boolean isIndeterminate = false;
/*     */   
/*  41 */   private StackLayout stack = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public AZProgressBar(Composite parent)
/*     */   {
/*  48 */     this(parent, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public AZProgressBar(Composite parent, boolean isIndeterminate)
/*     */   {
/*  59 */     super(parent, 0);
/*     */     
/*  61 */     this.incrementalProgressBar = new ProgressBar(this, 256);
/*  62 */     this.indeterminateProgressBar = new ProgressBar(this, 258);
/*     */     
/*     */ 
/*  65 */     this.stack = new StackLayout();
/*  66 */     setLayout(this.stack);
/*  67 */     pack();
/*     */     
/*  69 */     setIndeterminate(isIndeterminate);
/*     */   }
/*     */   
/*     */   public void setIndeterminate(boolean isIndeterminate) {
/*  73 */     if ((this.isIndeterminate != isIndeterminate) || (null == this.stack.topControl)) {
/*  74 */       this.isIndeterminate = isIndeterminate;
/*  75 */       if (isIndeterminate) {
/*  76 */         this.stack.topControl = this.indeterminateProgressBar;
/*     */       } else {
/*  78 */         this.incrementalProgressBar.setMinimum(0);
/*  79 */         this.incrementalProgressBar.setMaximum(100);
/*  80 */         this.incrementalProgressBar.setSelection(0);
/*  81 */         this.stack.topControl = this.incrementalProgressBar;
/*     */       }
/*  83 */       layout();
/*     */     }
/*     */   }
/*     */   
/*     */   public void done() {
/*  88 */     this.incrementalProgressBar.setSelection(this.incrementalProgressBar.getMaximum());
/*  89 */     this.stack.topControl = null;
/*  90 */     layout();
/*     */   }
/*     */   
/*     */   public void setSelection(int value) {
/*  94 */     if (this.incrementalProgressBar.getMaximum() < value) {
/*  95 */       done();
/*     */     } else {
/*  97 */       this.incrementalProgressBar.setSelection(value);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setPercentage(int percentage) {
/* 102 */     if ((percentage > 0) && (percentage < 101))
/*     */     {
/* 104 */       int range = this.incrementalProgressBar.getMaximum() - this.incrementalProgressBar.getMinimum();
/*     */       
/* 106 */       setSelection(this.incrementalProgressBar.getMinimum() + range * percentage / 100);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getMaximum()
/*     */   {
/* 114 */     return this.incrementalProgressBar.getMaximum();
/*     */   }
/*     */   
/*     */   public int getMinimum() {
/* 118 */     return this.incrementalProgressBar.getMinimum();
/*     */   }
/*     */   
/*     */   public int getSelection() {
/* 122 */     return this.incrementalProgressBar.getSelection();
/*     */   }
/*     */   
/*     */   public void setMaximum(int value) {
/* 126 */     this.incrementalProgressBar.setMaximum(value);
/*     */   }
/*     */   
/*     */   public void setMinimum(int value) {
/* 130 */     this.incrementalProgressBar.setMinimum(value);
/*     */   }
/*     */   
/*     */   public boolean isIndeterminate() {
/* 134 */     return this.isIndeterminate;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/AZProgressBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */