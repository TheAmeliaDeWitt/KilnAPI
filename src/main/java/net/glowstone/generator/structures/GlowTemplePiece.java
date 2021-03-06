package net.glowstone.generator.structures;

import net.glowstone.generator.objects.RandomItemsContent;
import net.glowstone.generator.objects.RandomItemsContent.RandomAmountItem;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Random;

public abstract class GlowTemplePiece extends GlowStructurePiece
{
	private int depth;
	private int height;
	private int horizPos = -1;
	private int width;

	public GlowTemplePiece()
	{
	}

	/**
	 * Creates a temple piece.
	 *
	 * @param random   the PRNG that will choose the orientation
	 * @param location the root location
	 * @param size     the size as a width-height-depth vector
	 */
	public GlowTemplePiece( Random random, Location location, Vector size )
	{
		super( random, location, size );
		width = size.getBlockX();
		height = size.getBlockY();
		depth = size.getBlockZ();
	}

	protected void adjustHorizPos( World world )
	{
		if ( horizPos >= 0 )
		{
			return;
		}

		int sumY = 0;
		int blockCount = 0;
		for ( int x = boundingBox.getMin().getBlockX(); x <= boundingBox.getMax().getBlockX(); x++ )
		{
			for ( int z = boundingBox.getMin().getBlockZ(); z <= boundingBox.getMax().getBlockZ(); z++ )
			{
				int y = world.getHighestBlockYAt( x, z );
				Material type = world.getBlockAt( x, y - 1, z ).getType();
				while ( ( type == Material.LEAVES || type == Material.LEAVES_2 || type == Material.LOG || type == Material.LOG_2 ) && y > 1 )
				{
					y--;
					type = world.getBlockAt( x, y - 1, z ).getType();
				}
				sumY += Math.max( world.getSeaLevel(), y + 1 );
				blockCount++;
			}
		}
		horizPos = sumY / blockCount;
		boundingBox.offset( new Vector( 0, horizPos - boundingBox.getMin().getBlockY(), 0 ) );
	}

	protected RandomItemsContent getChestContent()
	{
		RandomItemsContent chestContent = new RandomItemsContent();
		chestContent.addItem( new RandomAmountItem( Material.DIAMOND, 1, 3 ), 3 );
		chestContent.addItem( new RandomAmountItem( Material.IRON_INGOT, 1, 5 ), 10 );
		chestContent.addItem( new RandomAmountItem( Material.GOLD_INGOT, 2, 7 ), 15 );
		chestContent.addItem( new RandomAmountItem( Material.EMERALD, 1, 3 ), 2 );
		chestContent.addItem( new RandomAmountItem( Material.BONE, 4, 6 ), 20 );
		chestContent.addItem( new RandomAmountItem( Material.ROTTEN_FLESH, 3, 7 ), 16 );
		chestContent.addItem( new RandomAmountItem( Material.SADDLE, 1, 1 ), 3 );
		chestContent.addItem( new RandomAmountItem( Material.IRON_BARDING, 1, 1 ), 1 );
		chestContent.addItem( new RandomAmountItem( Material.GOLD_BARDING, 1, 1 ), 1 );
		chestContent.addItem( new RandomAmountItem( Material.DIAMOND_BARDING, 1, 1 ), 1 );
		return chestContent;
	}

	public int getDepth()
	{
		return depth;
	}

	public void setDepth( int depth )
	{
		this.depth = depth;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight( int height )
	{
		this.height = height;
	}

	public int getHorizPos()
	{
		return horizPos;
	}

	public void setHorizPos( int horizPos )
	{
		this.horizPos = horizPos;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth( int width )
	{
		this.width = width;
	}
}
