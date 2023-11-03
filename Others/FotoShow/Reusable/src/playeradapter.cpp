
/* ========================================================================
  * (C) 2002 EDC competence centre in St.Peterburg 
  * ========================================================================
  * PROJECT: Raptor: Location Library User Interaction Modules
  * ======================================================================== */
/** 
  * \author OGO Oleg Golosovsky     
  * \date	 
  * \file playeradapter.cpp	 
  * \brief	 CPlayerAdapter interface implementation
  *
  * File and Version Information (update automatically):  
  * $Revision: 1.3 $
  * $Header: D:/CVSREP/SPB/loclib/Notifier/Notifier/Src/playeradapter.cpp,v 1.3 2003/05/13 09:47:37 OGO Exp $
  * $Id: playeradapter.cpp,v 1.3 2003/05/13 09:47:37 OGO Exp $
  * tag: $Name:  $
  * $Log: playeradapter.cpp,v $
  * Revision 1.3  2003/05/13 09:47:37  OGO
  * Code comments changed and added
  *
  * Revision 1.2  2003/03/11 11:59:13  OGO
  * adding comments
  *
  *
  */


#include "playeradapter.h"


CPlayerAdapter::CPlayerAdapter() : 
    iState(ENotReady),
	iIsFileLoaded(0)
    {
    }

CPlayerAdapter* CPlayerAdapter::NewL(const TDesC& aFileName)
    {
    CPlayerAdapter* self = NewLC(aFileName);
    CleanupStack::Pop();  // self
    return self;
    }

CPlayerAdapter* CPlayerAdapter::NewLC(const TDesC& aFileName)
    {
    CPlayerAdapter* self = new (ELeave) CPlayerAdapter();
    CleanupStack::PushL(self);
    self->ConstructL(aFileName);
    return self;
    }


void CPlayerAdapter::ConstructL(const TDesC& aFileName)
    {
    // Create an audio player utility instance for playing sample data from a file
    // causes MMdaAudioPlayerCallback::MapcInitComplete to be called
    iMdaAudioPlayerUtility = CMdaAudioPlayerUtility::NewFilePlayerL(aFileName, *this);
	iIsFileLoaded = KRequestPending;
    }

CPlayerAdapter::~CPlayerAdapter()
    {
    delete iMdaAudioPlayerUtility;    
    iMdaAudioPlayerUtility = NULL;
    }



// Note that this implementation of the virtual function does not leave.
void CPlayerAdapter::PlayL(TInt aMaxVolumePercent)
    {
	if(iState!=EReadyToPlay)
	{
		iIsFileLoaded = aMaxVolumePercent;
		return;
	}
		
	TInt MaxVolume = iMdaAudioPlayerUtility->MaxVolume();
	iMdaAudioPlayerUtility->SetVolume((TInt)((TReal)MaxVolume/100.0)*aMaxVolumePercent);

    iMdaAudioPlayerUtility->Play();
    iState = EPlaying;
    }


// CMdaAudioPlayerUtility is not able to record
void CPlayerAdapter::RecordL() 
    {
    }


// Note that this implementation of the virtual function does not leave.
void CPlayerAdapter::StopL()
    {
    iMdaAudioPlayerUtility->Stop();
    iState = EReadyToPlay;
    }



// from MMdaAudioPlayerCallback
void CPlayerAdapter::MapcInitComplete(TInt aError, const TTimeIntervalMicroSeconds& /*aDuration*/)
{
    iState = aError ? ENotReady : EReadyToPlay;
	if(iIsFileLoaded!= 0)
	{
		PlayL(iIsFileLoaded);
		iIsFileLoaded = 0;
	}
}


void CPlayerAdapter::MapcPlayComplete(TInt aError)
    {
    iState = aError ? ENotReady : EReadyToPlay;
    }


TBool CPlayerAdapter::IsPlaying()
{
	if(iState!=EReadyToPlay)
			return ETrue;

	return EFalse;
}
