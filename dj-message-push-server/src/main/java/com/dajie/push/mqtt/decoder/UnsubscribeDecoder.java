package com.dajie.push.mqtt.decoder;

import com.dajie.push.mqtt.MQTTParseUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import com.dajie.push.mqtt.message.AbstractMessage;
import com.dajie.push.mqtt.message.UnsubscribeMessage;

/**
 *
 * @author wills
 */
class UnsubscribeDecoder extends BaseDecoder {

    @Override
    void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //Common decoding part
        in.resetReaderIndex();
        UnsubscribeMessage message = new UnsubscribeMessage();
        if (!decodeCommonHeader(message, in)) {
            in.resetReaderIndex();
            return;
        }
        
        //check qos level
        if (message.getQos() != AbstractMessage.QOSType.LEAST_ONE) {
            throw new CorruptedFrameException("Found an Usubscribe message with qos other than LEAST_ONE, was: " + message.getQos());
        }
            
        int start = in.readerIndex();
        //read  messageIDs
        message.setMessageID(in.readUnsignedShort());
        int readed = in.readerIndex()- start;
        while (readed < message.getRemainingLength()) {
            message.addTopic(MQTTParseUtils.decodeString(in));
            readed = in.readerIndex()- start;
        }
        
        out.add(message);
    }
    
}
