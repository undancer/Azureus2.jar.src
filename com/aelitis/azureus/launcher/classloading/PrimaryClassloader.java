/*     */ package com.aelitis.azureus.launcher.classloading;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.ArrayList;
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
/*     */ public class PrimaryClassloader
/*     */   extends URLClassLoader
/*     */   implements PeeringClassloader
/*     */ {
/*  32 */   private final ArrayList peersLoaders = new ArrayList();
/*     */   
/*     */   private final ClassLoader packageLoader;
/*  35 */   private static final String packageName = PrimaryClassloader.class.getPackage().getName();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   private PrimaryClassloader()
/*     */   {
/*  42 */     super(generateURLs(), getSystemClassLoader().getParent());
/*  43 */     this.packageLoader = getSystemClassLoader();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   /**
/*     */    * @deprecated
/*     */    */
/*     */   public PrimaryClassloader(ClassLoader parent)
/*     */   {
/*  55 */     super(generateURLs(), parent.getParent());
/*  56 */     this.packageLoader = parent;
/*     */   }
/*     */   
/*     */   private static URL[] generateURLs()
/*     */   {
/*  61 */     String classpath = System.getProperty("java.class.path");
/*     */     
/*  63 */     String[] paths = classpath.split(File.pathSeparator);
/*  64 */     URL[] urls = new URL[paths.length + 1];
/*     */     try
/*     */     {
/*  67 */       for (int i = 0; i < paths.length; i++)
/*     */       {
/*  69 */         urls[i] = new File(paths[i]).getCanonicalFile().toURI().toURL();
/*  70 */         System.out.print(urls[i] + " ; ");
/*     */       }
/*     */       
/*  73 */       urls[(urls.length - 1)] = new File(".").getCanonicalFile().toURI().toURL();
/*  74 */       System.out.println(urls[(urls.length - 1)]);
/*     */     }
/*     */     catch (Exception e) {
/*  77 */       System.err.println("Invalid classpath detected\n");
/*  78 */       e.printStackTrace();
/*  79 */       System.exit(1);
/*     */     }
/*     */     
/*  82 */     return urls;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected Class loadClass(String name, boolean resolve)
/*     */     throws ClassNotFoundException
/*     */   {
/*     */     Class c;
/*     */     
/*     */ 
/*     */     try
/*     */     {
/*     */       Class c;
/*     */       
/*     */ 
/*  99 */       if (!name.startsWith(packageName)) {
/* 100 */         c = super.loadClass(name, resolve);
/*     */       } else {
/* 102 */         c = this.packageLoader.loadClass(name);
/*     */       }
/*     */     } catch (ClassNotFoundException e) {
/* 105 */       c = peerFindLoadedClass(name);
/* 106 */       if (c == null)
/* 107 */         c = peerLoadClass(name);
/* 108 */       if (c == null)
/* 109 */         throw e;
/* 110 */       if (resolve)
/* 111 */         resolveClass(c);
/*     */     }
/* 113 */     return c;
/*     */   }
/*     */   
/*     */   private Class peerFindLoadedClass(String className)
/*     */   {
/* 118 */     Class c = null;
/* 119 */     synchronized (this.peersLoaders)
/*     */     {
/* 121 */       for (int i = 0; (i < this.peersLoaders.size()) && (c == null); i++)
/*     */       {
/* 123 */         WeakReference ref = (WeakReference)this.peersLoaders.get(i);
/* 124 */         SecondaryClassLoader loader = (SecondaryClassLoader)ref.get();
/* 125 */         if (loader != null) {
/* 126 */           c = loader.findLoadedClassHelper(className);
/*     */         } else
/* 128 */           this.peersLoaders.remove(i--);
/*     */       }
/*     */     }
/* 131 */     return c;
/*     */   }
/*     */   
/*     */   private Class peerLoadClass(String className)
/*     */   {
/* 136 */     Class c = null;
/* 137 */     synchronized (this.peersLoaders)
/*     */     {
/* 139 */       for (int i = 0; (i < this.peersLoaders.size()) && (c == null); i++)
/*     */       {
/* 141 */         WeakReference ref = (WeakReference)this.peersLoaders.get(i);
/* 142 */         SecondaryClassLoader loader = (SecondaryClassLoader)ref.get();
/* 143 */         if (loader != null) {
/* 144 */           c = loader.findClassHelper(className);
/*     */         }
/*     */       }
/*     */     }
/* 148 */     return c;
/*     */   }
/*     */   
/*     */   void registerSecondaryClassloader(SecondaryClassLoader loader)
/*     */   {
/* 153 */     synchronized (this.peersLoaders)
/*     */     {
/* 155 */       this.peersLoaders.add(new WeakReference(loader));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static ClassLoader getBootstrappedLoader()
/*     */   {
/* 168 */     ClassLoader loader = ClassLoader.getSystemClassLoader();
/*     */     
/*     */     try
/*     */     {
/* 172 */       return (ClassLoader)loader.loadClass(PrimaryClassloader.class.getName()).newInstance();
/*     */     }
/*     */     catch (Exception e) {
/* 175 */       System.err.println("Could not instantiate Classloader\n");
/* 176 */       e.printStackTrace();
/* 177 */       System.exit(1); }
/* 178 */     return null;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/launcher/classloading/PrimaryClassloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */