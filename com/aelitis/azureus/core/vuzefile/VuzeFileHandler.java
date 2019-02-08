/*     */ package com.aelitis.azureus.core.vuzefile;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.plugins.utils.StaticUtilities;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloader;
/*     */ import org.gudy.azureus2.plugins.utils.resourcedownloader.ResourceDownloaderFactory;
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
/*     */ 
/*     */ 
/*     */ public class VuzeFileHandler
/*     */ {
/*  44 */   private static final VuzeFileHandler singleton = new VuzeFileHandler();
/*     */   
/*     */ 
/*     */   public static VuzeFileHandler getSingleton()
/*     */   {
/*  49 */     return singleton;
/*     */   }
/*     */   
/*  52 */   private final CopyOnWriteList<VuzeFileProcessor> processors = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public VuzeFile loadVuzeFile(String target)
/*     */   {
/*     */     try
/*     */     {
/*  65 */       File test_file = new File(target);
/*     */       
/*  67 */       if (test_file.isFile())
/*     */       {
/*  69 */         return getVuzeFile(new FileInputStream(test_file));
/*     */       }
/*     */       
/*     */ 
/*  73 */       URL url = new URI(target).toURL();
/*     */       
/*  75 */       String protocol = url.getProtocol().toLowerCase();
/*     */       
/*  77 */       if ((protocol.equals("http")) || (protocol.equals("https")))
/*     */       {
/*  79 */         ResourceDownloader rd = StaticUtilities.getResourceDownloaderFactory().create(url);
/*     */         
/*  81 */         return getVuzeFile(rd.download());
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {}
/*     */     
/*     */ 
/*  87 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public VuzeFile loadVuzeFile(byte[] bytes)
/*     */   {
/*  94 */     return loadVuzeFile(new ByteArrayInputStream(bytes));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public VuzeFile loadVuzeFile(InputStream is)
/*     */   {
/* 101 */     return getVuzeFile(is);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public VuzeFile loadVuzeFile(File file)
/*     */   {
/* 108 */     InputStream is = null;
/*     */     try
/*     */     {
/* 111 */       is = new FileInputStream(file);
/*     */       
/* 113 */       return getVuzeFile(is);
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 117 */       return null;
/*     */     }
/*     */     finally
/*     */     {
/* 121 */       if (is != null) {
/*     */         try
/*     */         {
/* 124 */           is.close();
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected VuzeFile getVuzeFile(InputStream is)
/*     */   {
/*     */     try
/*     */     {
/* 137 */       BufferedInputStream bis = new BufferedInputStream(is);
/*     */       try
/*     */       {
/* 140 */         bis.mark(100);
/*     */         
/* 142 */         boolean is_json = false;
/*     */         
/*     */         for (;;)
/*     */         {
/* 146 */           int next = bis.read();
/*     */           
/* 148 */           if (next == -1) {
/*     */             break;
/*     */           }
/*     */           
/*     */ 
/* 153 */           char c = (char)next;
/*     */           
/* 155 */           if (!Character.isWhitespace(c))
/*     */           {
/* 157 */             is_json = c == '{';
/*     */             
/* 159 */             break;
/*     */           }
/*     */         }
/*     */         
/* 163 */         bis.reset();
/*     */         byte[] bytes;
/*     */         Map map;
/*     */         Map map;
/* 167 */         if (is_json)
/*     */         {
/* 169 */           bytes = FileUtil.readInputStreamAsByteArray(bis, 2097152);
/*     */           
/* 171 */           map = BDecoder.decodeFromJSON(new String(bytes, "UTF-8"));
/*     */         }
/*     */         else
/*     */         {
/* 175 */           map = BDecoder.decode(bis);
/*     */         }
/*     */         
/* 178 */         return loadVuzeFile(map);
/*     */       }
/*     */       finally
/*     */       {
/* 182 */         is.close();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 187 */       return null;
/*     */     }
/*     */     catch (Throwable e) {}
/*     */   }
/*     */   
/*     */   public VuzeFile loadVuzeFile(Map map)
/*     */   {
/* 194 */     if ((map.containsKey("vuze")) && (!map.containsKey("info")))
/*     */     {
/* 196 */       return new VuzeFileImpl(this, (Map)map.get("vuze"));
/*     */     }
/*     */     
/* 199 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public VuzeFile loadAndHandleVuzeFile(String target, int expected_types)
/*     */   {
/* 207 */     VuzeFile vf = loadVuzeFile(target);
/*     */     
/* 209 */     if (vf == null)
/*     */     {
/* 211 */       return null;
/*     */     }
/*     */     
/* 214 */     handleFiles(new VuzeFile[] { vf }, expected_types);
/*     */     
/* 216 */     return vf;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void handleFiles(VuzeFile[] files, int expected_types)
/*     */   {
/* 224 */     Iterator<VuzeFileProcessor> it = this.processors.iterator();
/*     */     
/* 226 */     while (it.hasNext())
/*     */     {
/* 228 */       VuzeFileProcessor proc = (VuzeFileProcessor)it.next();
/*     */       try
/*     */       {
/* 231 */         proc.process(files, expected_types);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 235 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 239 */     for (int i = 0; i < files.length; i++)
/*     */     {
/* 241 */       VuzeFile vf = files[i];
/*     */       
/* 243 */       VuzeFileComponent[] comps = vf.getComponents();
/*     */       
/* 245 */       for (int j = 0; j < comps.length; j++)
/*     */       {
/* 247 */         VuzeFileComponent comp = comps[j];
/*     */         
/* 249 */         if (!comp.isProcessed())
/*     */         {
/* 251 */           Debug.out("Failed to handle Vuze file component " + comp.getContent());
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public VuzeFile create()
/*     */   {
/* 260 */     return new VuzeFileImpl(this);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addProcessor(VuzeFileProcessor proc)
/*     */   {
/* 267 */     this.processors.add(proc);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/vuzefile/VuzeFileHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */