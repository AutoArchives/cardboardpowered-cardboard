package org.cardboardpowered.impl;

import net.minecraft.server.BannedPlayerList;

/**
 * @deprecated Use CraftProfileBanList
 */
@Deprecated
public class ProfileBanList extends CraftProfileBanList {

	public ProfileBanList(BannedPlayerList list) {
		super(list);
	}
}