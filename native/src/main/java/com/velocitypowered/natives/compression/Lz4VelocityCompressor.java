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

package com.velocitypowered.natives.compression;

import static com.google.common.base.Preconditions.checkArgument;

import com.velocitypowered.natives.util.BufferPreference;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

/**
 * Implements LZ4 compression.
 */
public class Lz4VelocityCompressor implements VelocityCompressor {

  public static final VelocityCompressorFactory FACTORY = Lz4VelocityCompressor::new;

  private final LZ4Compressor compressor;
  private final LZ4FastDecompressor decompressor;

  private Lz4VelocityCompressor(int level) {
    System.out.println("[Velocity-Native] Creating LZ4 Compressor (level " + level + ")");
    // LZ4 factory is likely stateless/singleton
    LZ4Factory factory = LZ4Factory.fastestInstance();
    this.compressor = factory.fastCompressor();
    this.decompressor = factory.fastDecompressor();
  }

  @Override
  public void inflate(ByteBuf source, ByteBuf destination, int uncompressedSize)
      throws DataFormatException {
    // We (probably) can't nicely deal with >=1 buffer nicely, so let's scream loudly.
    checkArgument(source.nioBufferCount() == 1, "source has multiple backing buffers");
    checkArgument(destination.nioBufferCount() == 1, "destination has multiple backing buffers");

    ByteBuffer srcNio = source.nioBuffer();
    // We want to write up to uncompressedSize
    // Ensure capacity or assume it's there? The caller usually provides enough.
    // The previous code had destination.nioBuffer(writerIndex, uncompressedSize)
    ByteBuffer destNio = destination.nioBuffer(destination.writerIndex(), destination.writableBytes());

    try {
      int compressedLen = source.readableBytes();
      int oldLimit = destNio.limit();
      destNio.limit(destNio.position() + uncompressedSize);

      decompressor.decompress(srcNio, destNio);
      
      destNio.limit(oldLimit); // Restore limit (optional if we update writerIndex based on uncompressedSize)

      // Update indices
      source.readerIndex(source.readerIndex() + compressedLen);
      destination.writerIndex(destination.writerIndex() + uncompressedSize);

    } catch (Exception e) {
       throw new DataFormatException("LZ4 decompression failed: " + e.getMessage());
    }
  }

  @Override
  public void deflate(ByteBuf source, ByteBuf destination) throws DataFormatException {
    checkArgument(source.nioBufferCount() == 1, "source has multiple backing buffers");
    checkArgument(destination.nioBufferCount() == 1, "destination has multiple backing buffers");

    ByteBuffer srcNio = source.nioBuffer();
    int uncompressedLen = source.readableBytes();
    
    int maxCompressedLength = compressor.maxCompressedLength(uncompressedLen);
    destination.ensureWritable(maxCompressedLength);
    
    ByteBuffer destNio = destination.nioBuffer(destination.writerIndex(), maxCompressedLength);

    try {
      int startPos = destNio.position();
      compressor.compress(srcNio, destNio);
      int compressedLength = destNio.position() - startPos;
      
      // Update indices
      source.readerIndex(source.readerIndex() + uncompressedLen);
      destination.writerIndex(destination.writerIndex() + compressedLength);
    } catch (Exception e) {
        throw new DataFormatException("LZ4 compression failed: " + e.getMessage());
    }
  }

  @Override
  public void close() {
    // No-op for LZ4
  }

  @Override
  public BufferPreference preferredBufferType() {
    return BufferPreference.DIRECT_PREFERRED;
  }
}
