/*     */ package org.gudy.azureus2.core3.util.protocol.azplug;
/*     */ 
/*     */ import com.aelitis.azureus.core.AzureusCore;
/*     */ import com.aelitis.azureus.core.AzureusCoreFactory;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions;
/*     */ import com.aelitis.azureus.core.util.AZ3Functions.provider;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.core3.util.UrlUtils;
/*     */ import org.gudy.azureus2.plugins.PluginInterface;
/*     */ import org.gudy.azureus2.plugins.PluginManager;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCException;
/*     */ import org.gudy.azureus2.plugins.ipc.IPCInterface;
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
/*     */ public class AZPluginConnection
/*     */   extends HttpURLConnection
/*     */ {
/*  48 */   private int response_code = 200;
/*  49 */   private String response_msg = "OK";
/*     */   
/*     */   private InputStream input_stream;
/*  52 */   private final Map<String, List<String>> headers = new HashMap();
/*     */   
/*     */ 
/*     */ 
/*     */   protected AZPluginConnection(URL _url)
/*     */   {
/*  58 */     super(_url);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void connect()
/*     */     throws IOException
/*     */   {
/*  66 */     String url = getURL().toString();
/*     */     
/*  68 */     int pos = url.indexOf("?");
/*     */     
/*  70 */     if (pos == -1)
/*     */     {
/*  72 */       throw new IOException("Malformed URL - ? missing");
/*     */     }
/*     */     
/*  75 */     url = url.substring(pos + 1);
/*     */     
/*  77 */     String[] bits = url.split("&");
/*     */     
/*  79 */     Map args = new HashMap();
/*     */     
/*  81 */     for (int i = 0; i < bits.length; i++)
/*     */     {
/*  83 */       String bit = bits[i];
/*     */       
/*  85 */       String[] x = bit.split("=");
/*     */       
/*  87 */       if (x.length == 2)
/*     */       {
/*  89 */         String lhs = x[0];
/*  90 */         String rhs = UrlUtils.decode(x[1]);
/*     */         
/*  92 */         args.put(lhs.toLowerCase(), rhs);
/*     */       }
/*     */     }
/*     */     
/*  96 */     String plugin_id = (String)args.get("id");
/*     */     
/*  98 */     if (plugin_id == null)
/*     */     {
/* 100 */       throw new IOException("Plugin id missing");
/*     */     }
/*     */     
/* 103 */     String plugin_name = (String)args.get("name");
/* 104 */     String arg = (String)args.get("arg");
/*     */     
/* 106 */     if (arg == null)
/*     */     {
/* 108 */       arg = "";
/*     */     }
/*     */     
/*     */     String plugin_str;
/*     */     String plugin_str;
/* 113 */     if (plugin_name == null)
/*     */     {
/* 115 */       plugin_str = "with id '" + plugin_id + "'";
/*     */     }
/*     */     else
/*     */     {
/* 119 */       plugin_str = "'" + plugin_name + "' (id " + plugin_id + ")";
/*     */     }
/*     */     
/* 122 */     if (plugin_id.equals("subscription"))
/*     */     {
/* 124 */       AZ3Functions.provider az3 = AZ3Functions.getProvider();
/*     */       
/* 126 */       if (az3 == null)
/*     */       {
/* 128 */         throw new IOException("Subscriptions are not available");
/*     */       }
/*     */       try
/*     */       {
/* 132 */         az3.subscribeToSubscription(arg);
/*     */         
/* 134 */         this.input_stream = new ByteArrayInputStream(VuzeFileHandler.getSingleton().create().exportToBytes());
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 138 */         throw new IOException("Subscription addition failed: " + Debug.getNestedExceptionMessage(e));
/*     */       }
/*     */       
/*     */     }
/*     */     else
/*     */     {
/* 144 */       PluginInterface pi = AzureusCoreFactory.getSingleton().getPluginManager().getPluginInterfaceByID(plugin_id);
/*     */       
/* 146 */       if (pi == null)
/*     */       {
/* 148 */         throw new IOException("Plugin " + plugin_str + " is required - go to 'Tools->Plugins->Installation Wizard' to install.");
/*     */       }
/*     */       
/* 151 */       IPCInterface ipc = pi.getIPC();
/*     */       try
/*     */       {
/* 154 */         if (ipc.canInvoke("handleURLProtocol", new Object[] { this, arg }))
/*     */         {
/* 156 */           this.input_stream = ((InputStream)ipc.invoke("handleURLProtocol", new Object[] { this, arg }));
/*     */         }
/*     */         else
/*     */         {
/* 160 */           this.input_stream = ((InputStream)ipc.invoke("handleURLProtocol", new Object[] { arg }));
/*     */         }
/*     */       }
/*     */       catch (IPCException ipce) {
/* 164 */         Throwable e = ipce;
/*     */         
/* 166 */         if (e.getCause() != null)
/*     */         {
/* 168 */           e = e.getCause();
/*     */         }
/*     */         
/* 171 */         throw new IOException("Communication error with plugin '" + plugin_str + "': " + Debug.getNestedExceptionMessage(e));
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, List<String>> getHeaderFields()
/*     */   {
/* 179 */     return this.headers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getHeaderField(String name)
/*     */   {
/* 186 */     List<String> values = (List)this.headers.get(name);
/*     */     
/* 188 */     if ((values == null) || (values.size() == 0))
/*     */     {
/* 190 */       return null;
/*     */     }
/*     */     
/* 193 */     return (String)values.get(values.size() - 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setHeaderField(String name, String value)
/*     */   {
/* 201 */     List<String> values = (List)this.headers.get(name);
/*     */     
/* 203 */     if (values == null)
/*     */     {
/* 205 */       values = new ArrayList();
/*     */       
/* 207 */       this.headers.put(name, values);
/*     */     }
/*     */     
/* 210 */     values.add(value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 218 */     return this.input_stream;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setResponse(int _code, String _msg)
/*     */   {
/* 226 */     this.response_code = _code;
/* 227 */     this.response_msg = _msg;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getResponseCode()
/*     */   {
/* 233 */     return this.response_code;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getResponseMessage()
/*     */   {
/* 239 */     return this.response_msg;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean usingProxy()
/*     */   {
/* 245 */     return false;
/*     */   }
/*     */   
/*     */   public void disconnect() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/azplug/AZPluginConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */