package com.hyf.hotrefresh.remoting;

import com.hyf.hotrefresh.common.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.*;

/**
 * @author baB_hyf
 * @date 2022/05/15
 */
public enum RpcMessageCompression implements EnumCodeAware {

    GZIP((byte) 1, new GZIPCompressor()), //
    DEFLATER((byte) 2, new DeflaterCompressor()), //
    ZIP((byte) 3, new ZIPCompressor()), //
    ;

    private byte       code;
    private Compressor compressor;

    RpcMessageCompression(byte code, Compressor compressor) {
        this.code = code;
        this.compressor = compressor;
    }

    public static RpcMessageCompression getCompression(byte code) {
        for (RpcMessageCompression compression : values()) {
            if (compression.code == code) {
                return compression;
            }
        }

        throw new IllegalArgumentException("Message compression code not support: " + code);
    }

    @Override
    public byte getCode() {
        return 0;
    }

    public byte[] compress(byte[] bytes) {
        return this.compressor.compress(bytes);
    }

    public byte[] decompress(byte[] bytes) {
        return this.compressor.decompress(bytes);
    }

    public interface Compressor {
        byte[] compress(byte[] bytes);

        byte[] decompress(byte[] bytes);
    }

    public static class GZIPCompressor implements Compressor {

        @Override
        public byte[] compress(byte[] bytes) {
            if (bytes == null || bytes.length == 0) {
                throw new NullPointerException("bytes is null");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (GZIPOutputStream gos = new GZIPOutputStream(baos)) {
                gos.write(bytes);
                gos.flush();
                gos.finish();
                return baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("gzip compress error", e);
            }
        }

        @Override
        public byte[] decompress(byte[] bytes) {
            if (bytes == null) {
                throw new NullPointerException("bytes is null");
            }
            try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
                return IOUtils.readAsByteArray(gis);
            } catch (IOException e) {
                throw new RuntimeException("gzip decompress error", e);
            }
        }
    }

    public static class DeflaterCompressor implements Compressor {

        @Override
        public byte[] compress(byte[] bytes) {
            if (bytes == null || bytes.length == 0) {
                throw new NullPointerException("bytes is null");
            }
            Deflater deflater = new Deflater();
            deflater.setInput(bytes);
            deflater.finish();
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                int len;
                byte[] buf = new byte[1024];
                while (!deflater.finished()) {
                    len = deflater.deflate(buf);
                    baos.write(buf, 0, len);
                }
                deflater.end();
                return baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("deflater compress error", e);
            }
        }

        @Override
        public byte[] decompress(byte[] bytes) {
            if (bytes == null) {
                throw new NullPointerException("bytes is null");
            }
            Inflater inflater = new Inflater();
            inflater.setInput(bytes);
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                int len;
                byte[] buf = new byte[1024];
                while (!inflater.finished()) {
                    len = inflater.inflate(buf);
                    if (len == 0) {
                        break;
                    }
                    baos.write(buf, 0, len);
                }
                inflater.end();
                return baos.toByteArray();
            } catch (IOException | DataFormatException e) {
                throw new RuntimeException("deflater decompress error", e);
            }
        }
    }

    public static class ZIPCompressor implements Compressor {

        @Override
        public byte[] compress(byte[] bytes) {
            if (bytes == null || bytes.length == 0) {
                throw new NullPointerException("bytes is null");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(baos)) {
                ZipEntry entry = new ZipEntry("zip");
                entry.setSize(bytes.length);
                zos.putNextEntry(entry);
                zos.write(bytes);
                zos.closeEntry();
                return baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("zip compress error", e);
            }
        }

        @Override
        public byte[] decompress(byte[] bytes) {
            if (bytes == null) {
                throw new NullPointerException("bytes is null");
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes))) {
                while (zis.getNextEntry() != null) {
                    int len;
                    byte[] buf = new byte[1024];
                    while ((len = zis.read(buf)) != -1) {
                        baos.write(buf, 0, len);
                    }
                }
                return baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("zip decompress error", e);
            }
        }
    }
}
