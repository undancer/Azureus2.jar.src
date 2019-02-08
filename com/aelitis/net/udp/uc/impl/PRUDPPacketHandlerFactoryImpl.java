/*     */ package com.aelitis.net.udp.uc.impl;
/*     */ 
/*     */ import com.aelitis.net.udp.uc.PRUDPPacketHandler;
/*     */ import com.aelitis.net.udp.uc.PRUDPReleasablePacketHandler;
/*     */ import com.aelitis.net.udp.uc.PRUDPRequestHandler;
/*     */ import java.net.InetAddress;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.util.AEMonitor;
/*     */ import org.gudy.azureus2.core3.util.Debug;
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
/*     */ public class PRUDPPacketHandlerFactoryImpl
/*     */ {
/*  43 */   private static Map<Integer, PRUDPPacketHandlerImpl> receiver_map = new HashMap();
/*     */   
/*  45 */   private static AEMonitor class_mon = new AEMonitor("PRUDPPHF");
/*  46 */   private static Map releasable_map = new HashMap();
/*  47 */   private static Set non_releasable_set = new HashSet();
/*     */   
/*     */   public static List<PRUDPPacketHandler> getHandlers()
/*     */   {
/*     */     try
/*     */     {
/*  53 */       class_mon.enter();
/*     */       
/*  55 */       return new ArrayList(receiver_map.values());
/*     */     }
/*     */     finally
/*     */     {
/*  59 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PRUDPPacketHandler getHandler(int port, InetAddress bind_ip, PRUDPRequestHandler request_handler)
/*     */   {
/*  69 */     Integer f_port = new Integer(port);
/*     */     try
/*     */     {
/*  72 */       class_mon.enter();
/*     */       
/*  74 */       non_releasable_set.add(f_port);
/*     */       
/*  76 */       PRUDPPacketHandlerImpl receiver = (PRUDPPacketHandlerImpl)receiver_map.get(f_port);
/*     */       
/*  78 */       if (receiver == null)
/*     */       {
/*  80 */         receiver = new PRUDPPacketHandlerImpl(port, bind_ip, null);
/*     */         
/*  82 */         receiver_map.put(f_port, receiver);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  90 */       if (request_handler != null)
/*     */       {
/*  92 */         receiver.setRequestHandler(request_handler);
/*     */       }
/*     */       
/*  95 */       return receiver;
/*     */     }
/*     */     finally
/*     */     {
/*  99 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public static PRUDPReleasablePacketHandler getReleasableHandler(int port, PRUDPRequestHandler request_handler)
/*     */   {
/* 108 */     final Integer f_port = new Integer(port);
/*     */     try
/*     */     {
/* 111 */       class_mon.enter();
/*     */       
/* 113 */       PRUDPPacketHandlerImpl receiver = (PRUDPPacketHandlerImpl)receiver_map.get(f_port);
/*     */       
/* 115 */       if (receiver == null)
/*     */       {
/* 117 */         receiver = new PRUDPPacketHandlerImpl(port, null, null);
/*     */         
/* 119 */         receiver_map.put(f_port, receiver);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 127 */       if (request_handler != null)
/*     */       {
/* 129 */         receiver.setRequestHandler(request_handler);
/*     */       }
/*     */       
/* 132 */       PRUDPPacketHandlerImpl f_receiver = receiver;
/*     */       
/* 134 */       PRUDPReleasablePacketHandler rel = new PRUDPReleasablePacketHandler()
/*     */       {
/*     */ 
/*     */         public PRUDPPacketHandler getHandler()
/*     */         {
/*     */ 
/* 140 */           return this.val$f_receiver;
/*     */         }
/*     */         
/*     */         public void release()
/*     */         {
/*     */           try
/*     */           {
/* 147 */             PRUDPPacketHandlerFactoryImpl.class_mon.enter();
/*     */             
/* 149 */             List l = (List)PRUDPPacketHandlerFactoryImpl.releasable_map.get(f_port);
/*     */             
/* 151 */             if (l == null)
/*     */             {
/* 153 */               Debug.out("hmm");
/*     */ 
/*     */ 
/*     */             }
/* 157 */             else if (!l.remove(this))
/*     */             {
/* 159 */               Debug.out("hmm");
/*     */ 
/*     */ 
/*     */             }
/* 163 */             else if (l.size() == 0)
/*     */             {
/* 165 */               if (!PRUDPPacketHandlerFactoryImpl.non_releasable_set.contains(f_port))
/*     */               {
/* 167 */                 this.val$f_receiver.destroy();
/*     */                 
/* 169 */                 PRUDPPacketHandlerFactoryImpl.receiver_map.remove(f_port);
/*     */               }
/*     */               
/* 172 */               PRUDPPacketHandlerFactoryImpl.releasable_map.remove(f_port);
/*     */             }
/*     */             
/*     */           }
/*     */           finally
/*     */           {
/* 178 */             PRUDPPacketHandlerFactoryImpl.class_mon.exit();
/*     */           }
/*     */           
/*     */         }
/* 182 */       };
/* 183 */       List l = (List)releasable_map.get(f_port);
/*     */       
/* 185 */       if (l == null)
/*     */       {
/* 187 */         l = new ArrayList();
/*     */         
/* 189 */         releasable_map.put(f_port, l);
/*     */       }
/*     */       
/* 192 */       l.add(rel);
/*     */       
/* 194 */       if (l.size() > 1024)
/*     */       {
/* 196 */         Debug.out("things going wrong here");
/*     */       }
/*     */       
/* 199 */       return rel;
/*     */     }
/*     */     finally
/*     */     {
/* 203 */       class_mon.exit();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/net/udp/uc/impl/PRUDPPacketHandlerFactoryImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */