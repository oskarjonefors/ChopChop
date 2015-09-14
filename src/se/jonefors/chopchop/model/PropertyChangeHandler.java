package se.jonefors.chopchop.model;

import java.beans.PropertyChangeListener;

/**
 * @author Oskar Jönefors
 */
public interface PropertyChangeHandler {
    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);
}
