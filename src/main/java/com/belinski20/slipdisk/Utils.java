package com.belinski20.slipdisk;

import org.bukkit.Location;

import java.io.IOException;

public interface Utils {

    void add(final Location loc);

    boolean contains(String id) throws IOException;

    void remove(final Location loc);

}
