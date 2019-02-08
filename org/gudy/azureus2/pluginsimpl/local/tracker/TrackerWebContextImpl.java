/*     */ package org.gudy.azureus2.pluginsimpl.local.tracker;
/*     */ 
/*     */ import java.net.InetAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServer;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerAuthenticationListener;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerException;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerFactory;
/*     */ import org.gudy.azureus2.core3.tracker.server.TRTrackerServerListener2;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.plugins.tracker.TrackerException;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerAuthenticationAdapter;
/*     */ import org.gudy.azureus2.plugins.tracker.web.TrackerAuthenticationListener;
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
/*     */ public class TrackerWebContextImpl
/*     */   extends TrackerWCHelper
/*     */   implements TRTrackerServerListener2, TRTrackerServerAuthenticationListener
/*     */ {
/*     */   protected TRTrackerServer server;
/*  47 */   protected List<TrackerAuthenticationListener> auth_listeners = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public TrackerWebContextImpl(TrackerImpl _tracker, String name, int port, int protocol, InetAddress bind_ip, Map<String, Object> properties)
/*     */     throws TrackerException
/*     */   {
/*  60 */     setTracker(_tracker);
/*     */     
/*     */     try
/*     */     {
/*  64 */       if (protocol == 1)
/*     */       {
/*  66 */         this.server = TRTrackerServerFactory.create(name, 1, port, bind_ip, false, false, properties);
/*     */       }
/*     */       else
/*     */       {
/*  70 */         this.server = TRTrackerServerFactory.createSSL(name, 1, port, bind_ip, false, false, properties);
/*     */       }
/*     */       
/*  73 */       this.server.addListener2(this);
/*     */     }
/*     */     catch (TRTrackerServerException e)
/*     */     {
/*  77 */       throw new TrackerException("TRTrackerServerFactory failed", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  84 */     return this.server.getName();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEnableKeepAlive(boolean enable)
/*     */   {
/*  91 */     this.server.setEnableKeepAlive(enable);
/*     */   }
/*     */   
/*     */   public URL[] getURLs()
/*     */   {
/*     */     try
/*     */     {
/*  98 */       URL url = new URL((this.server.isSSL() ? "https" : "http") + "://" + this.server.getHost() + ":" + this.server.getPort() + "/");
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 103 */       if (url.getPort() != this.server.getPort())
/*     */       {
/* 105 */         Debug.out("Invalid URL '" + url + "' - check tracker configuration");
/*     */         
/* 107 */         url = new URL("http://i.am.invalid:" + this.server.getPort() + "/");
/*     */       }
/*     */       
/* 110 */       return new URL[] { url };
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 114 */       Debug.printStackTrace(e);
/*     */     }
/* 116 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InetAddress getBindIP()
/*     */   {
/* 123 */     return this.server.getBindIP();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean authenticate(String headers, URL resource, String user, String password)
/*     */   {
/* 133 */     for (int i = 0; i < this.auth_listeners.size(); i++) {
/*     */       try
/*     */       {
/* 136 */         TrackerAuthenticationListener listener = (TrackerAuthenticationListener)this.auth_listeners.get(i);
/*     */         
/*     */         boolean res;
/*     */         boolean res;
/* 140 */         if ((listener instanceof TrackerAuthenticationAdapter))
/*     */         {
/* 142 */           res = ((TrackerAuthenticationAdapter)listener).authenticate(headers, resource, user, password);
/*     */         }
/*     */         else
/*     */         {
/* 146 */           res = listener.authenticate(resource, user, password);
/*     */         }
/*     */         
/* 149 */         if (res)
/*     */         {
/* 151 */           return true;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 155 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 159 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public byte[] authenticate(URL resource, String user)
/*     */   {
/* 167 */     for (int i = 0; i < this.auth_listeners.size(); i++) {
/*     */       try
/*     */       {
/* 170 */         byte[] res = ((TrackerAuthenticationListener)this.auth_listeners.get(i)).authenticate(resource, user);
/*     */         
/* 172 */         if (res != null)
/*     */         {
/* 174 */           return res;
/*     */         }
/*     */       }
/*     */       catch (Throwable e) {
/* 178 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     
/* 182 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */   public void addAuthenticationListener(TrackerAuthenticationListener l)
/*     */   {
/*     */     try
/*     */     {
/* 190 */       this.this_mon.enter();
/*     */       
/* 192 */       this.auth_listeners.add(l);
/*     */       
/* 194 */       if (this.auth_listeners.size() == 1)
/*     */       {
/* 196 */         this.server.addAuthenticationListener(this);
/*     */       }
/*     */     }
/*     */     finally {
/* 200 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void removeAuthenticationListener(TrackerAuthenticationListener l)
/*     */   {
/*     */     try
/*     */     {
/* 209 */       this.this_mon.enter();
/*     */       
/* 211 */       this.auth_listeners.remove(l);
/*     */       
/* 213 */       if (this.auth_listeners.size() == 0)
/*     */       {
/* 215 */         this.server.removeAuthenticationListener(this);
/*     */       }
/*     */     }
/*     */     finally {
/* 219 */       this.this_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public void destroy()
/*     */   {
/* 226 */     super.destroy();
/*     */     
/* 228 */     this.auth_listeners.clear();
/*     */     
/* 230 */     this.server.removeAuthenticationListener(this);
/*     */     
/* 232 */     this.server.close();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/tracker/TrackerWebContextImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */