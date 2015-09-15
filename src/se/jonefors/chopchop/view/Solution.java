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

import se.jonefors.chopchop.model.Segment;

import java.util.List;

/**
 * A class representing a solution: A list of segments and a label for the problem.
 *
 * @author Oskar Jönefors
 */

public class Solution {

    private final List<Segment> segments;
    private final String label;

    public Solution(List<Segment> segments, String label) {
        this.segments = segments;
        this.label = label;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public String getLabel() {
        return label;
    }

}
