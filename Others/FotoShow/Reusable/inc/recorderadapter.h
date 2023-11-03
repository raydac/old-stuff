// Copyright (c) 2001, Nokia Mobile Phones. All rights reserved.

#ifndef __CRECORDERADAPTER__
#define __CRECORDERADAPTER__

#include <e32std.h>
#include <MdaAudioSampleEditor.h>
#include <mda\\client\\utility.h> // for MMdaObjectStateChangeObserver


/*! 
  @class CRecorderAdapter
  
  @discussion An instance of class CRecorderAdapter is an adapter object for the CMdaAudioRecorderUtility class.
  */
class CRecorderAdapter : public CActive, MMdaObjectStateChangeObserver
    {
public:
/*!
  @function NewL
  
  @discussion Create a CRecorderAdapter object using two phase construction,
  and return a pointer to the created object
  @param aAppUi the User Interface
  @result pointer to new object
  */
    static CRecorderAdapter* NewL();
    
/*!
  @function NewLC
  
  @discussion Create a CRecorderAdapter object using two phase construction,
  and return a pointer to the created object
  @param aAppUi the User Interface
  @result pointer to new object
  */
    static CRecorderAdapter* NewLC();

/*!
  @function ~CRecorderAdapter
  
  @discussion Destroy the object and release all memory objects
  */
    ~CRecorderAdapter();
/*!
  @function DoCancel
  
  @discussion Cancel any outstanding requests
  */
    void DoCancel();

/*!
  @function RunL
  
  @discussion Respond to an event
  */
    void RunL();
public: 
/*!
  @function PlayL
  
  @discussion Begin playback of the audio sample. 
  */
    void PlayL();
    void PlaySyncL();

/*!
  @function StopL
  
  @discussion Stop playback or recording of the audio sample.
              Note that this implementation of the virtual function does not leave. 
  */
    void StopL();

/*!
  @function RecordL
  
  @discussion Record using the audio utility.
  */
    void RecordL();


public: // from MMdaObjectStateChangeObserver
/*!
  @function MoscoStateChangeEvent
  
  @discussion Handle the change of state of an audio recorder utility.
  @param aObject The audio sample object that has changed state
  @param aPreviousState The state before the change
  @aCurrentState The state after the change
  @aErrorCode if not KErrNone, that error that caused the state change
  */
    void MoscoStateChangeEvent(CBase* aObject, TInt aPreviousState, TInt aCurrentState, TInt aErrorCode);
    
private:

/*!
  @function CRecorderAdapter
  
  @discussion Perform the first phase of two phase construction 
  @param aAppUi the Ui to use
  */
    CRecorderAdapter();
      
/*!
  @function ConstructL
  
  @discussion Perform the second phase construction of a CRecorderAdapter object
  */
    void ConstructL();

private:
/** The audio recorder utility object. */
    CMdaAudioRecorderUtility* iMdaAudioRecorderUtility;

	TBool iPlayInProgress;

    };

#endif // __CRECORDERADAPTER__
