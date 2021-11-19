package com.igormaznitsa.jaroptimizer;

import java.io.*;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.zip.*;

public class Main
{
    private static class FileInfo
    {
        public static final int FLAG_STORED = 0;
        public static final int FLAG_DEFLATE = 1;

        private boolean lg_isDirectory;
        private String s_Name;
        private byte [] ab_data;
        private int i_compressedLength;
        private int i_compressionFlag;

        private float f_compressRatio;

        public int getSize()
        {
            if (isDirectory()) return 0;
            return ab_data.length;
        }

        public float compressRatio()
        {
            return f_compressRatio;
        }

        public byte [] getData()
        {
            return ab_data;
        }

        public ZipEntry makeEntry()
        {
            ZipEntry p_entry = new ZipEntry(s_Name);
            if (!lg_isDirectory)
            {
                p_entry.setMethod(i_compressionFlag == FLAG_STORED ? ZipEntry.STORED : ZipEntry.DEFLATED);
                p_entry.setSize(ab_data.length);
                p_entry.setCompressedSize(-1);
                CRC32 p_crc32 = new CRC32();
                p_crc32.update(ab_data);
                p_entry.setCrc(p_crc32.getValue());
                p_entry.setExtra(null);
                p_entry.setTime(0);
            }
            else
            {
                p_entry.setCompressedSize(-1);
                p_entry.setSize(0);
                p_entry.setExtra(null);
                p_entry.setTime(0);
                p_entry.setMethod(ZipEntry.DEFLATED);
            }
            return p_entry;
        }

        public int getCompressedSize()
        {
            if (isDirectory()) return 0;
            return i_compressedLength;
        }

        public int getCompressionFlag()
        {
            return i_compressionFlag;
        }

        public String getName()
        {
            return s_Name;
        }

        public FileInfo(ZipEntry _zipEntry, byte [] _data, int _level) throws IOException
        {
            if (_zipEntry.isDirectory())
            {
                ab_data = null;
                s_Name = _zipEntry.getName();
                lg_isDirectory = true;
                return;
            }

            lg_isDirectory = false;
            ab_data = _data;
            s_Name = _zipEntry.getName();

            ByteArrayOutputStream p_outStream = new ByteArrayOutputStream(16384);
            Deflater p_deflater = new Deflater(_level);
            DeflaterOutputStream p_deflaterStream = new DeflaterOutputStream(p_outStream, p_deflater);
            p_deflaterStream.write(ab_data, 0, ab_data.length);
            p_deflaterStream.close();

            i_compressedLength = p_outStream.toByteArray().length;
            p_outStream = null;
            p_deflater = null;
            p_deflaterStream = null;

            f_compressRatio = (float) ab_data.length / (float) i_compressedLength;

            if (ab_data.length <= i_compressedLength) i_compressionFlag = FLAG_STORED;
            else i_compressionFlag = FLAG_DEFLATE;
        }

        public float getCompressRatio()
        {
            return f_compressRatio;
        }

        public boolean isDirectory()
        {
            return lg_isDirectory;
        }

        public String toString()
        {
            if (lg_isDirectory)
                return "Name: " + s_Name + " directory";
            else
                return "Name: " + s_Name + " Size: " + ab_data.length + " CSize : " + i_compressedLength + " Method: " + (i_compressionFlag == FLAG_STORED ? "STORED" : "DEFLATE");
        }
    }


    public static byte [] optimizeJar(File _file, int _compress) throws IOException
    {
        // Разбираем JAR и сохраняем в памяти от больших файлов к маленьким
        ZipInputStream p_zipFile = new ZipInputStream(new FileInputStream(_file));

        TreeSet p_entrySet = new TreeSet(
                new Comparator()
                {
                    public int compare(Object o1, Object o2)
                    {
                        FileInfo p_f1 = (FileInfo) o1;
                        FileInfo p_f2 = (FileInfo) o2;
                        if (p_f1.isDirectory() && !p_f2.isDirectory()) return -1;
                        if (!p_f1.isDirectory() && p_f2.isDirectory()) return 1;
                        if (p_f1.getCompressRatio() < p_f2.getCompressRatio()) return -1;
                        return 1;
                    }
                }
        );

        ZipEntry p_entry = null;
        byte[] ab_bufferArray = new byte[1024];
        while (true)
        {
            p_entry = p_zipFile.getNextEntry();
            if (p_entry == null) break;

            FileInfo p_finfo = null;
            if (p_entry.isDirectory())
            {
                p_finfo = new FileInfo(p_entry, null, 0);
            }
            else
            {
                ByteArrayOutputStream p_baosEntry = new ByteArrayOutputStream(16384);
                byte [] ab_entryData = null;
                if (p_entry.getSize() < 0)
                {
                    while (true)
                    {
                        int i_readLen = p_zipFile.read(ab_bufferArray, 0, ab_bufferArray.length);
                        if (i_readLen < 0) break;
                        p_baosEntry.write(ab_bufferArray, 0, i_readLen);
                    }
                    p_zipFile.closeEntry();
                    p_baosEntry.close();
                    ab_entryData = p_baosEntry.toByteArray();
                    p_baosEntry = null;
                }
                else
                {
                    ab_entryData = new byte[(int) p_entry.getSize()];
                    p_zipFile.read(ab_entryData);
                }
                p_finfo = new FileInfo(p_entry, ab_entryData, _compress);
            }

            p_entrySet.add(p_finfo);
        }

        // Компрессия JAR
        ByteArrayOutputStream p_fos = new ByteArrayOutputStream(65535);
        ZipOutputStream p_zipOutStream = new ZipOutputStream(p_fos);

        p_zipOutStream.setMethod(ZipOutputStream.DEFLATED);
        p_zipOutStream.setLevel(_compress);
        p_zipOutStream.setComment(null);

        Iterator p_iterator = p_entrySet.iterator();
        while (p_iterator.hasNext())
        {
            FileInfo p_info = (FileInfo) p_iterator.next();
            ZipEntry p_et = p_info.makeEntry();
            p_zipOutStream.putNextEntry(p_et);
            if (!p_et.isDirectory())
            {
                p_zipOutStream.write(p_info.getData(), 0, p_info.getData().length);
            }
            p_zipOutStream.closeEntry();
        }

        p_zipOutStream.finish();
        p_fos.close();
        return p_fos.toByteArray();
    }


    public static void main(String [] _args)
    {
        String s_fileName = _args[0];
        File p_file = new File(s_fileName);
        try
        {
            optimizeJar(p_file, 9);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
