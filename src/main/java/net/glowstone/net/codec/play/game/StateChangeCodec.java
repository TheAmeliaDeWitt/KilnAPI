package net.glowstone.net.codec.play.game;

import com.flowpowered.network.Codec;

import net.glowstone.net.message.play.game.StateChangeMessage;

import java.io.IOException;

import io.netty.buffer.ByteBuf;

public final class StateChangeCodec implements Codec<StateChangeMessage>
{
	@Override
	public StateChangeMessage decode( ByteBuf buffer ) throws IOException
	{
		int reason = buffer.readByte();
		float value = buffer.readFloat();

		return new StateChangeMessage( reason, value );
	}

	@Override
	public ByteBuf encode( ByteBuf buf, StateChangeMessage message ) throws IOException
	{
		buf.writeByte( message.getReason() );
		buf.writeFloat( message.getValue() );
		return buf;
	}
}
