package org.filestore.ejb.file;

import java.util.Set;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class FileServiceBeanSoapHandler implements SOAPHandler<SOAPMessageContext> {

	public boolean handleMessage(SOAPMessageContext ctx) {
		try {
			SOAPMessage message = ctx.getMessage();
			SOAPEnvelope env = message.getSOAPPart().getEnvelope();
			SOAPHeader header = env.getHeader();
			if (header == null) {
				header = env.addHeader();
			}
	
			QName qname = new QName("http://filestore.miage.fr/file", "uuid");
			SOAPHeaderElement hid = header.addHeaderElement(qname);
			hid.setActor(SOAPConstants.URI_SOAP_ACTOR_NEXT);
			hid.addTextNode(UUID.randomUUID().toString());
	
			message.saveChanges();
		} catch ( Exception e ) {
			//
		}
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return false;
	}

	@Override
	public void close(MessageContext context) {
		
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}
}