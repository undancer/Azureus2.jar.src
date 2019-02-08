/*     */ package com.aelitis.azureus.ui.swt.views.skin.sidebar;
/*     */ 
/*     */ import com.aelitis.azureus.ui.mdi.MdiCloseListener;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntry;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImage;
/*     */ import com.aelitis.azureus.ui.mdi.MdiEntryVitalityImageListener;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.widgets.Tree;
/*     */ import org.eclipse.swt.widgets.TreeItem;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPeriodic;
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
/*     */ public class SideBarVitalityImageSWT
/*     */   implements MdiEntryVitalityImage
/*     */ {
/*     */   private String imageID;
/*     */   private final MdiEntry mdiEntry;
/*  51 */   private List<MdiEntryVitalityImageListener> listeners = Collections.EMPTY_LIST;
/*     */   
/*     */   private String tooltip;
/*     */   
/*     */   private Rectangle hitArea;
/*     */   
/*  57 */   private boolean visible = true;
/*     */   
/*     */   private int currentAnimationIndex;
/*     */   
/*  61 */   private String suffix = "";
/*     */   
/*     */   private TimerEventPerformer performer;
/*     */   
/*     */   private TimerEventPeriodic timerEvent;
/*     */   
/*     */   private Image[] images;
/*     */   
/*  69 */   private int delayTime = -1;
/*     */   
/*     */   private String fullImageID;
/*     */   
/*  73 */   private int alignment = 131072;
/*     */   
/*     */   public SideBarVitalityImageSWT(MdiEntry mdiEntry, String imageID) {
/*  76 */     this.mdiEntry = mdiEntry;
/*     */     
/*  78 */     mdiEntry.addListener(new MdiCloseListener()
/*     */     {
/*     */       public void mdiEntryClosed(MdiEntry entry, boolean userClosed) {
/*  81 */         ImageLoader imageLoader = ImageLoader.getInstance();
/*  82 */         if (SideBarVitalityImageSWT.this.fullImageID != null) {
/*  83 */           imageLoader.releaseImage(SideBarVitalityImageSWT.this.fullImageID);
/*     */         }
/*     */         
/*     */       }
/*  87 */     });
/*  88 */     setImageID(imageID);
/*     */   }
/*     */   
/*     */   public String getImageID()
/*     */   {
/*  93 */     return this.imageID;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public MdiEntry getMdiEntry()
/*     */   {
/* 100 */     return this.mdiEntry;
/*     */   }
/*     */   
/*     */   public void addListener(MdiEntryVitalityImageListener l)
/*     */   {
/* 105 */     if (this.listeners == Collections.EMPTY_LIST) {
/* 106 */       this.listeners = new ArrayList(1);
/*     */     }
/* 108 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */   public void triggerClickedListeners(int x, int y) {
/* 112 */     Object[] list = this.listeners.toArray();
/* 113 */     for (int i = 0; i < list.length; i++) {
/* 114 */       MdiEntryVitalityImageListener l = (MdiEntryVitalityImageListener)list[i];
/*     */       try {
/* 116 */         l.mdiEntryVitalityImage_clicked(x, y);
/*     */       } catch (Exception e) {
/* 118 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void setToolTip(String tooltip)
/*     */   {
/* 125 */     this.tooltip = tooltip;
/*     */   }
/*     */   
/*     */   public String getToolTip() {
/* 129 */     return this.tooltip;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setHitArea(Rectangle hitArea)
/*     */   {
/* 138 */     this.hitArea = hitArea;
/*     */   }
/*     */   
/*     */   public Rectangle getHitArea() {
/* 142 */     return this.hitArea;
/*     */   }
/*     */   
/*     */   public boolean isVisible()
/*     */   {
/* 147 */     return this.visible;
/*     */   }
/*     */   
/*     */   public void setVisible(boolean visible)
/*     */   {
/* 152 */     if (this.visible == visible) {
/* 153 */       return;
/*     */     }
/* 155 */     this.visible = visible;
/*     */     
/* 157 */     if (visible) {
/* 158 */       createTimerEvent();
/* 159 */     } else if (this.timerEvent != null) {
/* 160 */       this.timerEvent.cancel();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 165 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 167 */         if (SideBarVitalityImageSWT.this.mdiEntry != null) {
/* 168 */           SideBarVitalityImageSWT.this.mdiEntry.redraw();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private synchronized void createTimerEvent()
/*     */   {
/* 180 */     if (this.timerEvent != null) {
/* 181 */       this.timerEvent.cancel();
/*     */     }
/* 183 */     if ((this.images != null) && (this.images.length > 1)) {
/* 184 */       ImageLoader imageLoader = ImageLoader.getInstance();
/* 185 */       int delay = this.delayTime == -1 ? imageLoader.getAnimationDelay(this.imageID) : this.delayTime;
/*     */       
/*     */ 
/* 188 */       if (this.performer == null) {
/* 189 */         this.performer = new TimerEventPerformer() {
/* 190 */           private boolean exec_pending = false;
/*     */           
/* 192 */           private Object lock = this;
/*     */           
/*     */           public void perform(TimerEvent event) {
/* 195 */             synchronized (this.lock)
/*     */             {
/* 197 */               if (this.exec_pending)
/*     */               {
/* 199 */                 return;
/*     */               }
/*     */               
/* 202 */               this.exec_pending = true;
/*     */             }
/*     */             
/* 205 */             Utils.execSWTThread(new AERunnable() {
/*     */               public void runSupport() {
/* 207 */                 synchronized (SideBarVitalityImageSWT.3.this.lock)
/*     */                 {
/* 209 */                   SideBarVitalityImageSWT.3.this.exec_pending = false;
/*     */                 }
/*     */                 
/* 212 */                 if ((SideBarVitalityImageSWT.this.images == null) || (SideBarVitalityImageSWT.this.images.length == 0) || (!SideBarVitalityImageSWT.this.visible) || (SideBarVitalityImageSWT.this.hitArea == null))
/*     */                 {
/* 214 */                   return;
/*     */                 }
/* 216 */                 SideBarVitalityImageSWT.access$708(SideBarVitalityImageSWT.this);
/* 217 */                 if (SideBarVitalityImageSWT.this.currentAnimationIndex >= SideBarVitalityImageSWT.this.images.length) {
/* 218 */                   SideBarVitalityImageSWT.this.currentAnimationIndex = 0;
/*     */                 }
/* 220 */                 if ((SideBarVitalityImageSWT.this.mdiEntry instanceof SideBarEntrySWT)) {
/* 221 */                   SideBarEntrySWT sbEntry = (SideBarEntrySWT)SideBarVitalityImageSWT.this.mdiEntry;
/*     */                   
/* 223 */                   TreeItem treeItem = sbEntry.getTreeItem();
/* 224 */                   if ((treeItem == null) || (treeItem.isDisposed()) || (!sbEntry.swt_isVisible()))
/*     */                   {
/* 226 */                     return;
/*     */                   }
/* 228 */                   Tree parent = treeItem.getParent();
/* 229 */                   parent.redraw(SideBarVitalityImageSWT.this.hitArea.x, SideBarVitalityImageSWT.this.hitArea.y + treeItem.getBounds().y, SideBarVitalityImageSWT.this.hitArea.width, SideBarVitalityImageSWT.this.hitArea.height, true);
/*     */                   
/* 231 */                   parent.update();
/*     */                 }
/*     */               }
/*     */             });
/*     */           }
/*     */         };
/*     */       }
/* 238 */       this.timerEvent = SimpleTimer.addPeriodicEvent("Animate " + this.mdiEntry.getId() + "::" + this.imageID + this.suffix, delay, this.performer);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public int getCurrentAnimationIndex(Image[] images)
/*     */   {
/* 248 */     if (this.currentAnimationIndex >= images.length) {
/* 249 */       this.currentAnimationIndex = 0;
/* 250 */     } else if (this.currentAnimationIndex < 0) {
/* 251 */       this.currentAnimationIndex = 0;
/*     */     }
/* 253 */     return this.currentAnimationIndex;
/*     */   }
/*     */   
/*     */   public void switchSuffix(String suffix) {
/* 257 */     if (suffix == null) {
/* 258 */       suffix = "";
/*     */     }
/* 260 */     if (suffix.equals(this.suffix)) {
/* 261 */       return;
/*     */     }
/* 263 */     this.suffix = suffix;
/* 264 */     setImageID(this.imageID);
/*     */   }
/*     */   
/*     */   public void setImageID(final String id) {
/* 268 */     Utils.execSWTThread(new AERunnable() {
/*     */       public void runSupport() {
/* 270 */         ImageLoader imageLoader = ImageLoader.getInstance();
/* 271 */         String newFullImageID = id + SideBarVitalityImageSWT.this.suffix;
/* 272 */         if (newFullImageID.equals(SideBarVitalityImageSWT.this.fullImageID)) {
/* 273 */           return;
/*     */         }
/* 275 */         if (SideBarVitalityImageSWT.this.fullImageID != null) {
/* 276 */           imageLoader.releaseImage(SideBarVitalityImageSWT.this.fullImageID);
/*     */         }
/* 278 */         SideBarVitalityImageSWT.this.imageID = id;
/* 279 */         SideBarVitalityImageSWT.this.images = imageLoader.getImages(newFullImageID);
/* 280 */         if ((SideBarVitalityImageSWT.this.images == null) || (SideBarVitalityImageSWT.this.images.length == 0)) {
/* 281 */           imageLoader.releaseImage(newFullImageID);
/* 282 */           newFullImageID = id;
/* 283 */           SideBarVitalityImageSWT.this.images = imageLoader.getImages(id);
/*     */         }
/* 285 */         SideBarVitalityImageSWT.this.fullImageID = newFullImageID;
/* 286 */         SideBarVitalityImageSWT.this.currentAnimationIndex = 0;
/* 287 */         if (SideBarVitalityImageSWT.this.isVisible()) {
/* 288 */           SideBarVitalityImageSWT.this.createTimerEvent();
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Image getImage()
/*     */   {
/* 300 */     if ((this.images == null) || (this.images.length == 0) || (this.currentAnimationIndex >= this.images.length))
/*     */     {
/* 302 */       return null;
/*     */     }
/* 304 */     return this.images[this.currentAnimationIndex];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDelayTime(int delayTime)
/*     */   {
/* 311 */     if (this.delayTime == delayTime) {
/* 312 */       return;
/*     */     }
/* 314 */     this.delayTime = delayTime;
/* 315 */     if (isVisible()) {
/* 316 */       createTimerEvent();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int getDelayTime()
/*     */   {
/* 324 */     return this.delayTime;
/*     */   }
/*     */   
/*     */   public int getAlignment() {
/* 328 */     return this.alignment;
/*     */   }
/*     */   
/*     */   public void setAlignment(int alignment) {
/* 332 */     this.alignment = alignment;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/views/skin/sidebar/SideBarVitalityImageSWT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */