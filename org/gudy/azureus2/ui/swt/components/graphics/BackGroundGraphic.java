/*     */ package org.gudy.azureus2.ui.swt.components.graphics;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.eclipse.swt.widgets.MenuItem;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
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
/*     */ public class BackGroundGraphic
/*     */   implements Graphic
/*     */ {
/*     */   protected Canvas drawCanvas;
/*     */   protected Image bufferBackground;
/*     */   protected Color lightGrey;
/*     */   protected Color lightGrey2;
/*     */   protected Color colorWhite;
/*  55 */   protected AEMonitor this_mon = new AEMonitor("BackGroundGraphic");
/*     */   
/*     */ 
/*     */ 
/*     */   private boolean isSIIECSensitive;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void setSIIECSensitive(boolean b)
/*     */   {
/*  66 */     this.isSIIECSensitive = b;
/*     */   }
/*     */   
/*     */   public void initialize(Canvas canvas) {
/*  70 */     this.drawCanvas = canvas;
/*  71 */     this.lightGrey = ColorCache.getColor(canvas.getDisplay(), 250, 250, 250);
/*  72 */     this.lightGrey2 = ColorCache.getColor(canvas.getDisplay(), 233, 233, 233);
/*  73 */     this.colorWhite = ColorCache.getColor(canvas.getDisplay(), 255, 255, 255);
/*     */     
/*  75 */     Menu menu = new Menu(canvas);
/*     */     
/*  77 */     final MenuItem mi_binary = new MenuItem(menu, 32);
/*     */     
/*  79 */     mi_binary.setText(MessageText.getString("label.binary.scale.basis"));
/*     */     
/*  81 */     mi_binary.setSelection(COConfigurationManager.getBooleanParameter("ui.scaled.graphics.binary.based"));
/*     */     
/*  83 */     mi_binary.addListener(13, new Listener() {
/*     */       public void handleEvent(Event e) {
/*  85 */         COConfigurationManager.setParameter("ui.scaled.graphics.binary.based", mi_binary.getSelection());
/*     */       }
/*     */     });
/*     */     
/*  89 */     if (this.isSIIECSensitive)
/*     */     {
/*  91 */       final MenuItem mi_iec = new MenuItem(menu, 32);
/*     */       
/*  93 */       mi_iec.setText(MessageText.getString("ConfigView.section.style.useSIUnits"));
/*     */       
/*  95 */       mi_iec.setSelection(COConfigurationManager.getBooleanParameter("config.style.useSIUnits"));
/*     */       
/*  97 */       mi_iec.addListener(13, new Listener() {
/*     */         public void handleEvent(Event e) {
/*  99 */           COConfigurationManager.setParameter("config.style.useSIUnits", mi_iec.getSelection());
/*     */         }
/*     */       });
/*     */     }
/* 103 */     canvas.setMenu(menu);
/*     */   }
/*     */   
/*     */   public void refresh(boolean force) {}
/*     */   
/*     */   protected void drawBackGround(boolean sizeChanged)
/*     */   {
/* 110 */     if ((this.drawCanvas == null) || (this.drawCanvas.isDisposed())) {
/* 111 */       return;
/*     */     }
/* 113 */     if ((sizeChanged) || (this.bufferBackground == null)) {
/* 114 */       Rectangle bounds = this.drawCanvas.getClientArea();
/* 115 */       if ((bounds.height < 30) || (bounds.width < 100)) {
/* 116 */         return;
/*     */       }
/* 118 */       if ((this.bufferBackground != null) && (!this.bufferBackground.isDisposed())) {
/* 119 */         this.bufferBackground.dispose();
/*     */       }
/* 121 */       if ((bounds.width > 10000) || (bounds.height > 10000)) { return;
/*     */       }
/* 123 */       this.bufferBackground = new Image(this.drawCanvas.getDisplay(), bounds);
/*     */       
/* 125 */       Color[] colors = new Color[4];
/* 126 */       colors[0] = this.colorWhite;
/* 127 */       colors[1] = this.lightGrey;
/* 128 */       colors[2] = this.lightGrey2;
/* 129 */       colors[3] = this.lightGrey;
/* 130 */       GC gcBuffer = new GC(this.bufferBackground);
/* 131 */       for (int i = 0; i < bounds.height - 2; i++) {
/* 132 */         gcBuffer.setForeground(colors[(i % 4)]);
/* 133 */         gcBuffer.drawLine(1, i + 1, bounds.width - 1, i + 1);
/*     */       }
/* 135 */       gcBuffer.setForeground(Colors.black);
/* 136 */       gcBuffer.drawLine(bounds.width - 70, 0, bounds.width - 70, bounds.height - 1);
/*     */       
/* 138 */       gcBuffer.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);
/* 139 */       gcBuffer.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 144 */     if ((this.bufferBackground != null) && (!this.bufferBackground.isDisposed()))
/* 145 */       this.bufferBackground.dispose();
/*     */   }
/*     */   
/*     */   public void setColors(Color color1, Color color2, Color color3) {
/* 149 */     this.colorWhite = color1;
/* 150 */     this.lightGrey = color2;
/* 151 */     this.lightGrey2 = color3;
/* 152 */     this.drawCanvas.redraw();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/graphics/BackGroundGraphic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */