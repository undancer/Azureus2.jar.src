/*     */ package org.gudy.azureus2.ui.swt.views.table.impl;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableCellCore;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Monitor;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.gudy.azureus2.ui.swt.views.table.TableViewSWT;
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
/*     */ public class TableTooltips
/*     */   implements Listener
/*     */ {
/*  40 */   Shell toolTipShell = null;
/*     */   
/*  42 */   Shell mainShell = null;
/*     */   
/*  44 */   Label toolTipLabel = null;
/*     */   
/*     */ 
/*     */   private final Composite composite;
/*     */   
/*     */   private final TableViewSWT tv;
/*     */   
/*     */ 
/*     */   public TableTooltips(TableViewSWT tv, Composite composite)
/*     */   {
/*  54 */     this.tv = tv;
/*  55 */     this.composite = composite;
/*  56 */     this.mainShell = composite.getShell();
/*     */     
/*  58 */     composite.addListener(12, this);
/*  59 */     composite.addListener(1, this);
/*  60 */     composite.addListener(5, this);
/*  61 */     composite.addListener(32, this);
/*  62 */     this.mainShell.addListener(27, this);
/*  63 */     tv.getComposite().addListener(27, this);
/*     */   }
/*     */   
/*     */   public void handleEvent(Event event) {
/*  67 */     switch (event.type) {
/*     */     case 32: 
/*  69 */       if ((this.toolTipShell != null) && (!this.toolTipShell.isDisposed())) {
/*  70 */         this.toolTipShell.dispose();
/*     */       }
/*  72 */       TableCellCore cell = this.tv.getTableCell(event.x, event.y);
/*  73 */       if (cell == null)
/*  74 */         return;
/*  75 */       cell.invokeToolTipListeners(0);
/*  76 */       Object oToolTip = cell.getToolTip();
/*  77 */       if (oToolTip == null) {
/*  78 */         oToolTip = cell.getDefaultToolTip();
/*     */       }
/*     */       
/*     */ 
/*  82 */       if ((oToolTip == null) || (oToolTip.toString().length() == 0)) {
/*  83 */         return;
/*     */       }
/*  85 */       Display d = this.composite.getDisplay();
/*  86 */       if (d == null) {
/*  87 */         return;
/*     */       }
/*     */       
/*  90 */       this.toolTipShell = new Shell(this.composite.getShell(), 16384);
/*  91 */       FillLayout f = new FillLayout();
/*     */       try {
/*  93 */         f.marginWidth = 3;
/*  94 */         f.marginHeight = 1;
/*     */       }
/*     */       catch (NoSuchFieldError e) {}
/*     */       
/*  98 */       this.toolTipShell.setLayout(f);
/*  99 */       this.toolTipShell.setBackground(d.getSystemColor(29));
/*     */       
/* 101 */       Point size = new Point(0, 0);
/*     */       
/* 103 */       if ((oToolTip instanceof String)) {
/* 104 */         String sToolTip = (String)oToolTip;
/* 105 */         this.toolTipLabel = new Label(this.toolTipShell, 64);
/* 106 */         this.toolTipLabel.setForeground(d.getSystemColor(28));
/* 107 */         this.toolTipLabel.setBackground(d.getSystemColor(29));
/* 108 */         this.toolTipShell.setData("TableCellSWT", cell);
/* 109 */         this.toolTipLabel.setText(sToolTip.replaceAll("&", "&&"));
/*     */         
/*     */ 
/* 112 */         size = this.toolTipLabel.computeSize(-1, -1);
/* 113 */         if (size.x > 600) {
/* 114 */           size = this.toolTipLabel.computeSize(600, -1, true);
/*     */         }
/* 116 */       } else if ((oToolTip instanceof Image)) {
/* 117 */         Image image = (Image)oToolTip;
/* 118 */         this.toolTipLabel = new Label(this.toolTipShell, 16777216);
/* 119 */         this.toolTipLabel.setForeground(d.getSystemColor(28));
/* 120 */         this.toolTipLabel.setBackground(d.getSystemColor(29));
/* 121 */         this.toolTipShell.setData("TableCellSWT", cell);
/* 122 */         this.toolTipLabel.setImage(image);
/* 123 */         size = this.toolTipLabel.computeSize(-1, -1);
/*     */       }
/* 125 */       size.x += this.toolTipShell.getBorderWidth() * 2 + 2;
/* 126 */       size.y += this.toolTipShell.getBorderWidth() * 2;
/*     */       try {
/* 128 */         size.x += this.toolTipShell.getBorderWidth() * 2 + f.marginWidth * 2;
/* 129 */         size.y += this.toolTipShell.getBorderWidth() * 2 + f.marginHeight * 2;
/*     */       }
/*     */       catch (NoSuchFieldError e) {}
/*     */       
/* 133 */       Point pt = this.composite.toDisplay(event.x, event.y);
/*     */       Rectangle displayRect;
/*     */       try {
/* 136 */         displayRect = this.composite.getMonitor().getClientArea();
/*     */       } catch (NoSuchMethodError e) {
/* 138 */         displayRect = this.composite.getDisplay().getClientArea();
/*     */       }
/* 140 */       if (pt.x + size.x > displayRect.x + displayRect.width) {
/* 141 */         pt.x = (displayRect.x + displayRect.width - size.x);
/*     */       }
/*     */       
/* 144 */       if (pt.y + size.y > displayRect.y + displayRect.height) {
/* 145 */         pt.y -= size.y + 2;
/*     */       } else {
/* 147 */         pt.y += 21;
/*     */       }
/*     */       
/* 150 */       if (pt.y < displayRect.y) {
/* 151 */         pt.y = displayRect.y;
/*     */       }
/* 153 */       this.toolTipShell.setBounds(pt.x, pt.y, size.x, size.y);
/* 154 */       this.toolTipShell.setVisible(true);
/*     */       
/* 156 */       break;
/*     */     
/*     */ 
/*     */     case 12: 
/* 160 */       if ((this.mainShell != null) && (!this.mainShell.isDisposed()))
/* 161 */         this.mainShell.removeListener(27, this);
/* 162 */       if ((this.tv.getComposite() != null) && (!this.tv.getComposite().isDisposed())) {
/* 163 */         this.tv.getComposite().removeListener(27, this);
/*     */       }
/*     */       break;
/*     */     }
/* 167 */     if (this.toolTipShell != null) {
/* 168 */       this.toolTipShell.dispose();
/* 169 */       this.toolTipShell = null;
/* 170 */       this.toolTipLabel = null;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/impl/TableTooltips.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */