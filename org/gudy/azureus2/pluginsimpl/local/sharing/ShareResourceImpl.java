/*     */ package org.gudy.azureus2.pluginsimpl.local.sharing;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.BrokenMd5Hasher;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.TorrentUtils;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceDeletionVetoException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceDirContents;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceEvent;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceListener;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceWillBeDeletedListener;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
/*     */ import org.gudy.azureus2.pluginsimpl.local.torrent.TorrentManagerImpl;
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
/*     */ public abstract class ShareResourceImpl
/*     */   implements ShareResource
/*     */ {
/*  43 */   protected static BrokenMd5Hasher hasher = new BrokenMd5Hasher();
/*     */   
/*     */   protected ShareManagerImpl manager;
/*     */   
/*     */   protected int type;
/*     */   protected ShareResourceDirContents parent;
/*  49 */   protected Map attributes = new HashMap();
/*     */   
/*  51 */   protected List change_listeners = new ArrayList();
/*  52 */   protected List deletion_listeners = new ArrayList();
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ShareResourceImpl(ShareManagerImpl _manager, int _type)
/*     */   {
/*  61 */     this.manager = _manager;
/*  62 */     this.type = _type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ShareResourceImpl(ShareManagerImpl _manager, int _type, Map _map)
/*     */   {
/*  73 */     this.manager = _manager;
/*  74 */     this.type = _type;
/*     */     
/*  76 */     Map attrs = (Map)_map.get("attributes");
/*     */     
/*  78 */     if (attrs != null)
/*     */     {
/*  80 */       Iterator keys = attrs.keySet().iterator();
/*     */       
/*  82 */       while (keys.hasNext())
/*     */       {
/*  84 */         String key = (String)keys.next();
/*     */         try
/*     */         {
/*  87 */           String value = new String((byte[])attrs.get(key), "UTF8");
/*     */           
/*  89 */           TorrentAttribute ta = TorrentManagerImpl.getSingleton().getAttribute(key);
/*     */           
/*  91 */           if (ta == null)
/*     */           {
/*  93 */             Debug.out("Invalid attribute '" + key);
/*     */           }
/*     */           else {
/*  96 */             this.attributes.put(ta, value);
/*     */           }
/*     */         }
/*     */         catch (Throwable e) {
/* 100 */           Debug.printStackTrace(e);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void serialiseResource(Map map)
/*     */   {
/* 110 */     Iterator it = this.attributes.keySet().iterator();
/*     */     
/* 112 */     Map attrs = new HashMap();
/*     */     
/* 114 */     map.put("attributes", attrs);
/*     */     
/* 116 */     while (it.hasNext())
/*     */     {
/* 118 */       TorrentAttribute ta = (TorrentAttribute)it.next();
/*     */       
/* 120 */       String value = (String)this.attributes.get(ta);
/*     */       try
/*     */       {
/* 123 */         if (value != null)
/*     */         {
/* 125 */           attrs.put(ta.getName(), value.getBytes("UTF8"));
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 130 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public ShareResourceDirContents getParent()
/*     */   {
/* 138 */     return this.parent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void setParent(ShareResourceDirContents _parent)
/*     */   {
/* 145 */     this.parent = _parent;
/*     */   }
/*     */   
/*     */ 
/*     */   public ShareResource[] getChildren()
/*     */   {
/* 151 */     return new ShareResource[0];
/*     */   }
/*     */   
/*     */ 
/*     */   public int getType()
/*     */   {
/* 157 */     return this.type;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setAttribute(final TorrentAttribute attribute, String value)
/*     */   {
/* 165 */     ShareConfigImpl config = this.manager.getShareConfig();
/*     */     try
/*     */     {
/* 168 */       config.suspendSaving();
/*     */       
/* 170 */       ShareResource[] kids = getChildren();
/*     */       
/* 172 */       for (int i = 0; i < kids.length; i++)
/*     */       {
/* 174 */         kids[i].setAttribute(attribute, value);
/*     */       }
/*     */       
/* 177 */       String old_value = (String)this.attributes.get(attribute);
/*     */       
/* 179 */       if ((old_value == null) && (value == null)) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 184 */       if ((old_value != null) && (value != null) && (old_value.equals(value))) {
/*     */         return;
/*     */       }
/*     */       
/*     */ 
/* 189 */       this.attributes.put(attribute, value);
/*     */       try
/*     */       {
/* 192 */         config.saveConfig();
/*     */       }
/*     */       catch (ShareException e)
/*     */       {
/* 196 */         Debug.printStackTrace(e);
/*     */       }
/*     */       
/*     */ 
/*     */       try
/*     */       {
/* 202 */         config.resumeSaving();
/*     */       }
/*     */       catch (ShareException e)
/*     */       {
/* 206 */         Debug.printStackTrace(e);
/*     */       }
/*     */       
/*     */ 
/* 210 */       i = 0;
/*     */     }
/*     */     finally
/*     */     {
/*     */       try
/*     */       {
/* 202 */         config.resumeSaving();
/*     */       }
/*     */       catch (ShareException e)
/*     */       {
/* 206 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */     int i;
/* 210 */     for (; i < this.change_listeners.size(); i++) {
/*     */       try
/*     */       {
/* 213 */         ((ShareResourceListener)this.change_listeners.get(i)).shareResourceChanged(this, new ShareResourceEvent()
/*     */         {
/*     */ 
/*     */ 
/*     */           public int getType()
/*     */           {
/*     */ 
/* 220 */             return 1;
/*     */           }
/*     */           
/*     */ 
/*     */           public Object getData()
/*     */           {
/* 226 */             return attribute;
/*     */           }
/*     */         });
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 232 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getAttribute(TorrentAttribute attribute)
/*     */   {
/* 241 */     return (String)this.attributes.get(attribute);
/*     */   }
/*     */   
/*     */ 
/*     */   public TorrentAttribute[] getAttributes()
/*     */   {
/* 247 */     TorrentAttribute[] res = new TorrentAttribute[this.attributes.size()];
/*     */     
/* 249 */     this.attributes.keySet().toArray(res);
/*     */     
/* 251 */     return res;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void inheritAttributes(ShareResourceImpl source)
/*     */   {
/* 258 */     TorrentAttribute[] attrs = source.getAttributes();
/*     */     
/* 260 */     for (int i = 0; i < attrs.length; i++)
/*     */     {
/* 262 */       setAttribute(attrs[i], source.getAttribute(attrs[i]));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void delete()
/*     */     throws ShareException, ShareResourceDeletionVetoException
/*     */   {
/* 271 */     if (getParent() != null)
/*     */     {
/*     */ 
/* 274 */       throw new ShareResourceDeletionVetoException(MessageText.getString("plugin.sharing.remove.veto"));
/*     */     }
/*     */     
/* 277 */     delete(false);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void delete(boolean force)
/*     */     throws ShareException, ShareResourceDeletionVetoException
/*     */   {
/* 286 */     delete(force, true);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void delete(boolean force, boolean fire_listeners)
/*     */     throws ShareException, ShareResourceDeletionVetoException
/*     */   {
/* 296 */     if (!force)
/*     */     {
/* 298 */       canBeDeleted();
/*     */     }
/*     */     
/* 301 */     this.manager.delete(this, fire_listeners);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public abstract boolean canBeDeleted()
/*     */     throws ShareResourceDeletionVetoException;
/*     */   
/*     */ 
/*     */   public boolean isPersistent()
/*     */   {
/* 312 */     Map<String, String> properties = getProperties();
/*     */     
/* 314 */     if (properties == null)
/*     */     {
/* 316 */       return false;
/*     */     }
/*     */     
/* 319 */     String persistent_str = (String)properties.get("persistent");
/*     */     
/* 321 */     boolean persistent = (persistent_str != null) && (persistent_str.equalsIgnoreCase("true"));
/*     */     
/* 323 */     return persistent;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract void deleteInternal();
/*     */   
/*     */ 
/*     */   protected byte[] getFingerPrint(File file)
/*     */     throws ShareException
/*     */   {
/*     */     try
/*     */     {
/* 336 */       StringBuffer buffer = new StringBuffer();
/*     */       
/* 338 */       getFingerPrintSupport(buffer, file, TorrentUtils.getIgnoreSet());
/*     */       
/* 340 */       return hasher.calculateHash(buffer.toString().getBytes());
/*     */     }
/*     */     catch (ShareException e)
/*     */     {
/* 344 */       throw e;
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 348 */       throw new ShareException("ShareResource::getFingerPrint: fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void getFingerPrintSupport(StringBuffer buffer, File file, Set ignore_set)
/*     */     throws ShareException
/*     */   {
/*     */     try
/*     */     {
/* 361 */       if (file.isFile())
/*     */       {
/* 363 */         long mod = file.lastModified();
/* 364 */         long size = file.length();
/*     */         
/* 366 */         String file_name = file.getName();
/*     */         
/* 368 */         if (!ignore_set.contains(file_name.toLowerCase()))
/*     */         {
/*     */ 
/*     */ 
/* 372 */           buffer.append(file_name).append(":").append(mod).append(":").append(size);
/*     */         }
/* 374 */       } else if (file.isDirectory())
/*     */       {
/* 376 */         File[] dir_file_list = file.listFiles();
/*     */         
/* 378 */         List file_list = new ArrayList(Arrays.asList(dir_file_list));
/*     */         
/* 380 */         Collections.sort(file_list);
/*     */         
/* 382 */         for (int i = 0; i < file_list.size(); i++)
/*     */         {
/* 384 */           File f = (File)file_list.get(i);
/*     */           
/* 386 */           String file_name = f.getName();
/*     */           
/* 388 */           if ((!file_name.equals(".")) && (!file_name.equals("..")))
/*     */           {
/* 390 */             StringBuffer sub_print = new StringBuffer();
/*     */             
/* 392 */             getFingerPrintSupport(sub_print, f, ignore_set);
/*     */             
/* 394 */             if (sub_print.length() > 0)
/*     */             {
/* 396 */               buffer.append(":").append(sub_print);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       else {
/* 402 */         throw new ShareException("ShareResource::getFingerPrint: '" + file.toString() + "' doesn't exist");
/*     */       }
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 407 */       if ((e instanceof ShareException))
/*     */       {
/* 409 */         throw ((ShareException)e);
/*     */       }
/*     */       
/* 412 */       Debug.printStackTrace(e);
/*     */       
/* 414 */       throw new ShareException("ShareResource::getFingerPrint: fails", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected String getNewTorrentLocation()
/*     */     throws ShareException
/*     */   {
/* 422 */     return this.manager.getNewTorrentLocation();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void writeTorrent(ShareItemImpl item)
/*     */     throws ShareException
/*     */   {
/* 431 */     this.manager.writeTorrent(item);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void readTorrent(ShareItemImpl item)
/*     */     throws ShareException
/*     */   {
/* 440 */     this.manager.readTorrent(item);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void deleteTorrent(ShareItemImpl item)
/*     */   {
/* 447 */     this.manager.deleteTorrent(item);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File getTorrentFile(ShareItemImpl item)
/*     */   {
/* 454 */     return this.manager.getTorrentFile(item);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected abstract void checkConsistency()
/*     */     throws ShareException;
/*     */   
/*     */ 
/*     */ 
/*     */   public void addChangeListener(ShareResourceListener l)
/*     */   {
/* 466 */     this.change_listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeChangeListener(ShareResourceListener l)
/*     */   {
/* 473 */     this.change_listeners.remove(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void addDeletionListener(ShareResourceWillBeDeletedListener l)
/*     */   {
/* 480 */     this.deletion_listeners.add(l);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void removeDeletionListener(ShareResourceWillBeDeletedListener l)
/*     */   {
/* 487 */     this.deletion_listeners.remove(l);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/sharing/ShareResourceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */