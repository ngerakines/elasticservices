package com.socklabs.elasticservices.core;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.socklabs.error.EncodedError;
import com.socklabs.error.ErrorUtil;

import java.util.Map;

/**
 * Created by ngerakines on 2/26/14.
 */
public enum EsError implements EncodedError {
	INVALID_SERVICE_METHOD(1, "No method exists for the requested service."),
	SERVICE_EXECUTION_ERROR(2, "An error occurred processing the message.");

	private static final String[] NAMESPACE_PARTS = new String[] { "ELS", "COR", "ERR" };
	public static final String NAMESPACE = Joiner.on("").join(NAMESPACE_PARTS);

	private static final Map<String, EncodedError> REVERSE_LOOKUP_MAP = Maps.newHashMap();

	static {
		for (final EsError errorError : EsError.values()) {
			Preconditions.checkArgument(!REVERSE_LOOKUP_MAP.containsKey(errorError.code));
			REVERSE_LOOKUP_MAP.put(errorError.code(), errorError);
		}
	}

	private final int number;
	private final String code;
	private final String message;

	EsError(final int errorCode, final String errorMessage) {
		this.number = errorCode;
		this.code = ErrorUtil.hashId(errorCode);
		this.message = errorMessage;
	}

	@Override
	public int id() {
		return number;
	}

	@Override
	public String code() {
		return code;
	}

	@Override
	public String message() {
		return message;
	}

	@Override
	public String namespace() {
		return NAMESPACE;
	}

	@Override
	public String[] namespaceParts() {
		return NAMESPACE_PARTS;
	}

}
