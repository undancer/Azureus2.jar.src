/*     */ package com.aelitis.azureus.core.messenger.config;
/*     */ 
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessenger;
/*     */ import com.aelitis.azureus.core.messenger.PlatformMessengerException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class PlatformMetaSearchMessenger
/*     */ {
/*     */   private static final int MAX_TEMPLATE_LIST = 512;
/*  35 */   private static final PlatformMessengerConfig dispatcher = new PlatformMessengerConfig("searchtemplate", true);
/*     */   
/*     */ 
/*     */   private static final String OP_GET_TEMPLATE = "get-template";
/*     */   
/*     */   private static final String OP_GET_TEMPLATES = "get-templates";
/*     */   
/*     */   private static final String OP_LIST_POPULAR_TEMPLATES = "list-popular";
/*     */   
/*     */   private static final String OP_LIST_FEATURED_TEMPLATES = "list-featured";
/*     */   
/*     */   private static final String OP_TEMPLATE_SELECTED = "template-selected";
/*     */   
/*     */ 
/*     */   public static templateDetails getTemplate(String extension_key, long template_id)
/*     */     throws PlatformMessengerException
/*     */   {
/*  52 */     Map parameters = getParameter(template_id);
/*     */     
/*  54 */     if (extension_key != null)
/*     */     {
/*  56 */       parameters.put("extension_key", extension_key);
/*     */     }
/*     */     
/*  59 */     Map reply = dispatcher.syncInvoke("get-template", parameters);
/*     */     
/*  61 */     templateInfo info = getTemplateInfo(reply);
/*     */     
/*  63 */     if (info == null)
/*     */     {
/*  65 */       throw new PlatformMessengerException("Invalid reply: " + reply);
/*     */     }
/*     */     
/*  68 */     String name = (String)reply.get("name");
/*  69 */     String value = (String)reply.get("value");
/*  70 */     String engine_type = (String)reply.get("engine_id");
/*     */     
/*  72 */     if ((name == null) || (value == null) || (engine_type == null))
/*     */     {
/*  74 */       throw new PlatformMessengerException("Invalid reply; field missing: " + reply);
/*     */     }
/*     */     
/*     */     int type;
/*     */     
/*  79 */     if (engine_type.equals("json"))
/*     */     {
/*  81 */       type = 1;
/*     */     } else { int type;
/*  83 */       if (engine_type.equals("regexp"))
/*     */       {
/*  85 */         type = 2;
/*     */       }
/*     */       else
/*     */       {
/*  89 */         throw new PlatformMessengerException("Invalid type '" + engine_type + ": " + reply);
/*     */       }
/*     */     }
/*     */     int type;
/*  93 */     return new templateDetails(info, type, name, value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static templateInfo[] getTemplateDetails(String extension_key, long[] ids)
/*     */     throws PlatformMessengerException
/*     */   {
/* 103 */     if (ids.length == 0)
/*     */     {
/* 105 */       return new templateInfo[0];
/*     */     }
/*     */     
/* 108 */     String str = "";
/*     */     
/* 110 */     for (int i = 0; i < ids.length; i++)
/*     */     {
/* 112 */       str = str + (i == 0 ? "" : ",") + ids[i];
/*     */     }
/*     */     
/* 115 */     Map parameters = new HashMap();
/*     */     
/* 117 */     if (extension_key != null)
/*     */     {
/* 119 */       parameters.put("extension_key", extension_key);
/*     */     }
/*     */     
/* 122 */     parameters.put("templateIds", str);
/*     */     
/* 124 */     Map reply = dispatcher.syncInvoke("get-templates", parameters);
/*     */     
/* 126 */     return getTemplatesInfo(reply);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static templateInfo[] listTopPopularTemplates(String extension_key, String fud)
/*     */     throws PlatformMessengerException
/*     */   {
/* 136 */     Map parameters = new HashMap();
/*     */     
/* 138 */     if (extension_key != null)
/*     */     {
/* 140 */       parameters.put("extension_key", extension_key);
/*     */     }
/*     */     
/* 143 */     parameters.put("fud", fud);
/*     */     
/* 145 */     Map reply = dispatcher.syncInvoke("list-popular", parameters);
/*     */     
/* 147 */     return getTemplatesInfo(reply);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static templateInfo[] listAllPopularTemplates(String extension_key, String fud)
/*     */     throws PlatformMessengerException
/*     */   {
/* 157 */     Map parameters = new HashMap();
/*     */     
/* 159 */     if (extension_key != null)
/*     */     {
/* 161 */       parameters.put("extension_key", extension_key);
/*     */     }
/*     */     
/* 164 */     parameters.put("fud", fud);
/*     */     
/* 166 */     parameters.put("page-num", new Long(1L));
/* 167 */     parameters.put("items-per-page", new Long(512L));
/*     */     
/* 169 */     Map reply = dispatcher.syncInvoke("list-popular", parameters);
/*     */     
/* 171 */     return getTemplatesInfo(reply);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static templateInfo[] listFeaturedTemplates(String extension_key, String fud)
/*     */     throws PlatformMessengerException
/*     */   {
/* 181 */     Map parameters = new HashMap();
/*     */     
/* 183 */     if (extension_key != null)
/*     */     {
/* 185 */       parameters.put("extension_key", extension_key);
/*     */     }
/*     */     
/* 188 */     parameters.put("fud", fud);
/*     */     
/* 190 */     parameters.put("page-num", new Long(1L));
/* 191 */     parameters.put("items-per-page", new Long(512L));
/*     */     
/* 193 */     Map reply = dispatcher.syncInvoke("list-featured", parameters);
/*     */     
/* 195 */     return getTemplatesInfo(reply);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static templateInfo[] getTemplatesInfo(Map reply)
/*     */   {
/* 202 */     List templates = (List)reply.get("templates");
/*     */     
/* 204 */     List res = new ArrayList();
/*     */     
/* 206 */     for (int i = 0; i < templates.size(); i++)
/*     */     {
/* 208 */       Map m = (Map)templates.get(i);
/*     */       
/* 210 */       templateInfo info = getTemplateInfo(m);
/*     */       
/* 212 */       if (info != null)
/*     */       {
/* 214 */         res.add(info);
/*     */       }
/*     */     }
/*     */     
/* 218 */     templateInfo[] res_a = new templateInfo[res.size()];
/*     */     
/* 220 */     res.toArray(res_a);
/*     */     
/* 222 */     return res_a;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static templateInfo getTemplateInfo(Map m)
/*     */   {
/* 229 */     Long id = (Long)m.get("id");
/* 230 */     Boolean show = (Boolean)m.get("show");
/* 231 */     Long date = (Long)m.get("modified_dt");
/*     */     
/* 233 */     float rank_bias = 1.0F;
/*     */     try
/*     */     {
/* 236 */       String str = (String)m.get("rank_bias");
/*     */       
/* 238 */       if (str != null)
/*     */       {
/* 240 */         rank_bias = Float.parseFloat(str);
/*     */       }
/*     */     }
/*     */     catch (Throwable e) {
/* 244 */       Debug.out(e);
/*     */     }
/*     */     
/* 247 */     if (show == null)
/*     */     {
/* 249 */       show = Boolean.TRUE;
/*     */     }
/*     */     
/* 252 */     if ((id == null) || (show == null) || (date == null))
/*     */     {
/* 254 */       PlatformMessenger.debug("field missing from template info (" + m + ")");
/*     */     }
/*     */     else
/*     */     {
/* 258 */       return new templateInfo(id.longValue(), date.longValue(), show.booleanValue(), rank_bias);
/*     */     }
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
/* 278 */     return null;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void setTemplatetSelected(String extension_key, long template_id, String user_id, boolean is_selected)
/*     */     throws PlatformMessengerException
/*     */   {
/* 290 */     Map parameters = getParameter(template_id);
/*     */     
/* 292 */     if (extension_key != null)
/*     */     {
/* 294 */       parameters.put("extension_key", extension_key);
/*     */     }
/*     */     
/* 297 */     parameters.put("userId", user_id);
/* 298 */     parameters.put("selected", Boolean.valueOf(is_selected));
/*     */     
/* 300 */     dispatcher.syncInvoke("template-selected", parameters);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   protected static Map getParameter(long template_id)
/*     */   {
/* 307 */     Map parameters = new HashMap();
/*     */     
/* 309 */     parameters.put("templateId", new Long(template_id));
/*     */     
/* 311 */     return parameters;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class templateInfo
/*     */   {
/*     */     private long id;
/*     */     
/*     */     private long date;
/*     */     
/*     */     private boolean visible;
/*     */     
/*     */     private float rank_bias;
/*     */     
/*     */ 
/*     */     protected templateInfo(long _id, long _date, boolean _visible, float _rank_bias)
/*     */     {
/* 329 */       this.id = _id;
/* 330 */       this.date = _date;
/* 331 */       this.visible = _visible;
/* 332 */       this.rank_bias = _rank_bias;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getId()
/*     */     {
/* 338 */       return this.id;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getModifiedDate()
/*     */     {
/* 344 */       return this.date;
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isVisible()
/*     */     {
/* 350 */       return this.visible;
/*     */     }
/*     */     
/*     */ 
/*     */     public float getRankBias()
/*     */     {
/* 356 */       return this.rank_bias;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static class templateDetails
/*     */   {
/*     */     public static final int ENGINE_TYPE_JSON = 1;
/*     */     
/*     */     public static final int ENGINE_TYPE_REGEXP = 2;
/*     */     
/*     */     private PlatformMetaSearchMessenger.templateInfo info;
/*     */     
/*     */     private int type;
/*     */     
/*     */     private String name;
/*     */     
/*     */     private String value;
/*     */     
/*     */ 
/*     */     protected templateDetails(PlatformMetaSearchMessenger.templateInfo _info, int _type, String _name, String _value)
/*     */     {
/* 379 */       this.info = _info;
/* 380 */       this.type = _type;
/* 381 */       this.name = _name;
/* 382 */       this.value = _value;
/*     */     }
/*     */     
/*     */ 
/*     */     public int getType()
/*     */     {
/* 388 */       return this.type;
/*     */     }
/*     */     
/*     */ 
/*     */     public long getId()
/*     */     {
/* 394 */       return this.info.getId();
/*     */     }
/*     */     
/*     */ 
/*     */     public long getModifiedDate()
/*     */     {
/* 400 */       return this.info.getModifiedDate();
/*     */     }
/*     */     
/*     */ 
/*     */     public float getRankBias()
/*     */     {
/* 406 */       return this.info.getRankBias();
/*     */     }
/*     */     
/*     */ 
/*     */     public boolean isVisible()
/*     */     {
/* 412 */       return this.info.isVisible();
/*     */     }
/*     */     
/*     */ 
/*     */     public String getName()
/*     */     {
/* 418 */       return this.name;
/*     */     }
/*     */     
/*     */ 
/*     */     public String getValue()
/*     */     {
/* 424 */       return this.value;
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/messenger/config/PlatformMetaSearchMessenger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */