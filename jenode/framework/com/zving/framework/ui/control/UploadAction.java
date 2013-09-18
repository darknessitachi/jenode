package com.zving.framework.ui.control;

import com.zving.framework.utility.ObjectUtil;
import java.util.ArrayList;
import org.apache.commons.fileupload.FileItem;

public class UploadAction
{
  protected ArrayList<FileItem> items;

  public FileItem getFirstFile()
  {
    if (ObjectUtil.empty(this.items)) {
      return null;
    }
    return (FileItem)this.items.get(0);
  }

  public ArrayList<FileItem> getAllFiles() {
    return this.items;
  }

  public void setItems(ArrayList<FileItem> items) {
    this.items = items;
  }
}