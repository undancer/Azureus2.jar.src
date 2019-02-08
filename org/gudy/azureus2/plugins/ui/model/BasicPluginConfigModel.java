package org.gudy.azureus2.plugins.ui.model;

import org.gudy.azureus2.plugins.ui.components.UITextArea;
import org.gudy.azureus2.plugins.ui.config.ActionParameter;
import org.gudy.azureus2.plugins.ui.config.BooleanParameter;
import org.gudy.azureus2.plugins.ui.config.ColorParameter;
import org.gudy.azureus2.plugins.ui.config.DirectoryParameter;
import org.gudy.azureus2.plugins.ui.config.FileParameter;
import org.gudy.azureus2.plugins.ui.config.HyperlinkParameter;
import org.gudy.azureus2.plugins.ui.config.InfoParameter;
import org.gudy.azureus2.plugins.ui.config.IntParameter;
import org.gudy.azureus2.plugins.ui.config.LabelParameter;
import org.gudy.azureus2.plugins.ui.config.Parameter;
import org.gudy.azureus2.plugins.ui.config.ParameterGroup;
import org.gudy.azureus2.plugins.ui.config.ParameterTabFolder;
import org.gudy.azureus2.plugins.ui.config.PasswordParameter;
import org.gudy.azureus2.plugins.ui.config.StringListParameter;
import org.gudy.azureus2.plugins.ui.config.StringParameter;
import org.gudy.azureus2.plugins.ui.config.UIParameter;
import org.gudy.azureus2.plugins.ui.config.UIParameterContext;

public abstract interface BasicPluginConfigModel
  extends PluginConfigModel
{
  public static final String BLANK_RESOURCE = "_blank";
  
  /**
   * @deprecated
   */
  public abstract void addBooleanParameter(String paramString1, String paramString2, boolean paramBoolean);
  
  /**
   * @deprecated
   */
  public abstract void addStringParameter(String paramString1, String paramString2, String paramString3);
  
  public abstract BooleanParameter addBooleanParameter2(String paramString1, String paramString2, boolean paramBoolean);
  
  public abstract StringParameter addStringParameter2(String paramString1, String paramString2, String paramString3);
  
  public abstract StringListParameter addStringListParameter2(String paramString1, String paramString2, String[] paramArrayOfString, String paramString3);
  
  public abstract StringListParameter addStringListParameter2(String paramString1, String paramString2, String[] paramArrayOfString1, String[] paramArrayOfString2, String paramString3);
  
  public abstract PasswordParameter addPasswordParameter2(String paramString1, String paramString2, int paramInt, byte[] paramArrayOfByte);
  
  public abstract IntParameter addIntParameter2(String paramString1, String paramString2, int paramInt);
  
  public abstract IntParameter addIntParameter2(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract LabelParameter addLabelParameter2(String paramString);
  
  public abstract InfoParameter addInfoParameter2(String paramString1, String paramString2);
  
  public abstract HyperlinkParameter addHyperlinkParameter2(String paramString1, String paramString2);
  
  public abstract DirectoryParameter addDirectoryParameter2(String paramString1, String paramString2, String paramString3);
  
  public abstract FileParameter addFileParameter2(String paramString1, String paramString2, String paramString3);
  
  public abstract FileParameter addFileParameter2(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString);
  
  public abstract ActionParameter addActionParameter2(String paramString1, String paramString2);
  
  public abstract ColorParameter addColorParameter2(String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract UIParameter addUIParameter2(UIParameterContext paramUIParameterContext, String paramString);
  
  public abstract UITextArea addTextArea(String paramString);
  
  public abstract ParameterGroup createGroup(String paramString, Parameter[] paramArrayOfParameter);
  
  public abstract ParameterTabFolder createTabFolder();
  
  public abstract String getSection();
  
  public abstract String getParentSection();
  
  public abstract Parameter[] getParameters();
  
  public abstract void setLocalizedName(String paramString);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/model/BasicPluginConfigModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */