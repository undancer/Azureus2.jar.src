/*     */ package org.gudy.azureus2.platform.unix;
/*     */ 
/*     */ import com.aelitis.azureus.core.impl.AzureusCoreSingleInstanceClient;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.Constants;
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
/*     */ 
/*     */ public class ScriptBeforeStartup
/*     */ {
/*     */   private static PrintStream sysout;
/*     */   private static Object display;
/*     */   
/*     */   public static void main(String[] args)
/*     */   {
/*  46 */     System.setProperty("transitory.startup", "1");
/*     */     
/*     */ 
/*     */ 
/*  50 */     sysout = System.out;
/*     */     try {
/*  52 */       System.setOut(new PrintStream(new FileOutputStream("/dev/stderr")));
/*     */     }
/*     */     catch (FileNotFoundException e) {}
/*     */     
/*  56 */     String mi_str = System.getProperty("MULTI_INSTANCE");
/*  57 */     boolean mi = (mi_str != null) && (mi_str.equalsIgnoreCase("true"));
/*     */     
/*  59 */     if (!mi) {
/*  60 */       boolean argsSent = new AzureusCoreSingleInstanceClient().sendArgs(args, 500);
/*  61 */       if (argsSent)
/*     */       {
/*  63 */         String msg = "Passing startup args to already-running " + Constants.APP_NAME + " java process listening on [127.0.0.1: " + Constants.INSTANCE_PORT + "]";
/*  64 */         log(msg);
/*  65 */         sysout.println("exit");
/*     */         
/*  67 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*  73 */     String scriptAfterShutdown = COConfigurationManager.getStringParameter("scriptaftershutdown", null);
/*     */     
/*     */ 
/*  76 */     COConfigurationManager.removeParameter("scriptaftershutdown.exit");
/*  77 */     COConfigurationManager.removeParameter("scriptaftershutdown");
/*  78 */     COConfigurationManager.save();
/*  79 */     if (scriptAfterShutdown != null) {
/*  80 */       log("Script after " + Constants.APP_NAME + " shutdown did not run.. running now");
/*     */       
/*  82 */       sysout.println(scriptAfterShutdown);
/*     */       
/*  84 */       if (!scriptAfterShutdown.contains("$0"))
/*     */       {
/*  86 */         sysout.println("echo \"Restarting Azureus..\"");
/*  87 */         sysout.println("$0\n");
/*     */       }
/*     */       
/*  90 */       sysout.println("exit");
/*     */       
/*  92 */       return;
/*     */     }
/*     */     
/*  95 */     String moz = getNewGreDir();
/*     */     
/*  97 */     if (moz != null) {
/*  98 */       String s = "export MOZILLA_FIVE_HOME=\"" + moz + "\"\n" + "if [ \"$LD_LIBRARY_PATH x\" = \" x\" ] ; then\n" + "\texport LD_LIBRARY_PATH=$MOZILLA_FIVE_HOME;\n" + "else\n" + "\texport LD_LIBRARY_PATH=$MOZILLA_FIVE_HOME:$LD_LIBRARY_PATH\n" + "fi\n";
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 103 */       sysout.println(s);
/* 104 */       log("setting LD_LIBRARY_PATH to: $LD_LIBRARY_PATH");
/* 105 */       log("setting MOZILLA_FIVE_HOME to: $MOZILLA_FIVE_HOME");
/*     */     } else {
/* 107 */       log("Usable browser found");
/*     */     }
/*     */   }
/*     */   
/*     */   public static String getNewGreDir()
/*     */   {
/* 113 */     if (canOpenBrowser()) {
/* 114 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 120 */     String grePath = null;
/* 121 */     String[] confList = { "/etc/gre64.conf", "/etc/gre.d/gre64.conf", "/etc/gre.conf", "/etc/gre.d/gre.conf", "/etc/gre.d/xulrunner.conf", "/etc/gre.d/libxul0d.conf" };
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 130 */     log("Auto-scanning for GRE/XULRunner.  You can skip this by appending the GRE path to LD_LIBRARY_PATH and setting MOZILLA_FIVE_HOME.");
/*     */     try {
/* 132 */       Pattern pat = Pattern.compile("GRE_PATH=(.*)", 2);
/* 133 */       for (int i = 0; i < confList.length; i++) {
/* 134 */         File file = new File(confList[i]);
/* 135 */         if ((file.isFile()) && (file.canRead())) {
/* 136 */           log("  checking " + file + " for GRE_PATH");
/* 137 */           String fileText = FileUtil.readFileAsString(file, 16384);
/* 138 */           if (fileText != null) {
/* 139 */             Matcher matcher = pat.matcher(fileText);
/* 140 */             if (matcher.find()) {
/* 141 */               String possibleGrePath = matcher.group(1);
/* 142 */               if (isValidGrePath(new File(possibleGrePath))) {
/* 143 */                 grePath = possibleGrePath;
/* 144 */                 break;
/*     */               }
/*     */             }
/*     */           }
/*     */         } }
/*     */       FileFilter ffIsPossibleDir;
/*     */       Iterator iter;
/* 151 */       if (grePath == null) {
/* 152 */         ArrayList possibleDirs = new ArrayList();
/* 153 */         File libDir = new File("/usr");
/* 154 */         libDir.listFiles(new FileFilter() {
/*     */           public boolean accept(File pathname) {
/* 156 */             if (pathname.getName().startsWith("lib")) {
/* 157 */               this.val$possibleDirs.add(pathname);
/*     */             }
/* 159 */             return false;
/*     */           }
/* 161 */         });
/* 162 */         possibleDirs.add(new File("/usr/local"));
/* 163 */         possibleDirs.add(new File("/opt"));
/*     */         
/* 165 */         String[] possibleDirNames = { "mozilla", "firefox", "seamonkey", "xulrunner" };
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 172 */         ffIsPossibleDir = new FileFilter() {
/*     */           public boolean accept(File pathname) {
/* 174 */             String name = pathname.getName().toLowerCase();
/* 175 */             for (int i = 0; i < this.val$possibleDirNames.length; i++) {
/* 176 */               if (name.startsWith(this.val$possibleDirNames[i])) {
/* 177 */                 return true;
/*     */               }
/*     */             }
/* 180 */             return false;
/*     */           }
/*     */         };
/*     */         
/* 184 */         for (iter = possibleDirs.iterator(); iter.hasNext();) {
/* 185 */           File dir = (File)iter.next();
/*     */           
/* 187 */           File[] possibleFullDirs = dir.listFiles(ffIsPossibleDir);
/*     */           
/* 189 */           for (int i = 0; i < possibleFullDirs.length; i++) {
/* 190 */             log("  checking " + possibleFullDirs[i] + " for GRE");
/* 191 */             if (isValidGrePath(possibleFullDirs[i])) {
/* 192 */               grePath = possibleFullDirs[i].getAbsolutePath();
/* 193 */               break;
/*     */             }
/*     */           }
/* 196 */           if (grePath != null) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 202 */       if (grePath != null) {
/* 203 */         log("GRE found at " + grePath + ".");
/* 204 */         System.setProperty("org.eclipse.swt.browser.XULRunnerPath", grePath);
/*     */       }
/*     */     } catch (Throwable t) {
/* 207 */       log("Error trying to find suitable GRE: " + Debug.getNestedExceptionMessage(t));
/*     */       
/* 209 */       grePath = null;
/*     */     }
/*     */     
/* 212 */     if (!canOpenBrowser()) {
/* 213 */       log("Can't create browser.  Will try to set LD_LIBRARY_PATH and hope " + Constants.APP_NAME + " has better luck.");
/*     */     }
/*     */     
/*     */ 
/* 217 */     return grePath;
/*     */   }
/*     */   
/*     */   private static boolean canOpenBrowser() {
/*     */     try {
/* 222 */       Class claDisplay = Class.forName("org.eclipse.swt.widgets.Display");
/* 223 */       if (display != null) {
/* 224 */         display = claDisplay.newInstance();
/*     */       }
/* 226 */       Class claShell = Class.forName("org.eclipse.swt.widgets.Shell");
/* 227 */       Constructor shellConstruct = claShell.getConstructor(new Class[] { claDisplay });
/*     */       
/*     */ 
/* 230 */       Object shell = shellConstruct.newInstance(new Object[] { display });
/*     */       
/*     */ 
/*     */ 
/* 234 */       Class claBrowser = Class.forName("org.eclipse.swt.browser.Browser");
/* 235 */       Constructor[] constructors = claBrowser.getConstructors();
/* 236 */       for (int i = 0; i < constructors.length; i++) {
/* 237 */         if (constructors[i].getParameterTypes().length == 2) {
/* 238 */           Object browser = constructors[i].newInstance(new Object[] { shell, new Integer(0) });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 243 */           Method methSetUrl = claBrowser.getMethod("setUrl", new Class[] { String.class });
/*     */           
/*     */ 
/* 246 */           methSetUrl.invoke(browser, new Object[] { "about:blank" });
/*     */           
/*     */ 
/*     */ 
/* 250 */           break;
/*     */         }
/*     */       }
/* 253 */       Method methDisposeShell = claShell.getMethod("dispose", new Class[0]);
/* 254 */       methDisposeShell.invoke(shell, new Object[0]);
/*     */       
/* 256 */       return true;
/*     */     } catch (Throwable e) {
/* 258 */       log("Browser check failed with: " + Debug.getNestedExceptionMessage(e)); }
/* 259 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   private static boolean isValidGrePath(File dir)
/*     */   {
/* 265 */     if (!dir.isDirectory()) {
/* 266 */       return false;
/*     */     }
/*     */     
/* 269 */     if ((new File(dir, "components/libwidget_gtk.so").exists()) || (new File(dir, "libwidget_gtk.so").exists()))
/*     */     {
/* 271 */       log("\tCan not use GRE from " + dir + " as it's too old (GTK2 version required).");
/*     */       
/* 273 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 278 */     if ((new File(dir, "components/libwidget_gtk2.so").exists()) || (new File(dir, "libwidget_gtk2.so").exists()))
/*     */     {
/* 280 */       return true;
/*     */     }
/*     */     
/* 283 */     if ((!new File(dir, "components/libxpcom.so").exists()) && (!new File(dir, "libxpcom.so").exists()))
/*     */     {
/* 285 */       log("\tCan not use GRE from " + dir + " because it's missing libxpcom.so.");
/* 286 */       return false;
/*     */     }
/*     */     
/* 289 */     return true;
/*     */   }
/*     */   
/*     */   private static void log(String string) {
/* 293 */     sysout.println("echo \"" + string.replaceAll("\"", "\\\"") + "\"");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/platform/unix/ScriptBeforeStartup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */