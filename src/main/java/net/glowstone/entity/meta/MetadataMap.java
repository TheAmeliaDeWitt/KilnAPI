package net.glowstone.entity.meta;

import com.google.common.collect.ImmutableList;

import net.glowstone.util.DynamicallyTypedMapWithFloats;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A map for entity metadata.
 */
public class MetadataMap implements DynamicallyTypedMapWithFloats<MetadataIndex>
{
	private final List<Entry> changes = new ArrayList<>( 4 );
	private final Class<? extends Entity> entityClass;
	private final Map<MetadataIndex, Object> map = new EnumMap<>( MetadataIndex.class );
	public MetadataMap( Class<? extends Entity> entityClass )
	{
		this.entityClass = entityClass;
		set( MetadataIndex.STATUS, 0 );  // all entities have to have at least this
	}

	public boolean containsKey( MetadataIndex index )
	{
		return map.containsKey( index );
	}

	public Object get( MetadataIndex index )
	{
		return map.get( index );
	}

	@SuppressWarnings( "unchecked" )
	private <T> T get( MetadataIndex index, MetadataType expected, T def )
	{
		if ( index.getType() != expected )
		{
			throw new IllegalArgumentException( "Cannot get " + index + ": is " + index.getType() + ", not " + expected );
		}
		T t = ( T ) map.get( index );
		if ( t == null )
		{
			return def;
		}
		return t;
	}

	public boolean getBit( MetadataIndex index, int bit )
	{
		return ( getNumber( index ).intValue() & bit ) != 0;
	}

	@Override
	public boolean getBoolean( MetadataIndex index )
	{
		return get( index, MetadataType.BOOLEAN, false );
	}

	public byte getByte( MetadataIndex index )
	{
		return get( index, MetadataType.BYTE, ( byte ) 0 );
	}

	public List<Entry> getChanges()
	{
		Collections.sort( changes );
		return ImmutableList.copyOf( changes );
	}

	/**
	 * Returns a list containing copies of all the entries.
	 *
	 * @return a list containing copies of all the entries
	 */
	public List<Entry> getEntryList()
	{
		List<Entry> result = new ArrayList<>( map.size() );
		result.addAll( map.entrySet().stream().map( entry -> new Entry( entry.getKey(), entry.getValue() ) ).collect( Collectors.toList() ) );
		Collections.sort( result );
		return result;
	}

	@Override
	public float getFloat( MetadataIndex index )
	{
		return get( index, MetadataType.FLOAT, 0f );
	}

	@Override
	public int getInt( MetadataIndex index )
	{
		return get( index, MetadataType.INT, 0 );
	}

	public ItemStack getItem( MetadataIndex index )
	{
		return get( index, MetadataType.ITEM, null );
	}

	/**
	 * Returns the numeric value of a metadata field.
	 *
	 * @param index the field to look up
	 *
	 * @return the numeric value
	 *
	 * @throws IllegalArgumentException if the value doesn't exist or isn't numeric
	 */
	public Number getNumber( MetadataIndex index )
	{
		if ( !containsKey( index ) )
		{
			return 0;
		}
		Object o = get( index );
		if ( !( o instanceof Number ) )
		{
			throw new IllegalArgumentException( "Index " + index + " is of non-number type " + index.getType() );
		}
		return ( Number ) o;
	}

	/**
	 * Gets the optional position value for the given MetadataIndex.
	 *
	 * @param index the MetadataIndex of the optional position
	 *
	 * @return the position value as a BlockVector, null if the value is not present
	 */
	public BlockVector getOptPosition( MetadataIndex index )
	{
		return get( index, MetadataType.OPTPOSITION, null );
	}

	@Override
	public String getString( MetadataIndex index )
	{
		return get( index, MetadataType.STRING, null );
	}

	public void resetChanges()
	{
		changes.clear();
	}

	/**
	 * Sets the value of a metadata field.
	 *
	 * @param index the field to set
	 * @param value the new value
	 */
	public void set( MetadataIndex index, Object value )
	{
		// take numbers down to the correct precision
		if ( value != null )
		{
			if ( value instanceof Number )
			{
				Number n = ( Number ) value;
				switch ( index.getType() )
				{
					case BYTE:
						value = n.byteValue();
						break;
					case INT:
						value = n.intValue();
						break;
					case FLOAT:
						value = n.floatValue();
						break;
					default:
						// do nothing
				}
			}
			if ( !index.getType().getDataType().isAssignableFrom( value.getClass() ) )
			{
				throw new IllegalArgumentException( "Cannot assign " + value + " to " + index + ", expects " + index.getType() );
			}
			if ( !index.appliesTo( entityClass ) )
			{
				throw new IllegalArgumentException( "Index " + index + " does not apply to " + entityClass.getSimpleName() + ", only " + index.getAppliesTo().getSimpleName() );
			}
		}

		Object prev = map.put( index, value );
		if ( !Objects.equals( prev, value ) )
		{
			changes.add( new Entry( index, value ) );
		}
	}

	/**
	 * Sets or clears bits in an integer field.
	 *
	 * @param index  the field to update
	 * @param bit    a mask of the bits to set or clear
	 * @param status true to set; false to clear
	 */
	public void setBit( MetadataIndex index, int bit, boolean status )
	{
		if ( status )
		{
			set( index, getNumber( index ).intValue() | bit );
		}
		else
		{
			set( index, getNumber( index ).intValue() & ~bit );
		}
	}

	@Override
	public String toString()
	{
		return MetadataMap.class.getSimpleName() + "{entityClass=" + entityClass + ", map=" + map.keySet() + "}";
	}

	public static class Entry implements Comparable<Entry>
	{
		public final MetadataIndex index;
		public final Object value;

		public Entry( MetadataIndex index, Object value )
		{
			this.index = index;
			this.value = value;
		}

		@Override
		public int compareTo( Entry o )
		{
			return o.index.getIndex() - index.getIndex();
		}

		public MetadataIndex getIndex()
		{
			return index;
		}

		public Object getValue()
		{
			return value;
		}

		@Override
		public String toString()
		{
			return index + "=" + value;
		}
	}
}
