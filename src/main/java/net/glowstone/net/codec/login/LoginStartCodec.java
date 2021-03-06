package net.glowstone.net.codec.login;

import com.flowpowered.network.Codec;
import com.flowpowered.network.util.ByteBufUtils;

import net.glowstone.net.message.login.LoginStartMessage;

import java.io.IOException;

import io.netty.buffer.ByteBuf;

public final class LoginStartCodec implements Codec<LoginStartMessage>
{
	@Override
	public LoginStartMessage decode( ByteBuf buffer ) throws IOException
	{
		return new LoginStartMessage( ByteBufUtils.readUTF8( buffer ) );
	}

	@Override
	public ByteBuf encode( ByteBuf buf, LoginStartMessage message ) throws IOException
	{
		ByteBufUtils.writeUTF8( buf, message.getUsername() );
		return buf;
	}
}
