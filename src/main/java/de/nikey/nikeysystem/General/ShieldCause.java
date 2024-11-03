package de.nikey.nikeysystem.General;

public enum ShieldCause {
    /**
     * Resource pack isn't allowed to be downloaded
     */
    RESOURCE_PACK_DOWNLOAD,
    /**
     * Resource pack isn't allowed to be cleared
     */
    RESOURCE_PACK_CLEAR,
    /**
     * Resource pack isn't allowed to be removed
     */
    RESOURCE_PACK_REMOVE,
    /**
     * Effect target isn't allowed to be changed
     */
    EFFECT,
    /**
     * Adding item isn't allowed to the target
     */
    INVENTORY_ADD_ITEM,
    /**
     * Removing item isn't allowed to the target
     */
    INVENTORY_REMOVING_ITEM,
    /**
     * Opening target inventory isn't allowed
     */
    INVENTORY_OPEN_INVENTORY,
    /**
     * Opening target ender chest isn't allowed
     */
    INVENTORY_OPEN_ENDERCHEST,
    /**
     * Opening target equipment isn't allowed
     */
    INVENTORY_OPEN_EQUIPMENT,
    /**
     * Getting target location isn't allowed
     */
    LOCATION_GET_LOCATION,
    /**
     * Teleporting to target isn't allowed
     */
    TELEPORT,
    /**
     * Lastseen target isn't allowed
     */
    LASTSEEN,
    /**
     * Muting target isn't allowed
     */
    MUTE,
    /**
     * Unmuting target isn't allowed
     */
    UNMUTE,
    /**
     * Toggle muting target isn't allowed
     */
    TOGGLE_MUTE,
    /**
     * Toggle permission on target isn't allowed
     */
    TOGGLE_PERMISSION,
    /**
     * Skin reset on target isn't allowed
     */
    SKIN_RESET,
    /**
     * Skin setting on target isn't allowed
     */
    SKIN_SET,
    /**
     * Playing a sound for all contains a disallowed target
     */
    SOUND_PLAY_ALL,
    /**
     * Playing a sound for a disallowed target
     */
    SOUND_PLAY,
    /**
     * Stopping all sound for a disallowed target
     */
    SOUND_STOPALL,
    /**
     * Queuing a sound for a disallowed target
     */
    SOUND_QUEUE,
    /**
     * Clearing the queue for a disallowed target
     */
    SOUND_QUEUE_CLEAR,
    /**
     * Removing a song from the queue for a disallowed target
     */
    SOUND_QUEUE_REMOVE,
    /**
     * Changing stats for a disallowed target
     */
    STATS_CHANGE,
    /**
     * Blocking a command for a disallowed target
     */
    COMMAND_BLOCK_PLAYER,
    /**
     * Executing a command for a disallowed target
     */
    COMMAND_EXECUTEAS,
    /**
     * Toggling the tpsbar for a disallowed target
     */
    PERFORMANCE_TPSBAR,
    /**
     * Toggling always hide for a disallowed target
     */
    HIDE_HIDE,
    /**
     * Toggling hide immunity for a disallowed target
     */
    HIDE_IMMUNITY
}
