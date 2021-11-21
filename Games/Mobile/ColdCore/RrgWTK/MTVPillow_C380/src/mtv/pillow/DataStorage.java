package mtv.pillow;

import javax.microedition.rms.RecordStore;

public class DataStorage
{
    public static final int MAX_SCORE_RECORDS = 5;
    private static final int SCORES_SIZE = MAX_SCORE_RECORDS << 3;

    public static final int DATABLOCK_SIZE = Gamelet.getGameStateDataBlockSize();
    private static final int PROPTIONS_SIZE = 3;
    private static final int OPTIONS_NUMBER = 10;

    private static final int SUMMARY_SIZE = DATABLOCK_SIZE + OPTIONS_NUMBER + PROPTIONS_SIZE + SCORES_SIZE;

    public static byte[] ab_PrivateOptionsArray;
    public static byte[] ab_OptionsArray;

    private static String s_Name;

    private static final int RECORD_PRIVATEOPTIONS = 1;
    private static final int RECORD_OPTIONS = 2;
    private static final int RECORD_SCORES = 3;
    private static final int RECORD_DATA = 4;

    /**
         * Функция проверяет есть ли сохраненные данные
         *
         * @return true если есть и false если нет
         */
    public static final boolean hasSavedData()
    {
        if (i_InitStatus == STORE_NOMEMORY || i_InitStatus == STORE_NOTINITED) return false;
        return ab_PrivateOptionsArray[0] != 0;
    }

    /**
     * Функция сбрасывает флаг сохраненных данных
     */
    public static final void resetSavedDataFlag()
    {
        if (i_InitStatus == STORE_NOMEMORY || i_InitStatus == STORE_NOTINITED) return;
        ab_PrivateOptionsArray[0] = 0;
        RecordStore p_recStore = null;
        try
        {
            p_recStore = RecordStore.openRecordStore(s_Name, false);
            p_recStore.setRecord(RECORD_PRIVATEOPTIONS, ab_PrivateOptionsArray, 0, PROPTIONS_SIZE);
        }
        catch (Exception _ex)
        {

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

    /**
         * Возвращает массиы содержащий рекорды
         *
         * @return массив байт содержащий таблицу рекордов
         * @throws Exception порождается если были проблемы с получением массива из хранилища
         */
    public static final byte[] getTopScores() throws Exception
    {
        if (i_InitStatus == STORE_NOMEMORY || i_InitStatus == STORE_NOTINITED) return new byte[SCORES_SIZE];

        RecordStore p_recStore = null;
        try
        {
            p_recStore = RecordStore.openRecordStore(s_Name, false);
            byte[] ab_data = p_recStore.getRecord(RECORD_SCORES);
            if (ab_data.length != SCORES_SIZE) throw new Exception();
            return ab_data;
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

    /**
         * Возвращает количество игровых очков для записи в таблице с заданным индексом
         *
         * @param _scoreTable таблица игровых очков
         * @param _position   номер позиции в таблице от 0 до MAX_SCORE_RECORDS
         * @return количество очков для данной позиции
         */
    public static final int getTopScoresInPosition(byte[] _scoreTable, int _position)
    {
        if (i_InitStatus == STORE_NOMEMORY || i_InitStatus == STORE_NOTINITED) return 0;
        int i_scoffset = (_position << 3) + 4;
        int i_curScore = 0;
        for (int lz = 0; lz < 4; lz++)
        {
            i_curScore |= ((_scoreTable[i_scoffset++] & 0xFF) << (lz * 8));
        }
        return i_curScore;
    }

    /**
         * Возвращает строковое представление имени для рекорда в таблице с заданным индексом
         *
         * @param _scoreTable массив содержащий таблицу рекордов
         * @param _position   позиция в таблице
         * @return строку, определяющую имя или NULL если нет записи
         */
    public static final String getNameInPosition(byte[] _scoreTable, int _position)
    {
        if (i_InitStatus == STORE_NOMEMORY || i_InitStatus == STORE_NOTINITED) return null;
        int i_scoffset = _position << 3;
        if (_scoreTable[i_scoffset] == 0) return null;
        return LangBlock.decodeString(_scoreTable, i_scoffset);
    }

    /**
         * Добавить новую запись в таблицу игровых очков
         *
         * @param _scoreTable массив содержащий таблицу
         * @param _char0      первый символ имени
         * @param _char1      второй символ имени
         * @param _char2      третий символ имени
         * @param _score      игровые очки
         * @return true если рекорд вошел в таблицу и false если не вошел
         */
    public static final boolean addScoreInTable(byte[] _scoreTable, byte _char0, byte _char1, byte _char2, int _score)
    {
        if (i_InitStatus == STORE_NOMEMORY || i_InitStatus == STORE_NOTINITED) return false;
        for (int li = 0; li < MAX_SCORE_RECORDS; li++)
        {
            int i_offset = li << 3;
            int i_nameLen = _scoreTable[i_offset] & 0xFF;
            if (i_nameLen == 0)
            {
                _scoreTable[i_offset++] = 3;
                _scoreTable[i_offset++] = _char0;
                _scoreTable[i_offset++] = _char1;
                _scoreTable[i_offset++] = _char2;
                for (int lz = 0; lz < 4; lz++)
                {
                    _scoreTable[i_offset++] = (byte) (_score & 0xFF);
                    _score >>>= 8;
                }
                return true;
            }
            else
            {
                int i_curScore = getTopScoresInPosition(_scoreTable, li);

                if (_score >= i_curScore)
                {
                    int i_lastLength = SCORES_SIZE - i_offset - 8;
                    System.arraycopy(_scoreTable, i_offset, _scoreTable, i_offset + 8, i_lastLength);

                    _scoreTable[i_offset++] = 3;
                    _scoreTable[i_offset++] = _char0;
                    _scoreTable[i_offset++] = _char1;
                    _scoreTable[i_offset++] = _char2;
                    for (int lz = 0; lz < 4; lz++)
                    {
                        _scoreTable[i_offset++] = (byte) (_score & 0xFF);
                        _score >>>= 8;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
         * Добавить новую запись в таблицу игровых очков
         *
         * @param _scoreTable массив содержащий таблицу
         * @param _score      игровые очки
         * @return true если рекорд вошел в таблицу и false если не вошел
         */
    public static final boolean checkScores(byte[] _scoreTable, int _score)
    {
        if (i_InitStatus == STORE_NOMEMORY || i_InitStatus == STORE_NOTINITED) return false;
        for (int li = 0; li < MAX_SCORE_RECORDS; li++)
        {
            int i_offset = (li << 3) + 4;
            int i_nameLen = _scoreTable[i_offset] & 0xFF;
            if (i_nameLen == 0)
            {
                return true;
            }
            else
            {
                int i_curScore = getTopScoresInPosition(_scoreTable, li);

                if (_score >= i_curScore)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
         * Записываем блок данных в хранилище
         *
         * @param _dataBlock массив данных
         * @throws Exception порождается если произошел сбой при обработке
         */
    public static final void saveDataBlock(byte[] _dataBlock) throws Exception
    {
        if (i_InitStatus == STORE_NOMEMORY || i_InitStatus == STORE_NOTINITED) return;
        RecordStore p_recStore = null;
        String s_codeErr = "rms";
        int i_codeErr = 0;
        try
        {
            if (_dataBlock.length != DATABLOCK_SIZE) throw new Exception();
            i_codeErr++;
            p_recStore = RecordStore.openRecordStore(s_Name, false);
            i_codeErr++;
            Runtime.getRuntime().gc();
            p_recStore.setRecord(RECORD_DATA, _dataBlock, 0, DATABLOCK_SIZE);
            i_codeErr++;
            Runtime.getRuntime().gc();
            ab_PrivateOptionsArray[0] = -1;
            i_codeErr++;
            p_recStore.setRecord(RECORD_PRIVATEOPTIONS, ab_PrivateOptionsArray, 0, PROPTIONS_SIZE);
        }
        catch (Exception _ex)
        {
            String s_exName = _ex.getClass().getName();
            s_exName = s_exName.substring(s_exName.lastIndexOf('.'));
            throw new Exception(s_codeErr + i_codeErr + " " + s_exName);
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
            Runtime.getRuntime().gc();
        }
    }

    /**
         * Записываем массив Top scores в хранилище
         *
         * @param _topScores массив содержащий имена и очки
         * @throws Exception если произошел сбой в процессе обработки
         */
    public static final void saveScores(byte[] _topScores) throws Exception
    {
        if (i_InitStatus == STORE_NOMEMORY || i_InitStatus == STORE_NOTINITED) return;
        if (_topScores.length != SCORES_SIZE) throw new Exception();
        RecordStore p_recStore = null;
        try
        {
            p_recStore = RecordStore.openRecordStore(s_Name, false);
            p_recStore.setRecord(RECORD_SCORES, _topScores, 0, SCORES_SIZE);
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

    /**
     * Заканчивает работу блока и сохраняет опции и тех.опции в хранилище.
     */
    public static final void release()
    {
        if (i_InitStatus == STORE_NOMEMORY || i_InitStatus == STORE_NOTINITED) return;
        RecordStore p_recStore = null;
        try
        {
            p_recStore = RecordStore.openRecordStore(s_Name, false);
            p_recStore.setRecord(RECORD_PRIVATEOPTIONS, ab_PrivateOptionsArray, 0, PROPTIONS_SIZE);
            p_recStore.setRecord(RECORD_OPTIONS, ab_PrivateOptionsArray, 0, OPTIONS_NUMBER);

        }
        catch (Exception _ex)
        {
        }
        finally
        {
            s_Name = null;
            try
            {
                if (p_recStore != null) p_recStore.closeRecordStore();
            }
            catch (Exception e)
            {
            }
        }
    }

    /**
         * Возвращает блок данных, сохраненный в хранилище
         *
         * @return массив байт если хранилище имеет запись и null если нет записи
         * @throws Exception если произошел сбой в процессе обработки.
         */
    public static final byte[] loadDataBlock() throws Exception
    {
        if (i_InitStatus == STORE_NOMEMORY || i_InitStatus == STORE_NOTINITED) return null;
        if (ab_PrivateOptionsArray[0] == 0)
        {
            return null;
        }
        else
        {
            RecordStore p_recStore = null;
            try
            {
                p_recStore = RecordStore.openRecordStore(s_Name, false);
                byte[] ab_data = p_recStore.getRecord(RECORD_DATA);
                if (ab_data.length != DATABLOCK_SIZE) throw new Exception();
                return ab_data;
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

    public static final void saveOptions() throws Exception
    {
        if (i_InitStatus == STORE_NOMEMORY || i_InitStatus == STORE_NOTINITED) return;
        if (ab_OptionsArray.length != OPTIONS_NUMBER) throw new Exception();
        RecordStore p_recStore = null;
        try
        {
            p_recStore = RecordStore.openRecordStore(s_Name, false);
            p_recStore.setRecord(RECORD_OPTIONS, ab_OptionsArray, 0, OPTIONS_NUMBER);
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

    public static final int STORE_NOTINITED = 0;
    public static final int STORE_FIRSTSTART = 1;
    public static final int STORE_INITED = 2;
    public static final int STORE_NOMEMORY = 3;

    public static int i_InitStatus = STORE_NOTINITED;
    public static String  s_Status;

    /**
     * Инициализация блока
     * @param _name имя хранилища данных
     * @return true если хранилище впервые созданно, false если существует
     * @throws Exception исключение порождается если были проблемы при работе с хранилищем
     */
    public static final int init(String _name) throws Exception
    {
        s_Name = _name;
        s_Status = null;
        RecordStore p_recStore = null;
        String s_codeErr = "rms";
        int i_codeErr = 0;
        try
        {
            p_recStore = RecordStore.openRecordStore(s_Name, true);
            int i_recordsNumber = p_recStore.getNumRecords();
            if (i_recordsNumber < 4)
            {
                // Создаем области
                // Проверка доступного пространства
                int i_summSize = SUMMARY_SIZE + 32;
                int i_availableSize = p_recStore.getSizeAvailable();
                if (i_availableSize < i_summSize) throw new javax.microedition.rms.RecordStoreFullException(i_availableSize+"<"+i_summSize);
                // Создаем записи и заполняем их 0 пространством
                i_codeErr++;
                if (i_recordsNumber == 0)
                {
                    ab_PrivateOptionsArray = new byte[PROPTIONS_SIZE];
                    int i_id = p_recStore.addRecord(ab_PrivateOptionsArray, 0, ab_PrivateOptionsArray.length);
                    if (i_id != RECORD_PRIVATEOPTIONS) throw new Exception();
                }
                i_codeErr++;
                if (i_recordsNumber <= 1)
                {
                    ab_OptionsArray = new byte[OPTIONS_NUMBER];
                    int i_id = p_recStore.addRecord(ab_OptionsArray, 0, ab_OptionsArray.length);
                    if (i_id != RECORD_OPTIONS) throw new Exception();
                }
                i_codeErr++;
                if (i_recordsNumber <= 2)
                {
                    byte[] ab_array = new byte[SCORES_SIZE];
                    int i_id = p_recStore.addRecord(ab_array, 0, ab_array.length);
                    if (i_id != RECORD_SCORES) throw new Exception();
                }
                i_codeErr++;
                if (i_recordsNumber <= 3)
                {
                    byte[] ab_array = new byte[DATABLOCK_SIZE];
                    int i_id = p_recStore.addRecord(ab_array, 0, DATABLOCK_SIZE);
                    if (i_id != RECORD_DATA) throw new Exception();
                    ab_array = null;
                }
                i_InitStatus = STORE_FIRSTSTART;
            }
            else
            {
                i_codeErr = 900;
                ab_PrivateOptionsArray = p_recStore.getRecord(RECORD_PRIVATEOPTIONS);
                i_codeErr++;
                ab_OptionsArray = p_recStore.getRecord(RECORD_OPTIONS);
                i_codeErr++;
                if (ab_PrivateOptionsArray.length != PROPTIONS_SIZE || ab_OptionsArray.length != OPTIONS_NUMBER) throw new Exception();
                i_InitStatus = STORE_INITED;
            }
        }
        catch (Exception _ex)
        {
            s_Status = _ex.getMessage();
            if (_ex instanceof javax.microedition.rms.RecordStoreFullException)
            {
                i_InitStatus = STORE_NOMEMORY;
            }
            else
            {
                String s_exName = _ex.getClass().getName();
                s_exName = s_exName.substring(s_exName.lastIndexOf('.'));
                throw new Exception(s_codeErr + i_codeErr + " " + s_exName);
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
            Runtime.getRuntime().gc();
        }
        return i_InitStatus;
    }
}
