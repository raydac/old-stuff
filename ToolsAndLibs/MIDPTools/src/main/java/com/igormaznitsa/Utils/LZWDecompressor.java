package com.igormaznitsa.Utils;

import java.io.IOException;

public class LZWDecompressor
{
    private static final int INIT_BITS = 9;
    private static final int MAX_BITS = 12;
    private static final int TABLE_SIZE = 5021;
    private static final int CLEAR_TABLE = 256;
    private static final int TERMINATOR = 257;
    private static final int FIRST_CODE = 258;
    private static final int DECODE_STACK_DEPTH = 1024;

    private int i_input_bit_count = 0;
    private int i_input_bit_buffer = 0;
    private int i_num_bits = INIT_BITS;
    private int i_max_code;
    private short[] ai_prefix_code;
    private byte[] b_decode_stack;
    private byte[] ab_append_character;

    private int i_next_code;
    private int i_new_code;
    private int i_old_code;
    private int i_character;
    private int i_counter;
    private boolean lg_clear_flag;
    private int i_stringindex;

    public int i_maxTableSize;
    public int i_maxStackDepth;

    /**
     * The constructor
     */
    public LZWDecompressor()
    {
        reset();
    }

    /**
     * Reset the decompressor
     */
    public void reset()
    {
        i_maxTableSize = 0;
        i_maxStackDepth = 0;

        i_input_bit_count = 0;
        i_input_bit_buffer = 0;
        i_num_bits = INIT_BITS;
        i_max_code = ((1 << (INIT_BITS)) - 1);
        i_next_code = FIRST_CODE;
        i_new_code = 0;
        i_old_code = 0;
        i_character = 0;
        i_counter = 0;
        lg_clear_flag = true;
    }

    private byte[] ab_inputArray;
    private int i_StartPos;

    public void initLineLZW(byte[] _input, int _startpos)
    {
        reset();
        b_decode_stack = new byte[DECODE_STACK_DEPTH];
        ai_prefix_code = new short[TABLE_SIZE];
        ab_append_character = new byte[TABLE_SIZE];

        ab_inputArray = _input;
        i_StartPos = _startpos;
        i_stringindex = -1;
    }

    public int partlyExpand(byte[] _buffer, int _expandpos, int _len) throws IOException
    {
        int i_len = 0;

        try
        {
            while (i_len < _len)
            {
                if (i_stringindex < 0)
                {
                    while (i_input_bit_count <= 24)
                    {
                        i_input_bit_buffer |= (((int) ab_inputArray[i_StartPos++]) & 0xFF) << (24 - i_input_bit_count);
                        i_input_bit_count += 8;
                    }

                    i_new_code = i_input_bit_buffer >>> (32 - i_num_bits);
                    i_input_bit_buffer <<= i_num_bits;
                    i_input_bit_count -= i_num_bits;

                    if (i_new_code == TERMINATOR) break;

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
                    if (++i_counter == 1000) i_counter = 0;
                    if (i_new_code >= i_next_code)
                    {
                        b_decode_stack[0] = (byte) i_character;
                        i_stringindex = decode_string(b_decode_stack, 1, i_old_code);
                    }
                    else
                        i_stringindex = decode_string(b_decode_stack, 0, i_new_code);

                    i_character = b_decode_stack[i_stringindex];
                    if (i_stringindex>i_maxStackDepth) i_maxStackDepth = i_stringindex;
                }

                while (i_stringindex >= 0)
                {
                    if (i_stringindex>i_maxStackDepth) i_maxStackDepth = i_stringindex;
                    _buffer[_expandpos++] = b_decode_stack[i_stringindex--];
                    i_len++;
                    if (i_len == _len) return i_len;
                }
                i_stringindex = -1;

                if (i_next_code <= i_max_code)
                {      /* Add to string table if not full */
                    ai_prefix_code[i_next_code] = (short) i_old_code;
                    ab_append_character[i_next_code++] = (byte) i_character;
                    if (i_next_code>i_maxTableSize) i_maxTableSize=i_next_code;

                    if (i_next_code == i_max_code && i_num_bits < MAX_BITS)
                    {
                        int n = ++i_num_bits;
                        i_max_code = ((1 << (n)) - 1);
                    }
                }
                i_old_code = i_new_code;
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            e.printStackTrace();
            throw new IOException("AE:1");
        }
        finally
        {
            b_decode_stack = null;
            ai_prefix_code = null;
            ab_append_character = null;
            Runtime.getRuntime().gc();
        }

        return i_len;
    }

    /**
     * Decompression of LZW archive from an byte array to a buffer
     * @param _input an input byte array
     * @param _startpos the start position of the decompression
     * @param _buffer an output byte array
     * @param _expandpos the position for data expanding
     * @throws java.io.IOException the function throws the exception if it has any error in archive
     */
    public void fullExpand(byte[] _input, int _startpos, byte[] _buffer, int _expandpos) throws IOException
    {
        b_decode_stack = new byte[DECODE_STACK_DEPTH];
        ai_prefix_code = new short[TABLE_SIZE];
        ab_append_character = new byte[TABLE_SIZE];

        try
        {
            int i_stringindex;

            while (true)
            {
                while (i_input_bit_count <= 24)
                {
                    i_input_bit_buffer |= (((int) _input[_startpos++]) & 0xFF) << (24 - i_input_bit_count);
                    i_input_bit_count += 8;
                }
                i_new_code = i_input_bit_buffer >>> (32 - i_num_bits);
                i_input_bit_buffer <<= i_num_bits;
                i_input_bit_count -= i_num_bits;

                if (i_new_code == TERMINATOR) break;

                if (lg_clear_flag)
                {       /* Initialize or Re-Initialize */
                    lg_clear_flag = false;
                    i_old_code = i_new_code;   /* The next three lines have been moved */
                    i_character = i_old_code;  /* from the original */
                    _buffer[_expandpos++] = (byte) i_old_code;
                    continue;
                }
                if (i_new_code == CLEAR_TABLE)
                {     /* Clear string table */
                    lg_clear_flag = true;
                    i_num_bits = INIT_BITS;
                    i_next_code = FIRST_CODE;
                    i_max_code = ((1 << (i_num_bits)) - 1);
                    continue;
                }
                if (++i_counter == 1000) i_counter = 0;
                if (i_new_code >= i_next_code)
                {
                    b_decode_stack[0] = (byte) i_character;
                    i_stringindex = decode_string(b_decode_stack, 1, i_old_code);
                }
                else
                    i_stringindex = decode_string(b_decode_stack, 0, i_new_code);

                i_character = b_decode_stack[i_stringindex];
                if (i_stringindex>i_maxStackDepth) i_maxStackDepth = i_stringindex;
                while (i_stringindex >= 0) _buffer[_expandpos++] = b_decode_stack[i_stringindex--];

                if (i_next_code <= i_max_code)
                {      /* Add to string table if not full */
                    ai_prefix_code[i_next_code] = (short) i_old_code;
                    ab_append_character[i_next_code++] = (byte) i_character;
                    if (i_next_code > i_maxTableSize) i_maxTableSize = i_next_code;

                    if (i_next_code == i_max_code && i_num_bits < MAX_BITS)
                    {
                        int n = ++i_num_bits;
                        i_max_code = ((1 << (n)) - 1);
                    }
                }
                i_old_code = i_new_code;
            }
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            e.printStackTrace();
            throw new IOException("AE:1");
        }
        finally
        {
            b_decode_stack = null;
            ai_prefix_code = null;
            ab_append_character = null;
            Runtime.getRuntime().gc();
        }
    }

    private int decode_string(byte[] _buffer, int _pos, int _code) throws IOException
    {
        int i = 0;
        int i_indx = _pos;
        while (_code > 255)
        {
            _buffer[i_indx++] = ab_append_character[_code];
            if (_code>i_maxTableSize) i_maxTableSize = _code;
            _code = ai_prefix_code[_code];

            if (i++ >= DECODE_STACK_DEPTH) throw new IOException("AE:0");
        }
        _buffer[i_indx] = (byte) _code;
        return i_indx;
    }
}
