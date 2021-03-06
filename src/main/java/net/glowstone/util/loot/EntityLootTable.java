package net.glowstone.util.loot;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class EntityLootTable
{
	private static final LootItem[] NO_ITEMS = new LootItem[0];
	private final LootRandomValues experience;
	private final LootItem[] items;

	/**
	 * Parses a loot table from JSON.
	 *
	 * @param object a loot table in JSON form
	 */
	public EntityLootTable( JSONObject object )
	{
		if ( object.containsKey( "experience" ) )
		{
			this.experience = new LootRandomValues( ( JSONObject ) object.get( "experience" ) );
		}
		else
		{
			this.experience = null;
		}
		if ( object.containsKey( "items" ) )
		{
			JSONArray array = ( JSONArray ) object.get( "items" );
			this.items = new LootItem[array.size()];
			for ( int i = 0; i < array.size(); i++ )
			{
				JSONObject json = ( JSONObject ) array.get( i );
				this.items[i] = new LootItem( json );
			}
		}
		else
		{
			this.items = NO_ITEMS;
		}
	}

	public LootRandomValues getExperience()
	{
		return experience;
	}

	public LootItem[] getItems()
	{
		return items;
	}
}
