package voiceChat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class Client {

	public static void main(String[] args) throws Exception {

		EventLoopGroup group = new NioEventLoopGroup();

		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioDatagramChannel.class)
			.handler(new ClientHandler());

			ChannelFuture f = b.connect("10.156.145.104", 8080).sync();

			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}

	}

}
