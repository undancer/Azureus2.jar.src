/*     */ package org.gudy.azureus2.ui.swt.pluginsimpl;
/*     */ 
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.util.Iterator;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import org.eclipse.swt.events.MenuEvent;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Menu;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.AERunnable;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuContext;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.menus.MenuContextImpl;
/*     */ import org.gudy.azureus2.ui.common.util.MenuItemManager;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils;
/*     */ import org.gudy.azureus2.ui.swt.MenuBuildUtils.MenuBuilder;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.IMainStatusBar.CLabelUpdater;
/*     */ import org.gudy.azureus2.ui.swt.mainwindow.MainStatusBar.CLabelPadding;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTStatusEntry;
/*     */ import org.gudy.azureus2.ui.swt.plugins.UISWTStatusEntryListener;
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
/*     */ public class UISWTStatusEntryImpl
/*     */   implements UISWTStatusEntry, IMainStatusBar.CLabelUpdater
/*     */ {
/*  53 */   private AEMonitor this_mon = new AEMonitor("UISWTStatusEntryImpl@" + Integer.toHexString(hashCode()));
/*     */   
/*  55 */   private UISWTStatusEntryListener listener = null;
/*  56 */   private MenuContextImpl menu_context = MenuContextImpl.create("status_entry");
/*     */   
/*     */ 
/*  59 */   private boolean needs_update = false;
/*  60 */   private boolean needs_layout = false;
/*  61 */   private String text = null;
/*  62 */   private String tooltip = null;
/*  63 */   private boolean image_enabled = false;
/*  64 */   private Image original_image = null;
/*     */   private boolean check_scaled_image;
/*  66 */   private Image scaled_image = null;
/*  67 */   private boolean is_visible = false;
/*  68 */   private boolean needs_disposing = false;
/*  69 */   private boolean is_destroyed = false;
/*     */   
/*     */   private Menu menu;
/*     */   
/*  73 */   private CopyOnWriteArrayList<String> imageIDstoDispose = new CopyOnWriteArrayList();
/*  74 */   private String imageID = null;
/*     */   
/*     */   private void checkDestroyed() {
/*  77 */     if (this.is_destroyed) throw new RuntimeException("object is destroyed, cannot be reused");
/*     */   }
/*     */   
/*     */   public MenuContext getMenuContext() {
/*  81 */     return this.menu_context;
/*     */   }
/*     */   
/*     */   public boolean update(MainStatusBar.CLabelPadding label) {
/*  85 */     if ((this.needs_disposing) && (!label.isDisposed())) {
/*  86 */       if ((this.menu != null) && (!this.menu.isDisposed())) {
/*  87 */         this.menu.dispose();
/*  88 */         this.menu = null;
/*     */       }
/*  90 */       label.dispose();
/*     */       
/*  92 */       if (this.imageID != null) {
/*  93 */         this.imageIDstoDispose.add(this.imageID);
/*     */       }
/*  95 */       releaseOldImages();
/*  96 */       if (this.scaled_image != null) {
/*  97 */         this.scaled_image.dispose();
/*  98 */         this.scaled_image = null;
/*     */       }
/*     */       
/* 101 */       return true;
/*     */     }
/*     */     
/* 104 */     boolean do_layout = this.needs_layout;
/*     */     
/* 106 */     this.needs_layout = false;
/*     */     
/* 108 */     if (this.menu_context.is_dirty) { this.needs_update = true;this.menu_context.is_dirty = false; }
/* 109 */     if (!this.needs_update) { return do_layout;
/*     */     }
/*     */     try
/*     */     {
/* 113 */       this.this_mon.enter();
/* 114 */       update0(label);
/*     */     }
/*     */     finally {
/* 117 */       this.this_mon.exit();
/*     */     }
/*     */     
/* 120 */     return do_layout;
/*     */   }
/*     */   
/*     */ 
/*     */   private void releaseOldImages()
/*     */   {
/*     */     ImageLoader imageLoader;
/*     */     
/*     */     Iterator iter;
/* 129 */     if (this.imageIDstoDispose.size() > 0) {
/* 130 */       imageLoader = ImageLoader.getInstance();
/*     */       
/* 132 */       for (iter = this.imageIDstoDispose.iterator(); iter.hasNext();) {
/* 133 */         String id = (String)iter.next();
/* 134 */         imageLoader.releaseImage(id);
/* 135 */         iter.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void update0(MainStatusBar.CLabelPadding label) {
/* 141 */     label.setText(this.text);
/* 142 */     label.setToolTipText(this.tooltip);
/* 143 */     if (this.check_scaled_image) {
/* 144 */       this.check_scaled_image = false;
/* 145 */       if (this.scaled_image != null) {
/* 146 */         this.scaled_image.dispose();
/* 147 */         this.scaled_image = null;
/*     */       }
/* 149 */       if ((this.original_image != null) && (Utils.adjustPXForDPIRequired(this.original_image))) {
/* 150 */         this.scaled_image = Utils.adjustPXForDPI(label.getDisplay(), this.original_image);
/*     */       }
/*     */     }
/* 153 */     label.setImage(this.image_enabled ? this.scaled_image : this.scaled_image == null ? this.original_image : null);
/* 154 */     label.setVisible(this.is_visible);
/*     */     
/* 156 */     releaseOldImages();
/*     */     
/* 158 */     MenuItem[] items = MenuItemManager.getInstance().getAllAsArray(this.menu_context.context);
/* 159 */     if (((items.length > 0 ? 1 : 0) & (this.menu == null ? 1 : 0)) != 0) {
/* 160 */       this.menu = new Menu(label);
/* 161 */       label.setMenu(this.menu);
/*     */       
/* 163 */       MenuBuildUtils.addMaintenanceListenerForMenu(this.menu, new MenuBuildUtils.MenuBuilder()
/*     */       {
/*     */         public void buildMenu(Menu menu, MenuEvent menuEvent) {
/* 166 */           MenuItem[] items = MenuItemManager.getInstance().getAllAsArray(UISWTStatusEntryImpl.this.menu_context.context);
/* 167 */           MenuBuildUtils.addPluginMenuItems(items, menu, true, true, MenuBuildUtils.BASIC_MENU_ITEM_CONTROLLER);
/*     */         }
/*     */         
/*     */ 
/*     */       });
/*     */     }
/* 173 */     else if ((this.menu != null) && (items.length == 0)) {
/* 174 */       label.setMenu(null);
/* 175 */       if (!this.menu.isDisposed()) this.menu.dispose();
/* 176 */       this.menu = null;
/*     */     }
/*     */     
/* 179 */     this.needs_update = false;
/*     */   }
/*     */   
/*     */   void onClick() {
/* 183 */     UISWTStatusEntryListener listener0 = this.listener;
/* 184 */     if (listener0 != null) this.listener.entryClicked(this);
/*     */   }
/*     */   
/*     */   public void destroy() {
/*     */     try {
/* 189 */       this.this_mon.enter();
/* 190 */       this.is_visible = false;
/* 191 */       this.listener = null;
/* 192 */       this.original_image = null;
/* 193 */       this.needs_disposing = true;
/* 194 */       this.is_destroyed = true;
/*     */       
/*     */ 
/* 197 */       MenuItemManager.getInstance().removeAllMenuItems(this.menu_context.context);
/*     */     }
/*     */     finally {
/* 200 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setImage(int image_id)
/*     */   {
/* 208 */     if (this.imageID != null) {
/* 209 */       this.imageIDstoDispose.add(this.imageID);
/*     */     }
/*     */     
/* 212 */     switch (image_id) {
/*     */     case 3: 
/* 214 */       this.imageID = "greenled";
/* 215 */       break;
/*     */     case 1: 
/* 217 */       this.imageID = "redled";
/* 218 */       break;
/*     */     case 2: 
/* 220 */       this.imageID = "yellowled";
/* 221 */       break;
/*     */     default: 
/* 223 */       this.imageID = "grayled";
/*     */     }
/*     */     
/* 226 */     ImageLoader imageLoader = ImageLoader.getInstance();
/* 227 */     setImage(imageLoader.getImage(this.imageID));
/*     */   }
/*     */   
/*     */   public void setImage(Image image) {
/* 231 */     checkDestroyed();
/* 232 */     this.this_mon.enter();
/* 233 */     if (image != this.original_image) {
/* 234 */       this.needs_layout = true;
/* 235 */       this.check_scaled_image = true;
/* 236 */       this.original_image = image;
/*     */     }
/* 238 */     this.needs_update = true;
/* 239 */     this.this_mon.exit();
/*     */   }
/*     */   
/*     */   public void setImageEnabled(boolean enabled) {
/* 243 */     checkDestroyed();
/* 244 */     this.this_mon.enter();
/* 245 */     if (enabled != this.image_enabled) {
/* 246 */       this.needs_layout = true;
/*     */     }
/* 248 */     this.image_enabled = enabled;
/* 249 */     this.needs_update = true;
/* 250 */     this.this_mon.exit();
/*     */   }
/*     */   
/*     */   public void setListener(UISWTStatusEntryListener listener) {
/* 254 */     checkDestroyed();
/* 255 */     this.listener = listener;
/*     */   }
/*     */   
/*     */   public void setText(String text) {
/* 259 */     checkDestroyed();
/* 260 */     this.this_mon.enter();
/* 261 */     this.text = text;
/* 262 */     this.needs_update = true;
/* 263 */     this.this_mon.exit();
/*     */   }
/*     */   
/*     */   public void setTooltipText(String text) {
/* 267 */     checkDestroyed();
/* 268 */     this.this_mon.enter();
/* 269 */     this.tooltip = text;
/* 270 */     this.needs_update = true;
/* 271 */     this.this_mon.exit();
/*     */   }
/*     */   
/*     */   public void setVisible(boolean visible) {
/* 275 */     checkDestroyed();
/* 276 */     this.this_mon.enter();
/* 277 */     if (this.is_visible != visible) {
/* 278 */       this.needs_layout = true;
/*     */     }
/* 280 */     this.is_visible = visible;
/* 281 */     this.needs_update = true;
/* 282 */     this.this_mon.exit();
/*     */   }
/*     */   
/*     */   public void created(final MainStatusBar.CLabelPadding label) {
/* 286 */     final Listener click_listener = new Listener() {
/*     */       public void handleEvent(Event e) {
/* 288 */         UISWTStatusEntryImpl.this.onClick();
/*     */       }
/*     */       
/* 291 */     };
/* 292 */     Utils.execSWTThread(new AERunnable()
/*     */     {
/* 294 */       public void runSupport() { label.addListener(8, click_listener); } }, true);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/pluginsimpl/UISWTStatusEntryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */