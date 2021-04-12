package org.oddjob.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.arooa.utils.ListSetterHelper;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @oddjob.description Provides a Handler from a List of Handlers.
 *
 * @author rob
 */
public class HandlerListType implements ValueFactory<Handler> {

    /**
     * @oddjob.property
     * @oddjob.description List of Jetty Handlers.
     * @oddjob.required No, but pointless if missing.
     */
    private final List<Handler> handlers = new CopyOnWriteArrayList<>();


    @Override
    public Handler toValue() throws ArooaConversionException {
        HandlerList handlerList = new HandlerList();

        handlerList.setHandlers(handlers.toArray(
                new Handler[0]));

        return handlerList;
    }

    public Handler getHandlers(int index) {
        return handlers.get(index);
    }

    public void setHandlers(int index, Handler handler) {
        new ListSetterHelper<>(handlers).set(index, handler);
    }
}
