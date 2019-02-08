/*     */ package org.gudy.azureus2.core3.util.protocol.vuze;
/*     */ 
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
/*     */ class VuzeURLConnection
/*     */   extends HttpURLConnection
/*     */ {
/*     */   private final URL url;
/*  50 */   private int response_code = 200;
/*  51 */   private String response_msg = "OK";
/*     */   
/*     */   private InputStream input_stream;
/*  54 */   private final Map<String, List<String>> headers = new HashMap();
/*     */   
/*     */ 
/*     */   VuzeURLConnection(URL u)
/*     */   {
/*  59 */     super(u);
/*     */     
/*  61 */     this.url = u;
/*     */   }
/*     */   
/*     */ 
/*     */   public void connect()
/*     */     throws IOException
/*     */   {
/*  68 */     String str = this.url.toExternalForm();
/*     */     
/*  70 */     int pos = str.indexOf("=");
/*     */     
/*  72 */     str = str.substring(pos + 1);
/*     */     
/*  74 */     byte[] bytes = str.getBytes("ISO-8859-1");
/*     */     
/*  76 */     VuzeFile vf = VuzeFileHandler.getSingleton().loadVuzeFile(bytes);
/*     */     
/*  78 */     if (vf == null)
/*     */     {
/*  80 */       throw new IOException("Invalid vuze file");
/*     */     }
/*     */     
/*  83 */     this.input_stream = new ByteArrayInputStream(bytes);
/*     */   }
/*     */   
/*     */ 
/*     */   public Map<String, List<String>> getHeaderFields()
/*     */   {
/*  89 */     return this.headers;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public String getHeaderField(String name)
/*     */   {
/*  96 */     List<String> values = (List)this.headers.get(name);
/*     */     
/*  98 */     if ((values == null) || (values.size() == 0))
/*     */     {
/* 100 */       return null;
/*     */     }
/*     */     
/* 103 */     return (String)values.get(values.size() - 1);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setHeaderField(String name, String value)
/*     */   {
/* 111 */     List<String> values = (List)this.headers.get(name);
/*     */     
/* 113 */     if (values == null)
/*     */     {
/* 115 */       values = new ArrayList();
/*     */       
/* 117 */       this.headers.put(name, values);
/*     */     }
/*     */     
/* 120 */     values.add(value);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public InputStream getInputStream()
/*     */     throws IOException
/*     */   {
/* 128 */     if (this.input_stream == null)
/*     */     {
/* 130 */       connect();
/*     */     }
/*     */     
/* 133 */     return this.input_stream;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public void setResponse(int _code, String _msg)
/*     */   {
/* 141 */     this.response_code = _code;
/* 142 */     this.response_msg = _msg;
/*     */   }
/*     */   
/*     */ 
/*     */   public int getResponseCode()
/*     */   {
/* 148 */     return this.response_code;
/*     */   }
/*     */   
/*     */ 
/*     */   public String getResponseMessage()
/*     */   {
/* 154 */     return this.response_msg;
/*     */   }
/*     */   
/*     */ 
/*     */   public boolean usingProxy()
/*     */   {
/* 160 */     return false;
/*     */   }
/*     */   
/*     */   public void disconnect() {}
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/util/protocol/vuze/VuzeURLConnection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */