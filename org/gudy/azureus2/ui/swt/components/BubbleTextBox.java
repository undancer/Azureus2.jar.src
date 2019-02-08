/*     */ package org.gudy.azureus2.ui.swt.components;
/*     */ 
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.eclipse.swt.widgets.Widget;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BubbleTextBox
/*     */ {
/*     */   private Text textWidget;
/*     */   private Composite cBubble;
/*     */   private static final int PADDING_VERTICAL = 2;
/*     */   private int WIDTH_OVAL;
/*     */   private int HEIGHT_OVAL;
/*     */   private int INDENT_OVAL;
/*     */   private int HEIGHT_ICON_MAX;
/*     */   private int WIDTH_CLEAR;
/*     */   private int WIDTH_PADDING;
/*  59 */   private String text = "";
/*     */   
/*     */   public BubbleTextBox(Composite parent, int style) {
/*  62 */     this.cBubble = new Composite(parent, 536870912);
/*  63 */     this.cBubble.setLayout(new FormLayout());
/*     */     
/*  65 */     this.textWidget = new Text(this.cBubble, style & 0xF77F);
/*     */     
/*  67 */     FormData fd = new FormData();
/*  68 */     fd.top = new FormAttachment(0, 2);
/*  69 */     fd.bottom = new FormAttachment(100, -2);
/*  70 */     fd.left = new FormAttachment(0, 17);
/*  71 */     fd.right = new FormAttachment(100, -15);
/*  72 */     Utils.setLayoutData(this.textWidget, fd);
/*     */     
/*  74 */     this.WIDTH_OVAL = Utils.adjustPXForDPI(7);
/*  75 */     this.HEIGHT_OVAL = Utils.adjustPXForDPI(6);
/*  76 */     this.INDENT_OVAL = Utils.adjustPXForDPI(6);
/*  77 */     this.HEIGHT_ICON_MAX = Utils.adjustPXForDPI(12);
/*  78 */     this.WIDTH_CLEAR = Utils.adjustPXForDPI(7);
/*  79 */     this.WIDTH_PADDING = Utils.adjustPXForDPI(6);
/*     */     
/*  81 */     this.cBubble.addPaintListener(new PaintListener() {
/*     */       public void paintControl(PaintEvent e) {
/*  83 */         Rectangle clientArea = BubbleTextBox.this.cBubble.getClientArea();
/*  84 */         if (Utils.isGTK) {
/*  85 */           e.gc.setBackground(e.display.getSystemColor(25));
/*     */         } else {
/*  87 */           e.gc.setBackground(BubbleTextBox.this.textWidget.getBackground());
/*     */         }
/*  89 */         e.gc.setAdvanced(true);
/*  90 */         e.gc.setAntialias(1);
/*  91 */         e.gc.fillRoundRectangle(clientArea.x, clientArea.y, clientArea.width - 1, clientArea.height - 1, clientArea.height, clientArea.height);
/*     */         
/*     */ 
/*  94 */         e.gc.setAlpha(127);
/*  95 */         e.gc.drawRoundRectangle(clientArea.x, clientArea.y, clientArea.width - 1, clientArea.height - 1, clientArea.height, clientArea.height);
/*     */         
/*     */ 
/*     */ 
/*  99 */         e.gc.setAlpha(255);
/* 100 */         e.gc.setLineCap(1);
/*     */         
/* 102 */         int iconHeight = clientArea.height - Utils.adjustPXForDPI(9);
/* 103 */         if (iconHeight > BubbleTextBox.this.HEIGHT_ICON_MAX) {
/* 104 */           iconHeight = BubbleTextBox.this.HEIGHT_ICON_MAX;
/*     */         }
/* 106 */         int iconY = clientArea.y + (clientArea.height - iconHeight + 1) / 2;
/*     */         
/* 108 */         Color colorClearX = e.display.getSystemColor(18);
/*     */         
/* 110 */         e.gc.setForeground(colorClearX);
/*     */         
/* 112 */         e.gc.setLineWidth(Utils.adjustPXForDPI(2));
/* 113 */         e.gc.drawOval(clientArea.x + BubbleTextBox.this.INDENT_OVAL, iconY, BubbleTextBox.this.WIDTH_OVAL, BubbleTextBox.this.HEIGHT_OVAL);
/*     */         
/* 115 */         e.gc.drawPolyline(new int[] { clientArea.x + BubbleTextBox.this.INDENT_OVAL + BubbleTextBox.this.INDENT_OVAL, iconY + BubbleTextBox.this.INDENT_OVAL, clientArea.x + (int)(BubbleTextBox.this.INDENT_OVAL * 2.6D), iconY + iconHeight });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 122 */         boolean textIsBlank = BubbleTextBox.this.textWidget.getText().length() == 0;
/* 123 */         if (!textIsBlank) {
/* 124 */           int YADJ = (clientArea.height - (BubbleTextBox.this.WIDTH_CLEAR + BubbleTextBox.this.WIDTH_PADDING + BubbleTextBox.this.WIDTH_PADDING)) / 2;
/*     */           
/* 126 */           e.gc.setLineCap(2);
/*     */           
/* 128 */           Rectangle rXArea = new Rectangle(clientArea.x + clientArea.width - (BubbleTextBox.this.WIDTH_CLEAR + BubbleTextBox.this.WIDTH_PADDING), clientArea.y + BubbleTextBox.this.WIDTH_PADDING / 2, BubbleTextBox.this.WIDTH_CLEAR + BubbleTextBox.this.WIDTH_PADDING / 2, clientArea.height - BubbleTextBox.this.WIDTH_PADDING);
/*     */           
/*     */ 
/*     */ 
/* 132 */           BubbleTextBox.this.cBubble.setData("XArea", rXArea);
/*     */           
/* 134 */           e.gc.drawPolyline(new int[] { clientArea.x + clientArea.width - BubbleTextBox.this.WIDTH_PADDING, clientArea.y + BubbleTextBox.this.WIDTH_PADDING + YADJ, clientArea.x + clientArea.width - (BubbleTextBox.this.WIDTH_PADDING + BubbleTextBox.this.WIDTH_CLEAR), clientArea.y + BubbleTextBox.this.WIDTH_PADDING + BubbleTextBox.this.WIDTH_CLEAR + YADJ });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 140 */           e.gc.drawPolyline(new int[] { clientArea.x + clientArea.width - BubbleTextBox.this.WIDTH_PADDING, clientArea.y + BubbleTextBox.this.WIDTH_PADDING + BubbleTextBox.this.WIDTH_CLEAR + YADJ, clientArea.x + clientArea.width - (BubbleTextBox.this.WIDTH_PADDING + BubbleTextBox.this.WIDTH_CLEAR), clientArea.y + BubbleTextBox.this.WIDTH_PADDING + YADJ });
/*     */ 
/*     */         }
/*     */         
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 149 */     });
/* 150 */     this.cBubble.addListener(3, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 152 */         Rectangle r = (Rectangle)event.widget.getData("XArea");
/* 153 */         if ((r != null) && (r.contains(event.x, event.y))) {
/* 154 */           BubbleTextBox.this.textWidget.setText("");
/*     */         }
/*     */         
/*     */       }
/*     */       
/*     */ 
/* 160 */     });
/* 161 */     this.textWidget.addPaintListener(new PaintListener() {
/*     */       private Color existing_bg;
/*     */       
/*     */       public void paintControl(PaintEvent arg0) {
/* 165 */         Color current_bg = BubbleTextBox.this.textWidget.getBackground();
/*     */         
/* 167 */         if (!current_bg.equals(this.existing_bg))
/*     */         {
/* 169 */           this.existing_bg = current_bg;
/*     */           
/* 171 */           BubbleTextBox.this.cBubble.redraw();
/*     */         }
/*     */         
/*     */       }
/* 175 */     });
/* 176 */     this.textWidget.addModifyListener(new ModifyListener() {
/*     */       public void modifyText(ModifyEvent e) {
/* 178 */         boolean textWasBlank = BubbleTextBox.this.text.length() == 0;
/* 179 */         BubbleTextBox.this.text = BubbleTextBox.this.textWidget.getText();
/* 180 */         boolean textIsBlank = BubbleTextBox.this.text.length() == 0;
/* 181 */         if ((textWasBlank != textIsBlank) && (BubbleTextBox.this.cBubble != null)) {
/* 182 */           BubbleTextBox.this.cBubble.redraw();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public Composite getParent()
/*     */   {
/* 190 */     return this.cBubble;
/*     */   }
/*     */   
/*     */   public Text getTextWidget() {
/* 194 */     return this.textWidget;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/BubbleTextBox.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */