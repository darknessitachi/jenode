package ENode.Domain;

import ENode.Eventing.*;
import ENode.Infrastructure.*;
import ENode.Snapshoting.*;

/** Abstract base aggregate root class with strong type aggregate root id.
 
*/
public abstract class AggregateRoot<TAggregateRootId> extends AggregateRoot implements Serializable
{
	/** Default constructor.
	 
	*/
	protected AggregateRoot()
	{
	}
	/** Parameterized constructor.
	 
	 @param id
	*/
	protected AggregateRoot(TAggregateRootId id)
	{
		super(id.toString());
	}

	/** The strong type id of the aggregate root.
	 
	*/
	public final TAggregateRootId getId()
	{
		return getUniqueId() != null ? Utils.<TAggregateRootId>ConvertType(getUniqueId()) : null;
	}
	public final void setId(TAggregateRootId value)
	{
		setUniqueId(Utils.<String>ConvertType(value));
	}
}