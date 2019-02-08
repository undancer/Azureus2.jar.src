/*     */ package org.gudy.azureus2.pluginsimpl.local.ui.tables;
/*     */ 
/*     */ import com.aelitis.azureus.ui.common.table.TableColumnCore;
/*     */ import com.aelitis.azureus.ui.common.table.impl.TableColumnImpl;
/*     */ import org.gudy.azureus2.plugins.ui.UIInstance;
/*     */ import org.gudy.azureus2.plugins.ui.UIManagerListener;
/*     */ import org.gudy.azureus2.plugins.ui.UIRuntimeException;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumn;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableColumnCreationListener;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableContextMenuItem;
/*     */ import org.gudy.azureus2.plugins.ui.tables.TableManager;
/*     */ import org.gudy.azureus2.pluginsimpl.local.ui.UIManagerEventAdapter;
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
/*     */ public class TableManagerImpl
/*     */   implements TableManager
/*     */ {
/*     */   private UIManagerImpl ui_manager;
/*     */   
/*     */   public TableManagerImpl(UIManagerImpl _ui_manager)
/*     */   {
/*  42 */     this.ui_manager = _ui_manager;
/*     */   }
/*     */   
/*     */   public TableColumn createColumn(String tableID, String cellID) {
/*  46 */     final TableColumnImpl column = new TableColumnImpl(tableID, cellID);
/*     */     
/*  48 */     this.ui_manager.addUIListener(new UIManagerListener()
/*     */     {
/*     */       public void UIDetached(UIInstance instance) {}
/*     */       
/*     */       public void UIAttached(UIInstance instance) {
/*  53 */         UIManagerEventAdapter event = new UIManagerEventAdapter(TableManagerImpl.this.ui_manager.getPluginInterface(), 10, column);
/*     */         
/*     */ 
/*  56 */         UIManagerImpl.fireEvent(event);
/*     */ 
/*     */       }
/*     */       
/*     */ 
/*  61 */     });
/*  62 */     return column;
/*     */   }
/*     */   
/*     */   public void registerColumn(final Class forDataSourceType, final String cellID, final TableColumnCreationListener listener) {
/*  66 */     this.ui_manager.addUIListener(new UIManagerListener()
/*     */     {
/*     */       public void UIDetached(UIInstance instance) {}
/*     */       
/*     */       public void UIAttached(UIInstance instance) {
/*  71 */         UIManagerEventAdapter event = new UIManagerEventAdapter(TableManagerImpl.this.ui_manager.getPluginInterface(), 25, new Object[] { forDataSourceType, cellID, listener });
/*     */         
/*  73 */         UIManagerImpl.fireEvent(event);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void unregisterColumn(final Class forDataSourceType, final String cellID, final TableColumnCreationListener listener) {
/*  79 */     this.ui_manager.addUIListener(new UIManagerListener()
/*     */     {
/*     */       public void UIDetached(UIInstance instance) {}
/*     */       
/*     */       public void UIAttached(UIInstance instance) {
/*  84 */         UIManagerEventAdapter event = new UIManagerEventAdapter(TableManagerImpl.this.ui_manager.getPluginInterface(), 26, new Object[] { forDataSourceType, cellID, listener });
/*     */         
/*  86 */         UIManagerImpl.fireEvent(event);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public void addColumn(final TableColumn tableColumn) {
/*  92 */     if (!(tableColumn instanceof TableColumnCore)) {
/*  93 */       throw new UIRuntimeException("TableManager.addColumn(..) can only add columns created by createColumn(..)");
/*     */     }
/*     */     
/*  96 */     this.ui_manager.addUIListener(new UIManagerListener()
/*     */     {
/*     */       public void UIDetached(UIInstance instance) {}
/*     */       
/*     */       public void UIAttached(UIInstance instance) {
/* 101 */         UIManagerEventAdapter event = new UIManagerEventAdapter(TableManagerImpl.this.ui_manager.getPluginInterface(), 11, tableColumn);
/*     */         
/* 103 */         UIManagerImpl.fireEvent(event);
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */   public TableContextMenuItem addContextMenuItem(TableContextMenuItem parent, String resourceKey)
/*     */   {
/* 110 */     if (!(parent instanceof TableContextMenuItemImpl)) {
/* 111 */       throw new UIRuntimeException("parent must have been created by addContextMenuItem");
/*     */     }
/*     */     
/* 114 */     if (parent.getStyle() != 5) {
/* 115 */       throw new UIRuntimeException("parent menu item must have the menu style associated");
/*     */     }
/*     */     
/* 118 */     TableContextMenuItemImpl item = new TableContextMenuItemImpl((TableContextMenuItemImpl)parent, resourceKey);
/*     */     
/* 120 */     UIManagerImpl.fireEvent(this.ui_manager.getPluginInterface(), 14, new Object[] { item, parent });
/*     */     
/* 122 */     return item;
/*     */   }
/*     */   
/*     */   public TableContextMenuItem addContextMenuItem(String tableID, String resourceKey)
/*     */   {
/* 127 */     TableContextMenuItemImpl item = new TableContextMenuItemImpl(this.ui_manager.getPluginInterface(), tableID, resourceKey);
/*     */     
/*     */ 
/*     */ 
/* 131 */     UIManagerImpl.fireEvent(this.ui_manager.getPluginInterface(), 12, item);
/*     */     
/* 133 */     return item;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ui/tables/TableManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */