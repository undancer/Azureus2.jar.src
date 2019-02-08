package com.aelitis.azureus.ui.common.viewtitleinfo;

public abstract interface ViewTitleInfo
{
  public static final int TITLE_TEXT = 5;
  public static final int TITLE_INDICATOR_TEXT = 0;
  public static final int TITLE_INDICATOR_COLOR = 8;
  public static final int TITLE_ACTIVE_STATE = 9;
  public static final int TITLE_INDICATOR_TEXT_TOOLTIP = 1;
  public static final int TITLE_IMAGEID = 2;
  public static final int TITLE_IMAGE_TOOLTIP = 3;
  public static final int TITLE_LOGID = 7;
  public static final int TITLE_EXPORTABLE_DATASOURCE = 10;
  
  public abstract Object getTitleInfoProperty(int paramInt);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/com/aelitis/azureus/ui/common/viewtitleinfo/ViewTitleInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */