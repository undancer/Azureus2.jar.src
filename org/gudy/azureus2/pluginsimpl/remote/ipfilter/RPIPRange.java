/*     */ package org.gudy.azureus2.pluginsimpl.remote.ipfilter;
/*     */ 
/*     */ import org.gudy.azureus2.plugins.ipfilter.IPRange;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPException;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPObject;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPReply;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequest;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.RPRequestDispatcher;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RPIPRange
/*     */   extends RPObject
/*     */   implements IPRange
/*     */ {
/*     */   protected transient IPRange delegate;
/*     */   public String description;
/*     */   public String start_ip;
/*     */   public String end_ip;
/*     */   
/*     */   public static RPIPRange create(IPRange _delegate)
/*     */   {
/*  49 */     RPIPRange res = (RPIPRange)_lookupLocal(_delegate);
/*     */     
/*  51 */     if (res == null)
/*     */     {
/*  53 */       res = new RPIPRange(_delegate);
/*     */     }
/*     */     
/*  56 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected RPIPRange(IPRange _delegate)
/*     */   {
/*  63 */     super(_delegate);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/*  70 */     this.delegate = ((IPRange)_delegate);
/*     */     
/*  72 */     this.description = this.delegate.getDescription();
/*  73 */     this.start_ip = this.delegate.getStartIP();
/*  74 */     this.end_ip = this.delegate.getEndIP();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Object _setLocal()
/*     */     throws RPException
/*     */   {
/*  82 */     return _fixupLocal();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/*  89 */     String method = request.getMethod();
/*     */     
/*  91 */     if (method.equals("delete"))
/*     */     {
/*  93 */       this.delegate.delete();
/*     */       
/*  95 */       return null;
/*     */     }
/*     */     
/*  98 */     throw new RPException("Unknown method: " + method);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String getDescription()
/*     */   {
/* 107 */     return this.description;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setDescription(String str)
/*     */   {
/* 114 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public void checkValid()
/*     */   {
/* 120 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isValid()
/*     */   {
/* 126 */     notSupported();
/*     */     
/* 128 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isSessionOnly()
/*     */   {
/* 134 */     notSupported();
/*     */     
/* 136 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getStartIP()
/*     */   {
/* 142 */     return this.start_ip;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setStartIP(String str)
/*     */   {
/* 149 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getEndIP()
/*     */   {
/* 155 */     return this.end_ip;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setEndIP(String str)
/*     */   {
/* 162 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void setSessionOnly(boolean sessionOnly)
/*     */   {
/* 169 */     notSupported();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public boolean isInRange(String ipAddress)
/*     */   {
/* 176 */     notSupported();
/*     */     
/* 178 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public void delete()
/*     */   {
/* 184 */     this._dispatcher.dispatch(new RPRequest(this, "delete", null)).getResponse();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public int compareTo(Object other)
/*     */   {
/* 191 */     notSupported();
/*     */     
/* 193 */     return -1;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/ipfilter/RPIPRange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */