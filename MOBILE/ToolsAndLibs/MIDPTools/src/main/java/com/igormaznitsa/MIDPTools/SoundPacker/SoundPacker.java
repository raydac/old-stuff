package com.igormaznitsa.MIDPTools.SoundPacker;

import com.siemens.mp.game.MelodyComposer;

import java.io.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.Iterator;

/*
 * Copyright (C) 2002 Igor A. Maznitsa (igor.maznitsa@igormaznitsa.com).  All rights reserved.
 * Software is confidential and copyrighted. Title to Software and all associated intellectual property rights
 * is retained by Igor A. Maznitsa. You may not make copies of Software, other than a single copy of Software for
 * archival purposes.  Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse
 * engineer Software.  You acknowledge that Software is not designed, licensed or intended for use in the design,
 * construction, operation or maintenance of any nuclear facility. Igor A. Maznitsa disclaims any express or implied
 * warranty of fitness for such uses.
 *
 * This is an utility for packing OTT,MIDI and WAV files in one bin block.
 * Command line:
 *  (/M:model) [work_dir] [package_name]
 *  work_dir - directory where sound files being getted
 *  package_name - name of sound package
 */

public class SoundPacker
{
    private static final int MELODY_TYPE_OTT = 0;
    private static final int MELODY_TYPE_WAV = 1;
    private static final int MELODY_TYPE_MIDI = 2;
    private static final int MELODY_TYPE_MP3 = 3;
    private static final int MELODY_TYPE_MMF = 4;
    private static final int MELODY_TYPE_AMR = 5;

    private static final float LENGTH_FULL = 4f;
    private static final float LENGTH_HALF = 2f;
    private static final float LENGTH_1_4 = 1f;
    private static final float LENGTH_1_8 = 0.5f;
    private static final float LENGTH_1_16 = 0.25f;
    private static final float LENGTH_1_32 = 0.125f;

    private static final int ENTITY_NOTE = 0;
    private static final int ENTITY_SCALE = 1;
    private static final int ENTITY_STYLE = 2;
    private static final int ENTITY_TEMPO = 3;
    private static final int ENTITY_VOLUME = 4;

    private static final int NOTE_PAUSE = 0;
    private static final int NOTE_C = 1;
    private static final int NOTE_Cis = 2;
    private static final int NOTE_D = 3;
    private static final int NOTE_Dis = 4;
    private static final int NOTE_E = 5;
    private static final int NOTE_F = 6;
    private static final int NOTE_Fis = 7;
    private static final int NOTE_G = 8;
    private static final int NOTE_Gis = 9;
    private static final int NOTE_A = 10;
    private static final int NOTE_Ais = 11;
    private static final int NOTE_H = 12;

    private static final int DURATION_FULL = 0;
    private static final int DURATION_1_2 = 1;
    private static final int DURATION_1_4 = 2;
    private static final int DURATION_1_8 = 3;
    private static final int DURATION_1_16 = 4;
    private static final int DURATION_1_32 = 5;

    private static final int SPECIFIER_NONE = 0;
    private static final int SPECIFIER_DOTTED = 1;
    private static final int SPECIFIER_DOUBLEDOTTED = 2;
    private static final int SPECIFIER_2_3_LENGTH = 3;

    private static final int SCALE_1 = 0;
    private static final int SCALE_2 = 1;
    private static final int SCALE_3 = 2;
    private static final int SCALE_4 = 3;

    private static final int STYLE_NATURAL = 0;
    private static final int STYLE_CONTINOUS = 1;
    private static final int STYLE_STACCATO = 2;

    private static final int BEATSPERMINUTE_25 = 0;
    private static final int BEATSPERMINUTE_28 = 1;
    private static final int BEATSPERMINUTE_31 = 2;
    private static final int BEATSPERMINUTE_35 = 3;
    private static final int BEATSPERMINUTE_40 = 4;
    private static final int BEATSPERMINUTE_45 = 5;
    private static final int BEATSPERMINUTE_50 = 6;
    private static final int BEATSPERMINUTE_56 = 7;
    private static final int BEATSPERMINUTE_63 = 8;
    private static final int BEATSPERMINUTE_70 = 9;
    private static final int BEATSPERMINUTE_80 = 10;
    private static final int BEATSPERMINUTE_90 = 11;
    private static final int BEATSPERMINUTE_100 = 12;
    private static final int BEATSPERMINUTE_112 = 13;
    private static final int BEATSPERMINUTE_125 = 14;
    private static final int BEATSPERMINUTE_140 = 15;
    private static final int BEATSPERMINUTE_160 = 16;
    private static final int BEATSPERMINUTE_180 = 17;
    private static final int BEATSPERMINUTE_200 = 18;
    private static final int BEATSPERMINUTE_225 = 19;
    private static final int BEATSPERMINUTE_250 = 20;
    private static final int BEATSPERMINUTE_285 = 21;
    private static final int BEATSPERMINUTE_320 = 22;
    private static final int BEATSPERMINUTE_355 = 23;
    private static final int BEATSPERMINUTE_400 = 24;
    private static final int BEATSPERMINUTE_450 = 25;
    private static final int BEATSPERMINUTE_500 = 26;
    private static final int BEATSPERMINUTE_565 = 27;
    private static final int BEATSPERMINUTE_635 = 28;
    private static final int BEATSPERMINUTE_715 = 29;
    private static final int BEATSPERMINUTE_800 = 30;
    private static final int BEATSPERMINUTE_900 = 31;

    private static final int VOLUME_TONEOFF = 0;
    private static final int VOLUME_LEVEL1 = 1;
    private static final int VOLUME_LEVEL2 = 2;
    private static final int VOLUME_LEVEL3 = 3;
    private static final int VOLUME_LEVEL4 = 4;
    private static final int VOLUME_LEVEL5 = 5;
    private static final int VOLUME_LEVEL6 = 6;
    private static final int VOLUME_LEVEL7 = 7;
    private static final int VOLUME_LEVEL8 = 8;
    private static final int VOLUME_LEVEL9 = 9;
    private static final int VOLUME_LEVEL10 = 10;
    private static final int VOLUME_LEVEL11 = 11;
    private static final int VOLUME_LEVEL12 = 12;
    private static final int VOLUME_LEVEL13 = 13;
    private static final int VOLUME_LEVEL14 = 14;
    private static final int VOLUME_LEVEL15 = 15;

    private static final int MODEL_M50_SL45 = 0;
    private static final int MODEL_N6310i_N3410 = 1;
    private static final int MODEL_N7650 = 2;
    private static final int MODEL_C55 = 3;
    private static final int MODEL_S55 = 4;
    private static final int MODEL_S300 = 5;
    private static final int MODEL_OCAP = 6;

    private static final String[] as_formats_M50_SL45_N6310i_N3410 = new String[]{".ott"};
    private static final String[] as_formats_C55 = new String[]{".mid", ".wav"};
    private static final String[] as_formats_S55 = new String[]{".mid"};
    private static final String[] as_formats_N7650 = new String[]{".ott",".wav",".amr"};
    private static final String[] as_formats_OCAP = new String[]{".wav"};
    private static final String[] as_formats_S300 = new String[]{".mmf"};

    private static int i_LastOttMillisecindsLength;

    private static int getTempCoeff(int coeff)
    {
        int tempocoeff = 0;
        switch (coeff)
        {
            case BEATSPERMINUTE_25:
                tempocoeff = 2400;
                break;
            case BEATSPERMINUTE_28:
                tempocoeff = 2140;
                break;
            case BEATSPERMINUTE_31:
                tempocoeff = 1900;
                break;
            case BEATSPERMINUTE_35:
                tempocoeff = 1700;
                break;
            case BEATSPERMINUTE_40:
                tempocoeff = 1510;
                break;
            case BEATSPERMINUTE_45:
                tempocoeff = 1350;
                break;
            case BEATSPERMINUTE_50:
                tempocoeff = 1200;
                break;
            case BEATSPERMINUTE_56:
                tempocoeff = 1070;
                break;
            case BEATSPERMINUTE_63:
                tempocoeff = 950;
                break;
            case BEATSPERMINUTE_70:
                tempocoeff = 850;
                break;
            case BEATSPERMINUTE_80:
                tempocoeff = 760;
                break;
            case BEATSPERMINUTE_90:
                tempocoeff = 670;
                break;
            case BEATSPERMINUTE_100:
                tempocoeff = 600;
                break;
            case BEATSPERMINUTE_112:
                tempocoeff = 540;
                break;
            case BEATSPERMINUTE_125:
                tempocoeff = 480;
                break;
            case BEATSPERMINUTE_140:
                tempocoeff = 430;
                break;
            case BEATSPERMINUTE_160:
                tempocoeff = 380;
                break;
            case BEATSPERMINUTE_180:
                tempocoeff = 340;
                break;
            case BEATSPERMINUTE_200:
                tempocoeff = 300;
                break;
            case BEATSPERMINUTE_225:
                tempocoeff = 270;
                break;
            case BEATSPERMINUTE_250:
                tempocoeff = 240;
                break;
            case BEATSPERMINUTE_285:
                tempocoeff = 210;
                break;
            case BEATSPERMINUTE_320:
                tempocoeff = 190;
                break;
            case BEATSPERMINUTE_355:
                tempocoeff = 170;
                break;
            case BEATSPERMINUTE_400:
                tempocoeff = 150;
                break;
            case BEATSPERMINUTE_450:
                tempocoeff = 130;
                break;
            case BEATSPERMINUTE_500:
                tempocoeff = 120;
                break;
            case BEATSPERMINUTE_565:
                tempocoeff = 100;
                break;
            case BEATSPERMINUTE_635:
                tempocoeff = 90;
                break;
            case BEATSPERMINUTE_715:
                tempocoeff = 80;
                break;
            case BEATSPERMINUTE_800:
                tempocoeff = 70;
                break;
            case BEATSPERMINUTE_900:
                tempocoeff = 60;
                break;
        }
        return tempocoeff;
    }

    private static byte[] OttToSiemens(byte[] is) throws IOException
    {
        boolean isunicode = false;
        String name = "";
        BitInputStream bis = new BitInputStream(new ByteArrayInputStream(is));
        ByteArrayOutputStream p_baos = new ByteArrayOutputStream();
        for (int li = 0; li < 2; li++) p_baos.write(0);

        int notescale = SCALE_2;
        int bpm = BEATSPERMINUTE_63;
        int i_milliseclen = 0;
        int tempocoeff = getTempCoeff(bpm);

        while (true)
        {
            int cmnd = bis.readBits(8);
            if (cmnd == 0) break;
            for (int li = 0; li < cmnd; li++)
            {
                int cmpl = bis.readBits(7);
                switch (cmpl)
                {
                    // CANCEL COMMAND
                    case 0x05:
                        {
                        }
                        ;
                        break;
                        // RINGING TONE PROGRAMMING
                    case 0x25:
                        {
                        }
                        ;
                        break;
                        // SOUND
                    case 0x1D:
                        {
                            // Load song type
                            int stype = bis.readBits(3);
                            switch (stype)
                            {
                                // BASIC SONG
                                case 1:
                                    {
                                        int titlelength = bis.readBits(4);
                                        for (int lli = 0; lli < titlelength; lli++)
                                        {
                                            if (isunicode)
                                            {
                                                char chr = (char) bis.readBits(16);
                                                name += chr;
                                            }
                                            else
                                            {
                                                byte chr = (byte) bis.readBits(8);
                                                name += (char) chr;
                                            }
                                        }
                                        int songsequencelength = bis.readBits(8);
                                        for (int lli = 0; lli < songsequencelength; lli++)
                                        {
                                            // Reading of pattern header
                                            int lph = bis.readBits(3);
                                            if (lph != 0) throw new IOException("Error OTT file");
                                            // Reading of pattern id
                                            int ptrnid = bis.readBits(2);
                                            // Reading of pattern loop value
                                            int loopvalue = bis.readBits(4);

                                            ByteArrayOutputStream p_patternArray = new ByteArrayOutputStream(256);

                                            // Reading pattern specifier
                                            int ptrnspcfr = bis.readBits(8);
                                            if (ptrnspcfr != 0)
                                            {
                                                // New pattern
                                                for (int lxi = 0; lxi < ptrnspcfr; lxi++)
                                                {
                                                    int instrid = bis.readBits(3);
                                                    switch (instrid)
                                                    {
                                                        // Note
                                                        case 1:
                                                            {
                                                                // Reading note value
                                                                int notevalue = bis.readBits(4);

                                                                switch (notevalue)
                                                                {
                                                                    case NOTE_A:
                                                                        {
                                                                            switch (notescale)
                                                                            {
                                                                                case SCALE_1:
                                                                                    notevalue = MelodyComposer.TONE_A0;
                                                                                    break;
                                                                                case SCALE_2:
                                                                                    notevalue = MelodyComposer.TONE_A1;
                                                                                    break;
                                                                                case SCALE_3:
                                                                                    notevalue =
                                                                                            MelodyComposer.TONE_A2;
                                                                                    break;
                                                                                case SCALE_4:
                                                                                    notevalue = MelodyComposer.TONE_A3;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case NOTE_Ais:
                                                                        {
                                                                            switch (notescale)
                                                                            {
                                                                                case SCALE_1:
                                                                                    notevalue = MelodyComposer.TONE_AIS0;
                                                                                    break;
                                                                                case SCALE_2:
                                                                                    notevalue = MelodyComposer.TONE_AIS1;
                                                                                    break;
                                                                                case SCALE_3:
                                                                                    notevalue = MelodyComposer.TONE_AIS2;
                                                                                    break;
                                                                                case SCALE_4:
                                                                                    notevalue = MelodyComposer.TONE_AIS3;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case NOTE_C:
                                                                        {
                                                                            switch (notescale)
                                                                            {
                                                                                case SCALE_1:
                                                                                    notevalue = MelodyComposer.TONE_C0;
                                                                                    break;
                                                                                case SCALE_2:
                                                                                    notevalue = MelodyComposer.TONE_C1;
                                                                                    break;
                                                                                case SCALE_3:
                                                                                    notevalue = MelodyComposer.TONE_C2;
                                                                                    break;
                                                                                case SCALE_4:
                                                                                    notevalue = MelodyComposer.TONE_C3;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case NOTE_Cis:
                                                                        {
                                                                            switch (notescale)
                                                                            {
                                                                                case SCALE_1:
                                                                                    notevalue = MelodyComposer.TONE_CIS0;
                                                                                    break;
                                                                                case SCALE_2:
                                                                                    notevalue = MelodyComposer.TONE_CIS1;
                                                                                    break;
                                                                                case SCALE_3:
                                                                                    notevalue = MelodyComposer.TONE_CIS2;
                                                                                    break;
                                                                                case SCALE_4:
                                                                                    notevalue = MelodyComposer.TONE_CIS3;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case NOTE_D:
                                                                        {
                                                                            switch (notescale)
                                                                            {
                                                                                case SCALE_1:
                                                                                    notevalue = MelodyComposer.TONE_D0;
                                                                                    break;
                                                                                case SCALE_2:
                                                                                    notevalue = MelodyComposer.TONE_D1;
                                                                                    break;
                                                                                case SCALE_3:
                                                                                    notevalue = MelodyComposer.TONE_D2;
                                                                                    break;
                                                                                case SCALE_4:
                                                                                    notevalue = MelodyComposer.TONE_D3;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case NOTE_Dis:
                                                                        {
                                                                            switch (notescale)
                                                                            {
                                                                                case SCALE_1:
                                                                                    notevalue = MelodyComposer.TONE_DIS0;
                                                                                    break;
                                                                                case SCALE_2:
                                                                                    notevalue = MelodyComposer.TONE_DIS1;
                                                                                    break;
                                                                                case SCALE_3:
                                                                                    notevalue = MelodyComposer.TONE_DIS2;
                                                                                    break;
                                                                                case SCALE_4:
                                                                                    notevalue = MelodyComposer.TONE_DIS3;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case NOTE_E:
                                                                        {
                                                                            switch (notescale)
                                                                            {
                                                                                case SCALE_1:
                                                                                    notevalue = MelodyComposer.TONE_E0;
                                                                                    break;
                                                                                case SCALE_2:
                                                                                    notevalue = MelodyComposer.TONE_E1;
                                                                                    break;
                                                                                case SCALE_3:
                                                                                    notevalue = MelodyComposer.TONE_E2;
                                                                                    break;
                                                                                case SCALE_4:
                                                                                    notevalue = MelodyComposer.TONE_E3;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case NOTE_F:
                                                                        {
                                                                            switch (notescale)
                                                                            {
                                                                                case SCALE_1:
                                                                                    notevalue = MelodyComposer.TONE_F0;
                                                                                    break;
                                                                                case SCALE_2:
                                                                                    notevalue = MelodyComposer.TONE_F1;
                                                                                    break;
                                                                                case SCALE_3:
                                                                                    notevalue = MelodyComposer.TONE_F2;
                                                                                    break;
                                                                                case SCALE_4:
                                                                                    notevalue = MelodyComposer.TONE_F3;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case NOTE_Fis:
                                                                        {
                                                                            switch (notescale)
                                                                            {
                                                                                case SCALE_1:
                                                                                    notevalue = MelodyComposer.TONE_FIS0;
                                                                                    break;
                                                                                case SCALE_2:
                                                                                    notevalue = MelodyComposer.TONE_FIS1;
                                                                                    break;
                                                                                case SCALE_3:
                                                                                    notevalue = MelodyComposer.TONE_FIS2;
                                                                                    break;
                                                                                case SCALE_4:
                                                                                    notevalue = MelodyComposer.TONE_FIS3;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case NOTE_G:
                                                                        {
                                                                            switch (notescale)
                                                                            {
                                                                                case SCALE_1:
                                                                                    notevalue = MelodyComposer.TONE_G0;
                                                                                    break;
                                                                                case SCALE_2:
                                                                                    notevalue = MelodyComposer.TONE_G1;
                                                                                    break;
                                                                                case SCALE_3:
                                                                                    notevalue = MelodyComposer.TONE_G2;
                                                                                    break;
                                                                                case SCALE_4:
                                                                                    notevalue = MelodyComposer.TONE_G3;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case NOTE_Gis:
                                                                        {
                                                                            switch (notescale)
                                                                            {
                                                                                case SCALE_1:
                                                                                    notevalue = MelodyComposer.TONE_GIS0;
                                                                                    break;
                                                                                case SCALE_2:
                                                                                    notevalue = MelodyComposer.TONE_GIS1;
                                                                                    break;
                                                                                case SCALE_3:
                                                                                    notevalue = MelodyComposer.TONE_GIS2;
                                                                                    break;
                                                                                case SCALE_4:
                                                                                    notevalue = MelodyComposer.TONE_GIS3;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case NOTE_H:
                                                                        {
                                                                            switch (notescale)
                                                                            {
                                                                                case SCALE_1:
                                                                                    notevalue = MelodyComposer.TONE_H0;
                                                                                    break;
                                                                                case SCALE_2:
                                                                                    notevalue = MelodyComposer.TONE_H1;
                                                                                    break;
                                                                                case SCALE_3:
                                                                                    notevalue = MelodyComposer.TONE_H2;
                                                                                    break;
                                                                                case SCALE_4:
                                                                                    notevalue = MelodyComposer.TONE_H3;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case NOTE_PAUSE:
                                                                        {
                                                                            notevalue = MelodyComposer.TONE_PAUSE;
                                                                        }
                                                                        ;
                                                                        break;
                                                                }

                                                                // Reading note duration
                                                                int noteduration = bis.readBits(3);
                                                                // Reading note duration specifier
                                                                int durationspecifier = bis.readBits(2);

                                                                int milli_len = 0;
                                                                switch (noteduration)
                                                                {
                                                                    case DURATION_FULL:
                                                                        {
                                                                            switch (durationspecifier)
                                                                            {
                                                                                case SPECIFIER_2_3_LENGTH:
                                                                                case SPECIFIER_NONE:
                                                                                    {
                                                                                        noteduration = MelodyComposer.TONELENGTH_1_1;
                                                                                        milli_len = (int) Math.round(tempocoeff * LENGTH_FULL);
                                                                                    }
                                                                                    ;
                                                                                    break;
                                                                                case SPECIFIER_DOTTED:
                                                                                case SPECIFIER_DOUBLEDOTTED:
                                                                                    {
                                                                                        noteduration = MelodyComposer.TONELENGTH_DOTTED_1_1;
                                                                                        milli_len = (int) Math.round((tempocoeff * LENGTH_FULL) * 1.5f);
                                                                                    }
                                                                                    ;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case DURATION_1_2:
                                                                        {
                                                                            switch (durationspecifier)
                                                                            {
                                                                                case SPECIFIER_2_3_LENGTH:
                                                                                case SPECIFIER_NONE:
                                                                                    {
                                                                                        noteduration = MelodyComposer.TONELENGTH_1_2;
                                                                                        milli_len = (int) Math.round(tempocoeff * LENGTH_HALF);
                                                                                    }
                                                                                    ;
                                                                                    break;
                                                                                case SPECIFIER_DOTTED:
                                                                                case SPECIFIER_DOUBLEDOTTED:
                                                                                    {
                                                                                        noteduration = MelodyComposer.TONELENGTH_DOTTED_1_2;
                                                                                        milli_len = (int) Math.round((tempocoeff * LENGTH_HALF) * 1.5f);
                                                                                    }
                                                                                    ;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case DURATION_1_4:
                                                                        {
                                                                            switch (durationspecifier)
                                                                            {
                                                                                case SPECIFIER_2_3_LENGTH:
                                                                                case SPECIFIER_NONE:
                                                                                    {
                                                                                        noteduration = MelodyComposer.TONELENGTH_1_4;
                                                                                        milli_len = (int) Math.round(tempocoeff * LENGTH_1_4);
                                                                                    }
                                                                                    ;
                                                                                    break;
                                                                                case SPECIFIER_DOTTED:
                                                                                case SPECIFIER_DOUBLEDOTTED:
                                                                                    {
                                                                                        noteduration = MelodyComposer.TONELENGTH_DOTTED_1_4;
                                                                                        milli_len = (int) Math.round((tempocoeff * LENGTH_1_4) * 1.5f);
                                                                                    }
                                                                                    ;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case DURATION_1_8:
                                                                        {
                                                                            switch (durationspecifier)
                                                                            {
                                                                                case SPECIFIER_2_3_LENGTH:
                                                                                case SPECIFIER_NONE:
                                                                                    {
                                                                                        noteduration = MelodyComposer.TONELENGTH_1_8;
                                                                                        milli_len = (int) Math.round(tempocoeff * LENGTH_1_8);
                                                                                    }
                                                                                    ;
                                                                                    break;
                                                                                case SPECIFIER_DOTTED:
                                                                                case SPECIFIER_DOUBLEDOTTED:
                                                                                    {
                                                                                        noteduration = MelodyComposer.TONELENGTH_DOTTED_1_8;
                                                                                        milli_len = (int) Math.round((tempocoeff * LENGTH_1_8) * 1.5f);
                                                                                    }
                                                                                    ;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case DURATION_1_16:
                                                                        {
                                                                            switch (durationspecifier)
                                                                            {
                                                                                case SPECIFIER_2_3_LENGTH:
                                                                                case SPECIFIER_NONE:
                                                                                    {
                                                                                        noteduration = MelodyComposer.TONELENGTH_1_16;
                                                                                        milli_len = (int) Math.round(tempocoeff * LENGTH_1_16);
                                                                                    }
                                                                                    ;
                                                                                    break;
                                                                                case SPECIFIER_DOTTED:
                                                                                case SPECIFIER_DOUBLEDOTTED:
                                                                                    {
                                                                                        noteduration = MelodyComposer.TONELENGTH_DOTTED_1_16;
                                                                                        milli_len = (int) Math.round((tempocoeff * LENGTH_1_16) * 1.5f);
                                                                                    }
                                                                                    ;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                    case DURATION_1_32:
                                                                        {
                                                                            switch (durationspecifier)
                                                                            {
                                                                                case SPECIFIER_2_3_LENGTH:
                                                                                case SPECIFIER_NONE:
                                                                                    {
                                                                                        noteduration = MelodyComposer.TONELENGTH_1_32;
                                                                                        milli_len = (int) Math.round(tempocoeff * LENGTH_1_32);
                                                                                    }
                                                                                    ;
                                                                                    break;
                                                                                case SPECIFIER_DOTTED:
                                                                                case SPECIFIER_DOUBLEDOTTED:
                                                                                    {
                                                                                        noteduration = MelodyComposer.TONELENGTH_DOTTED_1_32;
                                                                                        milli_len = (int) Math.round((tempocoeff * LENGTH_1_32) * 1.5f);
                                                                                    }
                                                                                    ;
                                                                                    break;
                                                                            }
                                                                        }
                                                                        ;
                                                                        break;
                                                                }

                                                                i_milliseclen += milli_len;

                                                                p_patternArray.write(notevalue);
                                                                p_patternArray.write(noteduration);
                                                            }
                                                            ;
                                                            break;
                                                            // Scale
                                                        case 2:
                                                            {
                                                                // Reading note scale
                                                                notescale = bis.readBits(2);
                                                                //me = new MusicEntity(MusicEntity.ENTITY_SCALE, notescale);
                                                            }
                                                            ;
                                                            break;
                                                            // Style
                                                        case 3:
                                                            {
                                                                // Reading style value
                                                                int stylevalue = bis.readBits(2);
                                                                //me = new MusicEntity(MusicEntity.ENTITY_STYLE, stylevalue);
                                                            }
                                                            ;
                                                            break;
                                                            // Tempo
                                                        case 4:
                                                            {
                                                                // Reading bits per minute
                                                                int bitsperminute = bis.readBits(5);
                                                                //me = new MusicEntity(MusicEntity.ENTITY_TEMPO, bitsperminute);
                                                                tempocoeff = getTempCoeff(bitsperminute);
                                                                switch (bitsperminute)
                                                                {
                                                                    case BEATSPERMINUTE_25:
                                                                        bpm = 25;
                                                                        break;
                                                                    case BEATSPERMINUTE_28:
                                                                        bpm = 28;
                                                                        break;
                                                                    case BEATSPERMINUTE_31:
                                                                        bpm = 31;
                                                                        break;
                                                                    case BEATSPERMINUTE_35:
                                                                        bpm = 35;
                                                                        break;
                                                                    case BEATSPERMINUTE_40:
                                                                        bpm = 40;
                                                                        break;
                                                                    case BEATSPERMINUTE_45:
                                                                        bpm = 45;
                                                                        break;
                                                                    case BEATSPERMINUTE_50:
                                                                        bpm = 50;
                                                                        break;
                                                                    case BEATSPERMINUTE_56:
                                                                        bpm = 56;
                                                                        break;
                                                                    case BEATSPERMINUTE_63:
                                                                        bpm = 63;
                                                                        break;
                                                                    case BEATSPERMINUTE_70:
                                                                        bpm = 70;
                                                                        break;
                                                                    case BEATSPERMINUTE_80:
                                                                        bpm = 80;
                                                                        break;
                                                                    case BEATSPERMINUTE_90:
                                                                        bpm = 90;
                                                                        break;
                                                                    case BEATSPERMINUTE_100:
                                                                        bpm = 100;
                                                                        break;
                                                                    case BEATSPERMINUTE_112:
                                                                        bpm = 112;
                                                                        break;
                                                                    case BEATSPERMINUTE_125:
                                                                        bpm = 125;
                                                                        break;
                                                                    case BEATSPERMINUTE_140:
                                                                        bpm = 140;
                                                                        break;
                                                                    case BEATSPERMINUTE_160:
                                                                        bpm = 160;
                                                                        break;
                                                                    case BEATSPERMINUTE_180:
                                                                        bpm = 180;
                                                                        break;
                                                                    case BEATSPERMINUTE_200:
                                                                        bpm = 200;
                                                                        break;
                                                                    case BEATSPERMINUTE_225:
                                                                        bpm = 225;
                                                                        break;
                                                                    case BEATSPERMINUTE_250:
                                                                        bpm = 250;
                                                                        break;
                                                                    case BEATSPERMINUTE_285:
                                                                        bpm = 285;
                                                                        break;
                                                                    case BEATSPERMINUTE_320:
                                                                        bpm = 320;
                                                                        break;
                                                                    case BEATSPERMINUTE_355:
                                                                        bpm = 355;
                                                                        break;
                                                                    case BEATSPERMINUTE_400:
                                                                        bpm = 400;
                                                                        break;
                                                                    case BEATSPERMINUTE_450:
                                                                        bpm = 450;
                                                                        break;
                                                                    case BEATSPERMINUTE_500:
                                                                        bpm = 500;
                                                                        break;
                                                                    case BEATSPERMINUTE_565:
                                                                        bpm = 565;
                                                                        break;
                                                                    case BEATSPERMINUTE_635:
                                                                        bpm = 635;
                                                                        break;
                                                                    case BEATSPERMINUTE_715:
                                                                        bpm = 715;
                                                                        break;
                                                                    case BEATSPERMINUTE_800:
                                                                        bpm = 800;
                                                                        break;
                                                                    case BEATSPERMINUTE_900:
                                                                        bpm = 900;
                                                                        break;
                                                                }
                                                            }
                                                            ;
                                                            break;
                                                            // Volume
                                                        case 5:
                                                            {
                                                                // Reading volume
                                                                int volume = bis.readBits(4);
                                                                //me = new MusicEntity(MusicEntity.ENTITY_VOLUME, volume);
                                                            }
                                                            ;
                                                            break;
                                                        default :
                                                            throw new IOException("Unknown instruction [" + instrid + "]");
                                                    }
                                                }

                                                // Writing pattern to output stream
                                                byte[] ab_patternArray = p_patternArray.toByteArray();
                                                for (int lp = 0; lp <= loopvalue; lp++)
                                                {
                                                    for (int lx = 0; lx < ab_patternArray.length; lx++) p_baos.write(ab_patternArray[lx]);
                                                }
                                                ab_patternArray = null;
                                                p_patternArray.close();
                                                p_patternArray = null;
                                            }
                                            else
                                            {
                                                throw new IOException("Pointer to pattern is not supported");
                                            }
                                        }
                                    }
                                    ;
                                    break;
                                    // TEMPORARY SONG
                                case 2:
                                    ;
                                    break;
                                default :
                                    throw new IOException("Unsupported song format");
                            }
                        }
                        ;
                        break;
                        // UNICODE
                    case 0x22:
                        {
                            isunicode = true;
                        }
                        ;
                        break;
                }
                bis.octetAlign();
            }
        }
        byte[] ab_arr = p_baos.toByteArray();
        i_LastOttMillisecindsLength = i_milliseclen;
        ab_arr[0] = (byte) (bpm >> 8);
        ab_arr[1] = (byte) bpm;
        return ab_arr;
    }

    private static void writeOTTForNokia(byte[] _ottarray, DataOutputStream _dos) throws IOException
    {
        if (_ottarray.length <= 0x7F)
        {
            _dos.write(_ottarray.length);
        }
        else
        {
            _dos.write((_ottarray.length >> 8) | 0x80);
            _dos.write(_ottarray.length);
        }
        _dos.write(_ottarray);
    }

    private static void writeOTTForSiemens(byte[] _ottarray, DataOutputStream _dos) throws IOException
    {
        _ottarray = OttToSiemens(_ottarray);
        if (_dos == null) return;

        if (_ottarray.length <= 0x7F)
        {
            _dos.write(_ottarray.length);
        }
        else
        {
            _dos.write((_ottarray.length >> 8) | 0x80);
            _dos.write(_ottarray.length);
        }
        _dos.write(_ottarray);
    }

    private static void writeWAVForAll(byte[] _wavarray, DataOutputStream _dos, int _model) throws IOException
    {
        if (_wavarray.length <= 0x7F)
        {
            _dos.write(_wavarray.length);
        }
        else
        {
            _dos.write((_wavarray.length >> 8) | 0x80);
            _dos.write(_wavarray.length);
        }

        _dos.write(_wavarray);
    }

    private static void writeMMFForAll(byte[] _mmfarray, DataOutputStream _dos, int _model) throws IOException
    {
        if (_mmfarray.length <= 0x7F)
        {
            _dos.write(_mmfarray.length);
        }
        else
        {
            _dos.write((_mmfarray.length >> 8) | 0x80);
            _dos.write(_mmfarray.length);
        }

        _dos.write(_mmfarray);
    }

    private static void writeAMRForAll(byte[] _amrarray, DataOutputStream _dos, int _model) throws IOException
    {
        if (_amrarray.length <= 0x7F)
        {
            _dos.write(_amrarray.length);
        }
        else
        {
            _dos.write((_amrarray.length >> 8) | 0x80);
            _dos.write(_amrarray.length);
        }

        _dos.write(_amrarray);
    }

    private static void writeMIDIForAll(byte[] _midiarray, DataOutputStream _dos, int _model) throws IOException
    {
        if (_midiarray.length <= 0x7F)
        {
            _dos.write(_midiarray.length);
        }
        else
        {
            _dos.write((_midiarray.length >> 8) | 0x80);
            _dos.write(_midiarray.length);
        }
        _dos.write(_midiarray);
    }

    private static void outHelp()
    {
        System.out.println("SoundPacker utility.\r\n(C) 2002 All Copyright by Igor Maznitsa\r\nv 1.01 (21-Dec-2002)");
        System.out.println("\r\nCommand string:");
        System.out.println("com.igormaznitsa.MIDPTools.SoundPacker.SounfPacker (/M:model) [directory_name] [package_name]");
        System.out.println("/M:M50 packing the sounds for Siemens M50,Siemens SL45i [*.ott]");
        System.out.println("/M:C55 packing the sounds for Siemens C55 [*.ott,*.mid,*.wav]");
        System.out.println("/M:N3410 packing the sounds for Nokia 6310i and Nokia 3410 [*.ott]");
        System.out.println("/M:S55 packing the sounds for Siemens S55 [*.mid]");
        System.out.println("/M:N7650 packing the sounds for Nokia 7650 [*.ott,*.wav,*.amr]");
        System.out.println("/M:S300 packing the sounds for Samsung S300 [*.mmf]");
        System.out.println("/M:OCAP packing the sounds for OCAP [*.wav]");
    }

    private static int getOTTtimeLength(byte[] _ottFile) throws IOException
    {
        writeOTTForSiemens(_ottFile, null);
        return i_LastOttMillisecindsLength;
    }

    public static void main(String[] args)
    {
        int i_model = MODEL_M50_SL45;
        String[] as_fileextends = as_formats_M50_SL45_N6310i_N3410;

        String s_model = args[0].toLowerCase();

        String s_packname = "";

        if (s_model.startsWith("/m:"))
        {
            if (s_model.endsWith("m50"))
            {
                s_packname = "m50sl45.snd";
                i_model = MODEL_M50_SL45;
                as_fileextends = as_formats_M50_SL45_N6310i_N3410;
            }
            else if (s_model.endsWith("c55"))
            {
                s_packname = "c55.snd";
                i_model = MODEL_C55;
                as_fileextends = as_formats_C55;
            }
            else if (s_model.endsWith("n7650"))
            {
                s_packname = "n7650.snd";
                i_model = MODEL_N7650;
                as_fileextends = as_formats_N7650;
            }
            else if (s_model.endsWith("s300"))
            {
                s_packname = "s300.snd";
                i_model = MODEL_S300;
                as_fileextends = as_formats_S300;
            }
            else if (s_model.endsWith("ocap"))
            {
                s_packname = "ocap.snd";
                i_model = MODEL_OCAP;
                as_fileextends = as_formats_OCAP;
            }
            else if (s_model.endsWith("s55"))
            {
                s_packname = "s55.snd";
                i_model = MODEL_S55;
                as_fileextends = as_formats_S55;
            }
            else if (s_model.endsWith("n3410"))
            {
                s_packname = "n3410.snd";
                i_model = MODEL_N6310i_N3410;
                as_fileextends = as_formats_M50_SL45_N6310i_N3410;
            }
            else
            {
                outHelp();
                System.exit(1);
            }
        }
        else
        {
            outHelp();
            System.exit(1);
        }

        String s_dir = ".\\";
        if (args.length > 1) s_dir = args[1];


        if (args.length > 2) s_packname = args[2];

        File[] ap_filelist = new File(s_dir).listFiles();
        TreeMap p_sortedList = new TreeMap();

        for (int li = 0; li < ap_filelist.length; li++)
        {
            File p_file = ap_filelist[li];

            if (p_file.isFile())
            {
                String s_filename = p_file.getName().toLowerCase();
                for (int le = 0; le < as_fileextends.length; le++)
                {
                    if (s_filename.endsWith(as_fileextends[le]))
                    {
                        String s_withoutExt = s_filename.substring(0, s_filename.length() - as_fileextends[le].length());
                        p_sortedList.put(s_withoutExt, p_file);
                        break;
                    }
                }
            }
        }

        FileOutputStream p_fos = null;
        try
        {
            p_fos = new FileOutputStream(s_packname);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("I can't create file " + s_packname);
            System.exit(1);
        }
        DataOutputStream p_dos = new DataOutputStream(p_fos);

        ap_filelist = null;

        try
        {
            p_fos.write(p_sortedList.size());

            Iterator p_iter = p_sortedList.values().iterator();
            Vector p_name_vector = new Vector();
            while (p_iter.hasNext())
            {
                File p_file = (File) p_iter.next();
                int i_filetype = -1;

                String s_filename = p_file.getName().toLowerCase();

                for (int le = 0; le < as_fileextends.length; le++)
                {
                    if (s_filename.endsWith(as_fileextends[le]))
                    {
                        i_filetype = le;
                        break;
                    }
                }

                int i_typeIndx = i_filetype;

                if (i_filetype < 0) continue;

                if (as_fileextends[i_filetype].equals(".ott"))
                    i_filetype = MELODY_TYPE_OTT;
                else if (as_fileextends[i_filetype].equals(".wav"))
                    i_filetype = MELODY_TYPE_WAV;
                else if (as_fileextends[i_filetype].equals(".mid"))
                    i_filetype = MELODY_TYPE_MIDI;
                else if (as_fileextends[i_filetype].equals(".mmf"))
                    i_filetype = MELODY_TYPE_MMF;
                else if (as_fileextends[i_filetype].equals(".amr"))
                    i_filetype = MELODY_TYPE_AMR;

                s_filename = p_file.getName().toUpperCase().substring(0, p_file.getName().length() - 4);

                FileInputStream p_fis = null;
                try
                {
                    p_fis = new FileInputStream(p_file);
                }
                catch (FileNotFoundException e)
                {
                    System.err.println("I can't find file " + p_file.getName());
                    System.exit(1);
                }

                System.out.println("Processing "+p_file.getName());

                byte[] ab_arr = new byte[(int) (p_file.length())];
                try
                {
                    p_fis.read(ab_arr);
                    p_fis.close();
                }
                catch (IOException e)
                {
                    System.err.println("I can't read file " + p_file.getName());
                    System.exit(1);
                }

                switch (i_filetype)
                {
                    case MELODY_TYPE_MIDI:
                        {
                            p_dos.writeByte(MELODY_TYPE_MIDI);
                            p_dos.writeShort(0);
                            p_dos.writeByte(0);
                            System.out.println("TIMEDELAY=UNKNOWN");
                        }
                        ;
                        break;
                    case MELODY_TYPE_MMF:
                        {
                            p_dos.writeByte(MELODY_TYPE_MMF);
                            p_dos.writeShort(0);
                            p_dos.writeByte(0);
                            System.out.println("TIMEDELAY=UNKNOWN");
                        }
                        ;
                        break;
                    case MELODY_TYPE_AMR:
                        {
                            p_dos.writeByte(MELODY_TYPE_AMR);
                            p_dos.writeShort(0);
                            p_dos.writeByte(0);
                            System.out.println("TIMEDELAY=UNKNOWN");
                        }
                        ;
                        break;
                    case MELODY_TYPE_OTT:
                        {
                            p_dos.writeByte(MELODY_TYPE_OTT);
                            int i_tl = getOTTtimeLength(ab_arr);
                            int i_b0 = (i_tl >>> 16)&0xFF;
                            int i_b1 = (i_tl >>> 8)&0xFF;
                            int i_b2 = i_tl &0xFF;
                            p_dos.writeByte(i_b0);
                            p_dos.writeByte(i_b1);
                            p_dos.writeByte(i_b2);
                            System.out.println("TIMEDELAY="+i_tl+" ms");
                        }
                        ;
                        break;
                    case MELODY_TYPE_WAV:
                        {
                            p_dos.writeByte(MELODY_TYPE_WAV);
                            p_dos.writeShort(0);
                            p_dos.writeByte(0);
                            System.out.println("TIMEDELAY=UNKNOWN");
                        }
                        ;
                        break;
                }


                switch (i_model)
                {
                    case MODEL_N7650:
                        {
                            switch(i_filetype)
                            {
                                case MELODY_TYPE_OTT:
                                    {
                                        writeOTTForNokia(ab_arr, p_dos);
                                    }
                                    ;
                                    break;
                                case MELODY_TYPE_AMR:
                                    {
                                        writeAMRForAll(ab_arr, p_dos,i_model);
                                    }
                                    ;
                                    break;
                                case MELODY_TYPE_WAV:
                                    {
                                        writeWAVForAll(ab_arr, p_dos, i_model);
                                    }
                                    ;
                                    break;
                            }
                        };break;
                    case MODEL_S300:
                        {
                            switch(i_filetype)
                            {
                                case MELODY_TYPE_MMF:
                                    {
                                        writeMMFForAll(ab_arr, p_dos, i_model);
                                    }
                                    ;
                                    break;
                            }
                        };break;
                    case MODEL_OCAP:
                        {
                            switch(i_filetype)
                            {
                                case MELODY_TYPE_WAV:
                                    {
                                        writeWAVForAll(ab_arr, p_dos, i_model);
                                    }
                                    ;
                                    break;
                            }
                        };break;
                    case MODEL_S55:
                        {
                            switch(i_filetype)
                            {
                                case MELODY_TYPE_MIDI :
                                    {
                                        writeMIDIForAll(ab_arr, p_dos, i_model);
                                    };break;
                            }
                        };break;
                    case MODEL_C55:
                        {
                            switch (i_filetype)
                            {
                                case MELODY_TYPE_MIDI:
                                    {
                                        writeMIDIForAll(ab_arr, p_dos, i_model);
                                    }
                                    ;
                                    break;
                                case MELODY_TYPE_WAV:
                                    {
                                        writeWAVForAll(ab_arr, p_dos, i_model);
                                    }
                                    ;
                                    break;
                            }
                        }
                        ;
                        break;
                    case MODEL_M50_SL45:
                        {
                            switch (i_filetype)
                            {
                                case MELODY_TYPE_OTT:
                                    {
                                        writeOTTForSiemens(ab_arr, p_dos);
                                    }
                                    ;
                                    break;
                            }
                        }
                        ;
                        break;
                    case MODEL_N6310i_N3410:
                        {
                            switch (i_filetype)
                            {
                                case MELODY_TYPE_OTT:
                                    {
                                        writeOTTForNokia(ab_arr, p_dos);
                                    }
                                    ;
                                    break;
                            }
                        }
                        ;
                        break;
                }

                String s_NewName = s_filename.replace(' ','_').replace('-','_');
                p_name_vector.add(s_NewName + as_fileextends[i_typeIndx].toUpperCase().replace('.', '_'));
            }
            p_dos = null;
            p_fos.flush();
            p_fos.close();

            p_fos = new FileOutputStream("sounds.java");
            p_dos = new DataOutputStream(p_fos);

            Enumeration p_enum = p_name_vector.elements();
            int i_indx = 0;
            while (p_enum.hasMoreElements())
            {
                String s_name = (String) p_enum.nextElement();
                p_dos.writeBytes("private static final int SND_" + s_name + " = " + i_indx + ";\r\n");
                i_indx++;
            }
            p_dos.flush();
            p_dos.close();
            p_fos = null;
            p_dos = null;
        }
        catch (IOException ex)
        {
            System.err.println(ex);
            System.exit(1);
        }
    }
}