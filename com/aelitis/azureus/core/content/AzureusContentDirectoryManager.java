/*    */ package com.aelitis.azureus.core.content;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class AzureusContentDirectoryManager
/*    */ {
/* 28 */   private static final List directories = new ArrayList();
/*    */   
/*    */ 
/*    */ 
/*    */   public static void registerDirectory(AzureusContentDirectory directory)
/*    */   {
/* 34 */     synchronized (directories)
/*    */     {
/* 36 */       directories.add(directory);
/*    */     }
/*    */   }
/*    */   
/*    */   /* Error */
/*    */   public static AzureusContentDirectory[] getDirectories()
/*    */   {
/*    */     // Byte code:
/*    */     //   0: getstatic 39	com/aelitis/azureus/core/content/AzureusContentDirectoryManager:directories	Ljava/util/List;
/*    */     //   3: dup
/*    */     //   4: astore_0
/*    */     //   5: monitorenter
/*    */     //   6: getstatic 39	com/aelitis/azureus/core/content/AzureusContentDirectoryManager:directories	Ljava/util/List;
/*    */     //   9: getstatic 39	com/aelitis/azureus/core/content/AzureusContentDirectoryManager:directories	Ljava/util/List;
/*    */     //   12: invokeinterface 42 1 0
/*    */     //   17: anewarray 21	com/aelitis/azureus/core/content/AzureusContentDirectory
/*    */     //   20: invokeinterface 44 2 0
/*    */     //   25: checkcast 20	[Lcom/aelitis/azureus/core/content/AzureusContentDirectory;
/*    */     //   28: checkcast 20	[Lcom/aelitis/azureus/core/content/AzureusContentDirectory;
/*    */     //   31: aload_0
/*    */     //   32: monitorexit
/*    */     //   33: areturn
/*    */     //   34: astore_1
/*    */     //   35: aload_0
/*    */     //   36: monitorexit
/*    */     //   37: aload_1
/*    */     //   38: athrow
/*    */     // Line number table:
/*    */     //   Java source line #43	-> byte code offset #0
/*    */     //   Java source line #45	-> byte code offset #6
/*    */     //   Java source line #46	-> byte code offset #34
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	signature
/*    */     //   4	32	0	Ljava/lang/Object;	Object
/*    */     //   34	4	1	localObject1	Object
/*    */     // Exception table:
/*    */     //   from	to	target	type
/*    */     //   6	33	34	finally
/*    */     //   34	37	34	finally
/*    */   }
/*    */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/content/AzureusContentDirectoryManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */