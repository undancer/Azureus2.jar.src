/*     */ package org.gudy.azureus2.ui.console.commands;
/*     */ 
/*     */ import com.aelitis.azureus.core.tag.Tag;
/*     */ import com.aelitis.azureus.core.tag.TagFeatureRSSFeed;
/*     */ import com.aelitis.azureus.core.tag.TagManager;
/*     */ import com.aelitis.azureus.core.tag.TagManagerFactory;
/*     */ import com.aelitis.azureus.core.tag.TagType;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.gudy.azureus2.core3.download.DownloadManager;
/*     */ import org.gudy.azureus2.core3.util.Debug;
/*     */ import org.gudy.azureus2.ui.console.ConsoleInput;
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
/*     */ public class Tags
/*     */   extends IConsoleCommand
/*     */ {
/*     */   private List<Tag> current_tags;
/*     */   private Tag current_tag;
/*     */   
/*     */   public Tags()
/*     */   {
/*  37 */     super("tag", "tag");
/*     */   }
/*     */   
/*     */   public String getCommandDescriptions()
/*     */   {
/*  42 */     return "tags\t\tAccess to tags.";
/*     */   }
/*     */   
/*     */   public void printHelpExtra(PrintStream out, List<String> args) {
/*  46 */     out.println("> -----");
/*  47 */     out.println("Subcommands:");
/*  48 */     out.println("\tlist\t: List tags");
/*  49 */     out.println("\tcreate <name>\t: Create a new tag");
/*  50 */     out.println("\tselect <number>\t: Select tag <number> for further operations");
/*  51 */     out.println("The following commands operate on a selected tag");
/*  52 */     out.println("\ttorrents\t: List the tag's torrents");
/*  53 */     out.println("\tshow\t: Show tag properties");
/*  54 */     out.println("\tset_rssenable [yes|no]\t: Enable/disable RSS feed generation for the tag");
/*  55 */     out.println("\tdelete\t: Delete the tag");
/*     */     
/*  57 */     out.println("> -----");
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public void execute(String commandName, ConsoleInput ci, List<String> args)
/*     */   {
/*  64 */     if (args.isEmpty())
/*     */     {
/*  66 */       printHelp(ci.out, args);
/*     */       
/*  68 */       return;
/*     */     }
/*     */     
/*  71 */     String cmd = (String)args.get(0);
/*     */     
/*  73 */     TagManager tm = TagManagerFactory.getTagManager();
/*     */     
/*  75 */     TagType tt = tm.getTagType(3);
/*     */     
/*  77 */     if (cmd.equals("list"))
/*     */     {
/*  79 */       ci.out.println("> -----");
/*     */       
/*  81 */       this.current_tags = tt.getTags();
/*     */       
/*  83 */       int index = 1;
/*     */       
/*  85 */       for (Tag tag : this.current_tags)
/*     */       {
/*  87 */         String index_str = "" + index++;
/*     */         
/*  89 */         while (index_str.length() < 3) {
/*  90 */           index_str = index_str + " ";
/*     */         }
/*     */         
/*  93 */         String str = index_str + tag.getTagName(true) + ", downloads=" + tag.getTaggedCount();
/*     */         
/*  95 */         ci.out.println(str);
/*     */       }
/*     */       
/*  98 */       if (this.current_tags.size() == 0)
/*     */       {
/* 100 */         ci.out.println("No Tags");
/*     */       }
/* 102 */     } else if (cmd.equals("create"))
/*     */     {
/* 104 */       if (args.size() < 2)
/*     */       {
/* 106 */         ci.out.println("Usage: tag create <name>");
/*     */       }
/*     */       else
/*     */       {
/* 110 */         String tag_name = (String)args.get(1);
/*     */         
/* 112 */         if (tt.getTag(tag_name, true) != null)
/*     */         {
/* 114 */           ci.out.println("Tag already exists");
/*     */         } else {
/*     */           try
/*     */           {
/* 118 */             tt.createTag(tag_name, true);
/*     */             
/* 120 */             ci.out.println("Tag created");
/*     */           }
/*     */           catch (Throwable e)
/*     */           {
/* 124 */             ci.out.println("Failed to create tag: " + Debug.getNestedExceptionMessage(e));
/*     */           }
/*     */         }
/*     */       }
/* 128 */     } else if (cmd.equals("select"))
/*     */     {
/* 130 */       if (args.size() < 2)
/*     */       {
/* 132 */         ci.out.println("Usage: tag select <number>");
/*     */       }
/*     */       else {
/*     */         try
/*     */         {
/* 137 */           int index = Integer.parseInt((String)args.get(1));
/*     */           
/* 139 */           if (this.current_tags == null)
/*     */           {
/* 141 */             throw new Exception("tags must be listed prior to being selected");
/*     */           }
/* 143 */           if (this.current_tags.size() == 0)
/*     */           {
/* 145 */             throw new Exception("no tags exist");
/*     */           }
/* 147 */           if ((index < 0) || (index > this.current_tags.size()))
/*     */           {
/* 149 */             throw new Exception("tag index '" + index + "' is out of range");
/*     */           }
/*     */           
/* 152 */           this.current_tag = ((Tag)this.current_tags.get(index - 1));
/*     */           
/* 154 */           ci.out.println("Selected tag '" + this.current_tag.getTagName(true) + "'");
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 158 */           ci.out.println("Failed to select tag: " + Debug.getNestedExceptionMessage(e));
/*     */         }
/*     */         
/*     */       }
/*     */     }
/* 163 */     else if ((cmd.equals("torrents")) || (cmd.equals("show")) || (cmd.equals("delete")) || (cmd.equals("set_rssenable")))
/*     */     {
/*     */ 
/*     */ 
/*     */ 
/* 168 */       if (this.current_tag == null)
/*     */       {
/* 170 */         ci.out.println("No current tag - select one!");
/*     */ 
/*     */ 
/*     */       }
/* 174 */       else if (cmd.equals("torrents"))
/*     */       {
/* 176 */         ci.out.println("Torrents for tag '" + this.current_tag.getTagName(true) + "'");
/*     */         
/* 178 */         List<DownloadManager> downloads = new ArrayList((Set)this.current_tag.getTagged());
/*     */         
/* 180 */         Collections.sort(downloads, new IConsoleCommand.TorrentComparator());
/*     */         
/* 182 */         for (DownloadManager dm : downloads)
/*     */         {
/* 184 */           ci.out.println(getTorrentSummary(dm));
/*     */         }
/*     */       }
/* 187 */       else if (cmd.equals("show"))
/*     */       {
/* 189 */         ci.out.println("Details for tag '" + this.current_tag.getTagName(true) + "'");
/*     */         
/* 191 */         ci.out.println("\tRSS Enable: " + ((TagFeatureRSSFeed)this.current_tag).isTagRSSFeedEnabled());
/*     */       }
/* 193 */       else if (cmd.equals("set_rssenable"))
/*     */       {
/* 195 */         if (args.size() < 2)
/*     */         {
/* 197 */           ci.out.println("Usage: " + cmd + " [yes|no]");
/*     */         }
/*     */         else
/*     */         {
/* 201 */           String temp = (String)args.get(1);
/*     */           
/* 203 */           if ((temp.equals("yes")) || (temp.equals("no")))
/*     */           {
/* 205 */             ((TagFeatureRSSFeed)this.current_tag).setTagRSSFeedEnabled(temp.equals("yes"));
/*     */           }
/*     */           else
/*     */           {
/* 209 */             ci.out.println("Usage: " + cmd + " [yes|no]");
/*     */           }
/*     */         }
/* 212 */       } else if (cmd.equals("delete"))
/*     */       {
/* 214 */         this.current_tag.removeTag();
/*     */         
/* 216 */         this.current_tag = null;
/* 217 */         this.current_tags = null;
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 222 */       ci.out.println("Unsupported sub-command: " + cmd);
/*     */     }
/*     */     
/* 225 */     ci.out.println("> -----");
/*     */   }
/*     */ }


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/ui/console/commands/Tags.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */