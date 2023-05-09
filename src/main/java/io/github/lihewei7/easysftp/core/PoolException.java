package io.github.lihewei7.easysftp.core;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

/**
 * @author lihewei
 */
@SuppressWarnings("serial")
public class PoolException extends NestedRuntimeException {

    public PoolException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}
