// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: org/socklabs/elasticservices/examples/calc/calc.proto

package org.socklabs.elasticservices.examples.calc;

public final class CalcServiceProto {
	private CalcServiceProto() {
	}

	public static void registerAllExtensions(
			com.google.protobuf.ExtensionRegistry registry) {
	}

	public interface AddOrBuilder extends com.google.protobuf.MessageOrBuilder {

		// repeated int32 values = 1;
		java.util.List<java.lang.Integer> getValuesList();

		int getValuesCount();

		int getValues(int index);
	}

	public static final class Add extends com.google.protobuf.GeneratedMessage implements AddOrBuilder {
		// Use Add.newBuilder() to construct.
		private Add(Builder builder) {
			super(builder);
		}

		private Add(boolean noInit) {
		}

		private static final Add defaultInstance;

		public static Add getDefaultInstance() {
			return defaultInstance;
		}

		public Add getDefaultInstanceForType() {
			return defaultInstance;
		}

		public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
			return org.socklabs.elasticservices.examples.calc.CalcServiceProto.internal_static_org_socklabs_elasticservices_examples_calc_Add_descriptor;
		}

		protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
			return org.socklabs.elasticservices.examples.calc.CalcServiceProto.internal_static_org_socklabs_elasticservices_examples_calc_Add_fieldAccessorTable;
		}

		// repeated int32 values = 1;
		public static final int VALUES_FIELD_NUMBER = 1;
		private java.util.List<java.lang.Integer> values_;

		public java.util.List<java.lang.Integer> getValuesList() {
			return values_;
		}

		public int getValuesCount() {
			return values_.size();
		}

		public int getValues(int index) {
			return values_.get(index);
		}

		private void initFields() {
			values_ = java.util.Collections.emptyList();
			;
		}

		private byte memoizedIsInitialized = -1;

		public final boolean isInitialized() {
			byte isInitialized = memoizedIsInitialized;
			if (isInitialized != -1) {
				return isInitialized == 1;
			}

			memoizedIsInitialized = 1;
			return true;
		}

		public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
			getSerializedSize();
			for (int i = 0; i < values_.size(); i++) {
				output.writeInt32(1, values_.get(i));
			}
			getUnknownFields().writeTo(output);
		}

		private int memoizedSerializedSize = -1;

		public int getSerializedSize() {
			int size = memoizedSerializedSize;
			if (size != -1) {
				return size;
			}

			size = 0;
			{
				int dataSize = 0;
				for (int i = 0; i < values_.size(); i++) {
					dataSize += com.google.protobuf.CodedOutputStream.computeInt32SizeNoTag(values_.get(i));
				}
				size += dataSize;
				size += 1 * getValuesList().size();
			}
			size += getUnknownFields().getSerializedSize();
			memoizedSerializedSize = size;
			return size;
		}

		private static final long serialVersionUID = 0L;

		@java.lang.Override
		protected java.lang.Object writeReplace() throws java.io.ObjectStreamException {
			return super.writeReplace();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add parseFrom(
				com.google.protobuf.ByteString data) throws com.google.protobuf.InvalidProtocolBufferException {
			return newBuilder().mergeFrom(data).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add parseFrom(
				com.google.protobuf.ByteString data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
			return newBuilder().mergeFrom(data, extensionRegistry).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add parseFrom(byte[] data) throws com.google.protobuf.InvalidProtocolBufferException {
			return newBuilder().mergeFrom(data).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add parseFrom(
				byte[] data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
			return newBuilder().mergeFrom(data, extensionRegistry).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add parseFrom(java.io.InputStream input) throws java.io.IOException {
			return newBuilder().mergeFrom(input).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add parseFrom(
				java.io.InputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return newBuilder().mergeFrom(input, extensionRegistry).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add parseDelimitedFrom(java.io.InputStream input) throws java.io.IOException {
			Builder builder = newBuilder();
			if (builder.mergeDelimitedFrom(input)) {
				return builder.buildParsed();
			} else {
				return null;
			}
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add parseDelimitedFrom(
				java.io.InputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			Builder builder = newBuilder();
			if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
				return builder.buildParsed();
			} else {
				return null;
			}
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add parseFrom(
				com.google.protobuf.CodedInputStream input) throws java.io.IOException {
			return newBuilder().mergeFrom(input).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add parseFrom(
				com.google.protobuf.CodedInputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return newBuilder().mergeFrom(input, extensionRegistry).buildParsed();
		}

		public static Builder newBuilder() {
			return Builder.create();
		}

		public Builder newBuilderForType() {
			return newBuilder();
		}

		public static Builder newBuilder(org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add prototype) {
			return newBuilder().mergeFrom(prototype);
		}

		public Builder toBuilder() {
			return newBuilder(this);
		}

		@java.lang.Override
		protected Builder newBuilderForType(
				com.google.protobuf.GeneratedMessage.BuilderParent parent) {
			Builder builder = new Builder(parent);
			return builder;
		}

		public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<Builder> implements org.socklabs.elasticservices.examples.calc.CalcServiceProto.AddOrBuilder {
			public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
				return org.socklabs.elasticservices.examples.calc.CalcServiceProto.internal_static_org_socklabs_elasticservices_examples_calc_Add_descriptor;
			}

			protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
				return org.socklabs.elasticservices.examples.calc.CalcServiceProto.internal_static_org_socklabs_elasticservices_examples_calc_Add_fieldAccessorTable;
			}

			// Construct using org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add.newBuilder()
			private Builder() {
				maybeForceBuilderInitialization();
			}

			private Builder(BuilderParent parent) {
				super(parent);
				maybeForceBuilderInitialization();
			}

			private void maybeForceBuilderInitialization() {
				if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
				}
			}

			private static Builder create() {
				return new Builder();
			}

			public Builder clear() {
				super.clear();
				values_ = java.util.Collections.emptyList();
				;
				bitField0_ = (bitField0_ & ~0x00000001);
				return this;
			}

			public Builder clone() {
				return create().mergeFrom(buildPartial());
			}

			public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
				return org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add.getDescriptor();
			}

			public org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add getDefaultInstanceForType() {
				return org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add.getDefaultInstance();
			}

			public org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add build() {
				org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add result = buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(result);
				}
				return result;
			}

			private org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add buildParsed() throws com.google.protobuf.InvalidProtocolBufferException {
				org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add result = buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(
							result).asInvalidProtocolBufferException();
				}
				return result;
			}

			public org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add buildPartial() {
				org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add result = new org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add(
						this);
				int from_bitField0_ = bitField0_;
				if (((bitField0_ & 0x00000001) == 0x00000001)) {
					values_ = java.util.Collections.unmodifiableList(values_);
					bitField0_ = (bitField0_ & ~0x00000001);
				}
				result.values_ = values_;
				onBuilt();
				return result;
			}

			public Builder mergeFrom(com.google.protobuf.Message other) {
				if (other instanceof org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add) {
					return mergeFrom((org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add) other);
				} else {
					super.mergeFrom(other);
					return this;
				}
			}

			public Builder mergeFrom(org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add other) {
				if (other == org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add.getDefaultInstance()) {
					return this;
				}
				if (!other.values_.isEmpty()) {
					if (values_.isEmpty()) {
						values_ = other.values_;
						bitField0_ = (bitField0_ & ~0x00000001);
					} else {
						ensureValuesIsMutable();
						values_.addAll(other.values_);
					}
					onChanged();
				}
				this.mergeUnknownFields(other.getUnknownFields());
				return this;
			}

			public final boolean isInitialized() {
				return true;
			}

			public Builder mergeFrom(
					com.google.protobuf.CodedInputStream input,
					com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
				com.google.protobuf.UnknownFieldSet.Builder unknownFields = com.google
						.protobuf
						.UnknownFieldSet
						.newBuilder(
								this.getUnknownFields());
				while (true) {
					int tag = input.readTag();
					switch (tag) {
						case 0:
							this.setUnknownFields(unknownFields.build());
							onChanged();
							return this;
						default: {
							if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
								this.setUnknownFields(unknownFields.build());
								onChanged();
								return this;
							}
							break;
						}
						case 8: {
							ensureValuesIsMutable();
							values_.add(input.readInt32());
							break;
						}
						case 10: {
							int length = input.readRawVarint32();
							int limit = input.pushLimit(length);
							while (input.getBytesUntilLimit() > 0) {
								addValues(input.readInt32());
							}
							input.popLimit(limit);
							break;
						}
					}
				}
			}

			private int bitField0_;

			// repeated int32 values = 1;
			private java.util.List<java.lang.Integer> values_ = java.util.Collections.emptyList();
			;

			private void ensureValuesIsMutable() {
				if (!((bitField0_ & 0x00000001) == 0x00000001)) {
					values_ = new java.util.ArrayList<java.lang.Integer>(values_);
					bitField0_ |= 0x00000001;
				}
			}

			public java.util.List<java.lang.Integer> getValuesList() {
				return java.util.Collections.unmodifiableList(values_);
			}

			public int getValuesCount() {
				return values_.size();
			}

			public int getValues(int index) {
				return values_.get(index);
			}

			public Builder setValues(
					int index, int value) {
				ensureValuesIsMutable();
				values_.set(index, value);
				onChanged();
				return this;
			}

			public Builder addValues(int value) {
				ensureValuesIsMutable();
				values_.add(value);
				onChanged();
				return this;
			}

			public Builder addAllValues(
					java.lang.Iterable<? extends java.lang.Integer> values) {
				ensureValuesIsMutable();
				super.addAll(values, values_);
				onChanged();
				return this;
			}

			public Builder clearValues() {
				values_ = java.util.Collections.emptyList();
				;
				bitField0_ = (bitField0_ & ~0x00000001);
				onChanged();
				return this;
			}

			// @@protoc_insertion_point(builder_scope:org.socklabs.elasticservices.examples.calc.Add)
		}

		static {
			defaultInstance = new Add(true);
			defaultInstance.initFields();
		}

		// @@protoc_insertion_point(class_scope:org.socklabs.elasticservices.examples.calc.Add)
	}

	public interface ResultOrBuilder extends com.google.protobuf.MessageOrBuilder {

		// optional int32 value = 1;
		boolean hasValue();

		int getValue();
	}

	public static final class Result extends com.google.protobuf.GeneratedMessage implements ResultOrBuilder {
		// Use Result.newBuilder() to construct.
		private Result(Builder builder) {
			super(builder);
		}

		private Result(boolean noInit) {
		}

		private static final Result defaultInstance;

		public static Result getDefaultInstance() {
			return defaultInstance;
		}

		public Result getDefaultInstanceForType() {
			return defaultInstance;
		}

		public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
			return org.socklabs.elasticservices.examples.calc.CalcServiceProto.internal_static_org_socklabs_elasticservices_examples_calc_Result_descriptor;
		}

		protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
			return org.socklabs.elasticservices.examples.calc.CalcServiceProto.internal_static_org_socklabs_elasticservices_examples_calc_Result_fieldAccessorTable;
		}

		private int bitField0_;
		// optional int32 value = 1;
		public static final int VALUE_FIELD_NUMBER = 1;
		private int value_;

		public boolean hasValue() {
			return ((bitField0_ & 0x00000001) == 0x00000001);
		}

		public int getValue() {
			return value_;
		}

		private void initFields() {
			value_ = 0;
		}

		private byte memoizedIsInitialized = -1;

		public final boolean isInitialized() {
			byte isInitialized = memoizedIsInitialized;
			if (isInitialized != -1) {
				return isInitialized == 1;
			}

			memoizedIsInitialized = 1;
			return true;
		}

		public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
			getSerializedSize();
			if (((bitField0_ & 0x00000001) == 0x00000001)) {
				output.writeInt32(1, value_);
			}
			getUnknownFields().writeTo(output);
		}

		private int memoizedSerializedSize = -1;

		public int getSerializedSize() {
			int size = memoizedSerializedSize;
			if (size != -1) {
				return size;
			}

			size = 0;
			if (((bitField0_ & 0x00000001) == 0x00000001)) {
				size += com.google.protobuf.CodedOutputStream.computeInt32Size(1, value_);
			}
			size += getUnknownFields().getSerializedSize();
			memoizedSerializedSize = size;
			return size;
		}

		private static final long serialVersionUID = 0L;

		@java.lang.Override
		protected java.lang.Object writeReplace() throws java.io.ObjectStreamException {
			return super.writeReplace();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result parseFrom(
				com.google.protobuf.ByteString data) throws com.google.protobuf.InvalidProtocolBufferException {
			return newBuilder().mergeFrom(data).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result parseFrom(
				com.google.protobuf.ByteString data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
			return newBuilder().mergeFrom(data, extensionRegistry).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result parseFrom(byte[] data) throws com.google.protobuf.InvalidProtocolBufferException {
			return newBuilder().mergeFrom(data).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result parseFrom(
				byte[] data,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
			return newBuilder().mergeFrom(data, extensionRegistry).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result parseFrom(java.io.InputStream input) throws java.io.IOException {
			return newBuilder().mergeFrom(input).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result parseFrom(
				java.io.InputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return newBuilder().mergeFrom(input, extensionRegistry).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result parseDelimitedFrom(java.io.InputStream input) throws java.io.IOException {
			Builder builder = newBuilder();
			if (builder.mergeDelimitedFrom(input)) {
				return builder.buildParsed();
			} else {
				return null;
			}
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result parseDelimitedFrom(
				java.io.InputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			Builder builder = newBuilder();
			if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
				return builder.buildParsed();
			} else {
				return null;
			}
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result parseFrom(
				com.google.protobuf.CodedInputStream input) throws java.io.IOException {
			return newBuilder().mergeFrom(input).buildParsed();
		}

		public static org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result parseFrom(
				com.google.protobuf.CodedInputStream input,
				com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
			return newBuilder().mergeFrom(input, extensionRegistry).buildParsed();
		}

		public static Builder newBuilder() {
			return Builder.create();
		}

		public Builder newBuilderForType() {
			return newBuilder();
		}

		public static Builder newBuilder(org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result prototype) {
			return newBuilder().mergeFrom(prototype);
		}

		public Builder toBuilder() {
			return newBuilder(this);
		}

		@java.lang.Override
		protected Builder newBuilderForType(
				com.google.protobuf.GeneratedMessage.BuilderParent parent) {
			Builder builder = new Builder(parent);
			return builder;
		}

		public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<Builder> implements org.socklabs.elasticservices.examples.calc.CalcServiceProto.ResultOrBuilder {
			public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
				return org.socklabs.elasticservices.examples.calc.CalcServiceProto.internal_static_org_socklabs_elasticservices_examples_calc_Result_descriptor;
			}

			protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
				return org.socklabs.elasticservices.examples.calc.CalcServiceProto.internal_static_org_socklabs_elasticservices_examples_calc_Result_fieldAccessorTable;
			}

			// Construct using org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result.newBuilder()
			private Builder() {
				maybeForceBuilderInitialization();
			}

			private Builder(BuilderParent parent) {
				super(parent);
				maybeForceBuilderInitialization();
			}

			private void maybeForceBuilderInitialization() {
				if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
				}
			}

			private static Builder create() {
				return new Builder();
			}

			public Builder clear() {
				super.clear();
				value_ = 0;
				bitField0_ = (bitField0_ & ~0x00000001);
				return this;
			}

			public Builder clone() {
				return create().mergeFrom(buildPartial());
			}

			public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
				return org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result.getDescriptor();
			}

			public org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result getDefaultInstanceForType() {
				return org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result.getDefaultInstance();
			}

			public org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result build() {
				org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result result = buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(result);
				}
				return result;
			}

			private org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result buildParsed() throws com.google.protobuf.InvalidProtocolBufferException {
				org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result result = buildPartial();
				if (!result.isInitialized()) {
					throw newUninitializedMessageException(
							result).asInvalidProtocolBufferException();
				}
				return result;
			}

			public org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result buildPartial() {
				org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result result = new org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result(
						this);
				int from_bitField0_ = bitField0_;
				int to_bitField0_ = 0;
				if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
					to_bitField0_ |= 0x00000001;
				}
				result.value_ = value_;
				result.bitField0_ = to_bitField0_;
				onBuilt();
				return result;
			}

			public Builder mergeFrom(com.google.protobuf.Message other) {
				if (other instanceof org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result) {
					return mergeFrom((org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result) other);
				} else {
					super.mergeFrom(other);
					return this;
				}
			}

			public Builder mergeFrom(org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result other) {
				if (other == org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result.getDefaultInstance()) {
					return this;
				}
				if (other.hasValue()) {
					setValue(other.getValue());
				}
				this.mergeUnknownFields(other.getUnknownFields());
				return this;
			}

			public final boolean isInitialized() {
				return true;
			}

			public Builder mergeFrom(
					com.google.protobuf.CodedInputStream input,
					com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
				com.google.protobuf.UnknownFieldSet.Builder unknownFields = com.google
						.protobuf
						.UnknownFieldSet
						.newBuilder(
								this.getUnknownFields());
				while (true) {
					int tag = input.readTag();
					switch (tag) {
						case 0:
							this.setUnknownFields(unknownFields.build());
							onChanged();
							return this;
						default: {
							if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
								this.setUnknownFields(unknownFields.build());
								onChanged();
								return this;
							}
							break;
						}
						case 8: {
							bitField0_ |= 0x00000001;
							value_ = input.readInt32();
							break;
						}
					}
				}
			}

			private int bitField0_;

			// optional int32 value = 1;
			private int value_;

			public boolean hasValue() {
				return ((bitField0_ & 0x00000001) == 0x00000001);
			}

			public int getValue() {
				return value_;
			}

			public Builder setValue(int value) {
				bitField0_ |= 0x00000001;
				value_ = value;
				onChanged();
				return this;
			}

			public Builder clearValue() {
				bitField0_ = (bitField0_ & ~0x00000001);
				value_ = 0;
				onChanged();
				return this;
			}

			// @@protoc_insertion_point(builder_scope:org.socklabs.elasticservices.examples.calc.Result)
		}

		static {
			defaultInstance = new Result(true);
			defaultInstance.initFields();
		}

		// @@protoc_insertion_point(class_scope:org.socklabs.elasticservices.examples.calc.Result)
	}

	private static com.google.protobuf.Descriptors.Descriptor internal_static_org_socklabs_elasticservices_examples_calc_Add_descriptor;
	private static com.google.protobuf.GeneratedMessage.FieldAccessorTable internal_static_org_socklabs_elasticservices_examples_calc_Add_fieldAccessorTable;
	private static com.google.protobuf.Descriptors.Descriptor internal_static_org_socklabs_elasticservices_examples_calc_Result_descriptor;
	private static com.google.protobuf.GeneratedMessage.FieldAccessorTable internal_static_org_socklabs_elasticservices_examples_calc_Result_fieldAccessorTable;

	public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
		return descriptor;
	}

	private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

	static {
		java.lang.String[] descriptorData = {
				"\n5org/socklabs/elasticservices/examples/" +
						"calc/calc.proto\022*org.socklabs.elasticser" +
						"vices.examples.calc\"\025\n\003Add\022\016\n\006values\030\001 \003" +
						"(\005\"\027\n\006Result\022\r\n\005value\030\001 \001(\005B@\n*org.sockl" +
						"abs.elasticservices.examples.calcB\020CalcS" +
						"erviceProtoH\001"};
		com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
			public com.google.protobuf.ExtensionRegistry assignDescriptors(
					com.google.protobuf.Descriptors.FileDescriptor root) {
				descriptor = root;
				internal_static_org_socklabs_elasticservices_examples_calc_Add_descriptor = getDescriptor().getMessageTypes()
						.get(0);
				internal_static_org_socklabs_elasticservices_examples_calc_Add_fieldAccessorTable = new com.google.protobuf.GeneratedMessage.FieldAccessorTable(
						internal_static_org_socklabs_elasticservices_examples_calc_Add_descriptor,
						new java.lang.String[]{"Values",},
						org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add.class,
						org.socklabs.elasticservices.examples.calc.CalcServiceProto.Add.Builder.class);
				internal_static_org_socklabs_elasticservices_examples_calc_Result_descriptor = getDescriptor().getMessageTypes()
						.get(1);
				internal_static_org_socklabs_elasticservices_examples_calc_Result_fieldAccessorTable = new com.google.protobuf.GeneratedMessage.FieldAccessorTable(
						internal_static_org_socklabs_elasticservices_examples_calc_Result_descriptor,
						new java.lang.String[]{"Value",},
						org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result.class,
						org.socklabs.elasticservices.examples.calc.CalcServiceProto.Result.Builder.class);
				return null;
			}
		};
		com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(
				descriptorData, new com.google.protobuf.Descriptors.FileDescriptor[]{
		}, assigner);
	}

	// @@protoc_insertion_point(outer_class_scope)
}
