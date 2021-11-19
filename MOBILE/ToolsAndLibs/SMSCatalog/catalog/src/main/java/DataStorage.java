
import javax.microedition.rms.RecordStore;

public class DataStorage
{
    public static final int MAX_SCORE_RECORDS = 5;
    private static final int SCORES_SIZE = MAX_SCORE_RECORDS << 3;

    public static final int DATABLOCK_SIZE = App.getDataBlockSize();
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
    public static boolean hasSavedData()
    {
        //#ifdefined DEMO_STRING
        //$return false;
        //#else
        if (i_InitStatus == STORE_NOMEMORY || i_InitStatus == STORE_NOTINITED) return false;
        return ab_PrivateOptionsArray[0] != 0;
        //#endif
    }

    /**
     * Функция сбрасывает флаг сохраненных данных
     */
    public static void resetSavedDataFlag()
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
     * Записываем блок данных в хранилище
     *
     * @param _dataBlock массив данных
     * @throws Exception порождается если произошел сбой при обработке
     */
    public static void saveDataBlock(byte[] _dataBlock) throws Exception
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
     * Заканчивает работу блока и сохраняет опции и тех.опции в хранилище.
     */
    public static void release()
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
    public static byte[] loadDataBlock() throws Exception
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

    public static void saveOptions() throws Exception
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
    public static String s_Status;

    /**
         * Инициализация блока
         *
         * @param _name имя хранилища данных
         * @return true если хранилище впервые созданно, false если существует
         * @throws Exception исключение порождается если были проблемы при работе с хранилищем
         */
    public static int init(String _name) throws Exception
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
                if (i_availableSize < i_summSize) throw new javax.microedition.rms.RecordStoreFullException(i_availableSize + "<" + i_summSize);
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
