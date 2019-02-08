/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.TreeSet;
/*     */ import org.gudy.azureus2.core3.config.COConfigurationManager;
/*     */ import org.gudy.azureus2.core3.util.SHA1Hasher;
/*     */ import org.gudy.azureus2.ui.common.ExternalUIConst;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
/*     */ import org.pf.text.StringPattern;
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
/*     */ public class Set
/*     */   extends IConsoleCommand
/*     */ {
/*     */   private static final String NULL_STRING = "__NULL__";
/*     */   
/*     */   public Set()
/*     */   {
/*  41 */     super("set", "+");
/*     */   }
/*     */   
/*     */ 
/*  45 */   public String getCommandDescriptions() { return "set [options] [parameter] [value]\t\t+\tSet a configuration parameter. Use \"param name\" when the name includes a space. If value is omitted, the current setting is shown. Parameter may be a wildcard to narrow results"; }
/*     */   
/*     */   public void printHelpExtra(PrintStream out, List args) {
/*  48 */     out.println("> -----");
/*  49 */     out.println("'set' common parameter abbreviations: ");
/*  50 */     out.println("\tmax_up: Maximum upload speed in KB/sec");
/*  51 */     out.println("\tmax_down: Maximum download speed in KB/sec");
/*  52 */     out.println("'set' options: ");
/*  53 */     out.println("\t-export\t\tPrints all the options with non-defaut values.");
/*     */   }
/*     */   
/*     */   public void execute(String commandName, ConsoleInput ci, List args)
/*     */   {
/*  58 */     boolean non_defaults = false;
/*     */     
/*  60 */     Iterator it = args.iterator();
/*     */     
/*  62 */     while (it.hasNext()) {
/*  63 */       String arg = (String)it.next();
/*  64 */       if (arg.equals("-export")) {
/*  65 */         non_defaults = true;
/*  66 */         it.remove();
/*     */       }
/*     */     }
/*  69 */     if (args.isEmpty())
/*     */     {
/*  71 */       displayOptions(ci.out, new StringPattern("*"), non_defaults);
/*  72 */       return;
/*     */     }
/*  74 */     String external_name = (String)args.get(0);
/*  75 */     String internal_name = (String)ExternalUIConst.parameterlegacy.get(external_name);
/*  76 */     if ((internal_name == null) || (internal_name.length() == 0))
/*     */     {
/*  78 */       internal_name = external_name;
/*     */     }
/*     */     
/*     */ 
/*     */     Parameter param;
/*     */     
/*  84 */     switch (args.size())
/*     */     {
/*     */ 
/*     */     case 1: 
/*  88 */       StringPattern sp = new StringPattern(internal_name);
/*  89 */       if (sp.hasWildcard())
/*     */       {
/*  91 */         displayOptions(ci.out, sp, non_defaults);
/*     */ 
/*     */       }
/*     */       else
/*     */       {
/*  96 */         if (!COConfigurationManager.doesParameterDefaultExist(internal_name))
/*     */         {
/*  98 */           ci.out.println("> Command 'set': Parameter '" + external_name + "' unknown.");
/*  99 */           return;
/*     */         }
/* 101 */         param = Parameter.get(internal_name, external_name);
/*     */         
/* 103 */         ci.out.println(param.getString(false));
/*     */       }
/* 105 */       break;
/*     */     case 2: 
/*     */     case 3: 
/* 108 */       String setto = (String)args.get(1);
/*     */       String type;
/* 110 */       String type; if (args.size() == 2)
/*     */       {
/*     */ 
/* 113 */         param = Parameter.get(internal_name, external_name);
/* 114 */         type = param.getType();
/*     */       }
/*     */       else {
/* 117 */         type = (String)args.get(2);
/*     */       }
/* 119 */       boolean success = false;
/* 120 */       if ((type.equalsIgnoreCase("int")) || (type.equalsIgnoreCase("integer"))) {
/* 121 */         COConfigurationManager.setParameter(internal_name, Integer.parseInt(setto));
/* 122 */         success = true;
/*     */       }
/* 124 */       else if ((type.equalsIgnoreCase("bool")) || (type.equalsIgnoreCase("boolean")))
/*     */       {
/*     */         boolean value;
/*     */         boolean value;
/* 128 */         if ((setto.equalsIgnoreCase("true")) || (setto.equalsIgnoreCase("y")) || (setto.equals("1"))) {
/* 129 */           value = true;
/*     */         } else {
/* 131 */           value = false;
/*     */         }
/*     */         
/* 134 */         COConfigurationManager.setParameter(internal_name, value);
/* 135 */         success = true;
/*     */       }
/* 137 */       else if (type.equalsIgnoreCase("float")) {
/* 138 */         COConfigurationManager.setParameter(internal_name, Float.parseFloat(setto));
/* 139 */         success = true;
/*     */       }
/* 141 */       else if (type.equalsIgnoreCase("string")) {
/* 142 */         COConfigurationManager.setParameter(internal_name, setto);
/* 143 */         success = true;
/*     */       }
/* 145 */       else if (type.equalsIgnoreCase("password")) {
/* 146 */         SHA1Hasher hasher = new SHA1Hasher();
/*     */         
/* 148 */         byte[] password = setto.getBytes();
/*     */         
/*     */         byte[] encoded;
/*     */         byte[] encoded;
/* 152 */         if (password.length > 0)
/*     */         {
/* 154 */           encoded = hasher.calculateHash(password);
/*     */         }
/*     */         else
/*     */         {
/* 158 */           encoded = password;
/*     */         }
/*     */         
/* 161 */         COConfigurationManager.setParameter(internal_name, encoded);
/*     */         
/* 163 */         success = true;
/*     */       }
/*     */       
/* 166 */       if (success) {
/* 167 */         COConfigurationManager.save();
/* 168 */         ci.out.println("> Parameter '" + external_name + "' set to '" + setto + "'. [" + type + "]");
/*     */       } else {
/* 170 */         ci.out.println("ERROR: invalid type given");
/*     */       }
/* 172 */       break;
/*     */     default: 
/* 174 */       ci.out.println("Usage: 'set \"parameter\" value type', where type = int, bool, float, string, password");
/*     */     }
/*     */     
/*     */   }
/*     */   
/*     */   private void displayOptions(PrintStream out, StringPattern sp, boolean non_defaults)
/*     */   {
/* 181 */     sp.setIgnoreCase(true);
/* 182 */     Iterator I = non_defaults ? COConfigurationManager.getDefinedParameters().iterator() : COConfigurationManager.getAllowedParameters().iterator();
/* 183 */     Map backmap = new HashMap();
/* 184 */     for (Iterator iter = ExternalUIConst.parameterlegacy.entrySet().iterator(); iter.hasNext();) {
/* 185 */       Map.Entry entry = (Map.Entry)iter.next();
/* 186 */       backmap.put(entry.getValue(), entry.getKey());
/*     */     }
/* 188 */     TreeSet srt = new TreeSet();
/* 189 */     while (I.hasNext()) {
/* 190 */       String internal_name = (String)I.next();
/*     */       
/* 192 */       String external_name = (String)backmap.get(internal_name);
/*     */       
/* 194 */       if (external_name == null)
/*     */       {
/* 196 */         external_name = internal_name;
/*     */       }
/* 198 */       if (sp.matches(external_name))
/*     */       {
/* 200 */         Parameter param = Parameter.get(internal_name, external_name);
/*     */         
/* 202 */         if (non_defaults)
/*     */         {
/* 204 */           if (!param.isDefault())
/*     */           {
/* 206 */             srt.add(param.getString(true));
/*     */           }
/*     */         }
/*     */         else {
/* 210 */           srt.add(param.getString(false));
/*     */         }
/*     */       }
/*     */     }
/* 214 */     I = srt.iterator();
/* 215 */     while (I.hasNext()) {
/* 216 */       out.println((String)I.next());
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   private static class Parameter
/*     */   {
/*     */     private static final int PARAM_INT = 1;
/*     */     
/*     */     private static final int PARAM_BOOLEAN = 2;
/*     */     
/*     */     private static final int PARAM_STRING = 4;
/*     */     
/*     */     private static final int PARAM_OTHER = 8;
/*     */     
/*     */     private int type;
/*     */     
/*     */     private String iname;
/*     */     
/*     */     private String ename;
/*     */     private Object value;
/*     */     private boolean isSet;
/*     */     private Object def;
/*     */     
/*     */     public static Parameter get(String internal_name, String external_name)
/*     */     {
/* 242 */       int underscoreIndex = external_name.indexOf('_');
/* 243 */       int nextchar = external_name.charAt(underscoreIndex + 1);
/*     */       
/* 245 */       if ((internal_name != external_name) && ("ibs".indexOf(nextchar) >= 0))
/*     */       {
/*     */         try
/*     */         {
/* 249 */           if (nextchar == 105)
/*     */           {
/* 251 */             int value = COConfigurationManager.getIntParameter(internal_name, Integer.MIN_VALUE);
/* 252 */             return new Parameter(internal_name, external_name, value == Integer.MIN_VALUE ? (Integer)null : new Integer(value));
/*     */           }
/* 254 */           if (nextchar == 98)
/*     */           {
/*     */ 
/* 257 */             if (COConfigurationManager.getIntParameter(internal_name, Integer.MIN_VALUE) != Integer.MIN_VALUE)
/*     */             {
/* 259 */               boolean b = COConfigurationManager.getBooleanParameter(internal_name);
/* 260 */               return new Parameter(internal_name, external_name, Boolean.valueOf(b));
/*     */             }
/*     */             
/*     */ 
/* 264 */             return new Parameter(internal_name, external_name, (Boolean)null);
/*     */           }
/*     */           
/*     */ 
/*     */ 
/* 269 */           String value = COConfigurationManager.getStringParameter(internal_name, "__NULL__");
/* 270 */           return new Parameter(internal_name, external_name, "__NULL__".equals(value) ? null : value);
/*     */         }
/*     */         catch (Throwable e) {}
/*     */       }
/*     */       
/*     */ 
/*     */ 
/* 277 */       Object v = COConfigurationManager.getParameter(internal_name);
/*     */       try
/*     */       {
/* 280 */         if (((v instanceof Long)) || ((v instanceof Integer)))
/*     */         {
/* 282 */           int value = COConfigurationManager.getIntParameter(internal_name, Integer.MIN_VALUE);
/*     */           
/* 284 */           return new Parameter(internal_name, external_name, value == Integer.MIN_VALUE ? (Integer)null : new Integer(value));
/*     */         }
/* 286 */         if ((v instanceof Boolean))
/*     */         {
/* 288 */           boolean value = COConfigurationManager.getBooleanParameter(internal_name);
/*     */           
/* 290 */           return new Parameter(internal_name, external_name, Boolean.valueOf(value));
/*     */         }
/* 292 */         if (((v instanceof String)) || ((v instanceof byte[])))
/*     */         {
/* 294 */           String value = COConfigurationManager.getStringParameter(internal_name);
/*     */           
/* 296 */           return new Parameter(internal_name, external_name, "__NULL__".equals(value) ? null : value);
/*     */         }
/*     */         
/* 299 */         return new Parameter(internal_name, external_name, v, 8);
/*     */       }
/*     */       catch (Throwable e2) {}
/*     */       
/* 303 */       return new Parameter(internal_name, external_name, v, 8);
/*     */     }
/*     */     
/*     */ 
/*     */     public Parameter(String iname, String ename, Boolean val)
/*     */     {
/* 309 */       this(iname, ename, val, 2);
/*     */     }
/*     */     
/*     */     public Parameter(String iname, String ename, Integer val) {
/* 313 */       this(iname, ename, val, 1);
/*     */     }
/*     */     
/*     */     public Parameter(String iname, String ename, String val) {
/* 317 */       this(iname, ename, val, 4);
/*     */     }
/*     */     
/*     */     private Parameter(String _iname, String _ename, Object _val, int _type) {
/* 321 */       this.type = _type;
/* 322 */       this.iname = _iname;
/* 323 */       this.ename = _ename;
/* 324 */       this.value = _val;
/* 325 */       this.isSet = (this.value != null);
/*     */       
/* 327 */       if (!this.isSet)
/*     */       {
/* 329 */         this.def = COConfigurationManager.getDefault(this.iname);
/*     */         
/* 331 */         if (this.def != null)
/*     */         {
/* 333 */           if ((this.def instanceof Long))
/*     */           {
/* 335 */             this.type = 1;
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
/*     */ 
/*     */     public String getType()
/*     */     {
/* 349 */       switch (this.type)
/*     */       {
/*     */       case 2: 
/* 352 */         return "bool";
/*     */       case 1: 
/* 354 */         return "int";
/*     */       case 4: 
/* 356 */         return "string";
/*     */       }
/* 358 */       return "unknown";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public boolean isDefault()
/*     */     {
/* 365 */       return !this.isSet;
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */     public String getString(boolean set_format)
/*     */     {
/* 372 */       if (this.isSet) {
/* 373 */         if (set_format)
/*     */         {
/* 375 */           return "set " + quoteIfNeeded(this.ename) + " " + quoteIfNeeded(this.value.toString()) + " " + getType();
/*     */         }
/*     */         
/*     */ 
/* 379 */         return "> " + this.ename + ": " + this.value + " [" + getType() + "]";
/*     */       }
/*     */       
/* 382 */       if (this.def == null)
/*     */       {
/* 384 */         return "> " + this.ename + " is not set. [" + getType() + "]";
/*     */       }
/*     */       
/* 387 */       return "> " + this.ename + " is not set. [" + getType() + ", default: " + this.def + "]";
/*     */     }
/*     */     
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     protected String quoteIfNeeded(String str)
/*     */     {
/* 396 */       if (str.indexOf(' ') == -1)
/*     */       {
/* 398 */         return str;
/*     */       }
/*     */       
/* 401 */       return "\"" + str + "\"";
/*     */     }
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Set.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */