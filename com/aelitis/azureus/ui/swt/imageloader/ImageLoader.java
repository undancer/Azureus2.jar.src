/*      */ package com.aelitis.azureus.ui.swt.imageloader;
/*      */ 
/*      */ import com.aelitis.azureus.ui.skin.SkinProperties;
/*      */ import com.aelitis.azureus.ui.skin.SkinPropertiesImpl;
/*      */ import com.aelitis.azureus.ui.utils.ImageBytesDownloader;
/*      */ import com.aelitis.azureus.ui.utils.ImageBytesDownloader.ImageDownloaderListener;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.PrintStream;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collections;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.CopyOnWriteArrayList;
/*      */ import org.eclipse.swt.SWTException;
/*      */ import org.eclipse.swt.events.DisposeEvent;
/*      */ import org.eclipse.swt.events.DisposeListener;
/*      */ import org.eclipse.swt.graphics.Device;
/*      */ import org.eclipse.swt.graphics.GC;
/*      */ import org.eclipse.swt.graphics.Image;
/*      */ import org.eclipse.swt.graphics.ImageData;
/*      */ import org.eclipse.swt.graphics.PaletteData;
/*      */ import org.eclipse.swt.graphics.Point;
/*      */ import org.eclipse.swt.graphics.Rectangle;
/*      */ import org.eclipse.swt.widgets.Button;
/*      */ import org.eclipse.swt.widgets.Control;
/*      */ import org.eclipse.swt.widgets.Display;
/*      */ import org.eclipse.swt.widgets.Label;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*      */ import org.gudy.azureus2.core3.util.AERunnable;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.FileUtil;
/*      */ import org.gudy.azureus2.core3.util.IndentWriter;
/*      */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*      */ import org.gudy.azureus2.core3.util.SystemProperties;
/*      */ import org.gudy.azureus2.core3.util.TimerEvent;
/*      */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
/*      */ import org.gudy.azureus2.ui.swt.ImageRepository;
/*      */ import org.gudy.azureus2.ui.swt.Utils;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ImageLoader
/*      */   implements AEDiagnosticsEvidenceGenerator
/*      */ {
/*      */   private static ImageLoader instance;
/*      */   private static final boolean DEBUG_UNLOAD = false;
/*      */   private static final boolean DEBUG_REFCOUNT = false;
/*      */   private static final int GC_INTERVAL = 60000;
/*   64 */   private final String[] sSuffixChecks = { "-over", "-down", "-disabled", "-selected", "-gray" };
/*      */   
/*      */ 
/*      */ 
/*      */   private Display display;
/*      */   
/*      */ 
/*      */   public static Image noImage;
/*      */   
/*      */ 
/*      */   private final ConcurrentHashMap<String, ImageLoaderRefInfo> _mapImages;
/*      */   
/*      */ 
/*      */   private final ArrayList<String> notFound;
/*      */   
/*      */ 
/*      */   private CopyOnWriteArrayList<SkinProperties> skinProperties;
/*      */   
/*      */ 
/*      */   private int disabledOpacity;
/*      */   
/*      */ 
/*   86 */   private Set<String> cached_resources = new HashSet();
/*      */   
/*   88 */   private File cache_dir = new File(SystemProperties.getUserPath(), "cache");
/*      */   
/*      */   public static synchronized ImageLoader getInstance()
/*      */   {
/*   92 */     if (instance == null) {
/*   93 */       instance = new ImageLoader(Display.getDefault(), null);
/*      */       
/*   95 */       SkinPropertiesImpl skinProperties = new SkinPropertiesImpl(ImageRepository.class.getClassLoader(), "org/gudy/azureus2/ui/icons/", "icons.properties");
/*      */       
/*      */ 
/*   98 */       instance.addSkinProperties(skinProperties);
/*      */     }
/*  100 */     return instance;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public ImageLoader(Display display, SkinProperties skinProperties)
/*      */   {
/*  107 */     File[] files = this.cache_dir.listFiles();
/*      */     
/*  109 */     if (files != null) {
/*  110 */       for (File f : files) {
/*  111 */         String name = f.getName();
/*  112 */         if (name.endsWith(".ico")) {
/*  113 */           this.cached_resources.add(name);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  118 */     this._mapImages = new ConcurrentHashMap();
/*  119 */     this.notFound = new ArrayList();
/*  120 */     this.display = display;
/*  121 */     this.skinProperties = new CopyOnWriteArrayList();
/*  122 */     addSkinProperties(skinProperties);
/*      */     
/*  124 */     AEDiagnostics.addEvidenceGenerator(this);
/*      */     
/*  126 */     SimpleTimer.addPeriodicEvent("GC_ImageLoader", 60000L, new TimerEventPerformer()
/*      */     {
/*      */       public void perform(TimerEvent event) {
/*  129 */         ImageLoader.this.collectGarbage();
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Image[] findResources(String sKey)
/*      */   {
/*  148 */     if (Collections.binarySearch(this.notFound, sKey) >= 0) {
/*  149 */       return null;
/*      */     }
/*      */     
/*  152 */     for (int i = 0; i < this.sSuffixChecks.length; i++) {
/*  153 */       String sSuffix = this.sSuffixChecks[i];
/*      */       
/*  155 */       if (sKey.endsWith(sSuffix))
/*      */       {
/*  157 */         String sParentName = sKey.substring(0, sKey.length() - sSuffix.length());
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  165 */         String[] sParentFiles = null;
/*  166 */         ClassLoader cl = null;
/*  167 */         for (SkinProperties sp : this.skinProperties) {
/*  168 */           sParentFiles = sp.getStringArray(sParentName);
/*  169 */           if (sParentFiles != null) {
/*  170 */             cl = sp.getClassLoader();
/*  171 */             break;
/*      */           }
/*      */         }
/*  174 */         if (sParentFiles != null) {
/*  175 */           boolean bFoundOne = false;
/*  176 */           Image[] images = parseValuesString(cl, sKey, sParentFiles, sSuffix);
/*  177 */           if (images != null) {
/*  178 */             for (int j = 0; j < images.length; j++) {
/*  179 */               Image image = images[j];
/*  180 */               if (isRealImage(image)) {
/*  181 */                 bFoundOne = true;
/*      */               }
/*      */             }
/*  184 */             if (!bFoundOne) {
/*  185 */               for (int j = 0; j < images.length; j++) {
/*  186 */                 Image image = images[j];
/*  187 */                 if (isRealImage(image)) {
/*  188 */                   image.dispose();
/*      */                 }
/*      */               }
/*      */             } else {
/*  192 */               return images;
/*      */             }
/*      */           }
/*      */         }
/*      */         else {
/*  197 */           Image[] images = findResources(sParentName);
/*  198 */           if (images != null) {
/*  199 */             return images;
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  206 */     int i = Collections.binarySearch(this.notFound, sKey) * -1 - 1;
/*  207 */     if (i >= 0) {
/*  208 */       this.notFound.add(i, sKey);
/*      */     }
/*  210 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private Image[] parseValuesString(ClassLoader cl, String sKey, String[] values, String suffix)
/*      */   {
/*  222 */     Image[] images = null;
/*      */     
/*  224 */     int splitX = 0;
/*  225 */     int locationStart = 0;
/*  226 */     int useIndex = -1;
/*  227 */     if ((values[0].equals("multi")) && (values.length > 2)) {
/*  228 */       splitX = Integer.parseInt(values[1]);
/*  229 */       splitX = Utils.adjustPXForDPI(splitX);
/*  230 */       locationStart = 2;
/*  231 */     } else if ((values[0].equals("multi-index")) && (values.length > 3)) {
/*  232 */       splitX = Integer.parseInt(values[1]);
/*  233 */       splitX = Utils.adjustPXForDPI(splitX);
/*  234 */       useIndex = Integer.parseInt(values[2]);
/*  235 */       locationStart = 3;
/*      */     }
/*      */     
/*  238 */     if ((locationStart == 0) || (splitX <= 0)) {
/*  239 */       images = new Image[values.length];
/*  240 */       for (int i = 0; i < values.length; i++) {
/*  241 */         int index = values[i].lastIndexOf('.');
/*  242 */         if (index > 0) {
/*  243 */           String sTryFile = values[i].substring(0, index) + suffix + values[i].substring(index);
/*      */           
/*  245 */           images[i] = loadImage(this.display, cl, sTryFile, sKey);
/*      */           
/*  247 */           if (images[i] == null) {
/*  248 */             sTryFile = values[i].substring(0, index) + suffix.replace('-', '_') + values[i].substring(index);
/*      */             
/*  250 */             images[i] = loadImage(this.display, cl, sTryFile, sKey);
/*      */           }
/*      */         }
/*      */         
/*  254 */         if (images[i] == null) {
/*  255 */           images[i] = getNoImage(sKey);
/*      */         }
/*      */       }
/*      */     } else {
/*  259 */       Image image = null;
/*      */       
/*  261 */       String image_key = null;
/*      */       
/*  263 */       String origFile = values[locationStart];
/*  264 */       int index = origFile.lastIndexOf('.');
/*      */       
/*  266 */       if (index > 0) {
/*  267 */         if (useIndex == -1) {
/*  268 */           String sTryFile = origFile.substring(0, index) + suffix + origFile.substring(index);
/*      */           
/*  270 */           image = loadImage(this.display, cl, sTryFile, sKey);
/*      */           
/*      */ 
/*      */ 
/*  274 */           if (image == null) {
/*  275 */             sTryFile = origFile.substring(0, index) + suffix.replace('-', '_') + origFile.substring(index);
/*      */             
/*  277 */             image = loadImage(this.display, cl, sTryFile, sKey);
/*      */           }
/*      */         }
/*      */         else
/*      */         {
/*  282 */           String sTryFile = origFile.substring(0, index) + suffix + origFile.substring(index);
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*  287 */           image = getImageFromMap(sTryFile);
/*      */           
/*  289 */           if (image == null)
/*      */           {
/*  291 */             image = loadImage(this.display, cl, sTryFile, sTryFile);
/*      */             
/*  293 */             if (isRealImage(image))
/*      */             {
/*  295 */               image_key = sTryFile;
/*      */               
/*  297 */               addImage(image_key, image);
/*      */             }
/*  299 */             else if (sTryFile.matches(".*[-_]disabled.*"))
/*      */             {
/*  301 */               String sTryFileNonDisabled = sTryFile.replaceAll("[-_]disabled", "");
/*      */               
/*  303 */               image = getImageFromMap(sTryFileNonDisabled);
/*      */               
/*  305 */               if (!isRealImage(image))
/*      */               {
/*  307 */                 image = loadImage(this.display, cl, sTryFileNonDisabled, sTryFileNonDisabled);
/*      */                 
/*      */ 
/*  310 */                 if (isRealImage(image))
/*      */                 {
/*  312 */                   addImage(sTryFileNonDisabled, image);
/*      */                 }
/*      */               }
/*      */               
/*      */ 
/*  317 */               if (isRealImage(image))
/*      */               {
/*  319 */                 image = fadeImage(image);
/*      */                 
/*  321 */                 image_key = sTryFile;
/*      */                 
/*  323 */                 addImage(image_key, image);
/*      */                 
/*  325 */                 releaseImage(sTryFileNonDisabled);
/*      */               }
/*      */             }
/*      */           }
/*      */           else {
/*  330 */             image_key = sTryFile;
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  335 */       if (!isRealImage(image))
/*      */       {
/*  337 */         String temp_key = sKey + "-[multi-load-temp]";
/*      */         
/*  339 */         image = getImageFromMap(temp_key);
/*      */         
/*  341 */         if (isRealImage(image))
/*      */         {
/*  343 */           image_key = temp_key;
/*      */         }
/*      */         else
/*      */         {
/*  347 */           image = loadImage(this.display, cl, values[locationStart], sKey);
/*      */           
/*  349 */           if (isRealImage(image))
/*      */           {
/*  351 */             image_key = temp_key;
/*      */             
/*  353 */             addImage(image_key, image);
/*      */           }
/*      */         }
/*      */       }
/*      */       
/*  358 */       if (isRealImage(image)) {
/*  359 */         Rectangle bounds = image.getBounds();
/*  360 */         if (useIndex == -1) {
/*  361 */           images = new Image[(bounds.width + splitX - 1) / splitX];
/*  362 */           for (int i = 0; i < images.length; i++) {
/*  363 */             Image imgBG = Utils.createAlphaImage(this.display, splitX, bounds.height, (byte)0);
/*      */             
/*  365 */             int pos = i * splitX;
/*      */             try {
/*  367 */               images[i] = Utils.blitImage(this.display, image, new Rectangle(pos, 0, Math.min(splitX, bounds.width - pos), bounds.height), imgBG, new Point(0, 0));
/*      */             }
/*      */             catch (Exception e)
/*      */             {
/*  371 */               Debug.out(e);
/*      */             }
/*  373 */             imgBG.dispose();
/*      */           }
/*      */         } else {
/*  376 */           images = new Image[1];
/*  377 */           Image imgBG = Utils.createAlphaImage(this.display, splitX, bounds.height, (byte)0);
/*      */           try {
/*  379 */             int pos = useIndex * splitX;
/*      */             
/*  381 */             images[0] = Utils.blitImage(this.display, image, new Rectangle(pos, 0, Math.min(splitX, bounds.width - pos), bounds.height), imgBG, new Point(0, 0));
/*      */           }
/*      */           catch (Exception e)
/*      */           {
/*  385 */             Debug.out(e);
/*      */           }
/*  387 */           imgBG.dispose();
/*      */         }
/*      */         
/*  390 */         if (image_key != null)
/*      */         {
/*  392 */           releaseImage(image_key);
/*      */         }
/*      */         else
/*      */         {
/*  396 */           image.dispose();
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  401 */     return images;
/*      */   }
/*      */   
/*      */   private Image loadImage(Display display, ClassLoader cl, String res, String sKey)
/*      */   {
/*  406 */     Image img = null;
/*      */     
/*      */ 
/*  409 */     if (res == null) {
/*  410 */       for (int i = 0; i < this.sSuffixChecks.length; i++) {
/*  411 */         String sSuffix = this.sSuffixChecks[i];
/*      */         
/*  413 */         if (sKey.endsWith(sSuffix))
/*      */         {
/*  415 */           String sParentName = sKey.substring(0, sKey.length() - sSuffix.length());
/*      */           
/*  417 */           String sParentFile = null;
/*  418 */           for (SkinProperties sp : this.skinProperties) {
/*  419 */             sParentFile = sp.getStringValue(sParentName);
/*  420 */             if (sParentFile != null) {
/*  421 */               if (cl != null) break;
/*  422 */               cl = sp.getClassLoader(); break;
/*      */             }
/*      */           }
/*      */           
/*      */ 
/*  427 */           if (sParentFile != null) {
/*  428 */             int index = sParentFile.lastIndexOf('.');
/*  429 */             if (index > 0) {
/*  430 */               String sTryFile = sParentFile.substring(0, index) + sSuffix + sParentFile.substring(index);
/*      */               
/*  432 */               img = loadImage(display, cl, sTryFile, sKey);
/*      */               
/*  434 */               if (img != null) {
/*      */                 break;
/*      */               }
/*      */               
/*  438 */               sTryFile = sParentFile.substring(0, index) + sSuffix.replace('-', '_') + sParentFile.substring(index);
/*      */               
/*  440 */               img = loadImage(display, cl, sTryFile, sKey);
/*      */               
/*  442 */               if (img != null) {
/*      */                 break;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*  451 */     if (img == null) {
/*      */       try {
/*  453 */         if ((cl != null) && (res != null)) {
/*  454 */           InputStream is = cl.getResourceAsStream(res);
/*  455 */           if (is != null) {
/*      */             try {
/*  457 */               img = new Image(display, is);
/*      */             }
/*      */             finally
/*      */             {
/*  461 */               is.close();
/*      */             }
/*      */           }
/*      */         }
/*      */         
/*  466 */         if (img == null)
/*      */         {
/*      */ 
/*      */ 
/*  470 */           if ((res != null) && (res.contains("_disabled."))) {
/*  471 */             String id = sKey.substring(0, sKey.length() - 9);
/*  472 */             Image imgToFade = getImage(id);
/*  473 */             if (isRealImage(imgToFade)) {
/*  474 */               img = fadeImage(imgToFade);
/*      */             }
/*  476 */             releaseImage(id);
/*  477 */           } else if (sKey.endsWith("-gray")) {
/*  478 */             String id = sKey.substring(0, sKey.length() - 5);
/*  479 */             Image imgToGray = getImage(id);
/*  480 */             if (isRealImage(imgToGray)) {
/*  481 */               img = new Image(display, imgToGray, 2);
/*      */             }
/*  483 */             releaseImage(id);
/*      */           }
/*      */         }
/*      */         else {
/*  487 */           img = Utils.adjustPXForDPI(display, img);
/*      */         }
/*      */       } catch (Throwable e) {
/*  490 */         System.err.println("ImageRepository:loadImage:: Resource not found: " + res + "\n" + e);
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*  495 */     return img;
/*      */   }
/*      */   
/*      */   private Image fadeImage(Image imgToFade) {
/*  499 */     ImageData imageData = imgToFade.getImageData();
/*      */     Image img;
/*      */     Image img;
/*  502 */     if (imageData.alphaData != null) {
/*  503 */       if (this.disabledOpacity == -1) {
/*  504 */         for (int i = 0; i < imageData.alphaData.length; i++) {
/*  505 */           imageData.alphaData[i] = ((byte)((imageData.alphaData[i] & 0xFF) >> 3));
/*      */         }
/*      */       } else {
/*  508 */         for (int i = 0; i < imageData.alphaData.length; i++) {
/*  509 */           imageData.alphaData[i] = ((byte)((imageData.alphaData[i] & 0xFF) * this.disabledOpacity / 100));
/*      */         }
/*      */       }
/*      */       
/*  513 */       img = new Image(this.display, imageData);
/*      */     } else {
/*  515 */       Rectangle bounds = imgToFade.getBounds();
/*  516 */       Image bg = Utils.createAlphaImage(this.display, bounds.width, bounds.height, (byte)0);
/*      */       
/*      */ 
/*  519 */       img = Utils.renderTransparency(this.display, bg, imgToFade, new Point(0, 0), this.disabledOpacity == -1 ? 64 : this.disabledOpacity * 255 / 100);
/*      */       
/*      */ 
/*  522 */       bg.dispose();
/*      */     }
/*  524 */     return img;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void unLoadImages()
/*      */   {
/*  542 */     for (ImageLoaderRefInfo imageInfo : this._mapImages.values()) {
/*  543 */       Image[] images = imageInfo.getImages();
/*  544 */       if (images != null) {
/*  545 */         for (int i = 0; i < images.length; i++) {
/*  546 */           Image image = images[i];
/*  547 */           if (isRealImage(image)) {
/*  548 */             image.dispose();
/*      */           }
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private ImageLoaderRefInfo getRefInfoFromImageMap(String key)
/*      */   {
/*  560 */     return (ImageLoaderRefInfo)this._mapImages.get(key);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private void putRefInfoToImageMap(String key, ImageLoaderRefInfo info)
/*      */   {
/*  568 */     ImageLoaderRefInfo existing = (ImageLoaderRefInfo)this._mapImages.put(key, info);
/*      */     
/*  570 */     if (existing != null)
/*      */     {
/*  572 */       Image[] images = existing.getImages();
/*  573 */       if ((images != null) && (images.length > 0))
/*      */       {
/*  575 */         Debug.out("P: existing found! " + key + " -> " + existing.getString());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private ImageLoaderRefInfo putIfAbsentRefInfoToImageMap(String key, ImageLoaderRefInfo info)
/*      */   {
/*  585 */     ImageLoaderRefInfo x = (ImageLoaderRefInfo)this._mapImages.putIfAbsent(key, info);
/*      */     
/*  587 */     if (x != null)
/*      */     {
/*  589 */       Image[] images = x.getImages();
/*  590 */       if ((images != null) && (images.length > 0))
/*      */       {
/*  592 */         Debug.out("PIA: existing found! " + key + " -> " + x.getString());
/*      */       }
/*      */     }
/*      */     
/*  596 */     return x;
/*      */   }
/*      */   
/*      */   protected Image getImageFromMap(String sKey) {
/*  600 */     Image[] imagesFromMap = getImagesFromMap(sKey);
/*  601 */     if (imagesFromMap.length == 0) {
/*  602 */       return null;
/*      */     }
/*  604 */     return imagesFromMap[0];
/*      */   }
/*      */   
/*      */   protected Image[] getImagesFromMap(String sKey) {
/*  608 */     if (sKey == null) {
/*  609 */       return new Image[0];
/*      */     }
/*      */     
/*  612 */     ImageLoaderRefInfo imageInfo = getRefInfoFromImageMap(sKey);
/*  613 */     if ((imageInfo != null) && (imageInfo.getImages() != null)) {
/*  614 */       imageInfo.addref();
/*      */       
/*      */ 
/*      */ 
/*  618 */       return imageInfo.getImages();
/*      */     }
/*      */     
/*  621 */     return new Image[0];
/*      */   }
/*      */   
/*      */   public Image[] getImages(String sKey) {
/*  625 */     if (sKey == null) {
/*  626 */       return new Image[0];
/*      */     }
/*      */     
/*  629 */     if (!Utils.isThisThreadSWT()) {
/*  630 */       Debug.out("getImages called on non-SWT thread");
/*  631 */       return new Image[0];
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*  636 */     if ((sKey.startsWith("http://")) && (sKey.endsWith("-gray"))) {
/*  637 */       sKey = sKey.substring(0, sKey.length() - 5);
/*      */     }
/*      */     
/*  640 */     ImageLoaderRefInfo imageInfo = getRefInfoFromImageMap(sKey);
/*  641 */     if ((imageInfo != null) && (imageInfo.getImages() != null)) {
/*  642 */       imageInfo.addref();
/*      */       
/*      */ 
/*      */ 
/*  646 */       return imageInfo.getImages();
/*      */     }
/*      */     
/*      */ 
/*  650 */     String[] locations = null;
/*  651 */     ClassLoader cl = null;
/*  652 */     for (SkinProperties sp : this.skinProperties) {
/*  653 */       locations = sp.getStringArray(sKey);
/*  654 */       if ((locations != null) && (locations.length > 0)) {
/*  655 */         cl = sp.getClassLoader();
/*  656 */         break;
/*      */       }
/*      */     }
/*      */     
/*      */     Image[] images;
/*  661 */     if ((locations == null) || (locations.length == 0)) {
/*  662 */       Image[] images = findResources(sKey);
/*      */       
/*  664 */       if (images == null) {
/*  665 */         String cache_key = sKey.hashCode() + ".ico";
/*  666 */         if (this.cached_resources.contains(cache_key)) {
/*  667 */           File cache = new File(this.cache_dir, cache_key);
/*  668 */           if (cache.exists()) {
/*      */             try {
/*  670 */               FileInputStream fis = new FileInputStream(cache);
/*      */               try
/*      */               {
/*  673 */                 byte[] imageBytes = FileUtil.readInputStreamAsByteArray(fis);
/*  674 */                 InputStream is = new ByteArrayInputStream(imageBytes);
/*      */                 
/*  676 */                 org.eclipse.swt.graphics.ImageLoader swtImageLoader = new org.eclipse.swt.graphics.ImageLoader();
/*  677 */                 ImageData[] imageDatas = swtImageLoader.load(is);
/*  678 */                 images = new Image[imageDatas.length];
/*  679 */                 for (int i = 0; i < imageDatas.length; i++) {
/*  680 */                   images[i] = new Image(Display.getCurrent(), imageDatas[i]);
/*      */                 }
/*      */                 
/*      */ 
/*      */                 try {}catch (IOException e) {}
/*      */               }
/*      */               finally
/*      */               {
/*  688 */                 fis.close();
/*      */               }
/*      */             } catch (Throwable e) {
/*  691 */               Debug.printStackTrace(e);
/*      */             }
/*      */           }
/*      */         } else {
/*  695 */           this.cached_resources.remove(cache_key);
/*      */         }
/*      */         
/*  698 */         if (images == null) {
/*  699 */           images = new Image[0];
/*      */         }
/*      */       }
/*      */       
/*  703 */       for (int i = 0; i < images.length; i++) {
/*  704 */         if (images[i] == null) {
/*  705 */           images[i] = getNoImage(sKey);
/*      */         }
/*      */       }
/*      */     } else {
/*  709 */       images = parseValuesString(cl, sKey, locations, "");
/*      */     }
/*      */     
/*  712 */     ImageLoaderRefInfo info = new ImageLoaderRefInfo(images);
/*  713 */     putRefInfoToImageMap(sKey, info);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*  718 */     return images;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Image getImage(String sKey)
/*      */   {
/*  727 */     int pos = sKey.indexOf('#');
/*      */     
/*  729 */     if (pos == -1)
/*      */     {
/*  731 */       return getImageSupport(sKey);
/*      */     }
/*      */     
/*      */ 
/*  735 */     ImageLoaderRefInfo existing = getRefInfoFromImageMap(sKey);
/*      */     
/*  737 */     if (existing != null)
/*      */     {
/*      */ 
/*      */ 
/*  741 */       return getImageSupport(sKey);
/*      */     }
/*      */     
/*  744 */     String basisKey = sKey.substring(0, pos);
/*      */     
/*  746 */     Image basis = getImageSupport(basisKey);
/*      */     
/*  748 */     Image result = null;
/*      */     
/*  750 */     if (isRealImage(basis)) {
/*      */       try
/*      */       {
/*  753 */         long l = Long.parseLong(sKey.substring(pos + 1), 16);
/*      */         
/*  755 */         int to_red = (int)(l >> 16 & 0xFF);
/*  756 */         int to_green = (int)(l >> 8 & 0xFF);
/*  757 */         int to_blue = (int)(l & 0xFF);
/*      */         
/*  759 */         ImageData original_id = basis.getImageData();
/*      */         
/*  761 */         Image tempImg = new Image(basis.getDevice(), basis.getBounds());
/*      */         
/*  763 */         GC tempGC = new GC(tempImg);
/*      */         
/*  765 */         tempGC.drawImage(basis, 0, 0);
/*      */         
/*  767 */         tempGC.dispose();
/*      */         
/*  769 */         ImageData id = tempImg.getImageData();
/*      */         
/*  771 */         tempImg.dispose();
/*      */         
/*  773 */         int[] pixels = new int[id.width * id.height];
/*      */         
/*  775 */         id.getPixels(0, 0, pixels.length, pixels, 0);
/*      */         
/*  777 */         PaletteData palette = id.palette;
/*      */         
/*      */ 
/*      */ 
/*  781 */         if (palette.isDirect)
/*      */         {
/*  783 */           int redMask = palette.redMask;
/*  784 */           int greenMask = palette.greenMask;
/*  785 */           int blueMask = palette.blueMask;
/*  786 */           int redShift = palette.redShift;
/*  787 */           int greenShift = palette.greenShift;
/*  788 */           int blueShift = palette.blueShift;
/*      */           
/*  790 */           int[] rgbs = new int[id.width * id.height];
/*      */           
/*  792 */           for (int i = 0; i < pixels.length; i++)
/*      */           {
/*  794 */             int pixel = pixels[i];
/*      */             
/*  796 */             int red = pixel & redMask;
/*  797 */             red = redShift < 0 ? red >>> -redShift : red << redShift;
/*  798 */             int green = pixel & greenMask;
/*  799 */             green = greenShift < 0 ? green >>> -greenShift : green << greenShift;
/*  800 */             int blue = pixel & blueMask;
/*  801 */             blue = blueShift < 0 ? blue >>> -blueShift : blue << blueShift;
/*      */             
/*  803 */             rgbs[i] = (red << 16 | green << 8 | blue);
/*      */           }
/*      */           
/*  806 */           Arrays.sort(rgbs);
/*      */           
/*  808 */           int curr = -1;
/*  809 */           int len = 0;
/*      */           
/*  811 */           int max_len = -1;
/*  812 */           int max_rgb = 0;
/*      */           
/*  814 */           for (int i = 0; i < rgbs.length; i++)
/*      */           {
/*  816 */             int x = rgbs[i];
/*      */             
/*  818 */             if ((x != 0) && (x != 16777215))
/*      */             {
/*      */ 
/*      */ 
/*  822 */               if (x == curr) {
/*  823 */                 len++;
/*  824 */                 if (len > max_len) {
/*  825 */                   max_rgb = x;
/*  826 */                   max_len = len;
/*      */                 }
/*      */               } else {
/*  829 */                 curr = x;
/*  830 */                 len = 1;
/*      */               }
/*      */             }
/*      */           }
/*  834 */           to_red = redShift < 0 ? to_red << -redShift : to_red >>> -redShift;
/*  835 */           to_red &= redMask;
/*  836 */           to_green = greenShift < 0 ? to_green << -greenShift : to_green >>> -greenShift;
/*  837 */           to_green &= greenMask;
/*  838 */           to_blue = blueShift < 0 ? to_blue << -blueShift : to_blue >>> -blueShift;
/*  839 */           to_blue &= blueMask;
/*      */           
/*  841 */           int to_rgb = to_red | to_green | to_blue;
/*      */           
/*      */ 
/*  844 */           byte[] alphaData = null;
/*      */           
/*  846 */           if (original_id.alphaData != null)
/*      */           {
/*  848 */             id.alphaData = original_id.alphaData;
/*      */           }
/*  850 */           else if (original_id.transparentPixel >= 0)
/*      */           {
/*  852 */             alphaData = new byte[pixels.length];
/*      */             
/*  854 */             Arrays.fill(alphaData, (byte)-1);
/*      */             
/*  856 */             id.alphaData = alphaData;
/*      */             
/*  858 */             int[] original_pixels = new int[id.width * id.height];
/*      */             
/*  860 */             original_id.getPixels(0, 0, original_pixels.length, original_pixels, 0);
/*      */             
/*  862 */             for (int i = 0; i < original_pixels.length; i++)
/*      */             {
/*  864 */               if (original_pixels[i] == original_id.transparentPixel)
/*      */               {
/*  866 */                 alphaData[i] = 0;
/*      */               }
/*      */             }
/*      */           }
/*      */           
/*  871 */           for (int i = 0; i < pixels.length; i++)
/*      */           {
/*  873 */             int pixel = pixels[i];
/*      */             
/*  875 */             int red = pixel & redMask;
/*  876 */             red = redShift < 0 ? red >>> -redShift : red << redShift;
/*  877 */             int green = pixel & greenMask;
/*  878 */             green = greenShift < 0 ? green >>> -greenShift : green << greenShift;
/*  879 */             int blue = pixel & blueMask;
/*  880 */             blue = blueShift < 0 ? blue >>> -blueShift : blue << blueShift;
/*      */             
/*  882 */             int rgb = red << 16 | green << 8 | blue;
/*      */             
/*  884 */             if (rgb == max_rgb)
/*      */             {
/*  886 */               pixels[i] = to_rgb;
/*      */             }
/*      */           }
/*      */           
/*  890 */           id.setPixels(0, 0, pixels.length, pixels, 0);
/*      */           
/*  892 */           result = new Image(basis.getDevice(), id);
/*      */         }
/*      */       }
/*      */       catch (Throwable e) {
/*  896 */         Debug.out(e);
/*      */       }
/*      */       finally
/*      */       {
/*  900 */         releaseImage(basisKey);
/*      */       }
/*      */     }
/*      */     
/*  904 */     if (result == null)
/*      */     {
/*  906 */       result = getNoImage(sKey);
/*      */     }
/*      */     
/*  909 */     ImageLoaderRefInfo info = new ImageLoaderRefInfo(new Image[] { result });
/*      */     
/*  911 */     putRefInfoToImageMap(sKey, info);
/*      */     
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  918 */     return result;
/*      */   }
/*      */   
/*      */   private Image getImageSupport(String sKey)
/*      */   {
/*  923 */     Image[] images = getImages(sKey);
/*  924 */     if ((images == null) || (images.length == 0) || (images[0] == null) || (images[0].isDisposed())) {
/*  925 */       return getNoImage(sKey);
/*      */     }
/*  927 */     return images[0];
/*      */   }
/*      */   
/*      */   public long releaseImage(String sKey) {
/*  931 */     if (sKey == null) {
/*  932 */       return 0L;
/*      */     }
/*  934 */     ImageLoaderRefInfo imageInfo = getRefInfoFromImageMap(sKey);
/*  935 */     if (imageInfo != null) {
/*  936 */       imageInfo.unref();
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  951 */       return imageInfo.getRefCount();
/*      */     }
/*      */     
/*  954 */     return 0L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addImage(String key, Image image)
/*      */   {
/*  967 */     if (!Utils.isThisThreadSWT()) {
/*  968 */       Debug.out("addImage called on non-SWT thread");
/*  969 */       return;
/*      */     }
/*  971 */     ImageLoaderRefInfo existing = putIfAbsentRefInfoToImageMap(key, new ImageLoaderRefInfo(image));
/*      */     
/*  973 */     if (existing != null)
/*      */     {
/*  975 */       existing.setImages(new Image[] { image });
/*      */       
/*      */ 
/*  978 */       existing.addref();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addImage(String key, Image[] images)
/*      */   {
/*  986 */     if (!Utils.isThisThreadSWT()) {
/*  987 */       Debug.out("addImage called on non-SWT thread");
/*  988 */       return;
/*      */     }
/*  990 */     ImageLoaderRefInfo existing = putIfAbsentRefInfoToImageMap(key, new ImageLoaderRefInfo(images));
/*      */     
/*  992 */     if (existing != null)
/*      */     {
/*  994 */       existing.setImages(images);
/*  995 */       existing.addref();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void logRefCount(String key, ImageLoaderRefInfo info, boolean inc) {}
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void addImageNoDipose(String key, Image image)
/*      */   {
/* 1030 */     if (!Utils.isThisThreadSWT()) {
/* 1031 */       Debug.out("addImageNoDispose called on non-SWT thread");
/* 1032 */       return;
/*      */     }
/* 1034 */     ImageLoaderRefInfo existing = putIfAbsentRefInfoToImageMap(key, new ImageLoaderRefInfo(image));
/*      */     
/* 1036 */     if (existing != null) {
/* 1037 */       existing.setNonDisposable();
/*      */       
/* 1039 */       existing.setImages(new Image[] { image });
/*      */       
/*      */ 
/* 1042 */       existing.addref();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public static Image getNoImage()
/*      */   {
/* 1050 */     return getNoImage("explicit");
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private static Image getNoImage(String key)
/*      */   {
/* 1059 */     if (noImage == null) {
/* 1060 */       Display display = Display.getDefault();
/* 1061 */       int SIZE = 10;
/* 1062 */       noImage = new Image(display, 10, 10);
/* 1063 */       GC gc = new GC(noImage);
/* 1064 */       gc.setBackground(display.getSystemColor(7));
/* 1065 */       gc.fillRectangle(0, 0, 10, 10);
/* 1066 */       gc.setBackground(display.getSystemColor(3));
/* 1067 */       gc.drawRectangle(0, 0, 9, 9);
/* 1068 */       gc.dispose();
/*      */     }
/* 1070 */     return noImage;
/*      */   }
/*      */   
/*      */   public boolean imageExists(String name) {
/* 1074 */     boolean exists = isRealImage(getImage(name));
/*      */     
/*      */ 
/* 1077 */     releaseImage(name);
/*      */     
/* 1079 */     return exists;
/*      */   }
/*      */   
/*      */   public boolean imageAdded_NoSWT(String name) {
/* 1083 */     return this._mapImages.containsKey(name);
/*      */   }
/*      */   
/*      */   public boolean imageAdded(String name) {
/* 1087 */     Image[] images = getImages(name);
/* 1088 */     boolean added = (images != null) && (images.length > 0);
/* 1089 */     releaseImage(name);
/* 1090 */     return added;
/*      */   }
/*      */   
/*      */   public static boolean isRealImage(Image image) {
/* 1094 */     if ((image == null) || (image.isDisposed())) {
/* 1095 */       return false;
/*      */     }
/* 1097 */     if (noImage != null) {
/* 1098 */       return image != noImage;
/*      */     }
/* 1100 */     return image != getNoImage(null);
/*      */   }
/*      */   
/*      */   public int getAnimationDelay(String sKey) {
/* 1104 */     for (SkinProperties sp : this.skinProperties) {
/* 1105 */       int delay = sp.getIntValue(sKey + ".delay", -1);
/* 1106 */       if (delay >= 0) {
/* 1107 */         return delay;
/*      */       }
/*      */     }
/* 1110 */     return 100;
/*      */   }
/*      */   
/*      */   public Image getUrlImage(String url, ImageDownloaderListener l) {
/* 1114 */     return getUrlImage(url, null, l);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Image getUrlImage(String url, final Point maxSize, final ImageDownloaderListener l)
/*      */   {
/* 1123 */     if (!Utils.isThisThreadSWT()) {
/* 1124 */       Debug.out("getUrlImage called on non-SWT thread");
/* 1125 */       return null;
/*      */     }
/* 1127 */     if ((l == null) || (url == null)) {
/* 1128 */       return null;
/*      */     }
/*      */     String imageKey;
/*      */     String imageKey;
/* 1132 */     if (maxSize == null) {
/* 1133 */       imageKey = url;
/*      */     } else {
/* 1135 */       imageKey = maxSize.x + "x" + maxSize.y + ";" + url;
/*      */     }
/*      */     
/* 1138 */     if (imageExists(imageKey)) {
/* 1139 */       Image image = getImage(imageKey);
/* 1140 */       l.imageDownloaded(image, true);
/* 1141 */       return image;
/*      */     }
/*      */     
/* 1144 */     final String cache_key = url.hashCode() + ".ico";
/*      */     
/* 1146 */     final File cache_file = new File(this.cache_dir, cache_key);
/*      */     
/* 1148 */     if (this.cached_resources.contains(cache_key))
/*      */     {
/* 1150 */       if (cache_file.exists()) {
/*      */         try {
/* 1152 */           FileInputStream fis = new FileInputStream(cache_file);
/*      */           try
/*      */           {
/* 1155 */             byte[] imageBytes = FileUtil.readInputStreamAsByteArray(fis);
/* 1156 */             InputStream is = new ByteArrayInputStream(imageBytes);
/* 1157 */             Image image = new Image(Display.getCurrent(), is);
/*      */             
/*      */             try {}catch (IOException e) {}
/*      */             
/*      */             Image newImage;
/* 1162 */             if (maxSize != null) {
/* 1163 */               newImage = resizeImageIfLarger(image, maxSize);
/* 1164 */               if (newImage != null) {
/* 1165 */                 image.dispose();
/* 1166 */                 image = newImage;
/*      */               }
/*      */             }
/* 1169 */             putRefInfoToImageMap(imageKey, new ImageLoaderRefInfo(image));
/* 1170 */             l.imageDownloaded(image, true);
/* 1171 */             return image;
/*      */           } finally {
/* 1173 */             fis.close();
/*      */           }
/*      */           
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/* 1181 */           this.cached_resources.remove(cache_key);
/*      */         }
/*      */         catch (Throwable e)
/*      */         {
/* 1176 */           System.err.println(e.getMessage() + " for " + url + " at " + cache_file);
/* 1177 */           Debug.printStackTrace(e);
/*      */         }
/*      */       }
/*      */     }
/*      */     
/*      */ 
/*      */ 
/*      */ 
/* 1185 */     final String f_imageKey = imageKey;
/* 1186 */     ImageBytesDownloader.loadImage(url, new ImageBytesDownloader.ImageDownloaderListener()
/*      */     {
/*      */       public void imageDownloaded(final byte[] imageBytes) {
/* 1189 */         Utils.execSWTThread(new AERunnable()
/*      */         {
/*      */           public void runSupport()
/*      */           {
/* 1193 */             if (ImageLoader.this.imageExists(ImageLoader.2.this.val$f_imageKey)) {
/* 1194 */               Image image = ImageLoader.this.getImage(ImageLoader.2.this.val$f_imageKey);
/* 1195 */               ImageLoader.2.this.val$l.imageDownloaded(image, false);
/* 1196 */               return;
/*      */             }
/* 1198 */             FileUtil.writeBytesAsFile(ImageLoader.2.this.val$cache_file.getAbsolutePath(), imageBytes);
/* 1199 */             ImageLoader.this.cached_resources.add(ImageLoader.2.this.val$cache_key);
/* 1200 */             InputStream is = new ByteArrayInputStream(imageBytes);
/*      */             try {
/* 1202 */               Image image = new Image(Display.getCurrent(), is);
/*      */               try {
/* 1204 */                 is.close();
/*      */               }
/*      */               catch (IOException e) {}
/* 1207 */               if (ImageLoader.2.this.val$maxSize != null) {
/* 1208 */                 Image newImage = ImageLoader.this.resizeImageIfLarger(image, ImageLoader.2.this.val$maxSize);
/* 1209 */                 if (newImage != null) {
/* 1210 */                   image.dispose();
/* 1211 */                   image = newImage;
/*      */                 }
/*      */               }
/* 1214 */               ImageLoader.this.putRefInfoToImageMap(ImageLoader.2.this.val$f_imageKey, new ImageLoaderRefInfo(image));
/* 1215 */               ImageLoader.2.this.val$l.imageDownloaded(image, false);
/*      */             }
/*      */             catch (SWTException swte) {
/* 1218 */               System.err.println(swte.getMessage() + " for " + ImageLoader.2.this.val$f_imageKey + " at " + ImageLoader.2.this.val$cache_file);
/*      */             }
/*      */           }
/*      */         });
/*      */       }
/* 1223 */     });
/* 1224 */     return null;
/*      */   }
/*      */   
/*      */   private Image resizeImageIfLarger(Image image, Point maxSize)
/*      */   {
/* 1229 */     if ((image == null) || (image.isDisposed())) {
/* 1230 */       return null;
/*      */     }
/*      */     
/* 1233 */     Rectangle bounds = image.getBounds();
/* 1234 */     if ((maxSize.y > 0) && (bounds.height > maxSize.y)) {
/* 1235 */       int newX = bounds.width * maxSize.y / bounds.height;
/* 1236 */       if ((maxSize.x <= 0) || (newX <= maxSize.x)) {
/* 1237 */         ImageData scaledTo = image.getImageData().scaledTo(newX, maxSize.y);
/* 1238 */         Device device = image.getDevice();
/* 1239 */         return new Image(device, scaledTo);
/*      */       }
/*      */     }
/* 1242 */     if ((maxSize.x > 0) && (bounds.width > maxSize.x)) {
/* 1243 */       int newY = bounds.height * maxSize.x / bounds.width;
/* 1244 */       ImageData scaledTo = image.getImageData().scaledTo(maxSize.x, newY);
/* 1245 */       Device device = image.getDevice();
/* 1246 */       return new Image(device, scaledTo);
/*      */     }
/* 1248 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void generate(IndentWriter writer)
/*      */   {
/* 1259 */     writer.println("ImageLoader for " + this.skinProperties);
/* 1260 */     writer.indent();
/* 1261 */     long[] sizeCouldBeFree = { 0L };
/*      */     
/*      */ 
/* 1264 */     long[] totalSizeEstimate = { 0L };
/*      */     
/*      */     try
/*      */     {
/* 1268 */       writer.indent();
/*      */       try {
/* 1270 */         writer.println("Non-Disposable:");
/* 1271 */         writer.indent();
/* 1272 */         for (String key : this._mapImages.keySet()) {
/* 1273 */           ImageLoaderRefInfo info = (ImageLoaderRefInfo)this._mapImages.get(key);
/* 1274 */           if (info.isNonDisposable())
/*      */           {
/*      */ 
/* 1277 */             writeEvidenceLine(writer, key, info, totalSizeEstimate, sizeCouldBeFree);
/*      */           }
/*      */         }
/* 1280 */         writer.exdent();
/* 1281 */         writer.println("Disposable:");
/* 1282 */         writer.indent();
/* 1283 */         for (String key : this._mapImages.keySet()) {
/* 1284 */           ImageLoaderRefInfo info = (ImageLoaderRefInfo)this._mapImages.get(key);
/* 1285 */           if (!info.isNonDisposable())
/*      */           {
/*      */ 
/* 1288 */             writeEvidenceLine(writer, key, info, totalSizeEstimate, sizeCouldBeFree);
/*      */           }
/*      */         }
/*      */       }
/*      */       finally {}
/*      */       
/*      */ 
/* 1295 */       if (totalSizeEstimate[0] > 0L) {
/* 1296 */         writer.println(totalSizeEstimate[0] / 1024L + "k estimated used for images");
/*      */       }
/*      */       
/* 1299 */       if (sizeCouldBeFree[0] > 0L) {
/* 1300 */         writer.println(sizeCouldBeFree[0] / 1024L + "k could be freed");
/*      */       }
/*      */     } finally {
/* 1303 */       writer.exdent();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   private void writeEvidenceLine(IndentWriter writer, String key, ImageLoaderRefInfo info, long[] totalSizeEstimate, long[] sizeCouldBeFree)
/*      */   {
/* 1313 */     String line = info.getRefCount() + "] " + key;
/* 1314 */     if (Utils.isThisThreadSWT()) {
/* 1315 */       long sizeEstimate = 0L;
/* 1316 */       Image[] images = info.getImages();
/* 1317 */       for (int i = 0; i < images.length; i++) {
/* 1318 */         Image img = images[i];
/* 1319 */         if (img != null) {
/* 1320 */           if (img.isDisposed()) {
/* 1321 */             line = line + "; *DISPOSED*";
/*      */           } else {
/* 1323 */             Rectangle bounds = img.getBounds();
/* 1324 */             long est = bounds.width * bounds.height * 4L;
/* 1325 */             sizeEstimate += est;
/* 1326 */             totalSizeEstimate[0] += est;
/* 1327 */             if (info.canDispose()) {
/* 1328 */               sizeCouldBeFree[0] += est;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 1333 */       line = line + "; est " + sizeEstimate + " bytes";
/*      */     }
/* 1335 */     writer.println(line);
/*      */   }
/*      */   
/*      */   public void addSkinProperties(SkinProperties skinProperties) {
/* 1339 */     if (skinProperties == null) {
/* 1340 */       return;
/*      */     }
/* 1342 */     this.skinProperties.add(skinProperties);
/* 1343 */     this.disabledOpacity = skinProperties.getIntValue("imageloader.disabled-opacity", -1);
/*      */     
/* 1345 */     this.notFound.clear();
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void collectGarbage()
/*      */   {
/* 1354 */     Utils.execSWTThread(new AERunnable() {
/*      */       public void runSupport() {
/* 1356 */         int numRemoved = 0;
/* 1357 */         for (Iterator<String> iter = ImageLoader.this._mapImages.keySet().iterator(); iter.hasNext();) {
/* 1358 */           String key = (String)iter.next();
/* 1359 */           ImageLoaderRefInfo info = (ImageLoaderRefInfo)ImageLoader.this._mapImages.get(key);
/*      */           
/*      */ 
/*      */ 
/* 1363 */           if ((info != null) && (info.canDispose()))
/*      */           {
/*      */ 
/*      */ 
/* 1367 */             iter.remove();
/* 1368 */             numRemoved++;
/*      */             
/* 1370 */             Image[] images = info.getImages();
/* 1371 */             for (int j = 0; j < images.length; j++) {
/* 1372 */               Image image = images[j];
/* 1373 */               if (ImageLoader.isRealImage(image)) {
/* 1374 */                 image.dispose();
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void setLabelImage(Label label, final String key)
/*      */   {
/* 1391 */     Image bg = getImage(key);
/* 1392 */     label.setImage(bg);
/* 1393 */     label.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/* 1395 */         ImageLoader.this.releaseImage(key);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public Image setButtonImage(Button btn, final String key) {
/* 1401 */     Image bg = getImage(key);
/* 1402 */     btn.setImage(bg);
/* 1403 */     btn.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/* 1405 */         ImageLoader.this.releaseImage(key);
/*      */       }
/*      */       
/* 1408 */     });
/* 1409 */     return bg;
/*      */   }
/*      */   
/*      */   public void setBackgroundImage(Control control, final String key) {
/* 1413 */     Image bg = getImage(key);
/* 1414 */     control.setBackgroundImage(bg);
/* 1415 */     control.addDisposeListener(new DisposeListener() {
/*      */       public void widgetDisposed(DisposeEvent e) {
/* 1417 */         ImageLoader.this.releaseImage(key);
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */   public SkinProperties[] getSkinProperties() {
/* 1423 */     return (SkinProperties[])this.skinProperties.toArray(new SkinProperties[0]);
/*      */   }
/*      */   
/*      */   public static abstract interface ImageDownloaderListener
/*      */   {
/*      */     public abstract void imageDownloaded(Image paramImage, boolean paramBoolean);
/*      */   }
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/swt/imageloader/ImageLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */