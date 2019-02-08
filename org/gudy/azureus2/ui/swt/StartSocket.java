/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.impl.AzureusCoreSingleInstanceClient;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.net.Socket;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ public class StartSocket
/*     */ {
/*     */   private final String[] args;
/*     */   
/*     */   public StartSocket(String[] _args)
/*     */   {
/*  37 */     this.args = _args;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean sendArgs()
/*     */   {
/*  46 */     Socket sck = null;
/*  47 */     PrintWriter pw = null;
/*     */     try {
/*  49 */       String msg = "StartSocket: passing startup args to already-running Azureus java process listening on [127.0.0.1: " + Constants.INSTANCE_PORT + "]";
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  54 */       System.out.println(msg);
/*     */       
/*  56 */       sck = new Socket("127.0.0.1", Constants.INSTANCE_PORT);
/*     */       
/*     */ 
/*     */ 
/*  60 */       pw = new PrintWriter(new OutputStreamWriter(sck.getOutputStream(), "UTF8"));
/*     */       
/*  62 */       buffer = new StringBuilder("Azureus Start Server Access;args;");
/*     */       
/*  64 */       for (int i = 0; i < this.args.length; i++) {
/*  65 */         String arg = this.args[i].replaceAll("&", "&&").replaceAll(";", "&;");
/*  66 */         buffer.append(arg);
/*  67 */         buffer.append(';');
/*     */       }
/*     */       
/*  70 */       pw.println(buffer.toString());
/*  71 */       pw.flush();
/*     */       
/*  73 */       if (!AzureusCoreSingleInstanceClient.receiveReply(sck))
/*     */       {
/*  75 */         return 0;
/*     */       }
/*     */       
/*  78 */       return 1;
/*     */     } catch (Exception e) {
/*     */       StringBuilder buffer;
/*  81 */       e.printStackTrace();
/*  82 */       Debug.printStackTrace(e);
/*  83 */       return 0;
/*     */     }
/*     */     finally {
/*     */       try {
/*  87 */         if (pw != null) pw.close();
/*     */       }
/*     */       catch (Exception e) {}
/*     */       try
/*     */       {
/*  92 */         if (sck != null) sck.close();
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */   }
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/* 100 */     new StartSocket(args);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/StartSocket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */