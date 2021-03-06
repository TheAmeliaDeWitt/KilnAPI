package net.glowstone.net.codec;

import com.flowpowered.network.Codec;

import net.glowstone.net.GlowBufUtils;
import net.glowstone.net.message.KickMessage;
import net.glowstone.util.TextMessage;

import java.io.IOException;

import io.netty.buffer.ByteBuf;

public final class KickCodec implements Codec<KickMessage>
{
	@Override
	public KickMessage decode( ByteBuf buf ) throws IOException
	{
		TextMessage value = GlowBufUtils.readChat( buf );
		return new KickMessage( value );
	}

	@Override
	public ByteBuf encode( ByteBuf buf, KickMessage message ) throws IOException
	{
		GlowBufUtils.writeChat( buf, message.getText() );
		return buf;
	}
}
