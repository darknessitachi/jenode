package ENode.Infrastructure.Serializing;

/** Represents a serializer to serialize object to string.
 
*/
public interface IStringSerializer
{
	/** Serialize an object to string.
	 
	*/
	String Serialize(Object obj);
	/** Deserialize an object from a string.
	 
	*/
	Object Deserialize(String data);
	/** Deserialize a typed object from a string.
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	<T extends class> T Deserialize(String data);
}