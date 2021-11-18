package com.igormaznitsa.Utils;

import com.nokia.mid.ui.DirectGraphics;

/*
* Copyright © 2002 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
* Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
* is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
* archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
* engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
* construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
* warranty of fitness for such uses.
*/

public class WDecoder
{
    private static final int INIT_BITS = 9;
    private static final int MAX_BITS = 12;
    //private static final int TABLE_SIZE = 5021;
    private static int i_maxTableSize,i_maxDecodeStack;
    private static final int CLEAR_TABLE = 256;
    private static final int TERMINATOR = 257;
    private static final int FIRST_CODE = 258;
    //private static final int DECODE_STACK_DEPTH = 1024;

    private static int i_input_bit_count = 0;
    private static int i_input_bit_buffer = 0;
    private static int i_num_bits = INIT_BITS;
    private static int i_max_code;
    private static short[] ai_prefix_code;
    private static byte[] b_decode_stack;
    private static byte[] ab_append_character;

    private static int i_next_code;
    private static int i_new_code;
    private static int i_old_code;
    private static int i_character;
    private static boolean lg_clear_flag;
    private static int i_stringindex;

    private static byte[] ab_inputArray;
    private static int i_StartPos;
    private static boolean lg_nextflag;

    /**
     * Reset the decompressor
     * @param _input a byte array for stream input
     * @param _startpos the start position of the npcked data in the input array
     */
    public static void reset(byte[] _input, int _startpos)
    {
        i_input_bit_count = 0;
        i_input_bit_buffer = 0;
        i_num_bits = INIT_BITS;
        i_max_code = ((1 << (INIT_BITS)) - 1);
        i_next_code = FIRST_CODE;
        i_new_code = 0;
        i_old_code = 0;
        i_character = 0;
        lg_clear_flag = true;

        i_maxTableSize = ((_input[_startpos] & 0xFF) << 8) | (_input[_startpos + 1] & 0xFF);
        _startpos += 2;
        i_maxDecodeStack = ((_input[_startpos] & 0xFF) << 8) | (_input[_startpos + 1] & 0xFF);
        _startpos += 2;

        b_decode_stack = new byte[i_maxDecodeStack];
        ai_prefix_code = new short[i_maxTableSize];
        ab_append_character = new byte[i_maxTableSize];

        ab_inputArray = _input;
        i_StartPos = _startpos;
        i_stringindex = -1;
        lg_nextflag = false;
    }

    /**
     * Expanding of a block contains specified length to the buffer
     * @param _buffer a byte array which will be used as the buffer
     * @param _expandpos the start expand position in the buffer
     * @param _len the length of an expanded string
     * @return the length of a readed block
     */
    public static int expandBlock(byte[] _buffer, int _expandpos, int _len) //throws IOException
    {
        int i_len = 0;
        if (b_decode_stack == null) return -1;
//        try
//        {
        while (i_len < _len)
        {
            if (!lg_nextflag)
            {
                while (i_input_bit_count <= 24)
                {
                    i_input_bit_buffer |= (((int) ab_inputArray[i_StartPos++]) & 0xFF) << (24 - i_input_bit_count);
                    i_input_bit_count += 8;
                }

                i_new_code = i_input_bit_buffer >>> (32 - i_num_bits);
                i_input_bit_buffer <<= i_num_bits;
                i_input_bit_count -= i_num_bits;

                if (i_new_code == TERMINATOR)
                {
                    b_decode_stack = null;
                    ai_prefix_code = null;
                    ab_append_character = null;
                    ab_inputArray = null;
                    Runtime.getRuntime().gc();
                    break;
                }

                if (lg_clear_flag)
                {
                    /* Initialize or Re-Initialize */
                    lg_clear_flag = false;
                    i_old_code = i_new_code;   /* The next three lines have been moved */
                    i_character = i_old_code;  /* from the original */
                    _buffer[_expandpos++] = (byte) i_old_code;
                    i_len++;
                    continue;
                }
                if (i_new_code == CLEAR_TABLE)
                {
                    /* Clear string table */
                    lg_clear_flag = true;
                    i_num_bits = INIT_BITS;
                    i_next_code = FIRST_CODE;
                    i_max_code = ((1 << (i_num_bits)) - 1);
                    continue;
                }
                int i_code;
                int i_offst;
                if (i_new_code >= i_next_code)
                {
                    b_decode_stack[0] = (byte) i_character;
                    i_code = i_old_code;
                    i_offst = 1;
                }
                else
                {
                    i_code = i_new_code;
                    i_offst = 0;
                }

//                i_stringindex = decode_string(b_decode_stack, i_offst, i_code);
                //------------------------
                int i_indx = i_offst;
                int i_lcode = i_code;
                while (i_lcode > 255)
                {
                    b_decode_stack[i_indx++] = ab_append_character[i_lcode];
                    i_lcode = ai_prefix_code[i_lcode];
                }
                b_decode_stack[i_indx] = (byte) i_lcode;
                i_stringindex = i_indx;
                //------------------------

                i_character = b_decode_stack[i_stringindex];
            }

            while (i_stringindex >= 0)
            {
                _buffer[_expandpos++] = b_decode_stack[i_stringindex--];
                i_len++;
                if (i_len == _len)
                {
                    lg_nextflag = true;
                    return i_len;
                }
            }
            lg_nextflag = false;

            if (i_next_code <= i_max_code)
            {      /* Add to string table if not full */
                ai_prefix_code[i_next_code] = (short) i_old_code;
                ab_append_character[i_next_code++] = (byte) i_character;
                if (i_next_code == i_max_code && i_num_bits < MAX_BITS)
                    i_max_code = ((1 << (++i_num_bits)) - 1);
            }
            i_old_code = i_new_code;
        }
        /*}
        catch (ArrayIndexOutOfBoundsException e)
        {
            e.printStackTrace();
            throw new IOException("AE:1");
        }*/
        return i_len;
    }

    /**
     * Decompression of a packed image to a direct graphics context
     * @param _context A direct graphics object for output of the image
     * @param _x the X coordinate of output
     * @param _y the Y coordinate of output
     * @param _array an input array
     * @param _offset the offset of compressed data in the input array
     */
    public static void decodeImageToDirectGraphics(DirectGraphics _context, int _x, int _y, byte[] _array, int _offset)
    {
        int i_width = _array[_offset++] & 0xFF;
        int i_height = _array[_offset++] & 0xFF;
        int i_iterations = _array[_offset++] & 0xFF;

        int i_decodeTableLength = _array[_offset++];
        byte[] ab_decodeTable = new byte[i_decodeTableLength];
        int i_indx = 0;
        while (i_indx < i_decodeTableLength)
        {
            ab_decodeTable[i_indx++] = _array[_offset++];
        }

        short[] ash_rgbbuffer = new short[i_width];

        int i_xwidth = (i_width & 0x1) == 0 ? i_width : i_width + 1;

        WDecoder.reset(_array, _offset);

        int i_halfxw = i_xwidth >>> 1;
        int[] i_buffer = new int[i_xwidth + (i_halfxw << 1)];
        int i_buffer_len = i_buffer.length;
        byte[] ab_buffer = new byte[i_buffer_len >> 1];
        int i_bufferB_len = ab_buffer.length;

        for (int ly = 0; ly < i_height; ly++)
        {
            int i_len = WDecoder.expandBlock(ab_buffer, 0, i_bufferB_len);

            i_indx = 0;
            int li = 0;
            while (li < i_bufferB_len)
            {
                int i_value = ab_buffer[li++] & 0xFF;
                int i_val0 = ab_decodeTable[i_value >>> 4];
                int i_val1 = ab_decodeTable[i_value & 0xF];
                i_buffer[i_indx++] = i_val0;
                i_buffer[i_indx++] = i_val1;
            }

            if (i_len < 0) break;
            li = 0;
            while (li < i_buffer_len)
            {
                int f_b1 = i_buffer[li];
                int f_b2 = i_buffer[li + 1];
                int ln = 0;
                int f_a1,f_a2;
                while (ln < i_iterations)
                {
                    f_a1 = f_b1 + f_b2;
                    f_a2 = f_b1 - f_b2;
                    f_b1 = f_a1;
                    f_b2 = f_a2;
                    ln++;
                }
                i_buffer[li++] = f_b1;
                i_buffer[li++] = f_b2;
            }

            int i_offsetU = i_xwidth;
            int i_offsetV = i_xwidth + i_halfxw;
            long l_u = 0;
            long l_y = 0;
            long l_v = 0;

            long l_u_r = 0;
            long l_u_g = 0;
            long l_u_b = 0;

            long l_v_r = 0;
            long l_v_g = 0;
            long l_v_b = 0;

            long l_ur_plus_l_vr = 0;
            long l_ug_minus_l_vg = 0;
            long l_ub_plus_l_vb = 0;

            for (int i_offsetY = 0; i_offsetY < i_width; i_offsetY++)
            {
                l_y = ((long) i_buffer[i_offsetY]) << 20;

                if ((i_offsetY & 0x1) == 0)
                {
                    l_u = ((long) i_buffer[i_offsetU++]);
                    l_v = ((long) i_buffer[i_offsetV++]);

                    l_u_r = 1002617L * l_u;
                    l_u_g = 285936L * l_u;
                    l_u_b = 1157355L * l_u;

                    l_v_r = 651617L * l_v;
                    l_v_g = 678229L * l_v;
                    l_v_b = 1783229L * l_v;

                    l_ur_plus_l_vr = l_u_r + l_v_r;
                    l_ug_minus_l_vg = l_u_g + l_v_g;
                    l_ub_plus_l_vb = l_u_b - l_v_b;
                }

                //r = y + .95617 * u + .62143 * v;
                int r = (int) ((l_y + l_ur_plus_l_vr) >> 38);
                // g = y - 0.27269 * u - .64681 * v;
                int g = (int) ((l_y - l_ug_minus_l_vg) >> 38);
                //b = *p++ = (*y++) - 1.10374 * (*u++) + 1.70062 * (*v++);
                int b = (int) ((l_y - l_ub_plus_l_vb) >> 38);

                if (r > 0xFF) r = 0xFF;
                if (g > 0xFF) g = 0xFF;
                if (b > 0xFF) b = 0xFF;

                if (r < 0) r = 0;
                if (g < 0) g = 0;
                if (b < 0) b = 0;

                ash_rgbbuffer[i_offsetY] = (short) (((r & 0xF0) << 4) | (g & 0xF0) | (b >>> 4));
            }
            _context.drawPixels(ash_rgbbuffer, false, 0, i_width, _x, _y, i_width, 1, 0, DirectGraphics.TYPE_USHORT_444_RGB);
            _y++;
        }
        ab_decodeTable = null;
        ab_buffer = null;
        b_decode_stack = null;
        ai_prefix_code = null;
        ab_append_character = null;
        ash_rgbbuffer = null;
        i_buffer = null;
        Runtime.getRuntime().gc();
    }
}

