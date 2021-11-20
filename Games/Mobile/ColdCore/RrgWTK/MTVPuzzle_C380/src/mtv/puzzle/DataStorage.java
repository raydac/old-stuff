package mtv.puzzle;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class DataStorage
{
    public static final int MAX_SCORE_RECORDS = 5;
    private static final int SCORES_SIZE = MAX_SCORE_RECORDS<<3;

    public static final int DATABLOCK_SIZE = Gamelet.getGameStateDataBlockSize();
    private static final int PROPTIONS_SIZE = 3;
    private static final int OPTIONS_NUMBER = 10;

    private static final int SUMMARY_SIZE = DATABLOCK_SIZE + OPTIONS_NUMBER+PROPTIONS_SIZE+SCORES_SIZE;

    public static byte[] ab_PrivateOptionsArray;
    public static byte[] ab_OptionsArray;

    private static String s_Name;

    private static final int RECORD_PRIVATEOPTIONS = 1;
    private static final int RECORD_OPTIONS = 2;
    private static final int RECORD_SCORES = 3;
    private static final int RECORD_DATA = 4;

    /**
     * ������� ��������� ���� �� ����������� ������
     * @return true ���� ���� � false ���� ���
     */
    public static final boolean hasSavedData()
    {
        return ab_PrivateOptionsArray[0]!=0;
    }

    /**
     * ������� ���������� ���� ����������� ������
     */
    public static final void resetSavedDataFlag()
    {
        ab_PrivateOptionsArray[0]=0;
        RecordStore p_recStore = null;
        try
        {
            p_recStore = RecordStore.openRecordStore(s_Name, false);
            p_recStore.setRecord(RECORD_PRIVATEOPTIONS,ab_PrivateOptionsArray,0,PROPTIONS_SIZE);
        }
        catch(Exception _ex)
        {

        }
        finally
        {
            try
            {
                if (p_recStore!=null) p_recStore.closeRecordStore();
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * ���������� ������ ���������� �������
     * @return ������ ���� ���������� ������� ��������
     * @throws Exception ����������� ���� ���� �������� � ���������� ������� �� ���������
     */
    public static final byte [] getTopScores() throws Exception
    {
        RecordStore p_recStore = null;
        try
        {
            p_recStore = RecordStore.openRecordStore(s_Name, false);
            byte [] ab_data = p_recStore.getRecord(RECORD_SCORES);
            if (ab_data.length != SCORES_SIZE) throw new Exception();
            return ab_data;
        }
        finally
        {
            try
            {
                if (p_recStore!=null) p_recStore.closeRecordStore();
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * ���������� ���������� ������� ����� ��� ������ � ������� � �������� ��������
     * @param _scoreTable ������� ������� �����
     * @param _position ����� ������� � ������� �� 0 �� MAX_SCORE_RECORDS
     * @return ���������� ����� ��� ������ �������
     */
    public static final int getTopScoresInPosition(byte [] _scoreTable,int _position)
    {
        int i_scoffset = (_position << 3) + 4;
        int i_curScore = 0;
        for(int lz=0;lz<4;lz++)
        {
            i_curScore |= ((_scoreTable[i_scoffset++]&0xFF)<<(lz*8));
        }
        return i_curScore;
    }

    /**
     * ���������� ��������� ������������� ����� ��� ������� � ������� � �������� ��������
     * @param _scoreTable ������ ���������� ������� ��������
     * @param _position ������� � �������
     * @return ������, ������������ ��� ��� NULL ���� ��� ������
     */
    public static final String getNameInPosition(byte [] _scoreTable,int _position)
    {
        int i_scoffset = _position << 3;
        if (_scoreTable[i_scoffset] == 0) return null;
        return LangBlock.decodeString(_scoreTable,i_scoffset);
    }

    /**
     * �������� ����� ������ � ������� ������� �����
     * @param _scoreTable ������ ���������� �������
     * @param _char0 ������ ������ �����
     * @param _char1 ������ ������ �����
     * @param _char2 ������ ������ �����
     * @param _score ������� ����
     * @return true ���� ������ ����� � ������� � false ���� �� �����
     */
    public static final boolean addScoreInTable(byte [] _scoreTable,byte _char0,byte _char1,byte _char2,int _score)
    {
        for(int li=0;li<MAX_SCORE_RECORDS;li++)
        {
            int i_offset = li<<3;
            int i_nameLen = _scoreTable[i_offset]&0xFF;
            if (i_nameLen==0)
            {
                _scoreTable[i_offset++] = 3;
                _scoreTable[i_offset++] = _char0;
                _scoreTable[i_offset++] = _char1;
                _scoreTable[i_offset++] = _char2;
                for(int lz=0;lz<4;lz++)
                {
                    _scoreTable[i_offset++] = (byte)(_score & 0xFF);
                    _score >>>= 8;
                }
                return true;
            }
            else
            {
                int i_curScore = getTopScoresInPosition(_scoreTable,li);

                if (_score>=i_curScore)
                {
                    int i_lastLength = SCORES_SIZE - i_offset-8;
                    System.arraycopy(_scoreTable,i_offset,_scoreTable,i_offset+8,i_lastLength);

                    _scoreTable[i_offset++] = 3;
                    _scoreTable[i_offset++] = _char0;
                    _scoreTable[i_offset++] = _char1;
                    _scoreTable[i_offset++] = _char2;
                    for(int lz=0;lz<4;lz++)
                    {
                        _scoreTable[i_offset++] = (byte)(_score & 0xFF);
                        _score >>>= 8;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * �������� ����� ������ � ������� ������� �����
     * @param _scoreTable ������ ���������� �������
     * @param _score ������� ����
     * @return true ���� ������ ����� � ������� � false ���� �� �����
     */
    public static final boolean checkScores(byte [] _scoreTable,int _score)
    {
        for(int li=0;li<MAX_SCORE_RECORDS;li++)
        {
            int i_offset = (li<<3)+4;
            int i_nameLen = _scoreTable[i_offset]&0xFF;
            if (i_nameLen==0)
            {
                return true;
            }
            else
            {
                int i_curScore = getTopScoresInPosition(_scoreTable,li);

                if (_score>=i_curScore)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ���������� ���� ������ � ���������
     * @param _dataBlock ������ ������
     * @throws Exception ����������� ���� ��������� ���� ��� ���������
     */
    public static final void saveDataBlock(byte[] _dataBlock) throws Exception
    {
        if (_dataBlock.length != DATABLOCK_SIZE) throw new Exception("Err len");
        RecordStore p_recStore = null;
        String s_sss = "null";
        try
        {
            s_sss = "0";
            p_recStore = RecordStore.openRecordStore(s_Name, false);
            s_sss = "1";
            p_recStore.setRecord(RECORD_DATA,_dataBlock,0,DATABLOCK_SIZE);
            s_sss = "2";
            ab_PrivateOptionsArray[0] = -1;
            s_sss = "3";
            p_recStore.setRecord(RECORD_PRIVATEOPTIONS,ab_PrivateOptionsArray,0,PROPTIONS_SIZE);
            s_sss = "4";
        }
        catch(Exception _ex)
        {
            throw new Exception(s_sss);
        }
        finally
        {
            try
            {
                if (p_recStore!=null) p_recStore.closeRecordStore();
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * ���������� ������ Top scores � ���������
     * @param _topScores ������ ���������� ����� � ����
     * @throws Exception ���� ��������� ���� � �������� ���������
     */
    public static final void saveScores(byte[] _topScores) throws Exception
    {
        if (_topScores.length != SCORES_SIZE) throw new Exception();
        RecordStore p_recStore = null;
        try
        {
            p_recStore = RecordStore.openRecordStore(s_Name, false);
            p_recStore.setRecord(RECORD_SCORES,_topScores,0,SCORES_SIZE);
        }
        finally
        {
            try
            {
                if (p_recStore!=null) p_recStore.closeRecordStore();
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * ����������� ������ ����� � ��������� ����� � ���.����� � ���������.
     */
    public static final void release()
    {
        RecordStore p_recStore = null;
        try
        {
            p_recStore = RecordStore.openRecordStore(s_Name, false);
            p_recStore.setRecord(RECORD_PRIVATEOPTIONS,ab_PrivateOptionsArray,0,PROPTIONS_SIZE);
            p_recStore.setRecord(RECORD_OPTIONS,ab_PrivateOptionsArray,0,OPTIONS_NUMBER);

        }
        catch(Exception _ex)
        {
        }
        finally
        {
            s_Name = null;
            try
            {
                if (p_recStore!=null) p_recStore.closeRecordStore();
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * ���������� ���� ������, ����������� � ���������
     * @return ������ ���� ���� ��������� ����� ������ � null ���� ��� ������
     * @throws Exception ���� ��������� ���� � �������� ���������.
     */
    public static final byte[] loadDataBlock() throws Exception
    {
        if (ab_PrivateOptionsArray[0]==0)
        {
            return null;
        }
        else
        {
            RecordStore p_recStore = null;
            try
            {
                p_recStore = RecordStore.openRecordStore(s_Name, false);
                byte [] ab_data = p_recStore.getRecord(RECORD_DATA);
                if (ab_data.length!=DATABLOCK_SIZE) throw new Exception();
                return ab_data;
            }
            finally
            {
                try
                {
                    if (p_recStore!=null) p_recStore.closeRecordStore();
                }
                catch (Exception e)
                {
                }
            }
        }
    }

    public static final void saveOptions() throws Exception
    {
        if (ab_OptionsArray.length != OPTIONS_NUMBER) throw new Exception();
        RecordStore p_recStore = null;
        try
        {
            p_recStore = RecordStore.openRecordStore(s_Name, false);
            p_recStore.setRecord(RECORD_OPTIONS,ab_OptionsArray,0,OPTIONS_NUMBER);
        }
        finally
        {
            try
            {
                if (p_recStore!=null) p_recStore.closeRecordStore();
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
     * ������������� �����
     * @param _name ��� ��������� ������
     * @return true ���� ��������� ������� ��������, false ���� ����������
     * @throws Exception ���������� ����������� ���� ���� �������� ��� ������ � ����������
     */
    public static final boolean init(String _name) throws Exception
    {
        s_Name = _name;
        RecordStore p_recStore = null;
        try
        {
            p_recStore = RecordStore.openRecordStore(s_Name, true);

            if (p_recStore.getNumRecords() == 0)
            {
                // ������� �������
                // �������� ���������� ������������
                if (p_recStore.getSizeAvailable() < SUMMARY_SIZE + 32) throw new Exception();

                // ������� ������ � ��������� �� 0 �������������
                ab_PrivateOptionsArray = new byte[PROPTIONS_SIZE];
                int i_id = p_recStore.addRecord(ab_PrivateOptionsArray,0,ab_PrivateOptionsArray.length);
                if (i_id!=RECORD_PRIVATEOPTIONS) throw new Exception();
                ab_OptionsArray = new byte[OPTIONS_NUMBER];
                i_id = p_recStore.addRecord(ab_OptionsArray,0,ab_OptionsArray.length);
                if (i_id!=RECORD_OPTIONS) throw new Exception();
                byte [] ab_array = new byte[SCORES_SIZE];
                i_id = p_recStore.addRecord(ab_array,0,ab_array.length);
                if (i_id!=RECORD_SCORES) throw new Exception();
                ab_array = new byte[DATABLOCK_SIZE];
                i_id = p_recStore.addRecord(ab_array,0,ab_array.length);
                if (i_id!=RECORD_DATA) throw new Exception();
                ab_array = null;
                return true;
            }
            else
            {
                ab_PrivateOptionsArray = p_recStore.getRecord(RECORD_PRIVATEOPTIONS);
                ab_OptionsArray = p_recStore.getRecord(RECORD_OPTIONS);
                if (ab_PrivateOptionsArray.length != PROPTIONS_SIZE || ab_OptionsArray.length != OPTIONS_NUMBER) throw new Exception();
                return false;
            }
        }
        finally
        {
            try
            {
                if (p_recStore != null) p_recStore.closeRecordStore();
            }
            catch (Exception e)
            {
            }
        }
    }
}
