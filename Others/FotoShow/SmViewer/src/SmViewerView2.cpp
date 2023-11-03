/*
* ============================================================================
*  Name     : CSmViewerView2 from SmViewerView2.h
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
#include  "SmViewer.hrh"
#include  "SmViewerView2.h"
#include  "SmViewerContainer2.h" 
#include  <eikmenup.h>
#include  <SmViewer.mbg>

_LIT(KSmViewerMbmFileName, "\\system\\apps\\SMVIEWER\\SMVIEWER.MBM");

// ================= MEMBER FUNCTIONS =======================

// ---------------------------------------------------------
// CSmViewerView2::ConstructL(const TRect& aRect)
// EPOC two-phased constructor
// ---------------------------------------------------------
//
void CSmViewerView2::ConstructL()
    {
	iContainer=0;
    BaseConstructL( R_SMVIEWER_VIEW2 );
    }


// ---------------------------------------------------------
// CSmViewerView2::~CSmViewerView2()
// ?implementation_description
// ---------------------------------------------------------
//
CSmViewerView2::~CSmViewerView2()
{
    if ( iContainer )
        {
        AppUi()->RemoveFromViewStack( *this, iContainer );
        }

    delete iContainer;

	
	delete iViewer;
	iViewer=0;


}

// ---------------------------------------------------------
// TUid CSmViewerView2::Id()
// ?implementation_description
// ---------------------------------------------------------
//
TUid CSmViewerView2::Id() const
    {
    return KView2Id;
    }

// ---------------------------------------------------------
// CSmViewerView2::HandleCommandL(TInt aCommand)
// ?implementation_description
// ---------------------------------------------------------
//
void CSmViewerView2::HandleCommandL(TInt aCommand)
{   
	if(iContainer->DeActivateSlideShow()!=EFalse)
	// был активен ? gththbcetv
		return;
	

	CEikButtonGroupContainer * pCbaGroup = CEikButtonGroupContainer::Current();
	pCbaGroup->SetCommandSetL(R_AVKON_SOFTKEYS_SELECTION_LIST);
	pCbaGroup->DrawNow();

    switch ( aCommand )
        {
        case EAknSoftkeyCancel:
			{
				((CSmViewerAppUi*)AppUi())->ClosePopup();
			}
			break;

        case EAknSoftkeyBack:
            {
            ((CSmViewerAppUi*)AppUi())->ShowThumbPanel();
            break;
            }
		case ESmViewer2CmdAppZoomNormal:
			iContainer->ZoomNormal();
			break;
		case ESmViewer2CmdAppZoomIn:
			iContainer->ZoomIn();
			break;
		case ESmViewer2CmdAppZoomOut:
			iContainer->ZoomOut();
			break;
		case ESmViewer2CmdAppRotateRight:
			iContainer->RotateRight();
			break;
		case ESmViewer2CmdAppRotateLeft:
			iContainer->RotateLeft();
			break;
		case 	ESmViewerCmdSlideShow:
			iViewer->ScaleOptimumL();
			iContainer->DrawNow();
			iContainer->ActivateSlideShow();
			break;
        default:
            {
            AppUi()->HandleCommandL( aCommand );
            break;
            }
        }
    }

// ---------------------------------------------------------
// CSmViewerView2::HandleClientRectChange()
// ---------------------------------------------------------
//
void CSmViewerView2::HandleClientRectChange()
    {
    if ( iContainer )
        {
        iContainer->SetRect( ClientRect() );
        }
    }

// ---------------------------------------------------------
// CSmViewerView2::DoActivateL(...)
// ?implementation_description
// ---------------------------------------------------------
//
void CSmViewerView2::DoActivateL(
   const TVwsViewId& /*aPrevViewId*/,TUid /*aCustomMessageId*/,
   const TDesC8& /*aCustomMessage*/)
{
    if (!iContainer)
        {
        iContainer = new (ELeave) CSmViewerContainer2;
        iContainer->SetMopParent(this);
		delete iViewer;
		iViewer=0;
		iViewer = CPAlbImageViewerBasic::NewL(iContainer, ClientRect());    
        iContainer->ConstructL( ClientRect(),(CSmViewerAppUi*)AppUi(),iViewer,this);
        }
     AppUi()->AddToStackL( *this, iContainer );

}

// ---------------------------------------------------------
// CSmViewerView2::HandleCommandL(TInt aCommand)
// ?implementation_description
// ---------------------------------------------------------
//
void CSmViewerView2::DoDeactivate()
    {
    if ( iContainer )
        {
        AppUi()->RemoveFromViewStack( *this, iContainer );
        }
    
    delete iContainer;
    iContainer = NULL;
    }


void CSmViewerView2::DynInitMenuPaneL(TInt aResourceId,CEikMenuPane* aMenuPane)
{
	((CSmViewerAppUi*)AppUi())->DynInitMenuPaneL(aResourceId,aMenuPane);
}

// End of File


TInt CSmViewerView2::LoadImage(const TDesC &aFileName)
{
	TRAPD(err,iViewer->LoadImageL(aFileName, EColor4K));
	if(iContainer==0)
		return KErrDied;
	return err;
}
