/*     */ package org.gudy.azureus2.core3.disk.impl;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfo;
/*     */ import org.gudy.azureus2.core3.disk.DiskManagerFileInfoSet;
/*     */ import org.gudy.azureus2.core3.download.DownloadManagerState;
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
/*     */ public class DiskManagerFileInfoSetImpl
/*     */   implements DiskManagerFileInfoSet
/*     */ {
/*     */   final DiskManagerFileInfoImpl[] files;
/*     */   final DiskManagerHelper diskManager;
/*     */   
/*     */   public DiskManagerFileInfoSetImpl(DiskManagerFileInfoImpl[] files, DiskManagerHelper dm)
/*     */   {
/*  36 */     this.files = files;
/*  37 */     this.diskManager = dm;
/*     */   }
/*     */   
/*     */   public DiskManagerFileInfo[] getFiles() {
/*  41 */     return this.files;
/*     */   }
/*     */   
/*     */   public int nbFiles() {
/*  45 */     return this.files.length;
/*     */   }
/*     */   
/*     */   public void setPriority(int[] toChange) {
/*  49 */     if (toChange.length != this.files.length) {
/*  50 */       throw new IllegalArgumentException("array length mismatches the number of files");
/*     */     }
/*  52 */     DownloadManagerState dmState = this.diskManager.getDownloadState();
/*     */     try
/*     */     {
/*  55 */       dmState.suppressStateSave(true);
/*     */       
/*     */ 
/*  58 */       for (int i = 0; i < this.files.length; i++) {
/*  59 */         if (toChange[i] != 0)
/*  60 */           this.files[i].setPriority(toChange[i]);
/*     */       }
/*     */     } finally {
/*  63 */       dmState.suppressStateSave(false);
/*     */     }
/*     */   }
/*     */   
/*     */   public void setSkipped(boolean[] toChange, boolean setSkipped) {
/*  68 */     if (toChange.length != this.files.length) {
/*  69 */       throw new IllegalArgumentException("array length mismatches the number of files");
/*     */     }
/*  71 */     DownloadManagerState dmState = this.diskManager.getDownloadState();
/*     */     try
/*     */     {
/*  74 */       dmState.suppressStateSave(true);
/*     */       
/*  76 */       if (!setSkipped)
/*     */       {
/*  78 */         String[] types = this.diskManager.getStorageTypes();
/*     */         
/*  80 */         boolean[] toLinear = new boolean[toChange.length];
/*  81 */         boolean[] toReorder = new boolean[toChange.length];
/*     */         
/*  83 */         int num_linear = 0;
/*  84 */         int num_reorder = 0;
/*     */         
/*  86 */         for (int i = 0; i < toChange.length; i++)
/*     */         {
/*  88 */           if (toChange[i] != 0)
/*     */           {
/*  90 */             int old_type = DiskManagerUtil.convertDMStorageTypeFromString(types[i]);
/*     */             
/*  92 */             if (old_type == 2)
/*     */             {
/*  94 */               toLinear[i] = true;
/*     */               
/*  96 */               num_linear++;
/*     */             }
/*  98 */             else if (old_type == 4)
/*     */             {
/* 100 */               toReorder[i] = true;
/*     */               
/* 102 */               num_reorder++;
/*     */             }
/*     */           }
/*     */         }
/*     */         
/* 107 */         if (num_linear > 0)
/*     */         {
/* 109 */           if (!Arrays.equals(toLinear, setStorageTypes(toLinear, 1))) {
/*     */             return;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/* 115 */         if (num_reorder > 0)
/*     */         {
/* 117 */           if (!Arrays.equals(toReorder, setStorageTypes(toReorder, 3))) {
/*     */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 123 */       for (int i = 0; i < this.files.length; i++) {
/* 124 */         if (toChange[i] != 0)
/*     */         {
/* 126 */           this.files[i].setSkippedInternal(setSkipped);
/* 127 */           this.diskManager.skippedFileSetChanged(this.files[i]);
/*     */         }
/*     */       }
/* 130 */       if (!setSkipped) {
/* 131 */         DiskManagerUtil.doFileExistenceChecks(this, toChange, this.diskManager.getDownloadState().getDownloadManager(), true);
/*     */       }
/*     */     } finally {
/* 134 */       dmState.suppressStateSave(false);
/*     */     }
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public boolean[] setStorageTypes(boolean[] toChange, int newStroageType)
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_1
/*     */     //   1: arraylength
/*     */     //   2: aload_0
/*     */     //   3: getfield 155	org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoSetImpl:files	[Lorg/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoImpl;
/*     */     //   6: arraylength
/*     */     //   7: if_icmpeq +13 -> 20
/*     */     //   10: new 89	java/lang/IllegalArgumentException
/*     */     //   13: dup
/*     */     //   14: ldc 4
/*     */     //   16: invokespecial 157	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
/*     */     //   19: athrow
/*     */     //   20: aload_0
/*     */     //   21: getfield 155	org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoSetImpl:files	[Lorg/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoImpl;
/*     */     //   24: arraylength
/*     */     //   25: ifne +7 -> 32
/*     */     //   28: iconst_0
/*     */     //   29: newarray <illegal type>
/*     */     //   31: areturn
/*     */     //   32: aload_0
/*     */     //   33: getfield 156	org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoSetImpl:diskManager	Lorg/gudy/azureus2/core3/disk/impl/DiskManagerHelper;
/*     */     //   36: invokeinterface 178 1 0
/*     */     //   41: astore_3
/*     */     //   42: aload_0
/*     */     //   43: getfield 155	org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoSetImpl:files	[Lorg/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoImpl;
/*     */     //   46: arraylength
/*     */     //   47: newarray <illegal type>
/*     */     //   49: astore 4
/*     */     //   51: aload_0
/*     */     //   52: getfield 156	org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoSetImpl:diskManager	Lorg/gudy/azureus2/core3/disk/impl/DiskManagerHelper;
/*     */     //   55: invokeinterface 180 1 0
/*     */     //   60: astore 5
/*     */     //   62: iload_2
/*     */     //   63: iconst_2
/*     */     //   64: if_icmpeq +8 -> 72
/*     */     //   67: iload_2
/*     */     //   68: iconst_4
/*     */     //   69: if_icmpne +11 -> 80
/*     */     //   72: ldc 2
/*     */     //   74: invokestatic 173	org/gudy/azureus2/core3/util/Debug:out	(Ljava/lang/String;)V
/*     */     //   77: aload 4
/*     */     //   79: areturn
/*     */     //   80: aload 5
/*     */     //   82: iconst_1
/*     */     //   83: invokeinterface 183 2 0
/*     */     //   88: iconst_0
/*     */     //   89: istore 6
/*     */     //   91: iload 6
/*     */     //   93: aload_0
/*     */     //   94: getfield 155	org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoSetImpl:files	[Lorg/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoImpl;
/*     */     //   97: arraylength
/*     */     //   98: if_icmpge +189 -> 287
/*     */     //   101: aload_1
/*     */     //   102: iload 6
/*     */     //   104: baload
/*     */     //   105: ifne +6 -> 111
/*     */     //   108: goto +173 -> 281
/*     */     //   111: aload_3
/*     */     //   112: iload 6
/*     */     //   114: aaload
/*     */     //   115: invokestatic 171	org/gudy/azureus2/core3/disk/impl/DiskManagerUtil:convertDMStorageTypeFromString	(Ljava/lang/String;)I
/*     */     //   118: istore 7
/*     */     //   120: iload_2
/*     */     //   121: iload 7
/*     */     //   123: if_icmpne +12 -> 135
/*     */     //   126: aload 4
/*     */     //   128: iload 6
/*     */     //   130: iconst_1
/*     */     //   131: bastore
/*     */     //   132: goto +149 -> 281
/*     */     //   135: aload_0
/*     */     //   136: getfield 155	org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoSetImpl:files	[Lorg/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoImpl;
/*     */     //   139: iload 6
/*     */     //   141: aaload
/*     */     //   142: astore 8
/*     */     //   144: aload 8
/*     */     //   146: invokevirtual 166	org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoImpl:getCacheFile	()Lcom/aelitis/azureus/core/diskmanager/cache/CacheFile;
/*     */     //   149: iload_2
/*     */     //   150: invokestatic 169	org/gudy/azureus2/core3/disk/impl/DiskManagerUtil:convertDMStorageTypeToCache	(I)I
/*     */     //   153: invokeinterface 177 2 0
/*     */     //   158: aload 4
/*     */     //   160: iload 6
/*     */     //   162: iconst_1
/*     */     //   163: bastore
/*     */     //   164: aload_3
/*     */     //   165: iload 6
/*     */     //   167: aload 8
/*     */     //   169: invokevirtual 166	org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoImpl:getCacheFile	()Lcom/aelitis/azureus/core/diskmanager/cache/CacheFile;
/*     */     //   172: invokeinterface 176 1 0
/*     */     //   177: invokestatic 170	org/gudy/azureus2/core3/disk/impl/DiskManagerUtil:convertCacheStorageTypeToString	(I)Ljava/lang/String;
/*     */     //   180: aastore
/*     */     //   181: goto +100 -> 281
/*     */     //   184: astore 9
/*     */     //   186: aload 9
/*     */     //   188: invokestatic 174	org/gudy/azureus2/core3/util/Debug:printStackTrace	(Ljava/lang/Throwable;)V
/*     */     //   191: aload_0
/*     */     //   192: getfield 156	org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoSetImpl:diskManager	Lorg/gudy/azureus2/core3/disk/impl/DiskManagerHelper;
/*     */     //   195: aload 8
/*     */     //   197: new 91	java/lang/StringBuilder
/*     */     //   200: dup
/*     */     //   201: invokespecial 159	java/lang/StringBuilder:<init>	()V
/*     */     //   204: ldc 3
/*     */     //   206: invokevirtual 162	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   209: aload 8
/*     */     //   211: iconst_1
/*     */     //   212: invokevirtual 167	org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoImpl:getFile	(Z)Ljava/io/File;
/*     */     //   215: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */     //   218: ldc 1
/*     */     //   220: invokevirtual 162	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   223: aload 9
/*     */     //   225: invokestatic 175	org/gudy/azureus2/core3/util/Debug:getNestedExceptionMessage	(Ljava/lang/Throwable;)Ljava/lang/String;
/*     */     //   228: invokevirtual 162	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   231: invokevirtual 160	java/lang/StringBuilder:toString	()Ljava/lang/String;
/*     */     //   234: invokeinterface 181 3 0
/*     */     //   239: aload_3
/*     */     //   240: iload 6
/*     */     //   242: aload 8
/*     */     //   244: invokevirtual 166	org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoImpl:getCacheFile	()Lcom/aelitis/azureus/core/diskmanager/cache/CacheFile;
/*     */     //   247: invokeinterface 176 1 0
/*     */     //   252: invokestatic 170	org/gudy/azureus2/core3/disk/impl/DiskManagerUtil:convertCacheStorageTypeToString	(I)Ljava/lang/String;
/*     */     //   255: aastore
/*     */     //   256: goto +31 -> 287
/*     */     //   259: astore 10
/*     */     //   261: aload_3
/*     */     //   262: iload 6
/*     */     //   264: aload 8
/*     */     //   266: invokevirtual 166	org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoImpl:getCacheFile	()Lcom/aelitis/azureus/core/diskmanager/cache/CacheFile;
/*     */     //   269: invokeinterface 176 1 0
/*     */     //   274: invokestatic 170	org/gudy/azureus2/core3/disk/impl/DiskManagerUtil:convertCacheStorageTypeToString	(I)Ljava/lang/String;
/*     */     //   277: aastore
/*     */     //   278: aload 10
/*     */     //   280: athrow
/*     */     //   281: iinc 6 1
/*     */     //   284: goto -193 -> 91
/*     */     //   287: aload 5
/*     */     //   289: ldc 5
/*     */     //   291: aload_3
/*     */     //   292: invokeinterface 185 3 0
/*     */     //   297: aload_0
/*     */     //   298: aload_1
/*     */     //   299: aload 5
/*     */     //   301: invokeinterface 184 1 0
/*     */     //   306: iconst_1
/*     */     //   307: invokestatic 172	org/gudy/azureus2/core3/disk/impl/DiskManagerUtil:doFileExistenceChecks	(Lorg/gudy/azureus2/core3/disk/DiskManagerFileInfoSet;[ZLorg/gudy/azureus2/core3/download/DownloadManager;Z)V
/*     */     //   310: aload 5
/*     */     //   312: iconst_0
/*     */     //   313: invokeinterface 183 2 0
/*     */     //   318: aload 5
/*     */     //   320: invokeinterface 182 1 0
/*     */     //   325: goto +23 -> 348
/*     */     //   328: astore 11
/*     */     //   330: aload 5
/*     */     //   332: iconst_0
/*     */     //   333: invokeinterface 183 2 0
/*     */     //   338: aload 5
/*     */     //   340: invokeinterface 182 1 0
/*     */     //   345: aload 11
/*     */     //   347: athrow
/*     */     //   348: aload 4
/*     */     //   350: areturn
/*     */     // Line number table:
/*     */     //   Java source line #140	-> byte code offset #0
/*     */     //   Java source line #141	-> byte code offset #10
/*     */     //   Java source line #142	-> byte code offset #20
/*     */     //   Java source line #143	-> byte code offset #28
/*     */     //   Java source line #145	-> byte code offset #32
/*     */     //   Java source line #147	-> byte code offset #42
/*     */     //   Java source line #148	-> byte code offset #51
/*     */     //   Java source line #150	-> byte code offset #62
/*     */     //   Java source line #152	-> byte code offset #72
/*     */     //   Java source line #153	-> byte code offset #77
/*     */     //   Java source line #157	-> byte code offset #80
/*     */     //   Java source line #159	-> byte code offset #88
/*     */     //   Java source line #161	-> byte code offset #101
/*     */     //   Java source line #162	-> byte code offset #108
/*     */     //   Java source line #164	-> byte code offset #111
/*     */     //   Java source line #165	-> byte code offset #120
/*     */     //   Java source line #167	-> byte code offset #126
/*     */     //   Java source line #168	-> byte code offset #132
/*     */     //   Java source line #171	-> byte code offset #135
/*     */     //   Java source line #174	-> byte code offset #144
/*     */     //   Java source line #175	-> byte code offset #158
/*     */     //   Java source line #181	-> byte code offset #164
/*     */     //   Java source line #182	-> byte code offset #181
/*     */     //   Java source line #176	-> byte code offset #184
/*     */     //   Java source line #177	-> byte code offset #186
/*     */     //   Java source line #178	-> byte code offset #191
/*     */     //   Java source line #181	-> byte code offset #239
/*     */     //   Java source line #159	-> byte code offset #281
/*     */     //   Java source line #185	-> byte code offset #287
/*     */     //   Java source line #187	-> byte code offset #297
/*     */     //   Java source line #190	-> byte code offset #310
/*     */     //   Java source line #191	-> byte code offset #318
/*     */     //   Java source line #192	-> byte code offset #325
/*     */     //   Java source line #190	-> byte code offset #328
/*     */     //   Java source line #191	-> byte code offset #338
/*     */     //   Java source line #194	-> byte code offset #348
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	351	0	this	DiskManagerFileInfoSetImpl
/*     */     //   0	351	1	toChange	boolean[]
/*     */     //   0	351	2	newStroageType	int
/*     */     //   41	251	3	types	String[]
/*     */     //   49	300	4	modified	boolean[]
/*     */     //   60	279	5	dm_state	DownloadManagerState
/*     */     //   89	193	6	i	int
/*     */     //   118	4	7	old_type	int
/*     */     //   142	123	8	file	DiskManagerFileInfoImpl
/*     */     //   184	40	9	e	Throwable
/*     */     //   259	20	10	localObject1	Object
/*     */     //   328	18	11	localObject2	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   144	164	184	java/lang/Throwable
/*     */     //   144	164	259	finally
/*     */     //   184	239	259	finally
/*     */     //   259	261	259	finally
/*     */     //   80	310	328	finally
/*     */     //   328	330	328	finally
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/disk/impl/DiskManagerFileInfoSetImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */