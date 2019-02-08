/*    */ package com.aelitis.azureus.core.metasearch.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.metasearch.Engine;
/*    */ import com.aelitis.azureus.core.metasearch.ResultListener;
/*    */ import com.aelitis.azureus.core.metasearch.SearchException;
/*    */ import com.aelitis.azureus.core.metasearch.SearchParameter;
/*    */ import java.util.Map;
/*    */ import org.gudy.azureus2.core3.util.AEThread2;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SearchExecuter
/*    */ {
/*    */   private Map context;
/*    */   private ResultListener listener;
/*    */   
/*    */   public SearchExecuter(Map _context, ResultListener _listener)
/*    */   {
/* 43 */     this.context = _context;
/* 44 */     this.listener = _listener;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   public void search(final Engine engine, final SearchParameter[] searchParameters, final String headers, final int desired_max_matches)
/*    */   {
/* 54 */     new AEThread2("MetaSearch: " + engine.getName() + " runner", true)
/*    */     {
/*    */       public void run()
/*    */       {
/*    */         try
/*    */         {
/* 60 */           engine.search(searchParameters, SearchExecuter.this.context, desired_max_matches, -1, headers, SearchExecuter.this.listener);
/*    */         }
/*    */         catch (SearchException e) {}
/*    */       }
/*    */     }.start();
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/SearchExecuter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */