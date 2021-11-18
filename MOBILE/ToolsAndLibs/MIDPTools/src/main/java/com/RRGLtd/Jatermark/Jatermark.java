package com.RRGLtd.Jatermark;

import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.*;
import org.apache.bcel.classfile.*;

import java.io.*;
import java.util.jar.*;
import java.util.*;
import java.util.zip.ZipEntry;

import com.RRGLtd.Utils.Base64;

class VerifyClass
{
    public native byte[] ProcessClass(String sName, byte[] data);

    public native int SetClassPath(String path);

    public VerifyClass(String _classPath)
    {
        SetClassPath(_classPath);
    }

    static
    {
        try
        {
            System.loadLibrary("preverify");
        }
        catch (java.lang.UnsatisfiedLinkError e)
        {
            System.out.println(e);
        }
    }
}

public class Jatermark
{
    public static final int STRING_PRODUCERID = 0;
    public static final int STRING_USERID = 1;
    public static final int STRING_PRODUCTID = 2;
    public static final int STRING_TIMPESTAMP = 3;

    private static final int OFFSET_LENGTH_INTERVAL = 10;
    private static final int XOR_TIMESTAMP = 0xBB;
    private static final int XOR_PRODUCTID = 0x1A;
    private static final int XOR_PRODUCERID = 0x56;
    private static final int XOR_USERID = 0xCC;
    private static final int INIT_CRC_AKKUM = 0xFAC0CA0F;
    private static final int XOR_CRC = 0x876430;

    private VerifyClass p_verifyClass = new VerifyClass(System.getProperty("java.class.path", "."));

    private class ClassReference
    {
        public JarEntry p_classEntry;
        byte[] p_classArray;

        public ClassReference(JarEntry _entry, byte[] _classArray)
        {
            p_classArray = _classArray;
            p_classEntry = _entry;
        }
    }

    public class MarkerClass
    {
        public static final int ERRORCODE_OK = 0;
        public static final int ERRORCODE_CRCERROR = 1;
        public static final int ERRORCODE_FORMATERROR = 2;
        public static final int ERRORCODE_BLOCKABSENT = 3;

        private String s_producerId;
        private String s_userId;
        private String s_productId;
        private long l_timestamp;
        private byte[] ab_MarkerArray;
        private int i_errorCode;

        public int getErrorCode()
        {
            return i_errorCode;
        }

        private void parseMarkerArray(byte[] _array, int _base)
        {
            try
            {
                // Repair the array with the base code
                for (int li = 0; li < _array.length; li++)
                {
                    _array[li] = (byte) (_array[li] ^ ((li << 1) + _base));
                }

                // Repair the CRC code
                int i_backOffset = _array.length - 1;
                int i_b0 = _array[i_backOffset--] & 0xFF;
                int i_b1 = _array[i_backOffset--] & 0xFF;
                int i_b2 = _array[i_backOffset--] & 0xFF;
                int i_b3 = _array[i_backOffset--] & 0xFF;
                int i_crc = (i_b3 << 24) | (i_b2 << 16) | (i_b1 << 8) | i_b0;

                // Reading the timestamp
                int i_offset = 0;
                l_timestamp = ((long) (_array[i_offset++] ^ XOR_TIMESTAMP) & 0xFF) << 56;
                l_timestamp |= (((long) (_array[i_offset++] ^ XOR_TIMESTAMP) & 0xFF) << 48);
                l_timestamp |= (((long) (_array[i_offset++] ^ XOR_TIMESTAMP) & 0xFF) << 40);
                l_timestamp |= (((long) (_array[i_offset++] ^ XOR_TIMESTAMP) & 0xFF) << 32);
                l_timestamp |= (((long) (_array[i_offset++] ^ XOR_TIMESTAMP) & 0xFF) << 24);
                l_timestamp |= (((long) (_array[i_offset++] ^ XOR_TIMESTAMP) & 0xFF) << 16);
                l_timestamp |= (((long) (_array[i_offset++] ^ XOR_TIMESTAMP) & 0xFF) << 8);
                l_timestamp |= (((long) (_array[i_offset++] ^ XOR_TIMESTAMP) & 0xFF));

                //Reading the producerID
                int i_len = _array[i_offset++] & 0xFF;
                if ((i_len + i_offset - 1) >= _array.length)
                {
                    i_errorCode = ERRORCODE_FORMATERROR;
                    return;
                }

                StringBuffer p_buf = new StringBuffer(i_len);
                while (i_len > 0)
                {
                    p_buf.append((char) ((_array[i_offset++] ^ XOR_PRODUCERID) & 0xFF));
                    i_len--;
                }
                s_producerId = p_buf.toString();

                //Reading the userID
                i_len = _array[i_offset++] & 0xFF;
                if ((i_len + i_offset - 1) >= _array.length)
                {
                    i_errorCode = ERRORCODE_FORMATERROR;
                    return;
                }

                p_buf = new StringBuffer(i_len);
                while (i_len > 0)
                {
                    p_buf.append((char) ((_array[i_offset++] ^ XOR_USERID) & 0xFF));
                    i_len--;
                }
                s_userId = p_buf.toString();

                //Reading the productID
                i_len = _array[i_offset++] & 0xFF;
                if ((i_len + i_offset - 1) >= _array.length)
                {
                    i_errorCode = ERRORCODE_FORMATERROR;
                    return;
                }

                p_buf = new StringBuffer(i_len);
                while (i_len > 0)
                {
                    p_buf.append((char) ((_array[i_offset++] ^ XOR_PRODUCTID) & 0xFF));
                    i_len--;
                }
                s_productId = p_buf.toString();

                // Checking the CRC
                int i_etalon = generateHashCode(_array, 0, _array.length - 4);
                if (i_etalon != i_crc)
                {
                    i_errorCode = ERRORCODE_CRCERROR;
                }
            }
            catch (ArrayIndexOutOfBoundsException _ex)
            {
                i_errorCode = ERRORCODE_FORMATERROR;
            }
        }

        private final void parseInstruction(Code _code, int _base)
        {
            i_errorCode = ERRORCODE_OK;
            InstructionList p_list = new InstructionList((_code.getCode()));
            try
            {
                InstructionHandle[] ap_handles = p_list.getInstructionHandles();

                // We are looking for the interval value and the array length
                InstructionHandle p_instr = ap_handles[ap_handles.length - OFFSET_LENGTH_INTERVAL - 3];

                int i_interval = -1;
                int i_arrayLength = -1;

                if (p_instr.getInstruction() instanceof SIPUSH)
                {
                    int i_value = ((SIPUSH) p_instr.getInstruction()).getValue().intValue();
                    // Interval length
                    i_interval = i_value & 0x1FF;
                    // Array length
                    i_arrayLength = (i_value >>> 9) & 0xFF;
                }

                InstructionHandle p_startInstruction = ap_handles[0];

                byte[] ab_markArray = new byte[i_arrayLength];

                int i_indx = 0;
                while (i_indx < ab_markArray.length)
                {
                    for (int li = 0; li < i_interval; li++)
                        p_startInstruction = p_startInstruction.getNext();

                    int i_num = 0;
                    for (int li = 0; li < 2; li++)
                    {
                        if (i_indx == ab_markArray.length) break;
                        if (p_startInstruction.getInstruction() instanceof SIPUSH)
                        {
                            int i_value = ((SIPUSH) p_startInstruction.getInstruction()).getValue().intValue();
                            ab_markArray[i_indx++] = (byte) (i_value >>> 8);
                            if (i_indx < ab_markArray.length)
                            {
                                ab_markArray[i_indx++] = (byte) i_value;
                            }
                        }
                        else
                        {
                            throw new IOException();
                        }
                        p_startInstruction = p_startInstruction.getNext();

                        i_num++;
                    }
                    if (i_num == 2)
                    {
                        if (!(p_startInstruction.getInstruction() instanceof SWAP)) throw new IOException();
                        p_startInstruction = p_startInstruction.getNext();
                        if (!(p_startInstruction.getInstruction() instanceof POP2)) throw new IOException();
                    }
                    else
                    {
                        if (!(p_startInstruction.getInstruction() instanceof POP)) throw new IOException();
                    }
                    p_startInstruction = p_startInstruction.getNext();
                }

                parseMarkerArray(ab_markArray, _base);
            }
            catch (IOException _ex)
            {
                i_errorCode = ERRORCODE_BLOCKABSENT;
            }
        }

        private final byte[] createMarkerArray() throws IOException
        {
            ByteArrayOutputStream p_baos = new ByteArrayOutputStream();
            // Writing the timestamp
            p_baos.write((byte) (l_timestamp >>> 56) ^ XOR_TIMESTAMP);
            p_baos.write((byte) (l_timestamp >>> 48) ^ XOR_TIMESTAMP);
            p_baos.write((byte) (l_timestamp >>> 40) ^ XOR_TIMESTAMP);
            p_baos.write((byte) (l_timestamp >>> 32) ^ XOR_TIMESTAMP);
            p_baos.write((byte) (l_timestamp >>> 24) ^ XOR_TIMESTAMP);
            p_baos.write((byte) (l_timestamp >>> 16) ^ XOR_TIMESTAMP);
            p_baos.write((byte) (l_timestamp >>> 8) ^ XOR_TIMESTAMP);
            p_baos.write((byte) l_timestamp);
            // Writing the producerId
            p_baos.write(s_producerId.length());
            for (int li = 0; li < s_producerId.getBytes().length; li++)
            {
                byte b_b = (byte) (s_producerId.getBytes()[li] ^ XOR_PRODUCERID);
                p_baos.write(b_b);
            }

            // Writing the userId
            p_baos.write(s_userId.length());
            for (int li = 0; li < s_userId.getBytes().length; li++)
            {
                byte b_b = (byte) (s_userId.getBytes()[li] ^ XOR_USERID);
                p_baos.write(b_b);
            }

            // Writing the productId
            p_baos.write(s_productId.length());
            for (int li = 0; li < s_productId.getBytes().length; li++)
            {
                byte b_b = (byte) (s_productId.getBytes()[li] ^ XOR_PRODUCTID);
                p_baos.write(b_b);
            }

            p_baos.flush();

            byte[] ab_byteArray = new byte[p_baos.size() + 4];
            System.arraycopy(p_baos.toByteArray(), 0, ab_byteArray, 0, p_baos.size());

            int i_code = generateHashCode(ab_byteArray, 0, ab_byteArray.length - 4);

            int i_offset = ab_byteArray.length - 4;
            ab_byteArray[i_offset++] = (byte) (i_code >>> 24);
            ab_byteArray[i_offset++] = (byte) (i_code >>> 16);
            ab_byteArray[i_offset++] = (byte) (i_code >>> 8);
            ab_byteArray[i_offset] = (byte) i_code;

            return ab_byteArray;
        }

        public MarkerClass(Method _method)
        {
            ConstantPoolGen p_cp = new ConstantPoolGen(_method.getConstantPool());
            int i_base = 0;

            for (int li = 0; li < p_cp.getSize(); li++)
            {
                Constant p_const = p_cp.getConstant(li);
                if (p_const instanceof ConstantInteger)
                {
                    i_base = ((Integer) (((ConstantInteger) p_const).getConstantValue(_method.getConstantPool()))).intValue();
                    break;
                }
            }

            Code p_code = _method.getCode();
            parseInstruction(p_code, i_base);
        }

        public MarkerClass()
        {
        }

        public boolean isWatermarkExist(Method _method)
        {
            if (_method.isAbstract() || _method.isNative() || _method.isInterface()) return false;
            InstructionList p_ilist = new InstructionList(_method.getCode().getCode());
            InstructionHandle[] ap_handles = p_ilist.getInstructionHandles();
            try
            {
                if (ap_handles.length < (OFFSET_LENGTH_INTERVAL + 3)) return false;
                InstructionHandle p_firstInstr = ap_handles[ap_handles.length - OFFSET_LENGTH_INTERVAL - 3];
                if (p_firstInstr.getInstruction() instanceof SIPUSH)
                {
                    p_firstInstr = p_firstInstr.getNext();
                    if (p_firstInstr.getInstruction() instanceof DUP)
                    {
                        p_firstInstr = p_firstInstr.getNext();
                        if (p_firstInstr.getInstruction() instanceof POP2)
                        {
                            return true;
                        }
                    }
                }
                return false;
            }
            finally
            {
                p_ilist.dispose();
            }
        }

        public MarkerClass(String _producerId, String _productId, String _userId) throws IOException
        {
            l_timestamp = System.currentTimeMillis();
            s_producerId = _producerId;
            s_productId = _productId;
            s_userId = _userId;

            ab_MarkerArray = createMarkerArray();
        }

        public byte[] getMarkerArray(int _base)
        {
            byte[] ab_newArray = new byte[ab_MarkerArray.length];
            System.arraycopy(ab_MarkerArray, 0, ab_newArray, 0, ab_MarkerArray.length);

            for (int li = 0; li < ab_newArray.length; li++)
            {
                ab_newArray[li] = (byte) (ab_newArray[li] ^ ((li << 1) + _base));
            }

            return ab_newArray;
        }

        public String getProducerID()
        {
            return s_producerId;
        }

        public String getUserID()
        {
            return s_userId;
        }

        public String getProductID()
        {
            return s_productId;
        }

        public Date getTimeStamp()
        {
            return new Date(l_timestamp);
        }

        // Generating the hash code
        private final int generateHashCode(byte[] _array, int _offset, int _length)
        {
            int i_accum = INIT_CRC_AKKUM;
            int i_offset = _offset;
            int i_length = _length;
            while (i_length > 0)
            {
                int i_byte = _array[i_offset] & 0xFF;
                i_byte = (i_byte * i_length) ^ XOR_CRC;
                i_accum ^= i_byte;
                i_length--;
                i_offset++;
            }
            return i_accum;
        }
    }

    private byte[] addWatermarkBlock(byte[] _class, String _name, MarkerClass _watermarkBlock) throws IOException
    {
        ByteArrayInputStream p_bais = new ByteArrayInputStream(_class);
        JavaClass p_class = new ClassParser(p_bais, _name).parse();

        ClassGen p_classGen = new ClassGen(p_class);
        ConstantPoolGen p_cpg = p_classGen.getConstantPool();

        Method[] ap_methods = p_classGen.getMethods();

        // Getting of the base code
        int i_base = 0;
        for (int li = 0; li < p_cpg.getSize(); li++)
        {
            Constant p_constant = p_cpg.getConstant(li);
            if (p_constant instanceof ConstantInteger)
            {
                i_base = ((Integer) ((ConstantInteger) p_constant).getConstantValue(p_class.getConstantPool())).intValue();
                break;
            }
        }

        // To find the biggest method
        Method p_method = null;
        int i_maxLength = 0;
        int i_methodIndex = -1;
        for (int li = 0; li < ap_methods.length; li++)
        {
            if (ap_methods[li].isAbstract() || ap_methods[li].isNative()) continue;
            if (ap_methods[li].getCode().getCode().length > i_maxLength)
            {
                p_method = ap_methods[li];
                i_maxLength = ap_methods[li].getCode().getCode().length;
                i_methodIndex = li;
            }
        }

        MethodGen p_methGen = new MethodGen(p_method, p_class.getClassName(), p_cpg);
        StackMap p_stackMapAttribute = null;
        HashMap p_HashTableStackMapAttribute = null;
        int i_stackMapAttributeIndex = -1;

        InstructionList p_instrList = p_methGen.getInstructionList();
        InstructionHandle[] ap_handles = p_instrList.getInstructionHandles();

        if (ap_handles.length < OFFSET_LENGTH_INTERVAL + 5) throw new IOException("Too short method code for watrermark");

        Attribute[] ap_codeAttributes = p_methGen.getCodeAttributes();

        for (int li = 0; li < ap_codeAttributes.length; li++)
        {
            if (ap_codeAttributes[li] instanceof StackMap)
            {
                p_stackMapAttribute = (StackMap) ap_codeAttributes[li];
                i_stackMapAttributeIndex = li;
            }
        }

        if (i_stackMapAttributeIndex >= 0)
        {
            // Filling of the hashmap table
            p_HashTableStackMapAttribute = new HashMap();
            StackMapEntry[] ap_entries = p_stackMapAttribute.getStackMap();
            for (int li = 0; li < ap_entries.length; li++)
            {
                StackMapEntry p_entry = ap_entries[li];
                for (int lh = 0; lh < ap_handles.length; lh++)
                {
                    if (p_entry.getByteCodeOffset() == ap_handles[lh].getPosition())
                    {
                        p_HashTableStackMapAttribute.put(ap_handles[lh], p_entry);
                        break;
                    }
                }
            }
        }

        InstructionHandle p_startInstruction = ap_handles[ap_handles.length - OFFSET_LENGTH_INTERVAL];

        byte[] ab_markArray = _watermarkBlock.getMarkerArray(i_base);
        int i_dblLngth = (ab_markArray.length + 1) / 4;

        InstructionHandle p_bipush = p_instrList.insert(p_startInstruction, new SIPUSH((byte) 0));
        p_instrList.insert(p_startInstruction, new DUP());
        p_instrList.insert(p_startInstruction, new POP2());

        p_startInstruction = ap_handles[0];
        int i_interval = (ap_handles.length - OFFSET_LENGTH_INTERVAL - 3) / (i_dblLngth + 1);

        i_interval = i_interval > 511 ? 511 : i_interval;

        short sh_value = (short) (((ab_markArray.length << 9) | i_interval));

        p_instrList.insert(p_bipush, new SIPUSH(sh_value));
        try
        {
            p_instrList.delete(p_bipush);
        }
        catch (TargetLostException e)
        {
        }

        int i_indx = 0;
        while (i_indx < ab_markArray.length)
        {
            for (int li = 0; li < i_interval; li++) p_startInstruction = p_startInstruction.getNext();

            int i_num = 0;
            for (int li = 0; li < 2; li++)
            {
                if (i_indx == ab_markArray.length) break;
                short sh_akkum = (short) ((ab_markArray[i_indx++] & 0xFF) << 8);
                if (i_indx < ab_markArray.length)
                {
                    sh_akkum |= (short) (ab_markArray[i_indx++] & 0xFF);
                }
                p_instrList.insert(p_startInstruction, new SIPUSH(sh_akkum));
                i_num++;
            }
            if (i_num == 2)
            {
                p_instrList.insert(p_startInstruction, new SWAP());
                p_instrList.insert(p_startInstruction, new POP2());
            }
            else
            {
                p_instrList.insert(p_startInstruction, new POP());
            }
        }

        p_instrList.setPositions();
        p_instrList.update();

        // Update the StackMap attribute if that exists
        if (i_stackMapAttributeIndex >= 0)
        {
            Iterator p_itera = p_HashTableStackMapAttribute.keySet().iterator();
            while (p_itera.hasNext())
            {
                InstructionHandle p_instruction = (InstructionHandle) p_itera.next();
                StackMapEntry p_entry = (StackMapEntry) p_HashTableStackMapAttribute.get(p_instruction);
                p_entry.setByteCodeOffset(p_instruction.getPosition());
            }
            p_HashTableStackMapAttribute.clear();
            p_methGen.getCodeAttributes()[i_stackMapAttributeIndex] = p_stackMapAttribute;
        }

        p_methGen.setInstructionList(p_instrList);
        p_methGen.setMaxStack();
        p_methGen.setMaxLocals();
        p_method = p_methGen.getMethod();
        p_classGen.setMethodAt(p_method, i_methodIndex);
        p_class = p_classGen.getJavaClass();

        byte[] ab_classArray = p_class.getBytes();

        ab_classArray = p_verifyClass.ProcessClass(p_class.getClassName(), ab_classArray);

        return ab_classArray;
    }

    private final void markClass(Vector _classes, MarkerClass _marker)
    {
        // Finding the biggest class
        Iterator p_iter = _classes.iterator();
        ClassReference p_ref = null;
        while (p_iter.hasNext())
        {
            p_ref = (ClassReference) p_iter.next();

            // Placing the watermark code into the method of the class
            try
            {
                p_ref.p_classArray = addWatermarkBlock(p_ref.p_classArray, p_ref.p_classEntry.getName(), _marker);
            }
            catch (IOException e)
            {
                System.out.println("I can't add the watermark block");
            }
        }
    }

    private final Vector findMarker(Vector _classes) throws IOException
    {
        // Finding the biggest method
        Iterator p_iter = _classes.iterator();

        MarkerClass p_tmpmarkerclass = new MarkerClass();

        Vector p_vector = new Vector();

        while (p_iter.hasNext())
        {
            ClassReference p_ref = (ClassReference) p_iter.next();
            ByteArrayInputStream p_bais = new ByteArrayInputStream(p_ref.p_classArray);
            JavaClass p_jclass = new ClassParser(p_bais, p_ref.p_classEntry.getName().replace('/', '.')).parse();
            Method[] ap_methods = p_jclass.getMethods();
            for (int li = 0; li < ap_methods.length; li++)
            {
                Method p_meth = ap_methods[li];
                if (p_tmpmarkerclass.isWatermarkExist(p_meth))
                {
                    System.out.println("==========================\r\nAttemption of decoding a singnature\r\n==========================");
                    System.out.println("Class: " + p_jclass.getClassName() + " Method: " + p_meth.getName() + " " + p_meth.getSignature());
                    MarkerClass p_mrk = new MarkerClass(p_meth);
                    p_vector.add(p_mrk);
                    switch (p_mrk.getErrorCode())
                    {
                        case MarkerClass.ERRORCODE_CRCERROR:
                            {
                                System.out.println("!!! CRC ERROR !!!");
                            }
                        case MarkerClass.ERRORCODE_OK:
                            {
                                System.out.println("Signature has been read successfuly...");
                                System.out.println("ProducerID : " + p_mrk.getProducerID());
                                System.out.println("UserID : " + p_mrk.getUserID());
                                System.out.println("ProductID : " + p_mrk.getProductID());
                                System.out.println("Timestamp : " + p_mrk.getTimeStamp());
                            }
                            ;
                            break;
                        case MarkerClass.ERRORCODE_BLOCKABSENT:
                            {
                                System.out.println("There is not any signature in the method");
                            }
                            ;
                            break;
                        case MarkerClass.ERRORCODE_FORMATERROR:
                            {
                                System.out.println("!!! Signature format error !!!");
                            }
                            ;
                            break;
                    }
                }
            }
        }
        return p_vector;
    }

    public Vector checkWatermark(byte[] _jarFile) throws IOException
    {
        ByteArrayInputStream p_bis = new ByteArrayInputStream(_jarFile);
        JarInputStream p_jarInputStream = new JarInputStream(p_bis);
        byte[] ab_bufferArray = new byte[1024];
        Vector p_classVector = new Vector();

        while (true)
        {
            // Reading the next entry
            JarEntry p_entry = p_jarInputStream.getNextJarEntry();
            if (p_entry == null) break;

            byte[] ab_classArray = null;

            ByteArrayOutputStream p_baosEntry = new ByteArrayOutputStream(10000);

            if (p_entry.getSize() < 0)
            {
                while (true)
                {
                    int i_readLen = p_jarInputStream.read(ab_bufferArray, 0, ab_bufferArray.length);
                    if (i_readLen < 0) break;
                    p_baosEntry.write(ab_bufferArray, 0, i_readLen);
                }
                p_jarInputStream.closeEntry();
                p_baosEntry.close();
                ab_classArray = p_baosEntry.toByteArray();
                p_baosEntry = null;
            }
            else
            {
                ab_classArray = new byte[(int) p_entry.getSize()];
                p_jarInputStream.read(ab_classArray);
            }

            if (!p_entry.isDirectory() && p_entry.getName().toLowerCase().endsWith(".class"))
            {
                ClassReference p_ref = new ClassReference(p_entry, ab_classArray);
                p_classVector.add(p_ref);
            }
        }

        return findMarker(p_classVector);
    }

    public final byte[] markJARFile(byte[] _jarFile, String _producerId, String _userId, String _productId) throws Throwable
    {
        MarkerClass p_marker = new MarkerClass(_producerId, _productId, _userId);

        ByteArrayInputStream p_bis = new ByteArrayInputStream(_jarFile);
        JarInputStream p_jarInputStream = new JarInputStream(p_bis);

        Manifest p_manifest = p_jarInputStream.getManifest();

        ByteArrayOutputStream p_jarOut = new ByteArrayOutputStream(_jarFile.length + 1024);
        JarOutputStream p_jarOutputStream = null;
        if (p_manifest != null)
            p_jarOutputStream = new JarOutputStream(p_jarOut, p_manifest);
        else
            p_jarOutputStream = new JarOutputStream(p_jarOut);

        byte[] ab_bufferArray = new byte[1024];

        Vector p_classVector = new Vector();

        while (true)
        {
            // Reading the next entry
            JarEntry p_entry = p_jarInputStream.getNextJarEntry();
            if (p_entry == null) break;

            byte[] ab_classArray = null;

            ByteArrayOutputStream p_baosEntry = new ByteArrayOutputStream(10000);

            if (p_entry.getSize() < 0)
            {
                while (true)
                {
                    int i_readLen = p_jarInputStream.read(ab_bufferArray, 0, ab_bufferArray.length);
                    if (i_readLen < 0) break;
                    p_baosEntry.write(ab_bufferArray, 0, i_readLen);
                }
                p_jarInputStream.closeEntry();
                p_baosEntry.close();
                ab_classArray = p_baosEntry.toByteArray();
                p_baosEntry = null;
            }
            else
            {
                ab_classArray = new byte[(int) p_entry.getSize()];
                p_jarInputStream.read(ab_classArray);
            }

            if (!p_entry.isDirectory() && p_entry.getName().toLowerCase().endsWith(".class"))
            {
                ClassReference p_ref = new ClassReference(p_entry, ab_classArray);
                p_classVector.add(p_ref);
            }
            else
            {
                p_entry.setSize(ab_classArray.length);

                ZipEntry p_zen = new ZipEntry(p_entry.getName());
                p_zen.setSize(ab_classArray.length);
                p_zen.setCompressedSize(-1);

                p_jarOutputStream.putNextEntry(p_zen);
                if (ab_classArray != null) p_jarOutputStream.write(ab_classArray, 0, ab_classArray.length);
                p_jarOutputStream.closeEntry();
            }
            p_jarOutputStream.closeEntry();
        }
        markClass(p_classVector, p_marker);

        // Writing the classes from the class vector into the JAR stream
        Iterator p_iterator = p_classVector.iterator();
        while (p_iterator.hasNext())
        {
            ClassReference p_ref = (ClassReference) p_iterator.next();
            JarEntry p_entry = p_ref.p_classEntry;
            byte[] ab_classArray = p_ref.p_classArray;
            p_entry.setSize(ab_classArray.length);
            p_entry.setCompressedSize(-1);
            p_jarOutputStream.putNextEntry(p_entry);
            if (ab_classArray != null) p_jarOutputStream.write(ab_classArray, 0, ab_classArray.length);
        }

        p_jarOutputStream.flush();
        p_jarOutputStream.close();

        return p_jarOut.toByteArray();
    }

    private static final void _outTitle()
    {
        System.out.println("Jatermark™ utility v1.00");
        System.out.println("©2003 All Copyright by Raydac Research Group Ltd. (http://www.raydac-research.com)");
        System.out.println("-----------------------------------------------------------------------------------");
    }

    private static final void _outHelp()
    {
        System.out.println("For encoding : JAR-PACKAGE-NAME PRODUCERID USERID PRODUCTID");
        System.out.println("For decoding : JAR-PACKAGE-NAME");
    }

    public static char[] encodeDataToBase64(String _producer, String _user, String _product)
    {
        String s_str = _producer + _user + _product;
        return Base64.encode(s_str);
    }

    public static void main(String[] _args)
    {
        try
        {
            _outTitle();

            if (_args.length == 0)
            {
                _outHelp();
                return;
            }

            String s_jarFileName = _args[0];
            File p_JarFile = new File(s_jarFileName);
            byte[] ab_JarArray = new byte[(int) p_JarFile.length()];
            new FileInputStream(p_JarFile).read(ab_JarArray);

            new Jatermark();

            if (_args.length > 1)
            {
                String s_producerId = _args[1];
                String s_userId = _args[2];
                String s_productId = _args[3];

                byte[] ab_newJarFile = new Jatermark().markJARFile(ab_JarArray, s_producerId, s_userId, s_productId);

                // Creating new JAD file
                byte[] ab_JADFile = null;
                String s_jadFileName = null;
                if (_args[0].toLowerCase().endsWith(".jar"))
                {
                    s_jadFileName = _args[0].substring(0, _args[0].length() - 4) + ".jad";
                    File p_jadFile = new File(s_jadFileName);
                    if (p_jadFile.exists())
                    {
                        int i_size = (int) p_jadFile.length();
                        ab_JADFile = new byte[i_size];
                        FileInputStream p_fis = new FileInputStream(p_jadFile);
                        p_fis.read(ab_JADFile);
                        p_fis.close();
                    }
                }

                if (ab_JADFile != null)
                {                                   
                    JADReference p_JADContainer = new JADReference(ab_JADFile);
                    p_JADContainer.setNewSizeValue(ab_newJarFile.length);

                    String s_markString = new String(encodeDataToBase64(s_producerId, s_userId, s_productId));
                    p_JADContainer.AddValue("ContentLabel", s_markString);

                    ab_JADFile = p_JADContainer.getJADFile();
                    p_JADContainer = null;

                    FileOutputStream p_fos = new FileOutputStream(s_jadFileName + ".new");
                    p_fos.write(ab_JADFile);
                    p_fos.flush();
                    p_fos.close();
                    p_fos = null;
                }

//            if (!p_JarFile.delete()) throw new IOException("I can not remove old JAR file");
                String s_dirname = p_JarFile.getParent();
                if (s_dirname == null) s_dirname = "";

                FileOutputStream p_fos = new FileOutputStream(p_JarFile.getAbsolutePath() + ".new");

                p_fos.write(ab_newJarFile);
            }
            else
            {
                System.out.println("I am looking for any watermark in the jar...");
                new Jatermark().checkWatermark(ab_JarArray);
            }
        }
        catch (Throwable ex)
        {
            ex.printStackTrace();
        }
    }
}
