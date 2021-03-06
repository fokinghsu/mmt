package lemon.yixin.message.processor;

import lemon.yixin.message.bean.YiXinMessage;

/**
 * YiXin customer message business interface
 * @author lemon
 * @version 1.0
 *
 */
public interface YXMsgProcessor {
	/**
	 * customer message process<BR>
	 * @param mmt_token	is unique in MMT system
	 * @param msg
	 * @return
	 */
	String processBiz(String mmt_token, YiXinMessage msg);
	
}
