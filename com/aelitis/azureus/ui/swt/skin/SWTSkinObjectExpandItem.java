/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.events.ExpandEvent;
/*     */ import org.eclipse.swt.events.ExpandListener;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.ExpandBar;
/*     */ import org.eclipse.swt.widgets.ExpandItem;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ public class SWTSkinObjectExpandItem
/*     */   extends SWTSkinObjectContainer
/*     */   implements ExpandListener
/*     */ {
/*     */   private ExpandItem expandItem;
/*     */   private boolean expanded;
/*     */   private boolean textOverride;
/*     */   private Composite composite;
/*     */   private boolean fillHeight;
/*     */   
/*     */   public SWTSkinObjectExpandItem(SWTSkin skin, SWTSkinProperties properties, String sID, String sConfigID, SWTSkinObject parent)
/*     */   {
/*  54 */     super(skin, properties, null, sID, sConfigID, "expanditem", parent);
/*     */     
/*  56 */     createExpandItem();
/*     */   }
/*     */   
/*     */   private void createExpandItem()
/*     */   {
/*  61 */     if (!(this.parent instanceof SWTSkinObjectExpandBar)) {
/*  62 */       return;
/*     */     }
/*     */     
/*  65 */     final SWTSkinObjectExpandBar soExpandBar = (SWTSkinObjectExpandBar)this.parent;
/*     */     
/*  67 */     int style = 0;
/*  68 */     if (this.properties.getIntValue(this.sConfigID + ".border", 0) == 1) {
/*  69 */       style = 2048;
/*     */     }
/*     */     
/*  72 */     ExpandBar expandBar = soExpandBar.getExpandbar();
/*  73 */     expandBar.addExpandListener(this);
/*     */     
/*  75 */     this.expandItem = new ExpandItem(expandBar, style);
/*     */     
/*  77 */     String lastExpandStateID = "ui.skin." + this.sConfigID + ".expanded";
/*  78 */     if (COConfigurationManager.hasParameter(lastExpandStateID, true)) {
/*  79 */       boolean lastExpandState = COConfigurationManager.getBooleanParameter(lastExpandStateID, false);
/*     */       
/*  81 */       setExpanded(lastExpandState);
/*  82 */     } else if (this.properties.getBooleanValue(this.sConfigID + ".expanded", false)) {
/*  83 */       setExpanded(true);
/*     */     }
/*     */     
/*  86 */     this.composite = createComposite(soExpandBar.getComposite());
/*  87 */     this.expandItem.setControl(this.composite);
/*  88 */     this.composite.setLayoutData(null);
/*  89 */     this.composite.setData("skin.layedout", Boolean.valueOf(true));
/*     */     
/*  91 */     soExpandBar.addExpandItem(this);
/*     */     
/*  93 */     this.expandItem.addDisposeListener(new DisposeListener() {
/*     */       public void widgetDisposed(DisposeEvent e) {
/*  95 */         soExpandBar.removeExpandItem(SWTSkinObjectExpandItem.this);
/*     */       }
/*     */       
/*  98 */     });
/*  99 */     this.composite.addListener(11, new Listener() {
/* 100 */       private Map<Rectangle, Long> resize_history = new HashMap();
/*     */       
/* 102 */       public void handleEvent(Event event) { Rectangle bounds = SWTSkinObjectExpandItem.this.composite.getBounds();
/* 103 */         long now = SystemTime.getMonotonousTime();
/* 104 */         Long prev = (Long)this.resize_history.get(bounds);
/* 105 */         if ((prev != null) && 
/* 106 */           (now - prev.longValue() < 500L)) {
/* 107 */           return;
/*     */         }
/*     */         
/* 110 */         Iterator<Long> it = this.resize_history.values().iterator();
/* 111 */         while (it.hasNext()) {
/* 112 */           if (now - ((Long)it.next()).longValue() >= 500L) {
/* 113 */             it.remove();
/*     */           }
/*     */         }
/* 116 */         this.resize_history.put(bounds, Long.valueOf(now));
/* 117 */         Utils.execSWTThreadLater(0, new AERunnable() {
/*     */           public void runSupport() {
/* 119 */             SWTSkinObjectExpandBar soExpandBar = (SWTSkinObjectExpandBar)SWTSkinObjectExpandItem.this.parent;
/* 120 */             if (!SWTSkinObjectExpandItem.this.expandItem.isDisposed()) {
/* 121 */               soExpandBar.handleResize(SWTSkinObjectExpandItem.this.expandItem);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void relayout()
/*     */   {
/* 131 */     super.relayout();
/* 132 */     SWTSkinObjectExpandBar soExpandBar = (SWTSkinObjectExpandBar)this.parent;
/* 133 */     soExpandBar.handleResize(this.expandItem);
/*     */   }
/*     */   
/*     */ 
/*     */   protected void resizeComposite()
/*     */   {
/* 139 */     SWTSkinObjectExpandBar soExpandBar = (SWTSkinObjectExpandBar)this.parent;
/* 140 */     ExpandBar expandBar = soExpandBar.getExpandbar();
/* 141 */     if (this.composite.isDisposed()) {
/* 142 */       return;
/*     */     }
/*     */     
/* 145 */     if (!this.composite.isVisible()) {
/* 146 */       return;
/*     */     }
/*     */     
/* 149 */     Rectangle clientArea = expandBar.getClientArea();
/*     */     int newHeight;
/*     */     int newHeight;
/* 152 */     if (this.properties.getBooleanValue(this.sConfigID + ".fillheight", false)) {
/* 153 */       ExpandItem[] items = expandBar.getItems();
/* 154 */       int h = expandBar.getSpacing();
/* 155 */       for (ExpandItem item : items) {
/* 156 */         h += expandBar.getSpacing();
/*     */         
/*     */ 
/*     */ 
/* 160 */         int hh = item.getHeaderHeight();
/*     */         
/* 162 */         int ih = item.getHeight();
/* 163 */         if (hh < 0) {
/* 164 */           hh += item.getHeight();
/* 165 */         } else if (hh > ih) {
/* 166 */           hh -= ih;
/*     */         }
/*     */         
/* 169 */         h += hh;
/* 170 */         if ((this.expandItem != item) && 
/* 171 */           (item.getExpanded()) && (item.getControl().isVisible())) {
/* 172 */           h += item.getHeight();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 178 */       newHeight = clientArea.height - h;
/*     */     }
/*     */     else {
/* 181 */       newHeight = this.composite.computeSize(clientArea.width, -1, true).y;
/* 182 */       expandBar.computeSize(-1, -1, true);
/*     */     }
/*     */     
/* 185 */     if (this.expandItem.getHeight() != newHeight) {
/* 186 */       this.expandItem.setHeight(newHeight);
/*     */     }
/*     */   }
/*     */   
/*     */   public ExpandItem getExpandItem() {
/* 191 */     return this.expandItem;
/*     */   }
/*     */   
/*     */   public boolean isExpanded() {
/* 195 */     return this.expanded;
/*     */   }
/*     */   
/*     */   private void setExpandedVariable(boolean expand) {
/* 199 */     this.expanded = expand;
/* 200 */     String lastExpandStateID = "ui.skin." + this.sConfigID + ".expanded";
/* 201 */     COConfigurationManager.setParameter(lastExpandStateID, expand);
/*     */   }
/*     */   
/*     */   public void setExpanded(final boolean expand) {
/* 205 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 207 */         SWTSkinObjectExpandItem.this.expandItem.setExpanded(expand);
/* 208 */         SWTSkinObjectExpandItem.this.setExpandedVariable(expand);
/* 209 */         Utils.execSWTThreadLater(0, new AERunnable() {
/*     */           public void runSupport() {
/* 211 */             SWTSkinObjectExpandBar soExpandBar = (SWTSkinObjectExpandBar)SWTSkinObjectExpandItem.this.parent;
/* 212 */             soExpandBar.handleResize(SWTSkinObjectExpandItem.this.expandItem);
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void itemCollapsed(ExpandEvent e) {
/* 220 */     if (e.item == this.expandItem) {
/* 221 */       setExpandedVariable(false);
/*     */       
/* 223 */       Utils.execSWTThreadLater(0, new AERunnable() {
/*     */         public void runSupport() {
/* 225 */           SWTSkinObjectExpandBar soExpandBar = (SWTSkinObjectExpandBar)SWTSkinObjectExpandItem.this.parent;
/* 226 */           soExpandBar.handleResize(SWTSkinObjectExpandItem.this.expandItem);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */   public void itemExpanded(ExpandEvent e) {
/* 233 */     if (e.item == this.expandItem) {
/* 234 */       setExpandedVariable(true);
/* 235 */       Utils.execSWTThreadLater(0, new AERunnable() {
/*     */         public void runSupport() {
/* 237 */           SWTSkinObjectExpandBar soExpandBar = (SWTSkinObjectExpandBar)SWTSkinObjectExpandItem.this.parent;
/* 238 */           soExpandBar.handleResize(SWTSkinObjectExpandItem.this.expandItem);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String switchSuffix(String suffix, int level, boolean walkUp, boolean walkDown)
/*     */   {
/* 247 */     suffix = super.switchSuffix(suffix, level, walkUp, walkDown);
/*     */     
/* 249 */     if (suffix == null) {
/* 250 */       return null;
/*     */     }
/*     */     
/* 253 */     String sPrefix = this.sConfigID + ".text";
/* 254 */     String text = this.properties.getStringValue(sPrefix + suffix);
/* 255 */     if (text != null) {
/* 256 */       setText(text, true);
/*     */     }
/*     */     
/* 259 */     this.fillHeight = this.properties.getBooleanValue(this.sConfigID + ".fillheight", false);
/*     */     
/* 261 */     return suffix;
/*     */   }
/*     */   
/*     */   public void setText(String text) {
/* 265 */     setText(text, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void setText(final String text, boolean auto)
/*     */   {
/* 274 */     if (!auto) {
/* 275 */       this.textOverride = true;
/* 276 */     } else if (this.textOverride) {
/* 277 */       return;
/*     */     }
/*     */     
/* 280 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 282 */         if ((SWTSkinObjectExpandItem.this.expandItem != null) && (!SWTSkinObjectExpandItem.this.expandItem.isDisposed())) {
/* 283 */           if (Constants.isWindows) {
/* 284 */             SWTSkinObjectExpandItem.this.expandItem.setText(text.replaceAll("&", "&&"));
/*     */           } else {
/* 286 */             SWTSkinObjectExpandItem.this.expandItem.setText(text);
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public boolean fillsHeight()
/*     */   {
/* 295 */     return this.fillHeight;
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 299 */     super.dispose();
/* 300 */     if ((this.parent instanceof SWTSkinObjectExpandBar)) {
/* 301 */       SWTSkinObjectExpandBar soExpandBar = (SWTSkinObjectExpandBar)this.parent;
/* 302 */       ExpandBar expandbar = soExpandBar.getExpandbar();
/* 303 */       if ((expandbar != null) && (!expandbar.isDisposed())) {
/* 304 */         expandbar.removeExpandListener(this);
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectExpandItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */