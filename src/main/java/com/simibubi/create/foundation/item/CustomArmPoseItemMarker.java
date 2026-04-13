package com.simibubi.create.foundation.item;

/**
 * Marker for items whose crossbow-like arm pose is applied on the client by
 * {@link com.simibubi.create.foundation.item.render.SimpleCustomRenderer}. Must not reference client-only types so
 * implementing items load on dedicated servers.
 */
public interface CustomArmPoseItemMarker {
}
