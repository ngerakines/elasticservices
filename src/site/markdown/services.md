# Services

## Creating Services

To create a service, you'll need 3 things:

* Message definitions of the messages sent to and consumed by the service.
* An implementation of the MessageFactory class that composes the messages that is consumed by the service.
* An implementation of the Service interface that is the service being provided.

An example of this can be found in the examples/calc-service project.

In the resources folder (src/main/resources/com/socklabs/elasticservices/examples/calc) is the calc.proto file that defines the messages used.

```protobuf
package com.socklabs.elasticservices.examples.calc;

option optimize_for = SPEED;
option java_package = "com.socklabs.elasticservices.examples.calc";
option java_outer_classname = "CalcServiceProto";

message Add {
	repeated int32 values = 1;
}

message Subtract {
	repeated int32 values = 1;
}

message Result {
	optional int32 value = 1;
}
```

The CalcMessageFactory composes those messages with the help of the AbstractMessageFactory:

```java
public class CalcMessageFactory extends AbstractMessageFactory {

	public CalcMessageFactory() {
		super(Lists.newArrayList(CalcServiceProto.Add.class.getName(), CalcServiceProto.Result.class.getName()));
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
```

Lastly, the service is implemented through the CalcService class.

```java
public class CalcService extends AbstractService {

	private final ServiceRegistry serviceRegistry;
	private final ToggleFeature toggleFeature;

	private final MessageFactory calcMessageFactory;

	public CalcService(
			final ServiceProto.ServiceRef serviceRef,
			final ServiceRegistry serviceRegistry,
			final ToggleFeature toggleFeature) {
		super(serviceRef);
		this.serviceRegistry = serviceRegistry;
		this.toggleFeature = toggleFeature;
		this.calcMessageFactory = new CalcMessageFactory();
	}

	@Override
	public List<MessageFactory> getMessageFactories() {
		return ImmutableList.of(calcMessageFactory);
	}

	@Override
	public void handleMessage(final MessageController controller, final Message message) {
		final Optional<DateTime> expiresOptional = controller.getExpires();
		if (expiresOptional.isPresent()) {
			final DateTime expires = expiresOptional.get();
			if (DateTime.now().isAfter(expires)) {
				return;
			}
		}
		if (message instanceof CalcServiceProto.Subtract && toggleFeature.isEnabled()) {
			final CalcServiceProto.Subtract subtractMessage = (CalcServiceProto.Subtract) message;
			int result = 0;
			if (subtractMessage.getValuesCount() > 0) {
				result = subtractMessage.getValues(0);
				if (subtractMessage.getValuesCount() > 1) {
					for (int i = 1; i < subtractMessage.getValuesCount(); i++) {
						result -= subtractMessage.getValues(i);
					}
				}
			}
			final CalcServiceProto.Result resultMessage =
					CalcServiceProto.Result.newBuilder().setValue(result).build();
			serviceRegistry.reply(
					controller,
					getServiceRef(),
					resultMessage,
					ContentTypes.fromJsonClass(CalcServiceProto.Result.class));
		}
		if (message instanceof CalcServiceProto.Add) {
			int sum = 0;
			for (final Integer value : ((CalcServiceProto.Add) message).getValuesList()) {
				sum += value;
			}
			final CalcServiceProto.Result result =
					CalcServiceProto.Result.newBuilder().setValue(sum).build();
			serviceRegistry.reply(
					controller,
					getServiceRef(),
					result,
					ContentTypes.fromJsonClass(CalcServiceProto.Result.class));
		}
	}

}
```
