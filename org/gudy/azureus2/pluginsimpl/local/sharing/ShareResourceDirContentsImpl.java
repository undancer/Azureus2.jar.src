/*     */ package org.gudy.azureus2.pluginsimpl.local.sharing;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.internat.MessageText;
/*     */ import org.gudy.azureus2.core3.util.AEThread2;
/*     */ import org.gudy.azureus2.core3.util.BDecoder;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.RandomUtils;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResource;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceDeletionVetoException;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceDirContents;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceListener;
/*     */ import org.gudy.azureus2.plugins.sharing.ShareResourceWillBeDeletedListener;
/*     */ import org.gudy.azureus2.plugins.torrent.TorrentAttribute;
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
/*     */ public class ShareResourceDirContentsImpl
/*     */   extends ShareResourceImpl
/*     */   implements ShareResourceDirContents
/*     */ {
/*     */   private final File root;
/*     */   private final boolean recursive;
/*     */   private final Map<String, String> properties;
/*     */   private final byte[] personal_key;
/*  48 */   protected ShareResource[] children = new ShareResource[0];
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ShareResourceDirContentsImpl(ShareManagerImpl _manager, File _dir, boolean _recursive, boolean _personal, Map<String, String> _properties, boolean _async_check)
/*     */     throws ShareException
/*     */   {
/*  61 */     super(_manager, 3);
/*     */     
/*  63 */     this.root = _dir;
/*  64 */     this.recursive = _recursive;
/*  65 */     this.properties = _properties;
/*     */     
/*  67 */     if (!this.root.exists())
/*     */     {
/*  69 */       throw new ShareException("Dir '" + this.root.getName() + "' not found");
/*     */     }
/*     */     
/*  72 */     if (this.root.isFile())
/*     */     {
/*  74 */       throw new ShareException("Not a directory");
/*     */     }
/*     */     
/*  77 */     this.personal_key = (_personal ? RandomUtils.nextSecureHash() : null);
/*     */     
/*     */ 
/*     */ 
/*  81 */     if (_async_check)
/*     */     {
/*  83 */       new AEThread2("SM:asyncCheck", true)
/*     */       {
/*     */         public void run()
/*     */         {
/*     */           try
/*     */           {
/*  89 */             ShareResourceDirContentsImpl.this.checkConsistency();
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/*  93 */             Debug.out("Failed to update consistency", e);
/*     */           }
/*     */           
/*     */         }
/*     */         
/*     */       }.start();
/*     */     } else {
/* 100 */       checkConsistency();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected ShareResourceDirContentsImpl(ShareManagerImpl _manager, File _dir, boolean _recursive, Map _map)
/*     */     throws ShareException
/*     */   {
/* 113 */     super(_manager, 3, _map);
/*     */     
/* 115 */     this.root = _dir;
/* 116 */     this.recursive = _recursive;
/*     */     
/*     */ 
/*     */ 
/* 120 */     if (!this.root.exists())
/*     */     {
/* 122 */       Debug.out("Dir '" + this.root.getName() + "' not found");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     }
/* 128 */     else if (this.root.isFile())
/*     */     {
/* 130 */       throw new ShareException("Not a directory");
/*     */     }
/*     */     
/*     */ 
/* 134 */     this.personal_key = ((byte[])_map.get("per_key"));
/*     */     
/* 136 */     this.properties = BDecoder.decodeStrings((Map)_map.get("props"));
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean canBeDeleted()
/*     */     throws ShareResourceDeletionVetoException
/*     */   {
/* 146 */     for (int i = 0; i < this.children.length; i++)
/*     */     {
/* 148 */       if (!this.children[i].canBeDeleted())
/*     */       {
/* 150 */         return false;
/*     */       }
/*     */     }
/*     */     
/* 154 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected void checkConsistency()
/*     */     throws ShareException
/*     */   {
/* 164 */     List kids = checkConsistency(this.root);
/*     */     
/* 166 */     if (kids != null)
/*     */     {
/* 168 */       this.children = new ShareResource[kids.size()];
/*     */       
/* 170 */       kids.toArray(this.children);
/*     */     }
/*     */     else
/*     */     {
/* 174 */       this.children = new ShareResource[0];
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected List checkConsistency(File dir)
/*     */     throws ShareException
/*     */   {
/* 184 */     List kids = new ArrayList();
/*     */     
/* 186 */     File[] files = dir.listFiles();
/*     */     
/* 188 */     if ((files == null) || (!dir.exists()))
/*     */     {
/*     */ 
/*     */ 
/* 192 */       if (!isPersistent())
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 198 */         if (dir == this.root)
/*     */         {
/* 200 */           return null;
/*     */         }
/*     */         
/*     */ 
/* 204 */         this.manager.delete(this, true);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 209 */       for (int i = 0; i < files.length; i++)
/*     */       {
/* 211 */         File file = files[i];
/*     */         
/* 213 */         String file_name = file.getName();
/*     */         
/* 215 */         if ((!file_name.equals(".")) && (!file_name.equals("..")))
/*     */         {
/* 217 */           if (file.isDirectory())
/*     */           {
/* 219 */             if (this.recursive)
/*     */             {
/* 221 */               List child = checkConsistency(file);
/*     */               
/* 223 */               kids.add(new shareNode(this, file, child));
/*     */             }
/*     */             else
/*     */             {
/*     */               try {
/* 228 */                 ShareResource res = this.manager.getDir(file);
/*     */                 
/* 230 */                 if (res == null)
/*     */                 {
/* 232 */                   res = this.manager.addDir(this, file, this.personal_key != null, this.properties);
/*     */                 }
/*     */                 
/* 235 */                 kids.add(res);
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 239 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/*     */           }
/*     */           else {
/*     */             try {
/* 245 */               ShareResource res = this.manager.getFile(file);
/*     */               
/* 247 */               if (res == null)
/*     */               {
/* 249 */                 res = this.manager.addFile(this, file, this.personal_key != null, this.properties);
/*     */               }
/*     */               
/* 252 */               kids.add(res);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/* 256 */               Debug.printStackTrace(e);
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       
/* 262 */       for (int i = 0; i < kids.size(); i++)
/*     */       {
/* 264 */         Object o = kids.get(i);
/*     */         
/* 266 */         if ((o instanceof ShareResourceImpl))
/*     */         {
/* 268 */           ((ShareResourceImpl)o).setParent(this);
/*     */         }
/*     */         else {
/* 271 */           ((shareNode)o).setParent(this);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 276 */     return kids;
/*     */   }
/*     */   
/*     */ 
/*     */   protected void deleteInternal()
/*     */   {
/* 282 */     for (int i = 0; i < this.children.length; i++) {
/*     */       try
/*     */       {
/* 285 */         if ((this.children[i] instanceof ShareResourceImpl))
/*     */         {
/* 287 */           ((ShareResourceImpl)this.children[i]).delete(true);
/*     */         }
/*     */         else {
/* 290 */           ((shareNode)this.children[i]).delete(true);
/*     */         }
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 295 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected void serialiseResource(Map map)
/*     */   {
/* 304 */     super.serialiseResource(map);
/*     */     
/* 306 */     map.put("type", new Long(getType()));
/*     */     
/* 308 */     map.put("recursive", new Long(this.recursive ? 1L : 0L));
/*     */     try
/*     */     {
/* 311 */       map.put("file", this.root.toString().getBytes("UTF8"));
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/* 315 */       Debug.printStackTrace(e);
/*     */     }
/*     */     
/* 318 */     if (this.personal_key != null)
/*     */     {
/* 320 */       map.put("per_key", this.personal_key);
/*     */     }
/*     */     
/* 323 */     if (this.properties != null)
/*     */     {
/* 325 */       map.put("props", this.properties);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static ShareResourceImpl deserialiseResource(ShareManagerImpl manager, Map map)
/*     */     throws ShareException
/*     */   {
/*     */     try
/*     */     {
/* 337 */       File root = new File(new String((byte[])map.get("file"), "UTF8"));
/*     */       
/* 339 */       boolean recursive = ((Long)map.get("recursive")).longValue() == 1L;
/*     */       
/* 341 */       return new ShareResourceDirContentsImpl(manager, root, recursive, map);
/*     */ 
/*     */     }
/*     */     catch (UnsupportedEncodingException e)
/*     */     {
/*     */ 
/* 347 */       throw new ShareException("internal error", e);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/* 354 */     return this.root.toString();
/*     */   }
/*     */   
/*     */ 
/*     */   public File getRoot()
/*     */   {
/* 360 */     return this.root;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean isRecursive()
/*     */   {
/* 366 */     return this.recursive;
/*     */   }
/*     */   
/*     */ 
/*     */   public ShareResource[] getChildren()
/*     */   {
/* 372 */     return this.children;
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, String> getProperties()
/*     */   {
/* 378 */     return this.properties;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected class shareNode
/*     */     implements ShareResourceDirContents
/*     */   {
/*     */     protected ShareResourceDirContents node_parent;
/*     */     
/*     */     protected File node;
/*     */     
/*     */     protected ShareResource[] node_children;
/*     */     
/*     */ 
/*     */     protected shareNode(ShareResourceDirContents _parent, File _node, List kids)
/*     */     {
/* 395 */       this.node_parent = _parent;
/* 396 */       this.node = _node;
/*     */       
/* 398 */       this.node_children = new ShareResource[kids.size()];
/*     */       
/* 400 */       kids.toArray(this.node_children);
/*     */       
/* 402 */       for (int i = 0; i < this.node_children.length; i++)
/*     */       {
/* 404 */         Object o = this.node_children[i];
/*     */         
/* 406 */         if ((o instanceof ShareResourceImpl))
/*     */         {
/* 408 */           ((ShareResourceImpl)o).setParent(this);
/*     */         }
/*     */         else {
/* 411 */           ((shareNode)o).setParent(this);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public ShareResourceDirContents getParent()
/*     */     {
/* 420 */       return this.node_parent;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     protected void setParent(ShareResourceDirContents _parent)
/*     */     {
/* 427 */       this.node_parent = _parent;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getType()
/*     */     {
/* 433 */       return 3;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getName()
/*     */     {
/* 439 */       return this.node.toString();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void setAttribute(TorrentAttribute attribute, String value)
/*     */     {
/* 447 */       for (int i = 0; i < this.node_children.length; i++)
/*     */       {
/* 449 */         this.node_children[i].setAttribute(attribute, value);
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String getAttribute(TorrentAttribute attribute)
/*     */     {
/* 457 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public TorrentAttribute[] getAttributes()
/*     */     {
/* 463 */       return new TorrentAttribute[0];
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public void delete()
/*     */       throws ShareResourceDeletionVetoException
/*     */     {
/* 471 */       throw new ShareResourceDeletionVetoException(MessageText.getString("plugin.sharing.remove.veto"));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public void delete(boolean force)
/*     */       throws ShareException, ShareResourceDeletionVetoException
/*     */     {
/* 480 */       for (int i = 0; i < this.node_children.length; i++)
/*     */       {
/* 482 */         Object o = this.node_children[i];
/*     */         
/* 484 */         if ((o instanceof ShareResourceImpl))
/*     */         {
/* 486 */           ((ShareResourceImpl)o).delete(force);
/*     */         }
/*     */         else {
/* 489 */           ((shareNode)o).delete(force);
/*     */         }
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean canBeDeleted()
/*     */       throws ShareResourceDeletionVetoException
/*     */     {
/* 500 */       for (int i = 0; i < this.node_children.length; i++)
/*     */       {
/* 502 */         this.node_children[i].canBeDeleted();
/*     */       }
/*     */       
/* 505 */       return true;
/*     */     }
/*     */     
/*     */ 
/*     */     public File getRoot()
/*     */     {
/* 511 */       return this.node;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isRecursive()
/*     */     {
/* 517 */       return ShareResourceDirContentsImpl.this.recursive;
/*     */     }
/*     */     
/*     */ 
/*     */     public ShareResource[] getChildren()
/*     */     {
/* 523 */       return this.node_children;
/*     */     }
/*     */     
/*     */ 
/*     */     public Map<String, String> getProperties()
/*     */     {
/* 529 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isPersistent()
/*     */     {
/* 535 */       return false;
/*     */     }
/*     */     
/*     */     public void addChangeListener(ShareResourceListener l) {}
/*     */     
/*     */     public void removeChangeListener(ShareResourceListener l) {}
/*     */     
/*     */     public void addDeletionListener(ShareResourceWillBeDeletedListener l) {}
/*     */     
/*     */     public void removeDeletionListener(ShareResourceWillBeDeletedListener l) {}
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/pluginsimpl/local/sharing/ShareResourceDirContentsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */