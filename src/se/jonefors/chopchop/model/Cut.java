/*
 * ChopChop - A very simple 1D cut optimizer with printing capability.
 * Copyright (C) 2015  Oskar Jönefors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.jonefors.chopchop.model;

/**
 * @author Oskar Jönefors
 */

public class Cut {
    private final int length;
    private int quantity;

    /**
     * @param length    An int > 0.
     * @param quantity       An int > 0.
     */
    public Cut(int length, int quantity) {
        if (length <= 0) {
            throw new IllegalArgumentException("Cut: length must be > 0, was " + length);
        } else if (quantity <= 0) {
            throw new IllegalArgumentException("Cut: quantity must be > 0, was " + quantity);
        }
        this.length = length;
        this.quantity = quantity;
    }

    public int getLength() {
        return length;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("setQuantity: quantity must be > 0, was " + quantity);
        }

        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Cut of length " + length + ", repeated " + quantity + " times.";
    }
}
