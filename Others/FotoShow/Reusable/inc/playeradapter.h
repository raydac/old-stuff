
/* ========================================================================
  * (C) 2002 EDC competence centre in St.Peterburg 
  * ========================================================================
  * PROJECT: Raptor: Location Library User Interaction Modules
  * ======================================================================== */
/** 
  * \author OGO Oleg Golosovsky     
  * \date	 
  * \file	 playeradapter.h
  * \brief	An instance of class CPlayerAdapter is an adapter object for the CMdaAudioPlayerUtility class. 
  *
  * File and Version Information (update automatically):  
  * $Revision: 1.3 $
  * $Header: D:/CVSREP/SPB/loclib/Notifier/Notifier/Inc/playeradapter.h,v 1.3 2003/05/13 09:47:37 OGO Exp $
  * $Id: playeradapter.h,v 1.3 2003/05/13 09:47:37 OGO Exp $
  * tag: $Name:  $
  * $Log: playeradapter.h,v $
  * Revision 1.3  2003/05/13 09:47:37  OGO
  * Code comments changed and added
  *
  * Revision 1.2  2003/03/11 11:59:13  OGO
  * adding comments
  *
  *
  */

#ifndef __CPLAYERADAPTER__
#define __CPLAYERADAPTER__

#include <MdaAudioSamplePlayer.h>


/*! 
  @class CPlayerAdapter
  
  @discussion An instance of class CPlayerAdapter is an adapter object for 
              the CMdaAudioPlayerUtility class.
  */
class CPlayerAdapter : public CBase, public MMdaAudioPlayerCallback
    {
public:
/*!
  @function NewL
  
  @discussion Create a CPlayerAdapter object using two phase construction,
  and return a pointer to the created object
  @param aFileName the audio file
  @param aAppUi the User Interface
  @result pointer to new object
  */
    static CPlayerAdapter* NewL(const TDesC& aFileName);
    
/*!
  @function NewLC
  
  @discussion Create a CPlayerAdapter object using two phase construction,
  and return a pointer to the created object
  @param aFileName the audio file
  @param aAppUi the User Interface
  @result pointer to new object
  */
    static CPlayerAdapter* NewLC(const TDesC& aFileName);

/*!
  @function ~CPlayerAdapter
  
  @discussion Destroy the object and release all memory objects
  */
    ~CPlayerAdapter();

public: // from MAudioAdapter
/*!
  @function PlayL
  
  @discussion Begin playback of the audio sample. 
  */
    void PlayL(TInt aMaxVolumePercent);

/*!
  @function RecordL
  
  @discussion Do nothing. Recording is not supported.
  */
    void RecordL();

/*!
  @function StopL
  
  @discussion Stop playback of the audio sample.
              Note that this implementation of the virtual function does not leave. 
  */
    void StopL();

    
public: // from MMdaAudioPlayerCallback
/*!
  @function MapcInitComplete
  
  @discussion Handle the event when initialisation of the audio player utility is complete.
  @param aError The status of the audio sample after initialisation
  @param aDuration The duration of the sample
  */
    void MapcInitComplete(TInt aError, const TTimeIntervalMicroSeconds& aDuration);

/*!
  @function MapcInitComplete
  
  @discussion Handle the event when when the audio player utility completes asynchronous playing.
  @param aError The status of playback
  */
    void MapcPlayComplete(TInt aError);
	TBool IsPlaying();

private:

/*!
  @function CPlayerAdapter
  
  @discussion Perform the first phase of two phase construction 
  @param aAppUi the Ui to use
  */
    CPlayerAdapter();

    
    
/*!
  @function ConstructL
  
  @discussion Perform the second phase construction of a CPlayerAdapter object
  */
    void ConstructL(const TDesC& aFileName);

private:
/** The current state of the audio player utility. */
    enum TState
        {
        ENotReady,
        EReadyToPlay,
        EPlaying
        };

private:
    TState iState;
	TInt iIsFileLoaded;

/** The audio player utility object. */
    CMdaAudioPlayerUtility* iMdaAudioPlayerUtility;

    };

#endif // __CPLAYERADAPTER__



