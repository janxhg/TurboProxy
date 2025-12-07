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

package com.turbopowered.proxy.connection;

import com.turbopowered.proxy.protocol.MinecraftPacket;
import com.turbopowered.proxy.protocol.packet.AvailableCommandsPacket;
import com.turbopowered.proxy.protocol.packet.BossBarPacket;
import com.turbopowered.proxy.protocol.packet.BundleDelimiterPacket;
import com.turbopowered.proxy.protocol.packet.ClientSettingsPacket;
import com.turbopowered.proxy.protocol.packet.ClientboundCookieRequestPacket;
import com.turbopowered.proxy.protocol.packet.ClientboundSoundEntityPacket;
import com.turbopowered.proxy.protocol.packet.ClientboundStopSoundPacket;
import com.turbopowered.proxy.protocol.packet.ClientboundStoreCookiePacket;
import com.turbopowered.proxy.protocol.packet.DialogClearPacket;
import com.turbopowered.proxy.protocol.packet.DialogShowPacket;
import com.turbopowered.proxy.protocol.packet.DisconnectPacket;
import com.turbopowered.proxy.protocol.packet.EncryptionRequestPacket;
import com.turbopowered.proxy.protocol.packet.EncryptionResponsePacket;
import com.turbopowered.proxy.protocol.packet.HandshakePacket;
import com.turbopowered.proxy.protocol.packet.HeaderAndFooterPacket;
import com.turbopowered.proxy.protocol.packet.JoinGamePacket;
import com.turbopowered.proxy.protocol.packet.KeepAlivePacket;
import com.turbopowered.proxy.protocol.packet.LegacyHandshakePacket;
import com.turbopowered.proxy.protocol.packet.LegacyPingPacket;
import com.turbopowered.proxy.protocol.packet.LegacyPlayerListItemPacket;
import com.turbopowered.proxy.protocol.packet.LoginAcknowledgedPacket;
import com.turbopowered.proxy.protocol.packet.LoginPluginMessagePacket;
import com.turbopowered.proxy.protocol.packet.LoginPluginResponsePacket;
import com.turbopowered.proxy.protocol.packet.PingIdentifyPacket;
import com.turbopowered.proxy.protocol.packet.PluginMessagePacket;
import com.turbopowered.proxy.protocol.packet.RemovePlayerInfoPacket;
import com.turbopowered.proxy.protocol.packet.RemoveResourcePackPacket;
import com.turbopowered.proxy.protocol.packet.ResourcePackRequestPacket;
import com.turbopowered.proxy.protocol.packet.ResourcePackResponsePacket;
import com.turbopowered.proxy.protocol.packet.RespawnPacket;
import com.turbopowered.proxy.protocol.packet.ServerDataPacket;
import com.turbopowered.proxy.protocol.packet.ServerLoginPacket;
import com.turbopowered.proxy.protocol.packet.ServerLoginSuccessPacket;
import com.turbopowered.proxy.protocol.packet.ServerboundCookieResponsePacket;
import com.turbopowered.proxy.protocol.packet.ServerboundCustomClickActionPacket;
import com.turbopowered.proxy.protocol.packet.SetCompressionPacket;
import com.turbopowered.proxy.protocol.packet.StatusPingPacket;
import com.turbopowered.proxy.protocol.packet.StatusRequestPacket;
import com.turbopowered.proxy.protocol.packet.StatusResponsePacket;
import com.turbopowered.proxy.protocol.packet.TabCompleteRequestPacket;
import com.turbopowered.proxy.protocol.packet.TabCompleteResponsePacket;
import com.turbopowered.proxy.protocol.packet.TransferPacket;
import com.turbopowered.proxy.protocol.packet.UpsertPlayerInfoPacket;
import com.turbopowered.proxy.protocol.packet.chat.ChatAcknowledgementPacket;
import com.turbopowered.proxy.protocol.packet.chat.PlayerChatCompletionPacket;
import com.turbopowered.proxy.protocol.packet.chat.SystemChatPacket;
import com.turbopowered.proxy.protocol.packet.chat.keyed.KeyedPlayerChatPacket;
import com.turbopowered.proxy.protocol.packet.chat.keyed.KeyedPlayerCommandPacket;
import com.turbopowered.proxy.protocol.packet.chat.legacy.LegacyChatPacket;
import com.turbopowered.proxy.protocol.packet.chat.session.SessionPlayerChatPacket;
import com.turbopowered.proxy.protocol.packet.chat.session.SessionPlayerCommandPacket;
import com.turbopowered.proxy.protocol.packet.config.ActiveFeaturesPacket;
import com.turbopowered.proxy.protocol.packet.config.ClientboundCustomReportDetailsPacket;
import com.turbopowered.proxy.protocol.packet.config.ClientboundServerLinksPacket;
import com.turbopowered.proxy.protocol.packet.config.CodeOfConductAcceptPacket;
import com.turbopowered.proxy.protocol.packet.config.CodeOfConductPacket;
import com.turbopowered.proxy.protocol.packet.config.FinishedUpdatePacket;
import com.turbopowered.proxy.protocol.packet.config.KnownPacksPacket;
import com.turbopowered.proxy.protocol.packet.config.RegistrySyncPacket;
import com.turbopowered.proxy.protocol.packet.config.StartUpdatePacket;
import com.turbopowered.proxy.protocol.packet.config.TagsUpdatePacket;
import com.turbopowered.proxy.protocol.packet.title.LegacyTitlePacket;
import com.turbopowered.proxy.protocol.packet.title.TitleActionbarPacket;
import com.turbopowered.proxy.protocol.packet.title.TitleClearPacket;
import com.turbopowered.proxy.protocol.packet.title.TitleSubtitlePacket;
import com.turbopowered.proxy.protocol.packet.title.TitleTextPacket;
import com.turbopowered.proxy.protocol.packet.title.TitleTimesPacket;
import io.netty.buffer.ByteBuf;

/**
 * Interface for dispatching received Minecraft packets.
 */
public interface MinecraftSessionHandler {

  default boolean beforeHandle() {
    return false;
  }

  default void handleGeneric(MinecraftPacket packet) {

  }

  default void handleUnknown(ByteBuf buf) {

  }

  default void connected() {

  }

  default void disconnected() {

  }

  default void activated() {

  }

  default void deactivated() {

  }

  default void exception(Throwable throwable) {

  }

  default void writabilityChanged() {

  }

  default void readCompleted() {

  }

  default boolean handle(AvailableCommandsPacket commands) {
    return false;
  }

  default boolean handle(BossBarPacket packet) {
    return false;
  }

  default boolean handle(LegacyChatPacket packet) {
    return false;
  }

  default boolean handle(ClientSettingsPacket packet) {
    return false;
  }

  default boolean handle(DisconnectPacket packet) {
    return false;
  }

  default boolean handle(EncryptionRequestPacket packet) {
    return false;
  }

  default boolean handle(EncryptionResponsePacket packet) {
    return false;
  }

  default boolean handle(HandshakePacket packet) {
    return false;
  }

  default boolean handle(HeaderAndFooterPacket packet) {
    return false;
  }

  default boolean handle(JoinGamePacket packet) {
    return false;
  }

  default boolean handle(KeepAlivePacket packet) {
    return false;
  }

  default boolean handle(LegacyHandshakePacket packet) {
    return false;
  }

  default boolean handle(LegacyPingPacket packet) {
    return false;
  }

  default boolean handle(LoginPluginMessagePacket packet) {
    return false;
  }

  default boolean handle(LoginPluginResponsePacket packet) {
    return false;
  }

  default boolean handle(PluginMessagePacket packet) {
    return false;
  }

  default boolean handle(RespawnPacket packet) {
    return false;
  }

  default boolean handle(ServerLoginPacket packet) {
    return false;
  }

  default boolean handle(ServerLoginSuccessPacket packet) {
    return false;
  }

  default boolean handle(SetCompressionPacket packet) {
    return false;
  }

  default boolean handle(StatusPingPacket packet) {
    return false;
  }

  default boolean handle(StatusRequestPacket packet) {
    return false;
  }

  default boolean handle(StatusResponsePacket packet) {
    return false;
  }

  default boolean handle(TabCompleteRequestPacket packet) {
    return false;
  }

  default boolean handle(TabCompleteResponsePacket packet) {
    return false;
  }

  default boolean handle(LegacyTitlePacket packet) {
    return false;
  }

  default boolean handle(TitleTextPacket packet) {
    return false;
  }

  default boolean handle(TitleSubtitlePacket packet) {
    return false;
  }

  default boolean handle(TitleActionbarPacket packet) {
    return false;
  }

  default boolean handle(TitleTimesPacket packet) {
    return false;
  }

  default boolean handle(TitleClearPacket packet) {
    return false;
  }

  default boolean handle(LegacyPlayerListItemPacket packet) {
    return false;
  }

  default boolean handle(ResourcePackRequestPacket packet) {
    return false;
  }

  default boolean handle(RemoveResourcePackPacket packet) {
    return false;
  }

  default boolean handle(ResourcePackResponsePacket packet) {
    return false;
  }

  default boolean handle(KeyedPlayerChatPacket packet) {
    return false;
  }

  default boolean handle(SessionPlayerChatPacket packet) {
    return false;
  }

  default boolean handle(SystemChatPacket packet) {
    return false;
  }

  default boolean handle(KeyedPlayerCommandPacket packet) {
    return false;
  }

  default boolean handle(SessionPlayerCommandPacket packet) {
    return false;
  }

  default boolean handle(PlayerChatCompletionPacket packet) {
    return false;
  }

  default boolean handle(ServerDataPacket serverData) {
    return false;
  }

  default boolean handle(RemovePlayerInfoPacket packet) {
    return false;
  }

  default boolean handle(UpsertPlayerInfoPacket packet) {
    return false;
  }

  default boolean handle(LoginAcknowledgedPacket packet) {
    return false;
  }

  default boolean handle(ActiveFeaturesPacket packet) {
    return false;
  }

  default boolean handle(FinishedUpdatePacket packet) {
    return false;
  }

  default boolean handle(RegistrySyncPacket packet) {
    return false;
  }

  default boolean handle(TagsUpdatePacket packet) {
    return false;
  }

  default boolean handle(StartUpdatePacket packet) {
    return false;
  }

  default boolean handle(PingIdentifyPacket pingIdentify) {
    return false;
  }

  default boolean handle(ChatAcknowledgementPacket chatAcknowledgement) {
    return false;
  }

  default boolean handle(BundleDelimiterPacket bundleDelimiterPacket) {
    return false;
  }

  default boolean handle(TransferPacket transfer) {
    return false;
  }

  default boolean handle(KnownPacksPacket packet) {
    return false;
  }

  default boolean handle(ClientboundStoreCookiePacket packet) {
    return false;
  }

  default boolean handle(ClientboundCookieRequestPacket packet) {
    return false;
  }

  default boolean handle(ServerboundCookieResponsePacket packet) {
    return false;
  }

  default boolean handle(ClientboundCustomReportDetailsPacket packet) {
    return false;
  }

  default boolean handle(ClientboundServerLinksPacket packet) {
    return false;
  }

  default boolean handle(DialogClearPacket packet) {
    return false;
  }

  default boolean handle(DialogShowPacket packet) {
    return false;
  }

  default boolean handle(ServerboundCustomClickActionPacket packet) {
    return false;
  }

  default boolean handle(CodeOfConductPacket packet) {
    return false;
  }

  default boolean handle(CodeOfConductAcceptPacket packet) {
    return false;
  }

  default boolean handle(ClientboundSoundEntityPacket packet) {
    return false;
  }

  default boolean handle(ClientboundStopSoundPacket packet) {
    return false;
  }
}
