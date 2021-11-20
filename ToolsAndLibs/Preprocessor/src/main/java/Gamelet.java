
public class Gamelet
{
    /**
     * Уведомление от коллекции путей о завершении пути
     * @param _spriteCollection
     * @param _spriteOffset
     */
    public static final void notifyPathCompleted(int _pathCollectionID,int _pathOffset,int _spriteCollection,int _spriteOffset)
    {

    }

    public static final void notifyPathPointPassed(int _pathCollectionID,int _pathOffset,int _pathPointIndex, int _spriteCollection,int _spriteOffset)
    {
         System.out.println("Point passed "+_pathPointIndex);
    }

    public static final void notifySpriteAnimationCompleted(int _spriteCollection,int _spriteOffset)
    {
         //System.out.println("notify sprite animation completed "+_spriteCollection+':'+_spriteOffset);
    }
}
