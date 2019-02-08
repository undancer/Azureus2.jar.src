/*     */ package org.gudy.azureus2.core3.util.jar;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogAlert;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AETemporaryFileHandler;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
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
/*     */ public class AEJarSigner2
/*     */ {
/*     */   protected static Class JarSigner_class;
/*     */   protected final String keystore_name;
/*     */   protected final String keystore_password;
/*     */   protected final String alias;
/*     */   
/*     */   public AEJarSigner2(String _alias, String _keystore_name, String _keystore_password)
/*     */   {
/*  48 */     this.alias = _alias;
/*  49 */     this.keystore_name = _keystore_name;
/*  50 */     this.keystore_password = _keystore_password;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void loadJarSigner()
/*     */     throws IOException
/*     */   {
/*  60 */     String manual_tools_dir = COConfigurationManager.getStringParameter("Security.JAR.tools.dir");
/*     */     File tools_dir;
/*  62 */     File tools_dir; if (manual_tools_dir.length() == 0)
/*     */     {
/*  64 */       String java_home = System.getProperty("java.home");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*  69 */       File jh = new File(java_home);
/*     */       
/*  71 */       if (jh.getName().equalsIgnoreCase("jre"))
/*     */       {
/*  73 */         jh = jh.getParentFile();
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*     */ 
/*  79 */         String dir_name = jh.getName();
/*     */         
/*  81 */         if (dir_name.startsWith("jre"))
/*     */         {
/*  83 */           dir_name = "jdk" + dir_name.substring(3);
/*     */           
/*  85 */           jh = new File(jh.getParentFile(), dir_name);
/*     */         }
/*     */       }
/*     */       
/*  89 */       tools_dir = new File(jh, "lib");
/*     */     }
/*     */     else
/*     */     {
/*  93 */       tools_dir = new File(manual_tools_dir);
/*     */     }
/*     */     
/*  96 */     File tools_jar = new File(tools_dir, "tools.jar");
/*     */     
/*     */ 
/*     */ 
/* 100 */     if (tools_jar.exists())
/*     */     {
/*     */       try {
/* 103 */         ClassLoader cl = new URLClassLoader(new URL[] { tools_jar.toURL() }, AEJarSigner2.class.getClassLoader());
/*     */         
/* 105 */         JarSigner_class = cl.loadClass("sun.security.tools.JarSigner");
/*     */       }
/*     */       catch (Throwable e) {
/* 108 */         Logger.logTextResource(new LogAlert(false, 3, "Security.jar.signfail"), new String[] { e.getMessage() });
/*     */         
/*     */ 
/*     */ 
/* 112 */         Debug.printStackTrace(e);
/*     */         
/* 114 */         throw new IOException("JAR signing fails: " + e.getMessage());
/*     */       }
/*     */     }
/*     */     else {
/* 118 */       Logger.logTextResource(new LogAlert(false, 3, "Security.jar.tools_not_found"), new String[] { tools_dir.toString() });
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 123 */       throw new IOException("JAR signing fails: tools.jar not found");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void signJarFile(File input_file)
/*     */     throws IOException
/*     */   {
/* 133 */     if (JarSigner_class == null)
/*     */     {
/* 135 */       loadJarSigner();
/*     */     }
/*     */     
/* 138 */     PrintStream old_err = null;
/* 139 */     PrintStream old_out = null;
/*     */     
/* 141 */     String failure_msg = null;
/*     */     try
/*     */     {
/* 144 */       Object jar_signer = JarSigner_class.newInstance();
/*     */       
/* 146 */       String[] args = { "-keystore", this.keystore_name, "-storepass", this.keystore_password, input_file.toString(), this.alias };
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 155 */       old_err = System.err;
/* 156 */       old_out = System.out;
/*     */       
/* 158 */       ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */       
/* 160 */       PrintStream ps = new PrintStream(baos);
/*     */       
/* 162 */       System.setErr(ps);
/* 163 */       System.setOut(ps);
/*     */       try
/*     */       {
/* 166 */         JarSigner_class.getMethod("run", new Class[] { String[].class }).invoke(jar_signer, new Object[] { args });
/*     */ 
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*     */ 
/* 172 */         ps.close();
/*     */         
/* 174 */         String err_msg = baos.toString();
/*     */         
/* 176 */         if (err_msg.length() > 0)
/*     */         {
/* 178 */           failure_msg = err_msg;
/*     */         }
/*     */         else
/*     */         {
/* 182 */           Debug.printStackTrace(e);
/*     */           
/* 184 */           failure_msg = e.getMessage();
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 189 */       Debug.printStackTrace(e);
/*     */       
/* 191 */       failure_msg = e.getMessage();
/*     */     }
/*     */     finally
/*     */     {
/* 195 */       if (old_err != null)
/*     */       {
/* 197 */         System.setErr(old_err);
/* 198 */         System.setOut(old_out);
/*     */       }
/*     */     }
/*     */     
/* 202 */     if (failure_msg != null)
/*     */     {
/* 204 */       Debug.out("JAR signing fails '" + failure_msg + "'");
/*     */       
/* 206 */       Logger.logTextResource(new LogAlert(false, 3, "Security.jar.signfail"), new String[] { failure_msg });
/*     */       
/*     */ 
/*     */ 
/* 210 */       throw new IOException("JAR signing fails: " + failure_msg);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void signJarFile(File file, OutputStream os)
/*     */     throws IOException
/*     */   {
/* 221 */     signJarFile(file);
/*     */     
/* 223 */     FileInputStream fis = null;
/*     */     try
/*     */     {
/* 226 */       fis = new FileInputStream(file);
/*     */       
/* 228 */       FileUtil.copyFile(file, os, false); return;
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/* 233 */         if (fis != null) fis.close();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 237 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void signJarStream(InputStream is, OutputStream os)
/*     */     throws IOException
/*     */   {
/* 249 */     File temp_file = AETemporaryFileHandler.createTempFile();
/*     */     
/* 251 */     FileOutputStream fos = null;
/*     */     
/*     */     try
/*     */     {
/* 255 */       byte[] buffer = new byte[' '];
/*     */       
/* 257 */       fos = new FileOutputStream(temp_file);
/*     */       
/*     */       for (;;)
/*     */       {
/* 261 */         int len = is.read(buffer);
/*     */         
/* 263 */         if (len <= 0) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/* 268 */         fos.write(buffer, 0, len);
/*     */       }
/*     */       
/* 271 */       fos.close();
/*     */       
/* 273 */       fos = null;
/*     */       
/* 275 */       signJarFile(temp_file, os);
/*     */     }
/*     */     finally
/*     */     {
/*     */       try {
/* 280 */         is.close();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 284 */         Debug.printStackTrace(e);
/*     */       }
/*     */       
/* 287 */       if (fos != null) {
/*     */         try
/*     */         {
/* 290 */           fos.close();
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 294 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/* 297 */       temp_file.delete();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/jar/AEJarSigner2.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */