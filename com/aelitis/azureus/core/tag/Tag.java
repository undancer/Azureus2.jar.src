package com.aelitis.azureus.core.tag;

import java.util.Set;

public abstract interface Tag
  extends org.gudy.azureus2.plugins.tag.Tag
{
  public static final String TP_SETTINGS_REQUESTED = "Settings Requested";
  
  public abstract TagType getTagType();
  
  public abstract int getTagID();
  
  public abstract long getTagUID();
  
  public abstract String getTagName(boolean paramBoolean);
  
  public abstract void setTagName(String paramString)
    throws TagException;
  
  public abstract int getTaggableTypes();
  
  public abstract void setCanBePublic(boolean paramBoolean);
  
  public abstract boolean canBePublic();
  
  public abstract boolean isPublic();
  
  public abstract void setPublic(boolean paramBoolean);
  
  public abstract boolean[] isTagAuto();
  
  public abstract boolean isVisible();
  
  public abstract void setVisible(boolean paramBoolean);
  
  public abstract String getGroup();
  
  public abstract void setGroup(String paramString);
  
  public abstract String getImageID();
  
  public abstract void setImageID(String paramString);
  
  public abstract int[] getColor();
  
  public abstract void setColor(int[] paramArrayOfInt);
  
  public abstract void addTaggable(Taggable paramTaggable);
  
  public abstract void removeTaggable(Taggable paramTaggable);
  
  public abstract int getTaggedCount();
  
  public abstract Set<Taggable> getTagged();
  
  public abstract boolean hasTaggable(Taggable paramTaggable);
  
  public abstract void removeTag();
  
  public abstract String getDescription();
  
  public abstract void setDescription(String paramString);
  
  public abstract void setTransientProperty(String paramString, Object paramObject);
  
  public abstract Object getTransientProperty(String paramString);
  
  public abstract long getTaggableAddedTime(Taggable paramTaggable);
  
  public abstract void requestAttention();
  
  public abstract void addTagListener(TagListener paramTagListener, boolean paramBoolean);
  
  public abstract void removeTagListener(TagListener paramTagListener);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/Tag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */