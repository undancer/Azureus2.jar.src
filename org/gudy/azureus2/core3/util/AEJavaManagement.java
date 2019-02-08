/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AEJavaManagement
/*     */ {
/*     */   private static ThreadStuff thread_stuff;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static MemoryStuff memory_stuff;
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void initialise()
/*     */   {
/*     */     try
/*     */     {
/*  33 */       thread_stuff = (ThreadStuff)Class.forName("org.gudy.azureus2.core3.util.jman.AEThreadMonitor").newInstance();
/*     */ 
/*     */     }
/*     */     catch (ClassNotFoundException e) {}catch (Throwable e)
/*     */     {
/*     */ 
/*  39 */       e.printStackTrace();
/*     */     }
/*     */     try
/*     */     {
/*  43 */       memory_stuff = (MemoryStuff)Class.forName("org.gudy.azureus2.core3.util.jman.AEMemoryMonitor").newInstance();
/*     */ 
/*     */     }
/*     */     catch (ClassNotFoundException e) {}catch (Throwable e)
/*     */     {
/*     */ 
/*  49 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static long getThreadCPUTime()
/*     */   {
/*  56 */     if (thread_stuff == null)
/*     */     {
/*  58 */       return 0L;
/*     */     }
/*     */     
/*  61 */     return thread_stuff.getThreadCPUTime();
/*     */   }
/*     */   
/*     */ 
/*     */   public static void dumpThreads()
/*     */   {
/*  67 */     if (thread_stuff == null)
/*     */     {
/*  69 */       return;
/*     */     }
/*     */     
/*  72 */     thread_stuff.dumpThreads();
/*     */   }
/*     */   
/*     */ 
/*     */   public static long getMaxHeapMB()
/*     */   {
/*  78 */     if (memory_stuff == null)
/*     */     {
/*  80 */       return 0L;
/*     */     }
/*     */     
/*  83 */     return memory_stuff.getMaxHeapMB();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long getJVMLongOption(String[] options, String prefix)
/*     */   {
/*  91 */     long value = -1L;
/*     */     
/*  93 */     for (String option : options) {
/*     */       try
/*     */       {
/*  96 */         if (option.startsWith(prefix))
/*     */         {
/*  98 */           String val = option.substring(prefix.length());
/*     */           
/* 100 */           value = decodeJVMLong(val);
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 104 */         Debug.out("Failed to process option '" + option + "'", e);
/*     */       }
/*     */     }
/*     */     
/* 108 */     return value;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String[] setJVMLongOption(String[] options, String prefix, long val)
/*     */   {
/* 117 */     String new_option = prefix + encodeJVMLong(val);
/*     */     
/* 119 */     for (int i = 0; i < options.length; i++)
/*     */     {
/* 121 */       String option = options[i];
/*     */       
/* 123 */       if (option.startsWith(prefix))
/*     */       {
/* 125 */         options[i] = new_option;
/*     */         
/* 127 */         new_option = null;
/*     */       }
/*     */     }
/*     */     
/* 131 */     if (new_option != null)
/*     */     {
/* 133 */       String[] new_options = new String[options.length + 1];
/*     */       
/* 135 */       System.arraycopy(options, 0, new_options, 0, options.length);
/*     */       
/* 137 */       new_options[options.length] = new_option;
/*     */       
/* 139 */       options = new_options;
/*     */     }
/*     */     
/* 142 */     return options;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static long decodeJVMLong(String val)
/*     */     throws Exception
/*     */   {
/* 151 */     long mult = 1L;
/*     */     
/* 153 */     char last_char = Character.toLowerCase(val.charAt(val.length() - 1));
/*     */     
/* 155 */     if (!Character.isDigit(last_char))
/*     */     {
/* 157 */       val = val.substring(0, val.length() - 1);
/*     */       
/* 159 */       if (last_char == 'k')
/*     */       {
/* 161 */         mult = 1024L;
/*     */       }
/* 163 */       else if (last_char == 'm')
/*     */       {
/* 165 */         mult = 1048576L;
/*     */       }
/* 167 */       else if (last_char == 'g')
/*     */       {
/* 169 */         mult = 1073741824L;
/*     */       }
/*     */       else
/*     */       {
/* 173 */         throw new Exception("Invalid size unit '" + last_char + "'");
/*     */       }
/*     */     }
/*     */     
/* 177 */     return Long.parseLong(val) * mult;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String encodeJVMLong(long val)
/*     */   {
/* 184 */     if (val < 1024L)
/*     */     {
/* 186 */       return String.valueOf(val);
/*     */     }
/*     */     
/* 189 */     val /= 1024L;
/*     */     
/* 191 */     if (val < 1024L)
/*     */     {
/* 193 */       return String.valueOf(val) + "k";
/*     */     }
/*     */     
/* 196 */     val /= 1024L;
/*     */     
/* 198 */     if (val < 1024L)
/*     */     {
/* 200 */       return String.valueOf(val) + "m";
/*     */     }
/*     */     
/* 203 */     val /= 1024L;
/*     */     
/* 205 */     return String.valueOf(val) + "g";
/*     */   }
/*     */   
/*     */   public static abstract interface MemoryStuff
/*     */   {
/*     */     public abstract long getMaxHeapMB();
/*     */   }
/*     */   
/*     */   public static abstract interface ThreadStuff
/*     */   {
/*     */     public abstract long getThreadCPUTime();
/*     */     
/*     */     public abstract void dumpThreads();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/AEJavaManagement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */