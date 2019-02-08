/*     */ package com.aelitis.azureus.core.cnetwork.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetwork;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkException;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkListener;
/*     */ import com.aelitis.azureus.core.cnetwork.ContentNetworkManager;
/*     */ import com.aelitis.azureus.core.custom.Customization;
/*     */ import com.aelitis.azureus.core.custom.CustomizationManager;
/*     */ import com.aelitis.azureus.core.custom.CustomizationManagerFactory;
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileComponent;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileProcessor;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsEvidenceGenerator;
/*     */ import org.gudy.azureus2.core3.util.AEDiagnosticsLogger;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.Constants;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.FileUtil;
/*     */ import org.gudy.azureus2.core3.util.IndentWriter;
/*     */ import org.gudy.azureus2.core3.util.SimpleTimer;
/*     */ import org.gudy.azureus2.core3.util.TimerEvent;
/*     */ import org.gudy.azureus2.core3.util.TimerEventPerformer;
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
/*     */ public class ContentNetworkManagerImpl
/*     */   implements ContentNetworkManager, AEDiagnosticsEvidenceGenerator
/*     */ {
/*     */   private static final boolean LOAD_ALL_NETWORKS = true;
/*     */   private static final String CONFIG_FILE = "cnetworks.config";
/*  63 */   private static ContentNetworkManagerImpl singleton = new ContentNetworkManagerImpl();
/*     */   
/*     */ 
/*     */ 
/*     */   public static void preInitialise()
/*     */   {
/*  69 */     VuzeFileHandler.getSingleton().addProcessor(new VuzeFileProcessor()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void process(VuzeFile[] files, int expected_types)
/*     */       {
/*     */ 
/*     */ 
/*  77 */         for (int i = 0; i < files.length; i++)
/*     */         {
/*  79 */           VuzeFile vf = files[i];
/*     */           
/*  81 */           VuzeFileComponent[] comps = vf.getComponents();
/*     */           
/*  83 */           for (int j = 0; j < comps.length; j++)
/*     */           {
/*  85 */             VuzeFileComponent comp = comps[j];
/*     */             
/*  87 */             if (comp.getType() == 128) {
/*     */               try
/*     */               {
/*  90 */                 ((ContentNetworkManagerImpl)ContentNetworkManagerImpl.getSingleton()).importNetwork(comp.getContent());
/*     */                 
/*  92 */                 comp.setProcessed();
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/*  96 */                 ContentNetworkManagerImpl.log("Failed to import from vuze file", e);
/*     */                 
/*  98 */                 Debug.out(e);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */   public static ContentNetworkManager getSingleton()
/*     */   {
/* 110 */     return singleton;
/*     */   }
/*     */   
/* 113 */   private List<ContentNetworkImpl> networks = new ArrayList();
/*     */   
/* 115 */   private CopyOnWriteList<ContentNetworkListener> listeners = new CopyOnWriteList();
/*     */   
/*     */ 
/*     */   protected ContentNetworkManagerImpl()
/*     */   {
/* 120 */     loadConfig();
/*     */     
/* 122 */     addNetwork(new ContentNetworkVuze(this));
/*     */     
/* 124 */     AEDiagnostics.addEvidenceGenerator(this);
/*     */     
/* 126 */     CustomizationManager cust_man = CustomizationManagerFactory.getSingleton();
/*     */     
/* 128 */     Customization cust = cust_man.getActiveCustomization();
/*     */     
/* 130 */     if (cust != null)
/*     */     {
/* 132 */       String cust_name = COConfigurationManager.getStringParameter("cnetworks.custom.name", "");
/* 133 */       String cust_version = COConfigurationManager.getStringParameter("cnetworks.custom.version", "0");
/*     */       
/* 135 */       boolean new_name = !cust_name.equals(cust.getName());
/* 136 */       boolean new_version = Constants.compareVersions(cust_version, cust.getVersion()) < 0;
/*     */       
/* 138 */       if ((new_name) || (new_version)) {
/*     */         try
/*     */         {
/* 141 */           streams = cust.getResources("cnetworks");
/*     */           
/* 143 */           for (i = 0; i < streams.length;)
/*     */           {
/* 145 */             InputStream is = streams[i];
/*     */             try
/*     */             {
/* 148 */               VuzeFile vf = VuzeFileHandler.getSingleton().loadVuzeFile(is);
/*     */               
/* 150 */               if (vf != null)
/*     */               {
/* 152 */                 VuzeFileComponent[] comps = vf.getComponents();
/*     */                 
/* 154 */                 for (int j = 0; j < comps.length; j++)
/*     */                 {
/* 156 */                   VuzeFileComponent comp = comps[j];
/*     */                   
/* 158 */                   int type = comp.getType();
/*     */                   
/* 160 */                   if (type == 128)
/*     */                   {
/*     */ 
/*     */                     try
/*     */                     {
/*     */ 
/*     */ 
/* 167 */                       ContentNetwork imported = importNetwork(comp.getContent());
/*     */                       
/* 169 */                       imported.setPersistentProperty("is_cust", Boolean.valueOf(true));
/*     */                       
/* 171 */                       comp.setProcessed();
/*     */                     }
/*     */                     catch (Throwable e)
/*     */                     {
/* 175 */                       log("Failed to import customisation network", e);
/*     */                     }
/*     */                   }
/*     */                 }
/*     */               }
/*     */               
/*     */               try
/*     */               {
/* 183 */                 is.close();
/*     */               }
/*     */               catch (Throwable e) {}
/* 143 */               i++;
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
/*     */             }
/*     */             finally
/*     */             {
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
/*     */               try
/*     */               {
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
/* 183 */                 is.close();
/*     */               }
/*     */               catch (Throwable e) {}
/*     */             }
/*     */           }
/*     */         } finally {
/*     */           InputStream[] streams;
/*     */           int i;
/* 191 */           COConfigurationManager.setParameter("cnetworks.custom.name", cust.getName());
/* 192 */           COConfigurationManager.setParameter("cnetworks.custom.version", cust.getVersion());
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 198 */     SimpleTimer.addPeriodicEvent("MetaSearchRefresh", 82800000L, new TimerEventPerformer()
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */       public void perform(TimerEvent event)
/*     */       {
/*     */ 
/*     */ 
/* 207 */         ContentNetworkManagerImpl.this.checkForUpdates();
/*     */       }
/*     */     });
/*     */     
/* 211 */     if (this.networks.size() <= 1) {}
/*     */     
/* 213 */     new AEThread2("CNetwork:init", true)
/*     */     {
/*     */ 
/*     */       public void run()
/*     */       {
/* 218 */         ContentNetworkManagerImpl.this.checkForUpdates();
/*     */       }
/*     */     }.start();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void checkForUpdates() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ContentNetworkImpl importNetwork(Map content)
/*     */     throws IOException
/*     */   {
/* 235 */     ContentNetworkImpl network = ContentNetworkImpl.importFromBEncodedMapStatic(this, content);
/*     */     
/* 237 */     return addNetwork(network);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addContentNetwork(long id)
/*     */     throws ContentNetworkException
/*     */   {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public ContentNetwork getContentNetworkForURL(String url)
/*     */   {
/*     */     try
/*     */     {
/* 254 */       String host = new URL(url).getHost();
/*     */       
/* 256 */       for (ContentNetwork cn : getContentNetworks())
/*     */       {
/* 258 */         String site = (String)cn.getProperty(1);
/*     */         
/* 260 */         if ((site != null) && (site.endsWith(host)))
/*     */         {
/* 262 */           return cn;
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 267 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/* 270 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public ContentNetwork getStartupContentNetwork()
/*     */   {
/* 276 */     ContentNetwork[] networks = getContentNetworks();
/*     */     
/* 278 */     for (ContentNetwork network : networks)
/*     */     {
/* 280 */       if (network.isStartupNetwork())
/*     */       {
/* 282 */         return network;
/*     */       }
/*     */     }
/*     */     
/* 286 */     return getContentNetwork(1L);
/*     */   }
/*     */   
/*     */   /* Error */
/*     */   public ContentNetwork[] getContentNetworks()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: dup
/*     */     //   2: astore_1
/*     */     //   3: monitorenter
/*     */     //   4: aload_0
/*     */     //   5: getfield 399	com/aelitis/azureus/core/cnetwork/impl/ContentNetworkManagerImpl:networks	Ljava/util/List;
/*     */     //   8: aload_0
/*     */     //   9: getfield 399	com/aelitis/azureus/core/cnetwork/impl/ContentNetworkManagerImpl:networks	Ljava/util/List;
/*     */     //   12: invokeinterface 476 1 0
/*     */     //   17: anewarray 216	com/aelitis/azureus/core/cnetwork/impl/ContentNetworkImpl
/*     */     //   20: invokeinterface 481 2 0
/*     */     //   25: checkcast 209	[Lcom/aelitis/azureus/core/cnetwork/ContentNetwork;
/*     */     //   28: checkcast 209	[Lcom/aelitis/azureus/core/cnetwork/ContentNetwork;
/*     */     //   31: aload_1
/*     */     //   32: monitorexit
/*     */     //   33: areturn
/*     */     //   34: astore_2
/*     */     //   35: aload_1
/*     */     //   36: monitorexit
/*     */     //   37: aload_2
/*     */     //   38: athrow
/*     */     // Line number table:
/*     */     //   Java source line #292	-> byte code offset #0
/*     */     //   Java source line #294	-> byte code offset #4
/*     */     //   Java source line #295	-> byte code offset #34
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	signature
/*     */     //   0	39	0	this	ContentNetworkManagerImpl
/*     */     //   2	34	1	Ljava/lang/Object;	Object
/*     */     //   34	4	2	localObject1	Object
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   4	33	34	finally
/*     */     //   34	37	34	finally
/*     */   }
/*     */   
/*     */   public ContentNetworkImpl getContentNetwork(long id)
/*     */   {
/* 302 */     synchronized (this)
/*     */     {
/* 304 */       for (int i = 0; i < this.networks.size(); i++)
/*     */       {
/* 306 */         ContentNetworkImpl network = (ContentNetworkImpl)this.networks.get(i);
/*     */         
/* 308 */         if (network.getID() == id)
/*     */         {
/* 310 */           return network;
/*     */         }
/*     */       }
/*     */       
/* 314 */       return null;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected ContentNetworkImpl addNetwork(ContentNetworkImpl network)
/*     */   {
/* 322 */     boolean replace = false;
/*     */     
/* 324 */     synchronized (this)
/*     */     {
/* 326 */       Iterator<ContentNetworkImpl> it = this.networks.iterator();
/*     */       
/* 328 */       while (it.hasNext())
/*     */       {
/* 330 */         ContentNetworkImpl existing_network = (ContentNetworkImpl)it.next();
/*     */         
/* 332 */         if (existing_network.getID() == network.getID())
/*     */         {
/* 334 */           if (network.getVersion() > existing_network.getVersion())
/*     */           {
/*     */             try {
/* 337 */               existing_network.updateFrom(network);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 341 */               Debug.printStackTrace(e);
/*     */             }
/*     */             
/* 344 */             network = existing_network;
/*     */             
/* 346 */             replace = true;
/*     */             
/* 348 */             break;
/*     */           }
/*     */           
/*     */ 
/* 352 */           log("Network " + existing_network.getString() + " already up to date");
/*     */           
/* 354 */           return existing_network;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 359 */       if (replace)
/*     */       {
/* 361 */         log("Updated network: " + network.getString());
/*     */       }
/*     */       else
/*     */       {
/* 365 */         log("Added network: " + network.getString());
/*     */         
/* 367 */         this.networks.add(network);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 372 */       if (network.getID() != 1L)
/*     */       {
/* 374 */         saveConfig();
/*     */       }
/*     */     }
/*     */     
/* 378 */     Iterator<ContentNetworkListener> it = this.listeners.iterator();
/*     */     
/* 380 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/* 383 */         if (replace)
/*     */         {
/* 385 */           ((ContentNetworkListener)it.next()).networkChanged(network);
/*     */         }
/*     */         else
/*     */         {
/* 389 */           ((ContentNetworkListener)it.next()).networkAdded(network);
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 393 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */     
/* 397 */     return network;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void removeNetwork(ContentNetworkImpl network)
/*     */   {
/* 404 */     synchronized (this)
/*     */     {
/* 406 */       if (!this.networks.remove(network))
/*     */       {
/* 408 */         return;
/*     */       }
/*     */       
/* 411 */       network.destroy();
/*     */       
/* 413 */       saveConfig();
/*     */     }
/*     */     
/* 416 */     log("Removed network: " + network.getString());
/*     */     
/* 418 */     Iterator<ContentNetworkListener> it = this.listeners.iterator();
/*     */     
/* 420 */     while (it.hasNext()) {
/*     */       try
/*     */       {
/* 423 */         ((ContentNetworkListener)it.next()).networkRemoved(network);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 427 */         Debug.out(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void loadConfig()
/*     */   {
/* 435 */     if (FileUtil.resilientConfigFileExists("cnetworks.config"))
/*     */     {
/* 437 */       Map map = FileUtil.readResilientConfigFile("cnetworks.config");
/*     */       
/* 439 */       List list = (List)map.get("networks");
/*     */       
/* 441 */       if (list != null)
/*     */       {
/* 443 */         for (int i = 0; i < list.size(); i++)
/*     */         {
/* 445 */           Map cnet_map = (Map)list.get(i);
/*     */           
/*     */           try
/*     */           {
/* 449 */             ContentNetworkImpl cn = ContentNetworkImpl.importFromBEncodedMapStatic(this, cnet_map);
/*     */             
/* 451 */             if (cn.getID() != 1L)
/*     */             {
/* 453 */               this.networks.add(cn);
/*     */             }
/*     */           }
/*     */           catch (Throwable e) {
/* 457 */             log("Failed to load " + cnet_map, e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected void saveConfig()
/*     */   {
/* 467 */     Map map = new HashMap();
/*     */     
/* 469 */     List list = new ArrayList();
/*     */     
/* 471 */     map.put("networks", list);
/*     */     
/* 473 */     Iterator<ContentNetworkImpl> it = this.networks.iterator();
/*     */     
/* 475 */     while (it.hasNext())
/*     */     {
/* 477 */       ContentNetworkImpl network = (ContentNetworkImpl)it.next();
/*     */       
/* 479 */       if (network.getID() != 1L)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/* 484 */         Map cnet_map = new HashMap();
/*     */         try
/*     */         {
/* 487 */           network.exportToBEncodedMap(cnet_map);
/*     */           
/* 489 */           list.add(cnet_map);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 493 */           log("Failed to save " + network.getName(), e);
/*     */         }
/*     */       }
/*     */     }
/* 497 */     if (list.size() == 0)
/*     */     {
/* 499 */       FileUtil.deleteResilientConfigFile("cnetworks.config");
/*     */     }
/*     */     else
/*     */     {
/* 503 */       FileUtil.writeResilientConfigFile("cnetworks.config", map);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addListener(ContentNetworkListener listener)
/*     */   {
/* 511 */     this.listeners.add(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeListener(ContentNetworkListener listener)
/*     */   {
/* 518 */     this.listeners.remove(listener);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void generate(IndentWriter writer)
/*     */   {
/* 525 */     writer.println("Content Networks");
/*     */     try
/*     */     {
/* 528 */       writer.indent();
/*     */       
/* 530 */       synchronized (this)
/*     */       {
/* 532 */         Iterator<ContentNetworkImpl> it = this.networks.iterator();
/*     */         
/* 534 */         while (it.hasNext())
/*     */         {
/* 536 */           ContentNetworkImpl network = (ContentNetworkImpl)it.next();
/*     */           
/* 538 */           writer.println(network.getString());
/*     */         }
/*     */       }
/*     */     }
/*     */     finally {
/* 543 */       writer.exdent();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void log(String s, Throwable e)
/*     */   {
/* 552 */     AEDiagnosticsLogger diag_logger = AEDiagnostics.getLogger("CNetworks");
/*     */     
/* 554 */     diag_logger.log(s);
/* 555 */     diag_logger.log(e);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void log(String s)
/*     */   {
/* 562 */     AEDiagnosticsLogger diag_logger = AEDiagnostics.getLogger("CNetworks");
/*     */     
/* 564 */     diag_logger.log(s);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/cnetwork/impl/ContentNetworkManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */