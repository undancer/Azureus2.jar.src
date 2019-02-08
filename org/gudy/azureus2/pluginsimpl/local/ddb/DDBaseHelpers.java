/*     */ package org.gudy.azureus2.pluginsimpl.local.ddb;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import org.gudy.azureus2.core3.util.HashWrapper;
/*     */ import org.gudy.azureus2.core3.util.SHA1Simple;
/*     */ import org.gudy.azureus2.plugins.ddb.DistributedDatabaseException;
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
/*     */ 
/*     */ public class DDBaseHelpers
/*     */ {
/*     */   protected static byte[] encode(Object obj)
/*     */     throws DistributedDatabaseException
/*     */   {
/*  48 */     if (obj == null)
/*     */     {
/*  50 */       throw new DistributedDatabaseException("null not supported"); }
/*     */     byte[] res;
/*  52 */     byte[] res; if ((obj instanceof byte[]))
/*     */     {
/*  54 */       res = (byte[])obj;
/*     */     }
/*  56 */     else if ((obj instanceof String))
/*     */     {
/*     */       try {
/*  59 */         res = ((String)obj).getBytes("UTF-8");
/*     */       }
/*     */       catch (UnsupportedEncodingException e)
/*     */       {
/*  63 */         throw new DistributedDatabaseException("charset error", e);
/*     */       }
/*  65 */     } else { if (((obj instanceof Byte)) || ((obj instanceof Short)) || ((obj instanceof Integer)) || ((obj instanceof Long)) || ((obj instanceof Float)) || ((obj instanceof Double)) || ((obj instanceof Boolean)))
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  73 */         throw new DistributedDatabaseException("not supported yet!");
/*     */       }
/*     */       
/*     */       try
/*     */       {
/*  78 */         ByteArrayOutputStream baos = new ByteArrayOutputStream();
/*     */         
/*  80 */         ObjectOutputStream oos = new ObjectOutputStream(baos);
/*     */         
/*  82 */         oos.writeObject(obj);
/*     */         
/*  84 */         oos.close();
/*     */         
/*  86 */         res = baos.toByteArray();
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/*  90 */         throw new DistributedDatabaseException("encoding fails", e);
/*     */       }
/*     */     }
/*     */     
/*  94 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static Object decode(Class target, byte[] data)
/*     */     throws DistributedDatabaseException
/*     */   {
/* 104 */     if (target == byte[].class)
/*     */     {
/* 106 */       return data;
/*     */     }
/* 108 */     if (target == String.class)
/*     */     {
/*     */       try
/*     */       {
/* 112 */         return new String(data, "UTF-8");
/*     */       }
/*     */       catch (UnsupportedEncodingException e)
/*     */       {
/* 116 */         throw new DistributedDatabaseException("charset error", e);
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/* 121 */       ObjectInputStream iis = new ObjectInputStream(new ByteArrayInputStream(data));
/*     */       
/* 123 */       Object res = iis.readObject();
/*     */       
/* 125 */       if (target.isInstance(res))
/*     */       {
/* 127 */         return res;
/*     */       }
/*     */       
/*     */ 
/* 131 */       throw new DistributedDatabaseException("decoding fails, incompatible type");
/*     */ 
/*     */     }
/*     */     catch (DistributedDatabaseException e)
/*     */     {
/* 136 */       throw e;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 140 */       throw new DistributedDatabaseException("decoding fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static HashWrapper getKey(Class c)
/*     */     throws DistributedDatabaseException
/*     */   {
/* 151 */     String name = c.getName();
/*     */     
/* 153 */     if (name == null)
/*     */     {
/* 155 */       throw new DistributedDatabaseException("name doesn't exist for '" + c.getName() + "'");
/*     */     }
/*     */     
/* 158 */     return new HashWrapper(new SHA1Simple().calculateHash(name.getBytes()));
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/ddb/DDBaseHelpers.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */