/*     */ package com.aelitis.net.upnp.impl.services;
/*     */ 
/*     */ import com.aelitis.net.upnp.UPnPAction;
/*     */ import com.aelitis.net.upnp.UPnPActionArgument;
/*     */ import com.aelitis.net.upnp.UPnPActionInvocation;
/*     */ import com.aelitis.net.upnp.UPnPException;
/*     */ import com.aelitis.net.upnp.UPnPService;
/*     */ import com.aelitis.net.upnp.services.UPnPOfflineDownloader;
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
/*     */ public class UPnPSSOfflineDownloaderImpl
/*     */   implements UPnPOfflineDownloader
/*     */ {
/*     */   private UPnPServiceImpl service;
/*     */   
/*     */   protected UPnPSSOfflineDownloaderImpl(UPnPServiceImpl _service)
/*     */   {
/*  39 */     this.service = _service;
/*     */   }
/*     */   
/*     */ 
/*     */   public UPnPService getGenericService()
/*     */   {
/*  45 */     return this.service;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public long getFreeSpace(String client_id)
/*     */     throws UPnPException
/*     */   {
/*  54 */     UPnPAction act = this.service.getAction("GetFreeSpace");
/*     */     
/*  56 */     if (act == null)
/*     */     {
/*  58 */       throw new UPnPException("GetFreeSpace not supported");
/*     */     }
/*     */     
/*     */ 
/*  62 */     UPnPActionInvocation inv = act.getInvocation();
/*     */     
/*  64 */     inv.addArgument("NewClientID", client_id);
/*     */     
/*  66 */     UPnPActionArgument[] args = inv.invoke();
/*     */     
/*  68 */     for (int i = 0; i < args.length; i++)
/*     */     {
/*  70 */       UPnPActionArgument arg = args[i];
/*     */       
/*  72 */       String name = arg.getName();
/*     */       
/*  74 */       if (name.equalsIgnoreCase("NewFreeSpace"))
/*     */       {
/*  76 */         return Long.parseLong(arg.getValue());
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*  81 */     throw new UPnPException("result not found");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void activate(String client_id)
/*     */     throws UPnPException
/*     */   {
/*  91 */     UPnPAction act = this.service.getAction("Activate");
/*     */     
/*  93 */     if (act == null)
/*     */     {
/*  95 */       throw new UPnPException("Activate not supported");
/*     */     }
/*     */     
/*     */ 
/*  99 */     UPnPActionInvocation inv = act.getInvocation();
/*     */     
/* 101 */     inv.addArgument("NewClientID", client_id);
/*     */     
/* 103 */     UPnPActionArgument[] args = inv.invoke();
/*     */     
/* 105 */     for (int i = 0; i < args.length; i++)
/*     */     {
/* 107 */       UPnPActionArgument arg = args[i];
/*     */       
/* 109 */       String name = arg.getName();
/*     */       
/* 111 */       if (name.equalsIgnoreCase("NewStatus"))
/*     */       {
/* 113 */         return;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/* 118 */     throw new UPnPException("status not found");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] setDownloads(String client_id, String hash_list)
/*     */     throws UPnPException
/*     */   {
/* 129 */     UPnPAction act = this.service.getAction("SetDownloads");
/*     */     
/* 131 */     if (act == null)
/*     */     {
/* 133 */       throw new UPnPException("SetDownloads not supported");
/*     */     }
/*     */     
/*     */ 
/* 137 */     UPnPActionInvocation inv = act.getInvocation();
/*     */     
/* 139 */     inv.addArgument("NewClientID", client_id);
/* 140 */     inv.addArgument("NewTorrentHashList", hash_list);
/*     */     
/* 142 */     UPnPActionArgument[] args = inv.invoke();
/*     */     
/* 144 */     String result = null;
/* 145 */     String status = null;
/*     */     
/* 147 */     for (int i = 0; i < args.length; i++)
/*     */     {
/* 149 */       UPnPActionArgument arg = args[i];
/*     */       
/* 151 */       String name = arg.getName();
/*     */       
/* 153 */       if (name.equalsIgnoreCase("NewSetDownloadsResultList"))
/*     */       {
/* 155 */         result = arg.getValue();
/*     */       }
/* 157 */       else if (name.equalsIgnoreCase("NewStatus"))
/*     */       {
/* 159 */         status = arg.getValue();
/*     */       }
/*     */     }
/*     */     
/* 163 */     if ((result != null) && (status != null))
/*     */     {
/* 165 */       return new String[] { result, status };
/*     */     }
/*     */     
/* 168 */     throw new UPnPException("result or status not found");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String addDownload(String client_id, String hash, String torrent)
/*     */     throws UPnPException
/*     */   {
/* 180 */     UPnPAction act = this.service.getAction("AddDownload");
/*     */     
/* 182 */     if (act == null)
/*     */     {
/* 184 */       throw new UPnPException("AddDownload not supported");
/*     */     }
/*     */     
/*     */ 
/* 188 */     UPnPActionInvocation inv = act.getInvocation();
/*     */     
/* 190 */     inv.addArgument("NewClientID", client_id);
/* 191 */     inv.addArgument("NewTorrentHash", hash);
/* 192 */     inv.addArgument("NewTorrentData", torrent);
/*     */     
/* 194 */     UPnPActionArgument[] args = inv.invoke();
/*     */     
/* 196 */     for (int i = 0; i < args.length; i++)
/*     */     {
/* 198 */       UPnPActionArgument arg = args[i];
/*     */       
/* 200 */       String name = arg.getName();
/*     */       
/* 202 */       if (name.equalsIgnoreCase("NewStatus"))
/*     */       {
/* 204 */         return arg.getValue();
/*     */       }
/*     */     }
/*     */     
/* 208 */     throw new UPnPException("result not found");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String addDownloadChunked(String client_id, String hash, String chunk, int offset, int total_size)
/*     */     throws UPnPException
/*     */   {
/* 222 */     UPnPAction act = this.service.getAction("AddDownloadChunked");
/*     */     
/* 224 */     if (act == null)
/*     */     {
/* 226 */       throw new UPnPException("AddDownloadChunked not supported");
/*     */     }
/*     */     
/*     */ 
/* 230 */     UPnPActionInvocation inv = act.getInvocation();
/*     */     
/* 232 */     inv.addArgument("NewClientID", client_id);
/* 233 */     inv.addArgument("NewTorrentHash", hash);
/* 234 */     inv.addArgument("NewTorrentData", chunk);
/* 235 */     inv.addArgument("NewChunkOffset", String.valueOf(offset));
/* 236 */     inv.addArgument("NewTotalLength", String.valueOf(total_size));
/*     */     
/* 238 */     UPnPActionArgument[] args = inv.invoke();
/*     */     
/* 240 */     for (int i = 0; i < args.length; i++)
/*     */     {
/* 242 */       UPnPActionArgument arg = args[i];
/*     */       
/* 244 */       String name = arg.getName();
/*     */       
/* 246 */       if (name.equalsIgnoreCase("NewStatus"))
/*     */       {
/* 248 */         return arg.getValue();
/*     */       }
/*     */     }
/*     */     
/* 252 */     throw new UPnPException("result not found");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] updateDownload(String client_id, String hash, String required_map)
/*     */     throws UPnPException
/*     */   {
/* 264 */     UPnPAction act = this.service.getAction("UpdateDownload");
/*     */     
/* 266 */     if (act == null)
/*     */     {
/* 268 */       throw new UPnPException("UpdateDownload not supported");
/*     */     }
/*     */     
/*     */ 
/* 272 */     UPnPActionInvocation inv = act.getInvocation();
/*     */     
/* 274 */     inv.addArgument("NewClientID", client_id);
/* 275 */     inv.addArgument("NewTorrentHash", hash);
/* 276 */     inv.addArgument("NewPieceRequiredMap", required_map);
/*     */     
/* 278 */     UPnPActionArgument[] args = inv.invoke();
/*     */     
/* 280 */     String have = null;
/* 281 */     String status = null;
/*     */     
/* 283 */     for (int i = 0; i < args.length; i++)
/*     */     {
/* 285 */       UPnPActionArgument arg = args[i];
/*     */       
/* 287 */       String name = arg.getName();
/*     */       
/* 289 */       if (name.equalsIgnoreCase("NewPieceHaveMap"))
/*     */       {
/* 291 */         have = arg.getValue();
/*     */       }
/* 293 */       else if (name.equalsIgnoreCase("NewStatus"))
/*     */       {
/* 295 */         status = arg.getValue();
/*     */       }
/*     */     }
/*     */     
/* 299 */     if ((have != null) && (status != null))
/*     */     {
/* 301 */       return new String[] { have, status };
/*     */     }
/*     */     
/* 304 */     throw new UPnPException("have or status not found");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String removeDownload(String client_id, String hash)
/*     */     throws UPnPException
/*     */   {
/* 315 */     UPnPAction act = this.service.getAction("RemoveDownload");
/*     */     
/* 317 */     if (act == null)
/*     */     {
/* 319 */       throw new UPnPException("RemoveDownload not supported");
/*     */     }
/*     */     
/*     */ 
/* 323 */     UPnPActionInvocation inv = act.getInvocation();
/*     */     
/* 325 */     inv.addArgument("NewClientID", client_id);
/* 326 */     inv.addArgument("NewTorrentHash", hash);
/*     */     
/* 328 */     UPnPActionArgument[] args = inv.invoke();
/*     */     
/* 330 */     String status = null;
/*     */     
/* 332 */     for (int i = 0; i < args.length; i++)
/*     */     {
/* 334 */       UPnPActionArgument arg = args[i];
/*     */       
/* 336 */       String name = arg.getName();
/*     */       
/* 338 */       if (name.equalsIgnoreCase("NewStatus"))
/*     */       {
/* 340 */         status = arg.getValue();
/*     */       }
/*     */     }
/*     */     
/* 344 */     if (status != null)
/*     */     {
/* 346 */       return status;
/*     */     }
/*     */     
/* 349 */     throw new UPnPException("status not found");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String[] startDownload(String client_id, String hash)
/*     */     throws UPnPException
/*     */   {
/* 360 */     UPnPAction act = this.service.getAction("StartDownload");
/*     */     
/* 362 */     if (act == null)
/*     */     {
/* 364 */       throw new UPnPException("StartDownload not supported");
/*     */     }
/*     */     
/*     */ 
/* 368 */     UPnPActionInvocation inv = act.getInvocation();
/*     */     
/* 370 */     inv.addArgument("NewClientID", client_id);
/* 371 */     inv.addArgument("NewTorrentHash", hash);
/*     */     
/* 373 */     UPnPActionArgument[] args = inv.invoke();
/*     */     
/* 375 */     String status = null;
/* 376 */     String data_port = null;
/*     */     
/* 378 */     for (int i = 0; i < args.length; i++)
/*     */     {
/* 380 */       UPnPActionArgument arg = args[i];
/*     */       
/* 382 */       String name = arg.getName();
/*     */       
/* 384 */       if (name.equalsIgnoreCase("NewStatus"))
/*     */       {
/* 386 */         status = arg.getValue();
/*     */       }
/* 388 */       else if (name.equalsIgnoreCase("NewDataPort"))
/*     */       {
/* 390 */         data_port = arg.getValue();
/*     */       }
/*     */     }
/*     */     
/* 394 */     if ((status != null) && (data_port != null))
/*     */     {
/* 396 */       return new String[] { data_port, status };
/*     */     }
/*     */     
/* 399 */     throw new UPnPException("status or data port not found");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/upnp/impl/services/UPnPSSOfflineDownloaderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */