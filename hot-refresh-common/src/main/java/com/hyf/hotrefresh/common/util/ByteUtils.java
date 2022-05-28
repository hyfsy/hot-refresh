package com.hyf.hotrefresh.common.util;

import com.hyf.hotrefresh.common.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author baB_hyf
 * @date 2022/05/27
 */
public abstract class ByteUtils {

    public static byte[] parse(String str) {
        str = str.trim();
        str = str.substring(1, str.length() - 1);
        str = str.replaceAll("\r", "").replaceAll("\n", "").replace("\t", "");
        List<String> bytesList = Arrays.stream(str.split(",")).map(String::trim).collect(Collectors.toList());
        byte[] bytes = new byte[bytesList.size()];
        for (int i = 0; i < bytesList.size(); i++) {
            bytes[i] = Byte.parseByte(bytesList.get(i));
        }
        return bytes;
    }

    public static List<byte[]> parseArray(String str) {

        String[] bytesArray = str.split(Constants.FILE_NAME_SEPARATOR);

        List<byte[]> bytesList = new ArrayList<>(bytesArray.length);
        for (String bytes : bytesArray) {
            bytesList.add(parse(bytes));
        }

        return bytesList;
    }

    public static String toString(byte[] bytes) {
        return Arrays.toString(bytes);
    }

    public static String toString(Collection<byte[]> bytes) {
        return bytes.stream().map(ByteUtils::toString).collect(Collectors.joining(Constants.FILE_NAME_SEPARATOR));
    }
}
