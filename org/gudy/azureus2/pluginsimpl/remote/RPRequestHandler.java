/*     */ package org.gudy.azureus2.pluginsimpl.remote;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.plugins.PluginConfig;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPFilter;
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPRange;
/*     */ import org.gudy.azureus2.plugins.logging.Logger;
/*     */ import org.gudy.azureus2.plugins.logging.LoggerChannel;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.rpexceptions.RPInternalProcessException;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.rpexceptions.RPNoObjectIDException;
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
/*     */ 
/*     */ 
/*     */ public class RPRequestHandler
/*     */ {
/*     */   protected PluginInterface plugin_interface;
/*  42 */   protected Map reply_cache = new HashMap();
/*     */   
/*     */   public RPRequestHandler(PluginInterface _pi) {
/*  45 */     this.plugin_interface = _pi;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RPReply processRequest(RPRequest request)
/*     */   {
/*  52 */     return processRequest(request, null);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public RPReply processRequest(RPRequest request, RPRequestAccessController access_controller)
/*     */   {
/*  64 */     Long connection_id = new Long(request.getConnectionId());
/*     */     
/*  66 */     replyCache cached_reply = connection_id.longValue() == 0L ? null : (replyCache)this.reply_cache.get(connection_id);
/*     */     
/*  68 */     if (cached_reply != null)
/*     */     {
/*  70 */       if (cached_reply.getId() == request.getRequestId())
/*     */       {
/*  72 */         return cached_reply.getReply();
/*     */       }
/*     */     }
/*     */     
/*  76 */     RPReply reply = processRequestSupport(request, access_controller);
/*  77 */     if (reply == null) reply = new RPReply(null);
/*  78 */     this.reply_cache.put(connection_id, new replyCache(request.getRequestId(), reply));
/*     */     
/*  80 */     return reply;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPReply processRequestSupport(RPRequest request, RPRequestAccessController access_controller)
/*     */   {
/*     */     try
/*     */     {
/*  89 */       RPObject object = request.getObject();
/*  90 */       String method = request.getMethod();
/*     */       
/*  92 */       if ((object == null) && (method.equals("getSingleton"))) {
/*  93 */         RPObject pi = request.createRemotePluginInterface(this.plugin_interface);
/*  94 */         return new RPReply(pi);
/*     */       }
/*     */       
/*  97 */       if ((object == null) && (method.equals("getDownloads")))
/*     */       {
/*  99 */         RPPluginInterface pi = request.createRemotePluginInterface(this.plugin_interface);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 104 */         RPObject dm = (RPObject)pi._process(new RPRequest(null, "getDownloadManager", null)).getResponse();
/*     */         
/* 106 */         RPReply rep = dm._process(new RPRequest(null, "getDownloads", null));
/*     */         
/* 108 */         rep.setProperty("azureus_name", pi.azureus_name);
/* 109 */         rep.setProperty("azureus_version", pi.azureus_version);
/*     */         
/* 111 */         return rep;
/*     */       }
/* 113 */       if (object == null) {
/* 114 */         throw new RPNoObjectIDException();
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 120 */       object = RPObject._lookupLocal(object._getOID());
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 125 */       object._setLocal();
/*     */       
/* 127 */       if (method.equals("_refresh"))
/*     */       {
/* 129 */         return new RPReply(object);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 135 */       String name = object._getName();
/*     */       
/* 137 */       if (access_controller != null)
/*     */       {
/* 139 */         access_controller.checkAccess(name, request);
/*     */       }
/*     */       
/* 142 */       RPReply reply = object._process(request);
/*     */       
/* 144 */       if ((name.equals("IPFilter")) && (method.equals("setInRangeAddressesAreAllowed[boolean]")) && (request.getClientIP() != null))
/*     */       {
/*     */ 
/*     */ 
/* 148 */         String client_ip = request.getClientIP();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 153 */         boolean b = ((Boolean)request.getParams()[0]).booleanValue();
/*     */         
/* 155 */         LoggerChannel[] channels = this.plugin_interface.getLogger().getChannels();
/*     */         
/* 157 */         IPFilter filter = this.plugin_interface.getIPFilter();
/*     */         
/* 159 */         if (b)
/*     */         {
/* 161 */           if (filter.isInRange(client_ip))
/*     */           {
/*     */ 
/*     */ 
/* 165 */             for (int i = 0; i < channels.length; i++)
/*     */             {
/* 167 */               channels[i].log(1, "Adding range for client '" + client_ip + "' as allow/deny flag changed to allow");
/*     */             }
/*     */             
/*     */ 
/*     */ 
/* 172 */             filter.createAndAddRange("auto-added for remote interface", client_ip, client_ip, false);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 178 */             filter.save();
/*     */             
/* 180 */             this.plugin_interface.getPluginconfig().save();
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 185 */           IPRange[] ranges = filter.getRanges();
/*     */           
/* 187 */           for (int i = 0; i < ranges.length; i++)
/*     */           {
/* 189 */             if (ranges[i].isInRange(client_ip))
/*     */             {
/* 191 */               for (int j = 0; j < channels.length; j++)
/*     */               {
/* 193 */                 channels[j].log(1, "deleting range '" + ranges[i].getStartIP() + "-" + ranges[i].getEndIP() + "' for client '" + client_ip + "' as allow/deny flag changed to deny");
/*     */               }
/*     */               
/*     */ 
/*     */ 
/* 198 */               ranges[i].delete();
/*     */             }
/*     */           }
/*     */           
/* 202 */           filter.save();
/*     */           
/* 204 */           this.plugin_interface.getPluginconfig().save();
/*     */         }
/*     */       }
/*     */       
/* 208 */       return reply;
/*     */     }
/*     */     catch (RPException e)
/*     */     {
/* 212 */       return new RPReply(e);
/*     */     }
/*     */     catch (Exception e) {
/* 215 */       throw new RPInternalProcessException(e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static class replyCache
/*     */   {
/*     */     protected long id;
/*     */     
/*     */     protected RPReply reply;
/*     */     
/*     */ 
/*     */     protected replyCache(long _id, RPReply _reply)
/*     */     {
/* 230 */       this.id = _id;
/* 231 */       this.reply = _reply;
/*     */     }
/*     */     
/*     */ 
/*     */     protected long getId()
/*     */     {
/* 237 */       return this.id;
/*     */     }
/*     */     
/*     */ 
/*     */     protected RPReply getReply()
/*     */     {
/* 243 */       return this.reply;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/RPRequestHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */