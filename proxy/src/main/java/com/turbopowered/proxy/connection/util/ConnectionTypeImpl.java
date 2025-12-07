/*
 * Copyright (C) 2018-2023 Velocity Contributors
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

package com.turbopowered.proxy.connection.util;

import com.turbopowered.api.util.GameProfile;
import com.turbopowered.proxy.config.PlayerInfoForwarding;
import com.turbopowered.proxy.connection.ConnectionType;
import com.turbopowered.proxy.connection.backend.BackendConnectionPhase;
import com.turbopowered.proxy.connection.client.ClientConnectionPhase;

/**
 * Indicates the type of connection that has been made.
 */
public class ConnectionTypeImpl implements ConnectionType {

  private final ClientConnectionPhase initialClientPhase;
  private final BackendConnectionPhase initialBackendPhase;

  public ConnectionTypeImpl(ClientConnectionPhase initialClientPhase,
      BackendConnectionPhase initialBackendPhase) {
    this.initialClientPhase = initialClientPhase;
    this.initialBackendPhase = initialBackendPhase;
  }

  @Override
  public final ClientConnectionPhase getInitialClientPhase() {
    return initialClientPhase;
  }

  @Override
  public final BackendConnectionPhase getInitialBackendPhase() {
    return initialBackendPhase;
  }

  @Override
  public GameProfile addGameProfileTokensIfRequired(GameProfile original,
      PlayerInfoForwarding forwardingType) {
    return original;
  }
}

