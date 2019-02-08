/*    */ package org.gudy.azureus2.core3.stats.impl;
/*    */ 
/*    */ import com.aelitis.azureus.core.AzureusCore;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import org.gudy.azureus2.core3.stats.StatsWriterStreamer;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StatsWriterStreamerImpl
/*    */   implements StatsWriterStreamer
/*    */ {
/*    */   protected final AzureusCore core;
/*    */   
/*    */   public StatsWriterStreamerImpl(AzureusCore _core)
/*    */   {
/* 46 */     this.core = _core;
/*    */   }
/*    */   
/*    */ 
/*    */ 
/*    */ 
/*    */   public void write(OutputStream output_stream)
/*    */     throws IOException
/*    */   {
/* 55 */     new StatsWriterImpl(this.core).write(output_stream);
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/stats/impl/StatsWriterStreamerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */