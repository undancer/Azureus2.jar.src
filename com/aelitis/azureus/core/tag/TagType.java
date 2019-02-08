package com.aelitis.azureus.core.tag;

import java.util.List;

public abstract interface TagType
{
  public static final int TT_DOWNLOAD_CATEGORY = 1;
  public static final int TT_DOWNLOAD_STATE = 2;
  public static final int TT_DOWNLOAD_MANUAL = 3;
  public static final int TT_PEER_IPSET = 4;
  
  public abstract int getTagType();
  
  public abstract String getTagTypeName(boolean paramBoolean);
  
  public abstract boolean isTagTypeAuto();
  
  public abstract boolean isTagTypePersistent();
  
  public abstract long getTagTypeFeatures();
  
  public abstract boolean hasTagTypeFeature(long paramLong);
  
  public abstract Tag createTag(String paramString, boolean paramBoolean)
    throws TagException;
  
  public abstract void addTag(Tag paramTag);
  
  public abstract void removeTag(Tag paramTag);
  
  public abstract Tag getTag(int paramInt);
  
  public abstract Tag getTag(String paramString, boolean paramBoolean);
  
  public abstract List<Tag> getTags();
  
  public abstract List<Tag> getTagsForTaggable(Taggable paramTaggable);
  
  public abstract void removeTagType();
  
  public abstract TagManager getTagManager();
  
  public abstract int[] getColorDefault();
  
  public abstract void addTagTypeListener(TagTypeListener paramTagTypeListener, boolean paramBoolean);
  
  public abstract void removeTagTypeListener(TagTypeListener paramTagTypeListener);
  
  public abstract void addTagListener(Taggable paramTaggable, TagListener paramTagListener);
  
  public abstract void removeTagListener(Taggable paramTaggable, TagListener paramTagListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */