/*     */ package com.aelitis.azureus.core.dht.router.impl;
/*     */ 
/*     */ import com.aelitis.azureus.core.dht.impl.DHTLog;
/*     */ import com.aelitis.azureus.core.dht.router.DHTRouterContact;
/*     */ import com.aelitis.azureus.core.dht.router.DHTRouterContactAttachment;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.gudy.azureus2.core3.util.SystemTime;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DHTRouterNodeImpl
/*     */ {
/*     */   private final DHTRouterImpl router;
/*     */   private final int depth;
/*     */   private final boolean contains_router_node_id;
/*     */   private List<DHTRouterContactImpl> buckets;
/*     */   private List<DHTRouterContactImpl> replacements;
/*     */   private DHTRouterNodeImpl left;
/*     */   private DHTRouterNodeImpl right;
/*     */   private long last_lookup_time;
/*     */   
/*     */   protected DHTRouterNodeImpl(DHTRouterImpl _router, int _depth, boolean _contains_router_node_id, List<DHTRouterContactImpl> _buckets)
/*     */   {
/*  59 */     this.router = _router;
/*  60 */     this.depth = _depth;
/*  61 */     this.contains_router_node_id = _contains_router_node_id;
/*  62 */     this.buckets = _buckets;
/*     */   }
/*     */   
/*     */ 
/*     */   protected int getDepth()
/*     */   {
/*  68 */     return this.depth;
/*     */   }
/*     */   
/*     */ 
/*     */   protected boolean containsRouterNodeID()
/*     */   {
/*  74 */     return this.contains_router_node_id;
/*     */   }
/*     */   
/*     */ 
/*     */   protected DHTRouterNodeImpl getLeft()
/*     */   {
/*  80 */     return this.left;
/*     */   }
/*     */   
/*     */ 
/*     */   protected DHTRouterNodeImpl getRight()
/*     */   {
/*  86 */     return this.right;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void split(DHTRouterNodeImpl new_left, DHTRouterNodeImpl new_right)
/*     */   {
/*  94 */     this.buckets = null;
/*     */     
/*  96 */     if (this.replacements != null)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 102 */       for (DHTRouterContactImpl rep : this.replacements)
/*     */       {
/* 104 */         this.router.notifyRemoved(rep);
/*     */       }
/*     */       
/* 107 */       this.replacements = null;
/*     */     }
/*     */     
/* 110 */     this.left = new_left;
/* 111 */     this.right = new_right;
/*     */   }
/*     */   
/*     */ 
/*     */   protected List getBuckets()
/*     */   {
/* 117 */     return this.buckets;
/*     */   }
/*     */   
/*     */ 
/*     */   protected List<DHTRouterContactImpl> getReplacements()
/*     */   {
/* 123 */     return this.replacements;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void addNode(DHTRouterContactImpl node)
/*     */   {
/* 131 */     node.setBucketEntry();
/* 132 */     this.router.notifyAdded(node);
/*     */     
/* 134 */     this.buckets.add(node);
/*     */     
/* 136 */     requestNodeAdd(node, false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTRouterContact addReplacement(DHTRouterContactImpl replacement, int max_rep_per_node)
/*     */   {
/* 144 */     if (max_rep_per_node == 0)
/*     */     {
/* 146 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 151 */     boolean try_ping = false;
/*     */     
/* 153 */     if (this.replacements == null)
/*     */     {
/* 155 */       try_ping = true;
/*     */       
/* 157 */       this.replacements = new ArrayList();
/*     */ 
/*     */ 
/*     */     }
/* 161 */     else if (this.replacements.size() == max_rep_per_node)
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 166 */       if (replacement.hasBeenAlive())
/*     */       {
/* 168 */         for (int i = 0; i < this.replacements.size(); i++)
/*     */         {
/* 170 */           DHTRouterContactImpl r = (DHTRouterContactImpl)this.replacements.get(i);
/*     */           
/* 172 */           if (!r.hasBeenAlive())
/*     */           {
/* 174 */             try_ping = true;
/*     */             
/*     */ 
/* 177 */             this.router.notifyRemoved(r);
/*     */             
/* 179 */             this.replacements.remove(i);
/*     */             
/* 181 */             break;
/*     */           }
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/* 188 */         if (this.replacements.size() == max_rep_per_node)
/*     */         {
/* 190 */           DHTRouterContactImpl removed = (DHTRouterContactImpl)this.replacements.remove(0);
/*     */           
/*     */ 
/* 193 */           this.router.notifyRemoved(removed);
/*     */         }
/*     */         
/*     */       }
/*     */       else
/*     */       {
/* 199 */         for (int i = 0; i < this.replacements.size(); i++)
/*     */         {
/* 201 */           DHTRouterContactImpl r = (DHTRouterContactImpl)this.replacements.get(i);
/*     */           
/* 203 */           if (!r.hasBeenAlive())
/*     */           {
/*     */ 
/* 206 */             this.router.notifyRemoved(r);
/*     */             
/* 208 */             this.replacements.remove(i);
/*     */             
/* 210 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 216 */       try_ping = true;
/*     */     }
/*     */     
/*     */ 
/* 220 */     if (this.replacements.size() == max_rep_per_node)
/*     */     {
/*     */ 
/*     */ 
/* 224 */       return null;
/*     */     }
/*     */     
/*     */ 
/* 228 */     replacement.setReplacement();
/* 229 */     this.router.notifyAdded(replacement);
/*     */     
/* 231 */     this.replacements.add(replacement);
/*     */     
/* 233 */     if (try_ping)
/*     */     {
/* 235 */       for (int i = 0; i < this.buckets.size(); i++)
/*     */       {
/* 237 */         DHTRouterContactImpl c = (DHTRouterContactImpl)this.buckets.get(i);
/*     */         
/*     */ 
/*     */ 
/* 241 */         if ((!this.router.isID(c.getID())) && (!c.getPingOutstanding()))
/*     */         {
/* 243 */           c.setPingOutstanding(true);
/*     */           
/* 245 */           this.router.requestPing(c);
/*     */           
/* 247 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 252 */     return replacement;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected DHTRouterContactImpl updateExistingNode(byte[] node_id, DHTRouterContactAttachment attachment, boolean known_to_be_alive)
/*     */   {
/* 261 */     for (int k = 0; k < this.buckets.size(); k++)
/*     */     {
/* 263 */       DHTRouterContactImpl contact = (DHTRouterContactImpl)this.buckets.get(k);
/*     */       
/* 265 */       if (Arrays.equals(node_id, contact.getID()))
/*     */       {
/* 267 */         if (known_to_be_alive)
/*     */         {
/*     */ 
/* 270 */           alive(contact);
/*     */         }
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 277 */         int new_id = attachment.getInstanceID();
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 284 */         if (new_id != 0)
/*     */         {
/* 286 */           int old_id = contact.getAttachment().getInstanceID();
/*     */           
/* 288 */           if (old_id != new_id)
/*     */           {
/* 290 */             DHTLog.log("Instance ID changed for " + DHTLog.getString(contact.getID()) + ": old = " + old_id + ", new = " + new_id);
/*     */             
/*     */ 
/*     */ 
/* 294 */             contact.setAttachment(attachment);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 300 */             requestNodeAdd(contact, old_id != 0);
/*     */           }
/*     */         }
/*     */         
/* 304 */         return contact;
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 310 */     if (this.replacements != null)
/*     */     {
/* 312 */       for (int k = 0; k < this.replacements.size(); k++)
/*     */       {
/* 314 */         DHTRouterContactImpl contact = (DHTRouterContactImpl)this.replacements.get(k);
/*     */         
/* 316 */         if (Arrays.equals(node_id, contact.getID()))
/*     */         {
/* 318 */           if (known_to_be_alive)
/*     */           {
/*     */ 
/* 321 */             alive(contact);
/*     */           }
/*     */           
/* 324 */           return contact;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 329 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void alive(DHTRouterContactImpl contact)
/*     */   {
/* 338 */     contact.setPingOutstanding(false);
/*     */     
/*     */ 
/* 341 */     boolean was_alive = contact.isAlive();
/*     */     
/* 343 */     if (this.buckets.remove(contact))
/*     */     {
/* 345 */       contact.setAlive();
/*     */       
/* 347 */       if (!was_alive)
/*     */       {
/* 349 */         this.router.notifyNowAlive(contact);
/*     */       }
/*     */       
/*     */ 
/* 353 */       this.buckets.add(contact);
/*     */     }
/* 355 */     else if (this.replacements.remove(contact))
/*     */     {
/* 357 */       long last_time = contact.getFirstFailOrLastAliveTime();
/*     */       
/* 359 */       contact.setAlive();
/*     */       
/* 361 */       if (!was_alive)
/*     */       {
/* 363 */         this.router.notifyNowAlive(contact);
/*     */       }
/*     */       
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 371 */       if (contact.getLastAliveTime() - last_time > 30000L)
/*     */       {
/* 373 */         for (int i = 0; i < this.buckets.size(); i++)
/*     */         {
/* 375 */           DHTRouterContactImpl c = (DHTRouterContactImpl)this.buckets.get(i);
/*     */           
/*     */ 
/*     */ 
/* 379 */           if ((!this.router.isID(c.getID())) && (!c.getPingOutstanding()))
/*     */           {
/* 381 */             c.setPingOutstanding(true);
/*     */             
/* 383 */             this.router.requestPing(c);
/*     */             
/* 385 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */       
/*     */ 
/* 391 */       this.replacements.add(contact);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void dead(DHTRouterContactImpl contact, boolean force)
/*     */   {
/* 402 */     contact.setPingOutstanding(false);
/*     */     
/*     */ 
/* 405 */     boolean was_failing = contact.isFailing();
/*     */     
/* 407 */     if ((contact.setFailed()) || (force))
/*     */     {
/*     */ 
/*     */ 
/* 411 */       if (this.buckets.remove(contact))
/*     */       {
/* 413 */         if (!was_failing)
/*     */         {
/* 415 */           this.router.notifyNowFailing(contact);
/*     */         }
/*     */         
/*     */ 
/* 419 */         this.router.notifyRemoved(contact);
/*     */         
/* 421 */         if ((this.replacements != null) && (this.replacements.size() > 0))
/*     */         {
/*     */ 
/*     */ 
/* 425 */           boolean replaced = false;
/*     */           
/* 427 */           for (int i = this.replacements.size() - 1; i >= 0; i--)
/*     */           {
/* 429 */             DHTRouterContactImpl rep = (DHTRouterContactImpl)this.replacements.get(i);
/*     */             
/* 431 */             if (rep.hasBeenAlive())
/*     */             {
/* 433 */               DHTLog.log(DHTLog.getString(contact.getID()) + ": using live replacement " + DHTLog.getString(rep.getID()));
/*     */               
/*     */ 
/* 436 */               rep.setBucketEntry();
/* 437 */               this.router.notifyLocationChanged(rep);
/*     */               
/* 439 */               this.replacements.remove(rep);
/*     */               
/* 441 */               this.buckets.add(rep);
/*     */               
/* 443 */               replaced = true;
/*     */               
/* 445 */               requestNodeAdd(rep, false);
/*     */               
/* 447 */               break;
/*     */             }
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 453 */           if (!replaced)
/*     */           {
/* 455 */             DHTRouterContactImpl rep = (DHTRouterContactImpl)this.replacements.remove(this.replacements.size() - 1);
/*     */             
/* 457 */             DHTLog.log(DHTLog.getString(contact.getID()) + ": using unknown replacement " + DHTLog.getString(rep.getID()));
/*     */             
/*     */ 
/* 460 */             rep.setBucketEntry();
/* 461 */             this.router.notifyLocationChanged(rep);
/*     */             
/* 463 */             this.buckets.add(rep);
/*     */             
/*     */ 
/*     */ 
/*     */ 
/* 468 */             requestNodeAdd(rep, false);
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 473 */         if (!was_failing)
/*     */         {
/* 475 */           this.router.notifyNowFailing(contact);
/*     */         }
/*     */         
/*     */ 
/* 479 */         this.router.notifyRemoved(contact);
/*     */         
/* 481 */         this.replacements.remove(contact);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void requestNodeAdd(DHTRouterContactImpl contact, boolean definite_change)
/*     */   {
/* 494 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 496 */     if (now - contact.getLastAddedTime() > 10000L)
/*     */     {
/* 498 */       contact.setLastAddedTime(now);
/*     */       
/* 500 */       this.router.requestNodeAdd(contact);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/* 507 */     else if (definite_change)
/*     */     {
/* 509 */       this.router.log("requestNodeAdd for " + contact.getString() + " denied as too soon after previous ");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected long getTimeSinceLastLookup()
/*     */   {
/* 517 */     long now = SystemTime.getCurrentTime();
/*     */     
/* 519 */     if (now < this.last_lookup_time)
/*     */     {
/*     */ 
/*     */ 
/* 523 */       return Long.MAX_VALUE;
/*     */     }
/*     */     
/* 526 */     return now - this.last_lookup_time;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void setLastLookupTime()
/*     */   {
/* 532 */     this.last_lookup_time = SystemTime.getCurrentTime();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void print(String indent, String prefix)
/*     */   {
/* 540 */     if (this.left == null)
/*     */     {
/* 542 */       this.router.log(indent + prefix + ": buckets = " + this.buckets.size() + contactsToString(this.buckets) + ", replacements = " + (this.replacements == null ? "null" : new StringBuilder().append(this.replacements.size()).append(contactsToString(this.replacements)).toString()) + (this.contains_router_node_id ? " *" : " ") + (this == this.router.getSmallestSubtree() ? "SST" : "") + " tsll=" + getTimeSinceLastLookup());
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 552 */       this.router.log(indent + prefix + ":" + (this.contains_router_node_id ? " *" : " ") + (this == this.router.getSmallestSubtree() ? "SST" : ""));
/*     */       
/*     */ 
/* 555 */       this.left.print(indent + "  ", prefix + "1");
/*     */       
/* 557 */       this.right.print(indent + "  ", prefix + "0");
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected String contactsToString(List contacts)
/*     */   {
/* 565 */     StringBuilder sb = new StringBuilder(contacts.size() * 64);
/* 566 */     sb.append("{");
/*     */     
/* 568 */     for (int i = 0; i < contacts.size(); i++)
/*     */     {
/* 570 */       if (i > 0) {
/* 571 */         sb.append(", ");
/*     */       }
/* 573 */       ((DHTRouterContactImpl)contacts.get(i)).getString(sb);
/*     */     }
/*     */     
/* 576 */     sb.append("}");
/* 577 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/dht/router/impl/DHTRouterNodeImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */