package ENode.Infrastructure.Serializing;

/** Represents a serializer to serialize object to byte array.
 
*/
public interface IBinarySerializer
{
	/** Serialize an object to byte array.
	 
	*/
	byte[] Serialize(Object obj);
	/** Deserialize an object from a byte array.
	 
	*/
	Object Deserialize(byte[] data);
	/** Deserialize a typed object from a byte array.
	 
	*/
//C# TO JAVA CONVERTER TODO TASK: The C# 'class' constraint has no equivalent in Java:
	<T extends class> T Deserialize(byte[] data);
}