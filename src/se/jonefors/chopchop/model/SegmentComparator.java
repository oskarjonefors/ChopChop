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

import java.util.Comparator;

/**
 * Comparator class that sorts Segments by order of their length in descending order.
 *
 * @author Oskar Jönefors
 */

public class SegmentComparator implements Comparator<Segment> {
    @Override
    public int compare(Segment segment, Segment t1) {
                /* Longest first */
        return Integer.compare(t1.getLength(), segment.getLength());
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
