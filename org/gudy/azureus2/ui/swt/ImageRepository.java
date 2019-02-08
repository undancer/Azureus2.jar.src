/*     */ package org.gudy.azureus2.ui.swt;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.ui.skin.SkinProperties;
/*     */ import com.aelitis.azureus.ui.swt.imageloader.ImageLoader;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.InetAddress;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.imageio.ImageIO;
/*     */ import org.eclipse.swt.events.ModifyEvent;
/*     */ import org.eclipse.swt.events.ModifyListener;
/*     */ import org.eclipse.swt.graphics.GC;
/*     */ import org.eclipse.swt.graphics.ImageData;
/*     */ import org.eclipse.swt.graphics.Point;
/*     */ import org.eclipse.swt.graphics.Rectangle;
/*     */ import org.eclipse.swt.layout.FillLayout;
/*     */ import org.eclipse.swt.program.Program;
/*     */ import org.eclipse.swt.widgets.Display;
/*     */ import org.eclipse.swt.widgets.Label;
/*     */ import org.eclipse.swt.widgets.Shell;
/*     */ import org.eclipse.swt.widgets.Text;
/*     */ import org.gudy.azureus2.core3.peer.PEPeer;
/*     */ import org.gudy.azureus2.core3.util.AENetworkClassifier;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.peers.Peer;
/*     */ import org.gudy.azureus2.plugins.utils.LocationProvider;
/*     */ import org.gudy.azureus2.plugins.utils.Utilities;
/*     */ import org.gudy.azureus2.pluginsimpl.local.PluginCoreUtils;
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
/*     */ public class ImageRepository
/*     */ {
/*  60 */   private static final String[] noCacheExtList = { ".exe" };
/*     */   
/*     */ 
/*     */ 
/*  64 */   private static final boolean forceNoAWT = (Constants.isOSX) || (Constants.isWindows);
/*     */   private static LocationProvider flag_provider;
/*     */   private static long flag_provider_last_check;
/*     */   
/*  68 */   static void addPath(String path, String id) { SkinProperties[] skinProperties = ImageLoader.getInstance().getSkinProperties();
/*  69 */     if ((skinProperties != null) && (skinProperties.length > 0)) {
/*  70 */       skinProperties[0].addProperty(id, path);
/*     */     }
/*     */   }
/*     */   
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public static org.eclipse.swt.graphics.Image getImage(String name) {
/*  78 */     return ImageLoader.getInstance().getImage(name);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static org.eclipse.swt.graphics.Image getIconFromExtension(File file, String ext, boolean bBig, boolean minifolder)
/*     */   {
/*  88 */     org.eclipse.swt.graphics.Image image = null;
/*     */     try
/*     */     {
/*  91 */       String key = "osicon" + ext;
/*     */       
/*  93 */       if (bBig)
/*  94 */         key = key + "-big";
/*  95 */       if (minifolder) {
/*  96 */         key = key + "-fold";
/*     */       }
/*  98 */       image = ImageLoader.getInstance().getImage(key);
/*  99 */       if (ImageLoader.isRealImage(image)) {
/* 100 */         return image;
/*     */       }
/*     */       
/* 103 */       ImageLoader.getInstance().releaseImage(key);
/* 104 */       image = null;
/*     */       
/* 106 */       ImageData imageData = null;
/*     */       
/* 108 */       if (Constants.isWindows)
/*     */       {
/*     */         try
/*     */         {
/* 112 */           Class<?> enhancerClass = Class.forName("org.gudy.azureus2.ui.swt.win32.Win32UIEnhancer");
/* 113 */           Method method = enhancerClass.getMethod("getFileIcon", new Class[] { File.class, Boolean.TYPE });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 118 */           image = (org.eclipse.swt.graphics.Image)method.invoke(null, new Object[] { file, Boolean.valueOf(bBig) });
/*     */           
/*     */ 
/*     */ 
/* 122 */           if (image != null) {
/* 123 */             if (!bBig)
/* 124 */               image = force16height(image);
/* 125 */             if (minifolder)
/* 126 */               image = minifolderize(file.getParent(), image, bBig);
/* 127 */             ImageLoader.getInstance().addImageNoDipose(key, image);
/* 128 */             return image;
/*     */           }
/*     */         } catch (Exception e) {
/* 131 */           Debug.printStackTrace(e);
/*     */         } }
/* 133 */       if (Utils.isCocoa) {
/*     */         try {
/* 135 */           Class<?> enhancerClass = Class.forName("org.gudy.azureus2.ui.swt.osx.CocoaUIEnhancer");
/* 136 */           Method method = enhancerClass.getMethod("getFileIcon", new Class[] { String.class, Integer.TYPE });
/*     */           
/*     */ 
/*     */ 
/*     */ 
/* 141 */           image = (org.eclipse.swt.graphics.Image)method.invoke(null, new Object[] { file.getAbsolutePath(), Integer.valueOf(bBig ? '' : 16) });
/*     */           
/*     */ 
/*     */ 
/* 145 */           if (image != null) {
/* 146 */             if (!bBig)
/* 147 */               image = force16height(image);
/* 148 */             if (minifolder)
/* 149 */               image = minifolderize(file.getParent(), image, bBig);
/* 150 */             ImageLoader.getInstance().addImageNoDipose(key, image);
/* 151 */             return image;
/*     */           }
/*     */         } catch (Throwable t) {
/* 154 */           Debug.printStackTrace(t);
/*     */         }
/*     */       }
/*     */       
/* 158 */       if (imageData == null) {
/* 159 */         Program program = Program.findProgram(ext);
/* 160 */         if (program != null) {
/* 161 */           imageData = program.getImageData();
/*     */         }
/*     */       }
/*     */       
/* 165 */       if (imageData != null) {
/* 166 */         image = new org.eclipse.swt.graphics.Image(Display.getDefault(), imageData);
/* 167 */         if (!bBig)
/* 168 */           image = force16height(image);
/* 169 */         if (minifolder) {
/* 170 */           image = minifolderize(file.getParent(), image, bBig);
/*     */         }
/* 172 */         ImageLoader.getInstance().addImageNoDipose(key, image);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*     */ 
/* 179 */     if (image == null) {
/* 180 */       return getImage(minifolder ? "folder" : "transparent");
/*     */     }
/* 182 */     return image;
/*     */   }
/*     */   
/*     */   private static org.eclipse.swt.graphics.Image minifolderize(String path, org.eclipse.swt.graphics.Image img, boolean big) {
/* 186 */     org.eclipse.swt.graphics.Image imgFolder = getImage(big ? "folder" : "foldersmall");
/* 187 */     Rectangle folderBounds = imgFolder.getBounds();
/* 188 */     Rectangle dstBounds = img.getBounds();
/* 189 */     org.eclipse.swt.graphics.Image tempImg = Utils.renderTransparency(Display.getCurrent(), img, imgFolder, new Point(dstBounds.width - folderBounds.width, dstBounds.height - folderBounds.height), 204);
/*     */     
/*     */ 
/* 192 */     if (tempImg != null) {
/* 193 */       img.dispose();
/* 194 */       img = tempImg;
/*     */     }
/* 196 */     return img;
/*     */   }
/*     */   
/*     */   private static org.eclipse.swt.graphics.Image force16height(org.eclipse.swt.graphics.Image image) {
/* 200 */     if (image == null) {
/* 201 */       return image;
/*     */     }
/*     */     
/* 204 */     Rectangle bounds = image.getBounds();
/* 205 */     if (bounds.height != 16) {
/* 206 */       org.eclipse.swt.graphics.Image newImage = new org.eclipse.swt.graphics.Image(image.getDevice(), 16, 16);
/* 207 */       GC gc = new GC(newImage);
/*     */       try {
/* 209 */         if (!Constants.isUnix)
/*     */         {
/* 211 */           gc.setAdvanced(true);
/*     */         }
/*     */         
/* 214 */         gc.drawImage(image, 0, 0, bounds.width, bounds.height, 0, 0, 16, 16);
/*     */       } finally {
/* 216 */         gc.dispose();
/*     */       }
/*     */       
/* 219 */       image.dispose();
/* 220 */       image = newImage;
/*     */     }
/*     */     
/* 223 */     return image;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static org.eclipse.swt.graphics.Image getPathIcon(String path, boolean bBig, boolean minifolder)
/*     */   {
/* 234 */     if (path == null) {
/* 235 */       return null;
/*     */     }
/* 237 */     File file = null;
/* 238 */     boolean bDeleteFile = false;
/*     */     
/* 240 */     boolean noAWT = (forceNoAWT) || (!bBig);
/*     */     try
/*     */     {
/* 243 */       file = new File(path);
/*     */       
/*     */ 
/*     */ 
/*     */       String key;
/*     */       
/*     */ 
/* 250 */       if (file.isDirectory()) {
/* 251 */         if (noAWT) {
/* 252 */           if ((Constants.isWindows) || (Utils.isCocoa)) {
/* 253 */             return getIconFromExtension(file, "-folder", bBig, false);
/*     */           }
/* 255 */           return getImage("folder");
/*     */         }
/*     */         
/* 258 */         key = file.getPath();
/*     */       } else {
/* 260 */         int idxDot = file.getName().lastIndexOf(".");
/*     */         String key;
/* 262 */         if (idxDot == -1) {
/* 263 */           if (noAWT) {
/* 264 */             return getIconFromExtension(file, "", bBig, false);
/*     */           }
/*     */           
/* 267 */           key = "?!blank";
/*     */         } else {
/* 269 */           String ext = file.getName().substring(idxDot);
/* 270 */           key = ext;
/*     */           
/* 272 */           if (noAWT) {
/* 273 */             return getIconFromExtension(file, ext, bBig, minifolder);
/*     */           }
/*     */           
/* 276 */           for (int i = 0; i < noCacheExtList.length; i++) {
/* 277 */             if (noCacheExtList[i].equalsIgnoreCase(ext)) {
/* 278 */               key = file.getPath();
/* 279 */               break;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 285 */       if (bBig)
/* 286 */         key = key + "-big";
/* 287 */       if (minifolder) {
/* 288 */         key = key + "-fold";
/*     */       }
/* 290 */       String key = "osicon" + key;
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 296 */       org.eclipse.swt.graphics.Image image = ImageLoader.getInstance().getImage(key);
/* 297 */       if (ImageLoader.isRealImage(image)) {
/* 298 */         return image;
/*     */       }
/* 300 */       ImageLoader.getInstance().releaseImage(key);
/* 301 */       image = null;
/*     */       
/* 303 */       bDeleteFile = !file.exists();
/* 304 */       if (bDeleteFile) {
/* 305 */         file = File.createTempFile("AZ_", FileUtil.getExtension(path));
/*     */       }
/*     */       
/* 308 */       java.awt.Image awtImage = null;
/*     */       try
/*     */       {
/* 311 */         Class sfClass = Class.forName("sun.awt.shell.ShellFolder");
/* 312 */         if ((sfClass != null) && (file != null)) {
/* 313 */           Method method = sfClass.getMethod("getShellFolder", new Class[] { File.class });
/*     */           
/*     */ 
/* 316 */           if (method != null) {
/* 317 */             Object sfInstance = method.invoke(null, new Object[] { file });
/*     */             
/*     */ 
/*     */ 
/* 321 */             if (sfInstance != null) {
/* 322 */               method = sfClass.getMethod("getIcon", new Class[] { Boolean.TYPE });
/*     */               
/*     */ 
/* 325 */               if (method != null) {
/* 326 */                 awtImage = (java.awt.Image)method.invoke(sfInstance, new Object[] { Boolean.valueOf(bBig) });
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 337 */       if (awtImage != null) {
/* 338 */         ByteArrayOutputStream outStream = new ByteArrayOutputStream();
/* 339 */         ImageIO.write((BufferedImage)awtImage, "png", outStream);
/* 340 */         ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
/*     */         
/*     */ 
/* 343 */         image = new org.eclipse.swt.graphics.Image(Display.getDefault(), inStream);
/* 344 */         if (!bBig) {
/* 345 */           image = force16height(image);
/*     */         }
/* 347 */         if (minifolder) {
/* 348 */           image = minifolderize(file.getParent(), image, bBig);
/*     */         }
/*     */         
/* 351 */         ImageLoader.getInstance().addImageNoDipose(key, image);
/*     */         
/* 353 */         if ((bDeleteFile) && (file != null) && (file.exists())) {
/* 354 */           file.delete();
/*     */         }
/* 356 */         return image;
/*     */       }
/*     */     }
/*     */     catch (Exception e) {}
/*     */     
/*     */ 
/* 362 */     if ((bDeleteFile) && (file != null) && (file.exists())) {
/* 363 */       file.delete();
/*     */     }
/*     */     
/*     */ 
/* 367 */     String ext = FileUtil.getExtension(path);
/* 368 */     if (ext.length() == 0) {
/* 369 */       return getImage("folder");
/*     */     }
/*     */     
/* 372 */     return getIconFromExtension(file, ext, bBig, minifolder);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/* 378 */   private static org.eclipse.swt.graphics.Image flag_none = ImageLoader.getNoImage();
/* 379 */   private static Object flag_small_key = new Object();
/* 380 */   private static Object flag_big_key = new Object();
/*     */   
/* 382 */   private static Map<String, org.eclipse.swt.graphics.Image> flag_cache = new HashMap();
/*     */   
/*     */ 
/*     */   private static LocationProvider getFlagProvider()
/*     */   {
/* 387 */     if (flag_provider != null)
/*     */     {
/* 389 */       if (flag_provider.isDestroyed())
/*     */       {
/* 391 */         flag_provider = null;
/* 392 */         flag_provider_last_check = 0L;
/*     */       }
/*     */     }
/*     */     
/* 396 */     if (flag_provider == null)
/*     */     {
/* 398 */       long now = SystemTime.getMonotonousTime();
/*     */       
/* 400 */       if ((flag_provider_last_check == 0L) || (now - flag_provider_last_check > 20000L))
/*     */       {
/* 402 */         flag_provider_last_check = now;
/*     */         
/* 404 */         List<LocationProvider> providers = AzureusCoreFactory.getSingleton().getPluginManager().getDefaultPluginInterface().getUtilities().getLocationProviders();
/*     */         
/* 406 */         for (LocationProvider provider : providers)
/*     */         {
/* 408 */           if (provider.hasCapabilities(6L))
/*     */           {
/*     */ 
/*     */ 
/* 412 */             flag_provider = provider;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 418 */     return flag_provider;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static boolean hasCountryFlags(boolean small)
/*     */   {
/* 425 */     if (!Utils.isSWTThread())
/*     */     {
/* 427 */       Debug.out("Needs to be swt thread...");
/*     */       
/* 429 */       return false;
/*     */     }
/*     */     
/* 432 */     LocationProvider fp = getFlagProvider();
/*     */     
/* 434 */     if (fp == null)
/*     */     {
/* 436 */       return false;
/*     */     }
/*     */     
/* 439 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static org.eclipse.swt.graphics.Image getCountryFlag(Peer peer, boolean small)
/*     */   {
/* 447 */     return getCountryFlag(PluginCoreUtils.unwrap(peer), small);
/*     */   }
/*     */   
/* 450 */   private static Map<String, org.eclipse.swt.graphics.Image> net_images = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static org.eclipse.swt.graphics.Image getCountryFlag(PEPeer peer, boolean small)
/*     */   {
/* 457 */     if (peer == null)
/*     */     {
/* 459 */       return null;
/*     */     }
/*     */     
/* 462 */     Object peer_key = small ? flag_small_key : flag_big_key;
/*     */     
/* 464 */     org.eclipse.swt.graphics.Image flag = (org.eclipse.swt.graphics.Image)peer.getUserData(peer_key);
/*     */     
/* 466 */     if (flag == null)
/*     */     {
/* 468 */       LocationProvider fp = getFlagProvider();
/*     */       
/* 470 */       if (fp != null) {
/*     */         try
/*     */         {
/* 473 */           String ip = peer.getIp();
/*     */           
/* 475 */           if (HostNameToIPResolver.isDNSName(ip))
/*     */           {
/* 477 */             InetAddress peer_address = HostNameToIPResolver.syncResolve(ip);
/*     */             
/* 479 */             String cc_key = fp.getISO3166CodeForIP(peer_address) + (small ? ".s" : ".l");
/*     */             
/* 481 */             flag = (org.eclipse.swt.graphics.Image)flag_cache.get(cc_key);
/*     */             
/* 483 */             if (flag != null)
/*     */             {
/* 485 */               peer.setUserData(peer_key, flag);
/*     */             }
/*     */             else
/*     */             {
/* 489 */               InputStream is = fp.getCountryFlagForIP(peer_address, small ? 0 : 1);
/*     */               
/* 491 */               if (is != null) {
/*     */                 try
/*     */                 {
/* 494 */                   Display display = Display.getDefault();
/*     */                   
/* 496 */                   flag = new org.eclipse.swt.graphics.Image(display, is);
/*     */                   
/* 498 */                   flag = Utils.adjustPXForDPI(display, flag);
/*     */ 
/*     */                 }
/*     */                 finally
/*     */                 {
/*     */ 
/* 504 */                   is.close();
/*     */                 }
/*     */                 
/*     */               } else {
/* 508 */                 flag = flag_none;
/*     */               }
/*     */               
/* 511 */               flag_cache.put(cc_key, flag);
/*     */               
/* 513 */               peer.setUserData(peer_key, flag);
/*     */             }
/*     */           }
/*     */           else {
/* 517 */             String cat = AENetworkClassifier.categoriseAddress(ip);
/*     */             
/* 519 */             if (cat != "Public")
/*     */             {
/* 521 */               String key = "net_" + cat + (small ? "_s" : "_b");
/*     */               
/* 523 */               org.eclipse.swt.graphics.Image i = (org.eclipse.swt.graphics.Image)net_images.get(key);
/*     */               
/* 525 */               if (i == null)
/*     */               {
/* 527 */                 Utils.execSWTThread(new Runnable()
/*     */                 {
/*     */ 
/*     */                   public void run()
/*     */                   {
/*     */ 
/* 533 */                     org.eclipse.swt.graphics.Image i = ImageLoader.getInstance().getImage(this.val$key);
/*     */                     
/* 535 */                     ImageRepository.net_images.put(this.val$key, i); } }, false);
/*     */                 
/*     */ 
/*     */ 
/*     */ 
/* 540 */                 i = (org.eclipse.swt.graphics.Image)net_images.get(key);
/*     */               }
/*     */               
/* 543 */               if (ImageLoader.isRealImage(i))
/*     */               {
/* 545 */                 return i;
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 556 */     if (flag == flag_none)
/*     */     {
/* 558 */       return null;
/*     */     }
/*     */     
/* 561 */     return flag;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static org.eclipse.swt.graphics.Image getCountryFlag(InetAddress address, boolean small)
/*     */   {
/* 569 */     if (address == null)
/*     */     {
/* 571 */       return null;
/*     */     }
/*     */     
/* 574 */     org.eclipse.swt.graphics.Image flag = null;
/*     */     
/* 576 */     LocationProvider fp = getFlagProvider();
/*     */     
/* 578 */     if (fp != null) {
/*     */       try
/*     */       {
/* 581 */         String cc_key = fp.getISO3166CodeForIP(address) + (small ? ".s" : ".l");
/*     */         
/* 583 */         flag = (org.eclipse.swt.graphics.Image)flag_cache.get(cc_key);
/*     */         
/* 585 */         if (flag == null)
/*     */         {
/* 587 */           InputStream is = fp.getCountryFlagForIP(address, small ? 0 : 1);
/*     */           
/* 589 */           if (is != null) {
/*     */             try
/*     */             {
/* 592 */               Display display = Display.getDefault();
/*     */               
/* 594 */               flag = new org.eclipse.swt.graphics.Image(display, is);
/*     */               
/* 596 */               flag = Utils.adjustPXForDPI(display, flag);
/*     */ 
/*     */             }
/*     */             finally
/*     */             {
/*     */ 
/* 602 */               is.close();
/*     */             }
/*     */             
/*     */           } else {
/* 606 */             flag = flag_none;
/*     */           }
/*     */           
/* 609 */           flag_cache.put(cc_key, flag);
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {}
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 617 */     if (flag == flag_none)
/*     */     {
/* 619 */       return null;
/*     */     }
/*     */     
/* 622 */     return flag;
/*     */   }
/*     */   
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 628 */     Display display = new Display();
/* 629 */     Shell shell = new Shell(display, 1264);
/* 630 */     shell.setLayout(new FillLayout(512));
/*     */     
/* 632 */     final Label label = new Label(shell, 2048);
/*     */     
/* 634 */     Text text = new Text(shell, 2048);
/* 635 */     text.addModifyListener(new ModifyListener()
/*     */     {
/*     */       public void modifyText(ModifyEvent e) {
/* 638 */         org.eclipse.swt.graphics.Image pathIcon = ImageRepository.getPathIcon(this.val$text.getText(), false, false);
/* 639 */         label.setImage(pathIcon);
/*     */       }
/*     */       
/* 642 */     });
/* 643 */     shell.open();
/*     */     
/* 645 */     while (!shell.isDisposed()) {
/* 646 */       if (!display.readAndDispatch()) {
/* 647 */         display.sleep();
/*     */       }
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/swt/ImageRepository.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */