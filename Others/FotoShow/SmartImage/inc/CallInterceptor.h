/* Copyright (c) 2003, Nokia Mobile Phones. All rights reserved */

#ifndef __CALLINTERCEPTOR_H__
#define __CALLINTERCEPTOR_H__

#include <e32base.h>
#include <etel.h>
#include <logview.h>
#include "logfile.h"

class CSmartImageAppUi;




class CCallBackRunner : public CActive
	{
public:
	static CCallBackRunner* New(TInt aPriority);
	static CCallBackRunner* NewL(TInt aPriority);
	~CCallBackRunner();
	void Start(TCallBack aCallBack);
protected:
	CCallBackRunner(TInt aPriority);
	void RunL();
	void DoCancel();
protected:
	TCallBack iCallBack;
	};


class CCallInterceptor : public CBase
{
public:
	enum TRingStatus
		{
		EStatusUnknown,
		EStatusRinging,
		EStatusAnswering,
		EStatusHangingUp
		};


public:

/*!
  @function NewL
   
  @discussion Create a CCallInterceptor object
  @result a pointer to the created instance of CCallInterceptor
  */
    static CCallInterceptor* NewL(CSmartImageAppUi* aAppUi);

/*!
  @function NewLC
   
  @discussion Create a CCallInterceptor object
  @result a pointer to the created instance of CCallInterceptor
  */
    static CCallInterceptor* NewLC(CSmartImageAppUi* aAppUi);

/*!
  @function ~CCallInterceptor
  
  @discussion Destroy the object and release all memory objects
  */
    ~CCallInterceptor();


public:
	inline TRingStatus CallStatus()
		{ return iInProgress;}


protected:

	static TInt LogRefreshed(TAny* aThis);
	TInt LogRefreshed();

	static TInt TelLineStatusChanged(TAny* aThis);
	TInt TelLineStatusChanged();

	static TInt DurationEvent(TAny* aThis);
	TInt DurationEvent();

	static TInt SendToForeground(TAny* aThis);
	TInt SendToForeground();


	void StartForegroundTimerReminder();
	void ReadCallInformationL();


private:

/*!
  @function CCallInterceptor

  @discussion Constructs this object
  */
    CCallInterceptor();

/*!
  @function ConstructL

  @discussion Performs second phase construction of this object
  */
    void ConstructL(CSmartImageAppUi* aAppUi);

private:
    // Member variables
	CCallBackRunner* iLogProcessor;
	CCallBackRunner* iCallProcessor;
	CPeriodic*		iCallDuration;
	CPeriodic*		iTimerForForegroundRemind;



	TRingStatus iInProgress; // отсекает повторные вызовы ERinging
	RCall::TStatus iCallStatus; // текущий статус звонка

private:
	// telephone
	RTelServer iServer; 
	RPhone iPhone; 
	RLine iLine; 
	RCall iCurrentCall; 
	// logger
	CLogClient* iLogClient;
	CLogViewRecent* iRecentLogView;

	TBool iStopTimer;// set to Etrue if you want to interrupt foreground timer reminder


private:
	CSmartImageAppUi* iAppUi;

public:
	void HangUpL();
	void UnSetHook();
	void SetHookL();
	CLogFile* logfile;
};

#endif // __CALLINTERCEPTOR_H__

