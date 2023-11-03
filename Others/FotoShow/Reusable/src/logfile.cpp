
/* ========================================================================
  * Author:  Oleg Golosovskiy, http://www.sw4u.org mailto:mail@sw4u.org
  * ======================================================================== */
/** 
  * \author Oleg Golosovsky     
  * \brief	Loger 
  */

#include <hal.h>
#include <charconv.h>
#include "logfile.h"
#include "logfile.pan"
#include <MIUT_ERR.H>

_LIT8(KCrLf8, "\r\n");
_LIT(KCrLf, "\r\n");

static const TInt KAsciiStart = 0x20;
static const TInt KAsciiEnd = 0x7f;
static const TInt KHexCharLeft = '<';
static const TInt KHexCharRight = '>';

static const TInt KNumberOfDecimalPlaces = 3;

CLogFile* CLogFile::NewL(const TDesC& aFileName, TBool aInitialiseLog)
	{
    CLogFile* self = NewLC(aFileName, aInitialiseLog);
    CleanupStack::Pop();
    return(self);
	}


CLogFile* CLogFile::NewLC(const TDesC& aFileName, TBool aInitialiseLog)
	{
    CLogFile* self = new (ELeave) CLogFile();
    CleanupStack::PushL(self);
    self->ConstructL(aFileName, aInitialiseLog);
    return(self);
	}


CLogFile::CLogFile() : iCheckNestDepth(0)
	{
    // No implementation required
	}


CLogFile::~CLogFile()
	{
    iLogFile.Flush();
    iLogFile.Close();
    iSession.Close();
	}


void CLogFile::ConstructL(const TDesC& aFileName, TBool aInitialiseLog)
	{
    TInt period;
	User::LeaveIfError(HAL::Get(HALData::ESystemTickPeriod, period));

    iLogMillisecsPerTick = period / 1000;

    if (iLogMillisecsPerTick == 0)
    	{
        iLogMillisecsPerTick = 1;
    	}


    iSession.Connect();

    if (aInitialiseLog)
    	{
        User::LeaveIfError(iLogFile.Replace(iSession, aFileName, EFileShareExclusive));
    	}
    else
    	{
        TInt err = iLogFile.Open(iSession, aFileName, EFileShareExclusive | EFileWrite);

        switch (err)
        	{
            case KErrNone: // Opened ok, so seek to end of file
                {
                TInt position = 0;
                User::LeaveIfError(iLogFile.Seek(ESeekEnd, position));
                }
                break;

            case KErrNotFound: // File doesn't exist, so create it
                User::LeaveIfError(iLogFile.Create(iSession, aFileName, EFileShareExclusive | EFileWrite));
                break;

            default: // Unexepected error
                User::Leave(err);
                break;
        	}
    	}
	}


void CLogFile::LogTime()
	{
    StartWrite();
    LogTimeInternal();
    EndWrite();
	}


void CLogFile::Log(const TDesC8& aText)
	{
    StartWrite();
    LogTextInternal(aText);
    EndWrite();
	}


void CLogFile::Log(const TDesC& aText)
	{
    StartWrite();

    // Create character converter
    CCnvCharacterSetConverter* characterConverter = CCnvCharacterSetConverter::NewLC();
    CCnvCharacterSetConverter::TAvailability converterAvailability;
    converterAvailability = characterConverter->PrepareToConvertToOrFromL(KCharacterSetIdentifierAscii, iSession);

    for (TInt i = 0; i < aText.Length(); i++)
    	{
        if (aText.Mid(i).Find(KCrLf) == 0)
        	{
            LogNewline();
            i++;
        	}
        else if (converterAvailability == CCnvCharacterSetConverter::EAvailable)
        	{
            // Convert character from unicode
            TBuf<1> unicodeBuffer;
            TBuf8<10> asciiBuffer;

            unicodeBuffer.Append(aText[i]);
            TInt status = characterConverter->ConvertFromUnicode(asciiBuffer, unicodeBuffer);

            if (status >= 0)
                {
                LogTextInternal(asciiBuffer);
                }
            }
        else // character converter not available
            {
                TBuf8<1> asciiBuffer;
                asciiBuffer.Append(static_cast<TUint8>(aText[i]));
                LogTextInternal(asciiBuffer);
            }
        }

    CleanupStack::PopAndDestroy(characterConverter);
    EndWrite();
	}


void CLogFile::Log(TUint8 aByte)
	{
    StartWrite();
    LogByteInternal(aByte);
    EndWrite();        
	}


void CLogFile::Log(TUint aNumber)
	{
    StartWrite();
    LogIntInternal(aNumber);
    EndWrite();        
	}


void CLogFile::LogBytes(const TDesC8& aBuffer)
	{
    StartWrite();

    for (TInt i = 0; i < aBuffer.Length(); i++)
    	{
        LogByteInternal(aBuffer[i]);
    	}

    EndWrite();
	}

void CLogFile::Log(RCall::TStatus aStatus)
{
	TBuf8<KMaxName> incomingEvent;
	incomingEvent = _L8("Status: ");

	switch(aStatus)
	{
		case RCall::EStatusUnknown:
			{
			incomingEvent.Append(_L8("EStatusUnknown"));
			break;
			}
		case RCall::EStatusIdle:
			{
			incomingEvent.Append(_L8("EStatusIdle"));
			break;
			}
		case RCall::EStatusDialling:
			{
			incomingEvent.Append(_L8("EStatusDialling"));
			break;
			}
		case RCall::EStatusRinging:
			{
			incomingEvent.Append(_L8("EStatusRinging"));
			break;
			}
		case RCall::EStatusAnswering:
			{
			incomingEvent.Append(_L8("EStatusAnswering"));
			break;
			}
		case RCall::EStatusConnecting:
			{
			incomingEvent.Append(_L8("EStatusConnecting"));
			break;
			}
		case RCall::EStatusConnected:
			{
			incomingEvent.Append(_L8("EStatusConnected"));
			break;
			}
		case RCall::EStatusHangingUp:
			{
			incomingEvent.Append(_L8("EStatusHangingUp"));
			break;
			}

		}

	Log(incomingEvent);

}



void CLogFile::LogTimeInternal()
	{
    TBuf8<50> text;
    TInt timeInMillisecs = User::TickCount() * iLogMillisecsPerTick;
    TInt secs = timeInMillisecs / 1000;
    TInt millisecs = timeInMillisecs % 1000;
    text.Num(secs);
    text.Append('.');
	Write(text);
    text.Num(millisecs);

    while (text.Length() < KNumberOfDecimalPlaces)
    	{
        text.Insert(0, _L8("0"));
    	}

    text.Append('-');
	Write(text);
	}


void CLogFile::LogTextInternal(const TDesC8& aText)
	{
	TPtrC8 tail(aText.Ptr(), aText.Length());

    TInt newLinePosition = tail.Find(KCrLf8);
	while (newLinePosition != KErrNotFound)
		{
		if (newLinePosition > 0)
			{
			Write(tail.Left(newLinePosition));
			tail.Set(aText.Ptr() + newLinePosition, tail.Length() - newLinePosition);
			}
        LogNewline();
		tail.Set(aText.Ptr() + KCrLf8.iTypeLength, tail.Length() - KCrLf8.iTypeLength);

		newLinePosition = tail.Find(KCrLf8);
		}

	//	No more newlines left so print remainder
	Write(tail);

	}


void CLogFile::LogByteInternal(TUint8 aByte)
	{
    if ((aByte >= KAsciiStart) && (aByte < KAsciiEnd))
    	{
        // Display as ASCII char
        TBuf8<1> str;
        str.Append(aByte);
		Write(str);
    	}
    else
    	{
        // Display as hex number
        TBuf8<4> str;
        str.Append(KHexCharLeft);
        str.AppendNum((TUint)aByte, EHex);
        str.Append(KHexCharRight);
		Write(str);
    	}
	}


void CLogFile::LogIntInternal(TUint aNumber)
	{
    // Display as ASCII char
    TBuf8<20> str;
    str.Append(KHexCharLeft);
    str.AppendNum(aNumber, EHex);
    str.Append(KHexCharRight);
	Write(str);
	}


void CLogFile::LogNewline()
	{
    Write(KCrLf8);

    if (iAutoTimestamp)
    	{
        LogTimeInternal();
    	}
	}


void CLogFile::StartWrite()
	{
    ASSERT(iCheckNestDepth == 0);
    iCheckNestDepth++;

    if (iAutoNewline)
    	{
        LogNewline();
    	}
	}


void CLogFile::EndWrite()
{
    if (iAutoFlush)
    	{
        iLogFile.Flush();
    	}

    iCheckNestDepth--;
    ASSERT(iCheckNestDepth == 0);
}

void CLogFile::Write(const TDesC8& aText)
    {

    if (iLogFile.Write(aText) != KErrNone)
        {
        //  As the framework may be trapping User::Panic we need to
        //  produce the panic at a lower level.
        RThread().Panic(KLogFilePanic, TLogFileWriteFailed);
        }
    }

void CLogFile::SetAutoFlush(TBool aOn)
	{
    iAutoFlush = aOn;
	}


void CLogFile::SetAutoTimeStamp(TBool aOn)
	{
    iAutoTimestamp = aOn;
	}


void CLogFile::SetAutoNewline(TBool aOn)
	{
    iAutoNewline = aOn;
	}


void CLogFile::StaticLogL(const TDesC& aFileName, const TDesC8& aText)
	{
    CLogFile* logFile = NewLC(aFileName, EFalse);
    logFile->Log(aText);
    CleanupStack::Pop(logFile);
    delete logFile;
	}


void CLogFile::StaticLogL(const TDesC& aFileName, const TDesC& aText)
	{
    CLogFile* logFile = NewLC(aFileName, EFalse);
    logFile->Log(aText);
    CleanupStack::Pop(logFile);
    delete logFile;
	}



void CLogFile::Log( TRefByValue<const TDesC> aFmt,...)
{
	TBuf<256> buf;
	VA_LIST list;
	VA_START(list, aFmt);
	buf.FormatList(aFmt, list);
	Log(buf);
}
void CLogFile::PrintCurrentTime()
{
	TTime time;
	TBuf<100> dateString;
	time.HomeTime();
	_LIT(KDateString,"%-B%:0%J%:1%T%:2%S%:3%");
	time.FormatL(dateString,KDateString);
	Log(dateString);

}


void CLogFile::ShowErrorCode(TInt aErrCode)
{
	TBuf<256> string;
	string = _L("Error code: ");
	GetErrorCode(aErrCode,string);
	Log(string);
}


void CLogFile::GetErrorCode(TInt aErrCode,TDes& aString)
{

// Output the eror code as a string
	switch(aErrCode)
		{
		case KErrNone:
			aString.Append(_L("KErrNone"));
			break;
		case KErrNotFound:
			aString.Append(_L("KErrNotFound"));
			break;
		case KErrGeneral:
			aString.Append(_L("KErrGeneral"));
			break;
		case KErrCancel:
			aString.Append(_L("KErrCancel"));
			break;
		case KErrNoMemory:
			aString.Append(_L("KErrNoMemory"));
			break;
		case KErrNotSupported:
			aString.Append(_L("KErrNotSupported"));
			break;
		case KErrArgument:
			aString.Append(_L("KErrArgument"));
			break;
		case KErrTotalLossOfPrecision:
			aString.Append(_L("KErrTotalLossOfPrecision"));
			break;
		case KErrBadHandle:
			aString.Append(_L("KErrBadHandle"));
			break;
		case KErrOverflow:
			aString.Append(_L("KErrOverflow"));
			break;
		case KErrUnderflow:
			aString.Append(_L("KErrUnderflow"));
			break;
		case KErrAlreadyExists:
			aString.Append(_L("KErrAlreadyExists"));
			break;
		case KErrPathNotFound:
			aString.Append(_L("KErrPathNotFound"));
			break;
		case KErrDied:
			aString.Append(_L("KErrDied"));
			break;
		case KErrInUse:
			aString.Append(_L("KErrInUse"));
			break;
		case KErrServerTerminated:
			aString.Append(_L("KErrServerTerminated"));
			break;
		case KErrServerBusy:
			aString.Append(_L("KErrServerBusy"));
			break;
		case KErrCompletion:
			aString.Append(_L("KErrCompletion"));
			break;
		case KErrNotReady:
			aString.Append(_L("KErrNotReady"));
			break;
		case KErrUnknown:
			aString.Append(_L("KErrUnknown"));
			break;
		case KErrCorrupt:
			aString.Append(_L("KErrCorrupt"));
			break;
		case KErrAccessDenied:
			aString.Append(_L("KErrAccessDenied"));
			break;
		case KErrLocked:
			aString.Append(_L("KErrLocked"));
			break;
		case KErrWrite:
			aString.Append(_L("KErrWrite"));
			break;
		case KErrDisMounted:
			aString.Append(_L("KErrDisMounted"));
			break;
		case KErrEof:
			aString.Append(_L("KErrEof"));
			break;
		case KErrDiskFull:
			aString.Append(_L("KErrDiskFull"));
			break;
		case KErrBadDriver:
			aString.Append(_L("KErrBadDriver"));
			break;
		case KErrBadName:
			aString.Append(_L("KErrBadName"));
			break;
		case KErrCommsLineFail:
			aString.Append(_L("KErrCommsLineFail"));
			break;
		case KErrCommsFrame:
			aString.Append(_L("KErrCommsFrame"));
			break;
		case KErrCommsOverrun:
			aString.Append(_L("KErrCommsOverrun"));
			break;
		case KErrCommsParity:
			aString.Append(_L("KErrCommsParity"));
			break;
		case KErrTimedOut:
			aString.Append(_L("KErrTimedOut"));
			break;
		case KErrCouldNotConnect:
			aString.Append(_L("KErrCouldNotConnect"));
			break;
		case KErrCouldNotDisconnect:
			aString.Append(_L("KErrCouldNotDisconnect"));
			break;
		case KErrDisconnected:
			aString.Append(_L("KErrDisconnected"));
			break;
		case KErrBadLibraryEntryPoint:
			aString.Append(_L("KErrBadLibraryEntryPoint"));
			break;
		case KErrBadDescriptor:
			aString.Append(_L("KErrBadDescriptor"));
			break;
		case KErrAbort:
			aString.Append(_L("KErrAbort"));
			break;
		case KErrTooBig:
			aString.Append(_L("KErrTooBig"));
			break;
		case KErrDivideByZero:
			aString.Append(_L("KErrDivideByZero"));
			break;
		case KErrBadPower:
			aString.Append(_L("KErrBadPower"));
			break;
		case KErrDirFull:
			aString.Append(_L("KErrDirFull"));
			break;
//		case KMiutErrorBase:
//			aString.Append(_L("KMiutErrorBase"));
//			break;			
//		case KPop3ErrorBase:
//			aString.Append(_L("KPop3ErrorBase"));
//			break;
		case KPop3CannotConnect:
			aString.Append(_L("KPop3CannotConnect"));
			break;
		case KPop3InvalidUser:
			aString.Append(_L("KPop3InvalidUser"));
			break;
		case KPop3InvalidLogin:
			aString.Append(_L("KPop3InvalidLogin"));
			break;
		case KPop3CannotCreateApopLogonString:
			aString.Append(_L("KPop3CannotCreateApopLogonString"));
			break;
		case KPop3ProblemWithRemotePopServer:
			aString.Append(_L("KPop3ProblemWithRemotePopServer"));
			break;
		case KPop3CannotOpenServiceEntry:
			aString.Append(_L("KPop3CannotOpenServiceEntry"));
			break;

		case KPop3CannotSetRequiredFolderContext:
			aString.Append(_L("KPop3CannotSetRequiredFolderContext"));
			break;
		case KPop3InvalidApopLogin:
			aString.Append(_L("KPop3InvalidApopLogin"));
			break;
		case KPopTopError:
			aString.Append(_L("KPopTopError"));
			break;

//		case KImskBaseError:
//			aString.Append(_L("KImskBaseError"));
//			break;
		case KImskErrorDNSNotFound:
			aString.Append(_L("KImskErrorDNSNotFound"));
			break;
		case KImskErrorControlPanelLocked:
			aString.Append(_L("KImskErrorControlPanelLocked"));
			break;
		case KImskErrorISPOrIAPRecordNotFound:
			aString.Append(_L("KImskErrorISPOrIAPRecordNotFound"));
			break;
		case KImskErrorActiveSettingIsDifferent:
			aString.Append(_L("KImskErrorActiveSettingIsDifferent"));
			break;
		case KImskSecuritySettingsFailed:
			aString.Append(_L("KImskSecuritySettingsFailed"));
			break;
		case KImskTopError:
			aString.Append(_L("KImskTopError"));
			break;
//		case KImapBaseError:
//			aString.Append(_L("KImapBaseError"));
//			break;
		case KErrImapConnectFail:
			aString.Append(_L("KErrImapConnectFail"));
			break;
		case KErrImapServerFail:
			aString.Append(_L("KErrImapServerFail"));
			break;
		case KErrImapServerParse:
			aString.Append(_L("KErrImapServerParse"));
			break;
		case KErrImapServerBusy:
			aString.Append(_L("KErrImapServerBusy"));
			break;
		case KErrImapServerVersion:
			aString.Append(_L("KErrImapServerVersion"));
			break;
		case KErrImapSendFail:
			aString.Append(_L("KErrImapSendFail"));
			break;
		case KErrImapBadLogon:
			aString.Append(_L("KErrImapBadLogon"));
			break;
		case KErrImapSelectFail:
			aString.Append(_L("KErrImapSelectFail"));
			break;
		case KErrImapWrongFolder:
			aString.Append(_L("KErrImapWrongFolder"));
			break;
		case KErrImapServerNoSecurity:
			aString.Append(_L("KErrImapServerNoSecurity"));
			break;
		case KErrImapServerLoginDisabled:
			aString.Append(_L("KErrImapServerLoginDisabled"));
			break;
		case KErrImapTLSNegotiateFailed:
			aString.Append(_L("KErrImapTLSNegotiateFailed"));
			break;
		case KErrImapCantDeleteFolder:
			aString.Append(_L("KErrImapCantDeleteFolder"));
			break;
		case KImapTopError:
			aString.Append(_L("KImapTopError"));
			break;
		case KDmssBaseError:
			aString.Append(_L("KDmssBaseError"));
			break;
		case KDmssUnknownErr:
			aString.Append(_L("KDmssUnknownErr"));
			break;
		case KDmssMailboxUnavailableErr:
			aString.Append(_L("KDmssMailboxUnavailableErr"));
			break;
		case KDmssActionAbortedErr:
			aString.Append(_L("KDmssActionAbortedErr"));
			break;
		case KDmssActionNotTakenErr:
			aString.Append(_L("KDmssActionNotTakenErr"));
			break;
		case KDmssCmdUnrecognisedErr:
			aString.Append(_L("KDmssCmdUnrecognisedErr"));
			break;
		case KDmssSyntaxErrorErr:
			aString.Append(_L("KDmssSyntaxErrorErr"));
			break;
		case KDmssCmdNotImplementedErr:
			aString.Append(_L("KDmssCmdNotImplementedErr"));
			break;
		case KDmssBadSequenceErr:
			aString.Append(_L("KDmssBadSequenceErr"));
			break;
		case KDmssParamNotImplementedErr:
			aString.Append(_L("KDmssParamNotImplementedErr"));
			break;
		case KDmssMailboxNoAccessErr:
			aString.Append(_L("KDmssMailboxNoAccessErr"));
			break;
		case KDmssExceededStorageErr:
			aString.Append(_L("KDmssExceededStorageErr"));
			break;
		case KDmssMailboxNameErr:
			aString.Append(_L("KDmssMailboxNameErr"));
			break;
		case KDmssTransactionFailedErr:
			aString.Append(_L("KDmssTransactionFailedErr"));
			break;
		case KDmssTimeOutErr:
			aString.Append(_L("KDmssTimeOutErr"));
			break;
		case KDmssTopError:
			aString.Append(_L("KDmssTopError"));
			break;
//		case KSmtpBaseError:
//			aString.Append(_L("KSmtpBaseError"));
//			break;
		case KSmtpNoMailFromErr:
			aString.Append(_L("KSmtpNoMailFromErr"));
			break;
		case KSmtpUnknownErr:
			aString.Append(_L("KSmtpUnknownErr"));
			break;
		case KSmtpBadMailFromAddress:
			aString.Append(_L("KSmtpBadMailFromAddress"));
			break;
		case KSmtpBadRcptToAddress:
			aString.Append(_L("KSmtpBadRcptToAddress"));
			break;
		case KSmtpLoginRefused:
			aString.Append(_L("KSmtpLoginRefused"));
			break;
		case KSmtpNoMsgsToSendWithActiveSettings:
			aString.Append(_L("KSmtpNoMsgsToSendWithActiveSettings"));
			break;

		case KErrSmtpTLSNegotiateFailed:
			aString.Append(_L("KErrSmtpTLSNegotiateFailed"));
			break;
		case KSmtpTopError:
			aString.Append(_L("KSmtpTopError"));
			break;
//		case KImcmBaseError:
//			aString.Append(_L("KImcmBaseError"));
//			break;
		case KImcmHTMLPartNotPopulated:
			aString.Append(_L("KImcmHTMLPartNotPopulated"));
			break;
		case KImcmInvalidMessageStructure:
			aString.Append(_L("KImcmInvalidMessageStructure"));
			break;
//		case KPop3BaseError:
//			aString.Append(_L("KPop3BaseError"));
//			break;
		case KErrPop3TLSNegotiateFailed:
			aString.Append(_L("KErrPop3TLSNegotiateFailed"));
			break;
		case /* KErrGsmCCUnassignedNumber */ -4257:
			aString.Append(_L("Invalid phone number. Check the number and try again."));
			break;
		case /* KErrGsmCCNoRouteToTransitNetwork */ -4258:
			aString.Append(_L("Temporary network failure. Try again later."));
			break;
		case /* KErrGsmCCNoRouteToDestination */ -4259:
			aString.Append(_L("Invalid phone number. Check the number and try again." ));
			break;
		case /* KErrGsmCCChannelUnacceptable */ -4262:
			aString.Append(_L("Temporary network failure. Try again later." ));
			break;
		case /* KErrGsmCCOperatorDeterminedBarring */ -4264:
			aString.Append(_L("Operation is not allowed. Contact cellular network operator."));
			break;
		case /* KErrGsmCCUserBusy */ -4273:
			aString.Append(_L("Number is busy. Try again later."));
			break;
		case /* KErrGsmCCUserNotResponding */ -4274:
			aString.Append(_L("The remote user is not currently reachable. Try again later." ));
			break;
		case /* KErrGsmCCUserAlertingNoAnswer */ -4275:
			aString.Append(_L("The remote user is not currently reachable. Try again later."));
			break;
		case /* KErrGsmCCCallRejected */ -4276:
			aString.Append(_L("Short network failure. Try again immediately."));
			break;
		case /* KErrGsmCCNumberChanged */ -4277:
			aString.Append(_L("Number has changed. Check the number and try again." ));
			break;
		case /* KErrGsmCCNonSelectedUserClearing */ -4282:
			aString.Append(_L("Unknown network failure. Try again later."));
			break;
		case /* KErrGsmCCDestinationOutOfOrder */ -4283:
			aString.Append(_L("Temporary network failure. Try again later."));
			break;
		case /* KErrGsmCCInvalidNumberFormat */ -4284:
			aString.Append(_L("Invalid phone number. Check the number and try again."));
			break;
		case /* KErrGsmCCFacilityRejected */ -4285:
			aString.Append(_L("Operation is not supported. Contact cellular network operator." ));
			break;
		case /* KErrGsmCCResponseToStatusEnquiry */ -4286:
			aString.Append(_L("KErrGsmCCResponseToStatusEnquiry"));
			break;
		case /* KErrGsmCCNormalUnspecified */ -4287:
			aString.Append(_L("Unknown network failure. Try again later." ));
			break;
		case /* KErrGsmCCNoChannelAvailable */ -4290:
			aString.Append(_L("Network busy." ));
			break;
		case /* KErrGsmCCNetworkOutOfOrder */ -4294:
			aString.Append(_L("Serious cellular network failure. Please contact the cellular network operator." ));
			break;
		case /* KErrGsmCCTemporaryFailure */ -4297:
			aString.Append(_L("Short network failure. Try again immediately."));
			break;
		case /* KErrGsmCCSwitchingEquipmentCongestion */ -4298:
			aString.Append(_L("Network busy." ));
			break;
		case /* KErrGsmCCAccessInformationDiscarded */ -4299:
			aString.Append(_L("Temporary network failure. Try again later." ));
			break;
		case /* KErrGsmCCRequestedChannelNotAvailable */ -4300:
			aString.Append(_L("Network busy." ));
			break;
		case /* KErrGsmCCResourceNotAvailable */ -4303:
			aString.Append(_L("Network busy." ));
			break;
		case /* KErrGsmCCQualityOfServiceNotAvailable */ -4305:
			aString.Append(_L("Service can not be provided." ));
			break;
		case /* KErrGsmCCRequestedFacilityNotSubscribed */ -4306:
			aString.Append(_L("Check operator services or connection settings." ));
			break;
		case /* KErrGsmCCIncomingCallsBarredInCug */ -4311:
			aString.Append(_L("Incoming calls are not allowed within closed user group." ));
			break;
		case /* KErrGsmCCBearerCapabilityNotAuthorised */ -4313:
			aString.Append(_L("Unauthorised operation." ));
			break;
		case /* KErrGsmCCBearerCapabilityNotCurrentlyAvailable */ -4314:
			aString.Append(_L("Requested service is not presently available. Try again later." ));
			break;
		case /* KErrGsmCCServiceNotAvailable */ -4319:
			aString.Append(_L("Check operator services or connection settings." ));
			break;
 		default:
			{
				TBuf<25> ercode;
				ercode.Format(_L(" %d"),aErrCode);
				aString.Append(ercode);
			}
			break;
		}
}
