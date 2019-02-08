/*     */ package org.gudy.azureus2.pluginsimpl.remote.tracker;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.URL;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.plugins.torrent.Torrent;
/*     */ import org.gudy.azureus2.plugins.tracker.Tracker;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerException;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerListener;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerTorrent;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerAuthenticationListener;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebContext;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerWebPageGenerator;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPException;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPObject;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPReply;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequest;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequestDispatcher;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.torrent.RPTorrent;
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
/*     */ public class RPTracker
/*     */   extends RPObject
/*     */   implements Tracker
/*     */ {
/*     */   protected transient Tracker delegate;
/*     */   
/*     */   public static RPTracker create(Tracker _delegate)
/*     */   {
/*  50 */     RPTracker res = (RPTracker)_lookupLocal(_delegate);
/*     */     
/*  52 */     if (res == null)
/*     */     {
/*  54 */       res = new RPTracker(_delegate);
/*     */     }
/*     */     
/*  57 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPTracker(Tracker _delegate)
/*     */   {
/*  64 */     super(_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  71 */     this.delegate = ((Tracker)_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/*  79 */     return _fixupLocal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/*  87 */     String method = request.getMethod();
/*  88 */     Object[] params = request.getParams();
/*     */     
/*  90 */     if (method.equals("host[Torrent,boolean]"))
/*     */       try
/*     */       {
/*  93 */         Torrent torrent = params[0] == null ? null : (Torrent)((RPTorrent)params[0])._setLocal();
/*     */         
/*  95 */         if (torrent == null)
/*     */         {
/*  97 */           throw new RPException("Invalid torrent");
/*     */         }
/*     */         
/* 100 */         TrackerTorrent tt = this.delegate.host(torrent, ((Boolean)params[1]).booleanValue());
/*     */         
/* 102 */         RPTrackerTorrent res = RPTrackerTorrent.create(tt);
/*     */         
/* 104 */         return new RPReply(res);
/*     */       }
/*     */       catch (TrackerException e)
/*     */       {
/* 108 */         return new RPReply(e);
/*     */       }
/* 110 */     if (method.equals("getTorrents"))
/*     */     {
/* 112 */       TrackerTorrent[] torrents = this.delegate.getTorrents();
/*     */       
/* 114 */       RPTrackerTorrent[] res = new RPTrackerTorrent[torrents.length];
/*     */       
/* 116 */       for (int i = 0; i < res.length; i++)
/*     */       {
/* 118 */         res[i] = RPTrackerTorrent.create(torrents[i]);
/*     */       }
/*     */       
/* 121 */       return new RPReply(res);
/*     */     }
/*     */     
/* 124 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerTorrent host(Torrent torrent, boolean persistent)
/*     */     throws TrackerException
/*     */   {
/*     */     try
/*     */     {
/* 137 */       RPTrackerTorrent resp = (RPTrackerTorrent)this._dispatcher.dispatch(new RPRequest(this, "host[Torrent,boolean]", new Object[] { torrent, Boolean.valueOf(persistent) })).getResponse();
/*     */       
/*     */ 
/*     */ 
/* 141 */       resp._setRemote(this._dispatcher);
/*     */       
/* 143 */       return resp;
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 147 */       if ((e.getCause() instanceof TrackerException))
/*     */       {
/* 149 */         throw ((TrackerException)e.getCause());
/*     */       }
/*     */       
/* 152 */       throw e;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerTorrent host(Torrent torrent, boolean persistent, boolean passive)
/*     */     throws TrackerException
/*     */   {
/* 164 */     notSupported();
/*     */     
/* 166 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerTorrent publish(Torrent torrent)
/*     */     throws TrackerException
/*     */   {
/* 175 */     notSupported();
/*     */     
/* 177 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public TrackerTorrent[] getTorrents()
/*     */   {
/* 183 */     RPTrackerTorrent[] res = (RPTrackerTorrent[])this._dispatcher.dispatch(new RPRequest(this, "getTorrents", null)).getResponse();
/*     */     
/* 185 */     for (int i = 0; i < res.length; i++)
/*     */     {
/* 187 */       res[i]._setRemote(this._dispatcher);
/*     */     }
/*     */     
/* 190 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public TrackerTorrent getTorrent(Torrent t)
/*     */   {
/* 197 */     notSupported();
/*     */     
/* 199 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerWebContext createWebContext(int port, int protocol)
/*     */     throws TrackerException
/*     */   {
/* 209 */     notSupported();
/*     */     
/* 211 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerWebContext createWebContext(String name, int port, int protocol)
/*     */     throws TrackerException
/*     */   {
/* 222 */     notSupported();
/*     */     
/* 224 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerWebContext createWebContext(String name, int port, int protocol, InetAddress bind_ip)
/*     */     throws TrackerException
/*     */   {
/* 236 */     notSupported();
/*     */     
/* 238 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerWebContext createWebContext(String name, int port, int protocol, InetAddress bind_ip, Map<String, Object> properties)
/*     */     throws TrackerException
/*     */   {
/* 251 */     notSupported();
/*     */     
/* 253 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addListener(TrackerListener listener) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeListener(TrackerListener listener) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getName()
/*     */   {
/* 273 */     notSupported();
/*     */     
/* 275 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnableKeepAlive(boolean enable)
/*     */   {
/* 282 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public URL[] getURLs()
/*     */   {
/* 288 */     notSupported();
/*     */     
/* 290 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getBindIP()
/*     */   {
/* 296 */     notSupported();
/*     */     
/* 298 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addPageGenerator(TrackerWebPageGenerator generator) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removePageGenerator(TrackerWebPageGenerator generator) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerWebPageGenerator[] getPageGenerators()
/*     */   {
/* 317 */     notSupported();
/*     */     
/* 319 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void addAuthenticationListener(TrackerAuthenticationListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void removeAuthenticationListener(TrackerAuthenticationListener l) {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 339 */     notSupported();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/tracker/RPTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */