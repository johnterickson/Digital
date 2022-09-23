/*
 * Copyright (c) 2018 Helmut Neemann.
 * Use of this source code is governed by the GPL v3 license
 * that can be found in the LICENSE file.
 */
package de.neemann.digital.core.extern;

import de.neemann.digital.core.Bits;
import de.neemann.digital.core.Bits.NumberFormatException;
import de.neemann.digital.hdl.hgs.HGSMap;

/**
 * A port for external access
 */
public class Port implements HGSMap {
    private final int bits;
    private final PortType type;
    private final String name;

    /**
     * Type of the port
     */
    public enum PortType {
        /**
         * A single logic bit
         */
        LOGIC_BIT,

        /**
         * A vector of logic bits
         */
        LOGIC_VECTOR,

        /**
         * A vector of logic bits representing a number
         */
        UNSIGNED;
    }

    /**
     * Creates a new port
     *
     * @param name the name
     * @param type the type
     * @param bits the number of bits
     */
    public Port(String name, PortType type, int bits) {
        this.name = name;
        this.type = type;
        this.bits = bits;
    }

    /**
     * Creates a new port
     *
     * @param port the port
     */
    public Port(String port) {
        String[] tokens = port.split(":");

        name = tokens[0];

        if (tokens.length > 1) {
            try {
                bits = (int) Bits.decode(tokens[1]);
            } catch (NumberFormatException e) {
                throw new RuntimeException("invalid bit length: " + tokens[1], e);
            }
        } else {
            bits = 1;
        }

        if (tokens.length > 2) {
            switch (tokens[2]) {
                case "std_logic":
                    if (bits != 1)
                        throw new RuntimeException("std_logic port must have 1 bit.");
                    type = PortType.LOGIC_BIT;
                    break;
                case "std_logic_vector":
                    type = PortType.LOGIC_VECTOR;
                    break;
                case "unsigned":
                    type = PortType.UNSIGNED;
                    break;
                default:
                    throw new RuntimeException("unknown type: " + tokens[2]);
            }
        } else {
            type = bits == 1 ? PortType.LOGIC_BIT : PortType.LOGIC_VECTOR;
        }
    }

    /**
     * @return the number of bits
     */
    public int getBits() {
        return bits;
    }

    /**
     * @return the name of the port
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if (bits == 1)
            return name;
        else
            return name + ":" + bits;
    }

    @Override
    public Object hgsMapGet(String key) {
        switch (key) {
            case "name":
                return name;
            case "bits":
                return bits;
            case "type":
                return type.ordinal() + 1;
            default:
                return null;
        }
    }
}
