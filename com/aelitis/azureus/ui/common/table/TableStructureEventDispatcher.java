/*     */ package com.aelitis.azureus.ui.common.table;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ 
/*     */ public class TableStructureEventDispatcher
/*     */   implements TableStructureModificationListener
/*     */ {
/*  42 */   private static Map<String, TableStructureEventDispatcher> instances = new HashMap();
/*     */   
/*  44 */   private static AEMonitor class_mon = new AEMonitor("TableStructureEventDispatcher:class");
/*     */   
/*     */ 
/*     */   private CopyOnWriteList listeners;
/*     */   
/*  49 */   private AEMonitor listeners_mon = new AEMonitor("TableStructureEventDispatcher:L");
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private TableStructureEventDispatcher()
/*     */   {
/*  56 */     this.listeners = new CopyOnWriteList(2);
/*     */   }
/*     */   
/*     */   public static TableStructureEventDispatcher getInstance(String tableID) {
/*     */     try {
/*  61 */       class_mon.enter();
/*     */       
/*  63 */       TableStructureEventDispatcher instance = (TableStructureEventDispatcher)instances.get(tableID);
/*  64 */       if (instance == null) {
/*  65 */         instance = new TableStructureEventDispatcher();
/*  66 */         instances.put(tableID, instance);
/*     */       }
/*  68 */       return instance;
/*     */     }
/*     */     finally {
/*  71 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void addListener(TableStructureModificationListener listener) {
/*     */     try {
/*  77 */       this.listeners_mon.enter();
/*     */       
/*  79 */       if (!this.listeners.contains(listener)) {
/*  80 */         this.listeners.add(listener);
/*     */       }
/*     */     }
/*     */     finally
/*     */     {
/*  85 */       this.listeners_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void removeListener(TableStructureModificationListener listener) {
/*     */     try {
/*  91 */       this.listeners_mon.enter();
/*     */       
/*  93 */       this.listeners.remove(listener);
/*     */     }
/*     */     finally {
/*  96 */       this.listeners_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */   public void tableStructureChanged(boolean columnAddedOrRemoved, Class forPluginDataSourceType)
/*     */   {
/* 102 */     Iterator iter = this.listeners.iterator();
/* 103 */     while (iter.hasNext()) {
/* 104 */       TableStructureModificationListener listener = (TableStructureModificationListener)iter.next();
/*     */       try {
/* 106 */         listener.tableStructureChanged(columnAddedOrRemoved, forPluginDataSourceType);
/*     */       } catch (Throwable e) {
/* 108 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public void columnSizeChanged(TableColumnCore tableColumn, int diff) {
/* 114 */     Iterator iter = this.listeners.iterator();
/* 115 */     while (iter.hasNext()) {
/* 116 */       TableStructureModificationListener listener = (TableStructureModificationListener)iter.next();
/* 117 */       listener.columnSizeChanged(tableColumn, diff);
/*     */     }
/*     */   }
/*     */   
/*     */   public void columnInvalidate(TableColumnCore tableColumn)
/*     */   {
/* 123 */     Iterator iter = this.listeners.iterator();
/* 124 */     while (iter.hasNext()) {
/* 125 */       TableStructureModificationListener listener = (TableStructureModificationListener)iter.next();
/* 126 */       listener.columnInvalidate(tableColumn);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void cellInvalidate(TableColumnCore tableColumn, Object data_source)
/*     */   {
/* 133 */     Iterator iter = this.listeners.iterator();
/* 134 */     while (iter.hasNext()) {
/* 135 */       TableStructureModificationListener listener = (TableStructureModificationListener)iter.next();
/* 136 */       listener.cellInvalidate(tableColumn, data_source);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void columnOrderChanged(int[] iPositions)
/*     */   {
/* 144 */     Iterator iter = this.listeners.iterator();
/* 145 */     while (iter.hasNext()) {
/* 146 */       TableStructureModificationListener listener = (TableStructureModificationListener)iter.next();
/* 147 */       listener.columnOrderChanged(iPositions);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/table/TableStructureEventDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */