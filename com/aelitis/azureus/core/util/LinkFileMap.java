/*     */ package com.aelitis.azureus.core.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LinkFileMap
/*     */ {
/*     */   private final Map<wrapper, Entry> name_map;
/*     */   private final Map<Integer, Entry> index_map;
/*     */   
/*     */   public LinkFileMap()
/*     */   {
/*  49 */     this.name_map = new HashMap();
/*  50 */     this.index_map = new HashMap();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public File get(int index, File from_file)
/*     */   {
/*  57 */     if (index >= 0)
/*     */     {
/*  59 */       Entry entry = (Entry)this.index_map.get(Integer.valueOf(index));
/*     */       
/*  61 */       if (entry != null)
/*     */       {
/*  63 */         return entry.getToFile();
/*     */       }
/*     */     }
/*     */     else {
/*  67 */       Debug.out("unexpected index");
/*     */     }
/*     */     
/*  70 */     Entry entry = (Entry)this.name_map.get(new wrapper(from_file));
/*     */     
/*  72 */     if (entry == null)
/*     */     {
/*  74 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*  80 */     int e_index = entry.getIndex();
/*     */     
/*  82 */     if ((e_index >= 0) && (e_index != index))
/*     */     {
/*  84 */       return null;
/*     */     }
/*     */     
/*  87 */     return entry.getToFile();
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Entry getEntry(int index, File from_file)
/*     */   {
/*  96 */     if (index >= 0)
/*     */     {
/*  98 */       Entry entry = (Entry)this.index_map.get(Integer.valueOf(index));
/*     */       
/* 100 */       if (entry != null)
/*     */       {
/* 102 */         return entry;
/*     */       }
/*     */     }
/*     */     else {
/* 106 */       Debug.out("unexpected index");
/*     */     }
/*     */     
/* 109 */     Entry entry = (Entry)this.name_map.get(new wrapper(from_file));
/*     */     
/* 111 */     if (entry == null)
/*     */     {
/* 113 */       return null;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/* 119 */     int e_index = entry.getIndex();
/*     */     
/* 121 */     if ((e_index >= 0) && (e_index != index))
/*     */     {
/* 123 */       return null;
/*     */     }
/*     */     
/* 126 */     return entry;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public void put(int index, File from_file, File to_file)
/*     */   {
/* 136 */     Entry entry = new Entry(index, from_file, to_file, null);
/*     */     
/* 138 */     if (index >= 0)
/*     */     {
/* 140 */       this.index_map.put(Integer.valueOf(index), entry);
/*     */       
/*     */ 
/*     */ 
/* 144 */       if (this.name_map.size() > 0)
/*     */       {
/* 146 */         this.name_map.remove(new wrapper(from_file));
/*     */       }
/*     */     }
/*     */     else {
/* 150 */       wrapper wrap = new wrapper(from_file);
/*     */       
/* 152 */       Entry existing = (Entry)this.name_map.get(wrap);
/*     */       
/* 154 */       if ((existing == null) || (!existing.getFromFile().equals(from_file)) || (!existing.getToFile().equals(to_file)))
/*     */       {
/*     */ 
/*     */ 
/* 158 */         Debug.out("unexpected index");
/*     */       }
/*     */       
/* 161 */       this.name_map.put(wrap, entry);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void putMigration(File from_file, File to_file)
/*     */   {
/* 170 */     Entry entry = new Entry(-1, from_file, to_file, null);
/*     */     
/* 172 */     this.name_map.put(new wrapper(from_file), entry);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void remove(int index, File key)
/*     */   {
/* 180 */     if (index >= 0)
/*     */     {
/* 182 */       this.index_map.remove(Integer.valueOf(index));
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 189 */     if (this.name_map.size() > 0)
/*     */     {
/* 191 */       this.name_map.remove(new wrapper(key));
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean hasLinks()
/*     */   {
/* 198 */     for (Entry entry : this.index_map.values())
/*     */     {
/* 200 */       File to_file = entry.getToFile();
/*     */       
/* 202 */       if (to_file != null)
/*     */       {
/* 204 */         if (!entry.getFromFile().equals(to_file))
/*     */         {
/* 206 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 211 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */   public int size()
/*     */   {
/* 217 */     int size = 0;
/*     */     
/* 219 */     for (Entry entry : this.index_map.values())
/*     */     {
/* 221 */       File to_file = entry.getToFile();
/*     */       
/* 223 */       if (to_file != null)
/*     */       {
/* 225 */         if (!entry.getFromFile().equals(to_file))
/*     */         {
/* 227 */           size++;
/*     */         }
/*     */       }
/*     */     }
/*     */     
/* 232 */     return size;
/*     */   }
/*     */   
/*     */ 
/*     */   public Iterator<Entry> entryIterator()
/*     */   {
/* 238 */     if (this.index_map.size() > 0)
/*     */     {
/* 240 */       if (this.name_map.size() == 0)
/*     */       {
/* 242 */         return this.index_map.values().iterator();
/*     */       }
/*     */       
/* 245 */       Set<Entry> entries = new HashSet(this.index_map.values());
/*     */       
/* 247 */       entries.addAll(this.name_map.values());
/*     */       
/* 249 */       return entries.iterator();
/*     */     }
/*     */     
/* 252 */     return this.name_map.values().iterator();
/*     */   }
/*     */   
/*     */ 
/*     */   public String getString()
/*     */   {
/* 258 */     String str = "";
/*     */     
/* 260 */     if (this.index_map.size() > 0)
/*     */     {
/* 262 */       String i_str = "";
/*     */       
/* 264 */       for (Entry e : this.index_map.values())
/*     */       {
/* 266 */         i_str = i_str + (i_str.length() == 0 ? "" : ", ") + e.getString();
/*     */       }
/*     */       
/* 269 */       str = str + "i_map={ " + i_str + " }";
/*     */     }
/*     */     
/* 272 */     if (this.name_map.size() > 0)
/*     */     {
/* 274 */       String n_str = "";
/*     */       
/* 276 */       for (Entry e : this.name_map.values())
/*     */       {
/* 278 */         n_str = n_str + (n_str.length() == 0 ? "" : ", ") + e.getString();
/*     */       }
/*     */       
/* 281 */       str = str + "n_map={ " + n_str + " }";
/*     */     }
/*     */     
/* 284 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class Entry
/*     */   {
/*     */     private final int index;
/*     */     
/*     */     private final File from_file;
/*     */     
/*     */     private final File to_file;
/*     */     
/*     */ 
/*     */     private Entry(int _index, File _from_file, File _to_file)
/*     */     {
/* 300 */       this.index = _index;
/* 301 */       this.from_file = _from_file;
/* 302 */       this.to_file = _to_file;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getIndex()
/*     */     {
/* 308 */       return this.index;
/*     */     }
/*     */     
/*     */ 
/*     */     public File getFromFile()
/*     */     {
/* 314 */       return this.from_file;
/*     */     }
/*     */     
/*     */ 
/*     */     public File getToFile()
/*     */     {
/* 320 */       return this.to_file;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getString()
/*     */     {
/* 326 */       return this.index + ": " + this.from_file + " -> " + this.to_file;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   private static class wrapper
/*     */   {
/*     */     private final String file_str;
/*     */     
/*     */ 
/*     */     protected wrapper(File file)
/*     */     {
/* 339 */       this.file_str = file.toString();
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public boolean equals(Object other)
/*     */     {
/* 347 */       if ((other instanceof wrapper))
/*     */       {
/* 349 */         return this.file_str.equals(((wrapper)other).file_str);
/*     */       }
/*     */       
/* 352 */       return false;
/*     */     }
/*     */     
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 358 */       return this.file_str.hashCode();
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/util/LinkFileMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */