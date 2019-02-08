package com.aelitis.azureus.core.tag;

public abstract interface TagFeatureProperties
{
  public static final String PR_TRACKERS = "trackers";
  public static final String PR_UNTAGGED = "untagged";
  public static final String PR_TRACKER_TEMPLATES = "tracker_templates";
  public static final String PR_CONSTRAINT = "constraint";
  public static final int PT_STRING_LIST = 1;
  public static final int PT_BOOLEAN = 2;
  public static final int PT_LONG = 3;
  
  public abstract TagProperty[] getSupportedProperties();
  
  public abstract TagProperty getProperty(String paramString);
  
  public static abstract interface TagProperty
  {
    public abstract Tag getTag();
    
    public abstract int getType();
    
    public abstract String getName(boolean paramBoolean);
    
    public abstract void setStringList(String[] paramArrayOfString);
    
    public abstract String[] getStringList();
    
    public abstract void setBoolean(Boolean paramBoolean);
    
    public abstract Boolean getBoolean();
    
    public abstract void setLong(Long paramLong);
    
    public abstract Long getLong();
    
    public abstract String getString();
    
    public abstract void addListener(TagFeatureProperties.TagPropertyListener paramTagPropertyListener);
    
    public abstract void removeListener(TagFeatureProperties.TagPropertyListener paramTagPropertyListener);
    
    public abstract void syncListeners();
  }
  
  public static abstract interface TagPropertyListener
  {
    public abstract void propertyChanged(TagFeatureProperties.TagProperty paramTagProperty);
    
    public abstract void propertySync(TagFeatureProperties.TagProperty paramTagProperty);
  }
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/core/tag/TagFeatureProperties.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */