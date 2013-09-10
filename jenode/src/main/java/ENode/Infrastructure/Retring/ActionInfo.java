package ENode.Infrastructure.Retring;

/** A class contains the information of a specific retry action.
 
*/
public class ActionInfo
{
	/** The name of the action.
	 
	*/
	private String privateName;
	public final String getName()
	{
		return privateName;
	}
	private void setName(String value)
	{
		privateName = value;
	}
	/** The action delegate.
	 
	*/
	private Func<Object, Boolean> privateAction;
	public final Func<Object, Boolean> getAction()
	{
		return privateAction;
	}
	private void setAction(Func<Object, Boolean> value)
	{
		privateAction = value;
	}
	/** The parameter data of the action.
	 
	*/
	private Object privateData;
	public final Object getData()
	{
		return privateData;
	}
	private void setData(Object value)
	{
		privateData = value;
	}
	/** The next action of the current action. If the current action complete success, then the next action will be called.
	 
	*/
	private ActionInfo privateNext;
	public final ActionInfo getNext()
	{
		return privateNext;
	}
	private void setNext(ActionInfo value)
	{
		privateNext = value;
	}

	/** Parameterized constructor.
	 
	 @param name
	 @param action
	 @param data
	 @param next
	 @exception ArgumentNullException
	*/
	public ActionInfo(String name, Func<Object, Boolean> action, Object data, ActionInfo next)
	{
		if (name == null)
		{
			throw new ArgumentNullException("name");
		}
		if (action == null)
		{
			throw new ArgumentNullException("action");
		}
		setName(name);
		setAction(action);
		setData(data);
		setNext(next);
	}
}