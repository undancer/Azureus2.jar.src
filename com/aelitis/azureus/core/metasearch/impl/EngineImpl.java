/*      */ package com.aelitis.azureus.core.metasearch.impl;
/*      */ 
/*      */ import com.aelitis.azureus.core.messenger.config.PlatformMetaSearchMessenger;
/*      */ import com.aelitis.azureus.core.metasearch.Engine;
/*      */ import com.aelitis.azureus.core.metasearch.Result;
/*      */ import com.aelitis.azureus.core.metasearch.ResultListener;
/*      */ import com.aelitis.azureus.core.metasearch.SearchException;
/*      */ import com.aelitis.azureus.core.metasearch.SearchLoginException;
/*      */ import com.aelitis.azureus.core.metasearch.SearchParameter;
/*      */ import com.aelitis.azureus.core.metasearch.impl.plugin.PluginEngine;
/*      */ import com.aelitis.azureus.core.metasearch.impl.web.json.JSONEngine;
/*      */ import com.aelitis.azureus.core.metasearch.impl.web.regex.RegexEngine;
/*      */ import com.aelitis.azureus.core.metasearch.impl.web.rss.RSSEngine;
/*      */ import com.aelitis.azureus.core.subs.Subscription;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionManager;
/*      */ import com.aelitis.azureus.core.subs.SubscriptionManagerFactory;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*      */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*      */ import com.aelitis.azureus.util.ConstantsVuze;
/*      */ import com.aelitis.azureus.util.ImportExportUtils;
/*      */ import com.aelitis.azureus.util.JSONUtils;
/*      */ import java.io.File;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintWriter;
/*      */ import java.net.URL;
/*      */ import java.net.URLDecoder;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*      */ import org.gudy.azureus2.core3.config.ParameterListener;
/*      */ import org.gudy.azureus2.core3.internat.MessageText;
/*      */ import org.gudy.azureus2.core3.util.AEDiagnostics;
/*      */ import org.gudy.azureus2.core3.util.BEncoder;
/*      */ import org.gudy.azureus2.core3.util.Base32;
/*      */ import org.gudy.azureus2.core3.util.Debug;
/*      */ import org.gudy.azureus2.core3.util.RandomUtils;
/*      */ import org.gudy.azureus2.core3.util.UrlUtils;
/*      */ import org.json.simple.JSONArray;
/*      */ import org.json.simple.JSONObject;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class EngineImpl
/*      */   implements Engine
/*      */ {
/*      */   private static final int DEFAULT_UPDATE_CHECK_SECS = 86400;
/*      */   private static boolean logging_enabled;
/*      */   protected static final String LD_COOKIES = "cookies";
/*      */   protected static final String LD_ETAG = "etag";
/*      */   protected static final String LD_LAST_MODIFIED = "last_mod";
/*      */   protected static final String LD_LAST_UPDATE_CHECK = "last_update_check";
/*      */   protected static final String LD_UPDATE_CHECK_SECS = "update_check_secs";
/*      */   protected static final String LD_CREATED_BY_ME = "mine";
/*      */   protected static final String LD_AUTO_DL_SUPPORTED = "auto_dl_supported";
/*      */   protected static final String LD_LINK_IS_TORRENT = "link_is_torrent";
/*      */   private MetaSearchImpl meta_search;
/*      */   private int type;
/*      */   private long id;
/*      */   private long last_updated;
/*      */   private String name;
/*      */   private byte[] uid;
/*      */   private int version;
/*      */   
/*      */   static
/*      */   {
/*   75 */     COConfigurationManager.addAndFireParameterListeners(new String[] { "Logger.Enabled" }, new ParameterListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */       public void parameterChanged(String parameterName)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*   85 */         EngineImpl.access$002(COConfigurationManager.getBooleanParameter("Logger.Enabled"));
/*      */       }
/*      */     });
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected static EngineImpl importFromBEncodedMap(MetaSearchImpl meta_search, Map map)
/*      */     throws IOException
/*      */   {
/*   97 */     int type = ((Long)map.get("type")).intValue();
/*      */     
/*   99 */     if (type == 2)
/*      */     {
/*  101 */       return JSONEngine.importFromBEncodedMap(meta_search, map);
/*      */     }
/*  103 */     if (type == 1)
/*      */     {
/*  105 */       return RegexEngine.importFromBEncodedMap(meta_search, map);
/*      */     }
/*  107 */     if (type == 3)
/*      */     {
/*  109 */       return PluginEngine.importFromBEncodedMap(meta_search, map);
/*      */     }
/*  111 */     if (type == 4)
/*      */     {
/*  113 */       return RSSEngine.importFromBEncodedMap(meta_search, map);
/*      */     }
/*      */     
/*      */ 
/*  117 */     throw new IOException("Unknown engine type " + type);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public static Engine importFromJSONString(MetaSearchImpl meta_search, int type, long id, long last_updated, float rank_bias, String name, String content)
/*      */     throws IOException
/*      */   {
/*  133 */     JSONObject map = (JSONObject)JSONUtils.decodeJSON(content);
/*      */     
/*  135 */     if (type == 2)
/*      */     {
/*  137 */       return JSONEngine.importFromJSONString(meta_search, id, last_updated, rank_bias, name, map);
/*      */     }
/*  139 */     if (type == 1)
/*      */     {
/*  141 */       return RegexEngine.importFromJSONString(meta_search, id, last_updated, rank_bias, name, map);
/*      */     }
/*  143 */     if (type == 4)
/*      */     {
/*  145 */       return RSSEngine.importFromJSONString(meta_search, id, last_updated, rank_bias, name, map);
/*      */     }
/*      */     
/*      */ 
/*  149 */     throw new IOException("Unknown engine type " + type);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  178 */   private boolean is_public = true;
/*      */   
/*      */   private int az_version;
/*      */   
/*  182 */   private int selection_state = 0;
/*  183 */   private boolean selection_state_recorded = true;
/*      */   
/*  185 */   private int source = 0;
/*      */   
/*  187 */   private float rank_bias = 1.0F;
/*  188 */   private float preferred_count = 0.0F;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  194 */   private List first_level_mapping = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*  199 */   private List second_level_mapping = new ArrayList();
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   private String update_url;
/*      */   
/*      */ 
/*      */ 
/*      */   private int update_check_default_secs;
/*      */   
/*      */ 
/*      */ 
/*      */   private Map user_data;
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected EngineImpl(MetaSearchImpl _meta_search, int _type, long _id, long _last_updated, float _rank_bias, String _name)
/*      */   {
/*  219 */     this.meta_search = _meta_search;
/*  220 */     this.type = _type;
/*  221 */     this.id = _id;
/*  222 */     this.last_updated = _last_updated;
/*  223 */     this.rank_bias = _rank_bias;
/*  224 */     this.name = _name;
/*      */     
/*  226 */     this.version = 1;
/*  227 */     this.az_version = 5;
/*      */     
/*  229 */     allocateUID(this.id);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected EngineImpl(MetaSearchImpl _meta_search, Map map)
/*      */     throws IOException
/*      */   {
/*  241 */     this.meta_search = _meta_search;
/*      */     
/*  243 */     this.type = ((Long)map.get("type")).intValue();
/*      */     
/*  245 */     Long l_id = (Long)map.get("id");
/*      */     
/*  247 */     this.id = (l_id == null ? this.meta_search.getManager().getLocalTemplateID() : l_id.longValue());
/*  248 */     this.last_updated = ImportExportUtils.importLong(map, "last_updated");
/*  249 */     this.name = ImportExportUtils.importString(map, "name");
/*      */     
/*  251 */     this.selection_state = ((int)ImportExportUtils.importLong(map, "selected", 0L));
/*      */     
/*  253 */     this.selection_state_recorded = ImportExportUtils.importBoolean(map, "select_rec", true);
/*      */     
/*  255 */     this.source = ((int)ImportExportUtils.importLong(map, "source", 0L));
/*      */     
/*  257 */     this.rank_bias = ImportExportUtils.importFloat(map, "rank_bias", 1.0F);
/*  258 */     this.preferred_count = ImportExportUtils.importFloat(map, "pref_count", 0.0F);
/*      */     
/*  260 */     this.first_level_mapping = importBEncodedMappings(map, "l1_map");
/*  261 */     this.second_level_mapping = importBEncodedMappings(map, "l2_map");
/*      */     
/*  263 */     this.version = ((int)ImportExportUtils.importLong(map, "version", 1L));
/*      */     
/*  265 */     this.az_version = ((int)ImportExportUtils.importLong(map, "az_version", 5L));
/*      */     
/*  267 */     if (this.az_version > 5)
/*      */     {
/*  269 */       throw new IOException(MessageText.getString("metasearch.template.version.bad", new String[] { this.name }));
/*      */     }
/*      */     
/*  272 */     this.uid = ((byte[])map.get("uid"));
/*      */     
/*  274 */     if (this.uid == null)
/*      */     {
/*  276 */       allocateUID(this.id);
/*      */     }
/*      */     
/*  279 */     this.update_url = ImportExportUtils.importString(map, "update_url");
/*  280 */     this.update_check_default_secs = ((int)ImportExportUtils.importLong(map, "update_url_check_secs", 86400L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void exportToBencodedMap(Map map, boolean generic)
/*      */     throws IOException
/*      */   {
/*  292 */     map.put("type", new Long(this.type));
/*      */     
/*  294 */     ImportExportUtils.exportString(map, "name", this.name);
/*      */     
/*  296 */     map.put("source", new Long(this.source));
/*      */     
/*  298 */     exportBEncodedMappings(map, "l1_map", this.first_level_mapping);
/*  299 */     exportBEncodedMappings(map, "l2_map", this.second_level_mapping);
/*      */     
/*  301 */     map.put("version", new Long(this.version));
/*  302 */     map.put("az_version", new Long(this.az_version));
/*      */     
/*  304 */     ImportExportUtils.exportFloat(map, "rank_bias", this.rank_bias);
/*      */     
/*  306 */     if (!generic)
/*      */     {
/*  308 */       map.put("id", new Long(this.id));
/*      */       
/*  310 */       map.put("last_updated", new Long(this.last_updated));
/*      */       
/*  312 */       map.put("selected", new Long(this.selection_state));
/*      */       
/*  314 */       ImportExportUtils.exportBoolean(map, "select_rec", this.selection_state_recorded);
/*      */       
/*  316 */       ImportExportUtils.exportFloat(map, "pref_count", this.preferred_count);
/*      */       
/*  318 */       map.put("uid", this.uid);
/*      */     }
/*      */     
/*  321 */     if (this.update_url != null)
/*      */     {
/*  323 */       ImportExportUtils.exportString(map, "update_url", this.update_url);
/*      */     }
/*      */     
/*  326 */     if (this.update_check_default_secs != 86400) {
/*  327 */       map.put("update_url_check_secs", new Long(this.update_check_default_secs));
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected EngineImpl(MetaSearchImpl meta_search, int type, long id, long last_updated, float rank_bias, String name, JSONObject map)
/*      */     throws IOException
/*      */   {
/*  345 */     this(meta_search, type, id, last_updated, rank_bias, name);
/*      */     
/*  347 */     this.first_level_mapping = importJSONMappings(map, "value_map", true);
/*  348 */     this.second_level_mapping = importJSONMappings(map, "ctype_map", false);
/*      */     
/*  350 */     this.version = ((int)ImportExportUtils.importLong(map, "version", 1L));
/*  351 */     this.az_version = ((int)ImportExportUtils.importLong(map, "az_version", 5L));
/*      */     
/*  353 */     if (this.az_version > 5)
/*      */     {
/*  355 */       throw new IOException(MessageText.getString("metasearch.template.version.bad", new String[] { name }));
/*      */     }
/*      */     
/*  358 */     String uid_str = (String)map.get("uid");
/*      */     
/*  360 */     if (uid_str == null)
/*      */     {
/*  362 */       allocateUID(id);
/*      */     }
/*      */     else
/*      */     {
/*  366 */       this.uid = Base32.decode(uid_str);
/*      */     }
/*      */     
/*  369 */     this.update_url = ImportExportUtils.importString(map, "update_url");
/*  370 */     this.update_check_default_secs = ((int)ImportExportUtils.importLong(map, "update_url_check_secs", 86400L));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void exportToJSONObject(JSONObject res)
/*      */     throws IOException
/*      */   {
/*  381 */     exportJSONMappings(res, "value_map", this.first_level_mapping, true);
/*  382 */     exportJSONMappings(res, "ctype_map", this.second_level_mapping, false);
/*      */     
/*  384 */     res.put("version", new Long(this.version));
/*  385 */     res.put("az_version", new Long(this.az_version));
/*      */     
/*  387 */     res.put("uid", Base32.encode(this.uid));
/*      */     
/*  389 */     if (this.update_url != null)
/*      */     {
/*  391 */       ImportExportUtils.exportJSONString(res, "update_url", this.update_url);
/*      */     }
/*      */     
/*  394 */     res.put("update_url_check_secs", new Long(this.update_check_default_secs));
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected List importJSONMappings(JSONObject map, String str, boolean level_1)
/*      */     throws IOException
/*      */   {
/*  405 */     List result = new ArrayList();
/*      */     
/*  407 */     JSONObject field_map = (JSONObject)map.get(str);
/*      */     
/*  409 */     if (field_map != null)
/*      */     {
/*  411 */       Iterator it = field_map.entrySet().iterator();
/*      */       
/*  413 */       while (it.hasNext())
/*      */       {
/*  415 */         Map.Entry entry = (Map.Entry)it.next();
/*      */         
/*  417 */         String key = (String)entry.getKey();
/*  418 */         List mappings = (List)entry.getValue();
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  424 */         int from_field = vuzeFieldToID(key);
/*      */         
/*  426 */         if (from_field == -1)
/*      */         {
/*  428 */           log("Unrecognised remapping key '" + key + "'");
/*      */ 
/*      */         }
/*      */         else
/*      */         {
/*      */ 
/*  434 */           int to_field = level_1 ? from_field : 8;
/*      */           
/*  436 */           List frs_l = new ArrayList();
/*      */           
/*  438 */           for (int i = 0; i < mappings.size(); i++)
/*      */           {
/*  440 */             JSONObject mapping = (JSONObject)mappings.get(i);
/*      */             
/*  442 */             String from_str = URLDecoder.decode((String)mapping.get(level_1 ? "from_string" : "cat_string"), "UTF-8");
/*      */             
/*  444 */             if (from_str == null)
/*      */             {
/*  446 */               log("'from' value missing in " + mapping);
/*      */ 
/*      */             }
/*      */             else
/*      */             {
/*  451 */               from_str = URLDecoder.decode(from_str, "UTF-8");
/*      */               
/*  453 */               String to_str = URLDecoder.decode((String)mapping.get(level_1 ? "to_string" : "media_type"), "UTF-8");
/*      */               
/*  455 */               if (to_str == null)
/*      */               {
/*  457 */                 log("'to' value missing in " + mapping);
/*      */ 
/*      */               }
/*      */               else
/*      */               {
/*  462 */                 frs_l.add(new FieldRemapping(from_str, to_str)); }
/*      */             }
/*      */           }
/*  465 */           FieldRemapping[] frs = (FieldRemapping[])frs_l.toArray(new FieldRemapping[frs_l.size()]);
/*      */           
/*  467 */           result.add(new FieldRemapper(from_field, to_field, frs));
/*      */         }
/*      */       }
/*      */     }
/*  471 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void exportJSONMappings(JSONObject res, String str, List l, boolean level_1)
/*      */   {
/*  481 */     JSONObject field_map = new JSONObject();
/*      */     
/*  483 */     res.put(str, field_map);
/*      */     
/*  485 */     for (int i = 0; i < l.size(); i++)
/*      */     {
/*  487 */       FieldRemapper remapper = (FieldRemapper)l.get(i);
/*      */       
/*  489 */       int from_field = remapper.getInField();
/*      */       
/*      */ 
/*  492 */       String from_field_str = vuzeIDToField(from_field);
/*      */       
/*  494 */       JSONArray mappings = new JSONArray();
/*      */       
/*  496 */       field_map.put(from_field_str, mappings);
/*      */       
/*  498 */       FieldRemapping[] frs = remapper.getMappings();
/*      */       
/*  500 */       for (int j = 0; j < frs.length; j++)
/*      */       {
/*  502 */         FieldRemapping fr = frs[j];
/*      */         
/*  504 */         String from_str = UrlUtils.encode(fr.getMatchString());
/*      */         
/*  506 */         String to_str = fr.getReplacement();
/*      */         
/*  508 */         JSONObject map = new JSONObject();
/*      */         
/*  510 */         mappings.add(map);
/*      */         
/*  512 */         map.put(level_1 ? "from_string" : "cat_string", from_str);
/*  513 */         map.put(level_1 ? "to_string" : "media_type", to_str);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected List importBEncodedMappings(Map map, String name)
/*      */     throws IOException
/*      */   {
/*  525 */     List result = new ArrayList();
/*      */     
/*  527 */     List l = (List)map.get(name);
/*      */     
/*  529 */     if (l != null)
/*      */     {
/*  531 */       for (int i = 0; i < l.size(); i++)
/*      */       {
/*  533 */         Map entry = (Map)l.get(i);
/*      */         
/*  535 */         int from_field = ((Long)entry.get("from")).intValue();
/*  536 */         int to_field = ((Long)entry.get("to")).intValue();
/*      */         
/*  538 */         List l2 = (List)entry.get("maps");
/*      */         
/*  540 */         FieldRemapping[] mappings = new FieldRemapping[l2.size()];
/*      */         
/*  542 */         for (int j = 0; j < mappings.length; j++)
/*      */         {
/*  544 */           Map entry2 = (Map)l2.get(j);
/*      */           
/*  546 */           String from_str = ImportExportUtils.importString(entry2, "from");
/*  547 */           String to_str = ImportExportUtils.importString(entry2, "to");
/*      */           
/*  549 */           mappings[j] = new FieldRemapping(from_str, to_str);
/*      */         }
/*      */         
/*  552 */         result.add(new FieldRemapper(from_field, to_field, mappings));
/*      */       }
/*      */     }
/*      */     
/*  556 */     return result;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void exportBEncodedMappings(Map map, String name, List mappings)
/*      */     throws IOException
/*      */   {
/*  567 */     List l = new ArrayList();
/*      */     
/*  569 */     map.put(name, l);
/*      */     
/*  571 */     for (int i = 0; i < mappings.size(); i++)
/*      */     {
/*  573 */       FieldRemapper mapper = (FieldRemapper)mappings.get(i);
/*      */       
/*  575 */       Map m = new HashMap();
/*      */       
/*  577 */       l.add(m);
/*      */       
/*  579 */       m.put("from", new Long(mapper.getInField()));
/*  580 */       m.put("to", new Long(mapper.getOutField()));
/*      */       
/*  582 */       List l2 = new ArrayList();
/*      */       
/*  584 */       m.put("maps", l2);
/*      */       
/*  586 */       FieldRemapping[] frs = mapper.getMappings();
/*      */       
/*  588 */       for (int j = 0; j < frs.length; j++)
/*      */       {
/*  590 */         FieldRemapping fr = frs[j];
/*      */         
/*  592 */         Map m2 = new HashMap();
/*      */         
/*  594 */         l2.add(m2);
/*      */         
/*  596 */         ImportExportUtils.exportString(m2, "from", fr.getMatchString());
/*  597 */         ImportExportUtils.exportString(m2, "to", fr.getReplacement());
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String exportToJSONString()
/*      */     throws IOException
/*      */   {
/*  607 */     JSONObject obj = new JSONObject();
/*      */     
/*  609 */     exportToJSONObject(obj);
/*      */     
/*  611 */     return obj.toString();
/*      */   }
/*      */   
/*      */ 
/*      */   public int getAZVersion()
/*      */   {
/*  617 */     return this.az_version;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getVersion()
/*      */   {
/*  623 */     return this.version;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setVersion(int _v)
/*      */   {
/*  630 */     this.version = _v;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getUID()
/*      */   {
/*  636 */     return Base32.encode(this.uid);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setUID(String str)
/*      */   {
/*  643 */     this.uid = Base32.decode(str);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void allocateUID(long id)
/*      */   {
/*  650 */     this.uid = new byte[10];
/*      */     
/*  652 */     if ((id >= 0L) && (id < 2147483647L))
/*      */     {
/*      */ 
/*      */ 
/*  656 */       this.uid[0] = ((byte)(int)(id >> 24));
/*  657 */       this.uid[1] = ((byte)(int)(id >> 16));
/*  658 */       this.uid[2] = ((byte)(int)(id >> 8));
/*  659 */       this.uid[3] = ((byte)(int)id);
/*      */     }
/*      */     else
/*      */     {
/*  663 */       RandomUtils.nextSecureBytes(this.uid);
/*      */       
/*  665 */       configDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean sameLogicAs(Engine other)
/*      */   {
/*      */     try
/*      */     {
/*  674 */       Map m1 = exportToBencodedMap();
/*  675 */       Map m2 = other.exportToBencodedMap();
/*      */       
/*  677 */       String[] to_remove = { "type", "id", "last_updated", "selected", "select_rec", "source", "rank_bias", "pref_count", "version", "az_version", "uid" };
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  690 */       for (int i = 0; i < to_remove.length; i++)
/*      */       {
/*  692 */         m1.remove(to_remove[i]);
/*  693 */         m2.remove(to_remove[i]);
/*      */       }
/*      */       
/*  696 */       return BEncoder.mapsAreIdentical(m1, m2);
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  700 */       Debug.printStackTrace(e);
/*      */     }
/*  702 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public Result[] search(SearchParameter[] params, Map context, int desired_max_matches, int absolute_max_matches, String headers, final ResultListener listener)
/*      */     throws SearchException
/*      */   {
/*  717 */     if (context == null)
/*      */     {
/*  719 */       context = new HashMap();
/*      */     }
/*      */     try
/*      */     {
/*  723 */       final Set<Result> results_informed = new HashSet();
/*  724 */       final boolean[] complete_informed = { false };
/*      */       
/*  726 */       ResultListener interceptor = new ResultListener()
/*      */       {
/*      */ 
/*      */ 
/*      */         public void contentReceived(Engine engine, String content)
/*      */         {
/*      */ 
/*      */ 
/*  734 */           listener.contentReceived(engine, content);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void matchFound(Engine engine, String[] fields)
/*      */         {
/*  742 */           listener.matchFound(engine, fields);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void resultsReceived(Engine engine, Result[] results)
/*      */         {
/*  750 */           listener.resultsReceived(engine, results);
/*      */           
/*  752 */           synchronized (results_informed)
/*      */           {
/*  754 */             results_informed.addAll(Arrays.asList(results));
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */         public void resultsComplete(Engine engine)
/*      */         {
/*  762 */           listener.resultsComplete(engine);
/*      */           
/*  764 */           synchronized (results_informed)
/*      */           {
/*  766 */             complete_informed[0] = true;
/*      */           }
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void engineFailed(Engine engine, Throwable cause)
/*      */         {
/*  775 */           listener.engineFailed(engine, cause);
/*      */         }
/*      */         
/*      */ 
/*      */ 
/*      */ 
/*      */         public void engineRequiresLogin(Engine engine, Throwable cause)
/*      */         {
/*  783 */           listener.engineRequiresLogin(engine, cause);
/*      */         }
/*      */         
/*  786 */       };
/*  787 */       Result[] results = searchAndMap(params, context, desired_max_matches, absolute_max_matches, headers, listener == null ? null : interceptor);
/*      */       
/*  789 */       if (listener != null)
/*      */       {
/*      */ 
/*  792 */         List<Result> inform_result = new ArrayList();
/*      */         boolean inform_complete;
/*  794 */         synchronized (results_informed)
/*      */         {
/*  796 */           for (Result r : results)
/*      */           {
/*  798 */             if (!results_informed.contains(r))
/*      */             {
/*  800 */               inform_result.add(r);
/*      */             }
/*      */           }
/*      */           
/*  804 */           inform_complete = complete_informed[0] == 0;
/*      */         }
/*      */         
/*  807 */         if (inform_result.size() > 0)
/*      */         {
/*  809 */           listener.resultsReceived(this, (Result[])inform_result.toArray(new Result[inform_result.size()]));
/*      */         }
/*      */         
/*  812 */         if (inform_complete)
/*      */         {
/*  814 */           listener.resultsComplete(this);
/*      */         }
/*      */       }
/*      */       
/*  818 */       return results;
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*  822 */       if ((e instanceof SearchLoginException))
/*      */       {
/*  824 */         if (listener != null)
/*      */         {
/*  826 */           listener.engineRequiresLogin(this, e);
/*      */         }
/*      */         
/*  829 */         throw ((SearchLoginException)e);
/*      */       }
/*  831 */       if ((e instanceof SearchException))
/*      */       {
/*  833 */         if (listener != null)
/*      */         {
/*  835 */           listener.engineFailed(this, e);
/*      */         }
/*      */         
/*  838 */         throw ((SearchException)e);
/*      */       }
/*      */       
/*      */ 
/*  842 */       if (listener != null)
/*      */       {
/*  844 */         listener.engineFailed(this, e);
/*      */       }
/*      */       
/*  847 */       throw new SearchException("Search failed", e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   protected Result[] searchAndMap(SearchParameter[] params, Map context, int desired_max_matches, int absolute_max_matches, String headers, final ResultListener listener)
/*      */     throws SearchException
/*      */   {
/*  865 */     context.put("azid", ConstantsVuze.AZID);
/*      */     
/*  867 */     if (context.get("azsrc") == null)
/*      */     {
/*  869 */       context.put("azsrc", "search");
/*      */     }
/*      */     
/*  872 */     Result[] results = searchSupport(params, context, desired_max_matches, absolute_max_matches, headers, new ResultListener()
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       public void contentReceived(Engine engine, String content)
/*      */       {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*  886 */         if (listener != null) {
/*  887 */           listener.contentReceived(engine, content);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void matchFound(Engine engine, String[] fields)
/*      */       {
/*  896 */         if (listener != null) {
/*  897 */           listener.matchFound(engine, fields);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void resultsReceived(Engine engine, Result[] results)
/*      */       {
/*  906 */         if (listener != null) {
/*  907 */           listener.resultsReceived(engine, EngineImpl.this.mapResults(results));
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */       public void resultsComplete(Engine engine)
/*      */       {
/*  915 */         if (listener != null) {
/*  916 */           listener.resultsComplete(engine);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void engineFailed(Engine engine, Throwable cause)
/*      */       {
/*  925 */         EngineImpl.this.log("Search failed", cause);
/*      */         
/*  927 */         if (listener != null) {
/*  928 */           listener.engineFailed(engine, cause);
/*      */         }
/*      */       }
/*      */       
/*      */ 
/*      */ 
/*      */ 
/*      */       public void engineRequiresLogin(Engine engine, Throwable cause)
/*      */       {
/*  937 */         EngineImpl.this.log("Search requires login", cause);
/*      */         
/*  939 */         if (listener != null) {
/*  940 */           listener.engineRequiresLogin(engine, cause);
/*      */         }
/*      */         
/*      */       }
/*  944 */     });
/*  945 */     return mapResults(results);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Result[] mapResults(Result[] results)
/*      */   {
/*  952 */     for (int i = 0; i < results.length; i++)
/*      */     {
/*  954 */       Result result = results[i];
/*      */       
/*  956 */       for (int j = 0; j < this.first_level_mapping.size(); j++)
/*      */       {
/*  958 */         FieldRemapper mapper = (FieldRemapper)this.first_level_mapping.get(j);
/*      */         
/*  960 */         mapper.remap(result);
/*      */       }
/*      */       
/*  963 */       for (int j = 0; j < this.second_level_mapping.size(); j++)
/*      */       {
/*  965 */         FieldRemapper mapper = (FieldRemapper)this.second_level_mapping.get(j);
/*      */         
/*  967 */         mapper.remap(result);
/*      */       }
/*      */     }
/*      */     
/*  971 */     return results;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public void delete()
/*      */   {
/*  988 */     this.meta_search.removeEngine(this);
/*      */   }
/*      */   
/*      */ 
/*      */   protected MetaSearchImpl getMetaSearch()
/*      */   {
/*  994 */     return this.meta_search;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected int vuzeFieldToID(String field)
/*      */   {
/* 1001 */     for (int i = 0; i < FIELD_NAMES.length; i++)
/*      */     {
/* 1003 */       if (field.equalsIgnoreCase(FIELD_NAMES[i]))
/*      */       {
/* 1005 */         return FIELD_IDS[i];
/*      */       }
/*      */     }
/*      */     
/* 1009 */     return -1;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String vuzeIDToField(int id)
/*      */   {
/* 1016 */     for (int i = 0; i < FIELD_IDS.length; i++)
/*      */     {
/* 1018 */       if (id == FIELD_IDS[i])
/*      */       {
/* 1020 */         return FIELD_NAMES[i];
/*      */       }
/*      */     }
/*      */     
/* 1024 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getType()
/*      */   {
/* 1030 */     return this.type;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setId(long _id)
/*      */   {
/* 1037 */     this.id = _id;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getId()
/*      */   {
/* 1043 */     return this.id;
/*      */   }
/*      */   
/*      */ 
/*      */   public long getLastUpdated()
/*      */   {
/* 1049 */     return this.last_updated;
/*      */   }
/*      */   
/*      */ 
/*      */   public String getName()
/*      */   {
/* 1055 */     return this.name;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setName(String n)
/*      */   {
/* 1062 */     this.name = n;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isActive()
/*      */   {
/* 1068 */     int state = getSelectionState();
/*      */     
/* 1070 */     return (state != 0) && (state != 3);
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isPublic()
/*      */   {
/* 1076 */     return this.is_public;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setPublic(boolean p)
/*      */   {
/* 1083 */     this.is_public = p;
/*      */   }
/*      */   
/*      */ 
/*      */   public int getSelectionState()
/*      */   {
/* 1089 */     return this.selection_state;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSelectionState(int state)
/*      */   {
/* 1096 */     if (state != this.selection_state)
/*      */     {
/*      */ 
/*      */ 
/* 1100 */       if (getSource() == 1)
/*      */       {
/* 1102 */         if ((state == 2) || (this.selection_state == 2))
/*      */         {
/*      */ 
/* 1105 */           this.selection_state_recorded = false;
/*      */           
/* 1107 */           checkSelectionStateRecorded();
/*      */         }
/*      */       }
/*      */       
/* 1111 */       this.selection_state = state;
/*      */       
/* 1113 */       configDirty();
/*      */       
/* 1115 */       this.meta_search.stateChanged(this);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isAuthenticated()
/*      */   {
/* 1122 */     return false;
/*      */   }
/*      */   
/*      */ 
/*      */   public void recordSelectionState()
/*      */   {
/* 1128 */     this.selection_state_recorded = false;
/*      */     
/* 1130 */     checkSelectionStateRecorded();
/*      */   }
/*      */   
/*      */ 
/*      */   public void checkSelectionStateRecorded()
/*      */   {
/* 1136 */     if (!this.selection_state_recorded) {
/*      */       try
/*      */       {
/* 1139 */         boolean selected = (this.selection_state != 0) && (this.selection_state != 3);
/*      */         
/* 1141 */         log("Marking template id " + getId() + " as selected=" + selected);
/*      */         
/* 1143 */         PlatformMetaSearchMessenger.setTemplatetSelected(this.meta_search.getManager().getExtensionKey(), getId(), ConstantsVuze.AZID, selected);
/*      */         
/* 1145 */         this.selection_state_recorded = true;
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1149 */         log("Failed to record selection state", e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public int getSource()
/*      */   {
/* 1157 */     return this.source;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setSource(int _source)
/*      */   {
/* 1164 */     if (this.source != _source)
/*      */     {
/* 1166 */       this.source = _source;
/*      */       
/* 1168 */       configDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public float getRankBias()
/*      */   {
/* 1175 */     return this.rank_bias;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setRankBias(float _rank_bias)
/*      */   {
/* 1182 */     if (this.rank_bias != _rank_bias)
/*      */     {
/* 1184 */       this.rank_bias = _rank_bias;
/*      */       
/* 1186 */       configDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setPreferredDelta(float delta)
/*      */   {
/* 1194 */     float new_pref = this.preferred_count + delta;
/*      */     
/* 1196 */     new_pref = Math.max(0.0F, new_pref);
/* 1197 */     new_pref = Math.min(10.0F, new_pref);
/*      */     
/* 1199 */     if (new_pref != this.preferred_count)
/*      */     {
/* 1201 */       this.preferred_count = new_pref;
/*      */       
/* 1203 */       configDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public float getPreferredWeighting()
/*      */   {
/* 1210 */     return this.preferred_count;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public float applyRankBias(float _rank)
/*      */   {
/* 1217 */     float rank = _rank * this.rank_bias;
/*      */     
/* 1219 */     rank = (float)(rank * (1.0D + 0.025D * this.preferred_count));
/*      */     
/* 1221 */     rank = Math.min(rank, 1.0F);
/*      */     
/* 1223 */     rank = Math.max(rank, 0.0F);
/*      */     
/*      */ 
/*      */ 
/* 1227 */     return rank;
/*      */   }
/*      */   
/*      */ 
/*      */   public boolean isMine()
/*      */   {
/* 1233 */     return getLocalBoolean("mine", false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void setMine(boolean mine)
/*      */   {
/* 1240 */     setLocalBoolean("mine", mine);
/*      */   }
/*      */   
/*      */ 
/*      */   protected String getUpdateURL()
/*      */   {
/* 1246 */     return this.update_url;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setUpdateURL(String url)
/*      */   {
/* 1253 */     this.update_url = url;
/*      */   }
/*      */   
/*      */ 
/*      */   protected int getUpdateCheckSecs()
/*      */   {
/* 1259 */     long l = getLocalLong("update_check_secs", 0L);
/*      */     
/* 1261 */     if (l != 0L)
/*      */     {
/* 1263 */       return (int)l;
/*      */     }
/*      */     
/* 1266 */     return this.update_check_default_secs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setDefaultUpdateCheckSecs(int secs)
/*      */   {
/* 1273 */     this.update_check_default_secs = secs;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setLocalUpdateCheckSecs(int secs)
/*      */   {
/* 1280 */     setLocalLong("update_check_secs", secs);
/*      */   }
/*      */   
/*      */ 
/*      */   protected long getLastUpdateCheck()
/*      */   {
/* 1286 */     return getLocalLong("last_update_check", 0L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void setLastUpdateCheck(long when)
/*      */   {
/* 1293 */     setLocalLong("last_update_check", when);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   public int getAutoDownloadSupported()
/*      */   {
/* 1303 */     boolean ok = (supportsField(102)) || (supportsField(105));
/*      */     
/*      */ 
/*      */ 
/* 1307 */     return ok ? 1 : 2;
/*      */   }
/*      */   
/*      */   protected void configDirty()
/*      */   {
/* 1312 */     if (this.meta_search != null)
/*      */     {
/* 1314 */       this.meta_search.configDirty();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public void addPotentialAssociation(String key)
/*      */   {
/* 1322 */     this.meta_search.addPotentialAssociation(this, key);
/*      */   }
/*      */   
/*      */ 
/*      */   public Subscription getSubscription()
/*      */   {
/*      */     try
/*      */     {
/* 1330 */       VuzeFile vf = exportToVuzeFile(true);
/*      */       
/* 1332 */       byte[] bytes = vf.exportToBytes();
/*      */       
/* 1334 */       String url_str = "vuze://?body=" + new String(bytes, "ISO-8859-1");
/*      */       
/* 1336 */       boolean is_anon = isAnonymous();
/*      */       
/* 1338 */       SubscriptionManager sub_man = SubscriptionManagerFactory.getSingleton();
/*      */       
/* 1340 */       return sub_man.createSingletonRSS(vf.getName() + ": " + getName() + " (v" + getVersion() + ")", new URL(url_str), Integer.MAX_VALUE, is_anon);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     }
/*      */     catch (Throwable e)
/*      */     {
/*      */ 
/*      */ 
/*      */ 
/* 1351 */       Debug.out(e);
/*      */     }
/*      */     
/* 1354 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public void exportToVuzeFile(File target)
/*      */     throws IOException
/*      */   {
/* 1363 */     VuzeFile vf = VuzeFileHandler.getSingleton().create();
/*      */     
/* 1365 */     vf.addComponent(1, exportToBencodedMap());
/*      */     
/*      */ 
/*      */ 
/* 1369 */     vf.write(target);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public VuzeFile exportToVuzeFile()
/*      */     throws IOException
/*      */   {
/* 1377 */     return exportToVuzeFile(false);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   public VuzeFile exportToVuzeFile(boolean generic)
/*      */     throws IOException
/*      */   {
/* 1386 */     VuzeFile vf = VuzeFileHandler.getSingleton().create();
/*      */     
/* 1388 */     vf.addComponent(1, exportToBencodedMap(generic));
/*      */     
/*      */ 
/*      */ 
/* 1392 */     return vf;
/*      */   }
/*      */   
/*      */ 
/*      */   private String getLocalKey()
/*      */   {
/* 1398 */     return "metasearch.engine." + this.id + ".local";
/*      */   }
/*      */   
/*      */ 
/*      */   public void reset()
/*      */   {
/* 1404 */     synchronized (this)
/*      */     {
/* 1406 */       Map map = COConfigurationManager.getMapParameter(getLocalKey(), new HashMap());
/*      */       
/* 1408 */       map.remove("cookies");
/* 1409 */       map.remove("etag");
/* 1410 */       map.remove("last_mod");
/*      */       
/* 1412 */       COConfigurationManager.setParameter(getLocalKey(), map);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setLocalString(String key, String value)
/*      */   {
/* 1421 */     synchronized (this)
/*      */     {
/* 1423 */       String existing = getLocalString(key);
/*      */       
/* 1425 */       if ((existing != null) && (value != null) && (existing.equals(value)))
/*      */       {
/* 1427 */         return;
/*      */       }
/*      */       
/* 1430 */       Map map = COConfigurationManager.getMapParameter(getLocalKey(), new HashMap());
/*      */       try
/*      */       {
/* 1433 */         ImportExportUtils.exportString(map, key, value);
/*      */         
/* 1435 */         COConfigurationManager.setParameter(getLocalKey(), map);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1439 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected String getLocalString(String key)
/*      */   {
/* 1448 */     synchronized (this)
/*      */     {
/* 1450 */       Map map = COConfigurationManager.getMapParameter(getLocalKey(), new HashMap());
/*      */       try
/*      */       {
/* 1453 */         return ImportExportUtils.importString(map, key);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1457 */         Debug.printStackTrace(e);
/*      */         
/* 1459 */         return null;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setLocalBoolean(String key, boolean value)
/*      */   {
/* 1469 */     setLocalLong(key, value ? 1L : 0L);
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected boolean getLocalBoolean(String key, boolean def)
/*      */   {
/* 1477 */     return getLocalLong(key, def ? 1L : 0L) == 1L;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setLocalLong(String key, long value)
/*      */   {
/* 1485 */     synchronized (this)
/*      */     {
/* 1487 */       long existing = getLocalLong(key, 0L);
/*      */       
/* 1489 */       if (existing == value)
/*      */       {
/* 1491 */         return;
/*      */       }
/*      */       
/* 1494 */       Map map = COConfigurationManager.getMapParameter(getLocalKey(), new HashMap());
/*      */       try
/*      */       {
/* 1497 */         map.put(key, new Long(value));
/*      */         
/* 1499 */         COConfigurationManager.setParameter(getLocalKey(), map);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1503 */         Debug.printStackTrace(e);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected long getLocalLong(String key, long def)
/*      */   {
/* 1513 */     synchronized (this)
/*      */     {
/* 1515 */       Map map = COConfigurationManager.getMapParameter(getLocalKey(), new HashMap());
/*      */       try
/*      */       {
/* 1518 */         return ImportExportUtils.importLong(map, key, def);
/*      */       }
/*      */       catch (Throwable e)
/*      */       {
/* 1522 */         Debug.printStackTrace(e);
/*      */         
/* 1524 */         return def;
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void setUserData(Object key, Object value)
/*      */   {
/* 1534 */     synchronized (this)
/*      */     {
/* 1536 */       if (this.user_data == null)
/*      */       {
/* 1538 */         if (key == null)
/*      */         {
/* 1540 */           return;
/*      */         }
/*      */         
/* 1543 */         this.user_data = new HashMap(4);
/*      */       }
/*      */       
/* 1546 */       if (key == null)
/*      */       {
/* 1548 */         this.user_data.remove(key);
/*      */         
/* 1550 */         if (this.user_data.size() == 0)
/*      */         {
/* 1552 */           this.user_data = null;
/*      */         }
/*      */       }
/*      */       else {
/* 1556 */         this.user_data.put(key, value);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected Object getUserData(Object key)
/*      */   {
/* 1565 */     synchronized (this)
/*      */     {
/* 1567 */       if (this.user_data == null)
/*      */       {
/* 1569 */         return null;
/*      */       }
/*      */       
/* 1572 */       return this.user_data.get(key);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   protected File getDebugFile()
/*      */   {
/* 1579 */     if (logging_enabled)
/*      */     {
/* 1581 */       return new File(AEDiagnostics.getLogDir(), "MetaSearch_Engine_" + getId() + ".txt");
/*      */     }
/*      */     
/*      */ 
/* 1585 */     return null;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected synchronized void debugStart()
/*      */   {
/* 1592 */     File f = getDebugFile();
/*      */     
/* 1594 */     if (f != null)
/*      */     {
/* 1596 */       f.delete();
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected synchronized void debugLog(String str)
/*      */   {
/* 1604 */     File f = getDebugFile();
/*      */     
/* 1606 */     if (f != null)
/*      */     {
/* 1608 */       PrintWriter pw = null;
/*      */       try
/*      */       {
/* 1611 */         pw = new PrintWriter(new FileWriter(f, true));
/*      */         
/* 1613 */         pw.println(str);
/*      */ 
/*      */       }
/*      */       catch (Throwable e) {}finally
/*      */       {
/*      */ 
/* 1619 */         if (pw != null)
/*      */         {
/* 1621 */           pw.close();
/*      */         }
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   protected void log(String str)
/*      */   {
/* 1631 */     if (this.meta_search != null)
/*      */     {
/* 1633 */       this.meta_search.log("Engine " + getId() + ": " + str);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */ 
/*      */   protected void log(String str, Throwable e)
/*      */   {
/* 1642 */     if (this.meta_search != null)
/*      */     {
/* 1644 */       this.meta_search.log("Engine " + getId() + ": " + str, e);
/*      */     }
/*      */   }
/*      */   
/*      */ 
/*      */   public String getString()
/*      */   {
/* 1651 */     return "id=" + getId() + ", name=" + getName() + ", source=" + ENGINE_SOURCE_STRS[getSource()] + ", selected=" + SEL_STATE_STRINGS[getSelectionState()] + ", rb=" + this.rank_bias + ", pref=" + this.preferred_count;
/*      */   }
/*      */   
/*      */ 
/*      */ 
/*      */   public String getString(boolean full)
/*      */   {
/* 1658 */     return getString();
/*      */   }
/*      */   
/*      */   protected abstract Result[] searchSupport(SearchParameter[] paramArrayOfSearchParameter, Map paramMap, int paramInt1, int paramInt2, String paramString, ResultListener paramResultListener)
/*      */     throws SearchException;
/*      */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/metasearch/impl/EngineImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */