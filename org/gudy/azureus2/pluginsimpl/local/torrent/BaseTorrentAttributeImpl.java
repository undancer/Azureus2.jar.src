/*    */ package org.gudy.azureus2.pluginsimpl.local.torrent;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.gudy.azureus2.core3.util.Debug;
/*    */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*    */ import org.gudy.azureus2.plugins.torrent.TorrentAttributeEvent;
/*    */ import org.gudy.azureus2.plugins.torrent.TorrentAttributeListener;
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
/*    */ abstract class BaseTorrentAttributeImpl
/*    */   implements TorrentAttribute
/*    */ {
/*    */   private List listeners;
/*    */   
/*    */   protected BaseTorrentAttributeImpl()
/*    */   {
/* 34 */     this.listeners = new ArrayList();
/*    */   }
/*    */   
/*    */   public abstract String getName();
/*    */   
/* 39 */   public String[] getDefinedValues() { return new String[0]; }
/*    */   
/*    */   public void addDefinedValue(String name)
/*    */   {
/* 43 */     throw new RuntimeException("not supported");
/*    */   }
/*    */   
/*    */   public void removeDefinedValue(String name) {
/* 47 */     throw new RuntimeException("not supported");
/*    */   }
/*    */   
/*    */   public void addTorrentAttributeListener(TorrentAttributeListener l) {
/* 51 */     this.listeners.add(l);
/*    */   }
/*    */   
/*    */   public void removeTorrentAttributeListener(TorrentAttributeListener l) {
/* 55 */     this.listeners.remove(l);
/*    */   }
/*    */   
/*    */   protected List getTorrentAttributeListeners() {
/* 59 */     return this.listeners;
/*    */   }
/*    */   
/*    */   protected void notifyListeners(TorrentAttributeEvent ev) {
/* 63 */     Iterator itr = this.listeners.iterator();
/* 64 */     while (itr.hasNext()) {
/*    */       try {
/* 66 */         ((TorrentAttributeListener)itr.next()).event(ev);
/*    */       }
/*    */       catch (Throwable t) {
/* 69 */         Debug.printStackTrace(t);
/*    */       }
/*    */     }
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/torrent/BaseTorrentAttributeImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */