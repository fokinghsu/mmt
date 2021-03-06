package lemon.yixin.message.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.thoughtworks.xstream.XStream;

import lemon.shared.MMTContext;
import lemon.shared.api.Message;
import lemon.shared.api.MsgParser;
import lemon.shared.entity.MMTCharset;
import lemon.shared.toolkit.xstream.XStreamHelper;
import lemon.yixin.YiXinException;
import lemon.yixin.config.YiXin;
import lemon.yixin.config.bean.YiXinConfig;
import lemon.yixin.message.bean.YiXinMessage;
import lemon.yixin.message.processor.YXMsgProcessor;

/**
 * A message parser for YiXin
 * @author lemon
 * @version 1.0
 *
 */
public abstract class YXMsgParser implements MsgParser {
	public static final String PREFIX = "YX_";
	private static Log logger = LogFactory.getLog(YXMsgParser.class);
	protected XStream xStream = XStreamHelper.createXMLXStream();
	
	/**
	 * Get message parser
	 * @param msg
	 * @return
	 */
	public static MsgParser getParser(String msg){
		String msgType = getMsgType(msg);
		return (MsgParser) MMTContext.getApplicationContext().getBean(PREFIX + msgType);
	}
	
	/**
	 * Generate replay message
	 * @param mmt_token
	 * @param msg
	 * @return
	 */
	public final String parseMessage(String mmt_token, String msg){
		YiXinMessage message = toMsg(msg);
		// process business
		String retMsg = process(mmt_token, message);
		// build replay message
		return retMsg;
	}
	
	/**
	 * Convert string to message 
	 * @param msg
	 * @return
	 */
	protected abstract YiXinMessage toMsg(String msg);
	
	/**
	 * Convert YiXin message to XML
	 * @param rMsg
	 * @return
	 */
	protected abstract String toXML(Message rMsg);
	
	
	/**
	 * Business process
	 * @param mmt_token
	 * @param msg
	 * @return an string message
	 */
	private String process(String mmt_token, YiXinMessage msg){
		YXMsgProcessor biz = null;
		YiXinConfig cfg = YiXin.getConfig(mmt_token);
		if(null == cfg)
			throw new YiXinException("No customer's configure find.");
		try {
			biz = (YXMsgProcessor) MMTContext.getApplicationContext().getBean(Class.forName(cfg.getBiz_class()));
			return biz.processBiz(mmt_token, msg);
		} catch (ClassNotFoundException e) {
			logger.error("Can't find business implement class: " + cfg.getBiz_class());
			throw new YiXinException("Can't find business implement class: " + cfg.getBiz_class());
		}
	}
	
	/**
	 * Get message type
	 * @param msg
	 * @return
	 */
	private static String getMsgType(String msg) {
		InputStream is = null;
		try {
			try {
				is = new ByteArrayInputStream(msg.getBytes(MMTCharset.LOCAL_CHARSET));
				Document doc = new SAXBuilder().build(is);
				Element e = doc.getRootElement().getChild("MsgType");
				return e.getValue();
			} finally {
				if (null != is)
					is.close();
			}
		} catch (IOException | JDOMException e) {
			logger.error("Can't get message type:" + e.getMessage());
		}
		return null;
	}
}
