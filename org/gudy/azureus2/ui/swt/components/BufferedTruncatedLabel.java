/*     */ package org.gudy.azureus2.ui.swt.components;
/*     */ 
/*     */ import org.eclipse.swt.events.MouseListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Cursor;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.util.DisplayFormatters;
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
/*     */ public class BufferedTruncatedLabel
/*     */   extends BufferedWidget
/*     */ {
/*     */   protected Label label;
/*     */   protected int width;
/*  42 */   protected String value = "";
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public BufferedTruncatedLabel(Composite composite, int attrs, int _width)
/*     */   {
/*  50 */     super(new Label(composite, attrs));
/*     */     
/*  52 */     this.label = ((Label)getWidget());
/*  53 */     this.width = _width;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isDisposed()
/*     */   {
/*  59 */     return this.label.isDisposed();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLayoutData(GridData gd)
/*     */   {
/*  66 */     if (isDisposed()) {
/*  67 */       return;
/*     */     }
/*  69 */     this.label.setLayoutData(gd);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setText(String new_value)
/*     */   {
/*  76 */     if (this.label.isDisposed()) {
/*  77 */       return;
/*     */     }
/*     */     
/*  80 */     if (new_value == this.value)
/*     */     {
/*  82 */       return;
/*     */     }
/*     */     
/*  85 */     if ((new_value != null) && (this.value != null) && (new_value.equals(this.value)))
/*     */     {
/*     */ 
/*     */ 
/*  89 */       return;
/*     */     }
/*     */     
/*  92 */     this.value = new_value;
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  98 */     this.label.setText(this.value == null ? "" : DisplayFormatters.truncateString(this.value.replaceAll("&", "&&"), this.width));
/*     */   }
/*     */   
/*     */   public String getText() {
/* 102 */     return this.value == null ? "" : this.value;
/*     */   }
/*     */   
/*     */   public void addMouseListener(MouseListener listener) {
/* 106 */     this.label.addMouseListener(listener);
/*     */   }
/*     */   
/*     */   public void setForeground(Color color) {
/* 110 */     if (isDisposed()) {
/* 111 */       return;
/*     */     }
/* 113 */     this.label.setForeground(color);
/*     */   }
/*     */   
/*     */   public void setCursor(Cursor cursor) {
/* 117 */     if ((isDisposed()) || (cursor == null) || (cursor.isDisposed())) {
/* 118 */       return;
/*     */     }
/* 120 */     this.label.setCursor(cursor);
/*     */   }
/*     */   
/*     */   public void setToolTipText(String toolTipText) {
/* 124 */     if (isDisposed()) {
/* 125 */       return;
/*     */     }
/* 127 */     this.label.setToolTipText(toolTipText);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/BufferedTruncatedLabel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */