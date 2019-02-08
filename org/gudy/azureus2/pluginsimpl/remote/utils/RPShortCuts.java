/*     */ package org.gudy.azureus2.pluginsimpl.remote.utils;
/*     */ 
/*     */ import org.gudy.azureus2.plugins.download.Download;
/*     */ import org.gudy.azureus2.plugins.download.DownloadException;
/*     */ import org.gudy.azureus2.plugins.download.DownloadStats;
/*     */ import org.gudy.azureus2.plugins.utils.ShortCuts;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPException;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPObject;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPReply;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequest;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequestDispatcher;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.download.RPDownload;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.download.RPDownloadStats;
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
/*     */ 
/*     */ public class RPShortCuts
/*     */   extends RPObject
/*     */   implements ShortCuts
/*     */ {
/*     */   protected transient ShortCuts delegate;
/*     */   
/*     */   public static RPShortCuts create(ShortCuts _delegate)
/*     */   {
/*  46 */     RPShortCuts res = (RPShortCuts)_lookupLocal(_delegate);
/*     */     
/*  48 */     if (res == null)
/*     */     {
/*  50 */       res = new RPShortCuts(_delegate);
/*     */     }
/*     */     
/*  53 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPShortCuts(ShortCuts _delegate)
/*     */   {
/*  60 */     super(_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  67 */     this.delegate = ((ShortCuts)_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/*  75 */     Object res = _fixupLocal();
/*     */     
/*  77 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void _setRemote(RPRequestDispatcher _dispatcher)
/*     */   {
/*  84 */     super._setRemote(_dispatcher);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/*  91 */     String method = request.getMethod();
/*  92 */     Object[] params = request.getParams();
/*     */     
/*  94 */     if (method.equals("getDownload[byte[]]"))
/*     */       try
/*     */       {
/*  97 */         return new RPReply(RPDownload.create(this.delegate.getDownload((byte[])params[0])));
/*     */       }
/*     */       catch (DownloadException e)
/*     */       {
/* 101 */         return new RPReply(e);
/*     */       }
/* 103 */     if (method.equals("getDownloadStats[byte[]]"))
/*     */       try
/*     */       {
/* 106 */         return new RPReply(RPDownloadStats.create(this.delegate.getDownloadStats((byte[])params[0])));
/*     */       }
/*     */       catch (DownloadException e)
/*     */       {
/* 110 */         return new RPReply(e);
/*     */       }
/* 112 */     if (method.equals("restartDownload[byte[]]"))
/*     */     {
/*     */       try {
/* 115 */         this.delegate.restartDownload((byte[])params[0]);
/*     */       }
/*     */       catch (DownloadException e)
/*     */       {
/* 119 */         return new RPReply(e);
/*     */       }
/*     */       
/* 122 */       return null;
/*     */     }
/* 124 */     if (method.equals("stopDownload[byte[]]"))
/*     */     {
/*     */       try {
/* 127 */         this.delegate.stopDownload((byte[])params[0]);
/*     */       }
/*     */       catch (DownloadException e)
/*     */       {
/* 131 */         return new RPReply(e);
/*     */       }
/*     */       
/* 134 */       return null;
/*     */     }
/* 136 */     if (method.equals("removeDownload[byte[]]"))
/*     */     {
/*     */       try {
/* 139 */         this.delegate.removeDownload((byte[])params[0]);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 143 */         return new RPReply(e);
/*     */       }
/*     */       
/* 146 */       return null;
/*     */     }
/*     */     
/* 149 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Download getDownload(byte[] hash)
/*     */     throws DownloadException
/*     */   {
/*     */     try
/*     */     {
/* 161 */       RPDownload res = (RPDownload)this._dispatcher.dispatch(new RPRequest(this, "getDownload[byte[]]", new Object[] { hash })).getResponse();
/*     */       
/* 163 */       res._setRemote(this._dispatcher);
/*     */       
/* 165 */       return res;
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 169 */       if ((e.getCause() instanceof DownloadException))
/*     */       {
/* 171 */         throw ((DownloadException)e.getCause());
/*     */       }
/*     */       
/* 174 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public DownloadStats getDownloadStats(byte[] hash)
/*     */     throws DownloadException
/*     */   {
/*     */     try
/*     */     {
/* 185 */       RPDownloadStats res = (RPDownloadStats)this._dispatcher.dispatch(new RPRequest(this, "getDownloadStats[byte[]]", new Object[] { hash })).getResponse();
/*     */       
/* 187 */       res._setRemote(this._dispatcher);
/*     */       
/* 189 */       return res;
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 193 */       if ((e.getCause() instanceof DownloadException))
/*     */       {
/* 195 */         throw ((DownloadException)e.getCause());
/*     */       }
/*     */       
/* 198 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void restartDownload(byte[] hash)
/*     */     throws DownloadException
/*     */   {
/*     */     try
/*     */     {
/* 209 */       this._dispatcher.dispatch(new RPRequest(this, "restartDownload[byte[]]", new Object[] { hash })).getResponse();
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 213 */       if ((e.getCause() instanceof DownloadException))
/*     */       {
/* 215 */         throw ((DownloadException)e.getCause());
/*     */       }
/*     */       
/* 218 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void stopDownload(byte[] hash)
/*     */     throws DownloadException
/*     */   {
/*     */     try
/*     */     {
/* 229 */       this._dispatcher.dispatch(new RPRequest(this, "stopDownload[byte[]]", new Object[] { hash })).getResponse();
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 233 */       if ((e.getCause() instanceof DownloadException))
/*     */       {
/* 235 */         throw ((DownloadException)e.getCause());
/*     */       }
/*     */       
/* 238 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeDownload(byte[] hash)
/*     */     throws DownloadException
/*     */   {
/*     */     try
/*     */     {
/* 249 */       this._dispatcher.dispatch(new RPRequest(this, "removeDownload[byte[]]", new Object[] { hash })).getResponse();
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 253 */       if ((e.getCause() instanceof DownloadException))
/*     */       {
/* 255 */         throw ((DownloadException)e.getCause());
/*     */       }
/*     */       
/* 258 */       throw e;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/utils/RPShortCuts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */