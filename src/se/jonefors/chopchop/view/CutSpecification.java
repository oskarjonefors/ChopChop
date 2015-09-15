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

package se.jonefors.chopchop.view;

/**
 * A class representing a Cut in the CutTable.
 * The reason that the Cut class from the model package is not used for this purpose is that
 * the length and quantity in the table may be 0 to represent a blank cell, whereas this is not
 * permitted in the model.
 *
 * @author Oskar Jönefors
 */

public class CutSpecification {

    private int length;
    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
