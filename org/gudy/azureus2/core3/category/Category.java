package org.gudy.azureus2.core3.category;

import com.aelitis.azureus.core.tag.Tag;
import java.util.List;
import org.gudy.azureus2.core3.download.DownloadManager;
import org.gudy.azureus2.core3.download.DownloadManagerState;
import org.gudy.azureus2.core3.util.IndentWriter;

public abstract interface Category
  extends Tag
{
  public static final int TYPE_USER = 0;
  public static final int TYPE_ALL = 1;
  public static final int TYPE_UNCATEGORIZED = 2;
  public static final String AT_AUTO_TRANSCODE_TARGET = "at_att";
  public static final String AT_RSS_GEN = "at_rss_gen";
  public static final String AT_UPLOAD_PRIORITY = "at_up_pri";
  
  public abstract void addCategoryListener(CategoryListener paramCategoryListener);
  
  public abstract void removeCategoryListener(CategoryListener paramCategoryListener);
  
  public abstract boolean hasCategoryListener(CategoryListener paramCategoryListener);
  
  public abstract String getName();
  
  public abstract int getType();
  
  public abstract List<DownloadManager> getDownloadManagers(List<DownloadManager> paramList);
  
  public abstract void addManager(DownloadManagerState paramDownloadManagerState);
  
  public abstract void removeManager(DownloadManagerState paramDownloadManagerState);
  
  public abstract void setDownloadSpeed(int paramInt);
  
  public abstract int getDownloadSpeed();
  
  public abstract void setUploadSpeed(int paramInt);
  
  public abstract int getUploadSpeed();
  
  public abstract String getStringAttribute(String paramString);
  
  public abstract void setStringAttribute(String paramString1, String paramString2);
  
  public abstract boolean getBooleanAttribute(String paramString);
  
  public abstract void setBooleanAttribute(String paramString, boolean paramBoolean);
  
  public abstract int getIntAttribute(String paramString);
  
  public abstract void setIntAttribute(String paramString, int paramInt);
  
  public abstract void dump(IndentWriter paramIndentWriter);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/core3/category/Category.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */