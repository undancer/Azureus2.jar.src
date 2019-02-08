/*    */ package org.gudy.azureus2.ui.swt.views.table;
/*    */ 
/*    */ import com.aelitis.azureus.ui.common.table.TableGroupRowRunner;
/*    */ import com.aelitis.azureus.ui.common.table.TableView;
/*    */ import org.eclipse.swt.widgets.Event;
/*    */ import org.eclipse.swt.widgets.Listener;
/*    */ import org.gudy.azureus2.core3.util.AERunnable;
/*    */ import org.gudy.azureus2.ui.swt.Utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class TableSelectedRowsListener
/*    */   extends TableGroupRowRunner
/*    */   implements Listener
/*    */ {
/*    */   private final TableView<?> tv;
/*    */   private final boolean getOffSWT;
/*    */   protected Event event;
/*    */   
/*    */   public TableSelectedRowsListener(TableView<?> impl, boolean getOffSWT)
/*    */   {
/* 42 */     this.tv = impl;
/* 43 */     this.getOffSWT = getOffSWT;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public TableSelectedRowsListener(TableView<?> impl)
/*    */   {
/* 51 */     this.tv = impl;
/* 52 */     this.getOffSWT = true;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public final void handleEvent(Event e)
/*    */   {
/* 64 */     this.event = e;
/* 65 */     if (this.getOffSWT) {
/* 66 */       Utils.getOffOfSWTThread(new AERunnable() {
/*    */         public void runSupport() {
/* 68 */           TableSelectedRowsListener.this.tv.runForSelectedRows(TableSelectedRowsListener.this);
/*    */         }
/*    */       });
/*    */     } else {
/* 72 */       this.tv.runForSelectedRows(this);
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/views/table/TableSelectedRowsListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */