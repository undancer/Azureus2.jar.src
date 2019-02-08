/*     */ package com.aelitis.azureus.ui.swt.views.skin.sidebar;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdatable;
/*     */ import com.aelitis.azureus.ui.common.updater.UIUpdater;
/*     */ import com.aelitis.azureus.ui.common.viewtitleinfo.ViewTitleInfo;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*     */ import com.aelitis.azureus.ui.swt.uiupdater.UIUpdaterSWT;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Monitor;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Tree;
/*     */ import org.eclipse.swt.widgets.TreeItem;
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
/*     */ public class SideBarToolTips
/*     */   implements Listener, UIUpdatable
/*     */ {
/*  44 */   Shell toolTipShell = null;
/*     */   
/*  46 */   Shell mainShell = null;
/*     */   
/*  48 */   Label toolTipLabel = null;
/*     */   
/*     */ 
/*     */   private final Tree tree;
/*     */   
/*     */   private MdiEntry mdiEntry;
/*     */   
/*     */   private Point lastRelMouseHoverPos;
/*     */   
/*     */ 
/*     */   public SideBarToolTips(SideBar sidebar, Tree tree)
/*     */   {
/*  60 */     this.tree = tree;
/*  61 */     this.mainShell = tree.getShell();
/*     */     
/*  63 */     tree.addListener(12, this);
/*  64 */     tree.addListener(1, this);
/*  65 */     tree.addListener(5, this);
/*  66 */     tree.addListener(32, this);
/*  67 */     this.mainShell.addListener(27, this);
/*  68 */     tree.addListener(27, this);
/*     */   }
/*     */   
/*     */   public void handleEvent(Event event) {
/*  72 */     switch (event.type) {
/*     */     case 32: 
/*  74 */       handleHover(new Point(event.x, event.y));
/*  75 */       break;
/*     */     
/*     */ 
/*     */     case 12: 
/*  79 */       if ((this.mainShell != null) && (!this.mainShell.isDisposed())) {
/*  80 */         this.mainShell.removeListener(27, this);
/*     */       }
/*     */       break;
/*     */     }
/*     */     
/*  85 */     if (this.toolTipShell != null) {
/*  86 */       this.toolTipShell.dispose();
/*  87 */       this.toolTipShell = null;
/*  88 */       this.toolTipLabel = null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private void handleHover(Point mousePos)
/*     */   {
/* 100 */     if ((this.toolTipShell != null) && (!this.toolTipShell.isDisposed())) {
/* 101 */       this.toolTipShell.dispose();
/*     */     }
/* 103 */     if (this.tree.getItemCount() == 0) {
/* 104 */       return;
/*     */     }
/* 106 */     int indent = SideBar.END_INDENT ? this.tree.getClientArea().width - 1 : 0;
/* 107 */     TreeItem treeItem = this.tree.getItem(new Point(indent, mousePos.y));
/* 108 */     if (treeItem == null) {
/* 109 */       return;
/*     */     }
/* 111 */     this.mdiEntry = ((MdiEntry)treeItem.getData("MdiEntry"));
/* 112 */     if (this.mdiEntry == null) {
/* 113 */       return;
/*     */     }
/*     */     
/* 116 */     Rectangle itemBounds = treeItem.getBounds();
/* 117 */     Point relPos = new Point(mousePos.x, mousePos.y - itemBounds.y);
/* 118 */     String sToolTip = getToolTip(relPos);
/* 119 */     if ((sToolTip == null) || (sToolTip.length() == 0)) {
/* 120 */       return;
/*     */     }
/*     */     
/* 123 */     this.lastRelMouseHoverPos = relPos;
/*     */     
/* 125 */     Display d = this.tree.getDisplay();
/* 126 */     if (d == null) {
/* 127 */       return;
/*     */     }
/*     */     
/* 130 */     this.toolTipShell = new Shell(this.tree.getShell(), 16384);
/* 131 */     this.toolTipShell.addListener(12, new Listener()
/*     */     {
/*     */       public void handleEvent(Event event) {
/* 134 */         UIUpdaterSWT.getInstance().removeUpdater(SideBarToolTips.this);
/*     */       }
/* 136 */     });
/* 137 */     FillLayout f = new FillLayout();
/*     */     try {
/* 139 */       f.marginWidth = 3;
/* 140 */       f.marginHeight = 1;
/*     */     }
/*     */     catch (NoSuchFieldError e) {}
/*     */     
/* 144 */     this.toolTipShell.setLayout(f);
/* 145 */     this.toolTipShell.setBackground(d.getSystemColor(29));
/*     */     
/* 147 */     this.toolTipLabel = new Label(this.toolTipShell, 64);
/* 148 */     this.toolTipLabel.setForeground(d.getSystemColor(28));
/* 149 */     this.toolTipLabel.setBackground(d.getSystemColor(29));
/* 150 */     this.toolTipLabel.setText(sToolTip.replaceAll("&", "&&"));
/*     */     
/*     */ 
/* 153 */     Point size = this.toolTipLabel.computeSize(-1, -1);
/* 154 */     if (size.x > 600) {
/* 155 */       size = this.toolTipLabel.computeSize(600, -1, true);
/*     */     }
/* 157 */     size.x += this.toolTipShell.getBorderWidth() * 2 + 2;
/* 158 */     size.y += this.toolTipShell.getBorderWidth() * 2;
/*     */     try {
/* 160 */       size.x += this.toolTipShell.getBorderWidth() * 2 + f.marginWidth * 2;
/* 161 */       size.y += this.toolTipShell.getBorderWidth() * 2 + f.marginHeight * 2;
/*     */     }
/*     */     catch (NoSuchFieldError e) {}
/*     */     
/* 165 */     Point pt = this.tree.toDisplay(mousePos.x, mousePos.y);
/*     */     Rectangle displayRect;
/*     */     try {
/* 168 */       displayRect = this.tree.getMonitor().getClientArea();
/*     */     } catch (NoSuchMethodError e) {
/* 170 */       displayRect = this.tree.getDisplay().getClientArea();
/*     */     }
/* 172 */     if (pt.x + size.x > displayRect.x + displayRect.width) {
/* 173 */       pt.x = (displayRect.x + displayRect.width - size.x);
/*     */     }
/*     */     
/* 176 */     if (pt.y + size.y > displayRect.y + displayRect.height) {
/* 177 */       pt.y -= size.y + 2;
/*     */     } else {
/* 179 */       pt.y += 21;
/*     */     }
/*     */     
/* 182 */     if (pt.y < displayRect.y) {
/* 183 */       pt.y = displayRect.y;
/*     */     }
/* 185 */     this.toolTipShell.setBounds(pt.x, pt.y, size.x, size.y);
/* 186 */     this.toolTipShell.setVisible(true);
/* 187 */     UIUpdaterSWT.getInstance().addUpdater(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private String getToolTip(Point mousePos_RelativeToItem)
/*     */   {
/* 197 */     MdiEntryVitalityImage[] vitalityImages = this.mdiEntry.getVitalityImages();
/* 198 */     for (int i = 0; i < vitalityImages.length; i++) {
/* 199 */       SideBarVitalityImageSWT vitalityImage = (SideBarVitalityImageSWT)vitalityImages[i];
/* 200 */       if (vitalityImage != null)
/*     */       {
/*     */ 
/* 203 */         String indicatorToolTip = vitalityImage.getToolTip();
/* 204 */         if ((indicatorToolTip != null) && (vitalityImage.isVisible()))
/*     */         {
/*     */ 
/* 207 */           Rectangle hitArea = vitalityImage.getHitArea();
/* 208 */           if (hitArea != null)
/*     */           {
/*     */ 
/* 211 */             if (hitArea.contains(mousePos_RelativeToItem))
/* 212 */               return indicatorToolTip; }
/*     */         }
/*     */       }
/*     */     }
/* 216 */     if (this.mdiEntry.getViewTitleInfo() != null) {
/* 217 */       String tt = (String)this.mdiEntry.getViewTitleInfo().getTitleInfoProperty(1);
/* 218 */       return tt;
/*     */     }
/*     */     
/* 221 */     return null;
/*     */   }
/*     */   
/*     */   public String getUpdateUIName()
/*     */   {
/* 226 */     return "SideBarToolTips";
/*     */   }
/*     */   
/*     */   public void updateUI()
/*     */   {
/* 231 */     if ((this.toolTipLabel == null) || (this.toolTipLabel.isDisposed())) {
/* 232 */       return;
/*     */     }
/* 234 */     if ((this.mdiEntry == null) || (this.mdiEntry.getViewTitleInfo() == null)) {
/* 235 */       return;
/*     */     }
/* 237 */     String sToolTip = getToolTip(this.lastRelMouseHoverPos);
/* 238 */     if (sToolTip == null) {
/* 239 */       return;
/*     */     }
/*     */     
/* 242 */     this.toolTipLabel.setText(sToolTip.replaceAll("&", "&&"));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/sidebar/SideBarToolTips.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */