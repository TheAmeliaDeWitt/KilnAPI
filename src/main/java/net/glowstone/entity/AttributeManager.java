package net.glowstone.entity;

import com.flowpowered.network.Message;

import net.glowstone.net.GlowSession;
import net.glowstone.net.message.play.entity.EntityPropertyMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages the attributes described at https://minecraft.gamepedia.com/Attribute
 */
public class AttributeManager
{
	private static final List<Modifier> EMPTY_LIST = new ArrayList<>();

	private final GlowLivingEntity entity;
	private final Map<String, Property> properties;

	private boolean needsUpdate;

	/**
	 * Create an instance for the given entity.
	 *
	 * @param entity the entity whose attributes will be managed
	 */
	public AttributeManager( GlowLivingEntity entity )
	{
		this.entity = entity;
		properties = new HashMap<>();
		needsUpdate = false;
	}

	/**
	 * Adds an {@link EntityPropertyMessage} with our entity's properties to the given collection of
	 * messages, if the client's snapshot is stale.
	 *
	 * @param messages the message collection to add to
	 */
	public void applyMessages( Collection<Message> messages )
	{
		if ( !needsUpdate )
		{
			return;
		}
		messages.add( new EntityPropertyMessage( entity.entityId, properties ) );
		needsUpdate = false;
	}

	public Map<String, Property> getAllProperties()
	{
		// TODO: Defensive copy
		return properties;
	}

	/**
	 * Returns the base value of the given property.
	 *
	 * @param key the property to look up
	 *
	 * @return the property's base value, or its default value if it's not set
	 */
	public double getPropertyValue( Key key )
	{
		if ( properties.containsKey( key.toString() ) )
		{
			return properties.get( key.toString() ).value;
		}

		return key.def;
	}

	/**
	 * Sends the managed entity's properties to the client, if the client's snapshot is stale.
	 *
	 * @param session the client's session
	 */
	public void sendMessages( GlowSession session )
	{
		if ( !needsUpdate )
		{
			return;
		}
		int id = entity.entityId;
		if ( entity instanceof GlowPlayer )
		{
			GlowPlayer player = ( GlowPlayer ) entity;
			if ( player.getUniqueId().equals( session.getPlayer().getUniqueId() ) )
			{
				id = 0;
			}
		}
		session.send( new EntityPropertyMessage( id, properties ) );
		needsUpdate = false;
	}

	/**
	 * Updates a property and removes all modifiers.
	 *
	 * @param key   the property to update
	 * @param value the new value
	 */
	public void setProperty( Key key, double value )
	{
		setProperty( key.toString(), Math.max( key.min, Math.min( value, key.max ) ), null );
	}

	/**
	 * Updates a property and its modifiers.
	 *
	 * @param key       the property to update
	 * @param value     the new base value
	 * @param modifiers the new and retained modifiers, or {@code null} to remove all modifiers
	 */
	public void setProperty( String key, double value, List<Modifier> modifiers )
	{
		if ( properties.containsKey( key ) )
		{
			properties.get( key ).value = value;
			properties.get( key ).modifiers = modifiers;
		}
		else
		{
			properties.put( key, new Property( value, modifiers == null ? EMPTY_LIST : modifiers ) );
		}

		needsUpdate = true;
	}

	public enum Key
	{
		KEY_MAX_HEALTH( "generic.maxHealth", 20, 1024.0 ),
		KEY_FOLLOW_RANGE( "generic.followRange", 32, 2048 ),
		KEY_KNOCKBACK_RESISTANCE( "generic.knockbackResistance", 0, 1 ),
		KEY_MOVEMENT_SPEED( "generic.movementSpeed", 0.699999988079071, 1024.0 ),
		KEY_ATTACK_DAMAGE( "generic.attackDamage", 2, 2048.0 ),
		KEY_ATTACK_SPEED( "generic.attackSpeed", 4.0, 1024.0 ),
		KEY_ARMOR( "generic.armor", 0.0, 30.0 ),
		KEY_ARMOR_TOUGHNESS( "generic.armorToughness", 0.0, 20.0 ),
		KEY_LUCK( "generic.luck", 0, -1024, 1024 ),
		KEY_HORSE_JUMP_STRENGTH( "horse.jumpStrength", 0.7, 2 ),
		KEY_ZOMBIE_SPAWN_REINFORCEMENTS( "zombie.spawnReinforcements", 0, 1 );

		/**
		 * Default attribute value.
		 */
		private final double def;
		/**
		 * Maximum attribute value.
		 */
		private final double max;
		/**
		 * Minimum attribute value.
		 */
		private final double min;
		/**
		 * Attribute name from https://minecraft.gamepedia.com/Attribute
		 */
		private final String name;

		/**
		 * Creates an instance with a minimum value of 0.
		 *
		 * @param name attribute name
		 * @param def  default value
		 * @param max  maximum value
		 */
		Key( String name, double def, double max )
		{
			this( name, def, 0, max );
		}

		Key( String name, double def, double min, double max )
		{
			this.name = name;
			this.def = def;
			this.min = min;
			this.max = max;
		}

		public double getDef()
		{
			return def;
		}

		public double getMax()
		{
			return max;
		}

		public double getMin()
		{
			return min;
		}

		public String getName()
		{
			return name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	public static class Modifier
	{
		private final double amount;
		private final String name;
		private final byte operation;
		private final UUID uuid;

		public Modifier( String name, UUID uuid, double amount, byte operation )
		{
			this.name = name;
			this.uuid = uuid;
			this.amount = amount;
			this.operation = operation;
		}

		public double getAmount()
		{
			return amount;
		}

		public String getName()
		{
			return name;
		}

		public byte getOperation()
		{
			return operation;
		}

		public UUID getUuid()
		{
			return uuid;
		}
	}

	public static class Property
	{
		private List<Modifier> modifiers;
		private double value;

		public Property( double value, List<Modifier> modifiers )
		{
			this.value = value;
			this.modifiers = modifiers;
		}

		public List<Modifier> getModifiers()
		{
			return modifiers;
		}

		public double getValue()
		{
			return value;
		}
	}
}
