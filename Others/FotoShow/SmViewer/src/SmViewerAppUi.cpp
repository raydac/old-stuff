/*
* ============================================================================
*  Name     : CSmViewerAppUi from SmViewerAppUi.cpp
*  Part of  : SmViewer
*  Created  : 05/09/2003 by 
* ============================================================================
*/
/* ========================================================================
  * Author:  Oleg Golosovskiy, http://www.sw4u.org mailto:mail@sw4u.org
  * ======================================================================== */
/** 
  * \author Oleg Golosovsky     
  */


// INCLUDE FILES
#include "SmViewerAppUi.h"
#include "SmViewerView.h"
#include "SmViewerView2.h"
#include "SmViewerView3.h"
#include <SmViewer.rsg>
#include "smviewer.hrh"
#include "SmViewerContainer.h"
#include "aknnotewrappers.h"
#include <aknwaitdialog.h>
#include <avkon.hrh>
#include "SmViewerContainer2.h"
#include "SmViewerContainer3.h"
#include "SmViwerViewSettings.h"
#include  <eikmenup.h>
#include  <SmViewer.mbg>
#include  <e32math.h>

_LIT(KSmViewerMbmFileName, "\\system\\apps\\SMVIEWER\\SMVIEWER.MBM");

#ifdef _DEBUG
	_LIT(KSettingsFileName, "c:\\system\\apps\\SmViewer.ini");
#else
	_LIT(KSettingsFileName, "c:\\system\\apps\\SMVIEWER\\SmViewer.ini");
#endif

// ================= MEMBER FUNCTIONS =======================
//
// ----------------------------------------------------------
// CSmViewerAppUi::ConstructL()
// ?implementation_description
// ----------------------------------------------------------
//
void CSmViewerAppUi::ConstructL()
    {

	logfile=0;
	{
//		logfile = CLogFile::NewL(_L("SMVLog.txt"), ETrue);
		if(logfile)
		{
			logfile->SetAutoNewline(ETrue);
			logfile->SetAutoTimeStamp(EFalse);
			logfile->SetAutoFlush(ETrue);
			logfile->PrintCurrentTime();
			logfile->Log(_L8("Loading engin"));
		}
	}

	iResetRandom = ETrue;


    BaseConstructL();
	iIcons=0;

	iRandSed = TInt64(76834,56376);

    // Show tabs for main views from resources
//    CEikStatusPane* sp = StatusPane();

    // Fetch pointer to the default navi pane control
	iNaviPane = 0;
//    iNaviPane = (CAknNavigationControlContainer*)sp->ControlL(TUid::Uid(EEikStatusPaneUidNavi));

    // Tabgroup has been read from resource and it were pushed to the navi pane. 
    // Get pointer to the navigation decorator with the ResourceDecorator() function. 
    // Application owns the decorator and it has responsibility to delete the object.
	iDecoratedTabGroup =0;
//    iDecoratedTabGroup = iNaviPane->ResourceDecorator();
//    if (iDecoratedTabGroup)
//        {
//        iTabGroup = (CAknTabGroup*) iDecoratedTabGroup->DecoratedControl();
//        }


	LoadSettings();

	iPht = CPhoneBookTools::NewL();
    
	CSmViewerView* view1 = new (ELeave) CSmViewerView;

    CleanupStack::PushL( view1 );
    view1->ConstructL();
    AddViewL( view1 );      // transfer ownership to CAknViewAppUi
    CleanupStack::Pop();    // view1

    CSmViewerView2* view2 = new (ELeave) CSmViewerView2;

    CleanupStack::PushL( view2 );
    view2->ConstructL();
    AddViewL( view2 );      // transfer ownership to CAknViewAppUi
    CleanupStack::Pop();    // view2


    CSmViewerView3* view3 = CSmViewerView3::NewL();
    CleanupStack::PushL( view3 );
    AddViewL( view3 );      // transfer ownership to CAknViewAppUi
    CleanupStack::Pop();    // view3
    

    CSmViwerViewSettings* view4 = new (ELeave) CSmViwerViewSettings;
	CleanupStack::PushL( view4 );
	view4->ConstructL();
    AddViewL( view4 );      // transfer ownership to CAknViewAppUi
    CleanupStack::Pop();    // view4


	SetDefaultViewL(*view1);

    }

// ----------------------------------------------------
// CSmViewerAppUi::~CSmViewerAppUi()
// Destructor
// Frees reserved resources
// ----------------------------------------------------
//
CSmViewerAppUi::~CSmViewerAppUi()
    {

	SaveSettings();
	delete iPht;
    delete iDecoratedTabGroup;


	iRandAr.Reset();
	iRandAr.Close();

	delete logfile;
	logfile = 0;

   }

// ------------------------------------------------------------------------------
// CSmViewerAppUi::::DynInitMenuPaneL(TInt aResourceId,CEikMenuPane* aMenuPane)
//  This function is called by the EIKON framework just before it displays
//  a menu pane. Its default implementation is empty, and by overriding it,
//  the application can set the state of menu items dynamically according
//  to the state of application data.
// ------------------------------------------------------------------------------
//

void CSmViewerAppUi::DynInitMenuPaneL(TInt /*aResourceId*/,CEikMenuPane* /*aMenuPane*/)
{

/*
	if(R_SLIDE_SHOW_CASCADE_MENU==aResourceId)
	{
		if(Settings().iSlideCyclic==TSmViewerSettings::ESlideCyclicOn)
		{
			TInt pos;
			CEikMenuPaneItem* item = aMenuPane->ItemAndPos(ESmViewerCmdOptionsCyclic,pos);
			if(item->IconBitmap()==0)
				item->SetIcon(iEikonEnv->CreateIconL( KSmViewerMbmFileName, EMbmSmviewerCheck_box, EMbmSmviewerCheck_box ));
		}
	}
	if(R_INTERVAL_CASCADE_MENU==aResourceId)
	{
		{
			TInt interval = Settings().iSlideInterval;
			TInt pos;
			CEikMenuPaneItem* item = aMenuPane->ItemAndPos(ESmViewerCmdOptionsInterval+1+interval,pos);
			if(item->IconBitmap()==0)
				item->SetIcon(iEikonEnv->CreateIconL( KSmViewerMbmFileName, EMbmSmviewerCheck_box, EMbmSmviewerCheck_box ));
		}
	}


	if(R_DIRECTION_CASCADE_MENU==aResourceId)
	{
			TInt type = Settings().iSlideDirection;
			TInt pos;
			CEikMenuPaneItem* item = aMenuPane->ItemAndPos(ESmViewerCmdOptionsListUp+type,pos);
			if(item->IconBitmap()==0)
				item->SetIcon(iEikonEnv->CreateIconL( KSmViewerMbmFileName, EMbmSmviewerCheck_box, EMbmSmviewerCheck_box ));
	}*/


}

// ----------------------------------------------------
// CSmViewerAppUi::HandleKeyEventL(
//     const TKeyEvent& aKeyEvent,TEventCode /*aType*/)
// ?implementation_description
// ----------------------------------------------------
//
TKeyResponse CSmViewerAppUi::HandleKeyEventL(
    const TKeyEvent& /*aKeyEvent*/,TEventCode /*aType*/)
    {
    if ( iTabGroup == NULL )
        {
        return EKeyWasNotConsumed;
        }

/*
            TInt active = iTabGroup->ActiveTabIndex();
            TInt count = iTabGroup->TabCount();
        
            switch ( aKeyEvent.iCode )
                {
                case EKeyLeftArrow:
                    if ( active > 0 )
                        {
                        active--;
                        iTabGroup->SetActiveTabByIndex( active );
                        ActivateLocalViewL(TUid::Uid(iTabGroup->TabIdFromIndex(active)));
                        }
                    break;
                case EKeyRightArrow:
                    if( (active + 1) < count )
                        {
                        active++;
                        iTabGroup->SetActiveTabByIndex( active );
                        ActivateLocalViewL(TUid::Uid(iTabGroup->TabIdFromIndex(active)));
                        }
                    break;
                default:
                    return EKeyWasNotConsumed;
                    break;
                }*/
        

    return EKeyWasConsumed;
    }

void CSmViewerAppUi::SetTab(TInt aIndex)
{
	if(iTabGroup)
		iTabGroup->SetActiveTabByIndex(aIndex);
}

// ----------------------------------------------------
// CSmViewerAppUi::HandleCommandL(TInt aCommand)
// ?implementation_description
// ----------------------------------------------------
//
void CSmViewerAppUi::HandleCommandL(TInt aCommand)
    {
    switch ( aCommand )
        {
        case EEikCmdExit:
		case EAknSoftkeyExit:
            {
			if(ShowConfirmation(EExit))
				Exit();
            break;
            }
        case ESmViewerCmdAppTest:
            {
            break;
            }
		case ESmViewerCmdAppAbout:
			{
				About();
				break;
			}

/*
		case	ESmViewerCmdOptionsListUp:
		case	ESmViewerCmdOptionsListDown:
		case	ESmViewerCmdOptionsRandom:
					Settings().iSlideDirection = aCommand-ESmViewerCmdOptionsListUp;
					break;
		case	ESmViewerCmdOptionsCyclic:
					if(Settings().iSlideCyclic==TSmViewerSettings::ESlideCyclicOn)
						Settings().iSlideCyclic=TSmViewerSettings::ESlideCyclicOff;
					else
						Settings().iSlideCyclic=TSmViewerSettings::ESlideCyclicOn;
					break;
		case	ESmViewerCmdOptionsInterval3:
		case	ESmViewerCmdOptionsInterval5:
		case	ESmViewerCmdOptionsInterval7:
		case	ESmViewerCmdOptionsInterval10:
					Settings().iSlideInterval = aCommand-ESmViewerCmdOptionsInterval-1;
				break;
*/
        default:
            break;      
        }
    }

// End of File  

void CSmViewerAppUi::ShowThumbPanel()
{
	if(logfile) logfile->Log(_L8("ShowThumbPanel"));
	ActivateLocalViewL(KViewId);
}

void CSmViewerAppUi::ShowImage(TInt aNum,TBool aRunSlide/* =EFalse */,TBool aSelected /* = EFalse */)
{
	if(logfile) logfile->Log(_L8("ShowImage"));
	iCurName = aNum;
	iActivateSlideShow = aRunSlide;
	iShowSelected = aSelected;
	ActivateLocalViewL(KView2Id);
}

void CSmViewerAppUi::ShowLink(TInt aNum)
{
	if(logfile) logfile->Log(_L8("ShowLink"));
	iCurName = aNum;
	ActivateLocalViewL(KView3Id);
}

void CSmViewerAppUi::About()
{
	TBuf<255> errorMsg;
	iCoeEnv->ReadResource(errorMsg,R_ABOUT);
	CAknInformationNote* informationNote;
	informationNote = new (ELeave) CAknInformationNote;
	informationNote->SetTimeout(CAknNoteDialog::ENoTimeout);
	informationNote->ExecuteLD(errorMsg);
}

void CSmViewerAppUi::StartWaiter(TBool aWithCancel)
{
	iWaitDlg = 0;
	iWaitDlg = new(ELeave) CAknWaitDialog(NULL, ETrue);//REINTERPRET_CAST(CEikDialog**,&waitDlg));
	TInt err=0;
	if(aWithCancel)
		{TRAP(err,iWaitDlg->ExecuteLD(R_WAIT_NOTE));}
	else
		{TRAP(err,iWaitDlg->ExecuteLD(R_WAIT_NOTE_WITHOUT_CANCEL));}
	iWaitDlg->SetCallback(this);
}


void CSmViewerAppUi::StopWaiter()
{

    CCoeAppUi* ui = STATIC_CAST(CCoeAppUi*, iCoeEnv->AppUi()); 
    if (ui->IsDisplayingMenuOrDialog()) // Check whether wait note is displayed.
    {
        TKeyEvent key;
        key.iCode = EKeyEscape;
        key.iModifiers = 0;
        iCoeEnv->SimulateKeyEventL(key, EEventKey);
    }

}

void CSmViewerAppUi::DialogDismissedL( TInt /*aButtonId*/)
{
	CSmViewerContainer* c1 = ((CSmViewerView*)View(KViewId))->Container();
	if(c1!=0)
			c1->CancelThreadForFindImages();

	CSmViewerContainer2* c2 = ((CSmViewerView2*)View(KView2Id))->Container();
	if(c2!=0)
	{
		if(c2->iViewer->IsBusy())
		{
			ShowThumbPanel();
		}
	}
}

void CSmViewerAppUi::ClosePopup()
{
	CAknViewAppUi::ClosePopup();
}

void CSmViewerAppUi::UpdateImageNumber(TContactInfo &aInfo)
{
	CSmViewerContainer* c1 = ((CSmViewerView*)View(KViewId))->Container();
	if(c1!=0)
	{
		c1->UpdateImageNumberL(aInfo);
	}
}


void CSmViewerAppUi::UpdateImageLink(TInt aImgIndex)
{
	CSmViewerContainer* c1 = ((CSmViewerView*)View(KViewId))->Container();
	if(c1!=0)
	{
		c1->UpdateImageLinkL(aImgIndex);
	}
}

TBool CSmViewerAppUi::ShowConfirmation(TConfirmation aType)
{
		TBuf<255> errorMsg;

		switch(aType)
		{
			case EDeleteAllSelectedItems:
				iCoeEnv->ReadResource(errorMsg,R_DEL_ALL_ITEMS);
				break;
			case EDeleteCurrentItem:
				iCoeEnv->ReadResource(errorMsg,R_DEL_CURRENT_ITEM);
				break;
			case ERenameExistFile:
				iCoeEnv->ReadResource(errorMsg,R_RENAME_EXIST_FILE);
				break;
			case EExit:
				iCoeEnv->ReadResource(errorMsg,R_APP_EXIT);
				break;
			default:
				return EFalse;
		}

		CAknQueryDialog* qNote=0;
		qNote = CAknQueryDialog::NewL();
		qNote->PrepareLC(R_CONFIRMATION_QUERY_DIALOG);
		qNote->SetPromptL(errorMsg);
		TInt result = qNote->RunLD();
		if(result != 0)
			return ETrue;
		else
			return EFalse;

}

TInt CSmViewerAppUi::GetCurImageName(TDes& aName)
{
	TInt curn=-1;
	CSmViewerContainer* c1 = ((CSmViewerView*)View(KViewId))->Container();
	if(c1!=0)
	{
		curn = c1->GetFileName(iCurName,aName);
		if(c1->iStopShowIndex==-1)
			c1->iStopShowIndex=iCurName;
	}

	return curn;
}

void CSmViewerAppUi::GetNextImageName(TDes& aName)
{
	TInt oldCur=iRealOldCur;
	iCurName++;
	iRealOldCur = GetCurImageName(aName);
	if(aName.Length()==0)
		iCurName=oldCur;
}

void CSmViewerAppUi::GetPrevImageName(TDes& aName)
{
	TInt oldCur=iRealOldCur;
	iCurName--;
	iRealOldCur = GetCurImageName(aName);
	if(aName.Length()==0)
		iCurName=oldCur;
}

void CSmViewerAppUi::GetRandomImageName(TDes& aName)
{
	TInt oldCur=iRealOldCur;

	TInt MaxName=0;

	CSmViewerContainer* c1 = ((CSmViewerView*)View(KViewId))->Container();
	if(c1!=0)
	{
		if(ShowSelected())
			MaxName = c1->SelectedItems();
		else
			MaxName = c1->MaxName();
	}

//	iCurName = Math::Rand(iRandSed)%MaxName;


//	if(Settings().iSlideCyclic==TSmViewerSettings::ESlideCyclicOff)
	{

		

		if(iRandAr.Count()==0)
		{
			if(Settings().iSlideCyclic==TSmViewerSettings::ESlideCyclicOn)
				iResetRandom=ETrue;
			else
				if(iResetRandom==EFalse)
				{
					aName = _L("");
					iCurName=oldCur;
					return;
				}
		}
		
		iCurName = -1;
		
		if(iResetRandom)
		{
			iResetRandom=EFalse;
			iRandAr.Reset();

			for(TInt i=0;i<MaxName;i++)
				iRandAr.Append(i);

		}


		if(iRandAr.Count()!=0)
		{
			TInt index = Math::Rand(iRandSed)%(iRandAr.Count());
			iCurName = iRandAr[index];
			iRandAr.Remove(index);
		}
		
	}


	iRealOldCur = GetCurImageName(aName);
}

TInt CSmViewerAppUi::LoadSettings()
{
	iSettings.iListBoxSort = TSmViewerSettings::EByName;
	iSettings.iListBoxType = TSmViewerSettings::EList;
	iSettings.iSlideCyclic  = TSmViewerSettings::ESlideCyclicOn;	
	iSettings.iSlideDirection = TSmViewerSettings::ESlideListDown;
	iSettings.iSlideInterval = TSmViewerSettings::ESlideInterval3;

	
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
	lex.Val(iSettings.iListBoxSort);
	lex.SkipSpace();
	lex.Val(iSettings.iListBoxType);
	lex.SkipSpace();
	lex.Val(iSettings.iSlideCyclic);
	lex.SkipSpace();
	lex.Val(iSettings.iSlideDirection);
	lex.SkipSpace();
	lex.Val(iSettings.iSlideInterval);
	lex.SkipSpace();

	return ret;
}

TInt CSmViewerAppUi::SaveSettings()
{
	RFile file;
	TInt ret = KErrNone;

	ret = file.Replace(CEikonEnv::Static()->FsSession(), KSettingsFileName, EFileShareAny);
	if (ret != KErrNone)
		return ret;


	CSmViewerContainer* c1 = ((CSmViewerView*)View(KViewId))->Container();
	if(c1!=0)
	{
		if(c1->IsGridBox())
			iSettings.iListBoxType = TSmViewerSettings::EGrid;
		else
			iSettings.iListBoxType = TSmViewerSettings::EList;
	}


	TBuf8<256> settingsBuf;
	settingsBuf.Format(_L8("%1d %1d %1d %1d %1d "),iSettings.iListBoxSort,iSettings.iListBoxType,iSettings.iSlideCyclic,
									iSettings.iSlideDirection,iSettings.iSlideInterval);

	ret = file.Write(settingsBuf);
	file.Close();

	return ret;
}



void CSmViewerAppUi::ActivateLastViewL()
{
	ActivateLocalViewL(iLastUid);
}

void CSmViewerAppUi::ShowOptions(TInt aCuirIndex)
{
	iLastUid = iView->Id();
	iCurName = aCuirIndex;
	ActivateLocalViewL(KView4Id);
}

