package com.polaris.core.component;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The LifeCycleWithListenerManager for generic components.
 */
public abstract class LifeCycleWithListenerManager extends AbstractLifeCycle implements LifeCycleListenerManager{
    private final CopyOnWriteArrayList<LifeCycleListener> _listeners = new CopyOnWriteArrayList<LifeCycleListener>();

    @Override
    public void addLifeCycleListener(LifeCycleListener listener) {
        _listeners.add(listener);
    }

    @Override
    public void removeLifeCycleListener(LifeCycleListener listener) {
        _listeners.remove(listener);
    }
    @Override
    protected void setStarting() {
    	super.setStarting();
    	Iterator<LifeCycleListener> iterator = _listeners.iterator();
		while (iterator.hasNext()) {
			LifeCycleListener listener = iterator.next();
			listener.starting(this);
		}
    }
    @Override
    protected void setStarted() {
    	super.setStarted();
    	Iterator<LifeCycleListener> iterator = _listeners.iterator();
		while (iterator.hasNext()) {
			LifeCycleListener listener = iterator.next();
			listener.started(this);
		}
    }
    @Override
    protected void setStopping() {
    	super.setStopping();
    	Iterator<LifeCycleListener> iterator = _listeners.iterator();
		while (iterator.hasNext()) {
			LifeCycleListener listener = iterator.next();
			listener.stopping(this);
		}
    }
    @Override
    protected void setStopped() {
    	super.setStopped();
    	Iterator<LifeCycleListener> iterator = _listeners.iterator();
		while (iterator.hasNext()) {
			LifeCycleListener listener = iterator.next();
			listener.stopped(this);
			removeLifeCycleListener(listener);
		}
    }
    @Override
    protected void setFailed(Throwable th) {
    	super.setFailed(th);
    	Iterator<LifeCycleListener> iterator = _listeners.iterator();
		while (iterator.hasNext()) {
			LifeCycleListener listener = iterator.next();
			listener.failure(this, th);
		}
    }
}
