/*     */ package org.gudy.azureus2.ui.swt.components.graphics;
/*     */ 
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
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
/*     */ public class PingGraphic
/*     */   extends ScaledGraphic
/*     */   implements ParameterListener
/*     */ {
/*     */   private static final int ENTRIES = 2000;
/*     */   private static final int COLOR_AVERAGE = 0;
/*  44 */   public static Color[] defaultColors = { Colors.grey, Colors.blues[7], Colors.fadedGreen, Colors.fadedRed };
/*     */   
/*     */   private int internalLoop;
/*     */   
/*     */   private int graphicsUpdate;
/*     */   
/*     */   private Point oldSize;
/*     */   
/*     */   protected Image bufferImage;
/*  53 */   private int nbValues = 0;
/*     */   
/*  55 */   private int[][] all_values = new int[1]['ߐ'];
/*     */   
/*     */   private int currentPosition;
/*     */   
/*     */   private boolean externalAverage;
/*  60 */   private Color[] colors = defaultColors;
/*     */   
/*     */   private PingGraphic(Scale scale, ValueFormater formater) {
/*  63 */     super(scale, formater);
/*     */     
/*  65 */     this.currentPosition = 0;
/*     */     
/*  67 */     COConfigurationManager.addParameterListener("Graphics Update", this);
/*  68 */     parameterChanged("Graphics Update");
/*     */   }
/*     */   
/*     */   public static PingGraphic getInstance() {
/*  72 */     new PingGraphic(new Scale(false), new ValueFormater() {
/*     */       public String format(int value) {
/*  74 */         return value + " ms";
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setColors(Color[] _colors)
/*     */   {
/*  83 */     this.colors = _colors;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setExternalAverage(boolean b)
/*     */   {
/*  90 */     this.externalAverage = b;
/*     */   }
/*     */   
/*     */   public void addIntsValue(int[] new_values) {
/*     */     try {
/*  95 */       this.this_mon.enter();
/*     */       
/*  97 */       if (this.all_values.length < new_values.length)
/*     */       {
/*  99 */         int[][] new_all_values = new int[new_values.length][];
/*     */         
/* 101 */         System.arraycopy(this.all_values, 0, new_all_values, 0, this.all_values.length);
/*     */         
/* 103 */         for (int i = this.all_values.length; i < new_all_values.length; i++)
/*     */         {
/* 105 */           new_all_values[i] = new int['ߐ'];
/*     */         }
/*     */         
/* 108 */         this.all_values = new_all_values;
/*     */       }
/*     */       
/* 111 */       for (int i = 0; i < new_values.length; i++)
/*     */       {
/* 113 */         this.all_values[i][this.currentPosition] = new_values[i];
/*     */       }
/*     */       
/* 116 */       this.currentPosition += 1;
/*     */       
/* 118 */       if (this.nbValues < 2000)
/*     */       {
/* 120 */         this.nbValues += 1;
/*     */       }
/*     */       
/* 123 */       if (this.currentPosition >= 2000)
/*     */       {
/* 125 */         this.currentPosition = 0;
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/* 130 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void refresh(boolean force)
/*     */   {
/* 137 */     refresh();
/*     */   }
/*     */   
/*     */   public void refresh() {
/* 141 */     if ((this.drawCanvas == null) || (this.drawCanvas.isDisposed())) {
/* 142 */       return;
/*     */     }
/*     */     
/* 145 */     Rectangle bounds = this.drawCanvas.getClientArea();
/* 146 */     if ((bounds.height < 30) || (bounds.width < 100) || (bounds.width > 2000) || (bounds.height > 2000)) {
/* 147 */       return;
/*     */     }
/* 149 */     boolean sizeChanged = (this.oldSize == null) || (this.oldSize.x != bounds.width) || (this.oldSize.y != bounds.height);
/* 150 */     this.oldSize = new Point(bounds.width, bounds.height);
/*     */     
/* 152 */     this.internalLoop += 1;
/* 153 */     if (this.internalLoop > this.graphicsUpdate) {
/* 154 */       this.internalLoop = 0;
/*     */     }
/*     */     
/* 157 */     if ((this.internalLoop == 0) || (sizeChanged)) {
/* 158 */       drawChart(sizeChanged);
/*     */     }
/*     */     
/* 161 */     if ((this.bufferImage != null) && (!this.bufferImage.isDisposed())) {
/* 162 */       GC gc = new GC(this.drawCanvas);
/* 163 */       gc.drawImage(this.bufferImage, bounds.x, bounds.y);
/* 164 */       gc.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */   protected void drawChart(boolean sizeChanged) {
/*     */     try {
/* 170 */       this.this_mon.enter();
/*     */       
/*     */ 
/* 173 */       drawScale(sizeChanged);
/*     */       
/* 175 */       if ((this.bufferScale == null) || (this.bufferScale.isDisposed())) {
/*     */         return;
/*     */       }
/*     */       
/* 179 */       Rectangle bounds = this.drawCanvas.getClientArea();
/*     */       
/*     */ 
/* 182 */       if ((this.bufferImage != null) && (!this.bufferImage.isDisposed())) {
/* 183 */         this.bufferImage.dispose();
/*     */       }
/* 185 */       this.bufferImage = new Image(this.drawCanvas.getDisplay(), bounds);
/*     */       
/* 187 */       GC gcImage = new GC(this.bufferImage);
/*     */       
/* 189 */       gcImage.drawImage(this.bufferScale, 0, 0);
/*     */       
/* 191 */       gcImage.setAntialias(1);
/*     */       
/* 193 */       int oldAverage = 0;
/* 194 */       int[] oldTargetValues = new int[this.all_values.length];
/* 195 */       int[] maxs = new int[this.all_values.length];
/* 196 */       for (int x = 0; x < bounds.width - 71; x++) {
/* 197 */         int position = this.currentPosition - x - 1;
/* 198 */         if (position < 0)
/* 199 */           position += 2000;
/* 200 */         for (int z = 0; z < this.all_values.length; z++) {
/* 201 */           int value = this.all_values[z][position];
/* 202 */           if (value > maxs[z]) {
/* 203 */             maxs[z] = value;
/*     */           }
/*     */         }
/*     */       }
/* 207 */       int max = 0;
/* 208 */       for (int i = 0; i < maxs.length; i++) {
/* 209 */         if (maxs[i] > max) {
/* 210 */           max = maxs[i];
/*     */         }
/*     */       }
/*     */       
/* 214 */       this.scale.setMax(max);
/*     */       
/* 216 */       for (int x = 0; x < bounds.width - 71; x++) {
/* 217 */         int position = this.currentPosition - x - 1;
/* 218 */         if (position < 0) {
/* 219 */           position += 2000;
/*     */         }
/* 221 */         int xDraw = bounds.width - 71 - x;
/* 222 */         gcImage.setLineWidth(1);
/* 223 */         for (int z = this.externalAverage ? 1 : 0; z < this.all_values.length; z++) {
/* 224 */           int targetValue = this.all_values[z][position];
/* 225 */           int oldTargetValue = oldTargetValues[z];
/*     */           
/* 227 */           if (x > 1) {
/* 228 */             int h1 = bounds.height - this.scale.getScaledValue(targetValue) - 2;
/* 229 */             int h2 = bounds.height - this.scale.getScaledValue(oldTargetValue) - 2;
/* 230 */             gcImage.setForeground(z <= 2 ? this.colors[(z + 1)] : this.externalAverage ? this.colors[z] : this.colors[3]);
/* 231 */             gcImage.drawLine(xDraw, h1, xDraw + 1, h2);
/*     */           }
/*     */           
/* 234 */           oldTargetValues[z] = this.all_values[z][position];
/*     */         }
/*     */         
/* 237 */         int average = computeAverage(position);
/* 238 */         if (x > 6) {
/* 239 */           int h1 = bounds.height - this.scale.getScaledValue(average) - 2;
/* 240 */           int h2 = bounds.height - this.scale.getScaledValue(oldAverage) - 2;
/* 241 */           gcImage.setForeground(this.colors[0]);
/* 242 */           gcImage.setLineWidth(2);
/* 243 */           gcImage.drawLine(xDraw, h1, xDraw + 1, h2);
/*     */         }
/* 245 */         oldAverage = average;
/*     */       }
/*     */       
/* 248 */       if (this.nbValues > 0) {
/* 249 */         int height = bounds.height - this.scale.getScaledValue(computeAverage(this.currentPosition - 6)) - 2;
/* 250 */         gcImage.setForeground(this.colors[0]);
/* 251 */         gcImage.drawText(this.formater.format(computeAverage(this.currentPosition - 6)), bounds.width - 65, height - 12, true);
/*     */       }
/*     */       
/* 254 */       gcImage.dispose();
/*     */     }
/*     */     finally
/*     */     {
/* 258 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   protected int computeAverage(int position) {
/* 263 */     int sum = 0;
/* 264 */     int nbItems = 0;
/* 265 */     for (int i = -5; i < 6; i++) {
/* 266 */       int pos = position + i;
/* 267 */       if (pos < 0)
/* 268 */         pos += 2000;
/* 269 */       if (pos >= 2000)
/* 270 */         pos -= 2000;
/* 271 */       for (int z = 0; z < (this.externalAverage ? 1 : this.all_values.length); z++) {
/* 272 */         sum += this.all_values[z][pos];
/* 273 */         nbItems++;
/*     */       }
/*     */     }
/* 276 */     return sum / nbItems;
/*     */   }
/*     */   
/*     */   public void parameterChanged(String parameter)
/*     */   {
/* 281 */     this.graphicsUpdate = COConfigurationManager.getIntParameter("Graphics Update");
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 285 */     super.dispose();
/* 286 */     if ((this.bufferImage != null) && (!this.bufferImage.isDisposed())) {
/* 287 */       this.bufferImage.dispose();
/*     */     }
/* 289 */     COConfigurationManager.removeParameterListener("Graphics Update", this);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/graphics/PingGraphic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */