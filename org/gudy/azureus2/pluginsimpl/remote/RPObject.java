/*     */ package org.gudy.azureus2.pluginsimpl.remote;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import java.util.Random;
/*     */ import java.util.WeakHashMap;
/*     */ import org.gudy.azureus2.pluginsimpl.remote.rpexceptions.RPObjectNoLongerExistsException;
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
/*     */ 
/*     */ 
/*     */ public class RPObject
/*     */   implements Serializable
/*     */ {
/*  42 */   protected static transient Map object_registry = new WeakHashMap();
/*     */   
/*  44 */   protected static transient Map object_registry_reverse = new WeakHashMap();
/*     */   
/*  46 */   protected static transient long next_key = new Random().nextLong();
/*     */   
/*     */ 
/*     */   public Long _object_id;
/*     */   
/*     */ 
/*     */   protected transient Object __delegate;
/*     */   
/*     */ 
/*     */   protected transient RPRequestDispatcher _dispatcher;
/*     */   
/*     */ 
/*     */ 
/*     */   protected static RPObject _lookupLocal(Object key)
/*     */   {
/*  61 */     synchronized (object_registry)
/*     */     {
/*  63 */       RPObject res = (RPObject)object_registry.get(key);
/*     */       
/*  65 */       if (res != null)
/*     */       {
/*  67 */         res._setLocal();
/*     */       }
/*     */       
/*  70 */       return res;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static RPObject _lookupLocal(long object_id)
/*     */   {
/*  78 */     synchronized (object_registry)
/*     */     {
/*  80 */       Object res = object_registry_reverse.get(new Long(object_id));
/*     */       
/*  82 */       if (res == null) {
/*  83 */         throw new RPObjectNoLongerExistsException();
/*     */       }
/*     */       
/*  86 */       RPObject obj = (RPObject)object_registry.get(res);
/*  87 */       if (obj == null) {
/*  88 */         throw new RPObjectNoLongerExistsException();
/*     */       }
/*     */       
/*  91 */       return obj;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public RPObject() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected RPObject(Object key)
/*     */   {
/* 105 */     synchronized (object_registry)
/*     */     {
/* 107 */       RPObject existing = (RPObject)object_registry.get(key);
/*     */       
/* 109 */       if (existing != null)
/*     */       {
/* 111 */         this._object_id = existing._object_id;
/*     */       }
/*     */       else
/*     */       {
/* 115 */         this._object_id = new Long(next_key++);
/*     */         
/* 117 */         object_registry.put(key, this);
/*     */         
/* 119 */         object_registry_reverse.put(this._object_id, key);
/*     */       }
/*     */     }
/*     */     
/* 123 */     this.__delegate = key;
/*     */     
/* 125 */     _setDelegate(this.__delegate);
/*     */   }
/*     */   
/*     */ 
/*     */   public long _getOID()
/*     */   {
/* 131 */     return this._object_id.longValue();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void _setDelegate(Object _delegate)
/*     */   {
/* 138 */     throw new RuntimeException("you've got to implement this - " + _delegate);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object _getDelegate()
/*     */   {
/* 144 */     return this.__delegate;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected Object _fixupLocal()
/*     */     throws RPException
/*     */   {
/*     */     Object res;
/*     */     
/* 154 */     synchronized (object_registry)
/*     */     {
/* 156 */       res = object_registry_reverse.get(this._object_id);
/*     */     }
/*     */     
/* 159 */     if (res == null)
/*     */     {
/* 161 */       throw new RPObjectNoLongerExistsException();
/*     */     }
/*     */     
/* 164 */     _setDelegate(res);
/*     */     
/* 166 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void _setRemote(RPRequestDispatcher __dispatcher)
/*     */   {
/* 173 */     this._dispatcher = __dispatcher;
/*     */   }
/*     */   
/*     */ 
/*     */   protected RPRequestDispatcher getDispatcher()
/*     */   {
/* 179 */     return this._dispatcher;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public RPReply _process(RPRequest request)
/*     */   {
/* 186 */     throw new RuntimeException("you've got to implement this - " + request);
/*     */   }
/*     */   
/*     */ 
/*     */   public Object _setLocal()
/*     */   {
/* 192 */     throw new RuntimeException("you've got to implement this");
/*     */   }
/*     */   
/*     */ 
/*     */   public void _refresh()
/*     */   {
/* 198 */     RPObject res = (RPObject)this._dispatcher.dispatch(new RPRequest(this, "_refresh", null)).getResponse();
/*     */     
/* 200 */     _setDelegate(res);
/*     */   }
/*     */   
/*     */ 
/*     */   public String _getName()
/*     */   {
/* 206 */     String str = getClass().getName();
/*     */     
/* 208 */     int dp = str.lastIndexOf('.');
/*     */     
/* 210 */     if (dp != -1)
/*     */     {
/* 212 */       str = str.substring(dp + 1);
/*     */     }
/*     */     
/* 215 */     if (str.startsWith("RP"))
/*     */     {
/* 217 */       str = str.substring(2);
/*     */     }
/*     */     
/* 220 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */   public void notSupported()
/*     */   {
/* 226 */     throw new RuntimeException("RPObject:: method not supported");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void notSupported(Object o)
/*     */   {
/* 233 */     throw new RuntimeException("RPObject:: method not supported - " + o);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/remote/RPObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */