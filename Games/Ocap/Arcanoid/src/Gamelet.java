import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;

/**
 * Класс описывает игровой геймлет для игры Arcanoid
 * @author Igor A. Maznitsa (igor.maznisa@raydac-research.com)
 * @version 1.06
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
     * Флаг, показывающий, что игра имеет много игровых уровней-состояний
     */
    public static final int FLAG_MANYSTAGES = 0x04;

    /**
     * Флаг, показывающий, что проигрыш игрока на уровне не прекращает игру 
     */
    public static final int FLAG_STAGESCONTINUEAFTERLOST = 0x08;

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
     * Рекомендованная игрой задержка по времени в миллисекундах, между игровыми шагами.
     */
    public static final int TIMEDELAY_ITERATION = 80;

    /**
     * Идентификатор последнего игрового уровня
     */
    public static final int LASTSTAGE_ID = 0;

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
        if (i_State != STATE_UNINITED)
            throw new Throwable();
        _callbackInit(_parent);
        i_State = STATE_INITED;
    }

    /**
     * Инициализация игровой сессии
     * @param _parent класс-родитель геймлета
     * @param _sessionID уникальный идентификатор инициализируемой игровой сессии
     * @throws Throwable порождаетсе если проблемы с инициализацией сессии или геймлет в состоянии, в котором нельзя производить данную операцию
     */
    public static final void initSession(Class _parent, int _sessionID) throws Throwable
    {
        if (i_State != STATE_INITED) throw new Throwable(""+i_State);
        i_SessionID = _sessionID;
        _callbackInitSession(_parent, _sessionID);
        i_State = STATE_SESSIONINITED;
        i_GameState = GAMESTATE_PLAYED;
    }

    /**
     * Перезапуск игровой сессии по команде игрока
     * @exception Throwable порождается если состояние геймлета не позволяет осуществлять перезапуск или геймлет не поддерживает данную функцию 
     */
    public static final void restartCurrentStage() throws Throwable
    {
        if ((GAME_FLAGS & FLAG_STAGECANBERESTARTED) == 0)
            throw new Throwable();
        switch (i_State)
        {
            case STATE_STAGEINITED:
            case STATE_STAGEPAUSED:
            {
                _callbackRestartCurrentStage();
            }
                ;
                break;
            default:
                throw new Throwable();
        }
    }

    /**
     * Инициализация игровой фазы
     * @param _parent класс-родитель геймлета
     * @param _stageID уникальный идентификатор игровой фазы
     * @throws Throwable порождается, если нет возможности инициализировать фазу или геймлет в состоянии в котором нельзя производить данную операцию
     */
    public static final void initStage(Class _parent, int _stageID) throws Throwable
    {
        if (i_State != STATE_SESSIONINITED)
            throw new Throwable();
        i_StageID = _stageID;
        _callbackInitStage(_parent, _stageID);

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
        switch (i_State)
        {
            case STATE_STAGEINITED:
            case STATE_STAGEPAUSED:
            {
                _callbackSaveStage(_outStream);
            }
                ;
                break;
            default:
                throw new Throwable();
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
    public static final void loadState(Class _parent, DataInputStream _inStream) throws Throwable
    {
        final int i_sessionID = _inStream.readInt();
        final int i_stageID = _inStream.readInt();

        switch (i_State)
        {
            case STATE_INITED:
            {
                initSession(_parent, i_sessionID);
                initStage(_parent, i_stageID);
            }
                ;
                break;
            case STATE_SESSIONINITED:
            {
                disposeSession();
                initSession(_parent, i_sessionID);
                initStage(_parent, i_stageID);
            }
                ;
                break;
            case STATE_STAGEPAUSED:
            case STATE_STAGEINITED:
            {
                disposeStage();
                disposeSession();
                initSession(_parent, i_sessionID);
                initStage(_parent, i_stageID);
            }
                ;
                break;
            default:
                throw new Throwable();
        }
        _callbackLoadStage(_inStream);
    }

    /**
     * Дает команду геймлету поставить игровой процесс на паузу
     * @throws Throwable порождается если произошла ошибка или геймлет в неправильном состоянии
     */
    public static final void pause() throws Throwable
    {
        switch (i_State)
        {
            case STATE_STAGEINITED:
            {
                i_State = STATE_STAGEPAUSED;
                _callbackPauseStage();
            }
                ;
                break;
            case STATE_STAGEPAUSED:
                return;
            default:
                throw new Throwable();
        }
    }

    /**
     * Дает команду геймлету выйти с режима пауза в нормальный режим
     * @throws Throwable порождается если произошла ошибка или геймлет в неправильном состоянии
     */
    public static final void resume() throws Throwable
    {
        switch (i_State)
        {
            case STATE_STAGEPAUSED:
            {
                i_State = STATE_STAGEINITED;
                _callbackResumeStage();
            }
                ;
                break;
            case STATE_STAGEINITED:
                return;
            default:
                throw new Throwable();
        }
    }

    /**
     * Освобождает ресурсы игровой фазы и переводит геймлет в состояние инициализированной игровой сессии
     * @throws Throwable порождается если произошла ошибка или геймлет в неправильном состоянии
     */
    public static final void disposeStage() throws Throwable
    {
        switch (i_State)
        {
            case STATE_STAGEPAUSED:
            case STATE_STAGEINITED:
            {
                i_State = STATE_SESSIONINITED;
                _callbackDisposeStage();
            }
                ;
                break;
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
        switch (i_State)
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
            }
                ;
                break;
            default:
                throw new Throwable();
        }
    }

    /**
     * Освобождает ресурсы занятые геймлетом и переводит геймлет в неинициализированное состояние
     * @throws Throwable порождается если произошла ошибка или геймлет в "неправильном" состоянии
     */
    public static final void release() throws Throwable
    {
        switch (i_State)
        {
            case STATE_UNINITED:
                throw new Throwable();
            case STATE_INITED:
            {
                i_State = STATE_UNINITED;
                _callbackRelease();
            }
                ;
                break;
            case STATE_SESSIONINITED:
            {
                disposeSession();
                i_State = STATE_UNINITED;
                _callbackRelease();
            }
                ;
                break;
            case STATE_STAGEPAUSED:
            case STATE_STAGEINITED:
            {
                disposeStage();
                disposeSession();
            }
                ;
                break;
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
        _limit = (int) (((long) Math.abs(p_RNDGen.nextInt()) * (long) _limit) >>> 31);
        return _limit;
    }

    //TODO----------------------SECTION OF SPECIAL CONSTANTS-------------------------
    /**
     * Уникальный строковый идентификатор геймлета
     */
    public static final String GAME_ID = "GAME";

    /**
     * Игровые флаги, определяющие случаи эксплуатации игры
     */
    public static final int GAME_FLAGS = FLAG_CANBESAVED | FLAG_STAGECANBERESTARTED;

    /**
     * Размер ширины игрового поля, под которое писалась игра в оригинале
     */
    public static final int MAINRESOLUTION_WIDTH = 476;

    /**
     * Размер высоты игрового поля, под которое писалась игра в оригинале
     */
    public static final int MAINRESOLUTION_HEIGHT = 370;

    //TODO----------------------SECTION OF GAME VARIABLES------------------------
    public static final int GAMEACTION_UPDATEBALLAREA = 0;

    public static final int GAMEACTION_ADDREFLECTIONLINE = 1;

    public static final int GAMEACTION_UPDATEMAINGAMEFIELD = 2;
    
    public static final int GAMEACTION_UPDATEREFLECTIONFIELD = 3;

    public static final int GAMEACTION_EXPLOSION = 4;

    public static final int GAMEACTION_BEATTOROCKET = 5;

    
    private static final int I8_MAXBALLSPEED = 17 << 8;

    private static final int I8_MINBALLSPEED = 7 << 8;

    private static final int I8_BALLSPEEDSTEP = 5 << 8;

    private static final int I8_BALLSPEEDDEVIATION = 1 << 8;

    public static final int BLOCKMASK_REFLECTIONLINE = 0x40000000;

    public static final int BLOCKMASK_EXPLOSIONBALL = 0x20000000;

    public static final int I8_CELLWIDTH = 34 << 8;

    public static final int I8_CELLHEIGHT = 20 << 8;

    public static final int FIELDCELLWIDTH = 14;

    public static final int FIELDCELLHEIGHT = 12;

    public static final int I8_CELLFIELDHEIGHT = FIELDCELLHEIGHT * I8_CELLHEIGHT;

    public static final int I8_STARTGAMEFIELDY = (((I8_CELLHEIGHT + 0x7F) >> 8) * FIELDCELLHEIGHT) << 8;

    public static final int YOFREFLECTIONLINE = MAINRESOLUTION_HEIGHT - (I8_CELLHEIGHT >> 8)+5;

    public static final int INSGAMESTATE_PLAYERIN = 0;

    public static final int INSGAMESTATE_BALLIN = 1;

    public static final int INSGAMESTATE_PLAYING = 2;

    public static final int INSGAMESTATE_WIN = 3;

    public static final int INSGAMESTATE_LOST = 4;

    public static final int ATTEMPTIONS_INITNUMBER = 3;

    /**
     * Очки за удар по блоку
     */
    public static final int SCOREFORBEAT = 2;

    /**
     * Очки за выбивание блока
     */
    public static final int SCOREFORBLOCK = 5;

    /**
     * Очки за поимку бонуса "добавить очки"
     */
    public static final int SCOREFORBONUS = 100;

    /**
     * Очки за оставшуюся попытку на момент окончания уровня
     */
    public static final int SCOREFORBALL = 10;

    public static final int BLOCK_NONE = 0;

    public static final int BLOCK_NORMAL1 = 1;

    public static final int BLOCK_NORMAL2 = 2;

    public static final int BLOCK_NORMAL3 = 3;

    public static final int BLOCK_SAND = 4;

    public static final int BLOCK_IRON = 5;

    public static final int BLOCK_BONUSGENERATOR = 6;

    public static final int BLOCK_BOMBA = 7;

    public static final int BLOCK_TELEPORT1IN = 8;

    public static final int BLOCK_TELEPORT1OUT = 9;

    public static final int BLOCK_TELEPORT2IN = 10;

    public static final int BLOCK_TELEPORT2OUT = 11;

    public static final int BLOCK_TELEPORTIN_DOWN = 12;

    public static final int BLOCK_TELEPORTOUT_DOWN = 13;

    public static final int BLOCK_BONUSGENERATOR_DOWN = 14;

    public static int i_InsideGameState;

    public static int i_PlayerAttemptionsNumber;

    public static int i_PlayerScore;
    
    public static int i_MagnetoMode;

    public static int i_NumberOfDestroyableBlocksOnScreen;

    public static int i_StartNumberOfDestroyableBlocksOnScreen;

    public static final int MAXEXPLOSIONSNUMBER = 10;

    public static final int MAXBONUSES = 10;

    public static final int MAXBALLS = 10;

    public static final int I8_PLAYERHORZSPEED = 18 << 8;

    public static final int SPRITECOLLECTIONS_NUMBER = 4;

    public static final int PATHS_NUMBER = 20;

    public static final int PATHCOLLECTION_OTHER = 0;

    public static final int PATHCOLLECTION_BALLS = 1;

    public static final int COLLECTIONID_PLAYERROCKET = 0;

    public static final int COLLECTIONID_PLAYERBALLS = 1;

    public static final int COLLECTIONID_EXPLOSIONS = 2;

    public static final int COLLECTIONID_BONUSES = 3;

    public static final int BONUSID_INCREASEBALLSPEED = 1;

    public static final int BONUSID_DECREASEBALLSPEED = 2;

    public static final int BONUSID_INCREASEBALLSIZE = 3;

    public static final int BONUSID_DECREASEBALLSIZE = 4;

    public static final int BONUSID_NORMALBALL = 5;

    public static final int BONUSID_INCREASEROCKETSIZE = 6;

    public static final int BONUSID_DECREASEROCKETSIZE = 7;

    public static final int BONUSID_ADDLIFE = 8;

    public static final int BONUSID_DEATH = 9;

    public static final int BONUSID_MAGNET = 10;

    public static final int BONUSID_NEXTLEVEL = 11;

    public static final int BONUSID_ADDBALL = 12;

    public static final int BONUSID_ADDSCORE = 13;

    public static final int BONUSID_REFLECTOR = 14;

    private static final int DELAYTONEXTBONUSBYBONUSGENERATOR = 30;

    private static final int PLAYER_IN_MAINPOINT_X = MAINRESOLUTION_WIDTH >> 1;

    private static final int PLAYER_IN_MAINPOINT_Y = 309;//MAINRESOLUTION_HEIGHT - 60;

    private static final int BALLROCKETOFFSET = 5;
    
    public static SpriteCollection[] ap_SpriteCollections;

    //скорость мячей по X
    private static int[] ai_ballDeltaX;

    //скорость мячей по Y
    private static int[] ai_ballDeltaY;

    // переменная содержит задержку до разрешения генерации следующего бонуса бонус генератором при столкновении с мячем 
    private static int i_DelayToNextBonus;

    //индекс блока
    private static int[] ai_blockIndexForRemoving;

    // динамически изменяемый массив путей мячей
    public static short[] ash_ballsPathsArray;

    public static PathCollection p_PathCollection;

    public static PathCollection p_BallPathCollection;

    public static final int[] ai_GameField = new int[FIELDCELLWIDTH * FIELDCELLHEIGHT];

    public static final int[] ai_BonusField = new int[FIELDCELLWIDTH * FIELDCELLHEIGHT];

    public static final int[] ai_ReflectionLine = new int[FIELDCELLWIDTH];

    //TODO----------------------SECTION OF CALLBACK FUNCTIONS------------------------    

    /**
     * Обработка игровой итерации
     * @param _controlObject объект, содержащий информацию, контроллирующую игровой процесс
     * @return возвращет текущее состояние игрового процесса 
     * @exception Throwable порождается если было исключение в процессе отработки итерации
     */
    public static final int processIteration(ControlObject _controlObject) throws Throwable
    {
        if (i_DelayToNextBonus > 0)
            i_DelayToNextBonus--;

        p_PathCollection.processActive();
        p_BallPathCollection.processActive();

        SpriteCollection p_collectionbonuses = ap_SpriteCollections[COLLECTIONID_BONUSES];
        SpriteCollection p_collectionexplosions = ap_SpriteCollections[COLLECTIONID_EXPLOSIONS];
        SpriteCollection p_collectionballs = ap_SpriteCollections[COLLECTIONID_PLAYERBALLS];
        SpriteCollection p_collectionplayer = ap_SpriteCollections[COLLECTIONID_PLAYERROCKET];

        switch (i_InsideGameState)
        {
            case INSGAMESTATE_PLAYERIN:
            case INSGAMESTATE_BALLIN:
            {

            }
                ;
                break;
            case INSGAMESTATE_LOST:
            {
                if (p_collectionplayer.i_lastActiveSpriteOffset < 0 && p_collectionballs.i_lastActiveSpriteOffset < 0 && p_collectionbonuses.i_lastActiveSpriteOffset < 0)
                {
                    // на экране нет активных объектов
                    if (i_PlayerAttemptionsNumber <= 0)
                    {
                        // прекращаем игру
                        return GAMESTATE_PLAYERLOST;
                    }

                    // уменьшаем количество попыток и продолжаем игру
                    i_PlayerAttemptionsNumber--;
                    GameView.gameAction(GAMEACTION_UPDATEBALLAREA, 0);
                    _game_initPlayer();
                }
                p_collectionplayer.processActive();
            }
                ;
                break;
            case INSGAMESTATE_PLAYING:
            {
                SpriteCollection p_player = ap_SpriteCollections[COLLECTIONID_PLAYERROCKET];

                if (p_collectionballs.i_lastActiveSpriteOffset < 0)
                {
                    // на экране нет ни одного активного шарика, переводим все в режим INSGAMESTATE_LOST

                    // осуществляем взрыв ракетки
                    p_player.activateOne(0, SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_EXPLOSION1);
                    p_player.setNextTypeState(0, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);

                    GameView.gameAction(GAMEACTION_EXPLOSION,0);
                    
                    i_InsideGameState = INSGAMESTATE_LOST;
                }
                else
                {
                    int i_linkedBallOffset = p_player.getOptionalData(0);

                    int i_keyFlags = _controlObject.BUTTON_FLAGS;
                    if ((i_keyFlags & _controlObject.BUTTON_LEFT) != 0)
                    {
                        switch (p_player.getState(0))
                        {
                            case SPRITE_STATE_PLAYERROCKETSHORT_MOVINGRIGHT:
                            case SPRITE_STATE_PLAYERROCKETSHORT_STAND:
                            {
                                int i_frame = p_player.getFrameNumber(0);
                                int i_type = p_player.getType(0);

                                p_player.activateOne(0, i_type, SPRITE_STATE_PLAYERROCKETSHORT_MOVINGLEFT);
                                p_player.setFrameNumber(0, i_frame);
                            }
                                ;
                                break;
                        }

                        int i_oldX = 0;
                        if (i_linkedBallOffset >= 0)
                        {
                            i_oldX = p_player.ai_spriteDataArray[SpriteCollection.OFFSET_MAINX];
                        }

                        p_player.moveMainPointXY(0, 0 - I8_PLAYERHORZSPEED, 0);
                        if (!p_player.alignToArea(0, 0, 0, MAINRESOLUTION_WIDTH << 8, MAINRESOLUTION_HEIGHT << 8, true))
                            p_player.processActive();

                        if (i_linkedBallOffset >= 0)
                        {
                            int i_newX = p_player.ai_spriteDataArray[SpriteCollection.OFFSET_MAINX];

                            if (i_newX != i_oldX)
                            {
                                // смещаем шарик
                                ap_SpriteCollections[COLLECTIONID_PLAYERBALLS].moveMainPointXY(i_linkedBallOffset, i_newX - i_oldX, 0);
                            }
                        }
                    }
                    else if ((i_keyFlags & _controlObject.BUTTON_RIGHT) != 0)
                    {
                        switch (p_player.getState(0))
                        {
                            case SPRITE_STATE_PLAYERROCKETSHORT_MOVINGLEFT:
                            case SPRITE_STATE_PLAYERROCKETSHORT_STAND:
                            {
                                int i_frame = p_player.getFrameNumber(0);
                                int i_type = p_player.getType(0);

                                p_player.activateOne(0, i_type, SPRITE_STATE_PLAYERROCKETSHORT_MOVINGRIGHT);
                                p_player.setFrameNumber(0, i_frame);
                            }
                                ;
                                break;
                        }

                        int i_oldX = 0;
                        if (i_linkedBallOffset >= 0)
                        {
                            i_oldX = p_player.ai_spriteDataArray[SpriteCollection.OFFSET_MAINX];
                        }

                        p_player.moveMainPointXY(0, I8_PLAYERHORZSPEED, 0);
                        if (!p_player.alignToArea(0, 0, 0, MAINRESOLUTION_WIDTH << 8, MAINRESOLUTION_HEIGHT << 8, true))
                            p_player.processActive();

                        if (i_linkedBallOffset >= 0)
                        {
                            int i_newX = p_player.ai_spriteDataArray[SpriteCollection.OFFSET_MAINX];

                            if (i_newX != i_oldX)
                            {
                                // смещаем шарик
                                ap_SpriteCollections[COLLECTIONID_PLAYERBALLS].moveMainPointXY(i_linkedBallOffset, i_newX - i_oldX, 0);
                            }
                        }
                    }
                    else if ((i_keyFlags & _controlObject.BUTTON_FIRE) != 0)
                    {
                        // отпускаем шарик если есть
                        if (i_linkedBallOffset >= 0)
                        {
                            // шарик присутствует
                            p_player.setOptionalData(0, -1);
                            _game_startLinkedBall(i_linkedBallOffset);
                        }
                    }

                    // обрабатываем мячи на столкновение с ракеткой
                    _game_processBallsAndRocketCollision();

                    if (p_collectionbonuses.i_lastActiveSpriteOffset >= 0)
                    {
                        // проверяем на столкновение с падающими бонусами
                        _game_processBonusesAndRocketCollision();
                    }
                    
                    if (i_InsideGameState==INSGAMESTATE_PLAYING)
                    {
                        if (i_NumberOfDestroyableBlocksOnScreen==0) 
                        {
                            _game_initPlayerWinMode();
                            i_InsideGameState = INSGAMESTATE_WIN;
                            
                            // начисляем очки за оставшиеся шарики
                            i_PlayerScore += (i_PlayerAttemptionsNumber*SCOREFORBALL);
                        }
                    }
                }
            }
                ;
                break;
            case INSGAMESTATE_WIN:
            {
                // проверяем активность объектов шариков и игрока, если хоть один активен то продолжаем
                if (p_collectionballs.i_lastActiveSpriteOffset<0 && p_collectionplayer.i_lastActiveSpriteOffset<0)
                {
                    return GAMESTATE_PLAYERWIN;
                }
            }
                ;
                break;
        }

        p_collectionbonuses.processActive();
        p_collectionexplosions.processActive();
        p_collectionballs.processActive();

        return GAMESTATE_PLAYED;
    }

    /**
     * Возвращает текущие игровые очки игрока
     * @return игровые очки игрока как int
     */
    public static final int getPlayerScore()
    {
        return i_PlayerScore;
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
    private static final void _callbackInitStage(Class _parent, int _stageID) throws Throwable
    {
        for (int li = 0; li < ap_SpriteCollections.length; li++)
            ap_SpriteCollections[li].releaseAll();
        p_PathCollection.releaseAll();
        p_BallPathCollection.releaseAll();

        final int ARRLEN = ai_GameField.length; 
        
        i_StartNumberOfDestroyableBlocksOnScreen = 0;
        
        for (int li = 0; li < ARRLEN; li++)
        {
            int i_block = level_test_map[li];
            ai_GameField[li] = i_block;
            
            switch(i_block)
            {
                case BLOCK_BOMBA :
                case BLOCK_NORMAL1 :
                case BLOCK_NORMAL2 :
                case BLOCK_NORMAL3 :
                case BLOCK_SAND : i_StartNumberOfDestroyableBlocksOnScreen ++;
            }
        }

        i_NumberOfDestroyableBlocksOnScreen =  i_StartNumberOfDestroyableBlocksOnScreen;
        
        for (int li = 0; li < ARRLEN; li++)
            ai_BonusField[li] = level_test_bonus_map[li];

        i_MagnetoMode = 0;

        // выставляем половинки блоков
        for(int li=0;li<ARRLEN;li++)
        {
            int i_block = ai_GameField[li];
            switch(i_block)
            {
                case BLOCK_BONUSGENERATOR :
                {
                    ai_GameField[li+FIELDCELLWIDTH] = BLOCK_BONUSGENERATOR_DOWN;
                };break;
                case BLOCK_TELEPORT1IN :
                case BLOCK_TELEPORT2IN:
                {
                    ai_GameField[li+FIELDCELLWIDTH] = BLOCK_TELEPORTIN_DOWN;
                };break;
                case BLOCK_TELEPORT1OUT:
                case BLOCK_TELEPORT2OUT:
                {
                    ai_GameField[li+FIELDCELLWIDTH] = BLOCK_TELEPORTOUT_DOWN;
                };break;
            }
        }
        
        _game_PlaceReflectionLine();

        // инициализируем игрока
        _game_initPlayer();
    }

    /**
     * Обработка инициализации игровой сессии
     * @param _parent класс-родитель геймлета
     * @param _sessionID уникальный идентификатор сессии
     * @exception Throwable порождается если было невозможно произвести инициализацию сессии
     */
    private static final void _callbackInitSession(Class _parent, int _sessionID) throws Throwable
    {
        i_PlayerScore = 0;
        
        ap_SpriteCollections = new SpriteCollection[SPRITECOLLECTIONS_NUMBER];

        ap_SpriteCollections[COLLECTIONID_PLAYERROCKET] = new SpriteCollection(COLLECTIONID_PLAYERROCKET, 1, ash_SpritesTable);
        ap_SpriteCollections[COLLECTIONID_BONUSES] = new SpriteCollection(COLLECTIONID_BONUSES, MAXBONUSES, ash_SpritesTable);
        ap_SpriteCollections[COLLECTIONID_EXPLOSIONS] = new SpriteCollection(COLLECTIONID_EXPLOSIONS, MAXEXPLOSIONSNUMBER, ash_SpritesTable);
        ap_SpriteCollections[COLLECTIONID_PLAYERBALLS] = new SpriteCollection(COLLECTIONID_PLAYERBALLS, MAXBALLS, ash_SpritesTable);

        ai_ballDeltaX = new int[MAXBALLS];
        ai_ballDeltaY = new int[MAXBALLS];
        ai_blockIndexForRemoving = new int[MAXBALLS];

        ash_ballsPathsArray = new short[MAXBALLS << 3];

        // инициализируем массив
        int i_offset = 0;
        for (int li = 0; li < MAXBALLS; li++)
        {
            i_offset = li << 3;
            ash_ballsPathsArray[i_offset++] = 1;
            ash_ballsPathsArray[i_offset++] = 32;
        }

        p_PathCollection = new PathCollection(PATHCOLLECTION_OTHER, PATHS_NUMBER, ap_SpriteCollections, ash_Paths);
        p_BallPathCollection = new PathCollection(PATHCOLLECTION_BALLS, MAXBALLS, ap_SpriteCollections, ash_ballsPathsArray);

        i_PlayerAttemptionsNumber = ATTEMPTIONS_INITNUMBER;
    }

    /**
     * Обработка деиницилизации текущей игровой сессии
     */
    private static final void _callbackDisposeSession()
    {
        ap_SpriteCollections = null;
        p_PathCollection = null;
        ai_ballDeltaX = null;
        ai_ballDeltaY = null;
        ai_blockIndexForRemoving = null;

        Runtime.getRuntime().gc();
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
        for (int li = 0; li < SPRITECOLLECTIONS_NUMBER; li++)
        {
            ap_SpriteCollections[li].releaseAll();
        }
        p_PathCollection.releaseAll();
        p_BallPathCollection.releaseAll();
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
     * Функция вызывается SpriteCollection для уведомления об окончании анимации спрайта
     * @param _collection указатель на коллекцию
     * @param _spriteOffset смещение спрайта в массиве коллекции
     */
    public static final void notifySpriteAnimationCompleted(SpriteCollection _collection, int _spriteOffset)
    {

    }

    public static final void notifyPathPointPassed(PathCollection _pathCollection, int _pathOffset)
    {

    }

    public static final void notifyPathCompleted(PathCollection _pathCollection, int _pathOffset)
    {
        //System.out.println("path completed ");

        switch (_pathCollection.i_CollectionID)
        {
            case PATHCOLLECTION_BALLS:
            {
                int i_ballOffset = _pathCollection.getSpriteInfo(_pathOffset) & 0xFFFFFF;
                int i_ballIndex = i_ballOffset / SpriteCollection.SPRITEDATA_LENGTH;
                int i_cellOffset = ai_blockIndexForRemoving[i_ballIndex];

                //System.out.println("CELL OFFSET " + i_cellOffset);

                if (i_cellOffset >= 0)
                {
                    if ((i_cellOffset & (BLOCKMASK_EXPLOSIONBALL | BLOCKMASK_REFLECTIONLINE)) == 0)
                    {
                        _game_processBlockCollision(i_ballOffset, i_cellOffset & ~(BLOCKMASK_EXPLOSIONBALL | BLOCKMASK_REFLECTIONLINE));
                        _game_calculateNewBallPath(i_ballOffset);
                    }
                    else
                    {
                        if ((i_cellOffset & BLOCKMASK_EXPLOSIONBALL) != 0)
                        {
                            SpriteCollection p_balls = ap_SpriteCollections[COLLECTIONID_PLAYERBALLS];

                            // деактивизируем путь 
                            p_BallPathCollection.releasePathForSprite(COLLECTIONID_PLAYERBALLS, i_ballOffset);

                            // переводим мячик в режим взрыва
                            p_balls.activateOne(i_ballOffset, SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_EXPLOSION1);

                            GameView.gameAction(GAMEACTION_EXPLOSION,0);
                            
                            // выравниваем
                            p_balls.setNextTypeState(i_ballOffset, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);
                            p_balls.alignToArea(i_ballOffset, 0, 0, (MAINRESOLUTION_WIDTH - 1) << 8, (MAINRESOLUTION_HEIGHT - 1) << 8, true);
                        }
                        else if ((i_cellOffset & BLOCKMASK_REFLECTIONLINE) != 0)
                        {
                            // блок в зоне отражателей
                            i_cellOffset &= ~BLOCKMASK_REFLECTIONLINE;
                            ai_ReflectionLine[i_cellOffset] = BLOCK_NONE;
                            GameView.gameAction(GAMEACTION_UPDATEREFLECTIONFIELD,i_cellOffset);
                            
                            int i_cellx = i_cellOffset % FIELDCELLWIDTH;

                            SpriteCollection p_explosions = ap_SpriteCollections[COLLECTIONID_EXPLOSIONS];
                            final int i_explspr = p_explosions.i_lastInactiveSpriteOffset;

                            if (i_explspr >= 0)
                            {
                                // инициируем
                                int i8_x = (I8_CELLWIDTH * i_cellx) + (I8_CELLWIDTH >> 1);
                                int i8_y = (YOFREFLECTIONLINE << 8) + (I8_CELLHEIGHT >> 1);

                                p_explosions.activateOne(i_explspr, SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_EXPLOSION2);
                                p_explosions.setMainPointXY(i_explspr, i8_x, i8_y);
                                p_explosions.setNextTypeState(i_explspr, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);

                                p_explosions.alignToArea(i_explspr, 0, 0, (MAINRESOLUTION_WIDTH - 1) << 8, (MAINRESOLUTION_HEIGHT - 1) << 8, true);
                                
                                GameView.gameAction(GAMEACTION_EXPLOSION,0);
                            }
                            _game_calculateNewBallPath(i_ballOffset);
                        }
                    }
                }
                else
                    _game_calculateNewBallPath(i_ballOffset);

            }
                ;
                break;
            case PATHCOLLECTION_OTHER:
            {
                switch (_pathCollection.getOptionalData(_pathOffset))
                {
                    case PATH_ROCKETIN:
                    {
                        // выставляем флаг отсутствия шарика
                        ap_SpriteCollections[COLLECTIONID_PLAYERROCKET].setOptionalData(0,-1);
                        
                        // инициализируем мячик
                        _game_initBall();
                    }
                        ;
                        break;
                    case PATH_BONUSPATH:
                    case PATH_BONUSPATHFROMGENERATOR:
                    {
                        _pathCollection.releaseLinkedSprite(_pathOffset);
                    }
                        ;
                        break;
                    case PATH_BALLLIN:
                    {
                        // прилинковываем мячик к ракетке
                        ap_SpriteCollections[COLLECTIONID_PLAYERROCKET].setOptionalData(0, p_PathCollection.getSpriteInfo(_pathOffset) & 0xFFFFFF);
                        i_InsideGameState = INSGAMESTATE_PLAYING;
                    }
                        ;
                        break;
                    case PATH_ROCKETOUT:
                    {
                        _pathCollection.releaseLinkedSprite(_pathOffset);
                    }
                        ;
                        break;
                }
            }
                ;
                break;
        }
    }

    //TODO---------------------------SECTION OF GAME FUNCTIONS (all must be private)-----------------------

    /**
     * Проверяем ракетку и падающие бонусы на столкновение
     */
    private static final void _game_processBonusesAndRocketCollision()
    {
        SpriteCollection p_bonuses = ap_SpriteCollections[COLLECTIONID_BONUSES];
        SpriteCollection p_rocket = ap_SpriteCollections[COLLECTIONID_PLAYERROCKET];

        int i_collided = p_bonuses.findCollidedSprite(0, p_rocket, -1);
        if (i_collided >= 0)
        {
            // есть столкновение с бонусом
            int i_state = p_bonuses.getState(i_collided);

            // деактивизируем бонус и его путь
            p_PathCollection.releasePathForSprite(COLLECTIONID_BONUSES, i_collided);
            p_bonuses.releaseOne(i_collided);

            SpriteCollection p_balls = ap_SpriteCollections[COLLECTIONID_PLAYERBALLS];

            switch (i_state)
            {
                case SPRITE_STATE_BONUSES_ADDBALL:
                {
                    int i_lastBallOffset = p_balls.i_lastActiveSpriteOffset;
                    if (i_lastBallOffset >= 0)
                    {

                        int i_newBallOffset = p_balls.i_lastInactiveSpriteOffset;

                        if (i_newBallOffset >= 0)
                        {
                            // спрайт имеется, производим разделение
                            p_balls.cloneActive(i_lastBallOffset, i_newBallOffset);
                            final int i_newBallID = i_newBallOffset / SpriteCollection.SPRITEDATA_LENGTH;
                            final int i_oldBallID = i_lastBallOffset / SpriteCollection.SPRITEDATA_LENGTH;

                            ai_ballDeltaX[i_newBallID] = 0 - ai_ballDeltaX[i_oldBallID];
                            ai_ballDeltaY[i_newBallID] = 0 - ai_ballDeltaY[i_oldBallID];

                            _game_calculateNewBallPath(i_newBallOffset);
                        }

                    }
                }
                    ;
                    break;
                case SPRITE_STATE_BONUSES_ADDSCORE:
                {

                }
                    ;
                    break;
                case SPRITE_STATE_BONUSES_BALLDECREASE:
                {
                    p_balls.initIterator();
                    while (true)
                    {
                        int i_balloffset = p_balls.nextActiveSpriteOffset();
                        if (i_balloffset < 0)
                            break;
                        p_balls.activateOne(i_balloffset, SPRITE_OBJ_BALL, SPRITE_STATE_BALL_SMALL);
                    }
                }
                    ;
                    break;
                case SPRITE_STATE_BONUSES_BALLINCREASE:
                {
                    p_balls.initIterator();
                    while (true)
                    {
                        int i_balloffset = p_balls.nextActiveSpriteOffset();
                        if (i_balloffset < 0)
                            break;
                        p_balls.activateOne(i_balloffset, SPRITE_OBJ_BALL, SPRITE_STATE_BALL_BIG);
                    }
                }
                    ;
                    break;
                case SPRITE_STATE_BONUSES_BALLNORMAL:
                {
                    p_balls.initIterator();
                    while (true)
                    {
                        int i_balloffset = p_balls.nextActiveSpriteOffset();
                        if (i_balloffset < 0)
                            break;
                        p_balls.activateOne(i_balloffset, SPRITE_OBJ_BALL, SPRITE_STATE_BALL_NORMAL);
                    }
                }
                    ;
                    break;
                case SPRITE_STATE_BONUSES_BALLSPEEDMINUS:
                {
                    for (int li = 0; li < MAXBALLS; li++)
                    {
                        int i_curDeltaX = ai_ballDeltaX[li];
                        int i_curDeltaY = ai_ballDeltaY[li];

                        if (i_curDeltaX < 0)
                        {
                            i_curDeltaX += I8_BALLSPEEDSTEP;
                            if (i_curDeltaX > 0 - I8_MINBALLSPEED)
                                i_curDeltaX = 0 - I8_MINBALLSPEED;
                        }
                        else
                        {
                            i_curDeltaX -= I8_BALLSPEEDSTEP;
                            if (i_curDeltaX < I8_MINBALLSPEED)
                                i_curDeltaX = I8_MINBALLSPEED;
                        }

                        if (i_curDeltaY < 0)
                        {
                            i_curDeltaY += I8_BALLSPEEDSTEP;
                            if (i_curDeltaY < 0 - I8_MINBALLSPEED)
                                i_curDeltaY = 0 - I8_MINBALLSPEED;
                        }
                        else
                        {
                            i_curDeltaY -= I8_BALLSPEEDSTEP;
                            if (i_curDeltaY < I8_MINBALLSPEED)
                                i_curDeltaY = I8_MINBALLSPEED;
                        }
                        ai_ballDeltaX[li] = i_curDeltaX;
                        ai_ballDeltaY[li] = i_curDeltaY;
                    }
                }
                    ;
                    break;
                case SPRITE_STATE_BONUSES_BALLSPEEDPLUS:
                {
                    for (int li = 0; li < MAXBALLS; li++)
                    {
                        int i_curDeltaX = ai_ballDeltaX[li];
                        int i_curDeltaY = ai_ballDeltaY[li];

                        if (i_curDeltaX < 0)
                        {
                            i_curDeltaX -= I8_BALLSPEEDSTEP;
                            if (i_curDeltaX < 0 - I8_MAXBALLSPEED)
                                i_curDeltaX = 0 - I8_MAXBALLSPEED;
                        }
                        else
                        {
                            i_curDeltaX += I8_BALLSPEEDSTEP;
                            if (i_curDeltaX > I8_MAXBALLSPEED)
                                i_curDeltaX = I8_MAXBALLSPEED;
                        }

                        if (i_curDeltaY < 0)
                        {
                            i_curDeltaY -= I8_BALLSPEEDSTEP;
                            if (i_curDeltaY < 0 - I8_MAXBALLSPEED)
                                i_curDeltaY = 0 - I8_MAXBALLSPEED;
                        }
                        else
                        {
                            i_curDeltaY += I8_BALLSPEEDSTEP;
                            if (i_curDeltaY > I8_MAXBALLSPEED)
                                i_curDeltaY = I8_MAXBALLSPEED;
                        }
                        ai_ballDeltaX[li] = i_curDeltaX;
                        ai_ballDeltaY[li] = i_curDeltaY;
                    }
                }
                    ;
                    break;
                case SPRITE_STATE_BONUSES_BIGROCKET:
                {
                    int i_frame = p_rocket.getFrameNumber(0);
                    i_state = p_rocket.getState(0);
                    p_rocket.activateOne(0, SPRITE_OBJ_PLAYERROCKETLONG, i_state);
                    p_rocket.setFrameNumber(0, i_frame);
                }
                    ;
                    break;
                case SPRITE_STATE_BONUSES_SMALLROCKET:
                {
                    int i_frame = p_rocket.getFrameNumber(0);
                    i_state = p_rocket.getState(0);
                    p_rocket.activateOne(0, SPRITE_OBJ_PLAYERROCKETSHORT, i_state);
                    p_rocket.setFrameNumber(0, i_frame);
                }
                    ;
                    break;
                case SPRITE_STATE_BONUSES_LIFE:
                {
                    i_PlayerAttemptionsNumber++;
                    GameView.gameAction(GAMEACTION_UPDATEBALLAREA,0);
                }
                    ;
                    break;
                case SPRITE_STATE_BONUSES_MAGNET:
                {
                    i_MagnetoMode = 5;
                }
                    ;
                    break;
                case SPRITE_STATE_BONUSES_NEXTLEVEL:
                {

                }
                    ;
                    break;
                case SPRITE_STATE_BONUSES_REFLECTIONLINE:
                {
                    _game_PlaceReflectionLine();
                    GameView.gameAction(GAMEACTION_ADDREFLECTIONLINE, 0);
                }
                    ;
                    break;
            }

        }
    }

    private static final void _game_PlaceReflectionLine()
    {
        // выставляем линию отражателей
        for (int li = 0; li < FIELDCELLWIDTH; li++)
            ai_ReflectionLine[li] = BLOCK_NORMAL1;
    }

    /**
     * Проверяем мячики на отражение от ракетки, а так же на прохождение песчаного слоя
     */
    private static final void _game_processBallsAndRocketCollision()
    {
        final SpriteCollection p_ballSprites = ap_SpriteCollections[COLLECTIONID_PLAYERBALLS];
        final int[] ai_ballsarray = p_ballSprites.ai_spriteDataArray;
        final PathCollection p_ballspathcollection = p_BallPathCollection;
        p_ballspathcollection.initIterator();

        final int NULL = 0x7FFFFFFF;

        final SpriteCollection p_playerRocket = ap_SpriteCollections[COLLECTIONID_PLAYERROCKET];
        int i8_x = p_playerRocket.getScreenXY(0);
        int i8_y = (i8_x & 0xFFFF) << 8;
        i8_x = (i8_x >> 8) & 0xFFFFFF00;

        int i8_x2 = i8_x + ((p_playerRocket.getWidthHeight(0) >>> 8) & 0xFFFFFF00);

        while (true)
        {
            int i_ballPathOffset = p_ballspathcollection.nextActiveOffset();
            if (i_ballPathOffset < 0)
                break;

            // проверяем на пересечение линии ракетки за предыдущую итерацию пути
            int i8_xcoord = p_ballspathcollection.getIntersectionCoord(i_ballPathOffset, i8_y, false);
            int i_ballSpriteOffset = p_ballspathcollection.getSpriteInfo(i_ballPathOffset) & 0xFFFFFF;
            if (i8_xcoord != NULL)
            {
                // было пересечение, проверяем не попало ли это на зону ракетки

                if (i8_xcoord >= i8_x && i8_xcoord < i8_x2)
                {
                    int i_ballIndex = i_ballSpriteOffset / SpriteCollection.SPRITEDATA_LENGTH;
                    int i_currentPathDeltaX = p_ballspathcollection.ai_PathDynamicDataArray[i_ballPathOffset + PathCollection.OFFSET_DELTAX_I8];
                    int i_currentPathDeltaY = p_ballspathcollection.ai_PathDynamicDataArray[i_ballPathOffset + PathCollection.OFFSET_DELTAY_I8];

                    if (i_currentPathDeltaY > 0)
                    {
                        if (ai_ballDeltaX[i_ballIndex] != i_currentPathDeltaX)
                        {
                            ai_ballDeltaX[i_ballIndex] = i_currentPathDeltaX;
                        }

                        ai_ballDeltaY[i_ballIndex] = 0 - i_currentPathDeltaY;
                        p_ballspathcollection.releaseOne(i_ballPathOffset);

                        // выравниваем шарик
                        p_ballSprites.setMainPointXY(i_ballSpriteOffset, i8_xcoord, i8_y);

                        //проверяем на режим магнита
                        if (i_MagnetoMode > 0)
                        {
                            // проверяем нет ли уже прилинкованного к ракетке шарика
                            if (p_playerRocket.getOptionalData(0) >= 0)
                            {
                                // присутствует, так что просто отбиваем
                                _game_calculateNewBallPath(i_ballSpriteOffset);
                                GameView.gameAction(GAMEACTION_BEATTOROCKET,0);
                                
                            }
                            else
                            {
                                // прилинковываем
                                p_ballspathcollection.releasePathForSprite(COLLECTIONID_PLAYERBALLS, i_ballSpriteOffset);
                                p_ballSprites.moveMainPointXY(i_ballSpriteOffset,0,0-(BALLROCKETOFFSET<<8));
                                p_playerRocket.setOptionalData(0, i_ballSpriteOffset);
                                i_MagnetoMode--;
                            }
                        }
                        else
                            _game_calculateNewBallPath(i_ballSpriteOffset);

                    }

                }
            }
            else
            {
                // проверяем на прохождение песчаной секции
                int i_mx = ai_ballsarray[i_ballSpriteOffset + SpriteCollection.OFFSET_MAINX];
                int i_my = ai_ballsarray[i_ballSpriteOffset + SpriteCollection.OFFSET_MAINY];
                int i_cellx = i_mx / I8_CELLWIDTH;
                int i_celly = i_my / I8_CELLHEIGHT;
                if (i_celly < FIELDCELLHEIGHT)
                {
                    int i_offset = i_cellx + i_celly * FIELDCELLWIDTH;
                    if (ai_GameField[i_offset] == BLOCK_SAND)
                    {
                        // попадание на песчаную секцию
                        _game_processBlockCollision(i_ballSpriteOffset, i_offset);
                    }

                }
            }
        }
    }

    /**
     * Функция отрабатывает взаимодействие мячика и блока в заданной смещением ячейке
     * @param _blockOffset смещение блока с которым столкнулся шарик
     */
    private static final void _game_processBlockCollision(int _ballOffset, int _blockOffset)
    {
        final SpriteCollection p_explosions = ap_SpriteCollections[COLLECTIONID_EXPLOSIONS];
        final SpriteCollection p_balls = ap_SpriteCollections[COLLECTIONID_PLAYERBALLS];
        final int[] ai_gamefield = ai_GameField;
        final int[] ai_bonusfield = ai_BonusField;

        int i_cellx = _blockOffset % FIELDCELLWIDTH;
        int i_celly = _blockOffset / FIELDCELLWIDTH;

        int i8_cellx = i_cellx * I8_CELLWIDTH;
        int i8_celly = i_celly * I8_CELLHEIGHT;

        int i8_cellcenterx = i8_cellx + (I8_CELLWIDTH >> 1);
        int i8_cellcentery = i8_celly + (I8_CELLHEIGHT >> 1);

        switch (ai_gamefield[_blockOffset])
        {
            case BLOCK_SAND:
            {
                //убираем ячейку
                ai_gamefield[_blockOffset] = BLOCK_NONE;
                GameView.gameAction(GAMEACTION_UPDATEMAINGAMEFIELD,_blockOffset);
                i_PlayerScore += SCOREFORBEAT;
                
                i_NumberOfDestroyableBlocksOnScreen--;
                
                int i_explosionSpr = p_explosions.i_lastInactiveSpriteOffset;
                if (i_explosionSpr >= 0)
                {
                    // активизтируем спрайт рассыпающегося песка
                    p_explosions.activateOne(i_explosionSpr, SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_EXPLOSION2);
                    p_explosions.setMainPointXY(i_explosionSpr, i8_cellcenterx, i8_cellcentery);
                    p_explosions.setNextTypeState(i_explosionSpr, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);

                    GameView.gameAction(GAMEACTION_EXPLOSION,0);
                    
                    // выравниваем
                    p_explosions.alignToArea(i_explosionSpr, 0, 0, (MAINRESOLUTION_WIDTH - 1) << 8, (MAINRESOLUTION_HEIGHT - 1) << 8, true);
                }
            }
                ;
                break;
            case BLOCK_IRON:
            {
                // никаких действий
                i_PlayerScore += SCOREFORBEAT;
            }
                ;
                break;
            case BLOCK_BOMBA:
            {
                // генерируем спрайт взрыва
                int i_explosionSpr = p_explosions.i_lastInactiveSpriteOffset;
                i_PlayerScore += SCOREFORBLOCK;
                
                if (i_explosionSpr >= 0)
                {
                    // активизтируем спрайт 
                    p_explosions.activateOne(i_explosionSpr, SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_EXPLOSION1);
                    p_explosions.setMainPointXY(i_explosionSpr, i8_cellcenterx, i8_cellcentery);
                    p_explosions.setNextTypeState(i_explosionSpr, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);
                    GameView.gameAction(GAMEACTION_EXPLOSION,0);
                    
                    // выравниваем спрайт по зоне отображения
                    p_explosions.alignToArea(i_explosionSpr, 0, 0, (MAINRESOLUTION_WIDTH - 1) << 8, (MAINRESOLUTION_HEIGHT - 1) << 8, true);
                }

                GameView.gameAction(GAMEACTION_UPDATEMAINGAMEFIELD,_blockOffset);
                i_NumberOfDestroyableBlocksOnScreen--;
                
                // уменьшаем или убираем блоки по периметру
                for (int lx = i_cellx - 1; lx <= i_cellx + 1; lx++)
                {
                    if (lx < 0 || lx >= FIELDCELLWIDTH)
                        continue;
                    for (int ly = i_celly - 1; ly <= i_celly + 1; ly++)
                    {
                        if (ly < 0 || ly >= FIELDCELLHEIGHT)
                            continue;
                        if (ly == i_celly && lx == i_cellx)
                            continue;

                        int i_offst = ly * FIELDCELLWIDTH + lx;

                        // уменьшаем количество попаданий в ячейки или убираем
                        switch (ai_gamefield[i_offst])
                        {
                            case BLOCK_NONE:
                            {
                                // нет действий
                            }
                                ;
                                break;
                            case BLOCK_NORMAL1:
                            {
                                // убираем
                                ai_gamefield[i_offst] = BLOCK_NONE;
                                // убираем так же и бонус
                                ai_bonusfield[i_offst] = 0;
                                GameView.gameAction(GAMEACTION_UPDATEMAINGAMEFIELD,i_offst);
                                i_NumberOfDestroyableBlocksOnScreen--;
                                i_PlayerScore += SCOREFORBLOCK;
                            }
                                ;
                                break;
                            case BLOCK_NORMAL2:
                            {
                                // уменьшаем на одно
                                ai_gamefield[i_offst] = BLOCK_NORMAL1;
                                GameView.gameAction(GAMEACTION_UPDATEMAINGAMEFIELD,i_offst);
                            }
                                ;
                                break;
                            case BLOCK_NORMAL3:
                            {
                                // уменьшаем на одно
                                ai_gamefield[i_offst] = BLOCK_NORMAL2;
                                GameView.gameAction(GAMEACTION_UPDATEMAINGAMEFIELD,i_offst);
                            }
                                ;
                                break;
                            case BLOCK_IRON:
                            {
                                // нет действий, блок остается без изменений
                            }
                                ;
                                break;
                            case BLOCK_SAND:
                            {
                                // убираем
                                ai_gamefield[i_offst] = BLOCK_NONE;
                                // убираем так же и бонус
                                ai_bonusfield[i_offst] = 0;
                                GameView.gameAction(GAMEACTION_UPDATEMAINGAMEFIELD,i_offst);
                            }
                                ;
                                break;
                            case BLOCK_TELEPORT1IN:
                            case BLOCK_TELEPORT2IN:
                            case BLOCK_TELEPORT1OUT:
                            case BLOCK_TELEPORT2OUT:
                            case BLOCK_BONUSGENERATOR:
                            case BLOCK_TELEPORTIN_DOWN:
                            case BLOCK_TELEPORTOUT_DOWN:
                            case BLOCK_BONUSGENERATOR_DOWN:
                            {
                                // не производим действий над блоком
                            }
                                ;
                                break;
                        }

                    }
                }

            }
                ;
                break;
            case BLOCK_TELEPORTIN_DOWN:
            {
                int i_blockout = ai_gamefield[_blockOffset-FIELDCELLWIDTH] == BLOCK_TELEPORT1IN ? BLOCK_TELEPORT1OUT : BLOCK_TELEPORT2OUT;

                // ищем блок с выходом для первого телепортатора
                for (int li = 0; li < (FIELDCELLWIDTH * FIELDCELLHEIGHT); li++)
                {
                    if (ai_gamefield[li] == i_blockout)
                    {
                        // переводим координаты шарика
                        int i8_cx = (li % FIELDCELLWIDTH) * I8_CELLWIDTH + (I8_CELLWIDTH >> 1);
                        int i8_cy = (li / FIELDCELLWIDTH) * I8_CELLHEIGHT;
                        p_balls.setMainPointXY(_ballOffset, i8_cx, i8_cy);
                        break;
                    }
                }
            }
                ;
                break;
            case BLOCK_TELEPORT2IN:
            case BLOCK_TELEPORT1IN:
            {
                int i_blockout = ai_gamefield[_blockOffset] == BLOCK_TELEPORT1IN ? BLOCK_TELEPORT1OUT : BLOCK_TELEPORT2OUT;

                // ищем блок с выходом для первого телепортатора
                for (int li = 0; li < (FIELDCELLWIDTH * FIELDCELLHEIGHT); li++)
                {
                    if (ai_gamefield[li] == i_blockout)
                    {
                        // переводим координаты шарика
                        int i8_cx = (li % FIELDCELLWIDTH) * I8_CELLWIDTH + (I8_CELLWIDTH >> 1);
                        int i8_cy = (li / FIELDCELLWIDTH + 1) * I8_CELLHEIGHT;
                        p_balls.setMainPointXY(_ballOffset, i8_cx, i8_cy);
                        break;
                    }
                }
            }
                ;
                break;
            case BLOCK_NORMAL1:
            {
                // генерируем спрайт взрыва
                int i_explosionSpr = p_explosions.i_lastInactiveSpriteOffset;
                if (i_explosionSpr >= 0)
                {
                    // активизтируем спрайт 
                    p_explosions.activateOne(i_explosionSpr, SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_EXPLOSION1);
                    p_explosions.setMainPointXY(i_explosionSpr, i8_cellcenterx, i8_cellcentery);
                    p_explosions.setNextTypeState(i_explosionSpr, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);

                    GameView.gameAction(GAMEACTION_EXPLOSION,0);
                    
                    // выравниваем спрайт по зоне отображения
                    p_explosions.alignToArea(i_explosionSpr, 0, 0, (MAINRESOLUTION_WIDTH - 1) << 8, (MAINRESOLUTION_HEIGHT - 1) << 8, true);
                }
                // убираем блок
                ai_gamefield[_blockOffset] = BLOCK_NONE;
                i_PlayerScore += SCOREFORBLOCK;
                GameView.gameAction(GAMEACTION_UPDATEMAINGAMEFIELD,_blockOffset);
                i_NumberOfDestroyableBlocksOnScreen--;

                int i_bonus = ai_bonusfield[_blockOffset];
                ai_bonusfield[_blockOffset] = 0;
                if (i_bonus != 0)
                {
                    // под блоком был бонус, генерируем
                    int i_bonustype = -1;

                    switch (i_bonus)
                    {
                        case BONUSID_ADDBALL:
                        {
                            i_bonustype = SPRITE_STATE_BONUSES_ADDBALL;
                        }
                            ;
                            break;
                        case BONUSID_ADDLIFE:
                        {
                            i_bonustype = SPRITE_STATE_BONUSES_LIFE;
                        }
                            ;
                            break;
                        case BONUSID_ADDSCORE:
                        {
                            i_bonustype = SPRITE_STATE_BONUSES_ADDSCORE;
                        }
                            ;
                            break;
                        case BONUSID_DEATH:
                        {

                        }
                            ;
                            break;
                        case BONUSID_DECREASEBALLSIZE:
                        {
                            i_bonustype = SPRITE_STATE_BONUSES_BALLDECREASE;
                        }
                            ;
                            break;
                        case BONUSID_DECREASEBALLSPEED:
                        {
                            i_bonustype = SPRITE_STATE_BONUSES_BALLSPEEDMINUS;
                        }
                            ;
                            break;
                        case BONUSID_DECREASEROCKETSIZE:
                        {
                            i_bonustype = SPRITE_STATE_BONUSES_SMALLROCKET;
                        }
                            ;
                            break;
                        case BONUSID_INCREASEBALLSIZE:
                        {
                            i_bonustype = SPRITE_STATE_BONUSES_BIGROCKET;
                        }
                            ;
                            break;
                        case BONUSID_INCREASEBALLSPEED:
                        {
                            i_bonustype = SPRITE_STATE_BONUSES_BALLSPEEDPLUS;
                        }
                            ;
                            break;
                        case BONUSID_INCREASEROCKETSIZE:
                        {
                            i_bonustype = SPRITE_STATE_BONUSES_BIGROCKET;
                        }
                            ;
                            break;
                        case BONUSID_MAGNET:
                        {
                            i_bonustype = SPRITE_STATE_BONUSES_MAGNET;
                        }
                            ;
                            break;
                        case BONUSID_NEXTLEVEL:
                        {
                            i_bonustype = SPRITE_STATE_BONUSES_NEXTLEVEL;
                        }
                            ;
                            break;
                        case BONUSID_NORMALBALL:
                        {
                            i_bonustype = SPRITE_STATE_BONUSES_BALLNORMAL;
                        }
                            ;
                            break;
                        case BONUSID_REFLECTOR:
                        {
                            i_bonustype = SPRITE_STATE_BONUSES_REFLECTIONLINE;
                        }
                            ;
                            break;
                    }

                    if (i_bonustype >= 0)
                    {
                        SpriteCollection p_bonuses = ap_SpriteCollections[COLLECTIONID_BONUSES];
                        int i_sprite = p_bonuses.i_lastInactiveSpriteOffset;
                        int i_path = p_PathCollection.i_lastInactivePathOffset;

                        if (i_sprite >= 0 && i_path >= 0)
                        {
                            p_bonuses.activateOne(i_sprite, SPRITE_OBJ_BONUSES, i_bonustype);
                            p_PathCollection.activateOne(i_path, PATH_BONUSPATH, i8_cellcenterx, i8_cellcentery, 0x100, 0x100, COLLECTIONID_BONUSES, i_sprite, PATH_BONUSPATH, 0, 0, 0);
                        }
                    }
                }
            }
                ;
                break;
            case BLOCK_NORMAL2:
            {
                ai_gamefield[_blockOffset] = BLOCK_NORMAL1;
                i_PlayerScore += SCOREFORBEAT;
                GameView.gameAction(GAMEACTION_UPDATEMAINGAMEFIELD,_blockOffset);
                
                // генерируем спрайт взрыва в точке мяча
                int i_explosionSpr = p_explosions.i_lastInactiveSpriteOffset;
                if (i_explosionSpr >= 0)
                {
                    int i8_bx = p_balls.ai_spriteDataArray[_ballOffset + SpriteCollection.OFFSET_MAINX];
                    int i8_by = p_balls.ai_spriteDataArray[_ballOffset + SpriteCollection.OFFSET_MAINY];

                    // активизтируем спрайт 
                    p_explosions.activateOne(i_explosionSpr, SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_EXPLOSION3);
                    p_explosions.setMainPointXY(i_explosionSpr, i8_bx, i8_by);
                    p_explosions.setNextTypeState(i_explosionSpr, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);

                    GameView.gameAction(GAMEACTION_EXPLOSION,0);
                    
                    // выравниваем спрайт по зоне отображения
                    p_explosions.alignToArea(i_explosionSpr, 0, 0, (MAINRESOLUTION_WIDTH - 1) << 8, (MAINRESOLUTION_HEIGHT - 1) << 8, true);
                }
            }
                ;
                break;
            case BLOCK_NORMAL3:
            {
                ai_gamefield[_blockOffset] = BLOCK_NORMAL2;
                i_PlayerScore += SCOREFORBEAT;
                GameView.gameAction(GAMEACTION_UPDATEMAINGAMEFIELD,_blockOffset);
                // генерируем спрайт взрыва в точке мяча
                int i_explosionSpr = p_explosions.i_lastInactiveSpriteOffset;
                if (i_explosionSpr >= 0)
                {
                    int i8_bx = p_balls.ai_spriteDataArray[_ballOffset + SpriteCollection.OFFSET_MAINX];
                    int i8_by = p_balls.ai_spriteDataArray[_ballOffset + SpriteCollection.OFFSET_MAINY];

                    // активизтируем спрайт 
                    p_explosions.activateOne(i_explosionSpr, SPRITE_OBJ_EXPLOSION, SPRITE_STATE_EXPLOSION_EXPLOSION3);
                    p_explosions.setMainPointXY(i_explosionSpr, i8_bx, i8_by);
                    p_explosions.setNextTypeState(i_explosionSpr, SpriteCollection.SPRITEBEHAVIOUR_RELEASE);

                    GameView.gameAction(GAMEACTION_EXPLOSION,0);
                    
                    // выравниваем спрайт по зоне отображения
                    p_explosions.alignToArea(i_explosionSpr, 0, 0, (MAINRESOLUTION_WIDTH - 1) << 8, (MAINRESOLUTION_HEIGHT - 1) << 8, true);
                }
            }
                ;
                break;
            case BLOCK_BONUSGENERATOR_DOWN :
            {
                if (i_DelayToNextBonus <= 0)
                {

                    // генерируем блок с вылетом бонуса из координат центра "блока"
                    int i_topblockindex = _blockOffset;
                    if (_blockOffset > FIELDCELLWIDTH)
                    {
                        i_topblockindex -= FIELDCELLWIDTH;
                        if (ai_gamefield[i_topblockindex] != BLOCK_BONUSGENERATOR)
                            i_topblockindex = _blockOffset;
                        else
                            i8_cellcentery -= I8_CELLHEIGHT;
                    }

                    _game_generateBonusFromGenerator(i8_cellcenterx, i8_celly);

                    i_DelayToNextBonus = DELAYTONEXTBONUSBYBONUSGENERATOR;
                }
            };break;
            case BLOCK_BONUSGENERATOR:
            {
                if (i_DelayToNextBonus <= 0)
                {

                    // генерируем блок с вылетом бонуса из координат центра "блока"
                    int i_topblockindex = _blockOffset;
                    if (_blockOffset > FIELDCELLWIDTH)
                    {
                        i_topblockindex -= FIELDCELLWIDTH;
                        if (ai_gamefield[i_topblockindex] != BLOCK_BONUSGENERATOR)
                            i_topblockindex = _blockOffset;
                        else
                            i8_cellcentery -= I8_CELLHEIGHT;
                    }

                    _game_generateBonusFromGenerator(i8_cellcenterx, i8_cellcentery);

                    i_DelayToNextBonus = DELAYTONEXTBONUSBYBONUSGENERATOR;
                }
            }
                ;
                break;
        }
    }

    /**
     * Генерация бонуса из бонус генератора
     *
     */
    private static final void _game_generateBonusFromGenerator(int _i8cx, int _i8cy)
    {
        SpriteCollection p_bonuses = ap_SpriteCollections[COLLECTIONID_BONUSES];
        int i_spr = p_bonuses.i_lastInactiveSpriteOffset;
        int i_path = p_PathCollection.i_lastInactivePathOffset;
        if (i_spr >= 0 && i_path >= 0)
        {
            int i_bonusType = BONUSID_ADDBALL;

            int i_sprType = -1;
            switch (i_bonusType)
            {
                case BONUSID_ADDBALL:
                {
                    i_sprType = SPRITE_STATE_BONUSES_ADDBALL;
                }
                    ;
                    break;
                case BONUSID_ADDLIFE:
                {
                    i_sprType = SPRITE_STATE_BONUSES_LIFE;
                }
                    ;
                    break;
                case BONUSID_ADDSCORE:
                {
                    i_sprType = SPRITE_STATE_BONUSES_ADDSCORE;
                }
                    ;
                    break;
                case BONUSID_DEATH:
                {

                }
                    ;
                    break;
                case BONUSID_DECREASEBALLSIZE:
                {
                    i_sprType = SPRITE_STATE_BONUSES_BALLDECREASE;
                }
                    ;
                    break;
                case BONUSID_DECREASEBALLSPEED:
                {
                    i_sprType = SPRITE_STATE_BONUSES_BALLSPEEDMINUS;
                }
                    ;
                    break;
                case BONUSID_DECREASEROCKETSIZE:
                {
                    i_sprType = SPRITE_STATE_BONUSES_SMALLROCKET;
                }
                    ;
                    break;
                case BONUSID_INCREASEBALLSIZE:
                {
                    i_sprType = SPRITE_STATE_BONUSES_BALLINCREASE;
                }
                    ;
                    break;
                case BONUSID_INCREASEBALLSPEED:
                {
                    i_sprType = SPRITE_STATE_BONUSES_BALLSPEEDPLUS;
                }
                    ;
                    break;
                case BONUSID_INCREASEROCKETSIZE:
                {
                    i_sprType = SPRITE_STATE_BONUSES_BIGROCKET;
                }
                    ;
                    break;
                case BONUSID_MAGNET:
                {
                    i_sprType = SPRITE_STATE_BONUSES_MAGNET;
                }
                    ;
                    break;
                case BONUSID_NEXTLEVEL:
                {
                    i_sprType = SPRITE_STATE_BONUSES_NEXTLEVEL;
                }
                    ;
                    break;
                case BONUSID_NORMALBALL:
                {
                    i_sprType = SPRITE_STATE_BONUSES_BALLNORMAL;
                }
                    ;
                    break;
                case BONUSID_REFLECTOR:
                {
                    i_sprType = SPRITE_STATE_BONUSES_REFLECTIONLINE;
                }
                    ;
                    break;
            }

            if (i_sprType >= 0)
            {
                p_bonuses.activateOne(i_spr, SPRITE_OBJ_BONUSES, i_sprType);
                p_PathCollection.activateOne(i_path, PATH_BONUSPATHFROMGENERATOR, _i8cx, _i8cy, 0x100, 0x100, COLLECTIONID_BONUSES, i_spr, PATH_BONUSPATHFROMGENERATOR, 0, 0, _i8cx>(MAINRESOLUTION_WIDTH<<7) ? PathCollection.MODIFY_FLIPHORZ : 0);
            }
        }
    }

    /**
     * Инициализация выхода игрока
     */
    private static final void _game_initPlayer()
    {
        i_InsideGameState = INSGAMESTATE_PLAYERIN;
        SpriteCollection p_player = ap_SpriteCollections[COLLECTIONID_PLAYERROCKET];
        p_player.activateOne(0, SPRITE_OBJ_PLAYERROCKETNORMAL, SPRITE_STATE_PLAYERROCKETNORMAL_STAND);
        p_PathCollection.activateOne(p_PathCollection.i_lastInactivePathOffset, PATH_ROCKETIN, MAINRESOLUTION_WIDTH << 7, PLAYER_IN_MAINPOINT_Y << 8, 0x100, 0x100, COLLECTIONID_PLAYERROCKET, 0, PATH_ROCKETIN, 0, 0, 0);
    }

    /**
     * Инициализация шарика
     */
    private static final void _game_initBall()
    {
        i_InsideGameState = INSGAMESTATE_BALLIN;
        SpriteCollection p_ball = ap_SpriteCollections[COLLECTIONID_PLAYERBALLS];
        int i_spr = p_ball.i_lastInactiveSpriteOffset;
        p_ball.activateOne(i_spr, SPRITE_OBJ_BALL, SPRITE_STATE_BALL_NORMAL);
        p_PathCollection.activateOne(p_PathCollection.i_lastInactivePathOffset, PATH_BALLLIN, PLAYER_IN_MAINPOINT_X << 8, (PLAYER_IN_MAINPOINT_Y - BALLROCKETOFFSET) << 8, 0x100, 0x100, COLLECTIONID_PLAYERBALLS, i_spr, PATH_BALLLIN, 0, 0, 0);
    }

    final static int[] ai_ball_pointsArray = new int[MAXBALLS * 9];

    /**
     * Процедура расчета новой траектории шарика и учета его взаимодействий
     * @param _offset смещение спрайта шарика
     */
    private static final void _game_calculateNewBallPath(int _offset)
    {
        SpriteCollection p_balls = ap_SpriteCollections[COLLECTIONID_PLAYERBALLS];
        final int[] ai_ballsArray = p_balls.ai_spriteDataArray;
        final short[] ash_ballspathsarray = ash_ballsPathsArray;

        final int[] ai_ballPointsArray = ai_ball_pointsArray;

        // вычисляем идентификатор мяча
        final int i_ballID = _offset / SpriteCollection.SPRITEDATA_LENGTH;

        // выявляем размеры шарика
        int i8_width = p_balls.getWidthHeight(_offset);
        int i8_height = (i8_width & 0xFFFF) << 8;
        i8_width = (i8_width >>> 8) & 0xFFFFFF00;

        // выявляем координаты шарика
        int i8_x = ai_ballsArray[_offset + SpriteCollection.OFFSET_SCREENX];
        int i8_y = ai_ballsArray[_offset + SpriteCollection.OFFSET_SCREENY];

        int i8_deltaX = ai_ballDeltaX[i_ballID] + (getRndInt(100) > 50 ? I8_BALLSPEEDDEVIATION : 0 - I8_BALLSPEEDDEVIATION);
        int i8_deltaY = ai_ballDeltaY[i_ballID] + (getRndInt(100) > 50 ? I8_BALLSPEEDDEVIATION : 0 - I8_BALLSPEEDDEVIATION);

        if (i8_deltaX > 0)
        {
            if (i8_deltaX > I8_MAXBALLSPEED)
                i8_deltaX = I8_MAXBALLSPEED;
            else if (i8_deltaX < I8_MINBALLSPEED)
                i8_deltaX = I8_MINBALLSPEED;
        }
        else
        {
            if (i8_deltaX < 0 - I8_MAXBALLSPEED)
                i8_deltaX = 0 - I8_MAXBALLSPEED;
            else if (i8_deltaX > 0 - I8_MINBALLSPEED)
                i8_deltaX = 0 - I8_MINBALLSPEED;
        }

        if (i8_deltaY > 0)
        {
            if (i8_deltaY > I8_MAXBALLSPEED)
                i8_deltaY = I8_MAXBALLSPEED;
            else if (i8_deltaY < I8_MINBALLSPEED)
                i8_deltaY = I8_MINBALLSPEED;
        }
        else
        {
            if (i8_deltaY < 0 - I8_MAXBALLSPEED)
                i8_deltaY = 0 - I8_MAXBALLSPEED;
            else if (i8_deltaY > 0 - I8_MINBALLSPEED)
                i8_deltaY = 0 - I8_MINBALLSPEED;
        }

        boolean lg_toLeft = i8_deltaX < 0;
        boolean lg_toUp = i8_deltaY < 0;

        // выбираем точки граней мячика
        if (i8_deltaX < 0)
        {
            if (i8_deltaY < 0)
            {
                ai_ballPointsArray[0] = i8_x;
                ai_ballPointsArray[1] = i8_y + i8_height - 0x100;
                ai_ballPointsArray[2] = i8_width >> 1;
                ai_ballPointsArray[3] = 0 - (i8_height >> 1);

                ai_ballPointsArray[9] = i8_x;
                ai_ballPointsArray[10] = i8_y;
                ai_ballPointsArray[11] = (i8_width >> 1);
                ai_ballPointsArray[12] = (i8_height >> 1);

                ai_ballPointsArray[18] = i8_x + i8_width - 0x100;
                ai_ballPointsArray[19] = i8_y;
                ai_ballPointsArray[20] = 0 - (i8_width >> 1);
                ai_ballPointsArray[21] = (i8_height >> 1);

            }
            else
            {
                ai_ballPointsArray[0] = i8_x;
                ai_ballPointsArray[1] = i8_y;
                ai_ballPointsArray[2] = (i8_width >> 1);
                ai_ballPointsArray[3] = (i8_height >> 1);

                ai_ballPointsArray[9] = i8_x;
                ai_ballPointsArray[10] = i8_y + i8_height - 0x100;
                ai_ballPointsArray[11] = i8_width >> 1;
                ai_ballPointsArray[12] = 0 - (i8_height >> 1);

                ai_ballPointsArray[18] = i8_x + i8_width - 0x100;
                ai_ballPointsArray[19] = i8_y + i8_height - 0x100;
                ai_ballPointsArray[20] = 0 - (i8_width >> 1);
                ai_ballPointsArray[21] = 0 - (i8_height >> 1);

            }
        }
        else
        {
            if (i8_deltaY < 0)
            {
                ai_ballPointsArray[0] = i8_x;
                ai_ballPointsArray[1] = i8_y;
                ai_ballPointsArray[2] = (i8_width >> 1);
                ai_ballPointsArray[3] = (i8_height >> 1);

                ai_ballPointsArray[9] = i8_x + i8_width - 0x100;
                ai_ballPointsArray[10] = i8_y;
                ai_ballPointsArray[11] = 0 - (i8_width >> 1);
                ai_ballPointsArray[12] = (i8_height >> 1);

                ai_ballPointsArray[18] = i8_x + i8_width - 0x100;
                ai_ballPointsArray[19] = i8_y + i8_height - 0x100;
                ai_ballPointsArray[20] = 0 - (i8_width >> 1);
                ai_ballPointsArray[21] = 0 - (i8_height >> 1);

            }
            else
            {
                ai_ballPointsArray[0] = i8_x;
                ai_ballPointsArray[1] = i8_y + i8_height - 0x100;
                ai_ballPointsArray[2] = (i8_width >> 1);
                ai_ballPointsArray[3] = 0 - (i8_height >> 1);

                ai_ballPointsArray[9] = i8_x + i8_width - 0x100;
                ai_ballPointsArray[10] = i8_y + i8_height - 0x100;
                ai_ballPointsArray[11] = 0 - (i8_width >> 1);
                ai_ballPointsArray[12] = 0 - (i8_height >> 1);

                ai_ballPointsArray[18] = i8_x + i8_width - 0x100;
                ai_ballPointsArray[19] = i8_y;
                ai_ballPointsArray[20] = 0 - (i8_width >> 1);
                ai_ballPointsArray[21] = (i8_height >> 1);
            }
        }

        final int i8_absDeltaX = Math.abs(i8_deltaX);
        final int i8_absDeltaY = Math.abs(i8_deltaY);

        final int I8_AREAX = (MAINRESOLUTION_WIDTH - 1) << 8;
        final int I8_AREAY = (MAINRESOLUTION_HEIGHT - 1) << 8;

        int i_offset = 0;
        int i_indexOfSelected = 0;
        long l_lengthOfSelected = 0x7FFFFFFFFFFFFFFFL;
        for (int li = 0; li < 3; li++)
        {
            int i8_pntx = ai_ballPointsArray[i_offset++];
            int i8_pnty = ai_ballPointsArray[i_offset++];
            i_offset += 2;

            int i8_wallpntx2 = 0;
            int i8_wallpnty2 = 0;

            int i_steps = 0;

            // флаги отражения
            boolean lg_mirrorX = false;
            boolean lg_mirrorY = false;

            // индекс удаляемого блока
            int i_blockIndex = -1;

            // вычисляем точку пересечения со стенками поля
            if (lg_toLeft)
            {
                // направление влево
                int i_stepsX = i8_absDeltaX == 0 ? 0x7FFFFFFF : (i8_pntx + (i8_absDeltaX - 1)) / i8_absDeltaX;
                if (lg_toUp)
                {
                    int i_stepsY = i8_absDeltaY == 0 ? 0x7FFFFFFF : (i8_pnty + (i8_absDeltaY - 1)) / i8_absDeltaY;

                    // изначальное предположение угла
                    i8_wallpntx2 = 0;
                    i8_wallpnty2 = 0;

                    i_steps = i_stepsX;

                    if (i_stepsX < i_stepsY)
                    {
                        // пересечение будет с левой границей
                        i8_wallpnty2 = i8_pnty + (i_stepsX * i8_deltaY);
                        lg_mirrorX = true;
                    }
                    else if (i_stepsX > i_stepsY)
                    {
                        // пересечение будет с верхней границей
                        i8_wallpntx2 = i8_pntx + (i_stepsY * i8_deltaX);
                        i_steps = i_stepsY;
                        lg_mirrorY = true;
                    }
                    else
                    {
                        lg_mirrorX = true;
                        lg_mirrorY = true;
                    }
                }
                else
                {
                    int i_stepsY = i8_absDeltaY == 0 ? 0x7FFFFFFF : ((I8_AREAY - i8_pnty) + (i8_absDeltaY - 1)) / i8_absDeltaY;

                    // изначальное предположение угла
                    i8_wallpntx2 = 0;
                    i8_wallpnty2 = I8_AREAY;

                    i_steps = i_stepsX;

                    if (i_stepsX < i_stepsY)
                    {
                        // пересечение будет с левой границей
                        i8_wallpnty2 = i8_pnty + (i_stepsX * i8_deltaY);
                        lg_mirrorX = true;
                    }
                    else if (i_stepsX > i_stepsY)
                    {
                        // пересечение будет с нижней границей
                        i8_wallpntx2 = i8_pntx + (i_stepsY * i8_deltaX);
                        i_steps = i_stepsY;
                        lg_mirrorY = true;
                        i_blockIndex = BLOCKMASK_EXPLOSIONBALL;
                    }
                    else
                    {
                        lg_mirrorX = true;
                        lg_mirrorY = true;
                        i_blockIndex = BLOCKMASK_EXPLOSIONBALL;
                    }
                }
            }
            else
            {
                // направление вправо
                int i_stepsX = i8_absDeltaX == 0 ? 0x7FFFFFFF : ((I8_AREAX - i8_pntx) + (i8_absDeltaX - 1)) / i8_absDeltaX;

                i_steps = i_stepsX;

                if (lg_toUp)
                {
                    int i_stepsY = i8_absDeltaY == 0 ? 0x7FFFFFFF : (i8_pnty + (i8_absDeltaY - 1)) / i8_absDeltaY;

                    // изначальное предположение угла
                    i8_wallpntx2 = I8_AREAX;
                    i8_wallpnty2 = 0;

                    if (i_stepsX < i_stepsY)
                    {
                        // пересечение будет с левой границей
                        i8_wallpnty2 = i8_pnty + (i_stepsX * i8_deltaY);
                        lg_mirrorX = true;
                    }
                    else if (i_stepsX > i_stepsY)
                    {
                        // пересечение будет с верхней границей
                        i8_wallpntx2 = i8_pntx + (i_stepsY * i8_deltaX);
                        i_steps = i_stepsY;
                        lg_mirrorY = true;
                    }
                    else
                    {
                        lg_mirrorX = true;
                        lg_mirrorY = true;
                    }
                }
                else
                {
                    int i_stepsY = i8_absDeltaY == 0 ? 0x7FFFFFFF : ((I8_AREAY - i8_pnty) + (i8_absDeltaY - 1)) / i8_absDeltaY;

                    // изначальное предположение угла
                    i8_wallpntx2 = I8_AREAX;
                    i8_wallpnty2 = I8_AREAY;

                    if (i_stepsX < i_stepsY)
                    {
                        // пересечение будет с правой границей
                        i8_wallpnty2 = i8_pnty + (i_stepsX * i8_deltaY);
                        lg_mirrorX = true;
                    }
                    else if (i_stepsX > i_stepsY)
                    {
                        // пересечение будет с нижней границей
                        i8_wallpntx2 = i8_pntx + (i_stepsY * i8_deltaX);
                        i_steps = i_stepsY;
                        lg_mirrorY = true;
                        i_blockIndex = BLOCKMASK_EXPLOSIONBALL;
                    }
                    else
                    {
                        lg_mirrorX = true;
                        lg_mirrorY = true;
                        i_blockIndex = BLOCKMASK_EXPLOSIONBALL;
                    }
                }
            }

            final long l_distx = i8_wallpntx2 - i8_pntx;
            final long l_disty = i8_wallpnty2 - i8_pnty;
            long l_distance = l_distx * l_distx + l_disty * l_disty;

            // производим проверку на требование к просчету совпадения с ячейками на игровом поле
            boolean lg_needCheckingForCellsArea = (i8_wallpnty2 <= I8_STARTGAMEFIELDY) || (i8_pnty <= I8_STARTGAMEFIELDY);

            if (lg_needCheckingForCellsArea)
            {
                // проверяем отрезок на пересеченность с ячейками игрового поля
                // в случае пересечения, переносим конец отрезка на пересекаемую грань
                // и помечаем индекс блока с которым будет осуществлено столкновение

                int i8_diffx = i8_wallpntx2 - i8_pntx;
                if (i8_diffx == 0)
                    i8_diffx = 1;

                int i8_angleCoeff = ((i8_wallpnty2 - i8_pnty) << 8) / i8_diffx;

                // вычисляем точки первого пересечения
                int i8_edgeY, i8_edgeX;

                int i_cellx = i8_pntx / I8_CELLWIDTH;
                int i_celly = i8_pnty / I8_CELLHEIGHT;

                int i_cellstepx, i_cellstepy;
                int i_offsetstepx, i_offsetstepy;

                if (lg_toLeft)
                {
                    i_cellx--;
                    i8_edgeX = ((i_cellx + 1) * I8_CELLWIDTH) - 0x100;
                    i_cellstepx = -1;
                    i_offsetstepx = -I8_CELLWIDTH;
                }
                else
                {
                    i_cellx++;
                    i8_edgeX = i_cellx * I8_CELLWIDTH;
                    i_cellstepx = 1;
                    i_offsetstepx = I8_CELLWIDTH;
                }

                if (lg_toUp)
                {
                    i_celly--;
                    i8_edgeY = ((i_celly + 1) * I8_CELLHEIGHT) - 0x100;
                    i_cellstepy = -1;
                    i_offsetstepy = -I8_CELLHEIGHT;
                }
                else
                {
                    i_celly++;
                    i8_edgeY = i_celly * I8_CELLHEIGHT;
                    i_cellstepy = 1;
                    i_offsetstepy = I8_CELLHEIGHT;
                }

                final int i_finalcellx = (i8_wallpntx2 / I8_CELLWIDTH) + (lg_toLeft ? -1 : 1);
                final int i_finalcelly = (i8_wallpnty2 / I8_CELLHEIGHT) + (lg_toUp ? -1 : 1);

                long l_distanceToVert = 0x7FFFFFFFFFFFFFFFL;
                long l_distanceToHorz = 0x7FFFFFFFFFFFFFFFL;

                // проверка с горизонтальной
                int i8_horzx = 0;
                int i_horzblocloffset = -1;

                while (i_celly != i_finalcelly)
                {
                    if (i_celly < FIELDCELLHEIGHT)
                    {
                        i8_horzx = i8_pntx + ((i8_edgeY - i8_pnty) << 8) / i8_angleCoeff;

                        int i_cellxcoord = i8_horzx / I8_CELLWIDTH;

                        if (i_cellxcoord >= 0 && i_cellxcoord < FIELDCELLWIDTH)
                        {

                            int i_celloffset = i_celly * FIELDCELLWIDTH + (i8_horzx / I8_CELLWIDTH);
                            switch (ai_GameField[i_celloffset])
                            {
                                case BLOCK_NONE:
                                case BLOCK_SAND:
                                case BLOCK_TELEPORT1OUT:
                                case BLOCK_TELEPORT2OUT:
                                case BLOCK_TELEPORTOUT_DOWN:
                                {

                                }
                                    ;
                                    break;
                                default:
                                {
                                    i_horzblocloffset = i_celloffset;
                                    long l_diffx = i8_pntx - i8_horzx;
                                    long l_diffy = i8_pnty - i8_edgeY;
                                    l_distanceToHorz = l_diffx * l_diffx + l_diffy * l_diffy;
                                }
                            }

                            if (i_horzblocloffset >= 0)
                                break;
                        }
                    }

                    i_celly += i_cellstepy;
                    i8_edgeY += i_offsetstepy;
                }

                // проверка c вертикальной 
                int i8_verty = 0;
                int i_vertblocloffset = -1;

                if (i_cellx >= 0 && i_cellx < FIELDCELLWIDTH)
                    while (i_cellx != i_finalcellx)
                    {
                        i8_verty = i8_pnty + (((i8_edgeX - i8_pntx) * i8_angleCoeff) >> 8);

                        int i_ytmp = i8_verty / I8_CELLHEIGHT;

                        if (i_ytmp < FIELDCELLHEIGHT)
                        {
                            int i_celloffset = i_ytmp * FIELDCELLWIDTH + i_cellx;
                            switch (ai_GameField[i_celloffset])
                            {
                                case BLOCK_NONE:
                                case BLOCK_SAND:
                                case BLOCK_TELEPORT1OUT:
                                case BLOCK_TELEPORT2OUT:
                                case BLOCK_TELEPORTOUT_DOWN:
                                {

                                }
                                    ;
                                    break;
                                default:
                                {
                                    i_vertblocloffset = i_celloffset;
                                    long l_diffx = i8_pntx - i8_edgeX;
                                    long l_diffy = i8_pnty - i8_verty;
                                    l_distanceToVert = l_diffx * l_diffx + l_diffy * l_diffy;
                                }
                            }

                            if (i_vertblocloffset >= 0)
                                break;
                        }

                        i_cellx += i_cellstepx;
                        i8_edgeX += i_offsetstepx;
                    }

                if (l_distanceToHorz < l_distance || l_distanceToVert < l_distance)
                {
                    if (l_distanceToHorz < l_distanceToVert)
                    {
                        // выбираем горизонтальную точку пересечения
                        lg_mirrorY = true;
                        lg_mirrorX = false;

                        i8_wallpnty2 = i8_edgeY + (lg_toUp ? 0x100 : -0x100);
                        i8_wallpntx2 = i8_horzx + (lg_toLeft ? 0x100 : -0x100);
                        l_distance = l_distanceToHorz;

                        i_blockIndex = i_horzblocloffset;

                    }
                    else if (l_distanceToHorz > l_distanceToVert)
                    {
                        lg_mirrorY = false;
                        lg_mirrorX = true;

                        i8_wallpntx2 = i8_edgeX + (lg_toLeft ? 0x100 : -0x100);
                        i8_wallpnty2 = i8_verty + (lg_toUp ? 0x100 : -0x100);

                        l_distance = l_distanceToVert;

                        i_blockIndex = i_vertblocloffset;
                    }
                    else
                    {
                        lg_mirrorY = true;
                        lg_mirrorX = true;
                        i8_wallpntx2 = i8_horzx;
                        i8_wallpnty2 = i8_verty;

                        l_distance = l_distanceToHorz;

                        i_blockIndex = i_horzblocloffset;
                    }
                    int i8_dx = Math.abs(i8_wallpntx2 - i8_pntx);
                    int i8_dy = Math.abs(i8_wallpnty2 - i8_pnty);
                    i_steps = i8_dx > i8_dy ? i8_dx / i8_absDeltaX : i8_dy / i8_absDeltaY;
                }

                // проверка на столкновение с блоком телепортатором
                if (i_blockIndex != BLOCKMASK_EXPLOSIONBALL && i_blockIndex >= 0)
                {
                    int i_blocktype = ai_GameField[i_blockIndex];
                    switch (i_blocktype)
                    {
                        case BLOCK_TELEPORT1IN:
                        case BLOCK_TELEPORT2IN:
                        case BLOCK_TELEPORTIN_DOWN:
                        {
                            // выясняем индекс верхнего блока телепортатора
                            int i_topindex = i_blockIndex;
                            if (i_blockIndex > FIELDCELLWIDTH)
                            {
                                if (i_blocktype == BLOCK_TELEPORTIN_DOWN)
                                    i_topindex = i_blockIndex - FIELDCELLWIDTH;

                                // вычисляем координаты центральной точки блоков
                                i8_wallpntx2 = ((i_topindex % FIELDCELLWIDTH) * I8_CELLWIDTH) + (I8_CELLWIDTH >> 1);
                                i8_wallpnty2 = ((i_topindex / FIELDCELLWIDTH) + 1) * I8_CELLHEIGHT;

                                int i8_dx = Math.abs(i8_wallpntx2 - i8_pntx);
                                int i8_dy = Math.abs(i8_wallpnty2 - i8_pnty);
                                i_steps = i8_dx > i8_dy ? i8_dx / i8_absDeltaX : i8_dy / i8_absDeltaY;
                                long l_diffx = i8_wallpntx2 - i8_pntx;
                                long l_diffy = i8_wallpnty2 - i8_pnty;
                                l_distance = l_diffx * l_diffx + l_diffy * l_diffy;
                            }
                        }
                            ;
                            break;
                    }
                }

            }

            // производим проверку на требование к просчету совпадения с ячейками ряда, расположенного за тележкой 
            boolean lg_needCheckingForReflectionCellsArea = (i8_wallpnty2 >= (YOFREFLECTIONLINE << 8)) && (i8_pnty < (YOFREFLECTIONLINE << 8));

            if (lg_needCheckingForReflectionCellsArea)
            {
                // проверяем на пересечение с областью временного препятствия

                // вычисляем угловой коэффициент
                int i8_diffx = i8_pntx - i8_wallpntx2;
                int i8_diffy = i8_pnty - i8_wallpnty2;

                if (i8_diffx == 0)
                    i8_diffx = 1;

                int i8_coeff = (i8_diffy << 8) / i8_diffx;
                if (i8_coeff == 0)
                    i8_coeff = 1;

                // вычисляем координату точки X на линии
                int i8_xcollided = i8_pntx + (((YOFREFLECTIONLINE << 8) - i8_pnty) << 8) / i8_coeff;

                // выясняем состояние ячейки
                int i_cell = i8_xcollided / I8_CELLWIDTH;

                if ((i_cell>=0 && i_cell<FIELDCELLWIDTH) && ai_ReflectionLine[i_cell] != BLOCK_NONE)
                {
                    // будет произведено столкновение
                    // пересчитываем координаты точки столкновения шара и скорость
                    int i8_ycollided = i8_pnty + (((i8_xcollided - i8_pntx) * i8_coeff) >> 8);

                    // вычисляем дистанцию
                    long l_xdiff = i8_pntx - i8_xcollided;
                    long l_ydiff = i8_pnty - i8_ycollided;

                    i8_wallpntx2 = i8_xcollided;
                    i8_wallpnty2 = i8_ycollided;

                    l_distance = l_xdiff * l_xdiff + l_ydiff * l_ydiff;

                    // вычисляем количество шагов
                    int i8_dx = Math.abs((int) l_xdiff);
                    int i8_dy = Math.abs((int) l_ydiff);
                    i_steps = i8_dx > i8_dy ? i8_dx / i8_absDeltaX : i8_dy / i8_absDeltaY;

                    // выставляем флаг отражения по вертикали
                    lg_mirrorX = false;
                    lg_mirrorY = true;

                    // выставляем индекс удаляемого блока с флагом того что он в зоне отражателей
                    i_blockIndex = BLOCKMASK_REFLECTIONLINE | i_cell;
                }
            }

            ai_ballPointsArray[i_offset++] = i8_wallpntx2;
            ai_ballPointsArray[i_offset++] = i8_wallpnty2;
            ai_ballPointsArray[i_offset++] = i_steps;
            ai_ballPointsArray[i_offset++] = (lg_mirrorX ? 2 : 0x00) | (lg_mirrorY ? 1 : 0x00);

            ai_ballPointsArray[i_offset++] = i_blockIndex;

            if (l_distance < l_lengthOfSelected)
            {
                i_indexOfSelected = li;
                l_lengthOfSelected = l_distance;
            }
        }

        // задаем путь, который пройдет шарик до пересечения
        final int i_pathOffsetVal = i_ballID << 3;
        int i_pathoffset = i_pathOffsetVal + 2;

        int i_selectedBallOffset = i_indexOfSelected * 9;

        int i8_startx = ai_ballPointsArray[i_selectedBallOffset++];
        int i8_starty = ai_ballPointsArray[i_selectedBallOffset++];
        int i8_offx = ai_ballPointsArray[i_selectedBallOffset++];
        int i8_offy = ai_ballPointsArray[i_selectedBallOffset++];

        int i_destx = ((ai_ballPointsArray[i_selectedBallOffset++] + i8_offx) + 0x7F) >> 8;
        int i_desty = ((ai_ballPointsArray[i_selectedBallOffset++] + i8_offy) + 0x7F) >> 8;
        int i_steps = ai_ballPointsArray[i_selectedBallOffset++];
        int i_reflections = ai_ballPointsArray[i_selectedBallOffset++];
        int i_blockIndex = ai_ballPointsArray[i_selectedBallOffset++];

        int i8_newDeltaX = i8_deltaX;
        int i8_newDeltaY = i8_deltaY;

        if ((i_reflections & 2) != 0)
        {
            // отражение по X
            i8_newDeltaX = 0 - i8_newDeltaX;
        }

        if ((i_reflections & 1) != 0)
        {
            // отражение по Y
            i8_newDeltaY = 0 - i8_newDeltaY;
        }

        if (i_steps == 0)
            i_steps = 1;

        //System.out.println("x=" + ((i8_startx + i8_offx + 0x7F) >> 8) + " y=" + ((i8_starty + i8_offy + 0x7F) >> 8) + " steps=" + i_steps + " newDeltaXY=" + i8_newDeltaX + "," + i8_newDeltaY);

        ash_ballspathsarray[i_pathoffset++] = (short) ((i8_startx + i8_offx + 0x7F) >> 8);
        ash_ballspathsarray[i_pathoffset++] = (short) ((i8_starty + i8_offy + 0x7F) >> 8);
        ash_ballspathsarray[i_pathoffset++] = (short) (i_steps);
        ash_ballspathsarray[i_pathoffset++] = (short) (i_destx);
        ash_ballspathsarray[i_pathoffset++] = (short) (i_desty);
        ash_ballspathsarray[i_pathoffset] = 0;

        ai_ballDeltaX[i_ballID] = i8_newDeltaX;
        ai_ballDeltaY[i_ballID] = i8_newDeltaY;
        ai_blockIndexForRemoving[i_ballID] = i_blockIndex;

        p_BallPathCollection.activateOne(p_BallPathCollection.i_lastInactivePathOffset, 0xBABE, 0, 0, 0x100, 0x100, COLLECTIONID_PLAYERBALLS, _offset, i_pathOffsetVal, 0, 0, 0);
    }

    private static final void _game_initPlayerWinMode()
    {
        // "взрываем" летающие шарики, остановив их вначале
        SpriteCollection p_sprCol = ap_SpriteCollections[COLLECTIONID_PLAYERBALLS];
        PathCollection p_paths = p_BallPathCollection;
        
        p_sprCol.initIterator();
        p_paths.releaseAll();        
        while(true)
        {
            int i_ball = p_sprCol.nextActiveSpriteOffset();
            if (i_ball<0) break;
            p_sprCol.activateOne(i_ball,SPRITE_OBJ_EXPLOSION,SPRITE_STATE_EXPLOSION_EXPLOSION1+getRndInt(2));
            p_sprCol.alignToArea(i_ball,0,0,(MAINRESOLUTION_WIDTH-1)<<8,(MAINRESOLUTION_HEIGHT-1)<<8,true);
            p_sprCol.setNextTypeState(i_ball,SpriteCollection.SPRITEBEHAVIOUR_RELEASE);
            
            GameView.gameAction(GAMEACTION_EXPLOSION,0);
        }
        
        // переводим игрока на путь ухода
        p_paths = p_PathCollection;
        
        int i_pathforplayer = p_paths.i_lastInactivePathOffset;
        
        p_sprCol = ap_SpriteCollections[COLLECTIONID_PLAYERROCKET];
        
        int i8_x = p_sprCol.ai_spriteDataArray[SpriteCollection.OFFSET_MAINX];
        int i8_y = p_sprCol.ai_spriteDataArray[SpriteCollection.OFFSET_MAINY];
        
        p_paths.activateOne(i_pathforplayer,PATH_ROCKETOUT,i8_x,i8_y,0x100,0x100,COLLECTIONID_PLAYERROCKET,0,PATH_ROCKETOUT,0,0,0);
    }
    
    private static final void _game_startLinkedBall(int _ballOffset)
    {
        final int i_ballID = _ballOffset / SpriteCollection.SPRITEDATA_LENGTH;

        ai_ballDeltaX[i_ballID] = (I8_MAXBALLSPEED + I8_MINBALLSPEED) >> 1;
        ai_ballDeltaY[i_ballID] = 0 - ((I8_MAXBALLSPEED + I8_MINBALLSPEED) >> 1);

        _game_calculateNewBallPath(_ballOffset);
    }

    protected static final short[] ash_SpritesTable = new short[] {
            // Object PLAYERROCKETSHORT
            (short) 7,
            // Object PLAYERROCKETNORMAL
            (short) 10,
            // Object PLAYERROCKETLONG
            (short) 13,
            // Object BALL
            (short) 16,
            // Object BONUSES
            (short) 19,
            // Object MEGABLOCK
            (short) 32,
            // Object EXPLOSION
            (short) 37,
            // Object PLAYERROCKETSHORT state STAND
            (short) 40,
            // Object PLAYERROCKETSHORT state MOVINGLEFT
            (short) 40,
            // Object PLAYERROCKETSHORT state MOVINGRIGHT
            (short) 40,
            // Object PLAYERROCKETNORMAL state STAND
            (short) 51,
            // Object PLAYERROCKETNORMAL state MOVINGLEFT
            (short) 51,
            // Object PLAYERROCKETNORMAL state MOVINGRIGHT
            (short) 51,
            // Object PLAYERROCKETLONG state STAND
            (short) 62,
            // Object PLAYERROCKETLONG state MOVINGLEFT
            (short) 62,
            // Object PLAYERROCKETLONG state MOVINGRIGHT
            (short) 62,
            // Object BALL state NORMAL
            (short) 73,
            // Object BALL state SMALL
            (short) 84,
            // Object BALL state BIG
            (short) 95,
            // Object BONUSES state BALLSPEEDPLUS
            (short) 106,
            // Object BONUSES state BALLSPEEDMINUS
            (short) 106,
            // Object BONUSES state BALLINCREASE
            (short) 106,
            // Object BONUSES state BALLDECREASE
            (short) 106,
            // Object BONUSES state BALLNORMAL
            (short) 106,
            // Object BONUSES state BIGROCKET
            (short) 106,
            // Object BONUSES state SMALLROCKET
            (short) 106,
            // Object BONUSES state LIFE
            (short) 106,
            // Object BONUSES state MAGNET
            (short) 106,
            // Object BONUSES state NEXTLEVEL
            (short) 106,
            // Object BONUSES state ADDBALL
            (short) 106,
            // Object BONUSES state ADDSCORE
            (short) 106,
            // Object BONUSES state REFLECTIONLINE
            (short) 106,
            // Object MEGABLOCK state TELEPORTIN1
            (short) 117,
            // Object MEGABLOCK state TELEPORTOUT1
            (short) 117,
            // Object MEGABLOCK state TELEPORTIN2
            (short) 117,
            // Object MEGABLOCK state TELEPORTOUT2
            (short) 117,
            // Object MEGABLOCK state BONUSGENERATOR
            (short) 117,
            // Object EXPLOSION state EXPLOSION1
            (short) 128,
            // Object EXPLOSION state EXPLOSION2
            (short) 139,
            // Object EXPLOSION state EXPLOSION3
            (short) 128, (short) 61, (short) 45, (short) 0, (short) 0, (short) 61, (short) 45, (short) 4, (short) 0, (short) 0, (short) 30, (short) 0, (short) 83, (short) 45, (short) 0, (short) 0, (short) 83, (short) 45, (short) 4, (short) 0, (short) 0, (short) 41, (short) 0, (short) 120, (short) 45, (short) 0, (short) 0, (short) 120, (short) 45, (short) 4, (short) 0, (short) 0, (short) 60,
            (short) 0, (short) 20, (short) 20, (short) 0, (short) 0, (short) 20, (short) 20, (short) 3, (short) 0, (short) 0, (short) 10, (short) 10, (short) 16, (short) 16, (short) 0, (short) 0, (short) 16, (short) 16, (short) 2, (short) 0, (short) 0, (short) 8, (short) 8, (short) 24, (short) 24, (short) 0, (short) 0, (short) 24, (short) 24, (short) 6, (short) 0, (short) 0, (short) 12,
            (short) 12, (short) 62, (short) 62, (short) 0, (short) 0, (short) 62, (short) 62, (short) 3, (short) 0, (short) 0, (short) 31, (short) 31, (short) 34, (short) 40, (short) 0, (short) 0, (short) 34, (short) 40, (short) 1, (short) 0, (short) 1, (short) 0, (short) 0, (short) 44, (short) 44, (short) 0, (short) 0, (short) 44, (short) 44, (short) 8, (short) 0, (short) 1, (short) 22,
            (short) 22, (short) 44, (short) 44, (short) 0, (short) 0, (short) 44, (short) 44, (short) 6, (short) 0, (short) 1, (short) 22, (short) 22,
    };

    //------------------Sprite constants-----------------
    public static final int SPRITE_OBJ_PLAYERROCKETSHORT = 0;

    public static final int SPRITE_STATE_PLAYERROCKETSHORT_STAND = 0;

    public static final int SPRITE_STATE_PLAYERROCKETSHORT_MOVINGLEFT = 1;

    public static final int SPRITE_STATE_PLAYERROCKETSHORT_MOVINGRIGHT = 2;

    public static final int SPRITE_OBJ_PLAYERROCKETNORMAL = 1;

    public static final int SPRITE_STATE_PLAYERROCKETNORMAL_STAND = 0;

    public static final int SPRITE_STATE_PLAYERROCKETNORMAL_MOVINGLEFT = 1;

    public static final int SPRITE_STATE_PLAYERROCKETNORMAL_MOVINGRIGHT = 2;

    public static final int SPRITE_OBJ_PLAYERROCKETLONG = 2;

    public static final int SPRITE_STATE_PLAYERROCKETLONG_STAND = 0;

    public static final int SPRITE_STATE_PLAYERROCKETLONG_MOVINGLEFT = 1;

    public static final int SPRITE_STATE_PLAYERROCKETLONG_MOVINGRIGHT = 2;

    public static final int SPRITE_OBJ_BALL = 3;

    public static final int SPRITE_STATE_BALL_NORMAL = 0;

    public static final int SPRITE_STATE_BALL_SMALL = 1;

    public static final int SPRITE_STATE_BALL_BIG = 2;

    public static final int SPRITE_OBJ_BONUSES = 4;

    public static final int SPRITE_STATE_BONUSES_BALLSPEEDPLUS = 0;

    public static final int SPRITE_STATE_BONUSES_BALLSPEEDMINUS = 1;

    public static final int SPRITE_STATE_BONUSES_BALLINCREASE = 2;

    public static final int SPRITE_STATE_BONUSES_BALLDECREASE = 3;

    public static final int SPRITE_STATE_BONUSES_BALLNORMAL = 4;

    public static final int SPRITE_STATE_BONUSES_BIGROCKET = 5;

    public static final int SPRITE_STATE_BONUSES_SMALLROCKET = 6;

    public static final int SPRITE_STATE_BONUSES_LIFE = 7;

    public static final int SPRITE_STATE_BONUSES_MAGNET = 8;

    public static final int SPRITE_STATE_BONUSES_NEXTLEVEL = 9;

    public static final int SPRITE_STATE_BONUSES_ADDBALL = 10;

    public static final int SPRITE_STATE_BONUSES_ADDSCORE = 11;

    public static final int SPRITE_STATE_BONUSES_REFLECTIONLINE = 12;

    public static final int SPRITE_OBJ_MEGABLOCK = 5;

    public static final int SPRITE_STATE_MEGABLOCK_TELEPORTIN1 = 0;

    public static final int SPRITE_STATE_MEGABLOCK_TELEPORTOUT1 = 1;

    public static final int SPRITE_STATE_MEGABLOCK_TELEPORTIN2 = 2;

    public static final int SPRITE_STATE_MEGABLOCK_TELEPORTOUT2 = 3;

    public static final int SPRITE_STATE_MEGABLOCK_BONUSGENERATOR = 4;

    public static final int SPRITE_OBJ_EXPLOSION = 6;

    public static final int SPRITE_STATE_EXPLOSION_EXPLOSION1 = 0;

    public static final int SPRITE_STATE_EXPLOSION_EXPLOSION2 = 1;

    public static final int SPRITE_STATE_EXPLOSION_EXPLOSION3 = 2;

    //-------------temp data
    private static final byte[] level_test_map = new byte[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 5, 2, 2, 2, 2, 5, 0, 0, 6, 0, 0, 0, 0, 0, 5, 4, 4, 4, 4, 5, 0, 0, 0, 0, 0, 0, 0, 0, 5, 4, 4, 4, 4, 5, 0, 0, 0, 0, 3, 3, 3, 0, 5, 4, 4, 4, 4, 5, 0, 3, 2, 2, 0, 0, 3, 0, 5, 2, 2, 2, 2, 5, 0, 1, 0, 0, 9, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 8, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 3, 3, 3,
            0, 4, 4, 4, 4, 4, 4, 0, 4, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    private static final byte[] level_test_bonus_map = new byte[] {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 2, 3, 2, 1, 1, 2, 0, 3, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    // The array contains values for path controllers
    public static final short [] ash_Paths= new short[] {
         // PATH_ROCKETIN
         (short)3,(short)32,(short)-91,(short)59,(short)5,(short)75,(short)34,(short)5,(short)-68,(short)14,(short)5,(short)0,(short)1,(short)5,
         // PATH_ROCKETOUT
         (short)1,(short)32,(short)0,(short)1,(short)10,(short)0,(short)79,(short)10,
         // PATH_BONUSPATH
         (short)3,(short)32,(short)0,(short)0,(short)10,(short)0,(short)76,(short)10,(short)0,(short)246,(short)7,(short)0,(short)353,(short)5,
         // PATH_BONUSPATHFROMGENERATOR
         (short)5,(short)32,(short)0,(short)0,(short)3,(short)13,(short)10,(short)3,(short)21,(short)26,(short)10,(short)24,(short)95,(short)10,(short)24,(short)193,(short)10,(short)24,(short)358,(short)10,
         // PATH_BALLLIN
         (short)4,(short)32,(short)83,(short)87,(short)5,(short)70,(short)25,(short)5,(short)54,(short)-13,(short)5,(short)27,(short)-20,(short)5,(short)0,(short)0,(short)5,
    };

    // PATH offsets
    private static final int PATH_ROCKETIN = 0;
    private static final int PATH_ROCKETOUT = 14;
    private static final int PATH_BONUSPATH = 22;
    private static final int PATH_BONUSPATHFROMGENERATOR = 36;
    private static final int PATH_BALLLIN = 56;


}
