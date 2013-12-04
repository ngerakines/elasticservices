package org.socklabs.elasticservices.examples.calc.service;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.protobuf.Message;
import org.socklabs.elasticservices.core.message.AbstractMessageFactory;
import org.socklabs.elasticservices.examples.calc.CalcServiceProto;

public class CalcMessageFactory extends AbstractMessageFactory {

	public CalcMessageFactory() {
		super(
				Lists.newArrayList(
						CalcServiceProto.Add.class.getName(), CalcServiceProto.Result.class.getName()));
	}

	@Override
	protected Optional<Message> getPrototype(final String messageClass) {
		if (messageClass.endsWith(CalcServiceProto.Add.class.getName())) {
			return Optional.<Message>of(CalcServiceProto.Add.getDefaultInstance());
		} else if (messageClass.endsWith(CalcServiceProto.Result.class.getName())) {
			return Optional.<Message>of(CalcServiceProto.Result.getDefaultInstance());
		}
		return Optional.absent();
	}

}
