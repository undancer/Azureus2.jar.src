/*     */ package org.gudy.azureus2.ui.swt.components.graphics;
/*     */ 
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.FontMetrics;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.Colors;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.HSLColor;
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
/*     */ public class Plot3D
/*     */   implements Graphic, ParameterListener
/*     */ {
/*  43 */   private int Z_MAX = Integer.MAX_VALUE;
/*     */   
/*     */   private Canvas canvas;
/*     */   
/*  47 */   private String title = "";
/*     */   
/*     */   private String[] labels;
/*     */   
/*     */   private ValueFormater[] formatters;
/*     */   
/*     */   private int internalLoop;
/*     */   private int graphicsUpdate;
/*     */   private Point oldSize;
/*     */   protected Image bufferImage;
/*  57 */   protected AEMonitor this_mon = new AEMonitor("Plot3D");
/*     */   
/*  59 */   private int[][] values = new int[0][];
/*     */   
/*     */ 
/*     */   private Color[] colours;
/*     */   
/*     */ 
/*     */ 
/*     */   public Plot3D(String[] _labels, ValueFormater[] _formatters)
/*     */   {
/*  68 */     this.labels = _labels;
/*  69 */     this.formatters = _formatters;
/*     */     
/*  71 */     COConfigurationManager.addAndFireParameterListener("Graphics Update", this);
/*     */     
/*  73 */     parameterChanged("Graphics Update");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void initialize(Canvas _canvas)
/*     */   {
/*  80 */     this.canvas = _canvas;
/*     */     
/*  82 */     Device device = this.canvas.getDisplay();
/*     */     
/*  84 */     this.colours = new Color[16];
/*     */     
/*  86 */     HSLColor hsl = new HSLColor();
/*     */     
/*  88 */     hsl.initHSLbyRGB(130, 240, 240);
/*     */     
/*  90 */     int step = 128 / this.colours.length;
/*     */     
/*  92 */     int hue = this.colours.length * step;
/*     */     
/*  94 */     for (int i = 0; i < this.colours.length; i++)
/*     */     {
/*  96 */       hsl.setHue(hue);
/*     */       
/*  98 */       hue -= step;
/*     */       
/* 100 */       this.colours[i] = new Color(device, hsl.getRed(), hsl.getGreen(), hsl.getBlue());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setTitle(String str)
/*     */   {
/* 108 */     this.title = str;
/*     */   }
/*     */   
/*     */ 
/*     */   public Color[] getColours()
/*     */   {
/* 114 */     return this.colours;
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh(boolean force)
/*     */   {
/* 120 */     if ((this.canvas == null) || (this.canvas.isDisposed()))
/*     */     {
/* 122 */       return;
/*     */     }
/*     */     
/* 125 */     Rectangle bounds = this.canvas.getClientArea();
/*     */     
/* 127 */     if ((bounds.height < 30) || (bounds.width < 100) || (bounds.width > 2000) || (bounds.height > 2000)) {
/* 128 */       return;
/*     */     }
/* 130 */     boolean sizeChanged = (this.oldSize == null) || (this.oldSize.x != bounds.width) || (this.oldSize.y != bounds.height);
/*     */     
/* 132 */     this.oldSize = new Point(bounds.width, bounds.height);
/*     */     
/* 134 */     this.internalLoop += 1;
/*     */     
/* 136 */     if (this.internalLoop > this.graphicsUpdate)
/*     */     {
/* 138 */       this.internalLoop = 0;
/*     */     }
/*     */     
/* 141 */     if ((this.internalLoop == 0) || (sizeChanged) || (force))
/*     */     {
/* 143 */       drawPlot();
/*     */     }
/*     */     
/* 146 */     GC gc = new GC(this.canvas);
/*     */     
/* 148 */     gc.drawImage(this.bufferImage, bounds.x, bounds.y);
/*     */     
/* 150 */     gc.dispose();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void drawPlot()
/*     */   {
/* 157 */     int PAD_TOP = 10;
/* 158 */     int PAD_BOTTOM = 10;
/* 159 */     int PAD_RIGHT = 10;
/* 160 */     int PAD_LEFT = 10;
/*     */     
/* 162 */     double ANGLE_RADS = 0.7D;
/*     */     
/* 164 */     double ANGLE_TAN = Math.tan(0.7D);
/*     */     try
/*     */     {
/* 167 */       this.this_mon.enter();
/*     */       
/* 169 */       Rectangle bounds = this.canvas.getClientArea();
/*     */       
/* 171 */       if ((this.bufferImage != null) && (!this.bufferImage.isDisposed()))
/*     */       {
/* 173 */         this.bufferImage.dispose();
/*     */       }
/*     */       
/* 176 */       this.bufferImage = new Image(this.canvas.getDisplay(), bounds);
/*     */       
/* 178 */       GC image = new GC(this.bufferImage);
/*     */       
/* 180 */       int max_x = 0;
/* 181 */       int max_y = 0;
/* 182 */       int max_z = 0;
/*     */       
/* 184 */       for (int i = 0; i < this.values.length; i++)
/*     */       {
/* 186 */         int[] entry = (int[])this.values[i];
/*     */         
/* 188 */         if (entry[0] > max_x)
/*     */         {
/* 190 */           max_x = entry[0];
/*     */         }
/* 192 */         if (entry[1] > max_y)
/*     */         {
/* 194 */           max_y = entry[1];
/*     */         }
/* 196 */         if (entry[2] > max_z)
/*     */         {
/* 198 */           max_z = entry[2];
/*     */         }
/*     */       }
/*     */       
/* 202 */       max_z = Math.min(max_z, this.Z_MAX);
/*     */       
/* 204 */       int usable_width = bounds.width - 10 - 10;
/* 205 */       int usable_height = bounds.height - 10 - 10;
/*     */       try
/*     */       {
/* 208 */         image.setAntialias(1);
/*     */       }
/*     */       catch (Exception e) {}
/*     */       
/*     */ 
/* 213 */       double x_ratio = (usable_width - usable_height / 2.0F / ANGLE_TAN) / max_x;
/* 214 */       double y_ratio = usable_height / 2.0F / max_y;
/* 215 */       double z_ratio = usable_height / 2.0F / max_z;
/*     */       
/*     */ 
/*     */ 
/* 219 */       int x_axis_left_x = 10;
/* 220 */       int x_axis_left_y = usable_height + 10;
/* 221 */       int x_axis_right_x = 10 + usable_width;
/* 222 */       int x_axis_right_y = usable_height + 10;
/*     */       
/*     */ 
/* 225 */       int y_axis_left_x = 10;
/* 226 */       int y_axis_left_y = usable_height + 10;
/* 227 */       int y_axis_right_x = 10 + (int)(usable_height / 2.0F / ANGLE_TAN);
/* 228 */       int y_axis_right_y = usable_height / 2;
/*     */       
/* 230 */       int z_axis_bottom_x = 10;
/* 231 */       int z_axis_bottom_y = usable_height + 10;
/* 232 */       int z_axis_top_x = 10;
/* 233 */       int z_axis_top_y = 10 + usable_height / 2;
/*     */       
/* 235 */       Rectangle old_clip = image.getClipping();
/*     */       
/* 237 */       Utils.setClipping(image, new Rectangle(10, 10, usable_width, usable_height));
/*     */       
/* 239 */       image.setForeground(Colors.light_grey);
/*     */       
/* 241 */       int x_lines = 10;
/*     */       
/* 243 */       for (int i = 1; i < x_lines; i++)
/*     */       {
/* 245 */         int x1 = x_axis_left_x + (y_axis_right_x - y_axis_left_x) * i / x_lines;
/* 246 */         int y1 = x_axis_left_y - (y_axis_left_y - y_axis_right_y) * i / x_lines;
/*     */         
/* 248 */         int x2 = x_axis_right_x;
/* 249 */         int y2 = y1;
/*     */         
/* 251 */         image.drawLine(x1, y1, x2, y2);
/*     */       }
/*     */       
/* 254 */       int y_lines = 10;
/*     */       
/* 256 */       for (int i = 1; i < y_lines; i++)
/*     */       {
/* 258 */         int x1 = y_axis_left_x + (x_axis_right_x - x_axis_left_x) * i / x_lines;
/* 259 */         int y1 = y_axis_left_y;
/*     */         
/* 261 */         int x2 = y_axis_right_x + (x_axis_right_x - x_axis_left_x) * i / x_lines;
/* 262 */         int y2 = y_axis_right_y;
/*     */         
/* 264 */         image.drawLine(x1, y1, x2, y2);
/*     */       }
/*     */       
/* 267 */       Utils.setClipping(image, old_clip);
/*     */       
/* 269 */       int z_lines = 10;
/*     */       
/* 271 */       for (int i = 1; i < z_lines; i++)
/*     */       {
/* 273 */         int z = z_axis_bottom_y + (z_axis_top_y - z_axis_bottom_y) * i / z_lines;
/*     */         
/* 275 */         image.drawLine(z_axis_bottom_x, z, z_axis_bottom_x - 4, z);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 280 */       for (int i = 0; i < this.values.length; i++)
/*     */       {
/* 282 */         int[] entry = (int[])this.values[i];
/*     */         
/* 284 */         int z = Math.min(entry[2], this.Z_MAX);
/*     */         
/* 286 */         int draw_x = (int)(x_ratio * entry[0]);
/* 287 */         int draw_y = (int)(y_ratio * entry[1]);
/* 288 */         int draw_z = (int)(z_ratio * z);
/*     */         
/* 290 */         draw_x = (int)(draw_x + draw_y / ANGLE_TAN);
/*     */         
/* 292 */         image.setForeground(this.colours[((int)(z / max_z * (this.colours.length - 1)))]);
/*     */         
/* 294 */         image.drawLine(10 + draw_x, 10 + usable_height - draw_y, 10 + draw_x, 10 + usable_height - (draw_y + draw_z));
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 301 */       image.setForeground(Colors.black);
/*     */       
/* 303 */       image.drawRectangle(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
/*     */       
/* 305 */       int font_height = image.getFontMetrics().getHeight();
/* 306 */       int char_width = image.getFontMetrics().getAverageCharWidth();
/*     */       
/*     */ 
/*     */ 
/* 310 */       image.drawLine(x_axis_left_x, x_axis_left_y, x_axis_right_x, x_axis_right_y);
/* 311 */       image.drawLine(usable_width, x_axis_right_y - 4, x_axis_right_x, x_axis_right_y);
/* 312 */       image.drawLine(usable_width, x_axis_right_y + 4, x_axis_right_x, x_axis_right_y);
/*     */       
/* 314 */       String x_text = this.labels[0] + " - " + this.formatters[0].format(max_x);
/*     */       
/* 316 */       image.drawText(x_text, x_axis_right_x - 20 - x_text.length() * char_width, x_axis_right_y - font_height - 2, 1);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 324 */       String z_text = this.labels[2] + " - " + this.formatters[2].format(max_z);
/*     */       
/* 326 */       image.drawText(z_text, z_axis_top_x + 4, z_axis_top_y + 10, 1);
/*     */       
/* 328 */       image.drawLine(z_axis_bottom_x, z_axis_bottom_y, z_axis_top_x, z_axis_top_y);
/* 329 */       image.drawLine(z_axis_top_x - 4, z_axis_top_y + 10, z_axis_top_x, z_axis_top_y);
/* 330 */       image.drawLine(z_axis_top_x + 4, z_axis_top_y + 10, z_axis_top_x, z_axis_top_y);
/*     */       
/*     */ 
/*     */ 
/* 334 */       image.drawLine(y_axis_left_x, y_axis_left_y, y_axis_right_x, y_axis_right_y);
/* 335 */       image.drawLine(y_axis_right_x - 6, y_axis_right_y, y_axis_right_x, y_axis_right_y);
/* 336 */       image.drawLine(y_axis_right_x, y_axis_right_y + 6, y_axis_right_x, y_axis_right_y);
/*     */       
/* 338 */       String y_text = this.labels[1] + " - " + this.formatters[1].format(max_y);
/*     */       
/* 340 */       image.drawText(y_text, y_axis_right_x - y_text.length() * char_width, y_axis_right_y - font_height - 2, 1);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 347 */       image.drawText(this.title, (bounds.width - this.title.length() * char_width) / 2, 1, 1);
/*     */       
/* 349 */       image.dispose();
/*     */     }
/*     */     finally
/*     */     {
/* 353 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setMaxZ(int m)
/*     */   {
/* 361 */     this.Z_MAX = m;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void parameterChanged(String parameter)
/*     */   {
/* 368 */     this.graphicsUpdate = COConfigurationManager.getIntParameter("Graphics Update");
/*     */   }
/*     */   
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 374 */     if ((this.bufferImage != null) && (!this.bufferImage.isDisposed()))
/*     */     {
/* 376 */       this.bufferImage.dispose();
/*     */     }
/*     */     
/* 379 */     if (this.colours != null)
/*     */     {
/* 381 */       for (int i = 0; i < this.colours.length; i++)
/*     */       {
/* 383 */         this.colours[i].dispose();
/*     */       }
/*     */     }
/*     */     
/* 387 */     COConfigurationManager.removeParameterListener("Graphics Update", this);
/*     */   }
/*     */   
/*     */ 
/*     */   public void update(int[][] _values)
/*     */   {
/*     */     try
/*     */     {
/* 395 */       this.this_mon.enter();
/*     */       
/* 397 */       this.values = _values;
/*     */     }
/*     */     finally
/*     */     {
/* 401 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/graphics/Plot3D.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */