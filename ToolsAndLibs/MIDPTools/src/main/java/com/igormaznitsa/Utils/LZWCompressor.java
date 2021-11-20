package com.igormaznitsa.Utils;

import java.io.*;

public class LZWCompressor
{
    private static final int INIT_BITS = 9;
    private static final int MAX_BITS = 12;           /* Do not exceed 14 with this program */
    private static final int HASHING_SHIFT = MAX_BITS - 8;

    private static final int TABLE_SIZE = 5021;      /* number somewhat larger than 2^MAX_BITS.*/

    private static final int CLEAR_TABLE = 256;    /* Code to flush the string table */
    private static final int TERMINATOR = 257;    /* To mark EOF Condition, instead of MAX_VALUE */
    private static final int FIRST_CODE = 258;    /* First available code for code_value table */

    private static final int CHECK_TIME = 1;    /* Check comp ratio every CHECK_TIME chars input */

    private int[] code_value;
    private int[] prefix_code;
    private int[] append_character;

    private int bytes_in = 0;
    private int checkpoint = CHECK_TIME;

    private int bytes_out = 0;
    private int max_code;
    private int num_bits = INIT_BITS;

    private int output_bit_count = 0;
    private int output_bit_buffer = 0;

    public LZWCompressor()
    {
        code_value = new int[TABLE_SIZE];
        prefix_code = new int[TABLE_SIZE];
        append_character = new int[TABLE_SIZE];
    }

    public void reset()
    {
        num_bits = INIT_BITS;
        max_code = (1 << (num_bits)) - 1;
        checkpoint = CHECK_TIME;

        bytes_in = 0;
        bytes_out = 0;
        output_bit_count = 0;
        output_bit_buffer = 0;
    }

    public void compress(DataInputStream _input, DataOutputStream _output) throws IOException
    {
        int next_code = FIRST_CODE;
        int character;
        int string_code;
        int index;
        int i;             /* All purpose integer */
        int ratio_new;         /* New compression ratio as a percentage */
        int ratio_old = 100;     /* Original ratio at 100% */

        /* Initialize the string table first */
        for (i = 0; i < TABLE_SIZE; i++) code_value[i] = -1;
        //System.out.println("Compressing\n");
        string_code = _input.readUnsignedByte();     /* Get the first code */

        /* This is the main compression loop. Notice when the table is full we try
         * to increment the code size. Only when num_bits == MAX_BITS and the code
         * value table is full do we start to monitor the compression ratio.
         */
        while (true)
        {
            try
            {
                character = _input.readUnsignedByte();
            }
            catch (EOFException e)
            {
                break;
            }

            if ((++bytes_in % 1000) == 0)
            {     /* Count input bytes and pacifier */
                //System.out.print('.');
            }
            index = find_match(string_code, character);
            if (code_value[index] != -1)
                string_code = code_value[index];
            else
            {
                if (next_code <= max_code)
                {
                    code_value[index] = next_code++;
                    prefix_code[index] = string_code;
                    append_character[index] = character;
                }
                output_code(_output, string_code);   /* Send out current code */
                string_code = character;
                if (next_code > max_code)
                {      /* Is table Full? */
                    if (num_bits < MAX_BITS)
                    {     /* Any more bits? */
                        //System.out.print('+');
                        max_code = ((1 << (++num_bits)) - 1);  /* Increment code size then */
                    }
                    else if (bytes_in > checkpoint)
                    {         /* At checkpoint? */
                        if (num_bits == MAX_BITS)
                        {
                            ratio_new = bytes_out * 100 / bytes_in; /* New compression ratio */
                            if (ratio_new > ratio_old)
                            {        /* Has ratio degraded? */
                                output_code(_output, CLEAR_TABLE); /* YES,flush string table */
                                //System.out.print('C');
                                num_bits = INIT_BITS;
                                next_code = FIRST_CODE;        /* Reset to FIRST_CODE */
                                max_code = ((1 << (num_bits)) - 1); /* Re-Initialize this stuff */
                                bytes_in = bytes_out = 0;
                                ratio_old = 100;               /* Reset compression ratio */
                                for (i = 0; i < TABLE_SIZE; i++)   /* Reset code value array */
                                    code_value[i] = -1;
                            }
                            else                                /* NO, then save new */
                                ratio_old = ratio_new;            /* compression ratio */
                        }
                        checkpoint = bytes_in + CHECK_TIME;    /* Set new checkpoint */
                    }
                }
            }
        }
        output_code(_output, string_code);   /* Output the last code */
        if (next_code == max_code)
        {       /* Handles special case for bit */
            ++num_bits;                     /* increment on EOF */
            //System.out.print('+');
        }
        output_code(_output, TERMINATOR);    /* Output the end of buffer code */
        output_code(_output, 0);             /* Flush the output buffer */
        output_code(_output, 0);
        output_code(_output, 0);
        //System.out.print('\n');
    }


    private int find_match(int hash_prefix, int hash_character)
    {
        int index, offset;

        index = ((hash_character << HASHING_SHIFT) ^ hash_prefix);
        if (index == 0)
            offset = 1;
        else
            offset = TABLE_SIZE - index;

        while (true)
        {
            if (code_value[index] == -1) return (index);

            if (prefix_code[index] == hash_prefix && append_character[index] == hash_character) return index;

            index -= offset;
            if (index < 0) index += TABLE_SIZE;
        }
    }

    private void output_code(OutputStream _output, int _code) throws IOException
    {
        output_bit_buffer |= _code << (32 - num_bits - output_bit_count);
        output_bit_count += num_bits;
        while (output_bit_count >= 8)
        {
            _output.write(output_bit_buffer >>> 24);
            output_bit_buffer <<= 8;
            output_bit_count -= 8;
            bytes_out++;                    /* ADDED for compression monitoring */
        }
    }
}
