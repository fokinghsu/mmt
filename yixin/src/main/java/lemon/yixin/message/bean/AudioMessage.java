package lemon.yixin.message.bean;

import lemon.shared.toolkit.xstream.annotations.XStreamProcessCDATA;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Voice message
 * 
 * @author lemon
 * @version 1.0
 * 
 */
@XStreamAlias("xml")
@XStreamProcessCDATA
public class AudioMessage extends MediaMessage {
	public AudioMessage() {
		super(MsgType.AUDIO, "audio/aac");
	}

}
