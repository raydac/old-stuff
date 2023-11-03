/*
* ============================================================================
*  Name     : CSmViewerView from SmViewerView.cpp
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
#include  <aknviewappui.h>
#include  <avkon.hrh>
#include  <SmViewer.rsg>
#include  <SmViewer.mbg>
#include  <eikmenup.h>
#include  "SmViewerView.h"
#include  "SmViewer.hrh"
#include  "SmViewerContainer.h" 
#include <barsread.h>
#include <gulicon.h>

// ================= MEMBER FUNCTIONS =======================
_LIT(KSmViewerMbmFileName, "\\system\\apps\\SMVIEWER\\SMVIEWER.MBM");
// ---------------------------------------------------------
// CSmViewerView::ConstructL(const TRect& aRect)
// EPOC two-phased constructor
// ---------------------------------------------------------
//
void CSmViewerView::ConstructL()
    {
	iContainer =0;
    BaseConstructL( R_SMVIEWER_VIEW1 );
    }

// ---------------------------------------------------------
// CSmViewerView::~CSmViewerView()
// ?implementation_description
// ---------------------------------------------------------
//
CSmViewerView::~CSmViewerView()
{
    if ( iContainer )
	{
        AppUi()->RemoveFromViewStack( *this, iContainer );
	    delete iContainer;
		iContainer = NULL;
	}

}

// ---------------------------------------------------------
// TUid CSmViewerView::Id()
// ?implementation_description
// ---------------------------------------------------------
//
TUid CSmViewerView::Id() const
    {
    return KViewId;
    }

// ---------------------------------------------------------
// CSmViewerView::HandleCommandL(TInt aCommand)
// ?implementation_description
// ---------------------------------------------------------
//
void CSmViewerView::HandleCommandL(TInt aCommand)
    {   


    switch ( aCommand )
        {
        case EAknSoftkeyBack:
            {
#ifdef _DEBUG
				if(ETrue)
#else
				if(!iContainer->IsInProgress())
#endif
				{
					iContainer->CancelThreadForFindImages();
					iContainer->SizeChanged();
					iContainer->DrawNow();
					
					CEikButtonGroupContainer * pCbaGroup = CEikButtonGroupContainer::Current();
					pCbaGroup->SetCommandSetL(R_AVKON_SOFTKEYS_OPTIONS_EXIT);
					pCbaGroup->DrawNow();
					iContainer->SetCaptionL();
					
				}
				else
					AppUi()->HandleCommandL(EEikCmdExit);
            break;
            }
		case ESmViewerCmdAppOpen:
			{
				iContainer->ActivateItem(EFalse);
				break;
			}
		case 	ESmViewerCmdSlideShow:
			{
				iContainer->ActivateItem(ETrue);
				break;
			}
		case 	ESmViewerCmdSlideShowSelected:
			{
				iContainer->ActivateItem(ETrue,ETrue);
				break;
			}
		case ESmViewerCmdAppAttach:
			{
				iContainer->ActivateItemLink();
				break;
			}
		case ESmViewerCmdAppSelect:
			{
				iContainer->SelectItem(iContainer->CurrentItem(),ETrue);
				break;
			}
		case ESmViewerCmdAppUnSelect:
			{
				iContainer->SelectItem(iContainer->CurrentItem(),EFalse);
				break;
			}
		case ESmViewerCmdAppSelectAll:
			{
				iContainer->SelectAllItem(ETrue);
				break;
			}
		case ESmViewerCmdAppUnSelectAll:
			{
				iContainer->SelectAllItem(EFalse);
				break;
			}
		case ESmViewerCmdAppSelectMC:
			{
				iContainer->SelectMCItem();
				break;
			}
		case ESmViewerCmdAppSelectPH:
			{
				iContainer->SelectPHItem();
				break;
			}
		case ESmViewerCmdAppDelete:
			{
				iContainer->Delete();
				break;
			}
		case ESmViewerCmdAppRename:
			{
				iContainer->Rename();
				break;
			}
		case ESmViewerCmdAppGridView:
			{
				iContainer->SetListView(EFalse);
				break;
			}
		case ESmViewerCmdAppListView:
			{
				iContainer->SetListView(ETrue);
				break;
			}
		case ESmViewerCmdOptions:
			{
				iContainer->ActivateOptions();
				break;
			}
			break;
        default:
            {
            AppUi()->HandleCommandL( aCommand );
            break;
            }
        }
    }

// ---------------------------------------------------------
// CSmViewerView::HandleClientRectChange()
// ---------------------------------------------------------
//
void CSmViewerView::HandleClientRectChange()
    {
    if ( iContainer )
        {
        iContainer->SetRect( ClientRect() );
        }
    }

// ---------------------------------------------------------
// CSmViewerView::DoActivateL(...)
// ?implementation_description
// ---------------------------------------------------------
//
void CSmViewerView::DoActivateL(
   const TVwsViewId& /*aPrevViewId*/,TUid /*aCustomMessageId*/,
   const TDesC8& /*aCustomMessage*/)
{
    if (!iContainer)
        {
        iContainer = new (ELeave) CSmViewerContainer;
        iContainer->SetMopParent(this);
        iContainer->ConstructL( ClientRect(),(CSmViewerAppUi*)AppUi(),(TSmViewerSettings::TListBoxType)(((CSmViewerAppUi*)AppUi())->Settings().iListBoxType));
        } 
	else
		iContainer->Sorting();

	AppUi()->AddToStackL( *this, iContainer );
	iContainer->SetCaptionL(EFalse);
	iContainer->SetCurrentIten(((CSmViewerAppUi*)AppUi())->CurrentPicture());
	iContainer->SetFocus(ETrue,EDrawNow);
}

// ---------------------------------------------------------
// CSmViewerView::HandleCommandL(TInt aCommand)
// ?implementation_description
// ---------------------------------------------------------
//
void CSmViewerView::DoDeactivate()
    {


    if ( iContainer )
        {
        AppUi()->RemoveFromViewStack( *this, iContainer );
        }
/*    
    delete iContainer;
    iContainer = NULL;*/


    }

void CSmViewerView::DynInitMenuPaneL(TInt aResourceId,CEikMenuPane* aMenuPane)
{



	if(R_SELECT_CASCADE_MENU==aResourceId)
	{
		if(iContainer->IsCurrentItemSelected())
			aMenuPane->DeleteMenuItem(ESmViewerCmdAppSelect);
		else
			aMenuPane->DeleteMenuItem(ESmViewerCmdAppUnSelect);


		if(iContainer->SelectedItems()==iContainer->Count())
		{
			aMenuPane->DeleteMenuItem(ESmViewerCmdAppSelectAll);
			aMenuPane->DeleteMenuItem(ESmViewerCmdAppSelectPH);
			aMenuPane->DeleteMenuItem(ESmViewerCmdAppSelectMC);
		}
		else
		{
			
			TInt err;
			err=iCoeEnv->FsSession().ScanDrive(_L("E:"));
			if((KErrNone != err)&&(KErrInUse != err))
			{
				aMenuPane->DeleteMenuItem(ESmViewerCmdAppSelectPH);
				aMenuPane->DeleteMenuItem(ESmViewerCmdAppSelectMC);
			}
		}


			


	}

	if(R_SMVIEWER_VIEW1_MENU==aResourceId)
	{
		if(iContainer->MaxName()==0)
		{
			aMenuPane->DeleteMenuItem(ESmViewerCmdAppOpen);
			aMenuPane->DeleteMenuItem(ESmViewerCmdImage);
			aMenuPane->DeleteMenuItem(ESmViewerCmdSlideShow);
			aMenuPane->DeleteMenuItem(ESmViewerCmdSlideShowCascade);
			aMenuPane->DeleteMenuItem(ESmViewerCmdAppSelectMenu);
			aMenuPane->DeleteMenuItem(ESmViewerCmdAppGridView);
			aMenuPane->DeleteMenuItem(ESmViewerCmdAppListView);
			aMenuPane->DeleteMenuItem(ESmViewerCmdOptions);
		}
		else
		{
			if(iContainer->IsGridBox())
				aMenuPane->DeleteMenuItem(ESmViewerCmdAppGridView);
			else
				aMenuPane->DeleteMenuItem(ESmViewerCmdAppListView);

			if(iContainer->IsSelectedItems()&&
				(iContainer->SelectedItems()!=iContainer->Count()))
				aMenuPane->DeleteMenuItem(ESmViewerCmdSlideShow);
			else
				aMenuPane->DeleteMenuItem(ESmViewerCmdSlideShowCascade);
		
		
		}

	

	}


	((CSmViewerAppUi*)AppUi())->DynInitMenuPaneL(aResourceId,aMenuPane);
}



// End of File

