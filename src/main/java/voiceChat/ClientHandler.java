package voiceChat;

import java.net.InetSocketAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

public class ClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000.0F, 16, 2, 4, 16000.0F, true);
	DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);
	DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
	TargetDataLine mic;
	SourceDataLine source;
	byte[] buffer;
	
	public ClientHandler() throws Exception {
		mic = (TargetDataLine) AudioSystem.getLine(targetInfo);
		mic.open(format);
		mic.start();
		
		source = (SourceDataLine) AudioSystem.getLine(sourceInfo);
		source.open(format);
		source.start();
	}	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
		System.out.println("channel Active");

		byte buffer[] = new byte[1024];		
		
		while(true) {
			mic.read(buffer, 0, 1024);
			ByteBuf buf = Unpooled.buffer(1024);
			buf.writeBytes(buffer, 0, buffer.length);
			
			ctx.writeAndFlush(new DatagramPacket(buf, new InetSocketAddress("10.156.145.104", 8080)));
		}
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		ByteBuf in = msg.content();
		
		buffer = new byte[in.readableBytes()];
		in.readBytes(buffer);
		source.write(buffer, 0, buffer.length);
	}
}