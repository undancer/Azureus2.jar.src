/*     */ package org.gudy.azureus2.ui.swt.components.graphics;
/*     */ 
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Path;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.graphics.Region;
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
/*     */ public class PieUtils
/*     */ {
/*     */   public static void drawPie(GC gc, int x, int y, int width, int height, int percent)
/*     */   {
/*  39 */     Color background = gc.getBackground();
/*  40 */     gc.setForeground(Colors.blue);
/*  41 */     int angle = percent * 360 / 100;
/*  42 */     if (angle < 4)
/*  43 */       angle = 0;
/*  44 */     gc.setBackground(Colors.white);
/*  45 */     gc.fillArc(x, y, width, height, 0, 360);
/*  46 */     gc.setBackground(background);
/*  47 */     gc.fillArc(x, y, width, height, 90, angle * -1);
/*  48 */     gc.drawOval(x, y, width - 1, height - 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void drawPie(GC gc, Image image, int x, int y, int width, int height, int percent, boolean draw_border)
/*     */   {
/*  55 */     Rectangle image_size = image.getBounds();
/*     */     
/*  57 */     int width_pad = (width - image_size.width) / 2;
/*  58 */     int height_pad = (height - image_size.height) / 2;
/*     */     
/*  60 */     int angle = percent * 360 / 100;
/*  61 */     if (angle < 4) {
/*  62 */       angle = 0;
/*     */     }
/*     */     
/*  65 */     Region old_clipping = new Region();
/*     */     
/*  67 */     gc.getClipping(old_clipping);
/*     */     
/*  69 */     Path path_done = new Path(gc.getDevice());
/*     */     
/*  71 */     path_done.addArc(x, y, width, height, 90.0F, -angle);
/*  72 */     path_done.lineTo(x + width / 2, y + height / 2);
/*  73 */     path_done.close();
/*     */     
/*  75 */     gc.setClipping(path_done);
/*     */     
/*  77 */     gc.drawImage(image, x + width_pad, y + height_pad + 1);
/*     */     
/*  79 */     Path path_undone = new Path(gc.getDevice());
/*     */     
/*  81 */     path_undone.addArc(x, y, width, height, 90 - angle, angle - 360);
/*  82 */     path_undone.lineTo(x + width / 2, y + height / 2);
/*  83 */     path_undone.close();
/*     */     
/*  85 */     gc.setClipping(path_undone);
/*     */     
/*  87 */     gc.setAlpha(75);
/*  88 */     gc.drawImage(image, x + width_pad, y + height_pad + 1);
/*  89 */     gc.setAlpha(255);
/*     */     
/*  91 */     gc.setClipping(old_clipping);
/*     */     
/*  93 */     if (draw_border)
/*     */     {
/*  95 */       gc.setForeground(Colors.blue);
/*     */       
/*  97 */       if (percent == 100)
/*     */       {
/*  99 */         gc.drawOval(x, y, width - 1, height - 1);
/*     */ 
/*     */ 
/*     */       }
/* 103 */       else if (angle > 0)
/*     */       {
/* 105 */         gc.drawPath(path_done);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 110 */     path_done.dispose();
/* 111 */     path_undone.dispose();
/* 112 */     old_clipping.dispose();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/graphics/PieUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */