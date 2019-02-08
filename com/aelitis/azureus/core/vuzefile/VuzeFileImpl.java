/*     */ package com.aelitis.azureus.core.vuzefile;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.BEncoder;
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
/*     */ public class VuzeFileImpl
/*     */   implements VuzeFile
/*     */ {
/*     */   private final VuzeFileHandler handler;
/*     */   private VuzeFileComponent[] components;
/*     */   
/*     */   protected VuzeFileImpl(VuzeFileHandler _handler)
/*     */   {
/*  39 */     this.handler = _handler;
/*     */     
/*  41 */     this.components = new VuzeFileComponent[0];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   protected VuzeFileImpl(VuzeFileHandler _handler, Map map)
/*     */   {
/*  49 */     this.handler = _handler;
/*     */     
/*  51 */     List l_comps = (List)map.get("components");
/*     */     
/*  53 */     this.components = new VuzeFileComponent[l_comps.size()];
/*     */     
/*  55 */     for (int i = 0; i < l_comps.size(); i++)
/*     */     {
/*  57 */       Map comp = (Map)l_comps.get(i);
/*     */       
/*  59 */       int type = ((Long)comp.get("type")).intValue();
/*  60 */       Map content = (Map)comp.get("content");
/*     */       
/*  62 */       this.components[i] = new comp(type, content);
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public String getName()
/*     */   {
/*  69 */     String str = "";
/*     */     
/*  71 */     for (VuzeFileComponent comp : this.components)
/*     */     {
/*  73 */       str = str + (str.length() == 0 ? "" : ",") + comp.getTypeName();
/*     */     }
/*     */     
/*  76 */     return str;
/*     */   }
/*     */   
/*     */ 
/*     */   public VuzeFileComponent[] getComponents()
/*     */   {
/*  82 */     return this.components;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public VuzeFileComponent addComponent(int type, Map content)
/*     */   {
/*  90 */     VuzeFileComponent comp = new comp(type, content);
/*     */     
/*  92 */     int old_len = this.components.length;
/*     */     
/*  94 */     VuzeFileComponent[] res = new VuzeFileComponent[old_len + 1];
/*     */     
/*  96 */     System.arraycopy(this.components, 0, res, 0, old_len);
/*     */     
/*  98 */     res[old_len] = comp;
/*     */     
/* 100 */     this.components = res;
/*     */     
/* 102 */     return comp;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public Map exportToMap()
/*     */     throws IOException
/*     */   {
/* 110 */     Map map = new HashMap();
/*     */     
/* 112 */     Map vuze_map = new HashMap();
/*     */     
/* 114 */     map.put("vuze", vuze_map);
/*     */     
/* 116 */     List list = new ArrayList();
/*     */     
/* 118 */     vuze_map.put("components", list);
/*     */     
/* 120 */     for (int i = 0; i < this.components.length; i++)
/*     */     {
/* 122 */       VuzeFileComponent comp = this.components[i];
/*     */       
/* 124 */       Map entry = new HashMap();
/*     */       
/* 126 */       entry.put("type", new Long(comp.getType()));
/*     */       
/* 128 */       entry.put("content", comp.getContent());
/*     */       
/* 130 */       list.add(entry);
/*     */     }
/*     */     
/* 133 */     return map;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public byte[] exportToBytes()
/*     */     throws IOException
/*     */   {
/* 141 */     return BEncoder.encode(exportToMap());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String exportToJSON()
/*     */     throws IOException
/*     */   {
/* 149 */     return BEncoder.encodeToJSON(exportToMap());
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void write(File target)
/*     */     throws IOException
/*     */   {
/* 158 */     FileOutputStream fos = new FileOutputStream(target);
/*     */     try
/*     */     {
/* 161 */       fos.write(exportToBytes());
/*     */     }
/*     */     finally
/*     */     {
/* 165 */       fos.close();
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   protected static class comp
/*     */     implements VuzeFileComponent
/*     */   {
/*     */     private final int type;
/*     */     
/*     */     private final Map contents;
/*     */     
/*     */     private boolean processed;
/*     */     
/*     */     private Map user_data;
/*     */     
/*     */ 
/*     */     protected comp(int _type, Map _contents)
/*     */     {
/* 184 */       this.type = _type;
/* 185 */       this.contents = _contents;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getType()
/*     */     {
/* 191 */       return this.type;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getTypeName()
/*     */     {
/* 197 */       switch (this.type) {
/*     */       case 0: 
/* 199 */         return "None";
/*     */       case 1: 
/* 201 */         return "Search Template";
/*     */       case 2: 
/* 203 */         return "Navigation";
/*     */       case 4: 
/* 205 */         return "Condition Check";
/*     */       case 8: 
/* 207 */         return "Plugin";
/*     */       case 16: 
/* 209 */         return "Subscription";
/*     */       case 32: 
/* 211 */         return "Subscription";
/*     */       case 64: 
/* 213 */         return "Customization";
/*     */       case 128: 
/* 215 */         return "Content Network";
/*     */       case 256: 
/* 217 */         return "Search Operation";
/*     */       case 512: 
/* 219 */         return "Device";
/*     */       case 1024: 
/* 221 */         return "Config Settings";
/*     */       }
/* 223 */       return "Unknown";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public Map getContent()
/*     */     {
/* 230 */       return this.contents;
/*     */     }
/*     */     
/*     */ 
/*     */     public void setProcessed()
/*     */     {
/* 236 */       this.processed = true;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isProcessed()
/*     */     {
/* 242 */       return this.processed;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */     public synchronized void setData(Object key, Object value)
/*     */     {
/* 250 */       if (this.user_data == null)
/*     */       {
/* 252 */         this.user_data = new HashMap();
/*     */       }
/*     */       
/* 255 */       this.user_data.put(key, value);
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public synchronized Object getData(Object key)
/*     */     {
/* 262 */       if (this.user_data == null)
/*     */       {
/* 264 */         return null;
/*     */       }
/*     */       
/* 267 */       return this.user_data.get(key);
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/vuzefile/VuzeFileImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */