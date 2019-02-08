/*     */ package com.aelitis.azureus.ui.swt.skin;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.utils.FontUtils;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.events.DisposeEvent;
/*     */ import org.eclipse.swt.events.DisposeListener;
/*     */ import org.eclipse.swt.graphics.Font;
/*     */ import org.eclipse.swt.graphics.FontData;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.ExpandBar;
/*     */ import org.eclipse.swt.widgets.ExpandItem;
/*     */ import org.eclipse.swt.widgets.Listener;
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
/*     */ public class SWTSkinObjectExpandBar
/*     */   extends SWTSkinObjectContainer
/*     */ {
/*     */   private ExpandBar expandBar;
/*  44 */   private List<SWTSkinObjectExpandItem> expandItems = new ArrayList();
/*     */   
/*  46 */   private List<SWTSkinObjectExpandItem> fillHeightItems = new ArrayList();
/*     */   
/*     */   public SWTSkinObjectExpandBar(SWTSkin skin, SWTSkinProperties properties, String sID, String sConfigID, SWTSkinObject parent)
/*     */   {
/*  50 */     super(skin, properties, null, sID, sConfigID, "expandbar", parent);
/*  51 */     createExpandBar();
/*     */   }
/*     */   
/*     */   private void createExpandBar() { Composite createOn;
/*     */     Composite createOn;
/*  56 */     if (this.parent == null) {
/*  57 */       createOn = this.skin.getShell();
/*     */     } else {
/*  59 */       createOn = (Composite)this.parent.getControl();
/*     */     }
/*     */     
/*  62 */     int style = 0;
/*  63 */     if (this.properties.getIntValue(this.sConfigID + ".border", 0) == 1) {
/*  64 */       style = 2048;
/*     */     }
/*     */     
/*  67 */     this.expandBar = new ExpandBar(createOn, style);
/*     */     
/*  69 */     this.expandBar.setLayout(null);
/*  70 */     this.expandBar.setSpacing(1);
/*     */     
/*  72 */     if (!Utils.isGTK3) {
/*  73 */       this.expandBar.setFont(createOn.getFont());
/*     */     } else {
/*  75 */       FontData[] fontData = createOn.getFont().getFontData();
/*  76 */       for (FontData fd : fontData) {
/*  77 */         fd.setStyle(1);
/*  78 */         float height = FontUtils.getHeight(fontData) * 1.2F;
/*  79 */         FontUtils.setFontDataHeight(fontData, height);
/*     */       }
/*  81 */       final Font font = new Font(createOn.getDisplay(), fontData);
/*  82 */       this.expandBar.setFont(font);
/*  83 */       this.expandBar.setSpacing(3);
/*     */       
/*  85 */       this.expandBar.addDisposeListener(new DisposeListener() {
/*     */         public void widgetDisposed(DisposeEvent e) {
/*  87 */           Utils.disposeSWTObjects(new Object[] { font });
/*     */         }
/*     */       });
/*     */     }
/*     */     
/*  92 */     this.expandBar.addListener(11, new Listener() {
/*     */       public void handleEvent(Event event) {
/*  94 */         SWTSkinObjectExpandBar.this.handleResize(null);
/*     */       }
/*     */       
/*  97 */     });
/*  98 */     triggerListeners(4);
/*  99 */     setControl(this.expandBar);
/*     */   }
/*     */   
/*     */   protected void handleResize(ExpandItem itemResizing)
/*     */   {
/* 104 */     SWTSkinObjectExpandItem foundItem = null;
/* 105 */     if (itemResizing != null) {
/* 106 */       SWTSkinObjectExpandItem[] children = getChildren();
/* 107 */       for (SWTSkinObjectExpandItem item : children) {
/* 108 */         if (item.getExpandItem() == itemResizing) {
/* 109 */           foundItem = item;
/* 110 */           item.resizeComposite();
/* 111 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 116 */     for (SWTSkinObjectExpandItem autoItem : this.fillHeightItems) {
/* 117 */       if (autoItem != foundItem) {
/* 118 */         autoItem.resizeComposite();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void relayout()
/*     */   {
/* 127 */     super.relayout();
/* 128 */     handleResize(null);
/*     */   }
/*     */   
/*     */   protected void addExpandItem(SWTSkinObjectExpandItem item) {
/* 132 */     this.expandItems.add(item);
/*     */     
/* 134 */     if (item.fillsHeight()) {
/* 135 */       this.fillHeightItems.add(item);
/*     */     }
/*     */   }
/*     */   
/*     */   protected void removeExpandItem(SWTSkinObjectExpandItem item) {
/* 140 */     this.expandItems.remove(item);
/* 141 */     this.fillHeightItems.remove(item);
/*     */   }
/*     */   
/*     */   public SWTSkinObjectExpandItem[] getChildren() {
/* 145 */     return (SWTSkinObjectExpandItem[])this.expandItems.toArray(new SWTSkinObjectExpandItem[0]);
/*     */   }
/*     */   
/*     */   public ExpandBar getExpandbar() {
/* 149 */     return this.expandBar;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/skin/SWTSkinObjectExpandBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */