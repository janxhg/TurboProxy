/*
 * Copyright (C) 2021-2023 Velocity Contributors
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.turbopowered.proxy.tablist;

import com.turbopowered.api.proxy.Player;
import com.turbopowered.api.proxy.player.TabList;
import com.turbopowered.proxy.protocol.packet.LegacyPlayerListItemPacket;
import com.turbopowered.proxy.protocol.packet.RemovePlayerInfoPacket;
import com.turbopowered.proxy.protocol.packet.UpsertPlayerInfoPacket;

/**
 * Tab list interface with methods for handling player info packets.
 */
public interface InternalTabList extends TabList {

  Player getPlayer();

  default void processLegacy(LegacyPlayerListItemPacket packet) {
  }

  default void processUpdate(UpsertPlayerInfoPacket infoPacket) {
  }

  default void processRemove(RemovePlayerInfoPacket infoPacket) {
  }

  void clearAllSilent();
}
