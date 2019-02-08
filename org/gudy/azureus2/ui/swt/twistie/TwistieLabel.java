/*     */ package org.gudy.azureus2.ui.swt.twistie;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.MouseAdapter;
/*     */ import org.eclipse.swt.events.MouseEvent;
/*     */ import org.eclipse.swt.events.MouseListener;
/*     */ import org.eclipse.swt.events.PaintEvent;
/*     */ import org.eclipse.swt.events.PaintListener;
/*     */ import org.eclipse.swt.graphics.Color;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Label;
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
/*     */ public class TwistieLabel
/*     */   extends Composite
/*     */   implements ITwistieConstants
/*     */ {
/*  51 */   private int style = 2;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  56 */   static final int[] points_for_expanded = { 0, 2, 8, 2, 4, 6 };
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
/*  69 */   static final int[] points_for_collapsed = { 2, -1, 2, 8, 6, 4 };
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
/*  81 */   private Label titleLabel = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  87 */   private Color twistieColor = null;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  92 */   private boolean isCollapsed = true;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*  97 */   private Label descriptionLabel = null;
/*     */   
/*  99 */   private List listeners = new ArrayList();
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
/*     */   public TwistieLabel(Composite parent, int style)
/*     */   {
/* 115 */     super(parent, 0);
/* 116 */     setBackgroundMode(2);
/* 117 */     this.style = style;
/*     */     
/* 119 */     GridLayout gLayout = new GridLayout();
/* 120 */     gLayout.marginHeight = 0;
/* 121 */     gLayout.marginWidth = 0;
/* 122 */     gLayout.verticalSpacing = 0;
/* 123 */     gLayout.horizontalSpacing = 0;
/* 124 */     setLayout(gLayout);
/*     */     
/* 126 */     this.titleLabel = new Label(this, 0);
/*     */     
/* 128 */     if ((this.style & 0x8) != 0) {
/* 129 */       Label separator = new Label(this, 258);
/* 130 */       GridData labelData = new GridData(4, 16777216, true, false);
/* 131 */       labelData.horizontalIndent = 10;
/* 132 */       separator.setLayoutData(labelData);
/*     */     }
/*     */     
/* 135 */     if ((this.style & 0x4) != 0) {
/* 136 */       this.descriptionLabel = new Label(this, 64);
/* 137 */       GridData labelData = new GridData(4, 4, true, false);
/* 138 */       labelData.horizontalIndent = 10;
/* 139 */       this.descriptionLabel.setLayoutData(labelData);
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 144 */       Font initialFont = this.descriptionLabel.getFont();
/* 145 */       FontData[] fontData = initialFont.getFontData();
/* 146 */       for (int i = 0; i < fontData.length; i++) {
/* 147 */         fontData[i].setStyle(fontData[i].getStyle() | 0x2);
/*     */       }
/* 149 */       this.descriptionLabel.setFont(new Font(getDisplay(), fontData));
/*     */     }
/*     */     
/*     */ 
/* 153 */     if ((this.style & 0x10) != 0) {
/* 154 */       this.isCollapsed = false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 160 */     GridData labelData = new GridData(4, 16777216, true, false);
/* 161 */     labelData.horizontalIndent = 10;
/* 162 */     this.titleLabel.setLayoutData(labelData);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 167 */     MouseInterceptor interceptor = new MouseInterceptor(null);
/* 168 */     super.addMouseListener(interceptor);
/* 169 */     this.titleLabel.addMouseListener(interceptor);
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 174 */     addPaintListener(new PaintListener()
/*     */     {
/*     */ 
/*     */       public void paintControl(PaintEvent e)
/*     */       {
/*     */ 
/* 180 */         int offsetX = TwistieLabel.this.titleLabel.getBounds().x - 10;
/* 181 */         int offsetY = TwistieLabel.this.titleLabel.getBounds().y + 3;
/*     */         
/* 183 */         if (null != TwistieLabel.this.twistieColor) {
/* 184 */           e.gc.setBackground(TwistieLabel.this.twistieColor);
/*     */         } else {
/* 186 */           e.gc.setBackground(TwistieLabel.this.getForeground());
/*     */         }
/*     */         
/* 189 */         if (TwistieLabel.this.isCollapsed) {
/* 190 */           e.gc.fillPolygon(TwistieLabel.this.translate(TwistieLabel.points_for_collapsed, offsetX, offsetY));
/*     */         } else {
/* 192 */           e.gc.fillPolygon(TwistieLabel.this.translate(TwistieLabel.points_for_expanded, offsetX, offsetY));
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private int[] translate(int[] data, int x, int y)
/*     */   {
/* 207 */     int[] target = new int[data.length];
/* 208 */     for (int i = 0; i < data.length; i += 2) {
/* 209 */       data[i] += x;
/*     */     }
/* 211 */     for (int i = 1; i < data.length; i += 2) {
/* 212 */       data[i] += y;
/*     */     }
/* 214 */     return target;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addMouseListener(MouseListener listener)
/*     */   {
/* 221 */     if (null != this.titleLabel) {
/* 222 */       this.titleLabel.addMouseListener(listener);
/*     */     }
/* 224 */     super.addMouseListener(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeMouseListener(MouseListener listener)
/*     */   {
/* 231 */     if (null != this.titleLabel) {
/* 232 */       this.titleLabel.removeMouseListener(listener);
/*     */     }
/* 234 */     super.removeMouseListener(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTwistieForeground(Color color)
/*     */   {
/* 242 */     this.twistieColor = color;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setForeground(Color color)
/*     */   {
/* 249 */     if ((null != this.titleLabel) && (!this.titleLabel.isDisposed())) {
/* 250 */       this.titleLabel.setForeground(color);
/*     */     }
/* 252 */     if ((null != this.descriptionLabel) && (!this.descriptionLabel.isDisposed())) {
/* 253 */       this.descriptionLabel.setForeground(color);
/*     */     }
/*     */     
/* 256 */     if (null == this.twistieColor) {
/* 257 */       this.twistieColor = color;
/*     */     }
/*     */     
/* 260 */     super.setForeground(color);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setBackground(Color color)
/*     */   {
/* 267 */     if (null != this.titleLabel) {
/* 268 */       this.titleLabel.setBackground(color);
/*     */     }
/* 270 */     if (null != this.descriptionLabel) {
/* 271 */       this.descriptionLabel.setBackground(color);
/*     */     }
/*     */     
/* 274 */     super.setBackground(color);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setTitle(String string)
/*     */   {
/* 282 */     if (null != this.titleLabel) {
/* 283 */       this.titleLabel.setText(string);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setDescription(String string)
/*     */   {
/* 292 */     if (null != this.descriptionLabel) {
/* 293 */       this.descriptionLabel.setText(string);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setToolTipText(String string)
/*     */   {
/* 301 */     if (null != this.titleLabel) {
/* 302 */       this.titleLabel.setToolTipText(string);
/*     */     }
/*     */     
/* 305 */     if (null != this.descriptionLabel) {
/* 306 */       this.descriptionLabel.setToolTipText(string);
/*     */     }
/*     */     
/* 309 */     super.setToolTipText(string);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnabled(boolean enabled)
/*     */   {
/* 316 */     if (null != this.titleLabel) {
/* 317 */       this.titleLabel.setEnabled(enabled);
/*     */     }
/*     */     
/* 320 */     super.setEnabled(enabled);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean isCollapsed()
/*     */   {
/* 328 */     return this.isCollapsed;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setCollapsed(boolean c)
/*     */   {
/* 335 */     if (c != this.isCollapsed) {
/* 336 */       this.isCollapsed = c;
/* 337 */       redraw();
/* 338 */       notifyTwistieListeners();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addTwistieListener(ITwistieListener listener)
/*     */   {
/* 348 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */   public void removeTwistieListener(ITwistieListener listener) {
/* 352 */     this.listeners.remove(listener);
/*     */   }
/*     */   
/*     */   private void notifyTwistieListeners() {
/* 356 */     for (Iterator iterator = this.listeners.iterator(); iterator.hasNext();) {
/* 357 */       ((ITwistieListener)iterator.next()).isCollapsed(isCollapsed());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private class MouseInterceptor
/*     */     extends MouseAdapter
/*     */   {
/*     */     private MouseInterceptor() {}
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     public void mouseDown(MouseEvent e)
/*     */     {
/* 378 */       TwistieLabel.this.isCollapsed = (!TwistieLabel.this.isCollapsed);
/* 379 */       TwistieLabel.this.redraw();
/* 380 */       TwistieLabel.this.notifyTwistieListeners();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/twistie/TwistieLabel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */