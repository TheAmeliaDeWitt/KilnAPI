package net.glowstone.net.message.play.entity;

import com.flowpowered.network.Message;

public final class SpawnLightningStrikeMessage implements Message
{
	private final int id;
	private final int mode;
	private final double x;
	private final double y;
	private final double z;

	public SpawnLightningStrikeMessage( int id, double x, double y, double z )
	{
		this( id, 1, x, y, z );
	}

	public SpawnLightningStrikeMessage( int id, int mode, double x, double y, double z )
	{
		this.id = id;
		this.mode = mode;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getId()
	{
		return id;
	}

	public int getMode()
	{
		return mode;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getZ()
	{
		return z;
	}
}
