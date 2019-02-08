/*     */ package org.gudy.azureus2.ui.swt.components;
/*     */ 
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.swt.shells.GCStringPrinter;
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
/*     */ public class DoubleBufferedLabel
/*     */   extends Canvas
/*     */   implements PaintListener
/*     */ {
/*  39 */   private String text = "";
/*     */   
/*     */ 
/*     */ 
/*     */   public DoubleBufferedLabel(Composite parent, int style)
/*     */   {
/*  45 */     super(parent, style | 0x20000000);
/*     */     
/*     */ 
/*     */ 
/*  49 */     GridData gridData = new GridData(80);
/*     */     
/*  51 */     setLayoutData(gridData);
/*     */     
/*  53 */     addPaintListener(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setLayoutData(Object ld)
/*     */   {
/*  60 */     if ((ld instanceof GridData))
/*     */     {
/*  62 */       GridData gd = (GridData)ld;
/*     */       
/*     */ 
/*     */ 
/*  66 */       gd.verticalAlignment = 4;
/*     */     }
/*     */     
/*  69 */     super.setLayoutData(ld);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void paintControl(PaintEvent e)
/*     */   {
/*  76 */     e.gc.setAdvanced(true);
/*     */     
/*  78 */     Rectangle clientArea = getClientArea();
/*     */     
/*  80 */     GCStringPrinter sp = new GCStringPrinter(e.gc, getText(), clientArea, true, true, 16384);
/*     */     
/*     */ 
/*  83 */     sp.printString(e.gc, clientArea, 16384);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Point computeSize(int wHint, int hHint)
/*     */   {
/*  92 */     return computeSize(wHint, hHint, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Point computeSize(int wHint, int hHint, boolean changed)
/*     */   {
/*     */     try
/*     */     {
/* 102 */       return computeSize(wHint, hHint, changed, false);
/*     */ 
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/*     */ 
/* 108 */       Debug.out("Error while computing size for DoubleBufferedLabel with text:" + getText() + "; " + t.toString());
/*     */     }
/*     */     
/* 111 */     return new Point(0, 0);
/*     */   }
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
/*     */   public Point computeSize(int wHint, int hHint, boolean changed, boolean realWidth)
/*     */   {
/* 131 */     if ((wHint != -1) && (hHint != -1)) {
/* 132 */       return new Point(wHint, hHint);
/*     */     }
/* 134 */     Point pt = new Point(wHint, hHint);
/*     */     
/* 136 */     Point lastSize = new Point(0, 0);
/*     */     
/* 138 */     GC gc = new GC(this);
/*     */     
/* 140 */     GCStringPrinter sp = new GCStringPrinter(gc, getText(), new Rectangle(0, 0, 10000, 20), true, true, 16384);
/*     */     
/*     */ 
/* 143 */     sp.calculateMetrics();
/*     */     
/* 145 */     Point lastTextSize = sp.getCalculatedSize();
/*     */     
/* 147 */     gc.dispose();
/*     */     
/* 149 */     lastSize.x += lastTextSize.x + 10;
/* 150 */     lastSize.y = Math.max(lastSize.y, lastTextSize.y);
/*     */     
/* 152 */     if (wHint == -1) {
/* 153 */       pt.x = lastSize.x;
/*     */     }
/* 155 */     if (hHint == -1) {
/* 156 */       pt.y = lastSize.y;
/*     */     }
/*     */     
/* 159 */     return pt;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getText()
/*     */   {
/* 165 */     return this.text;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setText(String text)
/*     */   {
/* 172 */     if (text == null) {
/* 173 */       text = "";
/*     */     }
/*     */     
/* 176 */     if (text.equals(getText())) {
/* 177 */       return;
/*     */     }
/*     */     
/* 180 */     this.text = text;
/*     */     
/* 182 */     redraw();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/DoubleBufferedLabel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */