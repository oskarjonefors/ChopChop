package se.jonefors.chopchop.controller;

import java.beans.PropertyChangeListener;

/**
 * @author Oskar Jönefors
 */
interface PropertyChangeHandler {
    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);
}
