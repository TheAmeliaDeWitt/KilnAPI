package net.glowstone.util.nbt;

/**
 * Represents a single NBT tag.
 */
public abstract class Tag<T>
{
	/**
	 * The type of this tag.
	 */
	private final TagType type;

	public Tag( TagType type )
	{
		this.type = type;
	}

	@Override
	public final boolean equals( Object o )
	{
		if ( this == o )
		{
			return true;
		}
		if ( o == null || getClass() != o.getClass() )
		{
			return false;
		}

		Tag tag = ( Tag ) o;

		return type == tag.type && getValue().equals( tag.getValue() );
	}

	public TagType getType()
	{
		return type;
	}

	/**
	 * Gets the value of this tag.
	 *
	 * @return The value of this tag.
	 */
	public abstract T getValue();

	@Override
	public final int hashCode()
	{
		int result = type.hashCode();
		result = 31 * result + getValue().hashCode();
		return result;
	}

	@Override
	public final String toString()
	{
		StringBuilder builder = new StringBuilder( "TAG_" );
		builder.append( type.getName() ).append( ": " );
		valueToString( builder );
		return builder.toString();
	}

	protected void valueToString( StringBuilder builder )
	{
		builder.append( getValue() );
	}
}

