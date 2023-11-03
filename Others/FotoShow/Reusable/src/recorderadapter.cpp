// Copyright (c) 2001, Nokia Mobile Phones. All rights reserved.

#include <eikmenup.h>

#include "recorderadapter.h"

// Identifying string for this audio utility
_LIT(KAudioRecorder, "Recorder");

// An existing sound sample file 
_LIT(KRecorderFile, "C:\\documents\\record.wav");


CRecorderAdapter::CRecorderAdapter() : CActive(CActive::EPriorityStandard)
    {
    CActiveScheduler::Add(this);
    }

CRecorderAdapter* CRecorderAdapter::NewL()
    {
    CRecorderAdapter* self = NewLC();
    CleanupStack::Pop(); // self
    return self;
    }

CRecorderAdapter* CRecorderAdapter::NewLC()
    {
    CRecorderAdapter* self = new (ELeave) CRecorderAdapter();
    CleanupStack::PushL(self);
    self->ConstructL();
    return self;
    }


void CRecorderAdapter::ConstructL()
    {
    iMdaAudioRecorderUtility = CMdaAudioRecorderUtility::NewL(*this);
     // Open an existing sample file for playback or recording
    // causes MMdaObjectStateChangeObserver::MoscoStateChangeEvent to be called

#ifdef _DEBUG
	iMdaAudioRecorderUtility->OpenFileL(_L("C:\\documents\\record.wav"));
#else
	iMdaAudioRecorderUtility->OpenFileL(_L("z:\\nokia\\sounds\\digital\\message.mid"));
#endif

	iPlayInProgress=EFalse;


    }



void CRecorderAdapter::DoCancel()
{
	CActiveScheduler::Stop();
	iStatus=KErrCancel;
}


void CRecorderAdapter::RunL()
{
	CActiveScheduler::Stop();
}


CRecorderAdapter::~CRecorderAdapter()
    {
	    Cancel();
    delete iMdaAudioRecorderUtility;    
    iMdaAudioRecorderUtility = NULL;
    }


void CRecorderAdapter::PlayL()
    {


	// Play through the device speaker
    iMdaAudioRecorderUtility->SetAudioDeviceMode(CMdaAudioRecorderUtility::ETelephonyNonMixed);

    // Set maximum volume for playback
    iMdaAudioRecorderUtility->SetVolume(iMdaAudioRecorderUtility->MaxVolume());

    // Set the playback position to the start of the file
    iMdaAudioRecorderUtility->SetPosition(TTimeIntervalMicroSeconds(0));
    //

    iMdaAudioRecorderUtility->PlayL();

	iPlayInProgress = ETrue;
    }

void CRecorderAdapter::StopL()
    {
    iMdaAudioRecorderUtility->Stop();
    }


void CRecorderAdapter::RecordL()
    {

    // Open an existing sample file for playback or recording
    // causes MMdaObjectStateChangeObserver::MoscoStateChangeEvent to be called
    iMdaAudioRecorderUtility->OpenFileL(KRecorderFile);


    // Record from the device microphone
    iMdaAudioRecorderUtility->SetAudioDeviceMode(CMdaAudioRecorderUtility::ELocal);

    // Set maximum gain for recording
    iMdaAudioRecorderUtility->SetGain(iMdaAudioRecorderUtility->MaxGain());
    
    // Delete current audio sample from beginning of file
    iMdaAudioRecorderUtility->SetPosition(TTimeIntervalMicroSeconds(0));
    iMdaAudioRecorderUtility->CropL();
    //

    iMdaAudioRecorderUtility->RecordL();
    }

// from MMdaObjectStateChangeObserver
void CRecorderAdapter::MoscoStateChangeEvent(CBase* /*aObject*/, TInt aPreviousState, TInt aCurrentState, TInt aErrorCode)
{
	if(iPlayInProgress )
		if(iStatus==KRequestPending)
			if(aPreviousState==2&&aCurrentState==1)
			{
				TRequestStatus* st = &iStatus;
				if(aErrorCode==KErrNone)
					User::RequestComplete(st,KErrNone);
				else
					User::RequestComplete(st,aErrorCode);
			}
}


void CRecorderAdapter::PlaySyncL()
{
	// issue and wait
	SetActive();
	iStatus = KRequestPending;
	PlayL();
	CActiveScheduler::Start();
	iPlayInProgress = EFalse;
}

