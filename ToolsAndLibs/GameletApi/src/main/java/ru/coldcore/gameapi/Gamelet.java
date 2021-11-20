package ru.coldcore.gameapi;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;

/**
 * Класс описывает игровой геймлет 
 * @author Igor A. Maznitsa (igor.maznisa@raydac-research.com)
 * @version 3.00
 */
public class Gamelet
{
    /**
     * Флаг, показывающий, что игра может сохранять свои игровые состояния и загружать их
     */
    public static final int FLAG_CANBESAVED = 0x01;

    /**
     * Флаг, показывающий, что игра разрешает перезапускать игровые фазы
     */
    public static final int FLAG_STAGECANBERESTARTED = 0x02;
    
    /**
     * Флаг, показывает, что геймлет в неинициализированном состоянии
     */
    public static final int STATE_UNINITED = 0;

    /**
     * Флаг, показывает, что геймлет в инициализированном состоянии
     */
    public static final int STATE_INITED = 1;

    /**
     * Флаг, показывает, что геймлет в состоянии инициализированной игровой сессии
     */
    public static final int STATE_SESSIONINITED = 2;

    /**
     * Флаг, показывает, что геймлет в состоянии инициализированной игровой фазы
     */
    public static final int STATE_STAGEINITED = 3;

    /**
     * Флаг, показывает, что геймлет в состоянии инициализированной но поставленной на паузу игровой фазы
     */
    public static final int STATE_STAGEPAUSED = 4;

    /**
     * Флаг, показывает, что игра в рабочем состоянии
     */
    public static final int GAMESTATE_PLAYED = 0;

    /**
     * Флаг, показывает, что игра в законченном состоянии с выигрышем игрока
     */
    public static final int GAMESTATE_PLAYERWIN = 1;

    /**
     * Флаг, показывает, что игра в законченном состоянии с проигрышем игрока
     */
    public static final int GAMESTATE_PLAYERLOST = 2;
    
    /**
     * Флаг, показывает, что игра в законченном состоянии, была "ничья"
     */
    public static final int GAMESTATE_DRAWGAME = 3;
    
    /**
     * Переменная хранит показатель текущего состояния геймлета
     */
    public static int i_State;

    /**
     * Переменная хранит показатель текущего состояния игрового процесса
     */
    public static int i_GameState;

    /**
     * Переменная хранит идентификатор иекущей игровой сессии
     */
    public static int i_SessionID;

    /**
     * Переменная хранит идентификатор иекущей игровой фазы
     */
    public static int i_StageID;
    
    /**
     * Инициализация геймлета, до вызова данной функции, запрещены любые операции с геймлетом
     * @param _parent класс-родитель геймлета
     * @throws Throwable порождается если была ошибка инициализации или геймлет не в состоянии STATE_UNINITED
     */
    public static final void init(Class _parent) throws Throwable
    {
        if (i_State != STATE_UNINITED) throw new Throwable();
        _callbackInit(_parent);
    }
    
    /**
     * Инициализация игровой сессии
     * @param _parent класс-родитель геймлета
     * @param _sessionID уникальный идентификатор инициализируемой игровой сессии
     * @throws Throwable порождаетсе если проблемы с инициализацией сессии или геймлет в состоянии, в котором нельзя производить данную операцию
     */
    public static final void initSession(Class _parent,int _sessionID) throws Throwable
    {
        if (i_State != STATE_INITED)throw new Throwable();
        i_SessionID = _sessionID;
        _callbackInitSession(_parent,_sessionID);
        i_State = STATE_SESSIONINITED;
        i_GameState = GAMESTATE_PLAYED;
    }
    
    /**
     * Перезапуск игровой сессии по команде игрока
     * @exception Throwable порождается если состояние геймлета не позволяет осуществлять перезапуск или геймлет не поддерживает данную функцию 
     */
    public static final void restartCurrentStage() throws Throwable
    {
        if ((GAME_FLAGS & FLAG_STAGECANBERESTARTED)==0) throw new Throwable();
        switch(i_State)
        {
            case STATE_STAGEINITED :
            case STATE_STAGEPAUSED :
            {
                _callbackRestartCurrentStage();
            };break;
            default: throw new Throwable();
        }
    }
    
    /**
     * Инициализация игровой фазы
     * @param _parent класс-родитель геймлета
     * @param _stageID уникальный идентификатор игровой фазы
     * @throws Throwable порождается, если нет возможности инициализировать фазу или геймлет в состоянии в котором нельзя производить данную операцию
     */
    public static final void initStage(Class _parent,int _stageID) throws Throwable
    {
        if (i_State != STATE_SESSIONINITED)throw new Throwable();
        i_StageID = _stageID;
        _callbackInitStage(_parent,_stageID);
        i_State = STATE_STAGEINITED;
    }

    /**
     * Возвращает размер блока данных, требуемых для сохранения данных
     * @return количество байтов, требуемых для сохранения состояния
     */
    public static final int getDataBlockSzie()
    {
        int i_size = 8;
        i_size += _callbackGetDataBlockSize();
        return i_size;
    }

    /**
     * Записывает игровое состояние геймлета
     * @param _outStream поток, в который производится выгрузка данных
     * @throws Throwable порождается если произошла ошибка или геймлет в неправильном состоянии
     */
    public static final void saveState(DataOutputStream _outStream) throws Throwable
    {   
        switch(i_State)
        {
            case STATE_STAGEINITED:
            case STATE_STAGEPAUSED:
            {
                _callbackSaveStage(_outStream);
            };break;
            default: throw new Throwable();
        }

        _outStream.writeInt(i_SessionID);
        _outStream.writeInt(i_StageID);

        _callbackSaveStage(_outStream);
    }
    
    /**
     * Загружает игровое состояние геймлета
     * @param _parent класс-родитель геймлета
     * @param _inStream поток, из которого производится загрузка данных
     * @throws Throwable порождается если произошла ошибка или геймлет в неправильном состоянии
     */
    public static final void loadState(Class _parent,DataInputStream _inStream) throws Throwable
    {
        final int i_sessionID = _inStream.readInt();
        final int i_stageID = _inStream.readInt();
        
        switch(i_State)
        {
            case STATE_INITED :
            {
               initSession(_parent,i_sessionID);
               initStage(_parent,i_stageID);
            };break;
            case STATE_SESSIONINITED :
            {
                disposeSession();
                initSession(_parent,i_sessionID);
                initStage(_parent,i_stageID);
            };break;
            case STATE_STAGEPAUSED :
            case STATE_STAGEINITED :
            {
                disposeStage();
                disposeSession();
                initSession(_parent,i_sessionID);
                initStage(_parent,i_stageID);
            };break;
            default: throw new Throwable();
        }
        _callbackLoadStage(_inStream);
    }
    
    /**
     * Дает команду геймлету поставить игровой процесс на паузу
     * @throws Throwable порождается если произошла ошибка или геймлет в неправильном состоянии
     */
    public static final void pause() throws Throwable
    {
        switch(i_State)
        {
            case STATE_STAGEINITED :
            {
                i_State = STATE_STAGEPAUSED;
                _callbackPauseStage();
            };break;
            case STATE_STAGEPAUSED : return;
            default: throw new Throwable(); 
        }
    }

    /**
     * Дает команду геймлету выйти с режима пауза в нормальный режим
     * @throws Throwable порождается если произошла ошибка или геймлет в неправильном состоянии
     */
    public static final void resume() throws Throwable
    {
        switch(i_State)
        {
            case STATE_STAGEPAUSED :
            {
                i_State = STATE_STAGEINITED;
                _callbackResumeStage();
            };break;
            case STATE_STAGEINITED : return;
            default: throw new Throwable(); 
        }
    }

    /**
     * Освобождает ресурсы игровой фазы и переводит геймлет в состояние инициализированной игровой сессии
     * @throws Throwable порождается если произошла ошибка или геймлет в неправильном состоянии
     */
    public static final void disposeStage() throws Throwable
    {
        switch(i_State)
        {
            case STATE_STAGEPAUSED:
            case STATE_STAGEINITED:
            {
                i_State = STATE_SESSIONINITED;
                _callbackDisposeStage();
            };break;
            default:
            {
                throw new Throwable();
            }
        }
    }

    /**
     * Освобождает ресурсы игровой сессии и переводит геймлет в инициализированное состояние
     * @throws Throwable порождается если произошла ошибка или геймлет в неправильном состоянии
     */
    public static final void disposeSession() throws Throwable
    {
        switch(i_State)
        {
            case STATE_STAGEINITED:
            case STATE_STAGEPAUSED:
            {
                disposeStage();
            }
            case STATE_SESSIONINITED:
            {
                i_State = STATE_INITED;
                _callbackDisposeSession();
            };break;
            default: throw new Throwable();
        }
    }
    
    /**
     * Освобождает ресурсы занятые геймлетом и переводит геймлет в неинициализированное состояние
     * @throws Throwable порождается если произошла ошибка или геймлет в "неправильном" состоянии
     */
    public static final void release() throws Throwable
    {
        switch(i_State)
        {
            case STATE_UNINITED: throw new Throwable();
            case STATE_INITED:
            {
                i_State = STATE_UNINITED;
                _callbackRelease();
            };break;
            case STATE_SESSIONINITED:
            {
                disposeSession();
                i_State = STATE_UNINITED;
                _callbackRelease();
            };break;
            case STATE_STAGEPAUSED:
            case STATE_STAGEINITED:
            {
                disposeStage();
                disposeSession();
            };break;
        }
    }

//TODO----------------------SECTION OF SPECIAL FUNCTIONS-------------------------
    /**
     * Генератор псевдослучайных чисел 
     */
    private static final Random p_RNDGen = new Random(System.currentTimeMillis()); 

    /**
     * Функция генерирует и возвращает псевдослучайное числовое значение в заданном пределе (включительно).
     * @param _limit предел генерируемого числового значения (включительно)
     * @return сгенерированное псевдослучайное значение int в заданном пределе.
     */
    private static final int getRndInt(int _limit)
    {
        _limit++;
        _limit = (int) ((((long) p_RNDGen.nextInt() & 0x7FFFFFFFFFl) * (long) _limit) >>> 31);
        return _limit;
    }
    
//TODO----------------------SECTION OF SPECIAL CONSTANTS-------------------------
    /**
     * Уникальный строковый идентификатор геймлета
     */
    public static final String GAME_ID = "GAME"; 
    public static final int GAME_FLAGS = FLAG_CANBESAVED | FLAG_STAGECANBERESTARTED;
    
    
//TODO----------------------SECTION OF GAME VARIABLES------------------------
    
    
    
    
    
    
//TODO----------------------SECTION OF CALLBACK FUNCTIONS------------------------    

    /**
     * Обработка игровой итерации
     * @param _controlObject объект, содержащий информацию, контроллирующую игровой процесс 
     * @exception Throwable порождается если было исключение в процессе отработки итерации
     */
    public static final boolean processIteration(Object _controlObject) throws Throwable
    {
        return true;
    }

    /**
     * Возвращает текущие игровые очки игрока
     * @return игровые очки игрока как int
     */
    public static final int getPlayerScore()
    {
        return 0;
    }
    
    /**
     * Обработка инициализации геймлета
     * @param _parent класс-родитель геймлета
     * @exception Throwable порождается если было невозможно произвести инициализацию геймлета
     */
    private static final void _callbackInit(Class _parent) throws Throwable
    {
        
    }

    /**
     * Обработка инициализации игровой фазы
     * @param _parent класс-родитель геймлета
     * @param _stageID уникальный идентификатор фазы
     * @exception Throwable порождается если было невозможно произвести инициализацию фазы
     */
    private static final void _callbackInitStage(Class _parent,int _stageID) throws Throwable
    {
        
    }
    
    /**
     * Обработка инициализации игровой сессии
     * @param _parent класс-родитель геймлета
     * @param _sessionID уникальный идентификатор сессии
     * @exception Throwable порождается если было невозможно произвести инициализацию сессии
     */
    private static final void _callbackInitSession(Class _parent,int _sessionID) throws Throwable
    {
        
    }
    
    /**
     * Обработка деиницилизации текущей игровой сессии
     */
    private static final void _callbackDisposeSession()
    {
        
    }

    /**
     * Обработка постановки игрового процесса на паузу
     */
    private static final void _callbackPauseStage()
    {
        
    }
    
    /**
     * Обработка перехода игрового процесса из состояния паузы в состояние работы
     */
    private static final void _callbackResumeStage()
    {
        
    }
    
    /**
     * Обработка деиницилизации текущей фазы игры
     */
    private static final void _callbackDisposeStage()
    {
        
    }
    
    /**
     * Обработка глобальной деиницилизации геймлета
     */
    private static final void _callbackRelease()
    {
        
    }
    
    /**
     * Обработка перезапуска текущего игрового уровня по запросу игрока
     */
    private static final void _callbackRestartCurrentStage()
    {
        
    }
    
    /**
     * Запрос на размер игровых данных в байтах, требуемый для сохранения состояния игровой фазы
     * @return размер блока в байтах
     */
    private static final int _callbackGetDataBlockSize()
    {
        return 0;
    }

    /**
     * Загрузка игрового состояния из потока
     * @exception исключения генерируется если произвошла ошибка загрузки
     */
    private static final void _callbackLoadStage(DataInputStream _inStream) throws Throwable
    {
    }

    /**
     * Запись игрового состояния в поток
     * @exception исключения генерируется если произошла ошибка записи
     */
    private static final void _callbackSaveStage(DataOutputStream _outStream) throws Throwable
    {
    }
    
//TODO---------------------------SECTION OF NOTIFICATE FUNCTIONS---------------------------------------
    /**
     * Функция вызывается SpriteCollectionдля уведомления об окончании анимации спрайта
     * @param _collection указатель на коллекцию
     * @param _spriteOffset смещение спрайта в массиве коллекции
     */
    public static final void notifySpriteAnimationCompleted(SpriteCollection _collection, int _spriteOffset)
    {
        
    }

    public static final void notifyPathPointPassed(PathCollection _pathCollection,int _pathOffset)
    {
        
    }

    public static final void notifyPathCompleted(PathCollection _pathCollection,int _pathOffset)
    {
        
    }
    
//TODO---------------------------SECTION OF GAME FUNCTIONS (all must be private)-----------------------



}
