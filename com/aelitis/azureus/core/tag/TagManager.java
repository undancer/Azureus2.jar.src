package com.aelitis.azureus.core.tag;

import java.util.List;

public abstract interface TagManager
{
  public abstract boolean isEnabled();
  
  public abstract void setProcessingEnabled(boolean paramBoolean);
  
  public abstract TagType getTagType(int paramInt);
  
  public abstract List<TagType> getTagTypes();
  
  public abstract List<Tag> getTagsForTaggable(Taggable paramTaggable);
  
  public abstract List<Tag> getTagsForTaggable(int paramInt, Taggable paramTaggable);
  
  public abstract void setTagPublicDefault(boolean paramBoolean);
  
  public abstract boolean getTagPublicDefault();
  
  public abstract Tag lookupTagByUID(long paramLong);
  
  public abstract TaggableLifecycleHandler registerTaggableResolver(TaggableResolver paramTaggableResolver);
  
  public abstract void addTagManagerListener(TagManagerListener paramTagManagerListener, boolean paramBoolean);
  
  public abstract void removeTagManagerListener(TagManagerListener paramTagManagerListener);
  
  public abstract void addTagFeatureListener(int paramInt, TagFeatureListener paramTagFeatureListener);
  
  public abstract void removeTagFeatureListener(TagFeatureListener paramTagFeatureListener);
  
  public abstract void addTaggableLifecycleListener(long paramLong, TaggableLifecycleListener paramTaggableLifecycleListener);
  
  public abstract void removeTaggableLifecycleListener(long paramLong, TaggableLifecycleListener paramTaggableLifecycleListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */