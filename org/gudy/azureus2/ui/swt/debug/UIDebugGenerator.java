/*     */ package org.gudy.azureus2.ui.swt.debug;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreOperation;
/*     */ import com.aelitis.azureus.core.AzureusCoreOperationTask;
/*     */ import com.aelitis.azureus.core.AzureusCoreRunningListener;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider;
/*     */ import com.aelitis.azureus.ui.UserPrompterResultListener;
/*     */ import java.io.File;
/*     */ import java.io.FileFilter;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.util.zip.ZipEntry;
/*     */ import java.util.zip.ZipOutputStream;
/*     */ import org.eclipse.swt.graphics.Device;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.Image;
/*     */ import org.eclipse.swt.graphics.ImageData;
/*     */ import org.eclipse.swt.graphics.ImageLoader;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FormAttachment;
/*     */ import org.eclipse.swt.layout.FormData;
/*     */ import org.eclipse.swt.layout.FormLayout;
/*     */ import org.eclipse.swt.layout.GridData;
/*     */ import org.eclipse.swt.layout.GridLayout;
/*     */ import org.eclipse.swt.widgets.Button;
/*     */ import org.eclipse.swt.widgets.Composite;
/*     */ import org.eclipse.swt.widgets.Control;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Event;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Listener;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.logging.LogEvent;
/*     */ import org.gudy.azureus2.core3.logging.LogIDs;
/*     */ import org.gudy.azureus2.core3.logging.Logger;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.SystemProperties;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.platform.PlatformManager;
/*     */ import org.gudy.azureus2.platform.PlatformManagerFactory;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager.FeatureDetails;
/*     */ import org.gudy.azureus2.plugins.utils.FeatureManager.Licence;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderException;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderListener;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginInitializer;
/*     */ import org.gudy.azureus2.ui.swt.Messages;
/*     */ import org.gudy.azureus2.ui.swt.Utils;
/*     */ import org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT;
/*     */ import org.gudy.azureus2.ui.swt.shells.MessageBoxShell;
/*     */ 
/*     */ public class UIDebugGenerator
/*     */ {
/*     */   public static void generate(String sourceRef, String additionalText)
/*     */   {
/*  74 */     GeneratedResults gr = generate(null, new DebugPrompterListener()
/*     */     {
/*     */       public boolean promptUser(UIDebugGenerator.GeneratedResults gr) {
/*  77 */         UIDebugGenerator.promptUser(false, gr);
/*  78 */         if (gr.message == null) {
/*  79 */           return false;
/*     */         }
/*  81 */         return true;
/*     */       }
/*     */     });
/*  84 */     if (gr != null) {
/*  85 */       AZ3Functions.provider az3 = AZ3Functions.getProvider();
/*     */       
/*  87 */       if ((az3 != null) && (gr.sendNow)) {
/*  88 */         FeatureManager.Licence fullLicence = null;
/*  89 */         PluginInterface pi = PluginInitializer.getDefaultInterface();
/*  90 */         FeatureManager featman = pi.getUtilities().getFeatureManager();
/*     */         
/*  92 */         if (featman != null) {
/*  93 */           FeatureManager.FeatureDetails[] featureDetails = featman.getFeatureDetails("dvdburn");
/*  94 */           if ((featureDetails != null) && (featureDetails.length > 0))
/*     */           {
/*     */ 
/*  97 */             FeatureManager.FeatureDetails bestDetails = featureDetails[0];
/*  98 */             fullLicence = bestDetails.getLicence();
/*     */           }
/*     */         }
/* 101 */         sendNow(gr, sourceRef, additionalText, fullLicence);
/*     */       }
/*     */       else {
/* 104 */         MessageBoxShell mb = new MessageBoxShell(65826, "UIDebugGenerator.complete", new String[] { gr.file.toString() });
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 109 */         mb.open(new UserPrompterResultListener() {
/*     */           public void prompterClosed(int result) {
/* 111 */             if (result == 32) {
/*     */               try {
/* 113 */                 PlatformManagerFactory.getPlatformManager().showFile(this.val$gr.file.getAbsolutePath());
/*     */               }
/*     */               catch (Exception e)
/*     */               {
/* 117 */                 e.printStackTrace();
/*     */               }
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public static void sendNow(GeneratedResults gr, String sourceRef, String additionalText, FeatureManager.Licence fullLicence)
/*     */   {
/* 129 */     AZ3Functions.provider az3 = AZ3Functions.getProvider();
/* 130 */     if (az3 == null) {
/* 131 */       return;
/*     */     }
/*     */     
/* 134 */     if ((gr.email != null) && (gr.email.length() > 0)) {
/* 135 */       additionalText = additionalText + "\n" + gr.email;
/*     */     }
/*     */     
/* 138 */     ResourceDownloaderFactory rdf = org.gudy.azureus2.pluginsimpl.local.utils.resourcedownloader.ResourceDownloaderFactoryImpl.getSingleton();
/* 139 */     String url = az3.getDefaultContentNetworkURL(27, new Object[] { "/debugSender.start", Boolean.valueOf(true) });
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 144 */     StringBuilder postData = new StringBuilder();
/*     */     
/* 146 */     if (fullLicence != null) {
/* 147 */       postData.append("license=");
/* 148 */       postData.append(UrlUtils.encode(fullLicence.getKey()));
/* 149 */       postData.append("&");
/*     */     }
/* 151 */     postData.append("message=");
/* 152 */     postData.append(UrlUtils.encode(gr.message));
/* 153 */     postData.append("&error=");
/* 154 */     postData.append(UrlUtils.encode(additionalText));
/* 155 */     postData.append("&sourceRef=");
/* 156 */     postData.append(UrlUtils.encode(sourceRef));
/* 157 */     if ((gr.email != null) && (gr.email.length() > 0)) {
/* 158 */       postData.append("&email=");
/* 159 */       postData.append(UrlUtils.encode(gr.email));
/*     */     }
/* 161 */     postData.append("&debug_zip=");
/*     */     try {
/* 163 */       byte[] fileArray = FileUtil.readFileAsByteArray(gr.file);
/* 164 */       postData.append(UrlUtils.encode(new String(org.gudy.bouncycastle.util.encoders.Base64.encode(fileArray))));
/*     */       
/* 166 */       ResourceDownloader rd = rdf.create(new java.net.URL(url), postData.toString());
/*     */       
/* 168 */       rd.addListener(new ResourceDownloaderListener()
/*     */       {
/*     */         public void reportPercentComplete(ResourceDownloader downloader, int percentage) {}
/*     */         
/*     */ 
/*     */ 
/*     */         public void reportAmountComplete(ResourceDownloader downloader, long amount) {}
/*     */         
/*     */ 
/*     */ 
/*     */         public void reportActivity(ResourceDownloader downloader, String activity) {}
/*     */         
/*     */ 
/*     */ 
/*     */         public void failed(ResourceDownloader downloader, ResourceDownloaderException e)
/*     */         {
/* 184 */           Debug.out(e);
/*     */         }
/*     */         
/*     */         public boolean completed(ResourceDownloader downloader, InputStream data)
/*     */         {
/*     */           try {
/* 190 */             int i = data.available();
/* 191 */             byte[] b = new byte[i];
/* 192 */             data.read(b);
/*     */           }
/*     */           catch (Throwable t) {}
/*     */           
/* 196 */           return true;
/*     */         }
/*     */         
/* 199 */       });
/* 200 */       rd.asyncDownload();
/*     */     } catch (Exception e) {
/* 202 */       Debug.out(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static GeneratedResults generate(File[] extraLogDirs, DebugPrompterListener debugPrompterListener)
/*     */   {
/* 212 */     Display display = Display.getCurrent();
/* 213 */     if (display == null) {
/* 214 */       return null;
/*     */     }
/*     */     
/* 217 */     Shell activeShell = display.getActiveShell();
/* 218 */     if (activeShell != null) {
/* 219 */       activeShell.setCursor(display.getSystemCursor(1));
/*     */     }
/*     */     
/*     */ 
/* 223 */     while (display.readAndDispatch()) {}
/*     */     
/*     */ 
/* 226 */     Shell[] shells = display.getShells();
/* 227 */     if ((shells == null) || (shells.length == 0)) {
/* 228 */       return null;
/*     */     }
/*     */     
/* 231 */     File path = new File(SystemProperties.getUserPath(), "debug");
/* 232 */     if (!path.isDirectory()) {
/* 233 */       path.mkdir();
/*     */     } else {
/*     */       try {
/* 236 */         File[] files = path.listFiles();
/* 237 */         for (int i = 0; i < files.length; i++) {
/* 238 */           files[i].delete();
/*     */         }
/*     */       }
/*     */       catch (Exception e) {}
/*     */     }
/*     */     
/* 244 */     for (int i = 0; i < shells.length; i++) {
/*     */       try {
/* 246 */         Shell shell = shells[i];
/* 247 */         Image image = null;
/*     */         
/* 249 */         if ((shell.isDisposed()) || (shell.isVisible()))
/*     */         {
/*     */ 
/*     */ 
/* 253 */           if ((shell.getData("class") instanceof ObfusticateShell)) {
/* 254 */             ObfusticateShell shellClass = (ObfusticateShell)shell.getData("class");
/*     */             try
/*     */             {
/* 257 */               image = shellClass.generateObfusticatedImage();
/*     */             } catch (Exception e) {
/* 259 */               Debug.out("Obfuscating shell " + shell, e);
/*     */             }
/*     */           }
/*     */           else {
/* 263 */             Rectangle clientArea = shell.getClientArea();
/* 264 */             image = new Image(display, clientArea.width, clientArea.height);
/*     */             
/* 266 */             GC gc = new GC(shell);
/*     */             try {
/* 268 */               gc.copyArea(image, clientArea.x, clientArea.y);
/*     */             } finally {
/* 270 */               gc.dispose();
/*     */             }
/*     */           }
/*     */           
/* 274 */           if (image != null) {
/* 275 */             File file = new File(path, "image-" + i + ".vpg");
/* 276 */             String sFileName = file.getAbsolutePath();
/*     */             
/* 278 */             ImageLoader imageLoader = new ImageLoader();
/* 279 */             imageLoader.data = new ImageData[] { image.getImageData() };
/*     */             
/*     */ 
/* 282 */             imageLoader.save(sFileName, 4);
/*     */           }
/*     */         }
/*     */       } catch (Exception e) {
/* 286 */         Logger.log(new LogEvent(LogIDs.GUI, "Creating Obfusticated Image", e));
/*     */       }
/*     */     }
/*     */     
/* 290 */     GeneratedResults gr = new GeneratedResults();
/*     */     
/* 292 */     if (activeShell != null) {
/* 293 */       activeShell.setCursor(null);
/*     */     }
/*     */     
/* 296 */     if ((debugPrompterListener != null) && 
/* 297 */       (!debugPrompterListener.promptUser(gr))) {
/* 298 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 302 */     FileWriter fw = null;
/*     */     try {
/* 304 */       File fUserMessage = new File(path, "usermessage.txt");
/*     */       
/* 306 */       fw = new FileWriter(fUserMessage);
/*     */       
/* 308 */       fw.write(gr.message + "\n" + gr.email);
/*     */       
/* 310 */       fw.close();
/*     */       
/* 312 */       fw = null;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 316 */       if (fw != null) {
/*     */         try {
/* 318 */           fw.close();
/*     */         }
/*     */         catch (Throwable f) {}
/*     */       }
/* 322 */       e.printStackTrace();
/*     */     }
/*     */     
/* 325 */     CoreWaiterSWT.waitForCore(org.gudy.azureus2.ui.swt.shells.CoreWaiterSWT.TriggerInThread.ANY_THREAD, new AzureusCoreRunningListener()
/*     */     {
/*     */       public void azureusCoreRunning(AzureusCore core)
/*     */       {
/* 329 */         core.createOperation(3, new AzureusCoreOperationTask()
/*     */         {
/*     */           public void run(AzureusCoreOperation operation)
/*     */           {
/*     */             try {
/* 334 */               File fEvidence = new File(UIDebugGenerator.4.this.val$path, "evidence.log");
/* 335 */               PrintWriter pw = new PrintWriter(fEvidence, "UTF-8");
/*     */               
/* 337 */               AEDiagnostics.generateEvidence(pw);
/*     */               
/* 339 */               pw.close();
/*     */             }
/*     */             catch (IOException e)
/*     */             {
/* 343 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     });
/*     */     try
/*     */     {
/* 351 */       File outFile = new File(SystemProperties.getUserPath(), "debug.zip");
/* 352 */       if (outFile.exists()) {
/* 353 */         outFile.delete();
/*     */       }
/*     */       
/* 356 */       AEDiagnostics.flushPendingLogs();
/*     */       
/* 358 */       ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFile));
/*     */       
/*     */ 
/* 361 */       File logPath = new File(SystemProperties.getUserPath(), "logs");
/* 362 */       File[] files = logPath.listFiles(new FileFilter() {
/*     */         public boolean accept(File pathname) {
/* 364 */           return pathname.getName().endsWith(".log");
/*     */         }
/* 366 */       });
/* 367 */       addFilesToZip(out, files);
/*     */       
/*     */ 
/* 370 */       File userPath = new File(SystemProperties.getUserPath());
/* 371 */       files = userPath.listFiles(new FileFilter() {
/*     */         public boolean accept(File pathname) {
/* 373 */           return pathname.getName().endsWith(".log");
/*     */         }
/* 375 */       });
/* 376 */       addFilesToZip(out, files);
/*     */       
/*     */ 
/* 379 */       files = path.listFiles();
/* 380 */       addFilesToZip(out, files);
/*     */       
/*     */ 
/* 383 */       long ago = SystemTime.getCurrentTime() - 7776000000L;
/* 384 */       File azureusPath = new File(SystemProperties.getApplicationPath());
/* 385 */       files = azureusPath.listFiles(new FileFilter() {
/*     */         public boolean accept(File pathname) {
/* 387 */           return (pathname.getName().startsWith("hs_err")) && (pathname.lastModified() > this.val$ago);
/*     */         }
/* 389 */       });
/* 390 */       addFilesToZip(out, files);
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 395 */         File temp_file = File.createTempFile("AZU", "tmp");
/*     */         
/* 397 */         files = temp_file.getParentFile().listFiles(new FileFilter() {
/*     */           public boolean accept(File pathname) {
/* 399 */             return (pathname.getName().startsWith("hs_err")) && (pathname.lastModified() > this.val$ago);
/*     */           }
/* 401 */         });
/* 402 */         addFilesToZip(out, files);
/* 403 */         temp_file.delete();
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/* 408 */       File javaLogPath = new File(System.getProperty("user.home"), "Library" + File.separator + "Logs" + File.separator + "Java");
/*     */       
/* 410 */       if (javaLogPath.isDirectory()) {
/* 411 */         files = javaLogPath.listFiles(new FileFilter() {
/*     */           public boolean accept(File pathname) {
/* 413 */             return (pathname.getName().endsWith("log")) && (pathname.lastModified() > this.val$ago);
/*     */           }
/* 415 */         });
/* 416 */         addFilesToZip(out, files);
/*     */       }
/*     */       
/*     */ 
/* 420 */       File diagReportspath = new File(System.getProperty("user.home"), "Library" + File.separator + "Logs" + File.separator + "DiagnosticReports");
/*     */       
/* 422 */       if (diagReportspath.isDirectory()) {
/* 423 */         files = diagReportspath.listFiles(new FileFilter() {
/*     */           public boolean accept(File pathname) {
/* 425 */             return (pathname.getName().endsWith("crash")) && (pathname.lastModified() > this.val$ago);
/*     */           }
/* 427 */         });
/* 428 */         addFilesToZip(out, files);
/*     */       }
/*     */       
/* 431 */       boolean bLogToFile = COConfigurationManager.getBooleanParameter("Logging Enable");
/* 432 */       String sLogDir = COConfigurationManager.getStringParameter("Logging Dir", "");
/*     */       
/* 434 */       if ((bLogToFile) && (sLogDir != null)) {
/* 435 */         File loggingFile = new File(sLogDir, "az.log");
/* 436 */         if (loggingFile.isFile()) {
/* 437 */           addFilesToZip(out, new File[] { loggingFile });
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 443 */       if (extraLogDirs != null) {
/* 444 */         for (File file : extraLogDirs) {
/* 445 */           if (file.isDirectory())
/*     */           {
/*     */ 
/* 448 */             files = file.listFiles(new FileFilter() {
/*     */               public boolean accept(File pathname) {
/* 450 */                 return (pathname.getName().endsWith("stackdump")) || (pathname.getName().endsWith("log"));
/*     */               }
/*     */               
/* 453 */             });
/* 454 */             addFilesToZip(out, files);
/*     */           }
/*     */         }
/*     */       }
/* 458 */       out.close();
/*     */       
/* 460 */       if (outFile.exists()) {
/* 461 */         gr.file = outFile;
/* 462 */         return gr;
/*     */       }
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 467 */       e.printStackTrace();
/*     */     }
/*     */     
/* 470 */     return null;
/*     */   }
/*     */   
/*     */   private static void promptUser(final boolean allowEmpty, GeneratedResults gr) {
/* 474 */     final Shell shell = org.gudy.azureus2.ui.swt.components.shell.ShellFactory.createShell(Utils.findAnyShell(), 1264);
/*     */     
/* 476 */     final String[] text = { null, null };
/* 477 */     final int[] sendMode = { -1 };
/*     */     
/* 479 */     Utils.setShellIcon(shell);
/*     */     
/* 481 */     Messages.setLanguageText(shell, "UIDebugGenerator.messageask.title");
/*     */     
/* 483 */     shell.setLayout(new FormLayout());
/*     */     
/* 485 */     Label lblText = new Label(shell, 0);
/* 486 */     Messages.setLanguageText(lblText, "UIDebugGenerator.messageask.text");
/*     */     
/* 488 */     Text textMessage = new Text(shell, 2114);
/* 489 */     final Text textEmail = new Text(shell, 2048);
/*     */     
/* 491 */     textEmail.setMessage("optional@email.here");
/*     */     
/* 493 */     Composite cButtonsSuper = new Composite(shell, 0);
/* 494 */     GridLayout gl = new GridLayout();
/* 495 */     cButtonsSuper.setLayout(gl);
/*     */     
/* 497 */     Composite cButtons = new Composite(cButtonsSuper, 0);
/* 498 */     cButtons.setLayoutData(new GridData(16777216, 16777216, true, true));
/* 499 */     Utils.setLayout(cButtons, new org.eclipse.swt.layout.RowLayout());
/*     */     
/* 501 */     Button btnSendNow = new Button(cButtons, 8);
/* 502 */     btnSendNow.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 504 */         if (UIDebugGenerator.emptyCheck(this.val$textMessage, allowEmpty)) {
/* 505 */           text[0] = this.val$textMessage.getText();
/* 506 */           text[1] = textEmail.getText();
/* 507 */           sendMode[0] = 0;
/*     */         }
/* 509 */         shell.dispose();
/*     */       }
/* 511 */     });
/* 512 */     Button btnSendLater = new Button(cButtons, 8);
/* 513 */     btnSendLater.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 515 */         if (UIDebugGenerator.emptyCheck(this.val$textMessage, allowEmpty)) {
/* 516 */           text[0] = this.val$textMessage.getText();
/* 517 */           text[1] = textEmail.getText();
/* 518 */           sendMode[0] = 1;
/*     */         }
/* 520 */         shell.dispose();
/*     */       }
/* 522 */     });
/* 523 */     Button btnCancel = new Button(cButtons, 8);
/* 524 */     btnCancel.addListener(13, new Listener() {
/*     */       public void handleEvent(Event event) {
/* 526 */         this.val$shell.dispose();
/*     */       }
/*     */     });
/*     */     
/* 530 */     if (Constants.isOSX) {
/* 531 */       btnCancel.moveAbove(null);
/*     */     }
/* 533 */     Messages.setLanguageText(btnCancel, "Button.cancel");
/* 534 */     Messages.setLanguageText(btnSendNow, "Button.sendNow");
/* 535 */     Messages.setLanguageText(btnSendLater, "Button.sendManual");
/*     */     
/*     */ 
/*     */ 
/* 539 */     FormData fd = new FormData();
/* 540 */     fd.top = new FormAttachment(0, 5);
/* 541 */     fd.left = new FormAttachment(0, 5);
/* 542 */     fd.right = new FormAttachment(100, -5);
/* 543 */     lblText.setLayoutData(fd);
/*     */     
/* 545 */     fd = new FormData();
/* 546 */     fd.top = new FormAttachment(lblText, 10);
/* 547 */     fd.left = new FormAttachment(0, 5);
/* 548 */     fd.right = new FormAttachment(100, -5);
/* 549 */     fd.bottom = new FormAttachment(textEmail, -10);
/* 550 */     textMessage.setLayoutData(fd);
/*     */     
/* 552 */     fd = new FormData();
/* 553 */     fd.left = new FormAttachment(0, 5);
/* 554 */     fd.right = new FormAttachment(100, -5);
/* 555 */     fd.bottom = new FormAttachment(cButtonsSuper, -2);
/* 556 */     textEmail.setLayoutData(fd);
/*     */     
/* 558 */     fd = new FormData();
/* 559 */     fd.left = new FormAttachment(0, 5);
/* 560 */     fd.right = new FormAttachment(100, -5);
/* 561 */     fd.bottom = new FormAttachment(100, -1);
/* 562 */     cButtonsSuper.setLayoutData(fd);
/*     */     
/* 564 */     textMessage.setFocus();
/*     */     
/* 566 */     shell.setSize(500, 300);
/* 567 */     shell.layout();
/* 568 */     Utils.centreWindow(shell);
/* 569 */     shell.open();
/*     */     
/* 571 */     while (!shell.isDisposed()) {
/* 572 */       if (!shell.getDisplay().readAndDispatch()) {
/* 573 */         shell.getDisplay().sleep();
/*     */       }
/*     */     }
/* 576 */     if (sendMode[0] != -1) {
/* 577 */       gr.message = text[0];
/* 578 */       gr.email = text[1];
/*     */     }
/* 580 */     gr.sendNow = (sendMode[0] == 0);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static boolean emptyCheck(Text textMessage, boolean allowEmpty)
/*     */   {
/* 591 */     if (allowEmpty) {
/* 592 */       return true;
/*     */     }
/* 594 */     if (textMessage.getText().length() > 0) {
/* 595 */       return true;
/*     */     }
/*     */     
/* 598 */     new MessageBoxShell(32, "UIDebugGenerator.message.cancel", (String[])null).open(null);
/*     */     
/*     */ 
/* 601 */     return false;
/*     */   }
/*     */   
/*     */   private static void addFilesToZip(ZipOutputStream out, File[] files) {
/* 605 */     byte[] buf = new byte['Ѐ'];
/* 606 */     if (files == null) {
/* 607 */       return;
/*     */     }
/*     */     
/* 610 */     for (int j = 0; j < files.length; j++) {
/* 611 */       File file = files[j];
/*     */       FileInputStream in;
/*     */       try
/*     */       {
/* 615 */         in = new FileInputStream(file);
/*     */       }
/*     */       catch (FileNotFoundException e) {
/*     */         continue;
/*     */       }
/*     */       try {
/* 621 */         ZipEntry entry = new ZipEntry(file.getName());
/* 622 */         entry.setTime(file.lastModified());
/* 623 */         out.putNextEntry(entry);
/*     */         
/*     */         int len;
/* 626 */         while ((len = in.read(buf)) > 0) {
/* 627 */           out.write(buf, 0, len);
/*     */         }
/*     */         
/*     */ 
/* 631 */         out.closeEntry();
/*     */       }
/*     */       catch (IOException e) {
/* 634 */         e.printStackTrace();
/*     */       }
/*     */       try
/*     */       {
/* 638 */         in.close();
/*     */       }
/*     */       catch (IOException e) {
/* 641 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void obfusticateArea(Image image, Rectangle bounds)
/*     */   {
/* 651 */     GC gc = new GC(image);
/*     */     try {
/* 653 */       gc.setBackground(image.getDevice().getSystemColor(1));
/* 654 */       gc.setForeground(image.getDevice().getSystemColor(3));
/* 655 */       gc.fillRectangle(bounds);
/* 656 */       gc.drawRectangle(bounds);
/* 657 */       int x2 = bounds.x + bounds.width;
/* 658 */       int y2 = bounds.y + bounds.height;
/* 659 */       gc.drawLine(bounds.x, bounds.y, x2, y2);
/* 660 */       gc.drawLine(x2, bounds.y, bounds.x, y2);
/*     */     } finally {
/* 662 */       gc.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void obfusticateArea(Image image, Rectangle bounds, String text)
/*     */   {
/* 673 */     if (bounds.isEmpty()) {
/* 674 */       return;
/*     */     }
/* 676 */     if ((text == null) || (text.length() == 0)) {
/* 677 */       obfusticateArea(image, bounds);
/* 678 */       return;
/*     */     }
/*     */     
/* 681 */     GC gc = new GC(image);
/*     */     try {
/* 683 */       Device device = image.getDevice();
/* 684 */       gc.setBackground(device.getSystemColor(1));
/* 685 */       gc.setForeground(device.getSystemColor(3));
/* 686 */       gc.fillRectangle(bounds);
/* 687 */       gc.drawRectangle(bounds);
/* 688 */       Utils.setClipping(gc, bounds);
/* 689 */       gc.drawText(text, bounds.x + 2, bounds.y + 1);
/*     */     } finally {
/* 691 */       gc.dispose();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void obfusticateArea(Image image, Control control, String text)
/*     */   {
/* 702 */     if (control.isDisposed()) {
/* 703 */       return;
/*     */     }
/* 705 */     Rectangle bounds = control.getBounds();
/* 706 */     Point location = Utils.getLocationRelativeToShell(control);
/* 707 */     bounds.x = location.x;
/* 708 */     bounds.y = location.y;
/*     */     
/* 710 */     obfusticateArea(image, bounds, text);
/*     */   }
/*     */   
/*     */   public static abstract interface DebugPrompterListener
/*     */   {
/*     */     public abstract boolean promptUser(UIDebugGenerator.GeneratedResults paramGeneratedResults);
/*     */   }
/*     */   
/*     */   public static class GeneratedResults
/*     */   {
/*     */     public File file;
/*     */     public String message;
/*     */     boolean sendNow;
/*     */     public String email;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/debug/UIDebugGenerator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */