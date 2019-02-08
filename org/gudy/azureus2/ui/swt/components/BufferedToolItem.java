/*     */ package org.gudy.azureus2.ui.swt.components;
/*     */ 
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.ToolBar;
/*     */ import org.eclipse.swt.widgets.ToolItem;
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
/*     */ public class BufferedToolItem
/*     */   extends BufferedWidget
/*     */ {
/*     */   protected ToolItem item;
/*     */   
/*     */   public BufferedToolItem(ToolBar tool_bar, int attributes)
/*     */   {
/*  46 */     super(new ToolItem(tool_bar, attributes));
/*     */     
/*  48 */     this.item = ((ToolItem)getWidget());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnabled(boolean b)
/*     */   {
/*  55 */     if ((this.item.isDisposed()) || (this.item.getEnabled() == b))
/*     */     {
/*  57 */       return;
/*     */     }
/*     */     
/*  60 */     this.item.setEnabled(b);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSelection(boolean b)
/*     */   {
/*  67 */     if ((this.item.isDisposed()) || (this.item.getSelection() == b))
/*     */     {
/*  69 */       return;
/*     */     }
/*     */     
/*  72 */     this.item.setSelection(b);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setImage(Image i)
/*     */   {
/*  79 */     if (i != null)
/*  80 */       i.setBackground(this.item.getParent().getBackground());
/*  81 */     this.item.setImage(i);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object getData(String key)
/*     */   {
/*  88 */     return this.item.getData(key);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setData(String key, Object d)
/*     */   {
/*  96 */     this.item.setData(key, d);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(int type, Listener l)
/*     */   {
/* 104 */     this.item.addListener(type, l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/BufferedToolItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */