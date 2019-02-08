package org.gudy.azureus2.plugins.ui.model;

import org.gudy.azureus2.plugins.logging.LoggerChannel;
import org.gudy.azureus2.plugins.ui.components.UIProgressBar;
import org.gudy.azureus2.plugins.ui.components.UITextArea;
import org.gudy.azureus2.plugins.ui.components.UITextField;

public abstract interface BasicPluginViewModel
  extends PluginViewModel
{
  public abstract UITextField getStatus();
  
  public abstract UITextField getActivity();
  
  public abstract UITextArea getLogArea();
  
  public abstract UIProgressBar getProgress();
  
  public abstract void setConfigSectionID(String paramString);
  
  public abstract String getConfigSectionID();
  
  public abstract void attachLoggerChannel(LoggerChannel paramLoggerChannel);
}


/* Location:              /Users/Shared/Library/Application Support/Vuze/Azureus2.jar!/org/gudy/azureus2/plugins/ui/model/BasicPluginViewModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */