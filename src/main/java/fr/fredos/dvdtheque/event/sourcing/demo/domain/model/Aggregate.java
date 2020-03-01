package fr.fredos.dvdtheque.event.sourcing.demo.domain.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Collections.emptyList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

public abstract class Aggregate {
    private String id;
    private int baseVersion;
    private List<Event> newEvents;

    protected Aggregate(String id) {
        this(id, emptyList());
    }

    protected Aggregate(String id, List<Event> eventStream) {
        checkNotNull(id);
        checkNotNull(eventStream);
        this.id = id;
        eventStream.forEach(e -> {
            apply(e);
            this.baseVersion = e.getVersion();
        });
        this.newEvents = new ArrayList<>();
    }

    protected void applyNewEvent(Event event) {
        checkArgument(event.getVersion() == getNextVersion(),
                "New event version '%d' does not match expected next version '%d'",
                event.getVersion(), getNextVersion());
        apply(event);
        newEvents.add(event);
    }

    private void apply(Event event) {
        try {
            Method method = this.getClass().getDeclaredMethod("apply", event.getClass());
            method.setAccessible(true);
            method.invoke(this, event);
        } catch (InvocationTargetException e) {
            Throwables.propagate(e.getCause());
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new UnsupportedOperationException(
                    format("Aggregate '%s' doesn't apply event type '%s'", this.getClass(), event.getClass()), e);
        }
    }

    public String getId() {
        return id;
    }

    public int getBaseVersion() {
        return baseVersion;
    }

    public List<Event> getNewEvents() {
        return ImmutableList.copyOf(newEvents);
    }

    protected int getNextVersion() {
        return baseVersion + newEvents.size() + 1;
    }
}
