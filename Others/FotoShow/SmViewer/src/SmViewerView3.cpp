/*
* ============================================================================
*  Name     : CSmViewerView3 from SmViewerView3.h
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


#include <aknviewappui.h>
#include <avkon.hrh>
#include <aknconsts.h>
#include "SmViewerContainer3.h"
#include  <SmViewer.rsg>
#include  "SmViewer.hrh"
#include "SmViewerAppUi.h"
#include  <eikmenup.h>
#include  <SmViewer.mbg>

_LIT(KSmViewerMbmFileName, "\\system\\apps\\SMVIEWER\\SMVIEWER.MBM");


// #include <Application.rsg>

#include "SmViewerView3.h"
// #include "Container.h"

CSmViewerView3* CSmViewerView3::NewL()
    {
    CSmViewerView3* self = CSmViewerView3::NewLC();
    CleanupStack::Pop(self);
    return self;
    }

CSmViewerView3* CSmViewerView3::NewLC()
    {
    CSmViewerView3* self = new (ELeave) CSmViewerView3();
    CleanupStack::PushL(self);
    self->ConstructL();
    return self;
    }

CSmViewerView3::CSmViewerView3()
    {
    // no implementation required
    }

CSmViewerView3::~CSmViewerView3()
    {
    DoDeactivate();
    }

void CSmViewerView3::ConstructL()
    {
    BaseConstructL(R_SMVIEWER_VIEW3);
    }


TUid CSmViewerView3::Id() const
    {
    return KView3Id;
    }

void CSmViewerView3::DoActivateL(const TVwsViewId& /*aPrevViewId*/,
                                    TUid /*aCustomMessageId*/,
                                    const TDesC8& /*aCustomMessage*/)
{
    if(iContainer == NULL)
	{
		iContainer = new (ELeave) CSmViewerContainer3;
        iContainer->SetMopParent(this);
        iContainer->ConstructL( ClientRect(),(CSmViewerAppUi*)AppUi() );
        AppUi()->AddToStackL( *this, iContainer );
	}

}

void CSmViewerView3::DoDeactivate()
    {
    if (iContainer)
        {
        AppUi()->RemoveFromStack(iContainer);
        delete iContainer;
        iContainer = NULL;
        }
    }

void CSmViewerView3::HandleCommandL(TInt aCommand)
{   
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
        case EAknSoftkeyOk:
            {
            iEikonEnv->InfoMsg( _L("view3 ok") );
            break;
            }
		case ESmViewerCmdAppExit:
            {
            AppUi()->HandleCommandL(EEikCmdExit);
            break;
            }
        case EAknSoftkeyBack:
            {
            ((CSmViewerAppUi*)AppUi())->ShowThumbPanel();
            break;
            }
		case ESmViewer3CmdAppAttach:
			iContainer->AttachL();
			break;
		case ESmViewer3CmdAppDeattach:
			iContainer->DeAttachL();
			break;
        default:
            {
            AppUi()->HandleCommandL( aCommand );
            break;
            }
        }
}

void CSmViewerView3::DynInitMenuPaneL(TInt aResourceId,CEikMenuPane* aMenuPane)
{
	((CSmViewerAppUi*)AppUi())->DynInitMenuPaneL(aResourceId,aMenuPane);

}
