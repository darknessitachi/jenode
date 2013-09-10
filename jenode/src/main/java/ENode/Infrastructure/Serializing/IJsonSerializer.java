package ENode.Infrastructure.Serializing;

/** Represents a serializer to support json serialization or deserialization.
 
*/
public interface IJsonSerializer
{
	/** Serialize an object to json string.
	 
	*/
	String Serialize(Object obj);
	/** Deserialize a json string to object.
	 
	*/
	Object Deserialize(String value);
	/** Deserialize a json string to a strong type object.
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	<T extends class> T Deserialize(String value);
}