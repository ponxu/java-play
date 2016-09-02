package com.ponxu.test.xsender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author xuwenzhao
 * @date 2016-08-31
 */
public class RangeFile {
    public static final int CHUNK_SIZE = 1024 * 50;
    private int totalSize;
    private File file;

    public RangeFile(String filePath) {
        file = new File(filePath);
        totalSize = (int) file.length();
    }

    public List<Range> getRangeList(int rangeCount) throws IOException {
        int chunkCount = (int) Math.ceil(totalSize / (double) CHUNK_SIZE);
        int chunkCountPerRange = chunkCount / rangeCount;
        if (chunkCountPerRange == 0) {
            rangeCount = 1;
        }

        List<Range> list = new ArrayList<>();
        for (int i = 0; i < rangeCount; i++) {
            int offset = i * CHUNK_SIZE * chunkCountPerRange;
            int chunckOffset = i * chunkCountPerRange;
            int size = CHUNK_SIZE * chunkCountPerRange;
            if (i == rangeCount - 1) {
                size = totalSize - i * CHUNK_SIZE * chunkCountPerRange;
            }

            list.add(new Range(i, offset, size, chunckOffset).setFile(file));
        }

        return list;
    }

    public static class Range {
        int id;
        int offset;
        int size;
        int chunckOffset;

        RandomAccessFile af;
        private int i;

        public Range(int id, int offset, int size, int chunckOffset) {
            this.id = id;
            this.offset = offset;
            this.size = size;
            this.chunckOffset = chunckOffset;
        }

        public Range setFile(File f) throws IOException {
            af = new RandomAccessFile(f, "r");
            // af.seek(offset);
            return this;
        }

        public Chunk readChunk() throws IOException {
            if (i * CHUNK_SIZE >= size) {
                return null;
            }

            byte[] tmp = new byte[CHUNK_SIZE];
            int len = af.read(tmp);
            if (len != CHUNK_SIZE) {
                tmp = Arrays.copyOf(tmp, len);
            }

            Chunk chunk = new Chunk(chunckOffset + i, offset + i * CHUNK_SIZE, tmp);
            i++;
            return chunk;
        }
    }

    public static class Chunk {
        int id;
        int offset;
        byte[] data;

        public Chunk(int id, int offset, byte[] data) {
            this.id = id;
            this.offset = offset;
            this.data = data;
        }
    }

    public static void main(String[] args) throws Exception {
        // test1();

        for (int i = 0; i < 100; i++) {
            System.out.print(i + " ");
            test2();
        }
    }

    private static void test1() throws Exception {
        String f1 = "/home/xwz/下载/zookeeper-3.4.8.tar.gz";
        String f2 = "/tmp/zookeeper-3.4.8.tar.gz";

        RangeFile rf = new RangeFile(f1);
        FileOutputStream out = new FileOutputStream(f2);
        List<Range> list = rf.getRangeList(5);
        for (Range r : list) {
            Chunk c;
            while ((c = r.readChunk()) != null) {
                out.write(c.data);
            }
        }

        out.flush();
        out.close();

        System.out.println("md5sum " + f1 + " " + f2);
    }

    private static void test2() throws Exception {
        String f1 = "/tmp/xxxx.txt";
        String f2 = "/tmp/yyyy.txt";

        // 准备源文件
        int len = new Random().nextInt(9999999);
        RandomAccessFile file = new RandomAccessFile(f1, "rw");
        file.setLength(len);
        file.seek(new Random().nextInt(len));
        file.write(1);
        file.close();


        FileOutputStream out = new FileOutputStream(f2);

        RangeFile rf = new RangeFile(f1);
        List<Range> list = rf.getRangeList(5);
        for (Range r : list) {
            Chunk c;
            while ((c = r.readChunk()) != null) {
                out.write(c.data);
            }
        }

        out.flush();
        out.close();

        if (len != new File(f2).length()) {
            throw new RuntimeException(len + " " + new File(f2).length());
        }
        System.out.println(len + " " + new File(f2).length());
    }
}
