/*     */ package org.gudy.azureus2.ui.swt.components;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.utils.ColorCache;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseTrackListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.RGB;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.layout.RowData;
/*     */ import org.eclipse.swt.layout.RowLayout;
/*     */ import org.eclipse.swt.widgets.Canvas;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.gudy.azureus2.core3.config.ParameterListener;
/*     */ import org.gudy.azureus2.core3.config.impl.ConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
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
/*     */ public class Legend
/*     */ {
/*     */   public static Composite createLegendComposite(Composite panel, Color[] blockColors, String[] keys)
/*     */   {
/*  63 */     return createLegendComposite(panel, blockColors, keys, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Composite createLegendComposite(Composite panel, Color[] blockColors, String[] keys, boolean horizontal)
/*     */   {
/*  73 */     Object layout = panel.getLayout();
/*  74 */     Object layoutData = null;
/*  75 */     if ((layout instanceof GridLayout)) {
/*  76 */       layoutData = new GridData(768);
/*     */     }
/*  78 */     return createLegendComposite(panel, blockColors, keys, null, layoutData, horizontal);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static Composite createLegendComposite(Composite panel, Color[] blockColors, String[] keys, Object layoutData)
/*     */   {
/*  89 */     return createLegendComposite(panel, blockColors, keys, null, layoutData, true);
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
/*     */ 
/*     */   public static Composite createLegendComposite(Composite panel, Color[] blockColors, String[] keys, String[] key_texts, Object layoutData, boolean horizontal)
/*     */   {
/* 110 */     return createLegendComposite(panel, blockColors, keys, key_texts, layoutData, horizontal, null);
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
/*     */   public static Composite createLegendComposite(final Composite panel, final Color[] blockColors, final String[] keys, String[] key_texts, Object layoutData, boolean horizontal, final LegendListener listener)
/*     */   {
/* 123 */     final ConfigurationManager config = ConfigurationManager.getInstance();
/*     */     
/* 125 */     if (blockColors.length != keys.length) {
/* 126 */       return null;
/*     */     }
/* 128 */     final Color[] defaultColors = new Color[blockColors.length];
/* 129 */     final ParameterListener[] paramListeners = new ParameterListener[keys.length];
/* 130 */     System.arraycopy(blockColors, 0, defaultColors, 0, blockColors.length);
/*     */     
/* 132 */     Composite legend = new Composite(panel, 0);
/* 133 */     if (layoutData != null) {
/* 134 */       legend.setLayoutData(layoutData);
/*     */     }
/* 136 */     RowLayout layout = new RowLayout(horizontal ? 256 : 512);
/* 137 */     layout.wrap = true;
/* 138 */     layout.marginBottom = 0;
/* 139 */     layout.marginTop = 0;
/* 140 */     layout.marginLeft = 0;
/* 141 */     layout.marginRight = 0;
/* 142 */     layout.spacing = 0;
/* 143 */     legend.setLayout(layout);
/*     */     
/*     */ 
/*     */ 
/* 147 */     final int[] hover_state = { -1, 0 };
/*     */     
/* 149 */     for (int i = 0; i < blockColors.length; i++) {
/* 150 */       int r = config.getIntParameter(keys[i] + ".red", -1);
/* 151 */       if (r >= 0) {
/* 152 */         int g = config.getIntParameter(keys[i] + ".green");
/* 153 */         int b = config.getIntParameter(keys[i] + ".blue");
/*     */         
/* 155 */         Color color = ColorCache.getColor(panel.getDisplay(), r, g, b);
/* 156 */         blockColors[i] = color;
/*     */       }
/*     */       
/* 159 */       Composite colorSet = new Composite(legend, 0);
/*     */       
/* 161 */       Utils.setLayout(colorSet, new RowLayout(256));
/*     */       
/* 163 */       final Canvas cColor = new Canvas(colorSet, 2048);
/* 164 */       cColor.setData("Index", new Integer(i));
/*     */       
/* 166 */       Messages.setLanguageTooltip(cColor, "label.click.to.change");
/*     */       
/*     */ 
/*     */ 
/* 170 */       cColor.addPaintListener(new PaintListener() {
/*     */         public void paintControl(PaintEvent e) {
/* 172 */           int i = ((Integer)this.val$cColor.getData("Index")).intValue();
/* 173 */           e.gc.setBackground(blockColors[i]);
/* 174 */           e.gc.fillRectangle(e.x, e.y, e.width, e.height);
/*     */         }
/*     */         
/* 177 */       });
/* 178 */       cColor.addMouseListener(new MouseAdapter() {
/*     */         public void mouseUp(MouseEvent e) {
/* 180 */           Integer iIndex = (Integer)this.val$cColor.getData("Index");
/* 181 */           if (iIndex == null)
/* 182 */             return;
/* 183 */           int index = iIndex.intValue();
/*     */           
/* 185 */           if (e.button == 1)
/*     */           {
/* 187 */             RGB rgb = Utils.showColorDialog(panel, blockColors[index].getRGB());
/*     */             
/* 189 */             if (rgb != null)
/*     */             {
/* 191 */               config.setRGBParameter(keys[index], rgb.red, rgb.green, rgb.blue);
/*     */             }
/*     */           }
/*     */           else {
/* 195 */             config.removeRGBParameter(keys[index]);
/*     */           }
/*     */           
/*     */         }
/* 199 */       });
/* 200 */       final Label lblDesc = new Label(colorSet, 0);
/*     */       
/* 202 */       if (key_texts == null) {
/* 203 */         Messages.setLanguageText(lblDesc, keys[i]);
/*     */       } else {
/* 205 */         lblDesc.setText(key_texts[i]);
/*     */       }
/*     */       
/* 208 */       if (listener != null)
/*     */       {
/* 210 */         Messages.setLanguageTooltip(lblDesc, "label.click.to.showhide");
/*     */         
/* 212 */         final int f_i = i;
/*     */         
/* 214 */         lblDesc.addMouseListener(new MouseAdapter() {
/*     */           public void mouseUp(MouseEvent e) {
/* 216 */             boolean vis = !this.val$config.getBooleanParameter(keys[f_i] + ".vis", true);
/* 217 */             this.val$config.setParameter(keys[f_i] + ".vis", vis);
/* 218 */             listener.visibilityChange(vis, f_i);
/* 219 */             lblDesc.setForeground(vis ? lblDesc.getDisplay().getSystemColor(2) : Colors.grey);
/*     */           }
/*     */           
/* 222 */         });
/* 223 */         boolean vis = config.getBooleanParameter(keys[f_i] + ".vis", true);
/* 224 */         if (!vis) {
/* 225 */           listener.visibilityChange(vis, i);
/* 226 */           lblDesc.setForeground(Colors.grey);
/*     */         }
/*     */       }
/*     */       
/* 230 */       RowData data = new RowData();
/* 231 */       data.width = 20;
/* 232 */       data.height = (lblDesc.computeSize(-1, -1).y - 3);
/* 233 */       cColor.setLayoutData(data);
/*     */       
/*     */ 
/* 236 */       config.addParameterListener(keys[i], paramListeners[i = new ParameterListener() {
/*     */         public void parameterChanged(String parameterName) {
/* 238 */           for (int j = 0; j < this.val$keys.length; j++) {
/* 239 */             if (this.val$keys[j].equals(parameterName)) {
/* 240 */               final int index = j;
/*     */               
/* 242 */               final int r = config.getIntParameter(this.val$keys[j] + ".red", -1);
/* 243 */               if (r >= 0) {
/* 244 */                 final int g = config.getIntParameter(this.val$keys[j] + ".green");
/* 245 */                 final int b = config.getIntParameter(this.val$keys[j] + ".blue");
/*     */                 
/* 247 */                 Utils.execSWTThread(new AERunnable() {
/*     */                   public void runSupport() {
/* 249 */                     if ((Legend.4.this.val$panel == null) || (Legend.4.this.val$panel.isDisposed()))
/* 250 */                       return;
/* 251 */                     Color color = ColorCache.getColor(Legend.4.this.val$panel.getDisplay(), r, g, b);
/* 252 */                     Legend.4.this.val$blockColors[index] = color;
/* 253 */                     Legend.4.this.val$cColor.redraw();
/*     */                   }
/*     */                 });
/*     */               }
/*     */               else
/*     */               {
/* 259 */                 Utils.execSWTThread(new AERunnable() {
/*     */                   public void runSupport() {
/* 261 */                     if ((Legend.4.this.val$panel == null) || (Legend.4.this.val$panel.isDisposed()))
/* 262 */                       return;
/* 263 */                     Legend.4.this.val$blockColors[index] = Legend.4.this.val$defaultColors[index];
/* 264 */                     Legend.4.this.val$cColor.redraw();
/*     */                   }
/*     */                 });
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       });
/*     */       
/* 273 */       if (listener != null)
/*     */       {
/* 275 */         final int f_i = i;
/*     */         
/* 277 */         Control[] controls = { colorSet, cColor, lblDesc };
/*     */         
/* 279 */         MouseTrackListener ml = new MouseTrackListener()
/*     */         {
/*     */ 
/*     */ 
/*     */           public void mouseEnter(MouseEvent e)
/*     */           {
/*     */ 
/* 286 */             Legend.handleHover(this.val$listener, true, f_i, hover_state);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */           public void mouseExit(MouseEvent e)
/*     */           {
/* 293 */             Legend.handleHover(this.val$listener, false, f_i, hover_state);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*     */           public void mouseHover(MouseEvent e) {}
/*     */         };
/*     */         
/*     */ 
/* 303 */         for (Control c : controls)
/*     */         {
/* 305 */           c.addMouseTrackListener(ml);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 310 */     legend.addDisposeListener(new DisposeListener()
/*     */     {
/*     */ 
/*     */       public void widgetDisposed(DisposeEvent e)
/*     */       {
/* 315 */         System.arraycopy(this.val$defaultColors, 0, blockColors, 0, blockColors.length);
/* 316 */         for (int i = 0; i < keys.length; i++) {
/* 317 */           config.removeParameterListener(keys[i], paramListeners[i]);
/*     */         }
/*     */       }
/* 320 */     });
/* 321 */     return legend;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void handleHover(final LegendListener listener, boolean entry, int index, int[] state)
/*     */   {
/* 331 */     if (entry)
/*     */     {
/* 333 */       state[1] += 1;
/*     */       
/* 335 */       if (state[0] != index)
/*     */       {
/* 337 */         state[0] = index;
/*     */         
/* 339 */         listener.hoverChange(true, index);
/*     */       }
/*     */     }
/*     */     else {
/* 343 */       if (state[0] == -1)
/*     */       {
/* 345 */         return;
/*     */       }
/*     */       
/* 348 */       final int timer_index = state[1] += 1;
/*     */       
/* 350 */       Utils.execSWTThreadLater(100, new Runnable()
/*     */       {
/*     */ 
/*     */ 
/*     */         public void run()
/*     */         {
/*     */ 
/* 357 */           int leaving = this.val$state[0];
/*     */           
/* 359 */           if ((timer_index != this.val$state[1]) || (leaving == -1))
/*     */           {
/* 361 */             return;
/*     */           }
/*     */           
/* 364 */           this.val$state[0] = -1;
/*     */           
/* 366 */           listener.hoverChange(false, leaving);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface LegendListener
/*     */   {
/*     */     public abstract void hoverChange(boolean paramBoolean, int paramInt);
/*     */     
/*     */     public abstract void visibilityChange(boolean paramBoolean, int paramInt);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/components/Legend.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */