/*     */ package com.aelitis.azureus.core.proxy.socks.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.proxy.socks.AESocksProxyAddress;
/*     */ import java.net.InetAddress;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.HostNameToIPResolver;
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
/*     */ 
/*     */ public class AESocksProxyAddressImpl
/*     */   implements AESocksProxyAddress
/*     */ {
/*     */   protected final String unresolved_address;
/*     */   protected InetAddress address;
/*     */   protected final int port;
/*     */   
/*     */   protected AESocksProxyAddressImpl(String _unresolved_address, InetAddress _address, int _port)
/*     */   {
/*  49 */     this.unresolved_address = _unresolved_address;
/*  50 */     this.address = _address;
/*  51 */     this.port = _port;
/*     */     
/*  53 */     if (this.address == null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*  58 */       int dots = 0;
/*  59 */       boolean ok = true;
/*     */       
/*  61 */       for (int i = 0; i < this.unresolved_address.length(); i++)
/*     */       {
/*  63 */         char c = this.unresolved_address.charAt(i);
/*     */         
/*  65 */         if (c == '.')
/*     */         {
/*  67 */           dots++;
/*     */           
/*  69 */           if (dots > 3)
/*     */           {
/*  71 */             ok = false;
/*     */             
/*  73 */             break;
/*     */           }
/*  75 */         } else { if (!Character.isDigit(c))
/*     */           {
/*  77 */             ok = false;
/*     */             
/*  79 */             break;
/*     */           }
/*     */           
/*     */ 
/*     */ 
/*     */ 
/*  85 */           if (i > 15)
/*     */           {
/*  87 */             ok = false;
/*     */             
/*  89 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*  94 */       if ((ok) && (dots == 3)) {
/*     */         try
/*     */         {
/*  97 */           this.address = HostNameToIPResolver.syncResolve(this.unresolved_address);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 101 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getUnresolvedAddress()
/*     */   {
/* 110 */     return this.unresolved_address;
/*     */   }
/*     */   
/*     */ 
/*     */   public InetAddress getAddress()
/*     */   {
/* 116 */     return this.address;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 122 */     return this.port;
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/proxy/socks/impl/AESocksProxyAddressImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */