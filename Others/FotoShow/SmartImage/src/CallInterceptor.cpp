/* Copyright (c) 2003, Nokia Mobile Phones. All rights reserved */

#include "CallInterceptor.h"
#include <eikenv.h>
#include <eikdef.h>
#include <MdaAudioSampleEditor.h>
#include "SmartImageAppUi.h"
_LIT(KTsyName,"Phonetsy.tsy");

CCallInterceptor* CCallInterceptor::NewL(CSmartImageAppUi* aAppUi)
    {
    CCallInterceptor* self = NewLC(aAppUi);
    CleanupStack::Pop(self);
    return self;
    }

    
CCallInterceptor* CCallInterceptor::NewLC(CSmartImageAppUi* aAppUi)
    {
    CCallInterceptor* self = new (ELeave) CCallInterceptor();
    CleanupStack::PushL(self);
    self->ConstructL(aAppUi);
    return self;
    }


CCallInterceptor::CCallInterceptor() : 	iLogProcessor(0), iCallProcessor(0)
{
	iCallStatus = RCall::EStatusUnknown; 
	logfile=0;

	iLogClient=0;
	iRecentLogView=0;

	iLogProcessor=0;
	iCallProcessor=0;
	iCallDuration=0;
	iTimerForForegroundRemind=0;

	iAppUi =0;
}


CCallInterceptor::~CCallInterceptor()
{

	if(iCallDuration)
		iCallDuration->Cancel();
	delete iCallDuration;
	iCallDuration=0;

	if(iTimerForForegroundRemind)
		iTimerForForegroundRemind->Cancel();
	delete iTimerForForegroundRemind;
	iTimerForForegroundRemind=0;

	if(iRecentLogView)
		iRecentLogView->Cancel();
	delete iRecentLogView;
	iRecentLogView=0;
	if(iLogProcessor)
		iLogProcessor->Cancel();
	delete iLogProcessor;
	iLogProcessor=0;
	

	iLine.NotifyStatusChangeCancel();
	if(iCallProcessor)
		iCallProcessor->Cancel();
	delete iCallProcessor;
	iCallProcessor=0;

	iCallStatus = RCall::EStatusUnknown;


	iLine.Close(); //Closes the line. This function must be called for all RLine subsessions which have been opened — to prevent memory leakage.
	if(logfile) logfile->Log(_L8("iLine.Close"));

	iPhone.Close(); // Closes the phone. This function must be called for all RPhone subsessions which have been opened — to prevent memory leakage.
	if(logfile) logfile->Log(_L8("iPhone.Close"));

	iServer.Close(); // As the associated object is a reference counting object, it is destroyed if there are no other open references to it.
	if(logfile) logfile->Log(_L8("iServer.Close"));

	delete logfile;
	logfile=0;

}


void CCallInterceptor::ConstructL(CSmartImageAppUi* aAppUi)
{
	iAppUi = aAppUi;

	logfile=0;
	{
//		logfile = CLogFile::NewL(_L("SMInterceptor.txt"), ETrue);
		if(logfile)
		{
			logfile->SetAutoNewline(ETrue);
			logfile->SetAutoTimeStamp(EFalse);
			logfile->SetAutoFlush(ETrue);
			logfile->PrintCurrentTime();
			logfile->Log(_L8("Loading engin"));
		}
	}


	RTelServer::TPhoneInfo info;
	RPhone::TLineInfo lineInfo;
	User::LeaveIfError(iServer.Connect());
	User::LeaveIfError(iServer.LoadPhoneModule(KTsyName));
	User::LeaveIfError(iServer.GetPhoneInfo(0, info));
	User::LeaveIfError(iPhone.Open(iServer, info.iName));
	
	TInt lineNum;
	iPhone.EnumerateLines(lineNum);
	if (lineNum>0)
	{
		// 0: voice line
		// 1: fax ...
		// 2: modem line
		// 3: fax modem..
		TInt ret = KErrNone;
		// just for information voice line is supported
		User::LeaveIfError(ret = iPhone.GetLineInfo(0, lineInfo));
		if (ret == KErrNone)
			User::LeaveIfError(iLine.Open(iPhone, lineInfo.iName));
	}

	iLogClient = CLogClient::NewL(iAppUi->Environment()->FsSession());
	iRecentLogView = CLogViewRecent::NewL(*iLogClient);

	iLogProcessor = CCallBackRunner::NewL(CActive::EPriorityStandard);
	iCallProcessor = CCallBackRunner::NewL(CActive::EPriorityStandard);

	iInProgress = EStatusUnknown;// сбрасывется в начальное состояние

}



void CCallInterceptor::SetHookL()
{
	if(logfile) logfile->Log(_L8("set hook"));
	if(iCallProcessor)
	{
		if(!(iCallProcessor->IsActive()))
		{
			TCallBack callbackT(CCallInterceptor::TelLineStatusChanged,this);
			iCallProcessor->Start(callbackT);
			iLine.NotifyStatusChangeCancel();
			iLine.NotifyStatusChange(iCallProcessor->iStatus,iCallStatus);
		}

	}
}

void CCallInterceptor::UnSetHook()
{
	iLine.NotifyStatusChangeCancel();
}


TInt CCallInterceptor::TelLineStatusChanged(TAny* aThis)
{
	CCallInterceptor* ptr = (CCallInterceptor*)aThis;
	TInt res = ptr->TelLineStatusChanged();
	return res;
}

TInt CCallInterceptor::TelLineStatusChanged()
{
	iLine.GetStatus(iCallStatus);
	if(logfile) logfile->Log(iCallStatus);
	if(logfile) logfile->PrintCurrentTime();


	switch (
			iCallStatus
			//&&
		//	(iAppUi->Settings().iState==TSmiSettings::EActive)
			)
	{
		case RCall::EStatusUnknown:
			break;
		case RCall::EStatusHangingUp:
		case RCall::EStatusIdle:
			{
				if((iInProgress==EStatusRinging)||(iInProgress==EStatusAnswering))
				{
					if(logfile) logfile->Log(_L8("Duration cancel"));
					if(iCallDuration)
						iCallDuration->Cancel();
					delete iCallDuration;
					iCallDuration = 0;
					if(logfile) logfile->Log(_L8("Duration end"));
					
					if(iTimerForForegroundRemind)
						iTimerForForegroundRemind->Cancel();
					delete iTimerForForegroundRemind;
					iTimerForForegroundRemind=0;
					if(logfile) logfile->Log(_L8("Timer end"));
					
					iAppUi->EndOfCall();
					iInProgress = EStatusHangingUp;// сбрасывется в начальное состояние
					
					iCurrentCall.Close();
				}

				
			}
			break;
		case RCall::EStatusDialling:
			{// в случае dial мы можем переключиться в форегроунд ещё до соединения
				StartForegroundTimerReminder();
				if(logfile) logfile->Log(_L8("Timer start"));
			}
		case RCall::EStatusRinging:
			{
				if((iInProgress==EStatusUnknown)||(iInProgress==EStatusHangingUp))
				{
					iInProgress = EStatusRinging;
					ReadCallInformationL();
				}
				
			}
			break;
		case RCall::EStatusAnswering:
		case RCall::EStatusConnecting:
		case RCall::EStatusConnected:
				if((iInProgress==EStatusRinging))
				{
					iInProgress = EStatusAnswering;
					StartForegroundTimerReminder();
					if(logfile) logfile->Log(_L8("Timer start"));
				}
			break;
		default:
		break;
	}

	SetHookL();
	return EFalse;		
}



void CCallInterceptor::ReadCallInformationL()
{
	if(logfile) logfile->Log(_L8("ReadCallInformationL"));

	// это место для запуска проверки времени в будущем надо поменять 
	// на EStatusAnswering и т.д.
	if(iCallDuration)
		iCallDuration->Cancel();
	delete iCallDuration;

	TName callName;
	RLine::TLineInfo lineInfo;
	TInt ret = KErrNone;

	iLine.GetInfo(lineInfo);


	// открываем линию
	// iCurrentCall будет использоваться для duration
	if(iCallStatus == RCall::EStatusRinging)
	{
		if(logfile) logfile->Log(_L8("Open"));
		if(logfile) logfile->Log(lineInfo.iNameOfCallForAnswering);
		callName.Copy(lineInfo.iNameOfCallForAnswering);
		ret = iCurrentCall.OpenExistingCall(iLine,callName);
		if(logfile) logfile->ShowErrorCode(ret);
	}

	if(iCallStatus == RCall::EStatusDialling)
	{
		if(logfile) logfile->Log(_L8("Open"));
		if(logfile) logfile->Log(lineInfo.iNameOfLastCallAdded);
		callName.Copy(lineInfo.iNameOfLastCallAdded);
		ret = iCurrentCall.OpenExistingCall(iLine,callName);
		if(logfile) logfile->ShowErrorCode(ret);
	}

	if(logfile) logfile->ShowErrorCode(ret);

	if(ret==KErrNone) 
	{	
		if(iCallDuration==0)
			iCallDuration = CPeriodic::NewL(CActive::EPriorityStandard);
		if(!iCallDuration->IsActive())
			iCallDuration->Start(1000000,1000000,TCallBack(CCallInterceptor::DurationEvent,this ));
	}


	// запуск проверки добавления события в логер
	if(iLogProcessor==0)
		return;// вообще то это клинический случай 
	iRecentLogView->Cancel();
	if(!(iLogProcessor->IsActive()))
		{
			TCallBack callbackR(CCallInterceptor::LogRefreshed,this);
			iLogProcessor->Start(callbackR);
			if(logfile) logfile->Log(_L8("logproc started"));
		}

	TBool init = iRecentLogView->SetRecentListL(KLogNullRecentList,iLogProcessor->iStatus);
	if(init)
		if(logfile) logfile->Log(_L8("SetRectList passed"));
	else
		if(logfile) logfile->Log(_L8("SetRectList error"));

}


void CCallInterceptor::StartForegroundTimerReminder()
{
	if(iTimerForForegroundRemind==0)
		iTimerForForegroundRemind = CPeriodic::NewL(CActive::EPriorityStandard);
	if(!iTimerForForegroundRemind->IsActive())
		iTimerForForegroundRemind->Start(200000,1500000,TCallBack(CCallInterceptor::SendToForeground,this ));
	iStopTimer = EFalse;
}

TInt CCallInterceptor::SendToForeground()
{
	if(logfile) logfile->Log(_L8("Timer tick"));
	if(logfile) logfile->PrintCurrentTime();

	if(iAppUi)
		iAppUi->SendToForeground();

	if(iStopTimer)
	{
		if(iTimerForForegroundRemind!=0)
			iTimerForForegroundRemind->Cancel();
		delete iTimerForForegroundRemind;
		iTimerForForegroundRemind = 0;
	}


	return 1;
}


TInt CCallInterceptor::SendToForeground(TAny* aThis)
{
	CCallInterceptor* ptr = (CCallInterceptor*)aThis;
	return ptr->SendToForeground();
}

TInt CCallInterceptor::LogRefreshed()
{

	if(logfile) logfile->Log(_L8("LogEvent start"));
	if(logfile) logfile->PrintCurrentTime();

	const CLogEvent& event = iRecentLogView->Event();
	if(logfile) logfile->Log(_L("count %d"),iRecentLogView->CountL());
	{
		const CLogEvent& event1 = iRecentLogView->Event();
		if(logfile) logfile->Log(_L("ConId 0x%X"),event1.Contact());
		if(logfile) logfile->Log(event1.Number());
		if(logfile) logfile->Log(_L("LogId 0x%X "),event1.Id());
		if(logfile) logfile->Log(_L("LinkId 0x%X "),event1.Link());
		if(logfile) logfile->Log(_L("EvType 0x%X"),event1.EventType());//
		
	}
	if(iAppUi)
	{

		TRAPD(err,iAppUi->ShowL(event));
		if(err!=KErrNone)
		{
			if(logfile) logfile->Log(_L8("NewCallEventL err:"));
			if(logfile) logfile->ShowErrorCode(err);
		}
	}


	return EFalse;
}



TInt CCallInterceptor::LogRefreshed(TAny* aThis)
{
	CCallInterceptor* ptr = (CCallInterceptor*)aThis;
	ptr->LogRefreshed();
	return EFalse;
}

TInt CCallInterceptor::DurationEvent(TAny* aThis)
{
	CCallInterceptor* ptr = (CCallInterceptor*)aThis;
	TInt res = ptr->DurationEvent();
	return res;
}

TInt CCallInterceptor::DurationEvent()
{
	
	if(logfile) logfile->Log(_L8("DurationEvent"));
	{
		TTimeIntervalSeconds sec=0;
		TInt ret = iCurrentCall.GetCallDuration(sec);
//		iCurrentCall.iSpeakerControl = RCall::EMonitorSpeakerVolumeLow;
		if(ret==KErrNone)
		{
			iAppUi->UpdateDuration(sec);
		}

	}

	return ETrue;
}



CCallBackRunner* CCallBackRunner::New(TInt aPriority)
//
// Create a new idle object.
//
	{

	CCallBackRunner *pI=new CCallBackRunner(aPriority);
	if (pI!=NULL)
		CActiveScheduler::Add(pI);
	return(pI);
	}

CCallBackRunner* CCallBackRunner::NewL(TInt aPriority)
//
// Create a new idle object.
//
	{

	CCallBackRunner *pI=new(ELeave) CCallBackRunner(aPriority);
	CActiveScheduler::Add(pI);
	return(pI);
	}

CCallBackRunner::CCallBackRunner(TInt aPriority)
//
// Constructor.
//
	: CActive(aPriority)
	{}

CCallBackRunner::~CCallBackRunner()
//
// Destructor.
//
	{

	Cancel();
	}

void CCallBackRunner::Start(TCallBack aCallBack)
//
// Start the idler.
//
	{

	iCallBack=aCallBack;
	if(IsActive())
		Cancel();
	SetActive();
	}

void CCallBackRunner::RunL()
//
// Called when nothing of a higher priority can be scheduled.
//
	{

	if (iCallBack.CallBack())
		Start(iCallBack);
	}

void CCallBackRunner::DoCancel()
//
// Cancel
//
	{
	}


void CCallInterceptor::HangUpL()
{
	if( (iInProgress!=EStatusRinging)&&
		(iInProgress!=EStatusAnswering)
	  )
		return;

	iCurrentCall.HangUp();
}

