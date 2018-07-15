package voiceChat;

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

public class AppHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000.0F, 16, 2, 4, 16000.0F, true);
	DataLine.Info sourceInfo = new DataLine.Info(SourceDataLine.class, format);
	DataLine.Info targetInfo = new DataLine.Info(TargetDataLine.class, format);

	byte[] buffer;
	SourceDataLine source;
	TargetDataLine target;

	public AppHandler() throws Exception {
		source = (SourceDataLine) AudioSystem.getLine(sourceInfo);
		source.open(format);
		source.start();

		target = (TargetDataLine) AudioSystem.getLine(targetInfo);
		target.open(format);
		target.start();
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		System.out.println("read");

		ByteBuf in = Unpooled.buffer();
		ByteBuf out = Unpooled.buffer();

		in = msg.content();
		buffer = new byte[in.readableBytes()];
		in.readBytes(buffer);
		source.write(buffer, 0, buffer.length);

		target.read(buffer, 0, buffer.length);
		out.writeBytes(buffer, 0, buffer.length);

		ctx.writeAndFlush(new DatagramPacket(out, msg.sender()));
	}

}