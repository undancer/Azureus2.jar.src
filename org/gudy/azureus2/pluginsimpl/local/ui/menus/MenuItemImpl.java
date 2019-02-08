/*     */ package org.gudy.azureus2.pluginsimpl.local.ui.menus;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.util.Iterator;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ui.Graphic;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuBuilder;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemFillListener;
/*     */ import org.gudy.azureus2.plugins.ui.menus.MenuItemListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.UIManagerImpl;
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
/*     */ public class MenuItemImpl
/*     */   implements MenuItem
/*     */ {
/*     */   private PluginInterface pi;
/*     */   private String sMenuID;
/*     */   private String sName;
/*  45 */   private int style = 1;
/*     */   
/*  47 */   private boolean enabled = true;
/*     */   
/*     */   private Object data;
/*     */   
/*     */   private Graphic graphic;
/*     */   
/*  53 */   private CopyOnWriteList listeners = new CopyOnWriteList(1);
/*  54 */   private CopyOnWriteList m_listeners = new CopyOnWriteList(1);
/*     */   
/*  56 */   private CopyOnWriteList fill_listeners = new CopyOnWriteList(1);
/*     */   
/*  58 */   private CopyOnWriteList children = new CopyOnWriteList();
/*     */   
/*  60 */   private MenuItemImpl parent = null;
/*     */   
/*  62 */   private String display_text = null;
/*     */   
/*  64 */   private boolean visible = true;
/*     */   
/*  66 */   private MenuContextImpl menu_context = null;
/*     */   private MenuBuilder builder;
/*     */   
/*     */   public MenuItemImpl(PluginInterface _pi, String menuID, String key)
/*     */   {
/*  71 */     this.pi = _pi;
/*  72 */     if (this.pi == null) {
/*  73 */       this.pi = PluginInitializer.getDefaultInterface();
/*     */     }
/*  75 */     this.sMenuID = menuID;
/*  76 */     this.sName = key;
/*     */   }
/*     */   
/*     */   public MenuItemImpl(MenuItemImpl ti, String key) {
/*  80 */     this.pi = ti.pi;
/*  81 */     this.parent = ti;
/*  82 */     this.parent.addChildMenuItem(this);
/*  83 */     this.sMenuID = this.parent.getMenuID();
/*  84 */     this.sName = key;
/*     */   }
/*     */   
/*     */   public String getResourceKey() {
/*  88 */     return this.sName;
/*     */   }
/*     */   
/*     */   public String getMenuID() {
/*  92 */     return this.sMenuID;
/*     */   }
/*     */   
/*     */   public int getStyle() {
/*  96 */     return this.style;
/*     */   }
/*     */   
/*     */   public void setStyle(int _style) {
/* 100 */     if ((this.style == 5) && (_style != 5)) {
/* 101 */       throw new RuntimeException("cannot revert menu style MenuItem object to another style");
/*     */     }
/*     */     
/* 104 */     this.style = _style;
/*     */   }
/*     */   
/*     */   public Object getData() {
/* 108 */     return this.data;
/*     */   }
/*     */   
/*     */   public void setData(Object _data) {
/* 112 */     this.data = _data;
/*     */   }
/*     */   
/*     */   public boolean isEnabled() {
/* 116 */     return this.enabled;
/*     */   }
/*     */   
/*     */   public void setEnabled(boolean _enabled) {
/* 120 */     this.enabled = _enabled;
/*     */   }
/*     */   
/*     */   public void setGraphic(Graphic _graphic) {
/* 124 */     this.graphic = _graphic;
/*     */   }
/*     */   
/*     */   public Graphic getGraphic() {
/* 128 */     return this.graphic;
/*     */   }
/*     */   
/*     */   public void invokeMenuWillBeShownListeners(Object target) {
/* 132 */     for (Iterator iter = this.fill_listeners.iterator(); iter.hasNext();) {
/*     */       try {
/* 134 */         MenuItemFillListener l = (MenuItemFillListener)iter.next();
/* 135 */         l.menuWillBeShown(this, target);
/*     */       } catch (Throwable e) {
/* 137 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void addFillListener(MenuItemFillListener listener) {
/* 143 */     this.fill_listeners.add(listener);
/*     */   }
/*     */   
/*     */   public void removeFillListener(MenuItemFillListener listener) {
/* 147 */     this.fill_listeners.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */   public void invokeListenersMulti(Object[] rows)
/*     */   {
/* 153 */     invokeListenersOnList(this.m_listeners, rows);
/* 154 */     if ((rows == null) || (rows.length == 0)) {
/* 155 */       invokeListenersSingle(null);
/* 156 */       return;
/*     */     }
/* 158 */     for (int i = 0; i < rows.length; i++) {
/* 159 */       invokeListenersSingle(rows[i]);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addMultiListener(MenuItemListener l) {
/* 164 */     this.m_listeners.add(l);
/*     */   }
/*     */   
/*     */   public void removeMultiListener(MenuItemListener l) {
/* 168 */     this.m_listeners.remove(l);
/*     */   }
/*     */   
/*     */   private void invokeListenersSingle(Object o) {
/* 172 */     invokeListenersOnList(this.listeners, o);
/*     */   }
/*     */   
/*     */   public void addListener(MenuItemListener l) {
/* 176 */     this.listeners.add(l);
/*     */   }
/*     */   
/*     */   public void removeListener(MenuItemListener l) {
/* 180 */     this.listeners.remove(l);
/*     */   }
/*     */   
/*     */   public MenuItem getParent() {
/* 184 */     return this.parent;
/*     */   }
/*     */   
/*     */   public MenuItem[] getItems() {
/* 188 */     if (this.style != 5) {
/* 189 */       return null;
/*     */     }
/* 191 */     return (MenuItem[])this.children.toArray(new MenuItem[this.children.size()]);
/*     */   }
/*     */   
/*     */   public MenuItem getItem(String key)
/*     */   {
/* 196 */     if (this.style != 5) {
/* 197 */       return null;
/*     */     }
/* 199 */     Iterator itr = this.children.iterator();
/* 200 */     MenuItem result = null;
/* 201 */     while (itr.hasNext()) {
/* 202 */       result = (MenuItem)itr.next();
/* 203 */       if (key.equals(result.getResourceKey())) {
/* 204 */         return result;
/*     */       }
/*     */     }
/* 207 */     return null;
/*     */   }
/*     */   
/*     */   private void addChildMenuItem(MenuItem child) {
/* 211 */     if (this.style != 5) {
/* 212 */       throw new RuntimeException("cannot add to non-container MenuItem");
/*     */     }
/* 214 */     this.children.add(child);
/*     */   }
/*     */   
/*     */   public String getText() {
/* 218 */     if (this.display_text == null) {
/* 219 */       return MessageText.getString(getResourceKey());
/*     */     }
/* 221 */     return this.display_text;
/*     */   }
/*     */   
/*     */   public void setText(String text) {
/* 225 */     this.display_text = text;
/*     */   }
/*     */   
/*     */   protected void invokeListenersOnList(CopyOnWriteList listeners_to_notify, Object target)
/*     */   {
/* 230 */     for (Iterator iter = listeners_to_notify.iterator(); iter.hasNext();) {
/*     */       try {
/* 232 */         MenuItemListener l = (MenuItemListener)iter.next();
/* 233 */         l.selected(this, target);
/*     */       } catch (Throwable e) {
/* 235 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   protected void removeWithEvents(int root_menu_event, int sub_menu_event) {
/* 241 */     removeAllChildItems();
/* 242 */     if (this.parent != null) {
/* 243 */       UIManagerImpl.fireEvent(this.pi, sub_menu_event, new Object[] { this.parent, this });
/* 244 */       this.parent.children.remove(this);
/* 245 */       this.parent = null;
/*     */     }
/*     */     else {
/* 248 */       UIManagerImpl.fireEvent(this.pi, root_menu_event, this);
/*     */     }
/* 250 */     this.data = null;
/* 251 */     this.graphic = null;
/* 252 */     this.listeners.clear();
/* 253 */     this.fill_listeners.clear();
/* 254 */     this.m_listeners.clear();
/*     */     
/* 256 */     if (this.menu_context != null) this.menu_context.dirty();
/*     */   }
/*     */   
/*     */   public void remove() {
/* 260 */     removeWithEvents(19, 20);
/*     */   }
/*     */   
/*     */   public void removeAllChildItems()
/*     */   {
/* 265 */     MenuItem[] children = getItems();
/* 266 */     if (children != null)
/* 267 */       for (int i = 0; i < children.length; i++) { children[i].remove();
/*     */       }
/*     */   }
/*     */   
/* 271 */   public boolean isVisible() { return this.visible; }
/* 272 */   public void setVisible(boolean visible) { this.visible = visible; }
/*     */   
/*     */   public boolean isSelected() {
/* 275 */     if ((this.style != 2) && (this.style != 3)) {
/* 276 */       throw new RuntimeException("Style is not STYLE_CHECK or STYLE_RADIO");
/*     */     }
/* 278 */     if (this.data == null) {
/* 279 */       throw new RuntimeException("Item is neither selected or deselected");
/*     */     }
/* 281 */     if (!(this.data instanceof Boolean)) {
/* 282 */       throw new RuntimeException("Invalid data assigned to menu item, should be boolean: " + this.data);
/*     */     }
/* 284 */     return ((Boolean)this.data).booleanValue();
/*     */   }
/*     */   
/*     */   public void setContext(MenuContextImpl context) {
/* 288 */     this.menu_context = context;
/*     */   }
/*     */   
/*     */   public void setSubmenuBuilder(MenuBuilder builder)
/*     */   {
/* 293 */     this.builder = builder;
/*     */   }
/*     */   
/*     */   public MenuBuilder getSubmenuBuilder() {
/* 297 */     return this.builder;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/menus/MenuItemImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */