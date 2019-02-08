/*     */ package com.aelitis.azureus.core.dht.transport.udp.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportAlternativeContact;
/*     */ import com.aelitis.azureus.core.dht.transport.DHTTransportAlternativeNetwork;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
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
/*     */ public class DHTTransportAlternativeNetworkImpl
/*     */   implements DHTTransportAlternativeNetwork
/*     */ {
/*     */   private static final int LIVE_AGE_SECS = 1200;
/*     */   private static final int LIVEISH_AGE_SECS = 2400;
/*     */   private static final int MAX_CONTACTS_PUB = 64;
/*     */   private static final int MAX_CONTACTS_I2P = 16;
/*     */   private static final boolean TRACE = false;
/*     */   private final int network;
/*     */   private final int max_contacts;
/*  48 */   private final TreeSet<DHTTransportAlternativeContact> contacts = new TreeSet(new Comparator()
/*     */   {
/*     */ 
/*     */ 
/*     */ 
/*     */     public int compare(DHTTransportAlternativeContact o1, DHTTransportAlternativeContact o2)
/*     */     {
/*     */ 
/*     */ 
/*  57 */       int res = o1.getAge() - o2.getAge();
/*     */       
/*  59 */       if (res == 0)
/*     */       {
/*  61 */         res = o1.getID() - o2.getID();
/*     */       }
/*     */       
/*  64 */       return res;
/*     */     }
/*  48 */   });
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
/*     */   protected DHTTransportAlternativeNetworkImpl(int _net)
/*     */   {
/*  73 */     this.network = _net;
/*     */     
/*  75 */     this.max_contacts = (this.network == 3 ? 16 : 64);
/*     */   }
/*     */   
/*     */ 
/*     */   public int getNetworkType()
/*     */   {
/*  81 */     return this.network;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public List<DHTTransportAlternativeContact> getContacts(int max)
/*     */   {
/*  88 */     return getContacts(max, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected List<DHTTransportAlternativeContact> getContacts(int max, boolean live_only)
/*     */   {
/*  96 */     if (max == 0)
/*     */     {
/*  98 */       max = this.max_contacts;
/*     */     }
/*     */     
/* 101 */     List<DHTTransportAlternativeContact> result = new ArrayList(max);
/*     */     
/* 103 */     Set<Integer> used_ids = new HashSet();
/*     */     
/* 105 */     synchronized (this.contacts)
/*     */     {
/* 107 */       Iterator<DHTTransportAlternativeContact> it = this.contacts.iterator();
/*     */       
/* 109 */       while (it.hasNext())
/*     */       {
/* 111 */         DHTTransportAlternativeContact contact = (DHTTransportAlternativeContact)it.next();
/*     */         
/* 113 */         if ((live_only) && (contact.getAge() > 2400)) {
/*     */           break;
/*     */         }
/*     */         
/*     */ 
/* 118 */         Integer id = Integer.valueOf(contact.getID());
/*     */         
/* 120 */         if (!used_ids.contains(id))
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/* 125 */           used_ids.add(id);
/*     */           
/* 127 */           result.add(contact);
/*     */           
/* 129 */           if (result.size() == max) {
/*     */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 141 */     return result;
/*     */   }
/*     */   
/*     */ 
/*     */   private void trim()
/*     */   {
/* 147 */     Iterator<DHTTransportAlternativeContact> it = this.contacts.iterator();
/*     */     
/* 149 */     int pos = 0;
/*     */     
/* 151 */     while (it.hasNext())
/*     */     {
/* 153 */       it.next();
/*     */       
/* 155 */       pos++;
/*     */       
/* 157 */       if (pos > this.max_contacts)
/*     */       {
/* 159 */         it.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void addContactsForSend(List<DHTTransportAlternativeContact> new_contacts)
/*     */   {
/* 168 */     synchronized (this.contacts)
/*     */     {
/* 170 */       for (DHTTransportAlternativeContact new_contact : new_contacts)
/*     */       {
/*     */ 
/*     */ 
/* 174 */         this.contacts.add(new_contact);
/*     */       }
/*     */       
/* 177 */       if (this.contacts.size() > this.max_contacts)
/*     */       {
/* 179 */         trim();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addContactFromReply(DHTTransportAlternativeContact new_contact)
/*     */   {
/* 190 */     synchronized (this.contacts)
/*     */     {
/*     */ 
/*     */ 
/* 194 */       this.contacts.add(new_contact);
/*     */       
/* 196 */       if (this.contacts.size() > this.max_contacts)
/*     */       {
/* 198 */         trim();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected int getRequiredContactCount()
/*     */   {
/* 208 */     synchronized (this.contacts)
/*     */     {
/* 210 */       int num_contacts = this.contacts.size();
/*     */       
/* 212 */       int result = 0;
/*     */       
/* 214 */       if (num_contacts < this.max_contacts)
/*     */       {
/* 216 */         result = this.max_contacts - num_contacts;
/*     */       }
/*     */       else
/*     */       {
/* 220 */         Iterator<DHTTransportAlternativeContact> it = this.contacts.iterator();
/*     */         
/* 222 */         int pos = 0;
/*     */         
/* 224 */         while (it.hasNext())
/*     */         {
/* 226 */           DHTTransportAlternativeContact contact = (DHTTransportAlternativeContact)it.next();
/*     */           
/* 228 */           if (contact.getAge() > 1200)
/*     */           {
/* 230 */             result = this.max_contacts - pos;
/*     */             
/* 232 */             break;
/*     */           }
/*     */           
/*     */ 
/* 236 */           pos++;
/*     */         }
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/* 243 */       return result;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private String getString(DHTTransportAlternativeContact contact)
/*     */   {
/* 251 */     return contact.getProperties() + ", age=" + contact.getAge();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/transport/udp/impl/DHTTransportAlternativeNetworkImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */