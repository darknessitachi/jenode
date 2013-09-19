package com.zving.platform.privilege;

public class MetadataPriv extends AbstractMenuPriv
{
  public static final String MenuID = "Platform.Metadata";
  public static final String Add = "Platform.Metadata.Add";
  public static final String SimilarCreate = "Platform.Metadata.SimilarCreate";
  public static final String Delete = "Platform.Metadata.Delete";
  public static final String AddData = "Platform.Metadata.AddData";
  public static final String EditData = "Platform.Metadata.EditData";
  public static final String DeleteData = "Platform.Metadata.DeleteData";

  public MetadataPriv()
  {
    super("Platform.Metadata", null);
    addItem("Platform.Metadata.Add", "@{Common.Add}");
    addItem("Platform.Metadata.SimilarCreate", "@{Platform.SimilarCreate}");
    addItem("Platform.Metadata.Delete", "@{Common.Delete}");
    addItem("Platform.Metadata.AddData", "@{Platform.AddData}");
    addItem("Platform.Metadata.EditData", "@{Platform.EditData}");
    addItem("Platform.Metadata.DeleteData", "@{Platform.DeleteData}");
  }
}