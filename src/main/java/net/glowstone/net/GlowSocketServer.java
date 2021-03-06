package net.glowstone.net;

import net.glowstone.GlowServer;

import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public abstract class GlowSocketServer extends GlowNetworkServer
{

	protected final ServerBootstrap bootstrap;
	protected final EventLoopGroup bossGroup;
	protected final EventLoopGroup workerGroup;
	protected Channel channel;

	/**
	 * Creates an instance for the specified server.
	 *
	 * @param server the associated GlowServer
	 * @param latch  The countdown latch used during server startup to wait for network server
	 *               binding.
	 */
	public GlowSocketServer( GlowServer server, CountDownLatch latch )
	{
		super( server, latch );
		boolean epoll = GlowServer.EPOLL;
		boolean kqueue = GlowServer.KQUEUE;
		bossGroup = epoll ? new EpollEventLoopGroup() : kqueue ? new KQueueEventLoopGroup() : new NioEventLoopGroup();
		workerGroup = epoll ? new EpollEventLoopGroup() : kqueue ? new KQueueEventLoopGroup() : new NioEventLoopGroup();
		bootstrap = new ServerBootstrap();

		bootstrap.group( bossGroup, workerGroup ).channel( epoll ? EpollServerSocketChannel.class : kqueue ? KQueueServerSocketChannel.class : NioServerSocketChannel.class ).childOption( ChannelOption.TCP_NODELAY, true ).childOption( ChannelOption.SO_KEEPALIVE, true );
	}

	@Override
	public ChannelFuture bind( InetSocketAddress address )
	{
		ChannelFuture cfuture = this.bootstrap.bind( address ).addListener( future -> {
			if ( future.isSuccess() )
			{
				onBindSuccess( address );
			}
			else
			{
				onBindFailure( address, future.cause() );
			}
		} );
		channel = cfuture.channel();
		return cfuture;
	}

	public Channel getChannel()
	{
		return channel;
	}

	@Override
	public void shutdown()
	{
		channel.close();
		bootstrap.group().shutdownGracefully();
		bootstrap.childGroup().shutdownGracefully();

		try
		{
			bootstrap.group().terminationFuture().sync();
			bootstrap.childGroup().terminationFuture().sync();
		}
		catch ( InterruptedException e )
		{
			GlowServer.logger.log( Level.SEVERE, "Socket server shutdown process interrupted!", e );
		}
	}
}
