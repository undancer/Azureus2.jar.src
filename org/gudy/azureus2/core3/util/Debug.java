/*     */ package org.gudy.azureus2.core3.util;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.ConnectException;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
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
/*     */ public class Debug
/*     */ {
/*  31 */   private static final boolean STOP_AT_INITIALIZER = System.getProperty("debug.stacktrace.full", "0").equals("0");
/*     */   
/*     */   private static final AEDiagnosticsLogger diag_logger;
/*     */   
/*     */   static
/*     */   {
/*  37 */     AEDiagnosticsLogger temp_diag_logger = null;
/*     */     try {
/*  39 */       temp_diag_logger = AEDiagnostics.getLogger("debug");
/*     */       
/*  41 */       temp_diag_logger.setForced(true);
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*  46 */     diag_logger = temp_diag_logger;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void out(String _debug_message)
/*     */   {
/*  56 */     out(_debug_message, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void out(Throwable _exception)
/*     */   {
/*  65 */     out("", _exception);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void outNoStack(String str)
/*     */   {
/*  72 */     outNoStack(str, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void outNoStack(String str, boolean stderr)
/*     */   {
/*  80 */     diagLoggerLogAndOut("DEBUG::" + new Date(SystemTime.getCurrentTime()).toString() + "  " + str, stderr);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void outDiagLoggerOnly(String str)
/*     */   {
/*  87 */     diagLoggerLog(str);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void out(String _debug_msg, Throwable _exception)
/*     */   {
/*  96 */     if (((_exception instanceof ConnectException)) && (_exception.getMessage().startsWith("No route to host"))) {
/*  97 */       diagLoggerLog(_exception.toString());
/*  98 */       return;
/*     */     }
/* 100 */     if ((_exception instanceof UnknownHostException)) {
/* 101 */       diagLoggerLog(_exception.toString());
/* 102 */       return;
/*     */     }
/* 104 */     String header = "DEBUG::";
/* 105 */     header = header + new Date(SystemTime.getCurrentTime()).toString() + "::";
/*     */     
/*     */ 
/*     */ 
/* 109 */     String trace_trace_tail = null;
/*     */     try
/*     */     {
/* 112 */       throw new Exception();
/*     */     }
/*     */     catch (Exception e) {
/* 115 */       StackTraceElement[] st = e.getStackTrace();
/*     */       
/* 117 */       StackTraceElement first_line = st[2];
/* 118 */       String className = first_line.getClassName() + "::";
/* 119 */       String methodName = first_line.getMethodName() + "::";
/* 120 */       int lineNumber = first_line.getLineNumber();
/*     */       
/* 122 */       trace_trace_tail = getCompressedStackTrace(e, 3, 200, false);
/*     */       
/*     */ 
/* 125 */       diagLoggerLogAndOut(header + className + methodName + lineNumber + ":", true);
/* 126 */       if (_debug_msg.length() > 0) {
/* 127 */         diagLoggerLogAndOut("  " + _debug_msg, true);
/*     */       }
/* 129 */       if (trace_trace_tail != null) {
/* 130 */         diagLoggerLogAndOut("    " + trace_trace_tail, true);
/*     */       }
/* 132 */       if (_exception != null)
/* 133 */         diagLoggerLogAndOut(_exception);
/*     */     }
/*     */   }
/*     */   
/*     */   public static String getLastCaller() {
/* 138 */     return getLastCaller(0);
/*     */   }
/*     */   
/*     */   public static String getLastCaller(int numToGoBackFurther) {
/*     */     try {
/* 143 */       throw new Exception();
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */ 
/* 149 */       StackTraceElement[] st = e.getStackTrace();
/* 150 */       if ((st == null) || (st.length == 0))
/* 151 */         return "??";
/* 152 */       if (st.length > 3 + numToGoBackFurther) {
/* 153 */         return st[(3 + numToGoBackFurther)].toString();
/*     */       }
/* 155 */       return st[(st.length - 1)].toString();
/*     */     }
/*     */   }
/*     */   
/*     */   public static String getLastCallerShort() {
/* 160 */     return getLastCallerShort(0);
/*     */   }
/*     */   
/*     */   public static String getLastCallerShort(int numToGoBackFurther) {
/*     */     try {
/* 165 */       throw new Exception();
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */ 
/* 171 */       StackTraceElement[] st = e.getStackTrace();
/*     */       
/* 173 */       if ((st == null) || (st.length == 0))
/* 174 */         return "??";
/* 175 */       StackTraceElement ste; StackTraceElement ste; if (st.length > 3 + numToGoBackFurther) {
/* 176 */         ste = st[(3 + numToGoBackFurther)];
/*     */       } else {
/* 178 */         ste = st[(st.length - 1)];
/*     */       }
/* 180 */       String fn = ste.getFileName();
/*     */       
/* 182 */       if (fn != null) {
/* 183 */         return fn + ":" + ste.getLineNumber();
/*     */       }
/* 185 */       return ste.toString();
/*     */     }
/*     */   }
/*     */   
/*     */   public static void outStackTrace()
/*     */   {
/* 191 */     diagLoggerLogAndOut(getStackTrace(1), false);
/*     */   }
/*     */   
/*     */   private static String getStackTrace(int endNumToSkip) {
/* 195 */     String sStackTrace = "";
/*     */     try {
/* 197 */       throw new Exception();
/*     */     }
/*     */     catch (Exception e) {
/* 200 */       StackTraceElement[] st = e.getStackTrace();
/* 201 */       for (int i = 1; i < st.length - endNumToSkip; i++) {
/* 202 */         if (!st[i].getMethodName().endsWith("StackTrace"))
/* 203 */           sStackTrace = sStackTrace + st[i].toString() + "\n";
/*     */       }
/* 205 */       if (e.getCause() != null)
/* 206 */         sStackTrace = sStackTrace + "\tCaused By: " + getStackTrace(e.getCause()) + "\n";
/*     */     }
/* 208 */     return sStackTrace;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void killAWTThreads()
/*     */   {
/* 214 */     ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
/*     */     
/* 216 */     killAWTThreads(threadGroup);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private static String getCompressedStackTrace(Throwable t, int frames_to_skip)
/*     */   {
/* 224 */     return getCompressedStackTrace(t, frames_to_skip, 200);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getCompressedStackTrace(Throwable t, int frames_to_skip, int iMaxLines)
/*     */   {
/* 234 */     return getCompressedStackTrace(t, frames_to_skip, iMaxLines, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getCompressedStackTrace(Throwable t, int frames_to_skip, int iMaxLines, boolean showErrString)
/*     */   {
/* 245 */     StringBuilder sbStackTrace = new StringBuilder(showErrString ? t.toString() + "; " : "");
/* 246 */     StackTraceElement[] st = t.getStackTrace();
/*     */     
/* 248 */     if (iMaxLines < 0) {
/* 249 */       iMaxLines = st.length + iMaxLines;
/* 250 */       if (iMaxLines < 0) {
/* 251 */         iMaxLines = 1;
/*     */       }
/*     */     }
/* 254 */     int iMax = Math.min(st.length, iMaxLines + frames_to_skip);
/* 255 */     for (int i = frames_to_skip; i < iMax; i++)
/*     */     {
/* 257 */       if (i > frames_to_skip) {
/* 258 */         sbStackTrace.append(", ");
/*     */       }
/*     */       
/* 261 */       String classname = st[i].getClassName();
/* 262 */       String cnShort = classname.substring(classname.lastIndexOf(".") + 1);
/*     */       
/* 264 */       if (Constants.IS_CVS_VERSION) {
/* 265 */         if ((STOP_AT_INITIALIZER) && (st[i].getClassName().equals("com.aelitis.azureus.ui.swt.Initializer")))
/*     */         {
/*     */ 
/* 268 */           sbStackTrace.append("Initializer");
/* 269 */           break;
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 275 */         sbStackTrace.append(st[i].getMethodName());
/*     */         
/* 277 */         sbStackTrace.append(" (");
/* 278 */         sbStackTrace.append(st[i].getFileName());
/* 279 */         sbStackTrace.append(':');
/* 280 */         sbStackTrace.append(st[i].getLineNumber());
/* 281 */         sbStackTrace.append(')');
/*     */       } else {
/* 283 */         sbStackTrace.append(cnShort);
/* 284 */         sbStackTrace.append("::");
/* 285 */         sbStackTrace.append(st[i].getMethodName());
/* 286 */         sbStackTrace.append("::");
/* 287 */         sbStackTrace.append(st[i].getLineNumber());
/*     */       }
/*     */     }
/*     */     
/* 291 */     Throwable cause = t.getCause();
/*     */     
/* 293 */     if (cause != null) {
/* 294 */       sbStackTrace.append("\n\tCaused By: ");
/* 295 */       sbStackTrace.append(getCompressedStackTrace(cause, 0));
/*     */     }
/*     */     
/* 298 */     return sbStackTrace.toString();
/*     */   }
/*     */   
/*     */   public static String getStackTrace(boolean bCompressed, boolean bIncludeSelf) {
/* 302 */     return getStackTrace(bCompressed, bIncludeSelf, bIncludeSelf ? 0 : 1, 200);
/*     */   }
/*     */   
/*     */   public static String getStackTrace(boolean bCompressed, boolean bIncludeSelf, int iNumLinesToSkip, int iMaxLines)
/*     */   {
/* 307 */     if (bCompressed) {
/* 308 */       return getCompressedStackTrace(bIncludeSelf ? 2 + iNumLinesToSkip : 3 + iNumLinesToSkip, iMaxLines);
/*     */     }
/*     */     
/*     */ 
/* 312 */     return getStackTrace(1);
/*     */   }
/*     */   
/*     */   private static String getCompressedStackTrace(int frames_to_skip, int iMaxLines)
/*     */   {
/* 317 */     String trace_trace_tail = null;
/*     */     try
/*     */     {
/* 320 */       throw new Exception();
/*     */     } catch (Exception e) {
/* 322 */       trace_trace_tail = getCompressedStackTrace(e, frames_to_skip, iMaxLines, false);
/*     */     }
/*     */     
/* 325 */     return trace_trace_tail;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void killAWTThreads(ThreadGroup threadGroup)
/*     */   {
/* 332 */     Thread[] threadList = new Thread[threadGroup.activeCount()];
/*     */     
/* 334 */     threadGroup.enumerate(threadList);
/*     */     
/* 336 */     for (int i = 0; i < threadList.length; i++)
/*     */     {
/* 338 */       Thread t = threadList[i];
/*     */       
/* 340 */       if (t != null)
/*     */       {
/* 342 */         String name = t.getName();
/*     */         
/* 344 */         if (name.startsWith("AWT"))
/*     */         {
/* 346 */           out("Interrupting thread '".concat(t.toString()).concat("'"));
/*     */           
/* 348 */           t.interrupt();
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 353 */     if (threadGroup.getParent() != null)
/*     */     {
/* 355 */       killAWTThreads(threadGroup.getParent());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void dumpThreads(String name)
/*     */   {
/* 363 */     out(name + ":");
/*     */     
/* 365 */     ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
/*     */     
/* 367 */     dumpThreads(threadGroup, "\t");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void dumpThreads(ThreadGroup threadGroup, String indent)
/*     */   {
/* 375 */     Thread[] threadList = new Thread[threadGroup.activeCount()];
/*     */     
/* 377 */     threadGroup.enumerate(threadList);
/*     */     
/* 379 */     for (int i = 0; i < threadList.length; i++)
/*     */     {
/* 381 */       Thread t = threadList[i];
/*     */       
/* 383 */       if (t != null)
/*     */       {
/* 385 */         out(indent.concat("active thread = ").concat(t.toString()).concat(", daemon = ").concat(String.valueOf(t.isDaemon())));
/*     */       }
/*     */     }
/*     */     
/* 389 */     if (threadGroup.getParent() != null)
/*     */     {
/* 391 */       dumpThreads(threadGroup.getParent(), indent + "\t");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void dumpThreadsLoop(final String name)
/*     */   {
/* 399 */     new AEThread("Thread Dumper")
/*     */     {
/*     */       public void runSupport()
/*     */       {
/*     */         for (;;)
/*     */         {
/* 405 */           Debug.dumpThreads(name);
/*     */           try
/*     */           {
/* 408 */             Thread.sleep(5000L);
/*     */           } catch (Throwable e) {
/* 410 */             Debug.printStackTrace(e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */   public static void dumpSystemProperties()
/*     */   {
/* 420 */     out("System Properties:");
/*     */     
/* 422 */     Properties props = System.getProperties();
/*     */     
/* 424 */     Iterator it = props.keySet().iterator();
/*     */     
/* 426 */     while (it.hasNext())
/*     */     {
/* 428 */       String name = (String)it.next();
/*     */       
/* 430 */       out("\t".concat(name).concat(" = '").concat(props.get(name).toString()).concat("'"));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getNestedExceptionMessage(Throwable e)
/*     */   {
/* 438 */     String last_message = "";
/*     */     
/* 440 */     while (e != null)
/*     */     {
/*     */       String this_message;
/*     */       String this_message;
/* 444 */       if ((e instanceof UnknownHostException))
/*     */       {
/* 446 */         this_message = "Unknown host " + e.getMessage();
/*     */       } else { String this_message;
/* 448 */         if ((e instanceof FileNotFoundException))
/*     */         {
/* 450 */           this_message = "File not found: " + e.getMessage();
/*     */         }
/*     */         else
/*     */         {
/* 454 */           this_message = e.getMessage();
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 461 */       if (this_message == null)
/*     */       {
/* 463 */         this_message = e.getClass().getName();
/*     */         
/* 465 */         int pos = this_message.lastIndexOf(".");
/*     */         
/* 467 */         this_message = this_message.substring(pos + 1).trim();
/*     */       }
/*     */       
/* 470 */       if ((this_message.length() > 0) && (!last_message.contains(this_message)))
/*     */       {
/* 472 */         last_message = last_message + (last_message.length() == 0 ? "" : ", ") + this_message;
/*     */       }
/*     */       
/* 475 */       e = e.getCause();
/*     */     }
/*     */     
/* 478 */     return last_message;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static boolean containsException(Throwable error, Class<? extends Throwable> cla)
/*     */   {
/* 486 */     if (error == null)
/*     */     {
/* 488 */       return false;
/*     */     }
/* 490 */     if (cla.isInstance(error))
/*     */     {
/* 492 */       return true;
/*     */     }
/*     */     
/* 495 */     return containsException(error.getCause(), cla);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getNestedExceptionMessageAndStack(Throwable e)
/*     */   {
/* 502 */     return getNestedExceptionMessage(e) + ", " + getCompressedStackTrace(e, 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getCompressedStackTraceSkipFrames(int frames_to_skip)
/*     */   {
/* 509 */     return getCompressedStackTrace(new Throwable(), frames_to_skip + 1, 200, false);
/*     */   }
/*     */   
/*     */ 
/*     */   public static String getCompressedStackTrace()
/*     */   {
/* 515 */     return getCompressedStackTrace(new Throwable(), 1, 200, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String getCompressedStackTrace(int iMaxLines)
/*     */   {
/* 526 */     return getCompressedStackTrace(new Throwable(), 1, iMaxLines, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static String getExceptionMessage(Throwable e)
/*     */   {
/* 533 */     String message = e.getMessage();
/*     */     
/* 535 */     if ((message == null) || (message.length() == 0))
/*     */     {
/* 537 */       message = e.getClass().getName();
/*     */       
/* 539 */       int pos = message.lastIndexOf(".");
/*     */       
/* 541 */       message = message.substring(pos + 1);
/*     */     }
/* 543 */     else if ((e instanceof ClassNotFoundException))
/*     */     {
/* 545 */       if (!message.toLowerCase().contains("found"))
/*     */       {
/* 547 */         message = "Class " + message + " not found";
/*     */       }
/*     */     }
/*     */     
/* 551 */     return message;
/*     */   }
/*     */   
/*     */   public static void printStackTrace(Throwable e) {
/* 555 */     printStackTrace(e, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void printStackTrace(Throwable e, Object context)
/*     */   {
/* 564 */     if (((e instanceof ConnectException)) && (e.getMessage().startsWith("No route to host"))) {
/* 565 */       diagLoggerLog(e.toString());
/* 566 */       return;
/*     */     }
/* 568 */     if ((e instanceof UnknownHostException)) {
/* 569 */       diagLoggerLog(e.toString());
/* 570 */       return;
/*     */     }
/* 572 */     String header = "DEBUG::";
/* 573 */     header = header + new Date(SystemTime.getCurrentTime()).toString() + "::";
/* 574 */     String className = "?::";
/* 575 */     String methodName = "?::";
/* 576 */     int lineNumber = -1;
/*     */     try
/*     */     {
/* 579 */       throw new Exception();
/*     */     } catch (Exception f) {
/* 581 */       StackTraceElement[] st = f.getStackTrace();
/*     */       
/* 583 */       for (int i = 1; i < st.length; i++) {
/* 584 */         StackTraceElement first_line = st[i];
/* 585 */         className = first_line.getClassName() + "::";
/* 586 */         methodName = first_line.getMethodName() + "::";
/* 587 */         lineNumber = first_line.getLineNumber();
/*     */         
/*     */ 
/*     */ 
/* 591 */         if (!className.contains(".logging.")) { if (!className.endsWith(".Debug::")) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 601 */       diagLoggerLogAndOut(header + className + methodName + lineNumber + ":", true);
/*     */       try
/*     */       {
/* 604 */         ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */         
/* 606 */         PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos));
/*     */         
/* 608 */         if (context != null) { pw.print("  ");pw.println(context); }
/* 609 */         pw.print("  ");
/* 610 */         e.printStackTrace(pw);
/*     */         
/* 612 */         pw.close();
/*     */         
/* 614 */         String stack = baos.toString();
/*     */         
/* 616 */         diagLoggerLogAndOut(stack, true);
/*     */       }
/*     */       catch (Throwable ignore) {
/* 619 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   public static String getStackTrace(Throwable e) {
/* 625 */     try { ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */       
/* 627 */       PrintWriter pw = new PrintWriter(new OutputStreamWriter(baos));
/*     */       
/* 629 */       e.printStackTrace(pw);
/*     */       
/* 631 */       pw.close();
/*     */       
/* 633 */       return baos.toString();
/*     */     }
/*     */     catch (Throwable ignore) {}
/* 636 */     return "";
/*     */   }
/*     */   
/*     */   private static void diagLoggerLog(String str)
/*     */   {
/* 641 */     if (diag_logger == null) {
/* 642 */       System.out.println(str);
/*     */     } else {
/* 644 */       diag_logger.log(str);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void diagLoggerLogAndOut(String str, boolean stderr)
/*     */   {
/* 656 */     if (diag_logger == null) {
/* 657 */       if (stderr) {
/* 658 */         System.err.println(str);
/*     */       } else {
/* 660 */         System.out.println(str);
/*     */       }
/*     */     } else {
/* 663 */       diag_logger.logAndOut(str, stderr);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   private static void diagLoggerLogAndOut(Throwable e)
/*     */   {
/* 673 */     if (diag_logger == null) {
/* 674 */       e.printStackTrace();
/*     */     } else {
/* 676 */       diag_logger.logAndOut(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static String secretFileName(String key)
/*     */   {
/* 685 */     if (key == null) {
/* 686 */       return "";
/*     */     }
/* 688 */     String sep = File.separator;
/* 689 */     String regex = "([\\" + sep + "]?[^\\" + sep + "]{0,3}+)[^\\" + sep + "]*";
/*     */     
/*     */ 
/* 692 */     String secretName = key.replaceAll(regex, "$1");
/* 693 */     int iExtensionPos = key.lastIndexOf(".");
/* 694 */     if (iExtensionPos >= 0)
/* 695 */       secretName = secretName + key.substring(iExtensionPos);
/* 696 */     return secretName;
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 700 */     System.out.println(secretFileName("c:\\temp\\hello there.txt"));
/* 701 */     System.out.println(secretFileName("hello there.txt"));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/Debug.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */