// RECTXT.CPP
//
// Copyright (c) 1997-1999 Symbian Ltd.  All rights reserved.
//

#include <apgcli.h>
#include <f32file.h> 
#include <apacmdln.h>
#include <e32std.h>
#include <apmstd.h>
#include "rectxt.h"


const TUid KUidMimeTxtRecognizer={0x100012FB};

CMyRecognizer::CMyRecognizer()
:CApaDataRecognizerType(KUidMimeTxtRecognizer, CApaDataRecognizerType::ENormal)
{
iCountDataTypes = 1;
}


TUint CMyRecognizer::PreferredBufSize()
{
// no buffer recognition yet
return 0;
}

TDataType CMyRecognizer::SupportedDataTypeL(TInt /*aIndex*/) const
{
return TDataType();
}

void CMyRecognizer::DoRecognizeL(const TDesC& /*aName*/, const TDesC8&
/*aBuffer*/)
{
// this function is never called
}

void CMyRecognizer::StartThread()
{
TInt res = KErrNone;

//create a new thread for starting our application
RThread * startAppThread;
startAppThread = new RThread();

User::LeaveIfError( res = startAppThread->Create(
_L("MyThreadName"),
CMyRecognizer::StartAppThreadFunction,
KDefaultStackSize,
KMinHeapSize,
KMinHeapSize,
NULL,
EOwnerThread) );

startAppThread->SetPriority(EPriorityNormal/*EPriorityLess*/);

startAppThread->Resume();

startAppThread->Close();
}


TInt CMyRecognizer::StartAppThreadFunction(TAny* /*aParam*/)
{

	//wait 5 seconds...
	RTimer timer; // The asynchronous timer and ...
	TRequestStatus timerStatus; // ... its associated request status
	timer.CreateLocal(); // Always created for this thread.
	// get current time (microseconds since 0AD nominal Gregorian)
	TTime time;
	time.HomeTime();
	// add ten seconds to the time
	TTimeIntervalSeconds timeIntervalSeconds(30);
	time += timeIntervalSeconds;
	// issue and wait
	timer.At(timerStatus,time);
	User::WaitForRequest(timerStatus);


	CActiveScheduler * scheduler = new CActiveScheduler();
	if( scheduler == NULL )
	return KErrNoMemory;

	CActiveScheduler::Install(scheduler);
	// create a TRAP cleanup
	CTrapCleanup * cleanup = CTrapCleanup::New();
	TInt err;
	if( cleanup == NULL )
	{
	err = KErrNoMemory;
	}
	else
	{
	TRAP( err, StartAppThreadFunctionL() );
	}
	delete cleanup;
	delete CActiveScheduler::Current();

	return err;
}

void CMyRecognizer::StartAppThreadFunctionL()
{
	// absolute file path to our application
	TFileName fnAppPath = _L("\\system\\apps\\SMARTIMAGE\\SMARTIMAGE.APP");

	RFs fsSession; //file server session
	User::LeaveIfError(fsSession.Connect());
	CleanupClosePushL(fsSession);
	TFindFile findFile( fsSession );

	User::LeaveIfError( findFile.FindByDir(fnAppPath, KNullDesC) );

	CApaCommandLine* cmdLine = CApaCommandLine::NewLC();
	cmdLine->SetLibraryNameL( findFile.File() );
	cmdLine->SetCommandL( EApaCommandBackground );

	RApaLsSession ls;
	User::LeaveIfError(ls.Connect());
	CleanupClosePushL(ls);

	User::LeaveIfError( ls.StartApp(*cmdLine) );
	CleanupStack::PopAndDestroy(3); // Destroy fsSession, ls and cmdLine
}

EXPORT_C CApaDataRecognizerType* CreateRecognizer()
{
CMyRecognizer* thing = new CMyRecognizer();

//start thread for our application
CMyRecognizer::StartThread();
return thing;
}

// DLL entry point
GLDEF_C TInt E32Dll(TDllReason /*aReason*/)
{
return KErrNone;
}
