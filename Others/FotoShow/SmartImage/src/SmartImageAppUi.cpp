/*
* ============================================================================
*  Name     : CSmartImageAppUi from SmartImageAppUi.cpp
*  Part of  : SmartImage
*  Created  : 06/05/2003 by Oleg Golosovskiy
*  Implementation notes:
*     Initial content was generated by Series 60 AppWizard.
*  Version  :
*  Copyright: http://www.sw4u.org/
* ============================================================================
*/

// INCLUDE FILES
#include "SmartImageApp.h"
#include "SmartImageAppUi.h"
#include "SmartImageView.h"
#include "SmartImageView2.h"
#include <SmartImage.rsg>
#include "smartimage.hrh"
#include <AKNMESsAGEQUERYDIALOG.h>
#include <APGWGNAM.h>
#include <avkon.hrh>
#include <aknnotewrappers.h>
#include "SmartImageContainer2.h"


_LIT(KSettingsFileName, "c:\\system\\data\\SmImage.ini");
_LIT(KDefaultPictureFileName, "c:\\system\\data\\netpicture.smi");


// ================= MEMBER FUNCTIONS =======================
//
// ----------------------------------------------------------
// CSmartImageAppUi::ConstructL()
// ?implementation_description
// ----------------------------------------------------------
//
void CSmartImageAppUi::ConstructL()
{

	LoadSettings();

//	TBuf<256> cmdLine;
//	TInt lenc = 0;
//	lenc = RProcess().CommandLineLength();
//	RProcess().CommandLine(cmdLine);
    

//	if(Settings().iState==TSmiSettings::EActive)
//	SendToBackground(ETrue);
	
	iFirstLanch =0;
	iInterceptor=0;


    BaseConstructL();


    CSmartImageViewSettings* view1 = new (ELeave) CSmartImageViewSettings;

    CleanupStack::PushL( view1 );
    view1->ConstructL();
    AddViewL( view1 );      // transfer ownership to CAknViewAppUi
    CleanupStack::Pop();    // view1

    CSmartImageViewShow* view2 = new (ELeave) CSmartImageViewShow;

    CleanupStack::PushL( view2 );
    view2->ConstructL();
    AddViewL( view2 );      // transfer ownership to CAknViewAppUi
    CleanupStack::Pop();    // view2

    SetDefaultViewL(*view1);

	iInterceptor = CCallInterceptor::NewL(this);

	if(Settings().iState==TSmiSettings::EActive)
		iInterceptor->SetHookL();

	iConverter = CImageConverter::NewL(this);

//	SetAsHidden();
}

// ----------------------------------------------------
// CSmartImageAppUi::~CSmartImageAppUi()
// Destructor
// Frees reserved resources
// ----------------------------------------------------
//
CSmartImageAppUi::~CSmartImageAppUi()
{
	SaveSettings();

	delete iInterceptor;
	iInterceptor=0;

	delete iConverter;
	iConverter=0;

}

// ------------------------------------------------------------------------------
// CSmartImageAppUi::::DynInitMenuPaneL(TInt aResourceId,CEikMenuPane* aMenuPane)
//  This function is called by the EIKON framework just before it displays
//  a menu pane. Its default implementation is empty, and by overriding it,
//  the application can set the state of menu items dynamically according
//  to the state of application data.
// ------------------------------------------------------------------------------
//
void CSmartImageAppUi::DynInitMenuPaneL(
    TInt /*aResourceId*/,CEikMenuPane* /*aMenuPane*/)
    {
    }

// ----------------------------------------------------
// CSmartImageAppUi::HandleKeyEventL(
//     const TKeyEvent& aKeyEvent,TEventCode /*aType*/)
// ?implementation_description
// ----------------------------------------------------
//
TKeyResponse CSmartImageAppUi::HandleKeyEventL(
    const TKeyEvent& /*aKeyEvent*/,TEventCode /*aType*/)
{

	return EKeyWasNotConsumed;
}

// ----------------------------------------------------
// CSmartImageAppUi::HandleCommandL(TInt aCommand)
// ?implementation_description
// ----------------------------------------------------
//
void CSmartImageAppUi::HandleCommandL(TInt aCommand)
    {
    switch ( aCommand )
        {
		case EAknSoftkeyExit:
        case EEikCmdExit:
            {
			if(Settings().iState==TSmiSettings::EActive)
			{
				SaveSettings();
				iInterceptor->SetHookL();
				SendToBackground(ETrue);
			}
			else
			{
				iInterceptor->UnSetHook();
				Exit();
			}
            break;
            }
		case ESmartImageCmdAppHangUp:
			{
				break;
			}
		case ESmartImageAppAbout:
			{
				About();
				break;
			}
        case ESmartImageCmdAppTest:
            {
			ActivateLocalViewL(KView2Id);
			CLogEvent* Event2;
			Event2 = CLogEvent::NewL();
			Event2->SetNumber(_L("+79119108021"));
			ShowL(*Event2);
			delete Event2;
            break;
            }
        default:
            break;      
        }
    }



void CSmartImageAppUi::SendToBackground(TBool aSetAsHidden)
{
	TApaTaskList taskList(CEikonEnv::Static()->WsSession());
	TApaTask task=taskList.FindApp(KUidSmartImage);
	if (task.Exists())
	{
		task.SendToBackground();
		if(aSetAsHidden)
		{
			SetAsHidden();
		}
	}
}


void CSmartImageAppUi::SetAsHidden()
{
	RWsSession	ws;
	CArrayFixFlat<int> *wgl;
	CApaWindowGroupName* wgName;

	 ws.Connect();
	 wgl = new CArrayFixFlat<int>(5);
	 ws.WindowGroupList(wgl);
	 wgName = CApaWindowGroupName::NewLC(ws);
	 for(TInt i = 0;i < wgl->Count(); i++)
	 {
		 wgName->ConstructFromWgIdL(wgl->At(i));
		 if(wgName->AppUid() == KUidSmartImage)
		 {
			 TBool check = wgName->Hidden();
			 if(check==EFalse)
			 {
				wgName->SetHidden(ETrue);
				RWindowGroup& rootWin = CEikonEnv::Static()->RootWin();
				wgName->SetWindowGroupName(rootWin);
			 }
			 break;
		 }
	 }
	 CleanupStack::PopAndDestroy();// wgName;

	 ws.Close();
}
void CSmartImageAppUi::SendToForeground()
{

	TApaTaskList taskList(iEikonEnv->WsSession());
	TApaTask task=taskList.FindApp(KUidSmartImage);
	if (task.Exists())
	{
		RWsSession& ws = CEikonEnv::Static()->WsSession();
		TInt grId = ws.GetFocusWindowGroup();
		if(task.WgId()==grId)
		{
//			iAppContainer->SetDimmed(EFalse);
//			iAppContainer->MakeVisible(ETrue);
//			iAppContainer->PrepareForFocusGainL();
//			iAppContainer->SetFocus(ETrue,EDrawNow);
//			Document()->Hook()->StopTimer();
		}
		else
		{
			RWindowGroup windowGroup(iEikonEnv->RootWin());
			windowGroup.EnableScreenChangeEvents();
			windowGroup.EnableFocusChangeEvents();
			const TInt wgId = windowGroup.Identifier();
			task.SetWgId(wgId);
			task.BringToForeground();
		}

/*		
		iEikonEnv->BringForwards(ETrue);
		iEikonEnv->SetAutoForwarding(ETrue);
		iEikonEnv->BringOwnerToFront();
*/

	}
	
}

void CSmartImageAppUi::ActivateForeground()
{
	SendToForeground();

	if(iView)
	{
		if(iInterceptor->logfile) 
		iInterceptor->logfile->Log(_L8("iView activate"));
		if(iView->Id()==KView2Id)
			((CSmartImageViewShow*)View(KView2Id))->ResetView();
		else
			ActivateLocalViewL(KView2Id);
	}
	else
	{
		if(iInterceptor->logfile) 
		iInterceptor->logfile->Log(_L8("iView create"));
		ActivateLocalViewL(KView2Id);
	}
}

void CSmartImageAppUi::ShowL(const CLogEvent& aEvent)
{
	infoAboutCurrentLoadState.iStatus = CImageConverter::EStopLStatus;
	
	if(iInterceptor->logfile) 
	iInterceptor->logfile->Log(_L8("ShowL"));
	if(iInterceptor->logfile) 
	iInterceptor->logfile->Log(aEvent.Number());

	CContactDatabase* cntDB = 0;
	cntDB = CContactDatabase::OpenL(); 	

	infoAboutCurrentLoadState.iNumber = aEvent.Number();
//	CContactItemFieldDef *def = new (ELeave) CContactItemFieldDef;
//	CleanupStack::PushL(def);
//	def->AppendL(KUidContactFieldPhoneNumber);
//	const CContactIdArray* items2 = cntDB->FindLC(aEvent.Number(), def); 
	CContactIdArray* items2 = cntDB->MatchPhoneNumberL(aEvent.Number(),7);
	CleanupStack::PushL(items2);

	delete iField;
	iField = 0;
	HBufC* textref = 0;

	
	if(iInterceptor->logfile) 
	iInterceptor->logfile->Log(_L("found %d contacts"),items2->Count());
	
	if (items2->Count()>0)
	{

		


			{
				const TContactItemId& id = (*items2)[0] ;
				CContactItem* item = cntDB->ReadContactLC( id ) ;
				CContactItemFieldSet& fields = item->CardFields() ;
				
				infoAboutCurrentLoadState.iFirstName = _L("");
				TInt pos=fields.Find(KUidContactFieldGivenName);
				if (pos>=0 && pos< fields.Count() ) 
				{
					const CContactItemField& field1 = fields[pos] ;
					TStorageType storageType = field1.StorageType() ;
					if (storageType == KStorageTypeText)
					{
						CContactTextField* text = field1.TextStorage();
						infoAboutCurrentLoadState.iFirstName.Append(text->Text());
					}
				}
				
				infoAboutCurrentLoadState.iLastName = _L("");
				pos=fields.Find(KUidContactFieldFamilyName);
				if (pos>=0 && pos< fields.Count() ) 
				{
					const CContactItemField& field1 = fields[pos] ;
					TStorageType storageType = field1.StorageType() ;
					if (storageType == KStorageTypeText)
					{
						CContactTextField* text = field1.TextStorage();
						infoAboutCurrentLoadState.iLastName.Append(text->Text());
					}
				}
				
				infoAboutCurrentLoadState.iEMail = _L("");
				pos=fields.Find(KUidContactFieldEMail);
				if (pos>=0 && pos< fields.Count() ) 
				{
					const CContactItemField& field1 = fields[pos] ;
					TStorageType storageType = field1.StorageType() ;
					if (storageType == KStorageTypeText)
					{
						CContactTextField* text = field1.TextStorage();
						infoAboutCurrentLoadState.iEMail.Append(text->Text());
					}
				}
				


				pos = 0;
				while(-1!=(pos=fields.FindNext(KUidContactFieldPicture,KUidContactFieldVCardMapUnknown,pos)))
				{
					const CContactItemField& field1 = fields[pos] ;
					TStorageType storageType = field1.StorageType() ;
					if (storageType == KStorageTypeText)
					{
						CContactTextField* text = field1.TextStorage();
						TPtrC ptr = text->Text();
						textref = HBufC::NewL(ptr.Length());
						(*textref) = ptr;
						break;
					}
				}


				for ( TInt j = 0 ; j < fields.Count() ; j++ )
				{
					const CContactItemField& field = fields[j] ;
					TStorageType storageType = field.StorageType() ;
					const CContentType& contentType = field.ContentType() ;
					
				
					if ( storageType == KStorageTypeStore && contentType.Mapping() == KUidContactFieldVCardMapPHOTO )
					{
						iField = CContactItemField::NewL(field); // deleted by 
						iField->SetId(j);
					}
					
//#ifdef _DEBUG
//					for(TInt fldIndex=0;fldIndex<contentType.FieldTypeCount();fldIndex++)
//						TFieldType tp = contentType.FieldType(fldIndex);
//#endif
					
					
				}
				
				cntDB->CloseContactL( id ) ;
				CleanupStack::PopAndDestroy(item);// item CloseContactL
			}
			


	}

	// ����������� 
	// � textref - ������� ���� �������
	// � iField - ��������� ���� �������


	if(iInterceptor->logfile) 
	iInterceptor->logfile->Log(_L8("View2 activate"));


	if(iView)
	{
		if(iView->Id()==KView2Id)
			((CSmartImageViewShow*)View(KView2Id))->ResetView();
		else
			ActivateLocalViewL(KView2Id);
	}
	else
		ActivateLocalViewL(KView2Id);


	if((textref!=0)&&(iEikonEnv->FsSession().IsValidName(textref->Des())))
	{// bug ����� ����� � �� ���� !!!
		iConverter->StartFileToBitmapConvert(*textref); 
		delete iField;
		iField = 0;
	}
	else
	if(iField!=0)
	{
		CContactStoreField* storage = iField->StoreStorage() ;
		HBufC8* buf = storage->Thing() ;
		if (buf->Length()>0)
		{
			iConverter->StartDescriptorToBitmapConvert(*buf); 
		}
	}
	else
	{
		iConverter->StartFileToBitmapConvert(KDefaultPictureFileName); 
		delete iField;
		iField = 0;
	}


//	CleanupStack::PopAndDestroy(2,def); items2 def
	CleanupStack::PopAndDestroy(items2);
	delete textref;
	textref=0;
}


void CSmartImageAppUi::HandleForegroundEventL(TBool aForeground)
{

//	if(aForeground!=EFalse)
//		iFirstLanch++;
//
//	if(
//		iFirstLanch<2&&
//		aForeground//&&
////		(Settings().iState==TSmiSettings::EActive)
//		)
//	{
//		SendToBackground(ETrue);
//		return;
//	}
//


	if((aForeground!=EFalse))//&&(iFirstLanch>2))
	{
		
		if(iInterceptor)
		{
			if(iInterceptor->logfile) 
			iInterceptor->logfile->Log(_L8("Foreground"));
			if((iInterceptor->CallStatus()==CCallInterceptor::EStatusRinging)||
				(iInterceptor->CallStatus()== CCallInterceptor::EStatusAnswering))
			{
				if(
					(iView==0)||
					(iView->Id()!=KView2Id))
				{
					if(iInterceptor->logfile) 
					iInterceptor->logfile->Log(_L8("KView2Id"));
					ActivateLocalViewL(KView2Id);
				}
				else
					if(iInterceptor->logfile) 
						iInterceptor->logfile->Log(_L8("KView2Id already active"));
			}
			else
			{
				if(iView)
				if(iView->Id()!=KViewId)
				{
					if(iInterceptor->logfile) 
					iInterceptor->logfile->Log(_L8("KViewId activate"));
					ActivateLocalViewL(KViewId);
				}
			}
		}

	}
}

CSmartImageContainerShow* CSmartImageAppUi::ShowContainerL()
{
	__ASSERT_ALWAYS(((CSmartImageViewShow*)View(KView2Id))->Container()!=0,User::Leave(KErrGeneral));

	return ((CSmartImageViewShow*)View(KView2Id))->Container();

}


void CSmartImageAppUi::UpdateDuration(TTimeIntervalSeconds aSec)
{
	infoAboutCurrentLoadState.iDuration = aSec.Int();
	if(iView)
		if(iView->Id()==KView2Id)
			ShowContainerL()->DrawNow();
}


void CSmartImageAppUi::About()
{
	TBuf<255> errorMsg;
	iCoeEnv->ReadResource(errorMsg,R_ABOUT);
	CAknInformationNote* informationNote;
	informationNote = new (ELeave) CAknInformationNote;
	informationNote->SetTimeout(CAknNoteDialog::ENoTimeout);
	informationNote->ExecuteLD(errorMsg);
}


// End of File  

void CSmartImageAppUi::EndOfCall()
{
	if(iInterceptor->logfile) 
	iInterceptor->logfile->Log(_L("EndOfCall %d"),((CSmartImageViewShow*)View(KView2Id))->iLastCommand);
	if( ((CSmartImageViewShow*)View(KView2Id))->iLastCommand!=EAknSoftkeyClose)
		TimeWait(2000000);

	SendToBackground(ETrue);
}

void CSmartImageAppUi::TimeWait(TInt aInterval)
{
	TTimeIntervalMicroSeconds32 timeIntervalSeconds(aInterval);
	TRequestStatus timerStatus=0;
	RTimer timer; 
	timer.CreateLocal(); 
	// issue and wait
	timer.After(timerStatus,timeIntervalSeconds);
	User::WaitForRequest(timerStatus);
}

TInt CSmartImageAppUi::LoadSettings()
{

	iSettings.iState = TSmiSettings::EActive;
	iSettings.iDetailFirstName  = TSmiSettings::EShow;
	iSettings.iDetailLastName = TSmiSettings::EShow;
	iSettings.iDetailPhone = TSmiSettings::EShow;
	iSettings.iDetailDuration= TSmiSettings::EShow;
	iSettings.iDetailEmail= TSmiSettings::EShow;


	RFile file;
	TInt ret;

	ret = file.Open(CEikonEnv::Static()->FsSession(), KSettingsFileName, EFileRead | EFileShareAny);
	if (ret != KErrNone)
		return ret;

	TBuf8<100> settingsBuf;

	ret = file.Read(settingsBuf);
	file.Close();

	// Settings are simply implemented as a list of numbers in a text file.

	TLex8 lex(settingsBuf);
	lex.Val(iSettings.iState);
	lex.SkipSpace();
	lex.Val(iSettings.iDetailFirstName);
	lex.SkipSpace();
	lex.Val(iSettings.iDetailLastName);
	lex.SkipSpace();
	lex.Val(iSettings.iDetailPhone);
	lex.SkipSpace();
	lex.Val(iSettings.iDetailDuration);
	lex.SkipSpace();
	lex.Val(iSettings.iDetailEmail);
	lex.SkipSpace();

	return ret;
}

TInt CSmartImageAppUi::SaveSettings()
{
	RFile file;
	TInt ret = KErrNone;

	ret = file.Replace(CEikonEnv::Static()->FsSession(), KSettingsFileName, EFileShareAny);
	if (ret != KErrNone)
		return ret;


	TBuf8<256> settingsBuf;
	settingsBuf.Format(_L8("%1d %1d %1d %1d %1d %1d "),iSettings.iState,iSettings.iDetailFirstName,
									iSettings.iDetailLastName,iSettings.iDetailPhone,
									iSettings.iDetailDuration,iSettings.iDetailEmail );

	ret = file.Write(settingsBuf);
	file.Close();

	return ret;
}

