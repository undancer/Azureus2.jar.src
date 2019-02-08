/*     */ package com.aelitis.azureus.util;
/*     */ 
/*     */ import com.aelitis.azureus.core.util.CopyOnWriteList;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFile;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileComponent;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileHandler;
/*     */ import com.aelitis.azureus.core.vuzefile.VuzeFileProcessor;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
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
/*     */ public class NavigationHelper
/*     */ {
/*     */   public static final int COMMAND_SWITCH_TO_TAB = 1;
/*     */   public static final int COMMAND_CONDITION_CHECK = 2;
/*  41 */   private static CopyOnWriteList listeners = new CopyOnWriteList();
/*     */   
/*     */   private static List command_queue;
/*     */   
/*     */   protected static void initialise()
/*     */   {
/*  47 */     VuzeFileHandler.getSingleton().addProcessor(new VuzeFileProcessor()
/*     */     {
/*     */ 
/*     */ 
/*     */       public void process(VuzeFile[] files, int expected_types)
/*     */       {
/*     */ 
/*     */ 
/*  55 */         for (int i = 0; i < files.length; i++)
/*     */         {
/*  57 */           VuzeFile vf = files[i];
/*     */           
/*  59 */           VuzeFileComponent[] comps = vf.getComponents();
/*     */           
/*  61 */           for (int j = 0; j < comps.length; j++)
/*     */           {
/*  63 */             VuzeFileComponent comp = comps[j];
/*     */             
/*  65 */             if ((comp.getType() == 2) || (comp.getType() == 4))
/*     */             {
/*     */               try
/*     */               {
/*     */ 
/*  70 */                 List commands = (List)comp.getContent().get("commands");
/*     */                 
/*  72 */                 for (int k = 0; k < commands.size(); k++)
/*     */                 {
/*  74 */                   Map command = (Map)commands.get(k);
/*     */                   
/*  76 */                   int command_type = ((Long)command.get("type")).intValue();
/*     */                   
/*  78 */                   List l_args = (List)command.get("args");
/*     */                   
/*     */                   String[] args;
/*     */                   String[] args;
/*  82 */                   if (l_args == null)
/*     */                   {
/*  84 */                     args = new String[0];
/*     */                   }
/*     */                   else
/*     */                   {
/*  88 */                     args = new String[l_args.size()];
/*     */                     
/*  90 */                     for (int l = 0; l < args.length; l++)
/*     */                     {
/*  92 */                       args[l] = new String((byte[])(byte[])l_args.get(l), "UTF-8");
/*     */                     }
/*     */                   }
/*     */                   
/*  96 */                   NavigationHelper.addCommand(command_type, args);
/*     */                 }
/*     */                 
/*  99 */                 comp.setProcessed();
/*     */               }
/*     */               catch (Throwable e)
/*     */               {
/* 103 */                 Debug.printStackTrace(e);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     });
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   protected static void addCommand(int type, String[] args)
/*     */   {
/* 119 */     synchronized (listeners)
/*     */     {
/* 121 */       if (listeners.size() == 0)
/*     */       {
/* 123 */         if (command_queue == null)
/*     */         {
/* 125 */           command_queue = new ArrayList();
/*     */         }
/*     */         
/* 128 */         command_queue.add(new Object[] { new Integer(type), args });
/*     */       }
/*     */     }
/*     */     
/*     */ 
/*     */ 
/* 134 */     Iterator it = listeners.iterator();
/*     */     
/* 136 */     while (it.hasNext())
/*     */     {
/* 138 */       navigationListener l = (navigationListener)it.next();
/*     */       try
/*     */       {
/* 141 */         l.processCommand(type, args);
/*     */       }
/*     */       catch (Throwable e)
/*     */       {
/* 145 */         Debug.printStackTrace(e);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public static void addListener(navigationListener l)
/*     */   {
/*     */     List queue;
/*     */     
/* 156 */     synchronized (listeners)
/*     */     {
/* 158 */       listeners.add(l);
/*     */       
/* 160 */       queue = command_queue;
/*     */       
/* 162 */       command_queue = null;
/*     */     }
/*     */     
/* 165 */     if (queue != null)
/*     */     {
/* 167 */       for (int i = 0; i < queue.size(); i++)
/*     */       {
/* 169 */         Object[] entry = (Object[])queue.get(i);
/*     */         
/* 171 */         int type = ((Integer)entry[0]).intValue();
/* 172 */         String[] args = (String[])entry[1];
/*     */         try
/*     */         {
/* 175 */           l.processCommand(type, args);
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 179 */           Debug.printStackTrace(e);
/*     */         }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*     */     try
/*     */     {
/* 199 */       VuzeFile vf = VuzeFileHandler.getSingleton().create();
/*     */       
/* 201 */       Map content = new HashMap();
/*     */       
/* 203 */       List commands = new ArrayList();
/*     */       
/* 205 */       content.put("commands", commands);
/*     */       
/*     */ 
/*     */ 
/* 209 */       Map command1 = new HashMap();
/*     */       
/* 211 */       commands.add(command1);
/*     */       
/* 213 */       List l_args1 = new ArrayList();
/*     */       
/*     */ 
/*     */ 
/* 217 */       command1.put("type", new Long(1L));
/* 218 */       command1.put("args", l_args1);
/*     */       
/*     */ 
/*     */ 
/* 222 */       Map command2 = new HashMap();
/*     */       
/* 224 */       commands.add(command2);
/*     */       
/* 226 */       List l_args2 = new ArrayList();
/*     */       
/*     */ 
/*     */ 
/* 230 */       command2.put("type", new Long(1L));
/* 231 */       command2.put("args", l_args2);
/*     */       
/*     */ 
/*     */ 
/* 235 */       Map command3 = new HashMap();
/*     */       
/* 237 */       commands.add(command3);
/*     */       
/* 239 */       List l_args3 = new ArrayList();
/*     */       
/* 241 */       command3.put("type", new Long(2L));
/* 242 */       command3.put("args", l_args3);
/*     */       
/* 244 */       vf.addComponent(2, content);
/*     */       
/* 246 */       vf.write(new File("C:\\temp\\v3ui.vuze"));
/*     */     }
/*     */     catch (Throwable e)
/*     */     {
/* 250 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public static abstract interface navigationListener
/*     */   {
/*     */     public abstract void processCommand(int paramInt, String[] paramArrayOfString);
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/util/NavigationHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */